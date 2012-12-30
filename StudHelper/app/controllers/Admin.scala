package controllers

import com.codahale.jerkson.Json
import play.api.mvc.Action
import play.api.mvc.Controller
import transfer._

import models.{Admin => AdminModel}

object Admin extends Controller {
  
  def getAll = Action {
    val admins: Seq[AdminTransfer] = AdminModel.findAll
    
    val jsonString = Json.generate(admins)
    Ok(jsonString) as("application/json")
  }
  
  def get(id: Long) = Action { 
    val admin = AdminModel.find(id)
    
    admin match {
      case Some(x) => {
        val jsonString = Json.generate(x)
    	Ok(jsonString) as("application/json")
      }
      case _ => NotFound
    }
 
  }
  
  def create = Action { implicit request => {
      val jsonString = request.body.asText
      
      jsonString match {
        case Some(x) => {
          val admin = Json.parse[NewAdminTransfer](x)
          val id = AdminModel.create(admin)  
          
          id match {
	        case Some(_) => Created
	        case _ => InternalServerError
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
          val admin: AdminModel = Json.parse[AdminModel](x)
          if(AdminModel.update(admin.id.get, admin))
            Ok  
          else
            NotFound
        }
        case _ => InternalServerError
      }
    
    }
  }
  
  def delete(id: Long) = Action { 
    if(AdminModel.delete(id))
      Ok
    else 
      NotFound
  }

}