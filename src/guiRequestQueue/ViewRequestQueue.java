package guiRequestQueue;

import database.Database;
import entityClasses.User;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*******
 * <p> Title: ViewRequestQueue Class. </p>
 *
 * <p> Description: The View component of the Request Queue MVC triplet.  This singleton page
 * is accessible to both admin and staff users.  It displays all staff-submitted requests
 * (OPEN and CLOSED) oldest-first so the longest-pending items appear at the top.
 *
 * Layout (800 × 600):
 * <ul>
 *   <li>Area 1 — page title, username label, Return button, horizontal rule at y = 95.</li>
 *   <li>Area 2a — summary label (open / total counts) and Refresh button.</li>
 *   <li>Area 2b — ListView of request summary rows (selectable).</li>
 *   <li>Area 2c — detail panel: read-only TextArea with full request info; admin-only
 *       severity ComboBox with Update button; admin-only Close Comment TextArea; and
 *       admin-only Close Ticket / Reopen Ticket action buttons.</li>
 *   <li>Area 3 — horizontal rule at y = 525, Logout and Quit buttons.</li>
 * </ul>
 *
 * Admin-only widgets are hidden via {@code setVisible(false)} for staff users.
 * The list, detail panel, and action controls are reset on every call to
 * {@link #displayRequestQueue(Stage, User)}. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author CSE 360 Team
 *
 * @version 1.00	2026-04-09	Initial version
 * @version 1.01	2026-04-09	Added detail panel and admin Close/Reopen ticket actions
 * @version 1.02	2026-04-09	Added severity control and close comment
 */
public class ViewRequestQueue {

	/*-*******************************************************************************************

	Attributes

	 */

	private static double width  = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	/** Severity level choices available to admins. */
	protected static final String[] SEVERITY_LEVELS = {"Low", "Medium", "High", "Critical"};


	// ---- Area 1: Header ----

	/** Page title label. */
	protected static Label label_PageTitle   = new Label("Request Queue");
	/** Shows the currently logged-in username. */
	protected static Label label_UserDetails = new Label();
	/** Returns the user to their role's home page. */
	protected static Button button_Return    = new Button("Return");
	/** Horizontal rule separating the header from the main content. */
	protected static Line line_Separator1    = new Line(20, 95, width - 20, 95);


	// ---- Area 2a: Summary row ----

	/** Shows the count of open and total requests. */
	protected static Label label_Summary   = new Label("Open: 0   |   Total: 0");
	/** Reloads the request list from the database. */
	protected static Button button_Refresh = new Button("Refresh");


	// ---- Area 2b: Queue ListView ----

	/** Displays one formatted summary row per request. */
	protected static ListView<String> listview_Requests = new ListView<>();


	// ---- Area 2c: Detail panel ----

	/** Thin rule separating the list from the detail panel. */
	protected static Line line_SeparatorDetail = new Line(20, 308, width - 20, 308);

	/** Label above the detail TextArea. */
	protected static Label label_DetailHeading = new Label("Selected Request:");

	/** Severity label (admin only). */
	protected static Label label_SeverityPrompt = new Label("Severity:");
	/** Severity ComboBox (admin only) — lets admin reclassify a ticket. */
	protected static ComboBox<String> combobox_Severity = new ComboBox<>();
	/** Saves the severity change to the database (admin only). */
	protected static Button button_UpdateSeverity = new Button("Update");

	/** Read-only multi-line area showing full details of the selected request. */
	protected static TextArea textarea_Detail = new TextArea();

	/** Label for the close comment field (admin only). */
	protected static Label label_CloseComment = new Label("Close Comment (explain what was done / what went wrong):");
	/** Admin comment entered before closing a ticket (admin only). */
	protected static TextArea textarea_CloseComment = new TextArea();

	/** Closes the selected OPEN ticket (admin only). */
	protected static Button button_CloseTicket  = new Button("Close Ticket");
	/** Reopens the selected CLOSED ticket (admin only). */
	protected static Button button_ReopenTicket = new Button("Reopen Ticket");
	/** Inline feedback shown after a close/reopen/severity action. */
	protected static Label label_ActionFeedback = new Label();


	// ---- Area 3: Footer ----

	/** Horizontal rule separating content from footer. */
	protected static Line line_Separator4 = new Line(20, 525, width - 20, 525);
	/** Logs out the current user and returns to the login page. */
	protected static Button button_Logout = new Button("Logout");
	/** Terminates the application. */
	protected static Button button_Quit   = new Button("Quit");


	// ---- Singleton state ----

	private static ViewRequestQueue theView;

	@SuppressWarnings("unused")
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;
	protected static Pane  theRootPane;
	protected static User  theUser;

	private static Scene theRequestQueueScene;


	/*-*******************************************************************************************

	Constructor / Display Entry Point

	 */

	/**********
	 * <p> Method: displayRequestQueue(Stage ps, User user) </p>
	 *
	 * <p> Description: The single external entry point for this page.  Stores Stage/User,
	 * instantiates the singleton on first call, resets the detail panel, shows or hides
	 * admin-only controls based on the current user's role, loads the latest requests, and
	 * sets the Scene. </p>
	 *
	 * @param ps    the primary JavaFX Stage
	 * @param user  the currently logged-in Admin or Staff user
	 */
	public static void displayRequestQueue(Stage ps, User user) {

		theStage = ps;
		theUser  = user;

		if (theView == null) theView = new ViewRequestQueue();

		label_UserDetails.setText("User: " + theUser.getUserName());

		// Clear the detail panel on every visit
		textarea_Detail.clear();
		textarea_CloseComment.clear();
		label_ActionFeedback.setText("");
		button_CloseTicket.setDisable(true);
		button_ReopenTicket.setDisable(true);
		button_UpdateSeverity.setDisable(true);

		// Admin-only widgets visibility
		boolean isAdmin = theUser.getAdminRole();
		label_SeverityPrompt.setVisible(isAdmin);
		combobox_Severity.setVisible(isAdmin);
		button_UpdateSeverity.setVisible(isAdmin);
		label_CloseComment.setVisible(isAdmin);
		textarea_CloseComment.setVisible(isAdmin);
		button_CloseTicket.setVisible(isAdmin);
		button_ReopenTicket.setVisible(isAdmin);
		label_ActionFeedback.setVisible(isAdmin);

		ControllerRequestQueue.doLoadRequests();

		theStage.setTitle("CSE 360 Foundations: Request Queue");
		theStage.setScene(theRequestQueueScene);
		theStage.show();
	}


	/**********
	 * <p> Method: ViewRequestQueue() — private constructor </p>
	 *
	 * <p> Description: Builds and configures all GUI widgets exactly once. </p>
	 */
	private ViewRequestQueue() {

		theRootPane          = new Pane();
		theRequestQueueScene = new Scene(theRootPane, width, height);


		// ============================ Area 1: Header ============================

		setupLabelUI(label_PageTitle,   "Arial",  24, width,       Pos.CENTER,        0,   5);
		setupLabelUI(label_UserDetails, "Arial",  18, width - 220, Pos.BASELINE_LEFT, 20,  52);

		setupButtonUI(button_Return, "Dialog", 16, 170, Pos.CENTER, 608, 44);
		button_Return.setOnAction((_) -> { ControllerRequestQueue.performReturn(); });


		// ============================ Area 2a: Summary row ============================

		setupLabelUI(label_Summary, "Arial", 14, 500, Pos.BASELINE_LEFT, 20, 108);

		setupButtonUI(button_Refresh, "Dialog", 14, 120, Pos.CENTER, 638, 103);
		button_Refresh.setOnAction((_) -> { ControllerRequestQueue.doLoadRequests(); });


		// ============================ Area 2b: Request ListView ============================

		listview_Requests.setLayoutX(20);
		listview_Requests.setLayoutY(132);
		listview_Requests.setPrefWidth(760);
		listview_Requests.setPrefHeight(165);
		listview_Requests.setCellFactory(_ -> new javafx.scene.control.ListCell<>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? null : item);
				setFont(Font.font("Monospaced", 12));
			}
		});

		listview_Requests.getSelectionModel().selectedIndexProperty().addListener(
				(_, _, _) -> ControllerRequestQueue.doSelectRequest());


		// ============================ Area 2c: Detail panel ============================

		// "Selected Request:" heading + severity controls on the same row
		setupLabelUI(label_DetailHeading, "Arial", 13, 250, Pos.BASELINE_LEFT, 20, 315);

		setupLabelUI(label_SeverityPrompt, "Arial", 13, 65, Pos.BASELINE_LEFT, 490, 315);
		combobox_Severity.setItems(FXCollections.observableArrayList(SEVERITY_LEVELS));
		combobox_Severity.setStyle("-fx-font-family: 'Dialog'; -fx-font-size: 13px;");
		combobox_Severity.setMinWidth(105);
		combobox_Severity.setLayoutX(558);
		combobox_Severity.setLayoutY(311);

		setupButtonUI(button_UpdateSeverity, "Dialog", 13, 80, Pos.CENTER, 672, 311);
		button_UpdateSeverity.setDisable(true);
		button_UpdateSeverity.setOnAction((_) -> { ControllerRequestQueue.doUpdateSeverity(); });

		// Read-only detail TextArea
		textarea_Detail.setFont(Font.font("Dialog", 12));
		textarea_Detail.setLayoutX(20);
		textarea_Detail.setLayoutY(337);
		textarea_Detail.setPrefWidth(760);
		textarea_Detail.setPrefHeight(72);
		textarea_Detail.setEditable(false);
		textarea_Detail.setWrapText(true);
		textarea_Detail.setPromptText("Select a request above to see its full details.");

		// Close comment — admin only
		setupLabelUI(label_CloseComment, "Arial", 12, 560, Pos.BASELINE_LEFT, 20, 416);

		textarea_CloseComment.setFont(Font.font("Dialog", 12));
		textarea_CloseComment.setLayoutX(20);
		textarea_CloseComment.setLayoutY(433);
		textarea_CloseComment.setPrefWidth(760);
		textarea_CloseComment.setPrefHeight(48);
		textarea_CloseComment.setWrapText(true);
		textarea_CloseComment.setPromptText(
				"Optional — describe what was done or what the requester did wrong (saved when you close the ticket)");

		// Action buttons
		setupButtonUI(button_CloseTicket,  "Dialog", 14, 170, Pos.CENTER, 20,  490);
		button_CloseTicket.setDisable(true);
		button_CloseTicket.setOnAction((_) -> { ControllerRequestQueue.doCloseTicket(); });

		setupButtonUI(button_ReopenTicket, "Dialog", 14, 170, Pos.CENTER, 205, 490);
		button_ReopenTicket.setDisable(true);
		button_ReopenTicket.setOnAction((_) -> { ControllerRequestQueue.doReopenTicket(); });

		setupLabelUI(label_ActionFeedback, "Arial", 12, 355, Pos.BASELINE_LEFT, 390, 497);
		label_ActionFeedback.setWrapText(true);


		// ============================ Area 3: Footer ============================

		setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20,  540);
		button_Logout.setOnAction((_) -> { ControllerRequestQueue.performLogout(); });

		setupButtonUI(button_Quit,   "Dialog", 18, 250, Pos.CENTER, 300, 540);
		button_Quit.setOnAction((_) -> { ControllerRequestQueue.performQuit(); });


		theRootPane.getChildren().addAll(
				label_PageTitle, label_UserDetails, button_Return, line_Separator1,
				label_Summary,   button_Refresh,
				listview_Requests,
				line_SeparatorDetail,
				label_DetailHeading,
				label_SeverityPrompt, combobox_Severity, button_UpdateSeverity,
				textarea_Detail,
				label_CloseComment, textarea_CloseComment,
				button_CloseTicket, button_ReopenTicket, label_ActionFeedback,
				line_Separator4,
				button_Logout, button_Quit);
	}


	/*-*******************************************************************************************

	Helper methods

	 */

	private static void setupLabelUI(Label l, String ff, double f, double w,
			Pos p, double x, double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}

	private static void setupButtonUI(Button b, String ff, double f, double w,
			Pos p, double x, double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}
}
