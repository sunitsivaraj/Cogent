package au.example.app

import edu.uci.ics.jung.graph._
import edu.uci.ics.jung.graph.util.Graphs

import scala.util.parsing.combinator.JavaTokenParsers

/**
  * Created by Levent Yilmaz on 9/21/2016
  * Last Modified by Sunit Sivaraj on 04/23/2018
  */


class CoherenceModel(mName:String) {
  var g: Graph[String,Int] = null;
  var og: ObservableGraph[String,Int] = null;
  var ig = Graphs.synchronizedUndirectedGraph[String,Int](new  UndirectedSparseMultigraph[String,Int]())
  var activations:Map[String,Double]  = Map()
  var edgeWeights:Map[Int,Double] = Map()
  og = new ObservableGraph[String,Int](ig)
  g = og;

  def addNode(nodeName: String, defaultActivation: Double): Unit = {
    g.addVertex(nodeName)
    activations+=(nodeName -> defaultActivation)
  }

  def addEdge(source: String, target: String, weight: Double): Unit = {
    val edgeCount = g.getEdgeCount
    g.addEdge(edgeCount,source, target)
    edgeWeights+= (edgeCount -> weight)
  }

  def is (body : => Unit): CoherenceModel = {
    body
    this
  }

  def evidence(eName: String, defaultActivation:Double) = {
    addNode(eName,defaultActivation)
  }

  def facilitate(src: String, tgt: String, strength: Double)  = {
    addEdge(src,tgt,strength)
  }

  def graphIterator() {

    var V = g.getVertices
    var itr = V.iterator()
    System.out.println("Vertex List: ")
    while (itr.hasNext()) {
      var myN = itr.next()
      System.out.print("Node name: "+myN+ " ")
      System.out.println("Activation: "+ activations(myN))

      var incIter = g.getIncidentEdges(myN).iterator()
      while (incIter.hasNext()) {
        var myEdge = incIter.next()
        System.out.println("Incident edge number: "+ myEdge+" Weight is " + edgeWeights(myEdge) +" Source is "+g.getEndpoints(myEdge).getFirst+" Target is "+ g.getEndpoints(myEdge).getSecond)
        System.out.println("The opposite vertex of"+myN+" in edge"+ myEdge+" is  "+ g.getOpposite(myN,myEdge))
      }
    }
  }
}


object CoherenceModel  {
  var cm : CoherenceModel = _
  def apply(x:String): CoherenceModel = {
    cm = new CoherenceModel(x)
    cm
  }

  def evidence(eName: String, defaultActivation:Double) = {
    cm.addNode(eName,defaultActivation)
  }

  def facilitate(src: String, tgt: String, strength: Double)  = {
    cm.addEdge(src,tgt,strength)
  }

}

// Defining the structure
abstract class Node
case class CogModel(netspec: Network, portMappings:Option[List[Mapping]], behaviorMappings:Option[List[BMapping]])
case class Network(name: String, portlist: Option[List[Port]],portmapper: Option[Map[Port, Node]],evidences: List[Evidence], beliefs: List[Belief], hypotheses: List[Hypothesis], goals: List[Goal], actions: List[Action], coherenceConstraints: List[Constraint], analogies:List[Analogy],subNets: Option[List[Network]])
case class Port(name:String, ptype:String)
case class Evidence(name:String,explanation:Option[String], defaultActivation:Double) extends Node
case class Belief(name:String,explanation:Option[String], defaultActivation:Double) extends Node
case class Hypothesis(name:String,explanation: Option[String],defaultActivation:Double) extends Node
case class Goal(name:String,explanation: Option[String],defaultActivation:Double) extends Node
case class Action(name:String,explanation: Option[String],defaultActivation:Double) extends Node
case class Constraint (source:List[String], target:String, ctype: String, strength:Double)
case class Analogy(hyp_list: List[String], evi_list: List[String], strength: Double)
case class Cogent(name: String, dataModel: List[String], behaviorModel: List[String], cModel:CogModel)
case class Mapping(outNet: String, outPort: String, inpNet: String, inpPort: String, weight:Float)
case class BMapping(NetName: String, NodeName: String, BehName: String)


// Defining the grammar
class CoherenceNet extends JavaTokenParsers {
  def cogentspec: Parser[Cogent] = "cogent"~>ident~dataspec~bspec~cmspec ^^
    {case name~data~behavior~cm => Cogent(name.toString(),data,behavior,cm)}
  def dataspec: Parser[List[String]] = "@data"~>repsep(datastmt,";") ^^ (List() ++ _)
  def datastmt: Parser[String] = stringLiteral ^^ {case stmt => stmt}
  def bspec: Parser[List[String]] = "@behavior"~>repsep(datastmt,";") ^^ (List() ++ _)
  def bstmt: Parser[String] = stringLiteral ^^ {case stmt => stmt}
  def cmspec: Parser[CogModel] = "@cognitiveModel"~>netspec~opt(inoutmappinglist)~opt(behmappinglist)<~"endcogent" ^^
    {case netspec~inoutmappinglist~behmappinglist => CogModel(netspec,inoutmappinglist, behmappinglist)}

  def inoutmappinglist: Parser[List[Mapping]] = "@portmapping"~>rep(inoutmappingmember) ^^  (List() ++ _)
  def inoutmappingmember: Parser[Mapping] = "outp:"~>ident~"."~ident~"connects"~"inp:"~ident~"."~ident~"at"~floatingPointNumber ^^
    {case outNet~"."~outPort~"connects"~"inp:"~inpNet~"."~inpPort~"at"~value => Mapping(outNet.toString(),outPort.toString(),inpNet.toString(),inpPort.toString(),value.toFloat)}

  def behmappinglist: Parser[List[BMapping]] = "@behaviormapping"~>rep(behmappingmember) ^^  (List() ++ _)
  def behmappingmember: Parser[BMapping] = ident~"."~ident~"mapsto"~ident ^^
    {case netName~"."~nodeName~"mapsto"~behName => BMapping(netName.toString(), nodeName.toString(), behName.toString())}

  def netspec: Parser[Network] = "net"~>ident~opt(portlist)~opt(portmapper)~evidencelist~belieflist~hypothesislist~goallist~actionlist~constraintlist~analogylist~opt(netlist)<~"endnet" ^^
    {case ident~portlist~portmapper~evidencelist~belieflist~hypothesislist~goallist~actionlist~constraintlist~analogylist~netlist =>
      Network(ident.toString(),portlist,portmapper,evidencelist,belieflist,hypothesislist,goallist,actionlist,constraintlist,analogylist,netlist)}

  def netlist: Parser[List[Network]] = rep(netspec) ^^ (List() ++ _)
  def portlist : Parser[List[Port]] = "("~>inportlist~outportlist<~")" ^^
    {case inportlist~outportlist => inportlist ::: outportlist}
  def inportlist: Parser[List[Port]] = "inp"~":"~>repsep(inportmember, ",")<~";" ^^ (List() ++ _)
  def outportlist: Parser[List[Port]] = "outp"~":"~>repsep(outportmember, ",") ^^ (List() ++ _)
  def inportmember : Parser[Port] = ident ^^ {case name => Port(name.toString(),"in")}
  def outportmember : Parser[Port] = ident ^^ {case name => Port(name.toString(),"out")}

  def portmapper: Parser[Map[Port,Node]] = "["~>inportmapper~outportmapper<~"]" ^^
    {case inportmapper~outportmapper => inportmapper ++ outportmapper}
  def inportmapper: Parser[Map[Port,Node]] = repsep(inportmap, ",")<~";" ^^ (Map() ++ _)
  def outportmapper: Parser[Map[Port,Node]] = repsep(outportmap, ",") ^^ (Map() ++ _)
  def inportmap : Parser[(Port,Node)] = ident~"->"~ident ^^
    {case src~"->"~tgt => (Port(src.toString(),"in"),Evidence(tgt.toString(),Some(""),0.2))}
  def outportmap: Parser[(Port,Node)] =  ident~"->"~ident ^^
    {case src~"->"~tgt => (Port(tgt.toString(),"out"),Hypothesis(src.toString(),Some(""),0.2))}


  def evidencelist: Parser[List[Evidence]] = "@percepts"~>rep(evidencemember) ^^  (List() ++ _)
  def evidencemember: Parser[Evidence] = "evidence("~>ident~opt(explanation)~","~floatingPointNumber<~")" ^^
    {case ident~expl~","~value => Evidence(ident.toString(),expl,value.toDouble) }
  def explanation : Parser[String] = ":"~>stringLiteral ^^ {case expl => expl}

  def belieflist: Parser[List[Belief]] = "@beliefs"~>rep(beliefmember) ^^  (List() ++ _)
  def beliefmember: Parser[Belief] = "belief("~>ident~opt(explanation)~","~floatingPointNumber<~")" ^^
    {case ident~expl~","~value => Belief(ident.toString(),expl,value.toDouble) }

  def hypothesislist: Parser[List[Hypothesis]] = "@explanations"~>rep(hypothesismember) ^^  (List() ++ _)
  def hypothesismember: Parser[Hypothesis] = "hypothesis("~>ident~opt(explanation)~","~floatingPointNumber<~")" ^^
    {case ident~expl~","~value => Hypothesis(ident.toString(),expl,value.toDouble) }

  def goallist: Parser[List[Goal]] = "@goals"~>rep(goalmember) ^^  (List() ++ _)
  def goalmember: Parser[Goal] = "goal("~>ident~opt(explanation)~","~floatingPointNumber<~")" ^^
    {case ident~expl~","~value => Goal(ident.toString(),expl,value.toDouble) }


  def actionlist: Parser[List[Action]] = "@actions"~>rep(actionmember) ^^  (List() ++ _)
  def actionmember: Parser[Action] = "action("~>ident~opt(explanation)~","~floatingPointNumber<~")" ^^
    {case ident~expl~","~value => Action(ident.toString(),expl,value.toDouble) }


  def constraintlist: Parser[List[Constraint]] = "@constraints"~>rep(constraintmember) ^^  (List() ++ _)
  def constraintmember: Parser[Constraint] =  basicconstraint | compoundconstraint

  def basicconstraint: Parser[Constraint] =
    ident~"explains"~ident~"at"~floatingPointNumber ^^
      {case src~"explains"~tgt~"at"~value => Constraint(src.toString :: List(),tgt.toString,"explains",value.toDouble)} |
    ident~"contradicts"~ident~"at"~floatingPointNumber ^^
      {case src~"contradicts"~tgt~"at"~value => Constraint(src.toString :: List(),tgt.toString,"contradicts",value.toDouble)} |
    ident~"deduces"~ident~"at"~floatingPointNumber ^^
      {case src~"deduces"~tgt~"at"~value => Constraint(src.toString :: List(),tgt.toString,"deduces",value.toDouble)} |
    ident~"facilitates"~ident~"at"~floatingPointNumber ^^
      {case src~"facilitates"~tgt~"at"~value => Constraint(src.toString :: List(),tgt.toString,"facilitates",value.toDouble)} |
    ident~"incompatible"~ident~"at"~floatingPointNumber ^^
      {case src~"incompatible"~tgt~"at"~value => Constraint(src.toString :: List(),tgt.toString,"incompatible",value.toDouble)} |
    ident~"triggers"~ident~"at"~floatingPointNumber ^^
        {case src~"triggers"~tgt~"at"~value => Constraint(src.toString :: List(),tgt.toString,"triggers",value.toDouble)}


  def compoundconstraint : Parser[Constraint] = srclist~"explains"~ident~"at"~floatingPointNumber ^^
    {case srclist~"explains"~tgt~"at"~value => Constraint(srclist,tgt,"explains",value.toDouble) }
  def srclist: Parser[List[String]] = "["~>repsep(ident, ",")<~"]" ^^ (List() ++ _)


  def analogylist :Parser[List[Analogy]] = "@analogies"~>rep(analogymember) ^^ (List() ++ _)
  def analogymember: Parser[Analogy] =  "analogous"~hyplist~evilist~"at"~floatingPointNumber ^^
    {case "analogous"~hyplist~evilist~"at"~value => Analogy(hyplist, evilist, value.toDouble)}
  def hyplist: Parser[List[String]] = "("~>repsep(ident, ",")<~")" ^^ (List() ++ _)
  def evilist: Parser[List[String]] = "("~>repsep(ident, ",")<~")" ^^ (List() ++ _)
}


object NetExpr extends CoherenceNet {
  var result:String = null
  var myNet:Network = null
  var port_mapping:List[Mapping] = null
  var behave_mapping:List[BMapping] = null

  def parseExpression(inp: String): Unit = {
    result = "Successful parsing, Valid Input"
    if ((parseAll(cmspec, inp).successful) == true) {

      //println("get: " + parseAll(cmspec, inp).get)
      println(parseAll(cmspec,inp))
      myNet  = parseAll(cmspec, inp).get.asInstanceOf[CogModel].netspec
      port_mapping = parseAll(cmspec, inp).get.portMappings.head
      behave_mapping = parseAll(cmspec, inp).get.behaviorMappings.head
    }
      // if the cognitive specification is invalid
    else {println("invalid input")
      result = "Unsuccessfull parsing, Invalid Input"
    }
  }
}