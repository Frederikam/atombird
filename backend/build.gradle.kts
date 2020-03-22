import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    id("org.springframework.boot") version "2.3.0.M3"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    kotlin("jvm") version "1.3.70"
    kotlin("plugin.spring") version "1.3.70"
}

group = "com.frederikam"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_11

val commonsValidatorVersion = "1.6"
val romeVersion = "1.12.1"
val kotlinReactorExtensionsVersion = "1.0.2.RELEASE"
val fuelVersion = "2.2.1"
val kotlinVersion = "1.3.70"

repositories {
    mavenCentral()

    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
    maven { url = uri("https://dl.bintray.com/kittinunf/maven") }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.3.5")
    implementation("com.google.code.gson:gson")
    implementation("commons-validator:commons-validator:$commonsValidatorVersion")
    implementation("com.rometools:rome:$romeVersion")
    implementation("com.github.kittinunf.fuel:fuel:$fuelVersion")
    implementation("com.github.kittinunf.fuel:fuel-reactor:$fuelVersion")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:$kotlinReactorExtensionsVersion")
    implementation("com.google.guava:guava:28.2-jre")
    runtimeOnly("io.r2dbc:r2dbc-postgresql")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("io.projectreactor:reactor-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot.experimental:spring-boot-bom-r2dbc:0.1.0.BUILD-SNAPSHOT")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}
