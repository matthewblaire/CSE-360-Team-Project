package guiFirstAdmin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.Scene;


/*******
 * <p> Title: ViewFirstAdmin Class</p>
 *
 * <p> Description: The FirstAdmin Page View.  This class is used to require the very first user of
 * the system to specify an Admin Username and Password when there is no database for the
 * application.  This avoids the common weakness practice of hard coding credentials into the
 * application.</p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Lynn Robert Carter
 * @author Gerald Jones II
 *
 * @version 1.00		2025-08-15 Initial version
 * @version 2.00		2026-02-07 Front-end GUI overhaul
 *
 */

public class ViewFirstAdmin {

	/*-********************************************************************************************

	Attributes

	 */

	// These are the application values required by the user interface

	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// These are the widget attributes for the GUI

	private static Label label_ApplicationTitle = new Label("Administrator Account Setup");
	private static Label label_Subtitle =
			new Label("Welcome! Since no accounts exist yet, please create the first "
					+ "administrator account to get started.");

	protected static Label label_ErrorInfo = new Label();

	protected static TextField text_AdminUsername = new TextField();
	protected static PasswordField text_AdminPassword1 = new PasswordField();
	protected static PasswordField text_AdminPassword2 = new PasswordField();

	protected static TextField text_AdminFirstName = new TextField();
	protected static TextField text_AdminLastName = new TextField();

	private static Button button_AdminSetup = new Button("Create Account");

	// This alert is used should the user enter two passwords that do not match
	protected static Alert alertUsernamePasswordError = new Alert(AlertType.INFORMATION);

	// This button allow the user to abort creating the first admin account and terminate
	private static Button button_Quit = new Button("Quit");

	// These attributes are used to configure the page and populate it with this user's information
	protected static Stage theStage;
	private static Scene theFirstAdminScene = null;
	private static final int theRole = 1;		// Admin: 1; Student: 2; Staff: 3

	// This alert is used should the user enter an invalid password
	protected static Alert alertPasswordError = new Alert(AlertType.INFORMATION);

	/*-********************************************************************************************

	Constructor

	 */

	/**********
	 * <p> Method: public displayFirstAdmin(Stage ps) </p>
	 *
	 * <p> Description: This method is called when the application first starts. It create an
	 * an instance of the View class.
	 *
	 * NOTE: As described below, this code does not implement MVC using the singleton pattern used
	 * by most of the pages as the code is written this way because we know with certainty that it
	 * will only be called once.  For this reason, we directly call the private class constructor.
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 */
	public static void displayFirstAdmin(Stage ps) {

		// Establish the references to the GUI.  There is no user yet.
		theStage = ps;			// Establish a reference to the JavaFX Stage

		// This page is only called once so there is no need to save the reference to it
		new ViewFirstAdmin();	// Create an instance of the class

		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.
		applicationMain.FoundationsMain.activeHomePage = theRole;	// 1: Admin; 2: Student; 3: Staff

		// Set the title for the window, display the page, and wait for the Admin to do something
		theStage.setTitle("CSE 360 Project: First User Account Setup");
		theStage.setScene(theFirstAdminScene);
		theStage.show();
	}

	/**********
	 * <p> Method: private ViewFirstAdmin() </p>
	 *
	 * <p> Description: This constructor is called when the application first starts. It must
	 * handle when no user has been established.  It is never called again, either during the first
	 * run of the program after reboot or during normal operations of the program.  (This page is
	 * only used when there is no database for the application or there is a database, but there
	 * no users in the database.
	 *
	 * If there is no database or there are no users, it means that the person starting the system
	 * is an administrator, so a special GUI is provided to allow this Admin to create an account
	 * by setting a username and password.
	 *
	 * If there is at least one user, this page is not called.  Since this is only used once when
	 * the system is being set up, this MVC code does not use a singleton protocol.  For this
	 * reason, do not use this as a typical MVC pattern.</p>
	 *
	 */
	private ViewFirstAdmin() {

		// Main vertical container
		VBox mainContainer = new VBox(18);
		mainContainer.setAlignment(Pos.TOP_CENTER);
		mainContainer.setPadding(new Insets(25, 50, 25, 50));
		theFirstAdminScene = new Scene(mainContainer, width, height);

		// --- Title Section ---
		label_ApplicationTitle.setFont(Font.font("Arial", FontWeight.BOLD, 28));
		label_ApplicationTitle.setAlignment(Pos.CENTER);

		label_Subtitle.setFont(Font.font("Arial", 14));
		label_Subtitle.setAlignment(Pos.CENTER);
		label_Subtitle.setWrapText(true);
		label_Subtitle.setStyle("-fx-text-fill: #555555;");

		// --- Login Credentials Section ---
		VBox credentialsSection = createSection("Login Credentials");

		HBox usernameRow = createFieldRow("Username", text_AdminUsername, "Choose a username");
		text_AdminUsername.textProperty().addListener((_, _, _)
				-> {ControllerFirstAdmin.setAdminUsername(); });

		HBox password1Row = createFieldRow("Password", text_AdminPassword1, "Enter a password");
		text_AdminPassword1.textProperty().addListener((_, _, _)
				-> {ControllerFirstAdmin.setAdminPassword1(); });

		HBox password2Row = createFieldRow("Confirm Password", text_AdminPassword2,
				"Re-enter your password");
		text_AdminPassword2.textProperty().addListener((_, _, _)
				-> {ControllerFirstAdmin.setAdminPassword2(); });

		credentialsSection.getChildren().addAll(usernameRow, password1Row, password2Row);

		// --- Personal Information Section ---
		VBox personalSection = createSection("Personal Information");

		HBox firstNameRow = createFieldRow("First Name", text_AdminFirstName,
				"Enter your first name");
		text_AdminFirstName.textProperty().addListener((_, _, _)
				-> {ControllerFirstAdmin.setAdminFirstName(); });

		HBox lastNameRow = createFieldRow("Last Name", text_AdminLastName,
				"Enter your last name");
		text_AdminLastName.textProperty().addListener((_, _, _)
				-> {ControllerFirstAdmin.setAdminLastName(); });

		personalSection.getChildren().addAll(firstNameRow, lastNameRow);

		// --- Error Label ---
		label_ErrorInfo.setFont(Font.font("Arial", 14));
		label_ErrorInfo.setStyle("-fx-text-fill: red;");
		label_ErrorInfo.setAlignment(Pos.CENTER);
		label_ErrorInfo.setMinWidth(width - 100);
		label_ErrorInfo.setWrapText(true);

		// --- Buttons ---
		button_AdminSetup.setFont(Font.font("Dialog", 16));
		button_AdminSetup.setMinWidth(160);
		button_AdminSetup.setStyle(
				"-fx-background-color: #4a90d9; -fx-text-fill: white; "
				+ "-fx-background-radius: 5; -fx-cursor: hand;");
		button_AdminSetup.setOnAction((_) -> {
			ControllerFirstAdmin.doSetupAdmin(theStage, 1);
		});

		button_Quit.setFont(Font.font("Dialog", 16));
		button_Quit.setMinWidth(120);
		button_Quit.setStyle("-fx-background-radius: 5; -fx-cursor: hand;");
		button_Quit.setOnAction((_) -> {ControllerFirstAdmin.performQuit(); });

		HBox buttonRow = new HBox(20);
		buttonRow.setAlignment(Pos.CENTER);
		buttonRow.setPadding(new Insets(5, 0, 0, 0));
		buttonRow.getChildren().addAll(button_AdminSetup, button_Quit);

		// Password error alert setup
		alertPasswordError.setTitle("Password Error");
		alertPasswordError.setHeaderText("Password Error");

		// Place all sections into the main container
		mainContainer.getChildren().addAll(
				label_ApplicationTitle,
				label_Subtitle,
				credentialsSection,
				personalSection,
				label_ErrorInfo,
				buttonRow);
	}



	/*-********************************************************************************************

	Helper methods to reduce code length

	 */

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
