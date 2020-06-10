
object deps {

    const val asm = "org.ow2.asm:asm:7.1"
    const val jsr305 = "com.google.code.findbugs:jsr305:3.0.2"

    object kotlin {
        const val version = "1.3.72"
        const val coroutinesVersion = "1.3.7"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:$version"
        const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion"
        const val metadata = "org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.1.0"
    }

    object test {
        const val assertj = "org.assertj:assertj-core:3.11.1"
        const val compileTesting = "com.github.tschuchortdev:kotlin-compile-testing:1.2.8"
        const val junit = "junit:junit:4.12"
        const val kotlinCoroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${kotlin.coroutinesVersion}"
        const val truth = "com.google.truth:truth:1.0"
    }
}