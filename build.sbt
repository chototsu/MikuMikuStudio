lazy val root =
  (project.in(file("."))
    .settings(Common.settings: _*)
    .aggregate(engine, desktop/*, android*/, gdx/*, niftygui*/)
    )

lazy val engine = project

lazy val desktop = project.dependsOn(engine)

lazy val android = project

lazy val gdx = project.dependsOn(engine)

//lazy val niftygui = project.dependsOn(engine)

publishArtifact := false

publishLocal := {}

publish := {}

