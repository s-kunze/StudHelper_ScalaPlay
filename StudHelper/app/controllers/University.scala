package controllers

import org.squeryl.PrimitiveTypeMode._
import com.codahale.jerkson.Json
import play.api.mvc.Controller
import play.api.mvc.Action
import transfer._
import models.StudhelperDb
import models.{Department => DepartmentModel}
import play.api.Logger

object University extends Controller {

  def getAll = Action {
    val json = transaction {
	  val universites: Iterable[UniversityTransfer] = from(StudhelperDb.university)(u => select(u))
      Json.generate(universites)
	}  
	
    Ok(json) as("application/json")
  }
  
  def get(id: Long) = Action {
    transaction {
      try { 
        val university: UniversityTransfer = StudhelperDb.university.where(u => u.id === id).single
        val jsonString = Json.generate(university)

        Ok(jsonString) as("application/json")
      } catch {
        case e:RuntimeException => NotFound
        case _ => InternalServerError
      }
    }
  }
  
  def create = Action { implicit request => {
      val jsonString = request.body.asText
      
      jsonString match {
        case Some(x) => {
          val university = Json.parse[UniversityTransfer](x)
          transaction {
            val newUniversity = StudhelperDb.university insert university
	        newUniversity.id match {
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
          val university = Json.parse[UniversityTransfer](x)
          
          val rows = transaction {
            StudhelperDb.university.update(u =>
              where(u.id === university.id)
              set(u.name := university.name)
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
      val row = StudhelperDb.university.deleteWhere(u => u.id === id)
      
      if(row > 0) 
        Ok
      else
        NotFound
    }
  }
  
  def getAllDepartment(id: Long) = Action {
    transaction {
      try { 
        val university = StudhelperDb.university.where(u => u.id === id).single
        
        val departments: Iterable[DepartmentTransfer] = university.departments
        
        val jsonString = Json.generate(departments)

        Ok(jsonString) as("application/json")
      } catch {
        case e:RuntimeException => NotFound
        case _ => InternalServerError
      }
    }
  }
  
  def getDepartment(uniId: Long, depId: Long) = Action {
    transaction {
	  try { 
        val department: DepartmentTransfer = StudhelperDb.department.where(d => d.id === depId).single
	    
        val jsonString = Json.generate(department)
        
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
  
  def createDepartment(id: Long) = Action { implicit request => {
      val jsonString = request.body.asText
      
      jsonString match {
        case Some(x) => {
          val departmentTransfer = Json.parse[DepartmentTransfer](x)
          
          transaction {
            val university = StudhelperDb.university.where(u => u.id === id).single
           
            val department = DepartmentModel(departmentTransfer.name, Some(university.id))
            
            val newDepartment = StudhelperDb.department insert department
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
  
}