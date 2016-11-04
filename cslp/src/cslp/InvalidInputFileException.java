package cslp;

/**
 * InvalidInputFileException class
 *
 */
public class InvalidInputFileException extends Exception {
	
	/**
	 * Throws an exception for invalid file format.
	 * Output on console the error message.
	 * 
	 * @param message
	 */
	public InvalidInputFileException(String message) {
		super(message);
	}
}
