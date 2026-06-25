plugins {
    `java-library`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(libs.android.lint.api)
    compileOnly(libs.android.lint.checks)
}

tasks.jar {
    manifest {
        attributes["Lint-Registry-v2"] = "com.omniflow.lint.OmniFlowIssueRegistry"
    }
}
