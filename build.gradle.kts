import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.shadow)
}

allprojects {
    group = "net.mythicisland"
    version = "0.0.3"

    repositories {
        mavenCentral()
        maven("https://repo.simplecloud.app/snapshots")
        maven("https://repo.mythicisland.net/public")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.hypera.dev/snapshots")
        maven("https://buf.build/gen/maven")
    }
}

subprojects {
    apply {
        plugin("kotlin")
        plugin("com.gradleup.shadow")
    }

    dependencies {
        testImplementation(rootProject.libs.kotlin.test)
        implementation(rootProject.libs.kotlinx.coroutines.core)

        implementation(rootProject.libs.jnats)
        implementation(rootProject.libs.fastutil)
        implementation(rootProject.libs.caffeine)
        implementation(rootProject.libs.bundles.adventure)
        implementation(rootProject.libs.cloud.api)
        implementation(rootProject.libs.luckperms.minestom)
        implementation(rootProject.libs.bundles.configurate)
        implementation(rootProject.libs.bundles.logging)
    }

    kotlin {
        jvmToolchain(25)
        compilerOptions {
            jvmTarget = JvmTarget.JVM_25
            languageVersion = KotlinVersion.KOTLIN_2_4
            apiVersion = KotlinVersion.KOTLIN_2_4
        }
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    }

    tasks.withType<JavaExec> {
        jvmArgs("--enable-native-access=ALL-UNNAMED")
    }

    tasks.test {
        useJUnitPlatform()
    }

    tasks.shadowJar {
        mergeServiceFiles()
        archiveFileName.set("${project.name}.jar")
        manifest {
            attributes["Enable-Native-Access"] = "ALL-UNNAMED"
        }
    }

}