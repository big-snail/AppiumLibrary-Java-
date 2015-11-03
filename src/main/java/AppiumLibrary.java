

import java.net.MalformedURLException;

import org.robotframework.javalib.annotation.Autowired;
import org.robotframework.javalib.library.AnnotationLibrary;

import com.github.mobile.appiumlibrary.keywords.AndroidUtils;
import com.github.mobile.appiumlibrary.keywords.ApplicationManagement;
import com.github.mobile.appiumlibrary.keywords.Element;
import com.github.mobile.appiumlibrary.keywords.KeyEvent;
import com.github.mobile.appiumlibrary.keywords.Logging;
import com.github.mobile.appiumlibrary.keywords.RunOnFailure;
import com.github.mobile.appiumlibrary.keywords.Screenshot;
import com.github.mobile.appiumlibrary.keywords.Touch;
import com.github.mobile.appiumlibrary.keywords.Waiting;
import com.github.mobile.appiumlibrary.utils.Javadoc2Libdoc;

/**
 * AppiumLibrary is a mobile testing library for the Robot Framework and was
 * originally written in Python. This is the Java port of the Apppium
 *  Python library for Robot Framework. It uses the Appium
 *   libraries internally to control a web browser. See <a
 * href="http://appium.io/">Appium</a> for more
 * information on Appium. It runs tests in a Simulator/real mobile phone and can be used with the
 * Jython interpreter or any other Java application.<br>
 * <br>
 * <font size="+1"><b>Before running tests</b></font><br>
 * Prior to running test cases using AppiumLibrary, the library must be
 * imported into your Robot Framework test suite (see importing section), and
 * the `Open Application` keyword must be used to open a app to the desired
 * location.<br>
 * <br>
 * <font size="+1"><b>Locating elements</b></font><br>
 * All keywords in AppiumLibrary that need to find an element on the page
 * take an locator argument.<br>
 * <br>
 * <b>Key attributes</b><br>
 * By default, when a locator value is provided, it is matched against the key
 * attributes of the particular element type. The attributes <i>id</i> and
 * <i>name</i> are key attributes to all elements.<br>
 * <br>
 * List of key attributes:
 * <table border="1" cellspacing="0">
 * <tr>
 * <td><b>Element Type</b></td>
 * <td><b>Key Attributes</b></td>
 * </tr>
 * <tr>
 * <td>A</td>
 * <td>@id,@name,@href,text</td>
 * </tr>
 * <tr>
 * <td>IMG</td>
 * <td>@id,@name,@src,@alt</td>
 * </tr>
 * <tr>
 * <td>INPUT</td>
 * <td>@id,@name,@value,@src</td>
 * </tr>
 * <tr>
 * <td>BUTTON</td>
 * <td>@id,@name,@value,text</td>
 * </tr>
 * <tr>
 * <td>*</td>
 * <td>@id,@name</td>
 * </tr>
 * </table>
 * <br>
 * Example:
 * <table border="1" cellspacing="0">
 * <tr>
 * <td>Click Element</td>
 * <td>my_element</td>
 * </tr>
 * </table>
 * <br>
 * <b>Locator strategies</b><br>
 * It is also possible to specify the approach Selenium2Library should take to
 * find an element by specifying a locator strategy with a locator prefix.<br>
 * <br>
 * Supported strategies are:
 * <table border="1" cellspacing="0">
 * <tr>
 * <td><b>Strategy</b></td>
 * <td><b>Example</b></td>
 * <td><b>Description</b></td>
 * </tr>
 * <tr>
 * <td>identifier</td>
 * <td>Click Element | identifier=my_element</td>
 * <td>Matches by @id or @name attribute</td>
 * </tr>
 * <tr>
 * <td>id</td>
 * <td>Click Element | id=my_element</td>
 * <td>Matches by @id attribute</td>
 * </tr>
 * <tr>
 * <td>name</td>
 * <td>Click Element | name=my_element</td>
 * <td>Matches by @name attribute</td>
 * </tr>
 * <tr>
 * <td>xpath</td>
 * <td>Click Element | xpath=//div[@id='my_element']</td>
 * <td>Matches by arbitrary XPath expression</td>
 * </tr>
 * <tr>
 * <td>class</td>
 * <td>Click Element | class=UIAPickerWheel</td>
 * <td>Matches by class</td>
 * </tr>
 * <tr>
 * <td>accessibility_id</td>
 * <td>Click Element | accessibility_id=t</td>
 * <td>Accessibility options utilize.</td>
 * </tr>
 * <tr>
 * <td>android</td>
 * <td>Click Element | android=new UiSelector().description('Apps')</td>
 * <td>Matches by Android UI Automator</td>
 * </tr>
 * <tr>
 * <td>ios</td>
 * <td>Click Element |  ios=.buttons().withName('Apps')</td>
 * <td>Matches by iOS UI Automation</td>
 * </tr>
 * <tr>
 * <td>css</td>
 * <td>Click Element | css=.green_button</td>
 * <td>Matches by css in webview</td>
 * </tr>
 * </table>
 * <br>
 * List of log levels:
 * <table border="1" cellspacing="0">
 * <tr>
 * <td><b>Log Level</b></td>
 * <td><b>Description</b></td>
 * </tr>
 * <tr>
 * <td>DEBUG</td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>INFO</td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>HTML</td>
 * <td>Same as INFO, but message is in HTML format</td>
 * </tr>
 * <tr>
 * <td>TRACE</td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>WARN</td>
 * <td></td>
 * </tr>
 * </table>
 */
public class AppiumLibrary extends AnnotationLibrary {
	/**
	 * The list of keyword patterns for the AnnotationLibrary
	 */
	public static final String KEYWORD_PATTERN = "com/github/mobile/appiumlibrary/keywords/**/*.class";

	/**
	 * The javadoc to libdoc converter
	 */
	public static final Javadoc2Libdoc JAVADOC_2_LIBDOC = new Javadoc2Libdoc(AppiumLibrary.class);

	/**
	 * The library documentation is written in HTML
	 */
	public static final String ROBOT_LIBRARY_DOC_FORMAT = "HTML";

	/**
	 * The scope of this library is global.
	 */
	public static final String ROBOT_LIBRARY_SCOPE = "GLOBAL";
	
	public AppiumLibrary(){
		this("5.0");
	}
	
	public AppiumLibrary(String timeout){
		this(timeout, "Capture Page Screenshot");
	}
	
	/**
	 * AppiumLibrary is extended for Robot Framework to support Andorid & IOS automation test
	 */
	public AppiumLibrary(String timeout, String runOnFailure){
		super();
		addKeywordPattern(KEYWORD_PATTERN);
		createKeywordFactory();
		applicationManagement.setTimeout(timeout);
		this.runOnFailure.registerKeywordToRunOnFailure(runOnFailure);
	}
	
	@Autowired
	protected AndroidUtils androidUtils;
	
	@Autowired
	protected ApplicationManagement applicationManagement;
	
	@Autowired
	protected Element element;
	
	@Autowired
	protected KeyEvent keyEvent;
	
	@Autowired
	protected Logging logging;
	
	@Autowired
	protected RunOnFailure runOnFailure;
	
	@Autowired
	protected Screenshot screenshot;
	
	@Autowired
	protected Touch touch;
	
	@Autowired
	protected Waiting waiting;

	public AndroidUtils getAndroidUtils() {
		return androidUtils;
	}

	public ApplicationManagement getApplicationManagement() {
		return applicationManagement;
	}

	public Element getElement() {
		return element;
	}

	public KeyEvent getKeyEvent() {
		return keyEvent;
	}

	public Logging getLogging() {
		return logging;
	}

	public RunOnFailure getRunOnFailure() {
		return runOnFailure;
	}

	public Screenshot getScreenshot() {
		return screenshot;
	}

	public Touch getTouch() {
		return touch;
	}

	public Waiting getWaiting() {
		return waiting;
	}
	
	@Override
	public String getKeywordDocumentation(String keywordName) {
		String keywordDocumentation = JAVADOC_2_LIBDOC.getKeywordDocumentation(keywordName);
		if (keywordDocumentation == null) {
			try {
				return super.getKeywordDocumentation(keywordName);
			} catch (NullPointerException e) {
				return "";
			}
		}
		return keywordDocumentation;
	}
	
	public static void main(String[] args){
		AppiumLibrary appiumLibrary = new AppiumLibrary();
		try {
			appiumLibrary.applicationManagement.openApplication("http://localhost:4723/wd/hub", "autoTest", "platformName=Android,deviceName=Android Emulator,browserName=Chrome");
			appiumLibrary.applicationManagement.goToUrl("http://www.baidu.com");
			appiumLibrary.applicationManagement.closeApplication();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
