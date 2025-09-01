import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    `maven-publish`
    id("com.gradleup.shadow") version "9.0.2"
}

group = "net.dmulloy2"
version = "3.4.0-SNAPSHOT"
description = "SwornRPG"

var isSnapshot = version.toString().endsWith("-SNAPSHOT")

repositories {
    mavenLocal()
    mavenCentral()

    maven {
        name = "WorldEdit"
        url = uri("https://maven.enginehub.org/repo/")
    }

    maven {
        name = "Essentials"
        url = uri("https://ci.ender.zone/plugin/repository/everything/")
    }

    maven {
        name = "Vault"
        url = uri("https://jitpack.io")
    }
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")

    implementation("org.dizitart:nitrite:4.3.0")
    implementation("org.dizitart:nitrite-mvstore-adapter:4.3.0")

    implementation("net.dmulloy2:swornapi:2.0.0-SNAPSHOT")
    compileOnly("net.ess3:EssentialsX:2.17.2") {
        isTransitive = false
    }
    compileOnly("net.dmulloy2:swornnations:1.4.0-SNAPSHOT") {
        isTransitive = false
    }
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        isTransitive = false
    }
    compileOnly("net.dmulloy2:swornguns:3.0.0-SNAPSHOT") {
        isTransitive = false
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks {
    named<ShadowJar>("shadowJar"){
        dependencies {
            include(dependency("net.dmulloy2:swornapi:.*"))
        }
        relocate("net.dmulloy2.swornapi", "net.dmulloy2.swornrpg.swornapi")
        archiveFileName.set("SwornRPG.jar")
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    test {
        useJUnitPlatform()
    }

    processResources {
        var buildNumber = System.getenv("BUILD_NUMBER")
        var fullVersion = if (isSnapshot && buildNumber != null)
            "$version-$buildNumber"
        else
            version.toString()

        eachFile { expand("version" to fullVersion) }
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}
