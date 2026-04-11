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
 * and display discussion threads. </p>
 * 
 * @author Saam Kavusi
 * 
 * @version 1.00        2026-04-11 Initial version for staff discussion thread CRUD
 */

public class ModelThreadCRUD {
	
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	
	public ModelThreadCRUD() {
	}
	
	protected static void refreshThreadList() {
		List<DiscussionThread> threads = theDatabase.getThreadList();
		ViewThreadCRUD.listView_Threads.getItems().clear();
		for (DiscussionThread thread : threads) {
			ViewThreadCRUD.listView_Threads.getItems().add(thread);
		}
		ViewThreadCRUD.label_Status.setText("");
	}
	
	protected static void performRefresh() {
		refreshThreadList();
		
		Alert infoAlert = new Alert(AlertType.INFORMATION);
		infoAlert.setTitle("Thread List Refreshed");
		infoAlert.setHeaderText(null);
		infoAlert.setContentText("Discussion thread list refreshed successfully.");
		infoAlert.showAndWait();
	}
	
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