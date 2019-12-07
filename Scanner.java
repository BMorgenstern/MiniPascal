import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;

import java.util.HashMap;
import java.util.List;


public class Scanner extends Tokenstream {
	private StreamTokenizer strtok;
	
	private HashMap<Identifier, Var> vars = new HashMap<>();
	private HashMap<Identifier,PD> procedures = new HashMap<>();
	private boolean NumisReal;
	
	
	public Scanner(String filename) throws IOException {
	try {
		strtok = new StreamTokenizer(new FileReader(filename));
		setup();
		next();
		} catch (IOException e){
			throw new IOException("Error opening file " + filename); 
		}
	}

	public int line() {
		return strtok.lineno();
	}

	private void setup() {
		
		strtok.resetSyntax();
		//strtok.parseNumbers();

		strtok.quoteChar('\'');
		strtok.wordChars('A', 'z');
		strtok.wordChars('0', '9');
		strtok.slashSlashComments(true);
		strtok.whitespaceChars('\t', '\t');
		strtok.whitespaceChars('\n', '\n');
		strtok.whitespaceChars('\r', '\r');

		//strtok.ordinaryChar('-');
		//strtok.ordinaryChar('.');
		
		
		strtok.ordinaryChar('[');
		strtok.ordinaryChar(']');
	}
	public void pushBack() 
	{
		this.strtok.pushBack();
	}
	public void next() 
	{
		if (EOF != tok)
		{
			try 
			{	
				tok = strtok.nextToken();
				if(tok >= '0' && tok <= '9') 
				{
					strtok.sval = String.format("%s", tok);
				}
				if(StreamTokenizer.TT_WORD==tok) 
				{
					try 
					{
						nval = Integer.parseInt(strtok.sval);
						tok = Tokenstream.INTEGER;

						if('.' == strtok.nextToken()) 
						{
							try 
							{
								strtok.nextToken();
								int decpart = Integer.parseInt(strtok.sval);
								String rep = String.format("%d.%d", (int)nval, decpart);
								nval = Double.parseDouble(rep);
								tok = Tokenstream.DOUBLE;
							}
							catch(NumberFormatException e) 
							{
								strtok.pushBack();
								strtok.pushBack();
								return;
							}
						}
						else 
						{
							strtok.pushBack();
							return;
						}
					}
							
					catch(NumberFormatException e) 
					{
						sval = strtok.sval;
						tok = Tokenstream.STRING;				
					}
					strtok.sval = null;
					return;
				}
				if(' ' == tok)
				{
					next();
					return;
				}	
				if('{' == tok)
				{
					if('*' != strtok.nextToken()) 
					{
						strtok.pushBack();
						return;
					}
					boolean exited = false;
					tok = strtok.nextToken();
					while(!exited)
					{
						
							tok=strtok.nextToken();
							while( '*' != tok )
							{
								tok=strtok.nextToken();
							}
							tok=strtok.nextToken();
							exited = ('}' == tok);
					}
					
					next();
					
				}
					
				switch (tok) 
				{
					case StreamTokenizer.TT_EOF:
						tok = EOF; break;
					case StreamTokenizer.TT_NUMBER:
						nval = strtok.nval; tok = DOUBLE; break;
					case StreamTokenizer.TT_WORD:
						sval = strtok.sval;
					default:
						break;
				}
			} 
			catch (IOException e) 
			{ 
				throw new IllegalArgumentException(e.getMessage()); 
			}
		}
	}

		public String toString() 
		{
			switch (tok) 
			{
				case StreamTokenizer.TT_EOF: case EOF:
					return "<EOF>";
				case StreamTokenizer.TT_NUMBER: case DOUBLE:
					return ""+this.nval;
				case StreamTokenizer.TT_WORD: case STRING:
					return ""+this.sval;
				default:
					return "" + (char)tok;
			}
		}
		public void ErrorMsg() 
		{
			String found = this.sval;
			if (null == found)
			{
				found = String.valueOf(this.tok);
			}
			throw new ParseError("Error on line "+this.line()+'\n'+"Found "+found+" instead");
		}
		public void ErrorMsg(String expected) 
		{
			String found = this.sval;
			if (null == found)
			{
				found = String.valueOf(this.tok);
			}
			throw new ParseError("Error on line "+this.line()+'\n'+"Expected "+expected+'\n'+"Found "+found+" instead");
		}
		public String thisToken() 
		{
			if(Tokenstream.STRING == this.tok)
			{
				return this.sval;
			}
			else if(Tokenstream.DOUBLE == this.tok) 
			{
				return String.valueOf(this.nval);
			}
			else 
			{
				return String.valueOf((char)this.tok);
			}
		}
		public boolean anyMatchingToken(String[] toks) 
		{
			for(String tok: toks) 
			{
				if(matchToken(tok))
				{
					return true;
				}
			}
			return false;
		}
		public boolean anyMatchingToken(List<String> toks) 
		{
			return toks.contains(thisToken());
		}
		
		public boolean matchToken(String lookingFor) 
		{
			
			if(thisToken().equals(lookingFor)) 
			{
				return true;
			}
			else 
			{
				return false;
			}
		}
		public boolean nextIfMatch(String lookingFor) {
			if(matchToken(lookingFor)){
				this.next();
				return true;
			}
			return false;
		}
		
		public boolean nextIfAnyMatches(String[] toks) {
			if(anyMatchingToken(toks)){
				this.next();
				return true;
			}
			return false;
		}
		
		public boolean nextIfAnyMatches(List<String> toks) {
			if(anyMatchingToken(toks)){
				this.next();
				return true;
			}
			return false;
		}
		private boolean isNum() 
		{
			sign();
			return (Tokenstream.DOUBLE==this.tok || Tokenstream.INTEGER==this.tok);
		}
		private Val IntorReal(Sign sign) 
		{
			int multFactor = 1;
			if(null != sign) 
			{
				if(sign.toString().equals("-")) 
				{
					multFactor = -1;
				}
			}
			if(Tokenstream.DOUBLE==this.tok) 
			{
				return new Real(this.nval*multFactor);
			}
			else
			{
				return new Int((int)this.nval*multFactor);
			}
		}
		public Val getVal() 
		{
			Sign sign = sign();
			
			if(isNum()) 
			{
				return IntorReal(sign);
			}
			return null;
			
		}
		
		public void advanceOrError(String tok) throws Exception {
			if(!this.nextIfMatch(tok)) { 
				this.ErrorMsg(tok);
			}
		}
		
		public void advanceOrError(String[] toks) throws Exception {
			if(!this.nextIfAnyMatches(toks)) {
				String msg = "";
				for(String s:toks) {
					msg+=s;
					msg+=", or ";
				}
				this.ErrorMsg(msg);
			}
		}

	/*
	 * 
	 * Token getters
	 * 
	 * */
		private boolean isAVar(String id) 
		{
			return (null != getVar(id));
		}
		private Var getVar(String id) 
		{
			for (Identifier currid : vars.keySet()) 
			{
				if(currid.getID().toString().equals(id)) 
				{
					return vars.get(currid);
				}
			}
			
			return null;
		}
		
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
		
		
		private Identifier identifier() {
			for (String keyword: keywords) 
				if(this.sval.equals(keyword)) 
					return null;
			for (String pre: predef) 
				if(this.sval.equals(pre)) 
					return null;
			if(Tokenstream.STRING == this.tok && (this.sval.charAt(0) >= 'A' && this.sval.charAt(0) <= 'z')) 
				return new Identifier(this.sval);
			
			
			return null;
		}
		
		private void program() throws Exception{
			this.advanceOrError("program");
		}
		public Identifier identOrError() {
			Identifier ident = identifier();
			if (null==ident) 
			{
				this.ErrorMsg("An Identifier");
				return null;
			}
			else 
			{
				return ident;
			}
		}
		public Program scan() {
			try {
				program();
				Identifier progId = identOrError();
				this.next();
				this.advanceOrError(";");
				Block progBlock = block();
				this.advanceOrError(".");
				System.out.println("Parse successful");
				return new Program(progId, progBlock);
			}
			catch(Exception e) {
				
				System.err.println("Parse unsuccessful"+'\n'+e);
			}
			return null;
		}
		private Block block() throws Exception {
			return new Block(vardecpart(),procdecpart(),statementpart());
			
		}
		private VDP vardecpart() throws Exception {
			VDP vdp = new VDP(); 
			
			if(!this.nextIfMatch("var")) 
			{
				return null;//nullable
			}
			do
			{
				vdp.addVD(vardec());
			}while(null!=identifier());
			
			for(VD vd : vdp.getVDs()) 
			{
				Type vdtype = vd.getType();
				for(Identifier id : vd.getIds())
				{
					vars.put(id, new Var(id, vdtype));
				}
			}
			
			return vdp;
			
		}
		
		
		private VD vardec() throws Exception {
			Identifier id = identOrError();
			VD vdToAdd = new VD();
			vdToAdd.addID(id);
			this.next();
			while(this.matchToken(",")) {
				this.next();
				vdToAdd.addID(identOrError());
				this.next();
			}
			this.advanceOrError(":");
			vdToAdd.setType(type());
			this.advanceOrError(";");
			
			//fix this later
			//vars.add(thisvardec);
			return vdToAdd;
		}
		
		private SP statementpart() throws Exception 
		{
			return new SP(compstatement());	
		}
		private CompS compstatement() throws Exception {
			this.advanceOrError("begin");
			CompS comps = new CompS();
			do 
			{
				comps.addStatement(statement());
			}
			while(this.nextIfMatch(";"));
			this.advanceOrError("end");
			return comps;
			
		}
		private Statement statement() throws Exception {
				
				if(this.matchToken("begin")) 
				{
					return compstatement();
				}
				else if(this.nextIfMatch("if")) 
				{
					Expression e = expression();
					this.advanceOrError("then");
					Statement s = statement();
					if(this.nextIfMatch("else"))
					{
						return new IfS(e,s,statement());
					}
					return new IfS(e,s);
				}
				else if(this.nextIfMatch("read"))
				{
					return read();
				}
				else if(this.nextIfMatch("write"))
				{
					return write();
				}
				else if(this.nextIfMatch("while")) 
				{
					Expression e = expression();
					this.advanceOrError("do");
					return new WhileS(e, statement());
				}
				else if(isAVar(this.sval)) 
				{
					Var var = getVar(thisToken());
					this.next();
					variable(var);
					Expression assignment = assignment();
					if(!vars.containsKey(var.getID())) 
					{
						throw new Exception(String.format("Variable %s was not defined", var.getID()));
						
					}
					vars.get(var.getID()).setNumtype(assignment.getType());
					return new AssignS(var, assignment);
				}
				
			return null;
			
		}
		private Statement read() throws Exception {
			
			this.advanceOrError("(");
			if(!isAVar(this.sval))
			{
				this.ErrorMsg("A variable");
				throw new Error();
			}
			ReadS reads = new ReadS(getVar(thisToken()));
			this.next();
			//variable();
			while(this.matchToken(",")) 
			{
				this.next();
				
				if(!isAVar(this.sval))
				{
					this.ErrorMsg("A variable");
					throw new Error();
				}
				reads.addVar((getVar(thisToken())));
				this.next();
				//variable();
			}
			
			this.advanceOrError(")");
			return reads;
			
		}
		
		private WriteS write() throws Exception {
			
			this.advanceOrError("(");
			WriteS writes = new WriteS(expression());
			
			while(this.matchToken(",")) {
				this.next();
				writes.addExpr(expression());
			}
			
			this.advanceOrError(")");
			return writes;
		}

		private Expression assignment() throws Exception {
			this.advanceOrError(":");
			this.advanceOrError("=");
			//do type getting--setting--
			return expression();
			
		}

		private Expression expression() throws Exception
		{
			SimpExpr simpE = simpexp();
			Expression expr = simpE;
			expr.setType(simpE.getType());
			RelOp relop = rel();
			while(null != relop)
			{
				expr.add(relop);
				expr.add(simpexp());
				relop = rel();
			}
			return expr;//fix later...--.
				
		}
		private RelOp rel() throws Exception {
			String op;
			if(this.nextIfMatch("<")) 
			{
				op = "<";
				if(this.matchToken("=") || this.matchToken(">"))
				{
					op += thisToken();
					next();
					
				}
					
				return new RelOp(op);
			}
			else if(this.nextIfMatch(">")) 
			{
				op = ">";
				if(this.matchToken("="))
				{
					op += thisToken();
					next();
					
				}
				return new RelOp(op);
			}
			else if(this.nextIfMatch("=")) 
			{
				return new RelOp("=");
			}

			return null;
		}
		private SimpExpr simpexp() throws Exception{
			try 
			{
					
				
				Sign nextsign = sign(); 
				Term term = term();
				SimpExpr simpE = new SimpExpr(nextsign, term);
				Op nextOp = add();
				while(null != nextOp)
				{
					Term termToAdd = term();
					if(term.sameType(termToAdd))
					{
						simpE.addTerm(termToAdd, nextOp);
						nextOp = add();
					}
					
					else 
					{
						throw new Exception(String.format("Cannot apply %s to different types", nextOp.toString()));
					}
				}
				return simpE;
			}catch(NullPointerException npe) 
			{
				System.err.print(true);
				return null;
			}
		}

		private Term term() throws Exception 
		{
			Term retTerm = new Term(factor());
			Op multop = mult();
			while(null != multop)
			{
				Factor toMult = factor();
				if(retTerm.sameType(toMult)) 
				{
					retTerm.addMultOp(multop, toMult);
					multop = mult();
				}
				else 
				{
					throw new Exception(String.format("Cannot apply %s to different types", multop.toString()));
				}
				
			};
			return retTerm;
		}

		private Factor factor() throws Exception {
			Factor factor = null;
			if(this.isNum()) 
			{
				if(Tokenstream.DOUBLE == tok) 
				{
					factor = new Factor(getVal(), ftype.real);
				}
				else if(Tokenstream.INTEGER == tok)
				{
					factor = new Factor(getVal(), ftype.integer);
				}
				else 
				{
					factor = new Factor(getVal());
				}
				this.next();
				return factor;
			}
			else if(isAVar(thisToken())) 
			{
				Var v = getVar(this.thisToken());
				factor = new Factor(v, v.getNumtype());
				this.next();
				
				return factor;
			}
			else if(this.nextIfMatch("not"))
				factor();
			else if(this.nextIfMatch("(")) {
				Expression e = expression();
				this.advanceOrError(")");
				return new Factor(e);
			}
				
			return null;
		}
		private Op add() {
			if(this.anyMatchingToken(add)) {
				Op nextop = new Op(thisToken());
				this.next(); 
				return nextop;
			}
			else 
			{
				return null;
			}
		}
		private Op mult() {
			if(this.anyMatchingToken(mult)) {
				Op op = new Op(thisToken());
				this.next(); 
				return op;
			}
			else 
				return null;
		}
		private Sign sign() {
			if(this.matchToken("+") ||this.matchToken("-")) 
			{
				Sign sign = new Sign(thisToken());
				this.next();
				return sign;
			}
			return null;	
			
		}

		private PDP procdecpart() throws Exception {
			PDP pdp = null; 
			while(this.matchToken("procedure")) {
				PD pro = procdec();
				if(null == pdp) 
				{
					pdp = new PDP();
				}
				pdp.addProcedure(pro);
				procedures.put(pro.getID(), pro);
			}
			return pdp;
			
		}
		private PD procdec() throws Exception{
			this.next();
			Identifier id = identOrError();
			//procedures.add(this.sval);
			this.next();
			this.advanceOrError(";");
			Block block = block();
			return new PD(id, block);
		}
		
			
		private Type type() throws Exception 
		{
			if(this.nextIfMatch("array")) 
			{
				return arrayType();
			} 
			return simpType();
		}
		
		
		
		private SimpleType simpType() throws Exception 
		{
			if(this.anyMatchingToken(predef)) 
			{
				SimpleType s = new SimpleType(this.sval);
				next();
				return s;
			}
			throw new Exception();
		}
		private ArrayType arrayType() throws Exception 
		{
			Int begin = null;
			Int end = null;
			
			this.advanceOrError("[");
				try 
				{
					begin = (Int) getVal();
					this.next();
				}
				catch(Exception e)
				{
					throw new Exception("Array bounds must be ints");
				}
			this.advanceOrError(".");
				try 
				{
					end = (Int) getVal();
					this.next();
				}
				catch(Exception e)
				{
					throw new Exception("Array bounds must be ints");
				}
				
			this.advanceOrError("]");
			this.advanceOrError("of");
			IndexRange range = new IndexRange(begin,end);
			SimpleType type = simpType();
			return new ArrayType(range, type);
			
		}
		

		private Expression variable(Var var) throws Exception 
		{
			/*
			 * error if brackets and not arrayType*/
			Expression retExp = null;
			if(this.nextIfMatch("[")) 
			{
				if(var.getType().getClass() != ArrayType.class) 
				{
					throw new Exception(String.format("Indexing of non-array variable %s", var.getID().toString()));
				}
				retExp = expression();
				this.advanceOrError("]");
			}
			return retExp;
		}
		
		
}




