import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "com.ImHamba"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        jvmToolchain(11)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)

                implementation("moe.tlaster:precompose:1.5.7")
                implementation("moe.tlaster:precompose-viewmodel:1.5.7")

                implementation("uk.co.caprica:vlcj:4.7.0")

                implementation(compose.material3)
                implementation("org.jetbrains.compose.ui:ui-util:1.4.0")
            }
        }

        val jvmTest by getting {}
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "VideoCutter"
            packageVersion = "1.0.0"
        }
    }
}
