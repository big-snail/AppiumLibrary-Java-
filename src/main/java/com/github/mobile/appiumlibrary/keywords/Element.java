package com.github.mobile.appiumlibrary.keywords;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.appium.java_client.AppiumDriver;

import org.openqa.selenium.WebElement;
import org.robotframework.javalib.annotation.ArgumentNames;
import org.robotframework.javalib.annotation.Autowired;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywordOverload;
import org.robotframework.javalib.annotation.RobotKeywords;

import com.github.mobile.appiumlibrary.AppiumLibraryFatalException;
import com.github.mobile.appiumlibrary.RunOnFailureKeywordsAdapter;
import com.github.mobile.appiumlibrary.locators.ElementFinder;

@RobotKeywords
public class Element extends RunOnFailureKeywordsAdapter{
	@Autowired
	protected ApplicationManagement applicationManage;
	
	@Autowired
	protected Logging logging;
	
	private ElementFinder elementFinder = new ElementFinder();
	
	//**************************
	//Keywords method
	//*************************
	
	/**
	 * Clears the text field identified by `locator`.<br>
	 * See `introduction` for details about locating elements.
	 * @param locator
	 * 			Element locator
	 */
	@RobotKeyword
	@ArgumentNames({"locator"})
	public void cleanText(String locator){
		logging.info(String.format("Clear text field '%s'", locator));
		elementClearTextByLocator(locator);
	}
	
	@RobotKeyword
	@ArgumentNames({"locator"})
	public void clickElement(String locator){
		logging.info(String.format("Clicking element '%s'.", locator));
		elementFind(locator, true, true).get(0).click();
	}
	
	/**
	 * Click button
	 * @param indexOrName
	 * 			Element index or name
	 */
	@RobotKeyword
	@ArgumentNames({"indexOrName"})
	public void clickButton(String indexOrName){
		Map<String, String> platformClassDict = new HashMap<String, String>();
		platformClassDict.put("ios", "UIAButton");
		platformClassDict.put("android", "android.widget.Button");
		if(isSupportPlatform(platformClassDict)){
			String className = getClass(platformClassDict);
			clickElementByClassName(className, indexOrName);
		}
	}
	
	/**
	 * Types the given `text` into text field identified by `locator`.
	 * See `introduction` for details about locating elements.
	 * @param locator
	 * 			Element locator	
	 * @param text
	 * 			text to input
	 */
	@RobotKeyword
	@ArgumentNames({"locator","text"})
	public void inputText(String locator, String text){
		logging.info(String.format("Typing text '%s' into text field '%s'", text, locator));
		elementInputTextByLocator(locator, text);
	}
	
	/**
	 * Types the given password into text field identified by `locator`.<br>
	 * Difference between this keyword and `Input Text` is that this keyword
	 * does not log the given password. See `introduction` for details about
	 * locating elements.
	 * @param locator
	 * 				Element locator
	 * @param text
	 * 				text to input
	 */
	@RobotKeyword
	@ArgumentNames({"locator", "text"})
	public void inputPassword(String locator, String text){
		logging.info(String.format("Typing password into text field '%s'", locator));
		elementInputTextByLocator(locator, text);
	}
	
	/**
	 * Sets the given value into text field identified by `locator`. This is an IOS only keyword, input value makes use of set_value<br>
	 * See `introduction` for details about locating elements.
	 * @param locator
	 * 				Element locator
	 * @param text
	 * 				value to input
	 */
	@RobotKeyword
	@ArgumentNames({"locator", "text"})
	public void inputValue(String locator, String text){
		logging.info(String.format("Setting text '%s' into text field '%s'", text, locator));
		elementInputValueByLocator(locator, text);
	}
	
	/**
	 * Hides the software keyboard on the device, using the specified key to
	 * press. If no key name is given, the keyboard is closed by moving focus
	 * from the text field. iOS only.
	 */
	@RobotKeyword
	public void hideKeyboard(){
		AppiumDriver driver = applicationManage.getCurrentDriver();
		driver.hideKeyboard();
	}
	
	/**
	 * Verifies that current page contains `text`.<br>
	 * If this keyword fails, it automatically logs the page source
	 * using the log level specified with the optional `loglevel` argument.
	 * Giving `NONE` as level disables logging.
	 * @param text
	 * 			Text expected displayed
	 * @param loglevel
	 * 			Log level
	 */
	@RobotKeyword
	@ArgumentNames({"text","loglevel=INFO"})
	public void pageShouldContainText(String text, String loglevel){
		String source = applicationManage.logSource(loglevel);
		if(!source.contains(text)){
			throw new AppiumLibraryFatalException(String.format("Page should have contained text '%s', but it did not", text));
		}
		
		logging.info(String.format("Current page contains text '%s'.", text));
	}
	
	@RobotKeywordOverload
	public void pageShouldContainText(String text){
		pageShouldContainText(text, "INFO");
	}
	
	/**
	 * Verifies that current page not contains `text`.<br>
	 * If this keyword fails, it automatically logs the page source
	 * using the log level specified with the optional `loglevel` argument.
	 * Giving `NONE` as level disables logging.
	 * @param text
	 * 			Text unexpected displayed
	 * @param loglevel
	 * 			Log level
	 */
	@RobotKeyword
	@ArgumentNames({"text","loglevel=INFO"})
	public void pageShouldNotContainText(String text, String loglevel){
		String source = applicationManage.logSource(loglevel);
		if(source.contains(text)){
			throw new AppiumLibraryFatalException(String.format("Page should not have contained text '%s', but it did not", text));
		}
		
		logging.info(String.format("Current does not page contains text '%s'.", text));
	}
	
	@RobotKeywordOverload
	public void pageShouldNotContainText(String text){
		pageShouldNotContainText(text, "INFO");
	}
	
	/**
	 * Verifies that current page contains `locator` element.<br>
	 * If this keyword fails, it automatically logs the page source
	 * using the log level specified with the optional `loglevel` argument.
	 * Givin
	 * @param locator
	 * 			Element locator
	 * @param loglevel
	 * 			Log level
	 */
	@RobotKeyword
	@ArgumentNames({"locator", "loglevel=INFO"})
	public void pageShouldContainElement(String locator, String loglevel){
		if(!isElementPresent(locator)){
			applicationManage.logSource(loglevel);
			throw new AppiumLibraryFatalException(String.format("Page should have contained element '%s' but it did not", locator));
		}
		
		logging.info(String.format("Current page contains element '%s'.", locator));
	}
	
	@RobotKeywordOverload
	public void pageShouldContainElement(String locator){
		pageShouldContainElement(locator, "INFO");
	}
	
	/**
	 * Verifies that current page not contains `locator` element.<br>
	 * If this keyword fails, it automatically logs the page source
	 * using the log level specified with the optional `loglevel` argument.
	 * Givin
	 * @param locator
	 * 			Element Locator
	 * @param loglevel
	 * 			Log level
	 */
	@RobotKeyword
	@ArgumentNames({"locator", "loglevel=INFO"})
	public void pageShouldNotContainElement(String locator, String loglevel){
		if(isElementPresent(locator)){
			applicationManage.logSource(loglevel);
			throw new AppiumLibraryFatalException(String.format("Page should not have contained element '%s' but it did not", locator));
		}
		
		logging.info(String.format("Current page not contains element '%s'.", locator));
	}
	
	@RobotKeywordOverload
	public void pageShouldNotContainElement(String locator){
		pageShouldNotContainElement(locator, "INFO");
	}
	
	/**
	 * Verifies that element identified with locator is disabled.<br>
	 * Key attributes for arbitrary elements are `id` and `name`. See
	 * `introduction` for details about locating elements.
	 * @param locator
	 * 			Element locator
	 * @param loglevel
	 * 			Log level
	 */
	@RobotKeyword
	@ArgumentNames({"locator", "loglevel=INFO"})
	public void elementShouldBeDisabled(String locator, String loglevel){
		WebElement element = elementFind(locator, true, true).get(0);
		if(element.isEnabled()){
			applicationManage.logSource(loglevel);
			throw new AppiumLibraryFatalException(String.format("Element '%s' should be disabled but it did not", locator));
		}
		logging.info(String.format("Element '%s' is disabled .", locator));
	}
	
	@RobotKeywordOverload
	public void elementShouldBeDisabled(String locator){
		elementShouldBeDisabled(locator, "INFO");
	}
	
	/**
	 * Verifies that element identified with locator is enabled.<br>
	 * Key attributes for arbitrary elements are `id` and `name`. See
	 * `introduction` for details about locating elements.
	 * @param locator
	 * 			Element locator
	 * @param loglevel
	 * 			Log level
	 */
	@RobotKeyword
	@ArgumentNames({"locator", "loglevel=INFO"})
	public void elementShouldBeEnabled(String locator, String loglevel){
		WebElement element = elementFind(locator, true, true).get(0);
		if(!element.isEnabled()){
			applicationManage.logSource(loglevel);
			throw new AppiumLibraryFatalException(String.format("Element '%s' should be enabled but it did not", locator));
		}
		logging.info(String.format("Element '%s' is enabled .", locator));
	}
	
	@RobotKeywordOverload
	public void elementShouldBeEnabled(String locator){
		elementShouldBeEnabled(locator, "INFO");
	}
	
	/**
	 * 
	 * @param locator
	 * 			Element locator
	 * @param expected
	 * 			Name expected
	 */
	@RobotKeyword
	@ArgumentNames({"locator", "expected"})
	public void elementNameShouldBe(String locator, String expected){
		WebElement element = elementFind(locator, true, true).get(0);
		if(!expected.equals(element.getAttribute("name"))){
			throw new AppiumLibraryFatalException(String.format("Element '%s' name should be '%s' but is '%s'", locator, expected, element.getAttribute("name")));
		}
		
		logging.info(String.format("Element '%s' name is '%s'", locator, expected));
	}
	
	/**
	 * Get element attribute using given attribute: name, value,...
	 * @param locator
	 * 			Element locator
	 * @param attribute
	 * 			Attribute name
	 * @return	The specify attribute value
	 */
	@RobotKeyword
	@ArgumentNames({"locator", "attribute"})
	public String getElementAttribute(String locator, String attribute){
		WebElement element = elementFind(locator, true, true).get(0);
		logging.info(String.format("Element '%s' attribute is '%s'", locator, element.getAttribute(attribute)));
		return element.getAttribute(attribute);
	}
	
	/**
	 * 
	 * @param locator
	 * 			Element locator
	 * @param expected
	 * 			value expected
	 */
	@RobotKeyword
	@ArgumentNames({"locator", "expected"})
	public void elementValueShouldBe(String locator, String expected){
		WebElement element = elementFind(locator, true, true).get(0);
		if(!expected.equals(element.getAttribute("value"))){
			throw new AppiumLibraryFatalException(String.format("Element '%s' value should be '%s' but is '%s'", locator, expected, element.getAttribute("value")));
		}
	}
	//***************************
	//Internal Method
	//***************************
	
	protected boolean isIndex(String indexOrName){
		if(indexOrName.startsWith("index="))
			return true;
		else
			return false;
	}
	
	protected void clickElementByName(String name){
		AppiumDriver driver = applicationManage.getCurrentDriver();
		WebElement element = driver.findElementByName(name);
		element.click();
	}
	
	protected List<WebElement> findElementsByClassName(String className){
		AppiumDriver driver = applicationManage.getCurrentDriver();
		List<WebElement> elements = driver.findElementsByClassName(className);
		return elements;
	}
	
	protected WebElement findElementByClassName(String className, String indexOrName){
		List<WebElement> elements = findElementsByClassName(className);
		WebElement ele = null;
		if(isIndex(indexOrName)){
			int index = Integer.parseInt(indexOrName.split("=")[1]);
			ele = elements.get(index);
		}else{
			boolean found = false;
			for(WebElement element : elements){
				logging.info(element.getText());
				if(indexOrName.equals(element.getText())){
					found = true;
					ele = element;
				}
			}
			
			if(!found){
				throw new AppiumLibraryFatalException(String.format("Cannot find the element with name '%s'", indexOrName));
			}
		}
		
		return ele;
	}
	
	protected String getClass(Map<String, String> platformClassDict){
		return platformClassDict.get(applicationManage.getCurrentDriver().getCapabilities().getPlatform().name());
	}
	
	protected boolean isSupportPlatform(Map<String, String> platformClassDict){
		return platformClassDict.containsKey(applicationManage.getCurrentDriver().getCapabilities().getPlatform().name());
	}
	
	protected void clickElementByClassName(String className, String indexOrName){
		WebElement element = findElementByClassName(className, indexOrName);
		logging.info(String.format("Clicking element '%s'.", element.getText()));
		element.click();
	}
	
	protected void elementClearTextByLocator(String locator){
		WebElement element = elementFind(locator, true, true).get(0);
		element.clear();
	}
	
	protected void elementInputTextByLocator(String locator, String text){
		WebElement element = elementFind(locator, true, true).get(0);
		element.sendKeys(text);
	}
	
	protected void elementInputTextByClassName(String className, String indexOrName, String text){
		WebElement element = findElementByClassName(className, indexOrName);
		logging.info(String.format("input text in element as '%s'.", element.getText()));
		element.sendKeys(text);
	}
	
	protected void elementInputValueByLocator(String locator, String text){
		WebElement element = elementFind(locator, true, true).get(0);
		element.sendKeys(text);;
	}
	
	protected List<WebElement> elementFind(String locator, boolean firstOnly, boolean required, String tag){
		AppiumDriver driver = applicationManage.getCurrentDriver();
		List<WebElement> elements = elementFinder.find(driver, locator, tag);
		if(required && elements.size()==0){
			throw new AppiumLibraryFatalException(String.format("Element locator %s did not match any elements.", locator));
		}
		
		if(firstOnly){
			List<WebElement> tmp = new ArrayList<WebElement>();
			tmp.add(elements.get(0));
			elements = tmp;
		}
		
		return elements;
	}
	
	protected List<WebElement> elementFind(String locator, boolean firstOnly, boolean required){
		return elementFind(locator, firstOnly, required, null);
	}
	
	protected boolean isTextPresent(String text){
		String source = applicationManage.getSource();
		return source.contains(text);
	}
	
	protected boolean isElementPresent(String locator){
		AppiumDriver driver = applicationManage.getCurrentDriver();
		List<WebElement> elements = elementFinder.find(driver, locator);
		
		return elements.size()>0;
	}
}
