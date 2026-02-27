package guiViewPosts;

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
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*******
 * <p> Title: ViewViewPosts Class. </p>
 *
 * <p> Description: The View component of the Browse Posts MVC triplet.  This singleton page
 * lets a logged-in student browse all non-deleted posts in a chosen discussion thread.
 *
 * Layout (800 × 600):
 * <ul>
 *   <li>Area 1 — page title, username label, Return button, horizontal rule at y = 95.</li>
 *   <li>Area 2 — thread ComboBox, Posts ListView (with read indicator and reply counts),
 *       Replies ListView that loads when a post is selected.</li>
 *   <li>Area 3 — horizontal rule at y = 525, Logout and Quit buttons.</li>
 * </ul>
 *
 * Selecting a row in the Posts ListView triggers
 * {@link ControllerViewPosts#doSelectPost()}, which marks the post and all its replies as
 * read for the current user, then repopulates the Replies ListView. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author CSE 360 Team
 *
 * @version 1.00	2026-02-23	Initial version for Phase 2 — Student Discussion System
 */
public class ViewViewPosts {

	/*-*******************************************************************************************

	Attributes

	 */

	private static double width  = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;


	// ---- Area 1: Header ----

	/** Page title label. */
	protected static Label label_PageTitle   = new Label("Browse Posts");
	/** Shows the currently logged-in username. */
	protected static Label label_UserDetails = new Label();
	/** Returns the student to the Student Home page. */
	protected static Button button_Return    = new Button("Return");
	/** Horizontal rule separating the header from the main content. */
	protected static Line line_Separator1    = new Line(20, 95, width - 20, 95);


	// ---- Area 2: Thread selector and post list ----

	/** Prompts the student to choose a discussion thread. */
	protected static Label label_SelectThread = new Label("Select Thread:");
	/** ComboBox populated with all DiscussionThread rows from the database. */
	protected static ComboBox<DiscussionThread> combobox_Thread = new ComboBox<>();
	/** Button to reload the post list for the selected thread. */
	protected static Button button_LoadPosts  = new Button("Load Posts");

	/** Heading for the posts list. */
	protected static Label label_PostsList    = new Label("Posts:");
	/** Displays formatted post summaries; selection triggers reply loading. */
	protected static ListView<String> listview_Posts = new ListView<>();

	/** Dynamic heading for the replies pane; updated when a post is selected. */
	protected static Label label_RepliesTitle = new Label("Replies — select a post above");
	/** Displays the replies for the currently selected post. */
	protected static ListView<String> listview_Replies = new ListView<>();


	// ---- Area 3: Footer ----

	/** Horizontal rule separating content from footer. */
	protected static Line line_Separator4 = new Line(20, 525, width - 20, 525);
	/** Logs out the current user. */
	protected static Button button_Logout = new Button("Logout");
	/** Terminates the application. */
	protected static Button button_Quit   = new Button("Quit");


	// ---- Singleton state ----

	/** Singleton reference; null until displayViewPosts() is called for the first time. */
	private static ViewViewPosts theView;

	/** Database reference shared by this package. */
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/** The JavaFX Stage for this page. */
	protected static Stage theStage;
	/** The root Pane that holds all widgets for this page. */
	protected static Pane  theRootPane;
	/** The currently logged-in User. */
	protected static User  theUser;

	/** The single Scene instance reused on every visit. */
	private static Scene theViewPostsScene;


	/*-*******************************************************************************************

	Constructor / Display Entry Point

	 */

	/**********
	 * <p> Method: displayViewPosts(Stage ps, User user) </p>
	 *
	 * <p> Description: The single external entry point for this page.  It:
	 * <ol>
	 *   <li>Stores the Stage and User references.</li>
	 *   <li>Instantiates the singleton on first call.</li>
	 *   <li>Refreshes dynamic content: username label, thread ComboBox.</li>
	 *   <li>Clears both ListViews so they are empty on every visit.</li>
	 *   <li>Sets the Scene and shows the window.</li>
	 * </ol>
	 * </p>
	 *
	 * @param ps    the primary JavaFX Stage
	 * @param user  the currently logged-in Student
	 */
	public static void displayViewPosts(Stage ps, User user) {

		theStage = ps;
		theUser  = user;

		if (theView == null) theView = new ViewViewPosts();

		// Dynamic refresh
		label_UserDetails.setText("User: " + theUser.getUserName());

		List<DiscussionThread> threads = theDatabase.getThreadList();
		combobox_Thread.setItems(FXCollections.observableArrayList(threads));
		if (!threads.isEmpty())
			combobox_Thread.getSelectionModel().select(0);

		// Clear both lists
		listview_Posts.setItems(FXCollections.observableArrayList());
		listview_Replies.setItems(FXCollections.observableArrayList());
		label_RepliesTitle.setText("Replies — select a post above");
		ControllerViewPosts.postIds.clear();
		ControllerViewPosts.currentPosts.clear();
		ControllerViewPosts.replyIds.clear();

		theStage.setTitle("CSE 360 Foundations: Browse Posts");
		theStage.setScene(theViewPostsScene);
		theStage.show();
	}


	/**********
	 * <p> Method: ViewViewPosts() — private constructor </p>
	 *
	 * <p> Description: Builds and configures all GUI widgets exactly once (singleton pattern).
	 * Static layout and event handlers are set here; dynamic data is handled in
	 * displayViewPosts(). </p>
	 */
	private ViewViewPosts() {

		theRootPane     = new Pane();
		theViewPostsScene = new Scene(theRootPane, width, height);


		// ============================ Area 1: Header ============================

		setupLabelUI(label_PageTitle,   "Arial", 24, width, Pos.CENTER,       0,   5);
		setupLabelUI(label_UserDetails, "Arial", 18, width - 220, Pos.BASELINE_LEFT, 20, 52);

		setupButtonUI(button_Return, "Dialog", 16, 170, Pos.CENTER, 608, 44);
		button_Return.setOnAction((_) -> { ControllerViewPosts.performReturn(); });


		// ============================ Area 2: Thread selector ============================

		setupLabelUI(label_SelectThread, "Arial", 15, 120, Pos.BASELINE_LEFT, 20, 112);
		setupComboBoxUI(combobox_Thread, "Dialog", 14, 280, 145, 108);

		setupButtonUI(button_LoadPosts, "Dialog", 14, 120, Pos.CENTER, 440, 104);
		button_LoadPosts.setOnAction((_) -> { ControllerViewPosts.doLoadPosts(); });

		// Posts ListView
		setupLabelUI(label_PostsList, "Arial", 14, 200, Pos.BASELINE_LEFT, 20, 146);

		listview_Posts.setStyle("-fx-font-family: 'Dialog'; -fx-font-size: 13px;");
		listview_Posts.setLayoutX(20);
		listview_Posts.setLayoutY(165);
		listview_Posts.setPrefWidth(760);
		listview_Posts.setPrefHeight(162);
		// Fire controller when the user selects a row (mouse or keyboard)
		listview_Posts.getSelectionModel().selectedIndexProperty()
				.addListener((_, _, _) -> ControllerViewPosts.doSelectPost());

		// Replies section
		setupLabelUI(label_RepliesTitle, "Arial", 14, width, Pos.BASELINE_LEFT, 20, 342);

		listview_Replies.setStyle("-fx-font-family: 'Dialog'; -fx-font-size: 13px;");
		listview_Replies.setLayoutX(20);
		listview_Replies.setLayoutY(362);
		listview_Replies.setPrefWidth(760);
		listview_Replies.setPrefHeight(148);


		// ============================ Area 3: Footer ============================

		setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20,  540);
		button_Logout.setOnAction((_) -> { ControllerViewPosts.performLogout(); });

		setupButtonUI(button_Quit,   "Dialog", 18, 250, Pos.CENTER, 300, 540);
		button_Quit.setOnAction((_) -> { ControllerViewPosts.performQuit(); });


		// Add all widgets to the root Pane
		theRootPane.getChildren().addAll(
				label_PageTitle, label_UserDetails, button_Return, line_Separator1,
				label_SelectThread, combobox_Thread, button_LoadPosts,
				label_PostsList, listview_Posts,
				label_RepliesTitle, listview_Replies,
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

	static void setupComboBoxUI(ComboBox<?> cb, String ff, double f, double w,
			double x, double y) {
		cb.setStyle("-fx-font-family: '" + ff + "'; -fx-font-size: " + f + "px;");
		cb.setMinWidth(w);
		cb.setLayoutX(x);
		cb.setLayoutY(y);
	}
}
