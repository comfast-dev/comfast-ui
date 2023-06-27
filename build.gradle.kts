import java.net.URI

plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    signing

    id("io.freefair.lombok") version "8.0.1"
}

group = "dev.comfast"
version = "0.2.5"

dependencies {
    implementation("org.jetbrains:annotations:24.0.1")
    implementation("org.slf4j:slf4j-api:2.0.5")

    implementation("dev.comfast:comfast-commons:0.3.6")
    api("org.seleniumhq.selenium:selenium-java:4.8.3")
    implementation("org.seleniumhq.selenium:selenium-java:4.8.3")

    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation("org.assertj:assertj-core:3.24.2")
}

tasks.test {
    maxParallelForks = 1
    useJUnitPlatform()
    systemProperty("file.encoding", "UTF-8")
}

tasks.register<Test>("allTests") {
    useJUnitPlatform()
    systemProperty("file.encoding", "UTF-8")
    systemProperty("allTests", "true")
}

/// COMMON
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11

    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
    mavenLocal()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8" //required to properly encode/decode NonASCII characters
//    options.isWarnings = true
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        val opts = (options as StandardJavadocDocletOptions)
        opts.addBooleanOption("html5", true)
        opts.addBooleanOption("Xdoclint:all,-missing", true)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            pom {
                name.set("Comfast UI")
                description.set("Browser Automation library / framework based on Selenium")
                url.set("https://comfast.dev/open-source")
                licenses {
                    license {
                        name.set("GNU Affero General Public License v3.0")
                        url.set("https://www.gnu.org/licenses/agpl-3.0.en.html")
                    }
                }
                developers {
                    developer {
                        id.set("piotrkluz")
                        name.set("Piotr Kluz")
                        email.set("piox89@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com:comfast-dev/comfast-ui.git")
                    developerConnection.set("scm:git:git://github.com:comfast-dev/comfast-ui.git")
                    url.set("https://github.com/comfast-dev/comfast-ui")
                }
            }
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "OSSRH"
            url = if (project.version.toString().endsWith("-SNAPSHOT"))
                URI.create("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            else URI.create("https://s01.oss.sonatype.org/content/repositories/releases/")

            //load from %USER_HOME%/.gradle/gradle.properties
            val ossrhUsername: String? by project
            val ossrhPassword: String? by project
            credentials {
                username = ossrhUsername ?: System.getenv("MAVEN_USERNAME")
                password = ossrhPassword ?: System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["maven"])
}