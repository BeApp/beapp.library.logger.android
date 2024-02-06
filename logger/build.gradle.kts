import com.android.build.gradle.api.BaseVariantOutput
import java.io.ByteArrayOutputStream
import java.util.Locale

plugins {
	id("com.android.library")
	id("org.jetbrains.kotlin.android")
//    id("https://bitbucket.org/beappers/beapp.gradle/raw/master/publish-library.gradle") out of date
	id("maven-publish")
	id("org.jetbrains.dokka")
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
	compileSdk = 34

	defaultConfig {
		minSdk = 15

	}

	kotlin.explicitApi()

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
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}

	publishing.singleVariant("release")
}

dependencies {
	implementation(loggerCatalog.annotation.get())

	compileOnly(loggerCatalog.firebase.crashlytics.get())
	compileOnly(loggerCatalog.huawei.crash.get())

	testImplementation(loggerCatalog.junit.api.get())
	testImplementation(loggerCatalog.junit.engine.get())
}

/** Tests and test Coverage */
tasks.withType(Test::class.java) {
	useJUnitPlatform()
}
jacoco {
	toolVersion = "0.8.11"
	reportsDirectory.set(layout.buildDirectory.dir("jacoco"))
}

tasks.withType<Test>().configureEach {
	extensions.configure<JacocoTaskExtension> {
		isIncludeNoLocationClasses = true
		excludes = listOf("jdk.internal.*")
	}
}

/** Library publication  */
publishing {
	publications {
		register<MavenPublication>("release") {
			groupId = android.namespace
			artifactId = project.name
			version = generateVersionName(forceRelease = true)

			pom {
				description = project.description
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
	}
}
/** Git utils **/
fun getGitOriginUrl(): String {
	val baos = ByteArrayOutputStream()
	project.exec {
		commandLine("git", "config", "--get", "remote.origin.url")
		standardOutput = baos
	}
	return baos.toString().trim()
}