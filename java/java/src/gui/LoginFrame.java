package gui;

import model.Member;
import util.DBconn;
import util.PwChk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    public LoginFrame() {
        setTitle("Login");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2)); 

        JLabel userLabel = new JLabel("아이디:");
        JTextField userText = new JTextField(20);
        JLabel passwordLabel = new JLabel("비밀번호:");
        JPasswordField passwordText = new JPasswordField(20);

        // 필드 길이 제한
        userText.setDocument(new JTextFieldLimit(20)); 
        passwordText.setDocument(new JTextFieldLimit(60)); 

        JButton loginButton = new JButton("로그인");
        JButton signupButton = new JButton("회원가입");

        Action loginAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userText.getText();
                String password = new String(passwordText.getPassword());

                Member member = getMember(username, password);

                if (member != null) {
                    new MainFrame(member); 
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "아이디 혹은 비밀번호를 확인해주세요.");
                    userText.requestFocus();  
                }
            }
        };

        userText.addActionListener(loginAction);
        passwordText.addActionListener(loginAction);
        loginButton.addActionListener(loginAction);

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RegFrame();
            }
        });

        // ESC 키 이벤트 리스너 추가
        KeyListener escKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    int response = JOptionPane.showConfirmDialog(
                        LoginFrame.this,
                        "프로그램을 종료하시겠습니까?",
                        "종료 확인",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );
                    if (response == JOptionPane.YES_OPTION) {
                        System.exit(0);
                    }
                }
            }
        };

        // 각 필드 ESC 기능 호출 
        userText.addKeyListener(escKeyListener);
        passwordText.addKeyListener(escKeyListener);

        panel.add(userLabel);
        panel.add(userText);
        panel.add(passwordLabel);
        panel.add(passwordText);
        panel.add(loginButton);
        panel.add(signupButton);

        add(panel);
        setVisible(true); 
    }

    private Member getMember(String username, String password) {
        String sql = "SELECT * FROM MEMBER_LIST WHERE USER_ID = ?";

        try (Connection conn = DBconn.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("USER_PW");

                    // PwChk 유효성 검사 코드
                    if (PwChk.checkPassword(password, hashedPassword)) {
                        return new Member(
                            rs.getString("UID"),
                            rs.getString("USER_NAME"),
                            rs.getString("USER_ID"),
                            hashedPassword,
                            rs.getString("USER_EMAIL"),
                            rs.getDate("REG_DATE")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 각 필드 최대 길이 제한
    class JTextFieldLimit extends javax.swing.text.PlainDocument {
        private int limit;

        public JTextFieldLimit(int limit) {
            this.limit = limit;
        }

        @Override
        public void insertString(int offset, String str, javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
            if (str == null) {
                return;
            }
            if ((getLength() + str.length()) <= limit) {
                super.insertString(offset, str, attr);
            }
        }
    }
}
