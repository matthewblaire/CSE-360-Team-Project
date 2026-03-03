package guiReply;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.User;
import guiPost.ViewPost;
import database.Database;

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
    
//	/**********
//	 * <p> Method: performCreateReply(Stage stage, User user, Post post, String content) </p>
//	 * 
//	 * <p> Description: Creates a new reply and refreshes the post view
//	 * 
//	 * @param stage the gui stage to pass to view
//	 * 
//	 * @param user current user object
//	 * 
//	 * @param post object that the reply is associated with
//	 * 
//	 * @param content string of the new reply content
//	 * 
//	 */	
//    public static void performCreateReply(Stage stage, User user, Post post, String content) {
//        // Create new reply object
//        Reply newReply = new Reply();
//        newReply.setPostId((int) post.getAttributes().get("id"));
//        newReply.setUserId(user.getUserId());
//        newReply.setUserName(user.getUserName());
//        newReply.setContent(content.trim());
//        
//        // Save to database
//        Reply createdReply = theDatabase.createReply(newReply);
//        
//        if (createdReply != null) {
//            System.out.println("Reply created successfully");
//            // Refresh the post view to show the new reply
//            ViewPost.displayPost(stage, user, post);
//        } else {
//            System.out.println("Error: Failed to create reply");
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Error");
//            alert.setHeaderText("Error");
//            alert.setContentText("Failed to create reply. Please try again.");
//            alert.showAndWait();
//        }
//    }
    
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
    
    
//	/**********
//	 * <p> Method: performUpdateReply(Reply reply, User user, String newContent, Stage stage, Post post) </p>
//	 * 
//	 * <p> Description: Updates a reply after verifying ownership
//	 * 
//	 * @param reply object of the Reply to be updated
//	 * 
//	 * @param content string of the new reply content
//	 * 
//	 * @param user current user object
//	 * 
//	 * @param stage the gui stage to pass to view
//	 * 
//	 * @param post object that the reply is associated with
//	 * 
//	 */		
//    public static void performUpdateReply(Reply reply, User user, String newContent, Stage stage, Post post) {
//        // Verify ownership
//        int replyUserId = (int) reply.getAttributes().get("userId");
//        
//        if (user.getUserId() != replyUserId) {
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Error");
//            alert.setHeaderText("Authorization Error");
//            alert.setContentText("You can only edit your own replies.");
//            alert.showAndWait();
//            return;
//        }
//        
//        // Update in database
//        Reply updatedReply = theDatabase.updateReply(reply, newContent);
//        
//        if (updatedReply != null) {
//            // Refresh the post view to show updated reply
//            ViewPost.displayPost(stage, user, post);
//        } else {
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Error");
//            alert.setHeaderText("Update Failed");
//            alert.setContentText("Failed to update reply. Please try again.");
//            alert.showAndWait();
//        }
//    }
    
//	/**********
//	 * <p> Method: performMarkReplyDeleted(Reply reply, User user, Stage stage, Post post) </p>
//	 * 
//	 * <p> Description: Marks a reply as deleted after verifying ownership
//	 * 
//	 * @param reply object of the Reply to be marked
//	 * 
//	 * @param user current user object
//	 * 
//	 * @param stage the gui stage to pass to view
//	 * 
//	 * @param post object that the reply is associated with
//	 * 
//	 */
//    public static void performMarkReplyDeleted(Reply reply, User user, Stage stage, Post post) {
//        // Verify ownership
//        int replyUserId = (int) reply.getAttributes().get("userId");
//        int replyId = (int) reply.getAttributes().get("id");
//        
//        if (user.getUserId() != replyUserId && !user.getAdminRole() && !user.getNewStaffRole()) {
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Error");
//            alert.setHeaderText("Authorization Error");
//            alert.setContentText("You can only delete your own replies.");
//            alert.showAndWait();
//            return;
//        }
//        
//        // Mark as deleted in database
//        boolean success = theDatabase.markReplyDeleted(replyId);
//        
//        if (success) {
//            // Refresh the post view to show updated reply list
//        	ViewPost.displayPost(stage, user, post);
//        } else {
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Error");
//            alert.setHeaderText("Delete Failed");
//            alert.setContentText("Failed to delete reply. Please try again.");
//            alert.showAndWait();
//        }
//    }
    
}