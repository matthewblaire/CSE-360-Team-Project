package guiEditPost;

import java.util.Optional;

import database.Database;
import entityClasses.Post;
import entityClasses.Reply;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import recognizers.PostContentRecognizer;

/*******
 * <p> Title: ControllerEditPost Class. </p>
 *
 * <p> Description: Controller for the Edit Post / Reply page.  Provides protected static
 * methods invoked by {@link ViewEditPost} button and event handlers.
 *
 * Responsibilities:
 * <ul>
 *   <li>Load a post by ID, verifying the current user is the author, and pre-fill the edit
 *       TextArea.</li>
 *   <li>Save the edited post content back to the database after validation.</li>
 *   <li>Soft-delete the loaded post after confirmation, then reset the form.</li>
 *   <li>Load a reply by ID (author check) and pre-fill the reply TextArea.</li>
 *   <li>Save the edited reply content back to the database after validation.</li>
 *   <li>Soft-delete the loaded reply after confirmation, then reset the reply form.</li>
 *   <li>Track the live character count for the post content TextArea.</li>
 *   <li>Handle navigation (Return → My Posts, Logout, Quit).</li>
 * </ul>
 *
 * This controller is a collection of protected static methods — it is never instantiated.
 * All widget access goes through the public static fields of {@link ViewEditPost}. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author CSE 360 Team
 *
 * @version 1.00	2026-02-23	Initial version for Phase 2 — Student Discussion System
 */
public class ControllerEditPost {

	/** Database reference. */
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**
	 * Default constructor — not used.
	 */
	public ControllerEditPost() {
	}


	/**********
	 * <p> Method: doLoadPost() </p>
	 *
	 * <p> Description: Reads the Post ID from {@link ViewEditPost#text_PostId}, looks up the
	 * post in the database, verifies the current user is the author, and pre-fills
	 * {@link ViewEditPost#textarea_PostContent} with the current content.
	 *
	 * On any error (non-integer ID, post not found, wrong author, post deleted) an error
	 * message is shown in {@link ViewEditPost#label_PostFeedback} and the TextArea is
	 * cleared. </p>
	 */
	protected static void doLoadPost() {

		ViewEditPost.label_PostFeedback.setStyle("-fx-text-fill: red;");
		ViewEditPost.textarea_PostContent.clear();
		ViewEditPost.label_PostCharCount.setText("0 / 2000");
		ViewEditPost.button_SavePost.setDisable(true);
		ViewEditPost.button_DeletePost.setDisable(true);

		// Parse the postId field
		int postId;
		try {
			postId = Integer.parseInt(ViewEditPost.text_PostId.getText().trim());
			if (postId <= 0) throw new NumberFormatException();
		} catch (NumberFormatException e) {
			ViewEditPost.label_PostFeedback.setText("Post ID must be a positive integer.");
			return;
		}

		// Fetch the post from the database
		Post post = theDatabase.getPostById(postId);
		if (post == null) {
			ViewEditPost.label_PostFeedback.setText("Post ID " + postId + " does not exist.");
			return;
		}
		if (post.isDeleted()) {
			ViewEditPost.label_PostFeedback.setText(
					"Post ID " + postId + " has already been deleted.");
			return;
		}

		// Author check — students may only edit their own posts
		String currentUser = ViewEditPost.theUser.getUserName();
		if (!post.getAuthorUsername().equals(currentUser)) {
			ViewEditPost.label_PostFeedback.setText(
					"You can only edit posts that you authored.");
			return;
		}

		// Pre-fill the TextArea and enable action buttons
		ViewEditPost.textarea_PostContent.setText(post.getContent());
		doUpdatePostCharCount(post.getContent());
		ViewEditPost.label_PostFeedback.setStyle("-fx-text-fill: green;");
		ViewEditPost.label_PostFeedback.setText("Post loaded. Make your changes and click Save.");
		ViewEditPost.button_SavePost.setDisable(false);
		ViewEditPost.button_DeletePost.setDisable(false);
	}


	/**********
	 * <p> Method: doSavePost() </p>
	 *
	 * <p> Description: Validates the edited content, calls
	 * {@link Database#updatePost(int, String, String)}, and shows a success or error message.
	 * Validation uses the same {@link PostContentRecognizer} as the Create page so the same
	 * rules apply (non-empty, ≤ 2000 chars). </p>
	 */
	protected static void doSavePost() {

		String content = ViewEditPost.textarea_PostContent.getText();
		String error   = PostContentRecognizer.evaluatePostContent(content);
		if (!error.isEmpty()) {
			ViewEditPost.label_PostFeedback.setStyle("-fx-text-fill: red;");
			ViewEditPost.label_PostFeedback.setText(error);
			return;
		}

		int postId;
		try {
			postId = Integer.parseInt(ViewEditPost.text_PostId.getText().trim());
		} catch (NumberFormatException e) {
			ViewEditPost.label_PostFeedback.setStyle("-fx-text-fill: red;");
			ViewEditPost.label_PostFeedback.setText("Invalid Post ID.");
			return;
		}

		String currentUser = ViewEditPost.theUser.getUserName();
		int rows = theDatabase.updatePost(postId, content, currentUser);

		if (rows == 1) {
			ViewEditPost.label_PostFeedback.setStyle("-fx-text-fill: green;");
			ViewEditPost.label_PostFeedback.setText(
					"Post ID " + postId + " updated successfully.");
		} else {
			ViewEditPost.label_PostFeedback.setStyle("-fx-text-fill: red;");
			ViewEditPost.label_PostFeedback.setText(
					"Update failed — post not found, already deleted, or not authored by you.");
		}
	}


	/**********
	 * <p> Method: doDeletePost() </p>
	 *
	 * <p> Description: Shows a confirmation dialog, then soft-deletes the loaded post by
	 * calling {@link Database#softDeletePost(int, String)}.  On success the form is reset so
	 * the student cannot attempt to edit a deleted post. </p>
	 */
	protected static void doDeletePost() {

		int postId;
		try {
			postId = Integer.parseInt(ViewEditPost.text_PostId.getText().trim());
		} catch (NumberFormatException e) {
			ViewEditPost.label_PostFeedback.setStyle("-fx-text-fill: red;");
			ViewEditPost.label_PostFeedback.setText("Invalid Post ID.");
			return;
		}

		// Ask for confirmation before soft-deleting
		Alert confirm = new Alert(AlertType.CONFIRMATION);
		confirm.setTitle("Delete Post");
		confirm.setHeaderText("Delete Post ID " + postId + "?");
		confirm.setContentText("This will hide the post from all students. "
				+ "Existing replies will remain. This action cannot be undone.");
		Optional<ButtonType> result = confirm.showAndWait();
		if (!result.isPresent() || result.get() != ButtonType.OK) return;

		String currentUser = ViewEditPost.theUser.getUserName();
		int rows = theDatabase.softDeletePost(postId, currentUser);

		if (rows == 1) {
			// Reset the form so the deleted post cannot be edited again
			ViewEditPost.text_PostId.clear();
			ViewEditPost.textarea_PostContent.clear();
			ViewEditPost.label_PostCharCount.setText("0 / 2000");
			ViewEditPost.button_SavePost.setDisable(true);
			ViewEditPost.button_DeletePost.setDisable(true);
			ViewEditPost.label_PostFeedback.setStyle("-fx-text-fill: green;");
			ViewEditPost.label_PostFeedback.setText(
					"Post ID " + postId + " has been deleted.");
		} else {
			ViewEditPost.label_PostFeedback.setStyle("-fx-text-fill: red;");
			ViewEditPost.label_PostFeedback.setText(
					"Delete failed — post not found or not authored by you.");
		}
	}


	/**********
	 * <p> Method: doUpdatePostCharCount(String newText) </p>
	 *
	 * <p> Description: Updates the character-count label beneath the post TextArea on every
	 * keystroke.  The label turns red when the 2000-character limit is exceeded, matching the
	 * behavior on the Create Post page. </p>
	 *
	 * @param newText  the current content of the post TextArea
	 */
	protected static void doUpdatePostCharCount(String newText) {
		int len = (newText == null) ? 0 : newText.length();
		ViewEditPost.label_PostCharCount.setText(len + " / 2000");
		ViewEditPost.label_PostCharCount.setStyle(
				len > PostContentRecognizer.MAX_CONTENT_LENGTH
						? "-fx-text-fill: red;" : "-fx-text-fill: black;");
	}


	/**********
	 * <p> Method: doLoadReply() </p>
	 *
	 * <p> Description: Reads the Reply ID from {@link ViewEditPost#text_ReplyId}, fetches the
	 * reply, verifies the current user is the author, and pre-fills
	 * {@link ViewEditPost#textarea_ReplyContent}.  Error messages appear in
	 * {@link ViewEditPost#label_ReplyFeedback}. </p>
	 */
	protected static void doLoadReply() {

		ViewEditPost.label_ReplyFeedback.setStyle("-fx-text-fill: red;");
		ViewEditPost.textarea_ReplyContent.clear();
		ViewEditPost.button_SaveReply.setDisable(true);
		ViewEditPost.button_DeleteReply.setDisable(true);

		int replyId;
		try {
			replyId = Integer.parseInt(ViewEditPost.text_ReplyId.getText().trim());
			if (replyId <= 0) throw new NumberFormatException();
		} catch (NumberFormatException e) {
			ViewEditPost.label_ReplyFeedback.setText("Reply ID must be a positive integer.");
			return;
		}

		Reply reply = theDatabase.getReplyById(replyId);
		if (reply == null) {
			ViewEditPost.label_ReplyFeedback.setText(
					"Reply ID " + replyId + " does not exist.");
			return;
		}
		if (reply.isDeleted()) {
			ViewEditPost.label_ReplyFeedback.setText(
					"Reply ID " + replyId + " has already been deleted.");
			return;
		}

		String currentUser = ViewEditPost.theUser.getUserName();
		if (!reply.getAuthorUsername().equals(currentUser)) {
			ViewEditPost.label_ReplyFeedback.setText(
					"You can only edit replies that you authored.");
			return;
		}

		ViewEditPost.textarea_ReplyContent.setText(reply.getContent());
		ViewEditPost.label_ReplyFeedback.setStyle("-fx-text-fill: green;");
		ViewEditPost.label_ReplyFeedback.setText(
				"Reply loaded. Make your changes and click Save or Delete.");
		ViewEditPost.button_SaveReply.setDisable(false);
		ViewEditPost.button_DeleteReply.setDisable(false);
	}


	/**********
	 * <p> Method: doSaveReply() </p>
	 *
	 * <p> Description: Validates the edited content, calls
	 * {@link Database#updateReply(int, String, String)}, and shows a success or error
	 * message. </p>
	 */
	protected static void doSaveReply() {

		String content = ViewEditPost.textarea_ReplyContent.getText();
		String error   = PostContentRecognizer.evaluatePostContent(content);
		if (!error.isEmpty()) {
			ViewEditPost.label_ReplyFeedback.setStyle("-fx-text-fill: red;");
			ViewEditPost.label_ReplyFeedback.setText(error);
			return;
		}

		int replyId;
		try {
			replyId = Integer.parseInt(ViewEditPost.text_ReplyId.getText().trim());
		} catch (NumberFormatException e) {
			ViewEditPost.label_ReplyFeedback.setStyle("-fx-text-fill: red;");
			ViewEditPost.label_ReplyFeedback.setText("Invalid Reply ID.");
			return;
		}

		String currentUser = ViewEditPost.theUser.getUserName();
		int rows = theDatabase.updateReply(replyId, content, currentUser);

		if (rows == 1) {
			ViewEditPost.label_ReplyFeedback.setStyle("-fx-text-fill: green;");
			ViewEditPost.label_ReplyFeedback.setText(
					"Reply ID " + replyId + " updated successfully.");
		} else {
			ViewEditPost.label_ReplyFeedback.setStyle("-fx-text-fill: red;");
			ViewEditPost.label_ReplyFeedback.setText(
					"Update failed — reply not found or not authored by you.");
		}
	}


	/**********
	 * <p> Method: doDeleteReply() </p>
	 *
	 * <p> Description: Shows a confirmation dialog, then soft-deletes the loaded reply by
	 * calling {@link Database#softDeleteReply(int, String)}.  On success the reply form is
	 * reset so the student cannot attempt to edit a deleted reply. </p>
	 */
	protected static void doDeleteReply() {

		int replyId;
		try {
			replyId = Integer.parseInt(ViewEditPost.text_ReplyId.getText().trim());
		} catch (NumberFormatException e) {
			ViewEditPost.label_ReplyFeedback.setStyle("-fx-text-fill: red;");
			ViewEditPost.label_ReplyFeedback.setText("Invalid Reply ID.");
			return;
		}

		// Ask for confirmation before soft-deleting
		Alert confirm = new Alert(AlertType.CONFIRMATION);
		confirm.setTitle("Delete Reply");
		confirm.setHeaderText("Delete Reply ID " + replyId + "?");
		confirm.setContentText("This will hide the reply from all students. "
				+ "This action cannot be undone.");
		Optional<ButtonType> result = confirm.showAndWait();
		if (!result.isPresent() || result.get() != ButtonType.OK) return;

		String currentUser = ViewEditPost.theUser.getUserName();
		int rows = theDatabase.softDeleteReply(replyId, currentUser);

		if (rows == 1) {
			// Reset the reply form so the deleted reply cannot be edited again
			ViewEditPost.text_ReplyId.clear();
			ViewEditPost.textarea_ReplyContent.clear();
			ViewEditPost.button_SaveReply.setDisable(true);
			ViewEditPost.button_DeleteReply.setDisable(true);
			ViewEditPost.label_ReplyFeedback.setStyle("-fx-text-fill: green;");
			ViewEditPost.label_ReplyFeedback.setText(
					"Reply ID " + replyId + " has been deleted.");
		} else {
			ViewEditPost.label_ReplyFeedback.setStyle("-fx-text-fill: red;");
			ViewEditPost.label_ReplyFeedback.setText(
					"Delete failed — reply not found or not authored by you.");
		}
	}


	/**********
	 * <p> Method: performReturn() </p>
	 *
	 * <p> Description: Returns the student to the My Posts page. </p>
	 */
	protected static void performReturn() {
		guiMyPosts.ViewMyPosts.displayMyPosts(
				ViewEditPost.theStage, ViewEditPost.theUser);
	}


	/**********
	 * <p> Method: performLogout() </p>
	 *
	 * <p> Description: Logs out the current user and navigates to the login page. </p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewEditPost.theStage);
	}


	/**********
	 * <p> Method: performQuit() </p>
	 *
	 * <p> Description: Terminates the application. </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}
