package fr.beapp.logger.appender;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.agconnect.crash.AGConnectCrash;

import fr.beapp.logger.Logger;

/**
 * Send log messages on <a href="https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-Guides/agc-crash-getstarted">Crash by Huawei</a>.
 */
public class HuaweiCrashReportingAppender extends Appender {

	public HuaweiCrashReportingAppender() {
		this(Log.INFO);
	}

	public HuaweiCrashReportingAppender(@Logger.LogLevel int level) {
		super(level);
	}

	@Override
	public void log(@Logger.LogLevel int priority, @NonNull String message, @Nullable Throwable tr) {
		if (message != null && !message.isEmpty()) {
			AGConnectCrash.getInstance().log(String.format("%s: %s", Logger.findLevelName(priority), message));
		}

		if (tr != null) {
			AGConnectCrash.getInstance().log(Log.ERROR, tr.getLocalizedMessage());
		}
	}
}