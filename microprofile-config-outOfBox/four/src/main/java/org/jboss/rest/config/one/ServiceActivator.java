package org.jboss.rest.config.one;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class ServiceActivator extends Application {

    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(DemoResource.class);
        return classes;
    }

    public Set<Object> getSingletons() {
        return null;
    }
}
