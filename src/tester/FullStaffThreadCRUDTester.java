package tester;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import database.Database;
import entityClasses.DiscussionThread;

/*******
 * <p> Title: FullStaffThreadCRUDTester Class. </p>
 * 
 * <p> Description: This JUnit test class validates the minimum required database-level
 * behavior for the TP3 staff discussion thread CRUD feature. These tests focus on the
 * staff ability to create, update, read, and delete discussion threads while enforcing
 * the key protection rules for duplicate titles, the default "General" thread, and
 * threads that still contain posts. </p>
 * 
 * <p> The tests are intentionally centered on the database methods because those methods
 * implement the core business rules that the JavaFX page depends on. If these methods
 * behave correctly, the staff GUI can rely on them during normal operation. </p>
 * 
 * @author Saam Kavusi
 * 
 * @version 1.00        2026-04-11 Initial version for TP3 staff thread CRUD testing
 */

public class FullStaffThreadCRUDTester {

	/*-*******************************************************************************************

	Attributes
	
	 */
	
	/** Test database instance used for each test. */
	private Database testDatabase;

	/**********
	 * <p> Method: setUp() </p>
	 * 
	 * <p> Description: Creates a fresh database state before each test. The database is
	 * connected, all prior objects are removed, the tables are recreated, and the default
	 * "General" discussion thread is seeded so each test begins from a known state. </p>
	 * 
	 * @throws SQLException if database setup fails
	 */
	@BeforeEach
	public void setUp() throws SQLException {
		testDatabase = new Database();
		testDatabase.connectToDatabase();
		testDatabase.dropAllObjects();
		testDatabase.createTables();
		testDatabase.seedDefaultThread();
	}

	/**********
	 * <p> Method: tearDown() </p>
	 * 
	 * <p> Description: Closes the database connection after each test so test resources
	 * are released cleanly. </p>
	 */
	@AfterEach
	public void tearDown() {
		if (testDatabase != null) {
			testDatabase.closeConnection();
		}
	}

	/**********
	 * <p> Method: NormalTest01() </p>
	 * 
	 * <p> Description: Verifies that staff thread creation succeeds when a valid,
	 * non-duplicate title is supplied. The new title should appear in the thread list
	 * after creation. </p>
	 * 
	 * @throws SQLException if the database operation fails unexpectedly
	 */
	@Test
	public void NormalTest01() throws SQLException {
		testDatabase.createThread("Homework Help");

		List<DiscussionThread> threads = testDatabase.getThreadList();

		boolean found = false;
		for (DiscussionThread thread : threads) {
			if (thread.getTitle().equals("Homework Help")) {
				found = true;
				break;
			}
		}

		assertTrue(found);
	}

	/**********
	 * <p> Method: RobustTest01() </p>
	 * 
	 * <p> Description: Verifies that a duplicate discussion thread title is rejected.
	 * Since the default "General" thread is seeded before each test, attempting to create
	 * another thread named "General" must throw a SQLException. </p>
	 */
	@Test
	public void RobustTest01() {
		assertThrows(SQLException.class, () -> {
			testDatabase.createThread("General");
		});
	}

	/**********
	 * <p> Method: NormalTest02() </p>
	 * 
	 * <p> Description: Verifies that updating a thread title succeeds when the thread
	 * exists and the replacement title is valid and unique. </p>
	 * 
	 * @throws SQLException if the database operation fails unexpectedly
	 */
	@Test
	public void NormalTest02() throws SQLException {
		int threadId = testDatabase.createThread("Old Title");

		int rowsUpdated = testDatabase.updateThreadTitle(threadId, "New Title");

		assertEquals(1, rowsUpdated);

		List<DiscussionThread> threads = testDatabase.getThreadList();

		boolean found = false;
		for (DiscussionThread thread : threads) {
			if (thread.getThreadId() == threadId && thread.getTitle().equals("New Title")) {
				found = true;
				break;
			}
		}

		assertTrue(found);
	}

	/**********
	 * <p> Method: RobustTest02() </p>
	 * 
	 * <p> Description: Verifies that the default "General" discussion thread cannot be
	 * deleted. This ensures the required default thread is always preserved. </p>
	 */
	@Test
	public void RobustTest02() {
		assertThrows(SQLException.class, () -> {
			testDatabase.deleteThread(1);
		});
	}

	/**********
	 * <p> Method: NormalTest03() </p>
	 * 
	 * <p> Description: Verifies that deleting a normal custom thread succeeds when the
	 * thread exists and has no posts. </p>
	 * 
	 * @throws SQLException if the database operation fails unexpectedly
	 */
	@Test
	public void NormalTest03() throws SQLException {
		int threadId = testDatabase.createThread("Temporary Thread");

		int rowsDeleted = testDatabase.deleteThread(threadId);

		assertEquals(1, rowsDeleted);
		assertTrue(!testDatabase.doesThreadExist(threadId));
	}

/**********
 * <p> Method: RobustTest03() </p>
 * 
 * <p> Description: Verifies that updating a discussion thread to a title that already
 * exists is rejected. This ensures thread titles remain unique and that staff cannot
 * rename one thread so it duplicates another existing thread title. </p>
 * 
 * @throws SQLException if the setup database operations fail unexpectedly
 */
@Test
public void RobustTest03() throws SQLException {
	int firstThreadId = testDatabase.createThread("Thread One");
	testDatabase.createThread("Thread Two");

	assertThrows(SQLException.class, () -> {
		testDatabase.updateThreadTitle(firstThreadId, "Thread Two");
	});
} 

}