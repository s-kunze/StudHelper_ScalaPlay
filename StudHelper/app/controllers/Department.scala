package controllers

import play.api.mvc.Controller

import transfer._

object Department extends Controller {

  def getAll = TODO
  
  def get(id: Long, department: DepartmentTransfer) = TODO
  
  def create(id: Long, department: DepartmentTransfer) = TODO
  
  def update(department: DepartmentTransfer) = TODO
  
  def delete(id: Long) = TODO
  
  def getAllDegreeCourse(id: Long) = TODO
  
  def getDegreeCourse(depId: Long, degId: Long) = TODO
  
  def createDegreeCourse(id: Long, degreeCourse: DegreeCourseTransfer) = TODO
  
  def getModul(id: Long) = TODO
  
}