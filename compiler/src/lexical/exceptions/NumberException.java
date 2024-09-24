package lexical.exceptions;

public class NumberException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	
	public NumberException(String msg, int row, int col) {
		super(msg + ": row " + row + ", col " + col);
	}
}
