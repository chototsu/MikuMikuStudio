import sbt.Keys._

Common.settings

name := "mms-engine"

unmanagedSourceDirectories in Compile := Seq(
//  baseDirectory.value / "src/blender"
    baseDirectory.value / "src/bullet"
  , baseDirectory.value / "src/core"
  , baseDirectory.value / "src/core-data"
  , baseDirectory.value / "src/core-effects"
  , baseDirectory.value / "src/core-plugins"
  , baseDirectory.value / "src/desktop"
  , baseDirectory.value / "src/desktop-fx"
  , baseDirectory.value / "src/games"
//  , baseDirectory.value / "src/jheora"
  , baseDirectory.value / "src/jogg"
//  , baseDirectory.value / "src/lwjgl-oal"
//  , baseDirectory.value / "src/lwjgl-ogl"
  , baseDirectory.value / "src/mmd"
  , baseDirectory.value / "src/networking"
//  , baseDirectory.value / "src/niftygui"
  , baseDirectory.value / "src/ogre"
  , baseDirectory.value / "src/pack"
  , baseDirectory.value / "src/tools"
//  , baseDirectory.value / "src/terrain"
  , baseDirectory.value / "src/xml"
)

sources in Compile ~= {
  dirs => dirs filter(file => (!file.getAbsolutePath.contains("cinematic") && !file.getAbsolutePath.contains("CollisionShapeFactory.java")))
}

unmanagedResourceDirectories in Compile <<= unmanagedSourceDirectories in Compile

unmanagedResources in Compile ~= {
  dirs => dirs filter(file => (!file.getAbsolutePath.endsWith(".java") && !file.getAbsolutePath.endsWith(".scala")))
}

//unmanagedBase := baseDirectory.value / "lib2"

libraryDependencies += "java3d" % "vecmath" % "1.3.1"

libraryDependencies += "xpp3" % "xpp3" % "1.1.4c"

//libraryDependencies += "com.jme3" % "noise" % "3.0.0-SNAPSHOT"

libraryDependencies += "net.sf.sociaal" % "j-ogg-oggd" % "3.0.0.20130526"

libraryDependencies += "net.sf.sociaal" % "j-ogg-vorbisd" % "3.0.0.20130526"