package guiStaffRequest;

import database.Database;
import entityClasses.User;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*******
 * <p> Title: ViewStaffRequest Class. </p>
 *
 * <p> Description: The View component of the Staff Request Submission MVC triplet.  This
 * singleton page allows a logged-in staff user to compose and submit a request for a specific
 * administrator action.  The submitted request is stored as an OPEN row in the Requests table
 * and becomes immediately visible on the Request Queue page to all admins and staff.
 *
 * Layout (800 × 600):
 * <ul>
 *   <li>Area 1 — page title, username label, Return button, horizontal rule at y = 95.</li>
 *   <li>Area 2 — title field, description TextArea with live character count, inline error
 *       label, Submit Request button, and View Queue button.</li>
 *   <li>Area 3 — horizontal rule at y = 525, Logout and Quit buttons.</li>
 * </ul>
 *
 * Static widget layout is performed once in the private constructor.  Dynamic data (username
 * label, field resets) is refreshed on every call to
 * {@link #displayStaffRequest(Stage, User)}.
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author CSE 360 Team
 *
 * @version 1.00	2026-04-09	Initial version
 */
public class ViewStaffRequest {

	/*-*******************************************************************************************

	Attributes

	 */

	// Window dimensions from the shared application constants
	private static double width  = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;


	// ---- Area 1: Header ----

	/** Page title label. */
	protected static Label label_PageTitle   = new Label("Submit an Admin Request");
	/** Shows the currently logged-in username. */
	protected static Label label_UserDetails = new Label();
	/** Returns the staff user to the Staff Home page. */
	protected static Button button_Return    = new Button("Return");
	/** Horizontal rule separating the header from the main content. */
	protected static Line line_Separator1    = new Line(20, 95, width - 20, 95);


	// ---- Area 2: Request form ----

	/** Prompts the user to enter a concise request title. */
	protected static Label    label_TitlePrompt  = new Label("Title:");
	/** Single-line title input for the request. */
	protected static TextField textField_Title   = new TextField();

	/** Prompts the user to select a severity level. */
	protected static Label label_SeverityPrompt = new Label("Severity:");
	/** Severity level selector — Low / Medium / High / Critical. */
	protected static ComboBox<String> combobox_Severity = new ComboBox<>();
	/** Available severity levels. */
	protected static final String[] SEVERITY_LEVELS = {"Low", "Medium", "High", "Critical"};

	/** Prompts the user to enter a full description of the requested action. */
	protected static Label   label_DescPrompt    = new Label("Description (max 2000 characters):");
	/** Multi-line description input for the request. */
	protected static TextArea textarea_Description = new TextArea();
	/** Live character counter; turns red when the 2000-character limit is exceeded. */
	protected static Label   label_CharCount     = new Label("0 / 2000");

	/** Inline validation feedback label; shows errors in red text. */
	protected static Label   label_ErrorFeedback = new Label();

	/** Validates the form fields and submits the request to the database. */
	protected static Button button_Submit    = new Button("Submit Request");
	/** Navigates to the Request Queue page to view all pending requests. */
	protected static Button button_ViewQueue = new Button("View Queue");

	/** Success alert displayed after the request is successfully persisted. */
	protected static Alert alertSuccess = new Alert(AlertType.INFORMATION);


	// ---- Area 3: Footer ----

	/** Horizontal rule separating content from footer. */
	protected static Line line_Separator4 = new Line(20, 525, width - 20, 525);
	/** Logs out the current user and returns to the login page. */
	protected static Button button_Logout = new Button("Logout");
	/** Terminates the application. */
	protected static Button button_Quit   = new Button("Quit");


	// ---- Singleton state ----

	/** Singleton reference; null until displayStaffRequest() is called for the first time. */
	private static ViewStaffRequest theView;

	/** Database reference shared by this package. */
	@SuppressWarnings("unused")
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/** The JavaFX Stage for this page. */
	protected static Stage theStage;
	/** The root Pane that holds all widgets for this page. */
	protected static Pane  theRootPane;
	/** The currently logged-in User. */
	protected static User  theUser;

	/** The single Scene instance reused on every visit. */
	private static Scene theStaffRequestScene;


	/*-*******************************************************************************************

	Constructor / Display Entry Point

	 */

	/**********
	 * <p> Method: displayStaffRequest(Stage ps, User user) </p>
	 *
	 * <p> Description: The single external entry point for this page.  It:
	 * <ol>
	 *   <li>Stores the Stage and User references.</li>
	 *   <li>Instantiates the singleton on first call.</li>
	 *   <li>Refreshes dynamic content: username label, clears form fields.</li>
	 *   <li>Sets the Scene and shows the window.</li>
	 * </ol>
	 *
	 * @param ps    the primary JavaFX Stage
	 * @param user  the currently logged-in Staff user
	 */
	public static void displayStaffRequest(Stage ps, User user) {

		// Save shared references before the singleton constructor may read them
		theStage = ps;
		theUser  = user;

		// Instantiate the singleton on first call only
		if (theView == null) theView = new ViewStaffRequest();

		// --- Dynamic refresh ---

		// Update the username label to reflect the current user
		label_UserDetails.setText("User: " + theUser.getUserName());

		// Reset form fields and feedback so the form is clean on every visit
		textField_Title.clear();
		textarea_Description.clear();
		combobox_Severity.getSelectionModel().select("Medium");
		label_CharCount.setText("0 / 2000");
		label_CharCount.setStyle("-fx-text-fill: black;");
		label_ErrorFeedback.setText("");

		// Display the page
		theStage.setTitle("CSE 360 Foundations: Submit Admin Request");
		theStage.setScene(theStaffRequestScene);
		theStage.show();
	}


	/**********
	 * <p> Method: ViewStaffRequest() — private constructor </p>
	 *
	 * <p> Description: Builds and configures all GUI widgets.  This constructor runs exactly
	 * once (singleton pattern).  Every widget is assigned its font, size, position, and event
	 * handler here.  Data that changes per visit (username, field values) is NOT set here; that
	 * is handled in displayStaffRequest(). </p>
	 */
	private ViewStaffRequest() {

		// Create the root Pane and Scene
		theRootPane           = new Pane();
		theStaffRequestScene  = new Scene(theRootPane, width, height);


		// ============================ Area 1: Header ============================

		setupLabelUI(label_PageTitle,   "Arial",  24, width,       Pos.CENTER,         0,   5);
		setupLabelUI(label_UserDetails, "Arial",  18, width - 220, Pos.BASELINE_LEFT,  20,  52);

		setupButtonUI(button_Return, "Dialog", 16, 170, Pos.CENTER, 608, 44);
		button_Return.setOnAction((_) -> { ControllerStaffRequest.performReturn(); });


		// ============================ Area 2: Request form ============================

		// Title field
		setupLabelUI(label_TitlePrompt, "Arial", 14, 200, Pos.BASELINE_LEFT, 20, 110);

		textField_Title.setFont(Font.font("Dialog", 14));
		textField_Title.setLayoutX(20);
		textField_Title.setLayoutY(130);
		textField_Title.setPrefWidth(580);
		textField_Title.setPromptText("Brief title for your request (e.g. \"Reset password for user jdoe\")");

		// Severity selector — right of the title field on the same row
		setupLabelUI(label_SeverityPrompt, "Arial", 14, 70, Pos.BASELINE_LEFT, 615, 110);
		combobox_Severity.setItems(FXCollections.observableArrayList(SEVERITY_LEVELS));
		combobox_Severity.getSelectionModel().select("Medium");
		combobox_Severity.setStyle("-fx-font-family: 'Dialog'; -fx-font-size: 14px;");
		combobox_Severity.setMinWidth(110);
		combobox_Severity.setLayoutX(675);
		combobox_Severity.setLayoutY(128);

		// Description TextArea
		setupLabelUI(label_DescPrompt, "Arial", 14, 500, Pos.BASELINE_LEFT, 20, 168);

		textarea_Description.setFont(Font.font("Dialog", 13));
		textarea_Description.setLayoutX(20);
		textarea_Description.setLayoutY(190);
		textarea_Description.setPrefWidth(760);
		textarea_Description.setPrefHeight(235);
		textarea_Description.setWrapText(true);
		textarea_Description.setPromptText(
				"Describe the specific administrator action you need (max 2000 characters)");
		// Live char-count update on every keystroke
		textarea_Description.textProperty().addListener((_, _, newVal) ->
				ControllerStaffRequest.doUpdateCharCount(newVal));

		// Character count — right-aligned beneath the TextArea
		setupLabelUI(label_CharCount, "Dialog", 12, 760, Pos.BASELINE_RIGHT, 15, 430);

		// Inline error / feedback label (red text)
		setupLabelUI(label_ErrorFeedback, "Arial", 12, 760, Pos.BASELINE_LEFT, 20, 452);
		label_ErrorFeedback.setStyle("-fx-text-fill: red;");
		label_ErrorFeedback.setWrapText(true);

		// Action buttons
		setupButtonUI(button_Submit,    "Dialog", 16, 200, Pos.CENTER, 220, 472);
		button_Submit.setOnAction((_) -> { ControllerStaffRequest.performSubmit(); });

		setupButtonUI(button_ViewQueue, "Dialog", 16, 200, Pos.CENTER, 440, 472);
		button_ViewQueue.setOnAction((_) -> { ControllerStaffRequest.performViewQueue(); });

		// Configure the success alert
		alertSuccess.setTitle("Success");


		// ============================ Area 3: Footer ============================

		setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20,  540);
		button_Logout.setOnAction((_) -> { ControllerStaffRequest.performLogout(); });

		setupButtonUI(button_Quit,   "Dialog", 18, 250, Pos.CENTER, 300, 540);
		button_Quit.setOnAction((_) -> { ControllerStaffRequest.performQuit(); });


		// Add all widgets to the root Pane
		theRootPane.getChildren().addAll(
				label_PageTitle, label_UserDetails, button_Return, line_Separator1,
				label_TitlePrompt, textField_Title,
				label_SeverityPrompt, combobox_Severity,
				label_DescPrompt, textarea_Description, label_CharCount,
				label_ErrorFeedback,
				button_Submit, button_ViewQueue,
				line_Separator4,
				button_Logout, button_Quit);
	}


	/*-*******************************************************************************************

	Helper methods — duplicated per View per the codebase design convention

	 */

	/**
	 * Configures the standard fields for a Label widget.
	 *
	 * @param l   the Label to configure
	 * @param ff  font family name
	 * @param f   font size in points
	 * @param w   minimum width in pixels
	 * @param p   text alignment
	 * @param x   X position from the left edge
	 * @param y   Y position from the top
	 */
	private static void setupLabelUI(Label l, String ff, double f, double w,
			Pos p, double x, double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}


	/**
	 * Configures the standard fields for a Button widget.
	 *
	 * @param b   the Button to configure
	 * @param ff  font family name
	 * @param f   font size in points
	 * @param w   minimum width in pixels
	 * @param p   button alignment
	 * @param x   X position from the left edge
	 * @param y   Y position from the top
	 */
	private static void setupButtonUI(Button b, String ff, double f, double w,
			Pos p, double x, double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}
}
