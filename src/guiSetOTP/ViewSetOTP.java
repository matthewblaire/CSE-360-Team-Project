package guiSetOTP;

import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;

/*******
 * <p> Title: ViewSetOTP Class. </p>
 *
 * <p> Description: The Java/FX-based page for setting a one-time password for a user.
 * This allows an admin to help a user who has forgotten their password by setting
 * a temporary password that can be used to log in and establish a new password.</p>
 *
 */

public class ViewSetOTP {

	// These are the application values required by the user interface
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// GUI Area 1: Page title and user context
	/** The label displaying the page title. */
	protected static Label label_PageTitle = new Label();
	/** The label displaying the current admin user details. */
	protected static Label label_UserDetails = new Label();
	/** The button to navigate to the account update page. */
	protected static Button button_UpdateThisUser = new Button("Account Update");

	// Separator
	/** The horizontal line separating the header area from the main content. */
	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);

	// GUI Area 2: Select user and generate OTP
	/** The label prompting the admin to select a user. */
	protected static Label label_SelectUser = new Label("Select a user to reset password:");
	/** The combo box for selecting a user to reset password. */
	protected static ComboBox<String> combobox_SelectUser = new ComboBox<String>();

	/** The label displaying information about the selected user. */
	protected static Label label_SelectedUserInfo = new Label();
	/** The label for the one-time password field. */
	protected static Label label_OTPLabel = new Label("One-Time Password:");
	/** The text field displaying the generated one-time password. */
	protected static TextField text_OTP = new TextField();
	/** The button to generate a one-time password for the selected user. */
	protected static Button button_GenerateOTP = new Button("Generate One-Time Password");

	/** The label displaying instructions for sharing the one-time password. */
	protected static Label label_Instructions = new Label(
			"Share this one-time password with the user. They will use it to log in\n" +
			"and will be prompted to set a new password.");

	// Alert for success
	/** The alert displayed when a one-time password is successfully generated. */
	protected static Alert alertOTPGenerated = new Alert(AlertType.INFORMATION);

	// Alert for no user selected
	/** The alert displayed when no user is selected before generating an OTP. */
	protected static Alert alertNoUserSelected = new Alert(AlertType.WARNING);

	// Separator
	/** The horizontal line separating the main content from the navigation buttons. */
	protected static Line line_Separator2 = new Line(20, 525, width-20, 525);

	// GUI Area 3: Navigation buttons
	/** The button to return to the previous page. */
	protected static Button button_Return = new Button("Return");
	/** The button to log out of the application. */
	protected static Button button_Logout = new Button("Logout");
	/** The button to quit the application. */
	protected static Button button_Quit = new Button("Quit");

	// Class attributes
	private static ViewSetOTP theView;
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/** The JavaFX Stage used to display this page. */
	protected static Stage theStage;
	/** The root pane that holds all GUI widgets. */
	protected static Pane theRootPane;
	/** The current user of the application. */
	protected static User theUser;

	/** The scene for the Set OTP page. */
	public static Scene theSetOTPScene = null;
	/** The username of the currently selected user. */
	protected static String theSelectedUser = "";


	/**********
	 * <p> Method: displaySetOTP(Stage ps, User user) </p>
	 *
	 * <p> Description: Entry point to display the Set OTP page.</p>
	 *
	 * @param ps specifies the JavaFX Stage to be used for this GUI
	 * @param user specifies the current User of the application
	 */
	public static void displaySetOTP(Stage ps, User user) {
		theStage = ps;
		theUser = user;

		if (theView == null) theView = new ViewSetOTP();

		// Refresh the user list
		List<String> userList = theDatabase.getUserList();
		combobox_SelectUser.setItems(FXCollections.observableArrayList(userList));
		combobox_SelectUser.getSelectionModel().select(0);

		// Clear previous OTP display
		text_OTP.setText("");
		label_SelectedUserInfo.setText("");
		theSelectedUser = "";

		// Update user details label
		label_UserDetails.setText("Admin: " + theUser.getUserName());

		// Set up the scene
		theRootPane.getChildren().clear();
		theRootPane.getChildren().addAll(
			label_PageTitle, label_UserDetails, button_UpdateThisUser,
			line_Separator1,
			label_SelectUser, combobox_SelectUser,
			label_SelectedUserInfo,
			label_OTPLabel, text_OTP, button_GenerateOTP,
			label_Instructions,
			line_Separator2,
			button_Return, button_Logout, button_Quit
		);

		theStage.setTitle("CSE 360 Foundation Code: Set One-Time Password");
		theStage.setScene(theSetOTPScene);
		theStage.show();
	}

	/**********
	 * <p> Constructor: ViewSetOTP() </p>
	 *
	 * <p> Description: Initializes all GUI elements.</p>
	 */
	public ViewSetOTP() {
		theRootPane = new Pane();
		theSetOTPScene = new Scene(theRootPane, width, height);

		// GUI Area 1
		label_PageTitle.setText("Set One-Time Password");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("Admin: " + (theUser != null ? theUser.getUserName() : ""));
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);

		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((_) ->
			{guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser); });

		// GUI Area 2
		setupLabelUI(label_SelectUser, "Arial", 20, 350, Pos.BASELINE_LEFT, 20, 130);

		setupComboBoxUI(combobox_SelectUser, "Dialog", 16, 250, 350, 125);
		combobox_SelectUser.getSelectionModel().selectedItemProperty()
			.addListener((@SuppressWarnings("unused") ObservableValue<? extends String> observable,
				@SuppressWarnings("unused") String oldValue,
				@SuppressWarnings("unused") String newValue) -> {ControllerSetOTP.doSelectUser();});

		setupLabelUI(label_SelectedUserInfo, "Arial", 16, 500, Pos.BASELINE_LEFT, 50, 180);

		setupLabelUI(label_OTPLabel, "Arial", 20, 200, Pos.BASELINE_LEFT, 20, 230);

		setupTextUI(text_OTP, "Arial", 24, 200, Pos.CENTER, 220, 220, false);
		text_OTP.setStyle("-fx-font-weight: bold; -fx-background-color: #f0f0f0;");

		setupButtonUI(button_GenerateOTP, "Dialog", 18, 280, Pos.CENTER, 20, 290);
		button_GenerateOTP.setOnAction((_) -> {ControllerSetOTP.generateOTP();});

		setupLabelUI(label_Instructions, "Arial", 14, 700, Pos.BASELINE_LEFT, 20, 350);
		label_Instructions.setStyle("-fx-text-fill: #666666;");

		// Alerts
		alertOTPGenerated.setTitle("One-Time Password Set");
		alertOTPGenerated.setHeaderText("The one-time password has been set successfully.");

		alertNoUserSelected.setTitle("No User Selected");
		alertNoUserSelected.setHeaderText("Please select a user first.");
		alertNoUserSelected.setContentText("Select a user from the dropdown before generating a one-time password.");

		// GUI Area 3
		setupButtonUI(button_Return, "Dialog", 18, 210, Pos.CENTER, 20, 540);
		button_Return.setOnAction((_) -> {ControllerSetOTP.performReturn();});

		setupButtonUI(button_Logout, "Dialog", 18, 210, Pos.CENTER, 300, 540);
		button_Logout.setOnAction((_) -> {ControllerSetOTP.performLogout();});

		setupButtonUI(button_Quit, "Dialog", 18, 210, Pos.CENTER, 570, 540);
		button_Quit.setOnAction((_) -> {ControllerSetOTP.performQuit();});
	}

	// Helper methods
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}

	/** Initializes the standard fields for a button.
	 *
	 * @param b		The Button object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	protected static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}

	/** Initializes the standard fields for a combo box.
	 *
	 * @param c		The ComboBox object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the ComboBox
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	protected static void setupComboBoxUI(ComboBox<String> c, String ff, double f, double w, double x, double y) {
		c.setStyle("-fx-font: " + f + " " + ff + ";");
		c.setMinWidth(w);
		c.setLayoutX(x);
		c.setLayoutY(y);
	}

	private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, boolean e) {
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);
		t.setEditable(e);
	}
}
