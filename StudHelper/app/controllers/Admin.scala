package controllers

import org.squeryl.PrimitiveTypeMode._
import com.codahale.jerkson.Json
import play.api.mvc.Action
import play.api.mvc.Controller
import transfer._
import models.StudhelperDb
import models.{Admin => AdminModel}
import play.api.http.HeaderNames
import org.apache.commons.codec.binary.Base64
import auth.AuthorizationHandler
import exception.WrongPasswordException
import exception.NoUserFoundException
import java.io.UnsupportedEncodingException
import play.api.Logger

object Admin extends Controller {
  
  def auth = Action { implicit request => {
	  	try {
	    	val authString = request.headers.get(HeaderNames.AUTHORIZATION) 
	
	    	authString match {
	    	  case Some(x) => {
	    	    val authStr = new String(Base64.decodeBase64(x.getBytes("UTF-8")), "UTF-8")
	    	    
	    	    val auth = authStr.split(":")
	    	    
	    	    if(2 == auth.length) {
	    	      val username = auth(0)
	    	      val password = auth(1)
	    	      
	    	      try {
	    	        val auth = new AuthorizationHandler
	    	        val transfer = auth.authAdmin(username, password)
	    	        
	    	        val jsonString = Json.generate(transfer)
	    	        
	    	        Ok(jsonString) as("application/json")
	    	      } catch { 
					  case e: WrongPasswordException => Unauthorized
					  case e: NoUserFoundException => NotFound
					  case e: Exception => InternalServerError
	    	      }
	    	    } else {
	    	    	BadRequest	    	      
	    	    }
	    	  }
	    	  case _ => BadRequest
	    	}
	    } catch {
	      case e:UnsupportedEncodingException => InternalServerError
	    }
    
  	}
  }
}