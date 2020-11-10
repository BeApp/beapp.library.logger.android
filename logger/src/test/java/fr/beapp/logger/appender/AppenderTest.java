package fr.beapp.logger.appender;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.junit.Assert;
import org.junit.Test;

public class AppenderTest {

	private static class DummyAppender extends Appender {
		public DummyAppender(int level) {
			super(level);
		}

		@Override
		public void log(int priority, @NonNull String message, @Nullable Throwable tr) {
		}
	}

	@Test
	public void isLoggable_verbose() {
		Assert.assertFalse(new DummyAppender(Log.VERBOSE).isLoggable(-10));
		Assert.assertFalse(new DummyAppender(Log.VERBOSE).isLoggable(0));

		Assert.assertTrue(new DummyAppender(Log.VERBOSE).isLoggable(Log.VERBOSE));
		Assert.assertTrue(new DummyAppender(Log.VERBOSE).isLoggable(Log.DEBUG));
		Assert.assertTrue(new DummyAppender(Log.VERBOSE).isLoggable(Log.ERROR));
	}
	@Test
	public void isLoggable_error() {
		Assert.assertFalse(new DummyAppender(Log.ERROR).isLoggable(-10));
		Assert.assertFalse(new DummyAppender(Log.ERROR).isLoggable(0));
		Assert.assertFalse(new DummyAppender(Log.ERROR).isLoggable(Log.VERBOSE));
		Assert.assertFalse(new DummyAppender(Log.ERROR).isLoggable(Log.DEBUG));

		Assert.assertTrue(new DummyAppender(Log.ERROR).isLoggable(Log.ERROR));
	}
}