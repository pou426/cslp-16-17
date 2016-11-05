package cslp;

public class Error {

	private static boolean containsError = false;
	public static void throwError(String message) {
		containsError = true;
		System.out.println(message);
	}
	public static boolean getContainsError() {
		return containsError;
	}
}
