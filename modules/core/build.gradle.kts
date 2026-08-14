dependencies {
    api(project(":modules:common"))
    implementation(libs.jline)
    implementation(libs.minestom)
    testImplementation(libs.minestom.testing)
}