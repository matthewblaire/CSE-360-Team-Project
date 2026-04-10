package guiStaff;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
//import database.Database;
import entityClasses.User;
import guiStudent.ControllerStudentHome;


/*******
 * <p> Title: ViewStaffHome Class. </p>
 * 
 * <p> Description: The Java/FX-based Staff Home Page.  The page is a stub for some role needed for
 * the application.  The widgets on this page are likely the minimum number and kind for other role
 * pages that may be needed.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-04-20 Initial version
 *  
 */

public class ViewStaffHome {
	
	/*-*******************************************************************************************

	Attributes
	
	 */
	
	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;


	// These are the widget attributes for the GUI. There are 3 areas for this GUI.
	
	// GUI Area 1: It informs the user about the purpose of this page, whose account is being used,
	// and a button to allow this user to update the account settings
	/** The label displaying the page title. */
	protected static Label label_PageTitle = new Label();
	/** The label displaying the current user details. */
	protected static Label label_UserDetails = new Label();
	/** The button to navigate to the account update page. */
	protected static Button button_UpdateThisUser = new Button("Account Update");

	// This is a separator and it is used to partition the GUI for various tasks
	/** The line separator between the header area and the main content area. */
	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);

	// GUI ARea 2: This is a stub, so there are no widgets here.  For an actual role page, this are
	// would contain the widgets needed for the user to play the assigned role.
	
	/** the button navigates to user metrics view**/
	protected static Button button_viewUserMetrics = new Button("View User Metrics");

	/** The button to navigate to the staff request submission page. */
	protected static Button button_SubmitRequest = new Button("Submit Admin Request");
	/** The button to navigate to the request queue page. */
	protected static Button button_ViewQueue = new Button("View Request Queue");
	
	/**
	 * The button to navigate to browse posts
	 */
	protected static Button button_browsePosts = new Button("Browse Posts");
	
	/**
	 * The button to navigate to messages list
	 */
	protected static Button button_messagesList = new Button("My Messages");
	
	// This is a separator and it is used to partition the GUI for various tasks
	/** The line separator between the main content area and the bottom button area. */
	protected static Line line_Separator4 = new Line(20, 525, width-20,525);

	// GUI Area 3: This is last of the GUI areas.  It is used for quitting the application and for
	// logging out.
	/** The button to log out the current user. */
	protected static Button button_Logout = new Button("Logout");
	/** The button to quit the application. */
	protected static Button button_Quit = new Button("Quit");
	

	

	// This is the end of the GUI objects for the page.
	
	// These attributes are used to configure the page and populate it with this user's information
	private static ViewStaffHome theView;		// Used to determine if instantiation of the class
												// is needed

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/** The JavaFX Stage used to display this page. */
	protected static Stage theStage;			// The Stage that JavaFX has established for us
	/** The root Pane that holds all the GUI widgets. */
	protected static Pane theRootPane;			// The Pane that holds all the GUI widgets
	/** The current logged-in User. */
	protected static User theUser;				// The current logged in User

	private static Scene theStaffHomeScene;		// The shared Scene each invocation populates
	/** The role identifier for the staff role (Admin: 1; Student: 2; Staff: 3). */
	protected static final int theRole = 3;		// Admin: 1; Student: 2; Staff: 3

	/*-*******************************************************************************************

	Constructors
	
	 */

	/**********
	 * <p> Method: displayStaffHome(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Staff Home page to be displayed.
	 * 
	 * It first sets up every shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup.  If not, it instantiates the class, 
	 * initializes all the static aspects of the GIUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 * 
	 * After the instantiation, the code then populates the elements that change based on the user
	 * and the system's current state.  It then sets the Scene onto the stage, and makes it visible
	 * to the user.
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the User for this GUI and it's methods
	 * 
	 */
	public static void displayStaffHome(Stage ps, User user) {
		
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		
		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewStaffHome();		// Instantiate singleton if needed
		
		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.
		theDatabase.getUserAccountDetails(user.getUserName());
		applicationMain.FoundationsMain.activeHomePage = theRole;
		
		label_UserDetails.setText("User: " + theUser.getUserName());// Set the username

		// Set the title for the window, display the page, and wait for the Admin to do something
		theStage.setTitle("CSE 360 Foundations: Staff Home Page");
		theStage.setScene(theStaffHomeScene);						// Set this page onto the stage
		theStage.show();											// Display it to the user
	}
	
	/**********
	 * <p> Method: ViewStaffHome() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object. </p>
	 * 
	 * This is a singleton and is only performed once.  Subsequent uses fill in the changeable
	 * fields using the displayStaffHome method.</p>
	 * 
	 */
	private ViewStaffHome() {
		
		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theStaffHomeScene = new Scene(theRootPane, width, height);	// Create the scene
		
		// Set the title for the window
		
		// Populate the window with the title and other common widgets and set their static state
		
		// GUI Area 1
		label_PageTitle.setText("Staff Home Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
		
		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((_) -> {ControllerStaffHome.performUpdate(); });
		
		// GUI Area 2

			// This is a stub, so this area is empty - not anymore
		setupButtonUI(button_viewUserMetrics, "Dialog", 18, 300, Pos.CENTER, 250, 220);
		button_viewUserMetrics.setOnAction((_) -> { ControllerStaffHome.goToUserMetrics(); });

		setupButtonUI(button_SubmitRequest, "Dialog", 18, 300, Pos.CENTER, 250, 290);
		button_SubmitRequest.setOnAction((_) -> { ControllerStaffHome.goToSubmitRequest(); });

		setupButtonUI(button_ViewQueue, "Dialog", 18, 300, Pos.CENTER, 250, 350);
		button_ViewQueue.setOnAction((_) -> { ControllerStaffHome.goToRequestQueue(); });
		
		setupButtonUI(button_browsePosts, "Dialog", 18, 300, Pos.CENTER, 250, 160);
		button_browsePosts.setOnAction((_) -> { ControllerStaffHome.goToBrowsePosts(); });
		
		setupButtonUI(button_messagesList, "Dialog", 18, 300, Pos.CENTER, 250, 280);
		button_messagesList.setOnAction((_) -> { ControllerStaffHome.goToMessagesList(); });
		
		// GUI Area 3
        setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
        button_Logout.setOnAction((_) -> {ControllerStaffHome.performLogout(); });
        
        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
        button_Quit.setOnAction((_) -> {ControllerStaffHome.performQuit(); });

		// This is the end of the GUI initialization code
		
		// Place all of the widget items into the Root Pane's list of children
        theRootPane.getChildren().addAll(
			label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1, button_viewUserMetrics,
	        line_Separator4, button_Logout, button_Quit, button_browsePosts, button_messagesList,
			label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
			button_viewUserMetrics, button_SubmitRequest, button_ViewQueue,
	        line_Separator4, button_Logout, button_Quit);
	}
	
	
	/*-********************************************************************************************

	Helper methods to reduce code length

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
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, 
			double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}

	/**********
	 * Protected local method to return the current user
	 * 
	 * @param theUser		current user
	 */
	protected static User getTheUser() {
		return theUser;
	}
}
