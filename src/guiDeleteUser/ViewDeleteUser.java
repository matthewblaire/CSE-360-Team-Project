package guiDeleteUser;

import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;

/*******
 * <p> Title: GUIDeleteUser Class. </p>
 * 
 * <p> Description: The Java/FX-based page for deleting users.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * @author Matthew Blaire
 * 
 * @version 1.00		2025-08-20 Initial version (GUIAddRemoveRolesPage)
 * @version 2.00		2026-01-28 Duplicated and repurposed into ViewDeleteUser
 *  
 */

public class ViewDeleteUser {
	
	/*-*******************************************************************************************

	Attributes
	
	*/
	
	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	
	// These are the widget attributes for the GUI. There are 3 areas for this GUI.
	
	// GUI Area 1: It informs the user about the purpose of this page, whose account is being used,
	// and a button to allow this user to update the account settings.
	/** The label displaying the page title. */
	protected static Label label_PageTitle = new Label();
	/** The label displaying the current user details. */
	protected static Label label_UserDetails = new Label();
	/** The button to navigate to the account update page. */
	protected static Button button_UpdateThisUser = new Button("Account Update");
	
	// This is a separator and it is used to partition the GUI for various tasks
	/** The horizontal line separating the header area from the main content. */
	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);

	// When no user has been selected, only Area 2a is shown.  If a user in the ComboBox in Area 1a
	// has been specified, then Area 2b is made visible.

	// Area 2a: This allows the admin to select a user of the system as the first step in adding or
	// removing a role.  The act of selecting a user causes the change is the GUI.  The Admin does
	// not need to push a button to make this happen.
	/** The label prompting the admin to select a user to delete. */
	protected static Label label_SelectUser = new Label("Select a user to be deleted:");
	/** The combo box for selecting a user to be deleted. */
	protected static ComboBox <String> combobox_SelectUser = new ComboBox <String>();
	
	// Area 2b: When a user has been selected these widgets are shown and can be used
	/** The label displaying the selected user's current roles. */
	protected static Label label_CurrentRoles = new Label("Roles:");
	/** The label displaying the selected user's name. */
	protected static Label label_Name = new Label("Name:");
	/** The label displaying the selected user's preferred first name. */
	protected static Label label_PreferredFirstName = new Label("Preferred First Name:");
	/** The label displaying the selected user's email address. */
	protected static Label label_EmailAddress = new Label("Email Address:");
	/** The label displaying the selected user's username. */
	protected static Label label_Username = new Label("Username");

	/** The button to delete the selected user. */
	protected static Button button_DeleteUser = new Button("Delete User");
	
	/** The alert used to confirm user deletion after the delete button is clicked. */
	protected static Alert alertConfirmUserDeletion = new Alert(AlertType.CONFIRMATION, "Content Text 1", ButtonType.YES, ButtonType.NO);
	/** The alert displayed when a user account cannot be deleted. */
	protected static Alert alertUserDeletionError = new Alert(AlertType.ERROR);
		
	// This is a separator and it is used to partition the GUI for various tasks
	/** The horizontal line separating the main content from the navigation buttons. */
	protected static Line line_Separator4 = new Line(20, 525, width-20,525);

	// GUI Area 3: This is last of the GUI areas.  It is used for quitting the application, logging
	// out, and on other pages a return is provided so the user can return to a previous page when
	// the actions on that page are complete.  Be advised that in most cases in this code, the
	// return is to a fixed page as opposed to the actual page that invoked the pages.
	/** The button to return to the previous page. */
	protected static Button button_Return = new Button("Return");
	/** The button to log out of the application. */
	protected static Button button_Logout = new Button("Logout");
	/** The button to quit the application. */
	protected static Button button_Quit = new Button("Quit");

	// This is the end of the GUI objects for the page.
	
	// These attributes are used to configure the page and populate it with this user's information
	private static ViewDeleteUser theView;	// Used to determine if instantiation of the class
												// is needed
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;		

	/** The JavaFX Stage used to display this page. */
	protected static Stage theStage;
	/** The root pane that holds all GUI widgets. */
	protected static Pane theRootPane;
	/** The current user of the application. */
	protected static User theUser;

	/** The scene for the delete user page. */
	public static Scene theAddRemoveRolesScene = null;
	/** The username of the currently selected user to be deleted. */
	protected static String theSelectedUser = "";
	/** The role being added. */
	protected static String theAddRole = "";
	/** The role being removed. */
	protected static String theRemoveRole = "";
	
	/** The change listener that triggers when a different user is selected in the combo box. */
	protected static ChangeListener<String> selectedUserChangeListener = (@SuppressWarnings("unused") ObservableValue<? extends String> observable,
    		@SuppressWarnings("unused") String oldvalue,
    		@SuppressWarnings("unused") String newValue) -> {ControllerDeleteUser.doSelectUser();};


	/*-*******************************************************************************************

	Constructors
	
	*/

	/**********
	 * <p> Method: displayAddRemoveRoles(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the AddRevove page to be displayed.
	 * 
	 * It first sets up very shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup.  If not, it instantiates the class, 
	 * initializes all the static aspects of the GUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 * 
	 * After the instantiation, the code then populates the elements that change based on the user
	 * and the system's current state.  It then sets the Scene onto the stage, and makes it visible
	 * to the user.
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the User whose roles will be updated
	 *
	 */
	public static void displayDeleteUser(Stage ps, User user) {
		
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		
		// If not yet established, populate the static aspects of the GUI by creating the 
		// singleton instance of this class
		if (theView == null) theView = new ViewDeleteUser();
		
		// Default to no user selected
		combobox_SelectUser.getSelectionModel().select(0);
		
		
		// Remove the selectUser listener so it doesn't trigger during the subsequent refresh
		combobox_SelectUser.getSelectionModel().selectedItemProperty().removeListener(ViewDeleteUser.selectedUserChangeListener); 
		// Refresh the list of users 
		List<String> userList = theDatabase.getUserList();	
		userList.remove(theUser.getUserName());
		combobox_SelectUser.setItems(FXCollections.observableArrayList(userList));
		
		// Set the selection back to default
		combobox_SelectUser.getSelectionModel().select(0);   		
		
		// Add the listener back
		combobox_SelectUser.getSelectionModel().selectedItemProperty().addListener(selectedUserChangeListener);
		
		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.  This page is different from the others.  Since there are two 
		// modes (1: user has not been selected, and 2: user has been selected) there are two
		// lists of widgets to be displayed.  For this reason, we have implemented the following 
		// two controller methods to deal with this dynamic aspect.
		ControllerDeleteUser.repaintTheWindow();
		ControllerDeleteUser.doSelectUser();
	}

	
	/**********
	 * <p> Method: GUIAddRemoveRolesPage() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object. </p>
	 * 
	 * <p> This is a singleton, so this is performed just one.  Subsequent uses fill in the changeable
	 * fields using the displayAddRempoveRoles method.</p>
	 * 
	 */
	public ViewDeleteUser() {
		
		// This page is used by all roles, so we do not specify the role being used		
			
		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theAddRemoveRolesScene = new Scene(theRootPane, width, height);
		
		// Populate the window with the title and other common widgets and set their static state
		
		// GUI Area 1
		label_PageTitle.setText("Delete User Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
		
		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((_) -> 
			{guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser); });
		
		// GUI Area 2a
		setupLabelUI(label_SelectUser, "Arial", 20, 300, Pos.BASELINE_LEFT, 20, 130);
		
		setupComboBoxUI(combobox_SelectUser, "Dialog", 16, 250, 280, 125);
		List<String> userList = theDatabase.getUserList();
		userList.remove(theUser.getUserName());
		combobox_SelectUser.setItems(FXCollections.observableArrayList(userList));
		combobox_SelectUser.getSelectionModel().select(0);
		combobox_SelectUser.getSelectionModel().selectedItemProperty()
    	.addListener(ViewDeleteUser.selectedUserChangeListener);
		
		// GUI Area 2b
		
		setupLabelUI(label_Username, "Arial", 16, 300, Pos.BASELINE_LEFT, 50, 170);	
		setupLabelUI(label_Name, "Arial", 16, 300, Pos.BASELINE_LEFT, 50, 220);	
		setupLabelUI(label_PreferredFirstName, "Arial", 16, 300, Pos.BASELINE_LEFT, 50, 270);	
		setupLabelUI(label_CurrentRoles, "Arial", 16, 300, Pos.BASELINE_LEFT, 50, 320);	
		setupLabelUI(label_EmailAddress, "Arial", 16, 300, Pos.BASELINE_LEFT, 50, 370);	
		
	
		setupButtonUI(button_DeleteUser, "Dialog", 16, 150, Pos.BASELINE_LEFT, 460, 420);
		ViewDeleteUser.button_DeleteUser.setOnAction((_) -> 
			{
				ControllerDeleteUser.deleteSelectedUser();
			});
			

		
		// GUI Area 3		
		setupButtonUI(button_Return, "Dialog", 18, 210, Pos.CENTER, 20, 540);
		button_Return.setOnAction((_) -> {ControllerDeleteUser.performReturn(); });

		setupButtonUI(button_Logout, "Dialog", 18, 210, Pos.CENTER, 300, 540);
		button_Logout.setOnAction((_) -> {ControllerDeleteUser.performLogout(); });
    
		setupButtonUI(button_Quit, "Dialog", 18, 210, Pos.CENTER, 570, 540);
		button_Quit.setOnAction((_) -> {ControllerDeleteUser.performQuit(); });
		
		// This is the end of the GUI Widgets for the page
		
		// Due to the very dynamic nature of this page, setting the widget into the Root Pane has 
		// has been delegated to the repaintTheWindow and doSelectUser controller methods.
		// Don't follow this pattern if formatting of the page does not change dynamically.
	}	

	/*-*******************************************************************************************

	Helper methods used to minimizes the number of lines of code needed above
	
	*/

	/**********
	 * Private local method to initialize the standard fields for a label
	 * 
	 * @param l		The Label object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x,
			double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);		
	}
	
	
	/**********
	 * Private local method to initialize the standard fields for a button
	 * 
	 * @param b		The Button object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	protected static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x,
			double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}

	/**********
	 * Private local method to initialize the standard fields for a ComboBox
	 * 
	 * @param c		The ComboBox object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the ComboBox
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	protected static void setupComboBoxUI(ComboBox <String> c, String ff, double f, double w,
			double x, double y){
		c.setStyle("-fx-font: " + f + " " + ff + ";");
		c.setMinWidth(w);
		c.setLayoutX(x);
		c.setLayoutY(y);
	}
}
