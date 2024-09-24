package syntactic.exceptions;

import lexical.Token;
import syntactic.exceptions.enums.ExpectedPositionException;

public class SyntacticErrorException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	
	public SyntacticErrorException(String msg, Token token) {
		super(msg + " " + token.getToken() + " on row " + token.getRow());
	}
	
	public SyntacticErrorException(String msg, int row) {
		super(msg + " on row " + row);
	}
	
	public SyntacticErrorException(String msg, int row, ExpectedPositionException epx) {
		super(msg + " " + epx.toString().toLowerCase() + " row " + row);
	}
}
