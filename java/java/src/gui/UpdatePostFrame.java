package gui;

import javax.swing.*;
import model.Member;
import model.Post;
import dao.PostDAOImpl;

import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class UpdatePostFrame extends JDialog {
    private static final long serialVersionUID = 1L;
    private Member member;
    private PostDAOImpl postDAO;
	private BoardFrame boardFrame;

    public UpdatePostFrame(Member member, BoardFrame boardFrame) {
        super((Frame) null, "게시글 수정", true);
        this.member = member;
        this.postDAO = new PostDAOImpl();
        this.boardFrame = boardFrame; 

        // 기본 설정
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        // 메인 패널
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 상단 패널
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setPreferredSize(new Dimension(600, 80));

        // 로고 이미지
        ImageIcon logoIcon = new ImageIcon("../../img/logo.png");
        Image img = logoIcon.getImage();
        Image newImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(newImg));

        // 제목 레이블
        JLabel topTitleLabel = new JLabel("게시글 수정", SwingConstants.CENTER);
        topTitleLabel.setFont(new Font("굴림", Font.BOLD, 20));

        // 인사말 레이블
        JLabel greetingLabel = new JLabel(member.getUserName() + "님", SwingConstants.RIGHT);
        greetingLabel.setFont(new Font("굴림", Font.PLAIN, 12));

        topPanel.add(logoLabel, BorderLayout.WEST);
        topPanel.add(topTitleLabel, BorderLayout.CENTER);
        topPanel.add(greetingLabel, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // 제목 입력란
        JTextField titleField = createTitleField();
        
        // 본문 내용 입력란
        JTextArea contentArea = createContentArea();

        // 본문 내용을 위한 스크롤 패널
        JScrollPane contentScrollPane = new JScrollPane(contentArea);
        contentScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        contentScrollPane.setPreferredSize(new Dimension(580, 150));

        // 제목 및 본문 패널
        JPanel titleContentPanel = new JPanel();
        titleContentPanel.setLayout(new BorderLayout(10, 10));
        titleContentPanel.add(titleField, BorderLayout.NORTH);
        titleContentPanel.add(contentScrollPane, BorderLayout.CENTER);
        mainPanel.add(titleContentPanel, BorderLayout.CENTER);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton submitButton = new JButton("작성완료");
        JButton cancelButton = new JButton("취소");

        // 작성 완료 버튼 기능 설정
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handlePostSubmission(titleField, contentArea, mainPanel);
            }
        });

        // 취소 버튼 기능
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int response = JOptionPane.showConfirmDialog(mainPanel, "작성 중인 게시글을 취소하시겠습니까?", "취소 확인",
                        JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    dispose();
                }
            }
        });

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // ESC = 취소 Click Trigger
        mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "cancel");
        mainPanel.getActionMap().put("cancel", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int response = JOptionPane.showConfirmDialog(mainPanel, "게시글 작성을 취소하겠습니까?", "취소 확인",
                        JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    dispose();
                }
            }
        });

        setContentPane(mainPanel);
        pack();
        setVisible(true);
    }

    private JTextField createTitleField() {
        JTextField titleField = new JTextField("제목은 30자 이내로 작성 해주세요.", 30);
        titleField.setFont(new Font("굴림", Font.PLAIN, 14));
        titleField.setForeground(Color.GRAY);
        titleField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (titleField.getText().equals("제목은 30자 이내로 작성 해주세요.")) {
                    titleField.setText("");
                    titleField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (titleField.getText().isEmpty()) {
                    titleField.setForeground(Color.GRAY);
                    titleField.setText("제목은 30자 이내로 작성 해주세요.");
                }
            }
        });

        // 제목 길이 제한
        titleField.setDocument(new PlainDocument() {
            private static final long serialVersionUID = 1L;

            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if (str == null) return;

                if (getLength() + str.length() <= 30) {
                    super.insertString(offs, str, a);
                } else {
                    JOptionPane.showMessageDialog(titleField, "제목은 30자 이내로 작성해야 합니다.", "길이 초과 경고",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        return titleField;
    }

    private JTextArea createContentArea() {
        JTextArea contentArea = new JTextArea("본문 내용은 500자 이내로 작성해주세요.");
        contentArea.setFont(new Font("굴림", Font.PLAIN, 14));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setForeground(Color.GRAY);

        // 본문 길이 제한
        contentArea.setDocument(new PlainDocument() {
            private static final long serialVersionUID = 1L;

            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if (str == null) return;

                if (getLength() + str.length() <= 500) {
                    super.insertString(offs, str, a);
                } else {
                    JOptionPane.showMessageDialog(contentArea, "본문 내용은 500자 이내로 작성해야 합니다.", "길이 초과 경고",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        contentArea.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (contentArea.getText().equals("본문 내용은 500자 이내로 작성해주세요.")) {
                    contentArea.setText("");
                    contentArea.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (contentArea.getText().isEmpty()) {
                    contentArea.setForeground(Color.GRAY);
                    contentArea.setText("본문 내용은 500자 이내로 작성해주세요.");
                }
            }
        });

        return contentArea;
    }

    private void handlePostSubmission(JTextField titleField, JTextArea contentArea, JPanel mainPanel) {
        String title = titleField.getText();
        String contents = contentArea.getText();

        // 제목 및 본문 입력 유효성 검사
        if (title.isEmpty() || title.equals("제목은 30자 이내로 작성 해주세요.")) {
            JOptionPane.showMessageDialog(mainPanel, "제목을 입력하세요.");
            return;
        }

        if (contents.isEmpty() || contents.equals("본문 내용은 500자 이내로 작성해주세요.")) {
            JOptionPane.showMessageDialog(mainPanel, "본문 내용을 입력하세요.");
            return;
        }

        // 제목과 본문 길이 검사
        if (title.length() > 30) {
            JOptionPane.showMessageDialog(mainPanel, "제목은 30자 이내로 작성해야 합니다.");
            return;
        }

        if (contents.length() > 500) {
            JOptionPane.showMessageDialog(mainPanel, "본문 내용은 500자 이내로 작성해야 합니다.");
            return;
        }

        Post post = new Post();
        post.setpUserId(member.getUserId());
        post.setPostTitle(title);
        post.setContents(contents);

        // 게시글 등록
        if (postDAO.createPost(post)) {
            JOptionPane.showMessageDialog(mainPanel, "게시글이 작성되었습니다.");
            boardFrame.refreshPostList();
        } else {
            JOptionPane.showMessageDialog(mainPanel, "게시글 작성에 실패했습니다.");
        }

        dispose(); // 창 닫기
    }
}
