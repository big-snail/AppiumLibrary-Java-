package com.github.mobile.appiumlibrary.keywords;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.robotframework.javalib.annotation.ArgumentNames;
import org.robotframework.javalib.annotation.Autowired;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywordOverload;
import org.robotframework.javalib.annotation.RobotKeywords;

import com.github.mobile.appiumlibrary.AppiumLibraryFatalException;
import com.github.mobile.appiumlibrary.RunOnFailureKeywordsAdapter;
import com.github.mobile.appiumlibrary.utils.DriverCache;
import com.github.mobile.appiumlibrary.utils.Robotframework;

@RobotKeywords
public class ApplicationManagement extends RunOnFailureKeywordsAdapter{
	protected DriverCache driverCache = new DriverCache();
	/**
	 * Timeout in milliseconds
	 */
	protected double timeout = 5.0;

	/**
	 * Implicit wait in milliseconds
	 */
	protected double implicitWait = 0;

	/**
	 * Instantiated Logging keyword bean
	 */
	@Autowired
	protected Logging logging;
	
	// ##############################
	// Getter / Setter
	// ##############################
	
	public DriverCache getDriverCache(){
		return driverCache;
	}
	
	public AppiumDriver getCurrentDriver(){
		return driverCache.getCurrent();
	}
	
	public double getTimeout() {
		return timeout;
	}
	
	public void setTimeout(String timestr){
		this.timeout = Robotframework.timestrToSecs(timestr);
	}
	
	// ##############################
	// Keywords
	// ##############################
	/**
	 * Closes the current application.
	 */
	@RobotKeyword
	public void closeApplication(){
		logging.debug(String.format("Closing application with session id %s", driverCache.getCurrentSessionId()));
		driverCache.close();
	}
	
	
	/**
	 * Closes all open applications.<br>
	 * This keyword is meant to be used in test or suite teardown to
	 * make sure all the applications are closed before the test execution
	 * finishes.<br>
	 * After this keyword, the application indices returned by `Open Application`
	 * are reset and start from `1`
	 */
	@RobotKeyword
	public void closeAllApplications(){
		logging.debug("Closing all applications");
		driverCache.closeAll();
	}
	
	/**
	 * Opens a new application to given Appium server.
	 * Capabilities of appium server, Android and iOS,<br>
	 * Please check http://appium.io/slate/en/master/?python#appium-server-capabilities<br>
	 * @param remoteURL
	 * 			The appium server url
	 * @param alias
	 * 			Connection alias
	 * @param desiredCaps
	 * 			Desired capabilities, with key=value format and spilt by comma
	 * @throws MalformedURLException
	 */
	@RobotKeyword
	@ArgumentNames({"remoteURL","alias","desiredCaps"})
	public String openApplication(String remoteURL, String alias, String desiredCaps) throws MalformedURLException{
		AppiumDriver driver = createApplication(remoteURL, desiredCaps);
		logging.debug(String.format("Opened application with session id %s", driver.getSessionId()));
		if("".equals(alias))
			alias = null;
		return driverCache.register(driver, alias);
	}
	
	protected AppiumDriver createApplication(String remoteURL, String desiredCaps) throws MalformedURLException{
		DesiredCapabilities desiredCapabilities = createDesiredCapabilities(desiredCaps);
		AppiumDriver driver=null;
		String platformName = (String) desiredCapabilities.asMap().get("platformName");
		if(platformName.equalsIgnoreCase("ios")){
			driver = new IOSDriver(new URL(remoteURL), desiredCapabilities);
		}else if(platformName.equalsIgnoreCase("android")){
			driver = new AndroidDriver(new URL(remoteURL), desiredCapabilities);
		}else{
			throw new AppiumLibraryFatalException(String.format("Only support ios/android platform, but not the platform %s", platformName));
		}
		
		return driver;
	}
	
	protected DesiredCapabilities createDesiredCapabilities(String desiredCaps){
		DesiredCapabilities cap = new DesiredCapabilities();
		String[] keyValues = desiredCaps.split(",");
		for(String entry : keyValues){
			String[] entryMap = entry.split("=");
			if(entryMap.length<2){
				logging.info(String.format("The desired capabilities should be key=value format, but %s is not", entry));
				throw new AppiumLibraryFatalException("Desired capabilities format error");
			}
			
			cap.setCapability(entryMap[0].trim(), entryMap[1].trim());
		}
		
		return cap;
	}
	
	/**
	 * Switches the active application by index or alias.<br>
	 * `index_or_alias` is either application index (an integer) or alias
	 * (a string). Index is got as the return value of `Open Application`.<br>
	 * This keyword returns the index of the previous active application,
	 * which can be used to switch back to that application later.
	 * @param indexOrAlias
	 * 			Application index or alias
	 * @return The prior application index
	 */
	@RobotKeyword
	@ArgumentNames({"indexOrAlias"})
	public String switchApplication(String indexOrAlias){
		String oldIndex = driverCache.getCurrentSessionId();
		driverCache.switchApplication(indexOrAlias);
		return oldIndex;
	}
	
	/**
	 * Reset application
	 */
	@RobotKeyword
	public void resetApplication(){
		AppiumDriver driver= driverCache.getCurrent();
		driver.resetApp();
	}
	
	/**
	 * Removes the application that is identified with an application id
	 * 
	 * @param applicationId
	 * 				Application identifier
	 */
	@RobotKeyword
	@ArgumentNames({"applicationId"})
	public void removeApplication(String applicationId){
		AppiumDriver driver = driverCache.getCurrent();
		driver.removeApp(applicationId);
	}
	
	/**
	 * Returns the entire source of the current page.
	 * @return The page info with xml format
	 */
	@RobotKeyword
	public String getSource(){
		AppiumDriver driver = driverCache.getCurrent();
		return driver.getPageSource();
	}
	
	/**
	 * Logs and returns the entire html source of the current page or frame.<br>
	 * The `loglevel` argument defines the used log level. Valid log levels are
	 * `WARN`, `INFO` (default), `DEBUG`, `TRACE` and `NONE` (no logging).
	 * @param loglevel
	 * 			Log level
	 * @return The page info with xml format
	 */
	@RobotKeyword
	@ArgumentNames({"loglevel=INFO"})
	public String logSource(String loglevel){
		AppiumDriver driver = driverCache.getCurrent();
		String source = driver.getPageSource();
		logging.log(source, loglevel.toUpperCase());
		return source;
	}
	
	@RobotKeywordOverload
	public String logSource(){
		return logSource("INFO");
	}
	
	/**
	 * Goes one step backward in the browser history.
	 */
	@RobotKeyword
	public void goBack(){
		driverCache.getCurrent().navigate().back();
	}
	
	/**
	 * Lock the device
	 */
	@RobotKeyword
	public void lock(){
		driverCache.getCurrent().lockScreen(0);
	}
	
	/**
	 * Puts the application in the background on the device for a certain
	 * duration.
	 * @param seconds
	 * 			timeout for background app
	 */
	@RobotKeyword
	@ArgumentNames("seconds=5")
	public void backgroundApp(String seconds){
		int sec = Integer.parseInt(seconds);
		driverCache.getCurrent().runAppInBackground(sec);
	}
	
	@RobotKeywordOverload
	public void backgroundApp(){
		backgroundApp("5");
	}
	
	/**
	 * Set the device orientation to PORTRAIT
	 */
	@RobotKeyword
	public void portrait(){
		driverCache.getCurrent().rotate(ScreenOrientation.PORTRAIT);
	}
	
	/**
	 * Set the device orientation to LANSCAPE
	 */
	@RobotKeyword
	public void landscape(){
		driverCache.getCurrent().rotate(ScreenOrientation.LANDSCAPE);
	}
	
	/**
	 * Get current context.
	 * @return The current app context
	 */
	@RobotKeyword
	public String getCurrentContext(){
		return driverCache.getCurrent().getContext();
	}
	
	/**
	 * Get available contexts.
	 * @return All available contexts
	 */
	@RobotKeyword
	public Set<String> getContexts(){
		return driverCache.getCurrent().getContextHandles();
	}
	
	/**
	 * Switch to a new context
	 * @param contextName
	 * 			Context to switch to
	 */
	@RobotKeyword
	@ArgumentNames({"contextName"})
	public void switchToContext(String contextName){
		driverCache.getCurrent().switchTo().window(contextName);
	}
	
	/**
	 * Opens URL in default web browser.
	 * @param url
	 * 			The url switch to in mobile browser
	 */
	@RobotKeyword
	@ArgumentNames({"url"})
	public void goToUrl(String url){
		driverCache.getCurrent().get(url);
	}	
}
