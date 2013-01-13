package models

import org.squeryl.Schema
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl._

object StudhelperDb extends Schema {

  val admins = table[Admin]
  val university = table[University]
  val department = table[Department]
  
  val universityToDepartment = oneToManyRelation(university, department).via((u,d) => u.id === d.universityId) 
}