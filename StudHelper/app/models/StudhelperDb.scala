package models

import org.squeryl.Schema

object StudhelperDb extends Schema {

  val admins = table[Admin]
  
}