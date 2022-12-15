
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

class ChatRoom extends JPanel {
	private JPanel con;
	
	private JLabel RoomName;
	private JLabel LastTalk;
	private JLabel RoomImg;
	
	public Vector room_vc;
	public String userName;
	public String userList; 
	public String roomId;
	public String lasttalk = "welcome";
	
	private String protocol;
	private String data;
	
	public ImageIcon roomImg;
	//public String chatroomid;
	//public Vector<String> chatuserlists;
	public JavaObjClientView Mainview;
	public JavaObjClientChatView chattingroom;
	public ChatRoom chatroom;

	//mainview, ImageIcon img, 
	public ChatRoom(JavaObjClientView mainview, ImageIcon img, String username, String roomid, String userlist) {
		this.userName = username;
		this.userList = userlist;
		this.roomId = roomid;
		this.roomImg = img;
		chatroom = this;
		//this.room_vc = RoomVec;
		setPreferredSize(new Dimension(231, 76));
		setVisible(true);
		setLayout(null);

		RoomName = new JLabel(roomId);
		RoomName.setBounds(66, 10, 80, 21);
		add(RoomName);
		
		//ImageIcon roomImg = roomImg;
		RoomImg = new JLabel(roomImg);
		RoomImg.setBounds(5, 53, 58, -48);
		add(RoomImg);
		
		LastTalk = new JLabel(lasttalk);
		LastTalk.setBounds(66, 41, 126, 21);
		add(LastTalk);
		
		
		addMouseListener((MouseListener) new MouseAdapter() { // 전원 입장 안하면 안한사람 chatview 없어서 오류 
			public void mouseClicked(MouseEvent e) { // 메인뷰 말고 채팅방 통해서는??
				chattingroom = new JavaObjClientChatView(mainview, username, roomId, userList);
				//chattingroom = new JavaObjClientChatView(chatroom, username, roomId, userList);
				ChatMsg obcm = new ChatMsg(username, "1001", userlist);
				obcm.chatroomid = roomId;
				obcm.chatuserlists = userList;
				mainview.SendObject(obcm); // 채팅방 입장시 정보 불러오기
			}
		});
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	////////// 채팅방 데이터 전달 /////////
	
	public void AppendText(ChatMsg cm) {
		chattingroom.AppendText(cm);
	}
	
	public void AppendImage(ChatMsg cm) {
		chattingroom.AppendImage(cm);
		
	}

	public Component getUserList() {
		
		return null;
	}

	public void ChangeFriendProfile(ChatMsg cm) {
		// 
	}

	public void changelast(ChatRoom room, String data) {
		room.LastTalk.setText(data);
//		ChatRoom nroom = new ChatRoom(room.Mainview, room.roomImg, room.userName, room.roomId, room.userList);
//		room.removeAll();
//		nroom.LastTalk.setText(lasttalk);
//		nroom.repaint();
//		return nroom;
//		r = r.changelast(r, cm.data); // 메인에서 이렇게 해서 같이쓰면
//		repaint();  // 빈 라벨로 바뀜(채팅 두번 입력되는 오류) >> chatroom return 말고 바꾸기만
	}
	
	
}