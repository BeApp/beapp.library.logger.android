package fr.beapp.logger.appender;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import fr.beapp.logger.Logger;

/**
 * Send log messages on <a href="https://fabric.io/kits/android/crashlytics">Crashlytics by Fabric</a>.
 */
public class CrashReportingAppender extends Appender {

	public CrashReportingAppender() {
		this(Log.INFO);
	}

	public CrashReportingAppender(@Logger.LogLevel int level) {
		super(level);
	}

	@Override
	public void log(@Logger.LogLevel int priority, @NonNull String message, @Nullable Throwable tr) {
		Crashlytics.log(String.format("%s: %s", Logger.findLevelName(priority), message));

		if (tr != null) {
			Crashlytics.logException(tr);
		}
	}
}