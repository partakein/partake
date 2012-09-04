import sbt._
import Keys._
import PlayProject._
import de.johoop.jacoco4sbt.JacocoPlugin._

object ApplicationBuild extends Build {

    val appName         = "PartakePlay"
    val appVersion      = "0.3-SNAPSHOT"

    lazy val s = Defaults.defaultSettings ++ Seq(jacoco.settings:_*)

    val appDependencies = Seq(
        // Add your project dependencies here,
        "commons-lang" % "commons-lang" % "2.6",
        "org.owasp" % "antisamy" % "1.4",
        "ical4j" % "ical4j" % "0.9.20",
        "org.twitter4j" % "twitter4j-core" % "2.2.5",
        "net.sf.opencsv" % "opencsv" % "2.1",
        "org.apache.lucene" % "lucene-analyzers" % "3.0.2",
        "org.openid4java" % "openid4java" % "0.9.5",
        "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
        "commons-dbcp" % "commons-dbcp" % "1.4",
        "commons-pool" % "commons-pool" % "1.6",
        "net.java.dev.rome" % "rome" % "1.0.0",
        "net.java.dev.rome" % "rome-fetcher" % "1.0.0",
        "com.twitter" % "twitter-text" % "1.4.10",
        "net.sf.json-lib" % "json-lib" % "2.4" classifier "jdk15",

        // We want to have mockito-all in test scope, however it causes some errors
        "org.mockito" % "mockito-all" % "1.8.1",

        "junit" % "junit" % "4.8.2" % "test",
        "org.hamcrest" % "hamcrest-all" % "1.1" % "test",
        "org.scalatest" %% "scalatest" % "1.8" % "test"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA, settings = s).settings(
        // Add your own project settings here
        externalIvySettings(),
        testOptions in Test := Nil,
        parallelExecution in jacoco.Config := false,
        lessEntryPoints <<= baseDirectory(_ / "app" / "assets" / "stylesheets" / "partake-all.less")
    )

}
