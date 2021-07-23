package def_pkg;

import javax.swing.*;

public class BM_System {
	
	public static void main(String[] args) {
		JFrame frame=new JFrame("Bank Managment System");
		frame.setIconImage(  new ImageIcon(System.getProperty("user.dir") + "\\src\\def_pkg\\ddot.png").getImage() );
		
		GUI interf = new GUI();
		Login_Account user = new Login_Account();
	    interf.openSignInForm(frame, user);
		frame.setSize(800,500);  
		frame.setVisible(true);
		frame.setDefaultCloseOperation(frame.DISPOSE_ON_CLOSE);
	}
}
