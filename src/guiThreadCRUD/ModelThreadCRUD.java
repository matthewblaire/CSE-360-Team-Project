package guiThreadCRUD;

import java.sql.SQLException;
import java.util.List;

import database.Database;
import entityClasses.DiscussionThread;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import recognizers.TitleRecognizer;

/*******
 * <p> Title: ModelThreadCRUD Class. </p>
 * 
 * <p> Description: The model for the staff discussion thread CRUD page. This class
 * performs the validation and database operations needed to create, update, delete,
 * and display discussion threads.
 * 
 * This model implements the core application behavior for Staff User Story S3:
 * as a staff member, I can create, read, update, and delete discussion threads.
 * Its thread-management behavior is validated primarily by the JUnit tests in
 * {@code FullStaffThreadCRUDTester}. </p>
 * 
 * @author Saam Kavusi
 * 
 * @version 1.00        2026-04-11 Initial version for staff discussion thread CRUD
 */

public class ModelThreadCRUD {
	
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	
	/**
	 * Default constructor is not used.
	 */
	public ModelThreadCRUD() {
	}
	
	/**********
	 * <p> Method: refreshThreadList() </p>
	 * 
	 * <p> Description: This method reloads the current set of discussion threads from the
	 * database and repopulates the list view shown on the page. It also clears any prior
	 * status message so the refreshed view reflects the latest thread state. </p>
	 * 
	 * The database thread list used by this method is validated by
	 * {@code FullStaffThreadCRUDTester.NormalTest01()},
	 * {@code FullStaffThreadCRUDTester.NormalTest02()}, and
	 * {@code FullStaffThreadCRUDTester.NormalTest03()}.
	 */
	protected static void refreshThreadList() {
		List<DiscussionThread> threads = theDatabase.getThreadList();
		ViewThreadCRUD.listView_Threads.getItems().clear();
		for (DiscussionThread thread : threads) {
			ViewThreadCRUD.listView_Threads.getItems().add(thread);
		}
		ViewThreadCRUD.label_Status.setText("");
	}
	
	/**********
	 * <p> Method: performRefresh() </p>
	 * 
	 * <p> Description: This method refreshes the thread list and then informs the user that
	 * the visible discussion thread list has been updated successfully. </p>
	 * 
	 * The refreshed list content depends on the same thread-list behavior validated by
	 * {@code FullStaffThreadCRUDTester.NormalTest01()},
	 * {@code FullStaffThreadCRUDTester.NormalTest02()}, and
	 * {@code FullStaffThreadCRUDTester.NormalTest03()}.
	 */
	protected static void performRefresh() {
		refreshThreadList();
		
		Alert infoAlert = new Alert(AlertType.INFORMATION);
		infoAlert.setTitle("Thread List Refreshed");
		infoAlert.setHeaderText(null);
		infoAlert.setContentText("Discussion thread list refreshed successfully.");
		infoAlert.showAndWait();
	}
	
	/**********
	 * <p> Method: createThread() </p>
	 * 
	 * <p> Description: This method reads the new discussion thread title entered by the staff
	 * user, validates it, and requests creation of the thread in the database. If creation
	 * succeeds, the visible list is refreshed and the status message reports success. If
	 * validation or database creation fails, an error is shown to the user. </p>
	 * 
	 * The database thread-creation behavior used here is validated by
	 * {@code FullStaffThreadCRUDTester.NormalTest01()} for successful creation and
	 * {@code FullStaffThreadCRUDTester.RobustTest01()} for duplicate-title rejection.
	 */
	protected static void createThread() {
		String title = ViewThreadCRUD.textField_NewThreadTitle.getText().trim();
		
		String titleEvaluation = TitleRecognizer.evaluateTitle(title);
		if (!titleEvaluation.isEmpty()) {
			showError(titleEvaluation);
			return;
		}
		
		try {
			theDatabase.createThread(title);
			ViewThreadCRUD.textField_NewThreadTitle.setText("");
			refreshThreadList();
			ViewThreadCRUD.label_Status.setText("Thread created successfully.");
			ViewThreadCRUD.label_Status.setStyle("-fx-text-fill: green;");
		} catch (SQLException e) {
			showError(e.getMessage());
		}
	}
	
	/**********
	 * <p> Method: updateThread() </p>
	 * 
	 * <p> Description: This method updates the title of the currently selected discussion
	 * thread using the replacement title entered by the staff user. The method verifies that
	 * a thread has been selected and that the new title is valid before requesting the update.
	 * If the update succeeds, the page is refreshed and the user is informed of success.
	 * Otherwise, an error is shown. </p>
	 * 
	 * The database thread-update behavior used here is validated by
	 * {@code FullStaffThreadCRUDTester.NormalTest02()} for successful update and
	 * {@code FullStaffThreadCRUDTester.RobustTest03()} for duplicate-title rejection.
	 */
	protected static void updateThread() {
		DiscussionThread selectedThread =
				ViewThreadCRUD.listView_Threads.getSelectionModel().getSelectedItem();
		
		if (selectedThread == null) {
			showError("Please select a thread to update.");
			return;
		}
		
		String newTitle = ViewThreadCRUD.textField_EditThreadTitle.getText().trim();
		String titleEvaluation = TitleRecognizer.evaluateTitle(newTitle);
		if (!titleEvaluation.isEmpty()) {
			showError(titleEvaluation);
			return;
		}
		
		try {
			theDatabase.updateThreadTitle(selectedThread.getThreadId(), newTitle);
			ViewThreadCRUD.textField_EditThreadTitle.setText("");
			refreshThreadList();
			ViewThreadCRUD.label_Status.setText("Thread updated successfully.");
			ViewThreadCRUD.label_Status.setStyle("-fx-text-fill: green;");
		} catch (SQLException e) {
			showError(e.getMessage());
		}
	}
	
	/**********
	 * <p> Method: deleteThread() </p>
	 * 
	 * <p> Description: This method requests deletion of the currently selected discussion
	 * thread. It first checks that a thread is selected, then relies on the database rules
	 * to reject deletion of protected threads such as the default "General" thread or any
	 * thread that still contains posts. If the delete succeeds, the page is refreshed and
	 * the user is informed of success. Otherwise, an error is shown. </p>
	 * 
	 * The database thread-deletion behavior used here is validated by
	 * {@code FullStaffThreadCRUDTester.NormalTest03()} for successful deletion and
	 * {@code FullStaffThreadCRUDTester.RobustTest02()} for protection of the default
	 * "General" thread.
	 */
	protected static void deleteThread() {
		DiscussionThread selectedThread =
				ViewThreadCRUD.listView_Threads.getSelectionModel().getSelectedItem();
		
		if (selectedThread == null) {
			showError("Please select a thread to delete.");
			return;
		}
		
		try {
			theDatabase.deleteThread(selectedThread.getThreadId());
			ViewThreadCRUD.textField_EditThreadTitle.setText("");
			refreshThreadList();
			ViewThreadCRUD.label_Status.setText("Thread deleted successfully.");
			ViewThreadCRUD.label_Status.setStyle("-fx-text-fill: green;");
		} catch (SQLException e) {
			showError(e.getMessage());
		}
	}
	
	/**********
	 * <p> Method: showError(String message) </p>
	 * 
	 * <p> Description: This helper method displays a thread-management error to the user in
	 * two ways: it places the error message into the page status label and also shows a JavaFX
	 * error dialog with the same message. This keeps the cause of the failure visible even
	 * after the dialog is dismissed. </p>
	 * 
	 * This method supports failure handling for the same create, update, and delete paths
	 * validated by {@code FullStaffThreadCRUDTester.RobustTest01()},
	 * {@code FullStaffThreadCRUDTester.RobustTest02()}, and
	 * {@code FullStaffThreadCRUDTester.RobustTest03()}.
	 * 
	 * @param message specifies the error message to be shown to the user
	 */
	private static void showError(String message) {
		ViewThreadCRUD.label_Status.setText(message);
		ViewThreadCRUD.label_Status.setStyle("-fx-text-fill: red;");
		
		Alert errorAlert = new Alert(AlertType.ERROR);
		errorAlert.setTitle("Thread Management Error");
		errorAlert.setHeaderText("Unable to Complete Request");
		errorAlert.setContentText(message);
		errorAlert.showAndWait();
	}
}