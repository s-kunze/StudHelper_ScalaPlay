package models

import org.squeryl.Schema
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import org.squeryl.ForeignKeyDeclaration

object StudhelperDb extends Schema {

  val admins = table[Admin]
  val university = table[University]
  val department = table[Department]
  val degreeCourse = table[DegreeCourse]
  val part = table[Part]
  val modul = table[Modul]
  
  val universityToDepartment = oneToManyRelation(university, department).via((u,d) => u.id === d.universityId) 
  val departmentToDegreeCourse = oneToManyRelation(department, degreeCourse).via((dep,deg) => dep.id === deg.departmentId)
  val degreeCourseToPart = oneToManyRelation(degreeCourse, part).via((d,p) => d.id === p.degreeCourseId)
  val partToModul = oneToManyRelation(part, modul).via((p,m) => p.id === m.partId)
  
  /** the default constraint for all foreign keys in this schema */ 
  override def applyDefaultForeignKeyPolicy(foreignKeyDeclaration: ForeignKeyDeclaration) = 
    foreignKeyDeclaration.constrainReference

  universityToDepartment.foreignKeyDeclaration.constrainReference(onDelete cascade)
  departmentToDegreeCourse.foreignKeyDeclaration.constrainReference(onDelete cascade)
  degreeCourseToPart.foreignKeyDeclaration.constrainReference(onDelete cascade)
  partToModul.foreignKeyDeclaration.constrainReference(onDelete cascade)
}