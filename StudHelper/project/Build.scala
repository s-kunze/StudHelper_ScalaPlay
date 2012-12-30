import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "StudHelper"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "com.codahale" % "jerkson_2.9.1" % "0.5.0"
      // Add your project dependencies here,
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here      
    )

}
