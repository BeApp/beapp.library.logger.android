package fr.beapp.logger.appender

import android.util.Log
import fr.beapp.logger.Logger

/**
 * Write log messages in Android's log console
 */
@Suppress("MemberVisibilityCanBePrivate")
public open class DebugAppender(@Logger.LogLevel level: Int, private val tag: String) : Appender(level) {

	private companion object {
		private const val MAX_LOG_LENGTH: Int = 4000
	}

	private var useLevel: Boolean = true

	public constructor(tag: String) : this(Log.INFO, tag) {
		useLevel = false
	}


	override fun log(priority: Int, message: String, tr: Throwable?) {
		if (message.length < MAX_LOG_LENGTH) return logLine(priority, message)

		// Split by line, then ensure each line can fit into Log's maximum length.
		var i = 0
		while (i < message.length) {
			var newline = message.indexOf('\n', i)
			newline = if (newline != -1) newline else message.length
			do {
				val end = newline.coerceAtMost(i + MAX_LOG_LENGTH)
				logLine(priority, message.substring(i, end))
				i = end
			} while (i < newline)
			i++
		}
	}

	public override fun isLoggable(priority: Int): Boolean {
		return if (useLevel) super.isLoggable(priority) else Log.isLoggable(tag, priority)
	}

	protected open fun logLine(priority: Int, message: String) {
		if (priority == Log.ASSERT) Log.wtf(tag, message)
		else Log.println(priority, tag, message)

	}

}
