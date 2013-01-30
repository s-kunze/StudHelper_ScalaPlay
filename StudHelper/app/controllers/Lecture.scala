package controllers

import org.squeryl.PrimitiveTypeMode._
import com.codahale.jerkson.Json
import play.api.mvc.Controller
import play.api.mvc.Action
import transfer._
import models.StudhelperDb
import models.{Lecture => LectureModel, LectureToUser => LectureToUserModel}
import play.api.mvc.AnyContentAsJson
import scala.tools.nsc.io.Logger
import play.api.Logger
import models.LectureToUser

object Lecture extends Controller {

  def getAll = Action {
    val json = transaction {
	  val lectures: Iterable[LectureTransfer] = from(StudhelperDb.lecture)(l => select(l))
      Json.generate(lectures)
	}  
	
    Ok(json) as("application/json")
  }
  
  def get(id: Long) = Action {
    transaction {
      try { 
        val lecture: LectureTransfer = StudhelperDb.lecture.where(l => l.id === id).single
        val jsonString = Json.generate(lecture)

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
          val lecture: LectureModel = Json.parse[LectureTransfer](x.toString())

          transaction {
            val modul = StudhelperDb.modul.where(m => m.id === id).single
            
            val lectureDb = StudhelperDb.lecture.where(l => l.name === lecture.name)
            
            val newModulToLecture = lectureDb.headOption match {
              case None => { 
                StudhelperDb.lecture insert lecture
                modul.lectures.assign(lecture)
              }
              case Some(x) => {
            	  modul.lectures.assign(x)
              }
            }
            
            StudhelperDb.modulToLecture insert newModulToLecture
           
	        newModulToLecture.lectureId match {
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
          val lecture = Json.parse[LectureTransfer](x.toString())
          
          val rows = transaction {
            StudhelperDb.lecture.update(l =>
              where(l.id === lecture.id)
              set(
                  l.name := lecture.name,
                  l.creditPoints := lecture.creditPoints
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
      val row = StudhelperDb.lecture.deleteWhere(l => l.id === id)
      
      if(row > 0) 
        Ok
      else
        NotFound
    }
  }
  
  def addLecture(lecId: Long, userId: Long) = Action { implicit request => {
	    val mark = request.queryString.get("mark")
	    
	    Logger.debug(mark.toString)
	    
	    mark match {
	      case Some(x) => {
	        transaction {
		        val lecture = StudhelperDb.lecture.where(l => l.id === lecId).single
		        
		        val user = StudhelperDb.user.where(u => u.id === userId).single
		        
		        Logger.debug(x.head)
		        
		        Logger.debug("Mark: " + x.head.toFloat)
		        
		        val row = StudhelperDb.lectureToUser.update(l =>
		              where(l.lectureId === lecture.id and l.userId === user.id)
		              set(
		                  l.lectureId := lecture.id,
		                  l.userId := user.id,
		                  l.mark := x.head.toFloat
		              )
		            )
		        
		        if(row == 0) {		
		          
		          val newLectureToUser = lecture.users.assign(user) 
		          newLectureToUser.mark = x.head.toFloat
		          
		          val id = StudhelperDb.lectureToUser insert newLectureToUser
		          
		          id.lectureId match {
		            case 0 => InternalServerError
		            case _ => Created
		          }
		          
		        } else {    
		            
		        	if(row > 0) 
		        		Created
				    else
				    	InternalServerError    
			        }
	        	}
		  }
	      case _ => BadRequest
	    }
  	}    
  }
  
}