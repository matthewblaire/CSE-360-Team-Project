package guiStaffRequest;

import java.sql.SQLException;

import database.Database;
import recognizers.PostContentRecognizer;
import recognizers.TitleRecognizer;

/*******
 * <p> Title: ControllerStaffRequest Class. </p>
 *
 * <p> Description: The Java/FX-based Staff Request Submission Page.  This class provides the
 * controller actions based on the user's use of the JavaFX GUI widgets defined by the View class.
 *
 * Staff users fill in a title and description, then press Submit to create a new OPEN request
 * that all admins and staff can view in the Request Queue.
 *
 * The class has been written assuming that the View or the Model are the only classes that can
 * invoke these methods.  This is why each method has been declared as protected.  Do not change
 * any of these methods to public. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author CSE 360 Team
 *
 * @version 1.00	2026-04-09	Initial version
 */
public class ControllerStaffRequest {

	/*-*******************************************************************************************

	User Interface Actions for this page

	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and
	the Model is often just a stub, or will be a singleton instantiated object.

	 */

	/**
	 * Default constructor is not used.
	 */
	public ControllerStaffRequest() {
	}

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;


	/**********
	 * <p> Method: performSubmit() </p>
	 *
	 * <p> Description: Validates the title and description fields and, if valid, inserts a new
	 * OPEN request into the database on behalf of the currently logged-in staff user.  On success
	 * the form fields are cleared and the user is shown a confirmation alert.  On validation
	 * failure the inline error label is updated with the first error found. </p>
	 */
	protected static void performSubmit() {
		String title       = ViewStaffRequest.textField_Title.getText().trim();
		String description = ViewStaffRequest.textarea_Description.getText().trim();

		// Validate title
		String titleError = TitleRecognizer.evaluateTitle(title);
		if (!titleError.isEmpty()) {
			ViewStaffRequest.label_ErrorFeedback.setText(titleError);
			return;
		}

		// Validate description
		String descError = PostContentRecognizer.evaluatePostContent(description);
		if (!descError.isEmpty()) {
			ViewStaffRequest.label_ErrorFeedback.setText(descError);
			return;
		}

		// Clear any previous error
		ViewStaffRequest.label_ErrorFeedback.setText("");

		// Persist the request
		String severity = ViewStaffRequest.combobox_Severity.getValue();
		try {
			theDatabase.submitStaffRequest(ViewStaffRequest.theUser.getUserName(), title, description, severity);
		} catch (SQLException e) {
			ViewStaffRequest.label_ErrorFeedback.setText("Database error: " + e.getMessage());
			return;
		}

		// Success: clear the form and notify the user
		ViewStaffRequest.textField_Title.clear();
		ViewStaffRequest.textarea_Description.clear();
		ViewStaffRequest.label_CharCount.setText("0 / 2000");
		ViewStaffRequest.label_CharCount.setStyle("-fx-text-fill: black;");

		ViewStaffRequest.alertSuccess.setHeaderText("Request Submitted");
		ViewStaffRequest.alertSuccess.setContentText(
				"Your request has been added to the queue and will be reviewed by an admin.");
		ViewStaffRequest.alertSuccess.showAndWait();
	}


	/**********
	 * <p> Method: doUpdateCharCount(String newVal) </p>
	 *
	 * <p> Description: Updates the live character-count label beneath the description TextArea.
	 * The count turns red when the 2000-character limit is exceeded. </p>
	 *
	 * @param newVal the current text value from the description TextArea
	 */
	protected static void doUpdateCharCount(String newVal) {
		int len = newVal.length();
		ViewStaffRequest.label_CharCount.setText(len + " / 2000");
		if (len > 2000) {
			ViewStaffRequest.label_CharCount.setStyle("-fx-text-fill: red;");
		} else {
			ViewStaffRequest.label_CharCount.setStyle("-fx-text-fill: black;");
		}
	}


	/**********
	 * <p> Method: performViewQueue() </p>
	 *
	 * <p> Description: Navigates to the Request Queue page where all staff and admin users can
	 * view the full list of pending and resolved requests. </p>
	 */
	protected static void performViewQueue() {
		guiRequestQueue.ViewRequestQueue.displayRequestQueue(
				ViewStaffRequest.theStage, ViewStaffRequest.theUser);
	}


	/**********
	 * <p> Method: performReturn() </p>
	 *
	 * <p> Description: Returns the user to the Staff Home page. </p>
	 */
	protected static void performReturn() {
		guiStaff.ViewStaffHome.displayStaffHome(
				ViewStaffRequest.theStage, ViewStaffRequest.theUser);
	}


	/**********
	 * <p> Method: performLogout() </p>
	 *
	 * <p> Description: Logs out the current user and returns to the login page. </p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewStaffRequest.theStage);
	}


	/**********
	 * <p> Method: performQuit() </p>
	 *
	 * <p> Description: Terminates the application. </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}
