package semantic;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import lexical.Token;
import utils.TokenType;

public class Scope {
	private Stack<Token> identifiers;
	private Map<String, TokenType> typeIdentifiers;
	private int closeScope;
	
	public Scope() {
		this.identifiers = new Stack<>();
		this.typeIdentifiers = new HashMap<>();
		this.closeScope = 0;
	}
	
	public void push(Token identifier) {
		
		if(this.identifiers != null && identifier != null) {
			this.identifiers.push(identifier);
		}
	}
	
	public boolean isPossibleClose() {
		if(this.closeScope == 0) {
			return true;
		}
		
		return false;
	}
	
	public void incCloseScope() {
		this.closeScope++;
	}
	
	public void decCloseScope() {
		
		if(this.closeScope > 0) {
			this.closeScope--;
		}
	}
	
	public boolean searchToken(Token token) {
		if(this.identifiers.search(token) != -1) {
			return true;
		}
		
		return false;
	}
	
	public void insertIdentifier(Token token, TokenType tokenType) {
		this.typeIdentifiers.put(token.getToken(), tokenType);
	}
	
	public TokenType getType(Token token) {
		return this.typeIdentifiers.get(token.getToken());
	}
	
	public int getScopeSize() {
		return this.identifiers.size();
	}
	
	public TokenType getIdentifierType(Token token) {
		return this.typeIdentifiers.get(token.getToken());
	}
}
