package guiDeleteUser;

import java.sql.SQLException;
import java.util.Optional;

import database.Database;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;

/*******
 * <p> Title: ControllerDeleteUser Class. </p>
 * 
 * <p> Description: The Java/FX-based Delete User Page.  This class provides the controller
 * actions basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page has one of the more complex Controller Classes due to the fact that the changing the
 * values of widgets changes the layout of the page.  It is up to the Controller to determine what
 * to do and it involves the proper elements from View Class for this GUI page.
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

public class ControllerDeleteUser {
	
	/*-********************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */

	/**
	 * Default constructor is not used.
	 */
	public ControllerDeleteUser() {
	}
	
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;		

	/**********
	 * <p> Method: doSelectUser() </p>
	 * 
	 * <p> Description: This method uses the ComboBox widget, fetches which item in the ComboBox
	 * was selected (a user in this case), and establishes that user and the current user, setting
	 * easily accessible values without needing to do a query. </p>
	 * 
	 */
	protected static void doSelectUser() {
		ViewDeleteUser.theSelectedUser = 
				(String) ViewDeleteUser.combobox_SelectUser.getValue();
		theDatabase.getUserAccountDetails(ViewDeleteUser.theSelectedUser);
		setupSelectedUser();
	}
	
	
	/**********
	 * <p> Method: repaintTheWindow() </p>
	 * 
	 * <p> Description: This method determines the current state of the window and then establishes
	 * the appropriate list of widgets in the Pane to show the proper set of current values. </p>
	 * 
	 */
	protected static void repaintTheWindow() {
		// Clear what had been displayed
		ViewDeleteUser.theRootPane.getChildren().clear();
		
		// Determine which of the two views to show to the user
		if (ViewDeleteUser.theSelectedUser.compareTo("<Select a User>") == 0) {
			// Only show the request to select a user to be updated and the ComboBox
			ViewDeleteUser.theRootPane.getChildren().addAll(
					ViewDeleteUser.label_PageTitle, ViewDeleteUser.label_UserDetails, 
					ViewDeleteUser.button_UpdateThisUser, ViewDeleteUser.line_Separator1,
					ViewDeleteUser.label_SelectUser, ViewDeleteUser.combobox_SelectUser, 
					ViewDeleteUser.line_Separator4, ViewDeleteUser.button_Return,
					ViewDeleteUser.button_Logout, ViewDeleteUser.button_Quit);
		}
		else {
			// Show all the fields as there is a selected user (as opposed to the prompt)
			ViewDeleteUser.theRootPane.getChildren().addAll(
					ViewDeleteUser.label_PageTitle, ViewDeleteUser.label_UserDetails,
					ViewDeleteUser.button_UpdateThisUser, ViewDeleteUser.line_Separator1,
					ViewDeleteUser.label_SelectUser,
					ViewDeleteUser.combobox_SelectUser, 
					ViewDeleteUser.label_CurrentRoles,
					ViewDeleteUser.label_Name,
					ViewDeleteUser.label_PreferredFirstName,
					ViewDeleteUser.button_DeleteUser,
					ViewDeleteUser.label_EmailAddress,
					ViewDeleteUser.label_Username,
					ViewDeleteUser.line_Separator4, 
					ViewDeleteUser.button_Return,
					ViewDeleteUser.button_Logout,
					ViewDeleteUser.button_Quit);
		}
		
		// Add the list of widgets to the stage and show it
		
		// Set the title for the window
		ViewDeleteUser.theStage.setTitle("CSE 360 Foundation Code: Admin Opertations Page");
		ViewDeleteUser.theStage.setScene(ViewDeleteUser.theAddRemoveRolesScene);
		ViewDeleteUser.theStage.show();
	}
	
	
	/**********
	 * <p> Method: deleteSelectedUser() </p>
	 * 
	 * <p> Description: This method deletes the user selected on the DeleteUser page. The method will NOT delete if the current user is the 
	 * same as the one being deleted, or if the selected user is the last user with Admin role.  </p>
	 * 
	 */
	protected static void deleteSelectedUser() {
		System.out.println("*** Entering deleteSelectedUser");
		// Make sure the selected user is not the current user
		if (ViewDeleteUser.theSelectedUser.equals(ViewDeleteUser.theUser.getUserName()))
		{
			ViewDeleteUser.alertUserDeletionError.setTitle("Delete User");
			ViewDeleteUser.alertUserDeletionError.setContentText("You cannot delete your own account.");
			ViewDeleteUser.alertUserDeletionError.show();
			return;
		}
		// Make sure the selected user is not the last admin
		if (theDatabase.getCurrentAdminRole() == true)
		{
			// Selected user has Admin role, therefore we need to make sure this is not the last user with admin role
			try {
				int currentNumAdmins = theDatabase.getNumAdmins();
				if (currentNumAdmins - 1 <= 0) 
				{
					// Subtracting one admin would result in having zero admins. Don't remove the selected user
					ViewDeleteUser.alertUserDeletionError.setTitle("Delete User");
					ViewDeleteUser.alertUserDeletionError.setContentText("You cannot delete the last Administrator account.");
					ViewDeleteUser.alertUserDeletionError.show();
					return; 
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		// Show delete user confirmation ("Are you sure?" -> "Yes" / "No")
		ViewDeleteUser.alertConfirmUserDeletion.setTitle("Delete User");
		ViewDeleteUser.alertConfirmUserDeletion.setHeaderText("Are you sure?");
		ViewDeleteUser.alertConfirmUserDeletion.setContentText("This action is irreversible.");
		Optional<ButtonType> result = ViewDeleteUser.alertConfirmUserDeletion.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.YES) {
			// Deletion confirmed. Remove selected user from database then update the page
			try {
				theDatabase.remove(ViewDeleteUser.theSelectedUser);
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// Reset the delete user page
			ViewDeleteUser.displayDeleteUser(ViewDeleteUser.theStage, ViewDeleteUser.theUser);
		}
	}
	
	
	/**********
	 * <p> Method: setupSelectedUser() </p>
	 * 
	 * <p> Description: This method fetches the current values for the widgets whose values change
	 * based on which user has been selected and any actions that the admin takes. </p>
	 * 
	 */
	private static void setupSelectedUser() {
		System.out.println("*** Entering setupSelectedUser (ControllerDeleteUser)");
		
		// Create the list or roles that the user currently has with proper use of a comma between
		// items
		boolean notTheFirst = false;
		String theCurrentRoles = "";
		
		// Admin role - It can only be at the head of a list
		if (theDatabase.getCurrentAdminRole()) {
			theCurrentRoles += "Admin";
			notTheFirst = true;
		}
		
		// Student role - It could be at the head of the list or later in the list
		if (theDatabase.getCurrentNewStudentRole()) {
			if (notTheFirst)
				theCurrentRoles += ", Student"; 
			else {
				theCurrentRoles += "Student";
				notTheFirst = true;
			}
		}

		// Staff role - It could be at the head of the list or later in the list
		if (theDatabase.getCurrentNewStaffRole()) {
			if (notTheFirst)
				theCurrentRoles += ", Staff"; 
			else {
				theCurrentRoles += "Staff";
				notTheFirst = true;
			}
		}

		// Given the above actions, populate the related widgets with the new values
		ViewDeleteUser.label_CurrentRoles.setText("Current roles: " + 
				theCurrentRoles);		
		
		String FullName = theDatabase.getCurrentFirstName() + " " + theDatabase.getCurrentMiddleName() + " " + theDatabase.getCurrentLastName();
		ViewDeleteUser.label_Name.setText("Name: " + FullName);
		ViewDeleteUser.label_PreferredFirstName.setText("Preferred First Name: " + theDatabase.getCurrentPreferredFirstName());
		ViewDeleteUser.label_Username.setText("Username: " + theDatabase.getCurrentUsername());
		ViewDeleteUser.label_EmailAddress.setText("Email Address: " + theDatabase.getCurrentEmailAddress());
		
		ViewDeleteUser.setupButtonUI(ViewDeleteUser.button_DeleteUser, "Dialog", 16, 150, 
				Pos.CENTER, 460, 420);

		// Repaint the window showing this new values
		repaintTheWindow();

	}
	
	
	/**********
	 * <p> Method: performReturn() </p>
	 * 
	 * <p> Description: This method returns the user (who must be an Admin as only admins are the
	 * only users who have access to this page) to the Admin Home page. </p>
	 * 
	 */
	protected static void performReturn() {
		guiAdminHome.ViewAdminHome.displayAdminHome(ViewDeleteUser.theStage,
				ViewDeleteUser.theUser);
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
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewDeleteUser.theStage);
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