package guiBrowsePosts;

import java.util.ArrayList;
import java.util.List;

import database.Database;
import entityClasses.DiscussionThread;
import entityClasses.Post;
import entityClasses.User;
import guiPost.ViewPost;
import javafx.collections.FXCollections;
import javafx.stage.Stage;

/*******
 * <p> Title: ControllerViewPosts Class. </p>
 *
 * <p> Description: Controller for the Browse Posts page.  Provides protected static methods
 * invoked by {@link ViewBrowsePosts} button and selection handlers.
 *
 * Responsibilities:
 * <ul>
 *   <li>Load and display the list of posts for a selected thread.</li>
 *   <li>When a post is selected, load its replies and mark the post and all its visible
 *       replies as read for the current user.</li>
 *   <li>Handle navigation (Return → Student Home, Logout, Quit).</li>
 * </ul>
 *
 * This controller is a collection of protected static methods — it is never instantiated.
 * All widget access goes through the public static fields of {@link ViewBrowsePosts}. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author CSE 360 Team
 *
 * @version 1.00	2026-02-23	Initial version for Phase 2 — Student Discussion System
 */
public class ControllerBrowsePosts {

	/*-*******************************************************************************************

	Package-visible parallel ID lists — allow the controller to map a selected list-view index
	back to the actual database primary key without parsing the display string.

	 */

	/** postId for each item currently shown in listview_Posts; index-aligned. */
	static List<Integer> postIds = new ArrayList<>();

	/** Post objects parallel to postIds; used to rebuild a single display row in-place. */
	static List<Post> currentPosts = new ArrayList<>();

	/** replyId for each item currently shown in listview_Replies; index-aligned. */
	static List<Integer> replyIds = new ArrayList<>();

	/** Database reference. */
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**
	 * Default constructor — not used.
	 */
	public ControllerBrowsePosts() {
	}


	/**********
	 * <p> Method: doLoadPosts() </p>
	 *
	 * <p> Description: Reads the selected DiscussionThread from the ComboBox, queries the
	 * database for all non-deleted Posts in that thread, and populates
	 * {@link ViewBrowsePosts#listview_Posts}.  The parallel {@link #postIds} list is rebuilt so
	 * that a subsequent doSelectPost() call can retrieve the correct postId by index.
	 *
	 * Any previously shown replies are cleared. </p>
	 */
	protected static void doLoadPosts() {

		DiscussionThread selected = ViewBrowsePosts.combobox_Thread.getSelectionModel()
				.getSelectedItem();
		if (selected == null) return;

		int threadId = selected.getThreadId();
		List<Post> posts = theDatabase.getPostsByThread(threadId);

		postIds.clear();
		currentPosts.clear();
		List<String> displayLines = new ArrayList<>();

		String currentUser = ViewBrowsePosts.theUser.getUserName();

		for (Post p : posts) {
			postIds.add(p.getPostId());
			currentPosts.add(p);

			int replyCount  = theDatabase.getReplyCount(p.getPostId());
			int unreadCount = theDatabase.getUnreadReplyCount(p.getPostId(), currentUser);
			boolean isRead  = theDatabase.isPostRead(p.getPostId(), currentUser);

			displayLines.add(buildPostDisplayLine(p, replyCount, unreadCount, isRead));
		}

		ViewBrowsePosts.listview_Posts.setItems(
				FXCollections.observableArrayList(displayLines));

		// Clear replies pane and parallel reply list
		replyIds.clear();

	}


	/**********
	 * <p> Method: doSelectPost() </p>
	 *
	 * <p> Description: Fires when the user clicks a row in the Posts ListView.  It:
	 * <ol>
	 *   <li>Retrieves the selected index and the corresponding postId.</li>
	 *   <li>Marks the post as read for the current user.</li>
	 *   <li>Loads all replies for that post from the database.</li>
	 *   <li>Marks every visible reply as read for the current user.</li>
	 *   <li>Populates {@link ViewBrowsePosts#listview_Replies} with formatted Strings.</li>
	 *   <li>Refreshes the posts list so the read indicator (✓/○) updates immediately.</li>
	 * </ol>
	 * </p>
	 */
	protected static void doSelectPost() {

		int idx = ViewBrowsePosts.listview_Posts.getSelectionModel().getSelectedIndex();
		if (idx < 0 || idx >= postIds.size()) return;

		int postId = postIds.get(idx);
		String currentUser = ViewBrowsePosts.theUser.getUserName();

		// Mark post as read
		theDatabase.markPostAsRead(postId, currentUser);


		// Update just the one row so the unread indicator flips to read immediately,
		// without calling doLoadPosts() (which would clear the replies we just set).
		if (idx < currentPosts.size()) {
			Post p = currentPosts.get(idx);
			int replyCount  = theDatabase.getReplyCount(postId);
			int unreadCount = theDatabase.getUnreadReplyCount(postId, currentUser); // 0 now
			ViewBrowsePosts.listview_Posts.getItems().set(
					idx, buildPostDisplayLine(p, replyCount, unreadCount, true));
		}
	}


	protected static void doSearch() {
		String keyword = ViewBrowsePosts.text_Keyword.getText().trim();
		if (keyword.isEmpty()) {
			doLoadPosts();
			return;
		}
		DiscussionThread selected = ViewBrowsePosts.combobox_Thread.getSelectionModel().getSelectedItem();
		int threadId = (selected == null) ? -1 : selected.getThreadId();
		List<Post> results = theDatabase.searchPosts(keyword, threadId);

		postIds.clear();
		currentPosts.clear();
		List<String> displayLines = new ArrayList<>();
		String currentUser = ViewBrowsePosts.theUser.getUserName();

		for (Post p : results) {
			postIds.add(p.getPostId());
			currentPosts.add(p);
			int replyCount  = theDatabase.getReplyCount(p.getPostId());
			int unreadCount = theDatabase.getUnreadReplyCount(p.getPostId(), currentUser);
			boolean isRead  = theDatabase.isPostRead(p.getPostId(), currentUser);
			displayLines.add(buildPostDisplayLine(p, replyCount, unreadCount, isRead));
		}
		ViewBrowsePosts.listview_Posts.setItems(FXCollections.observableArrayList(displayLines));
		replyIds.clear();
	}


	/**
	 * Builds the encoded display string for a post row.
	 * Format: {@code "READ\t"} or {@code "UNREAD\t"} prefix, then human-readable text.
	 * The cell factory in ViewViewPosts strips the prefix and applies visual styling.
	 */
	private static String buildPostDisplayLine(Post p, int replyCount, int unreadCount, boolean isRead) {
		String prefix;
		if (!isRead) {
			prefix = "UNREAD\t";
		} else if (unreadCount > 0) {
			prefix = "NEW_REPLIES\t";
		} else {
			prefix = "READ\t";
		}
		String replyLabel = replyCount == 1 ? "1 reply" : replyCount + " replies";
		String replySuffix = unreadCount > 0 ? replyLabel + "  (" + unreadCount + " new)" : replyLabel;
		if (p.isDeleted()) {
			return prefix + "[Deleted post]  ·  @" + p.getAuthorUsername() + "  ·  " + replySuffix + "\n[This post has been deleted]";
		}
		String content = p.getContent() == null ? "" : p.getContent();
		String snippet = content.length() > 100 ? content.substring(0, 100).replace('\n', ' ') + "…" : content.replace('\n', ' ');
		return prefix + "@" + p.getAuthorUsername() + "  ·  " + replySuffix + "\n" + snippet;
	}


	public static void doHandlePostDoubleClick(Object selectedItem, Stage ps, User user) {
		int idx = ViewBrowsePosts.listview_Posts.getSelectionModel().getSelectedIndex();
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
		if (ViewBrowsePosts.theUser.getNumRoles() > 1)
		{
			guiMultipleRoleDispatch.ViewMultipleRoleDispatch.
			displayMultipleRoleDispatch(ViewBrowsePosts.theStage, ViewBrowsePosts.theUser);
		} else {
			
			// Admin role
			if (ViewBrowsePosts.theUser.getAdminRole()) {
				guiAdminHome.ViewAdminHome.displayAdminHome(ViewBrowsePosts.theStage, ViewBrowsePosts.theUser);
			} else if (ViewBrowsePosts.theUser.getNewStudentRole()) {
				guiStudent.ViewStudentHome.displayStudentHome(ViewBrowsePosts.theStage, ViewBrowsePosts.theUser);
			} else if (ViewBrowsePosts.theUser.getNewStaffRole()) {
				guiStaff.ViewStaffHome.displayStaffHome(ViewBrowsePosts.theStage, ViewBrowsePosts.theUser);
								// Other roles
			} else {
				System.out.println("***** BrowsePosts goToUserHome request has an invalid role");
			}
			
		}
		
	}


	/**********
	 * <p> Method: performLogout() </p>
	 *
	 * <p> Description: Logs out the current user and navigates to the login page. </p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewBrowsePosts.theStage);
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
