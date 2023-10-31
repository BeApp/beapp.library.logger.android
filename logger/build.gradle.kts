import java.util.Locale

plugins {
	id("com.android.library")
//    id("https://bitbucket.org/beappers/beapp.gradle/raw/master/publish-library.gradle") FixMe out of date ?
//	id("jacoco-android")
//	id("org.sonarqube")
	id("maven-publish")
	id("org.jetbrains.dokka")
}

val versionMajor = 1
val versionMinor = 4
val versionPatch = 0
val versionBuild = 0

fun generateVersionCode() = versionMajor * 1_000_000 + versionMinor * 10_000 + versionPatch * 100 + versionBuild

fun generateVersionName(): String {
	val isRelease = gradle.startParameter.taskRequests.toString().lowercase(Locale.getDefault()).contains("release")
	val isProd = gradle.startParameter.taskRequests.toString().lowercase(Locale.getDefault()).contains("prod")

	// display build number except for prod release
	return if (isRelease || isProd) {
		"$versionMajor.$versionMinor.$versionPatch"
	} else {
		"$versionMajor.$versionMinor.$versionPatch-$versionBuild"
	}
}

android {
	namespace = "fr.beapp.logger"
	compileSdk = 30

	defaultConfig {
		minSdk = 15
	}
    
    buildTypes {
        release {
            isMinifyEnabled = true
            //TODO proguard
        }
    }

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
}

dependencies {
	implementation("com.android.support:support-annotations:28.0.0")

	compileOnly("com.google.firebase:firebase-crashlytics:17.3.0") {
		isTransitive = true
	}
	compileOnly("com.huawei.agconnect:agconnect-crash:1.4.1.300")

	testImplementation("junit:junit:4.13.2")
}

//ext {
//    libraryName = "Beapp Logger"
//    libraryGroupId = "fr.beapp.logger"
//    libraryArtifactId = "logger"
//    libraryVersion = "1.4"
//    libraryPackaging = "aar"
//
//    libraryDescription = "A logger library to wrap and enhanced default Android logs"
//
//    siteUrl = "https://bitbucket.org/beappers/beapp.logger.andro"
//    gitUrl = "git@bitbucket.org:beappers/beapp.logger.andro.git"
//
//    developerId = "dvilleneuve"
//    developerName = "Damien Villeneuve"
//    developerEmail = "d.villeneuve@beapp.fr"
//
//    licenseName = "The Apache Software License, Version 2.0"
//    licenseUrl = "http://www.apache.org/licenses/LICENSE-2.0.txt"
//    allLicenses = ["Apache-2.0"]
//}
//apply froxm: "https://bitbucket.org/beappers/beapp.gradle/raw/master/publish-library.gradle"


//apply plugin: "jacoco-android"
//jacoco {
//	toolVersion = "0.8.3"
//}


//FixMe migrate to sonar-project.properties
//apply plugin: "org.sonarqube"
//sonarqube {
//    androidVariant "debug"
//    properties {
//        def appProject = project(":logger")
//
//        property "sonar.host.url", "https://sonar.beapp.fr"
//        property "sonar.projectKey", "beapp.logger.andro"
//        property "sonar.projectName", "${libraryName} - Android"
//        property "sonar.projectDescription", "${libraryDescription}"
//        property "sonar.projectVersion", "${libraryVersion}"
//        property "sonar.sourceEncoding", "UTF-8"
//
//        property "sonar.junit.reportPaths", "${appProject.buildDir}/$testResultsDirName/testDebugUnitTest"
//        property "sonar.coverage.jacoco.xmlReportPaths", fileTree(dir: "${appProject.buildDir}/reports/jacoco", includes: ["**/*.xml"]).files.join(",")
//    }
//}