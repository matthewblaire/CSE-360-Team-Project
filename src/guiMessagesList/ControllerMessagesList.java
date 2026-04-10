package guiMessagesList;

import java.util.ArrayList;
import java.util.List;

import database.Database;
import entityClasses.PrivateMessage;
import entityClasses.User;
import javafx.collections.FXCollections;
import javafx.stage.Stage;

/*******
 * <p> Title: ControllerMessagesList Class. </p>
 *
 * <p> Description: Controller for the Messages List page. Provides protected static methods
 * invoked by {@link ViewMessagesList} button and event handlers. </p>
 *
 * @author CSE 360 Team
 *
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
	 * <p> Method: doLoadMyMessages() </p>
	 *
	 * <p> Description: Queries the database for all distinct user + otherUser combinations,
	 * where each combination is treated as a message thread. </p>
	 */
	protected static void doLoadMyMessages() {

		String currentUser = ViewMessagesList.theUser.getUserName();

		List<PrivateMessage> messages = theDatabase.getMessagesConcerning(currentUser);

		listOfDistinctOtherUsers.clear();

		for (PrivateMessage m : messages) {
			String otherUser;
			if (m.senderUsername.equals(currentUser)) {
				otherUser = m.recipientUsername;
			}
			else {
				otherUser = m.senderUsername;
			}

			if (listOfDistinctOtherUsers.contains(otherUser) || otherUser.equals(currentUser)) {
				continue;
			}
			else {
				listOfDistinctOtherUsers.add(otherUser);
			}
		}

		List<String> displayLines = new ArrayList<String>();
		for (String otherUser : listOfDistinctOtherUsers) {
			List<PrivateMessage> messagesBetween = theDatabase.getMessagesBetween(currentUser, otherUser);
			String line = "Conversation with " + otherUser + "... "
					+ messagesBetween.size() + " messages total";
			displayLines.add(line);
		}

		ViewMessagesList.listview_MyMessageThreads.setItems(
				FXCollections.observableArrayList(displayLines));

		ViewMessagesList.label_PostCount.setText(
				"Message threads: " + listOfDistinctOtherUsers.size());
	}

	/**********
	 * <p> Method: doHandleMessageThreadDoubleClick(Object selectedItem, Stage ps, User user) </p>
	 *
	 * <p> Description: Opens the selected message thread. </p>
	 *
	 * @param selectedItem the selected list item
	 * @param ps the primary stage
	 * @param user the currently logged-in user
	 */
	public static void doHandleMessageThreadDoubleClick(Object selectedItem, Stage ps, User user) {
		int idx = ViewMessagesList.listview_MyMessageThreads.getSelectionModel().getSelectedIndex();
		if (idx < 0 || idx >= listOfDistinctOtherUsers.size()) return;
		String otherUser = listOfDistinctOtherUsers.get(idx);
		guiMessageThread.ViewMessageThread.displayMessageThread(ps, user, otherUser);
	}

	/**********
	 * <p> Method: performReturn() </p>
	 *
	 * <p> Description: Returns the student to the Student Home page. </p>
	 */
	protected static void performReturn() {
		staticHelpers.StaticHelpers.routeUserToHomeScreen(ViewMessagesList.theUser, ViewMessagesList.theStage);
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