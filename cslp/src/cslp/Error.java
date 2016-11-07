package cslp;

/**
 * Error class
 *
 */
public class Error {

	//private static boolean containsError = false;
	
	/**
	 * prints error and set containsError flag to true 
	 * @param message		an error message containing error information
	 */
	public static void throwError(String message) {
		/*if (!containsError)	{
			containsError = true;
		}
*/		System.err.println(message);
		System.err.println("The simulation will terminate.");
		System.exit(1);
	}
	/*public static boolean getContainsError() {
		return containsError;
	}*/
}
