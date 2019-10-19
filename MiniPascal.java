import java.util.ArrayList;

public class MiniPascal {
	private Scanner ts;
	private static String keywords[] = {
			"div", "or", "and", "not", "while",
			"program", "var", "array", "of", "do",
			"procedure", "begin", "end", "integer", 
			"read", "write", "if", "else", "then"
	};
	private static String predef[] = {
			"integer", "Boolean", "true", "false"
	};
	private static String add[] = {
			"+", "-","or"
	};
	private static String mult[] = {
			"*", "div", "and"
	};
	private ArrayList<String> vars = new ArrayList<String>();
	private ArrayList<String> procedures = new ArrayList<String>();
	public MiniPascal(Scanner ts) {
		this.ts = ts;
	}
	
	private boolean identifier() {
		if(ts.tok != Tokenstream.STRING) 
			return false;
		for (String keyword: keywords) 
			if(ts.sval.equals(keyword)) 
				return false;
		for (String pre: predef) 
			if(ts.sval.equals(pre)) 
				return false;
		
		return (ts.sval.charAt(0) >= 'A' && ts.sval.charAt(0) <= 'z');
	}
	
	private void program() throws Exception{
		ts.advanceOrError("program");
	}
	public void identOrError() {
		if (!identifier())
			ts.ErrorMsg("An Identifier");
	}
	public void parse() {
		try {
			program();
			identOrError();
			ts.next();
			ts.advanceOrError(";");
			block();
			ts.advanceOrError(".");
			System.out.println("Parse successful");
		}
		catch(Exception e) {
			
			System.err.println("Parse unsuccessful"+'\n'+e);
		}
	}
	private void block() throws Exception {
		vardecpart();
		procdecpart();
		statementpart();
		
	}
	private void vardecpart() throws Exception {
		if(!ts.nextIfMatch("var"))
			return;//nullable
		do{vardec();}while(identifier());
		
	}
	private void statementpart() throws Exception {
		compstatement();
		
	}
	private void compstatement() throws Exception {
		ts.advanceOrError("begin");
		do {
			statement();
		}
		while(ts.nextIfMatch(";"));
		ts.advanceOrError("end");

		
	}
	private void statement() throws Exception {
			
			if(ts.matchToken("begin")) {
				compstatement();
			}
			else if(ts.nextIfMatch("if")) {
				expression();
				ts.advanceOrError("then");
				statement();
				if(ts.nextIfMatch("else"))
					statement();
			}
			else if(ts.nextIfMatch("read")){
				read();
			}
			else if(ts.nextIfMatch("write")){
				write();
			}
			else if(ts.nextIfMatch("while")) {
				expression();
				ts.advanceOrError("do");
				statement();
			}
			else if(vars.contains(ts.sval)) {
				ts.next();
				variable();
				assignment();
			}
			
		
		
	}
	private void read() throws Exception {
		ts.advanceOrError("(");
		if(!ts.anyMatchingToken(vars)){
			ts.ErrorMsg("A variable");
			throw new Error();
		}
		ts.next();
		variable();
		while(ts.matchToken(",")) {
			ts.next();
			
			if(!ts.anyMatchingToken(vars)){
				ts.ErrorMsg("A variable");
				throw new Error();
			}
			
			ts.next();
			variable();
		}
		
		ts.advanceOrError(")");
		
	}
	
	private void write() throws Exception {
		ts.advanceOrError("(");
		expression();
		
		while(ts.matchToken(",")) {
			ts.next();
			expression();
		}
		
		ts.advanceOrError(")");
		
	}

	private void assignment() throws Exception {
		ts.advanceOrError(":");
		ts.advanceOrError("=");
		expression();
		
	}

	private void expression() throws Exception{
		do{
			simpexp();
		}while(rel()); 
			
	}
	private boolean rel() throws Exception {
		if(ts.nextIfMatch("<")) {
			if(ts.nextIfMatch("=") || ts.nextIfMatch(">"))
				return true;
			return true;
		}
		else if(ts.nextIfMatch(">")) {
			if(ts.nextIfMatch("=")) 
				return true;
			return true;
		}
		else if(ts.nextIfMatch("="))
			return true;
		return false;
	}
	private void simpexp() throws Exception{
		sign(); term();
		if(add())
			term();
		
	}

	private void term() throws Exception {
		do{
			factor();
		}while(mult());
	}

	private void factor() throws Exception {
		if(ts.isNum()) {
			ts.next();
			return;
		}
		else if(ts.anyMatchingToken(vars)) {
			ts.next();
			variable();
			return;
		}
		else if(ts.nextIfMatch("not"))
			factor();
		else if(ts.nextIfMatch("(")) {
			expression();
			ts.advanceOrError(")");
		}
			
		
	}
	private boolean add() {
		if(ts.anyMatchingToken(add)) {
			ts.next(); 
			return true;
		}
		else 
			return false;
	}
	private boolean mult() {
		if(ts.anyMatchingToken(mult)) {
			ts.next(); 
			return true;
		}
		else 
			return false;
	}
	private void sign() {
		if(ts.matchToken("+") ||ts.matchToken("-"))
			ts.next();
		
	}

	private void procdecpart() throws Exception {
		procdec();
		
	}
	private void procdec() throws Exception{
		while(ts.matchToken("procedure")) {
			ts.next();
			identOrError();
			procedures.add(ts.sval);
			ts.next();
			ts.advanceOrError(";");
			block();
			ts.next();
			
		}
	}
	private void vardec() throws Exception {
		identOrError();
		vars.add(ts.sval);
		ts.next();
		while(ts.matchToken(",")) {
			ts.next();
			identOrError();
			vars.add(ts.sval);
			ts.next();
		}
		ts.advanceOrError(":");
		type();
		ts.advanceOrError(";");
	}
		
	private void type() throws Exception {
		if(ts.nextIfMatch("array")) {
			arrayType();
		} 
		simpType();
	}
	private void simpType() throws Exception {
		ts.advanceOrError(predef);
	}
	private void arrayType() throws Exception {
		ts.advanceOrError("[");
		if(ts.isNum()) 
			ts.next();
		else
			ts.ErrorMsg("Number");
		ts.advanceOrError(".");
		ts.advanceOrError(".");
		if(ts.isNum()) 
			ts.next();
		else
			ts.ErrorMsg("Number");
		ts.advanceOrError("]");
		ts.advanceOrError("of");
	}
	private void variable() throws Exception {
		if(ts.nextIfMatch("[")) {
			expression();
			ts.advanceOrError("]");
		}
	}
};
