import java.util.ArrayList;
import java.util.List;

enum ftype{uninitialized,integer,real};

public interface Token
{
	public String toString();
	public List<Token> subTokens();
}

class Keyword implements Token
{
	private final String key;
	public Keyword(String key)
	{
		this.key = key;
	}
	public String toString() 
	{
		return key;
	}
	public List<Token> subTokens() 
	{
		return null;
	}
	
}


class Program implements Token{
	private Keyword progKey = new Keyword("program");
	private Identifier id;
	private Block block;
	public Program(Identifier id, Block block) {
		this.id = id;
		this.block = block;
	}
	public String toString() {
		return String.format("%s %s; %s.", progKey, id.toString(), block.toString());
	}
	public List<Token> subTokens() {
		List<Token> t = new ArrayList<Token>();
		t.add(id); t.add(block);
		return t;
	}
}

class Identifier implements Token{
	private String label;
	public Identifier(String sval) 
	{
		this.label = sval;
	}
	public String toString() 
	{
		return label;
	}
	public List<Token> subTokens() 
	{
		return null;
	}
	public String getID() 
	{
		return label;
	}
}

class Type implements Token
{
	public List<Token> subTokens()
	{
		return null;
	}
}

class SimpleType extends Type
{
	private Identifier typeID;
	public SimpleType(Identifier typeID) 
	{
		super();
		this.typeID = typeID;
	}
	public SimpleType(String typeID) 
	{
		super();
		this.typeID = new Identifier(typeID);
	}
	public String toString() 
	{
		return typeID.toString();
	}
}



class ArrayType extends Type{
	public ArrayType(IndexRange range, SimpleType type) {
		super();
		this.range = range;
		this.type = type;
	}
	private IndexRange range;
	private SimpleType type;
	public String toString() {
		return String.format("array[%s] of %s", range.toString(), type.toString());
	}
}

class IndexRange implements Token{
	public IndexRange(Int begin, Int end) {
		super();
		this.begin = begin;
		this.end = end;
	}

	private Int begin;
	private Int end;
	public String toString() {
		return String.format("%s..%s", begin.toString(), end.toString());
	}

	public List<Token> subTokens() {
		return null;
	}
}

class Block implements Token{
	public Block(VDP vdp, PDP pdp, SP sp) {
		super();
		this.vdp = vdp;
		this.pdp = pdp;
		this.sp = sp;
	}

	private VDP vdp;
	private PDP pdp;
	private SP sp;
	public String toString() {
		return String.format("%s %s %s", vdp.toString(), pdp.toString(), sp.toString());
	}

	public List<Token> subTokens() {
		List<Token> t = new ArrayList<Token>();
		t.add(vdp); t.add(pdp); t.add(sp);
		return t;
	}
}


class VDP implements Token{
	
	
	private List<VD> vrs;
	public List<VD> getVDs()
	{
		return vrs;
	}
	public VDP(List<VD> vrs) {
		super();
		this.vrs = vrs;
	}
	public VDP() 
	{
		vrs = new ArrayList<>();
	}
	public void addVD(VD vd) 
	{
		vrs.add(vd); 
	}
	public String toString() {
		String ret = "var ";
		if(vrs.isEmpty())
			return "";
		for(VD v : vrs)
			ret += v.toString()+";";
		return ret;
	}

	public List<Token> subTokens() {
		return null;
	}
}

class VD implements Token{
	
	
	private List<Identifier> ids;
	private Type type;
	
	public List<Identifier> getIds() {
		return ids;
	}
	public void setIds(List<Identifier> ids) {
		this.ids = ids;
	}
	public Type getType() {
		return type;
	}
	public VD() 
	{
		super();
		this.ids = new ArrayList<Identifier>();
		this.type = new Type();
	}
	
	public void addID(Identifier id) {
		ids.add(id);
	}
	public void setType(Type t) {
		type = t;
	}
	public String toString() {
		String vars = ids.get(0).toString();
		for(int i = 1; i<ids.size(); i++)
			vars += String.format(", %s", ids.get(i).toString());
		vars += String.format(": %s", type.toString());
		return vars;
	}

	public List<Token> subTokens() {
		List<Token> t = new ArrayList<Token>(ids);
		return t;
	}
}


class Var implements Token
{
	private Val value = null;
	private Identifier id;
	private Type type;
	public Type getType() 
	{
		return type;
	}

	public void setType(Type type) 
	{
		this.type = type;
	}

	private ftype numtype=ftype.uninitialized;
	
	
	public Var(Identifier id, Type type) 
	{
		this.id = id;
		this.type = type;
	}
	
	public Var(String id, Type type) 
	{
		this.id = new Identifier(id);
		this.type = type;
	}
	
	public Identifier getID() 
	{
		return id;
	}
	
	public void setValue(Val val) 
	{
		this.value = val;
	}
	
	
	public String toString() 
	{
		String valstr;
		if(null == value) 
		{
			valstr = "no value";
		}
		else 
		{
			valstr = value.toString();
		}
		String typestr;
		if(null == type) 
		{
			typestr = "no type";
		}
		else 
		{
			typestr = type.toString();
		}
		return String.format("%s of type: %s has value: %s", id.toString(), typestr, valstr);
	}
	public List<Token> subTokens() 
	{
		return null;
	}

	public ftype getNumtype() {
		return numtype;
	}

	public void setNumtype(ftype numtype) {
		this.numtype = numtype;
	}
	
}

class PDP implements Token{
	private List<Token> pds;
	public PDP() 
	{
		pds = new ArrayList<>();
	}
	public PDP(PD pd) 
	{
		pds = new ArrayList<>();
		this.addProcedure(pd);
	}
	public String toString() {
		String ret = "";
		for(Token p : pds)
			ret += p.toString()+";";
		return ret;
	}
	public void addProcedure(PD pro) 
	{
		pds.add(pro);
	}
	public List<Token> subTokens() {
		return pds;
	}
}

class PD implements Token{
	private Identifier id;
	private Block block;
	public PD(Identifier id, Block block) {
		this.id = id;
		this.block = block;
	}
	public String toString() {
		return String.format("procedure %s; %s", id.toString(), block.toString());
	}

	public List<Token> subTokens() {
		List<Token> t = new ArrayList<>();
		t.add(id); t.add(block);
		return t;
	}
	public Identifier getID() {
		return id;
	}
}

class SP implements Token{
	public SP(CompS s) {
		super();
		this.s = s;
	}

	private CompS s;
	
	public List<Token> subTokens() {
		return s.subTokens();
	}
}

class Expression implements Token
{
	private List<Token> exprParts;
	private ftype type;
	public void add(Token t) 
	{
		exprParts.add(t);
	}
	public Expression(List<Token> exprParts) 
	{
		this.exprParts = exprParts;
	}
	public Expression(Expression expr) 
	{
		this.exprParts = new ArrayList<>();
		exprParts.add(expr);
	}
	public Expression() 
	{
		this.exprParts = new ArrayList<>();
	}
	public List<Token> subTokens() 
	{
		return exprParts;
	}
	public ftype getType() {
		return type;
	}
	public void setType(ftype type) {
		this.type = type;
	}
}

class AddingTerm
{
	private Term term;
	private Op op;
	public AddingTerm(Term term, Op op) 
	{
		this.term = term;
		this.op = op;
	}
	public String toString() 
	{
		return String.format(" %s %s", op.toString(), term.toString());
	}
}

class MultingFactor
{
	private Factor factor;
	private Op op;
	public MultingFactor(Factor factor, Op op) 
	{
		this.factor = factor;
		this.op = op;
	}
	public String toString() 
	{
		return String.format(" %s %s", op.toString(), factor.toString());
	}
}

class SimpExpr extends Expression
{
	
	private Sign sign;
	private Term term;
	private List<AddingTerm> addterms;
	public ftype getType() 
	{
		return term.getType();
	}
	public SimpExpr(Sign sign, Term term) {
		super();
		this.sign = sign;
		this.term = term;
		addterms = new ArrayList<>();
	}
	public void addTerm(Term term, Op op) 
	{
		addterms.add(new AddingTerm(term,op));
	}
	public void addTerm(AddingTerm term) 
	{
		addterms.add(term);
	}
	public String toString() 
	{
		String signstr = "";
		if(null!=sign) 
		{
			signstr = this.sign.toString();
		}
		signstr += term.toString();
		for(AddingTerm opterm : addterms) 
		{
			signstr += opterm.toString();
		}
		return signstr;
	}
}

class RelOp implements Token
{
	private String op;
	public String toString() 
	{
		return op;
	}
	public RelOp(String op) 
	{
		this.op = op;
	}
	public List<Token> subTokens() 
	{
		return null;
	}
	
}

class Term implements Token
{
	private Factor factor;
	private List<MultingFactor> multOps;
	public Term(Factor f) 
	{
		factor = f;
		multOps = new ArrayList<>();
	}
	public void addMultOp(MultingFactor term) 
	{
		multOps.add(term);
	}
	public void addMultOp(Op op, Factor factor) 
	{
		multOps.add(new MultingFactor(factor, op));
	}
	public ftype getType() 
	{
		return factor.getType();
	}
	public boolean sameType(Factor f) 
	{
		return factor.sameType(f);
	}
	public boolean sameType(Term t) 
	{
		return factor.sameType(t.factor);
	}
	public String toString() 
	{
		String retStr = factor.toString();
		for(MultingFactor mf : multOps) 
		{
			retStr += mf.toString();
		}
		return retStr;
	}
	public List<Token> subTokens() 
	{
		return null;
	}
	
}

class Factor implements Token
{
	private Token factor;
	private ftype type=ftype.uninitialized;
	
	
	public Factor(Token fac) 
	{
		factor = fac;
		type=ftype.uninitialized;
	}
	public ftype getType() {
		return this.type;
	}
	public Factor(Token fac, ftype type) 
	{
		factor = fac;
		this.type = type;
	}
	public boolean sameType(Factor f) 
	{
		if(f.type != ftype.uninitialized) 
		{
			if(this.type == ftype.uninitialized) 
			{
				this.type = f.type;
				return true;
			}
			return f.type.equals(this.type);
		}
		return true;
	}
	public List<Token> subTokens() 
	{
		return factor.subTokens();
	}
	
}
class Sign implements Token
{
	private char sign;
	public Sign(String sign) {
		this.sign = sign.charAt(0);
	}
	public String toString() 
	{
		return String.valueOf(sign);
	}
	public List<Token> subTokens() {
		return null;
	}
	
}

class Op implements Token
{
	private char op;
	public Op(String op) {
		this.op = op.charAt(0);
	}
	public String toString() 
	{
		return String.valueOf(op);
	}
	public List<Token> subTokens() {
		return null;
	}
	
}

class Statement implements Token
{
	public List<Token> subTokens() {
		return this.subTokens();
	}	
}


class SimpS extends Statement
{
	public List<Token> subTokens() 
	{
		return null;
	}
	
}

class ReadS extends SimpS
{
	private List<Token> invars;
	public ReadS() 
	{
		invars = new ArrayList<>();
	}
	public ReadS(Var var) 
	{
		invars = new ArrayList<>();
		invars.add(var);
	}
	public void addVar(Var var) 
	{
		invars.add(var);
	}

	public List<Token> subTokens() 
	{
		return invars;
	}
}

class WriteS extends SimpS
{
	private List<Token> outvars;
	public WriteS() 
	{
		outvars = new ArrayList<>();
	}
	public WriteS(Expression exp) 
	{
		outvars = new ArrayList<>();
		outvars.add(exp);
	}
	public void addExpr(Expression exp) 
	{
		outvars.add(exp);
	}
	public List<Token> subTokens() 
	{
		return outvars;
	}
}

class IfS extends SimpS
{
	private Expression expr;
	private Statement statement;
	private Statement elseStatement;
	
	public IfS(Expression expr, Statement statement, Statement elseStatement) {
		super();
		this.expr = expr;
		this.statement = statement;
		this.elseStatement = elseStatement;
	}
	public IfS(Expression expr, Statement statement) {
		super();
		this.expr = expr;
		this.statement = statement;
		elseStatement = null;
	}
	
	public List<Token> subTokens()
	{
		List<Token> t = new ArrayList<>();
		t.add(expr); t.add(statement);
		if(null != elseStatement) 
		{
			t.add(elseStatement);
		}
		return t;
	}
	public String toString() 
	{
		String first = String.format("if %s then %s", expr.toString(), statement.toString());
		if(null != elseStatement) 
		{
			first += String.format(" else %s", elseStatement.toString());
		}
		return first;
	}
}

class WhileS extends SimpS
{
	public WhileS(Expression expr, Statement statement) {
		super();
		this.expr = expr;
		this.statement = statement;
	}
	private Expression expr;
	private Statement statement;
	public List<Token> subTokens()
	{
		List<Token> t = new ArrayList<>();
		t.add(expr); t.add(statement);
		return t;
	}
	public String toString() 
	{
		return String.format("while %s do %s", expr.toString(), statement.toString());
	}
}

class AssignS extends SimpS
{
	private Var var;
	private Expression expr;
	
	
	public AssignS(Var var, Expression expr) {
		super();
		this.var = var;
		this.expr = expr;
	}
	public String toString() 
	{
		return String.format("%s := %s", var.toString(), expr.toString());
	}
	public List<Token> subTokens()
	{
		List<Token> t = new ArrayList<>();
		t.add(var); t.add(expr);
		return t;
	}
	
}

class CompS extends Statement{
	public List<Statement> s;
	public CompS() 
	{
		super();
		this.s = new ArrayList<>();
	}
	public CompS(List<Statement> s) {
		super();
		this.s = s;
	}
	public void addStatement(Statement statement) 
	{
		s.add(statement);
	}
	public List<Token> subTokens() {
		return ((Token) s).subTokens();
	}	
}

class Val implements Token
{
	public List<Token> subTokens() 
	{
		return null;
	}
	
}

class Int extends Val{
	private Integer val;
	public String toString() {
		return val.toString();
	}
	public Int(int v) {
		val = v;
	}

	public List<Token> subTokens() {
		return null;
	}
}


class Real extends Val{
	private Double val;
	public String toString() {
		return val.toString();
	}
	public Real(double v) {
		val = v;
	}

	public List<Token> subTokens() {
		return null;
	}
}

