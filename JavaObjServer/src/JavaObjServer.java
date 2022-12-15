//JavaObjServer.java ObjectStream 기반 채팅 Server

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

public class JavaObjServer extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	JTextArea textArea;
	private JTextField txtPortNumber;

	private ServerSocket socket; // 서버소켓
	private Socket client_socket; // accept() 에서 생성된 client 소켓
	private Vector UserVec = new Vector(); // 연결된 사용자를 저장할 벡터
	private Vector RoomVec = new Vector(); // 전체 채팅방 관리를 위한 벡터(채팅방 생성시 사용)
	
	public String Chatroom_id; // 채팅방 목록(참가중인 id)
	public String Chatroom_user; // 채팅방 참가자
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JavaObjServer frame = new JavaObjServer();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public JavaObjServer() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 338, 440);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 300, 298);
		contentPane.add(scrollPane);

		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

		JLabel lblNewLabel = new JLabel("Port Number");
		lblNewLabel.setBounds(13, 318, 87, 26);
		contentPane.add(lblNewLabel);

		txtPortNumber = new JTextField();
		txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
		txtPortNumber.setText("30000");
		txtPortNumber.setBounds(112, 318, 199, 26);
		contentPane.add(txtPortNumber);
		txtPortNumber.setColumns(10);

		JButton btnServerStart = new JButton("Server Start");
		btnServerStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					socket = new ServerSocket(Integer.parseInt(txtPortNumber.getText()));
				} catch (NumberFormatException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				AppendText("Chat Server Running..");
				btnServerStart.setText("Chat Server Running..");
				btnServerStart.setEnabled(false); // 서버를 더이상 실행시키지 못 하게 막는다
				txtPortNumber.setEnabled(false); // 더이상 포트번호 수정못 하게 막는다
				AcceptServer accept_server = new AcceptServer();
				accept_server.start();
			}
		});
		btnServerStart.setBounds(12, 356, 300, 35);
		contentPane.add(btnServerStart);
	}

	// 새로운 참가자 accept() 하고 user thread를 새로 생성한다.
	class AcceptServer extends Thread {
		@SuppressWarnings("unchecked")
		public void run() {
			while (true) { // 사용자 접속을 계속해서 받기 위해 while문
				try {
					AppendText("Waiting new clients ...");
					client_socket = socket.accept(); // accept가 일어나기 전까지는 무한 대기중
					AppendText("새로운 참가자 from " + client_socket);
					// User 당 하나씩 Thread 생성
					UserService new_user = new UserService(client_socket);
					UserVec.add(new_user); // 새로운 참가자 배열에 추가
					new_user.start(); // 만든 객체의 스레드 실행
					AppendText("현재 참가자 수 " + UserVec.size());
				} catch (IOException e) {
					AppendText("accept() error");
					// System.exit(0);
				}
			}
		}
	}

	public void AppendText(String str) {
		// textArea.append("사용자로부터 들어온 메세지 : " + str+"\n");
		textArea.append(str + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public void AppendObject(ChatMsg msg) {
		// textArea.append("사용자로부터 들어온 object : " + str+"\n");
		textArea.append("code = " + msg.code + "\n");
		textArea.append("id = " + msg.UserName + "\n");
		textArea.append("data = " + msg.data + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	// 채팅방 관리
		//UserService new_user = new UserService(client_socket);
		//UserVec.add(new_user); // 새로운 참가자 배열에 추가
		class ChatService {
			private Vector room_vc;
			public String Chatroom_id;
			public String Chatroom_user;
			public ImageIcon Chatimg;
			public Vector protocol_vc = new Vector(); // 로그 기록(프로토콜)
			public Vector data_vc = new Vector(); // 로그 기록
			public Vector <ChatMsg> msg_vc;
			
			public ChatService(String id, String userlist) {
				this.Chatroom_id = id;
				this.Chatroom_user = userlist;
				this.room_vc = RoomVec;
				msg_vc = new Vector();
			}
			
			public void makeChatroom(String id, String userlist) {
				//this.room_vc = RoomVec; // 채팅방 생성시에 사용
				this.Chatroom_id = id;
				this.Chatroom_user = userlist;
				this.room_vc = RoomVec;
			}
		}
	
	// User 당 생성되는 Thread
	// Read One 에서 대기 -> Write All
	class UserService extends Thread {
		private ObjectInputStream ois;
		private ObjectOutputStream oos;

		private ChatService chatservice;
		private Socket client_socket;
		private Vector user_vc;
		public String UserName = "user";
		public String UserStatus = "O";
		
		ImageIcon imgcon0 =  new ImageIcon("src/basic.jpg");
		public ImageIcon UserImg = imgcon0; // 등록 안해두면 기본 이미지
		public String StateMsg = "hello"; // 상태메세지
		public String FriendName; // 친구 목록

		public UserService(Socket client_socket) {
			// TODO Auto-generated constructor stub
			// 매개변수로 넘어온 자료 저장
			this.client_socket = client_socket;
			this.user_vc = UserVec;
			
			try {
				oos = new ObjectOutputStream(client_socket.getOutputStream());
				oos.flush();
				ois = new ObjectInputStream(client_socket.getInputStream());
			} catch (Exception e) {
				AppendText("userService error");
			}
		}

		public void AllLogin(String name, ImageIcon img, String status, String msg) {
			try {
				AppendText(UserName + " : 서버에 " + name + " 님이 입장하셨습니다.");
				ChatMsg obcm = new ChatMsg(name, "100", UserName);
				obcm.statusmsg = StateMsg;
				//obcm.img = UserImg;
//				obcm.img = img;
//				obcm.UserStatus = status;
//				obcm.statusmsg = msg;
				oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("dos.writeObject() error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					AppendText("Server :: 전체 입장 오류!!");
					e1.printStackTrace();
					Logout(); // 에러가난 현재 객체를 벡터에서 지운다
				}
			}
		}
		
		public void Login(String msg) {
			AppendText("서버에 " + UserName + " 님이 입장하셨습니다.");
			try {
				for (int i = 0; i < user_vc.size(); i++) { // 본인 로그인, 다른사람에게도 전송
					UserService user = (UserService) user_vc.elementAt(i);
					ChatMsg obcm = new ChatMsg(UserName, "100", msg);
					//obcm.img = user.UserImg;
					obcm.statusmsg = user.StateMsg;
					obcm.UserStatus = user.UserStatus;
					user.oos.writeObject(obcm);
				}
//				ChatMsg obcm = new ChatMsg(UserName, "100", msg);
//				oos.writeObject(obcm);
//					//FriendName.concat(user.UserName + " ");
			} catch (IOException e) {
				AppendText("dos.writeObject() error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					AppendText("Server :: writeOne 오류!!");
					e1.printStackTrace();
					Logout(); // 에러가난 현재 객체를 벡터에서 지운다
				}
				
			}
		}

		public void Logout() {
			String msg = "[" + UserName + "]님이 퇴장 하였습니다.\n";
			UserVec.removeElement(this); // Logout한 현재 객체를 벡터에서 지운다
			ChatMsg obcm = new ChatMsg(UserName, "405", msg);
			WriteAllObject(obcm);
			//WriteAll(msg); // 나를 제외한 다른 User들에게 전송
			AppendText("사용자 " + "[" + UserName + "] 퇴장. 현재 참가자 수 " + UserVec.size());
		}

		// 모든 User들에게 방송. 각각의 UserService Thread의 WriteONe() 을 호출한다.
		public void WriteAll(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.UserStatus == "O")
					user.WriteOne(str);
			}
		}
		// 모든 User들에게 Object를 방송. 채팅 message와 image object를 보낼 수 있다
		public void WriteAllObject(Object ob) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.UserStatus == "O")
					user.WriteOneObject(ob);
			}
		}

		// 나를 제외한 User들에게 방송. 각각의 UserService Thread의 WriteONe() 을 호출한다.
		public void WriteOthers(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user != this && user.UserStatus == "O")
					user.WriteOne(str);
			}
		}

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
			}
			for (i = 0; i < bb.length; i++)
				packet[i] = bb[i];
			return packet;
		}

		// UserService Thread가 담당하는 Client 에게 1:1 전송
		public void WriteOne(String msg) {
			try {
				ChatMsg obcm = new ChatMsg("SERVER", "200", msg);
				oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("dos.writeObject() error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}

		// 귓속말 전송
		public void WritePrivate(String msg) {
			try {
				ChatMsg obcm = new ChatMsg("귓속말", "200", msg);
				oos.writeObject(obcm);
			} catch (IOException e) {
				AppendText("dos.writeObject() error");
				try {
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}
		public void WriteOneObject(Object ob) {
			try {
			    oos.writeObject(ob);
			} 
			catch (IOException e) {
				AppendText("oos.writeObject(ob) error");		
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;				
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Logout();
			}
		}
		
		
		public void ViewChatList(Object ob) { // 500 채팅방 리스트(id만 전달)
			try { // 채팅방 만들때마다 그때 전송하면 필요 없을 수도. 채팅방 아이디만 각자가 저장방식?
				ChatMsg obcm = (ChatMsg) ob;
				String chatlist = new String(); // 채팅방 id 전달용
				String userlist = new String(); // 본인이 속한 채팅방 확인용
				String[] argsuser;
				String[] argschat;
				//int check = 0;
				//if (args[i] == chatroom_id)
				
//				try {
//					for (int i = 0; i < user_vc.size(); i++) {
//						UserService user = (UserService) user_vc.elementAt(i);
//						if (user == this) {
//							ChatMsg obcm = new ChatMsg(user.UserName, "500", "chatID");
//							obcm.chatuserlists = user.ChatRoom;
//							//채팅방 참가자
//							oos.writeObject(obcm);
//						}
//					}
//				} catch (IOException e1) {
//					AppendText("Server :: 500 오류!!");
//					e1.printStackTrace();
//					Logout();
//				}
				
				
				if (chatservice.room_vc == null) {
					chatlist = chatlist.concat(" "); // 채팅방이 없는 경우
				} else  {
				for (int j = 0; j < chatservice.room_vc.size(); j ++) {
					ChatService chat = (ChatService) chatservice.room_vc.elementAt(j);
					userlist = chat.Chatroom_user;
					argsuser = userlist.split(" "); // 유저리스트 분리
					
					for (int i = 0; i < argsuser.length; i++) { // 각각 채팅방에서 유저 목록과 본인 확인
					if (obcm.UserName.equals(argsuser[i])) {
						chatlist = chatlist.concat(chat.Chatroom_id + " ");
					}
					}
				}		
				}
				ChatMsg sobcm = new ChatMsg(obcm.UserName, "500", chatlist);	//채팅방 목록 전달
				oos.writeObject(obcm);
			} catch (IOException e1) {
				AppendText("Server :: 500 오류!!");
				e1.printStackTrace();
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
			
		}
		
		public void ViewFriendList(Object ob) { // 900
			try {
				String friendlist = new String();
				
				for (int i = 0; i < user_vc.size(); i++) {
					UserService user = (UserService) user_vc.elementAt(i);
//					if (user == this) {
//						//본인인 경우
//					}
					friendlist.concat(user.UserName + " ");
					ChatMsg obcm = new ChatMsg(user.UserName, "900", friendlist);
					//obcm.chatuserlists = friendlist;
					oos.writeObject(obcm);
				}
			} catch (IOException e1) {
				AppendText("Server :: 900 오류!!");
				e1.printStackTrace();
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}
		
		public void MakeChatRooms(Object ob) { // 1000. userlist 받음
			try {
				ChatMsg obcm = (ChatMsg) ob;
				ChatMsg sendobcm; // 개인에게 뿌릴 용도
				String inviteUser = obcm.chatuserlists; // userlist 분리, 개인에게 뿌리기 용
				String[] args = inviteUser.split(" ");
				Date date = new Date();
				String roomid = new String(inviteUser + date);
				
				ChatService new_chat = new ChatService(roomid, obcm.chatuserlists); // 채팅방 생성
				RoomVec.add(new_chat); // 새로운 채팅방 배열에 추가
				
				for (int i = 0; i < user_vc.size(); i++) { // 전체 유저 목록에서 확인 후
					UserService user = (UserService) user_vc.elementAt(i);
				if (inviteUser.contains(user.UserName)) { // 초대한 사람이면 채팅방 뿌리기
					sendobcm = new ChatMsg(user.UserName, "1010", "newRoom");  // 방 생성 메시지
					sendobcm.chatroomid = roomid;  // 방 id, userlist 붙여 전송
					sendobcm.chatuserlists = obcm.chatuserlists;
					user.oos.writeObject(sendobcm);
				}
						
				// 초대 순서 바뀌면 앞사람 잘림
//					if (user.UserName.equals(args[i])) { // 전체 유저 목록에서 확인 후 초대한 사람이면
//						// 채팅방 뿌리기       ///  index length 2~~~ err
//						// if (user == this) // 본인인 경우
//						sendobcm = new ChatMsg(user.UserName, "1010", "newRoom");  // 방 생성 메시지
//						sendobcm.chatroomid = roomid;  // 방 id, userlist 붙여 전송
//						sendobcm.chatuserlists = obcm.chatuserlists;
//						user.oos.writeObject(sendobcm);
//					}
					
				}
				AppendText("1000 완료");
				
				
			} catch (IOException e1) {
				AppendText("Server :: 1000 오류!!");
				e1.printStackTrace();
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}
		
		public void ChatRoomLog(Object ob) {
			try {
				ChatMsg obcm = (ChatMsg) ob;
				ChatMsg sendobcm; // 개인에게 뿌릴 용도
				String user = obcm.UserName;
				String roomid = obcm.chatroomid;
				for (int i = 0; i < RoomVec.size(); i++) { // 채팅방을 찾아서
					ChatService chat = (ChatService) RoomVec.elementAt(i);
					if (roomid.equals(chat.Chatroom_id)) { // 입장한 채팅방의 로그를 보내줌(클라가 다시 보내야함)
						for (ChatMsg cm: chat.msg_vc) {
							sendobcm = cm;
							oos.writeObject(sendobcm);
						}
				}
			}
					
				AppendText("1001 완료");				
			} catch (IOException e1) {
				AppendText("Server :: 1001 오류!!");
				e1.printStackTrace();
				Logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}
		
		public void run() {
			while (true) { // 사용자 접속을 계속해서 받기 위해 while문
				try {
					Object obcm = null;
					String msg = null;
					ChatMsg cm = null;
					if (socket == null)
						break;
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
					if (obcm == null)
						break;
					if (obcm instanceof ChatMsg) {
						cm = (ChatMsg) obcm;
						AppendObject(cm);
					} else
						continue;
					if (cm.code.matches("100")) {
						UserName = cm.UserName;
						///
						for (int j = 0; j <user_vc.size(); j++) { // logout인 경우 재접속
							UserService user = (UserService) user_vc.elementAt(j);
							if (cm.UserName.equals(user.UserName)) { // 나갈 때 벡터 지우므로 벡터 제거 필요x
								
							}
						}
						///
						if(cm.img != null) {
							UserImg = cm.img;
						}
						if(cm.statusmsg != null) {
							StateMsg = cm.statusmsg;
						}
						UserStatus = "O"; // Online 상태
						Login(UserName); // 본인 로그인
						//AllLogin(cm.getData());  추가로 들어오는 유저도 추가
						for(int i = 0; i < user_vc.size(); i++) {
							UserService user = (UserService) user_vc.elementAt(i);
							if (UserName.equals(user.UserName))
								continue; // user.Login(본인에게 친구 로그인 정보)
							AllLogin(user.UserName, user.UserImg, user.UserStatus, user.StateMsg);
						}
					} else if (cm.code.matches("200")) { // 채팅목록 저장 >> 들어온 프로토콜,메시지 저장?
						msg = String.format("[%s] %s", cm.UserName, cm.data);
						AppendText(msg); // server 화면에 출력
						//String[] args = msg.split(" "); // 단어들을 분리한다.
						for (int i = 0; i < RoomVec.size(); i++) {
							ChatService chat = (ChatService) RoomVec.elementAt(i);
							if (cm.chatroomid.equals(chat.Chatroom_id)) {
								chat.msg_vc.add(cm); // 로그 기록(chatmsg 자체 저장)
								String userlist = chat.Chatroom_user;
								for (int j = 0; j < user_vc.size(); j++) {
								UserService user = (UserService) user_vc.elementAt(j);
								if (userlist.contains(user.UserName)) {
									user.WriteOneObject(cm);
								}
							}
						}
					}
						
						////////////
						/*
						if (args.length == 1) { // Enter key 만 들어온 경우 Wakeup 처리만 한다.
							UserStatus = "O";
						}  else if (args[1].matches("/list")) {
							WriteOne("User list\n");
							WriteOne("Name\tStatus\n");
							WriteOne("-----------------------------\n");
							for (int i = 0; i < user_vc.size(); i++) {
								UserService user = (UserService) user_vc.elementAt(i);
								WriteOne(user.UserName + "\t" + user.UserStatus + "\n");
							}
							WriteOne("-----------------------------\n");
						} else if (args[1].matches("/sleep")) {
							UserStatus = "S";
						} else if (args[1].matches("/wakeup")) {
							UserStatus = "O";
						} else if (args[1].matches("/to")) { // 귓속말
							for (int i = 0; i < user_vc.size(); i++) {
								UserService user = (UserService) user_vc.elementAt(i);
								if (user.UserName.matches(args[2]) && user.UserStatus.matches("O")) {
									String msg2 = "";
									for (int j = 3; j < args.length; j++) {// 실제 message 부분
										msg2 += args[j];
										if (j < args.length - 1)
											msg2 += " ";
									}
									// /to 빼고.. [귓속말] [user1] Hello user2..
									user.WritePrivate(args[0] + " " + msg2 + "\n");
									//user.WriteOne("[귓속말] " + args[0] + " " + msg2 + "\n");
									break;
								}
							}
						} else { // 일반 채팅 메시지
							UserStatus = "O";
							//WriteAll(msg + "\n"); // Write All
							WriteAllObject(cm);
						}*/
					} else if (cm.code.matches("400")) { // logout message 처리
						Logout();
						break;
					} else if (cm.code.matches("300")) { // img
						WriteAllObject(cm);
//						msg = String.format("[%s] %s", cm.UserName, cm.data);
//						AppendText(msg); // server 화면에 출력
//						String[] args = msg.split(" "); // 단어들을 분리한다.
//						for (int i = 0; i < RoomVec.size(); i++) {
//							ChatService chat = (ChatService) RoomVec.elementAt(i);
//							if (cm.chatroomid.equals(chat.Chatroom_id)) {
//								String userlist = chat.Chatroom_user;
//								for (int j = 0; j < user_vc.size(); j++) {
//								UserService user = (UserService) user_vc.elementAt(j);
//								if (userlist.contains(user.UserName)) {
//									user.WriteOneObject(cm);
//								}
//							}
//						}
//					}
					} else if (cm.code.matches("301")) { // file
						WriteAllObject(cm);
					}
					else if (cm.code.matches("500")) { // 채팅방 리스트
						//AppendText("500 received");
						//ViewChatList(cm); // 채팅방 정보 전송
						
					}
					else if (cm.code.matches("600")) { // 이모티콘
						
					} 
					else if (cm.code.matches("700")) { // 공지사항
						
					} else if (cm.code.matches("701")) { // 공지사항 보기 버튼
						
					} else if (cm.code.matches("702")) { // 공지사항 삭제
						
					}
					else if (cm.code.matches("800")) { // 투표
						
					} else if (cm.code.matches("801")) { // 투표 선택
						
					} else if (cm.code.matches("802")) { // 투표 결과
						
					}
					else if (cm.code.matches("900")) { // 친구 목록 화면
						AppendText("900 received");
						ViewFriendList(cm);
					}		
					else if (cm.code.matches("1000")) { // 채팅방 초대
						AppendText("1000 received");
						MakeChatRooms(cm);
					} else if (cm.code.matches("1001")) { // 채팅방 로그 불러오기
						AppendText("1001 received");
						ChatRoomLog(cm);
					}
					
				} catch (IOException e) {
					AppendText("ois.readObject() error");
					try {
						ois.close();
						oos.close();
						client_socket.close();
						Logout(); // 에러가난 현재 객체를 벡터에서 지운다
						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝
			} // while
		} // run
	}

}
