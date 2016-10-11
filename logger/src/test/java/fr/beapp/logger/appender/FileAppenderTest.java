package fr.beapp.logger.appender;

import android.util.Log;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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
			protected String buildLogline(int priority, String message) {
				return message;
			}
		};
	}

	@After
	public void teardown() {
		simpleFileAppender.close();
	}

	@Test
	public void log() throws Exception {
		simpleFileAppender.log(Log.DEBUG, "test", null);
		simpleFileAppender.log(Log.DEBUG, "foo", null);
		simpleFileAppender.log(Log.DEBUG, "bar", null);
		Assert.assertEquals("test\nfoo\nbar\n", out.toString());
	}

	@Test
	public void log_withException() throws Exception {
		simpleFileAppender.log(Log.DEBUG, formatter.format(new RuntimeException(), "test"), new RuntimeException());
		TestUtils.assertMatches("^test\njava.lang.RuntimeException\n.*", out.toString());
	}

}