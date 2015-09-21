## Build steps ##
kundera uses maven as build tool, so build process is not at all difficult.

Here are the steps-
  1. Checkout kundera code from trunk
  1. Ensure that maven and jdk 1.6 are already installed
  1. run `mvn clean install` under trunk folder to build the jar
  1. run `mvn eclipse:clean eclipse:eclipse` to generate Eclipse project and import in your favourite eclipse IDE to deep dive into the code
  1. The generated jar in target folder can be used as a jar for any client projects


Some important FAQ about the build-
  * The unit tests use **embedded** cassandra server instance at port 9165
  * The unit tests use their own storage-config.xml file in test/resources folder
  * The unit tests can be run on Externally running Cassandra server. However, do copy test's keyspace to storage-config.xml file and change the tests' setup method to comment out embedded mode and also correct the port to point to External cassandra server.
  * The pom.xml file contains system dependency link to 5 jar files copied in trunk/lib folder from Cassandra lib folder. The current trunk code supports cassandra version 0.6.1. Change/replace  the jar files from your Cassandra installation in case of other versions and correct the entries in pom.xml file. The jar files are-
    * apache-cassandra-0.6.1.jar
    * clhm-production.jar
    * high-scale-lib.jar
    * libthrift.jar
    * pelops.jar