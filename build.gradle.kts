plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.13.0"
}

group = "dev.davidemarcoli"
version = "0.3"

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.1.4")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf(/* Plugin Dependencies */))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    patchPluginXml {
        sinceBuild.set("221")
        untilBuild.set("231.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("INTELLI_CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("INTELLI_PRIVATE_KEY"))
        password.set(System.getenv("INTELLI_PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("INTELLI_PUBLISH_TOKEN"))
    }

//    runIde {
//        jbrVersion.set("jbr_jcef-11_0_15-linux-x64-b2043.56")
//    }
}
