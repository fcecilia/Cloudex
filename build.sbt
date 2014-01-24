name := "Cloudex"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.0")


resolvers += "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"



play.Project.playScalaSettings
