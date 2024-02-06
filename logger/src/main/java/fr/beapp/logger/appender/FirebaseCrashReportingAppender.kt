package fr.beapp.logger.appender

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import fr.beapp.logger.Logger
import fr.beapp.logger.Logger.findLevelName

/**
 * Send log messages on [Crashlytics by Firebase](https://firebase.google.com/docs/crashlytics/get-started?platform=android).
 */
open class FirebaseCrashReportingAppender(@Logger.LogLevel level: Int = Log.INFO) : Appender(level) {
    override fun log(@Logger.LogLevel priority: Int, message: String, tr: Throwable?) {
        FirebaseCrashlytics.getInstance().log(String.format("%s: %s", findLevelName(priority), message))
        if (tr != null) {
            FirebaseCrashlytics.getInstance().recordException(tr)
        }
    }
}