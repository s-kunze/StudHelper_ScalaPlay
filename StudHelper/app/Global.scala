import play.api.db.DB
import play.api.GlobalSettings
import play.api.Application

import org.squeryl.adapters.{H2Adapter, MySQLAdapter}
import org.squeryl.internals.DatabaseAdapter
import org.squeryl.{Session, SessionFactory}
import org.squeryl.Schema
import org.squeryl.PrimitiveTypeMode._

import models.StudhelperDb

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    SessionFactory.concreteFactory = app.configuration.getString("db.default.driver") match {
      case Some("org.h2.Driver") => Some(() => getSession(new H2Adapter, app))
      case Some("com.mysql.jdbc.Driver") => Some(() => getSession(new MySQLAdapter, app))
      case _ => sys.error("Database driver must be either org.h2.Driver or com.mysql.jdbc.Driver")
    }
    
    // TODO: remove this and add evolution, i don't want this yet...
    transaction {
      if(StudhelperDb.tables.isEmpty) {
        StudhelperDb.create
      }
    }
  }

  def getSession(adapter:DatabaseAdapter, app: Application) = Session.create(DB.getConnection()(app), adapter)
}