import sbt._
import sbt.Keys._

object Common {
  lazy val settings = Seq(
    organization := "info.projectkyoto"
    , version := "0.8.2-SNAPSHOT"
    , startYear := Some(2010)
    , description := "A game engine compatible with MikuMikuDance."
    , autoScalaLibrary := false
    , crossPaths := false
    , javacOptions ++= Seq("-encoding", "UTF-8", "-source", "1.6", "-target", "1.6")
    , javacOptions in doc := Seq("-locale", "en_US", "-encoding", "UTF-8", "-source", "1.6")
    , pomExtra := (
      <url>https://github.com/chototsu/MikuMikuStudio</url>
        <licenses>
          <license>
            <name>BSD-style</name>
            <url>https://raw.github.com/chototsu/MikuMikuStudio/master/LICENSE.txt</url>
            <distribution>repo</distribution>
          </license>
          <license>
            <name>BSD</name>
            <url>
              http://hub.jmonkeyengine.org/wiki/doku.php/bsd_license
            </url>
          </license>
        </licenses>
        <scm>
          <url>git@github.com:chototsu/MikuMikuStudio.git</url>
          <connection>scm:git:git@github.com:chototsu/MikuMikuStudio.git</connection>
        </scm>
        <developers>
          <developer>
            <id>chototsu</id>
            <name>Kazuhiko Kobayashi</name>
            <url>http://mms.projectkyoto.info/</url>
          </developer>
        </developers>)
    , publishMavenStyle := true
    , publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    }
    , publishArtifact in Test := false
    , pomIncludeRepository := { _ => false }
    , resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"


  )
}

