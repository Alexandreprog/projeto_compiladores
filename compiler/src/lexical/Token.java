package lexical;

import java.util.Objects;

import utils.TokenType;

public class Token {
	private String token;
	private TokenType type;

	private int row;
	private int column;
	
	public Token() {
		
	}
	
	public Token(String token, TokenType type) {
		this.token = token;
		this.type = type;
		
		this.row = -1;
		this.column = -1;
	}
	
	public Token(Token token) {
		this.token = token.getToken();
		this.type = token.getType();
		
		this.row = -1;
		this.column = -1;
	}
	
	public String getToken() {
		return token;
	}
	
	public TokenType getType() {
		return type;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getColumn() {
		return column;
	}
	
	public void setRow(int row) {
		this.row = row;
	}
	
	public void setColumn(int column) {
		this.column = column;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(token, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Token other = (Token) obj;
		return Objects.equals(token, other.token) && type == other.type;
	}	
	
	@Override
	public String toString() {
		return "Token [" + token + ", " + type + "]";
	}
}
