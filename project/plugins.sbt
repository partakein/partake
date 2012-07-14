// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "org.jacoco" % "org.jacoco.core" % "0.5.7.201204190339" artifacts(Artifact("org.jacoco.core", "jar", "jar")),
  "org.jacoco" % "org.jacoco.report" % "0.5.7.201204190339" artifacts(Artifact("org.jacoco.report", "jar", "jar")))

// Use the Play sbt plugin for Play projects
addSbtPlugin("play" % "sbt-plugin" % "2.0.3-RC1")

addSbtPlugin("de.johoop" % "jacoco4sbt" % "1.2.3")
