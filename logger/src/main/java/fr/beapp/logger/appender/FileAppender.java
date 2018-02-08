package fr.beapp.logger.appender;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

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
	private PrintStream printStream;
	private File outputFile;

	/**
	 * Possible placeholder for filenamePattern :
	 * <ul>
	 * <li><code>{date}</code> : replaced by current date</li>
	 * </ul>
	 */
	public FileAppender(@NonNull String filenamePattern) {
		try {
			outputFile = ensureOutputFile(filenamePattern);
			FileOutputStream fileOutputStream = new FileOutputStream(outputFile, false);
			printStream = new PrintStream(fileOutputStream);
		} catch (FileNotFoundException e) {
			// Can't use Logger.error here
			if (Log.isLoggable(FileAppender.class.getSimpleName(), Log.ERROR)) {
				String fullPath = outputFile != null ? outputFile.getAbsolutePath() : null;
				Log.e(FileAppender.class.getSimpleName(), "Couldn't write log in file " + fullPath, e);
			}
		}
		// Do not close the stream on finally block
	}

	public FileAppender(@NonNull PrintStream printStream) {
		this.printStream = printStream;
	}

	/**
	 * Close the stream opened to write the file
	 */
	public void close() {
		if (printStream != null) {
			printStream.close();
			printStream = null;
		}
	}

	@Override
	public void log(@Logger.LogLevel int priority, @NonNull String message, @Nullable Throwable tr) {
		if (printStream != null) {
			try {
				printStream.print(buildLogline(priority, message));
				printStream.print('\n');
				printStream.flush();
			} catch (Exception ignored) {
				// Can't use Logger.error here
			}
		}
	}

	public File getOutputFile() {
		return outputFile;
	}

	@NonNull
	protected File ensureOutputFile(@NonNull String filenamePattern) throws FileNotFoundException {
		File outputFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		if (outputFolder != null && (outputFolder.exists() || outputFolder.mkdirs())) {
			return new File(outputFolder, buildFilename(filenamePattern));
		}
		throw new FileNotFoundException("Couldn't ensure file with pattern '" + filenamePattern + "' at path '" + outputFolder + "'");
	}

	protected String buildFilename(@NonNull String filenamePattern) {
		String date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
		return filenamePattern.replace("{date}", date);
	}

	protected String buildLogline(@Logger.LogLevel int priority, @NonNull String message) {
		return String.format("%s %s[%s]: %s", dateTimeFormatter.format(new Date()), Logger.findLevelName(priority), Thread.currentThread().getName(), message);
	}

}