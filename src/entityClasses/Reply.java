package entityClasses;

import java.time.LocalDateTime;

/*******
 * <p> Title: Reply Class </p>
 *
 * <p> Description: This Reply class represents a reply to a student discussion post in the
 * Student Discussion System.  Every Reply is attached to exactly one Post (identified by postId).
 *
 * Replies support the same soft-delete pattern as Posts.  When a student deletes their own
 * reply, isDeleted is set to TRUE in the database; the row is never physically removed.  The
 * UI shows a "[Reply deleted]" notice in place of the original content.  When the parent post
 * is soft-deleted, replies (including deleted ones) remain visible so the conversation thread
 * is not broken. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author CSE 360 Team
 *
 * @version 1.00	2026-02-23	Initial version for Phase 2 — Student Discussion System
 */

public class Reply {

	/*-*******************************************************************************************

	Attributes

	 */

	// replyId is assigned by the database after insertion; it is 0 until createReply() returns.
	private int           replyId;
	private int           postId;			// FK → Posts.postId
	private String        authorUsername;	// FK → userDB.userName
	private String        content;			// Body text of the reply
	private LocalDateTime timestamp;		// When the reply was submitted
	private boolean       isDeleted;		// TRUE after a student soft-deletes the reply


	/*-*******************************************************************************************

	Constructors

	 */

	/*****
	 * <p> Method: Reply() </p>
	 *
	 * <p> Description: Default constructor — not used directly, required by the framework. </p>
	 */
	public Reply() { }


	/*****
	 * <p> Method: Reply(int postId, String authorUsername, String content,
	 *                    LocalDateTime timestamp) </p>
	 *
	 * <p> Description: Constructor used when creating a new reply before it is saved to the
	 * database.  The replyId field is intentionally left at 0; it is populated via
	 * {@code setReplyId()} once the database auto-increment value is retrieved. </p>
	 *
	 * @param postId         the post this reply is responding to
	 * @param authorUsername the username of the student writing the reply
	 * @param content        the body text of the reply
	 * @param timestamp      the moment the reply was submitted
	 */
	public Reply(int postId, String authorUsername, String content, LocalDateTime timestamp) {
		this.postId         = postId;
		this.authorUsername = authorUsername;
		this.content        = content;
		this.timestamp      = timestamp;
	}


	/*****
	 * <p> Method: Reply(int replyId, int postId, String authorUsername, String content,
	 *                    LocalDateTime timestamp, boolean isDeleted) </p>
	 *
	 * <p> Description: Full constructor used when loading a Reply from a database ResultSet.
	 * All six fields are supplied directly from the query result. </p>
	 *
	 * @param replyId        the database-generated primary key
	 * @param postId         the post this reply is attached to
	 * @param authorUsername the author's username
	 * @param content        the body text
	 * @param timestamp      when the reply was created
	 * @param isDeleted      true if the student has soft-deleted this reply
	 */
	public Reply(int replyId, int postId, String authorUsername, String content,
			LocalDateTime timestamp, boolean isDeleted) {
		this.replyId        = replyId;
		this.postId         = postId;
		this.authorUsername = authorUsername;
		this.content        = content;
		this.timestamp      = timestamp;
		this.isDeleted      = isDeleted;
	}


	/*-*******************************************************************************************

	Getters and Setters

	 */

	/*****
	 * <p> Method: int getReplyId() </p>
	 * <p> Description: Returns the database-assigned primary key. </p>
	 * @return the replyId (0 if the reply has not yet been inserted)
	 */
	public int getReplyId() { return replyId; }


	/*****
	 * <p> Method: void setReplyId(int replyId) </p>
	 * <p> Description: Sets the auto-generated replyId after the database INSERT returns the
	 * generated key. </p>
	 * @param replyId the auto-generated ID from the database
	 */
	public void setReplyId(int replyId) { this.replyId = replyId; }


	/*****
	 * <p> Method: int getPostId() </p>
	 * <p> Description: Returns the postId this reply is attached to. </p>
	 * @return the postId
	 */
	public int getPostId() { return postId; }

	/*****
	 * <p> Method: void setPostId() </p>
	 * <p> Description: sets the postId this reply is attached to. </p>
	 * @return the postId
	 */
	public void setPostId(int postId) { this.postId = postId; }
	

	/*****
	 * <p> Method: String getAuthorUsername() </p>
	 * <p> Description: Returns the username of the student who wrote this reply. </p>
	 * @return the author's username
	 */
	public String getAuthorUsername() { return authorUsername; }

	/*****
	 * <p> Method: void getAuthorUsername() </p>
	 * <p> Description: Returns the username of the student who wrote this reply. </p>
	 * param the author's username
	 */
	public void getAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }

	/*****
	 * <p> Method: String getContent() </p>
	 * <p> Description: Returns the body text of this reply. </p>
	 * @return the content string
	 */
	public String getContent() { return content; }
	
	/*****
	 * <p> Method: void getContent() </p>
	 * <p> Description: sets the body text of this reply. </p>
	 * @param the content string
	 */
	public void setContent(String content) { this.content = content; }


	/*****
	 * <p> Method: LocalDateTime getTimestamp() </p>
	 * <p> Description: Returns the moment this reply was created. </p>
	 * @return the creation timestamp
	 */
	public LocalDateTime getTimestamp() { return timestamp; }

	/*****
	 * <p> Method: void getTimestamp() </p>
	 * <p> Description: sets the moment this reply was created. </p>
	 * @param the creation timestamp
	 */
	public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

	/*****
	 * <p> Method: boolean isDeleted() </p>
	 * <p> Description: Returns true if the student has soft-deleted this reply. </p>
	 * @return true when the reply is soft-deleted
	 */
	public boolean isDeleted() { return isDeleted; }


	/*****
	 * <p> Method: void setDeleted(boolean deleted) </p>
	 * <p> Description: Marks this reply as soft-deleted (true) or restores it (false). </p>
	 * @param deleted the new soft-delete state
	 */
	public void setDeleted(boolean deleted) { this.isDeleted = deleted; }
}
