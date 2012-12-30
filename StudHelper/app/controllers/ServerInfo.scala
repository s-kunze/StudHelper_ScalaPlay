package controllers

import com.codahale.jerkson.Json
import play.api.mvc.Controller

import transfer._

object ServerInfo extends Controller {
  
  def get = {
    val info = ServerInfoTransfer("StudHelper","0.0.1")
    val jsonString = Json.generate(info)
    Ok(jsonString).as("application/json")
  }

}