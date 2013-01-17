package models

import org.squeryl.Schema
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._
import org.squeryl.ForeignKeyDeclaration

object StudhelperDb extends Schema {

  val admins = table[Admin]
  val university = table[University]
  val department = table[Department]
  
  /** One (University) To Many (Department) */
  val universityToDepartment = oneToManyRelation(university, department).via((u,d) => u.id === d.universityId) 
  
  /** the default constraint for all foreign keys in this schema */ 
  override def applyDefaultForeignKeyPolicy(foreignKeyDeclaration: ForeignKeyDeclaration) = 
    foreignKeyDeclaration.constrainReference

  /** If University is deleted, department should also delete */
  universityToDepartment.foreignKeyDeclaration.constrainReference(onDelete cascade)
}