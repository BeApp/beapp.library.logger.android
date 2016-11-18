package fr.beapp.logger;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.beapp.logger.appender.Appender;
import fr.beapp.logger.formatter.Formatter;

public class LoggerTest {

	private static class DummyAppender extends Appender {
		private final Integer key;
		private final Map<Integer, List<String>> acc;

		private DummyAppender(Integer key, Map<Integer, List<String>> acc) {
			this.key = key;
			this.acc = acc;
		}

		@Override
		public void log(@Logger.LogLevel int priority, @NonNull String message, @Nullable Throwable tr) {
			List<String> strings = acc.get(key);
			if (strings == null) {
				strings = new ArrayList<>();
				acc.put(key, strings);
			}
			strings.add(message);
		}
	}

	@Test
	@SuppressLint("UseSparseArrays")
	public void log() throws Exception {
		Map<Integer, List<String>> acc = new HashMap<>();

		DummyAppender appender1 = new DummyAppender(1, acc);
		DummyAppender appender5 = new DummyAppender(5, acc);

		Logger.add(appender1);
		Logger.add(appender5);
		Logger.formatter(new Formatter() {
			@Nullable
			@Override
			public String format(@Nullable Throwable tr, @Nullable String message, Object... args) {
				return "formatted:" + message;
			}
		});

		Logger.log(Log.DEBUG, null, "test message");

		Assert.assertArrayEquals(new String[]{"formatted:test message"}, acc.get(1).toArray());
		Assert.assertArrayEquals(new String[]{"formatted:test message"}, acc.get(5).toArray());
	}

}