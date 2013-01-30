package models

import org.squeryl.dsl._
import org.squeryl.KeyedEntity
import org.squeryl._
import transfer.LectureMarkTransfer

case class LectureToUser(val lectureId: Long, val userId: Long, var mark: Float) extends KeyedEntity[CompositeKey2[Long,Long]] {
  def id = CompositeKey2(lectureId, userId)
}

object LectureToUser {
  
  implicit def lectureMark2lectureMArkTransfer(lecture: LectureToUser) : LectureMarkTransfer = {
    LectureMarkTransfer("",0, lecture.mark)
  }
  
  
}