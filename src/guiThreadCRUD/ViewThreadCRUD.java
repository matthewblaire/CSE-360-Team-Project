package guiThreadCRUD;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import database.Database;

import entityClasses.User;
import guiPost.ControllerPost;
import entityClasses.DiscussionThread;

/*******
 * <p> Title: ViewThreadCRUD Class. </p>
 * 
 * <p> Description: The Java/FX-based Staff Thread CRUD page. This page allows staff
 * users to create, read, update, and delete discussion threads while preserving the
 * visual style and page flow used by the rest of the project. </p>
 * 
 * @author Saam Kavusi
 * 
 * @version 1.00        2026-04-11 Initial version for staff discussion thread CRUD
 */

public class ViewThreadCRUD {
	
	/*-*******************************************************************************************

	Attributes
	
	 */
	
	// These are the application values required by the user interface
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	// GUI Area 1
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();

	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);

	// GUI Area 2
	protected static Label label_ThreadList = new Label("Discussion Threads");
	protected static ListView<DiscussionThread> listView_Threads = new ListView<>();

	protected static Label label_NewThreadTitle = new Label("New Thread Title");
	protected static TextField textField_NewThreadTitle = new TextField();
	protected static Button button_CreateThread = new Button("Create Thread");

	protected static Label label_EditThreadTitle = new Label("Edit Selected Thread Title");
	protected static TextField textField_EditThreadTitle = new TextField();
	protected static Button button_UpdateThread = new Button("Update Thread");
	protected static Button button_DeleteThread = new Button("Delete Thread");
	protected static Button button_Refresh = new Button("Refresh");

	protected static Label label_Status = new Label("");

	protected static Line line_Separator4 = new Line(20, 500, width-20, 500);

	// GUI Area 3
	protected static Button button_Back = new Button("Back");
	protected static Button button_Quit = new Button("Quit");

	// Shared references
	private static ViewThreadCRUD theView;
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;
	protected static Pane theRootPane;
	protected static User theUser;
	protected static Alert alert1 = new Alert(AlertType.ERROR);	

	private static Scene theThreadCRUDScene;

	/*-*******************************************************************************************

	Constructors
	
	 */

	/**********
	 * <p> Method: displayThreadCRUD(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to
	 * cause the Thread CRUD page to be displayed. </p>
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and its methods
	 * 
	 * @param user specifies the User for this GUI and its methods
	 */
	public static void displayThreadCRUD(Stage ps, User user) {
		
		theStage = ps;
		theUser = user;
		
		if (theView == null) theView = new ViewThreadCRUD();
		
		theDatabase.getUserAccountDetails(user.getUserName());
		label_UserDetails.setText("User: " + theUser.getUserName());

		ModelThreadCRUD.refreshThreadList();

		theStage.setTitle("CSE 360 Foundations: Manage Discussion Threads");
		theStage.setScene(theThreadCRUDScene);
		theStage.show();
	}

	/**********
	 * <p> Method: ViewThreadCRUD() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user
	 * interface. This is a singleton and is only performed once. </p>
	 */
	private ViewThreadCRUD() {
		
		theRootPane = new Pane();
		theThreadCRUDScene = new Scene(theRootPane, width, height);

		// GUI Area 1
		label_PageTitle.setText("Manage Discussion Threads");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);

		// GUI Area 2
		setupLabelUI(label_ThreadList, "Arial", 20, 300, Pos.CENTER, 55, 115);

		listView_Threads.setLayoutX(40);
		listView_Threads.setLayoutY(150);
		listView_Threads.setMinWidth(300);
		listView_Threads.setPrefWidth(300);
		listView_Threads.setMinHeight(300);
		listView_Threads.setPrefHeight(300);

		listView_Threads.setCellFactory((_) -> new javafx.scene.control.ListCell<DiscussionThread>() {
			@Override
			protected void updateItem(DiscussionThread item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
				} else {
					setText(item.getTitle());
				}
			}
		});

		listView_Threads.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal != null) {
				textField_EditThreadTitle.setText(newVal.getTitle());
			}
		});

		setupLabelUI(label_NewThreadTitle, "Arial", 18, 280, Pos.CENTER, 390, 125);

		textField_NewThreadTitle.setLayoutX(390);
		textField_NewThreadTitle.setLayoutY(160);
		textField_NewThreadTitle.setMinWidth(310);
		textField_NewThreadTitle.setPrefWidth(310);


		setupButtonUI(button_CreateThread, "Dialog", 18, 220, Pos.CENTER, 420, 205);
		button_CreateThread.setOnAction(_ -> {
			ControllerThreadCRUD.performCreatethread(theStage, theUser, textField_NewThreadTitle.getText());
        });
		
		
		setupLabelUI(label_EditThreadTitle, "Arial", 18, 280, Pos.CENTER, 390, 255);

		textField_EditThreadTitle.setLayoutX(390);
		textField_EditThreadTitle.setLayoutY(290);
		textField_EditThreadTitle.setMinWidth(310);
		textField_EditThreadTitle.setPrefWidth(310);

		setupButtonUI(button_UpdateThread, "Dialog", 18, 220, Pos.CENTER, 420, 335);
		button_UpdateThread.setOnAction(_ -> {
			DiscussionThread dt = listView_Threads.getSelectionModel().getSelectedItem();
			if (dt == null) {
				alert1.setTitle("Error Dialog");
				alert1.setHeaderText("Error Dialog");
				alert1.setContentText("Please select from the list to perform the action on.");
				alert1.showAndWait();
				return;
			}
			ControllerThreadCRUD.performUpdatethread(theStage, theUser,
					textField_EditThreadTitle.getText(), dt.getThreadId());
        });
		
		setupButtonUI(button_DeleteThread, "Dialog", 18, 220, Pos.CENTER, 420, 380);
		button_DeleteThread.setOnAction(_ -> {
			DiscussionThread dt = listView_Threads.getSelectionModel().getSelectedItem();
			if (dt == null) {
				alert1.setTitle("Error Dialog");
				alert1.setHeaderText("Error Dialog");
				alert1.setContentText("Please select from the list to perform the action on.");
				alert1.showAndWait();
				return;
			}
			ControllerThreadCRUD.performDeletethread(theStage, theUser, dt.getThreadId());
        });
		
		setupButtonUI(button_Refresh, "Dialog", 18, 220, Pos.CENTER, 420, 425);

		setupLabelUI(label_Status, "Arial", 16, 620, Pos.BASELINE_LEFT, 40, 470);

		// GUI Area 3
		setupButtonUI(button_Back, "Dialog", 18, 250, Pos.CENTER, 140, 520);
		button_Back.setOnAction((_) -> { ControllerThreadCRUD.goBackToStaffHome(); });

		setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 410, 520);
		button_Quit.setOnAction((_) -> { System.exit(0); });

		theRootPane.getChildren().addAll(
				label_PageTitle, label_UserDetails, line_Separator1,
				label_ThreadList, listView_Threads,
				label_NewThreadTitle, textField_NewThreadTitle, button_CreateThread,
				label_EditThreadTitle, textField_EditThreadTitle,
				button_UpdateThread, button_DeleteThread, button_Refresh,
				label_Status, line_Separator4, button_Back, button_Quit);
	}

	/*-********************************************************************************************

	Helper methods to reduce code length

	 */

	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x,
			double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}

	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x,
			double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}
}