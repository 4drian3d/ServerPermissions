plugins {
    java
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
        options.release.set(17)
    }
    clean {
        delete("run")
    }
    runVelocity {
        velocityVersion(libs.versions.velocity.get())
    }
}

blossom {
    replaceTokenIn("src/main/java/io/github/_4drian3d/serverpermissions/utils/Constants.java")
    replaceToken("{version}", project.version)
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))
