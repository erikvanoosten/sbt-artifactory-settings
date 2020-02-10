import sbt.ScriptedPlugin.autoImport.scriptedBufferLog

inThisBuild(Seq(
  scalaVersion := "2.12.10",
  organization := "com.adevinta.unicron",
  description := "SBT plugin to manage Artifactory configuration for Unicron",
  sbtPlugin := true
))

lazy val artifactorySettings = {
  def getEnv(name: String): String = {
    val value = System.getenv(name)
    if (value == null) {
      println("You are missing the environment variables needed to access Artifactory.")
      println("Please follow this guide to configure your machine properly:")
      println("\nhttps://docs.mpi-internal.com/unicron/docs-zeus-migration-guide/setup-laptop/\n")
      sys.exit(-1)
    }
    value
  }
  val artifactoryContext = getEnv("ARTIFACTORY_CONTEXT")
  val artifactoryUser = getEnv("ARTIFACTORY_USER")
  val artifactoryPass = getEnv("ARTIFACTORY_PWD")

  Seq(
    resolvers := Seq("Artifactory Release Plugins" at s"$artifactoryContext/libs-release"),
    credentials := Seq(Credentials("Artifactory Realm", new URL(artifactoryContext).getAuthority, artifactoryUser, artifactoryPass)),
    publishTo := {
      val repository = if (isSnapshot.value) "libs-snapshot-local;build.timestamp=" + java.time.Instant.now().toEpochMilli else "libs-release-local"
      Some("Artifactory Realm for Publishing" at s"$artifactoryContext/$repository/")
    },
    dynverSonatypeSnapshots := true
  )
}

lazy val bintraySettings = Seq(
  publishMavenStyle := false,
  bintrayOrganization := Some("adevinta-unicron"),
  bintrayRepository := "sbt-plugins",
  licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
  dynverSonatypeSnapshots := false
)

val publishRepository = sys.props.get("publish.repository")

val publishSettings = {
  publishRepository
    .filter(_.toLowerCase == "bintray")
    .fold[Seq[Setting[_]]](artifactorySettings)(_ => bintraySettings)
}

val pluginsToDisable = {
  publishRepository
    .filter(_.toLowerCase == "bintray")
    .fold[Seq[AutoPlugin]](Seq(BintrayPlugin))(_ => Seq.empty)
}

lazy val root = Project(id = "sbt-artifactory-settings", base = file("."))
  .enablePlugins(SbtPlugin)
  .disablePlugins(pluginsToDisable :_*)
  .settings(publishSettings :_*)
  .settings(
    scriptedBufferLog := false,
    scriptedLaunchOpts ++= Seq("-Xmx1024M", s"-Dplugin.version=${version.value}")
  )
