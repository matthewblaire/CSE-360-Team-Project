package database;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import entityClasses.Post;
import entityClasses.Reply;

/*******
 * <p> Title: ParticipationGradingHelper Class </p>
 *
 * <p> Description: This helper class supports the staff grading support prototype.
 * It provides logic to determine how many different student posts a
 * selected student has replied to, and if the selected student has met the
 * minimum requirement of replying to at least three different student posts. </p>
 *
 * <p> This prototype focuses on the grading rule: multiple replies to the same post must 
 * count only once, replies to the student's own posts must not count, and deleted replies 
 * should not contribute toward grading credit. Keeping this logic in a helper class 
 * makes it easier to test and later integrate into a staff grading view. </p>
 *
 * @author CSE 360 Team
 * 
 * @version 1.0 2026-03-30
 */
public class ParticipationGradingHelper {

	/*****
	 * <p> Method: studentPostsReplyCount(...) </p>
	 *
	 * <p> Description: Counts how many different student authored posts were replied to
	 * by the selected student. A post counts only once even if the student wrote multiple
	 * replies to that same post. Replies marked as deleted are ignored. Replies to the
	 * student's own posts do not count toward the total. </p>
	 *
	 * @param selectedStudentUsername the student being evaluated
	 * @param posts the list of posts available for review
	 * @param replies the list of replies available for review
	 * @return the number of different student posts replied to by the selected student
	 */
	public static int studentPostsReplyCount( String selectedStudentUsername, List<Post> posts, List<Reply> replies) 
	{
		// Defensive guard: return 0 when required inputs are missing so the grading
		// helper fails safely and predictably instead of throwing an exception.
		if (selectedStudentUsername == null || posts == null || replies == null) {
			return 0;
		}

		// Build a quick lookup table from postId to Post so each reply can find
		// its original parent post efficiently.
		Map<Integer, Post> postMap = new HashMap<>();
		for (Post post : posts) {
			if (post != null) {
				postMap.put(post.getPostId(), post);
			}
		}

		// Use a Set so repeated replies to the same post still produce only one
		// unit of grading credit for that post.
		Set<Integer> uniquePostIdsRepliedTo = new HashSet<>();

		for (Reply reply : replies) {
			if (reply == null) continue;

			// Only replies written by the selected student are relevant.
			if (!selectedStudentUsername.equals(reply.getAuthorUsername())) continue;

			// Deleted replies should not contribute to grading credit.
			if (reply.isDeleted()) continue;

			// Find the original post this reply belongs to.
			Post parentPost = postMap.get(reply.getPostId());
			if (parentPost == null) continue;

			// Self-replies do not count because the grading rule is specifically about
			// responding to posts written by other students.
			if (selectedStudentUsername.equals(parentPost.getAuthorUsername())) continue;

			uniquePostIdsRepliedTo.add(parentPost.getPostId());
		}

		return uniquePostIdsRepliedTo.size();
	}

	/*****
	 * <p> Method: studentPostsReplyCountCompleted(...) </p>
	 *
	 * <p> Description: Determines whether the selected student has met the requirement
	 * of replying to at least three different student posts. This method
	 * reuses studentPostsReplyCount(...) so the threshold decision is based on the same
	 * counting logic used throughout the prototype. </p>
	 *
	 * @param selectedStudentUsername the student being evaluated
	 * @param posts the list of posts available for review
	 * @param replies the list of replies available for review
	 * @return true if the selected student replied to at least three different student posts,
	 *         otherwise false
	 */
	public static boolean studentPostsReplyCountCompleted(String selectedStudentUsername, List<Post> posts, List<Reply> replies) 
	{
		// Reusing the counting method here so the threshold rule stays in one place
		return studentPostsReplyCount(selectedStudentUsername, posts, replies) >= 3;
	}
}