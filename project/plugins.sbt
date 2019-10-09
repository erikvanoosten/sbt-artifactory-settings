val artifactoryContext = System.getenv("ARTIFACTORY_CONTEXT")
val artifactoryUser = System.getenv("ARTIFACTORY_USER")
val artifactoryPass = System.getenv("ARTIFACTORY_PWD")
resolvers += "Artifactory Realm Release Libs" at s"$artifactoryContext/libs-release"
credentials += Credentials("Artifactory Realm", new URL(artifactoryContext).getHost, artifactoryUser, artifactoryPass)

addSbtPlugin("com.schibsted.mp" % "sbt-tricklerdowner" % "1.4.4")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")
addSbtPlugin("com.dwijnand" % "sbt-dynver" % "4.0.0")
