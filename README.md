# Cogent #

Cogent is a web framework for simulating of cognitive agents.

## Installing Java, sbt, and Scalatra ##
Cogent is designed using the Scalatra micro web framework [Scalatra](http://scalatra.org/) <br/>
Scalatra will need Java and sbt to be installed <br/>

Install java from [Java download](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) <br/>
java version used for project is 1.8.0_144 <br/>

download sbt from [sbt download](https://www.scala-sbt.org/download.html) <br/>
sbt version used for project is 0.13.15 <br/>

## Installing IntelliJ IDEA ##
IntelliJ IDEA IDE was used in developing the application, although other IDE can be used for development.
The IDE can be downloaded from [IntelliJ download](https://www.jetbrains.com/idea/download/)

## Building & Running Cogent ##
To run Cogent, use the following commands (windows command prompt)

```sh
cd Cogent
sbt
> jetty:start
> browse
```
Cogent can be accessed by opening the following link [http://localhost:8080/](http://localhost:8080/) in a web browser.

## Sample Programs ##
Sample programs can be accessed from the sample programs folder of the project.

LS accepted analogy - the DSL for simulating the scenario where the Lethal Strike action is accepted. <br/>

LS rejected analogy - the DSL for simulating the scenario where the Lethal Strike action is rejected. <br/>

## Procedure for running the simulation ##
Step 1 (Specification): 
* Specify the DSL in the code editor section or upload an existing DSL using the Choose File button. <br/>

<br/>

Step 2 (Parsing):
* **Parse Model** button must be pressed, if the cognitive model has any syntax error, then a message "Unsuccessful Parsing, Invalid Input" appears.<br/>
* If the cognitive model does not contain any syntax errors, then the message "Successful Parsing, Valid Input" appears.<br/>
* Modify Input or correct the DSL specification until a successful parsing.<br/>

<br/>

Step 3 (Running Model): 
* **Run Model** button must be pressed, in case of a successful run, we ge the message "Run Successful!"<br/>
* At the moment, we cannot detect run time errors, common run time error causes include connecting two nodes that are not declared, and errors in scala code.<br/>
* Modify Input or correct the DSL specification until a successful run.<br/>

<br/>

Step 4 (Visualizing):
* The model is visualized by pressing the **Visualize Model** button.<br/>

<br/>

Step 5 (Analyzing results):
* Any messages printed using the "display" function in the behavior section of the DSL can be seen in the "behaviors executed" section of the console.<br/>
* The "network details" section of the console gives the various details of the model.<br/>

<br/>

Special Note:
* Adjust the parameters "Thresh", "Decay", and "Max Iter" based on preference.<br/>
* Thresh - threshold of the coherence network.<br/>
* Decay  - Decay parameter of the coherence network.<br/>
* Max Iter - Maximum number of iterations, the network can run.<br/>
* Modify the DSL and the parameters until satisfactory results are obtained, thus developing the model in a iterative top down approach.<br/>

## Closing the platform ##
The console section where we initially start the web application must be closed, this can be done as follows<br/>

```sh
> stop
exit
```