plugins {
    java
    `maven-publish`
}
description = "OctoCore Parent"

val octomcRepository = hasProperty("octomcUsername") && hasProperty("octomcPassword")

System.out.println("octomcRepository: " + octomcRepository)

subprojects {
    repositories {
        maven {
            url = uri("https://repo.octopvp.net/repo")
            name = "octomc"
            credentials {
                username = findProperty("octomcUsername") as String
                password = findProperty("octomcPassword") as String
                System.out.println("Username: " + username + " | Password: " + password)
            }
        }
    }
}