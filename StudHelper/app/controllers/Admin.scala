package controllers

import org.squeryl.PrimitiveTypeMode._

import com.codahale.jerkson.Json
import play.api.mvc.Action
import play.api.mvc.Controller
import transfer._

import models.StudhelperDb
import models.{Admin => AdminModel}

object Admin extends Controller {
  
  def getAll = Action {
	val json = transaction {
	  val admins: Iterable[AdminTransfer] = from(StudhelperDb.admins)(a => select(a))
      Json.generate(admins)
	}  
	
    Ok(json) as("application/json")
  }
  
  def get(id: Long) = Action { 
     transaction {
      try { 
        val admin: AdminTransfer = StudhelperDb.admins.where(a => a.id === id).single
        val jsonString = Json.generate(admin)

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
          val admin = Json.parse[NewAdminTransfer](x)
          transaction {
            val newAdmin = StudhelperDb.admins insert admin
	        newAdmin.id match {
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
          val admin = Json.parse[AdminTransfer](x)
          
          val rows = transaction {
            StudhelperDb.admins.update(s =>
              where(s.id === admin.id)
              set(s.username := admin.username,
                  s.password  := admin.password)
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
      val row = StudhelperDb.admins.deleteWhere(a => a.id === id)
      
      if(row > 0) 
        Ok
      else
        NotFound
    }
  }
}