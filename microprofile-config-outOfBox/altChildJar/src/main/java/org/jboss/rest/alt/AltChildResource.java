package org.jboss.rest.alt;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/alt")
public class AltChildResource {
    @GET
    @Path("/hello")
    public String getHello() {
       return "Hello from AltChildResource";
    }
}
