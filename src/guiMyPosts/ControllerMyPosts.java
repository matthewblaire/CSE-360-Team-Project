package guiMyPosts;

import java.util.ArrayList;
import java.util.List;

import database.Database;
import entityClasses.Post;
import entityClasses.User;
import guiPost.ViewPost;
import javafx.collections.FXCollections;
import javafx.stage.Stage;

/*******
 * <p> Title: ControllerMyPosts Class. </p>
 *
 * <p> Description: Controller for the My Posts page.  Provides protected static methods
 * invoked by {@link ViewMyPosts} button and event handlers.
 *
 * Responsibilities:
 * <ul>
 *   <li>Load the current student's posts with per-post reply count and unread reply count.</li>
 *   <li>Handle navigation (Return → Student Home, Logout, Quit).</li>
 * </ul>
 *
 * This controller is a collection of protected static methods — it is never instantiated.
 * All widget access goes through the public static fields of {@link ViewMyPosts}.
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author CSE 360 Team
 *
 * @version 1.00	2026-02-23	Initial version for Phase 2 — Student Discussion System
 */
public class ControllerMyPosts {

	/** postId for each item shown in listview_MyPosts; index-aligned. */
	static List<Integer> postIds = new ArrayList<>();

	/** Database reference. */
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**
	 * Default constructor — not used.
	 */
	public ControllerMyPosts() {
	}


	/**********
	 * <p> Method: doLoadMyPosts() </p>
	 *
	 * <p> Description: Queries the database for all non-deleted posts authored by the current
	 * user and populates {@link ViewMyPosts#listview_MyPosts}.  Each line shows the post ID,
	 * date, a content preview, total reply count, and unread reply count.  The parallel
	 * {@link #postIds} list is rebuilt so that index-to-postId mapping stays correct. </p>
	 */
	protected static void doLoadMyPosts() {

		String currentUser = ViewMyPosts.theUser.getUserName();
		List<Post> posts = theDatabase.getPostsByAuthor(currentUser);

		postIds.clear();
		List<String> displayLines = new ArrayList<>();

		for (Post p : posts) {
			postIds.add(p.getPostId());

			int replyCount  = theDatabase.getReplyCount(p.getPostId());
			int unreadCount = theDatabase.getUnreadReplyCount(p.getPostId(), currentUser);

			String date = p.getTimestamp().toLocalDate().toString();

			String preview = p.getContent().length() > 55
					? p.getContent().substring(0, 55) + "…"
					: p.getContent();

			// Show a "!" when there are unread replies to draw attention
			String unreadFlag = unreadCount > 0 ? " !" : "";
			String line = String.format("[ID: %d] %s | %s | Replies: %d | Unread: %d%s",
					p.getPostId(), date, preview, replyCount, unreadCount, unreadFlag);

			displayLines.add(line);
		}

		ViewMyPosts.listview_MyPosts.setItems(
				FXCollections.observableArrayList(displayLines));

		ViewMyPosts.label_PostCount.setText(
				"Your posts: " + posts.size());
	}


	
	public static void doHandlePostDoubleClick(Object selectedItem, Stage ps, User user) {
		int idx = ViewMyPosts.listview_MyPosts.getSelectionModel().getSelectedIndex();
		if (idx < 0 || idx >= postIds.size()) return;
		int postId = postIds.get(idx);
		ViewPost.displayPost(ps, user, postId);// displayPost(Stage ps, User user, Post post)
	}
	


	/**********
	 * <p> Method: performReturn() </p>
	 *
	 * <p> Description: Returns the student to the Student Home page. </p>
	 */
	protected static void performReturn() {
		staticHelpers.StaticHelpers.routeUserToHomeScreen(
				ViewMyPosts.theStage, ViewMyPosts.theUser);
	}


	/**********
	 * <p> Method: performLogout() </p>
	 *
	 * <p> Description: Logs out the current user and navigates to the login page. </p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewMyPosts.theStage);
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
