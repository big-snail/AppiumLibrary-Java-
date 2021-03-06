package com.github.mobile.appiumlibrary.keywords;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.robotframework.javalib.annotation.ArgumentNames;
import org.robotframework.javalib.annotation.Autowired;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywordOverload;
import org.robotframework.javalib.annotation.RobotKeywords;

import com.github.mobile.appiumlibrary.RunOnFailureKeywordsAdapter;
import com.github.mobile.appiumlibrary.utils.Robotframework;

@RobotKeywords
public class Screenshot extends RunOnFailureKeywordsAdapter{
	@Autowired
	protected ApplicationManagement applicationManage;
	
	@Autowired
	protected Logging logging;
	
	// ##############################
		// Keywords
		// ##############################
		@RobotKeywordOverload
		public void capturePageScreenshot() {
			capturePageScreenshot(null);
		}

		/**
		 * Take a screenshot of the current page and embed it into the log.<br>
		 * <br>
		 * The <b>filename</b> argument specifies the name of the file to write the
		 * screenshot into. If no filename is given, the screenshot is saved into
		 * file selenium-screenshot-&lt;counter&gt;.png under the directory where
		 * the Robot Framework log file is written into. The filename is also
		 * considered relative to the same directory, if it is not given in absolute
		 * format.<br>
		 * <br>
		 * A CSS can be used to modify how the screenshot is taken. By default the
		 * background color is changed to avoid possible problems with background
		 * leaking when the page layout is somehow broken.<br>
		 * 
		 * @param filename
		 *            Default=NONE. Name of the file to write.
		 */
		@RobotKeyword
		@ArgumentNames({ "filename=NONE" })
		public void capturePageScreenshot(String filename) {
			File logdir = logging.getLogDir();
			File path = new File(logdir, normalizeFilename(filename));
			String link = Robotframework.getLinkPath(path, logdir);

			TakesScreenshot takesScreenshot = ((TakesScreenshot) applicationManage.getCurrentDriver());
			if (takesScreenshot == null) {
				logging.warn("Can't take screenshot. No open browser found");
				return;
			}

			byte[] png = takesScreenshot.getScreenshotAs(OutputType.BYTES);
			writeScreenshot(path, png);

			logging.html(String.format(
					"</td></tr><tr><td colspan=\"3\"><a href=\"%s\"><img src=\"%s\" width=\"800px\"></a>", link, link));
		}
		
		// ##############################
		// Internal Methods
		// ##############################

		protected int screenshotIndex = 0;

		protected void writeScreenshot(File path, byte[] png) {
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(path);
				fos.write(png);
				fos.flush();
			} catch (IOException e) {
				logging.warn(String.format("Can't write screenshot '%s'", path.getAbsolutePath()));
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						logging.warn("Can't even close stream");
					}
				}
			}
		}

		protected String normalizeFilename(String filename) {
			if (filename == null) {
				screenshotIndex++;
				filename = String.format("appium-screenshot-%d.png", screenshotIndex);
			} else {
				filename = filename.replace('/', File.separatorChar);
			}
			return filename;
		}
}
