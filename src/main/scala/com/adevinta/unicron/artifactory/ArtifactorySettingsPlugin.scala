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

import com.adevinta.unicron.artifactory.ArtifactoryKeys._
import sbt.Keys._
import sbt._

object ArtifactorySettingsPlugin extends AutoPlugin {

  val autoImport: ArtifactoryKeys.type = ArtifactoryKeys

  override def buildSettings: Seq[Setting[_]] = Seq(
    artifactoryContextEnv := "ARTIFACTORY_CONTEXT",
    artifactoryUserEnv := "ARTIFACTORY_USER",
    artifactoryPassEnv := "ARTIFACTORY_PWD",

    artifactoryContext := {
      sys.env.getOrElse(artifactoryContextEnv.value, {
        println(s"The environment variable for the Artifactory context is not defined: ${artifactoryContextEnv.value}") // scalastyle:ignore
        sys.exit(-1)
      })
    },

    artifactoryHost := {
      new URL(artifactoryContext.value).getAuthority
    },

    artifactoryUser := {
      sys.env.getOrElse(artifactoryUserEnv.value, {
        println(s"The environment variable for the Artifactory user is not defined: ${artifactoryUserEnv.value}") // scalastyle:ignore
        sys.exit(-1)
      })
    },

    artifactoryPass := {
      sys.env.getOrElse(artifactoryPassEnv.value, {
        println(s"The environment variable for the Artifactory password is not defined: ${artifactoryPassEnv.value}") // scalastyle:ignore
        sys.exit(-1)
      })
    },

    artifactoryCredentials := Seq(
      Credentials(
        realm = "Artifactory Realm",
        host = artifactoryHost.value,
        userName = artifactoryUser.value,
        passwd = artifactoryPass.value
      )
    ),

    // Fix the build timestamp. Patch extracted from https://github.com/sbt/sbt/issues/2088
    artifactoryBuildTimestampSuffix := ";build.timestamp=" + java.time.Instant.now().toEpochMilli
  )

  override def projectSettings: Seq[Setting[_]] = Seq(
    artifactoryJvmReleasesResolver := s"${artifactoryContext.value}/libs-release/",
    artifactoryJvmSnapshotsResolver := s"${artifactoryContext.value}/libs-snapshot/",
    artifactoryJvmReleasesPublishResolver := s"${artifactoryContext.value}/libs-release-local/",
    artifactoryJvmSnapshotsPublishResolver := s"${artifactoryContext.value}/libs-snapshot-local${artifactoryBuildTimestampSuffix.value}/",

    artifactoryJvmResolvers := {
      Seq(
        if (isSnapshot.value) Some(Resolver.mavenLocal) else None,
        Some("Artifactory Release Libs" at artifactoryJvmReleasesResolver.value),
        if (isSnapshot.value) Some("Artifactory Snapshot Libs" at artifactoryJvmSnapshotsResolver.value) else None,
        Some(Resolver.jcenterRepo)
      ).flatten
    },

    artifactoryJvmPublishResolver := {
      val repository = if (isSnapshot.value) {
        artifactoryJvmSnapshotsPublishResolver.value
      } else {
        artifactoryJvmReleasesPublishResolver.value
      }
      Some("Artifactory Publishing" at repository)
    },

    credentials := artifactoryCredentials.value,
    resolvers := artifactoryJvmResolvers.value,
    publishTo := artifactoryJvmPublishResolver.value
  )
}
