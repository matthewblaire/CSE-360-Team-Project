package guiThreadCRUD;

import java.sql.SQLException;

import applicationMain.FoundationsMain;
import database.Database;
import entityClasses.User;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/*******
 * <p> Title: ControllerThreadCRUD Class. </p>
 * 
 * <p> Description: The Java/FX-based Thread CRUD page controller. This class provides
 * the controller actions based on the user's use of the JavaFX GUI widgets defined by
 * the View class.
 * 
 * This controller supports Staff User Story S3: as a staff member, I can create, read,
 * update, and delete discussion threads. The controller delegates those actions to the
 * model layer, which is validated primarily by the JUnit tests in
 * {@code FullStaffThreadCRUDTester}. </p>
 * 
 * @author Saam Kavusi
 * 
 * @version 1.00        2026-04-11 Initial version for staff discussion thread CRUD
 */

public class ControllerThreadCRUD {
	
	private  Alert alert = new Alert(AlertType.ERROR);

	/*-*******************************************************************************************
	
	User Interface Actions for this page
	
	This controller is not a class that gets instantiated. It is a collection of protected
	static methods that can be called by the View and Model.
	
	 */
	
	private static Database theDatabase = FoundationsMain.database;
	private static Alert alert1 = new Alert(AlertType.ERROR);	
	/**
	 * Default constructor is not used.
	 */

	public ControllerThreadCRUD() {
	}
	
	/**********
	 * <p> Method: void performCreatethread(Stage theStage, User theUser, String newThreadName) </p>
	 * 
	 * <p> Description: creates a new thread. </p>
	 * 
	 * @param stage the gui stage to pass to view
	 * @param user current user object
	 * @param newThreadName new thread name (String) to be created 
	 */
	protected static void performCreatethread(Stage theStage, User theUser, String newThreadName) {
		try {
			theDatabase.createThread(newThreadName);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			alert1.setTitle("Error Dialog");
			alert1.setHeaderText("Error Dialog");
			alert1.setContentText(e.getMessage());
			alert1.showAndWait();
		}
		ViewThreadCRUD.displayThreadCRUD(theStage, theUser);
	}
	
	/**********
	 * <p> Method: void performUpdatethread(Stage theStage, User theUser, String newThreadName, int threadId) </p>
	 * 
	 * <p> Description: updates thread's title. </p>
	 * 
	 * @param stage the gui stage to pass to view
	 * @param user current user object
	 * @param newThreadName new thread name (String) to be updated 
	 * @param threadId ID of the thread to be updated
	 */
	protected static void performUpdatethread(Stage theStage, User theUser, String newThreadName, int threadId) {
		try {
			theDatabase.updateThreadTitle(threadId, newThreadName);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			alert1.setTitle("Error Dialog");
			alert1.setHeaderText("Error Dialog");
			alert1.setContentText(e.getMessage());
			alert1.showAndWait();
		}
		ViewThreadCRUD.displayThreadCRUD(theStage, theUser);
	}
	
	/**********
	 * <p> Method: void performDeletethread(Stage theStage, User theUser, int threadId) </p>
	 * 
	 * <p> Description: deletes a thread. </p>
	 * 
	 * @param stage the gui stage to pass to view
	 * @param user current user object
	 * @param threadId ID of the thread to be deleted
	 */
	protected static void performDeletethread(Stage theStage, User theUser, int threadId) {
		try {
			theDatabase.deleteThread(threadId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			alert1.setTitle("Error Dialog");
			alert1.setHeaderText("Error Dialog");
			alert1.setContentText(e.getMessage());
			alert1.showAndWait();
		}
		ViewThreadCRUD.displayThreadCRUD(theStage, theUser);
	}
	
	/**********
	 * <p> Method: goBackToStaffHome() </p>
	 * 
	 * <p> Description: This method directs the staff user back to the Staff Home page. </p>
	 * 
	 */
	protected static void goBackToStaffHome() {
		guiStaff.ViewStaffHome.displayStaffHome(ViewThreadCRUD.theStage, ViewThreadCRUD.theUser);
	}
	
	/**********
	 * <p> Method: performCreateThread() </p>
	 * 
	 * <p> Description: This method requests creation of a new discussion thread using the
	 * title entered by the staff user. </p>
	 * 
	 * This function is validated through the database-level behavior checked by
	 * {@code FullStaffThreadCRUDTester.NormalTest01()} and
	 * {@code FullStaffThreadCRUDTester.RobustTest01()}.
	 */
	protected static void performCreateThread() {
		ModelThreadCRUD.createThread();
	}
	
	/**********
	 * <p> Method: performUpdateThread() </p>
	 * 
	 * <p> Description: This method requests an update to the selected discussion thread's
	 * title using the values entered by the staff user. </p>
	 * 
	 *  This function is validated through the database-level behavior checked by
	 * {@code FullStaffThreadCRUDTester.NormalTest02()} and
	 * {@code FullStaffThreadCRUDTester.RobustTest03()}.
	 */
	protected static void performUpdateThread() {
		ModelThreadCRUD.updateThread();
	}
	
	/**********
	 * <p> Method: performDeleteThread() </p>
	 * 
	 * <p> Description: This method requests deletion of the selected discussion thread. </p>
	 * 
	 * This function is validated through the database-level behavior checked by
	 * {@code FullStaffThreadCRUDTester.NormalTest03()} and
	 * {@code FullStaffThreadCRUDTester.RobustTest02()}.
	 */
	protected static void performDeleteThread() {
		ModelThreadCRUD.deleteThread();
	}
	
	/**********
	 * <p> Method: refreshThreadList() </p>
	 * 
	 * <p> Description: This method refreshes the visible list of discussion threads shown
	 * on the page. </p>
	 * 
	 * The thread-list content displayed by this action depends on the thread-creation,
	 * update, and deletion behaviors validated by
	 * {@code FullStaffThreadCRUDTester.NormalTest01()},
	 * {@code FullStaffThreadCRUDTester.NormalTest02()}, and
	 * {@code FullStaffThreadCRUDTester.NormalTest03()}.
	 */
	protected static void refreshThreadList() {
		ModelThreadCRUD.performRefresh();
	}
}