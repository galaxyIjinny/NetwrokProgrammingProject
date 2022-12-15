
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Window;
import java.awt.Color;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
// project > properties > java compiler ���� Ȯ��
import javax.swing.text.StyledDocument;

public class JavaObjClientChatView extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtInput;
	private String UserName;
	private JButton btnSend;
	private static final int BUF_LEN = 128;

	private JLabel lblUserName;
	// private JTextArea textArea;
	private JTextPane textArea;

	private Frame frame;
	private FileDialog fd;
	private JButton imgBtn;
	
	public JavaObjClientChatView view; // this 넘겨주는 용도 
	public JavaObjClientView Mainview;
	public JavaGameClientViewDrawing drawing;
	public FriendLabel FriendLabelHash;
	public String Room_ID;
	public String UserList;
	public ArrayList<FriendLabel> FriendLabelList = new ArrayList<FriendLabel>();
	
	/**
	 * Create the frame.
	 */
	public JavaObjClientChatView(JavaObjClientView mainview, String username, String roomid, String userlist) {
		UserName = username;
		Room_ID = roomid;
		UserList = userlist;
		Mainview = mainview;
		view = this;
		
		setTitle(roomid);
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(mainview.getX()+50, mainview.getY()+50, 388, 623);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(230, 230, 230));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 0, 372, 511);
		scrollPane.setBorder(null);
		contentPane.add(scrollPane);

		textArea = new JTextPane();
		textArea.setBackground(new Color(181, 233, 255));
		textArea.setEditable(true);
		textArea.setBorder(null);
		textArea.setFont(new Font("굴림체", Font.PLAIN, 14));
		scrollPane.setViewportView(textArea);

		txtInput = new JTextField();
		txtInput.setBounds(42, 508, 243, 35);
		txtInput.setBorder(null);
		contentPane.add(txtInput);
		txtInput.setColumns(10);

		btnSend = new JButton("");
		btnSend.setBorder(null);
		btnSend.setFont(new Font("굴림", Font.PLAIN, 14));
		btnSend.setBounds(286, 508, 42, 35);
		contentPane.add(btnSend);

		lblUserName = new JLabel(username);
		lblUserName.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUserName.setBackground(Color.WHITE);
		lblUserName.setFont(new Font("굴림", Font.BOLD, 14));
		lblUserName.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName.setBounds(0, 544, 42, 35);
		contentPane.add(lblUserName);
		setVisible(true);

		imgBtn = new JButton("+");
		imgBtn.setBorder(null);
		imgBtn.setFont(new Font("굴림", Font.PLAIN, 16));
		imgBtn.setBounds(0, 509, 42, 34);
		contentPane.add(imgBtn);
		
		JButton btnEmo = new JButton("");
		btnEmo.setBorder(null);
		btnEmo.setFont(new Font("굴림", Font.PLAIN, 14));
		btnEmo.setBounds(330, 508, 42, 35);
		contentPane.add(btnEmo);
		
		
//		JButton btnDrawing = new JButton("Drawing");
//		btnDrawing.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				drawing = new JavaGameClientViewDrawing(username, Mainview);
//			}
//		});
//		btnDrawing.setBounds(370, 619, 114, 40);
//		contentPane.add(btnDrawing);
		
		
		try {
			TextSendAction action = new TextSendAction();
			btnSend.addActionListener(action);
			txtInput.addActionListener(action);
			txtInput.requestFocus();
			ImageSendAction action2 = new ImageSendAction();
			imgBtn.addActionListener(action2);
		} catch(NumberFormatException e) {
			e.printStackTrace();
		}
	}

//	public void SetKeyGameMouse() { // 버튼 선택된 것 white background 설정에서 해제된 것처럼
//		if (keygame == null) {
//			keygame = new JavaObjClientChatView.GameKeyMouse(view, mainview);
//			keygame.REpaint();
//		}
//	}
		
	class MyWindowAdapter extends WindowAdapter { // x 종료버튼 처리
		MyWindowAdapter() {	}
		public void windowClosing(WindowEvent e) {
			Window wnd = e.getWindow();
			wnd.setVisible(false);
		}
//		@Override
//		public void windowOpened(WindowEvent e) {
//			setBounds(mainview.getX() + 50, mainview.getY() + 50, 395, 625);
//		}
	}
			
	// keyboard enter key 치면 서버로 전송
		class TextSendAction implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Send button을 누르거나 메시지 입력하고 Enter key 치면
				if (e.getSource() == btnSend || e.getSource() == txtInput) {
					String msg = null;
					// msg = String.format("[%s] %s\n", UserName, txtInput.getText());
					msg = txtInput.getText();
					SendMessage(msg);
					txtInput.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
					txtInput.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
					if (msg.contains("/exit")) // 종료 처리
						System.exit(0);
				}
			}
		}

	class ImageSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// 액션 이벤트가 sendBtn일때 또는 textField 에세 Enter key 치면
			if (e.getSource() == imgBtn) {
				frame = new Frame("이미지 첨부");
				fd = new FileDialog(frame, "이미지선택", FileDialog.LOAD);
				// frame.setVisible(true);
				// fd.setDirectory(".\\");
				fd.setVisible(true);
				//System.out.println(fd.getDirectory() + fd.getFile());
				String dir = fd.getDirectory();
				String file = fd.getFile();
				if (dir != null && file != null) {
				ChatMsg obcm = new ChatMsg(UserName, "300", "IMG");
				ImageIcon img = new ImageIcon(dir + file);
				obcm.chatroomid = Room_ID;
				obcm.Date = new Date();
				obcm.img = img;
				Mainview.SendObject(obcm);
				}
			}
		}
	}

	public void AppendIcon(ImageIcon icon) {
		AppendTextL("");
		int len = textArea.getDocument().getLength();
		// 끝으로 이동
		textArea.setCaretPosition(len);
		textArea.insertIcon(icon);
	}

	public void ChangeFriendProfile(ChatMsg cm) {
		FriendLabel f = FriendLabelHash.get(Mainview, cm.UserName); // 프사바꾸는 친구
		if (f == null)
			return;
		f.profileimg = cm.img; // 바꾸려는 이미지로 바꾼 후
		for (FriendLabel f1: FriendLabelList) { // 현재 채팅방 친구 라벨 목록에서 확인 후 변경
			if (f1.username.equals(cm.UserName))
				f1.profileimg = cm.img;
		}
	}
	
	public void CreateFriendIconHash() { // 친구 목록에서 userprofile 가져와 이미지 label에 저장
		String[] users = UserList.split(" ");
		for (int i=0; i<users.length; i++) {
			ImageIcon icon = Mainview.getUserIcon(users[i]);
			////
			/*for (UserProfile f: FriendVector) {
				if(f.username.equals(name))
					return f.profileimg;
			}*/
			/////
			if (icon == null)
				continue;
			FriendLabel f= new FriendLabel(icon, users[i]); // 라벨에 저장
			FriendLabelHash.put(users[i], f);
		}
	}
	
	// 화면에 출력
	public synchronized void AppendText(ChatMsg cm) {
		if (cm.UserName.equals(UserName)) {
			AppendTextR(cm);
		} else {
			FriendLabelHash = new FriendLabel(cm.img, cm.UserName);
			AppendTextL(cm);
		}
	}
	
	public void AppendTextL(ChatMsg cm) {
		FriendLabel f = FriendLabelHash.get(Mainview, cm.UserName); // 유저 정보 설정
		FriendLabel f2 = new FriendLabel(f.profileimg, f.username); // 이미지or라벨 문제??
		FriendLabelList.add(f2);
		textArea.setCaretPosition(textArea.getDocument().getLength());
		textArea.insertComponent(f2);
		//AppendTextL("\n");
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
		JLabel ll = new JLabel(" " + cm.data + " "); // 메시지 출력부분
		ll.setBorder(null);
		ll.setOpaque(true);
		ll.setBorder(null);
		ll.setFont(new Font("굴림", Font.PLAIN, 15));
		ll.setBackground(Color.WHITE);
		textArea.insertComponent(ll);
		
		JLabel lt = new JLabel(getTime(cm.Date));
		lt.setOpaque(false);
		lt.setBorder(null);
		lt.setFont(new Font("굴림", Font.PLAIN, 13));
		textArea.setCaretPosition(textArea.getDocument().getLength());
		textArea.insertComponent(lt);
		
		len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
		AppendTextL("\n");
	}
	
	public synchronized void AppendTextL(String msg) {
		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet left = new SimpleAttributeSet();
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		StyleConstants.setForeground(left, Color.BLACK);
		if (msg!=null && !msg.equals("") && !msg.equals("\n"))
			StyleConstants.setBackground(left, Color.WHITE);
		doc.setParagraphAttributes(doc.getLength(), 1, left, true);
		try {
			if (msg.equals("\n"))
				doc.insertString(doc.getLength(), msg, left);
			else
				doc.insertString(doc.getLength(), " " + msg + " ", left);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}
	
	public synchronized void AppendTextR(String msg) {
		int len = textArea.getDocument().getLength();
		msg = msg.trim();
		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet right = new SimpleAttributeSet();
		StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
		StyleConstants.setForeground(right, Color.BLACK);
		doc.setParagraphAttributes(doc.getLength(), 1, right, true);
		if (msg!=null && !msg.equals("") && !msg.equals("\n")) {
			JLabel lt = new JLabel(getTime(new Date())); // 날짜 덧붙여 말풍선
			lt.setOpaque(false);
			lt.setBorder(null);
			lt.setFont(new Font("굴림",Font.PLAIN, 13));
			lt.setBackground(Color.YELLOW);
			textArea.setCaretPosition(textArea.getDocument().getLength());
			textArea.insertComponent(lt);
			
			JLabel ll = new JLabel(" " + msg + " ");
			ll.setOpaque(true);
			ll.setBorder(null);
			ll.setFont(new Font("굴림", Font.PLAIN, 15));
			ll.setBackground(Color.YELLOW);
			textArea.setCaretPosition(textArea.getDocument().getLength());
			textArea.insertComponent(ll);
		}
			try {
				doc.insertString(doc.getLength(), "\n", right);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			textArea.setCaretPosition(textArea.getDocument().getLength());
	}
	
	public synchronized void AppendTextR(ChatMsg cm) {
		String msg = cm.data;
		int len = textArea.getDocument().getLength();
		msg = msg.trim();
		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet right = new SimpleAttributeSet();
		StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
		StyleConstants.setForeground(right, Color.BLACK);
		doc.setParagraphAttributes(doc.getLength(), 1, right, false);
		if (msg!=null && !msg.equals("") && !msg.equals("\n")) {
			JLabel lt = new JLabel(getTime(cm.Date));
			lt.setOpaque(false);
			lt.setBorder(null);
			lt.setFont(new Font("굴림",Font.PLAIN, 13));
			lt.setBackground(Color.YELLOW);
			textArea.setCaretPosition(textArea.getDocument().getLength());
			textArea.insertComponent(lt);
			
			JLabel ll = new JLabel(" " + msg + " ");
			ll.setOpaque(true);
			ll.setBorder(null);
			ll.setFont(new Font("굴림", Font.PLAIN, 15));
			ll.setBackground(Color.YELLOW);
			textArea.setCaretPosition(textArea.getDocument().getLength());
			textArea.insertComponent(ll);
		}
			try {
				doc.insertString(doc.getLength(), "\n", right);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			textArea.setCaretPosition(textArea.getDocument().getLength());
	}
	
	//public synchronized void AppendImage(ImageIcon ori_icon)
	public synchronized void AppendImage(ChatMsg cm) {
		if (cm.UserName.equals(UserName)) {
			AppendTextR("");
			JLabel lt = new JLabel(getTime(cm.Date));
			lt.setOpaque(false);
			lt.setBorder(null);
			lt.setFont(new Font("굴림", Font.PLAIN, 13));
			lt.setBackground(Color.YELLOW);
			textArea.setCaretPosition(textArea.getDocument().getLength());
			textArea.insertComponent(lt);
		} else {
			FriendLabelHash = new FriendLabel(cm.img, cm.UserName);
			FriendLabel f = FriendLabelHash.get(Mainview, cm.UserName);
			FriendLabel f2 = new FriendLabel(f.profileimg, f.username);
			FriendLabelList.add(f2);
			textArea.setCaretPosition(textArea.getDocument().getLength());
			textArea.insertComponent(f2);
			AppendTextL("\n");
			textArea.setCaretPosition(textArea.getDocument().getLength());
		}
		ImageIcon ori_icon = cm.img; ///---
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len); // place caret at the end (with no selection)
		Image ori_img = ori_icon.getImage();
		Image new_img;
		ImageIcon new_icon;
		int width, height;
		double ratio;
		width = ori_icon.getIconWidth();
		height = ori_icon.getIconHeight();
		// Image가 너무 크면 최대 가로 또는 세로 200 기준으로 축소시킨다.
		if (width > 200 || height > 200) {
			if (width > height) { 
				ratio = (double) height / width;
				width = 200;
				height = (int) (width * ratio);
			} else { 
				ratio = (double) width / height;
				height = 200;
				width = (int) (height * ratio);
			}
			new_img = ori_img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			new_icon = new ImageIcon(new_img);
			
			JButton ll = new JButton();
			ll.setBorder(null);
			ll.setIcon(new_icon);
			ll.setBorderPainted(false);
			ll.setOpaque(false);
			ll.setBorder(null);
			ll.setBackground(Color.WHITE);
			
			textArea.insertIcon(new_icon);
			
			if(!cm.UserName.equals(UserName)) { // 오른쪽에 시간 추가
				JLabel lt = new JLabel(getTime(cm.Date));
				lt.setOpaque(false);
				lt.setBorder(null);
				lt.setFont(new Font("굴림", Font.PLAIN, 13));
				textArea.setCaretPosition(textArea.getDocument().getLength());
				textArea.insertComponent(lt);
			}
			
			//textArea.insertComponent(ll);
			ll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
//					ChatClientChatRoomImageView imgView = new ChatClientChatRoomImageView();
				}
			});
//		} else {
//			textArea.insertIcon(ori_icon);
//		len = textArea.getDocument().getLength();
//		textArea.setCaretPosition(len);
//		textArea.replaceSelection("\n");
		// ImageViewAction viewaction = new ImageViewAction();
		// new_icon.addActionListener(viewaction); // 내부클래스로 액션 리스너를 상속받은 클래스로
		
			}else {	
			JButton ll = new JButton();
			ll.setBorder(null);
			ll.setIcon(ori_icon);
			ll.setBorderPainted(false);
			ll.setOpaque(false);
			ll.setBorder(null);
			ll.setBackground(Color.WHITE);
			
			textArea.insertComponent(ll);
//			ll.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					ChatClientChatRoomImageView imgView = new ChatClientChatRoomImageView();
//				}
//			});
			} 
		len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
		
		if (cm.UserName.equals(UserName))
			AppendTextR("\n");
		else
			AppendTextL("\n");
		
//		if(drawing != null) {
//			drawing.ClearScreen();
//			drawing.gc2.drawImage(ori_img, 0, 0, drawing.panel.getWidth(), drwaing..)
//			drawing.gc.drawImage(drawing.panelImage, 0, 0, drawing.panel);
//		}
		
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
			System.exit(0);
		}
		for (i = 0; i < bb.length; i++)
			packet[i] = bb[i];
		return packet;
	}

	public void SendMessage(String msg) { // 서버로 메세지를 보내는 메소드
		ChatMsg cm = new ChatMsg(UserName, "200", msg);
		cm.Date = new Date(); 
		cm.chatroomid = Room_ID;
		Mainview.SendObject(cm);
	}
	
	public String getTime(Date date) {
		SimpleDateFormat f = new SimpleDateFormat(" h시 m분 ");
		return f.format(date);
	}
}
