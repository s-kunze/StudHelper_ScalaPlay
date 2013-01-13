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

  def getAll = TODO
  
  def get(id: Long) = Action {
    transaction {
      try { 
        val university: UniversityTransfer = StudhelperDb.university.where(a => a.id === id).single
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
  
  def update = TODO
  
  def delete(id: Long) = TODO
  
  def getAllDepartment(id: Long) = TODO
  
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