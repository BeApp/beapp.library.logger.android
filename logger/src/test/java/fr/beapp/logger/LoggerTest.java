package fr.beapp.logger;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import fr.beapp.logger.appender.Appender;

public class LoggerTest {

	private static class DummyAppender extends Appender {
		private final Integer key;
		private final Map<Integer, Boolean> acc;

		private DummyAppender(Integer key, Map<Integer, Boolean> acc) {
			this.key = key;
			this.acc = acc;
		}

		@Override
		public void log(@Logger.LogLevel int priority, @NonNull String message, @Nullable Throwable tr) {
			acc.put(key, true);
		}
	}

	@Test
	@SuppressLint("UseSparseArrays")
	public void log() throws Exception {
		Map<Integer, Boolean> acc = new HashMap<>();

		DummyAppender appender1 = new DummyAppender(1, acc);
		DummyAppender appender5 = new DummyAppender(5, acc);

		Logger.add(appender1);
		Logger.add(appender5);

		Logger.log(Log.DEBUG, null, "test message");

		Assert.assertEquals(true, acc.get(1));
		Assert.assertEquals(true, acc.get(5));
	}

}