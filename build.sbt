
name := "train-analyzer"

scalaVersion := "2.11.7"


libraryDependencies ++= Seq(
  "org.skinny-framework" %% "skinny-orm" % "1.3.20",
  "org.mariadb.jdbc" % "mariadb-java-client" % "1.2.2",
  "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.4.1"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalacOptions in Compile ++= Seq("-feature", "-Xlint", "-Ywarn-dead-code", "-Ywarn-numeric-widen")
