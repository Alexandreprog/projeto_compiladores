package semantic.exceptions;

import lexical.Token;
import semantic.exceptions.enums.VariableDeclarationStatus;

public class VariableDeclarationErrorException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	
	public VariableDeclarationErrorException(Token token, VariableDeclarationStatus vds) {
		super("Variable '" + token.getToken() + "' in line '" + token.getRow() + "' " 
				+ vds.toString().toLowerCase() + " in the scope");
	}
}
