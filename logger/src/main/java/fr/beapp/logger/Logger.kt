package fr.beapp.logger

import android.util.Log
import androidx.annotation.IntDef
import fr.beapp.logger.appender.Appender
import fr.beapp.logger.formatter.DefaultFormatter
import fr.beapp.logger.formatter.Formatter
import fr.beapp.logger.formatter.SafeFormatter

/**
 * Logger class with enhanced {@link Formatter} and possibility to add multiple {@link Appender}s.
 * <br/>
 * This implementation was inspired by both :
 * <ul>
 * <li><a href="https://github.com/JakeWharton/timber">Square's Timber</a></li>
 * <li><a href="http://www.slf4j.org/">QOS's SLF4J</a></li>
 * </ul>
 */
public object Logger {

	@PublishedApi
	internal val APPENDERS: MutableList<Appender> = mutableListOf()

	@Volatile
	public var formatter: Formatter = SafeFormatter(DefaultFormatter())
		/**
		 * Set the {@link Formatter} to use in order to format every log message. The formatted message will be used on all {@link Appender}s.
		 * <br/>
		 * Default is {@link DefaultFormatter}.
		 *
		 * @param value The formatter to use
		 */
		@JvmStatic
		public set(value) {
			field = SafeFormatter(value)
		}


	/**
	 * Remove all {@link Appender}s already added
	 */
	@JvmStatic
	public fun removeAllAppenders() {
		synchronized(APPENDERS) {
			APPENDERS.clear()
		}
	}

	/**
	 * Add a new {@link Appender} to use to log messages
	 *
	 * @param appender The appender to add
	 */
	@JvmStatic
	public fun add(appender: Appender) {
		synchronized(APPENDERS) {
			APPENDERS.add(appender)
		}
	}

	/**
	 * Return all {@link Appender}s matching the given class
	 *
	 * @return a {@link List} of {@link Appender} matching the given class
	 */
	public inline fun <reified T : Appender> filterInstances(): List<T> {
		return APPENDERS.filterIsInstance<T>()
	}

	/**
	 * Return all {@link Appender}s matching the given class
	 * *Important* : this method is not type-safe and should be used only when the type can't be inferred
	 * ( available for java compatibility)
	 *
	 * @return a {@link List} of {@link Appender} matching the given class
	 */
	@Suppress("UNCHECKED_CAST")
	@JvmStatic
	public fun <T : Appender> findOfType(jClass: Class<T>): List<T> {
		return APPENDERS.filter { jClass.isInstance(it) } as? List<T> ?: emptyList()
	}


	/**
	 * Log a TRACE message with optional format args.
	 */
	@JvmStatic
	public fun trace(message: String, vararg args: Any): Unit = log(Log.VERBOSE, null, message, args)

	/**
	 * Log a DEBUG message with optional format args.
	 */
	@JvmStatic
	public fun debug(message: String, vararg args: Any): Unit = log(Log.DEBUG, null, message, args)

	/**
	 * Log an INFO message with optional format args.
	 */
	@JvmStatic
	public fun info(message: String, vararg args: Any): Unit = log(Log.INFO, null, message, args)

	/**
	 * Log a WARNING message with optional format args.
	 */
	@JvmStatic
	@JvmOverloads
	public fun warn(
		message: String? = null,
		throwable: Throwable? = null,
		vararg args: Any
	): Unit = log(Log.WARN, throwable, message, args)

	/**
	 * Log an ERROR message with optional format args.
	 */
	@JvmStatic
	@JvmOverloads
	public fun error(
		message: String? = null,
		throwable: Throwable? = null,
		vararg args: Any
	): Unit = log(Log.ERROR, throwable, message, args)


	/**
	 * Log an WTF message with optional format args.
	 */
	@JvmStatic
	public fun wtf(
		message: String? = null,
		throwable: Throwable? = null,
		vararg args: Any
	): Unit = log(Log.ASSERT, throwable, message, args)

	@JvmStatic
	public fun findLevelName(@LogLevel priority: Int): String {
		return when (priority) {
			Log.VERBOSE -> "TRACE"
			Log.DEBUG -> "DEBUG"
			Log.INFO -> "INFO"
			Log.WARN -> "WARN"
			Log.ERROR -> "ERROR"
			Log.ASSERT -> "WTF"
			else -> ""
		}
	}

	@JvmStatic
	private fun log(@LogLevel priority: Int, throwable: Throwable?, message: String?, vararg args: Any) {
		var formattedMessage: String? = null
		// Check if this appender can log at this level
		APPENDERS.forEach loop@{ appender ->
			if (!appender.isLoggable(priority)) return@loop

			// If the message wasn't formatted yet, we generate it. If it's still empty, stop here as we'll have nothing to log
			if (formattedMessage == null) {
				formattedMessage = formatter.format(throwable, message, args)
				if (formattedMessage == null || formattedMessage?.isEmpty() == true) {
					return
				}
			}

			appender.log(priority, formattedMessage ?: return, throwable)
		}

	}

	@IntDef(Log.VERBOSE, Log.DEBUG, Log.INFO, Log.WARN, Log.ERROR, Log.ASSERT)
	@Retention(AnnotationRetention.SOURCE)
	public annotation class LogLevel
}
