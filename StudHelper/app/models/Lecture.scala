package models

import org.squeryl.KeyedEntity
import org.squeryl.Query
import org.squeryl.dsl._
import transfer.LectureTransfer

case class Lecture(val name: String, val creditPoints: Int) extends KeyedEntity[Long] {
	val id: Long = 0
	
	lazy val moduls = StudhelperDb.modulToLecture.right(this)
	lazy val users = StudhelperDb.lectureToUser.left(this)
}

object Lecture {
  
  implicit def lectureTransfer2lecture(lectureTransfer: LectureTransfer) : Lecture = {
    Lecture(lectureTransfer.name, lectureTransfer.creditPoints)
  }
  
  implicit def lecture2lectureTransfer(lecture: Lecture) : LectureTransfer = {
    LectureTransfer(Some(lecture.id), lecture.name, lecture.creditPoints)
  }
  
  implicit def lectureList2lectureTransferList(lectures: Query[Lecture]) : Iterable[LectureTransfer] = {
    lectures map (x => LectureTransfer(Some(x.id), x.name, x.creditPoints))
  }
  
}