package entityClasses;

import java.time.LocalDateTime;

/*******
 * <p> Title: ReadStatus Class. </p>
 *
 * <p> Description: Entity class that records when a specific user has read a specific post or
 * reply.  A row exists in the ReadStatus table for each (username, targetId, targetType) triple
 * that represents a read event.  The presence of a row means the item has been read; the
 * absence of a row means it is unread.
 *
 * targetType values:
 * <ul>
 *   <li>{@code "post"}  — the targetId refers to a row in the Posts table.</li>
 *   <li>{@code "reply"} — the targetId refers to a row in the Replies table.</li>
 * </ul>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author CSE 360 Team
 *
 * @version 1.00	2026-02-23	Initial version for Phase 2 — Student Discussion System
 */
public class ReadStatus {

	/*-*******************************************************************************************

	Attributes

	 */

	/** Auto-generated primary key from the ReadStatus table. */
	private int id;

	/** The username of the user who read this item. */
	private String username;

	/** The primary key of the item that was read (postId or replyId). */
	private int targetId;

	/** Discriminator: {@code "post"} or {@code "reply"}. */
	private String targetType;

	/** The timestamp when the item was first marked as read. */
	private LocalDateTime readAt;


	/*-*******************************************************************************************

	Constructors

	 */

	/**
	 * Default no-arg constructor.
	 */
	public ReadStatus() {
	}

	/**
	 * Full constructor used when loading a row from the database.
	 *
	 * @param id         the auto-generated primary key
	 * @param username   the user who read the item
	 * @param targetId   the postId or replyId that was read
	 * @param targetType {@code "post"} or {@code "reply"}
	 * @param readAt     the timestamp the item was marked as read
	 */
	public ReadStatus(int id, String username, int targetId, String targetType,
			LocalDateTime readAt) {
		this.id         = id;
		this.username   = username;
		this.targetId   = targetId;
		this.targetType = targetType;
		this.readAt     = readAt;
	}

	/**
	 * Convenience constructor for creating a new ReadStatus record (id not yet known).
	 *
	 * @param username   the user who read the item
	 * @param targetId   the postId or replyId that was read
	 * @param targetType {@code "post"} or {@code "reply"}
	 * @param readAt     the timestamp the item was marked as read
	 */
	public ReadStatus(String username, int targetId, String targetType,
			LocalDateTime readAt) {
		this(0, username, targetId, targetType, readAt);
	}


	/*-*******************************************************************************************

	Getters and Setters

	 */

	/** @return the auto-generated primary key */
	public int getId()           { return id; }

	/** @param id the primary key to set */
	public void setId(int id)    { this.id = id; }

	/** @return the username of the reader */
	public String getUsername()            { return username; }

	/** @param username the username to set */
	public void setUsername(String username) { this.username = username; }

	/** @return the postId or replyId that was read */
	public int getTargetId()             { return targetId; }

	/** @param targetId the target ID to set */
	public void setTargetId(int targetId)  { this.targetId = targetId; }

	/** @return {@code "post"} or {@code "reply"} */
	public String getTargetType()              { return targetType; }

	/** @param targetType the target type to set */
	public void setTargetType(String targetType) { this.targetType = targetType; }

	/** @return the timestamp when the item was first marked as read */
	public LocalDateTime getReadAt()               { return readAt; }

	/** @param readAt the read-at timestamp to set */
	public void setReadAt(LocalDateTime readAt)    { this.readAt = readAt; }
}
