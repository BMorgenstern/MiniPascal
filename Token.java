import java.util.ArrayList;
import java.util.List;

public interface Token{
	public String toString();
	public List<Token> subTokens();
}

class Program implements Token{
	private Identifier id;
	private Block block;
	public Program(Identifier id, Block block) {
		this.id = id;
		this.block = block;
	}
	public String toString() {
		return String.format("program %s; %s.", id.toString(), block.toString());
	}
	public List<Token> subTokens() {
		return null;
	}
}

class Identifier implements Token{
	private String label;
	public String toString() {
		return label;
	}
	public List<Token> subTokens() {
		return null;
	}
}

class Type implements Token{
	public List<Token> subTokens() {
		return null;
	}
}

class SimpleType extends Type{
	private Identifier typeID;
	public String toString() {
		return typeID.toString();
	}
}



class ArrayType extends Type{
	private IndexRange range;
	private SimpleType type;
	public String toString() {
		return String.format("array[%s] of %s", range.toString(), type.toString());
	}
}

class IndexRange implements Token{
	private Num begin;
	private Num end;
	public String toString() {
		return String.format("%s..%s", begin.toString(), end.toString());
	}

	public List<Token> subTokens() {
		return null;
	}
}

class Block implements Token{
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
	public String toString() {
		String ret = "var";
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
		return null;
	}
}


class PDP implements Token{
	private List<PD> pds;
	public String toString() {
		String ret = "";
		for(PD p : pds)
			ret += p.toString()+";";
		return ret;
	}

	public List<Token> subTokens() {
		return null;
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
		return null;
	}
}

class SP implements Token{
	private CompS s;
	public List<Token> subTokens() {
		return s.subTokens();
	}
}

class Statement implements Token{
	public List<Token> subTokens() {
		return this.subTokens();
	}	
}

class SimpS implements Token{
	public List<Token> subTokens() {
		return null;
	}
	
}

class CompS implements Token{
	public List<Statement> s;
	public List<Token> subTokens() {
		return ((Token) s).subTokens();
	}	
}

class Num implements Token{
	private Integer val;
	public String toString() {
		return val.toString();
	}
	public Num(int v) {
		val = v;
	}

	public List<Token> subTokens() {
		return null;
	}
}
