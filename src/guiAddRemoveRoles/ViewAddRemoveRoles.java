package guiAddRemoveRoles;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;

/*******
 * <p> Title: GUIAddRemoveRolesPage Class. </p>
 * 
 * <p> Description: The Java/FX-based page for changing the assigned roles to users.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-20 Initial version
 *  
 */

public class ViewAddRemoveRoles {
	
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
	/** The label prompting the admin to select a user to update. */
	protected static Label label_SelectUser = new Label("Select a user to be updated:");
	/** The combo box for selecting a user whose roles will be modified. */
	protected static ComboBox <String> combobox_SelectUser = new ComboBox <String>();
	
	// Area 2b: When a user has been selected these widgets are shown and can be used
	/** The list of roles available to be added to the selected user. */
	protected static List<String> addList = new ArrayList<String>();
	/** The button to add the selected role to the user. */
	protected static Button button_AddRole = new Button("Add This Role");
	/** The list of roles available to be removed from the selected user. */
	protected static List<String> removeList = new ArrayList<String>();
	/** The button to remove the selected role from the user. */
	protected static Button button_RemoveRole = new Button("Remove This Role");
	/** The label displaying the selected user's current roles. */
	protected static Label label_CurrentRoles = new Label("This user's current roles:");
	/** The label prompting the admin to select a role to add. */
	protected static Label label_SelectRoleToBeAdded = new Label("Select a role to be added:");
	/** The combo box for selecting a role to add to the user. */
	protected static ComboBox <String> combobox_SelectRoleToAdd = new ComboBox <String>();
	/** The label prompting the admin to select a role to remove. */
	protected static Label label_SelectRoleToBeRemoved = new Label("Select a role to be removed:");
	/** The combo box for selecting a role to remove from the user. */
	protected static ComboBox <String> combobox_SelectRoleToRemove = new ComboBox <String>();
		
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
	private static ViewAddRemoveRoles theView;	// Used to determine if instantiation of the class
												// is needed
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;		

	/** The JavaFX Stage used to display this page. */
	protected static Stage theStage;
	/** The root pane that holds all GUI widgets. */
	protected static Pane theRootPane;
	/** The current user of the application. */
	protected static User theUser;

	/** The scene for the add/remove roles page. */
	public static Scene theAddRemoveRolesScene = null;
	/** The username of the currently selected user whose roles are being updated. */
	protected static String theSelectedUser = "";
	/** The role being added. */
	protected static String theAddRole = "";
	/** The role being removed. */
	protected static String theRemoveRole = "";



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
	public static void displayAddRemoveRoles(Stage ps, User user) {
		
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		
		// If not yet established, populate the static aspects of the GUI by creating the 
		// singleton instance of this class
		if (theView == null) theView = new ViewAddRemoveRoles();
		
		// Default to no user selected
		combobox_SelectUser.getSelectionModel().select(0);
		
		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.  This page is different from the others.  Since there are two 
		// modes (1: user has not been selected, and 2: user has been selected) there are two
		// lists of widgets to be displayed.  For this reason, we have implemented the following 
		// two controller methods to deal with this dynamic aspect.
		ControllerAddRemoveRoles.repaintTheWindow();
		ControllerAddRemoveRoles.doSelectUser();
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
	public ViewAddRemoveRoles() {
		
		// This page is used by all roles, so we do not specify the role being used		
			
		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theAddRemoveRolesScene = new Scene(theRootPane, width, height);
		
		// Populate the window with the title and other common widgets and set their static state
		
		// GUI Area 1
		label_PageTitle.setText("Add/Removed Roles Page");
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
		combobox_SelectUser.setItems(FXCollections.observableArrayList(userList));
		combobox_SelectUser.getSelectionModel().select(0);
		combobox_SelectUser.getSelectionModel().selectedItemProperty()
    	.addListener((@SuppressWarnings("unused") ObservableValue<? extends String> observable, 
    		@SuppressWarnings("unused") String oldvalue, 
    		@SuppressWarnings("unused") String newValue) -> {ControllerAddRemoveRoles.doSelectUser();});
		
		// GUI Area 2b
		setupLabelUI(label_CurrentRoles, "Arial", 16, 300, Pos.BASELINE_LEFT, 50, 170);	
		setupLabelUI(label_SelectRoleToBeAdded, "Arial", 20, 300, Pos.BASELINE_LEFT, 20, 210);
		setupComboBoxUI(combobox_SelectRoleToAdd, "Dialog", 16, 150, 280, 205);
		setupButtonUI(button_AddRole, "Dialog", 16, 150, Pos.CENTER, 460, 205);
		ViewAddRemoveRoles.button_AddRole.setOnAction((_) -> 
			{ControllerAddRemoveRoles.performAddRole(); });
		setupButtonUI(button_RemoveRole, "Dialog", 16, 150, Pos.CENTER, 460, 275);			
		ViewAddRemoveRoles.button_RemoveRole.setOnAction((_) -> 
			{ControllerAddRemoveRoles.performRemoveRole(); });
		setupLabelUI(label_SelectRoleToBeRemoved, "Arial", 20, 300, Pos.BASELINE_LEFT, 20, 280);	
		setupComboBoxUI(combobox_SelectRoleToRemove, "Dialog", 16, 150, 280, 275);	
		
		// GUI Area 3		
		setupButtonUI(button_Return, "Dialog", 18, 210, Pos.CENTER, 20, 540);
		button_Return.setOnAction((_) -> {ControllerAddRemoveRoles.performReturn(); });

		setupButtonUI(button_Logout, "Dialog", 18, 210, Pos.CENTER, 300, 540);
		button_Logout.setOnAction((_) -> {ControllerAddRemoveRoles.performLogout(); });
    
		setupButtonUI(button_Quit, "Dialog", 18, 210, Pos.CENTER, 570, 540);
		button_Quit.setOnAction((_) -> {ControllerAddRemoveRoles.performQuit(); });
		
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
