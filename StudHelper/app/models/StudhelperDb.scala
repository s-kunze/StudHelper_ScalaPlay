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
  val lecture = table[Lecture]
  val user = table[User]
  
  val universityToDepartment = oneToManyRelation(university, department).via((u,d) => u.id === d.universityId) 
  val departmentToDegreeCourse = oneToManyRelation(department, degreeCourse).via((dep,deg) => dep.id === deg.departmentId)
  val degreeCourseToPart = oneToManyRelation(degreeCourse, part).via((d,p) => d.id === p.degreeCourseId)
  val partToModul = oneToManyRelation(part, modul).via((p,m) => p.id === m.partId)
  val modulToLecture = manyToManyRelation(modul, lecture).via[ModulToLecture]((m,l,mtl) => (mtl.modulId === m.id, l.id === mtl.lectureId))  
  val lectureToUser = manyToManyRelation(lecture, user).via[LectureToUser]((l, u, ltu) => (ltu.lectureId === l.id, u.id === ltu.userId))
  val degreeCourseToUser = oneToManyRelation(degreeCourse, user).via((d, u) => d.id === u.degreeCourseId)
  
  override def applyDefaultForeignKeyPolicy(foreignKeyDeclaration: ForeignKeyDeclaration) = 
    foreignKeyDeclaration.constrainReference

  universityToDepartment.foreignKeyDeclaration.constrainReference(onDelete cascade)
  departmentToDegreeCourse.foreignKeyDeclaration.constrainReference(onDelete cascade)
  degreeCourseToPart.foreignKeyDeclaration.constrainReference(onDelete cascade)
  partToModul.foreignKeyDeclaration.constrainReference(onDelete cascade)
  modulToLecture.rightForeignKeyDeclaration.constrainReference(onDelete noAction)
  lectureToUser.rightForeignKeyDeclaration.constrainReference(onDelete noAction)
  degreeCourseToUser.foreignKeyDeclaration.constrainReference(onDelete noAction)
}