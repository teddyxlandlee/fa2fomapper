plugins {
    id("java")
    `maven-publish`
}

group = "xland.mcmodbridge"
version = "1.0.1"

repositories {
    maven(url = "https://maven.aliyun.com/repository/public") {
        name = "Aliyun"
    }
    maven(url = "https://lss233.littleservice.cn/repositories/minecraft") {
        name = "Lss233's Mirror"
    }
    //mavenCentral()
}

dependencies {
    implementation("cpw.mods", "modlauncher", "8.1.3")

    implementation("com.google.guava", "guava", "21.0")
    compileOnly("com.google.code.findbugs", "jsr305", "3.0.2")
    runtimeOnly("org.apache.logging.log4j", "log4j-core", "2.17.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
