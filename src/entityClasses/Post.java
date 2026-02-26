package entityClasses;

import java.time.LocalDateTime;

/*******
 * <p> Title: Post Class </p>
 *
 * <p> Description: This Post class represents a single student discussion post in the Student
 * Discussion System.  A post belongs to exactly one DiscussionThread, has an author identified
 * by their username, carries the post body text, records the creation timestamp, and carries a
 * soft-delete flag.
 *
 * When a student deletes their own post, isDeleted is set to TRUE in the database; the row is
 * never physically removed.  Replies attached to a deleted post remain visible, but the UI shows
 * a notice that the original post has been removed instead of the original content. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author CSE 360 Team
 *
 * @version 1.00	2026-02-23	Initial version for Phase 2 — Student Discussion System
 */

public class Post {

	/*-*******************************************************************************************

	Attributes

	 */

	// postId is assigned by the database after insertion; it is 0 until createPost() returns.
	private int           postId;
	private int           threadId;		// FK → DiscussionThreads.threadId
	private String        authorUsername;	// FK → userDB.userName
	private String        content;			// Body text of the post
	private LocalDateTime timestamp;		// When the post was created
	private boolean       isDeleted;		// TRUE after a student soft-deletes the post


	/*-*******************************************************************************************

	Constructors

	 */

	/*****
	 * <p> Method: Post() </p>
	 *
	 * <p> Description: Default constructor — not used directly, required by the framework. </p>
	 */
	public Post() { }


	/*****
	 * <p> Method: Post(int threadId, String authorUsername, String content,
	 *                   LocalDateTime timestamp) </p>
	 *
	 * <p> Description: Constructor used when creating a brand-new post before it is saved to the
	 * database.  The postId field is intentionally left at 0; it is populated via
	 * {@code setPostId()} once the database auto-increment value is retrieved.  isDeleted
	 * defaults to false because a newly created post is never deleted. </p>
	 *
	 * @param threadId       the thread this post is being submitted to
	 * @param authorUsername the username of the student authoring the post
	 * @param content        the body text of the post
	 * @param timestamp      the moment the post was submitted
	 */
	public Post(int threadId, String authorUsername, String content, LocalDateTime timestamp) {
		this.threadId       = threadId;
		this.authorUsername = authorUsername;
		this.content        = content;
		this.timestamp      = timestamp;
		this.isDeleted      = false;	// new posts are never pre-deleted
	}


	/*****
	 * <p> Method: Post(int postId, int threadId, String authorUsername, String content,
	 *                   LocalDateTime timestamp, boolean isDeleted) </p>
	 *
	 * <p> Description: Full constructor used when loading a Post from a database ResultSet.
	 * All six fields are supplied directly from the query result. </p>
	 *
	 * @param postId         the database-generated primary key
	 * @param threadId       the thread this post belongs to
	 * @param authorUsername the author's username
	 * @param content        the body text
	 * @param timestamp      when the post was created
	 * @param isDeleted      true if the student has soft-deleted this post
	 */
	public Post(int postId, int threadId, String authorUsername, String content,
			LocalDateTime timestamp, boolean isDeleted) {
		this.postId         = postId;
		this.threadId       = threadId;
		this.authorUsername = authorUsername;
		this.content        = content;
		this.timestamp      = timestamp;
		this.isDeleted      = isDeleted;
	}


	/*-*******************************************************************************************

	Getters and Setters

	 */

	/*****
	 * <p> Method: int getPostId() </p>
	 * <p> Description: Returns the database-assigned primary key. </p>
	 * @return the postId (0 if the post has not yet been inserted)
	 */
	public int getPostId() { return postId; }


	/*****
	 * <p> Method: void setPostId(int postId) </p>
	 * <p> Description: Sets the auto-generated postId after the database INSERT returns the
	 * generated key. </p>
	 * @param postId the auto-generated ID from the database
	 */
	public void setPostId(int postId) { this.postId = postId; }


	/*****
	 * <p> Method: int getThreadId() </p>
	 * <p> Description: Returns the threadId this post belongs to. </p>
	 * @return the threadId
	 */
	public int getThreadId() { return threadId; }


	/*****
	 * <p> Method: String getAuthorUsername() </p>
	 * <p> Description: Returns the username of the student who wrote this post. </p>
	 * @return the author's username
	 */
	public String getAuthorUsername() { return authorUsername; }


	/*****
	 * <p> Method: String getContent() </p>
	 * <p> Description: Returns the body text of this post. </p>
	 * @return the content string
	 */
	public String getContent() { return content; }


	/*****
	 * <p> Method: LocalDateTime getTimestamp() </p>
	 * <p> Description: Returns the moment this post was created. </p>
	 * @return the creation timestamp
	 */
	public LocalDateTime getTimestamp() { return timestamp; }


	/*****
	 * <p> Method: boolean isDeleted() </p>
	 * <p> Description: Returns true if the student has soft-deleted this post. </p>
	 * @return true when the post is soft-deleted
	 */
	public boolean isDeleted() { return isDeleted; }


	/*****
	 * <p> Method: void setDeleted(boolean deleted) </p>
	 * <p> Description: Marks this post as soft-deleted (true) or restores it (false). </p>
	 * @param deleted the new soft-delete state
	 */
	public void setDeleted(boolean deleted) { this.isDeleted = deleted; }
}
