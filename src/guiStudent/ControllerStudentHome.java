package guiStudent;


/*******
 * <p> Title: ControllerStudentHome Class. </p>
 * 
 * <p> Description: The Java/FX-based Student Home Page.  This class provides the controller
 * actions basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page is a stub for establish future roles for the application.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-17 Initial version
 * @version 1.01		2025-09-16 Update Javadoc documentation *  
 */

public class ControllerStudentHome {

	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */

	/**
	 * Default constructor is not used.
	 */
	public ControllerStudentHome() {
	}

	/**********
	 * <p> Method: goToCreatePost() </p>
	 *
	 * <p> Description: Navigates the student to the Create Post / Reply page (guiCreatePost)
	 * where they can submit a new post to a discussion thread or reply to an existing post.
	 * This satisfies the Phase 2 user story: "As a student, I can post statements and questions
	 * and receive replies so I can benefit from the insights and ideas of others." </p>
	 */
	protected static void goToCreatePost() {
		guiCreatePost.ViewCreatePost.displayCreatePost(ViewStudentHome.theStage,
				ViewStudentHome.theUser);
	}


	/**********
	 * <p> Method: goToBrowsePosts() </p>
	 *
	 * <p> Description: Navigates the student to the Browse Posts page (guiViewPosts) where
	 * they can read all non-deleted posts in any discussion thread, see reply counts and unread
	 * indicators, and view the full reply list for any selected post.
	 * This satisfies the Phase 2 user story: "As a student, I can browse posts others have
	 * made so I can learn from and contribute to ongoing discussions." </p>
	 */
	protected static void goToBrowsePosts() {
		guiViewPosts.ViewViewPosts.displayViewPosts(ViewStudentHome.theStage,
				ViewStudentHome.theUser);
	}


	/**********
	 * <p> Method: goToMyPosts() </p>
	 *
	 * <p> Description: Navigates the student to the My Posts page (guiMyPosts) where they
	 * can see all of their own posts with per-post reply counts and unread reply counts.
	 * This satisfies the Phase 2 user story: "As a student, I can see which posts have been
	 * replied to and which replies have not been read by me." </p>
	 */
	protected static void goToMyPosts() {
		guiMyPosts.ViewMyPosts.displayMyPosts(ViewStudentHome.theStage,
				ViewStudentHome.theUser);
	}


	/**********
	 * <p> Method: goToSearchPosts() </p>
	 *
	 * <p> Description: Navigates the student to the Search Posts page (guiSearchPosts) where
	 * they can search all posts by keyword with an optional thread filter.
	 * This satisfies the Phase 2 user story: "As a student, I can search for posts to quickly
	 * find help on a specific topic." </p>
	 */
	protected static void goToSearchPosts() {
		guiSearchPosts.ViewSearchPosts.displaySearchPosts(ViewStudentHome.theStage,
				ViewStudentHome.theUser);
	}


	/**********
	 * <p> Method: performUpdate() </p>
	 * 
	 * <p> Description: This method directs the user to the User Update Page so the user can change
	 * the user account attributes. </p>
	 * 
	 */
	protected static void performUpdate () {
		guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewStudentHome.theStage, ViewStudentHome.theUser);
	}	

	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: This method logs out the current user and proceeds to the normal login
	 * page where existing users can log in or potential new users with a invitation code can
	 * start the process of setting up an account. </p>
	 * 
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewStudentHome.theStage);
	}
	
	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program.  It leaves the
	 * database in a state where the normal login page will be displayed when the application is
	 * restarted.</p>
	 * 
	 */	
	protected static void performQuit() {
		System.exit(0);
	}
}
