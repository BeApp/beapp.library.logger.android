package fr.beapp.logger;


import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestUtils {

    public static void assertMatches(String expectedRegexp, String actual) {
        Pattern pattern = Pattern.compile(expectedRegexp);
        Matcher matcher = pattern.matcher(actual);
        assertTrue(matcher.find(), String.format(Locale.ENGLISH, "Actual value <%s> doesn't matches expected regexp <%s>", actual, expectedRegexp));
    }

}
