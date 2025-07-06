plugins {
	java
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
	jacoco
}

group = "com.zerotrust"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	runtimeOnly("com.h2database:h2:2.3.232")
	// https://mvnrepository.com/artifact/commons-codec/commons-codec
	implementation("commons-codec:commons-codec:1.18.0")

	// Security
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server:3.5.3")
	implementation("org.springframework.boot:spring-boot-starter-security:3.5.3")
	implementation("com.auth0:java-jwt:4.5.0")
	// https://mvnrepository.com/artifact/com.eatthepath/java-otp
	implementation("com.eatthepath:java-otp:0.4.0")

	// Tests
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.13.2")
	testImplementation("org.mockito:mockito-core:5.18.0")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)

	reports {
		xml.required.set(true)
		csv.required.set(false)
		html.required.set(true)
	}

	classDirectories.setFrom(
		fileTree("build/classes/java/main") {
			exclude("com/zerotrust/auth_gateway/AuthGatewayApplication.class")
		}
	)

	sourceDirectories.setFrom(files("src/main/java"))
	executionData.setFrom(fileTree(buildDir).include("/jacoco/test.exec"))
}

tasks.withType<Test> {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
}
