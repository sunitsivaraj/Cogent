package au.example.app

import net.liftweb.json.Serialization.write
import net.liftweb.json._
import org.scalatra._

import scala.collection.mutable.ListBuffer


class MyScalatraServlet extends TestWebAppStack  {
  get("/") {

    html.index(new java.util.Date)
  }

  //a variable to exchange data between post and get request
  var execsrc:String = _
  var myNetwork:Network = _
  var myPorts:List[Mapping] = _
  var cogentBehaviors:List[BMapping] = _
  var threshold:Double = 0.0001
  var decay:Double = 0.05
  var max_iter = 400
  var behavior:String = _
  var dataVariable:String = _

  post("/parsemodel") {
    val modelSrc = params("model")
    threshold = params("threshold").toDouble
    decay = params("decay").toDouble
    max_iter = params("iter").toInt

    var inpNet = modelSrc.toString()

    dataVariable  = inpNet.split("@data").lift(1).get.
      split("@behavior").lift(0).get

    behavior = inpNet.split("@behavior").lift(1).get.
      split("@cognitiveModel").lift(0).get

    val actions = behavior.split("def")

    val cm :String  = "@cognitiveModel"+
      inpNet.split("@cognitiveModel").lift(1).get


    NetExpr.parseExpression(cm)
    myNetwork = NetExpr.myNet
    myPorts = NetExpr.port_mapping
    cogentBehaviors = NetExpr.behave_mapping
    println(NetExpr.result)
    Ok(NetExpr.result)
  }

  get("/entities") {

    //-----------------------------PARSING THE HIERARCHICAL NETWORK NODES-----------------------------------------------
    // A recursive function that parses the input and adds network recursively
    def NetworkParser(myNetwork:Network):coherencemodel.Network ={
      //println("The name of the network added is " + myNetwork.name)

      //currently one network in model but will be expanded later
      var Net = new coherencemodel.Network(myNetwork.name)

      //get the evidence nodes
      for (myElem <- myNetwork.evidences) {
        var node = new coherencemodel.Node(myElem.defaultActivation, myElem.name)
        node.dataNode = true
        Net.add(node)
      }

      //get the hypothesis nodes
      for (myElem <- myNetwork.hypotheses) {
        var node = new coherencemodel.Node(myElem.defaultActivation, myElem.name)
        node.hypNode = true
        Net.add(node)
      }

      //get the belief nodes
      for (myElem <- myNetwork.beliefs) {
        var node = new coherencemodel.Node(myElem.defaultActivation, myElem.name)
        Net.add(node)
        node.beliefNode = true
      }

      //get the goal nodes
      for (myElem <- myNetwork.goals) {
        var node = new coherencemodel.Node(myElem.defaultActivation, myElem.name)
        Net.add(node)
        node.goalNode = true
      }

      //get the action nodes
      for (myElem <- myNetwork.actions) {
        var node = new coherencemodel.Node(myElem.defaultActivation, myElem.name)
        node.actionNode = true
        Net.add(node)
      }


      // Handling Hierarchy in Network(s)
      // This is inside a Option Container - Recursively add Networks here
      for(container <- myNetwork.subNets){
        for(myElem <- container)
          Net.add(NetworkParser(myElem))
      }

      return Net
    }


    //-----------------------------PARSING THE HIERARCHICAL NETWORK CONSTRAINTS AND PORTS-------------------------------
    var cohNet = new coherencemodel.CoherenceModel(NetworkParser(myNetwork), threshold, decay, max_iter)
    ConstraintParser(myNetwork)
    PortParser(myNetwork)
    PortAssociation(myPorts)


    // This is a recursive function for adding constraints to the Network(s)
    // The constraints are added using the coherenceModel class
    def ConstraintParser(myNetwork:Network):Unit= {
      // GET the coherenceModel Network object from the parsed Network Object
      var Net: coherencemodel.Network = cohNet.getNetwork(myNetwork.name)

      // Handling Constraints - Positive and Negative between nodes
      for (myElem <- myNetwork.coherenceConstraints) {
        // Handling Positive Constraints
        if (myElem.ctype == "explains" | myElem.ctype == "deduces" | myElem.ctype == "facilitates" | myElem.ctype == "triggers") {
          var target = cohNet.getNode(myElem.target, Net)
          var srcList = new ListBuffer[coherencemodel.Node]
          for (src <- myElem.source) {
            var srcNode = cohNet.getNode(src, Net)
            srcList += srcNode
          }
          if(myElem.ctype == "explains")
            cohNet.Explain(target, srcList, myElem.strength)
          else if(myElem.ctype == "deduces")
            cohNet.Deduce(target, srcList, myElem.strength)
          else if(myElem.ctype == "facilitates")
            cohNet.Facilitate(target, srcList, myElem.strength)
          else
            cohNet.Trigger(target, srcList, myElem.strength)
        }

        // Handling Negative Constraints
        if (myElem.ctype == "contradicts" | myElem.ctype == "incompatible") {
          var source = cohNet.getNode(myElem.source.head, Net)
          var target = cohNet.getNode(myElem.target, Net)
          if(myElem.ctype == "contradicts")
            cohNet.Contradict(source, target, myElem.strength)
          else
            cohNet.InCompatible(source, target, myElem.strength)
        }
      }

      // Handling Analogical Constraints
      for(analogy <- myNetwork.analogies){
        var hyp_list:ListBuffer[coherencemodel.Node] = new ListBuffer[coherencemodel.Node]()
        var evi_list:ListBuffer[coherencemodel.Node] = new ListBuffer[coherencemodel.Node]()

        // adding hypothesis
        for(hyp <- analogy.hyp_list){
          var myNode:coherencemodel.Node = cohNet.getNode(hyp, Net)
          hyp_list += myNode
        }

        // adding hypothesis
        for(evi <- analogy.evi_list){
          var myNode:coherencemodel.Node = cohNet.getNode(evi, Net)
          evi_list += myNode
        }

        cohNet.Analogy(hyp_list, evi_list, analogy.strength)
      }

      // Handling Hierarchy in Network(s)
      // This is inside a Option Container - Recursively add Networks here
      for (container <- myNetwork.subNets) {
        for (myElem <- container)
          ConstraintParser(myElem)
      }
    }



    // This is a recursive function for adding Port details to the Network(s)
    def PortParser(myNetwork:Network):Unit={
      // GET the coherenceModel Network object from the parsed Network Object
      var Net: coherencemodel.Network = cohNet.getNetwork(myNetwork.name)

      // Parsing the port information for each Network
      // Creating input and output ports for Networks - reference not added here
      myNetwork.portlist match {
        case Some(s) => {
          for (myElem <- s) {
            if (myElem.ptype == "in"){
              cohNet.addInputPort(myElem.name, Net)
            }
            else{cohNet.addOutputPort(myElem.name, Net)}
          }
        }
        case None => println("No port listed")
      }

      // Port association are made here - evidence/hypothesis are connected to the port nodes
      // Can Handle Evidence and Hypothesis differently if needed
      myNetwork.portmapper match {
        case Some(s) => {
          for (myElem <- s) {
            if (myElem._2.isInstanceOf[Evidence]) {
              cohNet.addPortReference(myElem._1.name, myElem._2.asInstanceOf[Evidence].name, Net)
            }
            else {
              cohNet.addPortReference(myElem._1.name, myElem._2.asInstanceOf[Hypothesis].name, Net)
            }
          }
        }
        case None =>  println("No port mapping available")
      }

      // Handling Hierarchy in Network(s)
      // This is inside a Option Container - Recursively add Networks here
      for (container <- myNetwork.subNets) {
        for (myElem <- container)
          PortParser(myElem)
      }

    }

    // PORT ASSOCIATIONS - CONNECTING INPUT AND OUTPUT
    def PortAssociation(mapping:List[Mapping]):Unit={
      for(ports <- mapping){
        cohNet.connect(ports.inpPort,ports.outPort, ports.weight)
      }
    }

    // Run and settle the network
    cohNet.runCoherenceModel()

    //--------------------------------------BEHAVIOR SECTION------------------------------------------------------------
    //Behaviors don't return anything they modify the data
    //activated behaviors seperated by semicolon
    var activated_behavior:String = ""

    //check nodes associated are activated and add behaviors
    for(beh <- cogentBehaviors){
      var network = cohNet.getNetwork(beh.NetName)
      var node = cohNet.getNode(beh.NodeName, network)
      if(node.activation > 0){
        activated_behavior += beh.BehName+"();"
      }
    }

    /*
    //Behavior Execution - Combining the data with behavior and executions
    var returnedValues:Vector[Any] = Eval(dataVariable+behavior+activated_behavior+"display();")
    for(value <- returnedValues){println(value)}
    */

    object Model{
      def behavior():Unit={}
    }
    /*
    println("--------------------------------------------------------------------------------------------------")
    val fileStream = getClass.getResourceAsStream("/model.txt")
    var mystring = scala.io.Source.fromInputStream(fileStream).getLines.mkString(sep = "\n")
    println(mystring.toString)
    var myobj:Any = Eval(mystring.toString)
    println(myobj)
    myobj.behavior()
    println("This statement is done")*/


    //Non display code
    var imports:String = "import scala.collection.mutable.ListBuffer\n"
    var evidence_changes:String = "var evidence_changes = ListBuffer[Tuple2[String,String]]()\n"
    var print_data:String = "var print_data:String = \"\";\n"
    var add_change:String = "def add_change(evi:String, value:String):Unit={evidence_changes += Tuple2(evi, value)}\n"
    var display = "def display(data:String):Unit={print_data = print_data + data+\"<br/>\"}\n"
    var print_all = "def print_all():String={return print_data}\n"
    var return_evidences:String = "def return_evidence():ListBuffer[Tuple2[String,String]]={return evidence_changes}\n"
    var return_all:String = "def return_all():Tuple2[String, ListBuffer[Tuple2[String,String]]]={return Tuple2(print_data, evidence_changes)}\n"
    var returnedValue:Tuple2[String, ListBuffer[Tuple2[String,String]]] = Eval(imports+evidence_changes+print_data+add_change+display+print_all+return_evidences+return_all+dataVariable+
      behavior+activated_behavior+"return_all()")

    //Extract print data out of returnedValue
    var console_display:String = returnedValue._1

    //Change Evidences based on returnedValue


    //--------------------------------------VISUALIZATION SECTION-------------------------------------------------------
    // Visualization of the Network
    case class Response(nodeDetails: String, nodes: ListBuffer[NodeData], connections:ListBuffer[ConnectionData], returnedValue:String)
    case class NodeData(id: String, label: String, color: String)
    case class ConnectionData(from: String,to: String, color:String, arrows:Option[String]=None)

    var NodeList = new ListBuffer[NodeData]
    var ConnList = new ListBuffer[ConnectionData]

    for(x <- cohNet.rootNode.getVertexID()){
      var color = "#42aaf4"
      if(x._2 == -1)
        color = "#ff6b79"
      NodeList += NodeData(x._1,x._1,color)
    }

    // Adding ports to the list - Input Ports
    for(x <- cohNet.inputports){
      var color = "#f4dc42"
      if(x.isUsed())
        NodeList += NodeData(x.name,x.name,color)
    }

    // Adding ports to the list - Output Ports
    for(x <- cohNet.outputports){
      var color = "#f4dc42"
      if(x.isUsed())
        NodeList += NodeData(x.name,x.name,color)
    }

    //remove redundant edges and add to ConnList
    var doneNodes = new ListBuffer[Tuple3[String,String,Int]]
    for(edge <- cohNet.rootNode.getConnections()){
      if(!doneNodes.contains(Tuple3(edge._2,edge._1,edge._3))){
        doneNodes += edge
        var color = "#42aaf4"
        if(edge._3 == -1)
          color = "#ff6b79"
        ConnList += ConnectionData(edge._1,edge._2,color)
      } }

    //add inputPort and reference connection
    for(x <- cohNet.inputports){
      if(x.isUsed())
        ConnList += ConnectionData(x.name,x.reference_name,"000000")
    }

    //add outputPort and reference connection
    for(x <- cohNet.outputports){
      if(x.isUsed())
        ConnList += ConnectionData(x.name,x.reference_name,"000000")
    }

    //Connecting Input and Output Port
    for(x <- cohNet.inputports){
      if(x.isUsed()) {
        if(x.connection_weight > 0)
          ConnList += ConnectionData(x.connection_name, x.name, "#42aaf4", Some("to;"))
        else
          ConnList += ConnectionData(x.connection_name, x.name, "#ff6b79", Some("to;"))
      }
    }

    val data = Response(cohNet.rootNode.getNodeDetail(), NodeList, ConnList, console_display)

    implicit val formats = DefaultFormats
    val jsonString = write(data)

    Ok(jsonString)
  }

}
