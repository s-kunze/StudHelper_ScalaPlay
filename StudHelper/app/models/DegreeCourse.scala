package models

import org.squeryl.KeyedEntity
import org.squeryl.Query
import org.squeryl.dsl._

import transfer.DegreeCourseTransfer

case class DegreeCourse(val name: String, val creditPoints: Int, departmentId: Option[Long]) extends KeyedEntity[Long] {
	val id: Long = 0
	
	lazy val department: ManyToOne[Department] = StudhelperDb.departmentToDegreeCourse.right(this)
	lazy val parts: OneToMany[Part] = StudhelperDb.degreeCourseToPart.left(this);
}

object DegreeCourse {
  
  implicit def degreeCourseTransfer2degreeCourse(degreeCourseTransfer: DegreeCourseTransfer) : DegreeCourse = {
    DegreeCourse(degreeCourseTransfer.name, degreeCourseTransfer.creditPoints, None)
  }
  
  implicit def degreeCourse2degreeCourseTransfer(degreeCourse: DegreeCourse) : DegreeCourseTransfer = {
    DegreeCourseTransfer(Some(degreeCourse.id), degreeCourse.name, degreeCourse.creditPoints)
  }
  
  implicit def degreeCourseList2degreeCourseTransferList(degreeCourses: Query[DegreeCourse]) : Iterable[DegreeCourseTransfer] = {
    degreeCourses map (x => DegreeCourseTransfer(Some(x.id), x.name, x.creditPoints))
  }
  
}