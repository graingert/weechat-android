import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main

plugins {
    id("com.android.library")
}

dependencies {
    implementation("org.aspectj:aspectjrt:1.9.6")
    implementation("androidx.annotation:annotation:1.1.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testImplementation("org.mockito:mockito-core:3.5.13")
}

android {
    compileSdkVersion(29)
    buildToolsVersion("29.0.2")

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildTypes {
        getByName("debug") {}

        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }

        create("dev") { initWith(getByName("release")) }
    }

    defaultConfig {
        targetSdkVersion(29)
        minSdkVersion(16)
    }
}

tasks.withType<JavaCompile> {
    doLast {
        println("weaving cats...")

        val args = arrayOf("-showWeaveInfo",
                           "-1.5",
                           "-inpath", destinationDir.toString(),
                           "-aspectpath", classpath.asPath,
                           "-d", destinationDir.toString(),
                           "-classpath", classpath.asPath,
                           "-bootclasspath", android.bootClasspath.joinToString(File.pathSeparator))

        val handler = MessageHandler(true)
        Main().run(args, handler)

        val log = project.logger
        for (message in handler.getMessages(null, true)) {
            when (message.kind) {
                IMessage.DEBUG -> log.warn("DEBUG " + message.message, message.thrown)
                IMessage.INFO -> log.warn("INFO: " + message.message, message.thrown)
                IMessage.WARNING -> log.warn("WARN: " + message.message, message.thrown)
                IMessage.FAIL,
                IMessage.ERROR,
                IMessage.ABORT -> log.error("ERROR: " + message.message, message.thrown)
            }
        }
    }
}
