package com.github.mobile.appiumlibrary.locators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.python.util.PythonInterpreter;

import com.github.mobile.appiumlibrary.utils.Python;

public class ElementFinder {
	protected final static Hashtable<String, CustomStrategy> registeredLocationStrategies = new Hashtable<String, CustomStrategy>();
	
	protected enum KeyAttrs{
		DEFAULT("@id,@name"), A("@id,@name,@href,normalize-space(descendant-or-self::text())"), IMG(
				"@id,@name,@src,@alt"), INPUT("@id,@name,@value,@src"), BUTTON(
				"@id,@name,@value,normalize-space(descendant-or-self::text())");
		
		protected String[] keyAttrs;
		
		KeyAttrs(String keyAttrs){
			this.keyAttrs = keyAttrs.split(",");
		}
		
		public String[] getKeyAttrs(){
			return keyAttrs;
		}
	}
	
	protected interface Strategy{
		List<WebElement> findBy(AppiumDriver webDriver, FindByCoordinates findByCoordinates);
	};
	
	protected enum StrategyEnum implements Strategy{
		DEFAULT{
			@Override
			public List<WebElement> findBy(AppiumDriver webDriver, FindByCoordinates findByCoordinates){
				if(findByCoordinates.criteria.startsWith("//")){
					return XPATH.findBy(webDriver, findByCoordinates);
				}
				
				return findByKeyAttrs(webDriver, findByCoordinates);
			}
		},
		IDENTIFIER{
			@Override
			public List<WebElement> findBy(AppiumDriver webDriver, FindByCoordinates findByCoordinates){
				List<WebElement> elements = webDriver.findElements(By.id(findByCoordinates.criteria));
				elements.addAll(webDriver.findElements(By.name(findByCoordinates.criteria)));
				return filterElements(elements, findByCoordinates);
			}
		},
		ID{
			@Override
			public List<WebElement> findBy(AppiumDriver webDriver, FindByCoordinates findByCoordinates){
				return filterElements(webDriver.findElements(By.id(findByCoordinates.criteria)), findByCoordinates);
			}
		},
		NAME{
			@Override
			public List<WebElement> findBy(AppiumDriver webDriver, FindByCoordinates findByCoordinates){
				return filterElements(webDriver.findElements(By.name(findByCoordinates.criteria)), findByCoordinates);
			}
		},
		XPATH{
			@Override
			public List<WebElement> findBy(AppiumDriver webDriver, FindByCoordinates findByCoordinates){
				return filterElements(webDriver.findElementsByXPath(findByCoordinates.criteria), findByCoordinates);
			}
		},
		CLASS{
			@Override
			public List<WebElement> findBy(AppiumDriver webDriver, FindByCoordinates findByCoordinates){
				return filterElements(webDriver.findElements(By.className(findByCoordinates.criteria)), findByCoordinates);
			}
		},
		ACCESSIBILITY_ID{
			@Override
			public List<WebElement> findBy(AppiumDriver webDriver, FindByCoordinates findByCoordinates){
				return filterElements(webDriver.findElementsByAccessibilityId(findByCoordinates.criteria), findByCoordinates);
			}
		},
		ANDROID{
			@Override
			public List<WebElement> findBy(AppiumDriver webDriver, FindByCoordinates findByCoordinates){
				AndroidDriver android = (AndroidDriver) webDriver;
				return filterElements(android.findElementsByAndroidUIAutomator(findByCoordinates.criteria), findByCoordinates);
			}
		},
		IOS{
			@Override
			public List<WebElement> findBy(AppiumDriver webDriver, FindByCoordinates findByCoordinates){
				IOSDriver ios = (IOSDriver) webDriver;
				return filterElements(ios.findElementsByIosUIAutomation(findByCoordinates.criteria), findByCoordinates);
			}
		},
		CSS{
			@Override
			public List<WebElement> findBy(AppiumDriver webDriver, FindByCoordinates findByCoordinates){
				return filterElements(webDriver.findElementsByCssSelector(findByCoordinates.criteria), findByCoordinates);
			}
		};
		
	}
	
	protected static List<WebElement> filterElements(List<WebElement> elements, FindByCoordinates findByCoordinates){
		if(findByCoordinates.tag == null){
			return elements;
		}
		
		List<WebElement> result = new ArrayList<WebElement>();
		for(WebElement element : elements){
			if(elementMatches(element, findByCoordinates)){
				result.add(element);
			}
		}
		
		return result;
	}
	
	protected static boolean elementMatches(WebElement element, FindByCoordinates findByCoordinates){
		if(!element.getTagName().toLowerCase().equals(findByCoordinates.tag)){
			return false;
		}
		
		if(findByCoordinates.constraints !=null){
			for(String name : findByCoordinates.constraints.keySet()){
				if(!element.getAttribute(name).equals(findByCoordinates.constraints.get(name))){
					return false;
				}
			}
		}
		
		return true;
	}
	
	protected static List<WebElement> findByKeyAttrs(AppiumDriver webDriver, FindByCoordinates findByCoordinates){
		KeyAttrs keyAttrs = KeyAttrs.DEFAULT;
		
		if(findByCoordinates.tag !=null){
			try{
				keyAttrs = KeyAttrs.valueOf(findByCoordinates.tag.trim().toUpperCase());				
			}catch(IllegalArgumentException e){
				
			}
		}
		String xpathCriteria = escapeXpathValue(findByCoordinates.criteria);
		String xpathTag = findByCoordinates.tag;
		
		if(findByCoordinates.tag == null){
			xpathTag = "*";
		}
		
		List<String> xpathConstraints = new ArrayList<String>();
		if(findByCoordinates.constraints !=null){
			for(Entry<String, String> entry : findByCoordinates.constraints.entrySet()){
				xpathConstraints.add(String.format("@%s='%s'", entry.getKey(), entry.getValue()));
			}
		}
		List<String> xpathSearchers = new ArrayList<String>();
		for(String attr : keyAttrs.getKeyAttrs()){
			xpathSearchers.add(String.format("%s=%s", attr, xpathCriteria));
		}
		xpathSearchers.addAll(getAttrsWithUrl(webDriver, keyAttrs, findByCoordinates.criteria));
		String xpath = String.format("//%s[%s(%s)]", xpathTag, Python.join(" and ", xpathConstraints)
				+ (xpathConstraints.size() > 0 ? " and " : ""), Python.join(" or ", xpathSearchers));

		return webDriver.findElements(By.xpath(xpath));
	}
	
	protected static List<String> getAttrsWithUrl(AppiumDriver webDriver, KeyAttrs keyAttrs, String criteria) {
		List<String> attrs = new ArrayList<String>();
		String url = null;
		String xpathUrl = null;
		String[] srcHref = { "@src", "@href" };
		for (String attr : srcHref) {
			for (String keyAttr : keyAttrs.getKeyAttrs()) {
				if (attr.equals(keyAttr)) {
					if (url == null || xpathUrl == null) {
						url = getBaseUrl(webDriver) + "/" + criteria;
						xpathUrl = escapeXpathValue(url);
					}
					attrs.add(String.format("%s=%s", attr, xpathUrl));
				}
			}
		}
		return attrs;
	}
	
	protected static String getBaseUrl(AppiumDriver webDriver) {
		String url = webDriver.getCurrentUrl();
		int lastIndex = url.lastIndexOf('/');
		if (lastIndex != -1) {
			url = url.substring(0, lastIndex);
		}
		return url;
	}
	
	public static void addLocationStrategy(String strategyName, String functionDefinition, String delimiter) {
		registeredLocationStrategies.put(strategyName.toUpperCase(), new CustomStrategy(functionDefinition, delimiter));
	}
	
	public static List<WebElement> find(AppiumDriver webDriver, String locator){
		return find(webDriver, locator, null);
	}
	
	public static List<WebElement> find(AppiumDriver webDriver, String locator, String tag){
		if(webDriver == null){
			
		}
		
		if(locator ==null){
			
		}
		
		FindByCoordinates findByCoordinates = new FindByCoordinates();
		Strategy strategy = parseLocator(findByCoordinates, locator);
		parseTag(findByCoordinates, strategy, tag);
		return strategy.findBy(webDriver, findByCoordinates);
	}
	
	protected static ThreadLocal<PythonInterpreter> loggingPythonInterpreter = new ThreadLocal<PythonInterpreter>() {

		@Override
		protected PythonInterpreter initialValue() {
			PythonInterpreter pythonInterpreter = new PythonInterpreter();
			pythonInterpreter.exec("from robot.variables import GLOBAL_VARIABLES; from robot.api import logger;");
			return pythonInterpreter;
		}
	};

	protected static void warn(String msg) {
		loggingPythonInterpreter.get().exec(
				String.format("logger.warn('%s');", msg.replace("'", "\\'").replace("\n", "\\n")));
	}
	
	protected static Strategy parseLocator(FindByCoordinates findByCoordinates, String locator){
		String prefix = null;
		String criteria = locator;
		if(!locator.startsWith("//")){
			String[] locatorParts = locator.split("=", 2);
			if(locatorParts.length == 2){
				prefix = locatorParts[0].trim().toUpperCase();
				criteria = locatorParts[1].trim();
			}
		}
		
		Strategy strategy = StrategyEnum.DEFAULT;
		if(prefix != null){
			try{
				strategy = StrategyEnum.valueOf(prefix);
			}catch(IllegalArgumentException e){
				// No standard locator type. Look for custom strategy
				CustomStrategy customStrategy = registeredLocationStrategies.get(prefix);
				if (customStrategy != null) {
					strategy = customStrategy;
				}
			}
		}
		
		findByCoordinates.criteria = criteria;
		return strategy;
	}
	
	protected static void parseTag(FindByCoordinates findByCoordinates, Strategy strategy, String tag) {
		if (tag == null) {
			return;
		}
		tag = tag.toLowerCase();
		Map<String, String> constraints = new TreeMap<String, String>();
		if (tag.equals("link")) {
			tag = "a";
		} else if (tag.equals("image")) {
			tag = "img";
		} else if (tag.equals("list")) {
			tag = "select";
		} else if (tag.equals("text area")) {
			tag = "textarea";
		} else if (tag.equals("radio button")) {
			tag = "input";
			constraints.put("type", "radio");
		} else if (tag.equals("checkbox")) {
			tag = "input";
			constraints.put("type", "checkbox");
		} else if (tag.equals("text field")) {
			tag = "input";
			constraints.put("type", "text");
		} else if (tag.equals("file upload")) {
			tag = "input";
			constraints.put("type", "file");
		}
		findByCoordinates.tag = tag;
		findByCoordinates.constraints = constraints;
	}
	
	@SuppressWarnings("unchecked")
	protected static List<WebElement> toList(Object o) {
		if (o instanceof List<?>) {
			return (List<WebElement>) o;
		}
		List<WebElement> list = new ArrayList<WebElement>();
		if (o instanceof WebElement) {
			list.add((WebElement) o);
			return list;
		}
		return list;
	}
	
	protected static class FindByCoordinates {

		String criteria;
		String tag;
		Map<String, String> constraints;
	}
	
	protected static class CustomStrategy implements Strategy{
		protected String functionDefinition;

		protected String delimiter;

		public CustomStrategy(String functionDefinition, String delimiter) {
			this.functionDefinition = functionDefinition;
			this.delimiter = delimiter;
		}

		@Override
		public List<WebElement> findBy(final AppiumDriver webDriver, final FindByCoordinates findByCoordinates) {
			return filterElements(webDriver.findElements(new By() {

				@Override
				public List<WebElement> findElements(SearchContext context) {
					Object[] arguments = null;
					if (delimiter == null) {
						arguments = new Object[1];
						arguments[0] = findByCoordinates.criteria;
					} else {
						String[] splittedCriteria = findByCoordinates.criteria.split(delimiter);
						arguments = new Object[splittedCriteria.length];
						for (int i = 0; i < splittedCriteria.length; i++) {
							arguments[i] = splittedCriteria[i];
						}
					}
					Object o = ((JavascriptExecutor) webDriver).executeScript(functionDefinition, arguments);
					return toList(o);
				}

			}), findByCoordinates);
		}
	}
	
	public static String escapeXpathValue(String value) {
		if (value.contains("\"") && value.contains("'")) {
			String[] partsWoApos = value.split("'");
			return String.format("concat('%s')", Python.join("', \"'\", '", Arrays.asList(partsWoApos)));
		}
		if (value.contains("'")) {
			return String.format("\"%s\"", value);
		}
		return String.format("'%s'", value);
	}
}
