package guiUserMetrics;

import javafx.stage.Stage;
import database.Database;
import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.User;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/*******
 * <p> Title: ControllerPost Class. </p>
 * 
 * <p> Description: This class provides the controller actions
 * to allow the staff to retrieve user metrics </p>
 * 
 * @author Azeer Esmail - Team 2
 * 
 */

public class ControllerUserMetrics {
	
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**
	 * Default constructor is not used.
	 */
	public ControllerUserMetrics() {
	}

    /**
	 * <p> Method: getUserMetrics(Stage stage, User currentUser, String selectedUser) </p>
	 * 
	 * <p> Description: Gets the metrics for a specified user </p>
	 * @param stage     the primary JavaFX Stage
	 * @param currentUser   the currently logged-in User
     * @param selectedUser	the user to show Metrics for
     * @return Hashtable key-value table of metric-name: metric-data 
     */
    public static Hashtable<String, Object> getUserMetrics(Stage stage, User currentUser, String selectedUser) {
    	if (currentUser.getNewStudentRole() == true) return null; 	
    	
    	Hashtable<String, Object> metrics = new Hashtable<>();
        List<Post> posts = theDatabase.getPostsByAuthor(selectedUser);
        List<Reply> replies = theDatabase.getRepliesByAuthor(selectedUser);
        
        List<Object[]> postsMetrics = new ArrayList<>();
        for (Post post : posts) {
        	postsMetrics.add(new Object[]{post.getTimestamp(), post.getContent().length()});
        }   
        
        List<Object[]> repliessMetrics = new ArrayList<>();
        for (Reply reply : replies) {
        	repliessMetrics.add(new Object[]{reply.getTimestamp(), reply.getContent().length()});
        }   
        
        metrics.put("postsMetrics", postsMetrics);
        metrics.put("repliesMetrics", repliessMetrics);
        return metrics;
    }
    
	
    /**
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: Logs out the current user and returns to login screen
     * @param stage the primary JavaFX Stage
     */
    public static void performLogout(Stage stage) {
        guiUserLogin.ViewUserLogin.displayUserLogin(stage);
    }
    
    
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