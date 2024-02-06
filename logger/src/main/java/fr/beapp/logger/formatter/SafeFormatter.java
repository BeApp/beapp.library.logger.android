package fr.beapp.logger.formatter;

import androidx.annotation.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Decorator formatter intended to filter out sensitive information as passwords.
 */
public class SafeFormatter implements Formatter {

    private Pattern sensitivePattern;
    private Formatter delegate;

    public SafeFormatter(Formatter delegate) {
        this(delegate, "password", "pass");
    }

    public SafeFormatter(Formatter delegate, String key, String... otherKeys) {
        this(delegate, patternFor(key, otherKeys));
    }

    public SafeFormatter(Formatter delegate, Pattern sensitivePattern) {
        this.delegate = delegate;
        this.sensitivePattern = sensitivePattern;
    }

    @Nullable
    @Override
    public String format(@Nullable Throwable tr, @Nullable String message, Object... args) {
        if (message != null) {
            // Do not pass args to delegate as they were already consumed
            return delegate.format(tr, cleanupMessage(message, args));
        }

        // Still call delegate as it may do some specific work on this case
        return delegate.format(tr, null, args);
    }

    protected String cleanupMessage(String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }

        Matcher matcher = sensitivePattern.matcher(message);
        return matcher.replaceAll("$1[HIDDEN]");
    }

    protected static Pattern patternFor(String key, String... otherKeys) {
        if (key == null) {
            throw new IllegalArgumentException("Key can't be null");
        }

        StringBuilder builder = new StringBuilder(key);
        for (String otherKey : otherKeys) {
            builder.append('|').append(otherKey);
        }

        return Pattern.compile("([\"']?(?:" + builder + ")[\"']?\\s*[=:]?\\s*[\"']?)([^\"']+)");
    }
}
