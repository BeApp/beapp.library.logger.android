package fr.beapp.logger.formatter;

import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.beapp.logger.TestUtils;

public class DefaultFormatterTest {

	private final DefaultFormatter formatter = new DefaultFormatter(DefaultFormatter.class.getCanonicalName());

	@Test
	public void format() throws Exception {
		Assert.assertNull(formatter.format(null, null));
		TestUtils.assertMatches("^f\\.b\\.l\\.f\\.DefaultFormatterTest:[0-9]+ - java\\.lang\\.RuntimeException.*", formatter.format(new RuntimeException(), null));
		TestUtils.assertMatches("^f\\.b\\.l\\.f\\.DefaultFormatterTest:[0-9]+ - test 1$", formatter.format(null, "test %d", 1));
		TestUtils.assertMatches("^f\\.b\\.l\\.f\\.DefaultFormatterTest:[0-9]+ - test 1\njava\\.lang\\.RuntimeException.*", formatter.format(new RuntimeException(), "test %d", 1));
	}

	@Test
	public void retrieveCaller() throws Exception {
		Assert.assertNull(new DefaultFormatter("unknown_sentinel").retrieveCaller());

		TestUtils.assertMatches("^f\\.b\\.l\\.f\\.DefaultFormatterTest:[0-9]+$", formatter.retrieveCaller());
	}

	@Test
	public void shortenCanonicalName() throws Exception {
		Assert.assertEquals("", formatter.shortenCanonicalName(""));
		Assert.assertEquals("j.l.String", formatter.shortenCanonicalName(String.class.getCanonicalName()));
		Assert.assertEquals("f.b.l.f.DefaultFormatterTest", formatter.shortenCanonicalName(DefaultFormatterTest.class.getCanonicalName()));
	}

}