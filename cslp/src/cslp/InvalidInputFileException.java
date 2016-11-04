package cslp;

/**
 * An exception class for any invalid input file format.
 * An error message will be displayed while throwing and exception.
 * 
 * @author home
 *
 */
public class InvalidInputFileException extends Exception {
	
	public InvalidInputFileException(String message) {
		super(message);
	}
}
