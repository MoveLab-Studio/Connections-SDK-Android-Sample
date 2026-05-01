pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            name = "MoveLab"
            url = uri("https://nexus.mls-cdn.net/repository/movelab/")
            credentials {
                username = providers.gradleProperty("nexusUser").orNull
                    ?: System.getenv("NEXUS_USER")
                password = providers.gradleProperty("nexusPassword").orNull
                    ?: System.getenv("NEXUS_PASSWORD")
            }
        }
    }
}
rootProject.name = "ConnectionsSDKSample"
include(":app")
