package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import entityClasses.DiscussionThread;
import entityClasses.Post;
import entityClasses.PrivateMessage;
import entityClasses.Reply;
import entityClasses.User;
import entityClasses.Request;

import java.time.LocalDateTime;

/*******
 * <p> Title: Database Class. </p>
 * 
 * <p> Description: This is an in-memory database built on H2.  Detailed documentation of H2 can
 * be found at https://www.h2database.com/html/main.html (Click on "PDF (2MP) for a PDF of 438 pages
 * on the H2 main page.)  This class leverages H2 and provides numerous special supporting methods.
 * </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 2.00		2025-04-29 Updated and expanded from the version produce by on a previous
 * 							version by Pravalika Mukkiri and Ishwarya Hidkimath Basavaraj
 * @version 2.01		2025-12-17 Minor updates for Spring 2026
 */

/*
 * The Database class is responsible for establishing and managing the connection to the database,
 * and performing operations such as user registration, login validation, handling invitation 
 * codes, and numerous other database related functions.
 */
public class Database {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	//  Shared variables used within this class
	private Connection connection = null;		// Singleton to access the database 
	private Statement statement = null;			// The H2 Statement is used to construct queries
	
	// These are the easily accessible attributes of the currently logged-in user
	// This is only useful for single user applications
	private String currentUsername;
	private String currentPassword;
	private String currentFirstName;
	private String currentMiddleName;
	private String currentLastName;
	private String currentPreferredFirstName;
	private String currentEmailAddress;
	private boolean currentAdminRole;
	private boolean currentStudentRole;
	private boolean currentStaffRole;
    private String currentOneTimePassword;

	/*******
	 * <p> Method: Database </p>
	 * 
	 * <p> Description: The default constructor used to establish this singleton object.</p>
	 * 
	 */
	
	public Database () {
		
	}
	
	
	
	/**
	 * Method: dropAllObjects
	 * 
	 * Description: Drops all objects in the database for testing purposes.
	 */
	public void dropAllObjects() {
		try {
			statement = connection.createStatement();
			statement.execute("DROP ALL OBJECTS");
		} catch (SQLException e){
			System.err.println("Failed to drop all objects");
		}
	}
	
/*******
 * <p> Method: connectToDatabase </p>
 * 
 * <p> Description: Used to establish the in-memory instance of the H2 database from secondary
 *		storage.</p>
 * @throws SQLException when the DriverManager is unable to establish a connection
 * 
 */
	
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement();
			
			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	
/*******
 * <p> Method: createTables </p>
 * 
 * <p> Description: Used to create new instances of the two database tables used by this class.</p>
 * 
 */
	private void createTables() throws SQLException {
		// Create the user database
		String userTable = "CREATE TABLE IF NOT EXISTS userDB ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
                + "oneTimePassword VARCHAR(255), "
				+ "firstName VARCHAR(255), "
				+ "middleName VARCHAR(255), "
				+ "lastName VARCHAR (255), "
				+ "preferredFirstName VARCHAR(255), "
				+ "emailAddress VARCHAR(255), "
				+ "adminRole BOOL DEFAULT FALSE, "
				+ "studentRole BOOL DEFAULT FALSE, "
				+ "staffRole BOOL DEFAULT FALSE)";
		statement.execute(userTable);
	    
	    // Create the invitation codes table
	    // PURPOSE: Stores invite codes that allow new users to create accounts.
	    // We include a deadline (expiresAt) so codes do NOT work forever.
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	            + "emailAddress VARCHAR(255), "
	            + "role VARCHAR(255), "      // allow more text (future-proof)
	            + "expiresAt TIMESTAMP)";    // NEW: invitation deadline
	    statement.execute(invitationCodesTable);

	    // SAFETY: Upgrade existing database schema if needed.
	    // If the column already exists, these will fail — we ignore those failures.
	    try {
	        statement.execute("ALTER TABLE InvitationCodes ALTER COLUMN role VARCHAR(255)");
	    } catch (SQLException e) {
	        // Column probably already updated — safe to ignore
	    }

	    try {
	        statement.execute("ALTER TABLE InvitationCodes ADD COLUMN expiresAt TIMESTAMP");
	    } catch (SQLException e) {
	        // Column probably already exists — safe to ignore
	    }

	    // Phase 2: create discussion system tables
	    createDiscussionTables();

		// Phase 3: create request versioning table
	    createRequestTables();
	    
	    // Phase 4: create 1-on-1 feedback tables
	    createPrivateMessageTables();

	    // SAFETY: add severity and closeComment to any Requests table created before these
	    // columns existed.  ALTER TABLE ADD COLUMN fails silently if already present.
	    try {
	        statement.execute(
	        		"ALTER TABLE Requests ADD COLUMN severity VARCHAR(20) NOT NULL DEFAULT 'Medium'");
	    } catch (SQLException e) {
	        // Column already exists — safe to ignore
	    }

	    try {
	        statement.execute("ALTER TABLE Requests ADD COLUMN closeComment VARCHAR(2000)");
	    } catch (SQLException e) {
	        // Column already exists — safe to ignore
	    }
	}
	
/**
 * Method: createPrivateMessageTable
 * 
 * Description: creates the tables required for private 1-on-1 messaging.
 * 
 * @throws SQLException if the statement fails
 */
private void createPrivateMessageTables() {
	String privateMessageTable = "CREATE TABLE IF NOT EXISTS PrivateMessages ("
			+ "messageId INT AUTO_INCREMENT PRIMARY KEY,"
			+ "replyId        INT NULL, "
			+ "postId         INT NULL, "
			+ "authorUsername VARCHAR(255) NOT NULL, "
			+ "recipientUsername VARCHAR(255) NOT NULL,"
			+ "content        VARCHAR(2000) NOT NULL, "
			+ "timestamp      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
			+ "isDeleted      BOOL DEFAULT FALSE "
			+ ")";
	try {
		statement.execute(privateMessageTable);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}


/**
 * doesMessageExist
 * 
 * Used for finding out whether a given messageId already exists in the database
 * 
 * @param messageId Id of the message to check for
 * @return true if message exists in the database, false if message does not exist in the database
 */
public Boolean doesMessageExist(int messageId)
{
	String query = "SELECT COUNT(*) FROM PrivateMessages WHERE messageId = ?";
	try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		pstmt.setInt(1, messageId);
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) return rs.getInt(1) > 0;
	} catch (SQLException e) {
		e.printStackTrace();
	}
	return false;
}

/**
 * createMessage
 * 
 * Method for inserting a message into the database
 * 
 * @param message Message to insert into the database
 * @return the generated ID of the message after insertion
 * @throws SQLException if the messageId is already in use, if the insertion query fails, or if the messageId fails to generate upon insertion
 */
public int createMessage(PrivateMessage message) throws SQLException {

	// Guard: reject messages with IDs that already exist
	if (doesMessageExist(message.messageId))
		throw new SQLException("createMessage: messageId" + message.messageId
				+ " already exists.");

	String insertMessage =
			"INSERT INTO PrivateMessages (replyId, postId, authorUsername, recipientUsername, content) "
			+ "VALUES (?, ?, ?, ?, ?)";

	try (PreparedStatement pstmt = connection.prepareStatement(
			insertMessage, Statement.RETURN_GENERATED_KEYS)) {

		pstmt.setInt(1,       message.replyId);
		pstmt.setInt(2, message.postId);
		pstmt.setString(3, message.senderUsername);
		pstmt.setString(4, message.recipientUsername);
		pstmt.setString(5,    message.content);
		pstmt.executeUpdate();

		// Retrieve the auto-generated postId and write it back into the object
		try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
			if (generatedKeys.next()) {
				int generatedMessageId = generatedKeys.getInt(1);
				message.messageId = generatedMessageId;
				return generatedMessageId;
			} else {
				throw new SQLException("createMessage: INSERT succeeded but no generated key "
						+ "was returned.");
			}
		}
	}
}


/**
 * getMessagesBetween
 * 
 * Used to read all messages between two users (userA and userB)
 * 
 * @param userA UserName of first relevant user (sender or recipient)
 * @param userB UserName of second relevant user (sender or recipient)
 * @return List of private messages between userA and userB
 */
public List<PrivateMessage> getMessagesBetween(String userA, String userB) {
	List<PrivateMessage> messages = new ArrayList<>();
	String query = "SELECT * "
			+ "FROM PrivateMessages WHERE  (authorUsername = ? AND recipientUsername = ?) OR (authorUsername = ? AND recipientUsername = ?) "
			+ "ORDER BY timestamp ASC";
	try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		pstmt.setString(1, userA);
		pstmt.setString(2, userB);
		pstmt.setString(3, userB);
		pstmt.setString(4, userA);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			messages.add(new PrivateMessage(
					rs.getString("authorUsername"),
					rs.getString("recipientUsername"),
					rs.getString("content"),
					rs.getInt("replyId"),
					rs.getInt("postId"),
					rs.getInt("messageId"),
					rs.getTimestamp("timestamp").toLocalDateTime(),
					rs.getBoolean("isDeleted")));
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}
	return messages;
}



/**
 * getMessagesConcerning
 * 
 * Used to retrieve all messages concerning a specific user; messages where that user is either the sender 
 * or the recipient.
 *  
 * @param user UserName of relevant user (either sender or recipient)
 * @return list of all messages concerning the passed-in user
 */
public List<PrivateMessage> getMessagesConcerning(String user) {
	List<PrivateMessage> messages = new ArrayList<>();
	String query = "SELECT * "
			+ "FROM PrivateMessages WHERE  (authorUsername = ? OR recipientUsername = ?) "
			+ "ORDER BY timestamp ASC";
	try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		pstmt.setString(1, user);
		pstmt.setString(2, user);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			messages.add(new PrivateMessage(
					rs.getString("authorUsername"),
					rs.getString("recipientUsername"),
					rs.getString("content"),
					rs.getInt("replyId"),
					rs.getInt("postId"),
					rs.getInt("messageId"),
					rs.getTimestamp("timestamp").toLocalDateTime(),
					rs.getBoolean("isDeleted")));
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}
	return messages;
}






/*******
 * <p> Method: createDiscussionTables </p>
 *
 * <p> Description: Creates the three tables required by the Phase 2 Student Discussion System:
 * DiscussionThreads, Posts, and Replies.  These are created after the existing user and
 * invitation tables so that foreign-key relationships can be expressed correctly.
 *
 * DiscussionThreads — one row per named thread (e.g., "General").  Students may not create
 * or delete threads; that is a Phase 3 staff function.  Threads are seeded once at startup via
 * {@link #seedDefaultThread()}.
 *
 * Posts — one row per student post.  The isDeleted column supports soft-delete: the row is
 * never physically removed, but isDeleted = TRUE causes the UI to hide the content.
 *
 * Replies — one row per reply.  Replies reference a Post by postId.  The isDeleted column
 * supports soft-delete so students can remove their own replies without breaking the thread.
 * Replies are kept even when the parent post is soft-deleted. </p>
 *
 * @throws SQLException if any CREATE TABLE statement fails
 */
	private void createDiscussionTables() throws SQLException {

		// DiscussionThreads — named containers that group related Posts
		String threadTable = "CREATE TABLE IF NOT EXISTS DiscussionThreads ("
				+ "threadId INT AUTO_INCREMENT PRIMARY KEY, "
				+ "title    VARCHAR(255) UNIQUE NOT NULL)";
		statement.execute(threadTable);

		// Posts — student questions and statements
		// isDeleted = FALSE by default; set to TRUE when a student deletes their own post
		String postsTable = "CREATE TABLE IF NOT EXISTS Posts ("
				+ "postId         INT AUTO_INCREMENT PRIMARY KEY, "
				+ "threadId       INT NOT NULL, "
				+ "authorUsername VARCHAR(255) NOT NULL, "
				+ "title          VARCHAR(255) DEFAULT 'No Title', "
				+ "content        VARCHAR(2000) NOT NULL, "
				+ "timestamp      TIMESTAMP NOT NULL, "
				+ "isDeleted      BOOL DEFAULT FALSE, "
				+ "FOREIGN KEY (threadId) REFERENCES DiscussionThreads(threadId))";
		statement.execute(postsTable);

		// Replies — responses to Posts; support soft-delete via isDeleted flag
		// isDeleted = FALSE by default; set to TRUE when a student deletes their own reply
		String repliesTable = "CREATE TABLE IF NOT EXISTS Replies ("
				+ "replyId        INT AUTO_INCREMENT PRIMARY KEY, "
				+ "postId         INT NOT NULL, "
				+ "authorUsername VARCHAR(255) NOT NULL, "
				+ "content        VARCHAR(2000) NOT NULL, "
				+ "timestamp      TIMESTAMP NOT NULL, "
				+ "isDeleted      BOOL DEFAULT FALSE, "
				+ "FOREIGN KEY (postId) REFERENCES Posts(postId))";
		statement.execute(repliesTable);

		// ReadStatus — tracks which posts/replies each user has already read.
		// The UNIQUE constraint on (username, targetId, targetType) allows MERGE (upsert)
		// so marking the same item as read multiple times is idempotent.
		String readStatusTable = "CREATE TABLE IF NOT EXISTS ReadStatus ("
				+ "id         INT AUTO_INCREMENT PRIMARY KEY, "
				+ "username   VARCHAR(255) NOT NULL, "
				+ "targetId   INT NOT NULL, "
				+ "targetType VARCHAR(10)  NOT NULL, "   // 'post' or 'reply'
				+ "readAt     TIMESTAMP NOT NULL, "
				+ "UNIQUE(username, targetId, targetType))";
		statement.execute(readStatusTable);
	}

	/*******
 * <p> Method: createRequestTables </p>
 *
 * <p> Description: Creates the Requests table used by the TP3 prototype for reopening a closed
 * request as a new row linked back to the original closed request. The nullable
 * originalClosedRequestId column refers to itself: original requests store NULL, while
 * reopened requests store the requestId of the closed request they were derived from. </p>
 *
 * @throws SQLException if the CREATE TABLE statement fails
 */
	private void createRequestTables() throws SQLException {

		String requestsTable = "CREATE TABLE IF NOT EXISTS Requests ("
				+ "requestId INT AUTO_INCREMENT PRIMARY KEY, "
				+ "authorUsername VARCHAR(255) NOT NULL, "
				+ "title VARCHAR(255) NOT NULL, "
				+ "description VARCHAR(2000) NOT NULL, "
				+ "status VARCHAR(20) NOT NULL, "
				+ "originalClosedRequestId INT NULL, "
				+ "severity VARCHAR(20) NOT NULL DEFAULT 'Medium', "
				+ "closeComment VARCHAR(2000), "
				+ "createdAt TIMESTAMP NOT NULL, "
				+ "FOREIGN KEY (originalClosedRequestId) REFERENCES Requests(requestId))";
		statement.execute(requestsTable);
	}


/*******
 * <p> Method: List<Post>; getPostsByThread(int threadId) </p>
 *
 * <p> Description: Returns ALL Posts belonging to the given thread (including soft-deleted
 * ones), ordered by timestamp ascending so students read the conversation in chronological
 * order.  Soft-deleted posts are included so their replies remain accessible; the caller
 * should check {@link Post#isDeleted()} and display a placeholder instead of the content.
 * </p>
 *
 * @param threadId  the threadId whose Posts should be retrieved
 * @return a List of Post objects (active and deleted); empty if the thread has no posts
 */
	public List<Post> getPostsByThread(int threadId) {
		List<Post> posts = new ArrayList<>();
		String query = "SELECT postId, threadId, authorUsername, title, content, timestamp, isDeleted "
				+ "FROM Posts WHERE threadId = ? "
				+ "ORDER BY timestamp ASC";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, threadId);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				posts.add(new Post(
						rs.getInt("postId"),
						rs.getInt("threadId"),
						rs.getString("authorUsername"),
						rs.getBoolean("isDeleted") ? "***Deleted***" : rs.getString("title"),
						rs.getBoolean("isDeleted") ? "***Deleted***" : rs.getString("content"),
						rs.getTimestamp("timestamp").toLocalDateTime(),
						rs.getBoolean("isDeleted")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return posts;
	}


/*******
 * <p> Method: List<Post>; getPostsByAuthor(String username) </p>
 *
 * <p> Description: Returns ALL Posts written by the given user (including soft-deleted ones),
 * ordered by timestamp descending (newest first) so the student sees their most recent
 * content at the top.  Soft-deleted posts are included so the student can see their full
 * history; the caller should check {@link Post#isDeleted()} and mark them accordingly. </p>
 *
 * @param username  the authorUsername to filter on
 * @return a List of Post objects (active and deleted); empty if the user has no posts
 */
	public List<Post> getPostsByAuthor(String username) {
		List<Post> posts = new ArrayList<>();
		String query = "SELECT postId, threadId, authorUsername, title, content, timestamp, isDeleted "
				+ "FROM Posts WHERE authorUsername = ? "
				+ "ORDER BY timestamp DESC";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				posts.add(new Post(
						rs.getInt("postId"),
						rs.getInt("threadId"),
						rs.getString("authorUsername"),
						rs.getString("title"),
						rs.getString("content"),
						rs.getTimestamp("timestamp").toLocalDateTime(),
						rs.getBoolean("isDeleted")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return posts;
	}


/*******
 * <p> Method: List<Post>; searchPosts(String keyword, int threadId) </p>
 *
 * <p> Description: Returns non-deleted Posts whose content contains the given keyword
 * (case-insensitive substring match).  If {@code threadId} is -1, all threads are searched;
 * otherwise only the specified thread is searched.  Results are ordered by timestamp
 * descending so the most recent matches appear first. </p>
 *
 * @param keyword   the search term; the query uses {@code LOWER(content) LIKE ?} so the
 *                  match is case-insensitive
 * @param threadId  the thread to restrict the search to, or -1 to search all threads
 * @return a List of matching Post objects; empty if no matches are found
 */
	public List<Post> searchPosts(String keyword, int threadId) {
		List<Post> posts = new ArrayList<>();
		String like = "%" + keyword.toLowerCase() + "%";
		String query;
		if (threadId == -1) {
			query = "SELECT postId, threadId, authorUsername, title, content, timestamp, isDeleted "
					+ "FROM Posts WHERE isDeleted = FALSE AND LOWER(content) LIKE ? "
					+ "ORDER BY timestamp DESC";
		} else {
			query = "SELECT postId, threadId, authorUsername, title, content, timestamp, isDeleted "
					+ "FROM Posts WHERE isDeleted = FALSE AND LOWER(content) LIKE ? "
					+ "AND threadId = ? ORDER BY timestamp DESC";
		}
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, like);
			if (threadId != -1) pstmt.setInt(2, threadId);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				posts.add(new Post(
						rs.getInt("postId"),
						rs.getInt("threadId"),
						rs.getString("authorUsername"),
						rs.getString("title"),
						rs.getString("content"),
						rs.getTimestamp("timestamp").toLocalDateTime(),
						rs.getBoolean("isDeleted")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return posts;
	}


/*******
 * <p> Method: List<Reply>; getRepliesForPost(int postId) </p>
 *
 * <p> Description: Returns all Replies for the given postId, ordered by timestamp ascending
 * so the conversation thread reads in chronological order.  Soft-deleted replies are included
 * so their position in the thread is preserved; the caller should check
 * {@link Reply#isDeleted()} and display a placeholder instead of the content. </p>
 *
 * @param postId  the postId whose Replies should be retrieved
 * @return a List of Reply objects (including soft-deleted); empty if the post has no replies
 */
	public List<Reply> getRepliesForPost(int postId) {
		List<Reply> replies = new ArrayList<>();
		String query = "SELECT replyId, postId, authorUsername, content, timestamp, isDeleted "
				+ "FROM Replies WHERE postId = ? ORDER BY timestamp ASC";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, postId);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				replies.add(new Reply(
						rs.getInt("replyId"),
						rs.getInt("postId"),
						rs.getString("authorUsername"),
						rs.getString("content"),
						rs.getTimestamp("timestamp").toLocalDateTime(),
						rs.getBoolean("isDeleted")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return replies;
	}


/*******
 * <p> Method: int getReplyCount(int postId) </p>
 *
 * <p> Description: Returns the total number of replies for the given post, regardless of
 * read status.  Used by the "My Posts" page to show how many people have responded. </p>
 *
 * @param postId  the postId to count replies for
 * @return the number of Reply rows referencing this postId
 */
	public int getReplyCount(int postId) {
		String query = "SELECT COUNT(*) FROM Replies WHERE postId = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, postId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) return rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}


/*******
 * <p> Method: int getUnreadReplyCount(int postId, String username) </p>
 *
 * <p> Description: Returns the number of replies to the given post that the specified user
 * has not yet read.  A reply is "unread" if there is no matching row in the ReadStatus table
 * with {@code targetType = 'reply'} for this user. </p>
 *
 * @param postId    the postId whose replies are examined
 * @param username  the viewer whose ReadStatus rows are checked
 * @return the count of unread replies (0 if all have been read or there are no replies)
 */
	public int getUnreadReplyCount(int postId, String username) {
		String query = "SELECT COUNT(*) FROM Replies r "
				+ "WHERE r.postId = ? AND r.isDeleted = FALSE "
				+ "AND r.replyId NOT IN ("
				+ "  SELECT targetId FROM ReadStatus "
				+ "  WHERE username = ? AND targetType = 'reply')";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, postId);
			pstmt.setString(2, username);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) return rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}


/*******
 * <p> Method: void markPostAsRead(int postId, String username) </p>
 *
 * <p> Description: Records that the given user has read the given post.  The operation is
 * idempotent: calling it multiple times does not create duplicate rows because the UNIQUE
 * constraint on (username, targetId, targetType) turns the INSERT into a no-op via
 * H2's MERGE statement. </p>
 *
 * @param postId    the postId to mark as read
 * @param username  the user marking the post as read
 */
	public void markPostAsRead(int postId, String username) {
		String merge = "MERGE INTO ReadStatus (username, targetId, targetType, readAt) "
				+ "KEY(username, targetId, targetType) VALUES (?, ?, 'post', ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(merge)) {
			pstmt.setString(1, username);
			pstmt.setInt(2, postId);
			pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


/*******
 * <p> Method: void markReplyAsRead(int replyId, String username) </p>
 *
 * <p> Description: Records that the given user has read the given reply.  Idempotent — see
 * {@link #markPostAsRead(int, String)} for details. </p>
 *
 * @param replyId   the replyId to mark as read
 * @param username  the user marking the reply as read
 */
	public void markReplyAsRead(int replyId, String username) {
		String merge = "MERGE INTO ReadStatus (username, targetId, targetType, readAt) "
				+ "KEY(username, targetId, targetType) VALUES (?, ?, 'reply', ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(merge)) {
			pstmt.setString(1, username);
			pstmt.setInt(2, replyId);
			pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


/*******
 * <p> Method: boolean isPostRead(int postId, String username) </p>
 *
 * <p> Description: Returns true if the given user has already read the given post (i.e.,
 * a row exists in ReadStatus with targetType = 'post'). </p>
 *
 * @param postId    the postId to check
 * @param username  the user to check
 * @return true if the post is already marked as read for this user; false otherwise
 */
	public boolean isPostRead(int postId, String username) {
		String query = "SELECT COUNT(*) FROM ReadStatus "
				+ "WHERE username = ? AND targetId = ? AND targetType = 'post'";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			pstmt.setInt(2, postId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) return rs.getInt(1) > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}


/*******
 * <p> Method: boolean isReplyRead(int replyId, String username) </p>
 *
 * <p> Description: Returns true if the given user has already read the given reply (i.e.,
 * a row exists in ReadStatus with targetType = 'reply'). </p>
 *
 * @param replyId   the replyId to check
 * @param username  the user to check
 * @return true if the reply is already marked as read for this user; false otherwise
 */
	public boolean isReplyRead(int replyId, String username) {
		String query = "SELECT COUNT(*) FROM ReadStatus "
				+ "WHERE username = ? AND targetId = ? AND targetType = 'reply'";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			pstmt.setInt(2, replyId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) return rs.getInt(1) > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}


/*******
 * <p> Method: Post getPostById(int postId) </p>
 *
 * <p> Description: Returns the Post row with the given postId, or {@code null} if no such
 * row exists.  Unlike {@link #getPostsByThread(int)}, this method returns soft-deleted posts
 * too, so the caller can decide whether to show them (e.g., to display the deleted notice). </p>
 *
 * @param postId  the postId to look up
 * @return the matching Post, or null if not found
 */
	public Post getPostById(int postId) {
		String query = "SELECT postId, threadId, authorUsername, title, content, timestamp, isDeleted "
				+ "FROM Posts WHERE postId = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, postId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return new Post(
						rs.getInt("postId"),
						rs.getInt("threadId"),
						rs.getString("authorUsername"),
						rs.getBoolean("isDeleted") ? "***Deleted***" : rs.getString("title"),
						rs.getBoolean("isDeleted") ? "***Deleted***" : rs.getString("content"),
						rs.getTimestamp("timestamp").toLocalDateTime(),
						rs.getBoolean("isDeleted"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}


/*******
 * <p> Method: Reply getReplyById(int replyId) </p>
 *
 * <p> Description: Returns the Reply row with the given replyId, or {@code null} if no such
 * row exists. </p>
 *
 * @param replyId  the replyId to look up
 * @return the matching Reply, or null if not found
 */
	public Reply getReplyById(int replyId) {
		String query = "SELECT replyId, postId, authorUsername, content, timestamp, isDeleted "
				+ "FROM Replies WHERE replyId = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, replyId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return new Reply(
						rs.getInt("replyId"),
						rs.getInt("postId"),
						rs.getString("authorUsername"),
						rs.getString("content"),
						rs.getTimestamp("timestamp").toLocalDateTime(),
						rs.getBoolean("isDeleted"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}


/*******
 * <p> Method: int updatePost(int postId, String newContent, String authorUsername) </p>
 *
 * <p> Description: Updates the content of a non-deleted Post, but only if the row belongs
 * to {@code authorUsername}.  The author check is performed inside the WHERE clause, so an
 * unprivileged user who guesses a postId cannot overwrite someone else's post.
 *
 * Returns the number of rows updated (1 on success; 0 if the post does not exist, is
 * soft-deleted, or the requester is not the author). </p>
 *
 * @param postId         the postId to update
 * @param newContent     the replacement content; should be pre-validated by
 *                       {@link recognizers.PostContentRecognizer}
 * @param authorUsername the username of the student attempting the edit
 * @return 1 if the update succeeded; 0 otherwise
 */
	public int updatePost(int postId, String newContent, String authorUsername) {
		String update = "UPDATE Posts SET content = ? "
				+ "WHERE postId = ? AND authorUsername = ? AND isDeleted = FALSE";
		try (PreparedStatement pstmt = connection.prepareStatement(update)) {
			pstmt.setString(1, newContent);
			pstmt.setInt(2, postId);
			pstmt.setString(3, authorUsername);
			return pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}


/*******
 * <p> Method: int softDeletePost(int postId, String authorUsername) </p>
 *
 * <p> Description: Sets isDeleted = TRUE for the given Post, but only if the row belongs to
 * {@code authorUsername}.  Replies attached to a soft-deleted post are kept intact; the UI
 * should display a "(post deleted)" notice in place of the original content.
 *
 * Returns the number of rows updated (1 on success; 0 if not found or wrong author). </p>
 *
 * @param postId         the postId to soft-delete
 * @param authorUsername the username of the student attempting the deletion
 * @return 1 if the post was soft-deleted; 0 otherwise
 */
	public int softDeletePost(int postId, String authorUsername) {
		String update = "UPDATE Posts SET isDeleted = TRUE "
				+ "WHERE postId = ? AND authorUsername = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(update)) {
			pstmt.setInt(1, postId);
			pstmt.setString(2, authorUsername);
			return pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}


/*******
 * <p> Method: int softDeleteReply(int replyId, String authorUsername) </p>
 *
 * <p> Description: Sets isDeleted = TRUE for the given Reply, but only if the row belongs to
 * {@code authorUsername}.  The row is never physically removed; the UI should display a
 * "[Reply deleted]" notice in place of the original content.
 *
 * Returns the number of rows updated (1 on success; 0 if not found or wrong author). </p>
 *
 * @param replyId        the replyId to soft-delete
 * @param authorUsername the username of the student attempting the deletion
 * @return 1 if the reply was soft-deleted; 0 otherwise
 */
	public int softDeleteReply(int replyId, String authorUsername) {
		String update = "UPDATE Replies SET isDeleted = TRUE "
				+ "WHERE replyId = ? AND authorUsername = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(update)) {
			pstmt.setInt(1, replyId);
			pstmt.setString(2, authorUsername);
			return pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}


/*******
 * <p> Method: int updateReply(int replyId, String newContent, String authorUsername) </p>
 *
 * <p> Description: Updates the content of a non-deleted Reply, but only if the row belongs to
 * {@code authorUsername}.  The author check and the isDeleted guard are both performed inside
 * the WHERE clause, so an unprivileged user cannot overwrite someone else's reply and deleted
 * replies cannot be edited.
 *
 * Returns the number of rows updated (1 on success; 0 if the reply does not exist, is
 * soft-deleted, or the requester is not the author). </p>
 *
 * @param replyId        the replyId to update
 * @param newContent     the replacement content; should be pre-validated by
 *                       {@link recognizers.PostContentRecognizer}
 * @param authorUsername the username of the student attempting the edit
 * @return 1 if the update succeeded; 0 otherwise
 */
	public int updateReply(int replyId, String newContent, String authorUsername) {
		String update = "UPDATE Replies SET content = ? "
				+ "WHERE replyId = ? AND authorUsername = ? AND isDeleted = FALSE";
		try (PreparedStatement pstmt = connection.prepareStatement(update)) {
			pstmt.setString(1, newContent);
			pstmt.setInt(2, replyId);
			pstmt.setString(3, authorUsername);
			return pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}


/*******
 * <p> Method: seedDefaultThread </p>
 *
 * <p> Description: Inserts the "General" discussion thread if it does not already exist.
 * This method is called once from {@code FoundationsMain.start()} after the database tables
 * have been created, ensuring that there is always at least one thread available for students
 * to post into.  Using INSERT IGNORE (H2: INSERT INTO ... WHERE NOT EXISTS) means the call is
 * safely idempotent — running it multiple times does not produce duplicate rows. </p>
 *
 * @throws SQLException if the INSERT statement fails for a reason other than the row already
 *         existing
 */
	public void seedDefaultThread() throws SQLException {
		String insertGeneral =
				"MERGE INTO DiscussionThreads (title) KEY(title) VALUES (?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertGeneral)) {
			pstmt.setString(1, "General");
			pstmt.executeUpdate();
		}
	}


/*******
 * <p> Method: int createPost(Post post) </p>
 *
 * <p> Description: Inserts a new Post row into the Posts table and returns the
 * auto-generated postId.  The generated key is also written back into the Post object via
 * {@code post.setPostId()} so the caller can reference it immediately (e.g., to display
 * "Your post was created with ID: X" or to chain a reply).
 *
 * The method validates that the referenced threadId exists before attempting the INSERT.
 * If the thread is not found a SQLException is thrown with a descriptive message rather than
 * letting the foreign-key constraint produce a less readable error. </p>
 *
 * @param post  the Post object to persist; must have threadId, authorUsername, content, and
 *              timestamp populated; postId may be 0 (it will be set by this method)
 * @return the auto-generated postId assigned by the database
 * @throws SQLException if the threadId does not exist, the INSERT fails, or no generated key
 *         is returned
 */
	public int createPost(Post post) throws SQLException {

		// Guard: reject posts aimed at a thread that does not exist
		if (!doesThreadExist(post.getThreadId()))
			throw new SQLException("createPost: threadId " + post.getThreadId()
					+ " does not exist in DiscussionThreads.");

		String insertPost =
				"INSERT INTO Posts (threadId, authorUsername, title, content, timestamp, isDeleted) "
				+ "VALUES (?, ?, ?, ?, ?, FALSE)";

		try (PreparedStatement pstmt = connection.prepareStatement(
				insertPost, Statement.RETURN_GENERATED_KEYS)) {

			pstmt.setInt(1,       post.getThreadId());
			pstmt.setString(2,    post.getAuthorUsername());
			pstmt.setString(3,    post.getTitle());
			pstmt.setString(4,    post.getContent());
			pstmt.setTimestamp(5, Timestamp.valueOf(post.getTimestamp()));
			pstmt.executeUpdate();

			// Retrieve the auto-generated postId and write it back into the object
			try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					int generatedPostId = generatedKeys.getInt(1);
					post.setPostId(generatedPostId);
					return generatedPostId;
				} else {
					throw new SQLException("createPost: INSERT succeeded but no generated key "
							+ "was returned.");
				}
			}
		}
	}


/*******
 * <p> Method: int createReply(Reply reply) </p>
 *
 * <p> Description: Inserts a new Reply row into the Replies table and returns the
 * auto-generated replyId.  The generated key is also written back into the Reply object via
 * {@code reply.setReplyId()}.
 *
 * The method validates that the referenced postId exists before attempting the INSERT.
 * If the post is not found a SQLException is thrown with a descriptive message.  Note that
 * replies are allowed on soft-deleted posts (isDeleted = TRUE); the soft-delete flag only
 * affects content visibility, not the ability to reply. </p>
 *
 * @param reply  the Reply object to persist; must have postId, authorUsername, content, and
 *               timestamp populated; replyId may be 0 (it will be set by this method)
 * @return the auto-generated replyId assigned by the database
 * @throws SQLException if the postId does not exist, the INSERT fails, or no generated key
 *         is returned
 */
	public int createReply(Reply reply) throws SQLException {

		// Guard: reject replies aimed at a post that does not exist
		if (!doesPostExist(reply.getPostId()))
			throw new SQLException("createReply: postId " + reply.getPostId()
					+ " does not exist in Posts.");

		String insertReply =
				"INSERT INTO Replies (postId, authorUsername, content, timestamp) "
				+ "VALUES (?, ?, ?, ?)";

		try (PreparedStatement pstmt = connection.prepareStatement(
				insertReply, Statement.RETURN_GENERATED_KEYS)) {

			pstmt.setInt(1,       reply.getPostId());
			pstmt.setString(2,    reply.getAuthorUsername());
			pstmt.setString(3,    reply.getContent());
			pstmt.setTimestamp(4, Timestamp.valueOf(reply.getTimestamp()));
			pstmt.executeUpdate();

			// Retrieve the auto-generated replyId and write it back into the object
			try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					int generatedReplyId = generatedKeys.getInt(1);
					reply.setReplyId(generatedReplyId);
					return generatedReplyId;
				} else {
					throw new SQLException("createReply: INSERT succeeded but no generated key "
							+ "was returned.");
				}
			}
			
		}
	}


/*******
 * <p> Method: List<DiscussionThread>; getThreadList() </p>
 *
 * <p> Description: Returns a list of all DiscussionThread objects currently in the database,
 * ordered by threadId ascending so that "General" (threadId = 1) always appears first.
 * This list is used to populate the thread-selection ComboBox on the Create Post page. </p>
 *
 * @return a List of DiscussionThread objects; an empty list if no threads exist (should not
 *         happen in normal operation after seedDefaultThread() has run)
 */
	public List<DiscussionThread> getThreadList() {
		List<DiscussionThread> threads = new ArrayList<>();
		String query = "SELECT threadId, title FROM DiscussionThreads ORDER BY threadId ASC";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				threads.add(new DiscussionThread(rs.getInt("threadId"),
						rs.getString("title")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return threads;
	}


/*******
 * <p> Method: boolean doesPostExist(int postId) </p>
 *
 * <p> Description: Returns true if a row with the given postId exists in the Posts table,
 * regardless of its isDeleted flag.  Used by {@link #createReply(Reply)} to guard against
 * replies to non-existent posts, and by the Controller to validate the Post ID the user
 * types before attempting the INSERT. </p>
 *
 * @param postId  the postId to look up
 * @return true if at least one row with that postId exists; false otherwise
 */
	public boolean doesPostExist(int postId) {
		String query = "SELECT COUNT(*) FROM Posts WHERE postId = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, postId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) return rs.getInt(1) > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}


/*******
 * <p> Method: boolean doesThreadExist(int threadId) </p>
 *
 * <p> Description: Returns true if a row with the given threadId exists in the
 * DiscussionThreads table.  Used by {@link #createPost(Post)} to guard against posts aimed
 * at non-existent threads before the foreign-key constraint fires. </p>
 *
 * @param threadId  the threadId to look up
 * @return true if at least one row with that threadId exists; false otherwise
 */
	public boolean doesThreadExist(int threadId) {
		String query = "SELECT COUNT(*) FROM DiscussionThreads WHERE threadId = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, threadId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) return rs.getInt(1) > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}


/*******
 * <p> Method: isDatabaseEmpty </p>
 * 
 * <p> Description: If the user database has no rows, true is returned, else false.</p>
 * 
 *  @return true if the database is empty, else it returns false
 * 
 */
	public boolean isDatabaseEmpty() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count") == 0;
			}
		}  catch (SQLException e) {
	        return false;
	    }
		return true;
	}
	
	
/*******
 * <p> Method: getNumberOfUsers </p>
 * 
 * <p> Description: Returns an integer .of the number of users currently in the user database. </p>
 * 
 * @return the number of user records in the database.
 * 
 */
	public int getNumberOfUsers() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch (SQLException e) {
	        return 0;
	    }
		return 0;
	}
	

	
/*******
 * <p> Method: remove </p>
 * 
 * <p> Description: Removes the user with the matching userName. </p>
 *
 * @param userName the username of the user to remove
 *
 * @throws SQLException if there is an issue executing the SQL command
 *
 */
public void remove(String userName) throws SQLException
{
	String removeUser = "DELETE FROM userDB WHERE userName = ?";
	try (PreparedStatement pstmt = connection.prepareStatement(removeUser)){
		pstmt.setString(1, userName);
		pstmt.executeUpdate();
	}
}

/*******
 * <p> Method: getNumAdmins </p>
 * 
 * <p> Description: Returns and integer of the number of admins currently in the user database. </p>
 * 
 * @return the number of admin records in the database (users with adminRole = TRUE).
 *
 * @throws SQLException if there is an issue executing the SQL command
 *
 */
public int getNumAdmins() throws SQLException
{
	String query = "SELECT COUNT(*) AS count FROM userDB WHERE adminRole = TRUE";
	try {
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count");
		}
	} catch (SQLException e) {
        return 0;
    }
	return 0;
}

/*******
 * <p> Method: register(User user) </p>
 * 
 * <p> Description: Creates a new row in the database using the user parameter. </p>
 * @param user registers a new user into the database.
 * 
 * @throws SQLException when there is an issue creating the SQL command or executing it.
 * 
 */
	public void register(User user) throws SQLException {
		String insertUser = "MERGE INTO userDB (userName, password, firstName, middleName, "
				+ "lastName, preferredFirstName, emailAddress, adminRole, studentRole, staffRole) "
				+ "KEY (userName) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			currentUsername = user.getUserName();
			pstmt.setString(1, currentUsername);
			
			currentPassword = user.getPassword();
			pstmt.setString(2, currentPassword);
			
			currentFirstName = user.getFirstName();
			pstmt.setString(3, currentFirstName);
			
			currentMiddleName = user.getMiddleName();			
			pstmt.setString(4, currentMiddleName);
			
			currentLastName = user.getLastName();
			pstmt.setString(5, currentLastName);
			
			currentPreferredFirstName = user.getPreferredFirstName();
			pstmt.setString(6, currentPreferredFirstName);
			
			currentEmailAddress = user.getEmailAddress();
			pstmt.setString(7, currentEmailAddress);
			
			currentAdminRole = user.getAdminRole();
			pstmt.setBoolean(8, currentAdminRole);
			
			currentStudentRole = user.getNewStudentRole();
			pstmt.setBoolean(9, currentStudentRole);
			
			currentStaffRole = user.getNewStaffRole();
			pstmt.setBoolean(10, currentStaffRole);
			
			pstmt.executeUpdate();
		}
		
	}
	
/*******
 *  <p> Method: List getUserList() </p>
 *  
 *  <P> Description: Generate an List of Strings, one for each user in the database,
 *  starting with {@code <Select User>} at the start of the list. </p>
 *  
 *  @return a list of userNames found in the database.
 */
	public List<String> getUserList () {
		List<String> userList = new ArrayList<String>();
		userList.add("<Select a User>");
		String query = "SELECT userName FROM userDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				userList.add(rs.getString("userName"));
			}
		} catch (SQLException e) {
	        return null;
	    }
//		System.out.println(userList);
		return userList;
	}

/*******
 * <p> Method: boolean loginAdmin(User user) </p>
 * 
 * <p> Description: Check to see that a user with the specified username, password, and role
 * 		is the same as a row in the table for the username, password, and role. </p>
 * 
 * @param user specifies the specific user that should be logged in playing the Admin role.
 * 
 * @return true if the specified user has been logged in as an Admin else false.
 * 
 */
	public boolean loginAdmin(User user){
		// Validates an admin user's login credentials so the user can login in as an Admin.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "adminRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();	// If a row is returned, rs.next() will return true		
		} catch  (SQLException e) {
	        e.printStackTrace();
	    }
		return false;
	}
	
	
	
	
/*******
 * <p> Method: boolean loginStudentRole(User user) </p>
 * 
 * <p> Description: Check to see that a user with the specified username, password, and role
 * 		is the same as a row in the table for the username, password, and role. </p>
 * 
 * @param user specifies the specific user that should be logged in playing the Student role.
 * 
 * @return true if the specified user has been logged in as an Student else false.
 * 
 */
	public boolean loginStudentRole(User user) {
		// Validates a student user's login credentials.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "studentRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
		       e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p> Method: boolean loginStaff(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username, password, and role
	 * 		is the same as a row in the table for the username, password, and role. </p>
	 * 
	 * @param user specifies the specific user that should be logged in playing the Reviewer role.
	 * 
	 * @return true if the specified user has been logged in as an Student else false.
	 * 
	 */
	// Validates a reviewer user's login credentials.
	public boolean loginStaffRole(User user) {
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "staffRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
		       e.printStackTrace();
		}
		return false;
	}
	
	
	/*******
	 * <p> Method: boolean doesUserExist(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username is  in the table. </p>
	 * 
	 * @param userName specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return true if the specified user is in the table else false.
	 * 
	 */
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM userDB WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}

	
	/*******
	 * <p> Method: int getNumberOfRoles(User user) </p>
	 * 
	 * <p> Description: Determine the number of roles a specified user plays. </p>
	 * 
	 * @param user specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return the number of roles this user plays (0 - 5).
	 * 
	 */	
	// Get the number of roles that this user plays
	public int getNumberOfRoles (User user) {
		int numberOfRoles = 0;
		if (user.getAdminRole()) numberOfRoles++;
		if (user.getNewStudentRole()) numberOfRoles++;
		if (user.getNewStaffRole()) numberOfRoles++;
		return numberOfRoles;
	}	

	
	/*******
	 * <p> Method: String generateInvitationCode(String emailAddress, String role) </p>
	 * 
	 * <p> Description: Given an email address and a roles, this method establishes and invitation
	 * code and adds a record to the InvitationCodes table.  When the invitation code is used, the
	 * stored email address is used to establish the new user and the record is removed from the
	 * table.</p>
	 * 
	 * @param emailAddress specifies the email address for this new user.
	 * 
	 * @param role specified the role that this new user will play.
	 *
	 * @param expiresAt the expiration deadline for this invitation code
	 *
	 * @return the code of six characters so the new user can use it to securely setup an account.
	 *
	 */
	// Generates a new invitation code and inserts it into the database.
	// Default deadline = 24 hours 
	// PURPOSE: New method that stores the expiration deadline in the DB.
	// AdminHome will call THIS once we add the DatePicker.
	public String generateInvitationCode(String emailAddress, String role,
			LocalDateTime expiresAt) {
	    String code = UUID.randomUUID().toString().substring(0, 6); // 6-char code

	    // IMPORTANT: we now insert expiresAt so the invite has a deadline
	    String query = "INSERT INTO InvitationCodes (code, emailaddress, role, expiresAt) VALUES (?, ?, ?, ?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.setString(2, emailAddress);
	        pstmt.setString(3, role);
	        pstmt.setTimestamp(4, Timestamp.valueOf(expiresAt)); // LocalDateTime -> SQL timestamp
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return code;
	}
	
	/**
	 * Returns the expiration deadline for the given invitation code, or null if not found.
	 *
	 * @param code the invitation code to look up
	 * @return the expiration time, or null if not found
	 */
	public LocalDateTime getInvitationExpiry(String code) {
	    String query = "SELECT expiresAt FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            Timestamp ts = rs.getTimestamp("expiresAt");
	            if (ts == null) return null;
	            return ts.toLocalDateTime();
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	/**
	 * Returns true if the invitation code is expired, and removes it if so.
	 *
	 * @param code the invitation code to check
	 * @return true if the code is expired, false otherwise
	 */
	public boolean isInvitationExpired(String code) {
	    LocalDateTime expiry = getInvitationExpiry(code);
	    // If expiry is missing (older records), treat as NOT expired (or decide to treat as expired).
	    // For safety with legacy invites, we allow it.
	    if (expiry == null) return false;
	    boolean expired = expiry.isBefore(LocalDateTime.now());
	    if (expired) {
	        // Cleanup: remove expired invite so it can't be used later
	        removeInvitationAfterUse(code);
	    }
	    return expired;
	}

	
	/*******
	 * <p> Method: int getNumberOfInvitations() </p>
	 * 
	 * <p> Description: Determine the number of outstanding invitations in the table.</p>
	 *  
	 * @return the number of invitations in the table.
	 * 
	 */
	// Number of invitations in the database
	public int getNumberOfInvitations() {
		String query = "SELECT COUNT(*) AS count FROM InvitationCodes";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch  (SQLException e) {
	        e.printStackTrace();
	    }
		return 0;
	}
	
	
	/*******
	 * <p> Method: boolean emailaddressHasBeenUsed(String emailAddress) </p>
	 * 
	 * <p> Description: Determine if an email address has been user to establish a user.</p>
	 * 
	 * @param emailAddress is a string that identifies a user in the table
	 *  
	 * @return true if the email address is in the table, else return false.
	 * 
	 */
	// Check to see if an email address is already in the database
	public boolean emailaddressHasBeenUsed(String emailAddress) {
	    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE emailAddress = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, emailAddress);
	        ResultSet rs = pstmt.executeQuery();
	 //     System.out.println(rs);
	        if (rs.next()) {
	            // Mark the code as used
	        	return rs.getInt("count")>0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return false;
	}
	
	
	/*******
	 * <p> Method: String getRoleGivenAnInvitationCode(String code) </p>
	 * 
	 * <p> Description: Get the role associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return the role for the code or an empty string.
	 * 
	 */
	// Obtain the roles associated with an invitation code.
	public String getRoleGivenAnInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("role");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return "";
	}

	
	/*******
	 * <p> Method: String getEmailAddressUsingCode (String code ) </p>
	 * 
	 * <p> Description: Get the email addressed associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return the email address for the code or an empty string.
	 * 
	 */
	// For a given invitation code, return the associated email address of an empty string
	public String getEmailAddressUsingCode (String code ) {
	    String query = "SELECT emailAddress FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("emailAddress");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return "";
	}
	
	
	/*******
	 * <p> Method: void removeInvitationAfterUse(String code) </p>
	 * 
	 * <p> Description: Remove an invitation record once it is used.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 */
	// Remove an invitation using an email address once the user account has been setup
	public void removeInvitationAfterUse(String code) {
	    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	        	int counter = rs.getInt("count");
	            // Only do the remove if the code is still in the invitation table
	        	if (counter > 0) {
        			query = "DELETE FROM InvitationCodes WHERE code = ?";
	        		try (PreparedStatement pstmt2 = connection.prepareStatement(query)) {
	        			pstmt2.setString(1, code);
	        			pstmt2.executeUpdate();
	        		}catch (SQLException e) {
	        	        e.printStackTrace();
	        	    }
	        	}
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
		
	}
	
	
	/*******
	 * <p> Method: String getFirstName(String username) </p>
	 * 
	 * <p> Description: Get the first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the first name of a user given that user's username 
	 *  
	 */
	// Get the First Name
	public String getFirstName(String username) {
		String query = "SELECT firstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("firstName"); // Return the first name if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	

	/*******
	 * <p> Method: void updateFirstName(String username, String firstName) </p>
	 * 
	 * <p> Description: Update the first name of a user given that user's username and the new
	 *		first name.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @param firstName is the new first name for the user
	 *  
	 */
	// update the first name
	public void updateFirstName(String username, String firstName) {
	    String query = "UPDATE userDB SET firstName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, firstName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentFirstName = firstName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	
	/*******
	 * <p> Method: String getMiddleName(String username) </p>
	 * 
	 * <p> Description: Get the middle name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the middle name of a user given that user's username 
	 *  
	 */
	// get the middle name
	public String getMiddleName(String username) {
		String query = "SELECT MiddleName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("middleName"); // Return the middle name if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}

	
	/*******
	 * <p> Method: void updateMiddleName(String username, String middleName) </p>
	 * 
	 * <p> Description: Update the middle name of a user given that user's username and the new
	 * 		middle name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param middleName is the new middle name for the user
	 *  
	 */
	// update the middle name
	public void updateMiddleName(String username, String middleName) {
	    String query = "UPDATE userDB SET middleName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, middleName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentMiddleName = middleName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getLastName(String username) </p>
	 * 
	 * <p> Description: Get the last name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the last name of a user given that user's username 
	 *  
	 */
	// get he last name
	public String getLastName(String username) {
		String query = "SELECT LastName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("lastName"); // Return last name role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updateLastName(String username, String lastName) </p>
	 * 
	 * <p> Description: Update the middle name of a user given that user's username and the new
	 * 		middle name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param lastName is the new last name for the user
	 *  
	 */
	// update the last name
	public void updateLastName(String username, String lastName) {
	    String query = "UPDATE userDB SET lastName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, lastName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentLastName = lastName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getPreferredFirstName(String username) </p>
	 * 
	 * <p> Description: Get the preferred first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the preferred first name of a user given that user's username 
	 *  
	 */
	// get the preferred first name
	public String getPreferredFirstName(String username) {
		String query = "SELECT preferredFirstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("firstName"); // Return the preferred first name if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updatePreferredFirstName(String username, String preferredFirstName) </p>
	 * 
	 * <p> Description: Update the preferred first name of a user given that user's username and
	 * 		the new preferred first name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param preferredFirstName is the new preferred first name for the user
	 *  
	 */
	// update the preferred first name of the user
	public void updatePreferredFirstName(String username, String preferredFirstName) {
	    String query = "UPDATE userDB SET preferredFirstName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, preferredFirstName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentPreferredFirstName = preferredFirstName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getEmailAddress(String username) </p>
	 * 
	 * <p> Description: Get the email address of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the email address of a user given that user's username 
	 *  
	 */
	// get the email address
	public String getEmailAddress(String username) {
		String query = "SELECT emailAddress FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("emailAddress"); // Return the email address if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updateEmailAddress(String username, String emailAddress) </p>
	 * 
	 * <p> Description: Update the email address name of a user given that user's username and
	 * 		the new email address.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param emailAddress is the new preferred first name for the user
	 *  
	 */
	// update the email address
	public void updateEmailAddress(String username, String emailAddress) {
	    String query = "UPDATE userDB SET emailAddress = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, emailAddress);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentEmailAddress = emailAddress;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: boolean getUserAccountDetails(String username) </p>
	 * 
	 * <p> Description: Get all the attributes of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return true of the get is successful, else false
	 *  
	 */
	// get the attributes for a specified user
	public boolean getUserAccountDetails(String username) {
		String query = "SELECT * FROM userDB WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();			
			rs.next();
	    	currentUsername = rs.getString(2);
	    	currentPassword = rs.getString(3);
            currentOneTimePassword = rs.getString(4);
	    	currentFirstName = rs.getString(5);
	    	currentMiddleName = rs.getString(6);
	    	currentLastName = rs.getString(7);
	    	currentPreferredFirstName = rs.getString(8);
	    	currentEmailAddress = rs.getString(9);
	    	currentAdminRole = rs.getBoolean(10);
	    	currentStudentRole = rs.getBoolean(11);
	    	currentStaffRole = rs.getBoolean(12);
			return true;
	    } catch (SQLException e) {
			return false;
	    }
	}
	
	
	/*******
	 * <p> Method: boolean updateUserRole(String username, String role, String value) </p>
	 * 
	 * <p> Description: Update a specified role for a specified user's and set and update all the
	 * 		current user attributes.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param role is string that specifies the role to update
	 * 
	 * @param value is the string that specified TRUE or FALSE for the role
	 * 
	 * @return true if the update was successful, else false
	 *  
	 */
	// Update a users role
	public boolean updateUserRole(String username, String role, String value) {
		if (role.compareTo("Admin") == 0) {
			String query = "UPDATE userDB SET adminRole = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentAdminRole = true;
				else
					currentAdminRole = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Student") == 0) {
			String query = "UPDATE userDB SET studentRole = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentStudentRole = true;
				else
					currentStudentRole = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Staff") == 0) {
			String query = "UPDATE userDB SET staffRole = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentStaffRole = true;
				else
					currentStaffRole = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		return false;
	}
	
	
	// Attribute getters for the current user
	/*******
	 * <p> Method: String getCurrentUsername() </p>
	 * 
	 * <p> Description: Get the current user's username.</p>
	 * 
	 * @return the username value is returned
	 *  
	 */
	public String getCurrentUsername() { return currentUsername;};

	
	/*******
	 * <p> Method: String getCurrentPassword() </p>
	 * 
	 * <p> Description: Get the current user's password.</p>
	 * 
	 * @return the password value is returned
	 *  
	 */
	public String getCurrentPassword() { return currentPassword;};

	
	/*******
	 * <p> Method: String getCurrentFirstName() </p>
	 * 
	 * <p> Description: Get the current user's first name.</p>
	 * 
	 * @return the first name value is returned
	 *  
	 */
	public String getCurrentFirstName() { return currentFirstName;};

	
	/*******
	 * <p> Method: String getCurrentMiddleName() </p>
	 * 
	 * <p> Description: Get the current user's middle name.</p>
	 * 
	 * @return the middle name value is returned
	 *  
	 */
	public String getCurrentMiddleName() { return currentMiddleName;};

	
	/*******
	 * <p> Method: String getCurrentLastName() </p>
	 * 
	 * <p> Description: Get the current user's last name.</p>
	 * 
	 * @return the last name value is returned
	 *  
	 */
	public String getCurrentLastName() { return currentLastName;};

	
	/*******
	 * <p> Method: String getCurrentPreferredFirstName( </p>
	 * 
	 * <p> Description: Get the current user's preferred first name.</p>
	 * 
	 * @return the preferred first name value is returned
	 *  
	 */
	public String getCurrentPreferredFirstName() { return currentPreferredFirstName;};

	
	/*******
	 * <p> Method: String getCurrentEmailAddress() </p>
	 * 
	 * <p> Description: Get the current user's email address name.</p>
	 * 
	 * @return the email address value is returned
	 *  
	 */
	public String getCurrentEmailAddress() { return currentEmailAddress;};

	
	/*******
	 * <p> Method: boolean getCurrentAdminRole() </p>
	 * 
	 * <p> Description: Get the current user's Admin role attribute.</p>
	 * 
	 * @return true if this user plays an Admin role, else false
	 *  
	 */
	public boolean getCurrentAdminRole() { return currentAdminRole;};

	
	/*******
	 * <p> Method: boolean getCurrentNewStudentRole() </p>
	 * 
	 * <p> Description: Get the current user's Student role attribute.</p>
	 * 
	 * @return true if this user plays a Student role, else false
	 *  
	 */
	public boolean getCurrentNewStudentRole() { return currentStudentRole;};

	
	/*******
	 * <p> Method: boolean getCurrentNewStaffRole() </p>
	 * 
	 * <p> Description: Get the current user's Reviewer role attribute.</p>
	 * 
	 * @return true if this user plays a Reviewer role, else false
	 *  
	 */
	public boolean getCurrentNewStaffRole() { return currentStaffRole;};

	/*******
	 * <p> Method: String getCurrentOneTimePassword() </p>
	 *
	 * <p> Description: Get the current user's one-time password.</p>
	 *
	 * @return the one-time password value is returned (may be null)
	 *
	 */
	public String getCurrentOneTimePassword() { return currentOneTimePassword;};


	/*******
	 * <p> Method: void setOneTimePassword(String username, String otp) </p>
	 *
	 * <p> Description: Set a one-time password for a user. This allows an admin to reset
	 * a user's password so they can log in and establish a new password.</p>
	 *
	 * @param username is the username of the user
	 * @param otp is the one-time password to set
	 *
	 */
	public void setOneTimePassword(String username, String otp) {
	    String query = "UPDATE userDB SET oneTimePassword = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, otp);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


	/*******
	 * <p> Method: void clearOneTimePassword(String username) </p>
	 *
	 * <p> Description: Clear the one-time password for a user after it has been used.</p>
	 *
	 * @param username is the username of the user
	 *
	 */
	public void clearOneTimePassword(String username) {
	    String query = "UPDATE userDB SET oneTimePassword = NULL WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        pstmt.executeUpdate();
	        currentOneTimePassword = null;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


	/*******
	 * <p> Method: void updatePassword(String username, String newPassword) </p>
	 *
	 * <p> Description: Update the password for a user.</p>
	 *
	 * @param username is the username of the user
	 * @param newPassword is the new password for the user
	 *
	 */
	public void updatePassword(String username, String newPassword) {
	    String query = "UPDATE userDB SET password = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, newPassword);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentPassword = newPassword;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	/*******
	 * <p> Debugging method</p>
	 * 
	 * <p> Description: Debugging method that dumps the database of the console.</p>
	 * 
	 * @throws SQLException if there is an issues accessing the database.
	 * 
	 */
	// Dumps the database.
	public void dump() throws SQLException {
		String query = "SELECT * FROM userDB";
		ResultSet resultSet = statement.executeQuery(query);
		ResultSetMetaData meta = resultSet.getMetaData();
		while (resultSet.next()) {
		for (int i = 0; i < meta.getColumnCount(); i++) {
		System.out.println(
		meta.getColumnLabel(i + 1) + ": " +
				resultSet.getString(i + 1));
		}
		System.out.println();
		}
		resultSet.close();
	}


	/*******
	 * <p> Method: void closeConnection()</p>
	 * 
	 * <p> Description: Closes the database statement and connection.</p>
	 * 
	 */
	// Closes the database statement and connection.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}

	/*******
 * <p> Method: int createRequest(Request request) </p>
 *
 * <p> Description: Inserts a new Request row into the Requests table and returns the generated
 * requestId.  This method is used for both original requests and reopened requests.  Original
 * rows should supply a null originalClosedRequestId; reopened rows should supply the original
 * closed request ID only after that ID has already been validated. </p>
 *
 * @param request the Request object to persist
 * @return the auto-generated requestId
 * @throws SQLException if the INSERT fails or no generated key is returned
 */
	public int createRequest(Request request) throws SQLException {

		String insertRequest =
				"INSERT INTO Requests (authorUsername, title, description, status, "
				+ "originalClosedRequestId, severity, createdAt) VALUES (?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement pstmt = connection.prepareStatement(
				insertRequest, Statement.RETURN_GENERATED_KEYS)) {

			pstmt.setString(1, request.getAuthorUsername());
			pstmt.setString(2, request.getTitle());
			pstmt.setString(3, request.getDescription());
			pstmt.setString(4, request.getStatus());

			if (request.getOriginalClosedRequestId() == null) {
				pstmt.setNull(5, Types.INTEGER);
			} else {
				pstmt.setInt(5, request.getOriginalClosedRequestId());
			}

			String sev = request.getSeverity();
			pstmt.setString(6, (sev != null && !sev.isEmpty()) ? sev : "Medium");
			pstmt.setTimestamp(7, Timestamp.valueOf(request.getCreatedAt()));
			pstmt.executeUpdate();

			try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					int generatedRequestId = generatedKeys.getInt(1);
					request.setRequestId(generatedRequestId);
					return generatedRequestId;
				} else {
					throw new SQLException("createRequest: INSERT succeeded but no generated key "
							+ "was returned.");
				}
			}
		}
	}


/*******
 * <p> Method: Request getRequestById(int requestId) </p>
 *
 * <p> Description: Returns the Request row with the given requestId, or null if no such row
 * exists.  This method is used by the tests to verify both the reopened request and the original
 * closed request it links to. </p>
 *
 * @param requestId the requestId to look up
 * @return the matching Request, or null if not found
 */
	public Request getRequestById(int requestId) {
		String query = "SELECT requestId, authorUsername, title, description, status, "
				+ "originalClosedRequestId, severity, closeComment, createdAt FROM Requests WHERE requestId = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, requestId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return buildRequestFromResultSet(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}


/*******
 * <p> Method: List<Request> getRequestHistory(int requestId) </p>
 *
 * <p> Description: Returns the full history chain for a request. If {@code requestId} is the
 * original closed request, the result includes that row and every reopened descendant. If
 * {@code requestId} refers to a reopened request, the method first resolves its
 * originalClosedRequestId and then returns the full chain anchored at that original request.
 * Results are ordered by creation time ascending so the history reads oldest-to-newest. </p>
 *
 * @param requestId the original or reopened request ID whose history should be retrieved
 * @return a chronologically ordered list of related request rows; empty if the anchor is invalid
 */
	public List<Request> getRequestHistory(int requestId) {
		List<Request> requests = new ArrayList<>();
		Request anchor = getRequestById(requestId);
		if (anchor == null) return requests;

		int rootRequestId = (anchor.getOriginalClosedRequestId() == null)
				? anchor.getRequestId()
				: anchor.getOriginalClosedRequestId();

		String query = "SELECT requestId, authorUsername, title, description, status, "
				+ "originalClosedRequestId, severity, closeComment, createdAt FROM Requests "
				+ "WHERE requestId = ? OR originalClosedRequestId = ? "
				+ "ORDER BY createdAt ASC, requestId ASC";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, rootRequestId);
			pstmt.setInt(2, rootRequestId);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				requests.add(buildRequestFromResultSet(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return requests;
	}


/*******
 * <p> Method: boolean doesClosedRequestExist(int requestId) </p>
 *
 * <p> Description: Returns true only when the specified requestId exists in the Requests table
 * and that row is marked CLOSED.  This is the critical validation gate for reopening: a reopened
 * request must point to a real closed request, not to an open request or a non-existent row. </p>
 *
 * @param requestId the original requestId to validate
 * @return true if the request exists and is CLOSED
 */
	public boolean doesClosedRequestExist(int requestId) {
		String query = "SELECT COUNT(*) FROM Requests WHERE requestId = ? AND status = 'CLOSED'";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, requestId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) return rs.getInt(1) > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}


/*******
 * <p> Method: String validateOriginalClosedRequestId(int originalClosedRequestId) </p>
 *
 * <p> Description: Validates that the supplied original closed request ID refers to an existing
 * request row whose status is CLOSED.  The method returns an empty string when the ID is valid;
 * otherwise it returns a user-facing error message explaining that the ID does not belong to a
 * closed request.  This mirrors the recognizer pattern used elsewhere in the project. </p>
 *
 * @param originalClosedRequestId the original request ID supplied for the reopened request
 * @return an empty string if valid, else an error message
 */
	public String validateOriginalClosedRequestId(int originalClosedRequestId) {
		if (originalClosedRequestId <= 0) {
			return "Original closed request ID number must be greater than 0.";
		}
		if (!doesClosedRequestExist(originalClosedRequestId)) {
			return "Original closed request ID number does not belong to a closed request.";
		}
		return "";
	}


/*******
 * <p> Method: boolean isStaffUser(String username) </p>
 *
 * <p> Description: Returns true when the given username exists in userDB and has the Staff role.
 * Reopened request creation is limited to staff users. </p>
 *
 * @param username the username to validate
 * @return true if the user has the Staff role
 */
	public boolean isStaffUser(String username) {
		String query = "SELECT COUNT(*) FROM userDB WHERE userName = ? AND staffRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) return rs.getInt(1) > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}


/*******
 * <p> Method: boolean isAdminUser(String username) </p>
 *
 * <p> Description: Returns true when the given username exists in userDB and has the Admin role.
 * This is used to gate request-history visibility methods intended for administrators. </p>
 *
 * @param username the username to validate
 * @return true if the user has the Admin role
 */
	public boolean isAdminUser(String username) {
		String query = "SELECT COUNT(*) FROM userDB WHERE userName = ? AND adminRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) return rs.getInt(1) > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}


/*******
 * <p> Method: int closeRequest(int requestId) </p>
 *
 * <p> Description: Marks an existing request row as CLOSED with no close comment.
 * Delegates to {@link #closeRequest(int, String)}. </p>
 *
 * @param requestId the requestId to close
 * @return the number of rows updated
 */
	public int closeRequest(int requestId) {
		return closeRequest(requestId, null);
	}


/*******
 * <p> Method: int closeRequest(int requestId, String comment) </p>
 *
 * <p> Description: Marks an existing request row as CLOSED and records the admin's
 * close comment explaining the resolution.  The row is intentionally retained so later
 * reopened versions can link back to it and preserve the request history. </p>
 *
 * @param requestId the requestId to close
 * @param comment   the admin's resolution note; may be null or empty
 * @return the number of rows updated
 */
	public int closeRequest(int requestId, String comment) {
		String update = "UPDATE Requests SET status = 'CLOSED', closeComment = ? WHERE requestId = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(update)) {
			if (comment == null || comment.isBlank()) {
				pstmt.setNull(1, Types.VARCHAR);
			} else {
				pstmt.setString(1, comment.trim());
			}
			pstmt.setInt(2, requestId);
			return pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}


/*******
 * <p> Method: int reopenRequest(int requestId) </p>
 *
 * <p> Description: Sets the status of an existing CLOSED request back to OPEN.  This is an
 * admin-only direct status update — it modifies the existing row rather than creating a new
 * history row.  It is distinct from {@link #reopenClosedRequest} which is the staff workflow
 * that preserves history by inserting a new linked row. </p>
 *
 * @param requestId the requestId to reopen
 * @return the number of rows updated
 */
	public int reopenRequest(int requestId) {
		// Clear closeComment when reopening so stale resolution notes are not shown
		String update = "UPDATE Requests SET status = 'OPEN', closeComment = NULL WHERE requestId = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(update)) {
			pstmt.setInt(1, requestId);
			return pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}


/*******
 * <p> Method: int getRequestCount() </p>
 *
 * <p> Description: Returns the number of rows currently stored in the Requests table. </p>
 *
 * @return the number of request rows, or 0 if the query fails
 */
	public int getRequestCount() {
		String query = "SELECT COUNT(*) FROM Requests";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) return rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}


/*******
 * <p> Method: int reopenClosedRequest(String staffUsername, String title, String description,
 * int originalClosedRequestId) </p>
 *
 * <p> Description: Creates a brand-new OPEN request row that represents a reopened version of a
 * previously closed request.  The original request is never overwritten.  Instead, the new row
 * stores the original request ID in originalClosedRequestId so the system preserves history and
 * allows callers to navigate back to the original closed request.
 *
 * Input validation is performed before insertion:
 * title must satisfy {@link recognizers.TitleRecognizer},
 * description must satisfy {@link recognizers.PostContentRecognizer},
 * the originalClosedRequestId must refer to an existing CLOSED request, and
 * staffUsername must belong to a user with the Staff role. </p>
 *
 * @param staffUsername the staff user reopening the request
 * @param title the updated title for the reopened request
 * @param description the updated description for the reopened request
 * @param originalClosedRequestId the original closed request to link back to
 * @return the generated requestId for the new reopened request
 * @throws SQLException if validation fails or the INSERT fails
 */
	public int reopenClosedRequest(String staffUsername, String title, String description,
			int originalClosedRequestId) throws SQLException {

		String titleError = recognizers.TitleRecognizer.evaluateTitle(title);
		if (!titleError.isEmpty()) {
			throw new SQLException("reopenClosedRequest: " + titleError);
		}

		String descriptionError =
				recognizers.PostContentRecognizer.evaluatePostContent(description);
		if (!descriptionError.isEmpty()) {
			throw new SQLException("reopenClosedRequest: " + descriptionError);
		}

		String originalIdError = validateOriginalClosedRequestId(originalClosedRequestId);
		if (!originalIdError.isEmpty()) {
			throw new SQLException("reopenClosedRequest: " + originalIdError);
		}

		if (!isStaffUser(staffUsername)) {
			throw new SQLException("reopenClosedRequest: Only a Staff user may reopen a request.");
		}

		// Preserve history by inserting a new OPEN row instead of overwriting the closed request.
		Request reopened = new Request(staffUsername, title, description, "OPEN",
				originalClosedRequestId, LocalDateTime.now());
		return createRequest(reopened);
	}


/*******
 * <p> Method: List<Request> getRequestsVisibleToAdmin(String adminUsername) </p>
 *
 * <p> Description: Returns all request rows visible to an administrator, including original and
 * reopened versions. This supports admin review of reopened requests and their preserved
 * historical links. Non-admin callers are rejected with a SQLException. </p>
 *
 * @param adminUsername the username requesting the admin-visible request list
 * @return a list of all requests ordered newest-first
 * @throws SQLException if the caller is not an admin
 */
	public List<Request> getRequestsVisibleToAdmin(String adminUsername) throws SQLException {
		if (!isAdminUser(adminUsername)) {
			throw new SQLException("getRequestsVisibleToAdmin: Only an Admin user may view all "
					+ "requests.");
		}

		List<Request> requests = new ArrayList<>();
		String query = "SELECT requestId, authorUsername, title, description, status, "
				+ "originalClosedRequestId, severity, closeComment, createdAt FROM Requests "
				+ "ORDER BY createdAt DESC, requestId DESC";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				requests.add(buildRequestFromResultSet(rs));
			}
		}
		return requests;
	}


/*******
 * <p> Method: int submitStaffRequest(String staffUsername, String title, String description) </p>
 *
 * <p> Description: Validates the title and description using the project recognizers and then
 * inserts a new OPEN request row authored by the given staff user.  The
 * {@code originalClosedRequestId} is left null because this is an original request, not a
 * reopen.  Any staff user (not just users with the staffRole flag) may call this method from
 * the GUI; role enforcement is handled at the page level. </p>
 *
 * @param staffUsername the username of the staff user submitting the request
 * @param title         the request title
 * @param description   the full description of the requested admin action
 * @return the auto-generated requestId assigned by the database
 * @throws SQLException if validation fails or the INSERT fails
 */
	public int submitStaffRequest(String staffUsername, String title, String description,
			String severity) throws SQLException {

		String titleError = recognizers.TitleRecognizer.evaluateTitle(title);
		if (!titleError.isEmpty()) {
			throw new SQLException("submitStaffRequest: " + titleError);
		}

		String descError = recognizers.PostContentRecognizer.evaluatePostContent(description);
		if (!descError.isEmpty()) {
			throw new SQLException("submitStaffRequest: " + descError);
		}

		String resolvedSeverity = (severity != null && !severity.isEmpty()) ? severity : "Medium";
		Request request = new Request(staffUsername, title, description, "OPEN", null,
				resolvedSeverity, LocalDateTime.now());
		return createRequest(request);
	}


/*******
 * <p> Method: int updateRequestSeverity(int requestId, String severity) </p>
 *
 * <p> Description: Updates the severity level of an existing request.  Called by admins from
 * the Request Queue page to re-classify a ticket after reviewing it. </p>
 *
 * @param requestId the request to update
 * @param severity  the new severity level (Low / Medium / High / Critical)
 * @return the number of rows updated
 */
	public int updateRequestSeverity(int requestId, String severity) {
		String update = "UPDATE Requests SET severity = ? WHERE requestId = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(update)) {
			pstmt.setString(1, severity);
			pstmt.setInt(2, requestId);
			return pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}


/*******
 * <p> Method: List<Request> getAllRequests() </p>
 *
 * <p> Description: Returns every row in the Requests table ordered by createdAt ascending
 * (oldest first) so the queue presents the highest-priority, longest-pending items at the
 * top.  Both OPEN and CLOSED requests are included so admins and staff have a complete
 * historical view.  No role check is performed here; callers are responsible for ensuring
 * only authorised users reach this method. </p>
 *
 * @return a chronologically ordered list of all request rows; empty if the table is empty
 */
	public List<Request> getAllRequests() {
		List<Request> requests = new ArrayList<>();
		String query = "SELECT requestId, authorUsername, title, description, status, "
				+ "originalClosedRequestId, severity, closeComment, createdAt FROM Requests "
				+ "ORDER BY createdAt ASC, requestId ASC";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				requests.add(buildRequestFromResultSet(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return requests;
	}


	/**
	 * Builds a {@link Request} from the current row in a Requests query result set.
	 *
	 * @param rs the positioned result set
	 * @return the mapped Request domain object
	 * @throws SQLException if the row cannot be read
	 */
	private Request buildRequestFromResultSet(ResultSet rs) throws SQLException {
		Integer originalId = (Integer) rs.getObject("originalClosedRequestId");

		String severity = "Medium";
		try {
			String s = rs.getString("severity");
			if (s != null && !s.isBlank()) severity = s;
		} catch (SQLException ignored) { }

		String closeComment = null;
		try { closeComment = rs.getString("closeComment"); } catch (SQLException ignored) { }

		return new Request(
				rs.getInt("requestId"),
				rs.getString("authorUsername"),
				rs.getString("title"),
				rs.getString("description"),
				rs.getString("status"),
				originalId,
				severity,
				closeComment,
				rs.getTimestamp("createdAt").toLocalDateTime());
	}
	
	
	/*******
	 * <p> Method: List<Post>; getRepliesByAuthor(String username) </p>
	 *
	 * <p> Description: Returns ALL Replies written by the given user (including soft-deleted ones),
	 * ordered by timestamp descending (newest first) so the student sees their most recent
	 * content at the top.  Soft-deleted posts are included so the student can see their full
	 * history; the caller should check {@link Post#isDeleted()} and mark them accordingly. </p>
	 *
	 * @param username  the authorUsername to filter on
	 * @return a List of Reply objects (active and deleted); empty if the user has no replies
	 */
		public List<Reply> getRepliesByAuthor(String username) {
			List<Reply> replies = new ArrayList<>();
			String query = "SELECT replyId, postId, authorUsername, content, timestamp, isDeleted "
					+ "FROM Replies WHERE authorUsername = ? "
					+ "ORDER BY timestamp DESC";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, username);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					replies.add(new Reply(
							rs.getInt("replyId"),
							rs.getInt("postId"),
							rs.getString("authorUsername"),
							rs.getString("content"),
							rs.getTimestamp("timestamp").toLocalDateTime(),
							rs.getBoolean("isDeleted")));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return replies;
		}
	
}
