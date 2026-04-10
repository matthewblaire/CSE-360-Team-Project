package guiPost;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.User;
import guiBrowsePosts.ViewBrowsePosts;
import guiReply.ControllerReply;
import guiReply.ViewNewReply;
import guiReply.ViewReply;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import database.Database;

/*******
 * <p> Title: ViewPost Class. </p>
 *
 * <p> Description: This class provides the JavaFX GUI widgets to enable
 * the user to view a post in the system and interact with it. </p>
 *
 * @author CSE 360 Team
 *
 */


public class ViewPost {

    /*-*******************************************************************************************
    Attributes
    */

    private static double width  = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    // GUI Widgets
    protected static Label label_PageTitle   = new Label("View Post");
    protected static Label label_UserDetails = new Label();

    // Post display elements
    protected static Label    label_PostTitle   = new Label();
    protected static Label    label_PostMeta    = new Label(); // author + date on one line
    protected static TextArea textArea_Content  = new TextArea();

    // Scrollable area for replies
    protected static ScrollPane scrollPane_Replies  = new ScrollPane();
    protected static VBox       vbox_Replies        = new VBox(10);
    protected static Label      label_RepliesHeader = new Label("Replies");

    // Separator lines
    protected static Line line_Separator1 = new Line(20, 95, width - 20, 95);
    protected static Line line_Separator2 = new Line(20, 525, width - 20, 525);

    // Navigation buttons
    protected static Button button_ReturnToAllPosts = new Button("Return");
    protected static Button button_Reply            = new Button("Reply");
    protected static Button button_Edit             = new Button("Edit");
    protected static Button button_Save_Edit        = new Button("Save");
    protected static Button button_Delete           = new Button("Delete");
    protected static Button button_Logout           = new Button("Logout");
    protected static Button button_Quit             = new Button("Quit");
    protected static Button button_Feedback         = new Button("Feedback");

    private static Database  theDatabase = applicationMain.FoundationsMain.database;

    protected static Stage theStage;
    protected static Pane  theRootPane;
    protected static User  theUser;
    protected static Post  currentPost;

    private static Scene            theViewPostScene;
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy  h:mm a");
    private static Alert            alert = new Alert(Alert.AlertType.CONFIRMATION);

	/**********
	 * <p> Method: displayPost(Stage ps, User user, int postId) </p>
	 *
	 * <p> Description: Displays the Post page for the given post ID. </p>
	 *
	 * @param ps     the primary JavaFX Stage
	 * @param user   the currently logged-in User
	 * @param postId the ID of the post to display
	 */
    public static void displayPost(Stage ps, User user, int postId) {
        theStage = ps;
        theUser  = user;
        currentPost = theDatabase.getPostById(postId);

        new ViewPost();

        // Mark post and all its replies as read for the current user
        theDatabase.markPostAsRead(postId, theUser.getUserName());
        List<Reply> allReplies = theDatabase.getRepliesForPost(postId);
        if (allReplies != null) {
            for (Reply r : allReplies) {
                theDatabase.markReplyAsRead(r.getReplyId(), theUser.getUserName());
            }
        }

        populatePostDetails();
        loadReplies();

        label_UserDetails.setText("User: " + theUser.getUserName());

        theStage.setTitle("CSE 360 Foundations: View Post");
        theStage.setScene(theViewPostScene);
        theStage.show();
    }

	/**********
	 * <p> Method: ViewPost() — private constructor </p>
	 *
	 * <p> Description: Builds and configures all GUI widgets (singleton pattern).
	 * Static layout and event handlers are set here; dynamic data is handled in
	 * displayPost(). </p>
	 */
    private ViewPost() {
        theRootPane = new Pane();
        theViewPostScene = new Scene(theRootPane, width, height);
        theRootPane.setStyle("-fx-background-color: #f5f6fa;");

        // ── Area 1: Header ───────────────────────────────────────────────────────
        setupLabelUI(label_PageTitle, "Arial", 24, width, Pos.CENTER, 0, 5);

        setupLabelUI(label_UserDetails, "Arial", 18, width - 220, Pos.BASELINE_LEFT, 20, 52);

        setupButtonUI(button_ReturnToAllPosts, "Dialog", 16, 170, Pos.CENTER, 608, 44);
        button_ReturnToAllPosts.setOnAction(_ -> {
            ViewBrowsePosts.displayBrowsePosts(theStage, theUser);
        });

        // ── Area 2: Post metadata ─────────────────────────────────────────────────
        setupLabelUI(label_PostTitle, "Arial", 20, width - 40, Pos.BASELINE_LEFT, 20, 108);
        label_PostTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        label_PostTitle.setStyle("-fx-text-fill: #2c3e50;");

        // Author + date combined on one compact line
        setupLabelUI(label_PostMeta, "Arial", 12, width - 40, Pos.BASELINE_LEFT, 20, 136);
        label_PostMeta.setStyle("-fx-text-fill: #7f8c8d;");

        // ── Area 3: Post content ──────────────────────────────────────────────────
        textArea_Content.setLayoutX(20);
        textArea_Content.setLayoutY(158);
        textArea_Content.setPrefWidth(width - 40);
        textArea_Content.setPrefHeight(130);
        textArea_Content.setWrapText(true);
        textArea_Content.setEditable(false);
        textArea_Content.setFont(Font.font("Arial", 14));
        textArea_Content.setStyle(
            "-fx-control-inner-background: white;" +
            "-fx-background-color: white;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-background-radius: 5;" +
            "-fx-border-radius: 5;"
        );

        // ── Area 4: Replies header row + action buttons ───────────────────────────
        setupLabelUI(label_RepliesHeader, "Arial", 15, 200, Pos.BASELINE_LEFT, 20, 304);
        label_RepliesHeader.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        label_RepliesHeader.setStyle("-fx-text-fill: #2c3e50;");

        // Reply button — always shown, anchored to the right of the header row
        setupButtonUI(button_Reply, "Dialog", 13, 90, Pos.CENTER, width - 115, 297);
        button_Reply.setStyle("-fx-background-color: #4a90d9; -fx-text-fill: white; -fx-background-radius: 5;");

        final VBox repliesContainer = vbox_Replies;
        button_Reply.setOnAction(_ -> {
            boolean alreadyShowing = false;
            for (javafx.scene.Node node : repliesContainer.getChildren()) {
                if (node instanceof ViewNewReply) {
                    alreadyShowing = true;
                    break;
                }
            }
            if (!alreadyShowing) {
                ViewNewReply newReplyView = new ViewNewReply(theStage, theUser, currentPost, repliesContainer);
                repliesContainer.getChildren().add(newReplyView);
                try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
                javafx.application.Platform.runLater(() ->
                    javafx.application.Platform.runLater(() ->
                        scrollPane_Replies.setVvalue(1.0)));
            }
        });

        // Edit / Save / Delete — only added when the user has permission
        boolean isAuthor = theUser.getUserName().equals(currentPost.getAuthorUsername());
        boolean isStaffOrAdmin = theUser.getNewStaffRole() || theUser.getAdminRole();

        if (isAuthor && !currentPost.isDeleted()) {
            setupButtonUI(button_Edit, "Dialog", 13, 90, Pos.CENTER, width - 220, 297);
            button_Edit.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5;");
            button_Edit.setOnAction(_ -> {
                textArea_Content.setEditable(true);
                button_Edit.setVisible(false);
                button_Save_Edit.setVisible(true);
            });

            setupButtonUI(button_Save_Edit, "Dialog", 13, 90, Pos.CENTER, width - 220, 297);
            button_Save_Edit.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 5;");
            button_Save_Edit.setVisible(false);
            button_Save_Edit.setOnAction(_ -> {
                textArea_Content.setEditable(false);
                button_Edit.setVisible(true);
                button_Save_Edit.setVisible(false);
                ControllerPost.performUpdatePost(theStage, theUser, currentPost, textArea_Content.getText());
            });

            theRootPane.getChildren().addAll(button_Edit, button_Save_Edit);
        }

        if ((isAuthor || isStaffOrAdmin) && !currentPost.isDeleted()) {
            setupButtonUI(button_Delete, "Dialog", 13, 90, Pos.CENTER, width - 325, 297);
            button_Delete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5;");
            button_Delete.setOnAction(_ -> {
                alert.setTitle("Confirmation Dialog");
                alert.setHeaderText("Please Confirm Deletion");
                alert.setContentText("Are you sure you want to delete this post?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    ControllerPost.performMarkPostDeleted(theStage, theUser, currentPost);
                }
            });
            theRootPane.getChildren().add(button_Delete);
        }
        
        if (isStaffOrAdmin && currentPost != null && !currentPost.isDeleted()) {
            setupButtonUI(button_Feedback, "Dialog", 13, 110, Pos.CENTER, width - 440, 297);
            button_Feedback.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white; -fx-background-radius: 5;");
            button_Feedback.setOnAction(_ -> {
                guiMessageThread.ViewMessageThread.displayMessageThread(
                        theStage,
                        theUser,
                        currentPost.getAuthorUsername(),
                        currentPost.getPostId(),
                        -1);
            });
            theRootPane.getChildren().add(button_Feedback);
        }

        // ── Area 5: Replies scroll pane ───────────────────────────────────────────
        vbox_Replies.setStyle("-fx-padding: 8px;");
        vbox_Replies.setFillWidth(true);

        scrollPane_Replies.setLayoutX(20);
        scrollPane_Replies.setLayoutY(325);
        scrollPane_Replies.setPrefWidth(width - 40);
        scrollPane_Replies.setPrefHeight(188);
        scrollPane_Replies.setContent(vbox_Replies);
        scrollPane_Replies.setFitToWidth(true);
        scrollPane_Replies.setStyle(
            "-fx-background: white;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;"
        );

        // ── Area 6: Footer ────────────────────────────────────────────────────────
        setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
        button_Logout.setOnAction(_ -> { ControllerPost.performLogout(theStage); });

        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
        button_Quit.setOnAction(_ -> { ControllerPost.performQuit(); });

        // Add all permanent widgets to the root pane
        theRootPane.getChildren().addAll(
            label_PageTitle, label_UserDetails, button_ReturnToAllPosts,
            line_Separator1,
            label_PostTitle, label_PostMeta,
            textArea_Content,
            label_RepliesHeader, button_Reply,
            scrollPane_Replies,
            line_Separator2,
            button_Logout, button_Quit
        );
    }

    /*-*******************************************************************************************
    Helper Methods
    */

	/**********
	 * Populates the post title, combined author/date meta line, and content area.
	 */
    private static void populatePostDetails() {
        if (currentPost != null) {
            label_PostTitle.setText(currentPost.getTitle());

            String author = currentPost.getAuthorUsername();
            String dateStr = "Unknown";
            if (currentPost.getTimestamp() != null) {
                LocalDateTime created = (LocalDateTime) currentPost.getTimestamp();
                dateStr = created.format(dateFormatter);
            }
            label_PostMeta.setText("Posted by " + author + "  ·  " + dateStr);

            textArea_Content.setText(currentPost.getContent());
        }
    }

	/**********
	 * Clears and reloads the replies list from the database.
	 */
    private static void loadReplies() {
        vbox_Replies.getChildren().clear();

        List<Reply> replies = ControllerReply.performGetReplies(currentPost);

        if (replies != null && !replies.isEmpty()) {
            for (Reply reply : replies) {
                ViewReply replyView = new ViewReply(reply, theUser, theStage, currentPost);
                vbox_Replies.getChildren().add(replyView);
            }
        } else {
            Label noRepliesLabel = new Label("No replies yet — be the first to reply!");
            noRepliesLabel.setFont(Font.font("Arial", 13));
            noRepliesLabel.setStyle("-fx-text-fill: #aaaaaa; -fx-padding: 20px;");
            noRepliesLabel.setMaxWidth(Double.MAX_VALUE);
            noRepliesLabel.setAlignment(Pos.CENTER);
            vbox_Replies.getChildren().add(noRepliesLabel);
        }
    }

	/**********
	 * Initialises the standard fields for a Label.
	 */
    private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);
    }

	/**********
	 * Initialises the standard fields for a Button.
	 */
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
	    b.setFont(Font.font(ff, f));
	    b.setMinWidth(w);
	    b.setAlignment(p);
	    b.setLayoutX(x);
	    b.setLayoutY(y);
	}
}
