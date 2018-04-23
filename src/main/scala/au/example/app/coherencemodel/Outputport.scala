package au.example.app.coherencemodel

/**
  * a class to represent a output port of a Node class
  * @author  Sunit Sivaraj
  * @version 1.0
  * @since   2016-05-31
  * @constructor get portname of type String and net of type Network
  */
class Outputport(portname:String, net:Network) {
  private var reference:Node = null
  var name:String = portname
  var reference_name:String = null
  private var network:Network = net

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
    * A public method to get the harmony of the network - calls the recursive method
    * @return Nothing
    */
  def getHarmony():Double={
    getHarmony(network)
  }

  /**
    * get the harmony of the network
    * @return Harmony
    */
  private def getHarmony(net:Network):Double={
    var harmony:Double = 0
    for(item <- net.network_elements){
      if(item.isInstanceOf[Node]){harmony = harmony+item.asInstanceOf[Node].getHarmony()}
      else {harmony = harmony + getHarmony(item.asInstanceOf[Network])}
    }
    return harmony
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