plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "com.dk"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

extra["springBootAdminVersion"] = "3.3.5"

sourceSets {
    create("jmx") {
        kotlin {
            srcDir("src/main/kotlin")
            include("**/JmxInvoke.kt")
        }
    }
    main {
        kotlin {
            exclude("**/JmxInvoke.kt")
        }
    }
}

springBoot {
    mainClass.set("com.dk.springbootkubernetes.SpringBootKubernetesApplicationKt")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("de.codecentric:spring-boot-admin-starter-client")
    implementation("de.codecentric:spring-boot-admin-starter-server")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    "jmxImplementation"(kotlin("stdlib"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.register<Jar>("jmxJar") {
    archiveBaseName.set("jmx-invoke")
    from(sourceSets["jmx"].output)
    from(configurations["jmxRuntimeClasspath"].map { if (it.isDirectory) it else zipTree(it) })
    manifest {
        attributes["Main-Class"] = "com.dk.springbootkubernetes.JmxInvokeKt"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

dependencyManagement {
    imports {
        mavenBom("de.codecentric:spring-boot-admin-dependencies:${property("springBootAdminVersion")}")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
tasks.jar {
    // Disable the plain jar compilation
    enabled = false
}