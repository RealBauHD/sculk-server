plugins {
    id("java")
    alias(libs.plugins.shadow)
}

dependencies {
    implementation(project(":api"))

    // netty
    implementation(platform(libs.netty.bom))
    implementation("io.netty:netty5-transport")
    implementation("io.netty:netty5-codec")
    implementation("io.netty:netty5-transport-native-epoll")
    implementation("io.netty:netty5-transport-native-epoll:linux-x86_64")
    implementation("io.netty:netty5-transport-native-epoll:linux-aarch_64")

    // terminal
    implementation(libs.jline)
    runtimeOnly(libs.jansi)

    implementation(libs.bundles.log4j)
    implementation(libs.fastutil)
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "de.bauhd.minecraft.server.Main"
            attributes["Multi-Release"] = true
        }
    }

    shadowJar {
        archiveFileName.set("minecraft-server.jar")
    }

    test {
        useJUnitPlatform()
    }
}