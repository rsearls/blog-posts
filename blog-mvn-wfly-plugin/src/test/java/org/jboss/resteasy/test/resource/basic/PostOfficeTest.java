package org.jboss.resteasy.test.resource.basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.resource.basic.resource.PostOfficeResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.exporter.zip.ZipExporterImpl;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

public class PostOfficeTest {

    private static Client client;

    @BeforeClass
    public static void init() {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void after() throws Exception {
        client.close();
        client = null;
    }

    private String generateURL(String path) {
        // context-root set to cloud in jboss-web.xml
        return PortProviderUtil.generateURL(path, "cloud");
    }

    @Test
    public void pingIT() {
        WebTarget base = client.target(generateURL("/postoffice/ping"));
        try {
            Response response = base.request().get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            String body = response.readEntity(String.class);
            Assert.assertTrue(body.startsWith("pong"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
