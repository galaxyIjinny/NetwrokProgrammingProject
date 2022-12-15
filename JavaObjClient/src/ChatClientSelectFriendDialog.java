import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;

//import JavaObjServer.UserService;
import javax.swing.JPanel;
import javax.swing.JTextPane;

class ChatClientSelectFriendDialog extends JDialog {
	public String userlist = ("");
	private JButton okBtn = new JButton("초대");
	public JavaObjClientView main;
	public Vector<UserProfile> FriendVector = new Vector<UserProfile>(); 
	public JCheckBox[] usercheck = new JCheckBox[FriendVector.size()];

	public ChatClientSelectFriendDialog(JavaObjClientView view, Vector friend) {
		getContentPane().setBackground(new Color(222, 222, 222));
		main = view;
		FriendVector = main.FriendVector;
		//Container con = getContentPane();
		setVisible(true);
		getContentPane().setLayout(null);
		setTitle("채팅방 만들기");
		setBounds(main.getX()+10, main.getY()+10, 238, 340);
		
		okBtn.setBounds(76, 264, 71, 37);
		getContentPane().add(okBtn);
		//con.add(okBtn);
		
		JTextPane friendPanel = new JTextPane();
		friendPanel.setLayout(null);
		friendPanel.setBounds(0, 0, 222, 264);
		friendPanel.setBackground(Color.WHITE);
		getContentPane().add(friendPanel);
		//con.add(friendPanel);
		//con.add(usercheck);
		
		FriendListener listener = new FriendListener();
		for (int i=0; i<FriendVector.size(); i++) {
			UserProfile user = (UserProfile) FriendVector.elementAt(i);
			//FriendLabel f = new FriendLabel(user.profileimg, user.username);
			usercheck[i] = new JCheckBox(user.username); // checkbox init 문제...
			usercheck[i].addItemListener(listener);
			//friendPanel.setCaretPosition(len);
//			friendPanel.insertComponent(f);
//			friendPanel.insertComponent(usercheck[i]);
//			friendPanel.setCaretPosition(0);
			}
		
		
		okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				// userlist = itemlistener list 
				//setVisible(false);
			}
		});
	}

	class FriendListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				for (int i=0; i<main.FriendVector.size(); i++) {
					if (e.getItem() == usercheck[i])
						userlist.concat(main.FriendVector.elementAt(i).username + " ");
				}
			}
		}
	}
	
	public String ShowDialog() {
		return userlist;
	}
}