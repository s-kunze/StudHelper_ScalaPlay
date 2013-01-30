package controllers

import org.squeryl.PrimitiveTypeMode._
import com.codahale.jerkson.Json
import play.api.http.HeaderNames
import play.api.mvc.Action
import play.api.mvc.Controller
import transfer._
import models.StudhelperDb
import models.{User => UserModel}
import transfer.NewUserTransfer
import java.io.UnsupportedEncodingException
import org.apache.commons.codec.binary.Base64
import exception.WrongPasswordException
import exception.NoUserFoundException
import auth.AuthorizationHandler
import auth.AuthorizationHandler
import play.api.Logger
import play.api.mvc.AnyContentAsJson
import scala.collection.mutable.LinkedList
import transfer.LectureMarkTransfer

object User extends Controller {
  
  def getAll = Action {
    val json = transaction {
	  val users: Iterable[UserTransfer] = from(StudhelperDb.user)(u => select(u))
      Json.generate(users)
	}  
	
    Ok(json) as("application/json")
  }
  
  def get(id: Long) = Action {
    transaction {
      try { 
        val user: UserTransfer = StudhelperDb.user.where(u => u.id === id).single
        val jsonString = Json.generate(user)

        Ok(jsonString) as("application/json")
      } catch {
        case e:RuntimeException => NotFound
        case _ => InternalServerError
      }
    }
  }
  
  def create(id: Long) = Action { implicit request => {
      val jsonString = request.body
      
      jsonString match {
        case AnyContentAsJson(x) => {
          val user: UserModel = Json.parse[NewUserTransfer](x.toString())
          
          transaction {
            val degreeCourse = StudhelperDb.degreeCourse.where(d => d.id === id).single
            
            val newUser = degreeCourse.users.assign(user)
            
            StudhelperDb.user insert newUser
            
	        newUser.id match {
	          case 0 => InternalServerError
		      case _ => Created
		    }
          }
        }
        case _ => InternalServerError

      }
  	}
  }
 
  def update = Action { implicit request => {
      val jsonString = request.body
      
      jsonString match {
        case AnyContentAsJson(x) => {
          val user = Json.parse[UserTransfer](x.toString())
          
          val rows = transaction {
            StudhelperDb.user.update(u =>
              where(u.id === user.id)
              set(
                  u.firstname := user.firstname,
                  u.lastname := user.lastname,
                  u.username := user.username,
                  u.creditPoints := user.creditpoints
              )
            )
          }
          
          if(rows > 0)
            Ok
          else 
            NotFound
        }
        case _ => InternalServerError
      }
    
    }
  }
  
  def delete(id: Long) = Action { 
    transaction {
      val row = StudhelperDb.user.deleteWhere(u => u.id === id)
      
      if(row > 0) 
        Ok
      else
        NotFound
    }
  }
  
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
	    	        val transfer = auth.authUser(username, password)
	    	        
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
  
  def getDegreeCourse(id: Long) = Action {
    transaction {
        val user = StudhelperDb.user.where(u => u.id === id).single

        user.degreeCourseId match {
          case Some(x) => {            
            val degreeCourse: DegreeCourseTransfer = StudhelperDb.degreeCourse.where(d => d.id === x).single
            
            val jsonString = Json.generate(degreeCourse)
            
       		Ok(jsonString) as("application/json")            
          }
          case _ => NotFound
        }
        
    }
  }
  
  def getLectures(id: Long) = Action {
    val json = transaction {
	  val user = StudhelperDb.user.where(u => u.id === id).single
	  val lectures = user.lectures
	  
	  var result = new LinkedList[LectureMarkTransfer]
	  
	  for(lecture <- lectures) {
		  val lectureToUser: LectureMarkTransfer = StudhelperDb.lectureToUser.where(l => l.lectureId === lecture.id and l.userId === user.id).single
	  
		  lectureToUser.name = lecture.name
		  lectureToUser.creditPoints = lecture.creditPoints
		  
		  result = result.append(LinkedList(lectureToUser))
	  }
	  
      Json.generate(result)
	}  
	
    Ok(json) as("application/json")
  }

}