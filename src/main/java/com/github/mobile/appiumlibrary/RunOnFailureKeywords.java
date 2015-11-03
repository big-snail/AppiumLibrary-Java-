package com.github.mobile.appiumlibrary;

public interface RunOnFailureKeywords {

	/**
	 * This method is called by the
	 * com.github.mobile.appiumlibrary.aspects.RunOnFailureAspect in
	 * case a exception is thrown in one of the public methods of a keyword
	 * class.
	 */
	void runOnFailureByAspectJ();

}
