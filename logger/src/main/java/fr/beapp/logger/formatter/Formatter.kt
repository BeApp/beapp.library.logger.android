package fr.beapp.logger.formatter;

/**
 * The formatter is used to format the log message which will be sent to {@link fr.beapp.logger.appender.Appender}s.
 */
public fun interface Formatter {

	/**
	 * Called to format the message which will be logged by {@link fr.beapp.logger.appender.Appender}s
	 *
	 * @param tr      An optional {@link Throwable}
	 * @param message The log message to format
	 * @param args    Optional arguments used to format the message
	 * @return The formatted message, or <code>null</code> if anything went wrong
	 */
	public fun format(tr: Throwable?, message: String?, vararg args: Any?): String?

}
