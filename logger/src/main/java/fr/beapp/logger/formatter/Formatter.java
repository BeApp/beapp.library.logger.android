package fr.beapp.logger.formatter;

import androidx.annotation.Nullable;

/**
 * The formatter is used to format the log message which will be sent to {@link fr.beapp.logger.appender.Appender}s.
 */
public interface Formatter {

	/**
	 * Called to format the message which will be logged by {@link fr.beapp.logger.appender.Appender}s
	 *
	 * @param tr      An optional {@link Throwable}
	 * @param message The log message to format
	 * @param args    Optional arguments used to format the message
	 * @return The formatted message, or <code>null</code> if anything went wrong
	 */
	@Nullable
	String format(@Nullable Throwable tr, @Nullable String message, Object... args);

}
