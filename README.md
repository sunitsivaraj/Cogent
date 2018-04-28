# Cogent #

Cogent is a web framework for simulation of cognitive agents.

## Installing Java, sbt, and Scalatra ##
Cogent is designed using the Scalatra micro web framework [Scalatra](http://scalatra.org/) <br/>
Scalatra will need Java and sbt to be installed <br/>

Install java from [Java download](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) <br/>
java version used for project is 1.8.0_144 <br/>

download sbt from [sbt download](https://www.scala-sbt.org/download.html) <br/>
sbt version used for project is 0.13.15 <br/>

## Installing IntelliJ IDEA##
IntelliJ IDEA IDE was used in developing the application, algthough other IDE can be used for development.
The IDE can be downloaded from [IntelliJ download] (https://www.jetbrains.com/idea/download/)

## Building & Running Cogent ##
To run Cogent, use the following commands (windows command prompt)

```sh
cd Cogent
sbt
> jetty:start
> browse
```

Cogent can accessed by opening the following link [http://localhost:8080/](http://localhost:8080/) in a web browser.