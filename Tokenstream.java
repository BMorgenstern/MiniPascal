
abstract class Tokenstream {
	final static int DOUBLE = -97;
	final static int NAME = -98;
	final static int STRING = -98;
	final static int EOF = -100;
	
	public int tok;
	public double nval; 
	public String sval;
	
	abstract public void next();
	
	public IllegalArgumentException ParseError(String msg)
	{
		return new ParseError(msg + " but found "+ this);
	}
	
	class ParseError extends IllegalArgumentException {
		public ParseError(String s)
		{super(s); }
	}
	

}
