package lexical.exceptions;

public class LexicalErrorException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	
	public LexicalErrorException(String msg, int row, int col) {
		super(msg + ": row " + row + ", col " + col);
	}
}
