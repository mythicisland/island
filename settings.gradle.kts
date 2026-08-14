plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(
    "modules:common",
    "modules:core",
    "modules:lobby",
    "modules:ui"
)

rootProject.name = "island"