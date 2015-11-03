package com.github.mobile.appiumlibrary;

public class AppiumLibraryNonFatalException extends RuntimeException{
	/**
	 * Mark this exception as non fatal
	 */
	public static final boolean ROBOT_EXIT_ON_FAILURE = false;

	public AppiumLibraryNonFatalException() {
		super();
	}

	public AppiumLibraryNonFatalException(String string) {
		super(string);
	}

	public AppiumLibraryNonFatalException(Throwable t) {
		super(t);
	}

	public AppiumLibraryNonFatalException(String string, Throwable t) {
		super(string, t);
	}
}
