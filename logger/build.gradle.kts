import com.android.build.gradle.api.BaseVariantOutput
import java.io.ByteArrayOutputStream
import java.util.Locale

plugins {
	id("com.android.library")
//    id("https://bitbucket.org/beappers/beapp.gradle/raw/master/publish-library.gradle") FixMe out of date ?
	id("maven-publish")
	id("org.jetbrains.dokka")
	jacoco
	id("com.mxalbert.gradle.jacoco-android")
}

val versionMajor = 1
val versionMinor = 4
val versionPatch = 0
val versionBuild = 0

fun generateVersionCode() = versionMajor * 1_000_000 + versionMinor * 10_000 + versionPatch * 100 + versionBuild

fun generateVersionName(forceRelease: Boolean? = null): String {
	val isRelease = forceRelease ?: gradle.startParameter.taskRequests.toString().lowercase(Locale.getDefault()).contains("release")
	val isProd = gradle.startParameter.taskRequests.toString().lowercase(Locale.getDefault()).contains("prod")

	// display build number except for prod release
	return if (isRelease || isProd) {
		"$versionMajor.$versionMinor.$versionPatch"
	} else {
		"$versionMajor.$versionMinor.$versionPatch-$versionBuild"
	}
}

fun BaseVariantOutput.renameAarFile() {
	if (outputFile == null || !outputFile.name.endsWith(".aar")) return
	val isRelease = name.contains("release")
	val fileName = "${project.name}-${generateVersionName(forceRelease = isRelease)}${if (!isRelease) "-$name" else ""}.aar"

	val renamedFile = File(outputFile.parent, fileName)
	outputFile.renameTo(renamedFile)
}

android {
	namespace = "fr.beapp.logger"
	description = "A logger library to wrap and enhanced default Android logs"
	compileSdk = 30

	defaultConfig {
		minSdk = 15

	}

	buildTypes {
		debug {
			testCoverage {
				enableUnitTestCoverage = true
			}
		}
		release {
			isMinifyEnabled = true
			//TODO proguard
		}
	}

	libraryVariants.all {
		outputs.all {
			renameAarFile()
		}

	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}

	publishing.singleVariant("release")
}

dependencies {
	implementation("com.android.support:support-annotations:28.0.0")

	compileOnly("com.google.firebase:firebase-crashlytics:17.3.0") {
		isTransitive = true
	}
	compileOnly("com.huawei.agconnect:agconnect-crash:1.4.1.300")

	testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
	testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}


jacoco {
	toolVersion = "0.8.11"
	reportsDirectory.set(layout.buildDirectory.dir("jacoco"))
}


tasks.withType(Test::class.java) {
	useJUnitPlatform()
}

tasks.withType<Test>().configureEach {
	extensions.configure<JacocoTaskExtension> {
		isIncludeNoLocationClasses = true
		excludes = listOf("jdk.internal.*")
	}
}



publishing {
	publications {
		register<MavenPublication>("release") {
			groupId = android.namespace
			artifactId = "logger"
			version = generateVersionName(forceRelease = true)

			pom {
				licenses {
					license {
						name = "The Apache Software License, Version 2.0"
						url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
						distribution = "[\"Apache-2.0\"]"
					}
				}

				developers {
					developer {
						id = "dvilleneuve"
						name = "Damien Villeneuve"
						email = "d.villeneuve@beapp.fr"
					}
				}

				scm {
					val gitUrl = getGitOriginUrl()
					connection = "scm:git:$gitUrl"
					developerConnection = "scm:git:$gitUrl"
					url = "https://bitbucket.org/beappers/beapp.logger.andro"
				}
			}

			afterEvaluate {
				from(components["release"])
			}
		}
//		artifacts {
//			add("release", file("$buildDir/outputs/aar/${project.name}-release.aar"))
//		}
	}
}

fun getGitOriginUrl(): String {
	val baos = ByteArrayOutputStream()
	val exec = project.exec {
		commandLine("git", "config", "--get", "remote.origin.url")
		standardOutput = baos
	}
	return baos.toString().trim()
}


//apply froxm: "https://bitbucket.org/beappers/beapp.gradle/raw/master/publish-library.gradle"
