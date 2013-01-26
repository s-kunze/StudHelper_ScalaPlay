package controllers

import org.squeryl.PrimitiveTypeMode._
import com.codahale.jerkson.Json
import play.api.mvc.Controller
import play.api.mvc.Action
import transfer._
import models.StudhelperDb
import models.{Modul => ModulModel, Lecture => LectureModel}
import play.api.Logger

object Modul extends Controller {

  def getAll = Action {
    val json = transaction {
	  val moduls: Iterable[ModulTransfer] = from(StudhelperDb.modul)(m => select(m))
      Json.generate(moduls)
	}  
	
    Ok(json) as("application/json")
  }
  
  def get(id: Long) = Action {
    transaction {
      try { 
        val modul: ModulTransfer = StudhelperDb.modul.where(m => m.id === id).single
        val jsonString = Json.generate(modul)

        Ok(jsonString) as("application/json")
      } catch {
        case e:RuntimeException => NotFound
        case _ => InternalServerError
      }
    }
  }
  
  def create(id: Long) = Action { implicit request => {
      val jsonString = request.body.asText
      
      jsonString match {
        case Some(x) => {
          val modul: ModulModel = Json.parse[ModulTransfer](x)

          transaction {
            val part = StudhelperDb.part.where(p => p.id === id).single
       		val newModul = part.moduls.assign(modul)
            StudhelperDb.modul insert newModul
            
	        newModul.id match {
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
      val jsonString = request.body.asText
    
      jsonString match {
        case Some(x) => {
          val modul = Json.parse[ModulTransfer](x)
          
          val rows = transaction {
            StudhelperDb.modul.update(m =>
              where(m.id === modul.id)
              set(
                  m.name := modul.name,
                  m.creditPoints := modul.creditPoints
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
      val row = StudhelperDb.modul.deleteWhere(m => m.id === id)
      
      if(row > 0) 
        Ok
      else
        NotFound
    }
  }
  
  def getAllLecture(id: Long) = Action {
    transaction {
      try { 
        val modul = StudhelperDb.modul.where(m => m.id === id).single
        
        val lectures: Iterable[LectureTransfer] = modul.lectures
        
        val jsonString = Json.generate(lectures)

        Ok(jsonString) as("application/json")
      } catch {
        case e:RuntimeException => NotFound
        case _ => InternalServerError
      }
    }
  }
  
  def getLecture(modId: Long, lecId: Long) = Action {
    transaction {
	  try { 
        val lecture: LectureTransfer = StudhelperDb.lecture.where(l => l.id === lecId).single
	    
        val jsonString = Json.generate(lecture)
        
        Ok(jsonString) as("application/json")
      } catch {
        case e:RuntimeException => NotFound
        case e:Exception => { 
          Logger.error("", e)
          InternalServerError
        }
      }
    }
  }
  
  def createLecture(id: Long) = Action { implicit request => {
      val jsonString = request.body.asText
      
      jsonString match {
        case Some(x) => {
          val lecture: LectureModel = Json.parse[LectureTransfer](x)

          transaction {
            val modul = StudhelperDb.modul.where(m => m.id === id).single

            val lectureDb = StudhelperDb.lecture.where(l => l.name === lecture.name)
            
            val newModulToLecture = lectureDb.headOption match {
              case None => { 
                StudhelperDb.lecture insert lecture
                modul.lectures.assign(lecture)
              }
              case Some(x) => modul.lectures.assign(x)
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
  
  def getDepartment(id: Long) = TODO
  
}