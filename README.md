# sbt-artifactory-settings

SBT plugin to manage Artifactory configuration for Spark jobs

## Getting started

Following you will find how to configure your laptop environment as well as the project that will use this plugin.

### Environment configuration

When running Continuous integration in Travis there are some environment variables already provided that this plugin depends on:
- `ARTIFACTORY_CONTEXT`: which value will be something like `https://artifactory.mpi-internal.com/artifactory`.
- `ARTIFACTORY_USER`: With your artifactory user, something like `name.surname@adevinta.com`.
- `ARTIFACTORY_PWD`: The artifactory token

The idea is to setup your computer in a similar fashion by providing those environment variables.

If you work with `bash` you'll need to put them in `.bashrc`, while if you work with `zsh` you'll need to modify `.zshrc`:

```bash
export ARTIFACTORY_CONTEXT="https://artifactory.mpi-internal.com/artifactory"
export ARTIFACTORY_USER="user@adevinta.com"
export ARTIFACTORY_PWD="artifactory-token"
```

### Project configuration

Given the typical project structure for a Scala application with [SBT](https://www.scala-sbt.org/):

```
-- project
 |  \-- plugins.sbt
 |-- src
 \-- build.sbt
```

You'll need to configure `project/plugins.sbt` and `build.sbt` as shown below:

`project/plugins.sbt` is where the SBT plugins are specified. Given that we need a minimum setup for getting the plugins
 from artifactory, you will need to load some configuration from the environment variables like:

```scala
val artifactoryContext = System.getenv("ARTIFACTORY_CONTEXT")
val artifactoryUser = System.getenv("ARTIFACTORY_USER")
val artifactoryPass = System.getenv("ARTIFACTORY_PWD")
resolvers += "Artifactory Release Plugins" at s"$artifactoryContext/libs-release"
credentials += Credentials("Artifactory Realm", new URL(artifactoryContext).getAuthority, artifactoryUser, artifactoryPass)
addSbtPlugin("com.adevinta.unicron" % "sbt-artifactory-settings" % "<version>")

// the rest of the plugins are added here
```

`build.sbt`:

This is where the project build is configured. You will need to enable the artifactory plugin
 in the project containing the Scala code for Spark like:
```scala
lazy val root = Project(id = "my-project", base = file("."))
  .enablePlugins(ArtifactorySettingsPlugin)
```

## Settings

This plugin provides several settings configured by default, but they can also be customised by the user:

| Name | Type | Description |
| ---- | ---- | ----------- |
| *artifactoryContextEnv* | `String` | Name for the environment variable holding the artifactory context. Default `ARTIFACTORY_CONTEXT` |
| *artifactoryUserEnv* | `String` | Name for the environment variable holding the artifactory user. Default `ARTIFACTORY_USER` |
| *artifactoryPassEnv* | `String` | Name for the environment variable holding the artifactory password. Default `ARTIFACTORY_PWD` |
| *artifactoryContext* | `String` | Artifactory base URL |
| *artifactoryUser* | `String` | Artifactory user |
| *artifactoryPass* | `String` | Artifactory password |
| *artifactoryHost* | `String` | Artifactory host. Default: Extracted from the `artifactoryContext` |
| *artifactoryCredentials* | `Seq[Credentials]` | Credentials for Artifactory |
| *artifactoryJvmResolvers* | `Seq[Resolver]` | Resolvers for Artifactory dependencies |
| *artifactoryJvmSnapshotsRepository* | `String` | Repository for publishing JVM snapshot artifacts including the base URL |
| *artifactoryJvmReleasesRepository* | `String`  | Repository for publishing JVM release artifacts including the base URL |
| *artifactoryJvmPublishResolver* | `Option[Resolver]` | Artifactory repository for publishing JVM artifacts |
| *artifactoryJvmReleasesPublishResolver* | `String` | Repository for publishing JVM release artifacts |
| *artifactoryJvmSnapshotsPublishResolver* | `String` | Repository for publishing JVM snapshot artifacts |
