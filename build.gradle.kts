plugins {
    java
    alias(libs.plugins.idea.ext)
    alias(libs.plugins.blossom)
    alias(libs.plugins.runvelocity)
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(libs.velocity)
    annotationProcessor(libs.velocity)
    compileOnly(libs.miniplaceholders)
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }
    clean {
        delete("run")
    }
    runVelocity {
        velocityVersion(libs.versions.velocity.get())
    }
}

sourceSets {
    main {
        blossom {
            javaSources {
                property("version", project.version.toString())
            }
        }
    }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))
