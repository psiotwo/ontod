plugins {
    kotlin("jvm") version "1.8.0"
    id("maven-publish")
}

group = "com.github.psiotwo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation("net.sourceforge.owlapi:owlapi-distribution:5.5.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

configure<PublishingExtension> {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "com.github.psiotwo"
            artifactId = "ontod"
            version = "0.0.1-SNAPSHOT"
            from(components["java"])
        }
    }
}
