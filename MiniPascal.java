public class MiniPascal {
	private Scanner ts;
	
	public MiniPascal(Scanner ts) {
		this.ts = ts;
	}
	
	public Token parse() 
	{
		return ts.scan();
	}
};