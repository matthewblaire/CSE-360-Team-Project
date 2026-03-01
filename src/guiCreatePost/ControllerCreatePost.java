package guiCreatePost;

import java.sql.SQLException;
import java.time.LocalDateTime;

import database.Database;
import entityClasses.DiscussionThread;
import entityClasses.Post;
import entityClasses.Reply;
import recognizers.PostContentRecognizer;

/*******
 * <p> Title: ControllerCreatePost Class </p>
 *
 * <p> Description: The Controller component of the Create Post / Reply page.  This class is a
 * collection of protected static methods invoked by button and text-change handlers defined in
 * {@link ViewCreatePost}.  It is never instantiated.
 *
 * Responsibilities:
 * <ul>
 *   <li>doCreatePost()  — validate and persist a new Post to the database.</li>
 *   <li>doCreateReply() — validate the target postId and content, then persist a new Reply.</li>
 *   <li>doUpdateCharCount() — keep the live character counter in sync with the TextArea.</li>
 *   <li>doSelectThread() — handle ComboBox selection changes (reserved for future use).</li>
 *   <li>performReturn() / performLogout() / performQuit() — standard navigation actions.</li>
 * </ul>
 *
 * All validation is delegated to {@link PostContentRecognizer} so that the same rules are
 * exercised by both the GUI and the automated test suite. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author CSE 360 Team
 *
 * @version 1.00	2026-02-23	Initial version for Phase 2 — Student Discussion System
 */

public class ControllerCreatePost {

	/*-*******************************************************************************************

	The User Interface Actions for this page

	This controller is not a class that gets instantiated.  Rather, it is a collection of
	protected static methods that can be called by the View (which is a singleton) and the Model.

	*/

	/** Default constructor — not used. */
	public ControllerCreatePost() { }

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;


	/**********
	 * <p> Method: doCreatePost() </p>
	 *
	 * <p> Description: Called when the user clicks the [Submit Post] button.  The method:
	 * <ol>
	 *   <li>Reads the content from the TextArea.</li>
	 *   <li>Validates the content using PostContentRecognizer; shows an inline error if
	 *       invalid.</li>
	 *   <li>Reads the selected thread from the ComboBox; shows an error if none selected.</li>
	 *   <li>Builds a Post entity with the current user, content, thread, and timestamp.</li>
	 *   <li>Calls {@code Database.createPost()} to persist the row.</li>
	 *   <li>Shows a success alert that includes the generated postId.</li>
	 *   <li>Clears the post content TextArea so the form is ready for a new post.</li>
	 * </ol>
	 * </p>
	 */
	protected static void doCreatePost() {

		// Step 1: read the content the student has typed
		String title = ViewCreatePost.textField_Title.getText();
		String content = ViewCreatePost.textarea_PostContent.getText();

		// Step 2: validate the content using the recognizer
		String contentError = PostContentRecognizer.evaluatePostContent(content);
		if (!contentError.isEmpty()) {
			ViewCreatePost.label_ErrorFeedback.setText(contentError);
			return;
		}

		// Step 3: make sure the student has actually selected a thread
		DiscussionThread selectedThread = ViewCreatePost.combobox_Thread.getValue();
		if (selectedThread == null) {
			ViewCreatePost.label_ErrorFeedback.setText(
					"Please select a thread before submitting your post.");
			return;
		}

		// Step 4: build the Post entity — postId is 0 here and will be set by createPost()
		Post post = new Post(
				selectedThread.getThreadId(),
				ViewCreatePost.theUser.getUserName(),
				title,
				content,
				LocalDateTime.now());

		// Step 5: persist to database
		try {
			int generatedPostId = theDatabase.createPost(post);

			// Step 6: show success message with the generated ID
			ViewCreatePost.alertSuccess.setTitle("Post Created");
			ViewCreatePost.alertSuccess.setHeaderText("Your post was submitted successfully.");
			ViewCreatePost.alertSuccess.setContentText(
					"Post ID: " + generatedPostId
					+ "\nThread: "  + selectedThread.getTitle()
					+ "\nYou can share this ID with others so they can reply to it.");
			ViewCreatePost.alertSuccess.showAndWait();

			// Step 7: clear the post form, reset error feedback
			ViewCreatePost.textarea_PostContent.clear();
			ViewCreatePost.label_CharCount.setText("0 / " + PostContentRecognizer.MAX_CONTENT_LENGTH);
			ViewCreatePost.label_ErrorFeedback.setText("");

		} catch (SQLException e) {
			ViewCreatePost.label_ErrorFeedback.setText(
					"Database error while creating post: " + e.getMessage());
			e.printStackTrace();
		}
	}


	/**********
	 * <p> Method: doCreateReply() </p>
	 *
	 * <p> Description: Called when the user clicks the [Submit Reply] button.  The method:
	 * <ol>
	 *   <li>Reads and validates the Post ID field — must be a positive integer.</li>
	 *   <li>Checks that the post with that ID exists in the database.</li>
	 *   <li>Reads the reply content from the TextArea.</li>
	 *   <li>Validates the content using PostContentRecognizer.</li>
	 *   <li>Builds a Reply entity and calls {@code Database.createReply()} to persist it.</li>
	 *   <li>Shows a success alert with the generated replyId.</li>
	 *   <li>Clears the reply fields so the form is ready for another reply.</li>
	 * </ol>
	 * </p>
	 */
	protected static void doCreateReply() {

		// Step 1: read and parse the Post ID
		String postIdText = ViewCreatePost.text_PostId.getText().trim();
		int postId;
		try {
			postId = Integer.parseInt(postIdText);
			if (postId <= 0) throw new NumberFormatException("postId must be positive");
		} catch (NumberFormatException e) {
			ViewCreatePost.label_ErrorFeedback.setText(
					"Post ID must be a positive integer (e.g., 1). "
					+ "You can find the Post ID in the success message shown after posting.");
			return;
		}

		// Step 2: verify the referenced post exists
		if (!theDatabase.doesPostExist(postId)) {
			ViewCreatePost.label_ErrorFeedback.setText(
					"No post found with Post ID " + postId
					+ ". Please check the ID and try again.");
			return;
		}

		// Step 3: read the reply content
		String replyContent = ViewCreatePost.textarea_ReplyContent.getText();

		// Step 4: validate the reply content
		String contentError = PostContentRecognizer.evaluatePostContent(replyContent);
		if (!contentError.isEmpty()) {
			ViewCreatePost.label_ErrorFeedback.setText(contentError);
			return;
		}

		// Step 5: build the Reply entity
		Reply reply = new Reply(
				postId,
				ViewCreatePost.theUser.getUserName(),
				replyContent,
				LocalDateTime.now());

		// Step 6: persist to database
		try {
			int generatedReplyId = theDatabase.createReply(reply);

			// Step 7: show success message
			ViewCreatePost.alertSuccess.setTitle("Reply Submitted");
			ViewCreatePost.alertSuccess.setHeaderText("Your reply was submitted successfully.");
			ViewCreatePost.alertSuccess.setContentText(
					"Reply ID: " + generatedReplyId
					+ "\nIn reply to Post ID: " + postId);
			ViewCreatePost.alertSuccess.showAndWait();

			// Step 8: clear the reply form
			ViewCreatePost.textarea_ReplyContent.clear();
			ViewCreatePost.text_PostId.clear();
			ViewCreatePost.label_ErrorFeedback.setText("");

		} catch (SQLException e) {
			ViewCreatePost.label_ErrorFeedback.setText(
					"Database error while creating reply: " + e.getMessage());
			e.printStackTrace();
		}
	}


	/**********
	 * <p> Method: doUpdateCharCount(String newText) </p>
	 *
	 * <p> Description: Called on every keystroke in the post-content TextArea to update the live
	 * character counter label.  The counter turns red when the content exceeds the maximum
	 * allowed length to give the student an early visual warning. </p>
	 *
	 * @param newText  the current full text of the post-content TextArea
	 */
	protected static void doUpdateCharCount(String newText) {
		int length = (newText == null) ? 0 : newText.length();
		ViewCreatePost.label_CharCount.setText(
				length + " / " + PostContentRecognizer.MAX_CONTENT_LENGTH);

		// Red warning when over the limit; black when within the limit
		if (length > PostContentRecognizer.MAX_CONTENT_LENGTH)
			ViewCreatePost.label_CharCount.setStyle("-fx-text-fill: red;");
		else
			ViewCreatePost.label_CharCount.setStyle("-fx-text-fill: black;");
	}


	/**********
	 * <p> Method: doSelectThread() </p>
	 *
	 * <p> Description: Called when the thread ComboBox selection changes.  Currently clears any
	 * stale error message so the student gets a clean state when picking a different thread.
	 * Reserved for further logic in future phases (e.g., showing a thread description). </p>
	 */
	protected static void doSelectThread() {
		ViewCreatePost.label_ErrorFeedback.setText("");
	}


	/**********
	 * <p> Method: performReturn() </p>
	 *
	 * <p> Description: Returns the student to the Student Home page without saving any
	 * unsaved content. </p>
	 */
	protected static void performReturn() {
		guiStudent.ViewStudentHome.displayStudentHome(
				ViewCreatePost.theStage, ViewCreatePost.theUser);
	}


	/**********
	 * <p> Method: performLogout() </p>
	 *
	 * <p> Description: Logs out the current user and navigates to the standard login page. </p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewCreatePost.theStage);
	}


	/**********
	 * <p> Method: performQuit() </p>
	 *
	 * <p> Description: Terminates the application.  All data was already persisted to the
	 * database so no cleanup is required. </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}
