package controllers

import org.squeryl.PrimitiveTypeMode._
import com.codahale.jerkson.Json
import play.api.mvc.Controller
import play.api.mvc.Action
import transfer._
import models.StudhelperDb
import models.{Part => PartModel, DegreeCourse => DegreeCourseModel}
import play.api.Logger
import play.api.mvc.AnyContentAsJson

object DegreeCourse extends Controller {

  def getAll = Action {
    Logger.debug("Get DegreeCourses")
    val json = transaction {
	  val degreeCourses: Iterable[DegreeCourseTransfer] = from(StudhelperDb.degreeCourse)(d => select(d))
      Json.generate(degreeCourses)
	}  
	
    Ok(json) as("application/json")
  }           
  
  def get(id: Long) = Action {
    Logger.debug("Get DegreeCourse")
    transaction {
      try { 
        val degreeCourse: DegreeCourseTransfer = StudhelperDb.degreeCourse.where(d => d.id === id).single
        val jsonString = Json.generate(degreeCourse)

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
          val degreeCourse: DegreeCourseModel = Json.parse[DegreeCourseTransfer](x.toString())

          transaction {
            val department = StudhelperDb.department.where(d => d.id === id).single
       		val newDegreeCourse = department.degreeCourses.assign(degreeCourse)
            StudhelperDb.degreeCourse insert newDegreeCourse
            
	        newDegreeCourse.id match {
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
          val degreeCourse = Json.parse[DegreeCourseTransfer](x.toString())
          
          val rows = transaction {
            StudhelperDb.degreeCourse.update(d =>
              where(d.id === degreeCourse.id)
              set(
                  d.name := degreeCourse.name,
                  d.creditPoints := degreeCourse.creditPoints
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
      val row = StudhelperDb.degreeCourse.deleteWhere(d => d.id === id)
      
      if(row > 0) 
        Ok
      else
        NotFound
    }
  }
  
  def getAllPart(id: Long) = Action {
    transaction {
      try { 
        val degreeCourse = StudhelperDb.degreeCourse.where(d => d.id === id).single
        
        val parts: Iterable[PartTransfer] = degreeCourse.parts
        
        val jsonString = Json.generate(parts)

        Ok(jsonString) as("application/json")
      } catch {
        case e:RuntimeException => NotFound
        case _ => InternalServerError
      }
    }
  }
  
  def getPart(degId: Long, parId: Long) = Action {
    transaction {
	  try { 
        val part: PartTransfer = StudhelperDb.part.where(p => p.id === parId).single
	    
        val jsonString = Json.generate(part)
        
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
  
  def createPart(id: Long) = Action { implicit request => {
      val jsonString = request.body
      
      jsonString match {
        case AnyContentAsJson(x) => {
          val part: PartModel = Json.parse[PartTransfer](x.toString())

          transaction {
            val degreeCourse = StudhelperDb.degreeCourse.where(d => d.id === id).single
       		val newPart = degreeCourse.parts.assign(part)
            StudhelperDb.part insert newPart
            
	        newPart.id match {
	          case 0 => InternalServerError
		      case _ => Created
		    }
          }
        }
        case _ => InternalServerError
      }
  	}
  }
  
}