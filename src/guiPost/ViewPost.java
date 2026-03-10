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
import guiReply.ControllerReply;
import guiReply.ViewNewReply;
import guiReply.ViewReply;
import guiViewPosts.ViewViewPosts;

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
 * @author Azeer Esmail
 * 
 */


public class ViewPost {
    
    /*-*******************************************************************************************
    Attributes
    */
    
    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;
    
    // GUI Widgets
    protected static Label label_PageTitle = new Label("View Post");
    protected static Label label_UserDetails = new Label();
    
    // Post display elements
    protected static Label label_PostTitle = new Label();
    protected static Label label_PostAuthor = new Label();
    protected static Label label_CreatedTime = new Label();
    protected static Label label_UpdatedTime = new Label();
    protected static TextArea textArea_Content = new TextArea();
    
    // Scrollable area for replies
    protected static ScrollPane scrollPane_Replies = new ScrollPane();
    protected static VBox vbox_Replies = new VBox(15); // 15px spacing between replies
    protected static Label label_RepliesHeader = new Label("Replies");
    
    protected static Line line_Separator1 = new Line(20, 95, width-20, 95);
    protected static Line line_Separator2 = new Line(20, 180, width-20, 180);
    protected static Line line_Separator3 = new Line(20, 500+300, width-20, 500+300);
    protected static Line line_Separator4 = new Line(20, 500+300, width-20, 500+300);
    
    // Navigation buttons
    protected static Button button_ReturnToAllPosts = new Button("Return to All Posts");
    protected static Button button_Reply = new Button("Reply");
    protected static Button button_Edit = new Button("Edit");
    protected static Button button_Save_Edit = new Button("Save");
    protected static Button button_Delete = new Button("Delete");
    protected static Button button_Logout = new Button("Logout");
    protected static Button button_Quit = new Button("Quit");
    
    private static ViewPost theView;
    private static Database theDatabase = applicationMain.FoundationsMain.database;
    
    protected static Stage theStage;
    protected static Pane theRootPane;
    protected static User theUser;
    protected static Post currentPost;
    
    private static Scene theViewPostScene;
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    
	/**********
	 * <p> Method: displayPost(Stage ps, User user) </p>
	 * 
	 * <p> Description: displays the Post in a new page
	 * 
	 * @param stage the gui stage to pass to view
	 * 
	 * @param user current user object
	 * 
	 */	
    public static void displayPost(Stage ps, User user, int postId) {
        theStage = ps;
        theUser = user;
        currentPost = theDatabase.getPostById(postId);
        
        theView = null;
        theView = new ViewPost();
        
        // Populate post details
        populatePostDetails();
        
        // Load and display replies
        loadReplies();
        
        label_UserDetails.setText("User: " + theUser.getUserName());
        
        theStage.setTitle("CSE 360 Foundations: View Post");
        theStage.setScene(theViewPostScene);
        theStage.show();
    }
    
	/**********
	 * <p> Method: ViewPost()  </p>
	 * 
	 * <p> Description: This constructor initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object.</p>
	 * 
	 */
    private ViewPost() {
        theRootPane = new Pane();
//        theViewPostScene = new Scene(theRootPane, width, height);
        double extendedHeight = height + 270; // Add height for replies section
        theViewPostScene = new Scene(theRootPane, width, extendedHeight);
        
        // GUI Area 1 - Title and user info
        label_PageTitle.setText("View Post");
        setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);
        
        label_UserDetails.setText("User: " + (theUser != null ? theUser.getUserName() : ""));
        setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
        
        // GUI Area 2 - Post metadata
        setupLabelUI(label_PostTitle, "Arial", 24, width-100, Pos.BASELINE_LEFT, 50, 110);
        label_PostTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        setupLabelUI(label_PostAuthor, "Arial", 14, 300, Pos.BASELINE_LEFT, 50, 145);
        
        setupLabelUI(label_CreatedTime, "Arial", 12, 300, Pos.BASELINE_LEFT, 50, 165);
        
        setupLabelUI(label_UpdatedTime, "Arial", 12, 300, Pos.BASELINE_LEFT, 350, 165);
        
        // GUI Area 3 - Post content
        textArea_Content.setLayoutX(50);
        textArea_Content.setLayoutY(195);
        textArea_Content.setPrefWidth(width - 100);
        textArea_Content.setPrefHeight(200);
        textArea_Content.setWrapText(true);
        textArea_Content.setEditable(false);
        textArea_Content.setFont(Font.font("Arial", 14));
        
        // GUI Area 4 - Replies header
        setupLabelUI(label_RepliesHeader, "Arial", 18, 200, Pos.BASELINE_LEFT, 50, 410);
        label_RepliesHeader.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
     // GUI Area 5 - Scrollable replies section
        vbox_Replies.setStyle("-fx-padding: 10px;");

        scrollPane_Replies.setLayoutX(50);
        scrollPane_Replies.setLayoutY(440);
        scrollPane_Replies.setPrefWidth(width - 100);
        scrollPane_Replies.setPrefHeight(300);
        scrollPane_Replies.setContent(vbox_Replies);
        scrollPane_Replies.setFitToWidth(true);
        scrollPane_Replies.setStyle("-fx-background: white; -fx-border-color: #e0e0e0;");

        // Store reference to vbox_Replies for access in button handler
        final VBox repliesContainer = vbox_Replies;

        // Action buttons at the bottom of post
        setupButtonUI(button_Reply, "Dialog", 18, 100, Pos.CENTER, 20, 750);
        button_Reply.setOnAction(_ -> {
            // Check if reply input is already showing
            boolean alreadyShowing = false;
            for (javafx.scene.Node node : repliesContainer.getChildren()) {
                if (node instanceof ViewNewReply) {
                    alreadyShowing = true;
                    break;
                }
            }
            
            if (!alreadyShowing) {
                // Create and add the new reply input at the top of replies section
                ViewNewReply newReplyView = new ViewNewReply(theStage, theUser, currentPost, repliesContainer);
                repliesContainer.getChildren().add(newReplyView);
                // Wait for the layout to complete before scrolling
                try { Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();} 
                javafx.application.Platform.runLater(() -> {
                    javafx.application.Platform.runLater(() -> {
                        scrollPane_Replies.setVvalue(1.0); // Scroll to bottom to show the new reply input
                    });
                });
            }
        });
        
        // Action buttons at the bottom of post
        if ((theUser.getUserName().equals(currentPost.getAuthorUsername()) || theUser.getNewStaffRole() || theUser.getAdminRole())
        		&& !currentPost.isDeleted()) {
            setupButtonUI(button_Delete, "Dialog", 18, 100, Pos.CENTER, 620, 750);
            button_Delete.setOnAction(_ -> {
                alert.setTitle("Confirmation Dialog");
                alert.setHeaderText("Please Confirm Deletion");
                alert.setContentText("Are you sure you want to delete this post?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    ControllerPost.performMarkPostDeleted(theStage, theUser, currentPost);
                }
            });
            theRootPane.getChildren().addAll(button_Delete);
        }
        
        
        
        if (theUser.getUserName().equals(currentPost.getAuthorUsername())
        		&&(currentPost.isDeleted() != true)) {

            button_Edit.setVisible(true);
            setupButtonUI(button_Edit, "Dialog", 18, 100, Pos.CENTER, 320, 750);
            button_Edit.setOnAction(_ -> {
                textArea_Content.setEditable(true);
                button_Edit.setVisible(false);
                button_Save_Edit.setVisible(true);
            });
            
            setupButtonUI(button_Delete, "Dialog", 18, 100, Pos.CENTER, 620, 750);
            button_Delete.setOnAction(_ -> {
                alert.setTitle("Confirmation Dialog");
                alert.setHeaderText("Please Confirm Deletion");
                alert.setContentText("Are you sure you want to delete this post?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    ControllerPost.performMarkPostDeleted(theStage, theUser, currentPost);
                }
            });
            
            button_Save_Edit.setVisible(false);
            button_Save_Edit.setOnAction(_ -> {
                textArea_Content.setEditable(false);
                button_Edit.setVisible(true);
                button_Save_Edit.setVisible(false);
                ControllerPost.performUpdatePost(theStage, theUser, currentPost, textArea_Content.getText());
            });
            setupButtonUI(button_Save_Edit, "Dialog", 18, 100, Pos.CENTER, 320, 750);
            
            theRootPane.getChildren().addAll(button_Edit, button_Save_Edit);
        }
        
        // GUI Area 6 - Navigation buttons
        setupButtonUI(button_ReturnToAllPosts, "Dialog", 18, 150, Pos.CENTER, 20, 810);
        button_ReturnToAllPosts.setOnAction(_ -> {
        	ViewViewPosts.displayViewPosts(theStage, theUser);
        });
        
        setupButtonUI(button_Logout, "Dialog", 18, 150, Pos.CENTER, 320, 810);
        button_Logout.setOnAction(_ -> { 
            ControllerPost.performLogout(theStage);
        });
        
        setupButtonUI(button_Quit, "Dialog", 18, 150, Pos.CENTER, 620, 810);
        button_Quit.setOnAction(_ -> { 
            ControllerPost.performQuit();
        });
        
        // Add all widgets to the root pane
        theRootPane.getChildren().addAll(
            label_PageTitle, label_UserDetails,
            label_PostTitle, label_PostAuthor, label_CreatedTime, label_UpdatedTime,
            textArea_Content, label_RepliesHeader, scrollPane_Replies,
            line_Separator1, line_Separator2, line_Separator3, line_Separator4,
            button_ReturnToAllPosts, button_Reply, button_Logout, button_Quit
        );
    }
    
    /*-*******************************************************************************************
    Helper Methods
    */
    
    
	/**********
	 * Private local method to populate post details and GUI 
	 * 
	 */
    private static void populatePostDetails() {
        if (currentPost != null) {
            label_PostTitle.setText((String) currentPost.getTitle());
            label_PostAuthor.setText("Posted by: " + currentPost.getAuthorUsername());
            
            if (currentPost.getTimestamp() != null) {
                LocalDateTime created = (LocalDateTime) currentPost.getTimestamp();
                label_CreatedTime.setText("Created: " + created.format(dateFormatter));
            } else {
                label_CreatedTime.setText("Created: Unknown");
            }
            
//            if (currentPost.getAttributes().get("updated") != null) {
//                LocalDateTime updated = (LocalDateTime) currentPost.getAttributes().get("updated");
//                label_UpdatedTime.setText("Last Updated: " + (updated != null ? updated.format(dateFormatter) : "Unknown"));
//            } else {
//                label_UpdatedTime.setText("Last Updated: Unknown");
//            }
            
            textArea_Content.setText((String)currentPost.getContent());
        }
    }
    
    
	/**********
	 * Private local method to populate/load and reload post's replies details and GUI 
	 * 
	 */
    private static void loadReplies() {
        // Clear existing replies
        vbox_Replies.getChildren().clear();
        
        // Get replies from database
        List<Reply> replies = ControllerReply.performGetReplies(currentPost);
        
        if (replies != null && !replies.isEmpty()) {
            for (Reply reply : replies) {
                // Create a ReplyView for each reply and add to the VBox
            	ViewReply replyView = new ViewReply(reply, theUser, theStage, currentPost);
                vbox_Replies.getChildren().add(replyView);
            }
        } else {
            // Show a message when there are no replies
            Label noRepliesLabel = new Label("No replies yet. Click 'Reply' to be the first!");
            noRepliesLabel.setFont(Font.font("Arial", 14));
            noRepliesLabel.setStyle("-fx-text-fill: #7f8d8c; -fx-padding: 20px; -fx-alignment: center;");
            noRepliesLabel.setMaxWidth(Double.MAX_VALUE);
            noRepliesLabel.setAlignment(Pos.CENTER);
            vbox_Replies.getChildren().add(noRepliesLabel);
        }
    }
    
	/**********
	 * Private local method to initialize the standard fields for a label
	 * 
	 * @param l		The Label object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
    private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);
    }


	/**********
	 * Private local method to initialize the standard fields for a button
	 * 
	 * @param b		The Button object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
	    b.setFont(Font.font(ff, f));
	    b.setMinWidth(w);
	    b.setAlignment(p);
	    b.setLayoutX(x);
	    b.setLayoutY(y);
	}
}