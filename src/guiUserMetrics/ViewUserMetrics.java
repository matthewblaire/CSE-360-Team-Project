package guiUserMetrics;

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
import entityClasses.Post;
import entityClasses.User;
import guiStaff.ViewStaffHome;
import javafx.scene.control.TextArea;
import java.time.LocalDateTime;
import java.util.Hashtable;
import java.util.List;

import database.Database;

/*******
 * <p> Title: ViewUserMetrics Class. </p>
 *
 * <p> Description: This class provides the JavaFX GUI widgets to enable
 * the user to view a post in the system and interact with it. </p>
 *
 * @author Azeer Esmail - Team 2
 *
 */


public class ViewUserMetrics {

    /*-*******************************************************************************************
    Attributes
    */

    private static double width  = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    // GUI Widgets
    protected static Label label_UserDetails = new Label();
    protected static Label label_PageTitle   = new Label("View User Metrics");
    // Separator lines
    protected static Line line_Separator1 = new Line(20, 95, width - 20, 95);
    protected static Line line_Separator2 = new Line(20, 525, width - 20, 525);

    // Navigation buttons
    protected static Button button_ReturnToHomePage = new Button("Return");
    protected static Button button_Logout           = new Button("Logout");
    protected static Button button_Quit             = new Button("Quit");

    private static Database  theDatabase = applicationMain.FoundationsMain.database;

    protected static Stage theStage;
    protected static Pane  theRootPane;
    protected static User  theUser;
    protected static Post  currentPost;

    private static Scene            theViewScene;
	/** The combo box for selecting a user to show metrics. */
    protected static ComboBox<String> combobox_SelectUser = new ComboBox<String>();
	protected static String theSelectedUser = "";
	/** The text field displaying the metrics. */
	public static TextArea text_Metrics = new TextArea();
	Hashtable<String, Object> hs;
	/**********
	 * <p> Method: displayUserMetrics(Stage ps, User user </p>
	 *
	 * <p> Description: Displays the ViewUserMetrics page. </p>
	 *
	 * @param ps     the primary JavaFX Stage
	 * @param user   the currently logged-in User
	 */
    public static void displayUserMetrics(Stage ps, User user) {
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		new ViewUserMetrics();

		// Refresh the user list
		List<String> userList = theDatabase.getUserList();
		combobox_SelectUser.setItems(FXCollections.observableArrayList(userList));
		combobox_SelectUser.getSelectionModel().select(0);
		text_Metrics.setText("");
		
		theStage.setTitle("CSE 360 Foundations: Staff Home Page");
		theStage.setScene(theViewScene);
		theStage.show();	
    }

	/**********
	 * <p> Method: ViewUserMetrics() — private constructor </p>
	 *
	 * <p> Description: Builds and configures all GUI widgets (singleton pattern).
	 * Static layout and event handlers are set here; dynamic data is handled in
	 * displayUserMetrics(). </p>
	 */
    private ViewUserMetrics() {
        theRootPane = new Pane();
        theViewScene = new Scene(theRootPane, width, height);
        theRootPane.setStyle("-fx-background-color: #f5f6fa;");

        // ── Area 1: Header ───────────────────────────────────────────────────────
        setupLabelUI(label_PageTitle, "Arial", 24, width, Pos.CENTER, 0, 5);

        setupLabelUI(label_UserDetails, "Arial", 18, width - 220, Pos.BASELINE_LEFT, 20, 52);

        setupButtonUI(button_ReturnToHomePage, "Dialog", 16, 170, Pos.CENTER, 608, 44);
        button_ReturnToHomePage.setOnAction(_ -> {
        	ViewStaffHome.displayStaffHome(theStage, theUser);
        });

		setupComboBoxUI(combobox_SelectUser, "Dialog", 16, 25, 20, 125);
		combobox_SelectUser.getSelectionModel().selectedItemProperty()
			.addListener((@SuppressWarnings("unused") ObservableValue<? extends String> observable,
				@SuppressWarnings("unused") String oldValue,
				@SuppressWarnings("unused") String newValue) -> {
					String selectedUser = combobox_SelectUser.getValue();
					String textMetrics = "";
					if (selectedUser == null || selectedUser.equals("<Select a User>")) return;

					hs = ControllerUserMetrics.getUserMetrics(theStage, theUser, selectedUser);
					if (hs == null) return;
			        
					@SuppressWarnings("unchecked")
					List<Object[]> postsMetrics = (List<Object[]>) hs.get("postsMetrics");
					Double postsTotalLength = 0.;
					if (postsMetrics != null) {
				        for (Object[] row: postsMetrics) {
				            LocalDateTime timestamp = (LocalDateTime) row[0];
				            Integer len = (Integer) row[1];
				            postsTotalLength += len;
				        	textMetrics += "Posted at: " + timestamp + " Length:" + len +"\n";
				        }
					}
					Double postsAvgLength = Math.ceil(postsTotalLength / (postsMetrics.size() + 10E-10));
					
					textMetrics += "\n";
					@SuppressWarnings("unchecked")
					List<Object[]> repliesMetrics = (List<Object[]>) hs.get("repliesMetrics");
					Double repliesTotalLength = 0.;
					if (repliesMetrics != null) {
				        for (Object[] row: repliesMetrics) {
				            LocalDateTime timestamp = (LocalDateTime) row[0];
				            Integer len = (Integer) row[1];
				            repliesTotalLength += len;
				            System.out.println();
				            textMetrics += "Replied at: " + timestamp + " Length:" + len +"\n";
				        }
					}
					Double repliesAvgLength = Math.ceil(repliesTotalLength / (repliesMetrics.size() + 10E-10));
					
					textMetrics += "-----------------------------------------\n";
					textMetrics += "Posts: " + postsMetrics.size() + "  Average length: " + postsAvgLength +"\n";
					textMetrics += "Replies: " + repliesMetrics.size() + "  Average length: " + repliesAvgLength +"\n";
					text_Metrics.setText(textMetrics);
				});
		
        text_Metrics.setLayoutX(20);
        text_Metrics.setLayoutY(158);
        text_Metrics.setPrefWidth(width - 40);
        text_Metrics.setPrefHeight(250);
        text_Metrics.setWrapText(true);
        text_Metrics.setEditable(false);
        text_Metrics.setFont(Font.font("Arial", 14));
        text_Metrics.setStyle(
            "-fx-control-inner-background: white;" +
            "-fx-background-color: white;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-background-radius: 5;" +
            "-fx-border-radius: 5;"
        );
        // ── Area 6: Footer ────────────────────────────────────────────────────────
        setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
        button_Logout.setOnAction(_ -> { ControllerUserMetrics.performLogout(theStage); });

        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
        button_Quit.setOnAction(_ -> { ControllerUserMetrics.performQuit(); });

        // Add all permanent widgets to the root pane
        theRootPane.getChildren().addAll(
            label_PageTitle, label_UserDetails,
            line_Separator1, button_ReturnToHomePage,
            line_Separator2, combobox_SelectUser, text_Metrics,
            button_Logout, button_Quit
        );
    }

    /*-*******************************************************************************************
    Helper Methods
    */


	/**********
	 * Private local method to initialize the standard fields for a Label.
	 * 
	 * @param b		The Label object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
    private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
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
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
	    b.setFont(Font.font(ff, f));
	    b.setMinWidth(w);
	    b.setAlignment(p);
	    b.setLayoutX(x);
	    b.setLayoutY(y);
	}
	
	/** Initializes the standard fields for a combo box.
	 *
	 * @param c		The ComboBox object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the ComboBox
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	protected static void setupComboBoxUI(ComboBox<String> c, String ff, double f, double w, double x, double y) {
		c.setStyle("-fx-font: " + f + " " + ff + ";");
		c.setMinWidth(w);
		c.setLayoutX(x);
		c.setLayoutY(y);
	}
}
