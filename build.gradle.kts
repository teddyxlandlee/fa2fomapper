import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes as ops

buildscript {
    dependencies {
        classpath("org.ow2.asm", "asm", "9.3")
    }
}

plugins {
    id("java")
    `maven-publish`
}

group = "xland.mcmodbridge"
version = "1.0.4"

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

task("generateModClass") {
    val fileUuid = "128ab79f-83fb-4369-b476-bac808e39019"
    val modId = "fa2fomapper"
    val file = tasks.processResources.get().destinationDir.resolve("${fileUuid}.class")
    afterEvaluate {
        if (file.exists()) file.delete()
        file.createNewFile()
        val writer = ClassWriter(3)
        writer.visit(ops.V1_6, ops.ACC_PUBLIC + ops.ACC_SUPER, fileUuid, null,
            "java/lang/Object", null)
        writer.visitMethod(ops.ACC_PUBLIC, "<init>", "()V", null, null).run {
            this.visitVarInsn(ops.ALOAD, 0)
            this.visitMethodInsn(ops.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
            this.visitInsn(ops.RETURN)
        }
        writer.visitAnnotation("Lnet/minecraftforge/fml/common/Mod;", true).run {
            this.visit("value", modId)
        }
        writer.visitSource(null, "ASM Generated")

        file.outputStream().run {
            this.write(writer.toByteArray())
            this.close()
        }
    }
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching(listOf("mods.toml", "fabric.mod.json", "quilt.mod.json5")) {
        expand("version" to project.version)
    }

    dependsOn("generateModClass")
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
