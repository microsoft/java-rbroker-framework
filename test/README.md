DeployR Java RBroker Framework JUnit Tests
==========================================

The DeployR Java RBroker Framework ships with a set of unit tests.

A Gradle build script is provided to run the unit tests:

```
build.gradle
```

By default, the build configuration assumes an instance of the DeployR server
is running on `localhost`. If your instance of DeployR is running at some
other IP address then please udpate the `connection.endpoint` property in the
build.gradle configuration file as appropriate.

You do not need to install Gradle before running these commands. To run
this example application on a Unix based OS, run the following shell
script:

```
gradlew test
```


To run this example application on a Windows based OS, run the following
batch file:

```
gradlew.bat test
```
