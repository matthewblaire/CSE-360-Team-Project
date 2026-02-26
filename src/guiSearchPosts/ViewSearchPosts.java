package guiSearchPosts;

import java.util.ArrayList;
import java.util.List;

import database.Database;
import entityClasses.DiscussionThread;
import entityClasses.User;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*******
 * <p> Title: ViewSearchPosts Class. </p>
 *
 * <p> Description: The View component of the Search Posts MVC triplet.  This singleton page
 * lets a logged-in student search all posts by keyword, with an optional thread filter,
 * satisfying the Phase 2 user story:
 * "As a student, I can search for posts to quickly find help on a specific topic."
 *
 * Layout (800 × 600):
 * <ul>
 *   <li>Area 1 — page title, username label, Return button, horizontal rule at y = 95.</li>
 *   <li>Area 2 — keyword TextField, thread ComboBox (includes "All Threads" option),
 *       Search button, result-count label, and a results ListView.</li>
 *   <li>Area 3 — horizontal rule at y = 525, Logout and Quit buttons.</li>
 * </ul>
 *
 * The Search button triggers {@link ControllerSearchPosts#doSearch()}.  Each result row
 * shows post ID, author, thread ID, date, content preview, and a read/unread indicator. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author CSE 360 Team
 *
 * @version 1.00	2026-02-23	Initial version for Phase 2 — Student Discussion System
 */
public class ViewSearchPosts {

	/*-*******************************************************************************************

	Attributes

	 */

	private static double width  = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;


	// ---- Area 1: Header ----

	/** Page title label. */
	protected static Label label_PageTitle   = new Label("Search Posts");
	/** Shows the currently logged-in username. */
	protected static Label label_UserDetails = new Label();
	/** Returns the student to the Student Home page. */
	protected static Button button_Return    = new Button("Return");
	/** Horizontal rule separating the header from the main content. */
	protected static Line line_Separator1    = new Line(20, 95, width - 20, 95);


	// ---- Area 2: Search controls ----

	/** Label for the keyword field. */
	protected static Label label_Keyword    = new Label("Keyword:");
	/** Text field for the search keyword. */
	protected static TextField text_Keyword = new TextField();

	/** Label for the thread filter ComboBox. */
	protected static Label label_Thread     = new Label("Thread (optional):");
	/**
	 * ComboBox populated with "All Threads" (threadId = -1) followed by all real threads.
	 * Selecting "All Threads" passes -1 to {@link database.Database#searchPosts}.
	 */
	protected static ComboBox<DiscussionThread> combobox_Thread = new ComboBox<>();

	/** Executes the search. */
	protected static Button button_Search   = new Button("Search");

	/** Shows the number of results found. */
	protected static Label label_ResultCount = new Label("Enter a keyword to search.");

	/** Displays formatted search-result summaries. */
	protected static ListView<String> listview_Results = new ListView<>();


	// ---- Area 3: Footer ----

	/** Horizontal rule separating content from footer. */
	protected static Line line_Separator4 = new Line(20, 525, width - 20, 525);
	/** Logs out the current user. */
	protected static Button button_Logout = new Button("Logout");
	/** Terminates the application. */
	protected static Button button_Quit   = new Button("Quit");


	// ---- Singleton state ----

	/** Singleton reference; null until displaySearchPosts() is called for the first time. */
	private static ViewSearchPosts theView;

	/** Database reference shared by this package. */
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/** The JavaFX Stage for this page. */
	protected static Stage theStage;
	/** The root Pane that holds all widgets for this page. */
	protected static Pane  theRootPane;
	/** The currently logged-in User. */
	protected static User  theUser;

	/** The single Scene instance reused on every visit. */
	private static Scene theSearchPostsScene;


	/*-*******************************************************************************************

	Constructor / Display Entry Point

	 */

	/**********
	 * <p> Method: displaySearchPosts(Stage ps, User user) </p>
	 *
	 * <p> Description: The single external entry point for this page.  Stores references,
	 * instantiates the singleton on first call, refreshes the thread ComboBox, clears prior
	 * search state, and displays the page. </p>
	 *
	 * @param ps    the primary JavaFX Stage
	 * @param user  the currently logged-in Student
	 */
	public static void displaySearchPosts(Stage ps, User user) {

		theStage = ps;
		theUser  = user;

		if (theView == null) theView = new ViewSearchPosts();

		// Dynamic refresh — update username label
		label_UserDetails.setText("User: " + theUser.getUserName());

		// Rebuild thread ComboBox with "All Threads" sentinel first
		List<DiscussionThread> dbThreads = theDatabase.getThreadList();
		List<DiscussionThread> threadOptions = new ArrayList<>();
		threadOptions.add(new DiscussionThread(-1, "All Threads"));
		threadOptions.addAll(dbThreads);
		combobox_Thread.setItems(FXCollections.observableArrayList(threadOptions));
		combobox_Thread.getSelectionModel().select(0);  // default to "All Threads"

		// Clear prior search state
		text_Keyword.clear();
		listview_Results.setItems(FXCollections.observableArrayList());
		label_ResultCount.setText("Enter a keyword to search.");
		ControllerSearchPosts.resultPostIds.clear();

		theStage.setTitle("CSE 360 Foundations: Search Posts");
		theStage.setScene(theSearchPostsScene);
		theStage.show();
	}


	/**********
	 * <p> Method: ViewSearchPosts() — private constructor </p>
	 *
	 * <p> Description: Builds and configures all GUI widgets exactly once (singleton pattern).
	 * Static layout and event handlers are set here; dynamic data is handled in
	 * displaySearchPosts(). </p>
	 */
	private ViewSearchPosts() {

		theRootPane        = new Pane();
		theSearchPostsScene = new Scene(theRootPane, width, height);


		// ============================ Area 1: Header ============================

		setupLabelUI(label_PageTitle,   "Arial", 24, width, Pos.CENTER,            0,  5);
		setupLabelUI(label_UserDetails, "Arial", 18, width - 220, Pos.BASELINE_LEFT, 20, 52);

		setupButtonUI(button_Return, "Dialog", 16, 170, Pos.CENTER, 608, 44);
		button_Return.setOnAction((_) -> { ControllerSearchPosts.performReturn(); });


		// ============================ Area 2: Search controls ============================

		// Keyword row
		setupLabelUI(label_Keyword, "Arial", 15, 85, Pos.BASELINE_LEFT, 20, 112);
		text_Keyword.setFont(Font.font("Dialog", 14));
		text_Keyword.setMinWidth(260);
		text_Keyword.setLayoutX(110);
		text_Keyword.setLayoutY(108);
		text_Keyword.setPromptText("e.g. midterm");
		// Allow pressing Enter to trigger search
		text_Keyword.setOnAction((_) -> { ControllerSearchPosts.doSearch(); });

		// Thread filter row (right side, same row as keyword)
		setupLabelUI(label_Thread, "Arial", 14, 145, Pos.BASELINE_LEFT, 390, 112);
		setupComboBoxUI(combobox_Thread, "Dialog", 13, 215, 538, 108);

		// Search button (centered below the search controls)
		setupButtonUI(button_Search, "Dialog", 16, 180, Pos.CENTER, 310, 148);
		button_Search.setOnAction((_) -> { ControllerSearchPosts.doSearch(); });

		// Results count label
		setupLabelUI(label_ResultCount, "Arial", 14, width - 40, Pos.BASELINE_LEFT, 20, 192);

		// Results ListView
		listview_Results.setStyle("-fx-font-family: 'Dialog'; -fx-font-size: 13px;");
		listview_Results.setLayoutX(20);
		listview_Results.setLayoutY(215);
		listview_Results.setPrefWidth(760);
		listview_Results.setPrefHeight(296);


		// ============================ Area 3: Footer ============================

		setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20,  540);
		button_Logout.setOnAction((_) -> { ControllerSearchPosts.performLogout(); });

		setupButtonUI(button_Quit,   "Dialog", 18, 250, Pos.CENTER, 300, 540);
		button_Quit.setOnAction((_) -> { ControllerSearchPosts.performQuit(); });


		// Add all widgets to the root Pane
		theRootPane.getChildren().addAll(
				label_PageTitle, label_UserDetails, button_Return, line_Separator1,
				label_Keyword, text_Keyword,
				label_Thread, combobox_Thread,
				button_Search,
				label_ResultCount, listview_Results,
				line_Separator4, button_Logout, button_Quit);
	}


	/*-*******************************************************************************************

	Helper methods — duplicated per View per the codebase design convention

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

	private static void setupComboBoxUI(ComboBox<?> cb, String ff, double f, double w,
			double x, double y) {
		cb.setStyle("-fx-font-family: '" + ff + "'; -fx-font-size: " + f + "px;");
		cb.setMinWidth(w);
		cb.setLayoutX(x);
		cb.setLayoutY(y);
	}
}
