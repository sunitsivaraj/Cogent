package au.example.app.coherencemodel

import scala.collection.mutable.ListBuffer

/**
  * this class handles all the cases in Coherence Theory
  * @author  Sunit Sivaraj
  * @version 1.0
  * @since   2016-05-31
  * @constructor Initializes Networks array of type ListBuffer
  */
class CoherenceModel(root:Network, threshold:Double, decay:Double, max_iter:Int) {

  var inputports  = new ListBuffer[Inputport]
  var outputports = new ListBuffer[Outputport]
  parameters.thresh = threshold
  parameters.decay = decay
  parameters.max_iter = max_iter
  private var special = new Node(1, "Sp")
  var model = new IAC_Runner(root)
  var rootNode = root

  /**
    * This is a utility function
    * This method checks child if there return the object or else return null
    * @param n This is the edge object of Node class type
    * @param ch This is the edge object of Node class type
    * @return a NodeEdge object
    */
  private def getConnection(n:Node,ch:Node):NodeEdge={
    for(x <- n.connectedNodes){
      if(x.connection == ch)
        return x
    }
    return null
  }

  /**
    * This method is a part of the ECHO model
    * This method sets up activation links between nodes
    * @param prop1 This is a proposition node of type Node
    * @param prop2 This is proposition nodes of type Node
    * @param Weight This is the connection weight
    * @return Nothing
    */
  private def Cohere(prop1:Node, prop2:ListBuffer[Node], Weight:Double):Unit={
    var div = prop2.length
    var linkWeight = Weight/div

    for(node <- prop2){
      //setting up excitation links
      var e1 = new NodeEdge(node,linkWeight)
      var e2 = new NodeEdge(prop1,linkWeight)
      node.addConnection(e2)
      prop1.addConnection(e1)}

    //establish connection among hypothesis
    if(prop2.length > 1){
      for(x <- 0 to prop2.length-2){
        for(y <- x+1 to prop2.length-1){

          var hy1:Node = prop2(x)
          var hy2:Node = prop2(y)
          var check = getConnection(hy1,hy2)

          //edge found
          if(check != null)
            CohereAlterWeight(hy1,check,linkWeight)

          //no previous edge is found
          else
            CohereAddNewEdge(hy1,hy2,linkWeight)
        }  }//end of for loops
    } }

  /**
    * This method is used to alter weights of edges based on how their nodes are explained
    * @param prop1 This is the Node object
    * @param check This is the NodeEdge object
    * @param linkWeight This is a Double value
    * @return Nothing
    */
  private def CohereAlterWeight(prop1:Node,check:NodeEdge,linkWeight:Double):Unit={
    if(check.weight > 0){
      check.weight = check.weight+linkWeight
      var e = getConnection(check.connection,prop1)
      e.weight = e.weight+linkWeight}
  }

  /**
    * This method is used to add a new Edge in case of an explain statement
    * @param prop1 This is a Node object
    * @param prop2 This is a Node object
    * @param linkWeight This is a Double value for the weight of the edge
    * @return Nothing
    */
  private def CohereAddNewEdge(prop1:Node,prop2:Node,linkWeight:Double):Unit={
    var e1 = new NodeEdge(prop1,linkWeight)
    var e2 = new NodeEdge(prop2,linkWeight)
    prop1.addConnection(e2)
    prop2.addConnection(e1)
  }

  /**
    * This method is used for establishing a contradictory link
    * @param prop1 This parameter is a Node object
    * @param prop2 This parameter is a Node object
    * @return Nothing
    */
  private def InCohere(prop1:Node, prop2:Node, Weight:Double):Unit={
    var e1 = new NodeEdge(prop1,-Weight)
    var e2 = new NodeEdge(prop2,-Weight)

    prop1.addConnection(e2)
    prop2.addConnection(e1)
  }

  /**
    * This method is a part of the Explanatory Coherence
    * This method sets up activation links between nodes
    * @param hyp1 This is the evidence node of type Node
    * @param hyp2 This is the hypothesis node or hypotheses nodes of type Node
    * @param Weight This is the connection weight
    * @return Nothing
    */
  def Explain(hyp1:Node, hyp2:ListBuffer[Node], Weight:Double):Unit={
    // based on the principle of Explanatory Coherence
    Cohere(hyp1, hyp2, Weight)
  }

  /**
    * This method is a part of the Deliberative Coherence
    * This method sets up activation links between nodes
    * @param goal This is the goal node of type Node
    * @param actions This is the action nodes of type Node
    * @param Weight This is the connection weight
    * @return Nothing
    */
  def Facilitate(goal:Node, actions:ListBuffer[Node], Weight:Double):Unit={
    // based on the principle of Deliberative Coherence
    Cohere(goal, actions, Weight)
  }

  /**
    * This method is a part of the Deductive Coherence
    * This method sets up activation links between nodes
    * @param belief This is the belief node of type Node
    * @param hypotheses This is the hypotheses nodes of type Node
    * @param Weight This is the connection weight
    * @return Nothing
    */
  def Deduce(belief:Node, hypotheses:ListBuffer[Node], Weight:Double):Unit={
    // based on the principle of Deductive Coherence
    Cohere(belief, hypotheses, Weight)
  }

  /**
    * This method is a part of the Deductive Coherence
    * This method sets up activation links between nodes
    * @param belief This is the belief node of type Node
    * @param propositions This is the action or goal nodes of type Node
    * @param Weight This is the connection weight
    * @return Nothing
    */
  def Trigger(belief:Node, propositions:ListBuffer[Node], Weight:Double):Unit={
    Cohere(belief, propositions, Weight)
  }

  /**
    * This method is used for establishing a contradictory link in Explanatory and Deductive Coherence
    * @param prop1 This parameter is a Node object
    * @param prop2 This parameter is a Node object
    * @param Weight This is the connection weight
    * @return Nothing
    */
  def Contradict(prop1:Node, prop2:Node, Weight:Double):Unit={
    // based on the principle of Explanatory and Deductive Coherence
    InCohere(prop1, prop2, Weight)
  }

  /**
    * This method is used for establishing a Incompatible Link in Deliberative Coherence
    * @param action This parameter is a Node object
    * @param goal This parameter is a Node object
    * @param Weight This is the connection weight
    * @return Nothing
    */
  def InCompatible(action:Node, goal:Node, Weight:Double):Unit={
    // based on the principle of Deliberative Coherence
    InCohere(action, goal, Weight)
  }


  /**
    * This method is used for establishing Analogical connection between set of nodes
    * @param hypList
    * @param eviList
    * @return Nothing
    */
  def Analogy(hypList:ListBuffer[Node], eviList:ListBuffer[Node], Weight:Double):Unit={
    var linkWeight:Double = Weight/hypList.length
    //connect all nodes
    println("length", hypList.length)
    for(i <- 0 until hypList.length){
        var e1 = new NodeEdge(hypList(i),linkWeight)
        var e2 = new NodeEdge(eviList(i),linkWeight)
        hypList(i).addConnection(e2)
        eviList(i).addConnection(e1)
      }
    println("Analogy set up successfully")
  }

  private def establishDataPriority():Unit={
    rootNode.establishDataPriority(special)
  }

  /**
    * This method is used to run the echo model
    * @return Nothing
    */
  def runCoherenceModel(){
    establishDataPriority()
    model.establishCompetition()
    model.cycle()
  }

  /**
    * This is a function to find and return a node object given a network
    * @param name This is a Node object
    * @param net This is a Node object
    * @return Node
    */
  def getNode(name:String,net:Network):Node={
        for(node <- net.network_elements)
          if(node.ID == name){return node.asInstanceOf[Node]}
    return null
  }

  /**
    * This method is method to return a network object given a name
    * @param name This is a Network name
    * @param net This is a Network name
    * @return Network
    */
  def getNetwork(name:String,net:Network=root): Network ={
    if(net.ID == name){return net}
    for(item <- net.network_elements){
      if(item.isInstanceOf[Network]){return getNetwork(name, item.asInstanceOf[Network])}
    }
    return null
  }

  /**
    * This method is used to create a new Input Port
    * @param portName This is the name of the input port of String type
    * @param net This is a Network object to look for the node
    * @return Nothing
    */
  def addInputPort(portName:String, net:Network): Unit ={
    var inp = new Inputport(portName)
    inputports += inp
  }

  /**
    * This method is used to create a new Output Port
    * @param portName This is the name of the output port of String type
    * @param net This is a Network object to look for the node
    * @return Nothing
    */
  def addOutputPort(portName:String, net:Network):Unit={
    var out = new Outputport(portName, net)
    outputports += out
  }

  /**
    * This method is associates a port with a evidence/hypothesis node
    * Assuming no two ports have the same name
    * @param portName This is the name of the input/output port of String type
    * @param ref This is a name of the Node object to be associated with the Port
    * @param net This a Network object associated with the Nodes/Port
    * @return Nothing
    */
  def addPortReference(portName:String, ref:String, net:Network):Unit={
    var reference_node = getNode(ref, net)
    for(port <- outputports){
      if(port.name == portName){port.addAssociation(reference_node)}
    }
    for(port <- inputports){
      if(port.name == portName){port.addAssociation(reference_node)}
    }
  }

  /**
    * function to connect an input and output port with a weight
    * @param inpName This is the name of the input port of String type
    * @param outName This is the name of the output port of String type
    * @param weight This is the weight of the connection between ports
    * @return Nothing
    */
  def connect(inpName:String,outName:String,weight:Double):Unit={
    var inp:Inputport = null
    var out:Outputport = null

    for(port <- inputports){
      if(port.name == inpName){inp = port} }
    for(port <- outputports){
      if(port.name ==outName){out = port} }

    var portedge = new PortEdge(out,weight)
    inp.addReference(portedge)
  }
}