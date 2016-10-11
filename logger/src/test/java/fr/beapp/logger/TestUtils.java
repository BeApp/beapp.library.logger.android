package fr.beapp.logger;


import org.junit.Assert;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestUtils {

	public static void assertMatches(String expectedRegexp, String actual) {
		Pattern pattern = Pattern.compile(expectedRegexp);
		Matcher matcher = pattern.matcher(actual);
		Assert.assertTrue(String.format(Locale.ENGLISH, "Actual value <%s> doesn't matches expected regexp <%s>", actual, expectedRegexp), matcher.find());
	}

}
