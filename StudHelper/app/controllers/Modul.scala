package controllers

import org.squeryl.PrimitiveTypeMode._
import com.codahale.jerkson.Json
import play.api.mvc.Controller
import play.api.mvc.Action
import transfer._
import models.StudhelperDb
import models.{Modul => ModulModel, Lecture => LectureModel}
import play.api.Logger
import play.api.mvc.AnyContentAsJson

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
      val jsonString = request.body
      
      jsonString match {
        case AnyContentAsJson(x) => {
          val modul: ModulModel = Json.parse[ModulTransfer](x.toString())

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
      val jsonString = request.body
      
      jsonString match {
        case AnyContentAsJson(x) => {
          val modul = Json.parse[ModulTransfer](x.toString())
          
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
	  Logger.debug("Create lecture")
      val jsonString = request.body
      
      Logger.debug(jsonString.toString)
      
      jsonString match {
        case AnyContentAsJson(x) => {
          val lecture: LectureModel = Json.parse[LectureTransfer](x.toString())

          Logger.debug("Lecture: " + lecture.name)
          
          transaction {
            val modul = StudhelperDb.modul.where(m => m.id === id).single

            Logger.debug("Modul: " + modul.name)
            
            val lectureDb = StudhelperDb.lecture.where(l => l.name === lecture.name)
            
            val newModulToLecture = lectureDb.headOption match {
              case None => { 
                Logger.debug("noch nicht vorhanden")
                StudhelperDb.lecture insert lecture
                modul.lectures.assign(lecture)
              }
              case Some(x) => {
            	  Logger.debug("noch vorhanden")
            	  modul.lectures.assign(x)
              }
            }
       		
            Logger.debug("insert")
            
            StudhelperDb.modulToLecture insert newModulToLecture

            Logger.debug("ready")
            Logger.debug("" + newModulToLecture.lectureId)
           
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
  
  def getDepartment(id: Long) = Action {
    transaction {
      val modul = StudhelperDb.modul.where(m => m.id === id).single
      
      val part = modul.part.head
      
      val par = StudhelperDb.part.where(p => p.id === part.id).single
      
      val degreeCourse = par.degreeCourse.head
      
      val deg = StudhelperDb.degreeCourse.where(d => d.id === degreeCourse.id).single
      
      val department: DepartmentTransfer = deg.department.head
      
      val jsonString = Json.generate(department)
      
      Logger.debug(jsonString)
      
      Ok(jsonString) as("application/json") 
    }
  }
  
}