package guiSearchPosts;

import java.util.ArrayList;
import java.util.List;

import database.Database;
import entityClasses.DiscussionThread;
import entityClasses.Post;
import javafx.collections.FXCollections;

/*******
 * <p> Title: ControllerSearchPosts Class. </p>
 *
 * <p> Description: Controller for the Search Posts page.  Provides protected static methods
 * invoked by {@link ViewSearchPosts} button handlers.
 *
 * Responsibilities:
 * <ul>
 *   <li>Execute a keyword search (case-insensitive substring) over post content, with an
 *       optional thread filter, and populate the results ListView.</li>
 *   <li>Handle navigation (Return → Student Home, Logout, Quit).</li>
 * </ul>
 *
 * This controller is a collection of protected static methods — it is never instantiated.
 * All widget access goes through the public static fields of {@link ViewSearchPosts}. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author CSE 360 Team
 *
 * @version 1.00	2026-02-23	Initial version for Phase 2 — Student Discussion System
 */
public class ControllerSearchPosts {

	/** postId for each search result shown in listview_Results; index-aligned. */
	static List<Integer> resultPostIds = new ArrayList<>();

	/** Database reference. */
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**
	 * Default constructor — not used.
	 */
	public ControllerSearchPosts() {
	}


	/**********
	 * <p> Method: doSearch() </p>
	 *
	 * <p> Description: Reads the keyword from the TextField and the optional thread filter
	 * from the ComboBox, then calls {@link Database#searchPosts(String, int)}.  If the
	 * keyword is blank the results list is cleared and a hint is shown in the results label.
	 *
	 * Each result line shows the post ID, author, thread, date, content preview, and read
	 * status indicator. </p>
	 */
	protected static void doSearch() {

		String keyword = ViewSearchPosts.text_Keyword.getText().trim();
		if (keyword.isEmpty()) {
			ViewSearchPosts.label_ResultCount.setText("Enter a keyword to search.");
			ViewSearchPosts.listview_Results.setItems(
					FXCollections.observableArrayList());
			resultPostIds.clear();
			return;
		}

		// Determine thread filter: first item is "All Threads" (threadId = -1)
		DiscussionThread selectedThread =
				ViewSearchPosts.combobox_Thread.getSelectionModel().getSelectedItem();
		int threadId = (selectedThread == null) ? -1 : selectedThread.getThreadId();

		List<Post> results = theDatabase.searchPosts(keyword, threadId);

		resultPostIds.clear();
		List<String> displayLines = new ArrayList<>();
		String currentUser = ViewSearchPosts.theUser.getUserName();

		for (Post p : results) {
			resultPostIds.add(p.getPostId());

			boolean isRead = theDatabase.isPostRead(p.getPostId(), currentUser);
			String date    = p.getTimestamp().toLocalDate().toString();
			String preview = p.getContent().length() > 55
					? p.getContent().substring(0, 55) + "…"
					: p.getContent();

			String line = String.format("[ID: %d] @%s | Thread %d | %s | %s %s",
					p.getPostId(), p.getAuthorUsername(), p.getThreadId(),
					date, preview, isRead ? "✓" : "○");
			displayLines.add(line);
		}

		ViewSearchPosts.listview_Results.setItems(
				FXCollections.observableArrayList(displayLines));
		ViewSearchPosts.label_ResultCount.setText(
				"Results: " + results.size() + " post(s) matched \"" + keyword + "\"");
	}


	/**********
	 * <p> Method: performReturn() </p>
	 *
	 * <p> Description: Returns the student to the Student Home page. </p>
	 */
	protected static void performReturn() {
		guiStudent.ViewStudentHome.displayStudentHome(
				ViewSearchPosts.theStage, ViewSearchPosts.theUser);
	}


	/**********
	 * <p> Method: performLogout() </p>
	 *
	 * <p> Description: Logs out the current user and navigates to the login page. </p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewSearchPosts.theStage);
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
