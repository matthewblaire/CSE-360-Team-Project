package recognizers;

public class TitleRecognizer {
	public static int MAX_TITLE_LENGTH = 255;
	
	public static String evaluateTitle(String title)
	{
		if (title.isEmpty())
		{
			return "Title cannot be empty.";
		}
		if (title.length() > MAX_TITLE_LENGTH) {
			return "Title cannot be longer than 255 characters.";
		}
		return "";
	}
}
