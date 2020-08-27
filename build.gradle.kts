import com.vanniktech.maven.publish.MavenPublishPluginExtension
import org.gradle.jvm.tasks.Jar

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = deps.kotlin.version))
    }
}

plugins {
    id("com.vanniktech.maven.publish") version "0.11.1" apply false
    id("org.jetbrains.dokka") version "0.10.1" apply false
}

subprojects {
    repositories {
        mavenCentral()
        @Suppress("UnstableApiUsage")
        exclusiveContent {
            forRepository {
                maven {
                    name = "JCenter"
                    setUrl("https://jcenter.bintray.com/")
                }
            }
            // add dokka to all submodules
            filter {
                includeModule("org.jetbrains.dokka", "dokka-fatjar")
            }
        }
    }

    pluginManager.withPlugin("java-library") {
        configure<JavaPluginExtension> {
            // TODO: use jdk8?
            sourceCompatibility = JavaVersion.VERSION_1_7
            targetCompatibility = JavaVersion.VERSION_1_7
        }
    }

    // Configure publishing
    pluginManager.withPlugin("com.vanniktech.maven.publish") {
        configure<MavenPublishPluginExtension> {

            targets {
                this.configureEach {
                    // publish within build dir until signing and auto-publish are fixed in kotlin gradle
                    val url = file("${rootProject.buildDir}/localMaven").toURI().toString()

                    releaseRepositoryUrl = url
                    snapshotRepositoryUrl = url
                }
            }

            useLegacyMode = false
            releaseSigningEnabled = false
        }

        // Configure automatic-module-name, but only for published modules
        @Suppress("UnstableApiUsage")
        val automaticModuleName = providers.gradleProperty("AUTOMATIC_MODULE_NAME")
                .forUseAtConfigurationTime()
        if (automaticModuleName.isPresent) {
            val name = automaticModuleName.get()
            tasks.withType<Jar>().configureEach {
                manifest {
                    attributes("Automatic-Module-Name" to name)
                }
            }
        }
    }

    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        dependencies.add("api", deps.kotlin.stdlib)
    }
}