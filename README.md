This library provides a logger mechanism inspired from both [Timber](https://github.com/JakeWharton/timber) and [SLF4J](http://www.slf4j.org/).

# Usage

Two steps are needed :

1. Configure Logger with as much appenders you as want, in the onCreate of your application class
2. Call `Logger`'s static methods everywhere throughout your app
3. (optional) You can also use a different formatter to fit your needs on message formatting
 
 
```kotlin

Logger.add(DebugAppender("MyProjectTag"))
Logger.add(FirebaseCrashReportingAppender())
Logger.add(HuaweiCrashReportingAppender())
Logger.add(FileAppender("myproject-{date}.log"))

// Optional
Logger.formatter(DefaultFormatter())

Logger.trace("This is trace test number %d", 1)
Logger.debug("This is debug test number %d", 1)
Logger.info("This is info test number %d", 1)
Logger.warn("This is warning test number %d", 1)
Logger.error("This is error test number %d", exception, 1)
```

# Installation

Add jcenter's repository in your project's repositories list, then add the dependency.

```kotlin
repositories {
	jcenter()
	// ...
	maven(url = "http://repository.beapp.fr/libs-release-local")
}

dependencies {
	implementation("fr.beapp.logger:logger:<latest-release>")
}
```