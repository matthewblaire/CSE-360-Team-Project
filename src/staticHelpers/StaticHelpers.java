package staticHelpers;

import entityClasses.User;
import javafx.stage.Stage;

public class StaticHelpers {
	
	public static void routeUserToHomeScreen(Stage theStage, User theUser)
	{
		if (theUser.getNumRoles() > 1 && theUser.getCurrentRole() == null)
		{
			guiMultipleRoleDispatch.ViewMultipleRoleDispatch.
			displayMultipleRoleDispatch(theStage, theUser);
		} else {
			
			// Admin role
			if (theUser.getCurrentRole().equals("Admin")) {
				guiAdminHome.ViewAdminHome.displayAdminHome(theStage, theUser);
			} else if (theUser.getCurrentRole().equals("Student")) {
				guiStudent.ViewStudentHome.displayStudentHome(theStage, theUser);
			} else if (theUser.getCurrentRole().equals("Staff")) {
				guiStaff.ViewStaffHome.displayStaffHome(theStage, theUser);
								// Other roles
			} else {
				System.out.println("*****  routeUserToHomeScreen request has an invalid role");
			}
			
		}
	}

}
