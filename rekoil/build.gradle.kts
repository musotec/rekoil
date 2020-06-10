import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    kotlin("jvm")
    id("com.vanniktech.maven.publish")
}

tasks.named<Jar>("jar") {
    manifest {
        attributes("Automatic-Module-Name" to "tech.muso.rekoil")
    }
}

// Configure Dokka
afterEvaluate {
    tasks.named<DokkaTask>("dokka") {
        outputDirectory = "$rootDir/docs/0.0.x"
        outputFormat = "gfm"
    }
}

dependencies {
//    compileOnly(Dependencies.jsr305)

    api(deps.kotlin.stdlib)
    api(deps.kotlin.coroutinesCore)

    testCompileOnly(deps.jsr305)
    testImplementation(deps.test.junit)
    testImplementation(deps.test.kotlinCoroutines)
    testImplementation(deps.test.assertj)
}