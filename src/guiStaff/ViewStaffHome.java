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

import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

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
 *  @version 1.01		2026-04-08 Added staff discussion thread CRUD UI controls
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
	
	// GUI Area 2: Staff thread CRUD management widgets
	
	/** Label for the thread management area. */
	protected static Label label_ThreadManagement = new Label();

	/** List of discussion threads available for staff management. */
	protected static ListView<String> listView_Threads = new ListView<>();

	/** Text field for creating a new thread. */
	protected static TextField text_NewThreadTitle = new TextField();

	/** Text field for renaming the selected thread. */
	protected static TextField text_UpdatedThreadTitle = new TextField();

	/** Button to refresh the thread list. */
	protected static Button button_RefreshThreads = new Button("Refresh Threads");

	/** Button to create a new thread. */
	protected static Button button_CreateThread = new Button("Create Thread");

	/** Button to update the selected thread title. */
	protected static Button button_UpdateThread = new Button("Rename Selected Thread");

	/** Button to delete the selected thread. */
	protected static Button button_DeleteThread = new Button("Delete Selected Thread");
	
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
		
		refreshThreadList();

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
		
			// This is a stub, so this area is empty
		
		// GUI Area 2
		label_ThreadManagement.setText("Discussion Thread Management");
		setupLabelUI(label_ThreadManagement, "Arial", 22, width, Pos.BASELINE_LEFT, 20, 120);

		listView_Threads.setLayoutX(20);
		listView_Threads.setLayoutY(160);
		listView_Threads.setPrefWidth(300);
		listView_Threads.setPrefHeight(260);

		text_NewThreadTitle.setLayoutX(350);
		text_NewThreadTitle.setLayoutY(170);
		text_NewThreadTitle.setPrefWidth(280);
		text_NewThreadTitle.setPromptText("Enter new thread title");

		text_UpdatedThreadTitle.setLayoutX(350);
		text_UpdatedThreadTitle.setLayoutY(230);
		text_UpdatedThreadTitle.setPrefWidth(280);
		text_UpdatedThreadTitle.setPromptText("Enter updated title for selected thread");

		setupButtonUI(button_RefreshThreads, "Dialog", 16, 180, Pos.CENTER, 350, 290);
		button_RefreshThreads.setOnAction((_) -> { ControllerStaffHome.performRefreshThreads(); });

		setupButtonUI(button_CreateThread, "Dialog", 16, 180, Pos.CENTER, 350, 340);
		button_CreateThread.setOnAction((_) -> { ControllerStaffHome.performCreateThread(); });

		setupButtonUI(button_UpdateThread, "Dialog", 16, 220, Pos.CENTER, 350, 390);
		button_UpdateThread.setOnAction((_) -> { ControllerStaffHome.performUpdateThread(); });

		setupButtonUI(button_DeleteThread, "Dialog", 16, 220, Pos.CENTER, 350, 440);
		button_DeleteThread.setOnAction((_) -> { ControllerStaffHome.performDeleteThread(); });
		
		
		// GUI Area 3
        setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
        button_Logout.setOnAction((_) -> {ControllerStaffHome.performLogout(); });
        
        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
        button_Quit.setOnAction((_) -> {ControllerStaffHome.performQuit(); });

		// This is the end of the GUI initialization code
		
		// Place all of the widget items into the Root Pane's list of children
        theRootPane.getChildren().addAll(
			label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
			label_ThreadManagement, listView_Threads, text_NewThreadTitle,
			text_UpdatedThreadTitle, button_RefreshThreads, button_CreateThread,
			button_UpdateThread, button_DeleteThread,
	        line_Separator4, button_Logout, button_Quit);
        
	}
	
	/**********
	 * <p> Method: refreshThreadList() </p>
	 *
	 * <p> Description: Refreshes the visible ListView of active discussion threads
	 * so staff can select a thread to rename or delete. If there are no active
	 * threads, the ListView displays a placeholder message. </p>
	 */
	
	protected static void refreshThreadList() {
		listView_Threads.getItems().clear();

		java.util.List<entityClasses.DiscussionThread> threads =
				theDatabase.getActiveDiscussionThreads();

		if (threads.isEmpty()) {
			listView_Threads.getItems().add("No active threads available");
			return;
		}

		for (entityClasses.DiscussionThread thread : threads) {
			listView_Threads.getItems().add(thread.getThreadId() + ": " + thread.getTitle());
		}
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
}
