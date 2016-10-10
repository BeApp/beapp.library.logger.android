package fr.beapp.logger.appender;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;

import fr.beapp.logger.Logger;

public class CrashReportingAppender extends Appender {

	@Override
	public void log(@Logger.LogLevel int priority, @NonNull String tag, @NonNull String message, @Nullable Throwable t) {
		Crashlytics.log(String.format("%s: %s", Logger.findLevelName(priority), message));

		if (t != null) {
			Crashlytics.logException(t);
		}
	}
}