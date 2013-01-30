package auth

import org.squeryl.PrimitiveTypeMode._
import models.{StudhelperDb, User}
import transfer.AuthTransfer
import exception.WrongPasswordException
import exception.NoUserFoundException
import play.api.Logger
import transfer.UserType


class AuthorizationHandler {

  def authUser(username: String, password: String) : AuthTransfer = {
    transaction {
      try {
    	val user = StudhelperDb.user.where(u => u.username === username).single
    	
    	Logger.debug("  User: " + user.id)
    	Logger.debug("  User: " + user.username)
    	
    	if(!password.equals(user.password)) {
    		Logger.debug("Wrong password")
    		throw new WrongPasswordException("Wrong password");
		}

    	AuthTransfer(user.id,UserType.USER,user.username)
      } catch {
      	case e: Exception => throw new NoUserFoundException("No User Found")
      }
    } 
  }
  
  def authAdmin(username: String, password: String) : AuthTransfer = {
    transaction {
      try {
    	val user = StudhelperDb.admins.where(u => u.username === username).single
    	
    	if(!password.equals(user.password)) {
    		throw new WrongPasswordException("Wrong password");
		}
    	
    	AuthTransfer(user.id,UserType.ADMIN,user.username)
      } catch {
      	case e: Exception => throw new NoUserFoundException("No User Found")
      }
    } 
  }
  
  
}
