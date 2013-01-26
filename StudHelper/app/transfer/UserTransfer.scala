package transfer

case class UserTransfer(id: Option[Long], firstname: String, 
                        lastname: String, username: String, 
                        creditpoints: Int)