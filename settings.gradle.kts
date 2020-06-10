pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        @Suppress("UnstableApiUsage")
        exclusiveContent {
            forRepository {
                maven {
                    name = "JCenter"
                    setUrl("https://jcenter.bintray.com/")
                }
            }
            filter {
                includeModule("org.jetbrains.dokka", "dokka-fatjar")
            }
        }
    }
}

rootProject.name = "rekoil-root"
include(":rekoil")