import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "PartakePlay"
    val appVersion      = "0.3-SNAPSHOT"

    // TODO(mayah): We should share this information with ivy.xml... we should be able to call externalIvyFile()
    // somewhere, but it conceals play libraries. Too bad.
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
        "log4j" % "log4j" % "1.2.17",
        "net.sf.json-lib" % "json-lib" % "2.4" classifier "jdk15",

        "junit" % "junit" % "4.8.2",
        "org.mockito" % "mockito-all" % "1.8.1",
        "org.hamcrest" % "hamcrest-all" % "1.1"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
        // Add your own project settings here
        externalIvySettings()
    )

}
