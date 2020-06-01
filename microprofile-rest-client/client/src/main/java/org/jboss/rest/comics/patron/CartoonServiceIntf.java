package org.jboss.rest.comics.patron;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@RegisterRestClient(baseUri ="http://localhost:8888/some-root-context")
@Path("/theService")
@Singleton
public interface CartoonServiceIntf {

    @GET
    @Path("/graphicNovels/json")
    @Produces(MediaType.APPLICATION_JSON)
    List<String> getGraphicNovels ();

    @GET
    @Path("/get/{category}")
    @Produces(MediaType.APPLICATION_JSON)
    List<String> getCategory ( @PathParam("category") String category);
}
