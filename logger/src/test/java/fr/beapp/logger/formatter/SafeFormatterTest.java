package fr.beapp.logger.formatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import android.support.annotation.Nullable;

import org.junit.jupiter.api.Test;

public class SafeFormatterTest {

    private static class SimpleFormatter implements Formatter {
        @Override
        public String format(@Nullable Throwable tr, @Nullable String message, Object... args) {
            return "SimpleFormatter: " + message;
        }
    }

    @Test
    public void cleanupMessage() {
        Formatter formatter = new SafeFormatter(new SimpleFormatter());

        assertEquals("SimpleFormatter: Logging user john with pass [HIDDEN]", formatter.format(null, "Logging user %s with pass %s", "john", "s3cr3t"));
        assertEquals("SimpleFormatter: Logging user=john with pass=[HIDDEN]", formatter.format(null, "Logging user=%s with pass=%s", "john", "s3cr3t"));
        assertEquals("SimpleFormatter: {\"login\":\"john\",\"password\":\"[HIDDEN]\"}", formatter.format(null, "{\"login\":\"john\",\"password\":\"s3cre3t\"}"));
        assertEquals("SimpleFormatter: {\"login\": \"john\", \"password\": \"[HIDDEN]\"}", formatter.format(null, "{\"login\": \"john\", \"password\": \"s3cre3t\"}"));
    }

    @Test()
    public void patternFor_nullKey() {
        assertThrows(IllegalArgumentException.class, () -> SafeFormatter.patternFor(null));
    }

    @Test
    public void patternFor() {
        assertEquals("([\"']?(?:pass)[\"']?\\s*[=:]?\\s*[\"']?)([^\"']+)", SafeFormatter.patternFor("pass").pattern());
        assertEquals("([\"']?(?:pass|password)[\"']?\\s*[=:]?\\s*[\"']?)([^\"']+)", SafeFormatter.patternFor("pass", "password").pattern());
        assertEquals("([\"']?(?:pass|password|key)[\"']?\\s*[=:]?\\s*[\"']?)([^\"']+)", SafeFormatter.patternFor("pass", "password", "key").pattern());
    }
}