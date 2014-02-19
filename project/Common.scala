import sbt._
import sbt.Keys._

object Common {
  lazy val settings = Seq(
    organization := "info.projectkyoto"
    , version := "1.0.0-SNAPSHOT"
    //    , homepage := Some(url("https://github.com/chototsu/MikuMikuStudio"))
    , startYear := Some(2010)
    , description := "A game engine compatible with MikuMikuDance."
    //    , licenses += "The BSD 3-Clause License" -> url("http://opensource.org/licenses/BSD-3-Clause")
    , autoScalaLibrary := false
    , crossPaths := false
    , javacOptions ++= Seq("-encoding", "UTF-8", "-source", "1.6", "-target", "1.6")
    , javacOptions in doc := Seq("-locale", "en_US", "-encoding", "UTF-8", "-source", "1.6")
    , resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots"
    , resolvers += "nifty-maven-repo.sourceforge.net" at "http://nifty-gui.sourceforge.net/nifty-maven-repo"
    , pomExtra := (
      <url>https://github.com/chototsu/MikuMikuStudio</url>
        <licenses>
          <license>
            <name>BSD-style</name>
            <url>http://www.opensource.org/licenses/bsd-license.php</url>
            <distribution>repo</distribution>
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
  )
}

