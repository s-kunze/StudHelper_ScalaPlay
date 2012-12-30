package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import transfer._

case class Admin(id: Pk[Long] = NotAssigned, username: String, password: String)

object Admin {
  
  implicit def newAdminTransfer2admin(newAdmin: NewAdminTransfer) : Admin = {
    Admin(NotAssigned, newAdmin.username, newAdmin.password)
  } 
  
  implicit def adminList2adminTransferList(admins: Seq[Admin]) : Seq[AdminTransfer] = {
    admins map (x => AdminTransfer(x.id.get, x.username, x.password)  )
  }
  
  val parser = {
    get[Pk[Long]]("admin.id") ~
    get[String]("admin.username") ~
    get[String]("admin.password") map {
      case id~username~password => Admin(id, username, password)
    }
  }
  
  def findAll : Seq[Admin] = {
    DB.withConnection { implicit connection =>
        SQL("""SELECT * FROM ADMIN""").as(Admin.parser *)
    }
  }
  
  def find(id: Long): Option[Admin] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM ADMIN WHERE ID = {id}").on('id -> id).as(Admin.parser.singleOpt)
    }
  }
  
  def create(admin: Admin) : Option[Long] = {
    DB.withConnection { implicit connection =>
      val id = SQL(
        """INSERT INTO ADMIN(USERNAME,PASSWORD) values ({username},{password})"""
      ).on(
        'username -> admin.username,
        'password -> admin.password
      ).executeUpdate()
      
      if(id == 1) {
        return Some(id)
      } else {
        return None
      }
    }
  }
  
  def update(id: Long, admin: Admin) : Boolean = {
    DB.withConnection { implicit connection =>
      val res = SQL(
        """UPDATE ADMIN SET USERNAME = {username}, PASSWORD = {password} WHERE ID = {id}"""
      ).on(
        'id -> id,
        'username -> admin.username,
        'password -> admin.password
      ).executeUpdate()
      
      if(res > 0) {
        return true;
      } else {
        return false;
      }
    }
  }
  
  def delete(id: Long) : Boolean = {
    DB.withConnection { implicit connection =>
      val res = SQL(
        """
          DELETE FROM ADMIN WHERE ID = {id})
        """
      ).on(
        'id -> id
      ).executeUpdate();
      
      if(res > 0) {
        return true;
      } else {
        return false;
      }
  }
    
  }
  
}