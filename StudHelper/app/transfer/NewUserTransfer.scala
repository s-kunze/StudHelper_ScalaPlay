package transfer

case class NewUserTransfer(firstname: String, lastname: String,
                           username: String, password: String,
                           password2: String)