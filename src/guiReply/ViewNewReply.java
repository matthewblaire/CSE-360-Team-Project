package guiReply;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.SQLException;

import entityClasses.Post;
import entityClasses.User;
import guiReply.ControllerReply;

/*******
 * <p> Title: ViewNewReply Class. </p>
 * 
 * <p> Description: This class provides the JavaFX GUI widgets to enable
 * the user to create a new reply in the system. </p>
 * 
 * @author Azeer Esmail
 * 
 */

public class ViewNewReply extends VBox {
	
    /*-*******************************************************************************************
    Attributes
    */
    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH - 120;
    private static Alert alert = new Alert(Alert.AlertType.INFORMATION);
    
    private TextArea textArea_Reply;
    private Button button_Save;
    private Button button_Cancel;
    private Post currentPost;
    private User currentUser;
    private Stage currentStage;
    private VBox repliesContainer;
    
    
	/**********
	 * <p> Method: ViewNewReply(Stage stage, User user, Post post, VBox container) </p>
	 * 
	 * <p> Description: This constructor initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object.</p>
	 * 
	 */
    public ViewNewReply(Stage stage, User user, Post post, VBox container) {
        super(10);
        this.currentStage = stage;
        this.currentUser = user;
        this.currentPost = post;
        this.repliesContainer = container;
        setupNewReplyView();
    }
    
    
	/**********
	 * <p> Method: setupNewReplyView() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object.</p>
	 * 
	 */
    private void setupNewReplyView() {
        // Reply input area
        Label label_NewReply = new Label("Write your reply:");
        label_NewReply.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        textArea_Reply = new TextArea();
        textArea_Reply.setPrefWidth(width);
        textArea_Reply.setPrefHeight(100);
        textArea_Reply.setWrapText(true);
        textArea_Reply.setFont(Font.font("Arial", 14));
        textArea_Reply.setPromptText("Enter your reply here...");
        
        // Buttons
        button_Save = new Button("Save");
        button_Save.setFont(Font.font("Arial", 14));
        button_Save.setMinWidth(100);
        button_Save.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        button_Save.setOnAction(_ -> {
            String replyContent = textArea_Reply.getText();
            if (replyContent == null || replyContent.trim().isEmpty()) {
                alert.setTitle("Error");
                alert.setHeaderText("Error");
                alert.setContentText("Reply cannot be empty");
                alert.showAndWait();
                return;
            }
            try {
				ControllerReply.performCreateReply(currentStage, currentUser, currentPost, replyContent);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        });
        
        button_Cancel = new Button("Cancel");
        button_Cancel.setFont(Font.font("Arial", 14));
        button_Cancel.setMinWidth(100);
        button_Cancel.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        button_Cancel.setOnAction(_ -> {
            // Remove this reply input from the container
            repliesContainer.getChildren().remove(this);
        });
        
        HBox buttonBox = new HBox(20, button_Save, button_Cancel);
        buttonBox.setAlignment(Pos.CENTER);
        
        // Add all elements
        getChildren().addAll(label_NewReply, textArea_Reply, buttonBox);
        
        // Set styling
        setStyle("-fx-padding: 15px; -fx-background-color: #f0f7fb; -fx-border-color: #3498db; -fx-border-radius: 5px;");
        setMaxWidth(width + 20);
        setMinWidth(width + 20);
    }
}