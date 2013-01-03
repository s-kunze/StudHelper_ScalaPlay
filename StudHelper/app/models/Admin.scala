package models

import org.squeryl.KeyedEntity
import org.squeryl.Query

import transfer._

case class Admin(val username: String, val password: String) extends KeyedEntity[Long] {
  val id:Long = 0
}

object Admin {
  
  implicit def newAdminTransfer2admin(newAdmin: NewAdminTransfer) : Admin = {
    Admin(newAdmin.username, newAdmin.password)
  }
  
  implicit def adminTransfer2admin(admin: AdminTransfer) : Admin = {
    Admin(admin.username, admin.password)
  }
  
  implicit def admin2adminTransfer(admin : Admin) : AdminTransfer = {
    AdminTransfer(admin.id, admin.username, admin.password)
  }
  
  implicit def adminList2adminTransferList(admins: Query[Admin]) : Iterable[AdminTransfer] = {
    admins map (x => AdminTransfer(x.id, x.username, x.password))
  }
  
}