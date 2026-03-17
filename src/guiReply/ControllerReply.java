package guiReply;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.User;
import guiPost.ViewPost;
import database.Database;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import applicationMain.FoundationsMain;

/*******
 * <p> Title: ControllerReply Class. </p>
 * 
 * <p> Description: This class provides the controller actions
 * to allow the user to perform CRUD operations on Replies</p>
 * 
 * @author Azeer Esmail
 * 
 */

public class ControllerReply {
    
    private static Database theDatabase = FoundationsMain.database;
    
	/**********
	 * <p> Method: performCreateReply(Stage stage, User user, Post post, String content) </p>
	 * 
	 * <p> Description: Creates a new reply and refreshes the post view
	 * 
	 * @param stage the gui stage to pass to view
	 * 
	 * @param user current user object
	 * 
	 * @param post object that the reply is associated with
	 * 
	 * @param content string of the new reply content
	 * @throws SQLException 
	 * 
	 */	
    public static void performCreateReply(Stage stage, User user, Post post, String content) throws SQLException {
        // Create new reply object
        Reply newReply = new Reply();
        newReply.setPostId(post.getPostId());
        newReply.setContent(content);
        newReply.getAuthorUsername(user.getUserName());
        newReply.setTimestamp(LocalDateTime.now());
        
        // Save to database
        int ret = theDatabase.createReply(newReply);
        if (ret > 0) {
            System.out.println("Reply created successfully");
            // Mark the new reply as read for its author immediately
            theDatabase.markReplyAsRead(ret, user.getUserName());
            // Refresh the post view to show the new reply
            ViewPost.displayPost(stage, user, post.getPostId());
        } else {
            System.out.println("Error: Failed to create reply");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText("Failed to create reply. Please try again.");
            alert.showAndWait();
        }
    }
    
	/**********
	 * <p> Method: List<Reply> performGetReplies(Post post) </p>
	 * 
	 * <p> Description: Gets the replies of a certain post from the DB.
	 * 
	 * @param post object to get the replies for
	 * 
	 * @return a list of Replies associated with the post
	 * 
	 */	
    public static List<Reply> performGetReplies(Post post) {
        return theDatabase.getRepliesForPost(post.getPostId());
    }
    
    
	/**********
	 * <p> Method: performUpdateReply(Reply reply, User user, String newContent, Stage stage, Post post) </p>
	 * 
	 * <p> Description: Updates a reply after verifying ownership
	 * 
	 * @param reply object of the Reply to be updated
	 * 
	 * @param content string of the new reply content
	 * 
	 * @param user current user object
	 * 
	 * @param stage the gui stage to pass to view
	 * 
	 * @param post object that the reply is associated with
	 * 
	 */		
    public static void performUpdateReply(Reply reply, User user, String newContent, Stage stage, Post post) {
    	
    	
    	// Verify ownership
    	  boolean currentUserCanEditThisReply = reply.getAuthorUsername().equals(user.getUserName());
    	  if (!currentUserCanEditThisReply)
    	  {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Authorization Error");
            alert.setContentText("You can only edit your own replies.");
            alert.showAndWait();
            return;
    	  }
    	  // try to update the reply in the database
    	  boolean success = theDatabase.updateReply(reply.getReplyId(), newContent, user.getUserName()) == 1;
    	  
    	  // refresh the page on success or show an error on fail
    	  if (success)
    	  {
    		  ViewPost.displayPost(stage, user, post.getPostId());
    	  }
    	  else
    	  {
	          Alert alert = new Alert(Alert.AlertType.ERROR);
	          alert.setTitle("Error");
	          alert.setHeaderText("Update Failed");
	          alert.setContentText("Failed to update reply. Please try again.");
	          alert.showAndWait();
    	  }

    }
    
	/**********
	 * <p> Method: performMarkReplyDeleted(Reply reply, User user, Stage stage, Post post) </p>
	 * 
	 * <p> Description: Marks a reply as deleted after verifying ownership
	 * 
	 * @param reply object of the Reply to be marked
	 * 
	 * @param user current user object
	 * 
	 * @param stage the gui stage to pass to view
	 * 
	 * @param post object that the reply is associated with
	 * 
	 */
    public static void performMarkReplyDeleted(Reply reply, User user, Stage stage, Post post) {
//        // Verify ownership
    	 boolean currentUserCanDeleteThisReply = reply.getAuthorUsername().equals(user.getUserName());
    	 currentUserCanDeleteThisReply = (currentUserCanDeleteThisReply || (user.getAdminRole() || user.getNewStaffRole()));
    	 if (!currentUserCanDeleteThisReply)
    	 {
    		 Alert alert = new Alert(Alert.AlertType.ERROR);
	         alert.setTitle("Error");
	         alert.setHeaderText("Authorization Error");
	         alert.setContentText("You can only delete your own replies.");
	         alert.showAndWait();
	         return;
    	 }
    	 // Mark as deleted in database
    	 boolean success = theDatabase.softDeleteReply(reply.getReplyId(), user.getUserName()) == 1;
    	 if (success) {
            // Refresh the post view to show updated reply list
        	ViewPost.displayPost(stage, user, post.getPostId());
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Delete Failed");
            alert.setContentText("Failed to delete reply. Please try again.");
            alert.showAndWait();
        }
    }
    
}