package fr.beapp.logger.appender

import fr.beapp.logger.Logger

/**
 * An appender is intended to write the log message somewhere
 */
public abstract class Appender(@Logger.LogLevel protected val level: Int) {

	/**
	 * Called in order to log the message according to the appender's strategy.
	 *
	 * @param priority [@{@link fr.beapp.logger.Logger.LogLevel}] Priority of the log message
	 * @param message  The log message
	 * @param tr       An optional {@link Throwable} to display in the log
	 */
	public abstract fun log(@Logger.LogLevel priority: Int, message: String, tr: Throwable?)

	public open fun isLoggable(@Logger.LogLevel priority: Int): Boolean {
		return priority >= this.level
	}

}
