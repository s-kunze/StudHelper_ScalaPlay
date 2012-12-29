package transfer

abstract class Transfer

/* Server Info */
case class ServerInfo(name: String, version: String) extends Transfer

/* UserTransfer Objects */
case class AuthTransfer(id: Long, `type`: String) extends Transfer // type = USER | ADMIN

case class NewUserTransfer(firstname: String, lastname: String,
                           username: String, password: String,
                           password2: String) extends Transfer
                           
case class UserTransfer(id: Long, firstname: String, 
                        lastname: String, username: String, 
                        creditpoints: Int) extends Transfer
                        
/* BackendTransfer Objects */
case class UniversityTransfer(id: Long, name: String) extends Transfer

case class DepartmentTransfer(id: Long, name: String) extends Transfer

case class DegreeCourseTransfer(id: Long, name: String, creditPoints: Int) extends Transfer

case class PartTransfer(id: Long, name: String, creditPoints: Int) extends Transfer

case class ModulTransfer(id: Long, name: String, creditPoints: Int) extends Transfer

case class LectureTransfer(id: Long, name: String, creditPoints: Int) extends Transfer

case class LectureMarkTransfer(name: String, creditPoints: Int, mark: Float) extends Transfer