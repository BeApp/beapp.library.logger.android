package fr.beapp.logger.formatter

import fr.beapp.logger.Logger
import java.io.PrintWriter
import java.io.StringWriter
import java.util.Locale
import java.util.WeakHashMap

public open class DefaultFormatter(private val stacktraceSentinel: String? = Logger::class.java.canonicalName) : Formatter {
	private companion object {
		val SHORTEN_CLASS_NAMES = WeakHashMap<String, String>()
	}

	override fun format(tr: Throwable?, message: String?, vararg args: Any?): String? {
		val computedMessage = when {
			// Swallow message if it's null and there's no throwable.
			message == null -> tr?.let(::getStackTraceString) ?: return null
			else -> {
				// format arguments if there's any
				val formattedMessage = if (args.isEmpty()) message
				else String.format(message, *args)
				// include stacktrace if there's a throwable
				if (tr != null) "$formattedMessage\n${getStackTraceString(tr)}"
				else formattedMessage
			}
		}
		val caller = retrieveCaller() ?: return computedMessage
		return String.format("%s - %s", caller, computedMessage)
	}

	public open fun getStackTraceString(t: Throwable): String? {
		// Don't replace this with Log.getStackTraceString() - it hides UnknownHostException, which is not what we want.
		val sw = StringWriter(256)
		val pw = PrintWriter(sw, false)
		return pw.use { printWriter ->
			t.printStackTrace(printWriter)
			printWriter.flush()
			sw.toString()
		}
	}

	public open fun retrieveCaller(): String? {
		try {
			val stackTraceElements = Thread.currentThread().stackTrace
			var isInLoggerClass = false
			for (stackTraceElement in stackTraceElements) {
				if (stacktraceSentinel == stackTraceElement.className) {
					isInLoggerClass = true
				} else if (isInLoggerClass) {
					return String.format(Locale.ENGLISH, "%s:%d", shortenCanonicalName(stackTraceElement.className), stackTraceElement.lineNumber)
				}
			}
		} catch (ignored: Exception) {
			// Can't use Logger.error here
		}
		return null
	}

	public open fun shortenCanonicalName(canonicalName: String): String {
		if (canonicalName.isEmpty()) return ""
		synchronized(SHORTEN_CLASS_NAMES) {
			var shortenClassName = SHORTEN_CLASS_NAMES[canonicalName]
			if (shortenClassName == null) {
				val shortenName = StringBuilder()
				var lastIndex = 0
				var nextSep = -1
				do {
					shortenName.append(canonicalName[nextSep + 1])
					nextSep = canonicalName.indexOf('.', nextSep + 1)
					if (nextSep > -1) {
						shortenName.append('.')
						lastIndex = nextSep + 1
					}
				} while (nextSep > -1)
				if (lastIndex < canonicalName.length - 1) {
					shortenName.append(canonicalName.substring(lastIndex + 1))
				}
				shortenClassName = shortenName.toString()
				SHORTEN_CLASS_NAMES[canonicalName] = shortenClassName
			}
			return shortenClassName
		}
	}


}