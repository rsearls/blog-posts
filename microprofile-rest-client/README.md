### microprofile-rest-client project contain code to support blog post.

This project provides a simple example of using the Microprofile Rest 
Client API. This is done with two WAR applications.  One is a REST service.
The other is a REST client that calls the service.
  
###Requriements
* WildFly 19 or newer
* maven
* JDK 1.8 or newer


### Build
```
mvn clean package
``` 

### Deploy
```
cp ./service/target//microprofile-rest-client-service.war ${WILDFLY_HOME}/standalone/deployments/.
cp ./client/target/microprofile-rest-client-client.war ${WILDFLY_HOME}/standalone/deployments/.
${WILDFLY_HOME}/bin/standalone.sh
``` 

### Test the service
```
curl -v http://localhost:8080/microprofile-rest-client-service/theService/ping
``` 
```
curl -v http://localhost:8080/microprofile-rest-client-service/theService/get/comics
``` 

### Test the client
```
curl -v http://localhost:8080/microprofile-rest-client-client/thePatron/hello
``` 
```
curl -v http://localhost:8080/microprofile-rest-client-client/thePatron/all
``` 
