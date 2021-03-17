import sbt.ScriptedPlugin.autoImport.scriptedBufferLog

inThisBuild(Seq(
  
  name := "sbt-artifactory-settings",
  organization := "com.github.adevinta.unicron",
  description := "SBT plugin to manage Artifactory configuration",
  
  licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
  homepage := Some(url(s"https://github.com/adevinta/${name.value}")),
  developers := List(Developer("cre-team", "CRE Team", "gp.gt.cre@adevinta.com", url("https://github.com/orgs/adevinta/teams/cre"))),
  scmInfo := Some(ScmInfo(url(s"https://github.com/adevinta/${name.value}"), s"scm:git:git@github.com:adevinta/${name.value}.git")),

  organizationName := "Adevinta",
  startYear := Some(2021),

  usePgpKeyHex("E362921A4CE8BD97916B06CEC6DDC7B1869C9349"),

  dynverSonatypeSnapshots := true,

  scalaVersion := "2.12.12",
  sbtPlugin.withRank(KeyRanks.Invisible) := true,
))

lazy val root = Project(id = "sbt-artifactory-settings", base = file("."))
  .enablePlugins(SbtPlugin, AutomateHeaderPlugin)
  .settings(
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    publishTo := sonatypePublishToBundle.value,
    scriptedBufferLog := false,
    scriptedLaunchOpts ++= Seq("-Xmx1024M", s"-Dplugin.version=${version.value}")
  )
