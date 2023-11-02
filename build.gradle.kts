// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
	alias(libs.plugins.android.library) apply false
	alias(libs.plugins.dokka) apply false
	alias(libs.plugins.jacoco) apply false
}

buildscript {
	dependencies {
		classpath(libs.gradle.tools.get())
		classpath(libs.kotlin.gradle.plugin.get())
	}
}