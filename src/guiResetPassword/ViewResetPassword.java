package guiResetPassword;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*******
 * <p> Title: ViewResetPassword Class. </p>
 *
 * <p> Description: The Java/FX-based page for resetting a user's password after
 * they have logged in with a one-time password. The user must set a new password
 * before they can access the system.</p>
 *
 */

public class ViewResetPassword {

	// These are the application values required by the user interface
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// GUI Elements
	protected static Label label_PageTitle = new Label("Set New Password");
	protected static Label label_Instructions = new Label(
			"Your password has been reset by an administrator.\n" +
			"Please enter a new password below.");
	protected static Label label_Username = new Label();

	protected static PasswordField text_Password1 = new PasswordField();
	protected static PasswordField text_Password2 = new PasswordField();
	protected static Button button_SetPassword = new Button("Set Password");
	protected static Button button_Quit = new Button("Quit");

	// Alerts
	protected static Alert alertPasswordMismatch = new Alert(AlertType.ERROR);
	protected static Alert alertPasswordEmpty = new Alert(AlertType.ERROR);
	protected static Alert alertPasswordInvalid = new Alert(AlertType.ERROR);
	protected static Alert alertPasswordSet = new Alert(AlertType.INFORMATION);

	// Class attributes
	private static ViewResetPassword theView;
	protected static Stage theStage;
	private static Pane theRootPane;
	protected static String theUsername;

	public static Scene theResetPasswordScene = null;


	/**********
	 * <p> Method: displayResetPassword(Stage ps, String username) </p>
	 *
	 * <p> Description: Entry point to display the Reset Password page.</p>
	 *
	 * @param ps the JavaFX Stage
	 * @param username the username of the user resetting their password
	 */
	public static void displayResetPassword(Stage ps, String username) {
		theStage = ps;
		theUsername = username;

		if (theView == null) theView = new ViewResetPassword();

		// Clear password fields
		text_Password1.setText("");
		text_Password2.setText("");

		// Update username display
		label_Username.setText("Username: " + username);

		// Set up the scene
		theRootPane.getChildren().clear();
		theRootPane.getChildren().addAll(
			label_PageTitle, label_Instructions, label_Username,
			text_Password1, text_Password2,
			button_SetPassword, button_Quit
		);

		theStage.setTitle("CSE 360 Foundation Code: Set New Password");
		theStage.setScene(theResetPasswordScene);
		theStage.show();
	}


	/**********
	 * <p> Constructor: ViewResetPassword() </p>
	 *
	 * <p> Description: Initializes all GUI elements.</p>
	 */
	private ViewResetPassword() {
		theRootPane = new Pane();
		theResetPasswordScene = new Scene(theRootPane, width, height);

		// Page title
		setupLabelUI(label_PageTitle, "Arial", 32, width, Pos.CENTER, 0, 20);

		// Instructions
		setupLabelUI(label_Instructions, "Arial", 18, width, Pos.CENTER, 0, 80);

		// Username display
		setupLabelUI(label_Username, "Arial", 20, width, Pos.CENTER, 0, 150);
		label_Username.setStyle("-fx-font-weight: bold;");

		// Password fields
		setupTextUI(text_Password1, "Arial", 18, 300, Pos.BASELINE_LEFT, 250, 220, true);
		text_Password1.setPromptText("Enter new password");

		setupTextUI(text_Password2, "Arial", 18, 300, Pos.BASELINE_LEFT, 250, 280, true);
		text_Password2.setPromptText("Confirm new password");

		// Set Password button
		setupButtonUI(button_SetPassword, "Dialog", 18, 200, Pos.CENTER, 300, 350);
		button_SetPassword.setOnAction((_) -> {ControllerResetPassword.doSetPassword();});

		// Quit button
		setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 275, 540);
		button_Quit.setOnAction((_) -> {ControllerResetPassword.performQuit();});

		// Alerts
		alertPasswordMismatch.setTitle("Passwords Do Not Match");
		alertPasswordMismatch.setHeaderText("The two passwords must be identical.");
		alertPasswordMismatch.setContentText("Please re-enter your passwords.");

		alertPasswordEmpty.setTitle("Password Required");
		alertPasswordEmpty.setHeaderText("Password cannot be empty.");
		alertPasswordEmpty.setContentText("Please enter a password.");

		alertPasswordSet.setTitle("Password Updated");
		alertPasswordSet.setHeaderText("Your password has been updated successfully.");
		alertPasswordSet.setContentText("Please log in with your new password.");
		
		alertPasswordInvalid.setTitle("Password is Invalid");
		alertPasswordInvalid.setHeaderText("");
		alertPasswordInvalid.setContentText("Please enter a different password.");
	}


	// Helper methods
	private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}

	private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}

	private void setupTextUI(PasswordField t, String ff, double f, double w, Pos p, double x, double y, boolean e) {
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);
		t.setEditable(e);
	}
}
