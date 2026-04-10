package guiMessageThread;

import java.time.format.DateTimeFormatter;
import java.util.List;

import entityClasses.Post;
import entityClasses.PrivateMessage;
import entityClasses.Reply;
import entityClasses.User;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*******
 * <p> Title: ViewMessageThread Class. </p>
 *
 * <p> Description: The View component of the Message Thread MVC triplet. This singleton page
 * shows the currently logged-in user all private messages exchanged with one other user,
 * ordered chronologically. When a message references a valid Post or Reply, the page also
 * displays a note showing what that message is responding to. This page also supports
 * composing a new message, optionally pre-linked to a Post or Reply selected elsewhere
 * in the application. </p>
 *
 * @author CSE 360 Team
 *
 */
public class ViewMessageThread {

	/*-*******************************************************************************************

	Attributes

	 */

	private static double width  = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;


	// ---- Area 1: Header ----

	/** Page title label. */
	protected static Label label_PageTitle   = new Label("Message Thread");
	/** Shows the currently logged-in username. */
	protected static Label label_UserDetails = new Label();
	/** Returns the user to the messages list page. */
	protected static Button button_Return    = new Button("Return");
	/** Horizontal rule separating the header from the main content. */
	protected static Line line_Separator1    = new Line(20, 95, width - 20, 95);


	// ---- Area 2: Message thread ----

	/** Shows who the conversation is with. */
	protected static Label label_ThreadTitle = new Label("Conversation");
	/** Shows the count of messages in the displayed thread. */
	protected static Label label_MessageCount = new Label("Messages: 0");
	/** Refresh button to reload the thread. */
	protected static Button button_Refresh = new Button("Refresh");
	/** Displays formatted private messages in chronological order. */
	protected static ListView<String> listview_MessageThread = new ListView<String>();


	// ---- Area 2b: New message composer ----

	/** Explains whether the next outgoing message is linked to a Post or Reply. */
	protected static Label label_DraftContext = new Label();
	/** Shows a readable preview of the linked Post or Reply, if present. */
	protected static Label label_LinkPreview = new Label();
	/** Text entry area for the new outgoing message. */
	protected static TextArea textarea_NewMessage = new TextArea();
	/** Sends the composed message. */
	protected static Button button_Send = new Button("Send");
	/** Clears any Post/Reply linkage from the current draft. */
	protected static Button button_ClearLink = new Button("Clear Link");


	// ---- Area 3: Footer ----

	/** Horizontal rule separating content from footer. */
	protected static Line line_Separator4 = new Line(20, 525, width - 20, 525);
	/** Logs out the current user. */
	protected static Button button_Logout = new Button("Logout");
	/** Terminates the application. */
	protected static Button button_Quit   = new Button("Quit");


	// ---- Singleton state ----

	/** Singleton reference; null until displayMessageThread() is called for the first time. */
	private static ViewMessageThread theView;

	/** The JavaFX Stage for this page. */
	protected static Stage theStage;
	/** The root Pane that holds all widgets for this page. */
	protected static Pane  theRootPane;
	/** The currently logged-in User. */
	protected static User  theUser;
	/** The other participant in the displayed thread. */
	protected static String theOtherUser;

	/** The Post targeted by the next outgoing message, or -1 if none. */
	protected static int pendingPostId = -1;
	/** The Reply targeted by the next outgoing message, or -1 if none. */
	protected static int pendingReplyId = -1;

	/** The single Scene instance reused on every visit. */
	private static Scene theMessageThreadScene;

	/** Formatter used for message timestamps. */
	private static DateTimeFormatter dateFormatter =
			DateTimeFormatter.ofPattern("MMM d, yyyy  h:mm a");


	/*-*******************************************************************************************

	Constructor / Display Entry Point

	 */

	/**********
	 * <p> Method: displayMessageThread(Stage ps, User user, String otherUser) </p>
	 *
	 * <p> Description: The single external entry point for this page when no draft linkage
	 * should be preloaded. </p>
	 *
	 * @param ps         the primary JavaFX Stage
	 * @param user       the currently logged-in User
	 * @param otherUser  the other participant in the conversation
	 */
	public static void displayMessageThread(Stage ps, User user, String otherUser) {
		displayMessageThread(ps, user, otherUser, -1, -1);
	}

	/**********
	 * <p> Method: displayMessageThread(Stage ps, User user, String otherUser,
	 * int postId, int replyId) </p>
	 *
	 * <p> Description: The single external entry point for this page when a new outgoing
	 * message should be pre-linked to a specific Post or Reply. </p>
	 *
	 * @param ps         the primary JavaFX Stage
	 * @param user       the currently logged-in User
	 * @param otherUser  the other participant in the conversation
	 * @param postId     the Post to link to the next outgoing message, or -1 if none
	 * @param replyId    the Reply to link to the next outgoing message, or -1 if none
	 */
	public static void displayMessageThread(Stage ps, User user, String otherUser,
			int postId, int replyId) {

		theStage = ps;
		theUser  = user;
		theOtherUser = otherUser;

		pendingPostId = postId;
		pendingReplyId = replyId;

		if (theView == null) theView = new ViewMessageThread();
		theStage.setTitle("CSE 360 Foundations: Message Thread");
		theStage.setScene(theMessageThreadScene);

		label_UserDetails.setText("User: " + theUser.getUserName());
		label_ThreadTitle.setText("Conversation with " + theOtherUser);
		listview_MessageThread.setItems(FXCollections.observableArrayList());

		updateDraftContextLabels();
		loadThread();

		theStage.show();
	}


	/**********
	 * <p> Method: ViewMessageThread() — private constructor </p>
	 *
	 * <p> Description: Builds and configures all GUI widgets exactly once (singleton pattern).
	 * Static layout and event handlers are set here; dynamic data is handled in
	 * displayMessageThread(). </p>
	 */
	private ViewMessageThread() {

		theRootPane = new Pane();
		theMessageThreadScene = new Scene(theRootPane, width, height);


		// ============================ Area 1: Header ============================

		setupLabelUI(label_PageTitle,   "Arial", 24, width, Pos.CENTER,              0,  5);
		setupLabelUI(label_UserDetails, "Arial", 18, width - 220, Pos.BASELINE_LEFT, 20, 52);

		setupButtonUI(button_Return, "Dialog", 16, 170, Pos.CENTER, 608, 44);
		button_Return.setOnAction((_) -> { ControllerMessageThread.performReturn(); });


		// ============================ Area 2: Message thread ============================

		setupLabelUI(label_ThreadTitle, "Arial", 18, 500, Pos.BASELINE_LEFT, 20, 108);
		setupLabelUI(label_MessageCount, "Arial", 15, 250, Pos.BASELINE_LEFT, 20, 138);

		setupButtonUI(button_Refresh, "Dialog", 14, 120, Pos.CENTER, 660, 108);
		button_Refresh.setOnAction((_) -> {
			updateDraftContextLabels();
			loadThread();
		});

		listview_MessageThread.setStyle("-fx-font-family: 'Dialog'; -fx-font-size: 13px;");
		listview_MessageThread.setLayoutX(20);
		listview_MessageThread.setLayoutY(170);
		listview_MessageThread.setPrefWidth(760);
		listview_MessageThread.setPrefHeight(165);


		// ============================ Area 2b: Composer ============================

		setupLabelUI(label_DraftContext, "Arial", 13, 760, Pos.BASELINE_LEFT, 20, 345);
		label_DraftContext.setStyle("-fx-text-fill: #2c3e50;");

		setupLabelUI(label_LinkPreview, "Arial", 12, 760, Pos.BASELINE_LEFT, 20, 368);
		label_LinkPreview.setStyle("-fx-text-fill: #555555;");

		textarea_NewMessage.setLayoutX(20);
		textarea_NewMessage.setLayoutY(395);
		textarea_NewMessage.setPrefWidth(760);
		textarea_NewMessage.setPrefHeight(80);
		textarea_NewMessage.setWrapText(true);
		textarea_NewMessage.setFont(Font.font("Arial", 14));

		setupButtonUI(button_Send, "Dialog", 14, 120, Pos.CENTER, 660, 482);
		button_Send.setOnAction((_) -> {
			ControllerMessageThread.performSendMessage(textarea_NewMessage.getText());
		});

		setupButtonUI(button_ClearLink, "Dialog", 14, 120, Pos.CENTER, 525, 482);
		button_ClearLink.setOnAction((_) -> {
			pendingPostId = -1;
			pendingReplyId = -1;
			updateDraftContextLabels();
		});


		// ============================ Area 3: Footer ============================

		setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
		button_Logout.setOnAction((_) -> { ControllerMessageThread.performLogout(); });

		setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
		button_Quit.setOnAction((_) -> { ControllerMessageThread.performQuit(); });


		theRootPane.getChildren().addAll(
				label_PageTitle, label_UserDetails, button_Return, line_Separator1,
				label_ThreadTitle, label_MessageCount, button_Refresh, listview_MessageThread,
				label_DraftContext, label_LinkPreview, textarea_NewMessage, button_ClearLink, button_Send,
				line_Separator4, button_Logout, button_Quit);
	}


	/*-*******************************************************************************************

	Helper methods

	 */

	/**********
	 * <p> Method: loadThread() </p>
	 *
	 * <p> Description: Loads and formats all messages in the current thread, including any
	 * referenced Post or Reply context. </p>
	 */
	private static void loadThread() {

		List<PrivateMessage> messages =
				ControllerMessageThread.doLoadMessagesBetweenCurrentUserAndOtherUser();

		java.util.List<String> displayLines = new java.util.ArrayList<String>();

		for (PrivateMessage m : messages) {
			String senderLabel =
					m.senderUsername.equals(theUser.getUserName()) ? "You" : m.senderUsername;

			String timestampText = (m.timestamp == null)
					? "Unknown time"
					: m.timestamp.format(dateFormatter);

			StringBuilder line = new StringBuilder();

			String responseContext = buildResponseContext(m);
			if (!responseContext.isEmpty()) {
				line.append(responseContext).append("\n\n");
			}

			line.append(senderLabel)
			    .append("  •  ")
			    .append(timestampText)
			    .append("\n")
			    .append(m.isDeleted ? "[message deleted]" : m.content);

			displayLines.add(line.toString());
		}

		listview_MessageThread.setItems(FXCollections.observableArrayList(displayLines));
		label_MessageCount.setText("Messages: " + messages.size());
	}

	/**********
	 * <p> Method: buildResponseContext(PrivateMessage message) </p>
	 *
	 * <p> Description: Builds a human-readable context line describing the Post or Reply
	 * that the given message references, if any. </p>
	 *
	 * @param message the private message whose referenced content should be described
	 *
	 * @return the formatted context line, or an empty string if no valid reference exists
	 */
	private static String buildResponseContext(PrivateMessage message) {

		Reply referencedReply = ControllerMessageThread.doResolveReferencedReply(message);
		if (referencedReply != null) {
			String replyContent = referencedReply.isDeleted()
					? "[reply deleted]"
					: referencedReply.getContent();
			return "This message is in response to: " + replyContent
					+ " by " + referencedReply.getAuthorUsername();
		}

		Post referencedPost = ControllerMessageThread.doResolveReferencedPost(message);
		if (referencedPost != null) {
			String postContent = referencedPost.isDeleted()
					? "[post deleted]"
					: referencedPost.getContent();
			return "This message is in response to: " + postContent
					+ " by " + referencedPost.getAuthorUsername();
		}

		return "";
	}

	/**********
	 * <p> Method: updateDraftContextLabels() </p>
	 *
	 * <p> Description: Updates the labels that describe the Post or Reply that the next
	 * outgoing message will be linked to. </p>
	 */
	private static void updateDraftContextLabels() {

		if (pendingReplyId >= 0) {
			Reply reply = ControllerMessageThread.doResolveReferencedReply(
					new PrivateMessage("", "", "", pendingReplyId, -1));

			label_DraftContext.setText("Your next message will be linked to Reply #" + pendingReplyId);

			if (reply != null) {
				String content = reply.isDeleted() ? "[reply deleted]" : reply.getContent();
				label_LinkPreview.setText("Reply preview: " + content
						+ " by " + reply.getAuthorUsername());
			}
			else {
				label_LinkPreview.setText("Reply preview unavailable.");
			}
			return;
		}

		if (pendingPostId >= 0) {
			Post post = ControllerMessageThread.doResolveReferencedPost(
					new PrivateMessage("", "", "", -1, pendingPostId));

			label_DraftContext.setText("Your next message will be linked to Post #" + pendingPostId);

			if (post != null) {
				String content = post.isDeleted() ? "[post deleted]" : post.getContent();
				label_LinkPreview.setText("Post preview: " + content
						+ " by " + post.getAuthorUsername());
			}
			else {
				label_LinkPreview.setText("Post preview unavailable.");
			}
			return;
		}

		label_DraftContext.setText("Your next message is not linked to a specific post or reply.");
		label_LinkPreview.setText("");
	}

	private static void setupLabelUI(Label l, String ff, double f, double w,
			Pos p, double x, double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}

	private static void setupButtonUI(Button b, String ff, double f, double w,
			Pos p, double x, double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}
}