package org.jboss.postoffice.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

/**
 * Utility to upload WAR file and save to disk
 */
@Path("/postoffice")
public class PostOfficeResource {

    private static String env_mailbox =  System.getenv("MAILBOX");
    private static final String MAILBOX_FOLDER = (env_mailbox==null ? "/tmp/mailbox" : env_mailbox);

    @Context
    private ServletContext context;

    @GET
    @Path("/ping")
    @Produces("text/plain")
    public String ping() {
        return "pong \n";
    }

    @GET
    @Path("/checkmail")
    @Produces("text/plain")
    public String checkmail() {
        String report;
        File mailbox = new File(MAILBOX_FOLDER);
        if (mailbox.exists()) {
            String [] mList = mailbox.list();
            if (mList != null && mList.length > 0) {
                StringBuilder sb = new StringBuilder();
                sb.append("mailbox file cnt: " + mList.length + "\n");
                for (int i=0; i < mList.length; i++ ) {
                    sb.append(mList[i] + "\n");
                }
                report = MAILBOX_FOLDER + ": " + sb.toString();
            } else {
                report = MAILBOX_FOLDER + " is empty";
            }

        } else {
            report = "directory " + MAILBOX_FOLDER + " does not exist";
        }
        return report + "\n";
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(MultipartFormDataInput input) throws IOException {
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();

        // Get file data to save
        List<InputPart> inputParts = uploadForm.get("attachment");
        for (InputPart inputPart : inputParts) {
            try {
                MultivaluedMap<String, String> header = inputPart.getHeaders();
                String fileName = getFileName(header);
                // convert the uploaded file to inputstream
                InputStream inputStream = inputPart.getBody(InputStream.class, null);
                File mailbox = new File(MAILBOX_FOLDER);
                if (!mailbox.exists()) {
                    mailbox.mkdir();
                }
                fileName = mailbox.getCanonicalPath() + File.separator + fileName;

                try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
                    inputStream.transferTo(outputStream);
                }

                return Response.status(200).entity("Uploaded file name : " + fileName
                        + "\n").build();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Response.status(200).build();
    }

    @DELETE
    @Path("/delete/{filename}")
    public Response deleteFile(@PathParam("filename") String filename) throws Exception {
        File mailbox = new File(MAILBOX_FOLDER);

        File f = new File(mailbox.getCanonicalPath() + File.separator + filename);
        if (f.exists() && f.isFile()) {
            f.delete();
        } else if (!f.exists()){
            return Response.status(400).entity(filename + " does not exist\n").build();
        } else if (f.isDirectory()) {
            return Response.status(400).entity(filename + " is a directory not a file\n").build();
        }
        return Response.status(200).entity("success\n").build();
    }

    private String getFileName(MultivaluedMap<String, String> header) {

        String[] contentDisposition = header.getFirst("Content-Disposition")
                .split(";");

        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {
                String[] name = filename.split("=");
                String finalFileName = name[1].trim().replaceAll("\"", "");
                return finalFileName;
            }
        }
        return "unknown";
    }
}
