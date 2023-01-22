plugins {
    java
    alias(libs.plugins.blossom)
    alias(libs.plugins.runvelocity)
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/") {
        content {
            includeGroup("com.velocitypowered")
        }
    }
    mavenCentral()
}

dependencies {
    compileOnly(libs.velocity)
    annotationProcessor(libs.velocity)
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(11)
    }
    clean {
        delete("run")
    }
    runVelocity {
        velocityVersion(libs.versions.velocity.get())
    }
}

blossom {
    replaceTokenIn("src/main/java/me/adrianed/serverpermissions/utils/Constants.java")
    replaceToken("{version}", project.version)
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(11))
