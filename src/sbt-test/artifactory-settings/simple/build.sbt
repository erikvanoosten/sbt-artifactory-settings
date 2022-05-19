import org.scalatest.{FlatSpec, Matchers}
import com.github.daniel.shuy.sbt.scripted.scalatest.ScriptedScalaTestSuiteMixin

/* This function allows to set environment variables in the JVM, even when they are read-only usually */
def mockEnvVar(key: String, value: String): Unit = {
  val field = System.getenv().getClass.getDeclaredField("m")
  field.setAccessible(true)
  val map = field.get(System.getenv()).asInstanceOf[java.util.Map[java.lang.String, java.lang.String]]
  map.put(key, value)
}

/* Mock the needed environment variables for the test */
val mockEnvVars: Unit = {
  mockEnvVar("ARTIFACTORY_CONTEXT", "https://host.com/path")
  mockEnvVar("ARTIFACTORY_USER", "user@adevinta.com")
  mockEnvVar("ARTIFACTORY_PWD", "secret")
}

lazy val root = (project in file("."))
  .enablePlugins(ArtifactorySettingsPlugin)
  .settings(
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.12.10",
    artifactoryBuildTimestampSuffix := ";build.timestamp=1570434693212",

    scriptedScalaTestSpec := Some(new FlatSpec with Matchers with ScriptedScalaTestSuiteMixin {
      override val sbtState: State = state.value

      "The user" should "be able to customize what env vars to use for the artifactory context, user and password" in {
        artifactoryContextEnv.value shouldBe "ARTIFACTORY_CONTEXT"
        artifactoryUserEnv.value shouldBe "ARTIFACTORY_USER"
        artifactoryPassEnv.value shouldBe "ARTIFACTORY_PWD"

        artifactoryContext.value shouldBe "https://host.com/path"
        artifactoryHost.value shouldBe "host.com"
        artifactoryUser.value shouldBe "user@adevinta.com"
        artifactoryPass.value shouldBe "secret"
      }

      "The JVM resolvers" should "be defined" in {
        artifactoryJvmReleasesResolver.value shouldBe "https://host.com/path/libs-release/"
        artifactoryJvmSnapshotsResolver.value shouldBe "https://host.com/path/libs-snapshot/"
        artifactoryJvmReleasesPublishResolver.value shouldBe "https://host.com/path/libs-release-local/"
        artifactoryJvmSnapshotsPublishResolver.value shouldBe "https://host.com/path/libs-snapshot-local;build.timestamp=1570434693212/"
      }
      
      "The credentials" should "be configured" in {
        credentials.value.size shouldBe 1
        credentials.value.head shouldBe a[DirectCredentials]

        val c = credentials.value.head.asInstanceOf[DirectCredentials]
        c.realm shouldBe "Artifactory Realm"
        c.host shouldBe "host.com"
        c.userName shouldBe "user@adevinta.com"
        c.passwd shouldBe "secret"
      }

      "The resolvers" should "be configured" in {
        resolvers.value shouldBe Seq(
          Resolver.mavenLocal,
          "Artifactory Release Libs" at "https://host.com/path/libs-release/",
          "Artifactory Snapshot Libs" at "https://host.com/path/libs-snapshot/"
        )
      }

      "The publish Resolver" should "be configured" in {
        publishTo.value shouldBe Some(
          "Artifactory Publishing" at "https://host.com/path/libs-snapshot-local;build.timestamp=1570434693212/"
        )
      }
    })
  )
