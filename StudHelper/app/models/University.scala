package models

import org.squeryl.KeyedEntity
import org.squeryl.Query
import org.squeryl.dsl._

import transfer.UniversityTransfer

case class University(val name: String) extends KeyedEntity[Long] {
	val id:Long = 0
	
	lazy val departments: OneToMany[Department] = StudhelperDb.universityToDepartment.left(this);
}

object University {
  
  implicit def universityTransfer2university(university: UniversityTransfer) : University = {
    University(university.name)
  }
  
  implicit def university2universityTransfer(university: University) : UniversityTransfer = {
    UniversityTransfer(Some(university.id), university.name)
  }
  
  implicit def universityList2universityTransferList(universities: Query[University]) : Iterable[UniversityTransfer] = {
    universities map (x => UniversityTransfer(Some(x.id), x.name))
  }
  
}