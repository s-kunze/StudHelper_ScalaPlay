package controllers

import play.api.mvc.Controller

import transfer._

object Modul extends Controller {

  def getAll = TODO
  
  def get(id: Long, modul: ModulTransfer) = TODO
  
  def create(id: Long, modul: ModulTransfer) = TODO
  
  def update(modul: ModulTransfer) = TODO
  
  def delete(id: Long) = TODO
  
  def getAllLecture(id: Long) = TODO
  
  def getLecture(modId: Long, lecId: Long) = TODO
  
  def createLecture(id: Long, lecture: LectureTransfer) = TODO
  
  def getDepartment(id: Long) = TODO
  
}