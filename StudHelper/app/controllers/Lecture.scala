package controllers

import play.api.mvc.Controller

import transfer._

object Lecture extends Controller {

  def getAll = TODO
  
  def get(id: Long, lecture: LectureTransfer) = TODO
  
  def create(id: Long, lecture: LectureTransfer) = TODO
  
  def update(lecture: LectureTransfer) = TODO
  
  def delete(id: Long) = TODO
  
  def addLecture(lecId: Long, userId: Long, mark: String) = TODO
  
}