package fr.beapp.logger

import android.util.Log
import fr.beapp.logger.Logger.add
import fr.beapp.logger.Logger.debug
import fr.beapp.logger.Logger.error
import fr.beapp.logger.Logger.filterInstances
import fr.beapp.logger.Logger.findLevelName
import fr.beapp.logger.Logger.findOfType
import fr.beapp.logger.Logger.info
import fr.beapp.logger.Logger.removeAllAppenders
import fr.beapp.logger.Logger.trace
import fr.beapp.logger.Logger.warn
import fr.beapp.logger.Logger.wtf
import fr.beapp.logger.appender.Appender
import fr.beapp.logger.appender.DebugAppender
import fr.beapp.logger.formatter.Formatter
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.LinkedList

class LoggerTest {
	private class DummyAppender(@Logger.LogLevel level: Int) : Appender(level) {
		val acc: MutableList<String> = LinkedList()
		override fun log(@Logger.LogLevel priority: Int, message: String, tr: Throwable?) {
			acc.add(message)
		}
	}

	private val formatter = Formatter { _: Throwable?, message: String?, _: Array<Any?>? -> "formatted: $message" }

	private val silentFormatter = Formatter { _: Throwable?, message: String?, _: Array<Any?>? -> "" }
	private val nullFormatter = Formatter { _: Throwable?, message: String?, _: Array<Any?>? -> null }

	@Test
	fun `test log`() {
		val appender1 = DummyAppender(Log.DEBUG)
		val appender2 = DummyAppender(Log.INFO)
		val appender3 = DummyAppender(Log.ASSERT)
		val appender4 = DummyAppender(Log.VERBOSE)
		removeAllAppenders()
		add(appender1)
		add(appender2)
		add(appender3)
		Logger.formatter = formatter
		trace("trace")
		debug("debug")
		info("info")
		warn("warn")
		error("error")
		warn()
		error()
		wtf("wtf")
		wtf()

		Assertions.assertArrayEquals(
			arrayOf("formatted: debug", "formatted: info", "formatted: warn", "formatted: error", "formatted: null", "formatted: null", "formatted: wtf", "formatted: null"),
			appender1.acc.toTypedArray()
		)
		Assertions.assertArrayEquals(
			arrayOf("formatted: info", "formatted: warn", "formatted: error", "formatted: null", "formatted: null", "formatted: wtf", "formatted: null"),
			appender2.acc.toTypedArray()
		)
		Assertions.assertArrayEquals(arrayOf("formatted: wtf"), appender3.acc.toTypedArray())
		Logger.formatter = silentFormatter
		removeAllAppenders()
		add(appender4)
		trace("trace")
		Assertions.assertArrayEquals(arrayOf<String>(), appender4.acc.toTypedArray())
		Logger.formatter = nullFormatter
		trace("trace")
		Assertions.assertArrayEquals(arrayOf<String>(), appender4.acc.toTypedArray())
	}

	@Test
	fun `test findOfType`() {
		val appender1 = DummyAppender(Log.INFO)
		val appender2 = DummyAppender(Log.INFO)
		removeAllAppenders()
		add(appender1)
		add(appender2)
		val debugAppenders = findOfType(DebugAppender::class.java)
		Assertions.assertEquals(0, debugAppenders.size)
		val dummyAppenders = findOfType(DummyAppender::class.java)
		Assertions.assertEquals(2, dummyAppenders.size)
		Assertions.assertEquals(appender1, dummyAppenders[0])
		Assertions.assertEquals(appender2, dummyAppenders[1])
	}

	@Test
	fun `test filterInstances`() {
		val appender1 = DummyAppender(Log.INFO)
		val appender2 = DummyAppender(Log.INFO)
		removeAllAppenders()
		add(appender1)
		add(appender2)
		val debugAppenders = filterInstances<DebugAppender>()
		Assertions.assertEquals(0, debugAppenders.size)
		val dummyAppenders: List<DummyAppender> = filterInstances()
		Assertions.assertEquals(2, dummyAppenders.size)
		Assertions.assertEquals(appender1, dummyAppenders[0])
		Assertions.assertEquals(appender2, dummyAppenders[1])
	}

	@Test
	fun testFindLevelName() {
		Assertions.assertEquals("TRACE", findLevelName(Log.VERBOSE))
		Assertions.assertEquals("DEBUG", findLevelName(Log.DEBUG))
		Assertions.assertEquals("INFO", findLevelName(Log.INFO))
		Assertions.assertEquals("WARN", findLevelName(Log.WARN))
		Assertions.assertEquals("ERROR", findLevelName(Log.ERROR))
		Assertions.assertEquals("WTF", findLevelName(Log.ASSERT))
		Assertions.assertEquals("", findLevelName(42))
	}


}