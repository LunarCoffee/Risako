import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    mavenCentral()
    jcenter()
}

plugins {
    kotlin("jvm") version "1.3.31"
}

group = "dev.lunarcoffee"
version = "0.1.0"

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    // Loading commands/listeners.
    compile(kotlin("reflect"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.2.1")

    // Discord API wrapper.
    implementation("net.dv8tion:JDA:4.ALPHA.0_89")

    implementation("io.github.microutils:kotlin-logging:1.6.24")
    implementation("org.slf4j:slf4j-api:1.7.26")
    implementation("org.slf4j:slf4j-jdk14:1.7.26")

    implementation("org.litote.kmongo:kmongo-coroutine:3.10.1")

    implementation("org.yaml:snakeyaml:1.21")
    implementation("com.google.code.gson:gson:2.8.5")

    implementation("com.github.kittinunf.fuel:fuel:2.1.0")
    implementation("com.github.kittinunf.fuel:fuel-coroutines:2.1.0")
    implementation("com.github.kittinunf.fuel:fuel-gson:2.1.0")

    implementation("org.jetbrains.kotlin:kotlin-script-util:1.3.21")
    implementation("org.jetbrains.kotlin:kotlin-script-runtime:1.3.21")
    implementation("org.jetbrains.kotlin:kotlin-compiler:1.3.21")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.3.21")
    runtimeOnly("org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:1.3.21")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
