package fr.beapp.logger.appender

import android.util.Log
import fr.beapp.logger.Logger
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.PrintStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Write log messages on Android's filesystem.
 * this class handle file rotation based on current day.
 */
public open class FileAppender
/**
 * Possible placeholder for filenamePattern :
 * <ul>
 * <li><code>{date}</code> : replaced by current date</li>
 * </ul>
 */
@Throws(FileNotFoundException::class)
@JvmOverloads
constructor(
	@Logger.LogLevel logLevel: Int = Log.INFO,
	logsDirectory: File,
	filenamePattern: String
) : Appender(logLevel) {

	private val dateTimeFormatter: SimpleDateFormat = SimpleDateFormat("HH:mm:ssZZ", Locale.ENGLISH)
	private val outputFile: File = ensureOutputFile(logsDirectory, buildFilename(filenamePattern))
	private val printStream: PrintStream = ensurePrintStream(outputFile)

	/**
	 * Close the stream opened to write the file
	 */
	public fun close() {
		printStream.close()
	}

	override fun log(priority: Int, message: String, tr: Throwable?) {
		try {
			printStream.print(buildLogline(priority, message))
			printStream.print('\n')
			printStream.flush()
		} catch (ignored: Exception) {
			// Can't use Logger.error here
		}
	}

	public fun getOutputFile(): File = outputFile

	@Throws(FileNotFoundException::class)
	protected open fun ensurePrintStream(outputFile: File): PrintStream {
		val fileOutputStream = FileOutputStream(outputFile, false)
		return PrintStream(fileOutputStream, true)
	}

	@Throws(FileNotFoundException::class)
	protected open fun ensureOutputFile(logsDirectory: File, filename: String): File {
		val logFile = File(logsDirectory, filename)
		val logFolder = logFile.parentFile ?: throw FileNotFoundException("Couldn't create log file '$filename'")

		if (logFolder.exists() || logFolder.mkdirs()) {
			return logFile
		}
		throw FileNotFoundException("Couldn't ensure log file '$logFile'")
	}

	protected open fun buildFilename(filenamePattern: String): String {
		val now = Date()
		val date = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(now)
		val time = SimpleDateFormat("HH-mm-ss", Locale.ENGLISH).format(now)

		return filenamePattern
			.replace("{date}", date)
			.replace("{time}", time)
	}

	protected open fun buildLogline(@Logger.LogLevel priority: Int, message: String): String {
		return String.format(
			"%s %s[%s]: %s", dateTimeFormatter.format(Date()),
			Logger.findLevelName(priority),
			Thread.currentThread().name,
			message
		)
	}

}