package guiPost;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.User;
import guiStudent.ViewStudentHome;
import guiViewPosts.ViewViewPosts;
import database.Database;

import java.util.ArrayList;
import java.util.List;

import applicationMain.FoundationsMain;

/*******
 * <p> Title: ControllerPost Class. </p>
 * 
 * <p> Description: This class provides the controller actions
 * to allow the user to perform CRUD operations on Posts</p>
 * 
 * @author Azeer Esmail
 * 
 */

public class ControllerPost {
    
    private static Database theDatabase = FoundationsMain.database;
    

	/**********
	 * <p> Method: performReturnToAllPosts(Stage stage, User user) </p>
	 * 
	 * <p> Description: Navigates back to the All Posts view
	 * 
	 * @param stage the gui stage to pass to view
	 * 
	 * @param user current user object
	 * 
	 */	
    public static void performReturnToAllPosts(Stage stage, User user) {
//        ViewPosts.displayPosts(stage, user);
    }
    
    
//	/**********
//	 * <p> Method: performCreatePost(Stage stage, User user, String title, String content) </p>
//	 * 
//	 * <p> Description: Creates a new post and navigates to its view
//	 * 
//	 * @param stage the gui stage to pass to view
//	 * 
//	 * @param user current user object
//	 * 
//	 * @param title of the new post
//	 * 
//	 * @param content of the new post
//	 * 
//	 */	
//    public static void performCreatePost(Stage stage, User user, String title, String content) {
//        // Validate input
//        if (title == null || title.trim().isEmpty()) {
//            System.out.println("Error: Post title cannot be empty");
//            // You could show an alert dialog here
//            return;
//        }
//        
//        if (content == null || content.trim().isEmpty()) {
//            System.out.println("Error: Post content cannot be empty");
//            // You could show an alert dialog here
//            return;
//        }
//        
//        // Create new post object
//        Post newPost = new Post();
//        newPost.setUserId(user.getUserId());
//        newPost.setUserName(user.getUserName());
//        newPost.setTitle(title.trim());
//        newPost.setContent(content.trim());
//        
//        // Save to database
//        Post createdPost = theDatabase.createPost(newPost);
//        
//        if (createdPost != null) {
//            // Navigate to the newly created post
//            ViewPost.displayPost(stage, user, createdPost);
//        } else {
//            System.out.println("Error: Failed to create post");
//            // You could show an alert dialog here
//        }
//    }
    
    
	/**********
	 * <p> Method: performUpdatePost(Stage stage, User user, Post post, String content) </p>
	 * 
	 * <p> Description: Updates a post and navigates to its view
	 * 
	 * @param stage the gui stage to pass to view
	 * 
	 * @param user current user object
	 * 
	 * @param post Post object of the affected post
	 * 
	 * @param content new content to be updated
	 * 
	 */	
    
    public static void performUpdatePost(Stage stage, User user, Post post, String content) {
        if (user.getUserName().equals(post.getAuthorUsername())) {
        	int res = theDatabase.updatePost(post.getPostId(), content, post.getAuthorUsername());
        	if (res == 0) System.out.println("Update successful");
        	post.setContent(content);
        	ViewPost.displayPost(stage, user, post.getPostId());
        } 
    }
    
    
	/**********
	 * <p> Method: performMarkPostDeleted(Stage stage, User user, Post post) </p>
	 * 
	 * <p> Description: Marks a post as deleted in DB and returns to All Posts Page
	 * 
	 * @param stage the gui stage to pass to view
	 * 
	 * @param user current user object
	 * 
	 * @param post Post object of the affected post
	 * 
	 */	
    public static void performMarkPostDeleted(Stage stage, User user, Post post) {
    	int postId = post.getPostId();
    	String postUserUsername = user.getUserName();
        if (post.getAuthorUsername().equals(postUserUsername) || user.getNewStaffRole() || user.getAdminRole()) {
        	theDatabase.softDeletePost(postId, postUserUsername);
        	ViewViewPosts.displayViewPosts(stage, user);
        } 
    }
    
   
//	/**********
//	 * <p> Method: performCancelNewPost(Stage stage, User user) </p>
//	 * 
//	 * <p> Description: Cancels creation of new post
//	 * 
//	 * @param stage the gui stage to pass to view
//	 * 
//	 * @param user current user object
//	 * 
//	 */	
//    public static void performCancelNewPost(Stage stage, User user) {
//        ViewPosts.displayPosts(stage, user);
//    }
//    
    
//	/**********
//	 * <p> Method: performLogout() </p>
//	 * 
//	 * <p> Description: Logs out the current user and returns to login screen
//	 * 
//	 */	
//    public static void performLogout() {
//        guiUserLogin.ViewUserLogin.displayUserLogin(ViewPosts.theStage);
//    }
//    
    
	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: Quits the application
	 * 
	 */	
    public static void performQuit() {
        System.exit(0);
    }
}