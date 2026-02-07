package guiUserLogin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import recognizers.InviteCodeRecognizer;


/*******
 * <p> Title: GUIStartupPage Class. </p>
 *
 * <p> Description: The Java/FX-based System Startup Page.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Lynn Robert Carter
 * @author Gerald Jones II
 *
 * @version 1.00		2025-04-20 Initial version
 * @version 2.00		2026-02-07 Front-end GUI overhaul
 *
 */

public class ViewUserLogin {

	/*-********************************************************************************************

	Attributes

	 *********************************************************************************************/

	// These are the application values required by the user interface

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	private static Label label_ApplicationTitle = new Label("User Login");
	private static Label label_Subtitle = new Label(
			"Sign in to your account or use an invitation code to create a new one.");

	protected static Alert alertUsernamePasswordError = new Alert(AlertType.INFORMATION);

	// Login fields
	protected static TextField text_Username = new TextField();
	protected static PasswordField text_Password = new PasswordField();
	private static Button button_Login = new Button("Log In");

	// Invitation fields
	private static TextField text_Invitation = new TextField();
	private static Button button_SetupAccount = new Button("Set Up Account");

	private static Button button_Quit = new Button("Quit");

	private static Stage theStage;
	public static Scene theUserLoginScene = null;

	private static ViewUserLogin theView = null;


	/*-********************************************************************************************

	Constructor

	 *********************************************************************************************/

	public static void displayUserLogin(Stage ps) {

		// Establish the references to the GUI. There is no current user yet.
		theStage = ps;

		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewUserLogin();

		// Reset all input fields from the last use
		text_Username.setText("");
		text_Password.setText("");
		text_Invitation.setText("");

		// Set the title for the window, display the page, and wait for the user to do something
		theStage.setTitle("CSE 360 Project: User Login Page");
		theStage.setScene(theUserLoginScene);
		theStage.show();
	}

	/**********
	 * <p> Method: ViewUserLoginPage() </p>
	 *
	 * <p> Description: This method is called when the application first starts. It must handle
	 * two cases: 1) when no has been established and 2) when one or more users have been
	 * established.
	 *
	 * If there are no users in the database, this means that the person starting the system must
	 * be an administrator, so a special GUI is provided to allow this Admin to set a username and
	 * password.
	 *
	 * If there is at least one user, then a different display is shown for existing users to login
	 * and for potential new users to provide an invitation code and if it is valid, they are taken
	 * to a page where they can specify a username and password.</p>
	 *
	 */
	private ViewUserLogin() {

		// Main vertical container
		VBox mainContainer = new VBox(18);
		mainContainer.setAlignment(Pos.TOP_CENTER);
		mainContainer.setPadding(new Insets(25, 50, 25, 50));
		theUserLoginScene = new Scene(mainContainer, width, height);

		// --- Title Section ---
		label_ApplicationTitle.setFont(Font.font("Arial", FontWeight.BOLD, 28));
		label_ApplicationTitle.setAlignment(Pos.CENTER);

		label_Subtitle.setFont(Font.font("Arial", 14));
		label_Subtitle.setAlignment(Pos.CENTER);
		label_Subtitle.setWrapText(true);
		label_Subtitle.setStyle("-fx-text-fill: #555555;");

		// --- Sign In Section ---
		VBox loginSection = createSection("Sign In");

		HBox usernameRow = createFieldRow("Username", text_Username, "Enter your username");
		HBox passwordRow = createFieldRow("Password", text_Password, "Enter your password");

		button_Login.setFont(Font.font("Dialog", 16));
		button_Login.setMinWidth(160);
		button_Login.setStyle(
				"-fx-background-color: #4a90d9; -fx-text-fill: white; "
				+ "-fx-background-radius: 5; -fx-cursor: hand;");
		button_Login.setOnAction((_) -> {ControllerUserLogin.doLogin(theStage); });

		HBox loginButtonRow = new HBox();
		loginButtonRow.setAlignment(Pos.CENTER);
		loginButtonRow.setPadding(new Insets(5, 0, 0, 0));
		loginButtonRow.getChildren().add(button_Login);

		alertUsernamePasswordError.setTitle("Invalid username/password!");
		alertUsernamePasswordError.setHeaderText(null);

		loginSection.getChildren().addAll(usernameRow, passwordRow, loginButtonRow);

		// --- Divider ---
		Label dividerLabel = new Label("or");
		dividerLabel.setFont(Font.font("Arial", 14));
		dividerLabel.setStyle("-fx-text-fill: #999999;");
		dividerLabel.setAlignment(Pos.CENTER);

		// --- Account Setup Section ---
		VBox invitationSection = createSection("New User? Set Up an Account");

		HBox invitationRow = createFieldRow("Invitation Code", text_Invitation,
				"Enter your 6-character code");

		button_SetupAccount.setFont(Font.font("Dialog", 16));
		button_SetupAccount.setMinWidth(160);
		button_SetupAccount.setStyle(
				"-fx-background-color: #4a90d9; -fx-text-fill: white; "
				+ "-fx-background-radius: 5; -fx-cursor: hand;");
		button_SetupAccount.setOnAction((_) -> {
			// Verify that the code is the correct format
			String inviteEval = InviteCodeRecognizer.evaluateInviteCode(
					text_Invitation.getText());

			if (inviteEval != "") {
				// invite code is malformed
				alertUsernamePasswordError.setTitle("Invalid invite code!");
				alertUsernamePasswordError.setHeaderText(inviteEval);
				alertUsernamePasswordError.showAndWait();
				return;
			}

			System.out.println("**** Calling doSetupAccount");
			ControllerUserLogin.doSetupAccount(theStage, text_Invitation.getText());
		});

		HBox setupButtonRow = new HBox();
		setupButtonRow.setAlignment(Pos.CENTER);
		setupButtonRow.setPadding(new Insets(5, 0, 0, 0));
		setupButtonRow.getChildren().add(button_SetupAccount);

		invitationSection.getChildren().addAll(invitationRow, setupButtonRow);

		// --- Quit Button ---
		button_Quit.setFont(Font.font("Dialog", 16));
		button_Quit.setMinWidth(120);
		button_Quit.setStyle("-fx-background-radius: 5; -fx-cursor: hand;");
		button_Quit.setOnAction((_) -> {ControllerUserLogin.performQuit(); });

		HBox quitRow = new HBox();
		quitRow.setAlignment(Pos.CENTER);
		quitRow.setPadding(new Insets(5, 0, 0, 0));
		quitRow.getChildren().add(button_Quit);

		// Place all sections into the main container
		mainContainer.getChildren().addAll(
				label_ApplicationTitle,
				label_Subtitle,
				loginSection,
				dividerLabel,
				invitationSection,
				quitRow);
	}


	/*-********************************************************************************************

	Helper methods to reduce code length

	 *********************************************************************************************/

	/**********
	 * Creates a styled section box with a title label, a light background, and a rounded border.
	 *
	 * @param title		The section heading text
	 * @return a VBox ready to have field rows added as children
	 */
	private VBox createSection(String title) {
		VBox section = new VBox(10);
		section.setPadding(new Insets(15, 20, 15, 20));
		section.setStyle(
				"-fx-border-color: #cccccc; "
				+ "-fx-border-radius: 8; "
				+ "-fx-background-radius: 8; "
				+ "-fx-background-color: #f8f8f8;");

		Label sectionTitle = new Label(title);
		sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
		sectionTitle.setStyle("-fx-text-fill: #333333;");
		section.getChildren().add(sectionTitle);

		return section;
	}

	/**********
	 * Creates a horizontal row containing a right-aligned label and a text field.
	 *
	 * @param labelText		The text for the label
	 * @param field			The TextField or PasswordField to display
	 * @param prompt		The placeholder prompt text for the field
	 * @return an HBox containing the label and field
	 */
	private HBox createFieldRow(String labelText, TextField field, String prompt) {
		HBox row = new HBox(15);
		row.setAlignment(Pos.CENTER_LEFT);

		Label label = new Label(labelText);
		label.setFont(Font.font("Arial", 14));
		label.setMinWidth(140);
		label.setAlignment(Pos.CENTER_RIGHT);

		field.setFont(Font.font("Arial", 14));
		field.setMinWidth(300);
		field.setMaxWidth(300);
		field.setPromptText(prompt);

		row.getChildren().addAll(label, field);
		return row;
	}
}
