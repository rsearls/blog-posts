package org.jboss.rest.cartoon.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Path("/")
public class CartoonService {

    @GET
    @Path("/graphicNovels")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getGraphicNovels ()
    {
        return graphicNovels;
    }

    @GET
    @Path("/tv")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getTv ()
    {
        return tv;
    }

    @GET
    @Path("/get/{category}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getCategory ( @PathParam("category") String category)
    {
        List<String> result = null;

        if (category != null && !category.isEmpty()) {
            result = categoryMap.get(category.toLowerCase());
        }

        if (result == null) {
            result = new ArrayList(Arrays.asList("Unknown category"));
        }
        return result;
    }

    /**
     * Simple check that service is available
     * @return
     */
    @GET
    @Path("/ping")
    public String ping ()
    {
        return "CartoonService is alive  ";
    }


    List<String> graphicNovels = new ArrayList(
            Arrays.asList("Nimona",
                    "Watchmen",
                    "Maus: A Survivors Tale"));

    List<String> tv = new ArrayList(
            Arrays.asList("The Jetsons",
                    "Rocky and Bullwinkle"));

    List<String> comics = new ArrayList(
            Arrays.asList("Beetle Baily",
                    "Dick Tracy",
                    "Krazy Kat"));

    HashMap<String, List<String>> categoryMap = new HashMap();

    public CartoonService() {
        categoryMap.put("graphicnovels", graphicNovels);
        categoryMap.put("tv", tv);
        categoryMap.put("comics", comics);
    }
}
