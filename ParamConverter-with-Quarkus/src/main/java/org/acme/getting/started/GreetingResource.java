package org.acme.getting.started;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Instant;

@Path("/hello")
public class GreetingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello RESTEasy";
    }

    @Path("time")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String helloTime(@QueryParam("name") String name, @QueryParam("time") Instant time) {
        return "Hello " + name + ", " + time;
    }

}