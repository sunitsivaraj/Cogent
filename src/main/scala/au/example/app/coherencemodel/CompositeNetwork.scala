package au.example.app.coherencemodel

import scala.collection.mutable.ListBuffer

/**
  * this class is inherited by the Node and Network class
  * the main purpose of this class is to use node and network class interchangeably
 *
  * @author  Sunit Sivaraj
  * @version 1.0
  * @since   2016-05-31
  */
trait CompositeNetwork {
  var ID:String = null
  var parentID:String = null
  def update():Unit
  def setNetInput():Unit
  def notSettled():Int
  def printNode():Unit
  def setupCompetition():Unit
  def getNodeDetail():String
  def getVertexID(): ListBuffer[Tuple2[String,Int]]
  def getConnections(): ListBuffer[Tuple3[String,String,Int]]
  def establishDataPriority(special:Node):Unit
}