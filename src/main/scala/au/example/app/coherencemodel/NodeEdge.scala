package au.example.app.coherencemodel

/**
  * a class to represent an edge of a Node class
  * a Node class can have multiple NodeEdge objects
  * This class has details of the weight and the node information
  * @author  Sunit Sivaraj
  * @version 1.0
  * @since   2016-05-31
  * @constructor get ch of type Node and wt of Type Double
  */
class NodeEdge(ch:Node,wt:Double) {
  var connection:Node = ch
  var weight:Double = wt
}