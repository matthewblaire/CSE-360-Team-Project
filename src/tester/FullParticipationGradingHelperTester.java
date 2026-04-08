package tester;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import database.ParticipationGradingHelper;
import entityClasses.Post;
import entityClasses.Reply;

/*******
 * <p> Title: FullParticipationGradingHelperTester Class </p>
 *
 * <p> Description: This JUnit test class verifies the grading support prototype in
 * ParticipationGradingHelper. The tests focus on the logic behind determining
 * whether a selected student has replied to at least three different student authored
 * posts. Positive and negative cases are both included so the grading threshold and
 * unique post counting behavior are demonstrated clearly. </p>
 *
 * @author Saam Kavusi
 *
 * @version 1.00 2026-03-30 
 */

public class FullParticipationGradingHelperTester {
	
	 /*****
	 * Verifies that a selected student with no replies receives a count of 0.
	 */
	@Test
	public void NormalTest01() {
		try {
			List<Post> posts = new ArrayList<>();
			List<Reply> replies = new ArrayList<>();

			posts.add(new Post(1, 1, "studentA", "Title 1", "Post 1", LocalDateTime.now(), false));
			posts.add(new Post(2, 1, "studentB", "Title 2", "Post 2", LocalDateTime.now(), false));
			
			// No replies exist, so the selected student should receive no participation credit.
			assertEquals(0,
					ParticipationGradingHelper.studentPostsReplyCount("selectedStudent", posts, replies));
		} catch (Exception e) {
			fail("This is a valid test case");
		}
	}

	 /*****
	 * Verifies that replies to two different student posts produce a count of 2.
	 */
	@Test
	public void NormalTest02() {
		try {
			List<Post> posts = new ArrayList<>();
			List<Reply> replies = new ArrayList<>();

			posts.add(new Post(1, 1, "studentA", "Title 1", "Post 1", LocalDateTime.now(), false));
			posts.add(new Post(2, 1, "studentB", "Title 2", "Post 2", LocalDateTime.now(), false));

			replies.add(new Reply(1, 1, "selectedStudent", "Reply to post 1", LocalDateTime.now(), false));
			replies.add(new Reply(2, 2, "selectedStudent", "Reply to post 2", LocalDateTime.now(), false));
			
			// Two replies to two different student-authored posts should count as 2.
			assertEquals(2,
					ParticipationGradingHelper.studentPostsReplyCount("selectedStudent", posts, replies));
		} catch (Exception e) {
			fail("This is a valid test case");
		}
	}

	 /*****
	 * Verifies that replies to three different student posts produce a count of 3.
	 */
	@Test
	public void NormalTest03() {
		try {
			List<Post> posts = new ArrayList<>();
			List<Reply> replies = new ArrayList<>();

			posts.add(new Post(1, 1, "studentA", "Title 1", "Post 1", LocalDateTime.now(), false));
			posts.add(new Post(2, 1, "studentB", "Title 2", "Post 2", LocalDateTime.now(), false));
			posts.add(new Post(3, 1, "studentC", "Title 3", "Post 3", LocalDateTime.now(), false));

			replies.add(new Reply(1, 1, "selectedStudent", "Reply to post 1", LocalDateTime.now(), false));
			replies.add(new Reply(2, 2, "selectedStudent", "Reply to post 2", LocalDateTime.now(), false));
			replies.add(new Reply(3, 3, "selectedStudent", "Reply to post 3", LocalDateTime.now(), false));

			// This is the exact threshold case for the grading rule.
			assertEquals(3,
					ParticipationGradingHelper.studentPostsReplyCount("selectedStudent", posts, replies));
		} catch (Exception e) {
			fail("This is a valid test case");
		}
	}

	 /*****
	 * Verifies that multiple replies to the same post count only once.
	 */
	@Test
	public void NormalTest04() {
		try {
			List<Post> posts = new ArrayList<>();
			List<Reply> replies = new ArrayList<>();

			posts.add(new Post(1, 1, "studentA", "Title 1", "Post 1", LocalDateTime.now(), false));

			replies.add(new Reply(1, 1, "selectedStudent", "First reply to same post", LocalDateTime.now(), false));
			replies.add(new Reply(2, 1, "selectedStudent", "Second reply to same post", LocalDateTime.now(), false));

			// The selected student replied twice to the same post, so only one unique post counts.
			assertEquals(1,
					ParticipationGradingHelper.studentPostsReplyCount("selectedStudent", posts, replies));
		} catch (Exception e) {
			fail("This is a valid test case");
		}
	}

	 /*****
	 * Verifies that replying to the student's own post does not count toward the total.
	 */
	@Test
	public void NormalTest05() {
		try {
			List<Post> posts = new ArrayList<>();
			List<Reply> replies = new ArrayList<>();

			posts.add(new Post(1, 1, "selectedStudent", "Own Title", "Own Post", LocalDateTime.now(), false));
			posts.add(new Post(2, 1, "studentB", "Title 2", "Post 2", LocalDateTime.now(), false));

			replies.add(new Reply(1, 1, "selectedStudent", "Reply to own post", LocalDateTime.now(), false));
			replies.add(new Reply(2, 2, "selectedStudent", "Reply to other student's post", LocalDateTime.now(), false));

			// Only the reply to another student's post should count toward participation credit.
			assertEquals(1,
					ParticipationGradingHelper.studentPostsReplyCount("selectedStudent", posts, replies));
		} catch (Exception e) {
			fail("This is a valid test case");
		}
	}

	 /*****
	 * Verifies that zero qualifying replies does not satisfy the minimum threshold.
	 */
	@Test
	public void RobustTest01() {
		try {
			List<Post> posts = new ArrayList<>();
			List<Reply> replies = new ArrayList<>();

			posts.add(new Post(1, 1, "studentA", "Title 1", "Post 1", LocalDateTime.now(), false));
			posts.add(new Post(2, 1, "studentB", "Title 2", "Post 2", LocalDateTime.now(), false));

			// With zero qualifying posts replied to, the threshold must not be met.
			assertFalse(
					ParticipationGradingHelper.studentPostsReplyCountCompleted("selectedStudent", posts, replies));
		} catch (Exception e) {
			fail("This is a valid test case");
		}
	}

	 /*****
	 * Verifies that two qualifying replies does not satisfy the minimum threshold.
	 */
	@Test
	public void RobustTest02() {
		try {
			List<Post> posts = new ArrayList<>();
			List<Reply> replies = new ArrayList<>();

			posts.add(new Post(1, 1, "studentA", "Title 1", "Post 1", LocalDateTime.now(), false));
			posts.add(new Post(2, 1, "studentB", "Title 2", "Post 2", LocalDateTime.now(), false));

			replies.add(new Reply(1, 1, "selectedStudent", "Reply to post 1", LocalDateTime.now(), false));
			replies.add(new Reply(2, 2, "selectedStudent", "Reply to post 2", LocalDateTime.now(), false));

			// Two is below the required minimum of three different student posts.
			assertFalse(
					ParticipationGradingHelper.studentPostsReplyCountCompleted("selectedStudent", posts, replies));
		} catch (Exception e) {
			fail("This is a valid test case");
		}
	}

	 /*****
	 * Verifies that three qualifying replies satisfies the minimum threshold.
	 */
	@Test
	public void RobustTest03() {
		try {
			List<Post> posts = new ArrayList<>();
			List<Reply> replies = new ArrayList<>();

			posts.add(new Post(1, 1, "studentA", "Title 1", "Post 1", LocalDateTime.now(), false));
			posts.add(new Post(2, 1, "studentB", "Title 2", "Post 2", LocalDateTime.now(), false));
			posts.add(new Post(3, 1, "studentC", "Title 3", "Post 3", LocalDateTime.now(), false));

			replies.add(new Reply(1, 1, "selectedStudent", "Reply to post 1", LocalDateTime.now(), false));
			replies.add(new Reply(2, 2, "selectedStudent", "Reply to post 2", LocalDateTime.now(), false));
			replies.add(new Reply(3, 3, "selectedStudent", "Reply to post 3", LocalDateTime.now(), false));

			// Three different student posts is the exact threshold for completion.
			assertTrue(
					ParticipationGradingHelper.studentPostsReplyCountCompleted("selectedStudent", posts, replies));
		} catch (Exception e) {
			fail("This is a valid test case");
		}
	}

	 /*****
	 * Verifies that more than three qualifying replies still satisfies the minimum threshold.
	 */
	@Test
	public void RobustTest04() {
		try {
			List<Post> posts = new ArrayList<>();
			List<Reply> replies = new ArrayList<>();

			posts.add(new Post(1, 1, "studentA", "Title 1", "Post 1", LocalDateTime.now(), false));
			posts.add(new Post(2, 1, "studentB", "Title 2", "Post 2", LocalDateTime.now(), false));
			posts.add(new Post(3, 1, "studentC", "Title 3", "Post 3", LocalDateTime.now(), false));
			posts.add(new Post(4, 1, "studentD", "Title 4", "Post 4", LocalDateTime.now(), false));

			replies.add(new Reply(1, 1, "selectedStudent", "Reply to post 1", LocalDateTime.now(), false));
			replies.add(new Reply(2, 2, "selectedStudent", "Reply to post 2", LocalDateTime.now(), false));
			replies.add(new Reply(3, 3, "selectedStudent", "Reply to post 3", LocalDateTime.now(), false));
			replies.add(new Reply(4, 4, "selectedStudent", "Reply to post 4", LocalDateTime.now(), false));

			// Any value above the threshold should still return true.
			assertTrue(
					ParticipationGradingHelper.studentPostsReplyCountCompleted("selectedStudent", posts, replies));
		} catch (Exception e) {
			fail("This is a valid test case");
		}
	}

	 /*****
	 * Verifies that a deleted reply does not count toward the minimum threshold.
	 */
	@Test 
	public void RobustTest05() {
		try {
			List<Post> posts = new ArrayList<>();
			List<Reply> replies = new ArrayList<>();

			posts.add(new Post(1, 1, "studentA", "Title 1", "Post 1", LocalDateTime.now(), false));
			posts.add(new Post(2, 1, "studentB", "Title 2", "Post 2", LocalDateTime.now(), false));
			posts.add(new Post(3, 1, "studentC", "Title 3", "Post 3", LocalDateTime.now(), false));

			replies.add(new Reply(1, 1, "selectedStudent", "Reply to post 1", LocalDateTime.now(), false));
			replies.add(new Reply(2, 2, "selectedStudent", "Reply to post 2", LocalDateTime.now(), false));
			replies.add(new Reply(3, 3, "selectedStudent", "Deleted reply to post 3", LocalDateTime.now(), true));

			// One reply is deleted, so only two qualifying posts remain and the threshold is not met.
			assertFalse(
					ParticipationGradingHelper.studentPostsReplyCountCompleted("selectedStudent", posts, replies));
		} catch (Exception e) {
			fail("This is a valid test case");
		}
	}
	
	 /*****
	 * Verifies that missing input lists and username are handled safely by returning 0.
	 */
	@Test
	public void RobustTest06() {
		try {
			// Null inputs should not crash the prototype; they should safely return 0.
			assertEquals(0,
					ParticipationGradingHelper.studentPostsReplyCount(null, null, null));
		} catch (Exception e) {
			fail("This is a valid test case");
		}
	}
}