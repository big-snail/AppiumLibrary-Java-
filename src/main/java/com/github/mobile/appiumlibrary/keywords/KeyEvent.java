package com.github.mobile.appiumlibrary.keywords;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;

import org.robotframework.javalib.annotation.ArgumentNames;
import org.robotframework.javalib.annotation.Autowired;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywordOverload;
import org.robotframework.javalib.annotation.RobotKeywords;

import com.github.mobile.appiumlibrary.AppiumLibraryFatalException;
import com.github.mobile.appiumlibrary.RunOnFailureKeywordsAdapter;

@RobotKeywords
public class KeyEvent extends RunOnFailureKeywordsAdapter{
	@Autowired
	protected ApplicationManagement applicationManage;
	
	/**
	 * Sends a press of keycode to the device. Android only. 
	 * Possible keycodes & meta states can be found in 
	 * http://developer.android.com/reference/android/view/KeyEvent.html<br>
	 * Meta state describe the pressed state of key modifiers such as 
	 * Shift, Ctrl & Alt keys. The Meta State is an integer in which each
	 * bit set to 1 represents a pressed meta key. For example
	 * META_SHIFT_ON = 1
	 * META_ALT_ON = 2
	 * MetaState=1 --> Shift is pressed
	 * MetaState=2 --> Alt is pressed
	 * MetaState=3 --> Shift+Alt is pressed
	 * @param keycode
	 * 				the keycode to be sent to the device
	 * @param metastate
	 * 				status of the meta keys
	 */
	@RobotKeyword
	@ArgumentNames({"keycode", "metastate=None"})
	public void pressKeycode(String keycode, String metastate){
		AppiumDriver driver = applicationManage.getCurrentDriver();
		int key = Integer.parseInt(keycode);
		int meta = metastate!=null ? Integer.parseInt(metastate) : 0;
		if(driver instanceof AndroidDriver){
			AndroidDriver android = (AndroidDriver) driver;
			if(meta!=0)
				android.sendKeyEvent(key, meta);
			else
				android.sendKeyEvent(key);
		}else{
			throw new AppiumLibraryFatalException("This keyword only support android platform");
		}
	}
	
	@RobotKeywordOverload
	public void pressKeycode(String keycode){
		pressKeycode(keycode, null);
	}
}
