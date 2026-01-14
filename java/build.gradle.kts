plugins {
    id("java")
}

group = "me.sigreturn"
version = "1.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(files("libs/HytaleServer.jar"))
}
