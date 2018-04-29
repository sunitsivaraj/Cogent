# Cogent #

Cogent is a web framework for simulating of cognitive agents.

## Installing Java, sbt, and Scalatra ##
Cogent is designed using the Scalatra micro web framework [Scalatra](http://scalatra.org/) <br/>
Scalatra will need Java and sbt to be installed <br/>

Java can be installed from [Java download](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) <br/>
Java version used for project is 1.8.0_144 <br/>

sbt can be installed from [sbt download](https://www.scala-sbt.org/download.html) <br/>
sbt version used for project is 0.13.15 <br/>

## Installing IntelliJ IDEA ##
IntelliJ IDEA IDE was used in developing the application, although other IDE can also be used for development.<br/>
The IDE can be downloaded from [IntelliJ download](https://www.jetbrains.com/idea/download/)

## Downloading Cogent ##
Cogent can be downloaded to a local machine using the Clone or the download option in github. <br/>

or using the following command in command line interface (windows git Bash)<br/>

```sh
git clone https://github.com/szs0144/Cogent.git master
```

## Building & Running Cogent ##
To run Cogent, the following commands (windows command prompt) can be used

```sh
cd Cogent
sbt
> jetty:start
> browse
```
Cogent can be accessed by opening the following link [http://localhost:8080/](http://localhost:8080/) in a web browser. <br/>

*Note*: sbt would probably take a longer time in the very first build of the project, as it would check and install necessary libraries for the project.

## Sample Programs ##
Sample programs (*Lethal Strike problem*) can be accessed from the *sample programs* folder of the project.<br/>

*LS accepted analogy* - the DSL for simulating the scenario where the *Lethal Strike* action is accepted. <br/>

*LS rejected analogy* - the DSL for simulating the scenario where the *Lethal Strike* action is rejected. <br/>

## Procedure for running the simulation ##
*Step 1 (DSL Specification)*: 
* Specify the DSL in the *code editor* section or upload an existing DSL using the **Choose File** button. <br/>

<br/>

*Step 2 (DSL Parsing)*:
* **Parse Model** button must be pressed, if the cognitive model has any syntax error(s), then a message "Unsuccessful Parsing, Invalid Input" appears.<br/>
* If the cognitive model does not contain any syntax error(s), then the message "Successful Parsing, Valid Input" appears.<br/>
* Modify Input or correct the DSL specification until a successful parsing.<br/>

<br/>

*Step 3 (Running Model)*: 
* **Run Model** button must be pressed, in case of a successful run, we get the message "Run Successful!".<br/>
* At the moment, we cannot detect any run time errors, common run time error causes include connecting two nodes that are not declared, and errors in the scala code in *behavior* section.<br/>
* Modify Input or correct the DSL specification until a successful run.<br/>

<br/>

*Step 4 (Network Visualization)*:
* The model is visualized by pressing the **Visualize Model** button.<br/>

<br/>

*Step 5 (Analyzing Results)*:
* Any messages printed using the *display* function in the *behavior* section of the DSL can be seen in the **behaviors executed** section of the Console.<br/>
* The **network details** section of the Console gives the various details of the model.<br/>

<br/>

*Special Note*:
* Parameters such as *Thresh*, *Decay*, and *Max Iter* can be adjusted based on the user's preference.<br/>
* *Thresh* - threshold of the coherence network.<br/>
* *Decay*  - Decay parameter of the coherence network.<br/>
* *Max Iter* - Maximum number of iterations, the network can run.<br/>
* The DSL and the parameters can be modified until satisfactory results are obtained, thus developing a cognitive model in an iterative top down fashion.<br/>

## Closing the platform ##
The console section where we initially start the web application must be closed to exit the platform, this can be done as follows<br/>

```sh
> stop
exit
```