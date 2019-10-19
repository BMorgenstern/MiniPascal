import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.List;


public class Scanner extends Tokenstream {
	private StreamTokenizer strtok;
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
		
		strtok.whitespaceChars('\t', '\t');
		strtok.whitespaceChars('\n', '\n');
		strtok.whitespaceChars('\r', '\r');
		
		strtok.ordinaryChar('-');
		strtok.ordinaryChar('.');
		strtok.ordinaryChar('[');
		strtok.ordinaryChar(']');
	}
	
		public void next() {
			if (tok != EOF)
				try {
					
					tok = strtok.nextToken();
					if(strtok.sval != null) {
						try {
							nval = Integer.parseInt(strtok.sval);
							tok = Tokenstream.DOUBLE;
						}
							
						catch(NumberFormatException e) {
							sval = strtok.sval;
							tok = Tokenstream.STRING;
							
						}
						strtok.sval = null;
						return;
					}
					if(tok == ' ')
						next();
					switch (tok) {
					case StreamTokenizer.TT_EOF:
						tok = EOF; break;
					case StreamTokenizer.TT_NUMBER:
						nval = strtok.nval; tok = DOUBLE; break;
					case StreamTokenizer.TT_WORD:
						sval = strtok.sval;
					default:
						break;
					}
				} catch (IOException e) { throw new IllegalArgumentException(e.getMessage()); }
		}

		public String toString() {
			switch (tok) {
			case StreamTokenizer.TT_EOF: case EOF:
				return "<EOF>";
			case StreamTokenizer.TT_NUMBER: case DOUBLE:
				return ""+strtok.nval;
			case StreamTokenizer.TT_WORD: 
				return ""+strtok.sval;
			default:
				return "" + (char)tok;
			}
		}
		public void ErrorMsg() {
			String found = this.sval;
			if (found == null)
				found = String.valueOf(this.tok);
			throw new ParseError("Error on line "+this.line()+'\n'+"Found "+found+" instead");
		}
		public void ErrorMsg(String expected) {
			String found = this.sval;
			if (found == null)
				found = String.valueOf(this.tok);
			throw new ParseError("Error on line "+this.line()+'\n'+"Expected "+expected+'\n'+"Found "+found+" instead");
		}
		public boolean anyMatchingToken(String[] toks) {
			for(String tok: toks)
				if(matchToken(tok))
					return true;
			return false;
		}
		public boolean anyMatchingToken(List<String> toks) {
			String match;
			if(this.tok == Tokenstream.STRING)
				match = this.sval;
			else {
				match = String.valueOf((char)this.tok);
			}
			return toks.contains(match);
		}
		public boolean matchToken(String lookingFor) {
			String match;
			if(this.tok == Tokenstream.STRING)
				match = this.sval;
			else {
				match = String.valueOf((char)this.tok);
			}
			if(match.equals(lookingFor)) {
				//System.out.println(lookingFor+" found");
				return true;
			}
			else {
				//ErrorMsg(lookingFor);
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
		
		public boolean isNum() {
			return this.tok==Tokenstream.DOUBLE;
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
}




