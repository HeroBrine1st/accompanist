plugins {
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.vanniktech.mavenPublish)
}

subprojects {
    group = "ru.herobrine1st.accompanist"
    version = "0.1.0"
    plugins.withId("com.vanniktech.maven.publish") {
        mavenPublishing {
            pom {
                licenses {
                    license {
                        name = "GNU General Public License, Version 3"
                        url = "https://www.gnu.org/licenses/gpl-3.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "herobrine1st"
                        name = "HeroBrine1st Erquilenne"
                        email = "accompanist@herobrine1st.ru"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/HeroBrine1st/accompanist.git"
                    developerConnection = "scm:git:ssh://github.com/HeroBrine1st/accompanist.git"
                    url = "http://github.com/HeroBrine1st/accompanist"
                }
            }
        }
        configure<PublishingExtension> {
            publishing.repositories {
                maven {
                    name = "forgejo"
                    url = uri("https://git.herobrine1st.ru/api/packages/HeroBrine1st/maven")

                    credentials(PasswordCredentials::class)
                }
            }
        }
    }
}