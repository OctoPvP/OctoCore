import java.text.SimpleDateFormat
import java.util.Date;

plugins {
    java
    `maven-publish`
    id("com.gorylenko.gradle-git-properties") version "2.4.1"
}
description = "OctoCore Parent"

val octomcRepository = hasProperty("octomcUsername") && hasProperty("octomcPassword")

System.out.println("octomcRepository: " + octomcRepository)


val git = Git(rootProject.layout.projectDirectory)
val gitHash = git("rev-parse", "--short=7", "HEAD").getText().trim()
val implementationVersion = gitHash //System.getenv("BUILD_NUMBER") ?: "\"$gitHash\""
val date = git("show", "-s", "--format=%ci", gitHash).getText().trim()
val gitBranch = git("rev-parse", "--abbrev-ref", "HEAD").getText().trim()
val buildNumber = System.getenv("BUILD_NUMBER") ?: "0"
val buildDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())
subprojects {
    tasks {
        withType<Jar> {
            manifest {
                attributes(
                    "Implementation-Version" to "git-OctoCore-$implementationVersion",
                    "Git-Branch" to gitBranch,
                    "Git-Commit" to gitHash,
                    "Build-Number" to buildNumber,
                    "Git-Date" to date,
                    "Build-Date" to buildDate,
                    "Core" to "true"
                )
            }
        }
    }
}
