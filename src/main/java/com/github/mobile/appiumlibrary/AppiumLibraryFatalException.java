package com.github.mobile.appiumlibrary;

@SuppressWarnings("serial")
public class AppiumLibraryFatalException extends RuntimeException{
	/**
	 * Mark this exception as fatal
	 */
	public static final boolean ROBOT_EXIT_ON_FAILURE = true;

	public AppiumLibraryFatalException() {
		super();
	}

	public AppiumLibraryFatalException(String string) {
		super(string);
	}

	public AppiumLibraryFatalException(Throwable t) {
		super(t);
	}

	public AppiumLibraryFatalException(String string, Throwable t) {
		super(string, t);
	}
}
