package entityClasses;

import java.time.LocalDateTime;

/**
 * Entity class for private messages
 */
public class PrivateMessage {
	public int messageId = -1;
	public int replyId = -1;
	public int postId = -1;
	public String senderUsername = "";
	public String recipientUsername = "";
	public String content = "";
	public LocalDateTime timestamp = LocalDateTime.now();
	public Boolean isDeleted = false;
	
	/**
	 * Unused default constructor
	 */
	public PrivateMessage() {} 
	
	/**
	 * Constructor for messages that have not yet been added to the database
	 * @param sender The sender of this message
	 * @param recipient The recipient of this message
	 * @param content The content of this message
	 * @param replyId The relevant reply for this message (if applicable, otherwise use -1)
	 * @param postId The relevant post for this message (if applicable, otherwise use -1)
	 */
	public PrivateMessage(String sender, String recipient, String content, int replyId, int postId) {
		this.senderUsername = sender;
		this.recipientUsername = recipient;
		this.content = content;
		this.replyId = replyId;
		this.postId = postId;
	}
	
	/**
	 * Constructor for messages that have already been added to the database
	 * @param sender The sender of this message
	 * @param recipient The recipient of this message
	 * @param content The content of this message
	 * @param replyId The relevant reply for this message (if applicable, otherwise use -1)
	 * @param postId The relevant post for this message (if applicable, otherwise use -1)
	 * @param messageId The ID of this message
	 */
	public PrivateMessage(String sender, String recipient, String content, int replyId, int postId, int messageId, LocalDateTime timestamp, Boolean isDeleted) {
		this.senderUsername = sender;
		this.recipientUsername = recipient;
		this.content = content;
		this.replyId = replyId;
		this.postId = postId;
		this.messageId = messageId;
		this.timestamp = timestamp;
		this.isDeleted = isDeleted;
	}
	
	
}
