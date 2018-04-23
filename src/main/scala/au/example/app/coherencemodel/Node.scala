package au.example.app.coherencemodel

import scala.collection.mutable.ListBuffer

/**
  * It contains all standard node utility functions needed for coherence network.
  * this class represents a hypothesis or an evidence node in the coherence network.
  *
  * @author  Sunit Sivaraj
  * @version 1.0
  * @since   2016-05-31
  * @constructor get the activation of node of type Double and ID of node of type String
  */
class Node(act:Double ,id: String) extends CompositeNetwork{
  var activation = act
  ID = id

  var dataNode:Boolean = false
  var hypNode:Boolean = false
  var actionNode:Boolean = false
  var goalNode:Boolean = false
  var beliefNode:Boolean = false

  var connectedNodes = new ListBuffer[NodeEdge]
  var settled:Boolean = false
  private var netInput:Double = 0
  private var previousActivation:Double = 999
  private var ports = new ListBuffer[PortEdge]

  /**
    * A node class can have multiple edges
    * This method is used to add an edge to a node class
    * @param child This is the edge object of NodeEdge class type
    * @return Nothing
    */
  def addConnection(child: NodeEdge):Unit={
    connectedNodes += child
  }

  /**
    * A node class can have multiple ports associated with it
    * This method is used to add a port to a node class
    * @param port This is the port object of Port class type
    * @return Nothing
    */
  def addPortEdge(port:PortEdge):Unit={
    ports += port
  }


  /**
    * A method to get the harmony associated with it
    * @return Double
    */
  def getHarmony():Double={
    var harmony:Double = 0
    for(edge <- connectedNodes){
      harmony = harmony + activation*edge.weight*edge.connection.activation
    }
    return harmony
  }

  /**
    * A node class is a part of the IAC network
    * This method is the update function in the IAC network
    * This method takes the netInput value and updates the activation of the node
    * @return Nothing
    */
  def update():Unit={
    previousActivation = activation
    if(netInput > 0)
      activation = activation *(1-parameters.decay)+(netInput *(parameters.maxi - activation))
    else
      activation = activation *(1-parameters.decay)+(netInput *(activation - parameters.mini))

    //handle edge cases
    if(activation > parameters.maxi){
      activation = parameters.maxi}
    if(activation < parameters.mini){
      activation = parameters.mini}

    if(Math.abs(previousActivation - activation) < parameters.thresh)
      settled = true

    else
      settled = false
  }

  /**
    * A node class is part of the IAC network
    * This method is used to set the netInput value
    * The netInput value is calculated based on the nodes and ports associated with the node
    * @return Nothing
    */
  def setNetInput():Unit={
    var excitation:Double  = 0.0
    var inhibition:Double  = 0.0

    for(c <- connectedNodes)
    {
      if(c.weight >= 0){
        excitation = excitation + (c.weight*c.connection.activation)}
      else if(c.weight < 0 ){
        inhibition = inhibition + (c.weight*c.connection.activation)}

    }

    var portactivation:Double = 0
    for(port <- ports){
      portactivation = portactivation + port.returnActivation()
    }
    netInput = excitation+inhibition+portactivation
  }

  /**
    * A function to check if a node has settled
    * @return INT
    */
  def notSettled():Int={
    if(settled == false)
      return 1
    else
      return 0
  }

  /**
    * This method is used to print a node's ID and activation
    * @return Nothing
    */
  def printNode():Unit={
    println(ID,activation)
    println("Child Nodes: ")
    for(n <- connectedNodes){println(n.connection.ID,n.weight)}
    println()
  }


  /**
    * This method is used to return a node's Details in Html friendly format
    * @return String
    */
  def getNodeDetail():String={
    var value = "<b> Node: "
    value += ID.toString()+"</b> with activation of "+activation.toString()
    value += "<br/> Connections: <br/>"
    for(n <- connectedNodes){
      value += n.connection.ID.toString() +" with a weight of "+n.weight.toString()+"<br/>"
    }
    value += "<br/>"
    return value
  }

  /**
    * This method is used to return a list of node IDS in the network
    * @return ListBuffer[String]
    */
  def getVertexID():ListBuffer[Tuple2[String,Int]]={
    var vertex = new ListBuffer[Tuple2[String,Int]]
    var value = 1
    if(activation < 0)
      value = -1
    vertex += Tuple2(ID,value)
    return vertex
  }

  /**
    * This method is used to return a list of connections in the network
    * @return ListBuffer[String]
    */
  def getConnections():ListBuffer[Tuple3[String,String,Int]]={
    var edges = new ListBuffer[Tuple3[String,String,Int]]
    for(edge <- connectedNodes){
      var value = 1
      if(edge.weight < 0)
        value = -1
      edges += new Tuple3(ID,edge.connection.ID,value)
    }
    return edges
  }

  /**
    * This method sets up automatic competition between Hypothesis nodes
    * Hypothesis nodes that explain a common evidence, but do not explain each other compete
    * @return Nothing
    */
  private def setupCompetitionHypothesis():Unit={
    //check if the node is a hypothesis
    if(hypNode){
      //scan all the nodes and get the evidences and check for common hypothesis
      for(e <- connectedNodes){
        //node is an evidence node
        if(e.connection.dataNode && e.weight > 0){
          for(c <- e.connection.connectedNodes){
            if(c.connection.ID != ID && c.connection.hypNode){
              //check if there is already a connection else give inhibition link
              if(checkNode(c.connection.ID) == 0 && c.connection.ID != "Sp" && this.ID != "Sp" && c.weight > 0){
                var e1 = new NodeEdge(c.connection,parameters.inhibit)
                var e2 = new NodeEdge(this,parameters.inhibit)
                c.connection.addConnection(e2)
                this.addConnection(e1)
              }
            }
          } } } } }

  /**
    * This method sets up automatic competition between belief nodes
    * Belief nodes that deduce a common evidence or Hypothesis, but do not deduce each other compete
    * @return Nothing
    */
  private def setupCompetitionBeliefs():Unit={
    //check if the node is a belief Node
    if(beliefNode){
      //scan all the nodes and get the evidences and check for common hypothesis
      for(e <- connectedNodes){
        //node is an evidence node
        if((e.connection.dataNode || e.connection.hypNode) && e.weight > 0){
          for(c <- e.connection.connectedNodes){
            if(c.connection.ID != ID && c.connection.beliefNode){
              //check if there is already a connection else give inhibition link
              if(checkNode(c.connection.ID) == 0 && c.connection.ID != "Sp" && this.ID != "Sp" && c.weight > 0){
                var e1 = new NodeEdge(c.connection,parameters.inhibit)
                var e2 = new NodeEdge(this,parameters.inhibit)
                c.connection.addConnection(e2)
                this.addConnection(e1)
              }
            }
          } } } }
  }

  /**
    * This method sets up automatic competition between action nodes
    * Action nodes that facilitate a common goal, but do not facilitate each other compete
    * @return Nothing
    */
  private def setupCompetitionActions():Unit={
    //check if the node is a belief Node
    if(actionNode){
      //scan all the nodes and get the evidences and check for common hypothesis
      for(e <- connectedNodes){
        //node is an evidence node
        if(e.connection.goalNode && e.weight > 0){
          for(c <- e.connection.connectedNodes){
            if(c.connection.ID != ID && c.connection.actionNode){
              //check if there is already a connection else give inhibition link
              if(checkNode(c.connection.ID) == 0 && c.connection.ID != "Sp" && this.ID != "Sp" && c.weight > 0){
                var e1 = new NodeEdge(c.connection,parameters.inhibit)
                var e2 = new NodeEdge(this,parameters.inhibit)
                c.connection.addConnection(e2)
                this.addConnection(e1)
              }
            }
          } } } }
  }


  /**
    * This method sets up automatic competition between nodes
    * @return Nothing
    */
  def setupCompetition():Unit={
    setupCompetitionHypothesis()
    setupCompetitionBeliefs()
    setupCompetitionActions()
  }

  /**
    * This method is used to check if a node is a child of another node
    * @param id name of the node
    * @return Int
    */
  private def checkNode(id:String):Int={
    for(c <- connectedNodes){
      if(c.connection.ID == id)
        return 1
    }
    return 0
  }

  /**
    * This method is used establish data priority in explanatory coherence
    * @param special This is the special node with higher priority
    * @return Nothing
    */
  def establishDataPriority(special: Node): Unit = {
    //check if the node is a data node and establish connection
    if(dataNode) {
      var spEdge = new NodeEdge(special, parameters.data_excite)
      addConnection(spEdge)
    }
  }

}