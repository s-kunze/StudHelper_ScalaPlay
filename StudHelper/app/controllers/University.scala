package controllers

import org.squeryl.PrimitiveTypeMode._
import com.codahale.jerkson.Json
import play.api.mvc.Controller
import play.api.mvc.Action
import transfer._
import models.StudhelperDb
import play.api.Logger
import models.{Department => DepartmentModel}
import play.api.mvc.AnyContentAsJson

object University extends Controller {

  def getAll = Action {
    Logger.debug("Get Universities")
    val json = transaction {
	  val universites: Iterable[UniversityTransfer] = from(StudhelperDb.university)(u => select(u))
      Json.generate(universites)
	}  
	
    Ok(json) as("application/json")
  }
  
  def get(id: Long) = Action {
    Logger.debug("Get University")
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
	  Logger.debug("Create University")
      val jsonString = request.body
      
      jsonString match {
        case AnyContentAsJson(x) => {
          val university = Json.parse[UniversityTransfer](x.toString())
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
	  Logger.debug("Update University")
      val jsonString = request.body
      
      jsonString match {
        case AnyContentAsJson(x) => {
          val university = Json.parse[UniversityTransfer](x.toString())
          
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
    Logger.debug("Delete University")
    transaction {
      val row = StudhelperDb.university.deleteWhere(u => u.id === id)
      
      if(row > 0) 
        Ok
      else
        NotFound
    }
  }
  
  def getAllDepartment(id: Long) = Action {
    Logger.debug("Get Departments for University")
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
    Logger.debug("Get Department for University")
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
	  Logger.debug("Create Department")
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
  
}