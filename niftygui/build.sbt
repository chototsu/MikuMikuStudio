Common.settings

name := "mms-niftygui-support"

unmanagedSourceDirectories in Compile := Seq(
  baseDirectory.value / "../engine/src/niftygui"
  , baseDirectory.value / "../engine/src/core/com/jme3/cinematic"
)

unmanagedResourceDirectories in Compile <<= unmanagedSourceDirectories in Compile

unmanagedResources in Compile ~= {
  dirs => dirs filter(file => (!file.getAbsolutePath.endsWith(".java") && !file.getAbsolutePath.endsWith(".scala")))
}

//libraryDependencies += "lessvoid" % "nifty" % "1.3.3"

libraryDependencies += "net.sf.sociaal" % "nifty" % "3.0.0.20130526"

libraryDependencies += "net.sf.sociaal" % "nifty-style-black" % "3.0.0.20130526"


//sources in Compile ~= {
//  dirs => dirs filter(_.getAbsolutePath.contains("cinematic"))
//}

//unmanagedBase := baseDirectory.value / "lib2"
