package au.example.app.coherencemodel

/**
  * all global variables are declared here
  * other classes use this class to access these variables
  * @author  Sunit Sivaraj
  * @version 1.0
  * @since   2016-05-31
  */
object parameters {
  var maxi:Double = 1 //maximum activation value
  var mini:Double = -1 //minimum activation value
  var decay:Double = 0.05 //decay parameter
  var thresh:Double = 0.0001 //threshold
  var default_activation:Double = 0.01 //default activation
  var excite:Double = 0.04 //excitation link weight
  var inhibit:Double = -0.06 //inhibition link weight
  var data_excite:Double = 0.05 //data excitation link
  var max_iter:Int = 400 //max iterations for network
}