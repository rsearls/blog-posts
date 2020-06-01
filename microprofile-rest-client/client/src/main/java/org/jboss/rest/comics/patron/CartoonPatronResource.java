package org.jboss.rest.comics.patron;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/thePatron")
@ApplicationScoped
public class CartoonPatronResource {
    @Inject
    @RestClient
    CartoonServiceIntf service;

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getAll(){
        List<String> result = new ArrayList<>();
        try {
            result.addAll(service.getCategory("GraphicNovels"));
            result.addAll(service.getCategory("TV"));
            result.addAll(service.getCategory("comics"));
        } catch (Exception e) {
            result.add(e.getMessage());
        }
        return result;
    }

    @GET
    @Path("/get/{category}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getCategory (@PathParam("category") String category){
        return service.getCategory(category);
    }

    /**
     * Simple check that service is available
     * @return
     */
    @GET
    @Path("/hello")
    public String getHello() {
        if (service == null) {
            return "Hello back.  The service is null";
        }
        return "Hello from CartoonPatronResource  ";
    }
}
