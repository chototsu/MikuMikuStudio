import sbt._
import sbt.Keys._

object Common {
  lazy val settings = Seq(
    organization := "info.projectkyoto"
    , version := "1.0.0-SNAPSHOT"
    , autoScalaLibrary := false
    , crossPaths := false
    , javacOptions ++= Seq("-encoding", "UTF-8", "-source", "1.6", "-target", "1.6")
    , javacOptions in doc := Seq("-locale", "en_US", "-encoding", "UTF-8", "-source", "1.6")
    , resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots"
    , resolvers += "nifty-maven-repo.sourceforge.net" at "http://nifty-gui.sourceforge.net/nifty-maven-repo"
  )
}

