package org.jboss.postoffice.core;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath(value="/")
public class PostOfficeApplication extends Application {
    public static Set<Class<?>> classes = new HashSet();
    public static Set<Object> singletons = new HashSet<Object>();

    public Set<Class<?>> getClasses() {
        classes.add(PostOfficeResource.class);
        return classes;
    }
    public Set<Object> getSingletons() {
        return singletons;
    }
}