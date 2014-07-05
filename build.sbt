lazy val root =
  (project.in(file("."))
    .settings(Common.settings: _*)
    .aggregate(engine, desktop/*, android*/, gdx, niftygui, mms_gdx_natives_ios, mms_gdx_natives_android, mms_gdx_natives_desktop)
    )

lazy val engine = project

lazy val desktop = project.dependsOn(engine)

//lazy val android = project

lazy val gdx = project.dependsOn(engine)

lazy val niftygui = project.dependsOn(engine)

lazy val mms_gdx_natives_ios = project.dependsOn(gdx)

lazy val mms_gdx_natives_android = project.dependsOn(gdx)

lazy val mms_gdx_natives_desktop = project.dependsOn(gdx)


publishArtifact := false

publishLocal := {}

publish := {}

