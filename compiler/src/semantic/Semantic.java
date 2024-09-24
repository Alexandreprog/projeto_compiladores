package semantic;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

import lexical.Token;
import semantic.exceptions.TypeCompatibilityErrorExceptions;
import syntactic.exceptions.enums.ExpectedPositionException;
import utils.TokenType;

public class Semantic {
	private List<Scope> scopes;
	private int currentScope;
	private Stack<TokenType> stackType; 
	private List<Token> variablePackage;
	
	public Semantic() {
		this.scopes = new ArrayList<>();
		this.currentScope = -1;
		this.stackType = new Stack<>();
		this.variablePackage = new ArrayList<>();
	}
	
	public void openScope() {
		this.scopes.add(new Scope());
		this.currentScope++;
	}
	
	public void beginCountScope() {
		this.scopes.get(currentScope).incCloseScope();
	}
	
	public void endCountScope() {
		this.scopes.get(currentScope).decCloseScope();
	}
	
	public void closeScope() {
		
		if(this.scopes.get(currentScope).isPossibleClose()) {
			this.scopes.remove(currentScope);
			this.currentScope--;
		}
	}
	
	public void pushIdentifier(Token token) {
		this.scopes.get(currentScope).push(token);
	}
	
	public boolean searchIdentifier(Token token) {
		return this.scopes.get(currentScope).searchToken(token);
	}
	
	public TokenType getTokenType(Token token) {
		return this.scopes.get(currentScope).getType(token);
	}
	
	public boolean isIdentifierDeclared(Token token) {
		int scope = this.currentScope;
		
		while(scope >= 0) {
			if(this.scopes.get(scope).searchToken(token)) {
				return true;
			}
			else {
				scope--;
			}
		}
		
		return false;
	}
	
	public void closeVariablePackage(TokenType tokenType) {
		
		for (Token token : this.variablePackage) {
			this.scopes.get(currentScope).insertIdentifier(token, tokenType);
		}
		
		this.variablePackage.clear();
	}
	
	public void prepareVariablePackage(Token token) {
		this.variablePackage.add(token);
	}
	
	public void pushToken(Token token) {
		int currentScopeAux = currentScope;
		TokenType tokenType = null;
		
		while(currentScopeAux >= 0) {
			tokenType = this.scopes.get(currentScopeAux).getIdentifierType(token);
			
			if(tokenType == null) {
				currentScopeAux--;
			}
			else {
				break;
			}
		}
		
		if(tokenType == null) {
			this.stackType.add(token.getType());
		}
		else {
			this.stackType.add(tokenType);
		}
	}
	
	public boolean checkType() {
		try {
			TokenType last = stackType.pop();
			
			while(stackType.size() > 0) {
				TokenType tt = stackType.pop();
				
				if(tt != last && 
					last == TokenType.REAL_NUMBER && tt == TokenType.INTEGER_NUMBER) {
					return false;
				}
				
				last = tt;
			}
		}
		catch(EmptyStackException e) {
			return true;
		}
		
		return true;
	}
	
	public boolean checkOpType(Token tk) {
		TokenType v2 = stackType.pop();
		
		if(v2 == TokenType.INTEGER_NUMBER) {
			TokenType v1 = stackType.pop();
			
			if(v1 == TokenType.INTEGER_NUMBER) {
				stackType.push(TokenType.INTEGER_NUMBER);
			}
			else if(v1 == TokenType.REAL_NUMBER) {
				stackType.push(TokenType.REAL_NUMBER);
			}
			else {
				throw new TypeCompatibilityErrorExceptions(tk, ExpectedPositionException.BEFORE);
			}
		}
		else if(v2 == TokenType.REAL_NUMBER) {
			TokenType v1 = stackType.pop();
			
			if(v1 == TokenType.INTEGER_NUMBER) {
				stackType.push(TokenType.REAL_NUMBER);
			}
			else if(v1 == TokenType.REAL_NUMBER) {
				stackType.push(TokenType.REAL_NUMBER);
			}
			else {
				throw new TypeCompatibilityErrorExceptions(tk, ExpectedPositionException.BEFORE);
			}
		}
		else if(v2 == TokenType.RESERVED_WORD) {
			TokenType v1 = stackType.pop();
			
			if(v1 == v2) {
				stackType.push(TokenType.RESERVED_WORD);
			}
			else {
				throw new TypeCompatibilityErrorExceptions(tk, ExpectedPositionException.BEFORE);
			}
		}
		else {
			throw new TypeCompatibilityErrorExceptions(tk, ExpectedPositionException.ON);
		}
		
		return true;
	}
	
	public boolean checkValidityRelOp(Token tk) {
		TokenType v2 = stackType.pop();
		
		if(v2 == TokenType.INTEGER_NUMBER || v2 == TokenType.REAL_NUMBER) {
			TokenType v1 = stackType.pop();
			
			if(v1 == TokenType.INTEGER_NUMBER || v1 == TokenType.REAL_NUMBER) {
				stackType.push(TokenType.RESERVED_WORD);
				return true;
			}
		}
		
		throw new TypeCompatibilityErrorExceptions(tk, ExpectedPositionException.ON);
	}
	
	public boolean checkValidityCond(Token tk) {
		TokenType v2 = stackType.pop();
		
		if(v2 == TokenType.RESERVED_WORD) {
			TokenType v1 = stackType.pop();
			
			if(v1 == TokenType.RESERVED_WORD) {
				return true;
			}
		}
		
		throw new TypeCompatibilityErrorExceptions(tk, ExpectedPositionException.ON);	
	}
	
	public int getScopeSize() {
		return this.scopes.get(currentScope).getScopeSize();
	}
}
