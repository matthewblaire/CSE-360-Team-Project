package recognizers;

/**
 * This class contains the methods necessary to recognize a valid first, middle, or last name.
 */
public class NameRecognizer {
	
	public static int MAX_NAME_LENGTH = 50;

	/**
	 * Method: evaluateName
	 * Description: This method evaluates a string as a valid name. If the string is not empty, has an appropriate
	 * length, and does not contain any special characters except hyphens, the method will return an empty string.
	 * If not, a specific error message will be returned.
	 * @param name
	 * @return errorMessage
	 */
	public static String evaluateName(String name) {
		
		if (name.isEmpty())
		{
			return "Name must not be empty. Please enter a name and try again.";
		}
		
		if (name.length() > MAX_NAME_LENGTH)
		{
			return "Name is too long. Please shorten and try again.";
		}
		
		// Make sure no special characters are present
		for (int i = 0; i < name.length(); i++)
		{
			if (("~`!@#$%^&*()_+={}[]|\\:;\"'<>,.?/".indexOf(name.charAt(i)) >= 0)) {
				return "Name cannot contain any special characters. Please remove <" + name.charAt(i) + "> and try again.";
			}
		}
		return "";
	}
	
}
