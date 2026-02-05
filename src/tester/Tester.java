package tester;

import recognizers.PasswordRecognizer;
import recognizers.UserNameRecognizer;




/**
 * Title: Tester class
 * 
 * Description: This class contains methods for testing several functionalities of the application including, but not limited to,
 * input validation.
 */
public class Tester {
	
	
	/**
	 * Default constructor is not used
	 */
	public Tester() {
		
	}
	
	// These integers track testing statistics
	public static int numPassed = 0;
	public static int numFailed = 0;
	
	
	/**********
	 * <p> Method: runTests() </p>
	 *
	 * <p> Description: Runs through a sequence of test cases, printing statistics afterwards.
	 * 	Information about what these tests target can be found in "TP1 Test Cases.pdf"
	 * </p>
	 */
	public static void runTests() {
		
		// Username Test Cases
		System.out.println("---Beginning Username Tests---");
		performUsernameTestCase(1, "UsernameIsTooLong", false);
		performUsernameTestCase(2, "Username12345678", true);
		performUsernameTestCase(3, "User_name", true);
		performUsernameTestCase(4, "User.name", true);
		performUsernameTestCase(5, "User-name", true);
		performUsernameTestCase(6, "User__name", false);
		performUsernameTestCase(7, "User..name", false);
		performUsernameTestCase(8, "User--name", false);
		performUsernameTestCase(9, "1user", false);
		performUsernameTestCase(10, "four", true);
		performUsernameTestCase(11, "ABC", false);
		performUsernameTestCase(12, "", false);
		performUsernameTestCase(13, "T-E_S.T", true);
		performUsernameTestCase(14, "T-_E_.S.-T", false);
		printStats();
		System.out.println("---End Username Tests---");
		
		// Password Validation (Length) Test Cases
		System.out.println("---Beginning Password Validation Tests---");
		performPasswordTestCase(1, "", false);
		performPasswordTestCase(2, "ArizonaState2026!", true);
		performPasswordTestCase(3, "A", false);
		performPasswordTestCase(4, "ArizonaState2026", false);
		performPasswordTestCase(5, "ArizonaState!", false);
		performPasswordTestCase(6, "02052026!", false);
		performPasswordTestCase(7, "arizonastate2026!", false);
		performPasswordTestCase(8, "ARIZONASTATE2026!", false);
		printStats();
		System.out.println("---End Password Validation Tests---");
		// Password Validation (Equality Comparison) Test Cases

//		// Invite Code (Length) Test Cases
//		System.out.println("---Beginning Invite Code (Length) Tests---");
//		printStats();
//		System.out.println("---End Invite Code (Length) Tests---");
//		// Invite Code (Generation and Invalidation) Test Cases
//		System.out.println("---Beginning Invite Code (Generation and Invalidation) Tests---");
//		printStats();
//		System.out.println("---End Invite Code (Generation and Invalidation) Tests---");
//		// Email Validation Test Cases
//		System.out.println("---Beginning Email Validation Tests---");
//		printStats();
//		System.out.println("---End Username Tests---");
//		// Name Validation Test Cases
//		System.out.println("---Beginning Name Validation Tests---");
//		printStats();
//		System.out.println("---End Name Validation Tests---");
		
		
	}
	
	
	
	
	/**
	 * This method prints the current testing statistics to console.
	 */
	public static void printStats() {
		System.out.println("Testing statistics:");
		System.out.println("Passed: "+ numPassed + "/" + (numPassed + numFailed));
		System.out.println("Failed: "+ numFailed + "/" + (numPassed + numFailed) );
	
	}
	
	
	
	
	/*
	 * This method sets up the input value for the Username test from the input parameters,
	 * displays test execution information, invokes precisely the same recognizer
	 * that the interactive JavaFX mainline uses, interprets the returned value,
	 * and displays the interpreted result.
	 */
	public static void performUsernameTestCase(int testCase, String inputUsername, Boolean expectedPass )
	{
		/************** Display an individual test case header **************/
		System.out.println("____________________________________________________________________________\n\nTest case: " + testCase);
		System.out.println("Input: \"" + inputUsername + "\"");
		System.out.println("______________");
		System.out.println("\nFinite state machine execution trace:");
		
		/************** Call the recognizer to process the input **************/
		String result = UserNameRecognizer.checkForValidUserName(inputUsername);
		
		if (result.isEmpty())
		{
			// username is valid
			if (expectedPass) {
				// username is valid, as expected (PASS)
				System.out.println("***Success*** The password <" + inputUsername + 
						"> is valid, so this is a pass!");
				numPassed++;
			} else {
				// username is valid, against expectations (FAIL)
				System.out.println("***Failure*** The username <" + inputUsername + "> is valid." + 
						"\nBut it was supposed to be invalid, so this is a failure!\n");
				System.out.println("Error message: " + result);
				numFailed++;
			}
		} else {
			// username is invalid
			if (expectedPass)
			{
				//
				// username is invalid, against expectations (FAIL)
				//
				System.out.println("***Failure*** The username <" + inputUsername + "> is invalid." + 
						"\nBut it was supposed to be valid, so this is a failure!\n");
				System.out.println("Error message: " + result);
				numFailed++;
			} else {
				//
				// username is invalid, as expected (PASS) 
				//
				System.out.println("***Success*** The username <" + inputUsername + "> is invalid." + 
						"\nBut it was supposed to be invalid, so this is a pass!\n");
				System.out.println("Error message: " + result);
				numPassed++;
			}
		}
	}
	
	/*
	 * This method sets up the input value for the test from the input parameters,
	 * displays test execution information, invokes precisely the same recognizer
	 * that the interactive JavaFX mainline uses, interprets the returned value,
	 * and displays the interpreted result.
	 */
	private static void performPasswordTestCase(int testCase, String inputText, boolean expectedPass) {
				
		/************** Display an individual test case header **************/
		System.out.println("____________________________________________________________________________\n\nTest case: " + testCase);
		System.out.println("Input: \"" + inputText + "\"");
		System.out.println("______________");
		System.out.println("\nFinite state machine execution trace:");
		
		/************** Call the recognizer to process the input **************/
		String resultText = PasswordRecognizer.evaluatePassword(inputText);
		
		/************** Interpret the result and display that interpreted information **************/
		System.out.println();
		
		// If the resulting text is empty, the recognizer accepted the input
		if (resultText != "") {
			 // If the test case expected the test to pass then this is a failure
			if (expectedPass) {
				System.out.println("***Failure*** The password <" + inputText + "> is invalid." + 
						"\nBut it was supposed to be valid, so this is a failure!\n");
				System.out.println("Error message: " + resultText);
				numFailed++;
			}
			// If the test case expected the test to fail then this is a success
			else {			
				System.out.println("***Success*** The password <" + inputText + "> is invalid." + 
						"\nBut it was supposed to be invalid, so this is a pass!\n");
				System.out.println("Error message: " + resultText);
				numPassed++;
			}
		}
		
		// If the resulting text is empty, the recognizer accepted the input
		else {	
			// If the test case expected the test to pass then this is a success
			if (expectedPass) {	
				System.out.println("***Success*** The password <" + inputText + 
						"> is valid, so this is a pass!");
				numPassed++;
			}
			// If the test case expected the test to fail then this is a failure
			else {
				System.out.println("***Failure*** The password <" + inputText + 
						"> was judged as valid" + 
						"\nBut it was supposed to be invalid, so this is a failure!");
				numFailed++;
			}
		}
		displayPasswordEvaluation();
	}
	
	private static void displayPasswordEvaluation() {
		
		if (PasswordRecognizer.foundUpperCase)
			System.out.println("At least one upper case letter - Satisfied");
		else
			System.out.println("At least one upper case letter - Not Satisfied");

		if (PasswordRecognizer.foundLowerCase)
			System.out.println("At least one lower case letter - Satisfied");
		else
			System.out.println("At least one lower case letter - Not Satisfied");
	

		if (PasswordRecognizer.foundNumericDigit)
			System.out.println("At least one digit - Satisfied");
		else
			System.out.println("At least one digit - Not Satisfied");

		if (PasswordRecognizer.foundSpecialChar)
			System.out.println("At least one special character - Satisfied");
		else
			System.out.println("At least one special character - Not Satisfied");

		if (PasswordRecognizer.foundLongEnough)
			System.out.println("At least 8 characters - Satisfied");
		else
			System.out.println("At least 8 characters - Not Satisfied");
	}
	
	
}