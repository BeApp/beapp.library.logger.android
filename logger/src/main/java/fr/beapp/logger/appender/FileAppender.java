package fr.beapp.logger.appender;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fr.beapp.logger.Logger;

/**
 * Write log messages on Android's filesystem.
 * this class handle file rotation based on current day.
 */
public class FileAppender extends Appender {

	private final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("HH:mm:ssZZ", Locale.ENGLISH);
	private final PrintStream printStream;
	private final File outputFile;

	/**
	 * Possible placeholder for filenamePattern :
	 * <ul>
	 * <li><code>{date}</code> : replaced by current date</li>
	 * </ul>
	 */
	public FileAppender(@NonNull File logsDirectory, @NonNull String filenamePattern) throws FileNotFoundException {
		this(Log.INFO, logsDirectory, filenamePattern);
	}

	/**
	 * Possible placeholder for filenamePattern :
	 * <ul>
	 * <li><code>{date}</code> : replaced by current date</li>
	 * </ul>
	 */
	public FileAppender(@Logger.LogLevel int level, @NonNull File logsDirectory, @NonNull String filenamePattern) throws FileNotFoundException {
		super(level);
		outputFile = ensureOutputFile(logsDirectory, buildFilename(filenamePattern));
		printStream = ensurePrintStream(outputFile);
	}

	/**
	 * Close the stream opened to write the file
	 */
	public void close() {
		printStream.close();
	}

	@Override
	public void log(@Logger.LogLevel int priority, @NonNull String message, @Nullable Throwable tr) {
		try {
			printStream.print(buildLogline(priority, message));
			printStream.print('\n');
			printStream.flush();
		} catch (Exception ignored) {
			// Can't use Logger.error here
		}
	}

	@NonNull
	public File getOutputFile() {
		return outputFile;
	}

	@NonNull
	protected PrintStream ensurePrintStream(@NonNull File outputFile) throws FileNotFoundException {
		FileOutputStream fileOutputStream = new FileOutputStream(outputFile, false);
		return new PrintStream(fileOutputStream, true);
	}

	@NonNull
	protected File ensureOutputFile(@NonNull File logsDirectory, @NonNull String filename) throws FileNotFoundException {
		File logFile = new File(logsDirectory, filename);
		File logFolder = logFile.getParentFile();

		if (logFolder.exists() || logFolder.mkdirs()) {
			return logFile;
		}
		throw new FileNotFoundException("Couldn't ensure log file '" + logFile + "'");
	}

	@NonNull
	protected String buildFilename(@NonNull String filenamePattern) {
		Date now = new Date();
		String date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(now);
		String time = new SimpleDateFormat("HH-mm-ss", Locale.ENGLISH).format(now);

		String filename = filenamePattern;
		filename = filename.replace("{date}", date);
		filename = filename.replace("{time}", time);
		return filename;
	}

	@NonNull
	protected String buildLogline(@Logger.LogLevel int priority, @NonNull String message) {
		return String.format("%s %s[%s]: %s", dateTimeFormatter.format(new Date()), Logger.findLevelName(priority), Thread.currentThread().getName(), message);
	}

}