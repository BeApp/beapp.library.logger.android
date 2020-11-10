package fr.beapp.logger.formatter;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.WeakHashMap;

import fr.beapp.logger.Logger;

public class DefaultFormatter implements Formatter {

	private static final WeakHashMap<String, String> SHORTEN_CLASS_NAMES = new WeakHashMap<>();

	private final String stacktraceSentinel;

	public DefaultFormatter() {
		this(Logger.class.getCanonicalName());
	}

	public DefaultFormatter(String stacktraceSentinel) {
		this.stacktraceSentinel = stacktraceSentinel;
	}

	@Nullable
	@Override
	public String format(@Nullable Throwable tr, @Nullable String message, Object... args) {
		if (message == null || message.isEmpty()) {
			message = null;
		}

		if (message == null) {
			if (tr == null) {
				return null; // Swallow message if it's null and there's no throwable.
			}
			message = getStackTraceString(tr);
		} else {
			if (args.length > 0) {
				message = String.format(message, args);
			}
			if (tr != null) {
				message += "\n" + getStackTraceString(tr);
			}
		}

		String caller = retrieveCaller();
		if (caller == null)
			return message;

		return String.format("%s - %s", caller, message);
	}

	@Nullable
	protected String getStackTraceString(@NonNull Throwable t) {
		// Don't replace this with Log.getStackTraceString() - it hides UnknownHostException, which is not what we want.
		StringWriter sw = new StringWriter(256);
		PrintWriter pw = new PrintWriter(sw, false);
		try {
			t.printStackTrace(pw);
			pw.flush();
			return sw.toString();
		} finally {
			pw.close();
		}
	}

	@Nullable
	protected String retrieveCaller() {
		try {
			StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

			boolean isInLoggerClass = false;
			for (StackTraceElement stackTraceElement : stackTraceElements) {
				if (stacktraceSentinel.equals(stackTraceElement.getClassName())) {
					isInLoggerClass = true;
				} else if (isInLoggerClass) {
					return String.format(Locale.ENGLISH, "%s:%d", shortenCanonicalName(stackTraceElement.getClassName()), stackTraceElement.getLineNumber());
				}
			}
		} catch (Exception ignored) {
			// Can't use Logger.error here
		}
		return null;
	}

	@NonNull
	protected String shortenCanonicalName(@NonNull final String canonicalName) {
		if (canonicalName.isEmpty())
			return "";

		synchronized (SHORTEN_CLASS_NAMES) {
			String shortenClassName = SHORTEN_CLASS_NAMES.get(canonicalName);
			if (shortenClassName == null) {
				StringBuilder shortenName = new StringBuilder();

				int lastIndex = 0;
				int nextSep = -1;
				do {
					shortenName.append(canonicalName.charAt(nextSep + 1));
					nextSep = canonicalName.indexOf('.', nextSep + 1);

					if (nextSep > -1) {
						shortenName.append('.');
						lastIndex = nextSep + 1;
					}
				} while (nextSep > -1);

				if (lastIndex < canonicalName.length() - 1) {
					shortenName.append(canonicalName.substring(lastIndex + 1));
				}

				shortenClassName = shortenName.toString();
				SHORTEN_CLASS_NAMES.put(canonicalName, shortenClassName);
			}

			return shortenClassName;
		}
	}

}
