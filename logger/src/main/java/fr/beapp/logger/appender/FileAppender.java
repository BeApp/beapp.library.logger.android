package fr.beapp.logger.appender;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fr.beapp.logger.Logger;

public class FileAppender extends Appender {

	private static final SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("HH:mm:ssZZ", Locale.ENGLISH);

	private PrintStream printStream;

	/**
	 * Possible placeholder for filenamePattern :
	 * <ul>
	 * <li><code>{date}</code> : replaced by current date</li>
	 * </ul>
	 */
	public FileAppender(String filenamePattern) {
		File outputFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		if (outputFolder != null && (outputFolder.exists() || outputFolder.mkdirs())) {
			File outputFile = new File(outputFolder, buildFilename(filenamePattern));
			try {
				FileOutputStream fileOutputStream = new FileOutputStream(outputFile, true);
				printStream = new PrintStream(fileOutputStream);
			} catch (FileNotFoundException e) {
				e.printStackTrace();    // Can't use Logger.error here
			}
		}
	}

	@Override
	public void log(@Logger.LogLevel int priority, @NonNull String message, @Nullable Throwable t) {
		if (printStream != null) {
			try {
				printStream.print(buildLogline(priority, message));
				if (t != null) {
					t.printStackTrace(printStream);
				}
				printStream.print('\n');
				printStream.flush();
			} catch (Exception ignored) {
			}
		}
	}

	private String buildFilename(String filenamePattern) {
		String date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
		filenamePattern = filenamePattern.replace("{date}", date);
		return filenamePattern;
	}

	private String buildLogline(int priority, String message) {
		return String.format("%s %s[%s]: %s", DATE_TIME_FORMATTER.format(new Date()), Logger.findLevelName(priority), Thread.currentThread().getName(), message);
	}

}