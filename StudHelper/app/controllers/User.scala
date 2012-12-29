package controllers

import play.api.mvc.Controller

import transfer._

object User extends Controller {

  def getAll = TODO
  
  def get(id: Long, userTransfer: UserTransfer) = TODO
  
  def create(id: Long, newUserTransfer: NewUserTransfer) = TODO
 
  def update(userTransfer: UserTransfer) = TODO
  
  def delete(id: Long) = TODO
  
  def auth(authTransfer: AuthTransfer) = TODO
  
  def getDegreeCourse(id: Long) = TODO
  
  def getLectures(id: Long) = TODO

}