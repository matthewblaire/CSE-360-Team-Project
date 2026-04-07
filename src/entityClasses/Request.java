package entityClasses;

import java.time.LocalDateTime;

/*******
 * <p> Title: Request Class </p>
 *
 * <p> Description: This class represents a help or support request that can later be
 * closed and, if needed, re-opened by a staff user as a new version.  The new version keeps a
 * link back to the original closed request through {@code originalClosedRequestId} so the system
 * preserves historical context rather than overwriting the original row. </p>
 *
 * @author Maggie Willis
 *
 * @version 1.00 2026-04-04 Initial prototype for HW3 request reopen/history support
 */
public class Request {

	/*-*******************************************************************************************

	Attributes

	 */

	private int requestId;                   // Database-assigned primary key
	private String authorUsername;           // Username of the user who created this request row
	private String title;                    // Short title describing the request
	private String description;              // Detailed request description
	private String status;                   // OPEN or CLOSED
	private Integer originalClosedRequestId; // Null for original rows; set for reopened copies
	private LocalDateTime createdAt;         // Time the row was created


	/*-*******************************************************************************************

	Constructors

	 */

	/*****
	 * <p> Method: Request() </p>
	 *
	 * <p> Description: Default constructor.  It exists for framework compatibility and is not
	 * used directly by the prototype tests. </p>
	 */
	public Request() { }


	/*****
	 * <p> Method: Request(String authorUsername, String title, String description, String status,
	 * Integer originalClosedRequestId, LocalDateTime createdAt) </p>
	 *
	 * <p> Description: Constructor used before a Request is inserted into the database.  The
	 * requestId is still unknown at this point, so it remains 0 until the database returns a
	 * generated key. </p>
	 *
	 * @param authorUsername the username associated with this request row
	 * @param title the request title
	 * @param description the request description
	 * @param status the lifecycle status for the request
	 * @param originalClosedRequestId the linked closed request ID for reopened requests, else null
	 * @param createdAt when this request row was created
	 */
	public Request(String authorUsername, String title, String description, String status,
			Integer originalClosedRequestId, LocalDateTime createdAt) {
		this.authorUsername = authorUsername;
		this.title = title;
		this.description = description;
		this.status = status;
		this.originalClosedRequestId = originalClosedRequestId;
		this.createdAt = createdAt;
	}


	/*****
	 * <p> Method: Request(int requestId, String authorUsername, String title, String description,
	 * String status, Integer originalClosedRequestId, LocalDateTime createdAt) </p>
	 *
	 * <p> Description: Full constructor used when loading a Request from the database. </p>
	 *
	 * @param requestId the database-assigned primary key
	 * @param authorUsername the username associated with this request row
	 * @param title the request title
	 * @param description the request description
	 * @param status the lifecycle status for the request
	 * @param originalClosedRequestId the linked original closed request ID, else null
	 * @param createdAt when this request row was created
	 */
	public Request(int requestId, String authorUsername, String title, String description,
			String status, Integer originalClosedRequestId, LocalDateTime createdAt) {
		this.requestId = requestId;
		this.authorUsername = authorUsername;
		this.title = title;
		this.description = description;
		this.status = status;
		this.originalClosedRequestId = originalClosedRequestId;
		this.createdAt = createdAt;
	}


	/*-*******************************************************************************************

	Getters and Setters

	 */

	/*****
	 * <p> Method: int getRequestId() </p>
	 * <p> Description: Returns the database-assigned request ID. </p>
	 * @return the request ID
	 */
	public int getRequestId() { return requestId; }


	/*****
	 * <p> Method: void setRequestId(int requestId) </p>
	 * <p> Description: Sets the generated request ID after a successful INSERT. </p>
	 * @param requestId the generated request ID
	 */
	public void setRequestId(int requestId) { this.requestId = requestId; }


	/*****
	 * <p> Method: String getAuthorUsername() </p>
	 * <p> Description: Returns the username associated with this request row. </p>
	 * @return the request author username
	 */
	public String getAuthorUsername() { return authorUsername; }


	/*****
	 * <p> Method: String getTitle() </p>
	 * <p> Description: Returns the request title. </p>
	 * @return the title text
	 */
	public String getTitle() { return title; }


	/*****
	 * <p> Method: String getDescription() </p>
	 * <p> Description: Returns the request description. </p>
	 * @return the description text
	 */
	public String getDescription() { return description; }


	/*****
	 * <p> Method: String getStatus() </p>
	 * <p> Description: Returns the lifecycle status. </p>
	 * @return OPEN or CLOSED
	 */
	public String getStatus() { return status; }


	/*****
	 * <p> Method: Integer getOriginalClosedRequestId() </p>
	 * <p> Description: Returns the linked original closed request ID when this row is a reopened
	 * version.  Original requests return null. </p>
	 * @return the original closed request ID, or null
	 */
	public Integer getOriginalClosedRequestId() { return originalClosedRequestId; }


	/*****
	 * <p> Method: LocalDateTime getCreatedAt() </p>
	 * <p> Description: Returns when the request row was created. </p>
	 * @return the creation timestamp
	 */
	public LocalDateTime getCreatedAt() { return createdAt; }


	/*****
	 * <p> Method: void setStatus(String status) </p>
	 * <p> Description: Updates the lifecycle status for this request object. </p>
	 * @param status the new lifecycle status
	 */
	public void setStatus(String status) { this.status = status; }
}
