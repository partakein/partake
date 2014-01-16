import sbt._
import Keys._
import play.Project._
import de.johoop.jacoco4sbt.JacocoPlugin._

object ApplicationBuild extends Build {

    val appName         = "PartakePlay"
    val appVersion      = "0.3-SNAPSHOT"

    lazy val s = Defaults.defaultSettings ++ Seq(jacoco.settings:_*)

    val appDependencies = Seq(
        // Add your project dependencies here,
        jdbc,
        javaJpa,
        "commons-lang" % "commons-lang" % "2.6",
        "org.owasp" % "antisamy" % "1.4",
        "ical4j" % "ical4j" % "0.9.20",
        "org.twitter4j" % "twitter4j-core" % "3.0.5",
        "net.sf.opencsv" % "opencsv" % "2.1",
        "org.apache.lucene" % "lucene-analyzers" % "3.0.2",
        "org.openid4java" % "openid4java" % "0.9.5",
        "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
        "commons-dbcp" % "commons-dbcp" % "1.4",
        "commons-pool" % "commons-pool" % "1.6",
        "net.java.dev.rome" % "rome" % "1.0.0",
        "net.java.dev.rome" % "rome-fetcher" % "1.0.0",
        "com.twitter" % "twitter-text" % "1.4.10",

        // We want to have mockito in test scope, however it causes some errors
        "org.mockito" % "mockito-core" % "1.9.5",

        "junit" % "junit" % "4.11" % "test",
        "org.hamcrest" % "hamcrest-all" % "1.3" % "test",
        "org.scalatest" %% "scalatest" % "1.9.2" % "test"
    )

    val main = play.Project(appName, appVersion, appDependencies, settings = s).settings(
        // Add your own project settings here
        externalIvySettings(),
        testOptions in Test := Nil,
        parallelExecution in jacoco.Config := false,
        lessEntryPoints <<= baseDirectory(_ / "app" / "assets" / "stylesheets" / "partake-all.less")
    )

}
