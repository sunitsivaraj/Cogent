package au.example.app.coherencemodel

import scala.collection.mutable.ListBuffer


/**
  * a class to have a network representation
  * This class can contain list of nodes or network objects
  * @author  Sunit Sivaraj
  * @version 1.0
  * @since   2016-05-31
  * @constructor get the Network ID of type String
  */
class Network extends CompositeNetwork{
  def this(id:String){
    this()
    ID = id
  }
  var network_elements = new ListBuffer[CompositeNetwork]

  /**
    * this function can be used to add an node or an network to a network
    * a Composite Design pattern is established
    * @param element This is the array of CompositeNetwork object
    * @return Nothing
    */
  def add(element: CompositeNetwork*):Unit={
    for(item <- element){
      item.parentID = ID
      network_elements += item
    }
  }

  /**
    * A Network class is a part of the IAC network
    * This method is the update function in the IAC network
    * This method passes its control to all its child which could be a node or network
    * @return Nothing
    */
  def update():Unit={
    for(node <- network_elements){
      node.update()
    }
  }

  /**
    * A node class is part of the IAC network
    * This method is used to set the netInput value
    * The netInput value is calculated based on the nodes and ports associated with the node
    * This method passes its control to its child nodes which could be a node or network
    * @return Nothing
    */
  def setNetInput():Unit={
    for(node <- network_elements){
      node.setNetInput()
    }
  }

  /**
    * A function to check if a node has settled
    * This function passes its control to all its child nodes
    * @return INT
    */
  def notSettled():Int={
    var sum:Int = 0
    for(node <- network_elements){
      sum += node.notSettled()
    }
    return sum
  }


  /**
    * This method is used to print a node's ID and activation
    * This function passes its control to all its child nodes
    * @return Nothing
    */
  def printNode():Unit={
    for(node <- network_elements){
      node.printNode()
    }
  }


  /**
    * This method is used to get a Node's details in Html friendly format
    * This function passes its control to all its child nodes
    * @return String
    */
  def getNodeDetail():String={
    var value = ""
    for(node <- network_elements){
      value += node.getNodeDetail()
    }
    return value
  }

  /**
    * This method is used to return a list of node IDS in the network
    * @return ListBuffer[String]
    */
  def getVertexID():ListBuffer[Tuple2[String,Int]]={
    var vertices = new ListBuffer[Tuple2[String,Int]]
    for(item <- network_elements) {
      vertices ++= item.getVertexID()
    }
    return vertices
  }


  /**
    * This method is used to return a list of connections in the network
    * @return ListBuffer[String]
    */
  def getConnections():ListBuffer[Tuple3[String,String,Int]]={
    var edges = new ListBuffer[Tuple3[String,String,Int]]
    for(item <- network_elements){
      edges ++= item.getConnections()
    }
    return edges
  }

  /**
    * This method sets up automatic competition in ECHO 2
    * This function passes its control to all its child nodes
    * @return Nothing
    */
  def setupCompetition():Unit={
    for(c <- network_elements){
      c.setupCompetition()
    }
  }

  /**
    * This method sets up data priority for Explanatory coherence
    * This function passes its control to all its child nodes
    * @return Nothing
    */
  def establishDataPriority(special: Node): Unit = {
    for(c <- network_elements){
      c.establishDataPriority(special)
    }
  }
}