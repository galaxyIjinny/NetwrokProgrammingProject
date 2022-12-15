
// JavaObjClientView.java ObjecStram 기반 Client
//실질적인 채팅 창
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.ImageObserver;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.Container;

import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class JavaObjClientView extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel sideContentpane; // 목록 버튼 패널
	private JPanel listContentpane; // 목록에 대한 리스트(친구/채팅) 보여주는 패널
	private JPanel FriendListPanel; // 친구 패널
	private JPanel ChatListPanel; // 채팅 패널
	private JPanel FriendListHeader;
	private JPanel ChatListHeader;
	private JTextPane FriendListPane; // 유저 각각을 붙일 패널
	//private JPanel FriendListPane;
	private JTextPane ChatListPane; // 채팅방 붙일 패널
	private JPanel mainCon;
	private Container con;
	
	private String UserName;
	private String IpAddr;
	private String PortNo;
	private String myChatRoomIds = ("");
	private String myFriends;
	private ImageIcon UserIcon;
	private String UserStatus;
	private String StatusMsg;
	public ImageIcon icon = new ImageIcon("src/basic.jpg");
	//ImageIcon icon = new ImageIcon("icons/icon2.jepg");
	//ImageIcon icon = new ImageIcon(getClass().getResource("icons\\profilebasic.jpg"));
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
	private Socket socket; // 연결소켓

	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	private JTextPane textArea;

	private Frame frame;
	private FileDialog fd;
	private JButton imgBtn;

	JPanel panel;
	private JLabel lblMouseEvent;
	private Graphics gc;
	private int pen_size = 2; // minimum 2
	// 그려진 Image를 보관하는 용도, paint() 함수에서 이용한다.
	private Image panelImage = null; 
	private Graphics gc2 = null;
	public JavaGameClientViewDrawing drawing;
	public JavaObjClientView Mainview;
//	public JavaObjClientChatListView ChatListview;
//	public JavaObjClientFriendListView FriendListview;
	public ChatClientSelectFriendDialog dialog;
	public UserProfile f;
	public ChatRoom r;
	//public JavaObjClientNotice noticeview;
	
	public Vector<UserProfile> FriendVector = new Vector<UserProfile>(); // Friend 
	private Vector<ChatRoom> ChatRoomVector = new Vector<ChatRoom>();

	
	/**
	 * Create the frame.
	 * @throws BadLocationException 
	 */
	public JavaObjClientView(String username, String ip_addr, String port_no)  {
		Mainview = this;
		UserName = username;
		//try { socket, oos,ois}
		
		setTitle(username);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 472, 668);	
		mainCon = new JPanel();
		mainCon.setLayout(null);
		//setContentPane(mainCon);
		setVisible(true);
		con = getContentPane();
		con.add(mainCon);
		
		/////왼쪽 버튼 패널/////
		sideContentpane = new JPanel();
		sideContentpane.setBackground(new Color(230, 230, 230));
		sideContentpane.setBounds(0, 0, 126, 629);
		sideContentpane.setBorder(new EmptyBorder(5, 5, 5, 5));
		sideContentpane.setLayout(null);
		mainCon.add(sideContentpane);
		
		JButton btnProfileButton = new JButton(); // 메인화면 버튼
		btnProfileButton.setFont(new Font("굴림", Font.PLAIN, 14));
		btnProfileButton.setBorder(null);
		btnProfileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FriendListPanel.setVisible(true);
				ChatListPanel.setVisible(false);
				
//				ChatMsg msg = new ChatMsg(UserName, "900", username);
//				SendObject(msg);
			}
			//if (username ) // user 미리 등록? >> 프로필 불러오기
			//UserImg =  
		});
		btnProfileButton.setBounds(57, 72, 69, 69);
		btnProfileButton.setBackground(new Color(255, 255, 255));
		//btnProfileButton.setDisabledIcon(new ImageIcon("icons/friendbtnoff.jpg"));
		//btnProfileButton.setIcon(new ImageIcon("icons/friendbtnon.png"));
		sideContentpane.add(btnProfileButton);
		
		JButton btnChatListButton = new JButton(); // 채팅방 목록 버튼
		btnChatListButton.setBackground(new Color(0, 0, 0));
		btnChatListButton.setFont(new Font("굴림", Font.PLAIN, 14));
		btnChatListButton.setBorder(null);
		btnChatListButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatListPanel.setVisible(true);
				FriendListPanel.setVisible(false);
			} // username에 맞는 채팅방 불러오기
		});
		btnChatListButton.setBounds(57, 180, 69, 69);
		//btnChatListButton.setDisabledIcon(new ImageIcon("icons/chatbtnon.png"));
		//btnChatListButton.setIcon(new ImageIcon("src/cahtbtnoff.jpg"));
		sideContentpane.add(btnChatListButton);
		
		JButton btnInviteButton = new JButton("초 대"); // ....임시......
		btnInviteButton.setFont(new Font("굴림", Font.PLAIN, 14));
		btnInviteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatMsg msg = new ChatMsg(UserName, "1000", "makeRoom");
				//msg.chatuserlists = "k h j"; // 임의로 채팅방 생성
				String userlist = new String(UserName + " ");
				for (UserProfile f: FriendVector) {
					if(f.username.equals(UserName))
						continue;
					userlist = userlist.concat(f.username + " ");
				}
				msg.chatuserlists = userlist;
				SendObject(msg);
			}
		});
		btnInviteButton.setBounds(57,509, 69, 40);
		sideContentpane.add(btnInviteButton);
		
		JButton btnNewButton = new JButton("종 료"); // 종료 버튼
		btnNewButton.setFont(new Font("굴림", Font.PLAIN, 14));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatMsg msg = new ChatMsg(UserName, "400", "Bye");
				SendObject(msg);
				System.exit(0);
			}
		});
		btnNewButton.setBounds(57, 579, 69, 40);
		sideContentpane.add(btnNewButton);
		
		
		///////// 오른쪽 화면///////////
		/// 친구 레이아웃 ///	
		
		FriendListPanel = new JPanel(); // 친구 패널  panelFriend
		FriendListPanel.setBounds(125,0,331,629);
		//FriendListPanel.setBackground(Color.WHITE);
		FriendListPanel.setLayout(null);
		FriendListPanel.setBorder(null);
		mainCon.add(FriendListPanel);
		
		JScrollPane scrollFriend = new JScrollPane();
		scrollFriend.setBounds(0, 70, 331, 559);
		FriendListPanel.add(scrollFriend);
		
		FriendListHeader = new JPanel();  // 친구 헤더
		FriendListHeader.setBackground(new Color(255, 255, 255));
		FriendListHeader.setBounds(0, 0, 331, 71);
		FriendListHeader.setLayout(null);
		FriendListPanel.add(FriendListHeader);
		JLabel FriendLabel = new JLabel("친구"); // 헤더 이름
		FriendLabel.setBounds(0,0,93,71);
		FriendLabel.setBorder(null);
		FriendLabel.setFont(new Font("맑은 고딕",Font.BOLD, 20));
		FriendLabel.setBackground(Color.WHITE);
		FriendLabel.setHorizontalAlignment(SwingConstants.CENTER);
		FriendListHeader.add(FriendLabel);
		
		FriendListPane = new JTextPane(); // textPaneFriendList
		FriendListPane.setEditable(false);
		//FriendListPanel.add(FriendLabel);
		scrollFriend.setViewportView(FriendListPane);
		//scrollFriend.add(FriendListPane);
		
		/// 채팅 레이아웃 ///
		
		ChatListPanel = new JPanel(); // 채팅 패널
		ChatListPanel.setBounds(125,0,331,629);
		//ChatListPanel.setBackground(Color.WHITE);
		ChatListPanel.setLayout(null);
		ChatListPanel.setBorder(null);
		mainCon.add(ChatListPanel);
		
		JScrollPane scrollChat = new JScrollPane();
		scrollChat.setBounds(0, 70, 331, 559);
		ChatListPanel.add(scrollChat);
		
//		JTextPane textPaneChatList = new JTextPane();
//		textPaneChatList.setEditable(false);
//		scrollChat.setViewportView(textPaneChatList);
		
		ChatListHeader = new JPanel();  // 채팅 헤더
		ChatListHeader.setBackground(new Color(255, 255, 255));
		ChatListHeader.setBounds(0, 0, 331, 71);
		ChatListHeader.setLayout(null);
		ChatListPanel.add(ChatListHeader);
		JLabel ChatLabel = new JLabel("채팅");
		ChatLabel.setBounds(0,0,93,71);
		ChatLabel.setBorder(null);
		ChatLabel.setFont(new Font("맑은 고딕",Font.BOLD, 20));
		ChatLabel.setBackground(Color.WHITE);
		ChatLabel.setHorizontalAlignment(SwingConstants.CENTER);
		ChatListHeader.add(ChatLabel);

		JButton btnNewChatRoom = new JButton(" + "); // 채팅방추가 버튼
		btnNewChatRoom.setBounds(261, 21, 47, 31);
		ChatListHeader.add(btnNewChatRoom);
		btnNewChatRoom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatClientSelectFriendDialog dialog = new ChatClientSelectFriendDialog(Mainview, FriendVector);
				String userlist = dialog.ShowDialog();
				dialog = null; // dialog 삭제
				if (userlist != null) {
					ChatMsg obcm = new ChatMsg(UserName, "1000", "new room");
					obcm.chatuserlists = userlist;
					SendObject(obcm);
				}
			}
		});
		btnNewChatRoom.setBackground(Color.WHITE);
		btnNewChatRoom.setFont(new Font("", Font.PLAIN,25));
		btnNewChatRoom.setBorder(null);
		
		ChatListPane = new JTextPane(); // textPaneChatList. 채팅방 붙일 패널
		ChatListPane.setEditable(false);
		scrollChat.setViewportView(ChatListPane);
		
		ChatListPanel.setVisible(false);
		FriendListPanel.setVisible(true);
		repaint();

		try {
			socket = new Socket(ip_addr, Integer.parseInt(port_no));
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());

			//user 본인
			AddFriend(Mainview, icon, UserName, "O", "hello");
			//UserIcon = icon1;
			
			// 로그인
			ChatMsg obcm = new ChatMsg(UserName, "100", "Hello");
			obcm.UserStatus = "O";
			obcm.statusmsg = "hello";
			//obcm.img = icon;
			SendObject(obcm);			
			repaint();
			
			ListenNetwork net = new ListenNetwork();
			net.start();

		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	// Server Message를 수신해서 화면에 표시
	class ListenNetwork extends Thread {
		public void run() {
			while (true) {
				try {

					Object obcm = null;
					String msg = null;
					ChatMsg cm;
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
					if (obcm == null)
						break;
					if (obcm instanceof ChatMsg) {
						cm = (ChatMsg) obcm;
						msg = String.format("[%s]\n%s", cm.UserName, cm.data);
					} else
						continue;
					switch (cm.code) {
					case "100": // 로그인 시
						//myFriends = cm.getChatuserlists();
						LoginNewFriend(cm);
						//FriendListPanel.removeAll();
						break;
					case "120": // profile 변경
						ChangeFriendProfile(cm);
						break;
					case "130": // 상태메시지 변경
						ChangeStatusMsg(cm);
						break;
					case "200": // chat message
						AppendText(cm);
						break;
					case "300": // Image 첨부
						AppendImage(cm);
						break;
					case "405": // 친구가 로그아웃 시
						for (UserProfile f: FriendVector) {
							if(f.username.equals(cm.UserName))
								f.UserStatus = "S";
						}
						break;
					case "500": // Mouse Event 수신
						drawing.DoMouseEvent(cm);
						break;
					case "1001": // 채팅방 로그 받음(다시 전송해서 결과를 받아야 함...인줄 알았으나 메시지는 잘 받음)
						
						break;
					case "1010": // 생성된 채팅방 받음
//						ChatRoom mychat = new ChatRoom(Mainview, cm.img, UserName, cm.chatroomid, cm.chatuserlists); // 유저가 속한 채팅방 정보 저장
//						ChatRoomVector.add(mychat);
//						myChatRoomIds = myChatRoomIds.concat(cm.chatroomid + " ");
						AddChatRoom(cm);
						break;
					}
				} catch (IOException e) {
					//AppendText("ois.readObject() error");
					try {
						ois.close();
						oos.close();
						socket.close();
						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝
			}
		}
	}

	// Mouse Event 수신 처리
	public void DoMouseEvent(ChatMsg cm) {
		Color c;
		if (cm.UserName.matches(UserName)) // 본인 것은 이미 Local 로 그렸다.
			return;
		c = new Color(255, 0, 0); // 다른 사람 것은 Red
		gc2.setColor(c);
		gc2.fillOval(cm.mouse_e.getX() - pen_size/2, cm.mouse_e.getY() - cm.pen_size/2, cm.pen_size, cm.pen_size);
		gc.drawImage(panelImage, 0, 0, panel);
	}

	public void SendMouseEvent(MouseEvent e) {
		ChatMsg cm = new ChatMsg(UserName, "500", "MOUSE");
		cm.mouse_e = e;
		cm.pen_size = pen_size;
		SendObject(cm);
	}

	class MyMouseWheelEvent implements MouseWheelListener {
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			// TODO Auto-generated method stub
			if (e.getWheelRotation() < 0) { // 위로 올리는 경우 pen_size 증가
				if (pen_size < 20)
					pen_size++;
			} else {
				if (pen_size > 2)
					pen_size--;
			}
			lblMouseEvent.setText("mouseWheelMoved Rotation=" + e.getWheelRotation() 
				+ " pen_size = " + pen_size + " " + e.getX() + "," + e.getY());

		}
		
	}
	// Mouse Event Handler
	class MyMouseEvent implements MouseListener, MouseMotionListener {
		@Override
		public void mouseDragged(MouseEvent e) {
			lblMouseEvent.setText(e.getButton() + " mouseDragged " + e.getX() + "," + e.getY());// 좌표출력가능
			Color c = new Color(0,0,255);
			gc2.setColor(c);
			gc2.fillOval(e.getX()-pen_size/2, e.getY()-pen_size/2, pen_size, pen_size);
			// panelImnage는 paint()에서 이용한다.
			gc.drawImage(panelImage, 0, 0, panel);
			SendMouseEvent(e);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			lblMouseEvent.setText(e.getButton() + " mouseMoved " + e.getX() + "," + e.getY());
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			lblMouseEvent.setText(e.getButton() + " mouseClicked " + e.getX() + "," + e.getY());
			Color c = new Color(0,0,255);
			gc2.setColor(c);
			gc2.fillOval(e.getX()-pen_size/2, e.getY()-pen_size/2, pen_size, pen_size);
			gc.drawImage(panelImage, 0, 0, panel);
			SendMouseEvent(e);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			lblMouseEvent.setText(e.getButton() + " mouseEntered " + e.getX() + "," + e.getY());
			// panel.setBackground(Color.YELLOW);

		}

		@Override
		public void mouseExited(MouseEvent e) {
			lblMouseEvent.setText(e.getButton() + " mouseExited " + e.getX() + "," + e.getY());
			// panel.setBackground(Color.CYAN);

		}

		@Override
		public void mousePressed(MouseEvent e) {
			lblMouseEvent.setText(e.getButton() + " mousePressed " + e.getX() + "," + e.getY());

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			lblMouseEvent.setText(e.getButton() + " mouseReleased " + e.getX() + "," + e.getY());
			// 드래그중 멈출시 보임

		}
	}

	class ImageSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// 액션 이벤트가 sendBtn일때 또는 textField 에세 Enter key 치면
			if (e.getSource() == imgBtn) {
				frame = new Frame("이미지첨부");
				fd = new FileDialog(frame, "이미지 선택", FileDialog.LOAD);
				// frame.setVisible(true);
				// fd.setDirectory(".\\");
				fd.setVisible(true);
				// System.out.println(fd.getDirectory() + fd.getFile());
				if (fd.getDirectory().length() > 0 && fd.getFile().length() > 0) {
					ChatMsg obcm = new ChatMsg(UserName, "300", "IMG");
					ImageIcon img = new ImageIcon(fd.getDirectory() + fd.getFile());
					obcm.img = img;
					SendObject(obcm);
				}
			}
		}
	}

	public void AppendIcon(ImageIcon icon) {
		int len = textArea.getDocument().getLength();
		// 끝으로 이동
		textArea.setCaretPosition(len);
		textArea.insertIcon(icon);
	}

	///// 채팅방에서 이뤄지는 부분 //////////
	
	// 화면에 출력
	public void AppendText(ChatMsg cm) {
		ChatRoom r = SearchRoom(cm.chatroomid);
		if (cm.chatroomid.equals(r.roomId)) {
			r.AppendText(cm);
			r.changelast(r, cm.data);
			repaint();
		}
//		if ((r = SearchRoom(cm.chatroomid)) != null) { // 채팅방 없으면 새로 생성
//			if (r.roomview == null)
//				r.JavaObjClientChatView(); // NewChatClientChatRoomView()
//			r.AppendImage(cm);
//		}
	}
	
	public void AppendImage(ChatMsg cm) {
		ChatRoom r = SearchRoom(cm.chatroomid);
		if (cm.chatroomid.equals(r.roomId)) {
			r.AppendImage(cm);
			r.changelast(r, "사진을 보냈습니다.");
			repaint();
		};
//		if ((r = SearchRoom(cm.chatroomid)) != null) {
//			if (r.roomview == null)
//				r.JavaObjClientChatView();
//			r.AppendImage(cm);
//		}
	}
	
	/////////////////////////////////

	// Windows 처럼 message 제외한 나머지 부분은 NULL 로 만들기 위한 함수
	public byte[] MakePacket(String msg) {
		byte[] packet = new byte[BUF_LEN];
		byte[] bb = null;
		int i;
		for (i = 0; i < BUF_LEN; i++)
			packet[i] = 0;
		try {
			bb = msg.getBytes("euc-kr");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		for (i = 0; i < bb.length; i++)
			packet[i] = bb[i];
		return packet;
	}

	// Server에게 network으로 전송
	public void SendMessage(String msg) {
		try {
			ChatMsg obcm = new ChatMsg(UserName, "200", msg);
			oos.writeObject(obcm);
		} catch (IOException e) {
			//AppendText("oos.writeObject() error");
			try {
				ois.close();
				oos.close();
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.exit(0);
			}
		}
	}

	public void SendObject(Object ob) { // 서버로 메세지를 보내는 메소드
		try {
			oos.writeObject(ob);
		} catch (IOException e) {
			// textArea.append("메세지 송신 에러!!\n");
		}
	}
	
	public void ChangeFriendProfile(ChatMsg cm) {
		UserIcon = cm.img;
		for (UserProfile f : FriendVector) {
			if (f.username.equals(cm.UserName)) {
				f.setIcon(cm); // f.profileimg = cm.img;
			}
		}
//		for (ChatRoom r : ChatRoomVector) {
//			if (r.getUserList().contains(cm.UserName))
//				r.ChangeFriendProfile(cm);
//		}
	}
	
	
	public void ChangeStatusMsg(ChatMsg cm) {
		StatusMsg = cm.statusmsg;
//		for (UserProfile f: FriendVector) {
//			// ////
//		}
	}
	
	public void LogoutFriend(ChatMsg cm) {
		for (UserProfile f: FriendVector) {
			if (f.username.equals(cm.UserName)) {
				//f.setOnline(false);
			}
		}
	}
	
	public void LoginNewFriend(ChatMsg cm) {
		for (UserProfile f: FriendVector) {
			if (f.username.equals(cm.UserName)) {
//				if (cm.UserStatus.equals("O")) {
//					f.setOnline(true);
//				} else {
//					f.setOnline(false);
//				}
				f.setStatusmsg(cm.statusmsg);
				return;
			}
		}
		AddFriend(Mainview, cm.img, cm.UserName, cm.UserStatus, cm.statusmsg); // 100으로 로그인된 친구들 패널 추가
	
	}

	public void AddFriend(JavaObjClientView view, ImageIcon icon, String username, String userstatus, String statusmsg) {
		int len = FriendListPane.getDocument().getLength();
		FriendListPane.setCaretPosition(len);
		UserProfile f = new UserProfile(icon, username, userstatus, statusmsg); // 유저 프로필,이름 생성 후
		//f.getuserProfile();
		//FriendListPane.add(f);  // 출력 안됨.(Panel x). con써서 하면 되긴하는데 img배경 침범, 하나만 출력
		//FriendListPane.revalidate(); 
		//FriendListPanel.add(f); 
		FriendListPane.insertComponent(f); // 유저 목록 패널에 부착. setPreferredSize 와 함께 사용
		//con.add(f) // 전체 화면에 부착됨
		if (username.equals(UserName)) {
			f.setBackground(new Color(240,240,0)); 
			f.setStatusChangeACtive(); // 본인 상태 변경
		}
		//f.setProfileButtonActive(); // 프사 변경, 프사 확인 버튼 활성화
		FriendVector.add(f);
		//FriendListPane.setCaretPosition(0);
		repaint();
	}
	
	public UserProfile SearchFriend(String name) {
		for (UserProfile f: FriendVector) {
			if(f.username.equals(name))
				return f;
		}
		return null;
	}
	
	public void AddChatRoom(ChatMsg cm) {
		int len = ChatListPane.getDocument().getLength();
		ChatListPane.setCaretPosition(len);
		ChatRoom r = new ChatRoom(Mainview, icon, UserName, cm.chatroomid, cm.chatuserlists);
		ChatListPane.insertComponent(r); // 채팅방 생성 후 부착
		//ChatListPane.add(r);
		ChatRoomVector.add(r); 
		//drawing = r.chattingroom.drawing;
		ChatListPane.setCaretPosition(0);
		repaint();
	}
	
	public ChatRoom SearchRoom(String room_id) {
		for (ChatRoom r: ChatRoomVector) {
			if (r.roomId.equals(room_id))
				return r;
		}
		return null;
	}
	
	public ImageIcon getUserIcon(String name) {
		for (UserProfile f: FriendVector) {
			if(f.username.equals(name))
				return f.profileimg;
		}
		return null;
	}
	
	
	
}
