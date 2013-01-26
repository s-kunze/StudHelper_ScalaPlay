package controllers

import org.squeryl.PrimitiveTypeMode._
import com.codahale.jerkson.Json
import play.api.mvc.Controller
import play.api.mvc.Action
import transfer._
import models.StudhelperDb
import models.{Modul => ModulModel, Part => PartModel}
import play.api.Logger

object Part extends Controller {

  def getAll = Action {
    val json = transaction {
	  val parts: Iterable[PartTransfer] = from(StudhelperDb.part)(p => select(p))
      Json.generate(parts)
	}  
	
    Ok(json) as("application/json")
  }
  
  def get(id: Long) = Action {
    transaction {
      try { 
        val part: PartTransfer = StudhelperDb.part.where(p => p.id === id).single
        val jsonString = Json.generate(part)

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
          val part: PartModel = Json.parse[PartTransfer](x)

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
  
  def update = Action { implicit request => {
      val jsonString = request.body.asText
    
      jsonString match {
        case Some(x) => {
          val part = Json.parse[PartTransfer](x)
          
          val rows = transaction {
            StudhelperDb.part.update(p =>
              where(p.id === part.id)
              set(
                  p.name := part.name,
                  p.creditPoints := part.creditPoints
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
      val row = StudhelperDb.part.deleteWhere(p => p.id === id)
      
      if(row > 0) 
        Ok
      else
        NotFound
    }
  }
  
  def getAllModul(id: Long) = Action {
    transaction {
      try { 
        val part = StudhelperDb.part.where(p => p.id === id).single
        
        val moduls: Iterable[ModulTransfer] = part.moduls
        
        val jsonString = Json.generate(moduls)

        Ok(jsonString) as("application/json")
      } catch {
        case e:RuntimeException => NotFound
        case _ => InternalServerError
      }
    }
  }
  
  def getModul(parId: Long, modId: Long) = Action {
    transaction {
	  try { 
        val modul: ModulTransfer = StudhelperDb.modul.where(m => m.id === modId).single
	    
        val jsonString = Json.generate(modul)
        
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
  
  def createModul(id: Long) = Action { implicit request => {
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
  
}