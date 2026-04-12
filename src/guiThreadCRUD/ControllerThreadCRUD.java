package guiThreadCRUD;

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
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated. It is a collection of protected
	static methods that can be called by the View and Model.
	
	 */
	
	/**
	 * Default constructor is not used.
	 */
	public ControllerThreadCRUD() {
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