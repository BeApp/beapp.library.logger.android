package fr.beapp.logger.fomatter;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface Formatter {

	@Nullable
	String format(@Nullable Throwable tr, @Nullable String message, Object... args);

}
