plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(
    "games:battle",
    "games:lobby"
)

rootProject.name = "island"