import java.io.IOException;


public class TestParser1 {
		public static void main(String[] args) {
			
			
			Scanner ts1,ts2,ts3,ts4,ts5;
			try {
				
				ts1 = new Scanner("test1.pas");
				ts2 = new Scanner("test2.pas");
				ts3 = new Scanner("test3.pas");
				ts4 = new Scanner("test4.pas");
				ts5 = new Scanner("test5.pas");
			} catch (IOException e) {
				
				e.printStackTrace();
				return;
			}
			
			MiniPascal mp1 = new MiniPascal(ts1);
			MiniPascal mp2 = new MiniPascal(ts2);
			MiniPascal mp3 = new MiniPascal(ts3);
			MiniPascal mp4 = new MiniPascal(ts4);
			MiniPascal mp5 = new MiniPascal(ts5);
			
			mp1.parse();
			mp2.parse();
			mp3.parse();
			mp4.parse();
			mp5.parse();

	}
}
