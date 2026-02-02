package guiAdminHome;

import java.util.List;

import database.Database;
import entityClasses.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*******
 * <p> Title: ViewUserList Class. </p>
 * 
 * <p> Description: The Java/FX-based page for listing all users.</p>
 * 
 * @author Azeer Esmail
 *  
 */

public class ViewUserList {
	
	/*-*******************************************************************************************

	Attributes
	
	*/
	// These are the application values required by the user interface
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;
	protected static Label label_PageTitle = new Label();
	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);
	protected static Line line_Separator4 = new Line(20, 525, width-20,525);
	protected static Button button_Return = new Button("Return");
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	protected static Stage theStage;			// The Stage that JavaFX has established for us
	protected static Pane theRootPane;			// The Pane that holds all the GUI widgets 
	protected static User theUser;				// The current user of the application
	public static Scene theUserListScene = null;	// The Scene each invocation populates


	/*-*******************************************************************************************
	
	Constructors
	
	*/
	public ViewUserList() {
		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theUserListScene = new Scene(theRootPane, width, height);
		
		// Populate the window with the title and other common widgets and set their static state
		label_PageTitle.setText("User List");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);
	
		setupButtonUI(button_Return, "Dialog", 18, 210, Pos.CENTER, 20, 540);
		button_Return.setOnAction((_) -> {guiAdminHome.ViewAdminHome.displayAdminHome(ViewUserList.theStage,
				ViewUserList.theUser); });
	}
	
	
	
	/**********
	 * <p> Method: displayUserList(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the entry point to cause
	 * the UserList Home page to be displayed.
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the User for this GUI and it's methods
	 * 
	 */
	public static void displayUserList(Stage ps, User user) {
		// Establish the references to the GUI and the current user
		theUser = user;
		theStage = ps;

		// Create an instance to initialize static attributes
		ViewUserList theView = new ViewUserList();
		
		// Clear the view
		ViewUserList.theRootPane.getChildren().clear();
		
		// Populate the user list
		populateUserList();
		
		// Populate the static aspects
		ViewUserList.theRootPane.getChildren().addAll(
				ViewUserList.label_PageTitle,
				ViewUserList.line_Separator4, 
				ViewUserList.button_Return);

		// Set the title for the window, the stage, the scene and show
		ViewUserList.theStage.setTitle("User List");
		ViewUserList.theStage.setScene(ViewUserList.theUserListScene);
		ViewUserList.theStage.show();
	}
	
	
	/**********
	 * <p> Method: populateUserList() </p>
	 * 
	 * <p> Description: This method will populate the GUI with a list of existing users </p>
	 * 
	 */
	private static void populateUserList() {
	    // Get the list of all usernames from database
	    List<String> usernames = theDatabase.getUserList();
	    
	    // Remove the "<Select a User>" placeholder from the list
	    usernames.remove("<Select a User>");
	    
	    double startY = 120; // Starting Y position for first user
	    double yIncrement = 30; // Space between each user
	    
	    for (int i = 0; i < usernames.size(); i++) {
	        String username = usernames.get(i);
	        
	        // Get user details from database
	        theDatabase.getUserAccountDetails(username);
	        
	        // Create labels for each user attribute
	        Label usernameLabel = new Label(username);
	        setupLabelUI(usernameLabel, "Arial", 14, 150, Pos.CENTER_LEFT, 50, startY + (i * yIncrement));
	        
	        Label firstNameLabel = new Label(theDatabase.getCurrentFirstName());
	        setupLabelUI(firstNameLabel, "Arial", 14, 150, Pos.CENTER_LEFT, 220, startY + (i * yIncrement));
	        
	        // Create roles string
	        String roles = "";
	        if (theDatabase.getCurrentAdminRole()) roles += "Admin ";
	        if (theDatabase.getCurrentNewStudentRole()) roles += "Student ";
	        if (theDatabase.getCurrentNewStaffRole()) roles += "Staff ";
	        if (roles.isEmpty()) roles = "No Roles";
	        
	        Label rolesLabel = new Label(roles.trim());
	        setupLabelUI(rolesLabel, "Arial", 14, 200, Pos.CENTER_LEFT, 400, startY + (i * yIncrement));
	        
	        Label emailLabel = new Label(theDatabase.getCurrentEmailAddress());
	        setupLabelUI(emailLabel, "Arial", 14, 250, Pos.CENTER_LEFT, 620, startY + (i * yIncrement));
	        
	        // Add all labels to the root pane
	        theRootPane.getChildren().addAll(usernameLabel, firstNameLabel, rolesLabel, emailLabel);
	    }
	    
	    // Add column headers
	    Label headerUsername = new Label("Username");
	    setupLabelUI(headerUsername, "Arial", 16, 150, Pos.CENTER_LEFT, 50, startY - 30);
	    headerUsername.setStyle("-fx-font-weight: bold;");
	    
	    Label headerFirstName = new Label("First Name");
	    setupLabelUI(headerFirstName, "Arial", 16, 150, Pos.CENTER_LEFT, 220, startY - 30);
	    headerFirstName.setStyle("-fx-font-weight: bold;");
	    
	    Label headerRoles = new Label("Roles");
	    setupLabelUI(headerRoles, "Arial", 16, 200, Pos.CENTER_LEFT, 400, startY - 30);
	    headerRoles.setStyle("-fx-font-weight: bold;");
	    
	    Label headerEmail = new Label("Email Address");
	    setupLabelUI(headerEmail, "Arial", 16, 250, Pos.CENTER_LEFT, 620, startY - 30);
	    headerEmail.setStyle("-fx-font-weight: bold;");
	    
	    // Add headers to the root pane
	    theRootPane.getChildren().addAll(headerUsername, headerFirstName, headerRoles, headerEmail);
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

