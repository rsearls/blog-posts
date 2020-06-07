---
layout:     post
title:      "MicroProfile Config 1.3; Examination of The Basics RESTEasy Web Application"
subtitle:   ""
date:       Jun 1, 2020 
author:     Rebecca Searls
---

In this article I will discuss the out-of-the-box ConfigSources the MicroProfile 
Configuration specification mandates every implementor provide.
I will show how these ConfigSources can be used to customize
the configuration of a simple REST application that runs in Wildlfy,
and discuss some of the nuances of their use.

The REST application used for this discussion serves two purposes.  One,
it is a simple REST app.  Two, its endpoints provide information on
the base set of ConfigSources Wildfly allocates to every REST app.

Three nearly identical WAR files are provided in this project.  This allows
me to show how a small addition to each archive affects the base set of
ConfigSources provided by Wildfly and how configuration properties set 
externally are handled. WAR file microprofile-config-one.war in project 
module one allows only the minimal set of config options.  In module two a 
microprofile-config.properties file is added to the base app.  Module three
adds to two's file set, a third party JAR file which contains its own 
microprofile-config.properties file. 

### Requriements
* Source code: [microprofile-config-one](https://github.com/rsearls/blog-posts/???????????)
* WildFly 19 or newer
* maven
* JDK 1.8 or newer
>> Note
>>
>>I am using a unix environment.  This has not been tested on Windows.
>>I will be running Wildfly and executing cURL commands in a terminal 
>>windows.
>>



### Resource Class
Here is the skeleton of the REST resource that will be used for this discussion.  
I will leave it to the reader to look at the class source code. [link point to file]
For clarity in calling the different WAR files, the value of @Path is the project
module name, so for module one, it is one, for module two, it is two, ... etc.

````
@Path("/one")
public class DemoResource {

    @GET
    @Path("/provider/list")
    public String getProviderList() { 
        // list all registered ConfigProviders by name and ordinal
    }
    
    @GET
    @Path("/{source}/properties/")
    public String getProviderProperties( @PathParam("source") String source) {
        // For the specified (ConfigSource) source, list all the properties 
        // (key/value).
    }
    
    @GET
    @Path("/lookup/{key}")
    public String getKeyValue ( @PathParam("key") String key) {
        // List each ConfigSource containing the property key 
    }
    
    @GET
    @Path("/get/{key}")
    public String getValue ( @PathParam("key") String key) {
        // Return the value of the property (key) the system selects
        // to be used.
    }
}
````

I suggest you build and deploy the applications, so you can call the endpoints
as I do and see the output, however I will list the output here as well.  I 
will be using cURL to call the endpoints.

#### Build and Deploy
```
mvn clean package
``` 
```
cp ./one/target/microprofile-config-one.war ${WILDFLY_HOME}/standalone/deployments/.
cp ./two/target/microprofile-config-two.war ${WILDFLY_HOME}/standalone/deployments/.
cp ./three/target/microprofile-config-three.war ${WILDFLY_HOME}/standalone/deployments/.
${WILDFLY_HOME}/bin/standalone.sh
``` 

### Base set of ConfigSources, microprofile-config-one.war

Lets look at the "out of the box" set of ConfigSources provided
by Wildfly for a REST applications

````
curl http://localhost:8080/microprofile-config-one/one/provider/list

Ordinal   Name
400   SysPropConfigSource
300   EnvConfigSource
60   null:null:ServletConfigSource
50   null:null:FilterConfigSource
40   null:ServletContextConfigSource
````

The specification requires that each ConfigSource provide a name and an ordinal.
The ordinal designates the priority of the ConfigSource. Higher ordinals have
priority over lower ordinals, Properties in higher priority ConfigSources
override the identical property (key) of a lower priority ConfigSource.
There is no required format for ConfigSource names.  Wildfly chooses to use 
the class name.

The first 2 ConfigSources in the list are required by the specification,
One for retrieving system properties with a default ordinal of 400 and the other
for Environment variables with a default ordinal of 300. They are
SysPropConfigSource and EnvConfigSource respectively in Wildfly.
Note that, specification implementors are allowed to assign different 
ordinals to their implementation's of these two ConfigSources.

The following 3 ConfigSources are not spec required.  These are the
base set of ConfigSources RESTEasy provides to handle configuration settings
for a REST application.  They are used to retrieved properties from the 
web.xml file. ServletConfigSource contains properties from the <servlet> element. 
FilterConfigSource from the <filter> element and ServletContextConfigSource from 
<servlet-context> elements.  Notice the low ordinal for these.  These ConfigSources 
will be discussed in more detail later in the article.

> SideNote
>```` 
>What is the "null:" in the names of RESTEasy's ConfigSources?
>
>A naming format for these ConfigSources was defined by the implementor.
>When the desired text is not retrieve, "null" is used as a placeholder.
>This will be corrected in the future, but it does not affect configuration 
>processing.
>````

>````
>What is the highest possible ordinal?  
>
>It is Integer.MAX_VALUE, 2147483647. Registering a ConfigSource with ordinal
>2147483647 will always be the top of the priority list, however this would 
>not be "best practice". It would most likely break a lot of stuff and 
>cause your colleagues to yell WTF or worse.
>````

>````
>What happens if I set my ConfigSource ordinal to (Integer.MAX_VALUE + 1)? 
> 
>A java.lang.NumberFormatException will be thrown.
>````

>````
>Which ConfigSource takes precedence if 2 or more have the same ordinal?
>
>It is indeterminate.  The specification makes no statement about it.
>This is not an issue until there is a name collision of a property name.
>The first encountered name in the list of ConfigSources takes precedence.
>````

>````
>Debugging why a particular property value is not applied could present a
>challenge for the user.  Any number of microprofile-config.properties files
>could reside in the classpath of the application and any number of third party 
>ConfigSources can be active in an execution environment.  This increases
>the potential for name collision.  Currently there are no common tools for
>determining the set of ConfigSources and their ordinal in a given environment,
>or general rules for documenting this information.  At this time it is left
>to the user to develop a process and/or tools to ferret out this information.
>````
>

#### Inspect ConfigSources Contents 
I will leave it to the reader to print the property list for SysPropConfigSource 
and EnvConfigSource because the output for each is 100+ lines long.  Printing the
properties for EnvConfigSource yields the same information as executing command
printenv on unix in a terminal.  Here are the cURL commands to do this.

````
curl http://localhost:8080/microprofile-config-one/one/SysPropConfigSource/properties
curl http://localhost:8080/microprofile-config-one/one/EnvConfigSource/properties
```` 

Lets look at the RESTEasy ConfigSources (default) property settings for 
microprofile-config-one.war.   The web.xml file for this application is
empty, however there are some default configuration values made available
to RESTEasy through the ConfigSources.  Lets see what those are.

Lets look at ServletConfigSource.

````
curl http://localhost:8080/microprofile-config-one/one/ServletConfigSource/properties

Ordinal   Name
60   null:null:ServletConfigSource
	 resteasy.servlet.mapping.prefix : /
	 javax.ws.rs.Application : org.jboss.rest.config.one.ServiceActivator
````
At deployment time Wildfly determined no mapping prefix was declared in the 
web.xml file. It assigned a default value.  ServiceActivator was discovered
in the archive and registered as the appropriate property.

Now lets see FilterConfigSource's contents.
````
curl http://localhost:8080/microprofile-config-one/one/FilterConfigSource/properties

Ordinal   Name
50   null:null:FilterConfigSource
	 no entries
````
There are no default filter related properties.

Finally we'll list ServletContextConfigSource's contents.
````
curl http://localhost:8080/microprofile-config-one/one/ServletContextConfigSource/properties

Ordinal   Name
40   null:ServletContextConfigSource
	 resteasy.preferJacksonOverJsonB : false
	 resteasy.scanned.resources : org.jboss.rest.config.one.DemoResource
	 resteasy.document.expand.entity.references : false
	 resteasy.unwrapped.exceptions : javax.ejb.EJBException
````
All properties are default values except resteasy.scanned.resources.
DemoResource is the app's REST resource class that RESTEasy will process.


According to the specification I should be able to override the value
of any of RESTEasy's properties via a system property and environment 
variable.  Lets test it out.

I'm picking property resteasy.preferJacksonOverJsonB to change because
it will not negatively impact the runnability of the application.

````
Stop Wildfly running.

In the terminal window where Wildfly was running define the system property.
export resteasy.preferJacksonOverJsonB=TRUE
````
Well that didn't work.  Unix Bash does not allow the "." character in
the property key.  Execution of the command reports error, "not a valid identifier".
 
Moving on to testing with an environment variable.  On the command line to
run Wildfly add this, -Dresteasy.preferJacksonOverJsonB=TRUE

````
./bin/standalone.sh  -Dresteasy.preferJacksonOverJsonB=TRUE
````

I expect to see property resteasy.preferJacksonOverJsonB=TRUE in EnvConfigSource 
and resteasy.preferJacksonOverJsonB=false in ServletContextConfigSource.

````
curl http://localhost:8080/microprofile-config-one/one/lookup/resteasy.preferJacksonOverJsonB

Ordinal   Name
400   SysPropConfigSource
	 resteasy.preferJacksonOverJsonB : TRUE
40   null:ServletContextConfigSource
	 resteasy.preferJacksonOverJsonB : TRUE
````
Hmmm, that's odd.  ServletContextConfigSource's value was changed to the
environment variable value.  Why might that be?  Wildfly must be processing
this value on bootup and overriding its own default value.

????I think the results of these two test case are atypical for externally setting
configuration settings.  One because of Unix Bash's restrictions and two changing
one of Wildfly's default values at bootup. 

Before we continue remove the environment variable setting.
````
Stop Wildfly
Restart Wildlfy without the -D option
./bin/standalone.sh
````
 
### microprofile-config.properties file, microprofile-config-two.war
Project module two adds META-INF/microprofile-config.properties to the
archive.  It contains a single property statement,
"resteasy.preferJacksonOverJsonB=TruE".  We will investigate how this changes 
the list of ConfigSources and overrides the default property.

Lets check the applications list of ConfigSources.
````
curl http://localhost:8080/microprofile-config-two/two/provider/list

Ordinal   Name
400   SysPropConfigSource
300   EnvConfigSource
100   PropertiesConfigSource[source=vfs:/content/microprofile-config-two.war/WEB-INF/classes/META-INF/microprofile-config.properties]
60   null:null:ServletConfigSource
50   null:null:FilterConfigSource
40   null:ServletContextConfigSource
````
PropertiesConfigSource is Wildfly's ConfigSource for processing
microprofile-config.properties files.

>The specification states "an ..  implementation must provide ... A 
>ConfigSource for each property file META-INF/microprofile-config.properties
>found on theclasspath."

It's interesting that Wildlfy includes as part of the class's name a 
reference to the properties file.  It will be quite helpful when one
needs to track down the location of a property setting.

The default ordinal for any microprofile-config.properties is 100.  
The file's ordinal can be changed with the addition of property,
config_ordinal, (e.g. config_ordinal=95).  Examples of this will
be shown later.

Here are the ConfigSources that contain property, resteasy.preferJacksonOverJsonB
````
curl http://localhost:8080/microprofile-config-two/two/lookup/resteasy.preferJacksonOverJsonB

Ordinal   Name
100   PropertiesConfigSource[source=vfs:/content/microprofile-config-two.war/WEB-INF/classes/META-INF/microprofile-config.properties]
	 resteasy.preferJacksonOverJsonB : TruE
40   null:ServletContextConfigSource
	 resteasy.preferJacksonOverJsonB : false

````

Here is the value that will be returned when RESTEasy requests the property
from the system.
````
curl http://localhost:8080/microprofile-config-two/two/get/resteasy.preferJacksonOverJsonB

TruE
````
This is the expected value.  PropertiesConfigSource has a higher ordinal than
ServletContextConfigSource, therefore PropertiesConfigSource is queried first. 
A value is found and thus returned to the caller.

### Multiple microprofile-config.properties files, microprofile-config-three.war

Module three builds upon the contents of module two.  It add a third party
JAR file, childJar.JAR, to the archive.  childJar only contains a 
microprofile-config.properties with property, "resteasy.preferJacksonOverJsonB=MayBe"

Every META-INF/microprofile-config.properties file found in the classpath
of an application must have a corresponding ConfigSource in the execution
environment, according to the specification.  Lets confirm that is true. 

````
curl http://localhost:8080/microprofile-config-three/three/provider/list

Ordinal   Name
400   SysPropConfigSource
300   EnvConfigSource
100   PropertiesConfigSource[source=vfs:/content/microprofile-config-three.war/WEB-INF/lib/childJar-1.0.jar/META-INF/microprofile-config.properties]
100   PropertiesConfigSource[source=vfs:/content/microprofile-config-three.war/WEB-INF/classes/META-INF/microprofile-config.properties]
60   null:null:ServletConfigSource
50   null:null:FilterConfigSource
40   null:ServletContextConfigSource
````
Yes, Wildfly does adhear to that rule.  There is a PropertiesConfigSource for
both childJar's META-INF/microprofile-config.properties and microprofile-config-three's
META-INF/microprofile-config.properties file.  

Both ConfigSources have the default ordinal, 100.  Remember there is no
guarantee which order ConfigSources of the same ordinal will reside in the
list.  In this case childJar's ConfigSource is first.  Property, 
"resteasy.preferJacksonOverJsonB=MayBe", will win out over microprofile-config-three's
setting, "resteasy.preferJacksonOverJsonB=TruE" and the lower ordinal, 
ServletContextConfigSource setting, "resteasy.preferJacksonOverJsonB : false".

````
curl http://localhost:8080/microprofile-config-three/three/lookup/resteasy.preferJacksonOverJsonB

Ordinal   Name
100   PropertiesConfigSource[source=vfs:/content/microprofile-config-three.war/WEB-INF/lib/childJar-1.0.jar/META-INF/microprofile-config.properties]
	 resteasy.preferJacksonOverJsonB : MayBe
100   PropertiesConfigSource[source=vfs:/content/microprofile-config-three.war/WEB-INF/classes/META-INF/microprofile-config.properties]
	 resteasy.preferJacksonOverJsonB : TruE
40   null:ServletContextConfigSource
	 resteasy.preferJacksonOverJsonB : false
````

Just for completeness lets confirm that the property value for the first 
PropertiesConfigSource in the list is returned.  Yes, it is.
````
curl http://localhost:8080/microprofile-config-three/three/get/resteasy.preferJacksonOverJsonB

MayBe
````

### App Configuration with web.xml, microprofile-config-four.war

````
curl http://localhost:8080/microprofile-config-four/four/provider/list

Ordinal   Name
400   SysPropConfigSource
300   EnvConfigSource
100   PropertiesConfigSource[source=vfs:/content/microprofile-config-four.war/WEB-INF/classes/META-INF/microprofile-config.properties]
60   null:null:ServletConfigSource
50   null:null:FilterConfigSource
40   null:ServletContextConfigSource
````

````
curl http://localhost:8080/microprofile-config-four/four/ServletConfigSource/properties

Ordinal   Name
60   null:null:ServletConfigSource
	 no entries
````

````
curl http://localhost:8080/microprofile-config-four/four/FilterConfigSource/properties

Ordinal   Name
50   null:null:FilterConfigSource
	 system : system-filter
	 javax.ws.rs.Application : org.jboss.rest.config.one.ServiceActivator
	 aquarium : fish-filter
````

````
curl http://localhost:8080/microprofile-config-four/four/ServletContextConfigSource/properties

Ordinal   Name
40   null:ServletContextConfigSource
	 resteasy.preferJacksonOverJsonB : false
	 month : April
	 resteasy.scanned.resources : org.jboss.rest.config.one.DemoResource
	 courts : legal-courts
	 resteasy.document.expand.entity.references : false
	 resteasy.unwrapped.exceptions : javax.ejb.EJBException
````




in this case its additive because DemoResource is in the 
classpath and REST rules are that scanning for files must
be preformed.

resteasy.scanned.resources=org.jboss.rest.alt.AltResource

curl http://localhost:8080/microprofile-config-four/alt/hello
Hello from AltResource


curl http://localhost:8080/microprofile-config-five/alt/hello
Hello from AltChildResource



