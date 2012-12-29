package controllers

import play.api.mvc.Controller

import transfer._

object University extends Controller {

  def getAll = TODO
  
  def get(id: Long, university: UniversityTransfer) = TODO
  
  def create(university: UniversityTransfer) = TODO
  
  def update(university: UniversityTransfer) = TODO
  
  def delete(id: Long) = TODO
  
  def getAllDepartment(id: Long) = TODO
  
  def getDepartment(uniId: Long, depId: Long) = TODO
  
  def createDepartment(id: Long, department: DepartmentTransfer) = TODO
  
}