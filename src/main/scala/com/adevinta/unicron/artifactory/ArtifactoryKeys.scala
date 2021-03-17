/*
 * Copyright 2021 Adevinta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.adevinta.unicron.artifactory

import sbt._

object ArtifactoryKeys {

  val artifactoryContextEnv = settingKey[String]("Name for the environment variable holding the artifactory context.")
  val artifactoryUserEnv = settingKey[String]("Name for the environment variable holding the artifactory user.")
  val artifactoryPassEnv = settingKey[String]("Name for the environment variable holding the artifactory password.")

  val artifactoryContext = settingKey[String]("Artifactory base URL")
  val artifactoryHost = settingKey[String]("Artifactory Host")
  val artifactoryUser = settingKey[String]("Artifactory user")
  val artifactoryPass = settingKey[String]("Artifactory password")

  val artifactoryCredentials = settingKey[Seq[Credentials]]("Credentials for artifactory")

  val artifactoryJvmResolvers = settingKey[Seq[Resolver]]("Resolvers for Artifactory dependencies")
  val artifactoryJvmReleasesResolver = settingKey[String]("Repository for resolving JVM release artifacts")
  val artifactoryJvmSnapshotsResolver = settingKey[String]("Repository for resolving JVM snapshot artifacts")

  val artifactoryJvmPublishResolver = settingKey[Option[Resolver]]("Artifactory resolver for publishing JVM artifacts")
  val artifactoryJvmReleasesPublishResolver = settingKey[String]("Repository for publishing JVM release artifacts")
  val artifactoryJvmSnapshotsPublishResolver = settingKey[String]("Repository for publishing JVM snapshot artifacts")

  val artifactoryBuildTimestampSuffix = settingKey[String]("Internal. Build timestamp suffix")
}
