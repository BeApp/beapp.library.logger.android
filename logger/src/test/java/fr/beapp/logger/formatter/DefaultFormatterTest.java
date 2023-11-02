package fr.beapp.logger.formatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import fr.beapp.logger.TestUtils;

public class DefaultFormatterTest {

    private final DefaultFormatter formatter = new DefaultFormatter(DefaultFormatter.class.getCanonicalName());

    @Test
    public void format() {
        assertNull(formatter.format(null, null));
        TestUtils.assertMatches("^f\\.b\\.l\\.f\\.DefaultFormatterTest:[0-9]+ - java\\.lang\\.RuntimeException.*", formatter.format(new RuntimeException(), null));
        TestUtils.assertMatches("^f\\.b\\.l\\.f\\.DefaultFormatterTest:[0-9]+ - test 1$", formatter.format(null, "test %d", 1));
        TestUtils.assertMatches("^f\\.b\\.l\\.f\\.DefaultFormatterTest:[0-9]+ - test 1\njava\\.lang\\.RuntimeException.*", formatter.format(new RuntimeException(), "test %d", 1));
    }

    @Test
    public void retrieveCaller() {
        assertNull(new DefaultFormatter("unknown_sentinel").retrieveCaller());

        TestUtils.assertMatches("^f\\.b\\.l\\.f\\.DefaultFormatterTest:[0-9]+$", formatter.retrieveCaller());
    }

    @Test
    public void shortenCanonicalName() {
        assertEquals("", formatter.shortenCanonicalName(""));
        assertEquals("j.l.String", formatter.shortenCanonicalName(String.class.getCanonicalName()));
        assertEquals("f.b.l.f.DefaultFormatterTest", formatter.shortenCanonicalName(DefaultFormatterTest.class.getCanonicalName()));
    }

}