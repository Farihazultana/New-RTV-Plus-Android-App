pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
}

rootProject.name = "RTV-Plus-Android-App-Revamp"
//include(":app")
include(":app")
include(":carouselview")
include(":whynotimagecarousel")
