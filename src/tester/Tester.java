package tester;

import java.sql.SQLException;
import java.time.LocalDateTime;

import java.util.List;

import database.Database;
import entityClasses.Post;
import entityClasses.Reply;
import recognizers.EmailAddressRecognizer;
import recognizers.InviteCodeRecognizer;
import recognizers.NameRecognizer;
import recognizers.PasswordRecognizer;
import recognizers.PostContentRecognizer;
import recognizers.UserNameRecognizer;

/**
 * Title: Tester class
 * 
 * Description: This class contains methods for testing several functionalities of the application including, but not limited to,
 * input validation.
 */
public class Tester {
	
	
	/**
	 * Default constructor is not used
	 */
	public Tester() {
		
	}
	
	/** Number of passed tests in the current category. */
	public static int numPassed = 0;
	/** Number of failed tests in the current category. */
	public static int numFailed = 0;

	/** Total number of passed tests across all categories. */
	public static int totalPassed = 0;
	/** Total number of failed tests across all categories. */
	public static int totalFailed = 0;
	
	
	/**
	 * Method: resetCurrentStats()
	 * 
	 * Description: This method resets numPassed and numFailed to zero after adding their values to totalPassed and 
	 * totalFailed respectively.
	 */
	private static void resetCurrentStats() {
		totalPassed += numPassed;
		totalFailed += numFailed;
		
		numPassed = 0;
		numFailed = 0;
	}
	
	/**
	 * Method: resetAllStats()
	 * 
	 * Description: This method resets numPassed, totalPassed, numFailed, and totalFailed all to 0.
	 */
	private static void resetAllStats() {
		numPassed = 0;
		numFailed = 0;
		totalPassed = 0;
		totalFailed = 0;
	}
	
	
	/**********
	 * <p> Method: runTests() </p>
	 *
	 * <p> Description: Runs through a sequence of test cases, printing statistics afterwards.
	 * 	Information about what these tests target can be found in "TP1 Test Cases.pdf"
	 * </p>
	 */
	public static void runTests() {
		resetAllStats();
		
		
		
		// Username Test Cases
		System.out.println("---Beginning Username Tests---");
		performUsernameTestCase(1, "UsernameIsTooLong", false);
		performUsernameTestCase(2, "Username12345678", true);
		performUsernameTestCase(3, "User_name", true);
		performUsernameTestCase(4, "User.name", true);
		performUsernameTestCase(5, "User-name", true);
		performUsernameTestCase(6, "User__name", false);
		performUsernameTestCase(7, "User..name", false);
		performUsernameTestCase(8, "User--name", false);
		performUsernameTestCase(9, "1user", false);
		performUsernameTestCase(10, "four", true);
		performUsernameTestCase(11, "ABC", false);
		performUsernameTestCase(12, "", false);
		performUsernameTestCase(13, "T-E_S.T", true);
		performUsernameTestCase(14, "T-_E_.S.-T", false);
		printStats();
		resetCurrentStats();
		System.out.println("---End Username Tests---");
		
		// Password Validation (Length) Test Cases
		System.out.println("---Beginning Password Validation Tests---");
		performPasswordTestCase(1, "", false);
		performPasswordTestCase(2, "ArizonaState2026!", true);
		performPasswordTestCase(3, "A", false);
		performPasswordTestCase(4, "ArizonaState2026", false);
		performPasswordTestCase(5, "ArizonaState!", false);
		performPasswordTestCase(6, "02052026!", false);
		performPasswordTestCase(7, "arizonastate2026!", false);
		performPasswordTestCase(8, "ARIZONASTATE2026!", false);
		printStats();
		resetCurrentStats();
		System.out.println("---End Password Validation Tests---");
		

		// Invite Code (Length) Test Cases
		System.out.println("---Beginning Invite Code Tests---");
		performInviteCodeTestCase(1,"abcd12",true);
		performInviteCodeTestCase(2,"abcd",false);
		performInviteCodeTestCase(3,"abcd123",false);
		performInviteCodeTestCase(4,"",false);
		
		printStats();
		resetCurrentStats();
		System.out.println("---End Invite Code Tests---");
		
//		// Email Validation Test Cases
		System.out.println("---Beginning Email Validation Tests---");
		performEmailTestCase(1, "student@asu.edu", true);
		performEmailTestCase(2, "student.asu.edu", false);
		performEmailTestCase(3, "student@@asu.edu", false);
		performEmailTestCase(4, "", false);
		performEmailTestCase(5, "@asu.edu", false);
		performEmailTestCase(6, "student@.edu", false);
		performEmailTestCase(7, "student", false);
		
		printStats();
		resetCurrentStats();
		System.out.println("---End Email Validation Tests---");
		
		// Name Validation Test Cases
		System.out.println("---Beginning Name Validation Tests---");
		performNameTestCase(1, "", false);
		performNameTestCase(2, "NameWithFiftyCharsABCDEABCDEABCDEABCDEABCDEABCDEAB", true);
		performNameTestCase(3, "NameWithFiftyOneCharsABCDEABCDEABCDEABCDEABCDEABCDE", false);
		performNameTestCase(4, "A", true);
		performNameTestCase(5, "A-B", true);
		performNameTestCase(6, "A$B", false);
		
		printStats();
		resetCurrentStats();
		System.out.println("---End Name Validation Tests---");
		
		// ---- Phase 2: Post Content Recognizer Tests ----
		System.out.println("---Beginning Post Content Validation Tests---");
		performPostContentTestCase(1, "Test Title",  "Hello, I have a question about the homework.", true);
		performPostContentTestCase(2, "Test Title",  "A",                                             true);
		performPostContentTestCase(3, "Test Title",  "",                                              false);
		performPostContentTestCase(4, "Test Title",  "   ",                                           false);
		performPostContentTestCase(5, "Test Title",  null,                                            false);
		performPostContentTestCase(6, "Test Title",  "\n\n\n",                                        false);
		performPostContentTestCase(7, "Test Title",  "\t   \t",                                       false);
		performPostContentTestCase(8, "Test Title",  generateString(2000),                            true);
		performPostContentTestCase(9, "Test Title",  generateString(2001),                            false);
		performPostContentTestCase(10, "Test Title", "What is due this week? #homework",              true);
		printStats();
		resetCurrentStats();
		System.out.println("---End Post Content Validation Tests---");

		// ---- Phase 2: Database-Level CREATE Tests ----
		System.out.println("---Beginning Discussion CREATE Tests---");
		performDiscussionCreateTests();
		printStats();
		resetCurrentStats();
		System.out.println("---End Discussion CREATE Tests---");

		// ---- Phase 2: Database-Level READ Tests ----
		System.out.println("---Beginning Discussion READ Tests---");
		performDiscussionReadTests();
		printStats();
		resetCurrentStats();
		System.out.println("---End Discussion READ Tests---");

		// ---- Phase 2: Database-Level UPDATE Tests ----
		System.out.println("---Beginning Discussion UPDATE Tests---");
		performDiscussionUpdateTests();
		printStats();
		resetCurrentStats();
		System.out.println("---End Discussion UPDATE Tests---");

		// ---- Phase 2: Database-Level DELETE Tests ----
		System.out.println("---Beginning Discussion DELETE Tests---");
		performDiscussionDeleteTests();
		printStats();
		resetCurrentStats();
		System.out.println("---End Discussion DELETE Tests---");

		printFinalStats();
	}


	/*-*******************************************************************************************

	Phase 2 Test Helpers

	 */

	/**
	 * Generates a String of exactly {@code length} 'x' characters, used to build boundary
	 * test inputs for the PostContentRecognizer length limit.
	 *
	 * @param length  the number of characters in the returned String
	 * @return a String of {@code length} 'x' characters
	 */
	private static String generateString(int length) {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) sb.append('x');
		return sb.toString();
	}


	/**
	 * Performs a single PostContentRecognizer test case and records the result.
	 *
	 * <p> The method mirrors the pattern used by all other performXxxTestCase methods in this
	 * class.  It calls {@link PostContentRecognizer#evaluatePostContent(String)} with the given
	 * input, compares the emptiness of the returned error string to {@code expectedPass}, and
	 * prints a pass or failure message. </p>
	 *
	 * @param testCase     the test case number displayed in the header
	 * @param content      the post/reply body text to validate (may be null)
	 * @param expectedPass true if the content is expected to be valid
	 */
	public static void performPostContentTestCase(int testCase, String title, String content,
			boolean expectedPass) {

		/*** Display the individual test case header ***/
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: " + testCase);
		// Truncate very long inputs in the display to keep output readable
		String display = (content == null) ? "null"
				: (content.length() > 60 ? content.substring(0, 60) + "…[" + content.length() + " chars]"
				: "\"" + content + "\"");
		System.out.println("Input: " + display);
		System.out.println("______________");

		/*** Call the recognizer ***/
		String result = PostContentRecognizer.evaluatePostContent(content);

		if (result.isEmpty()) {
			// Content is valid
			if (expectedPass) {
				System.out.println("***Success*** The content is valid, as expected - PASS");
				numPassed++;
			} else {
				System.out.println("***Failure*** The content was accepted but was supposed "
						+ "to be invalid - FAIL");
				numFailed++;
			}
		} else {
			// Content is invalid
			if (expectedPass) {
				System.out.println("***Failure*** The content was rejected but was supposed "
						+ "to be valid - FAIL");
				System.out.println("Error message: " + result);
				numFailed++;
			} else {
				System.out.println("***Success*** The content is invalid, as expected - PASS");
				System.out.println("Error message: " + result);
				numPassed++;
			}
		}
	}


	/**
	 * Runs all database-level CREATE tests for the Phase 2 discussion system.
	 *
	 * <p> These tests exercise {@link Database#createPost(Post)} and
	 * {@link Database#createReply(Reply)} directly, covering:
	 * <ol>
	 *   <li>Creating a valid post in the seeded "General" thread (threadId = 1).</li>
	 *   <li>Attempting to create a post in a non-existent thread (should throw).</li>
	 *   <li>Creating a valid reply to the post created in test 1.</li>
	 *   <li>Attempting to create a reply to a non-existent post (should throw).</li>
	 *   <li>Verifying that the post and reply content are stored correctly.</li>
	 *   <li>Verifying that a second post by a different author is persisted independently.</li>
	 * </ol>
	 *
	 * The General thread (threadId = 1) is guaranteed to exist because
	 * {@code FoundationsMain.start()} calls {@code database.seedDefaultThread()} before
	 * {@code Tester.runTests()} is invoked. </p>
	 */
	private static void performDiscussionCreateTests() {

		Database db = applicationMain.FoundationsMain.database;

		// ---- Test 1: Create a valid post in the General thread ----
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: 1 (DB CREATE - valid post in General thread)");
		int savedPostId = -1;
		try {
			Post post1 = new Post(1, "student1", "Test Title",
					"What topics are covered on the midterm?", LocalDateTime.now());
			savedPostId = db.createPost(post1);

			if (savedPostId > 0) {
				System.out.println("***Success*** Post created with postId = " + savedPostId
						+ " - PASS");
				numPassed++;
			} else {
				System.out.println("***Failure*** createPost returned " + savedPostId
						+ " (expected > 0) - FAIL");
				numFailed++;
			}
		} catch (SQLException e) {
			System.out.println("***Failure*** Unexpected SQLException: " + e.getMessage()
					+ " - FAIL");
			numFailed++;
		}

		// ---- Test 2: Attempt to create a post in a non-existent thread ----
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: 2 (DB CREATE - post to non-existent threadId 9999)");
		try {
			Post badPost = new Post(9999, "student1", "Test Title",
					"This should fail because thread 9999 does not exist.",
					LocalDateTime.now());
			db.createPost(badPost);
			// If we reach here, the guard did not fire - test fails
			System.out.println("***Failure*** createPost accepted a non-existent threadId "
					+ "- FAIL");
			numFailed++;
		} catch (SQLException e) {
			System.out.println("***Success*** createPost correctly rejected non-existent "
					+ "threadId: " + e.getMessage() + " - PASS");
			numPassed++;
		}

		// ---- Test 3: Create a valid reply to the post from Test 1 ----
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: 3 (DB CREATE - valid reply to post " + savedPostId + ")");
		int savedReplyId = -1;
		if (savedPostId > 0) {
			try {
				Reply reply1 = new Reply(savedPostId, "student2",
						"The midterm covers Chapters 1–5 plus the FSM material.",
						LocalDateTime.now());
				savedReplyId = db.createReply(reply1);

				if (savedReplyId > 0) {
					System.out.println("***Success*** Reply created with replyId = "
							+ savedReplyId + " - PASS");
					numPassed++;
				} else {
					System.out.println("***Failure*** createReply returned " + savedReplyId
							+ " (expected > 0) - FAIL");
					numFailed++;
				}
			} catch (SQLException e) {
				System.out.println("***Failure*** Unexpected SQLException: " + e.getMessage()
						+ " - FAIL");
				numFailed++;
			}
		} else {
			System.out.println("***Skipped*** Post from Test 1 was not created; "
					+ "cannot test reply - counted as FAIL");
			numFailed++;
		}

		// ---- Test 4: Attempt to create a reply to a non-existent post ----
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: 4 (DB CREATE - reply to non-existent postId 999999)");
		try {
			Reply badReply = new Reply(999999, "student2",
					"This should fail because postId 999999 does not exist.",
					LocalDateTime.now());
			db.createReply(badReply);
			// If we reach here, the guard did not fire - test fails
			System.out.println("***Failure*** createReply accepted a non-existent postId "
					+ "- FAIL");
			numFailed++;
		} catch (SQLException e) {
			System.out.println("***Success*** createReply correctly rejected non-existent "
					+ "postId: " + e.getMessage() + " - PASS");
			numPassed++;
		}

		// ---- Test 5: Verify doesPostExist returns true for the created post ----
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: 5 (DB READ-BACK - doesPostExist(" + savedPostId + "))");
		if (savedPostId > 0) {
			boolean exists = db.doesPostExist(savedPostId);
			if (exists) {
				System.out.println("***Success*** doesPostExist(" + savedPostId
						+ ") returned true - PASS");
				numPassed++;
			} else {
				System.out.println("***Failure*** doesPostExist(" + savedPostId
						+ ") returned false after a successful INSERT - FAIL");
				numFailed++;
			}
		} else {
			System.out.println("***Skipped*** No post to verify - counted as FAIL");
			numFailed++;
		}

		// ---- Test 6: Verify doesPostExist returns false for a non-existent post ----
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: 6 (DB READ-BACK - doesPostExist(999999))");
		boolean shouldBeFalse = db.doesPostExist(999999);
		if (!shouldBeFalse) {
			System.out.println("***Success*** doesPostExist(999999) returned false "
					+ "as expected - PASS");
			numPassed++;
		} else {
			System.out.println("***Failure*** doesPostExist(999999) returned true "
					+ "for a non-existent post - FAIL");
			numFailed++;
		}

		// ---- Test 7: Create a second post by a different author ----
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: 7 (DB CREATE - second post by different author)");
		try {
			Post post2 = new Post(1, "student2", "Test Title",
					"I also have a question - will the exam be open-book?",
					LocalDateTime.now());
			int postId2 = db.createPost(post2);

			if (postId2 > savedPostId) {
				System.out.println("***Success*** Second post created with postId = " + postId2
						+ " (greater than first postId = " + savedPostId + ") - PASS");
				numPassed++;
			} else {
				System.out.println("***Failure*** Second postId " + postId2
						+ " is not greater than first postId " + savedPostId
						+ " - FAIL");
				numFailed++;
			}
		} catch (SQLException e) {
			System.out.println("***Failure*** Unexpected SQLException on second post: "
					+ e.getMessage() + " - FAIL");
			numFailed++;
		}
	}
	
	
	
	
	/**
	 * Runs all database-level READ tests for the Phase 2 discussion system.
	 *
	 * <p> These tests exercise the READ and ReadStatus methods added to {@link Database}:
	 * <ol>
	 *   <li>getPostsByThread - returns posts for the General thread.</li>
	 *   <li>getPostsByAuthor - returns posts by a known author.</li>
	 *   <li>searchPosts (all threads) - keyword match finds the seeded post.</li>
	 *   <li>searchPosts (specific thread) - same keyword, filtered to thread 1.</li>
	 *   <li>searchPosts - keyword that matches nothing returns empty list.</li>
	 *   <li>getRepliesForPost - returns replies for a known post.</li>
	 *   <li>getReplyCount - count matches the number of replies inserted.</li>
	 *   <li>markPostAsRead / isPostRead - round-trip read-status for a post.</li>
	 *   <li>markReplyAsRead / isReplyRead - round-trip read-status for a reply.</li>
	 *   <li>getUnreadReplyCount - unread count drops to zero after marking replies read.</li>
	 * </ol>
	 *
	 * The test creates its own Post and Reply fixtures inside the General thread
	 * (threadId = 1) so it is self-contained and does not depend on the CREATE test
	 * data remaining in a particular state. </p>
	 */
	private static void performDiscussionReadTests() {

		Database db = applicationMain.FoundationsMain.database;
		final String READER = "readTestUser";

		// ------------------------------------------------------------------
		// Set up: create a post and two replies used by multiple tests below
		// ------------------------------------------------------------------
		int readTestPostId  = -1;
		int readTestReply1  = -1;
		int readTestReply2  = -1;

		try {
			Post setup = new Post(1, "student1", "Test Title",
					"READ-TEST: This post is used by the READ unit tests.",
					LocalDateTime.now());
			readTestPostId = db.createPost(setup);

			Reply r1 = new Reply(readTestPostId, "student2",
					"READ-TEST reply one.", LocalDateTime.now());
			readTestReply1 = db.createReply(r1);

			Reply r2 = new Reply(readTestPostId, "student2",
					"READ-TEST reply two.", LocalDateTime.now());
			readTestReply2 = db.createReply(r2);
		} catch (SQLException e) {
			System.out.println("READ TESTS SETUP FAILED: " + e.getMessage());
			numFailed += 10;
			return;
		}

		// ---- Test 1: getPostsByThread returns at least one post for thread 1 ----
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: 1 (DB READ - getPostsByThread(1) returns posts)");
		List<Post> threadPosts = db.getPostsByThread(1);
		if (!threadPosts.isEmpty()) {
			System.out.println("***Success*** getPostsByThread(1) returned "
					+ threadPosts.size() + " post(s) - PASS");
			numPassed++;
		} else {
			System.out.println("***Failure*** getPostsByThread(1) returned 0 posts "
					+ "after INSERT - FAIL");
			numFailed++;
		}

		// ---- Test 2: getPostsByAuthor returns post by "student1" ----
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: 2 (DB READ - getPostsByAuthor(\"student1\") returns posts)");
		List<Post> authorPosts = db.getPostsByAuthor("student1");
		if (!authorPosts.isEmpty()) {
			System.out.println("***Success*** getPostsByAuthor(\"student1\") returned "
					+ authorPosts.size() + " post(s) - PASS");
			numPassed++;
		} else {
			System.out.println("***Failure*** getPostsByAuthor(\"student1\") returned "
					+ "0 posts - FAIL");
			numFailed++;
		}

		// ---- Test 3: searchPosts all threads - known keyword finds results ----
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: 3 (DB READ - searchPosts(\"READ-TEST\", -1) finds results)");
		List<Post> searchAll = db.searchPosts("READ-TEST", -1);
		if (!searchAll.isEmpty()) {
			System.out.println("***Success*** searchPosts(\"READ-TEST\", -1) returned "
					+ searchAll.size() + " result(s) - PASS");
			numPassed++;
		} else {
			System.out.println("***Failure*** searchPosts returned 0 results for a known "
					+ "keyword - FAIL");
			numFailed++;
		}

		// ---- Test 4: searchPosts with thread filter 1 - still finds results ----
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: 4 (DB READ - searchPosts(\"READ-TEST\", 1) with thread filter)");
		List<Post> searchFiltered = db.searchPosts("READ-TEST", 1);
		if (!searchFiltered.isEmpty()) {
			System.out.println("***Success*** searchPosts(\"READ-TEST\", 1) returned "
					+ searchFiltered.size() + " result(s) - PASS");
			numPassed++;
		} else {
			System.out.println("***Failure*** searchPosts with thread filter returned 0 "
					+ "results for a known keyword - FAIL");
			numFailed++;
		}

		// ---- Test 5: searchPosts - non-matching keyword returns empty list ----
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: 5 (DB READ - searchPosts(\"ZZZNONEXISTENTKEYWORDZZZ\", -1))");
		List<Post> noResults = db.searchPosts("ZZZNONEXISTENTKEYWORDZZZ", -1);
		if (noResults.isEmpty()) {
			System.out.println("***Success*** searchPosts returned 0 results for a "
					+ "non-matching keyword - PASS");
			numPassed++;
		} else {
			System.out.println("***Failure*** searchPosts returned " + noResults.size()
					+ " results for a keyword that should not match any post - FAIL");
			numFailed++;
		}

		// ---- Test 6: getRepliesForPost returns exactly 2 replies ----
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: 6 (DB READ - getRepliesForPost(" + readTestPostId
				+ ") returns 2 replies)");
		List<Reply> replies = db.getRepliesForPost(readTestPostId);
		if (replies.size() == 2) {
			System.out.println("***Success*** getRepliesForPost(" + readTestPostId
					+ ") returned 2 replies - PASS");
			numPassed++;
		} else {
			System.out.println("***Failure*** getRepliesForPost returned " + replies.size()
					+ " (expected 2) - FAIL");
			numFailed++;
		}

		// ---- Test 7: getReplyCount matches the number of inserted replies ----
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: 7 (DB READ - getReplyCount(" + readTestPostId + ") == 2)");
		int count = db.getReplyCount(readTestPostId);
		if (count == 2) {
			System.out.println("***Success*** getReplyCount(" + readTestPostId
					+ ") returned 2 - PASS");
			numPassed++;
		} else {
			System.out.println("***Failure*** getReplyCount returned " + count
					+ " (expected 2) - FAIL");
			numFailed++;
		}

		// ---- Test 8: markPostAsRead / isPostRead round-trip ----
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: 8 (DB READ - markPostAsRead / isPostRead round-trip)");
		db.markPostAsRead(readTestPostId, READER);
		boolean postIsRead = db.isPostRead(readTestPostId, READER);
		if (postIsRead) {
			System.out.println("***Success*** isPostRead returned true after "
					+ "markPostAsRead - PASS");
			numPassed++;
		} else {
			System.out.println("***Failure*** isPostRead returned false after "
					+ "markPostAsRead - FAIL");
			numFailed++;
		}

		// ---- Test 9: markReplyAsRead / isReplyRead round-trip ----
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: 9 (DB READ - markReplyAsRead / isReplyRead round-trip)");
		db.markReplyAsRead(readTestReply1, READER);
		boolean replyIsRead = db.isReplyRead(readTestReply1, READER);
		if (replyIsRead) {
			System.out.println("***Success*** isReplyRead returned true after "
					+ "markReplyAsRead - PASS");
			numPassed++;
		} else {
			System.out.println("***Failure*** isReplyRead returned false after "
					+ "markReplyAsRead - FAIL");
			numFailed++;
		}

		// ---- Test 10: getUnreadReplyCount drops to 0 after marking all replies read ----
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: 10 (DB READ - getUnreadReplyCount drops to 0 after "
				+ "marking all replies read)");
		// Mark the second reply as read too
		db.markReplyAsRead(readTestReply2, READER);
		int unread = db.getUnreadReplyCount(readTestPostId, READER);
		if (unread == 0) {
			System.out.println("***Success*** getUnreadReplyCount returned 0 after both "
					+ "replies were marked as read - PASS");
			numPassed++;
		} else {
			System.out.println("***Failure*** getUnreadReplyCount returned " + unread
					+ " (expected 0) after marking all replies read - FAIL");
			numFailed++;
		}
	}


	/**
	 * Runs all database-level UPDATE tests for the Phase 2 discussion system.
	 *
	 * <p> These tests exercise {@link Database#updatePost}, {@link Database#softDeletePost},
	 * {@link Database#updateReply}, {@link Database#getPostById}, and
	 * {@link Database#getReplyById}:
	 * <ol>
	 *   <li>updatePost - correct author, content changes in the database.</li>
	 *   <li>updatePost - wrong author, returns 0 rows updated (rejected).</li>
	 *   <li>updatePost - non-existent postId, returns 0 rows updated.</li>
	 *   <li>softDeletePost - correct author, isDeleted flips to TRUE.</li>
	 *   <li>softDeletePost - wrong author, returns 0 rows updated.</li>
	 *   <li>updatePost on a soft-deleted post - returns 0 (blocked by isDeleted filter).</li>
	 *   <li>updateReply - correct author, content changes in the database.</li>
	 *   <li>updateReply - wrong author, returns 0 rows updated.</li>
	 * </ol>
	 * </p>
	 */
	private static void performDiscussionUpdateTests() {

		Database db = applicationMain.FoundationsMain.database;

		// ------------------------------------------------------------------
		// Set up: create a post and a reply to exercise all UPDATE paths
		// ------------------------------------------------------------------
		int updatePostId  = -1;
		int updateReplyId = -1;

		try {
			Post setup = new Post(1, "student1", "Test Title",
					"UPDATE-TEST: Original post content.", LocalDateTime.now());
			updatePostId = db.createPost(setup);

			Reply rSetup = new Reply(updatePostId, "student2",
					"UPDATE-TEST: Original reply content.", LocalDateTime.now());
			updateReplyId = db.createReply(rSetup);
		} catch (SQLException e) {
			System.out.println("UPDATE TESTS SETUP FAILED: " + e.getMessage());
			numFailed += 8;
			return;
		}

		// ---- Test 1: updatePost - correct author changes content ----
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: 1 (DB UPDATE - updatePost correct author)");
		int rows = db.updatePost(updatePostId, "EDITED: Updated post content.", "student1");
		if (rows == 1) {
			Post check = db.getPostById(updatePostId);
			if (check != null && check.getContent().startsWith("EDITED:")) {
				System.out.println("***Success*** updatePost changed content in DB - PASS");
				numPassed++;
			} else {
				System.out.println("***Failure*** updatePost returned 1 but content unchanged"
						+ " - FAIL");
				numFailed++;
			}
		} else {
			System.out.println("***Failure*** updatePost returned " + rows
					+ " (expected 1) - FAIL");
			numFailed++;
		}

		// ---- Test 2: updatePost - wrong author, must be rejected ----
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: 2 (DB UPDATE - updatePost wrong author rejected)");
		int rowsWrongAuthor = db.updatePost(updatePostId, "HIJACKED content.", "student2");
		if (rowsWrongAuthor == 0) {
			System.out.println("***Success*** updatePost rejected wrong-author edit "
					+ "(0 rows updated) - PASS");
			numPassed++;
		} else {
			System.out.println("***Failure*** updatePost allowed wrong-author edit - FAIL");
			numFailed++;
		}

		// ---- Test 3: updatePost - non-existent postId ----
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: 3 (DB UPDATE - updatePost non-existent postId)");
		int rowsNoPost = db.updatePost(999999, "Should not matter.", "student1");
		if (rowsNoPost == 0) {
			System.out.println("***Success*** updatePost returned 0 for non-existent post "
					+ "- PASS");
			numPassed++;
		} else {
			System.out.println("***Failure*** updatePost returned " + rowsNoPost
					+ " for a non-existent postId - FAIL");
			numFailed++;
		}

		// ---- Test 4: softDeletePost - correct author flips isDeleted ----
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: 4 (DB UPDATE - softDeletePost correct author)");
		int deleteRows = db.softDeletePost(updatePostId, "student1");
		if (deleteRows == 1) {
			Post deleted = db.getPostById(updatePostId);
			if (deleted != null && deleted.isDeleted()) {
				System.out.println("***Success*** softDeletePost set isDeleted = TRUE - PASS");
				numPassed++;
			} else {
				System.out.println("***Failure*** softDeletePost returned 1 but isDeleted "
						+ "not TRUE - FAIL");
				numFailed++;
			}
		} else {
			System.out.println("***Failure*** softDeletePost returned " + deleteRows
					+ " (expected 1) - FAIL");
			numFailed++;
		}

		// ---- Test 5: softDeletePost - wrong author, must be rejected ----
		// Create a fresh post for this test (the previous one is already deleted)
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: 5 (DB UPDATE - softDeletePost wrong author rejected)");
		int freshPostId = -1;
		try {
			Post fresh = new Post(1, "student1", "Test Title",
					"UPDATE-TEST: Fresh post for delete-auth test.", LocalDateTime.now());
			freshPostId = db.createPost(fresh);
		} catch (SQLException e) {
			System.out.println("***Skipped*** Could not create fresh post: "
					+ e.getMessage() + " - counted as FAIL");
			numFailed++;
			freshPostId = -1;
		}
		if (freshPostId > 0) {
			int wrongDelete = db.softDeletePost(freshPostId, "student2");
			if (wrongDelete == 0) {
				System.out.println("***Success*** softDeletePost rejected wrong-author delete"
						+ " (0 rows) - PASS");
				numPassed++;
			} else {
				System.out.println("***Failure*** softDeletePost allowed wrong-author delete"
						+ " - FAIL");
				numFailed++;
			}
		}

		// ---- Test 6: updatePost on a soft-deleted post is blocked ----
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: 6 (DB UPDATE - updatePost on deleted post blocked)");
		int blockedEdit = db.updatePost(updatePostId, "Should be blocked.", "student1");
		if (blockedEdit == 0) {
			System.out.println("***Success*** updatePost returned 0 for a soft-deleted post"
					+ " - PASS");
			numPassed++;
		} else {
			System.out.println("***Failure*** updatePost allowed edit on a deleted post "
					+ "- FAIL");
			numFailed++;
		}

		// ---- Test 7: updateReply - correct author changes content ----
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: 7 (DB UPDATE - updateReply correct author)");
		int replyRows = db.updateReply(updateReplyId, "EDITED: Updated reply.", "student2");
		if (replyRows == 1) {
			Reply check = db.getReplyById(updateReplyId);
			if (check != null && check.getContent().startsWith("EDITED:")) {
				System.out.println("***Success*** updateReply changed content in DB - PASS");
				numPassed++;
			} else {
				System.out.println("***Failure*** updateReply returned 1 but content "
						+ "unchanged - FAIL");
				numFailed++;
			}
		} else {
			System.out.println("***Failure*** updateReply returned " + replyRows
					+ " (expected 1) - FAIL");
			numFailed++;
		}

		// ---- Test 8: updateReply - wrong author, must be rejected ----
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: 8 (DB UPDATE - updateReply wrong author rejected)");
		int replyWrong = db.updateReply(updateReplyId, "HIJACKED.", "student1");
		if (replyWrong == 0) {
			System.out.println("***Success*** updateReply rejected wrong-author edit "
					+ "(0 rows) - PASS");
			numPassed++;
		} else {
			System.out.println("***Failure*** updateReply allowed wrong-author edit - FAIL");
			numFailed++;
		}
	}


	/**
	 * Runs all database-level DELETE tests for the Phase 2 discussion system.
	 *
	 * <p> These tests exercise {@link Database#softDeleteReply} and confirm that the
	 * author-guard and isDeleted flag behave correctly:
	 * <ol>
	 *   <li>softDeleteReply - correct author, isDeleted flips to TRUE.</li>
	 *   <li>softDeleteReply - wrong author, returns 0 rows (rejected).</li>
	 *   <li>softDeleteReply - non-existent replyId, returns 0 rows.</li>
	 *   <li>updateReply on a soft-deleted reply - returns 0 (blocked by isDeleted filter).</li>
	 *   <li>getReplyById after delete - isDeleted flag is TRUE in the returned object.</li>
	 *   <li>softDeletePost symmetry - soft-deleting a post does not remove its replies.</li>
	 * </ol>
	 * </p>
	 */
	private static void performDiscussionDeleteTests() {

		Database db = applicationMain.FoundationsMain.database;

		// ------------------------------------------------------------------
		// Set up: create a post with two replies to exercise all DELETE paths
		// ------------------------------------------------------------------
		int deletePostId  = -1;
		int deleteReply1  = -1;
		int deleteReply2  = -1;

		try {
			Post setup = new Post(1, "student1", "Test Title",
					"DELETE-TEST: Post used by delete unit tests.", LocalDateTime.now());
			deletePostId = db.createPost(setup);

			Reply r1 = new Reply(deletePostId, "student2",
					"DELETE-TEST reply one.", LocalDateTime.now());
			deleteReply1 = db.createReply(r1);

			Reply r2 = new Reply(deletePostId, "student2",
					"DELETE-TEST reply two - kept to test post-delete visibility.",
					LocalDateTime.now());
			deleteReply2 = db.createReply(r2);
		} catch (SQLException e) {
			System.out.println("DELETE TESTS SETUP FAILED: " + e.getMessage());
			numFailed += 6;
			return;
		}

		// ---- Test 1: softDeleteReply - correct author flips isDeleted ----
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: 1 (DB DELETE - softDeleteReply correct author)");
		int rows = db.softDeleteReply(deleteReply1, "student2");
		if (rows == 1) {
			Reply check = db.getReplyById(deleteReply1);
			if (check != null && check.isDeleted()) {
				System.out.println("***Success*** softDeleteReply set isDeleted = TRUE - PASS");
				numPassed++;
			} else {
				System.out.println("***Failure*** softDeleteReply returned 1 but isDeleted "
						+ "not TRUE - FAIL");
				numFailed++;
			}
		} else {
			System.out.println("***Failure*** softDeleteReply returned " + rows
					+ " (expected 1) - FAIL");
			numFailed++;
		}

		// ---- Test 2: softDeleteReply - wrong author, must be rejected ----
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: 2 (DB DELETE - softDeleteReply wrong author rejected)");
		int wrongRows = db.softDeleteReply(deleteReply2, "student1");   // student1 did NOT write it
		if (wrongRows == 0) {
			System.out.println("***Success*** softDeleteReply rejected wrong-author delete "
					+ "(0 rows) - PASS");
			numPassed++;
		} else {
			System.out.println("***Failure*** softDeleteReply allowed wrong-author delete "
					+ "- FAIL");
			numFailed++;
		}

		// ---- Test 3: softDeleteReply - non-existent replyId ----
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: 3 (DB DELETE - softDeleteReply non-existent replyId)");
		int noRows = db.softDeleteReply(999999, "student2");
		if (noRows == 0) {
			System.out.println("***Success*** softDeleteReply returned 0 for non-existent "
					+ "replyId - PASS");
			numPassed++;
		} else {
			System.out.println("***Failure*** softDeleteReply returned " + noRows
					+ " for a non-existent replyId - FAIL");
			numFailed++;
		}

		// ---- Test 4: updateReply on a soft-deleted reply is blocked ----
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: 4 (DB DELETE - updateReply on deleted reply blocked)");
		int blockedEdit = db.updateReply(deleteReply1, "Should be blocked.", "student2");
		if (blockedEdit == 0) {
			System.out.println("***Success*** updateReply returned 0 for a soft-deleted reply"
					+ " - PASS");
			numPassed++;
		} else {
			System.out.println("***Failure*** updateReply allowed edit on a deleted reply "
					+ "- FAIL");
			numFailed++;
		}

		// ---- Test 5: getReplyById returns isDeleted = true after delete ----
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: 5 (DB DELETE - getReplyById reflects isDeleted flag)");
		Reply deleted = db.getReplyById(deleteReply1);
		if (deleted != null && deleted.isDeleted()) {
			System.out.println("***Success*** getReplyById returned reply with isDeleted = "
					+ "true - PASS");
			numPassed++;
		} else {
			System.out.println("***Failure*** getReplyById did not reflect isDeleted = true "
					+ "after softDeleteReply - FAIL");
			numFailed++;
		}

		// ---- Test 6: Soft-deleting the post does not remove replies ----
		System.out.println(
				"____________________________________________________________________________"
				+ "\n\nTest case: 6 (DB DELETE - softDeletePost preserves replies)");
		db.softDeletePost(deletePostId, "student1");
		List<Reply> replies = db.getRepliesForPost(deletePostId);
		if (replies.size() == 2) {
			System.out.println("***Success*** getRepliesForPost returned 2 replies after "
					+ "the parent post was soft-deleted - PASS");
			numPassed++;
		} else {
			System.out.println("***Failure*** getRepliesForPost returned " + replies.size()
					+ " replies after parent soft-delete (expected 2) - FAIL");
			numFailed++;
		}
	}


	/**
	 * This method prints the current testing statistics to console.
	 */
	public static void printStats() {
		System.out.println("Testing statistics (Current Category):");
		System.out.println("Passed: "+ numPassed + "/" + (numPassed + numFailed));
		System.out.println("Failed: "+ numFailed + "/" + (numPassed + numFailed) );
	}
	
	
	/**
	 * This method prints the final testing statistics to console.
	 */
	public static void printFinalStats() {
		System.out.println("Testing statistics (Overall):");
		System.out.println("Passed: "+ totalPassed + "/" + (totalPassed + totalFailed));
		System.out.println("Failed: "+ totalFailed + "/" + (totalPassed + totalFailed) );
	}
	
	
	/**
	 * Performs a single email address validation test case and records the result.
	 *
	 * @param testCase the test case number
	 * @param inputEmail the email address to validate
	 * @param expectedPass true if the input is expected to be valid
	 */
	public static void performEmailTestCase(int testCase, String inputEmail, Boolean expectedPass) {
		/************** Display an individual test case header **************/
		System.out.println("____________________________________________________________________________\n\nTest case: " + testCase);
		System.out.println("Input: \"" + inputEmail + "\"");
		System.out.println("______________");
		
		/************** Display an individual test case header **************/
		System.out.println("____________________________________________________________________________\n\nTest case: " + testCase);
		System.out.println("Input: \"" + inputEmail + "\"");
		System.out.println("______________");
		
		/************** Call the recognizer to process the input **************/
		String result = EmailAddressRecognizer.checkEmailAddress(inputEmail);
		
		if (result.isEmpty())
		{
			// input is valid
			if (expectedPass) {
				// input is valid, as expected (PASS)
				System.out.println("***Success*** The email <" + inputEmail + 
						"> is valid, so this is a pass!");
				numPassed++;
			} else {
				// input is valid, against expectations (FAIL)
				System.out.println("***Failure*** The email <" + inputEmail + "> is valid." + 
						"\nBut it was supposed to be invalid, so this is a failure!\n");
				System.out.println("Error message: " + result);
				numFailed++;
			}
		} else {
			// input is invalid
			if (expectedPass)
			{
				//
				// input is invalid, against expectations (FAIL)
				//
				System.out.println("***Failure*** The email <" + inputEmail + "> is invalid." + 
						"\nBut it was supposed to be valid, so this is a failure!\n");
				System.out.println("Error message: " + result);
				numFailed++;
			} else {
				//
				// input is invalid, as expected (PASS) 
				//
				System.out.println("***Success*** The email <" + inputEmail + "> is invalid." + 
						"\nBut it was supposed to be invalid, so this is a pass!\n");
				System.out.println("Error message: " + result);
				numPassed++;
			}
		}
	}
	
	
	/**
	 * Performs a single name validation test case and records the result.
	 *
	 * @param testCase the test case number
	 * @param inputName the name to validate
	 * @param expectedPass true if the input is expected to be valid
	 */
	public static void performNameTestCase(int testCase, String inputName, Boolean expectedPass) {
		/************** Display an individual test case header **************/
		System.out.println("____________________________________________________________________________\n\nTest case: " + testCase);
		System.out.println("Input: \"" + inputName + "\"");
		System.out.println("______________");
		
		/************** Call the recognizer to process the input **************/
		String result = NameRecognizer.evaluateName(inputName);
		
		if (result.isEmpty())
		{
			// input is valid
			if (expectedPass) {
				// input is valid, as expected (PASS)
				System.out.println("***Success*** The name <" + inputName + 
						"> is valid, so this is a pass!");
				numPassed++;
			} else {
				// input is valid, against expectations (FAIL)
				System.out.println("***Failure*** The name <" + inputName + "> is valid." + 
						"\nBut it was supposed to be invalid, so this is a failure!\n");
				System.out.println("Error message: " + result);
				numFailed++;
			}
		} else {
			// input is invalid
			if (expectedPass)
			{
				//
				// input is invalid, against expectations (FAIL)
				//
				System.out.println("***Failure*** The name <" + inputName + "> is invalid." + 
						"\nBut it was supposed to be valid, so this is a failure!\n");
				System.out.println("Error message: " + result);
				numFailed++;
			} else {
				//
				// input is invalid, as expected (PASS) 
				//
				System.out.println("***Success*** The name <" + inputName + "> is invalid." + 
						"\nBut it was supposed to be invalid, so this is a pass!\n");
				System.out.println("Error message: " + result);
				numPassed++;
			}
		}
	}
	
	
	/**
	 * Performs a single invite code validation test case and records the result.
	 *
	 * @param testCase the test case number
	 * @param inputCode the invite code to validate
	 * @param expectedPass true if the input is expected to be valid
	 */
	public static void performInviteCodeTestCase(int testCase, String inputCode, Boolean expectedPass) {
		/************** Display an individual test case header **************/
		System.out.println("____________________________________________________________________________\n\nTest case: " + testCase);
		System.out.println("Input: \"" + inputCode + "\"");
		System.out.println("______________");
		
		
		/************** Call the recognizer to process the input **************/
		String result = InviteCodeRecognizer.evaluateInviteCode(inputCode);
		
		if (result.isEmpty())
		{
			// input is valid
			if (expectedPass) {
				// input is valid, as expected (PASS)
				System.out.println("***Success*** The code <" + inputCode + 
						"> is valid, so this is a pass!");
				numPassed++;
			} else {
				// input is valid, against expectations (FAIL)
				System.out.println("***Failure*** The code <" + inputCode + "> is valid." + 
						"\nBut it was supposed to be invalid, so this is a failure!\n");
				System.out.println("Error message: " + result);
				numFailed++;
			}
		} else {
			// input is invalid
			if (expectedPass)
			{
				//
				// input is invalid, against expectations (FAIL)
				//
				System.out.println("***Failure*** The code <" + inputCode + "> is invalid." + 
						"\nBut it was supposed to be valid, so this is a failure!\n");
				System.out.println("Error message: " + result);
				numFailed++;
			} else {
				//
				// input is invalid, as expected (PASS) 
				//
				System.out.println("***Success*** The code <" + inputCode + "> is invalid." + 
						"\nBut it was supposed to be invalid, so this is a pass!\n");
				System.out.println("Error message: " + result);
				numPassed++;
			}
		}
	}
	
	
	
	
	/**
	 * Performs a single username validation test case and records the result.
	 *
	 * @param testCase the test case number
	 * @param inputUsername the username to validate
	 * @param expectedPass true if the input is expected to be valid
	 */
	public static void performUsernameTestCase(int testCase, String inputUsername, Boolean expectedPass )
	{
		/************** Display an individual test case header **************/
		System.out.println("____________________________________________________________________________\n\nTest case: " + testCase);
		System.out.println("Input: \"" + inputUsername + "\"");
		System.out.println("______________");
		System.out.println("\nFinite state machine execution trace:");
		
		/************** Call the recognizer to process the input **************/
		String result = UserNameRecognizer.checkForValidUserName(inputUsername);
		
		if (result.isEmpty())
		{
			// username is valid
			if (expectedPass) {
				// username is valid, as expected (PASS)
				System.out.println("***Success*** The username <" + inputUsername + 
						"> is valid, so this is a pass!");
				numPassed++;
			} else {
				// username is valid, against expectations (FAIL)
				System.out.println("***Failure*** The username <" + inputUsername + "> is valid." + 
						"\nBut it was supposed to be invalid, so this is a failure!\n");
				System.out.println("Error message: " + result);
				numFailed++;
			}
		} else {
			// username is invalid
			if (expectedPass)
			{
				//
				// username is invalid, against expectations (FAIL)
				//
				System.out.println("***Failure*** The username <" + inputUsername + "> is invalid." + 
						"\nBut it was supposed to be valid, so this is a failure!\n");
				System.out.println("Error message: " + result);
				numFailed++;
			} else {
				//
				// username is invalid, as expected (PASS) 
				//
				System.out.println("***Success*** The username <" + inputUsername + "> is invalid." + 
						"\nBut it was supposed to be invalid, so this is a pass!\n");
				System.out.println("Error message: " + result);
				numPassed++;
			}
		}
	}
	
	/*
	 * This method sets up the input value for the Password test from the input parameters,
	 * displays test execution information, invokes precisely the same recognizer
	 * that the interactive JavaFX mainline uses, interprets the returned value,
	 * and displays the interpreted result.
	 */
	private static void performPasswordTestCase(int testCase, String inputText, boolean expectedPass) {
				
		/************** Display an individual test case header **************/
		System.out.println("____________________________________________________________________________\n\nTest case: " + testCase);
		System.out.println("Input: \"" + inputText + "\"");
		System.out.println("______________");
		System.out.println("\nFinite state machine execution trace:");
		
		/************** Call the recognizer to process the input **************/
		String resultText = PasswordRecognizer.evaluatePassword(inputText);
		
		/************** Interpret the result and display that interpreted information **************/
		System.out.println();
		
		// If the resulting text is empty, the recognizer accepted the input
		if (resultText != "") {
			 // If the test case expected the test to pass then this is a failure
			if (expectedPass) {
				System.out.println("***Failure*** The password <" + inputText + "> is invalid." + 
						"\nBut it was supposed to be valid, so this is a failure!\n");
				System.out.println("Error message: " + resultText);
				numFailed++;
			}
			// If the test case expected the test to fail then this is a success
			else {			
				System.out.println("***Success*** The password <" + inputText + "> is invalid." + 
						"\nBut it was supposed to be invalid, so this is a pass!\n");
				System.out.println("Error message: " + resultText);
				numPassed++;
			}
		}
		
		// If the resulting text is empty, the recognizer accepted the input
		else {	
			// If the test case expected the test to pass then this is a success
			if (expectedPass) {	
				System.out.println("***Success*** The password <" + inputText + 
						"> is valid, so this is a pass!");
				numPassed++;
			}
			// If the test case expected the test to fail then this is a failure
			else {
				System.out.println("***Failure*** The password <" + inputText + 
						"> was judged as valid" + 
						"\nBut it was supposed to be invalid, so this is a failure!");
				numFailed++;
			}
		}
		displayPasswordEvaluation();
	}
	
	private static void displayPasswordEvaluation() {
		
		if (PasswordRecognizer.foundUpperCase)
			System.out.println("At least one upper case letter - Satisfied");
		else
			System.out.println("At least one upper case letter - Not Satisfied");

		if (PasswordRecognizer.foundLowerCase)
			System.out.println("At least one lower case letter - Satisfied");
		else
			System.out.println("At least one lower case letter - Not Satisfied");
	

		if (PasswordRecognizer.foundNumericDigit)
			System.out.println("At least one digit - Satisfied");
		else
			System.out.println("At least one digit - Not Satisfied");

		if (PasswordRecognizer.foundSpecialChar)
			System.out.println("At least one special character - Satisfied");
		else
			System.out.println("At least one special character - Not Satisfied");

		if (PasswordRecognizer.foundLongEnough)
			System.out.println("At least 8 characters - Satisfied");
		else
			System.out.println("At least 8 characters - Not Satisfied");
	}
	
	
}