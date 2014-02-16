Common.settings

name := "mms-desktop"

unmanagedSourceDirectories in Compile ++= Seq(
    baseDirectory.value / "../engine/src/lwjgl-oal"
    , baseDirectory.value / "../engine/src/lwjgl-ogl"
    , baseDirectory.value / "../engine/src/blender"
)

libraryDependencies += "org.lwjgl.lwjgl" % "lwjgl" % "2.9.0"

libraryDependencies += "net.java.jinput" % "jinput" % "2.0.5"

libraryDependencies += "org.bushe" % "eventbus" % "1.4"
