package syntactic.exceptions;

import lexical.Token;

public class BadConstructionConditionalErrorException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	
	public BadConstructionConditionalErrorException(String msg, Token token) {
		super(msg + token.getToken() + " on row " + token.getRow());
	}
}
