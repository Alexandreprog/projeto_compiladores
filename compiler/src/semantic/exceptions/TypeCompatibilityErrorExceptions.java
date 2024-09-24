package semantic.exceptions;

import lexical.Token;
import syntactic.exceptions.enums.ExpectedPositionException;

public class TypeCompatibilityErrorExceptions extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	
	public TypeCompatibilityErrorExceptions(Token tk, ExpectedPositionException epe) {
		super("Type mismatch " + epe.toString().toLowerCase() + " line " + tk.getRow());
	}
}
