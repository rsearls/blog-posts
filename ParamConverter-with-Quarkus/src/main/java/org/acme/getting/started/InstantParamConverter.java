package org.acme.getting.started;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.Provider;
import java.time.Instant;
import java.time.format.DateTimeParseException;

@Provider
public class InstantParamConverter implements ParamConverter<Instant> {

    public Instant fromString(String value){
        try {
            String x = Instant.parse(value).toString();
            return Instant.parse(value);
        } catch (DateTimeParseException e) {

        }
        return null;
    }

    public String toString(Instant value){
        return value.toString();
    }
}
