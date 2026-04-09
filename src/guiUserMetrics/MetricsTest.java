//package guiUserMetrics;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import java.util.Hashtable;
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import applicationMain.FoundationsMain;
//import entityClasses.User;
//import guiStaff.ViewStaffHome;
//import guiStudent.ViewStudentHome;
//import guiUserLogin.ViewUserLogin;
//import guiUserLogin.ControllerUserLogin;
//import javafx.application.Platform;
//import javafx.stage.Stage;
//
///*******
// * <p> Title: MetricsTest Class. </p>
// * 
// * <p> Description: a class to test the metrics retrieval of a user </p>
// * 
// * @author Azeer Esmail - Team 2
// * 
// */
//public class MetricsTest {
//	
//	/**
//	 * the variable to hold the metrics when retrieved
//	 */
//	public Hashtable<String, Object> metrics;
//	
//	/** Unused default constructor **/
//	MetricsTest(){}
//	
//	
//	@BeforeAll
//	static void setUpBeforeClass() throws Exception {
//		Platform.startup(() -> {});
//	}
//
//	
//	/**
//	 * Test that authorized staff1 can retrieve metrics for student1.
//	 */
//	@Test
//	public void test1() {
//        CountDownLatch latch = new CountDownLatch(1);
//        
//        Platform.runLater(() -> {
//            try {
//                Stage stage = new Stage();
//                new FoundationsMain().start(stage);
//                ViewUserLogin.getText_Username().setText("staff1");
//                ViewUserLogin.getText_Password().setText("");
//                ViewUserLogin.getButton_Login().fire();
//                User currentUser = ViewStaffHome.getTheUser();
//                metrics = ControllerUserMetrics.getUserMetrics(stage, currentUser, "student1");
//                // Assert launch
//                
//            } finally {
//                latch.countDown();
//            }
//        });
//        
//		try {
//			latch.await(2, TimeUnit.SECONDS);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} //
//		assertTrue(metrics != null);
//	}
//	
//	/**
//	 * Test that unauthorized student1 can't retrieve metrics for student1.
//	 */
//	@Test
//	public void test2() {
//        CountDownLatch latch = new CountDownLatch(1);
//        
//        Platform.runLater(() -> {
//            try {
//                Stage stage = new Stage();
//                new FoundationsMain().start(stage);
//                ViewUserLogin.getText_Username().setText("student1");
//                ViewUserLogin.getText_Password().setText("");
//                ViewUserLogin.getButton_Login().fire();
//                User currentUser = ViewStudentHome.getTheUser();
//                metrics = ControllerUserMetrics.getUserMetrics(stage, currentUser, "student1");
//                // Assert launch
//                
//            } finally {
//                latch.countDown();
//            }
//        });
//        
//		try {
//			latch.await(2, TimeUnit.SECONDS);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} //
//		assertTrue(metrics == null);
//	}
//
//	/**
//	 * Test that authorized staff1 can retrieve 5 posts and 0 replies for student1.
//	 */
//	@Test
//	public void test3() {
//        CountDownLatch latch = new CountDownLatch(1);
//        
//        Platform.runLater(() -> {
//            try {
//                Stage stage = new Stage();
//                new FoundationsMain().start(stage);
//                ViewUserLogin.getText_Username().setText("staff1");
//                ViewUserLogin.getText_Password().setText("");
//                ViewUserLogin.getButton_Login().fire();
//                User currentUser = ViewStaffHome.getTheUser();
//                metrics = ControllerUserMetrics.getUserMetrics(stage, currentUser, "student1");
//                // Assert launch
//                
//            } finally {
//                latch.countDown();
//            }
//        });
//        
//		try {
//			latch.await(2, TimeUnit.SECONDS);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} //
//		
//		List<Object[]> postsMetrics = (List<Object[]>) metrics.get("postsMetrics");
//		List<Object[]> repliesMetrics = (List<Object[]>) metrics.get("repliesMetrics");
//		assertTrue(metrics != null);
//		assertTrue(postsMetrics.size() == 5);
//		assertTrue(repliesMetrics.size() == 0);
//	}
//
//	
//	/**
//	 * Test that authorized staff1 can retrieve 1 post and 5 replies for student2.
//	 */
//	@Test
//	public void test4() {
//        CountDownLatch latch = new CountDownLatch(1);
//        
//        Platform.runLater(() -> {
//            try {
//                Stage stage = new Stage();
//                new FoundationsMain().start(stage);
//                ViewUserLogin.getText_Username().setText("staff1");
//                ViewUserLogin.getText_Password().setText("");
//                ViewUserLogin.getButton_Login().fire();
//                User currentUser = ViewStaffHome.getTheUser();
//                metrics = ControllerUserMetrics.getUserMetrics(stage, currentUser, "student2");
//                // Assert launch
//                
//            } finally {
//                latch.countDown();
//            }
//        });
//        
//		try {
//			latch.await(2, TimeUnit.SECONDS);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} //
//		
//		List<Object[]> postsMetrics = (List<Object[]>) metrics.get("postsMetrics");
//		List<Object[]> repliesMetrics = (List<Object[]>) metrics.get("repliesMetrics");
//		assertTrue(metrics != null);
//		assertTrue(postsMetrics.size() == 1);
//		assertTrue(repliesMetrics.size() == 6);
//		
//	}
//	/*********     Helpers     *********/
//	
//	private void preformLogin(String username, String password) {
//        CountDownLatch latch = new CountDownLatch(1);
//
//        Platform.runLater(() -> {
//            try {
//                Stage stage = new Stage();
//                new FoundationsMain().start(stage);
//
//                ViewUserLogin.getText_Username().setText(username);
//                ViewUserLogin.getText_Password().setText(password);
//                ViewUserLogin.getButton_Login().fire();
//                // Assert launch
//                assert(stage.getScene() != null);
//            } finally {
//                latch.countDown();
//            }
//        });
//        
//		try {
//			latch.await(1, TimeUnit.SECONDS);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} // wait for FX thread
//	}
//}
