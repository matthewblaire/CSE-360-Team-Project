package guiResetPassword;

import database.Database;


/*******
 * <p> Title: ControllerResetPassword Class. </p>
 *
 * <p> Description: Controller for the Reset Password page. Handles password
 * validation and updating the user's password after they logged in with a
 * one-time password.</p>
 *
 */

public class ControllerResetPassword {

	/**
	 * Default constructor is not used.
	 */
	public ControllerResetPassword() {
	}

	// Reference for the in-memory database
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	
	
	


	/**********
	 * <p> Method: doSetPassword() </p>
	 *
	 * <p> Description: Validates and sets the new password for the user.
	 * After successful password change, clears the one-time password and
	 * redirects to the login page.</p>
	 */
	protected static void doSetPassword() {
		String password1 = ViewResetPassword.text_Password1.getText();
		String password2 = ViewResetPassword.text_Password2.getText();

		// Validate password is not empty
		if (password1 == null || password1.isEmpty()) {
			ViewResetPassword.alertPasswordEmpty.showAndWait();
			return;
		}

		// Validate passwords match
		if (!password1.equals(password2)) {
			ViewResetPassword.alertPasswordMismatch.showAndWait();
			return;
		}

		// Update the password in the database
		theDatabase.updatePassword(ViewResetPassword.theUsername, password1);

		// Clear the one-time password so it cannot be reused
		theDatabase.clearOneTimePassword(ViewResetPassword.theUsername);

		System.out.println("Password updated for user: " + ViewResetPassword.theUsername);

		// Show success message
		ViewResetPassword.alertPasswordSet.showAndWait();

		// Redirect to login page
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewResetPassword.theStage);
	}


	/**********
	 * <p> Method: performQuit() </p>
	 *
	 * <p> Description: Exits the application.</p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}
