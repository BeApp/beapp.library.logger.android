package fr.beapp.logger.appender

import android.util.Log
import com.huawei.agconnect.crash.AGConnectCrash
import fr.beapp.logger.Logger
import fr.beapp.logger.Logger.findLevelName

/**
 * Send log messages on [Crash by Huawei](https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-Guides/agc-crash-getstarted).
 */
public open class HuaweiCrashReportingAppender(@Logger.LogLevel level: Int = Log.INFO) : Appender(level) {
    override fun log(@Logger.LogLevel priority: Int, message: String, tr: Throwable?) {
        if (message.isNotEmpty()) {
            AGConnectCrash.getInstance().log(String.format("%s: %s", findLevelName(priority), message))
        }
        if (tr != null) {
            AGConnectCrash.getInstance().log(Log.ERROR, tr.localizedMessage)
        }
    }
}