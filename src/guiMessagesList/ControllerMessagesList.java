package guiMessagesList;

import java.util.ArrayList;
import java.util.List;

import database.Database;
import entityClasses.Post;
import entityClasses.PrivateMessage;
import entityClasses.User;
import guiPost.ViewPost;
import javafx.collections.FXCollections;
import javafx.stage.Stage;

/*******
 * <p> Title: ControllerMyPosts Class. </p>
 *
 * <p> Description: Controller for the My Posts page.  Provides protected static methods
 * invoked by {@link ViewMessagesList} button and event handlers.
 *
 * Responsibilities:
 * <ul>
 *   <li>Load the current student's posts with per-post reply count and unread reply count.</li>
 *   <li>Handle navigation (Return → Student Home, Logout, Quit).</li>
 * </ul>
 *
 * This controller is a collection of protected static methods — it is never instantiated.
 * All widget access goes through the public static fields of {@link ViewMessagesList}. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author CSE 360 Team
 *
 * @version 1.00	2026-02-23	Initial version for Phase 2 — Student Discussion System
 */
public class ControllerMessagesList {

	/** other user for each item shown in listview_MyMessages; index-aligned. */
	public static List<String> listOfDistinctOtherUsers = new ArrayList<String>();

	/** Database reference. */
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**
	 * Default constructor — not used.
	 */
	public ControllerMessagesList() {
	}


	/**********
	 * <p> Method: doLoadMyPosts() </p>
	 *
	 * <p> Description: Queries the database for all distinct user + otherUser combinations, where each combination is treated as a message thread </p>
	 */
	protected static void doLoadMyMessages() {

		String currentUser = ViewMessagesList.theUser.getUserName();
		
		List<PrivateMessage> messages = theDatabase.getMessagesConcerning(currentUser);
		
		listOfDistinctOtherUsers.clear();

		for (PrivateMessage m : messages)
		{
			String otherUser;
			if (m.senderUsername.equals(currentUser))
			{
				otherUser = m.recipientUsername;
			}
			else
			{
				otherUser = m.senderUsername;
			}
			
			
			if (listOfDistinctOtherUsers.contains(otherUser) || otherUser.equals(currentUser))
			{
				continue;
			}
			else
			{
				listOfDistinctOtherUsers.add(otherUser);
			}
		}

		List<String> displayLines = new ArrayList<>();
		for (String otherUser : listOfDistinctOtherUsers)
		{
			List<PrivateMessage> messagesBetween = theDatabase.getMessagesBetween(currentUser, otherUser);
			String line = "Conversation with " + otherUser + "... " + messagesBetween.size() + " messages total";
			displayLines.add(line);
		}

		ViewMessagesList.listview_MyMessageThreads.setItems(
				FXCollections.observableArrayList(displayLines));

		ViewMessagesList.label_PostCount.setText(
				"Message threads: " + messages.size());
	}


	
	public static void doHandleMessageThreadDoubleClick(Object selectedItem, Stage ps, User user) {
		int idx = ViewMessagesList.listview_MyMessageThreads.getSelectionModel().getSelectedIndex();
		if (idx < 0 || idx >= listOfDistinctOtherUsers.size()) return;
		String otherUser = listOfDistinctOtherUsers.get(idx);
		assert(false);
		// replace with a call to display the message thread between the two users
		// ViewPost.displayPost(ps, user, postId);// displayPost(Stage ps, User user, Post post)
	}
	


	/**********
	 * <p> Method: performReturn() </p>
	 *
	 * <p> Description: Returns the student to the Student Home page. </p>
	 */
	protected static void performReturn() {
		guiStudent.ViewStudentHome.displayStudentHome(
				ViewMessagesList.theStage, ViewMessagesList.theUser);
	}


	/**********
	 * <p> Method: performLogout() </p>
	 *
	 * <p> Description: Logs out the current user and navigates to the login page. </p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewMessagesList.theStage);
	}


	/**********
	 * <p> Method: performQuit() </p>
	 *
	 * <p> Description: Terminates the application. </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}
