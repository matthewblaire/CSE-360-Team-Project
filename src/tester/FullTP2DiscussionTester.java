package tester;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import database.Database;
import entityClasses.Post;
import entityClasses.User;
import recognizers.PostContentRecognizer;

/*******
 * <p> Title: FullTP2DiscussionTester Class </p>
 *
 * <p> Description: This JUnit test class verifies key TP2 discussion-system behavior
 * related to the two assigned weaknesses for HW3 Task 2:
 * Improper Input Validation and Incorrect Authorization.
 *
 * The tests focus on:
 * 1) post/reply body validation through PostContentRecognizer, and
 * 2) selected database behaviors that support post creation and role management.
 *
 * These tests are intentionally small and independent so they clearly demonstrate
 * positive, negative, boundary, and coverage-oriented cases. </p>
 *
 * @author Saam Kavusi
 *
 * @version 1.00 2026-04-03
 */
public class FullTP2DiscussionTester {

	// -----------------------------
	// Improper Input Validation
	// -----------------------------

	// Verifies that normal non-empty content is accepted.
	@Test
	public void NormalTest01() {
		try {
			String result = PostContentRecognizer.evaluatePostContent(
					"Hello, I have a question about the homework.");
			assertEquals("", result);
		} catch (Exception e) {
			fail("This is a valid test case");
		}
	}

	// Verifies that minimum non-empty content is accepted.
	@Test
	public void NormalTest02() {
		try {
			String result = PostContentRecognizer.evaluatePostContent("A");
			assertEquals("", result);
		} catch (Exception e) {
			fail("This is a valid test case");
		}
	}

	// Verifies that null content is rejected.
	@Test
	public void RobustTest01() {
		try {
			String result = PostContentRecognizer.evaluatePostContent(null);
			assertFalse(result.isEmpty());
		} catch (Exception e) {
			fail("This is a valid test case");
		}
	}

	// Verifies that empty-string content is rejected.
	@Test
	public void RobustTest02() {
		try {
			String result = PostContentRecognizer.evaluatePostContent("");
			assertFalse(result.isEmpty());
		} catch (Exception e) {
			fail("This is a valid test case");
		}
	}

	// Verifies that whitespace-only content is rejected.
	@Test
	public void RobustTest03() {
		try {
			String result = PostContentRecognizer.evaluatePostContent("   \n\t  ");
			assertFalse(result.isEmpty());
		} catch (Exception e) {
			fail("This is a valid test case");
		}
	}

	// Verifies that content at the maximum allowed boundary is accepted.
	@Test
	public void RobustTest04() {
		try {
			String content = generateString(PostContentRecognizer.MAX_CONTENT_LENGTH);
			String result = PostContentRecognizer.evaluatePostContent(content);
			assertEquals("", result);
		} catch (Exception e) {
			fail("This is a valid test case");
		}
	}

	// Verifies that content beyond the maximum allowed boundary is rejected.
	@Test
	public void RobustTest05() {
		try {
			String content = generateString(PostContentRecognizer.MAX_CONTENT_LENGTH + 1);
			String result = PostContentRecognizer.evaluatePostContent(content);
			assertFalse(result.isEmpty());
		} catch (Exception e) {
			fail("This is a valid test case");
		}
	}

	// -----------------------------
	// Database / Authorization Support
	// -----------------------------

	// Verifies that the seeded General thread exists.
	@Test
	public void NormalTest03() {
		Database db = new Database();
		try {
			db.connectToDatabase();
			db.seedDefaultThread();

			assertTrue(db.doesThreadExist(1));
			db.closeConnection();
		} catch (Exception e) {
			db.closeConnection();
			fail("This is a valid test case");
		}
	}

	// Verifies that a valid post can be created in an existing thread.
	@Test
	public void NormalTest04() {
		Database db = new Database();
		try {
			db.connectToDatabase();
			db.seedDefaultThread();

			Post post = new Post(1, "student1", "Test Title",
					"This is a valid post.", LocalDateTime.now());

			int postId = db.createPost(post);

			assertTrue(postId > 0);
			assertTrue(db.doesPostExist(postId));

			db.closeConnection();
		} catch (Exception e) {
			db.closeConnection();
			fail("This is a valid test case");
		}
	}

	// Verifies that creating a post in a non-existent thread is rejected.
	@Test
	public void RobustTest06() {
		Database db = new Database();
		try {
			db.connectToDatabase();
			db.seedDefaultThread();

			Post badPost = new Post(9999, "student1", "Bad Title",
					"This should fail.", LocalDateTime.now());

			assertThrows(SQLException.class, () -> db.createPost(badPost));

			db.closeConnection();
		} catch (Exception e) {
			db.closeConnection();
			fail("This is a valid test case");
		}
	}

	// Verifies that a valid role update succeeds.
	@Test
	public void NormalTest05() {
		Database db = new Database();
		try {
			db.connectToDatabase();

			User user = new User("student1", "Password1!", "First", "M", "Last",
					"Pref", "student1@asu.edu", false, true, false);
			db.register(user);

			assertTrue(db.updateUserRole("student1", "Staff", "true"));

			db.closeConnection();
		} catch (Exception e) {
			db.closeConnection();
			fail("This is a valid test case");
		}
	}

	// Verifies that an invalid role string is rejected.
	@Test
	public void RobustTest07() {
		Database db = new Database();
		try {
			db.connectToDatabase();

			User user = new User("student2", "Password1!", "First", "M", "Last",
					"Pref", "student2@asu.edu", false, true, false);
			db.register(user);

			assertFalse(db.updateUserRole("student2", "InvalidRole", "true"));

			db.closeConnection();
		} catch (Exception e) {
			db.closeConnection();
			fail("This is a valid test case");
		}
	}

	// Verifies the admin-count boundary when exactly one admin exists.
	@Test
	public void RobustTest08() {
		Database db = new Database();
		try {
			db.connectToDatabase();

			User admin = new User("admin1", "Password1!", "Admin", "M", "One",
					"Admin", "admin1@asu.edu", true, false, false);
			db.register(admin);

			assertEquals(1, db.getNumAdmins());

			db.closeConnection();
		} catch (Exception e) {
			db.closeConnection();
			fail("This is a valid test case");
		}
	}

	/**
	 * Generates a string of exactly {@code length} 'x' characters.
	 *
	 * @param length desired string length
	 * @return string of repeated 'x' characters
	 */
	private String generateString(int length) {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) sb.append('x');
		return sb.toString();
	}
}