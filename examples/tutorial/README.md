Java RBroker Tutorial Example Applications
==========================================

The DeployR Java RBroker Framework ships with a number of sample applications
provided to demonstrate some of the key featues introduced by the
[Quick Start Tutorial](http://deployr.revolutionanalytics.com/documents/dev/rbroker)
for the framework.

R Analytics File Dependencies
-----------------------------

The R scripts and data models used by these example applications are
bundled by default within the DeployR repository within the
`tutorial-rbroker` directory owned by `testuser`.

However, if for any reason your DeployR repository does not contain
these files you can add them using the DeployR Repository Manager as
follows:

1. Log in as `testuser` into the Repository Manager
2. Create a new directory called `tutorial-rbroker`
3. Upload each of the files found in the `./analytics` directory
4. Set the access control on 5SecondNoOp.R to `Public`.

Running the Example Applications
--------------------------------

A Gradle build script is provided to run the example applications:

```
build.gradle
```

By default, the build configuration assumes an instance of the DeployR server
is running on `localhost`. If your instance of DeployR is running at some
other IP address then please udpate the `deployr.endpoint` property in the
configuration file as appropriate.

You do not need to install Gradle before running these commands. For
example, to run the `DiscreteBlocking` example application on a Unix based OS,
run the following shell script:

```
gradlew run -DtestClass=com.revo.deployr.tutorial.discrete.DiscreteBlocking
```

To run the `DiscreteBlocking` example application on a Windows based OS,
run the following batch file:

```
gradlew.bat run -DtestClass=com.revo.deployr.tutorial.discrete.DiscreteBlocking

```

The complete list of RBroker Framework Tutorial example applications are as
follows:


```
//
// Discrete Task Broker
//
gradlew run -DtestClass=com.revo.deployr.tutorial.discrete.DiscreteBlocking
gradlew run -DtestClass=com.revo.deployr.tutorial.discrete.DiscretePolling
gradlew run -DtestClass=com.revo.deployr.tutorial.discrete.DiscreteAsynchronous
gradlew run -DtestClass=com.revo.deployr.tutorial.discrete.DiscreteProfiling
gradlew run -DtestClass=com.revo.deployr.tutorial.discrete.DiscreteSimulation

//
// Pooled Task Broker
//
gradlew run -DtestClass=com.revo.deployr.tutorial.pooled.PooledSimulation

//
// Background Task Broker
//
gradlew run -DtestClass=com.revo.deployr.tutorial.background.BackgroundBasics
```
