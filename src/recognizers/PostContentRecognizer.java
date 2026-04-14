package recognizers;

/*******
 * <p> Title: PostContentRecognizer Class </p>
 *
 * <p> Description: A static input validator for discussion post and reply body text.  Consistent
 * with the other recognizers in this package, the single public method returns an empty string
 * when the input is valid and a descriptive, actionable error message when it is not.
 *
 * Rules enforced (in order):
 * <ol>
 *   <li>Content must not be null.</li>
 *   <li>Content must not be an empty string.</li>
 *   <li>Content must contain at least one non-whitespace character (no blank / newline-only
 *       posts).</li>
 *   <li>Content must not exceed {@value #MAX_CONTENT_LENGTH} characters.</li>
 * </ol>
 *
 * This class does NOT perform a JavaFX GUI operation; it only processes its String argument and
 * returns a result string, which makes it safe to call from both the Controller and the
 * Tester. 
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author CSE 360 Team
 *
 * @version 1.00	2026-02-23	Initial version for Phase 2 — Student Discussion System
 */

public class PostContentRecognizer {

	/** Maximum number of characters allowed in a post or reply body. */
	public static final int MAX_CONTENT_LENGTH = 2000;


	/*****
	 * <p> Method: String evaluatePostContent(String content) </p>
	 *
	 * <p> Description: Validates that a post or reply body is acceptable for storage.  The
	 * method applies the four rules described in the class Javadoc in order and returns as soon
	 * as the first violation is detected.  If all rules pass, an empty string is returned to
	 * signal that the content is valid. </p>
	 *
	 * @param content  the post or reply body to validate (may be null)
	 * @return an empty string when the content is valid; a descriptive error message otherwise
	 */
	public static String evaluatePostContent(String content) {

		// Rule 1: null content cannot be stored in the database VARCHAR column
		if (content == null)
			return "Post content must not be null.";

		// Rule 2: empty string carries no information and is not a valid post
		if (content.isEmpty())
			return "Post content must not be empty. Please enter a statement or question.";

		// Rule 3: whitespace-only content (spaces, tabs, newlines) is not meaningful
		if (content.trim().isEmpty())
			return "Post content must contain at least one non-whitespace character.";

		// Rule 4: enforce the maximum character cap so the database column is never overflowed
		if (content.length() > MAX_CONTENT_LENGTH)
			return "Post content must not exceed " + MAX_CONTENT_LENGTH
					+ " characters. Current length: " + content.length() + ".";

		// All rules satisfied — content is valid
		return "";
	}
}
