description = "OctoCore Parent"

val octomcRepository = hasProperty("octomcUsername") && hasProperty("octomcPassword")

System.out.println("octomcRepository: " + octomcRepository)

subprojects {
    repositories {

    }
}