package controllers

import org.squeryl.PrimitiveTypeMode._
import com.codahale.jerkson.Json
import play.api.mvc.Controller
import play.api.mvc.Action
import transfer._
import models.StudhelperDb
import models.{DegreeCourse => DegreeCourseModel, Department => DepartmentModel}
import play.api.Logger

object Department extends Controller {

  def getAll = Action {
    val json = transaction {
	  val departments: Iterable[DepartmentTransfer] = from(StudhelperDb.department)(d => select(d))
      Json.generate(departments)
	}  
	
    Ok(json) as("application/json")
  }
  
  def get(id: Long) = Action {
    transaction {
      try { 
        val department: DepartmentTransfer = StudhelperDb.department.where(d => d.id === id).single
        val jsonString = Json.generate(department)

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
          val department: DepartmentModel = Json.parse[DepartmentTransfer](x)

          transaction {
            val university = StudhelperDb.university.where(u => u.id === id).single
       		val newDepartment = university.departments.assign(department)
            StudhelperDb.department insert newDepartment
            
	        newDepartment.id match {
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
          val department = Json.parse[DepartmentTransfer](x)
          
          val rows = transaction {
            StudhelperDb.department.update(d =>
              where(d.id === department.id)
              set(d.name := department.name)
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
      val row = StudhelperDb.department.deleteWhere(d => d.id === id)
      
      if(row > 0) 
        Ok
      else
        NotFound
    }
  }
  
  def getAllDegreeCourse(id: Long) = Action {
    transaction {
      try { 
        val department = StudhelperDb.department.where(d => d.id === id).single
        
        val degreeCourses: Iterable[DegreeCourseTransfer] = department.degreeCourses
        
        val jsonString = Json.generate(degreeCourses)

        Ok(jsonString) as("application/json")
      } catch {
        case e:RuntimeException => NotFound
        case _ => InternalServerError
      }
    }
  }
  
  def getDegreeCourse(depId: Long, degId: Long) = Action {
    transaction {
	  try { 
        val degreeCourse: DegreeCourseTransfer = StudhelperDb.degreeCourse.where(d => d.id === depId).single
	    
        val jsonString = Json.generate(degreeCourse)
        
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
  
  def createDegreeCourse(id: Long) = Action { implicit request => {
      val jsonString = request.body.asText
      
      jsonString match {
        case Some(x) => {
          val degreeCourse: DegreeCourseModel = Json.parse[DegreeCourseTransfer](x)

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
  
  def getModul(id: Long) = TODO
  
}