package fr.beapp.logger.appender;

import android.support.annotation.NonNull;
import android.util.Log;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
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
	private Formatter formatter = new DefaultFormatter();

	@Before
	public void setup() {
		out = new ByteArrayOutputStream();
		simpleFileAppender = new FileAppender(new PrintStream(out)) {
			@Override
			protected String buildLogline(@Logger.LogLevel int priority, @NonNull String message) {
				return message;
			}
		};
	}

	@After
	public void teardown() {
		simpleFileAppender.close();
	}

	@Test
	public void testLog() throws Exception {
		simpleFileAppender.log(Log.DEBUG, "test", null);
		simpleFileAppender.log(Log.DEBUG, "foo", null);
		simpleFileAppender.log(Log.DEBUG, "bar", null);
		Assert.assertEquals("test\nfoo\nbar\n", out.toString());
	}

	@Test
	public void testLog_withException() throws Exception {
		simpleFileAppender.log(Log.DEBUG, formatter.format(new RuntimeException(), "test"), new RuntimeException());
		TestUtils.assertMatches("^test\njava.lang.RuntimeException\n.*", out.toString());
	}

	@Test
	public void testBuildFilename() throws Exception {
		Assert.assertEquals("", simpleFileAppender.buildFilename(""));
		Assert.assertEquals("test", simpleFileAppender.buildFilename("test"));
		Assert.assertEquals("test.log", simpleFileAppender.buildFilename("test.log"));

		String date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
		Assert.assertEquals("test-" + date + ".log", simpleFileAppender.buildFilename("test-{date}.log"));
	}

}