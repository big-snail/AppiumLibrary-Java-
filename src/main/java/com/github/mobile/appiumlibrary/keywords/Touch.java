package com.github.mobile.appiumlibrary.keywords;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;

import org.openqa.selenium.WebElement;
import org.robotframework.javalib.annotation.ArgumentNames;
import org.robotframework.javalib.annotation.Autowired;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywordOverload;
import org.robotframework.javalib.annotation.RobotKeywords;

import com.github.mobile.appiumlibrary.AppiumLibraryFatalException;
import com.github.mobile.appiumlibrary.RunOnFailureKeywordsAdapter;

@RobotKeywords
public class Touch extends RunOnFailureKeywordsAdapter{
	@Autowired
	protected Element element;
	
	@Autowired
	protected ApplicationManagement applicationManage;
	
	/**
	 * Zooms in on an element a certain amount.
	 * @param locator
	 * 				Element locator to zoom
	 */
	@RobotKeyword
	@ArgumentNames({"locator"})
	public void zoom(String locator){
		AppiumDriver driver = applicationManage.getCurrentDriver();
		WebElement ele = element.elementFind(locator, true, true).get(0);
		driver.zoom(ele);
	}
	
	/**
	 * Pinch in on an element a certain amount.
	 */
	@RobotKeyword
	@ArgumentNames({"locator"})
	public void pinch(String locator){
		AppiumDriver driver = applicationManage.getCurrentDriver();
		WebElement ele = element.elementFind(locator, true, true).get(0);
		driver.pinch(ele);
	}
	
	/**
	 * Swipe from one point to another point, for an optional duration.
	 * @param startX
	 * 			Start point x poistion
	 * @param startY
	 * 			Start point y position
	 * @param endX
	 * 			End point x position
	 * @param endY
	 * 			End point y postion
	 * @param duration
	 * 			Swipe in duration time
	 */
	@RobotKeyword
	@ArgumentNames({"startX","startY","endX", "endY", "duration=1000"})
	public void swipe(int startX, int startY, int endX, int endY, int duration){
		AppiumDriver driver = applicationManage.getCurrentDriver();
		driver.swipe(startX, startY, endX, endY, duration);
	}
	
	@RobotKeywordOverload
	public void swipe(int startX, int startY, int endX, int endY){
		swipe(startX, startY, endX, endY, 1000);
	}
	
	/**
	 * Scroll to the element which contain text
	 */
	@RobotKeyword
	@ArgumentNames({"text"})
	public void scroll(String text){
		AppiumDriver driver = applicationManage.getCurrentDriver();
		
		driver.scrollTo(text);
	}
	
	/**
	 * Long press the element
	 */
	@RobotKeyword
	@ArgumentNames({"locator"})
	public void longPress(String locator){
		AppiumDriver driver = applicationManage.getCurrentDriver();
		WebElement ele = element.elementFind(locator, true, true).get(0);
		TouchAction ta = new TouchAction(driver);
		ta.longPress(ele).perform();
	}
	
	/**
	 * Send a long key event to the device
	 * @param keycode
	 * 			The key code
	 * @param metastate
	 * 			Android device meta state
	 */
	@RobotKeyword
	@ArgumentNames({"keycode","metastate=None"})
	public void longPressKeyCode(String keycode, String metastate){
		AppiumDriver driver = applicationManage.getCurrentDriver();
		int key = Integer.parseInt(keycode);
		int meta = metastate!=null ? Integer.parseInt(metastate) : 0;
		if(driver instanceof AndroidDriver){
			AndroidDriver android = (AndroidDriver) driver;
			if(meta!=0)
				android.longPressKeyCode(key, meta);
			else
				android.longPressKeyCode(key);
		}else{
			throw new AppiumLibraryFatalException("This keyword only support android platform");
		}
	}
	
	
	@RobotKeywordOverload
	public void longPressKeyCode(String keycode){
		longPressKeyCode(keycode, null);
	}
	
	/**
	 * Tap on element
	 */
	@RobotKeyword
	@ArgumentNames({"locator"})
	public void tap(String locator){
		AppiumDriver driver = applicationManage.getCurrentDriver();
		WebElement ele = element.elementFind(locator, true, true).get(0);
		TouchAction ta = new TouchAction(driver);
		ta.tap(ele).perform();
	}
}

