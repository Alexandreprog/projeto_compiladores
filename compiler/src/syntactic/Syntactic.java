package syntactic;

import java.util.List;

import lexical.Token;
import semantic.Semantic;
import semantic.exceptions.TypeCompatibilityErrorExceptions;
import semantic.exceptions.VariableDeclarationErrorException;
import semantic.exceptions.enums.VariableDeclarationStatus;
import syntactic.exceptions.BadConstructionConditionalErrorException;
import syntactic.exceptions.ExpectedTokenSyntacticErrorException;
import syntactic.exceptions.SyntacticErrorException;
import syntactic.exceptions.TypeSyntacticalErrorException;
import syntactic.exceptions.enums.ExpectedPositionException;
import utils.TokenType;

public class Syntactic {
	private List<Token> tokenTable;
	private int lastToken;
	private Semantic semanticParser;
	
	public Syntactic(List<Token> tokenTable) {
		this.tokenTable = tokenTable;
		this.lastToken = 0;
		this.semanticParser = new Semantic();
	}
	
	public void check() {
		Program();
	}
	
	private void Program() {
		Token token = next();
		
		if (token.getToken().equals("program")){
			token = next();
			
			semanticParser.openScope();
			
			if(token.getType() == TokenType.IDENTIFIER) {
				semanticParser.pushIdentifier(token);
				token = next();	
				
				if(token.getToken().equals(";")) {
					token = next();
					
					token = variableDeclarations(token);
					
					token = subprogramDeclarations(token);
					
					token = compoundCommand(token);
					
					if(token.getToken().equals(".")) {
						semanticParser.closeScope();
						return;
					}
					else {
						throw new ExpectedTokenSyntacticErrorException(".", token, ExpectedPositionException.ON);
					}
				}
				else {
					throw new ExpectedTokenSyntacticErrorException(";", token, ExpectedPositionException.BEFORE);
				}
			}
		}
		else {
			throw new SyntacticErrorException("Undefined reference to 'program'", token.getRow());
		}
	}
	
	private Token variableDeclarations(Token token) {
		Token tk = token;
		
		if(token.getToken().equals("var")) {
			tk = next();	
			tk = listVariableDeclarations(tk);
		}
		else if(token.getType() == TokenType.IDENTIFIER) {
			throw new SyntacticErrorException("Undefined reference to 'var'", token.getRow());
		}
		
		return tk;
	}
	
	private Token listVariableDeclarations(Token token) {
		Token tk = token;
		
		tk = listIdentifiers(tk);
		
		if(tk.getToken().equals(":")) {
			tk = next();
			
			tokenType(tk);
			
			tk = next();
			
			if(tk.getToken().equals(";")) {
				tk = next();
				tk = listVariableDeclarationsBranch(tk);
			}
			else {
				throw new ExpectedTokenSyntacticErrorException(";", tk, ExpectedPositionException.BEFORE);
			}
		}
		else if(!tk.getToken().equals("begin") && !tk.getToken().equals("procedure")) {
			throw new ExpectedTokenSyntacticErrorException(":", tk, ExpectedPositionException.ON);
		}
		
		return tk;
	}
	
	private Token listVariableDeclarationsBranch(Token token) {
		Token tk = token;
		
		tk = listIdentifiers(tk);
		
		if(tk.getToken().equals(":")) {
			tk = next();
			
			tokenType(tk);
			
			tk = next();
			
			if(tk.getToken().equals(";")) {
				tk = listVariableDeclarationsBranch(next());
			}
			else {
				throw new ExpectedTokenSyntacticErrorException(";", tk, ExpectedPositionException.BEFORE);
			}
		}
		else if(!tk.getToken().equals("begin") && !tk.getToken().equals("procedure")) {
			throw new ExpectedTokenSyntacticErrorException(":", tk, ExpectedPositionException.ON);
		}
		
		return tk;
	}
	
	private Token listIdentifiers(Token token) {
		Token tk = token;
		
		if(tk.getType() == TokenType.IDENTIFIER) {
			if(!this.semanticParser.searchIdentifier(tk)) {
				this.semanticParser.pushIdentifier(tk);
				
				if(this.semanticParser.getScopeSize() > 1) {
					this.semanticParser.prepareVariablePackage(tk);
				}
				
				tk = next();
				tk = listIdentifiersBranch(tk);
			}
			else {
				throw new VariableDeclarationErrorException(tk, VariableDeclarationStatus.DECLARED);
			}
		}
		else if(!tk.getToken().equals("begin") && !tk.getToken().equals("procedure")){
			throw new ExpectedTokenSyntacticErrorException("an identifier", tk, ExpectedPositionException.BEFORE, ExpectedPositionException.ON);
		}
		
		return tk;
	}
	
	private Token listIdentifiersBranch(Token token) {
		Token tk = token;
		
		if(tk.getToken().equals(",")) {
			tk = next();
			
			if(tk.getType()  == TokenType.IDENTIFIER) {
				this.semanticParser.pushIdentifier(tk);
				this.semanticParser.prepareVariablePackage(tk);
				tk = next();
				tk = listIdentifiersBranch(tk);
			}
			else {
				throw new ExpectedTokenSyntacticErrorException("an identifier", tk, ExpectedPositionException.BEFORE, ExpectedPositionException.ON);
			}
		}
		
		return tk;
	}
	
	private void tokenType(Token token) {
		
		if(token.getToken().equals("integer")) {
			this.semanticParser.closeVariablePackage(TokenType.INTEGER_NUMBER);
			return;
		}
		else if(token.getToken().equals("real")) {
			this.semanticParser.closeVariablePackage(TokenType.REAL_NUMBER);
			return;
		}
		else if(token.getToken().equals("boolean")) {
			this.semanticParser.closeVariablePackage(TokenType.RESERVED_WORD);
			return;
		}
		else {
			throw new TypeSyntacticalErrorException(token);
		}
	}
	
	private Token subprogramDeclarations(Token token) {
		return subprogramDeclarationsBranch(token);
	}
	
	private Token subprogramDeclarationsBranch(Token token) {
		Token tk = subprogramDeclaration(token);
		
		if(tk == null) {
			return token;
		}
		
		if(tk.getToken().equals(";")) {
			tk = next();
			
			tk = subprogramDeclarationsBranch(tk);
		}
		else {
			throw new ExpectedTokenSyntacticErrorException(";", tk, ExpectedPositionException.BEFORE);
		}
		
		return tk;
	}
	
	private Token subprogramDeclaration(Token token) {
		Token tk = token;
		
		if(tk.getToken().equals("procedure")){
			tk = next();		
			
			if(tk.getType() == TokenType.IDENTIFIER) {
				if(this.semanticParser.searchIdentifier(tk)) {
					throw new VariableDeclarationErrorException(tk, VariableDeclarationStatus.DECLARED);
				}
				
				this.semanticParser.pushIdentifier(tk); // Push identifier in last scope
				this.semanticParser.openScope();
				this.semanticParser.pushIdentifier(tk); // Push identifier in the current scope
				tk = next();

				tk = arguments(tk);
				
				if(tk.getToken().equals(";")) {
					tk = next();
					
					tk = variableDeclarations(tk);
					
					tk = subprogramDeclarations(tk);
					
					tk = compoundCommand(tk);
					
					this.semanticParser.closeScope();
					return tk;
				}
				
				throw new ExpectedTokenSyntacticErrorException(";", tk, ExpectedPositionException.BEFORE);
			}
			
			throw new SyntacticErrorException("Expected an identifier but was found: ", tk);
		}
		else if(!tk.getToken().equals("begin")) {
			throw new ExpectedTokenSyntacticErrorException("procedure", tk, ExpectedPositionException.ON);
		}
		
		return null;
	}
	
	private Token arguments(Token token) {
		Token tk = token;
		
		if(tk.getToken().equals("(")) {
			tk = next();
			
			tk = listParameters(tk);
			
			if(tk.getToken().equals(")")) {
				return next();
			}
		}
		else if(tk.getToken().equals(";")) {
			return tk;
		}
		
		throw new SyntacticErrorException("Poorly formatted list of parameters", tk.getRow());
	}
	
	private Token listParameters(Token token) {
		Token tk = token;
		
		tk = listIdentifiers(token);
		
		if(tk.getToken().equals(":")) {
			tk = next();
			tokenType(tk);
			
			tk = next();
			
			tk = listParametersBranch(tk);
		}
		else {
			throw new ExpectedTokenSyntacticErrorException(":", tk, ExpectedPositionException.ON);
		}
		
		return tk;
	}
	
	private Token listParametersBranch(Token token) {
		Token tk = token;
		
		if(tk.getToken().equals(";")) {
			tk = next();
			
			tk = listIdentifiers(tk);
			
			if(tk.getToken().equals(":")) {
				tk = next();
				
				tokenType(tk);
				
				tk = next();
				
				tk = listParametersBranch(tk);
			}
		}
		
		return tk;
	}
	
	private Token compoundCommand(Token token) {
		Token tk = token;
		
		if(tk.getToken().equals("begin")) {
			tk = next();
			
			tk = optionalCommands(tk);
			
			if(!tk.getToken().equals("end")) {
				throw new ExpectedTokenSyntacticErrorException("end", tk, ExpectedPositionException.BEFORE);
			}
			
			Token rt = next();
			rt.setRow(tk.getRow());
			
			return rt;
		}
		
		return tk;
	}
	
	private Token optionalCommands(Token token) {
		Token tk = token;
		
		if(tk.getType() == TokenType.IDENTIFIER || tk.getToken().equals("if") || tk.getToken().equals("while") || tk.getToken().equals("begin")) {
			return commandList(tk);
		}
		
		return tk;
	}
	
	private Token commandList(Token token) {
		Token tk = token;
		
		tk = command(tk);
		
		tk = commandListBranch(tk);
		
		if(!this.semanticParser.checkType()) {
			throw new TypeCompatibilityErrorExceptions(tk, ExpectedPositionException.BEFORE);
		}
		
		return tk;
	}
	
	private Token commandListBranch(Token token) {
		Token tk = token;
		
		if(tk.getToken().equals(";")) {
			
			if(!this.semanticParser.checkType()) {
				throw new TypeCompatibilityErrorExceptions(tk, ExpectedPositionException.ON);
			}
			
			tk = command(next());
			tk = commandListBranch(tk);
			return tk;
		}
		else if(tk.getToken().equals("if") || tk.getToken().equals("while") || tk.getToken().equals("begin")) {
			throw new ExpectedTokenSyntacticErrorException(";", tk, ExpectedPositionException.BEFORE);
		}
		
		return tk;		
	}
	
	private Token command(Token token) {
		Token tk = token;
		
		Token aux = id(tk);
		
		if(!aux.getToken().equals(tk.getToken())){
			
			if(!this.semanticParser.isIdentifierDeclared(tk)) {
				throw new VariableDeclarationErrorException(tk, VariableDeclarationStatus.UNDECLARED);
			}
			
			this.semanticParser.pushToken(tk);

			if(aux.getToken().equals(":=")) {
				tk = next();
				tk = expression(tk);
			}
			
			else {
				tk = procedureActivationBranch(aux);
			}
		}
		else {
			aux = compoundCommand(tk);
			
			if(!aux.getToken().equals(tk.getToken())){
				tk = aux;
			}
			
			else {
				
				if(tk.getToken().equals("if")) {
					this.semanticParser.pushToken(tk);
					tk = next();
					tk = expression(tk);
					
					if(tk.getToken().equals("then")) {
						this.semanticParser.checkValidityCond(tk);
						tk = next();
						tk = command(tk);
						
						tk = elsePart(tk);
					}
					else {
						throw new BadConstructionConditionalErrorException("Expected 'then', but was found ", tk);
					}
				}
				
				else {
					if(tk.getToken().equals("while")) {
						this.semanticParser.pushToken(tk);
						tk = next();
						
						tk = expression(tk);
						
						if(tk.getToken().equals("do")) {
							this.semanticParser.checkValidityCond(tk);
							tk = next();
							
							tk = command(tk);
						}
						else {
							throw new ExpectedTokenSyntacticErrorException("do", tk, ExpectedPositionException.ON);
						}
					}
					else {
						throw new SyntacticErrorException("A ';' was not expected", tk.getRow(), ExpectedPositionException.BEFORE);
					}
				}
			}
		}
		
		return tk;
	}
	
	private Token elsePart(Token token) {
		Token tk = token;
		
		if(tk.getToken().equals("else")) {
			tk = command(next());
		}
		
		return tk;
	}
	
	private Token procedureActivationBranch(Token token) {
		Token tk = token;
		
		if(tk.getToken().equals("(")) {
			tk = listExpressions(next());
			
			if(tk.getToken().equals(")")) {
				return next();
			}
			
			throw new ExpectedTokenSyntacticErrorException(")", tk, ExpectedPositionException.BEFORE);
		}
		else if(tk.getToken().equals(";")) {
			return tk;
		}
		
		throw new ExpectedTokenSyntacticErrorException("(", tk, ExpectedPositionException.ON);
	}
	
	private Token id(Token token) {
		
		if(token.getType() == TokenType.IDENTIFIER) {
			return next();
		}
		
		return token;
	}
	
	private Token expression(Token token) {
		Token tk = token;
		
		tk = term2Branch(tk);
		
		if(tk.getToken().equals(token.getToken())) {
			tk = simpleExpression(tk);
		}
		
		Token tk2 = expressionBranch(tk);
		
		if(tk2.getToken().equals(tk.getToken())) {
			tk2 = term3Branch(tk);
		}
		
		return tk2;
	}
	
	private Token expressionBranch(Token token) {
		Token tk = token;
		
		if(tk.getType() == TokenType.RELATIONAL_OPERATOR) {
			relationalOp(tk);
			
			tk = next();
			
			tk = simpleExpression(tk);
			
			this.semanticParser.checkValidityRelOp(tk);
		}
		
		return tk;
	}
	
	private Token simpleExpression(Token token) {
		Token tk = token;
		
		Token aux = term(tk);
		
		if(aux.getToken().equals(tk.getToken())) {
			signal(tk);
			
			tk = next();
			
			tk = term(tk);
			aux = tk;
		}
		
		return simpleExpressionBranch(aux);
	}
	
	private Token simpleExpressionBranch(Token token) {
		Token tk = token;
		
		if(tk.getType() == TokenType.ADDITIVE_OPERATOR) {
			additiveOp(tk);
			
			tk = next();
			
			tk = term(tk);
			
			this.semanticParser.checkOpType(tk);
			
			tk = simpleExpressionBranch(tk);
		}
		
		return tk;
	}
	
	private Token term(Token token) {
		Token tk = token;
		
		Token aux = factor(tk);
		
		return termBranch(aux);
	}
	
	private Token termBranch(Token token) {
		Token tk = token;
		
		if(tk.getType() == TokenType.MULTIPLICATIVE_OPERATOR) {
			multiplicativeOp(tk);
			
			tk = next();
			
			Token aux = factor(tk);
			
			this.semanticParser.checkOpType(tk);
			
			if(aux.getToken().equals(tk.getToken())) {
				return tk;
			}
			
			tk = aux;
			
			tk = termBranch(tk);
		}
		
		return tk;
	}
	
	private Token term2Branch(Token token) {
		Token tk = token;
		
		if(tk.getType() == TokenType.RESERVED_WORD) {
			this.semanticParser.pushToken(tk);
			logOp(next());
			
			tk = next();
			
			Token aux = factor2Branch(tk);
			
			this.semanticParser.checkOpType(tk);
			
			if(aux.getToken().equals(tk.getToken())) {
				return tk;
			}
			
			tk = aux;
			
			tk = term3Branch(tk);
		}
		
		return tk;
	}
	
	private Token term3Branch(Token token) {
		Token tk = token;
		
		if(tk.getType() == TokenType.LOGICAL_OPERATOR) {
			logOp(tk);
			
			tk = next();
			
			Token aux = factor2Branch(tk);
			
			this.semanticParser.checkOpType(tk);
			
			if(aux.getToken().equals(tk.getToken())) {
				return tk;
			}
			
			tk = aux;
			
			tk = term3Branch(tk);
		}
		
		return tk;
	}
	
	private void logOp(Token tk) {
		
		if(tk.getToken().equals("and")) {
			return;
		}
		else if(tk.getToken().equals("or")) {
			return;
		}
		else if(tk.getToken().equals("not")) {
			throw new RuntimeException();
		}
	}
	
	private Token factor(Token token) {
		Token tk = token;
		
		if(tk.getType() == TokenType.IDENTIFIER) {
			if(!this.semanticParser.isIdentifierDeclared(tk)) {
				throw new VariableDeclarationErrorException(tk, VariableDeclarationStatus.UNDECLARED);
			}
			
			this.semanticParser.pushToken(tk);
			tk = next();
			
			return factorBranch(tk);
		}
		else if(tk.getType() == TokenType.INTEGER_NUMBER) {
			this.semanticParser.pushToken(tk);
			return next();
		}
		else if(tk.getType() == TokenType.REAL_NUMBER) {
			this.semanticParser.pushToken(tk);
			return next();
		}
		else if(tk.getToken().equals("(")) {
			tk = next();
			
			tk = expression(tk);
			
			if(!tk.getToken().equals(")")) {
				throw new ExpectedTokenSyntacticErrorException("A ) was expected, but was found: " + tk.getToken());
			}
			
			tk = next();
		}
		else if(tk.getToken().equals("not")) {
			tk = next();
			
			tk = factor2Branch(tk);
		}
		
		return tk;
	}
	
	private Token factorBranch(Token token) {
		Token tk = token;
		
		if(tk.getToken().equals("(")) {
			tk = next();
			
			tk = listExpressions(tk);
			
			if(!tk.getToken().equals(")")) {
				throw new ExpectedTokenSyntacticErrorException(")", tk, ExpectedPositionException.ON);
			}
			
			tk = next();
		}
		
		return tk;
	}
	
	private Token factor2Branch(Token token) {
		Token tk = token;

		if(tk.getToken().equals("True")) {
			this.semanticParser.pushToken(tk);
			return next();
		}
		else if(tk.getToken().equals("False")) {
			this.semanticParser.pushToken(tk);
			return next();
		}
		else if(tk.getToken().equals("(")) {
			tk = next();
			
			tk = expression(tk);
			
			if(!tk.getToken().equals(")")) {
				throw new ExpectedTokenSyntacticErrorException("A ) was expected, but was found: " + tk.getToken());
			}
			
			tk = next();
		}
		else if(tk.getToken().equals("not")) {
			tk = next();
			
			tk = factor2Branch(tk);
		}
		else if(this.semanticParser.getTokenType(tk) == TokenType.RESERVED_WORD) {
			this.semanticParser.pushToken(tk);
			return next();
		}
		
		return tk;
	}
	
	private Token listExpressions(Token token) {
		Token tk = expression(token);
		
		return listExpressionBranch(tk);
	}
	
	private Token listExpressionBranch(Token token) {
		Token tk = token;
		
		if(tk.getToken().equals(",")) {
			tk = next();
			
			tk = expression(tk);
			
			tk = listExpressionBranch(tk);
		}
		
		return tk;
	}
	
	private void multiplicativeOp(Token tk) {
		
		if(tk.getToken().equals("*")) {
			return;
		}
		else if(tk.getToken().equals("/")) {
			return;
		}
	}
	
	private void additiveOp(Token tk) {
		
		if(tk.getToken().equals("+")) {
			return;
		}
		else if(tk.getToken().equals("-")) {
			return;
		}
	}
	
	private void signal(Token tk) {
		if(tk.getToken().equals("+")) {
			return;
		}
		else if(tk.getToken().equals("-")) {
			return;
		}
	}
	
	private void relationalOp(Token tk) {
		if(tk.getToken().equals("=")) {
			return;
		}
		else if(tk.getToken().equals("<")) {
			return;
		}
		else if(tk.getToken().equals(">")) {
			return;
		}
		else if(tk.getToken().equals("<=")) {
			return;
		}
		else if(tk.getToken().equals(">=")) {
			return;
		}
		else if(tk.getToken().equals("<>")) {
			return;
		}
	}
	
	private Token next() {
		
		if(lastToken < tokenTable.size()) {
			return tokenTable.get(lastToken++);
		}
		
		return new Token("", TokenType.EOF);
	}
}