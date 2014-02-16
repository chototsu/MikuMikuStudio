import sbt.Keys._

Common.settings

name := "mms-niftygui-support"

unmanagedSourceDirectories in Compile := Seq(
  baseDirectory.value / "../engine/src/niftygui"
  , baseDirectory.value / "../engine/src/core/com/jme3/cinematic"
)

libraryDependencies += "lessvoid" % "nifty" % "1.3.3"


//sources in Compile ~= {
//  dirs => dirs filter(_.getAbsolutePath.contains("cinematic"))
//}

//unmanagedBase := baseDirectory.value / "lib2"
