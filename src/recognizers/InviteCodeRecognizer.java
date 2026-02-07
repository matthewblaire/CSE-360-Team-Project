package recognizers;




/**
 * This class contains the methods necessary to recognize valid invite code formatting. 
 */
public class InviteCodeRecognizer {
	
	
	/**
	 * Method: evaluateInviteCode
	 * Description: This method evaluates a string as a valid invite code. If the string follows the invite code format,
	 * the method will return an empty string. If not, a specific error message will be returned.
	 * @param code
	 * @return errorMessage
	 */
	public static String evaluateInviteCode(String code) {
		if (code.length() != 6)
		{
			return "Invite code is not formatted correctly. Please check that the code is entered correctly and try again.";
		}
		return "";
	}

}
