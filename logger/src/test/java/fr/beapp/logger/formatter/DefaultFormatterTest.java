package fr.beapp.logger.formatter;

import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultFormatterTest {

	private final DefaultFormatter formatter = new DefaultFormatter(DefaultFormatter.class.getCanonicalName());

	@Test
	public void format() throws Exception {
		Assert.assertNull(formatter.format(null, null));
		assertMatches("^f\\.b\\.l\\.f\\.DefaultFormatterTest:[0-9]+ - java\\.lang\\.RuntimeException.*", formatter.format(new RuntimeException(), null));
		assertMatches("^f\\.b\\.l\\.f\\.DefaultFormatterTest:[0-9]+ - test 1$", formatter.format(null, "test %d", 1));
		assertMatches("^f\\.b\\.l\\.f\\.DefaultFormatterTest:[0-9]+ - test 1\njava\\.lang\\.RuntimeException.*", formatter.format(new RuntimeException(), "test %d", 1));
	}

	@Test
	public void retrieveCaller() throws Exception {
		Assert.assertNull(new DefaultFormatter("unknown_sentinel").retrieveCaller());

		assertMatches("^f\\.b\\.l\\.f\\.DefaultFormatterTest:[0-9]+$", formatter.retrieveCaller());
	}

	@Test
	public void shortenCanonicalName() throws Exception {
		Assert.assertEquals("", formatter.shortenCanonicalName(""));
		Assert.assertEquals("j.l.String", formatter.shortenCanonicalName(String.class.getCanonicalName()));
		Assert.assertEquals("f.b.l.f.DefaultFormatterTest", formatter.shortenCanonicalName(DefaultFormatterTest.class.getCanonicalName()));
	}

	private void assertMatches(String expectedRegexp, String actual) {
		Pattern pattern = Pattern.compile(expectedRegexp);
		Matcher matcher = pattern.matcher(actual);
		Assert.assertTrue(String.format(Locale.ENGLISH, "Actual value <%s> doesn't matches expected regexp <%s>", actual, expectedRegexp), matcher.find());
	}

}