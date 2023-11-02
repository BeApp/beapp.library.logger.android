// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
	id("com.android.library") version "8.1.1" apply false
	kotlin("jvm") version "1.3.31" apply false
	id("org.jetbrains.dokka") version "1.9.0"
	id("com.mxalbert.gradle.jacoco-android") version "0.2.0" apply false
	jacoco
}

buildscript {
	dependencies {
		classpath("com.android.tools.build:gradle:4.2.2")
		classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10")
	}
}