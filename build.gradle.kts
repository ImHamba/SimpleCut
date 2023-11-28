import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
//    id("dev.hydraulic.conveyor") version "1.6"
}

group = "com.ImHamba"
version = "1.0.0"

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
                implementation(compose.material3)

                // for viewmodel functionality
                implementation("moe.tlaster:precompose:1.5.7")
                implementation("moe.tlaster:precompose-viewmodel:1.5.7")

                // for displaying video
                implementation("uk.co.caprica:vlcj:4.7.0")

                // for displaying video thumbnails
                implementation("org.bytedeco:javacv-platform:1.5.9")

                // for file dialog
                implementation("org.lwjgl:lwjgl-tinyfd:3.3.3")
                implementation("org.lwjgl:lwjgl:3.3.3:natives-windows")

                implementation("org.jetbrains.compose.components:components-splitpane-desktop:1.5.10")

                implementation(compose.materialIconsExtended)

            }
        }

        val jvmTest by getting {}
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Exe)
            packageName = "SimpleCut"
            packageVersion = "1.0.0"
        }

        buildTypes.release.proguard {
//            obfuscate.set(true)
            configurationFiles.from(project.file("proguard-rules.pro"))
        }
    }
}
