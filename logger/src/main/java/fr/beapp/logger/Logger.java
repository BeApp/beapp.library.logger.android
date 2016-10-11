package fr.beapp.logger;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fr.beapp.logger.appender.Appender;
import fr.beapp.logger.formatter.DefaultFormatter;
import fr.beapp.logger.formatter.Formatter;

/**
 * Logger class with enhanced {@link Formatter} and possibility to add multiple {@link Appender}s.
 * <br/>
 * This implementation was inspired by both :
 * <ul>
 * <li><a href="https://github.com/JakeWharton/timber">Square's Timber</a></li>
 * <li><a href="http://www.slf4j.org/">QOS's SLF4J</a></li>
 * </ul>
 */
public class Logger {

	private static final List<Appender> APPENDERS = new ArrayList<>();
	private static Formatter formatter = new DefaultFormatter();

	/**
	 * Set the {@link Formatter} to use in order to format every log message. The formatted message will be used on all {@link Appender}s.
	 * <br/>
	 * Default is {@link DefaultFormatter}.
	 *
	 * @param formatter The formatter to use
	 */
	public static void formatter(@NonNull Formatter formatter) {
		Logger.formatter = formatter;
	}

	/**
	 * Add a new {@link Appender} to use to log messages
	 *
	 * @param appender The appender to add
	 */
	public static void add(@NonNull Appender appender) {
		synchronized (APPENDERS) {
			APPENDERS.add(appender);
		}
	}

	/**
	 * Log a TRACE message with optional format args.
	 */
	public static void trace(@Nullable String message, Object... args) {
		log(Log.VERBOSE, null, message, args);
	}

	/**
	 * Log a DEBUG message with optional format args.
	 */
	public static void debug(@Nullable String message, Object... args) {
		log(Log.DEBUG, null, message, args);
	}

	/**
	 * Log an INFO message with optional format args.
	 */
	public static void info(@Nullable String message, Object... args) {
		log(Log.INFO, null, message, args);
	}

	/**
	 * Log a WARNING message with optional format args.
	 */
	public static void warn(@Nullable String message, Object... args) {
		log(Log.WARN, null, message, args);
	}

	/**
	 * Log a WARNING exception and a message with optional format args.
	 */
	public static void warn(@Nullable String message, @Nullable Throwable tr, Object... args) {
		log(Log.WARN, tr, message, args);
	}

	/**
	 * Log an ERROR message with optional format args.
	 */
	public static void error(@Nullable String message, Object... args) {
		log(Log.ERROR, null, message, args);
	}

	/**
	 * Log an ERROR exception and a message with optional format args.
	 */
	public static void error(@Nullable String message, @Nullable Throwable tr, Object... args) {
		log(Log.ERROR, tr, message, args);
	}

	/**
	 * Log an WTF message with optional format args.
	 */
	public static void wtf(@Nullable String message, Object... args) {
		log(Log.ASSERT, null, message, args);
	}

	/**
	 * Log an WTF exception and a message with optional format args.
	 */
	public static void wtf(@Nullable String message, @Nullable Throwable tr, Object... args) {
		log(Log.ASSERT, tr, message, args);
	}

	protected static boolean isLoggable(@LogLevel int priority) {
		return true;
	}

	@SuppressWarnings("ConstantConditions")
	protected static void log(@LogLevel int priority, @Nullable Throwable tr, @Nullable String message, Object... args) {
		if (!isLoggable(priority)) {
			return;
		}

		String formattedMessage = formatter.format(tr, message, args);
		if (formattedMessage == null || formattedMessage.isEmpty()) {
			return;
		}

		for (Appender appender : APPENDERS) {
			appender.log(priority, formattedMessage, tr);
		}
	}

	@NonNull
	public static String findLevelName(@Logger.LogLevel int priority) {
		switch (priority) {
			case Log.VERBOSE:
				return "TRACE";
			case Log.DEBUG:
				return "DEBUG";
			case Log.INFO:
				return "INFO";
			case Log.WARN:
				return "WARN";
			case Log.ERROR:
				return "ERROR";
			case Log.ASSERT:
				return "WTF";
		}
		return "";
	}

	@IntDef({Log.VERBOSE, Log.DEBUG, Log.INFO, Log.WARN, Log.ERROR, Log.ASSERT})
	public @interface LogLevel {
	}

}
