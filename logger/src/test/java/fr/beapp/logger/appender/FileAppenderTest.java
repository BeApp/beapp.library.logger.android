package fr.beapp.logger.appender;

import android.util.Log;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import android.util.Log;

import androidx.annotation.NonNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fr.beapp.logger.Logger;
import fr.beapp.logger.TestUtils;
import fr.beapp.logger.formatter.DefaultFormatter;
import fr.beapp.logger.formatter.Formatter;

public class FileAppenderTest {

    private FileAppender simpleFileAppender;
    private ByteArrayOutputStream out;
    private final Formatter formatter = new DefaultFormatter();

    @BeforeEach
    public void setup() throws Exception {
        out = new ByteArrayOutputStream();

        simpleFileAppender = new FileAppender(new File(""), "") {
            @NonNull
            @Override
            protected File ensureOutputFile(@NonNull File logsDirectory, @NonNull String filenamePattern) {
                return new File("");
            }

            @NonNull
            @Override
            protected PrintStream ensurePrintStream(@NonNull File outputFile) {
                return new PrintStream(out);
            }

            @NonNull
            @Override
            protected String buildLogline(@Logger.LogLevel int priority, @NonNull String message) {
                return message;
            }
        };
    }

    @AfterEach
    public void teardown() {
        simpleFileAppender.close();
    }

    @Test
    public void testLog() {
        simpleFileAppender.log(Log.DEBUG, "test", null);
        simpleFileAppender.log(Log.DEBUG, "foo", null);
        simpleFileAppender.log(Log.DEBUG, "bar", null);
        assertEquals("test\nfoo\nbar\n", out.toString());
    }

    @Test
    public void testLog_withException() {
        String format = formatter.format(new RuntimeException(), "test");
        assertNotNull(format);
        simpleFileAppender.log(Log.DEBUG, format, new RuntimeException());
        TestUtils.assertMatches("^test\njava.lang.RuntimeException\n.*", out.toString());
    }

    @Test
    public void testBuildFilename() {
        assertEquals("", simpleFileAppender.buildFilename(""));
        assertEquals("test", simpleFileAppender.buildFilename("test"));
        assertEquals("test.log", simpleFileAppender.buildFilename("test.log"));

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
        assertEquals("test-" + date + ".log", simpleFileAppender.buildFilename("test-{date}.log"));
    }

}