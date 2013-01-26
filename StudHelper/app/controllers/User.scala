package controllers

import org.squeryl.PrimitiveTypeMode._
import com.codahale.jerkson.Json
import play.api.mvc.Action
import play.api.mvc.Controller
import transfer._
import models.StudhelperDb
import models.{User => UserModel}
import transfer.NewUserTransfer

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
  
  def create(id: Long) = TODO
 
  def update = Action { implicit request => {
      val jsonString = request.body.asText
    
      jsonString match {
        case Some(x) => {
          val user = Json.parse[UserTransfer](x)
          
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
  
  def auth = TODO
  
  def getDegreeCourse(id: Long) = Action {
    transaction {
      try { 
        val user = StudhelperDb.user.where(u => u.id === id).single
        
        val degreeCourse = StudhelperDb.degreeCourse.where(d => d.id === user.degreeCourseId)
        
        val jsonString = Json.generate(degreeCourse)

        Ok(jsonString) as("application/json")
      } catch {
        case e:RuntimeException => NotFound
        case _ => InternalServerError
      }
    }
  }
  
  def getLectures(id: Long) = Action {
    val json = transaction {
	  val user = StudhelperDb.user.where(u => u.id === id).single
	  val lectures: Iterable[LectureTransfer] = user.lectures
	  
      Json.generate(lectures)
	}  
	
    Ok(json) as("application/json")
  }

}