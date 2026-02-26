package entityClasses;

/*******
 * <p> Title: DiscussionThread Class </p>
 *
 * <p> Description: This DiscussionThread class represents a discussion thread entity in the
 * Student Discussion System.  Students post questions and statements into threads.  The default
 * thread is "General", which is seeded at every application startup.  Students cannot create,
 * edit, or delete threads — that is a staff function reserved for Phase 3. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author CSE 360 Team
 *
 * @version 1.00	2026-02-23	Initial version for Phase 2 — Student Discussion System
 */

public class DiscussionThread {

	/*-*******************************************************************************************

	Attributes

	 */

	private int    threadId;	// Auto-generated primary key assigned by the database
	private String title;		// Human-readable thread name displayed in the UI (e.g., "General")


	/*-*******************************************************************************************

	Constructors

	 */

	/*****
	 * <p> Method: DiscussionThread() </p>
	 *
	 * <p> Description: Default constructor — not used directly, required by the framework. </p>
	 */
	public DiscussionThread() { }


	/*****
	 * <p> Method: DiscussionThread(int threadId, String title) </p>
	 *
	 * <p> Description: Constructor used when building a DiscussionThread object from a database
	 * ResultSet row.  Both fields are populated directly from the query result. </p>
	 *
	 * @param threadId  the auto-generated primary key from the DiscussionThreads table
	 * @param title     the human-readable title of the thread (e.g., "General")
	 */
	public DiscussionThread(int threadId, String title) {
		this.threadId = threadId;
		this.title    = title;
	}


	/*-*******************************************************************************************

	Getters

	 */

	/*****
	 * <p> Method: int getThreadId() </p>
	 *
	 * <p> Description: Returns the thread's database primary key. </p>
	 *
	 * @return the integer threadId
	 */
	public int getThreadId() { return threadId; }


	/*****
	 * <p> Method: String getTitle() </p>
	 *
	 * <p> Description: Returns the thread's title string. </p>
	 *
	 * @return the title (e.g., "General")
	 */
	public String getTitle() { return title; }


	/*****
	 * <p> Method: String toString() </p>
	 *
	 * <p> Description: Returns the title so that a JavaFX ComboBox&lt;DiscussionThread&gt; can
	 * display each thread by its human-readable name without requiring a custom cell factory. </p>
	 *
	 * @return the title string
	 */
	@Override
	public String toString() { return title; }
}
