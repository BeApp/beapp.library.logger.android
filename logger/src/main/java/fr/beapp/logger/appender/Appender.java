package fr.beapp.logger.appender;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import fr.beapp.logger.Logger;

public abstract class Appender {

	public abstract void log(@Logger.LogLevel int priority, @NonNull String message, @Nullable Throwable t);

}
