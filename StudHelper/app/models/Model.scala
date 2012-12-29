package models

abstract class Model

case class Admin(id: Long, username: String, password: String) extends Model

case class User(id: Long, firstname: String,
                lastname: String,username: String, 
                password: String, creditPoints: Int) extends Model

case class University(id: Long, name: String) extends Model

case class Department(id: Long, name: String) extends Model

case class DegreeCourse(id: Long, name: String, creditPoints: Int) extends Model

case class Part(id: Long, name: String, creditPoints: Int) extends Model

case class Modul(id: Long, name: String, creditPoints: Int) extends Model

case class Lecture(id: Long, name: String, creditPoints: Int) extends Model