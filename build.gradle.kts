plugins {
    kotlin("jvm") version "1.5.30"
}

group = "org.jbwapi"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.github.JavaBWAPI:JBWAPI:1.4")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    jar {
        manifest {
            attributes["Main-Class"] = "BotKt"
        }

        from(
            configurations.runtimeClasspath.get().map { if (it.isDirectory()) it else zipTree(it) }
        )
    }
}
