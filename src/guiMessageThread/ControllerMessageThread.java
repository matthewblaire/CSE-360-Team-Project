package guiMessageThread;

import java.util.List;

import applicationMain.FoundationsMain;
import database.Database;
import entityClasses.Post;
import entityClasses.PrivateMessage;
import entityClasses.Reply;

/*******
 * <p> Title: ControllerMessageThread Class. </p>
 *
 * <p> Description: Controller for the Message Thread page. Provides protected static methods
 * invoked by {@link ViewMessageThread} button and event handlers. </p>
 *
 * @author CSE 360 Team
 *
 */
public class ControllerMessageThread {

	/** Database reference. */
	private static Database theDatabase = FoundationsMain.database;

	/**
	 * Default constructor — not used.
	 */
	public ControllerMessageThread() {
	}

	/**********
	 * <p> Method: doLoadMessagesBetweenCurrentUserAndOtherUser() </p>
	 *
	 * <p> Description: Queries the database for all private messages between the currently
	 * logged-in user and the selected other user, ordered by timestamp ascending. </p>
	 *
	 * @return the ordered list of private messages in the thread
	 */
	protected static List<PrivateMessage> doLoadMessagesBetweenCurrentUserAndOtherUser() {
		String currentUser = ViewMessageThread.theUser.getUserName();
		String otherUser = ViewMessageThread.theOtherUser;
		return theDatabase.getMessagesBetween(currentUser, otherUser);
	}

	/**********
	 * <p> Method: doResolveReferencedPost(PrivateMessage message) </p>
	 *
	 * <p> Description: Resolves and returns the Post referenced by the given message, if any.
	 * If the message does not reference a valid Post, null is returned. </p>
	 *
	 * @param message the private message whose referenced Post should be resolved
	 *
	 * @return the referenced Post, or null if none exists
	 */
	protected static Post doResolveReferencedPost(PrivateMessage message) {
		if (message == null || message.postId < 0) return null;
		return theDatabase.getPostById(message.postId);
	}

	/**********
	 * <p> Method: doResolveReferencedReply(PrivateMessage message) </p>
	 *
	 * <p> Description: Resolves and returns the Reply referenced by the given message, if any.
	 * If the message does not reference a valid Reply, null is returned. </p>
	 *
	 * @param message the private message whose referenced Reply should be resolved
	 *
	 * @return the referenced Reply, or null if none exists
	 */
	protected static Reply doResolveReferencedReply(PrivateMessage message) {
		if (message == null || message.replyId < 0) return null;
		return theDatabase.getReplyById(message.replyId);
	}

	/**********
	 * <p> Method: performSendMessage(String content) </p>
	 *
	 * <p> Description: Creates and stores a new private message in the current thread.
	 * If a Post or Reply is currently selected as draft context, that linkage is written
	 * into the new message. </p>
	 *
	 * @param content the body text of the message to send
	 */
	protected static void performSendMessage(String content) {
		String trimmed = content == null ? "" : content.trim();
		if (trimmed.isEmpty()) return;

		try {
			PrivateMessage message = new PrivateMessage(
					ViewMessageThread.theUser.getUserName(),
					ViewMessageThread.theOtherUser,
					trimmed,
					ViewMessageThread.pendingReplyId,
					ViewMessageThread.pendingPostId);

			theDatabase.createMessage(message);

			ViewMessageThread.pendingPostId = -1;
			ViewMessageThread.pendingReplyId = -1;
			ViewMessageThread.textarea_NewMessage.clear();

			ViewMessageThread.displayMessageThread(
					ViewMessageThread.theStage,
					ViewMessageThread.theUser,
					ViewMessageThread.theOtherUser);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**********
	 * <p> Method: performReturn() </p>
	 *
	 * <p> Description: Returns the user to the Messages List page. </p>
	 */
	protected static void performReturn() {
		guiMessagesList.ViewMessagesList.displayMessagesList(
				ViewMessageThread.theStage, ViewMessageThread.theUser);
	}

	/**********
	 * <p> Method: performLogout() </p>
	 *
	 * <p> Description: Logs out the current user and navigates to the login page. </p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewMessageThread.theStage);
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