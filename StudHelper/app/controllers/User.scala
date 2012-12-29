package controllers

import com.codahale.jerkson.Json
import play.api.mvc.Action
import play.api.mvc.Controller

import transfer._

object User extends Controller {
  
  def getAll = TODO
  
  def get(id: Long) = Action { 
	val user = UserTransfer(id,"Stefan", "Kunze", "test",3)
    val jsonString = Json.generate(user)
    Ok(jsonString).as("application/json")
}
  
  def create(id: Long) = TODO
 
  def update = TODO
  
  def delete(id: Long) = TODO
  
  def auth = TODO
  
  def getDegreeCourse(id: Long) = TODO
  
  def getLectures(id: Long) = TODO

}