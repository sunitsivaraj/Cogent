package au.example.app.coherencemodel

/**
  * A class to have a representation for InputPort
  *
  * @author  Sunit Sivaraj
  * @version 1.0
  * @since   2016-05-31
  * @constructor get the PortName of type String and reference node of type Node
  */
class Inputport(portname:String) {
  private var reference:Node = null
  var reference_name:String = null
  var name:String = portname
  var connection_name:String = null
  var connection_weight:Double = 0.0

  /**
    * function to add the reference node
    * @param ref This is the Node associated with the Port
    * @return Nothing
    */
  def addAssociation(ref:Node):Unit={
    reference = ref
    reference_name = reference.ID
  }

  /**
    * function to add the portEdge to the Node class
    * @param edge This is the PortEdge object to be added to the node
    * @return Nothing
    */
  def addReference(edge:PortEdge):Unit={
    if(reference != null) {
      reference.addPortEdge(edge)
      connection_name = edge.outport_name
      connection_weight = edge.weight
    }
  }

  /**
    * function to check if the port is used
    * @return Nothing
    */
  def isUsed():Boolean={
    if(reference != null){return true}
    return false
  }
}