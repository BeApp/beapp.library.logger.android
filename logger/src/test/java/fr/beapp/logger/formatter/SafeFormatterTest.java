package fr.beapp.logger.formatter;

import android.support.annotation.Nullable;

import org.junit.Assert;
import org.junit.Test;

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

		Assert.assertEquals("SimpleFormatter: Logging user john with pass [HIDDEN]", formatter.format(null, "Logging user %s with pass %s", "john", "s3cr3t"));
		Assert.assertEquals("SimpleFormatter: Logging user=john with pass=[HIDDEN]", formatter.format(null, "Logging user=%s with pass=%s", "john", "s3cr3t"));
		Assert.assertEquals("SimpleFormatter: {\"login\":\"john\",\"password\":\"[HIDDEN]\"}", formatter.format(null, "{\"login\":\"john\",\"password\":\"s3cre3t\"}"));
		Assert.assertEquals("SimpleFormatter: {\"login\": \"john\", \"password\": \"[HIDDEN]\"}", formatter.format(null, "{\"login\": \"john\", \"password\": \"s3cre3t\"}"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void patternFor_nullKey() {
		SafeFormatter.patternFor(null);
	}

	@Test
	public void patternFor() {
		Assert.assertEquals("([\"']?(?:pass)[\"']?\\s*[=:]?\\s*[\"']?)([^\"']+)", SafeFormatter.patternFor("pass").pattern());
		Assert.assertEquals("([\"']?(?:pass|password)[\"']?\\s*[=:]?\\s*[\"']?)([^\"']+)", SafeFormatter.patternFor("pass", "password").pattern());
		Assert.assertEquals("([\"']?(?:pass|password|key)[\"']?\\s*[=:]?\\s*[\"']?)([^\"']+)", SafeFormatter.patternFor("pass", "password", "key").pattern());
	}
}