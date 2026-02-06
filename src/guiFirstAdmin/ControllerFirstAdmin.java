package guiFirstAdmin;

import java.sql.SQLException;
import database.Database;
import entityClasses.User;
import guiNewAccount.ViewNewAccount;
import javafx.stage.Stage;
import recognizers.UserNameRecognizer;

/*******
 * <p> Title: ControllerFirstAdmin Class. </p>
 * 
 * <p> Description: ControllerFirstAdmin class provides the controller actions based on the user's
 *  use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page contains a number of buttons that have not yet been implemented.  WHhen those buttons
 * are pressed, an alert pops up to tell the user that the function associated with the button has
 * not been implemented. Also, be aware that What has been implemented may not work the way the
 * final product requires and there maybe defects in this code.
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
 *  
 */

public class ControllerFirstAdmin {
	/*-********************************************************************************************

	The controller attributes for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/
	
	private static String adminUsername = "";
	private static String adminPassword1 = "";
	private static String adminPassword2 = "";
    private static String adminFirstName = "";
    private static String adminLastName = "";
	protected static Database theDatabase = applicationMain.FoundationsMain.database;		

	/*-********************************************************************************************

	The User Interface Actions for this page
	
	*/
	
	/**
	 * Default constructor is not used.
	 */
	public ControllerFirstAdmin() {
	}

	/**********
	 * <p> Method: setAdminUsername() </p>
	 * 
	 * <p> Description: This method is called when the user adds text to the username field in the
	 * View.  A private local copy of what was last entered is kept here.</p>
	 * 
	 */
	protected static void setAdminUsername() {
		adminUsername = ViewFirstAdmin.text_AdminUsername.getText();
		ViewFirstAdmin.label_UsernameInvalid.setText("");
	}
	
	
	/**********
	 * <p> Method: setAdminPassword1() </p>
	 * 
	 * <p> Description: This method is called when the user adds text to the password 1 field in
	 * the View.  A private local copy of what was last entered is kept here.</p>
	 * 
	 */
	protected static void setAdminPassword1() {
		adminPassword1 = ViewFirstAdmin.text_AdminPassword1.getText();
		ViewFirstAdmin.label_PasswordsDoNotMatch.setText("");
	}
	
	
	/**********
	 * <p> Method: setAdminPassword2() </p>
	 * 
	 * <p> Description: This method is called when the user adds text to the password 2 field in
	 * the View.  A private local copy of what was last entered is kept here.</p>
	 * 
	 */
	protected static void setAdminPassword2() {
		adminPassword2 = ViewFirstAdmin.text_AdminPassword2.getText();		
		ViewFirstAdmin.label_PasswordsDoNotMatch.setText("");
	}
	
	/**********
	 * <p> Setter Methods for Admin's Personal Information </p>
	 * 
	 */
	protected static void setAdminFirstName() { adminFirstName = ViewFirstAdmin.text_AdminFirstName.getText(); }
	protected static void setAdminLastName() {adminLastName = ViewFirstAdmin.text_AdminLastName.getText(); }
	
	/**********
	 * <p> Method: doSetupAdmin() </p>
	 * 
	 * <p> Description: This method is called when the user presses the button to set up the Admin
	 * account.  It start by trying to establish a new user and placing that user into the
	 * database.  If that is successful, we proceed to the UserUpdate page.</p>
	 * 
	 */
	protected static void doSetupAdmin(Stage ps, int r) {
		
		// If username is invalid, tell the user why and exit early
		String result = UserNameRecognizer.checkForValidUserName(adminUsername);
		if (result.isEmpty() == false)
		{
			ViewFirstAdmin.text_AdminUsername.setText("");
			ViewFirstAdmin.label_UsernameInvalid.setText(result);
			return;
		}
		
		// Make sure the two passwords are the same
		if (adminPassword1.compareTo(adminPassword2) == 0) {
			
			String passEval = passwordPopUpWindow.Model.evaluatePassword(ViewFirstAdmin.text_AdminPassword1.getText());
			// Make sure password conditions are satisfied
			if (passEval != "") {
				ViewFirstAdmin.text_AdminPassword1.setText("");
				ViewFirstAdmin.text_AdminPassword2.setText("");
				ViewFirstAdmin.alertPasswordError.setContentText(passEval);
				ViewFirstAdmin.alertPasswordError.showAndWait();
				return;
			}
			
			
			
			
        	// Create the passwords and proceed to the user home page
        	User user = new User(adminUsername, adminPassword1, adminFirstName, "", adminLastName, "", "", true, false, 
        			false);
            try {
            	// Create a new User object with admin role and register in the database
            	theDatabase.register(user);
            	}
            catch (SQLException e) {
                System.err.println("*** ERROR *** Database error trying to register a user: " + 
                		e.getMessage());
                e.printStackTrace();
                System.exit(0);
            }
            
            // User was established in the database sucessfully, redirect them to the login page to ask them to log in
            guiUserLogin.ViewUserLogin.displayUserLogin(ViewFirstAdmin.theStage);
		}
		else {
			// The two passwords are NOT the same, so clear the passwords, explain the passwords
			// must be the same, and clear the message as soon as the first character is typed.
			ViewFirstAdmin.text_AdminPassword1.setText("");
			ViewFirstAdmin.text_AdminPassword2.setText("");
			ViewFirstAdmin.label_PasswordsDoNotMatch.setText(
					"The two passwords must match. Please try again!");
		}
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
		System.out.println("Perform Quit");
		System.exit(0);
	}	
}

