plugins {
    alias(libs.plugins.kraken)
}

dependencies {
    implementation(libs.bundles.grpc)
}

maven {
    url = "https://github.com/mythicisland/island"
    description = "Common utilities and classes for our services and server."

    licenses {
        license {
            name = "Apache 2.0"
            url = "https://www.apache.org/licenses/LICENSE-2.0"
        }
    }

    developers {
        developer {
            id = "xxjanisxx"
            name = "Janis"
            email = "xxjanisxx@proton.me"
        }
    }

    scm {
        url = "https://github.com/mythicisland/island"
        connection = "scm:git:https://github.com/mythicisland/island.git"
        developerConnection = "scm:git:ssh://git@github.com/mythicisland/island.git"
    }

    ciManagement {
        system = "GitHub Actions"
        url = "https://github.com/mythicisland/island/actions"
    }

    organization {
        name = "Mythic Island"
        url = "https://mythicisland.net"
    }

    repositories {
        register("public") {
            url.set("https://repo.mythicisland.net/public")
            username.set(System.getenv("MAVEN_USER"))
            password.set(System.getenv("MAVEN_TOKEN"))
        }
    }
}
