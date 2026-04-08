package guiStaff;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;


/*******
 * <p> Title: ControllerStaffClass. </p>
 * 
 * <p> Description: The Java/FX-based Staff Home Page.  This class provides the controller
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
 * @version 1.01		2025-09-16 Update Javadoc documentation 
 * * @version 1.02		2026-04-08 Added staff discussion thread CRUD controller support
 */

public class ControllerStaffHome {
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */

	/**
	 * Default constructor is not used.
	 */
	public ControllerStaffHome() {
	}

	/**********
	 * <p> Method: performUpdate() </p>
	 * 
	 * <p> Description: This method directs the user to the User Update Page so the user can change
	 * the user account attributes. </p>
	 * 
	 */
	protected static void performUpdate () {
		guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewStaffHome.theStage, ViewStaffHome.theUser);
	}	
	
	
	/**********
	 * <p> Method: performRefreshThreads() </p>
	 * 
	 * <p> Description: Refreshes the visible thread list on the Staff Home page
	 * so staff can see the current active discussion threads. </p>
	 */
	protected static void performRefreshThreads() {
		ViewStaffHome.refreshThreadList();
	}

	
	/**********
	 * <p> Method: performCreateThread() </p>
	 * 
	 * <p> Description: Creates a new discussion thread using the title entered on
	 * the Staff Home page. If the title is blank, the method shows an error alert
	 * and does not attempt the database INSERT. </p>
	 */
	protected static void performCreateThread() {
		String newTitle = ViewStaffHome.text_NewThreadTitle.getText();

		if (newTitle == null || newTitle.trim().isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Invalid Thread Title");
			alert.setHeaderText("Thread title is required");
			alert.setContentText("Please enter a non-empty thread title before creating a thread.");
			alert.showAndWait();
			return;
		}

		try {
			applicationMain.FoundationsMain.database.createDiscussionThread(newTitle.trim());
			ViewStaffHome.text_NewThreadTitle.clear();
			ViewStaffHome.refreshThreadList();

			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Thread Created");
			alert.setHeaderText(null);
			alert.setContentText("The discussion thread was created successfully.");
			alert.showAndWait();
		} catch (Exception e) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Create Thread Failed");
			alert.setHeaderText("The thread could not be created");
			alert.setContentText("Reason: " + e.getMessage());
			alert.showAndWait();
		}
	}

	
	/**********
	 * <p> Method: performUpdateThread() </p>
	 * 
	 * <p> Description: Renames the currently selected discussion thread using the
	 * updated title entered on the Staff Home page. If no thread is selected or
	 * the updated title is blank, the method shows an error alert and does not
	 * attempt the database update. </p>
	 */
	protected static void performUpdateThread() {
		String selectedThreadEntry = ViewStaffHome.listView_Threads.getSelectionModel().getSelectedItem();
		String updatedTitle = ViewStaffHome.text_UpdatedThreadTitle.getText();

		if (selectedThreadEntry == null) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("No Thread Selected");
			alert.setHeaderText("Please select a thread");
			alert.setContentText("Select a discussion thread from the list before renaming it.");
			alert.showAndWait();
			return;
		}

		if (updatedTitle == null || updatedTitle.trim().isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Invalid Updated Title");
			alert.setHeaderText("Updated thread title is required");
			alert.setContentText("Please enter a non-empty updated title.");
			alert.showAndWait();
			return;
		}

		try {
			int threadId = parseThreadIdFromListEntry(selectedThreadEntry);
			boolean updated = applicationMain.FoundationsMain.database
					.updateDiscussionThreadTitle(threadId, updatedTitle.trim());

			if (updated) {
				ViewStaffHome.text_UpdatedThreadTitle.clear();
				ViewStaffHome.refreshThreadList();

				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Thread Updated");
				alert.setHeaderText(null);
				alert.setContentText("The selected discussion thread was renamed successfully.");
				alert.showAndWait();
			} else {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Update Failed");
				alert.setHeaderText("The thread could not be renamed");
				alert.setContentText("The selected thread may no longer exist or may already be deleted.");
				alert.showAndWait();
			}
		} catch (Exception e) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Update Failed");
			alert.setHeaderText("The thread could not be renamed");
			alert.setContentText("Reason: " + e.getMessage());
			alert.showAndWait();
		}
	}

	
	/**********
	 * <p> Method: performDeleteThread() </p>
	 * 
	 * <p> Description: Soft-deletes the currently selected discussion thread after
	 * two confirmation prompts. The first confirmation verifies the staff member
	 * intends to delete the selected thread. The second confirmation explains that
	 * deleting the thread will also soft-delete all posts and replies contained in
	 * that thread. </p>
	 */
	protected static void performDeleteThread() {
		String selectedThreadEntry = ViewStaffHome.listView_Threads.getSelectionModel().getSelectedItem();

		if (selectedThreadEntry == null) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("No Thread Selected");
			alert.setHeaderText("Please select a thread");
			alert.setContentText("Select a discussion thread from the list before deleting it.");
			alert.showAndWait();
			return;
		}

		Alert firstConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
		firstConfirmation.setTitle("Confirm Thread Deletion");
		firstConfirmation.setHeaderText("Are you sure you want to delete this thread?");
		firstConfirmation.setContentText("Selected thread: " + selectedThreadEntry);

		Optional<ButtonType> firstResult = firstConfirmation.showAndWait();
		if (firstResult.isEmpty() || firstResult.get() != ButtonType.OK) {
			return;
		}

		Alert secondConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
		secondConfirmation.setTitle("Confirm Cascade Delete");
		secondConfirmation.setHeaderText("Deleting this thread will also delete all posts and replies in it.");
		secondConfirmation.setContentText("Are you sure you want to continue?");

		Optional<ButtonType> secondResult = secondConfirmation.showAndWait();
		if (secondResult.isEmpty() || secondResult.get() != ButtonType.OK) {
			return;
		}

		try {
			int threadId = parseThreadIdFromListEntry(selectedThreadEntry);
			boolean deleted = applicationMain.FoundationsMain.database
					.softDeleteDiscussionThreadCascade(threadId);

			if (deleted) {
				ViewStaffHome.refreshThreadList();

				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Thread Deleted");
				alert.setHeaderText(null);
				alert.setContentText("The selected thread and its posts/replies were soft-deleted successfully.");
				alert.showAndWait();
			} else {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Delete Failed");
				alert.setHeaderText("The thread could not be deleted");
				alert.setContentText("The selected thread may no longer exist or may already be deleted.");
				alert.showAndWait();
			}
		} catch (Exception e) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Delete Failed");
			alert.setHeaderText("The thread could not be deleted");
			alert.setContentText("Reason: " + e.getMessage());
			alert.showAndWait();
		}
	}

	
	/**********
	 * <p> Method: parseThreadIdFromListEntry(String listEntry) </p>
	 * 
	 * <p> Description: Extracts the threadId from the ListView string format
	 * "threadId: title". This helper keeps the parsing logic in one place so the
	 * create/update/delete controller code remains shorter and easier to read.
	 * If the selected ListView entry is the placeholder message shown when no
	 * active threads are available, the method throws an exception instead of
	 * attempting to parse a threadId. </p>
	 * 
	 * @param listEntry the selected ListView entry in the format "threadId: title"
	 * @return the parsed integer threadId
	 */
	
	
	private static int parseThreadIdFromListEntry(String listEntry) {
		if (listEntry == null || listEntry.equals("No active threads available")) {
			throw new IllegalArgumentException("No valid thread is currently selected.");
		}

		String idPortion = listEntry.split(":", 2)[0].trim();
		return Integer.parseInt(idPortion);
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
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewStaffHome.theStage);
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
