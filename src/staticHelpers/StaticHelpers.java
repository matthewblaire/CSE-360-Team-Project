package staticHelpers;

import entityClasses.User;
import javafx.stage.Stage;

public class StaticHelpers {
	
	public static void routeUserToHomeScreen(Stage theStage, User theUser)
	{
		if (theUser.getNumRoles() > 1)
		{
			guiMultipleRoleDispatch.ViewMultipleRoleDispatch.
			displayMultipleRoleDispatch(theStage, theUser);
		} else {
			
			// Admin role
			if (theUser.getAdminRole()) {
				guiAdminHome.ViewAdminHome.displayAdminHome(theStage, theUser);
			} else if (theUser.getNewStudentRole()) {
				guiStudent.ViewStudentHome.displayStudentHome(theStage, theUser);
			} else if (theUser.getNewStaffRole()) {
				guiStaff.ViewStaffHome.displayStaffHome(theStage, theUser);
								// Other roles
			} else {
				System.out.println("*****  routeUserToHomeScreen request has an invalid role");
			}
			
		}
	}

}
