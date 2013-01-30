package controllers

import org.squeryl.PrimitiveTypeMode._
import com.codahale.jerkson.Json
import play.api.mvc.Controller
import play.api.mvc.Action
import transfer._
import models.StudhelperDb
import models.{DegreeCourse => DegreeCourseModel, Department => DepartmentModel, Modul => ModulModel}
import play.api.Logger
import scala.collection.mutable.LinkedList
import play.api.mvc.AnyContentAsJson

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
      val jsonString = request.body
      
      jsonString match {
        case AnyContentAsJson(x) => {
          val department: DepartmentModel = Json.parse[DepartmentTransfer](x.toString())

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
      val jsonString = request.body
      
      jsonString match {
        case AnyContentAsJson(x) => {
          val department = Json.parse[DepartmentTransfer](x.toString())
          
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
  
  def getModul(id: Long) = Action {
    transaction {  
      var result = new LinkedList[ModulTransfer]
      
      val department = StudhelperDb.department.where(d => d.id === id).single
      
      val degreeCourses = department.degreeCourses.elements
      
      for(degreeCourse <- degreeCourses) {
        Logger.debug("DegreeCourse-Id: " + degreeCourse.id)
        Logger.debug("DegreeCourse-Name: " + degreeCourse.name)
        val deg = StudhelperDb.degreeCourse.where(d => d.id === degreeCourse.id).single
        
        val parts = deg.parts.elements
        
        for(part <- parts) {
          Logger.debug("Part-Id: " + part.id)
          Logger.debug("Part-Name: " + part.name)
          val par = StudhelperDb.part.where(p => p.id === part.id).single
          
          val modules = par.moduls.elements
          
          for(modul <- modules) {
            val modulTransfer: ModulTransfer = modul

            Logger.debug("Modul-Name: " + modulTransfer.name)
            
            result = result.append( LinkedList(modulTransfer))
          }
          
        }
      }
      
      val jsonString = Json.generate(result)
        
      Ok(jsonString) as("application/json")
    }
  }
  
}