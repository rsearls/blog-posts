package org.acme.getting.started;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class GreetingResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/hello")
          .then()
             .statusCode(200)
             .body(is("Hello RESTEasy"));
    }

    @Test
    public void testHelloTimeEndpoint() {
        given()
                .when().get(URI.create("/hello/time?name=abc&time=2021-02-12T09:58:39Z"))
                .then()
                .statusCode(200)
                .body(is("Hello abc, 2021-02-12T09:58:39Z"));
    }

}