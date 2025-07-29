import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.AndroidBasePlugin
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePlugin

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.cashapp.sqldelight) apply false
    alias(libs.plugins.ksp) apply false
}


subprojects {
    project.plugins.whenPluginAdded {
        when(this) {
            is AndroidBasePlugin -> extensions.getByType<BaseExtension>().configure()
            is KotlinBasePlugin -> extensions.getByType<KotlinBaseExtension>().configure()
        }
    }
}


fun BaseExtension.configure() {
    compileSdkVersion(36)
    defaultConfig {
        minSdk = 28
        targetSdk = 36
    }
}

fun KotlinBaseExtension.configure() {
    jvmToolchain(21)
}
