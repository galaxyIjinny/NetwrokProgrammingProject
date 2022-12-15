import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

class UserProfile extends JPanel {
	ImageIcon basicicon = new ImageIcon("src/basic.jpg");
	
	public String username = "user"; // 유저 정보 저장 용도
	private String statusmsg = "hello!"; 
	private String data;
	public ImageIcon profileimg = basicicon;
	public String UserStatus;
	
	public JavaObjClientView main;
	public UserProfile profile;
	//public Vector<String> chatuserlists;

	public UserProfile(ImageIcon icon, String username, String userstatus, String statusmsg) {
		this.username = username;
		this.statusmsg = statusmsg;
		this.profileimg = icon;
		profile = this;
		setLayout(null);
		setVisible(true);
		setPreferredSize(new Dimension(231, 76));
		//setBackground(Color.WHITE);
		//setBounds(0, 0, 320, 77);

		JLabel userName = new JLabel(username);
		userName.setFont(new Font("굴림", Font.PLAIN, 12));
		userName.setBounds(78, 10, 126, 26);
		add(userName);
		
		//profileImg = new JLabel(profileimg);
		
//		ImageIcon ori_icon = icon;
//		Image ori_img = ori_icon.getImage();
//		Image new_img;
//		ImageIcon new_icon;
//		int width, height;
//		double ratio;
//		width = ori_icon.getIconWidth();
//		height = ori_icon.getIconHeight();
//		if (width > 70 || height > 60) {
//			if (width > height) { 
//				ratio = (double) height / width;
//				width = 70;
//				height = (int) (width * ratio);
//			} else { 
//				ratio = (double) width / height;
//				height = 60;
//				width = (int) (height * ratio);
//			}
//			new_img = ori_img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
//			new_icon = new ImageIcon(new_img);
//			}

//		JButton profileImg = new JButton();
//		profileImg.setIcon(icon);
//		profileImg.setBounds(5, 65, 69, -60);
//		profileImg.setBorderPainted(false);
//		add(profileImg);
		
		JLabel profileImg = new JLabel();
		profileImg.setIcon(icon);
		profileImg.setBounds(5, 65, 69, -60);
		//profileImg.setBorderPainted(false);
		add(profileImg);
		
		JLabel statusMsg = new JLabel(statusmsg);
		statusMsg.setBounds(78, 44, 126, 21);
		add(statusMsg);
	}
	
	public UserProfile getuserProfile() {
		setLayout(null);
		setVisible(true);
		setBounds(0, 0, 231, 76);
		//setBackground(Color.GREEN);

		JLabel userName = new JLabel(username);
		userName.setFont(new Font("굴림", Font.PLAIN, 12));
		userName.setBounds(78, 10, 126, 26);
		add(userName);
		
		//profileImg = new JLabel(profileimg);
		JButton profileImg = new JButton();
		profileImg.setIcon(profileimg);
		profileImg.setBounds(5, 62, 66, -57);
		profileImg.setBorderPainted(false);
		add(profileImg);
		
		JLabel statusMsg = new JLabel(statusmsg);
		statusMsg.setBounds(78, 44, 126, 21);
		add(statusMsg);
		return this;
	}
	
//	public UserProfile(String username) {
//		this.username = username;
//		setLayout(null);
//		setVisible(true);
//
//		userName = new JLabel(username);
//		userName.setFont(new Font("굴림", Font.PLAIN, 12));
//		userName.setBounds(78, 10, 126, 26);
//		add(userName);
//		
//		profileImg = new JButton();
//		profileImg.setIcon(profileimg);
//		profileImg.setBounds(5, 62, 66, -57);
//		add(profileImg);
//		
//		statusMsg = new JLabel(statusmsg);
//		statusMsg.setBounds(78, 44, 126, 21);
//		add(statusMsg);
//	}
	
	public void setStatusChangeACtive() {
		this.UserStatus = "O";
	}
	
	public String getstatemsg() {
		return statusmsg;
	}

	public void setStatusmsg(String statusmsg) {
		this.statusmsg = statusmsg;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public void setprofileimg(ImageIcon profileimg) {
		this.profileimg = profileimg;
	}
	
	public void setOnline(boolean bool) {
		if (bool) {
		this.UserStatus = "O";
		} else 
			this.UserStatus = "S";
	}

	public void setIcon(ChatMsg cm) {
		// TODO Auto-generated method stub
		
	}
}