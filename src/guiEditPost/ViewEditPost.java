package guiEditPost;

import database.Database;
import entityClasses.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*******
 * <p> Title: ViewEditPost Class. </p>
 *
 * <p> Description: The View component of the Edit Post / Reply MVC triplet.  This singleton
 * page lets a logged-in student edit or delete one of their own posts, and edit one of their
 * own replies, satisfying the Phase 2 user stories:
 * <ul>
 *   <li>"As a student, I can edit my own posts and replies."</li>
 *   <li>"As a student, I can delete my own posts."</li>
 *   <li>"As a student, I can delete my own replies."</li>
 * </ul>
 *
 * Layout (800 × 600):
 * <ul>
 *   <li>Area 1 — page title, username label, Return button, separator at y = 95.</li>
 *   <li>Area 2a (Edit Post) — Post-ID field + Load button, content TextArea with live
 *       character counter, Save Changes and Delete Post buttons, feedback label.</li>
 *   <li>Area 2b (Edit Reply) — Reply-ID field + Load button, content TextArea, Save Reply
 *       button, feedback label.</li>
 *   <li>Area 3 — separator at y = 525, Logout and Quit buttons.</li>
 * </ul>
 *
 * The Save and Delete buttons are disabled until a post is successfully loaded via the Load
 * button (or automatically pre-filled when navigating from the My Posts page).  Likewise the
 * Save Reply button is disabled until a reply is loaded. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author CSE 360 Team
 *
 * @version 1.00	2026-02-23	Initial version for Phase 2 — Student Discussion System
 */
public class ViewEditPost {

	/*-*******************************************************************************************

	Attributes

	 */

	private static double width  = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;


	// ---- Area 1: Header ----

	/** Page title label. */
	protected static Label label_PageTitle   = new Label("Edit My Post or Reply");
	/** Shows the currently logged-in username. */
	protected static Label label_UserDetails = new Label();
	/** Returns the student to the My Posts page. */
	protected static Button button_Return    = new Button("Return");
	/** Horizontal rule separating the header from the main content. */
	protected static Line line_Separator1    = new Line(20, 95, width - 20, 95);


	// ---- Area 2a: Edit Post ----

	/** Section heading for the post-edit form. */
	protected static Label label_EditPostSection = new Label("— Edit or Delete Your Post —");
	/** Label for the post ID input field. */
	protected static Label label_PostId          = new Label("Post ID:");
	/** Single-line field for the target postId. */
	protected static TextField text_PostId       = new TextField();
	/** Fetches the post and pre-fills the content TextArea. */
	protected static Button button_LoadPost      = new Button("Load Post");

	/** Prompts the student to edit the post body. */
	protected static Label label_PostContent     = new Label("Post Content (max 2000 chars):");
	/** Multi-line text input for the edited post body. */
	protected static TextArea textarea_PostContent = new TextArea();
	/** Live character counter; turns red when the 2000-char limit is exceeded. */
	protected static Label label_PostCharCount   = new Label("0 / 2000");
	/** Persists the edited content to the database. */
	protected static Button button_SavePost      = new Button("Save Changes");
	/** Soft-deletes the post after a confirmation dialog. */
	protected static Button button_DeletePost    = new Button("Delete Post");
	/** Inline success/error feedback for the post section. */
	protected static Label label_PostFeedback    = new Label();

	/** Horizontal rule separating the post-edit section from the reply-edit section. */
	protected static Line line_Separator2 = new Line(20, 325, width - 20, 325);


	// ---- Area 2b: Edit Reply ----

	/** Section heading for the reply-edit form. */
	protected static Label label_EditReplySection = new Label("— Edit or Delete Your Reply —");
	/** Label for the reply ID input field. */
	protected static Label label_ReplyId          = new Label("Reply ID:");
	/** Single-line field for the target replyId. */
	protected static TextField text_ReplyId       = new TextField();
	/** Fetches the reply and pre-fills the reply TextArea. */
	protected static Button button_LoadReply      = new Button("Load Reply");

	/** Prompts the student to edit the reply body. */
	protected static Label label_ReplyContent     = new Label("Reply Content:");
	/** Multi-line text input for the edited reply body. */
	protected static TextArea textarea_ReplyContent = new TextArea();
	/** Inline success/error feedback for the reply section. */
	protected static Label label_ReplyFeedback    = new Label();
	/** Persists the edited reply content to the database. */
	protected static Button button_SaveReply      = new Button("Save Reply");
	/** Soft-deletes the reply after a confirmation dialog. */
	protected static Button button_DeleteReply    = new Button("Delete Reply");


	// ---- Area 3: Footer ----

	/** Horizontal rule separating content from footer. */
	protected static Line line_Separator4 = new Line(20, 525, width - 20, 525);
	/** Logs out the current user. */
	protected static Button button_Logout = new Button("Logout");
	/** Terminates the application. */
	protected static Button button_Quit   = new Button("Quit");


	// ---- Singleton state ----

	/** Singleton reference; null until displayEditPost() is called for the first time. */
	private static ViewEditPost theView;

	/** Database reference shared by this package. */
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/** The JavaFX Stage for this page. */
	protected static Stage theStage;
	/** The root Pane that holds all widgets for this page. */
	protected static Pane  theRootPane;
	/** The currently logged-in User. */
	protected static User  theUser;

	/** The single Scene instance reused on every visit. */
	private static Scene theEditPostScene;


	/*-*******************************************************************************************

	Constructor / Display Entry Point

	 */

	/**********
	 * <p> Method: displayEditPost(Stage ps, User user, int prefillPostId) </p>
	 *
	 * <p> Description: The single external entry point for this page.  It stores the Stage and
	 * User references, instantiates the singleton on first call, clears both forms, and — if
	 * {@code prefillPostId} is greater than zero — fills the Post ID field and immediately
	 * calls the Load Post action so the TextArea is pre-populated on arrival. </p>
	 *
	 * @param ps             the primary JavaFX Stage
	 * @param user           the currently logged-in Student
	 * @param prefillPostId  a postId to pre-load, or 0 if arriving with no pre-selected post
	 */
	public static void displayEditPost(Stage ps, User user, int prefillPostId) {

		theStage = ps;
		theUser  = user;

		if (theView == null) theView = new ViewEditPost();

		// Dynamic refresh — reset both forms to a clean state
		label_UserDetails.setText("User: " + theUser.getUserName());

		text_PostId.clear();
		textarea_PostContent.clear();
		label_PostCharCount.setText("0 / 2000");
		label_PostCharCount.setStyle("-fx-text-fill: black;");
		label_PostFeedback.setText("");
		button_SavePost.setDisable(true);
		button_DeletePost.setDisable(true);

		text_ReplyId.clear();
		textarea_ReplyContent.clear();
		label_ReplyFeedback.setText("");
		button_SaveReply.setDisable(true);
		button_DeleteReply.setDisable(true);

		// If a specific post was requested (e.g., from the My Posts page), pre-load it
		if (prefillPostId > 0) {
			text_PostId.setText(String.valueOf(prefillPostId));
			ControllerEditPost.doLoadPost();
		}

		theStage.setTitle("CSE 360 Foundations: Edit My Post or Reply");
		theStage.setScene(theEditPostScene);
		theStage.show();
	}


	/**********
	 * <p> Method: ViewEditPost() — private constructor </p>
	 *
	 * <p> Description: Builds and configures all GUI widgets exactly once (singleton pattern).
	 * Static layout and event handlers are set here; dynamic data is handled in
	 * displayEditPost(). </p>
	 */
	private ViewEditPost() {

		theRootPane    = new Pane();
		theEditPostScene = new Scene(theRootPane, width, height);


		// ============================ Area 1: Header ============================

		setupLabelUI(label_PageTitle,   "Arial", 22, width, Pos.CENTER,            0,  5);
		setupLabelUI(label_UserDetails, "Arial", 18, width - 220, Pos.BASELINE_LEFT, 20, 52);

		setupButtonUI(button_Return, "Dialog", 16, 170, Pos.CENTER, 608, 44);
		button_Return.setOnAction((_) -> { ControllerEditPost.performReturn(); });


		// ============================ Area 2a: Edit Post ============================

		setupLabelUI(label_EditPostSection, "Arial", 15, width, Pos.CENTER, 0, 108);

		// Post ID row
		setupLabelUI(label_PostId, "Arial", 14, 75, Pos.BASELINE_LEFT, 20, 140);
		text_PostId.setFont(Font.font("Dialog", 14));
		text_PostId.setMinWidth(120);
		text_PostId.setLayoutX(100);
		text_PostId.setLayoutY(136);
		text_PostId.setPromptText("e.g. 3");

		setupButtonUI(button_LoadPost, "Dialog", 14, 120, Pos.CENTER, 240, 133);
		button_LoadPost.setOnAction((_) -> { ControllerEditPost.doLoadPost(); });

		// Post content area
		setupLabelUI(label_PostContent, "Arial", 13, 400, Pos.BASELINE_LEFT, 20, 165);

		textarea_PostContent.setFont(Font.font("Dialog", 13));
		textarea_PostContent.setLayoutX(20);
		textarea_PostContent.setLayoutY(182);
		textarea_PostContent.setPrefWidth(760);
		textarea_PostContent.setPrefHeight(80);
		textarea_PostContent.setWrapText(true);
		textarea_PostContent.setPromptText("Edit your post here…");
		textarea_PostContent.textProperty().addListener((_, _, newVal) ->
				ControllerEditPost.doUpdatePostCharCount(newVal));

		// Character counter — right-aligned beneath the TextArea
		setupLabelUI(label_PostCharCount, "Dialog", 12, 760, Pos.BASELINE_RIGHT, 20, 265);

		// Action buttons
		setupButtonUI(button_SavePost, "Dialog", 15, 165, Pos.CENTER, 175, 283);
		button_SavePost.setOnAction((_) -> { ControllerEditPost.doSavePost(); });

		setupButtonUI(button_DeletePost, "Dialog", 15, 150, Pos.CENTER, 420, 283);
		button_DeletePost.setStyle("-fx-text-fill: #cc0000;");
		button_DeletePost.setOnAction((_) -> { ControllerEditPost.doDeletePost(); });

		// Inline feedback label
		setupLabelUI(label_PostFeedback, "Arial", 12, 760, Pos.BASELINE_LEFT, 20, 308);
		label_PostFeedback.setWrapText(true);


		// ============================ Area 2b: Edit Reply ============================

		setupLabelUI(label_EditReplySection, "Arial", 15, width, Pos.CENTER, 0, 335);

		// Reply ID row
		setupLabelUI(label_ReplyId, "Arial", 14, 80, Pos.BASELINE_LEFT, 20, 365);
		text_ReplyId.setFont(Font.font("Dialog", 14));
		text_ReplyId.setMinWidth(120);
		text_ReplyId.setLayoutX(105);
		text_ReplyId.setLayoutY(361);
		text_ReplyId.setPromptText("e.g. 5");

		setupButtonUI(button_LoadReply, "Dialog", 14, 120, Pos.CENTER, 245, 358);
		button_LoadReply.setOnAction((_) -> { ControllerEditPost.doLoadReply(); });

		// Reply content area
		setupLabelUI(label_ReplyContent, "Arial", 13, 200, Pos.BASELINE_LEFT, 20, 393);

		textarea_ReplyContent.setFont(Font.font("Dialog", 13));
		textarea_ReplyContent.setLayoutX(20);
		textarea_ReplyContent.setLayoutY(410);
		textarea_ReplyContent.setPrefWidth(760);
		textarea_ReplyContent.setPrefHeight(72);
		textarea_ReplyContent.setWrapText(true);
		textarea_ReplyContent.setPromptText("Edit your reply here…");

		// Save Reply and Delete Reply buttons — side by side
		setupButtonUI(button_SaveReply, "Dialog", 15, 165, Pos.CENTER, 175, 490);
		button_SaveReply.setOnAction((_) -> { ControllerEditPost.doSaveReply(); });

		setupButtonUI(button_DeleteReply, "Dialog", 15, 150, Pos.CENTER, 420, 490);
		button_DeleteReply.setStyle("-fx-text-fill: #cc0000;");
		button_DeleteReply.setOnAction((_) -> { ControllerEditPost.doDeleteReply(); });

		// Inline feedback label
		setupLabelUI(label_ReplyFeedback, "Arial", 12, 760, Pos.BASELINE_LEFT, 20, 515);
		label_ReplyFeedback.setWrapText(true);


		// ============================ Area 3: Footer ============================

		setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20,  540);
		button_Logout.setOnAction((_) -> { ControllerEditPost.performLogout(); });

		setupButtonUI(button_Quit,   "Dialog", 18, 250, Pos.CENTER, 300, 540);
		button_Quit.setOnAction((_) -> { ControllerEditPost.performQuit(); });


		// Add all widgets to the root Pane
		theRootPane.getChildren().addAll(
				label_PageTitle, label_UserDetails, button_Return, line_Separator1,
				label_EditPostSection,
				label_PostId, text_PostId, button_LoadPost,
				label_PostContent, textarea_PostContent, label_PostCharCount,
				button_SavePost, button_DeletePost,
				label_PostFeedback,
				line_Separator2,
				label_EditReplySection,
				label_ReplyId, text_ReplyId, button_LoadReply,
				label_ReplyContent, textarea_ReplyContent,
				button_SaveReply, button_DeleteReply, label_ReplyFeedback,
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
