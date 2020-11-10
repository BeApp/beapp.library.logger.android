package fr.beapp.logger.appender;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import fr.beapp.logger.Logger;

/**
 * Send log messages on <a href="https://firebase.google.com/docs/crashlytics/get-started?platform=android">Crashlytics by Firebase</a>.
 */
public class FirebaseCrashReportingAppender extends Appender {

	public FirebaseCrashReportingAppender() {
		this(Log.INFO);
	}

	public FirebaseCrashReportingAppender(@Logger.LogLevel int level) {
		super(level);
	}

	@Override
	public void log(@Logger.LogLevel int priority, @NonNull String message, @Nullable Throwable tr) {
		FirebaseCrashlytics.getInstance().log(String.format("%s: %s", Logger.findLevelName(priority), message));

		if (tr != null) {
			FirebaseCrashlytics.getInstance().recordException(tr);
		}
	}
}