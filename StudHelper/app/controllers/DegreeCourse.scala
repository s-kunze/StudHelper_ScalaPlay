package controllers

import play.api.mvc.Controller

import transfer._

object DegreeCourse extends Controller {

  def getAll = TODO           
  
  def get(id: Long, degreeCourse: DegreeCourseTransfer) = TODO
  
  def create(id: Long, degreeCourse: DegreeCourseTransfer) = TODO
  
  def update(degreeCourse: DegreeCourseTransfer) = TODO
  
  def delete(id: Long) = TODO
  
  def getAllPart(id: Long) = TODO
  
  def getPart(degId: Long, parId: Long) = TODO
  
  def createPart(id: Long, part: PartTransfer) = TODO
  
}