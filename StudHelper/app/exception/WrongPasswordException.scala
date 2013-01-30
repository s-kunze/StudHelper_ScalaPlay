package exception

case class WrongPasswordException(val message: String) extends Exception