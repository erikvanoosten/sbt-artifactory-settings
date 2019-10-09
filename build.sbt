import sbt.ScriptedPlugin.autoImport.scriptedBufferLog

inThisBuild(Seq(
  scalaVersion := "2.12.10",
  organization := "com.adevinta.unicron",

  sbtPlugin := true
))

val artifactoryContext = System.getenv("ARTIFACTORY_CONTEXT")
val artifactoryUser = System.getenv("ARTIFACTORY_USER")
val artifactoryPass = System.getenv("ARTIFACTORY_PWD")
val artifactoryHost = new URL(artifactoryContext).getAuthority

lazy val root = Project(id = "sbt-artifactory-settings", base = file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    resolvers += "Artifactory Realm Release Libs" at s"$artifactoryContext/libs-release",
    credentials += Credentials("Artifactory Realm", artifactoryHost, artifactoryUser, artifactoryPass),
    publishTo := {
      val repository = if (isSnapshot.value) "libs-snapshot-local;build.timestamp=" + java.time.Instant.now().toEpochMilli else "libs-release-local"
      Some("Artifactory Realm for Publishing" at s"$artifactoryContext/$repository/")
    },
    scriptedBufferLog := false,
    scriptedLaunchOpts ++= Seq("-Xmx1024M", s"-Dplugin.version=${version.value}")
  )

