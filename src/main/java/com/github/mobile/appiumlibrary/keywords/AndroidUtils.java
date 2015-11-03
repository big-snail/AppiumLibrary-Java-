package com.github.mobile.appiumlibrary.keywords;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.NetworkConnectionSetting;
import io.appium.java_client.android.AndroidDriver;

import org.apache.commons.codec.binary.Base64;
import org.robotframework.javalib.annotation.ArgumentNames;
import org.robotframework.javalib.annotation.Autowired;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywordOverload;
import org.robotframework.javalib.annotation.RobotKeywords;

import com.github.mobile.appiumlibrary.AppiumLibraryFatalException;
import com.github.mobile.appiumlibrary.RunOnFailureKeywordsAdapter;

@RobotKeywords
public class AndroidUtils extends RunOnFailureKeywordsAdapter{
	@Autowired
	protected ApplicationManagement applicationManage;
	
	/**
	 * Returns an integer bitmask specifying the network connection type.
	 * Android only.<br>
	 * See `set network connection status` for more details.
	 * @return The andorid network current status
	 */
	@RobotKeyword
	public String getNetworkConnectionStatus(){
		String status = null;
		AppiumDriver driver = applicationManage.getCurrentDriver();
		if(driver instanceof AndroidDriver){
			AndroidDriver android = (AndroidDriver) driver;
			status = android.getNetworkConnection().value+"";
		}else{
			throw new AppiumLibraryFatalException("This keyword only support android platform");
		}
		
		return status;
	}
	
	/**
	 * Sets the network connection Status.
	 * Android only.<br>
	 * Possible values:<br>
	 * Value |(Alias)          | Data | Wifi | Airplane Mode<br>
	 * -------------------------------------------------<br>
	 * 0     |(None)           | 0    | 0    | 0<br>
	 * 1     |(Airplane Mode)  | 0    | 0    | 1<br>
	 * 2     |(Wifi only)      | 0    | 1    | 0<br>
	 * 4     |(Data only)      | 1    | 0    | 0<br>
	 * 6     |(All network on) | 1    | 1    | 0<br>
	 * @param connectionStatus
	 * 				The network status value
	 */
	@RobotKeyword
	@ArgumentNames({"connectionStatus"})
	public void setNetworkConnectionStatus(String connectionStatus){
		AppiumDriver driver = applicationManage.getCurrentDriver();
		int status = Integer.parseInt(connectionStatus);
		if(driver instanceof AndroidDriver){
			AndroidDriver android = (AndroidDriver) driver;
			NetworkConnectionSetting setting = new NetworkConnectionSetting(status);
			android.setNetworkConnection(setting);
		}else{
			throw new AppiumLibraryFatalException("This keyword only support android platform");
		}
	}
	
	/**
	 * Retrieves the file at `path` and return it's content.
	 * Android only.<br>
	 * @param path
	 * 			the path to the file on the device
	 * @param decode
	 * 			true/false decode the data (base64) before returning it (default=false)
	 * @return The pull file content
	 */
	@RobotKeyword
	@ArgumentNames({"path","decode=false"})
	public String pullFile(String path, String decode){
		AppiumDriver driver = applicationManage.getCurrentDriver();
		boolean isDecode = Boolean.parseBoolean(decode);
		byte[] theFile =null;
		if(driver instanceof AndroidDriver){
			AndroidDriver android = (AndroidDriver) driver;
			theFile = android.pullFile(path);
			if(isDecode){
				theFile = Base64.decodeBase64(theFile);
			}
		}else{
			throw new AppiumLibraryFatalException("This keyword only support android platform");
		}
		
		return new String(theFile);
	}
	
	@RobotKeywordOverload
	public String pullFile(String path){
		return pullFile(path, "false");
	}
	
	/**
	 * Retrieves a folder at `path`. Returns the folder's contents zipped.
	 * Android only.
	 * @param path
	 * 			the path to the folder on the device
	 * @param decode
	 * 			true/false decode the data (base64) before returning it (default=false)
	 * @return The pull folder info
	 */
	@RobotKeyword
	@ArgumentNames({"path","decode=false"})
	public String pullFolder(String path, String decode){
		AppiumDriver driver = applicationManage.getCurrentDriver();
		boolean isDecode = Boolean.parseBoolean(decode);
		byte[] theFolder =null;
		if(driver instanceof AndroidDriver){
			AndroidDriver android = (AndroidDriver) driver;
			theFolder = android.pullFolder(path);
			if(isDecode){
				theFolder = Base64.decodeBase64(theFolder);
			}
		}else{
			throw new AppiumLibraryFatalException("This keyword only support android platform");
		}
		
		return new String(theFolder);
	}
	
	@RobotKeywordOverload
	public String pullFolder(String path){
		return pullFolder(path, "false");
	}
	
	/**
	 * Puts the data in the file specified as `path`.
	 * Android only.<br>
	 * @param path
	 * 			the path on the device
	 * @param data
	 * 			data to be written to the file
	 * @param encode
	 * 			true/false encode the data as base64 before writing it to the file (default=false)
	 */
	@RobotKeyword
	@ArgumentNames({"path","data","encode=false"})
	public void pushFile(String path, String data, String encode){
		AppiumDriver driver = applicationManage.getCurrentDriver();
		boolean isEncode = Boolean.parseBoolean(encode);
		if(driver instanceof AndroidDriver){
			AndroidDriver android = (AndroidDriver) driver;
			if(isEncode){
				data = Base64.encodeBase64String(data.getBytes());
			}
			
			android.pushFile(path, data.getBytes());
		}else{
			throw new AppiumLibraryFatalException("This keyword only support android platform");
		}
	}
	
	@RobotKeywordOverload
	public void pushFile(String path, String data){
		pushFile(path, data, "false");
	}
}
