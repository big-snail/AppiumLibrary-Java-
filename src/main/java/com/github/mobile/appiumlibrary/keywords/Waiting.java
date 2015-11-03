package com.github.mobile.appiumlibrary.keywords;

import org.robotframework.javalib.annotation.ArgumentNames;
import org.robotframework.javalib.annotation.Autowired;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywordOverload;
import org.robotframework.javalib.annotation.RobotKeywords;

import com.github.mobile.appiumlibrary.AppiumLibraryNonFatalException;
import com.github.mobile.appiumlibrary.RunOnFailureKeywordsAdapter;
import com.github.mobile.appiumlibrary.utils.Robotframework;

@RobotKeywords
public class Waiting extends RunOnFailureKeywordsAdapter {
	@Autowired
	protected ApplicationManagement applicationManage;
	
	@Autowired
	protected Element element;
	
	/**
	 * Waits until the current page contains <b>text</b>.<br>
	 * Fails, if the timeout expires, before the text appears. <br>
	 * See `Introduction` for details about timeouts.<br>
	 * @param text
	 * 			The text to verify.
	 * @param timeout
	 * 			Default=NONE. Optional timeout interval.
	 * @param error
	 * 			Default=NONE. Optional custom error message.
	 */
	@RobotKeyword
	@ArgumentNames({"text", "timeout=None", "error=None"})
	public void waitUntilPageContains(final String text, String timeout, String error){
		if (error == null) {
			error = String.format("Text '%s' did not appear in <TIMEOUT>", text);
		}
		waitUntil(timeout, error, new WaitUntilFunction() {

			@Override
			public boolean isFinished() {
				return element.isTextPresent(text);
			}
		});
	}
	
	@RobotKeywordOverload
	public void waitUntilPageContains(String text){
		waitUntilPageContains(text, null);
	}
	
	@RobotKeywordOverload
	public void waitUntilPageContains(String text, String timeout){
		waitUntilPageContains(text, timeout, null);
	}
	
	/**
	 * Waits until the current page does not contain <b>text</b>.<br>
	 * <br>
	 * Fails, if the timeout expires, before the text disappears. <br>
	 * <br>
	 * See `Introduction` for details about timeouts.<br>
	 * @param text
	 * 			The text to verify.
	 * @param timeout
	 * 			Default=NONE. Optional timeout interval.
	 * @param error
	 * 			Default=NONE. Optional custom error message.
	 */
	@RobotKeyword
	@ArgumentNames({"text", "timeout=None", "error=None"})
	public void waitUntilPageNotContain(final String text, String timeout, String error){
		if (error == null) {
			error = String.format("Text '%s' did not disappear in <TIMEOUT>", text);
		}
		waitUntil(timeout, error, new WaitUntilFunction() {

			@Override
			public boolean isFinished() {
				return !element.isTextPresent(text);
			}
		});
	}
	
	@RobotKeywordOverload
	public void waitUntilPageNotContain(String text){
		waitUntilPageContains(text, null);
	}
	
	@RobotKeywordOverload
	public void waitUntilPageNotContain(String text, String timeout){
		waitUntilPageContains(text, timeout, null);
	}
	
	/**
	 * Waits until the element identified by <b>locator</b> is found on the
	 * current page.<br>
	 * <br>
	 * Fails, if the timeout expires, before the element appears. <br>
	 * <br>
	 * See `Introduction` for details about locators and timeouts.<br>
	 * 
	 * @param locator
	 *            The locator to locate the element.
	 * @param timeout
	 *            Default=NONE. Optional timeout interval.
	 * @param error
	 * 			  Default=NONE. Optional custom error message.
	 */
	@RobotKeyword
	@ArgumentNames({"locator", "timeout=None", "error=None"})
	public void waitUntilPageContainsElement(final String locator, String timeout, String error){
		if (error == null) {
			error = String.format("Element '%s' did not appear in <TIMEOUT>", locator);
		}
		waitUntil(timeout, error, new WaitUntilFunction() {

			@Override
			public boolean isFinished() {
				return element.isElementPresent(locator);
			}
		});
	}
	
	@RobotKeywordOverload
	public void waitUntilPageContainsElement(String locator, String timeout){
		waitUntilPageContainsElement(locator, timeout, null);
	}
	
	@RobotKeywordOverload
	public void waitUntilPageContainsElement(String locator){
		waitUntilPageContainsElement(locator, null);
	}
	
	/**
	 * Waits until the element identified by <b>locator</b> is not found on the
	 * current page.<br>
	 * <br>
	 * Fails, if the timeout expires, before the element disappears. <br>
	 * <br>
	 * See `Introduction` for details about locators and timeouts.<br>
	 * 
	 * @param locator
	 *            The locator to locate the element.
	 * @param timeout
	 *            Default=NONE. Optional timeout interval.
	 * @param error
	 *            Default=NONE. Optional custom error message.
	 */
	@RobotKeyword
	@ArgumentNames({"locator", "timeout=None", "error=None"})
	public void waitUntilPageNotContainElement(final String locator, String timeout, String error){
		if (error == null) {
			error = String.format("Element '%s' did not disappear in <TIMEOUT>", locator);
		}
		waitUntil(timeout, error, new WaitUntilFunction() {

			@Override
			public boolean isFinished() {
				return !element.isElementPresent(locator);
			}
		});
	}
	
	@RobotKeywordOverload
	public void waitUntilPageNotContainElement(String locator, String timeout){
		waitUntilPageNotContainElement(locator, timeout, null);
	}
	
	@RobotKeywordOverload
	public void waitUntilPageNotContainElement(String locator){
		waitUntilPageNotContainElement(locator, null);
	}
	//*****************************************
	//** Internal Method
	//****************************************
	protected void waitUntil(String timestr, String message, WaitUntilFunction function) {
		double timeout = timestr != null ? Robotframework.timestrToSecs(timestr) : applicationManage.getTimeout();
		message = message.replace("<TIMEOUT>", Robotframework.secsToTimestr(timeout));
		long maxtime = System.currentTimeMillis() + (long) (timeout * 1000);
		for (;;) {
			try {
				if (function.isFinished()) {
					break;
				}
			} catch (Throwable t) {
			}
			if (System.currentTimeMillis() > maxtime) {
				throw new AppiumLibraryNonFatalException(message);
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}
		}
	}

	protected static interface WaitUntilFunction {

		boolean isFinished();
	}
}
