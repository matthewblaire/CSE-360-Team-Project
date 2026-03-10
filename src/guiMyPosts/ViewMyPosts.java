package guiMyPosts;

import database.Database;
import entityClasses.User;
import guiViewPosts.ControllerViewPosts;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*******
 * <p> Title: ViewMyPosts Class. </p>
 *
 * <p> Description: The View component of the My Posts MVC triplet.  This singleton page
 * shows the currently logged-in student all of their own posts, with per-post reply counts
 * and unread reply counts, satisfying the Phase 2 user story:
 * "As a student, I can see which posts have been replied to and which replies have not been
 * read by me."
 *
 * Layout (800 × 600):
 * <ul>
 *   <li>Area 1 — page title, username label, Return button, horizontal rule at y = 95.</li>
 *   <li>Area 2 — post-count label and a ListView of post summaries (newest first).</li>
 *   <li>Area 3 — horizontal rule at y = 525, Logout and Quit buttons.</li>
 * </ul>
 *
 * The ListView is populated (and repopulated on every visit) by
 * {@link ControllerMyPosts#doLoadMyPosts()}. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author CSE 360 Team
 *
 * @version 1.00	2026-02-23	Initial version for Phase 2 — Student Discussion System
 */
public class ViewMyPosts {

	/*-*******************************************************************************************

	Attributes

	 */

	private static double width  = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;


	// ---- Area 1: Header ----

	/** Page title label. */
	protected static Label label_PageTitle   = new Label("My Posts");
	/** Shows the currently logged-in username. */
	protected static Label label_UserDetails = new Label();
	/** Returns the student to the Student Home page. */
	protected static Button button_Return    = new Button("Return");
	/** Horizontal rule separating the header from the main content. */
	protected static Line line_Separator1    = new Line(20, 95, width - 20, 95);


	// ---- Area 2: My posts list ----

	/** Shows the count of posts authored by the current user. */
	protected static Label label_PostCount   = new Label("Your posts: 0");
	/** Refresh button to reload posts. */
	protected static Button button_Refresh   = new Button("Refresh");
	/** Navigates to the Edit Post page with the selected post pre-loaded. */
	protected static Button button_EditPost  = new Button("Edit Selected Post");
	/** Displays formatted post summaries with reply and unread counts. */
	protected static ListView<String> listview_MyPosts = new ListView<>();


	// ---- Area 3: Footer ----

	/** Horizontal rule separating content from footer. */
	protected static Line line_Separator4 = new Line(20, 525, width - 20, 525);
	/** Logs out the current user. */
	protected static Button button_Logout = new Button("Logout");
	/** Terminates the application. */
	protected static Button button_Quit   = new Button("Quit");


	// ---- Singleton state ----

	/** Singleton reference; null until displayMyPosts() is called for the first time. */
	private static ViewMyPosts theView;

	/** Database reference shared by this package. */
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/** The JavaFX Stage for this page. */
	protected static Stage theStage;
	/** The root Pane that holds all widgets for this page. */
	protected static Pane  theRootPane;
	/** The currently logged-in User. */
	protected static User  theUser;

	/** The single Scene instance reused on every visit. */
	private static Scene theMyPostsScene;


	/*-*******************************************************************************************

	Constructor / Display Entry Point

	 */

	/**********
	 * <p> Method: displayMyPosts(Stage ps, User user) </p>
	 *
	 * <p> Description: The single external entry point for this page.  It stores the Stage and
	 * User references, instantiates the singleton on first call, then immediately loads the
	 * current user's posts so the list is populated before the page becomes visible. </p>
	 *
	 * @param ps    the primary JavaFX Stage
	 * @param user  the currently logged-in Student
	 */
	public static void displayMyPosts(Stage ps, User user) {

		theStage = ps;
		theUser  = user;

		if (theView == null) theView = new ViewMyPosts();

		// Dynamic refresh
		label_UserDetails.setText("User: " + theUser.getUserName());
		listview_MyPosts.setItems(FXCollections.observableArrayList());
		ControllerMyPosts.postIds.clear();

		// Populate the list immediately on every visit
		ControllerMyPosts.doLoadMyPosts();

		theStage.setTitle("CSE 360 Foundations: My Posts");
		theStage.setScene(theMyPostsScene);
		theStage.show();
	}


	/**********
	 * <p> Method: ViewMyPosts() — private constructor </p>
	 *
	 * <p> Description: Builds and configures all GUI widgets exactly once (singleton pattern).
	 * Static layout and event handlers are set here; dynamic data is handled in
	 * displayMyPosts(). </p>
	 */
	private ViewMyPosts() {

		theRootPane    = new Pane();
		theMyPostsScene = new Scene(theRootPane, width, height);


		// ============================ Area 1: Header ============================

		setupLabelUI(label_PageTitle,   "Arial", 24, width, Pos.CENTER,            0,  5);
		setupLabelUI(label_UserDetails, "Arial", 18, width - 220, Pos.BASELINE_LEFT, 20, 52);

		setupButtonUI(button_Return, "Dialog", 16, 170, Pos.CENTER, 608, 44);
		button_Return.setOnAction((_) -> { ControllerMyPosts.performReturn(); });


		// ============================ Area 2: My posts list ============================

		setupLabelUI(label_PostCount, "Arial", 15, 400, Pos.BASELINE_LEFT, 20, 112);

		setupButtonUI(button_Refresh, "Dialog", 14, 120, Pos.CENTER, 480, 108);
		button_Refresh.setOnAction((_) -> { ControllerMyPosts.doLoadMyPosts(); });

		setupButtonUI(button_EditPost, "Dialog", 14, 200, Pos.CENTER, 608, 108);
		button_EditPost.setOnAction((_) -> { ControllerMyPosts.goToEditPost(); });

		
	    // Double click handler for Posts
		listview_MyPosts.setOnMouseClicked(event -> {
	        if (event.getClickCount() == 2 && !listview_MyPosts.getSelectionModel().isEmpty()) {
	            Object selectedItem = listview_MyPosts.getSelectionModel().getSelectedItem();
	            ControllerMyPosts.doHandlePostDoubleClick(selectedItem, theStage, theUser);
	            event.consume();
	        }
	    });
		
		listview_MyPosts.setStyle("-fx-font-family: 'Dialog'; -fx-font-size: 13px;");
		listview_MyPosts.setLayoutX(20);
		listview_MyPosts.setLayoutY(138);
		listview_MyPosts.setPrefWidth(760);
		listview_MyPosts.setPrefHeight(340);


		// ============================ Area 3: Footer ============================

		setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20,  540);
		button_Logout.setOnAction((_) -> { ControllerMyPosts.performLogout(); });

		setupButtonUI(button_Quit,   "Dialog", 18, 250, Pos.CENTER, 300, 540);
		button_Quit.setOnAction((_) -> { ControllerMyPosts.performQuit(); });


		// Add all widgets to the root Pane
		theRootPane.getChildren().addAll(
				label_PageTitle, label_UserDetails, button_Return, line_Separator1,
				label_PostCount, button_Refresh, button_EditPost, listview_MyPosts,
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
}
