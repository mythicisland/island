plugins {
    id("application")
}

dependencies {
    implementation(project(":modules:core"))
    implementation(libs.minestom)
    testImplementation(libs.minestom.testing)
}

application {
    mainClass.set("net.mythicisland.lobby.launcher.LauncherKt")
}