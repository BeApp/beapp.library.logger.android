package fr.beapp.logger;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import fr.beapp.logger.appender.Appender;
import fr.beapp.logger.appender.DebugAppender;
import fr.beapp.logger.formatter.Formatter;

public class LoggerTest {

	private static class DummyAppender extends Appender {
		public final List<String> acc = new LinkedList<>();

		private DummyAppender(@Logger.LogLevel int level) {
			super(level);
		}

		@Override
		public void log(@Logger.LogLevel int priority, @NonNull String message, @Nullable Throwable tr) {
			acc.add(message);
		}
	}

	private Formatter formatter = new Formatter() {
		@Override
		public String format(@Nullable Throwable tr, @Nullable String message, Object... args) {
			return "formatted: " + message;
		}
	};

	@Test
	public void testLog() {
		DummyAppender appender1 = new DummyAppender(Log.DEBUG);
		DummyAppender appender2 = new DummyAppender(Log.INFO);
		DummyAppender appender3 = new DummyAppender(Log.ASSERT);

		Logger.removeAllAppenders();
		Logger.add(appender1);
		Logger.add(appender2);
		Logger.add(appender3);
		Logger.formatter(formatter);

		Logger.trace("trace");
		Logger.debug("debug");
		Logger.info("info");
		Logger.warn("warn");
		Logger.error("error");

		Assert.assertArrayEquals(new String[]{"formatted: debug", "formatted: info", "formatted: warn", "formatted: error"}, appender1.acc.toArray());
		Assert.assertArrayEquals(new String[]{"formatted: info", "formatted: warn", "formatted: error"}, appender2.acc.toArray());
		Assert.assertArrayEquals(new String[]{}, appender3.acc.toArray());
	}

	@Test
	public void testFindOfType() {
		DummyAppender appender1 = new DummyAppender(Log.INFO);
		DummyAppender appender2 = new DummyAppender(Log.INFO);

		Logger.removeAllAppenders();
		Logger.add(appender1);
		Logger.add(appender2);

		List<DebugAppender> debugAppenders = Logger.findOfType(DebugAppender.class);
		Assert.assertEquals(0, debugAppenders.size());

		List<DummyAppender> dummyAppenders = Logger.findOfType(DummyAppender.class);
		Assert.assertEquals(2, dummyAppenders.size());
		Assert.assertEquals(appender1, dummyAppenders.get(0));
		Assert.assertEquals(appender2, dummyAppenders.get(1));
	}

}