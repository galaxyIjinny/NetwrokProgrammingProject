
import java.awt.Dimension;
import java.io.Serializable;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

class FriendLabel extends JLabel { // 채팅방 관련 유저 패널
	private FriendLabel friendLabel;
	ImageIcon basicicon = new ImageIcon("icons\\user.png");
	//ImageIcon basicicon = new ImageIcon("src\\icon1.jpg");
	
	public JLabel userName;
	public JButton profileImg;
	
	public String username; 
	public String statusmsg; 
	public String data;
	public ImageIcon profileimg;
	public String UserStatus;
	
	public JavaObjClientView mainview;
	public JavaObjClientChatView chatview;
	public UserProfile up;

	public ImageIcon gprofileimg;
	public String gusername;
	public FriendLabel(ImageIcon icon, String username) {
		username = username;
		profileimg = icon;
		setPreferredSize(new Dimension(70, 50));
		setLayout(null);
		
		userName = new JLabel(username);
		userName.setBounds(6, 60, 50, 20);
		userName.setLayout(null);
		add(userName);
		
		profileImg = new JButton(icon);
		profileImg.setBounds(5, 5, 50, 50);
		add(profileImg);
		
		friendLabel = this;
	}

	public FriendLabel get(JavaObjClientView main, String id) { // 채팅 보낸사람 UserProfile에 담긴 정보 가져오기
		for (UserProfile f: main.FriendVector) {
			if(f.username.equals(id))
				gprofileimg =  f.profileimg;
				gusername = f.username;
				friendLabel.profileImg.setIcon(f.profileimg);
				friendLabel.userName.setText(f.username);
				friendLabel.setPreferredSize(new Dimension(70, 50));
		}
		//userName = new JLabel(id);
			
			//f.setText(id);
			
		//f.setBounds(66, 10, 80, 21);
		//add(f);
		return friendLabel;
	}

	public void put(String user, FriendLabel f) {
		f.username = user;
		f.setText(user);
	}
	
}