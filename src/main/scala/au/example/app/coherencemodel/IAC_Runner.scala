package au.example.app.coherencemodel

/**
  * a class to run the Interactive Activation Competition Network
  * @author  Sunit Sivaraj
  * @version 1.0
  * @since   2016-05-31
  * @constructor Initialize Networks of type ListBuffer
  */
class  IAC_Runner(root:Network) {
  //function to run a cycle of network
  var networkSettled:Boolean = false

  /**
    * A cycle function is part of the IAC network
    * This method is used to cycle through the network until the network settles
    * @return Nothing
    */
  def cycle():Unit={
    var cycle_count = 0
    while(networkSettled == false){
      //a synchronous update
      root.setNetInput()
      root.update()
      var check:Int = root.notSettled()
      if(check == 0){
        //root.printNode()
        networkSettled = true
      }
      cycle_count += 1
      if(cycle_count > parameters.max_iter){
        networkSettled = true
      }
    }
  }

  /**
    * This method is used to set up competition for echo 2
    * The process starts with the root node and spreads to all of them
    * @return Nothing
    */
  //this is the ECHO2 automatic competition setup
  def establishCompetition():Unit={
    //finding the root node and running the model
    root.setupCompetition()
  }
}