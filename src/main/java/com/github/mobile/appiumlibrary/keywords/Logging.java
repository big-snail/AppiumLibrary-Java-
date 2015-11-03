package com.github.mobile.appiumlibrary.keywords;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import org.robotframework.javalib.annotation.RobotKeywords;

import com.github.mobile.appiumlibrary.AppiumLibraryNonFatalException;
import com.github.mobile.appiumlibrary.RunOnFailureKeywordsAdapter;


@RobotKeywords
public class Logging extends RunOnFailureKeywordsAdapter {

	protected final static Map<String, String[]> VALID_LOG_LEVELS;
	protected static String logDir = null;

	static {
		VALID_LOG_LEVELS = new HashMap<String, String[]>();
		VALID_LOG_LEVELS.put("debug", new String[] { "debug", "" });
		VALID_LOG_LEVELS.put("html", new String[] { "info", ", True, False" });
		VALID_LOG_LEVELS.put("info", new String[] { "info", "" });
		VALID_LOG_LEVELS.put("trace", new String[] { "trace", "" });
		VALID_LOG_LEVELS.put("warn", new String[] { "warn", "" });
	}
	
	// ##############################
	// Internal Methods
	// ##############################

	protected void trace(String msg) {
		log(msg, "trace");
	}

	protected void debug(String msg) {
		log(msg, "debug");
	}

	protected void info(String msg) {
		log(msg, "info");
	}

	protected void html(String msg) {
		log(msg, "html");
	}

	protected void warn(String msg) {
		log(msg, "warn");
	}

	protected void log(String msg, String logLevel) {
		String[] methodParameters = VALID_LOG_LEVELS.get(logLevel.toLowerCase());
		if (methodParameters != null) {
			log0(msg, methodParameters[0], methodParameters[1]);
		} else {
			throw new AppiumLibraryNonFatalException(String.format("Given log level %s is invalid.", logLevel));
		}
	}

	protected void log0(String msg, String methodName, String methodArguments) {
		if (msg.length() > 1024) {
			// Message is too large.
			// There is a hard limit of 100k in the Jython source code parser
			try {
				// Write message to temp file
				File tempFile = File.createTempFile("AppiumLibrary-", ".log");
				tempFile.deleteOnExit();
				FileWriter writer = new FileWriter(tempFile);
				writer.write(msg);
				writer.close();

				// Read the message in Python back and log it.
				loggingPythonInterpreter.get().exec(
						String.format("from __future__ import with_statement\n" + "\n"
								+ "with open('%s', 'r') as msg_file:\n" + "    msg = msg_file.read()\n"
								+ "    logger.%s(msg%s)", tempFile.getAbsolutePath().replace("\\", "\\\\"), methodName,
								methodArguments));

			} catch (IOException e) {
				throw new AppiumLibraryNonFatalException("Error in handling temp file for long log message.", e);
			}
		} else {
			// Message is small enough to get parsed by Jython
			loggingPythonInterpreter.get().exec(
					String.format("logger.%s('%s'%s)", methodName, msg.replace("\\", "\\\\").replace("'", "\\'")
							.replace("\n", "\\n"), methodArguments));
		}
	}

	protected File getLogDir() {
		if (logDir == null) {
			PyString logDirName = (PyString) loggingPythonInterpreter.get().eval("GLOBAL_VARIABLES['${LOG FILE}']");
			if (logDirName != null && !(logDirName.asString().toUpperCase().equals("NONE"))) {
				return new File(logDirName.asString()).getParentFile();
			}
			logDirName = (PyString) loggingPythonInterpreter.get().eval("GLOBAL_VARIABLES['${OUTPUTDIR}']");
			return new File(logDirName.asString()).getParentFile();
		} else {
			return new File(logDir);
		}
	}

	public static void setLogDir(String logDirectory) {
		logDir = logDirectory;
	}

	protected static ThreadLocal<PythonInterpreter> loggingPythonInterpreter = new ThreadLocal<PythonInterpreter>() {

		@Override
		protected PythonInterpreter initialValue() {
			PythonInterpreter pythonInterpreter = new PythonInterpreter();
			pythonInterpreter.exec("from robot.variables import GLOBAL_VARIABLES; from robot.api import logger;");
			return pythonInterpreter;
		}
	};
}
