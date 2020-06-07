package org.jboss.rest.config.one;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigSource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.Map;

@Path("/one")
public class DemoResource {

    /**
     * List the active ConfigSources for this application.
     * @return
     */
    @GET
    @Path("/provider/list")
    public String getProviderList() {
        Iterable<ConfigSource> itConfigSrcs = ConfigProvider.getConfig().getConfigSources();
        StringBuilder sb = new StringBuilder();
        // Display all registered ConfigProviders by name and ordinal
        sb.append(String.format("%s   %s\n","Ordinal","Name"));
        itConfigSrcs.forEach(src ->
                sb.append(String.format("%s   %s\n",src.getOrdinal(),src.getName()))
        );
        return sb.toString();
    }


    /**
     * Display all the key/value properties for the specified ConfigSource.
     * @param source
     * @return
     */
    @GET
    @Path("/{source}/properties/")
    public String getProviderProperties( @PathParam("source") String source) {
        Iterable<ConfigSource> itConfigSrcs = ConfigProvider.getConfig().getConfigSources();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s   %s\n","Ordinal","Name"));
        boolean isFileFound = false;
        for (ConfigSource src : itConfigSrcs) {
            if (src.getName().contains(source)) {
                sb.append(String.format("%s   %s\n", src.getOrdinal(), src.getName()));
                getProperties(sb, src.getProperties());
                isFileFound = true;
            }
        }
        if (!isFileFound) {
            sb.append(String.format("\t No ConfigSource by name %s\n", source));
        }
        return sb.toString();
    }

    /**
     * List all ConfigSources that contain the specified key as well
     * as the key/value pair.
     *
     * @param key
     * @return
     */
    @GET
    @Path("/lookup/{key}")
    public String getKeyValue ( @PathParam("key") String key) {
        Iterable<ConfigSource> itConfigSrcs = ConfigProvider.getConfig().getConfigSources();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s   %s\n","Ordinal","Name"));
        // Identify each ConfigSource containing the key
        for (ConfigSource src : itConfigSrcs) {
            String value = src.getValue(key);
            if (value != null) {
                sb.append(String.format("%s   %s\n", src.getOrdinal(), src.getName()));
                sb.append(String.format("\t %s : %s\n", key, value));
            }
        }
        return sb.toString();
    }


    /**
     * Display the value of the property that the system will
     * use for the given key.
     *
     * @param key
     * @return
     */
    @GET
    @Path("/get/{key}")
    public String getValue ( @PathParam("key") String key) {
       return ConfigProvider.getConfig()
               .getOptionalValue(key, String.class)
               .orElse("--None Found--");
    }


    private void getProperties(StringBuilder sb, Map<String, String> propMap) {
        if (propMap.isEmpty()) {
            sb.append(String.format("\t no entries\n"));
        } else {
            for (Map.Entry<String, String> entry : propMap.entrySet()) {
                sb.append(String.format("\t %s : %s\n", entry.getKey(), entry.getValue()));
            }
        }
    }
}
