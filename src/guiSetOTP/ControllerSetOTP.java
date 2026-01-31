package guiSetOTP;

import java.util.UUID;
import database.Database;

/*******
 * <p> Title: ControllerSetOTP Class. </p>
 *
 * <p> Description: Controller for the Set One-Time Password page. Handles user
 * selection and OTP generation for password reset functionality.</p>
 *
 */

public class ControllerSetOTP {

	/**
	 * Default constructor is not used.
	 */
	public ControllerSetOTP() {
	}

	// Reference for the in-memory database
	private static Database theDatabase = applicationMain.FoundationsMain.database;


	/**********
	 * <p> Method: doSelectUser() </p>
	 *
	 * <p> Description: Handles user selection from the ComboBox. Updates the display
	 * to show information about the selected user.</p>
	 */
	protected static void doSelectUser() {
		String selectedUser = ViewSetOTP.combobox_SelectUser.getValue();

		if (selectedUser == null || selectedUser.equals("<Select a User>")) {
			ViewSetOTP.theSelectedUser = "";
			ViewSetOTP.label_SelectedUserInfo.setText("");
			ViewSetOTP.text_OTP.setText("");
			return;
		}

		ViewSetOTP.theSelectedUser = selectedUser;

		// Get user details
		if (theDatabase.getUserAccountDetails(selectedUser)) {
			String firstName = theDatabase.getCurrentFirstName();
			String lastName = theDatabase.getCurrentLastName();
			String email = theDatabase.getCurrentEmailAddress();

			String info = "Selected: " + selectedUser;
			if (firstName != null && !firstName.isEmpty()) {
				info += " (" + firstName;
				if (lastName != null && !lastName.isEmpty()) {
					info += " " + lastName;
				}
				info += ")";
			}
			if (email != null && !email.isEmpty()) {
				info += " - " + email;
			}

			ViewSetOTP.label_SelectedUserInfo.setText(info);
		}

		// Clear any previous OTP
		ViewSetOTP.text_OTP.setText("");
	}


	/**********
	 * <p> Method: generateOTP() </p>
	 *
	 * <p> Description: Generates a random one-time password for the selected user
	 * and stores it in the database.</p>
	 */
	protected static void generateOTP() {
		if (ViewSetOTP.theSelectedUser == null || ViewSetOTP.theSelectedUser.isEmpty()) {
			ViewSetOTP.alertNoUserSelected.showAndWait();
			return;
		}

		// Generate a random 8-character alphanumeric OTP
		String otp = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

		// Store in database
		theDatabase.setOneTimePassword(ViewSetOTP.theSelectedUser, otp);

		// Display to admin
		ViewSetOTP.text_OTP.setText(otp);

		// Show confirmation
		ViewSetOTP.alertOTPGenerated.setContentText(
				"One-time password for " + ViewSetOTP.theSelectedUser + " has been set to: " + otp +
				"\n\nShare this password with the user. They will be required to set a new password after logging in.");
		ViewSetOTP.alertOTPGenerated.showAndWait();

		System.out.println("One-time password set for user: " + ViewSetOTP.theSelectedUser + " OTP: " + otp);
	}


	/**********
	 * <p> Method: performReturn() </p>
	 *
	 * <p> Description: Returns to the Admin Home page.</p>
	 */
	protected static void performReturn() {
		guiAdminHome.ViewAdminHome.displayAdminHome(ViewSetOTP.theStage, ViewSetOTP.theUser);
	}


	/**********
	 * <p> Method: performLogout() </p>
	 *
	 * <p> Description: Logs out and returns to the login page.</p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewSetOTP.theStage);
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
