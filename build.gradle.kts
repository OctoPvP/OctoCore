import java.text.SimpleDateFormat
import java.util.*

plugins {
    java
    `maven-publish`
}
description = "OctoCore Parent"

val octomcRepository = hasProperty("octomcUsername") && hasProperty("octomcPassword")

System.out.println("octomcRepository: " + octomcRepository)


val git = Git(rootProject.layout.projectDirectory)
val gitHash = git("rev-parse"/*, "--short=7"*/, "HEAD").getText().trim()
val shortGitHash = gitHash.substring(0, 7)
val implementationVersion = gitHash //System.getenv("BUILD_NUMBER") ?: "\"$gitHash\""
val date = git("show", "-s", "--format=%ci", gitHash).getText().trim()
val gitBranch = git("rev-parse", "--abbrev-ref", "HEAD").getText().trim()
val getCommitUser = git("show", "-s", "--format=%cn", gitHash).getText().trim()
val buildNumber = System.getenv("BUILD_NUMBER") ?: "0"
val buildDate: String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())
subprojects {
    tasks {
        // generate build.properties file in build/resources/main/build.properties
        val buildProperties = register("buildProperties", WriteProperties::class.java) {
            outputFile = file("$buildDir/resources/main/build.properties")
            property("git.commit.id", gitHash)
            property("git.commit.id.abbrev", shortGitHash)
            property("git.commit.date", date)
            property("git.branch", gitBranch)
            property("build.number", buildNumber)
            property("build.date", buildDate)
        }
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
            dependsOn(buildProperties)
        }
    }
}
