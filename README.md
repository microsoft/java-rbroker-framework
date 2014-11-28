Java RBroker Framework for DeployR
==================================

The Java RBroker Framework provides a simple yet powerful API that supports the
rapid integration of R Analytics inside any Java application. Simply define an
RTask, submit your task to an instance of RBroker and be notified when
your RTaskResult is available.

The framework scales effortlessly to support simple integrations through
sophisticated solutions such as high throughput, realtime scoring engines.

Links
-----

  * [Download](http://deployr.revolutionanalytics.com/docanddown/#rbroker)
  * [Quick Start Tutorial](http://deployr.revolutionanalytics.com/documents/dev/rbroker)
  * [Framework API JavaDoc](http://deployr.revolutionanalytics.com/documents/dev/rbroker-javadoc)
  * [Framework Dependencies](#dependencies)
  * [Example Code](#examples)
  * [License](#license)

Dependencies
============


Declarative JAR Dependencies: Maven Central Repository Artifacts
----------------------------------------------------------------

Artifacts for each official release (since 7.3.0) of the DeployR Java
RBroker Framework are published to the Maven Central repository.

[ArtifactId](http://search.maven.org/#search|ga|1|a%3A%22jRBroker%22): `jRBroker`

Using build frameworks such as Maven and Gradle your Java client
application can simply declare a dependency on the appropriate version
of the `jRBroker` artifact to ensure all required JAR dependencies are resolved
and available at runtime.


Bundled JAR Dependencies
------------------------

If you are not defining your DeployR RBroker Framework JAR dependencies using
declarative tools then you must ensure the required JAR files are placed
directly on your application CLASSPATH.

Besides the DeployR Java RBroker Framework JAR itself, `jRBroker-<version>.jar`,
the framework depends on the
[DeployR Java Client Library](https://github.com/deployr/java-client-library)
and all of it's third party JAR dependencies.

Building the Java RBroker Framework
-----------------------------------

A Gradle build script is provided to build the DeployR Java RBroker
Framework:

```
build.gradle
```

By default, the build will generate a version of the  `jRBroker-<version>.jar`
file in the `build/libs` directory.

You do not need to install Gradle before running these commands. To
build the DeployR Java RBroker Framework a Unix based OS, run the following shell
script:

```
gradlew build
```

To build the DeployR Java RBroker Framework on a Windows based OS, run the following
batch file:

```
gradlew.bat build
```


Examples
========

The DeployR Java RBroker Framework ships with a number of sample applications
provided to demonstrate some of the key featues introduced by the
[Quick Start Tutorial](http://deployr.revolutionanalytics.com/documents/dev/rbroker)
for the Java client library. See
[here](examples/tutorial) for details.

License
=======

Copyright (C) 2010-2014 by Revolution Analytics Inc.

This program is licensed to you under the terms of Version 2.0 of the
Apache License. This program is distributed WITHOUT
ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,
MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the
Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0) for more 
details.
