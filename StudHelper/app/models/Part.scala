package models

import org.squeryl.KeyedEntity
import org.squeryl.Query
import org.squeryl.dsl._

import transfer.PartTransfer

case class Part(val name: String, val creditPoints: Int, degreeCourseId: Option[Long]) extends KeyedEntity[Long] {
	val id: Long = 0
	
	lazy val degreeCourse: ManyToOne[DegreeCourse] = StudhelperDb.degreeCourseToPart.right(this)
	lazy val moduls: OneToMany[Modul] = StudhelperDb.partToModul.left(this);
}

object Part {
  
  implicit def partTransfer2part(partTransfer: PartTransfer) : Part = {
    Part(partTransfer.name, partTransfer.creditPoints, None)
  }
  
  implicit def degreeCourse2degreeCourseTransfer(part: Part) : PartTransfer = {
    PartTransfer(Some(part.id), part.name, part.creditPoints)
  }
  
  implicit def degreeCourseList2degreeCourseTransferList(parts: Query[Part]) : Iterable[PartTransfer] = {
    parts map (x => PartTransfer(Some(x.id), x.name, x.creditPoints))
  }
  
}