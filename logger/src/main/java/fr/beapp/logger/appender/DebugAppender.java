package fr.beapp.logger.appender;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import fr.beapp.logger.Logger;

public class DebugAppender extends Appender {
	private static final int MAX_LOG_LENGTH = 4000;

	private final String tag;

	public DebugAppender(String tag) {
		this.tag = tag;
	}

	@Override
	public void log(@Logger.LogLevel int priority, @NonNull String message, @Nullable Throwable t) {
		if (message.length() < MAX_LOG_LENGTH) {
			if (priority == Log.ASSERT) {
				Log.wtf(tag, message);
			} else {
				Log.println(priority, tag, message);
			}
			return;
		}

		// Split by line, then ensure each line can fit into Log's maximum length.
		for (int i = 0, length = message.length(); i < length; i++) {
			int newline = message.indexOf('\n', i);
			newline = newline != -1 ? newline : length;
			do {
				int end = Math.min(newline, i + MAX_LOG_LENGTH);
				String part = message.substring(i, end);
				if (priority == Log.ASSERT) {
					Log.wtf(tag, part);
				} else {
					Log.println(priority, tag, part);
				}
				i = end;
			} while (i < newline);
		}
	}

}
