package fr.beapp.logger.appender;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import fr.beapp.logger.Logger;

/**
 * An appender is intended to write the log message somewhere
 */
public abstract class Appender {

	protected int level;

	public Appender(@Logger.LogLevel int level) {
		this.level = level;
	}

	/**
	 * Called in order to log the message according to the appender's strategy.
	 *
	 * @param priority Priority of the log message
	 * @param message  The log message
	 * @param tr       An optional {@link Throwable} to display in the log
	 */
	public abstract void log(@Logger.LogLevel int priority, @NonNull String message, @Nullable Throwable tr);

	public boolean isLoggable(@Logger.LogLevel int priority) {
		return priority >= this.level;
	}

}
