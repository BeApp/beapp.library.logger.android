package fr.beapp.logger;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import fr.beapp.logger.appender.Appender;
import fr.beapp.logger.appender.DebugAppender;
import fr.beapp.logger.formatter.Formatter;

public class LoggerJTest {

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

    private final Formatter formatter = (tr, message, args) -> "formatted: " + message;

    @Test
    public void testLog() {
        DummyAppender appender1 = new DummyAppender(Log.DEBUG);
        DummyAppender appender2 = new DummyAppender(Log.INFO);
        DummyAppender appender3 = new DummyAppender(Log.ASSERT);

        Logger.removeAllAppenders();
        Logger.add(appender1);
        Logger.add(appender2);
        Logger.add(appender3);
        Logger.setFormatter(formatter);

        Logger.trace("trace");
        Logger.debug("debug");
        Logger.info("info");
        Logger.warn("warn");
        Logger.warn();
        Logger.error();
        Logger.error("error");

        assertArrayEquals(new String[]{"formatted: debug", "formatted: info", "formatted: warn", "formatted: null", "formatted: null", "formatted: error"}, appender1.acc.toArray());
        assertArrayEquals(new String[]{"formatted: info", "formatted: warn", "formatted: null", "formatted: null", "formatted: error"}, appender2.acc.toArray());
        assertArrayEquals(new String[]{}, appender3.acc.toArray());
    }

    @Test
    public void testFindOfType() {
        DummyAppender appender1 = new DummyAppender(Log.INFO);
        DummyAppender appender2 = new DummyAppender(Log.INFO);

        Logger.removeAllAppenders();
        Logger.add(appender1);
        Logger.add(appender2);

        List<DebugAppender> debugAppenders = Logger.findOfType(DebugAppender.class);
        assertEquals(0, debugAppenders.size());

        List<DummyAppender> dummyAppenders = Logger.findOfType(DummyAppender.class);
        assertEquals(2, dummyAppenders.size());
        assertEquals(appender1, dummyAppenders.get(0));
        assertEquals(appender2, dummyAppenders.get(1));
    }

    @Test
    public void testFindLevelName() {
        assertEquals("TRACE", Logger.findLevelName(Log.VERBOSE));
        assertEquals("DEBUG", Logger.findLevelName(Log.DEBUG));
        assertEquals("INFO", Logger.findLevelName(Log.INFO));
        assertEquals("WARN", Logger.findLevelName(Log.WARN));
        assertEquals("ERROR", Logger.findLevelName(Log.ERROR));
        assertEquals("WTF", Logger.findLevelName(Log.ASSERT));
        assertEquals("", Logger.findLevelName(42));
    }

}