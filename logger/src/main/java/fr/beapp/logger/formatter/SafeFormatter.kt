@file:Suppress("MemberVisibilityCanBePrivate")

package fr.beapp.logger.formatter;

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Decorator formatter intended to filter out sensitive information as passwords.
 */
public class SafeFormatter(private val delegate: Formatter, private val sensitivePattern: Pattern) : Formatter {

	public companion object {
		@JvmStatic
		public fun patternFor(key: String?, vararg otherKeys: String): Pattern {
			if (key == null) throw IllegalArgumentException("Key can't be null");

			val builder: StringBuilder = StringBuilder(key)
			otherKeys.forEach { otherKey ->
				builder.append('|').append(otherKey)
			}
			return Pattern.compile("([\"']?(?:$builder)[\"']?\\s*[=:]?\\s*[\"']?)([^\"']+)")
		}

	}

	public constructor(delegate: Formatter, key: String, vararg otherKeys: String) : this(delegate, patternFor(key, *otherKeys))
	public constructor(delegate: Formatter) : this(delegate, "password", "pass")

	override fun format(tr: Throwable?, message: String?, vararg args: Any?): String? {
		if (message != null) {
			// Do not pass args to delegate as they were already consumed
			return delegate.format(tr, cleanupMessage(message, *args))
		}

		// Still call delegate as it may do some specific work on this case
		return delegate.format(tr, null, *args)
	}

	public fun cleanupMessage(message: String, vararg args: Any?): String {
		val matcher: Matcher = sensitivePattern.matcher(
			if (args.isEmpty()) message
			else String.format(message, *args)
		)
		return matcher.replaceAll("$1[HIDDEN]")
	}

}
