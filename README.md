# Cogent #

Cogent is a web framework for simulation cognitive agents.

## Installing Java, sbt, and Scalatra ##
Cogent is designed using the Scalatra micro web framework [Scalatra](http://scalatra.org/) <br/>
Scalatra will need Java and sbt to be installed <br/>

Install java from [Java download](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) <br/>
java version used for project is 1.8.0_144 <br/>

download sbt from [sbt download](https://www.scala-sbt.org/download.html) <br/>
sbt version used for project is 0.13.15 <br/>

## Building & Running Cogent ##

```sh
$ cd Cogent
$ ./sbt
> jetty:start
> browse
```

If `browse` doesn't launch your browser, manually open [http://localhost:8080/](http://localhost:8080/) in web browser.
