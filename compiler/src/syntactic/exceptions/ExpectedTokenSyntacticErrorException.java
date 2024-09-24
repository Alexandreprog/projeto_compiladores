package syntactic.exceptions;

import lexical.Token;
import syntactic.exceptions.enums.ExpectedPositionException;

public class ExpectedTokenSyntacticErrorException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	
	public ExpectedTokenSyntacticErrorException(String token) {
		super("Expected '" + token + "");
	}
	
	public ExpectedTokenSyntacticErrorException(String expected, Token token, ExpectedPositionException epe) {
		super("Expected '" + expected + "' " + epe.toString().toLowerCase() + " row " + token.getRow());
	}
	
	public ExpectedTokenSyntacticErrorException(String expected, Token token) {
		super("Expected '" + expected + "' after " + "'" + token.getToken() + "'" + " on row " + token.getRow());
	}
	
	public ExpectedTokenSyntacticErrorException(String expected, Token token, ExpectedPositionException tokenPos, ExpectedPositionException rowPos) {
		super("Expected '" + expected + "' " + tokenPos.toString().toLowerCase() + " '" + token.getToken() + "' " + rowPos.toString().toLowerCase() + " row " + token.getRow());
	}
}
