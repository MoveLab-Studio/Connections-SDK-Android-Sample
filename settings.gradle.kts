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
            name = "GitHub Packages"
            url = uri("https://maven.pkg.github.com/MoveLab-Studio/Connections-SDK")
            credentials {
                username = providers.gradleProperty("gpr.user").orNull
                    ?: System.getenv("GPR_USER")
                password = providers.gradleProperty("gpr.token").orNull
                    ?: System.getenv("GPR_TOKEN")
            }
        }
    }
}
rootProject.name = "ConnectionsSDKSample"
include(":app")
