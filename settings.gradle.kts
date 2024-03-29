@file:Suppress("UnstableApiUsage")

pluginManagement {
	repositories {
		google()
		mavenCentral()
		gradlePluginPortal()
	}
}
dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		google()
		mavenCentral()
		mavenLocal()
		maven { url = uri("https://developer.huawei.com/repo/") }
	}

	versionCatalogs {
		create("loggerCatalog") {
			from(files("./logger/logger.versions.toml"))
		}
		create("libs") {
			from(files("./libs.versions.toml"))
		}
	}
}

include(":logger")
