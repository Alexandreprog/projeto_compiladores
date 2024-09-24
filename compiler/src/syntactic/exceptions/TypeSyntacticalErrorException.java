package syntactic.exceptions;

import lexical.Token;

public class TypeSyntacticalErrorException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	
	public TypeSyntacticalErrorException(Token token) {
		super("Unknown type on row " + token.getRow());
	}
}
