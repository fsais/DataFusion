package fusion;

public class Utils {


	//*************************************************
	// Value UTIL FUNCTIONS 
	//*************************************************

	
	public static String removePunctuations1(String s) {
	    String res = "";
	    for (Character c : s.toCharArray()) {
	        if(Character.isLetterOrDigit(c))
	            res += c;
	    }
	    return res; //.toLowerCase().trim());
	}
	
	public static String removePunctuations(String x) {
	    String tmp;

	    tmp = x.toLowerCase();
	    tmp = tmp.replace(",", "");
	    tmp = tmp.replace(".", "");
	    tmp = tmp.replace(";", "");
	    tmp = tmp.replace("!", "");
	    tmp = tmp.replace("?", "");
	    tmp = tmp.replace("(", "");
	    tmp = tmp.replace(")", "");
	    tmp = tmp.replace("{", "");
	    tmp = tmp.replace("}", "");
	    tmp = tmp.replace("[", "");
	    tmp = tmp.replace("]", "");
	    tmp = tmp.replace("<", "");
	    tmp = tmp.replace(">", "");
	    tmp = tmp.replace("%", "");
	    tmp = tmp.replace("/", "");
	    tmp = tmp.replace("-", "");
	    tmp = tmp.replace("_", "");
	    tmp = tmp.replace("#", "");
	    return (String)tmp.toString();
	}

	
}
