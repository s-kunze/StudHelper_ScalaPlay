package models

import org.squeryl.KeyedEntity
import org.squeryl.Query
import org.squeryl.dsl._

import transfer._

case class User(val firstname: String, val lastname: String, val username: String,
                val password: String, val creditPoints: Int, val degreeCourseId: Option[Long]) extends KeyedEntity[Long] {
  val id:Long = 0
  
  lazy val degreeCourse: ManyToOne[DegreeCourse] = StudhelperDb.degreeCourseToUser.right(this)
  lazy val lectures = StudhelperDb.lectureToUser.right(this)
}

object User {
  
  implicit def newUserTransfer2user(newUserTransfer: NewUserTransfer) : User = {
    User(newUserTransfer.firstname, newUserTransfer.lastname,
         newUserTransfer.username, newUserTransfer.password, 0, None)
  }
  
  implicit def userTransfer2user(userTransfer: UserTransfer) : User = {
    User(userTransfer.firstname, userTransfer.lastname, userTransfer.username,
        userTransfer.password, userTransfer.creditpoints, None)
  }
  
  implicit def user2userTransfer(user: User) : UserTransfer = {
    UserTransfer(Some(user.id), user.firstname, user.lastname, user.username, user.creditPoints)
  }
  
  implicit def userList2userTransferList(users: Query[User]) : Iterable[UserTransfer] = {
    users map (x => UserTransfer(Some(x.id), x.firstname, x.lastname, x.username, x.creditPoints))
  }
  
}