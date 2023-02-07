description = "OctoCore Parent"

allprojects {
    repositories {
        maven ("https://repo.octopvp.net/repo"){
            name = "octomc"
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}