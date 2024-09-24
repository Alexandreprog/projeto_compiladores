package lexical;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lexical.exceptions.LexicalErrorException;
import lexical.exceptions.NumberException;
import utils.TokenType;

public class Scanner {
	private char[] sourceBuffer;
	private int sourceBufferSize;
	private Map<String, Token> reservedWords;
	private List<Token> tokenTable;
	private int pos;
	private int row;
	private int col;
	private int lastCol;
	
	public Scanner(String source) {
		this.reservedWords = new HashMap<>();
		this.reservedWords.put("program", new Token("program", TokenType.RESERVED_WORD));
		this.reservedWords.put("var", new Token("var", TokenType.RESERVED_WORD));
		this.reservedWords.put("integer", new Token("integer", TokenType.RESERVED_WORD));
		this.reservedWords.put("real", new Token("real", TokenType.RESERVED_WORD));
		this.reservedWords.put("boolean", new Token("boolean", TokenType.RESERVED_WORD));
		this.reservedWords.put("procedure", new Token("procedure", TokenType.RESERVED_WORD));
		this.reservedWords.put("begin", new Token("begin", TokenType.RESERVED_WORD));
		this.reservedWords.put("end", new Token("end", TokenType.RESERVED_WORD));
		this.reservedWords.put("if", new Token("if", TokenType.RESERVED_WORD));
		this.reservedWords.put("then", new Token("then", TokenType.RESERVED_WORD));
		this.reservedWords.put("else", new Token("else", TokenType.RESERVED_WORD));
		this.reservedWords.put("while", new Token("while", TokenType.RESERVED_WORD));
		this.reservedWords.put("do", new Token("do", TokenType.RESERVED_WORD));
		this.reservedWords.put("True", new Token("True", TokenType.RESERVED_WORD));
		this.reservedWords.put("False", new Token("False", TokenType.RESERVED_WORD));
		
		tokenTable = new ArrayList<>();
		
		
		this.pos = 0;
		this.row = 0;
		this.col = -1;
		this.lastCol = -1;
		try {
			String buffer = new String(Files.readAllBytes(Paths.get(source)));
			this.sourceBuffer = buffer.toCharArray();
			this.sourceBufferSize = this.sourceBuffer.length;
		} catch (IOException e) {
			System.out.println("Arquivo informado não encontrado: " + e.getMessage());
		}
	}
	
	public List<Token> generateTokenTable(){
		Token token = null;
		
		do {
			try {
				token = new Token(nextToken());
				
				token.setRow(row);
				
				int tokenPos = col - token.getToken().length();
				
				token.setColumn(tokenPos < 0 ? 0 : tokenPos);
				
				tokenTable.add(token);
			}
			catch(NullPointerException e) {
				token = null;
			}
		}while(token != null);
		
		return tokenTable;
	}
	
	private Token nextToken() {
		char currentChar;
		String content = "";
		
		while(true) {
			if(isEOF()) {
				break;
			}
			
			currentChar = nextChar();
			
			if(isLetter(currentChar) || currentChar == '_') {
				content += currentChar;
				content += processString();
				
				if(reservedWords.containsKey(content)) {
					return reservedWords.get(content);
				}
				else if(isLogicalOperator(content)) {
					return new Token(content, TokenType.LOGICAL_OPERATOR);
				}
				
				return new Token(content, TokenType.IDENTIFIER);
			}
			
			else if(isAdditiveOperator(Character.toString(currentChar))) {
				content += currentChar;
				return new Token(content, TokenType.ADDITIVE_OPERATOR);
			}

			else if(isMultiplicativeOperator(Character.toString(currentChar))) {
				content += currentChar;
				return new Token(content, TokenType.MULTIPLICATIVE_OPERATOR);
			}
			
			else if(isRelationalSymbols(currentChar)) {
				return processRelationalSymbols(currentChar);
			}
			
			else if(isDelimiters(currentChar)) {
				return processDelimiters(currentChar);
			}
			
			else if(Character.isDigit(currentChar)) {
				return processNumericString(currentChar);
			}
			
			else if(currentChar == '#') {
				processComments();
			}
			
			else if(!isSpecialCharacter(currentChar)){
				throw new LexicalErrorException("Unrecognized character", this.row, this.col);
			}
		}
		
		return null;
	}
	
	private String processString() {
		char currentChar = nextChar();;
		String content = "";
		
		while(checkAllowedCharactersIdentifier(currentChar)) {
			content += currentChar; 
			currentChar = nextChar();
		}
		
		if(isUnrecognizedCharacters(currentChar)) {
			throw new LexicalErrorException("Unrecognized character", this.row, this.col);
		}
		
		back();
		
		return content;
	}
	
	private Token processRelationalSymbols(char c) {
		char currentChar;
		String content = "";
		
		content += c;
		
		if(c == '>') {
			currentChar = nextChar();
			if(currentChar == '=') {
				content += currentChar;
			}
			else{
				back();
			}
		}
		else if(c == '<') {
			currentChar = nextChar();
			if(currentChar == '=' || currentChar == '>') {
				content += currentChar;
			}
			else{
				back();
			}
		}
		
		return new Token(content, TokenType.RELATIONAL_OPERATOR);
	}
	
	private Token processNumericString(char c) {
		char currentChar = nextChar();
		String content = "";
		
		content += c;
		
		while(Character.isDigit(currentChar)) {
			content += currentChar;
			currentChar = nextChar();
		}
		
		if(isBadFormattedNumber(currentChar)) {
			throw new NumberException("Badly formatted number", this.row, this.col);
		}
		
		if(currentChar == '.') {
			content += currentChar;
			content += processFloatingPoint();
			back();
			return new Token(content, TokenType.REAL_NUMBER);
		}
		
		back();
		return new Token(content, TokenType.INTEGER_NUMBER);
	}
	
	private String processFloatingPoint() {
		char currentChar = nextChar();
		String content = "";
		
		while(Character.isDigit(currentChar)) {
			content += currentChar;
			currentChar = nextChar();
		}
		
		if(currentChar == '.' || isLetter(currentChar)) {
			throw new NumberException("Badly formatted number", this.row, this.col);
		}
		
		return content;
	}
	
	private Token processDelimiters(char c) {
		String content = "";
		
		content += c;
		
		if(c == ';') {
			return new Token(content, TokenType.DELIMITERS);
		}
		else if(c == '.') {
			return new Token(content, TokenType.DELIMITERS);
		}
		else if(c == ':') {
			char new_c = nextChar();
			
			if(new_c == '=') {
				content += new_c;
				return new Token(content, TokenType.ASSIGNMENT);
			}
			
			back();			
			return new Token(content, TokenType.DELIMITERS);
		}
		else if(c == '(') {
			return new Token(content, TokenType.DELIMITERS);
		}
		else if(c == ')') {
			return new Token(content, TokenType.DELIMITERS);
		}
		else {
			return new Token(content, TokenType.DELIMITERS);
		}
	}
	
	private void processComments() {
		char currentChar;
		
		do {
			currentChar = nextChar();
		}while(currentChar != '\n' && currentChar != '\r' && !isEOF());
	}
	
	private boolean isSpecialCharacter(char c) {
		return c == ' ' || c == '\n' || c == '\t' || c == '\r';
	}
	
	private boolean isAdditiveOperator(String c) {
		return c.equals("+") || c.equals("-");
	}
	
	private boolean isMultiplicativeOperator(String c) {
		return c.equals("*") || c.equals("/");
	}
	
	private boolean isLogicalOperator(String c) {
		return c.equals("and") || c.equals("or") || c.equals("not");
	}
	
	private boolean isRelationalSymbols(char c) {
		return c == '>' || c == '<' || c == '!' || c == '=';
	}
	
	private boolean isDelimiters(char c) {
		return c == ';' || c == '.' || c == ':' || c == '(' || c == ')' || c == ',';
	}
	
	private boolean isLetter(char c) {
		return c >= 65 && c <= 90 || c >= 97 && c <= 122;
	}
	
	private boolean isBadFormattedNumber(char c) {
		String aux = "";
		aux += c;
		
		return isLetter(c) && !isAdditiveOperator(aux) && c != '#' || 
				isUnrecognizedCharacters(c) && !isAdditiveOperator(aux) && c != '#';
	}
	
	private boolean isUnrecognizedCharacters(char c) {
		return !checkAllowedCharactersIdentifier(c) &&
		!isSpecialCharacter(c) && 
		!isRelationalSymbols(c) &&
		!isDelimiters(c) &&
		!isAdditiveOperator(Character.toString(c)) && 
		!isMultiplicativeOperator(Character.toString(c));
	}
	
	private boolean checkAllowedCharactersIdentifier(char c) {
		return isLetter(c) || Character.isDigit(c) || c == '_';
	}
	
	private char nextChar() {
		if(this.pos + 1 > this.sourceBufferSize) {
			pos++;
			return ' ';
		}
		
		char currentChar = sourceBuffer[pos++];
		this.col++;
		
		if(this.pos >= 1) {
			if(sourceBuffer[this.pos - 1] == '\n') {
				this.row++;
				this.lastCol = col;
				this.col = -1;
			}
		}
		
		return currentChar;
	}
	
	private void back() {
		
		if(pos <= this.sourceBufferSize && this.sourceBuffer[this.pos - 1] == '\n') {
			this.row--;
			this.col = this.lastCol;
		}
		else {
			if(col > 0 && !isEOF()) {
				this.col--;
			}
		}
		
		this.pos--;
	}
	
	private boolean isEOF() {
		if(pos >= this.sourceBufferSize) {
			return true;
		}
		return false;
	}
}