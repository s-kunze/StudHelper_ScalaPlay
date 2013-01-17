package models

import org.squeryl.KeyedEntity
import org.squeryl.Query
import org.squeryl.dsl._

import transfer.DepartmentTransfer

case class Department(val name: String, val universityId: Option[Long]) extends KeyedEntity[Long] {
	val id:Long = 0
	
	lazy val university: ManyToOne[University] = StudhelperDb.universityToDepartment.right(this)
}

object Department {
  
  implicit def departmentTransfer2department(department: DepartmentTransfer) : Department = {
    Department(department.name, None)
  }
  
  implicit def department2departmentTransfer(department: Department) : DepartmentTransfer = {
    DepartmentTransfer(Some(department.id), department.name)
  }
  
  implicit def departmentList2departmentTransferList(departments: Query[Department]) : Iterable[DepartmentTransfer] = {
    departments map (x => DepartmentTransfer(Some(x.id), x.name))
  }
  
}