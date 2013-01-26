package models

import org.squeryl.KeyedEntity
import org.squeryl.Query
import org.squeryl.dsl._

import transfer.ModulTransfer

case class Modul(val name: String, val creditPoints: Int, partId: Option[Long]) extends KeyedEntity[Long] {
	val id: Long = 0
	
	lazy val part: ManyToOne[Part] = StudhelperDb.partToModul.right(this)
	lazy val lectures = StudhelperDb.modulToLecture.left(this)
}

object Modul {
  
  implicit def modulTransfer2modul(modulTransfer: ModulTransfer) : Modul = {
    Modul(modulTransfer.name, modulTransfer.creditPoints, None)
  }
  
  implicit def degreeCourse2degreeCourseTransfer(modul: Modul) : ModulTransfer = {
    ModulTransfer(Some(modul.id), modul.name, modul.creditPoints)
  }
  
  implicit def degreeCourseList2degreeCourseTransferList(moduls: Query[Modul]) : Iterable[ModulTransfer] = {
    moduls map (x => ModulTransfer(Some(x.id), x.name, x.creditPoints))
  }
  
}