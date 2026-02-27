package guiCreatePost;

import java.util.List;

import database.Database;
import entityClasses.DiscussionThread;
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
 * <p> Title: ViewCreatePost Class </p>
 *
 * <p> Description: The View component of the Create Post / Reply MVC triplet.  This singleton
 * page allows a logged-in student to:
 * <ul>
 *   <li>Write a new post (statement or question) and submit it to a chosen thread.</li>
 *   <li>Write a reply to an existing post identified by its Post ID.</li>
 * </ul>
 *
 * The page follows the three-area layout used throughout this codebase:
 * <ul>
 *   <li>Area 1 — page title, logged-in user label, and a Return button.</li>
 *   <li>Area 2 — the main interaction widgets: thread selector, post TextArea, char-count
 *       label, Submit Post button, divider, reply Post-ID field, reply TextArea, feedback
 *       label, and Submit Reply button.</li>
 *   <li>Area 3 — Logout and Quit buttons.</li>
 * </ul>
 *
 * Static widget layout is performed once in the private constructor.  Dynamic data (username
 * label, thread ComboBox contents, field resets) is refreshed on every call to
 * {@link #displayCreatePost(Stage, User)}. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author CSE 360 Team
 *
 * @version 1.00	2026-02-23	Initial version for Phase 2 — Student Discussion System
 */

public class ViewCreatePost {

	/*-*******************************************************************************************

	Attributes

	 */

	// Window dimensions from the shared application constants
	private static double width  = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;


	// ---- Area 1: Header ----

	/** Page title label. */
	protected static Label label_PageTitle    = new Label("Create a New Post or Reply");
	/** Shows the currently logged-in username. */
	protected static Label label_UserDetails  = new Label();
	/** Returns the student to the Student Home page. */
	protected static Button button_Return     = new Button("Return");
	/** Horizontal rule separating the header from the main content. */
	protected static Line line_Separator1     = new Line(20, 95, width - 20, 95);


	// ---- Area 2: Post Creation ----

	/** Prompts the student to choose a discussion thread. */
	protected static Label label_SelectThread = new Label("Select Thread:");
	/** ComboBox populated with all DiscussionThread rows from the database. */
	protected static ComboBox<DiscussionThread> combobox_Thread = new ComboBox<>();

	/** Prompts the student to type the post body. */
	protected static Label label_PostContent  = new Label(
			"Post Content (max 2000 characters):");
	/** Multi-line text input for the post body. */
	protected static TextArea textarea_PostContent = new TextArea();
	/** Live character counter; turns red when the limit is exceeded. */
	protected static Label label_CharCount    = new Label("0 / 2000");
	/** Validates content and submits the post to the database. */
	protected static Button button_SubmitPost = new Button("Submit Post");

	/** Visual divider between the post-creation and reply sections. */
	protected static Line line_Separator2     = new Line(20, 348, width - 20, 348);

	// ---- Area 2: Reply Section ----

	/** Section heading for the reply form. */
	protected static Label label_ReplySection = new Label(
			"— Reply to an Existing Post —");
	/** Prompts the student to enter the numeric Post ID they want to reply to. */
	protected static Label label_PostId       = new Label("Post ID:");
	/** Single-line field for the target postId. */
	protected static TextField text_PostId    = new TextField();
	/** Prompts the student to type the reply body. */
	protected static Label label_ReplyContent = new Label("Reply Content:");
	/** Multi-line text input for the reply body. */
	protected static TextArea textarea_ReplyContent = new TextArea();
	/** Inline feedback label; shows validation errors in red. */
	protected static Label label_ErrorFeedback = new Label();
	/** Validates the postId and content, then persists the reply. */
	protected static Button button_SubmitReply = new Button("Submit Reply");

	/** Horizontal rule separating the main content from the footer. */
	protected static Line line_Separator4     = new Line(20, 525, width - 20, 525);


	// ---- Area 3: Footer ----

	/** Logs out the current user and returns to the login page. */
	protected static Button button_Logout = new Button("Logout");
	/** Terminates the application. */
	protected static Button button_Quit   = new Button("Quit");


	// ---- Shared alert reused for both post and reply success messages ----
	/** Success alert shown after a post or reply is successfully persisted. */
	protected static Alert alertSuccess = new Alert(AlertType.INFORMATION);


	// ---- Singleton state ----

	/** Singleton reference; null until displayCreatePost() is called for the first time. */
	private static ViewCreatePost theView;

	/** Database reference shared by this package. */
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/** The JavaFX Stage for this page. */
	protected static Stage theStage;
	/** The root Pane that holds all widgets for this page. */
	protected static Pane  theRootPane;
	/** The currently logged-in User. */
	protected static User  theUser;

	/** The single Scene instance that is reused on every visit. */
	private static Scene theCreatePostScene;


	/*-*******************************************************************************************

	Constructor / Display Entry Point

	 */

	/**********
	 * <p> Method: displayCreatePost(Stage ps, User user) </p>
	 *
	 * <p> Description: The single external entry point for this page.  It:
	 * <ol>
	 *   <li>Stores the Stage and User references.</li>
	 *   <li>Instantiates the singleton on first call.</li>
	 *   <li>Refreshes dynamic content: username label, thread ComboBox, field resets.</li>
	 *   <li>Sets the Scene and shows the window.</li>
	 * </ol>
	 * </p>
	 *
	 * @param ps    the primary JavaFX Stage
	 * @param user  the currently logged-in Student
	 */
	public static void displayCreatePost(Stage ps, User user) {

		// Save shared references before the singleton constructor may read them
		theStage = ps;
		theUser  = user;

		// Instantiate the singleton on first call only; static layout happens inside the
		// constructor and is never repeated
		if (theView == null) theView = new ViewCreatePost();

		// --- Dynamic refresh ---

		// Update the username label to reflect the current user
		label_UserDetails.setText("User: " + theUser.getUserName());

		// Populate the thread ComboBox from the live database
		List<DiscussionThread> threads = theDatabase.getThreadList();
		combobox_Thread.setItems(FXCollections.observableArrayList(threads));
		if (!threads.isEmpty())
			combobox_Thread.getSelectionModel().select(0); // default to "General"

		// Reset fields and feedback so the form is clean on every visit
		textarea_PostContent.clear();
		textarea_ReplyContent.clear();
		text_PostId.clear();
		label_CharCount.setText("0 / 2000");
		label_CharCount.setStyle("-fx-text-fill: black;");
		label_ErrorFeedback.setText("");

		// Display the page
		theStage.setTitle("CSE 360 Foundations: Create Post / Reply");
		theStage.setScene(theCreatePostScene);
		theStage.show();
	}


	/**********
	 * <p> Method: ViewCreatePost() — private constructor </p>
	 *
	 * <p> Description: Builds and configures all GUI widgets.  This constructor runs exactly
	 * once (singleton pattern).  Every widget is assigned its font, size, position, and event
	 * handler here.  Data that changes per visit (username, thread list, field values) is NOT
	 * set here; that is handled in displayCreatePost(). </p>
	 */
	private ViewCreatePost() {

		// Create the root Pane and Scene
		theRootPane        = new Pane();
		theCreatePostScene = new Scene(theRootPane, width, height);


		// ============================ Area 1: Header ============================

		setupLabelUI(label_PageTitle, "Arial", 24, width, Pos.CENTER, 0, 5);

		setupLabelUI(label_UserDetails, "Arial", 18, width - 220, Pos.BASELINE_LEFT, 20, 52);

		setupButtonUI(button_Return, "Dialog", 16, 170, Pos.CENTER, 608, 44);
		button_Return.setOnAction((_) -> { ControllerCreatePost.performReturn(); });


		// ============================ Area 2: Post Creation ============================

		// Thread selection row (label + ComboBox side-by-side)
		setupLabelUI(label_SelectThread, "Arial", 15, 120, Pos.BASELINE_LEFT, 20, 112);
		setupComboBoxUI(combobox_Thread, "Dialog", 14, 300, 145, 108);
		combobox_Thread.setOnAction((_) -> { ControllerCreatePost.doSelectThread(); });

		// Post content area
		setupLabelUI(label_PostContent, "Arial", 14, 400, Pos.BASELINE_LEFT, 20, 148);

		textarea_PostContent.setFont(Font.font("Dialog", 13));
		textarea_PostContent.setLayoutX(20);
		textarea_PostContent.setLayoutY(170);
		textarea_PostContent.setPrefWidth(760);
		textarea_PostContent.setPrefHeight(108);
		textarea_PostContent.setWrapText(true);
		textarea_PostContent.setPromptText("Type your question or statement here…");
		// Live char-count update on every keystroke
		textarea_PostContent.textProperty().addListener((_, _, newVal) ->
			ControllerCreatePost.doUpdateCharCount(newVal));

		// Character count — right-aligned beneath the TextArea
		setupLabelUI(label_CharCount, "Dialog", 12, 760, Pos.BASELINE_RIGHT, 20, 282);

		// Submit Post button
		setupButtonUI(button_SubmitPost, "Dialog", 16, 180, Pos.CENTER, 310, 305);
		button_SubmitPost.setOnAction((_) -> { ControllerCreatePost.doCreatePost(); });


		// ============================ Area 2: Reply Section ============================

		// Section divider and heading
		setupLabelUI(label_ReplySection, "Arial", 15, width, Pos.CENTER, 0, 355);

		// Post ID row (label + TextField side-by-side)
		setupLabelUI(label_PostId, "Arial", 14, 75, Pos.BASELINE_LEFT, 20, 388);
		text_PostId.setFont(Font.font("Dialog", 14));
		text_PostId.setMinWidth(120);
		text_PostId.setLayoutX(100);
		text_PostId.setLayoutY(384);
		text_PostId.setPromptText("e.g. 3");

		// Reply content area
		setupLabelUI(label_ReplyContent, "Arial", 14, 200, Pos.BASELINE_LEFT, 20, 420);

		textarea_ReplyContent.setFont(Font.font("Dialog", 13));
		textarea_ReplyContent.setLayoutX(20);
		textarea_ReplyContent.setLayoutY(442);
		textarea_ReplyContent.setPrefWidth(760);
		textarea_ReplyContent.setPrefHeight(62);
		textarea_ReplyContent.setWrapText(true);
		textarea_ReplyContent.setPromptText("Type your reply here…");

		// Inline error / feedback label (red text)
		setupLabelUI(label_ErrorFeedback, "Arial", 12, 760, Pos.BASELINE_LEFT, 20, 508);
		label_ErrorFeedback.setStyle("-fx-text-fill: red;");
		label_ErrorFeedback.setWrapText(true);

		// Submit Reply button
		setupButtonUI(button_SubmitReply, "Dialog", 16, 180, Pos.CENTER, 310, 490);
		button_SubmitReply.setOnAction((_) -> { ControllerCreatePost.doCreateReply(); });


		// ============================ Area 3: Footer ============================

		setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20,  540);
		button_Logout.setOnAction((_) -> { ControllerCreatePost.performLogout(); });

		setupButtonUI(button_Quit,   "Dialog", 18, 250, Pos.CENTER, 300, 540);
		button_Quit.setOnAction((_) -> { ControllerCreatePost.performQuit(); });


		// Configure the shared success alert
		alertSuccess.setTitle("Success");


		// Add all widgets to the root Pane
		theRootPane.getChildren().addAll(
				label_PageTitle, label_UserDetails, button_Return, line_Separator1,
				label_SelectThread, combobox_Thread,
				label_PostContent,  textarea_PostContent, label_CharCount, button_SubmitPost,
				line_Separator2,
				label_ReplySection,
				label_PostId, text_PostId,
				label_ReplyContent, textarea_ReplyContent,
				label_ErrorFeedback, button_SubmitReply,
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


	/**
	 * Configures the standard fields for a ComboBox widget.
	 *
	 * @param cb  the ComboBox to configure
	 * @param ff  font family name
	 * @param f   font size in points
	 * @param w   minimum width in pixels
	 * @param x   X position from the left edge
	 * @param y   Y position from the top
	 */
	static void setupComboBoxUI(ComboBox<?> cb, String ff, double f, double w,
			double x, double y) {
		cb.setStyle("-fx-font-family: '" + ff + "'; -fx-font-size: " + f + "px;");
		cb.setMinWidth(w);
		cb.setLayoutX(x);
		cb.setLayoutY(y);
	}
}
