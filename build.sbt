name := "social"
organization in ThisBuild := "com.outr"
version in ThisBuild := "1.0.0-SNAPSHOT"
scalaVersion in ThisBuild := "2.12.7"
crossScalaVersions in ThisBuild := Seq("2.12.7", "2.11.12")
scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation")
resolvers in ThisBuild += Resolver.sonatypeRepo("releases")
resolvers in ThisBuild += Resolver.sonatypeRepo("snapshots")

publishTo in ThisBuild := sonatypePublishTo.value
sonatypeProfileName in ThisBuild := "com.outr"
publishMavenStyle in ThisBuild := true
licenses in ThisBuild := Seq("MIT" -> url("https://github.com/outr/social/blob/master/LICENSE"))
sonatypeProjectHosting in ThisBuild := Some(xerial.sbt.Sonatype.GitHubHosting("outr", "social", "matt@outr.com"))
homepage in ThisBuild := Some(url("https://github.com/outr/social"))
scmInfo in ThisBuild := Some(
  ScmInfo(
    url("https://github.com/outr/social"),
    "scm:git@github.com:outr/social.git"
  )
)
developers in ThisBuild := List(
  Developer(id="darkfrog", name="Matt Hicks", email="matt@matthicks.com", url=url("http://matthicks.com"))
)

lazy val root = project.in(file("."))
  .aggregate(core, facebook)

lazy val core = project.in(file("core"))
  .settings(
    name := "social-core"
  )

lazy val facebook = project.in(file("facebook"))
  .settings(
    name := "social-facebook",
    fork := true,
    updateOptions := updateOptions.value.withLatestSnapshots(false),
    libraryDependencies ++= Seq(
      "io.youi" %% "youi-client" % "0.9.10-SNAPSHOT",
      "com.machinepublishers" % "jbrowserdriver" % "1.0.1"
    )
  )
  .dependsOn(core)