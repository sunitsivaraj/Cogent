package au.example.app.coherencemodel

/**
  * a class to represent a connection between an input and a output port
  * @author  Sunit Sivaraj
  * @version 1.0
  * @since   2016-05-31
  * @constructor outport of type Outputport and strength of type Double
  */
class PortEdge(outPort:Outputport,strength:Double) {
  private var out:Outputport = outPort
  var weight:Double = strength
  var outport_name:String = outPort.name

  /**
    * A node class can have multiple ports associated with it
    * @return Nothing
    */
  def returnActivation():Double={
    //divided by 2 as edges get repeated twice for each node
    return out.getHarmony()/2
  }
}
