import java.io.IOException;

class Main{
	
	public static void main(String[] args) throws IOException {
     	try {
 		@SuppressWarnings("unused")
		MusicPlayerGUI gui = new MusicPlayerGUI();
		}// end try
		catch (IOException e){
			System.out.println(e.getMessage());
			return;
		}// end catch
		
	}// end main()
	
}// end Main class 