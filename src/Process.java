import javax.swing.JFrame;


public class Process {
	private static final int WIDTH = 1920;
	private static final int HEIGHT = 1080;
	
	public static void main(String args[]){
		
		JFrame frame = new ImageFrame( WIDTH, HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

	}
}
