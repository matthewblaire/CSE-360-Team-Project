package tester;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import database.Database;
import entityClasses.DiscussionThread;
import entityClasses.Post;
import entityClasses.Reply;

/*******
 * <p> Title: FullStaffThreadCRUDTester Class </p>
 *
 * <p> Description: This JUnit test class verifies the staff CRUD support
 * for discussion threads in TP3. The tests focus on thread creation, reading,
 * updating, and soft delete behavior, including the required cascade soft delete
 * of posts and replies when a staff member deletes a thread. </p>
 *
 * <p> These tests validate the new Database methods added for staff thread
 * management and help demonstrate that the staff thread management portion
 * of TP3 works correctly before the JavaFX GUI is connected to it. These
 * tests specifically validate the database support used by the Staff Home
 * page thread management controls. </p>
 *
 * @author Saam Kavusi
 *
 * @version 1.00 2026-04-08
 */

public class FullStaffThreadCRUDTester {

	/*****
	 * Verifies that a new discussion thread can be created successfully.
	 */
	@Test
	public void NormalTest01() {
		Database db = new Database();
		try {
			db.connectToDatabase();
			db.seedDefaultThread();

			int threadId = db.createDiscussionThread("Homework Questions");

			assertTrue(threadId > 0);
			assertFalse(db.isDiscussionThreadDeleted(threadId));

			db.closeConnection();
		} catch (Exception e) {
			db.closeConnection();
			fail("This is a valid test case");
		}
	}

	/*****
	 * Verifies that active discussion threads are returned in the active thread list.
	 */
	@Test
	public void NormalTest02() {
		Database db = new Database();
		try {
			db.connectToDatabase();
			db.seedDefaultThread();
			db.createDiscussionThread("Exam Review");

			List<DiscussionThread> threads = db.getActiveDiscussionThreads();

			boolean found = false;
			for (DiscussionThread thread : threads) {
				if ("Exam Review".equals(thread.getTitle())) {
					found = true;
					break;
				}
			}

			assertTrue(found);

			db.closeConnection();
		} catch (Exception e) {
			db.closeConnection();
			fail("This is a valid test case");
		}
	}

	/*****
	 * Verifies that a discussion thread title can be updated successfully.
	 */
	@Test
	public void NormalTest03() {
		Database db = new Database();
		try {
			db.connectToDatabase();
			db.seedDefaultThread();

			int threadId = db.createDiscussionThread("Old Thread Title");
			boolean updated = db.updateDiscussionThreadTitle(threadId, "New Thread Title");

			assertTrue(updated);

			List<DiscussionThread> threads = db.getActiveDiscussionThreads();
			boolean foundUpdatedTitle = false;
			for (DiscussionThread thread : threads) {
				if (thread.getThreadId() == threadId
						&& "New Thread Title".equals(thread.getTitle())) {
					foundUpdatedTitle = true;
					break;
				}
			}

			assertTrue(foundUpdatedTitle);

			db.closeConnection();
		} catch (Exception e) {
			db.closeConnection();
			fail("This is a valid test case");
		}
	}

	/*****
	 * Verifies that a thread soft-delete succeeds and marks the thread as deleted.
	 */
	@Test
	public void NormalTest04() {
		Database db = new Database();
		try {
			db.connectToDatabase();
			db.seedDefaultThread();

			int threadId = db.createDiscussionThread("Temporary Thread");
			boolean deleted = db.softDeleteDiscussionThreadCascade(threadId);

			assertTrue(deleted);
			assertTrue(db.isDiscussionThreadDeleted(threadId));

			db.closeConnection();
		} catch (Exception e) {
			db.closeConnection();
			fail("This is a valid test case");
		}
	}

	/*****
	 * Verifies that soft deleting a thread also soft-deletes its posts and replies.
	 */
	@Test
	public void NormalTest05() {
		Database db = new Database();
		try {
			db.connectToDatabase();
			db.seedDefaultThread();

			int threadId = db.createDiscussionThread("Delete Cascade Thread");

			Post post = new Post(threadId, "student1", "Thread Title",
					"Post inside the thread being deleted.", LocalDateTime.now());
			int postId = db.createPost(post);

			Reply reply = new Reply(postId, "student2",
					"Reply inside the thread being deleted.", LocalDateTime.now());
			int replyId = db.createReply(reply);

			boolean deleted = db.softDeleteDiscussionThreadCascade(threadId);

			assertTrue(deleted);
			assertTrue(db.isDiscussionThreadDeleted(threadId));
			assertTrue(db.getPostById(postId).isDeleted());
			assertTrue(db.getReplyById(replyId).isDeleted());

			db.closeConnection();
		} catch (Exception e) {
			db.closeConnection();
			fail("This is a valid test case");
		}
	}

	/*****
	 * Verifies that a non existent thread cannot be updated.
	 */
	@Test
	public void RobustTest01() {
		Database db = new Database();
		try {
			db.connectToDatabase();
			db.seedDefaultThread();

			boolean updated = db.updateDiscussionThreadTitle(999999, "Should Fail");
			assertFalse(updated);

			db.closeConnection();
		} catch (Exception e) {
			db.closeConnection();
			fail("This is a valid test case");
		}
	}

	/*****
	 * Verifies that a non existent thread cannot be soft-deleted.
	 */
	@Test
	public void RobustTest02() {
		Database db = new Database();
		try {
			db.connectToDatabase();
			db.seedDefaultThread();

			boolean deleted = db.softDeleteDiscussionThreadCascade(999999);
			assertFalse(deleted);

			db.closeConnection();
		} catch (Exception e) {
			db.closeConnection();
			fail("This is a valid test case");
		}
	}

	/*****
	 * Verifies that a soft deleted thread no longer appears in the active thread list.
	 */
	@Test
	public void RobustTest03() {
		Database db = new Database();
		try {
			db.connectToDatabase();
			db.seedDefaultThread();

			int threadId = db.createDiscussionThread("Hidden Deleted Thread");
			db.softDeleteDiscussionThreadCascade(threadId);

			List<DiscussionThread> threads = db.getActiveDiscussionThreads();

			boolean foundDeletedThread = false;
			for (DiscussionThread thread : threads) {
				if (thread.getThreadId() == threadId) {
					foundDeletedThread = true;
					break;
				}
			}

			assertFalse(foundDeletedThread);

			db.closeConnection();
		} catch (Exception e) {
			db.closeConnection();
			fail("This is a valid test case");
		}
	}

	/*****
	 * Verifies that a soft deleted thread cannot be updated again.
	 */
	@Test
	public void RobustTest04() {
		Database db = new Database();
		try {
			db.connectToDatabase();
			db.seedDefaultThread();

			int threadId = db.createDiscussionThread("Will Be Deleted");
			db.softDeleteDiscussionThreadCascade(threadId);

			boolean updated = db.updateDiscussionThreadTitle(threadId, "Should Not Update");
			assertFalse(updated);

			db.closeConnection();
		} catch (Exception e) {
			db.closeConnection();
			fail("This is a valid test case");
		}
	}

	/*****
	 * Verifies that staff can retrieve all threads, including deleted ones,
	 * through the staff specific thread list.
	 */
	@Test
	public void RobustTest05() {
		Database db = new Database();
		try {
			db.connectToDatabase();
			db.seedDefaultThread();

			int threadId = db.createDiscussionThread("Staff Visible Deleted Thread");
			db.softDeleteDiscussionThreadCascade(threadId);

			List<DiscussionThread> allThreads = db.getAllDiscussionThreadsForStaff();

			boolean foundDeletedThread = false;
			for (DiscussionThread thread : allThreads) {
				if (thread.getThreadId() == threadId) {
					foundDeletedThread = true;
					break;
				}
			}

			assertTrue(foundDeletedThread);

			db.closeConnection();
		} catch (Exception e) {
			db.closeConnection();
			fail("This is a valid test case");
		}
	}
}