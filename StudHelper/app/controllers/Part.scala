package controllers

import play.api.mvc.Controller

import transfer._

object Part extends Controller {

  def getAll = TODO
  
  def get(id: Long, part: PartTransfer) = TODO
  
  def create(id: Long, part: PartTransfer) = TODO
  
  def update(part: PartTransfer) = TODO
  
  def delete(id: Long) = TODO
  
  def getAllModul(id: Long) = TODO
  
  def getModul(parId: Long, modId: Long) = TODO
  
  def createModul(id: Long, modul: ModulTransfer) = TODO
  
}