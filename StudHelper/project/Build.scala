import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "StudHelper"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "com.codahale" % "jerkson_2.9.1" % "0.5.0",
      "org.squeryl" %% "squeryl" % "0.9.5-2",
      "mysql" % "mysql-connector-java" % "5.1.10",
      "org.apache.commons" % "commons-lang3" % "3.1"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here      
    )

}
