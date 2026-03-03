package guiReply;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.shape.Line;
import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javafx.scene.layout.HBox;

/*******
 * <p> Title: ViewReply Class. </p>
 * 
 * <p> Description: This class provides the JavaFX GUI widgets to enable
 * the user to view a reply in the system and interact with it. </p>
 * 
 * @author Azeer Esmail
 * 
 */

public class ViewReply extends VBox {
	
    /*-*******************************************************************************************
    Attributes
    */
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH - 100;
    private String authorUsername;
    private boolean isDeleted;
    private Post post;
    private Stage theStage;

    
	/**********
	 * <p> Method: ViewReply(Reply reply, User theUser, Stage theStage, Post post) </p>
	 * 
	 * <p> Description: This constructor initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object.</p>
	 * 
	 */	
    public ViewReply(Reply reply, User theUser, Stage theStage, Post post) {
        super(10); // 10px spacing between elements
        
        this.authorUsername = reply.getAuthorUsername();
        this.isDeleted = reply.isDeleted();
        this.post = post;
        this.theStage = theStage;
        
        setupReplyView(reply, theUser);
    }
    
    
	/**********
	 * <p> Method: setupReplyView(Stage ps, User user) </p>
	 * 
	 * <p> Description: displays the Reply in a parent element to be view under its post
	 * 
	 * @param stage the gui stage to pass to view
	 * 
	 * @param user current user object
	 * 
	 */	
    private void setupReplyView(Reply reply, User theUser) {
        // Reply author
        Label label_Author = new Label("Replied by: " + reply.getAuthorUsername());
        label_Author.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        label_Author.setStyle("-fx-text-fill: #2c3e50;");
        
        // Reply content
        Label label_Content = new Label((String) reply.getContent());
        label_Content.setFont(Font.font("Arial", 14));
        label_Content.setWrapText(true);
        label_Content.setMaxWidth(width - 40);
        label_Content.setStyle("-fx-padding: 10px; -fx-background-color: #f8f9fa; -fx-background-radius: 5px;");
        
        // Timestamps
        String createdStr = "Unknown";
        String updatedStr = "Unknown";
        
        if (reply.getTimestamp() != null) {
            LocalDateTime created = (LocalDateTime) reply.getTimestamp();
            createdStr = created.format(dateFormatter);
        }
        
//        if (reply.getAttributes().get("updated") != null) {
//            LocalDateTime updated = (LocalDateTime) reply.getAttributes().get("updated");
//            updatedStr = updated.format(dateFormatter);
//        }
        
        Label label_Timestamps = new Label("Created: " + createdStr + " | Last Updated: " + updatedStr);
        label_Timestamps.setFont(Font.font("Arial", 11));
        label_Timestamps.setStyle("-fx-text-fill: #7f8c8d;");
        
        // Separator line
        Line separator = new Line(0, 0, width - 40, 0);
        separator.setStyle("-fx-stroke: #bdc3c7; -fx-stroke-width: 1;");

        Button button_Edit = new Button("Edit");
        Button button_Save_Edit = new Button("Save");
        Button button_Delete = new Button("Delete");
        button_Edit.setVisible(false);
        button_Save_Edit.setVisible(false);
        button_Delete.setVisible(false);
        
        // Create a container for buttons
        HBox buttonContainer = new HBox(10); // 10px spacing between buttons
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);

        // Create a TextField for editing (initially hidden)
        TextField textField_Edit = new TextField();
        textField_Edit.setFont(Font.font("Arial", 14));
        textField_Edit.setMaxWidth(width - 40);
        textField_Edit.setVisible(false);
        textField_Edit.setStyle("-fx-padding: 10px; -fx-background-color: white; -fx-border-color: #3498db; -fx-border-radius: 5px;");

        // Style the buttons for inline display within the reply
        button_Edit.setFont(Font.font("Arial", 12));
        button_Edit.setMinWidth(70);
        button_Edit.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        button_Edit.setOnAction(_ -> {
            // Hide the content label and show the edit field
            label_Content.setVisible(false);
            textField_Edit.setText(label_Content.getText()); // Set current content
            textField_Edit.setVisible(true);
            
            // Toggle button visibility
            button_Edit.setVisible(false);
            button_Delete.setVisible(false);
            button_Save_Edit.setVisible(true);
        });

        button_Save_Edit.setFont(Font.font("Arial", 12));
        button_Save_Edit.setMinWidth(70);
        button_Save_Edit.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        button_Save_Edit.setVisible(false); // Initially hidden
        button_Save_Edit.setOnAction(_ -> {
            String newContent = textField_Edit.getText();
            if (newContent == null || newContent.trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error");
                alert.setContentText("Reply content cannot be empty");
                alert.showAndWait();
                return;
            }
            
            // Call controller to update reply
//            ControllerReply.performUpdateReply(reply, theUser, newContent.trim(), theStage, post);
        });

        button_Delete.setFont(Font.font("Arial", 12));
        button_Delete.setMinWidth(70);
        button_Delete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        button_Delete.setOnAction(_ -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Please Confirm Deletion");
            alert.setContentText("Are you sure you want to delete this reply?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
//                ControllerReply.performMarkReplyDeleted(reply, theUser, theStage, post);
            }
        });

        // Show buttons if user is the author and reply is not deleted
        if (theUser.getUserName() == this.authorUsername && !this.isDeleted) {
            button_Edit.setVisible(true);
            buttonContainer.getChildren().addAll(button_Edit, button_Save_Edit);
        } else {
            buttonContainer.getChildren().clear();
        }
        
        // Show deleted button if user is staff or admin and reply is not not deleted
        if ((theUser.getUserName() == this.authorUsername || theUser.getAdminRole()|| theUser.getNewStaffRole()) && !this.isDeleted) {
        	button_Delete.setVisible(true);
            buttonContainer.getChildren().addAll(button_Delete);
        } else {
            buttonContainer.getChildren().clear();
        }
        // Then add all elements to the VBox, including the button container and edit field
        getChildren().addAll(label_Author, label_Content, textField_Edit, label_Timestamps, separator, buttonContainer);
        ////////////////////////////////////////////////////////////////////////////////
        
        
        // Set styling for the reply container
        setStyle("-fx-padding: 15px; -fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5px;");
        setMaxWidth(width);
        setMinWidth(width);
    }
}