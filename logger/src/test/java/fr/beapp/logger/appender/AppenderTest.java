package fr.beapp.logger.appender;

import android.util.Log;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.junit.jupiter.api.Test;

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
        assertFalse(new DummyAppender(Log.VERBOSE).isLoggable(-10));
        assertFalse(new DummyAppender(Log.VERBOSE).isLoggable(0));

        assertTrue(new DummyAppender(Log.VERBOSE).isLoggable(Log.VERBOSE));
        assertTrue(new DummyAppender(Log.VERBOSE).isLoggable(Log.DEBUG));
        assertTrue(new DummyAppender(Log.VERBOSE).isLoggable(Log.ERROR));
    }

    @Test
    public void isLoggable_error() {
        assertFalse(new DummyAppender(Log.ERROR).isLoggable(-10));
        assertFalse(new DummyAppender(Log.ERROR).isLoggable(0));
        assertFalse(new DummyAppender(Log.ERROR).isLoggable(Log.VERBOSE));
        assertFalse(new DummyAppender(Log.ERROR).isLoggable(Log.DEBUG));

        assertTrue(new DummyAppender(Log.ERROR).isLoggable(Log.ERROR));
    }
}