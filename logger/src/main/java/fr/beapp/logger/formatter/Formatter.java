package fr.beapp.logger.formatter;

import android.support.annotation.Nullable;

public interface Formatter {

	@Nullable
	String format(@Nullable Throwable tr, @Nullable String message, Object... args);

}
