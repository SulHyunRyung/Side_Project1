package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import model.Comment;
import model.Member;
import model.Post;
import dao.CommentDAO;
import dao.CommentDAOImpl;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.List;

public class PostViewFrame extends JDialog {
    private static final long serialVersionUID = 1L;
    private Post post;
    private Member member;
    private CommentDAO commentDAO;
    private JTextField commentField;

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}
    public PostViewFrame(Post post, Member member) {
        super((Frame) null, "게시물 보기", true);
        this.post = post;
        this.setMember(member);
        this.commentDAO = new CommentDAOImpl();

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // 메인 패널
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        // 상단 패널 (로고, 제목, 작성자, 작성일자 등)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        // 로고 이미지 (100x100 크기) 좌측 정렬
        ImageIcon logoIcon = new ImageIcon("../../img/logo.png");
        Image img = logoIcon.getImage();
        Image newImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(newImg));
        logoLabel.setPreferredSize(new Dimension(100, 100));
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logoPanel.add(logoLabel);
        topPanel.add(logoPanel, BorderLayout.WEST);

        // 제목 (가운데 정렬)
        JLabel titleLabel = new JLabel(post.getPostTitle());
        titleLabel.setFont(new Font("굴림", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // 작성자, 작성일자, 수정일자 패널
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        JPanel authorDatePanel = new JPanel();
        authorDatePanel.setLayout(new BoxLayout(authorDatePanel, BoxLayout.Y_AXIS));

        JLabel authorLabel = new JLabel("작성자 : " + post.getpUserId());
        authorLabel.setFont(new Font("굴림", Font.PLAIN, 12));

        JLabel createDateLabel = new JLabel("작성일자 : " + dateFormat.format(post.getCreateDate()));
        createDateLabel.setFont(new Font("굴림", Font.PLAIN, 12));

        JLabel updateDateLabel = null;
        if (!dateFormat.format(post.getCreateDate()).equals(dateFormat.format(post.getUpdateDate()))) {
            updateDateLabel = new JLabel("수정일자 : " + dateFormat.format(post.getUpdateDate()));
            updateDateLabel.setFont(new Font("굴림", Font.PLAIN, 12));
        }

        authorDatePanel.add(authorLabel);
        authorDatePanel.add(createDateLabel);
        if (updateDateLabel != null) {
            authorDatePanel.add(updateDateLabel);
        }

        topPanel.add(authorDatePanel, BorderLayout.EAST);
        mainPanel.add(topPanel);

        // 게시글 내용
        JTextArea contentArea = new JTextArea(post.getContents());
        contentArea.setFont(new Font("굴림", Font.PLAIN, 14));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setEditable(false);
        JScrollPane contentScrollPane = new JScrollPane(contentArea);
        contentScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        contentScrollPane.setPreferredSize(new Dimension(580, 200));
        mainPanel.add(contentScrollPane);

     // 댓글 테이블
        String[] columnNames = {"USER_NAME", "COMMENT", "WRITE_DATE"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
			private static final long serialVersionUID = -473041902424109532L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false;  // 셀을 편집할 수 없게 설정
            }
        };

        JTable commentsTable = new JTable(tableModel);
        commentsTable.setRowHeight(70);
        TableColumnModel columnModel = commentsTable.getColumnModel();

        // 열 너비 비율 설정
        int tableWidth = 580;  // JTable 전체 너비
        int[] columnWidths = {
            (int) (tableWidth * 0.20),  // 첫 번째 열 (USER_NAME) 30%
            (int) (tableWidth * 0.60),  // 두 번째 열 (COMMENT) 55%
            (int) (tableWidth * 0.20)   // 세 번째 열 (WRITE_DATE) 15%
        };

        // 각 열의 너비를 설정
        columnModel.getColumn(0).setPreferredWidth(columnWidths[0]);  // USER_NAME 열의 너비 설정
        columnModel.getColumn(1).setPreferredWidth(columnWidths[1]);  // COMMENT 열의 너비 설정
        columnModel.getColumn(2).setPreferredWidth(columnWidths[2]);  // WRITE_DATE 열의 너비 설정

        // JTextArea 셀 렌더러 정의
        DefaultTableCellRenderer commentCellRenderer = new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 8857970459268400619L;

			@Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JTextArea textArea = new JTextArea(value != null ? value.toString() : "");
                textArea.setLineWrap(true);  // 줄 바꿈 허용
                textArea.setWrapStyleWord(true);  // 단어 단위로 줄 바꿈
                textArea.setFont(table.getFont());  // 테이블의 폰트 사용
                textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));  // 테두리 설정

                if (isSelected) {
                    textArea.setBackground(table.getSelectionBackground());
                    textArea.setForeground(table.getSelectionForeground());
                } else {
                    textArea.setBackground(table.getBackground());
                    textArea.setForeground(table.getForeground());
                }
                return textArea;
            }
        };

        // 두 번째 컬럼(Comment)에 셀 렌더러 적용
        columnModel.getColumn(1).setCellRenderer(commentCellRenderer);

        // 댓글 테이블을 JScrollPane에 추가
        JScrollPane commentsScrollPane = new JScrollPane(commentsTable);
        commentsScrollPane.setPreferredSize(new Dimension(580, 150));

        // 기존 레이아웃에서 JScrollPane을 올바르게 배치
        mainPanel.add(commentsScrollPane);


        // 댓글 데이터 로드
        loadComments(tableModel);

        // 댓글 작성 입력창 및 버튼 패널
        JPanel commentPanel = new JPanel(new BorderLayout());
        commentField = new JTextField("댓글은 100글자까지 입력 가능합니다.");
        // 100글자 제한 DocumentFilter 추가
        ((AbstractDocument) commentField.getDocument()).setDocumentFilter(new DocumentFilter() {
            private final int MAX_CHARACTERS = 100;

            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (fb.getDocument().getLength() + string.length() <= MAX_CHARACTERS) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (fb.getDocument().getLength() - length + text.length() <= MAX_CHARACTERS) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        
        JButton submitCommentButton = new JButton("등록");


        // 댓글 삭제 버튼
        JButton deleteCommentButton = new JButton("삭제");
        deleteCommentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = commentsTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(PostViewFrame.this, "삭제할 댓글을 선택하세요.");
                    return;
                }

                String userId = member.getUserId();
                List<Comment> comments = commentDAO.viewComment(post.getPostId());
                Comment selectedComment = comments.get(selectedRow);

                if (!userId.equals(selectedComment.getcUserId())) {
                    JOptionPane.showMessageDialog(PostViewFrame.this, "본인의 댓글만 삭제할 수 있습니다.");
                    return;
                }

                int result = JOptionPane.showConfirmDialog(PostViewFrame.this, "정말로 댓글을 삭제하시겠습니까?", "댓글 삭제", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    boolean success = commentDAO.deleteComment(selectedComment.getCommentId());
                    if (success) {
                        JOptionPane.showMessageDialog(PostViewFrame.this, "댓글이 삭제되었습니다.");
                        loadComments(tableModel);
                    } else {
                        JOptionPane.showMessageDialog(PostViewFrame.this, "댓글 삭제에 실패했습니다.");
                    }
                }
            }
        });

        commentField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (commentField.getText().equals("댓글은 100글자까지 입력 가능합니다.")) {
                    commentField.setText("");
                    commentField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (commentField.getText().isEmpty()) {
                    commentField.setForeground(Color.GRAY);
                    commentField.setText("댓글은 100글자까지 입력 가능합니다.");
                }
            }
        });


        submitCommentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String commentText = commentField.getText();
                if (!commentText.isEmpty() && !commentText.equals("댓글은 100글자까지 입력 가능합니다.")) {
                    Comment comment = new Comment();
                    comment.setcPostId(post.getPostId());
                    comment.setcUserId(member.getUserId());
                    comment.setComment(commentText);

                    boolean success = commentDAO.writeComment(comment);
                    if (success) {
                        JOptionPane.showMessageDialog(PostViewFrame.this, "댓글이 추가되었습니다.");
                        commentField.setText("");
                        loadComments(tableModel);
                    } else {
                        JOptionPane.showMessageDialog(PostViewFrame.this, "댓글 추가에 실패했습니다.");
                    }
                }
            }
        });
        // ESC 키 눌림 이벤트 처리
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        });
        this.setFocusable(true);  // 이 컴포넌트가 포커스를 받을 수 있도록 설정

        contentArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        });
        commentsTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                } else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                	deleteCommentButton.doClick();
                }
            }
        });
        
        // 댓글 입력 시에만 종료 문구 + ENTER 기능 추가
        commentField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    int result = JOptionPane.showConfirmDialog(PostViewFrame.this, 
                            "댓글 작성을 취소하고 창을 닫으시겠습니까?", 
                            "나가기", 
                            JOptionPane.YES_NO_OPTION);

                    if (result == JOptionPane.YES_OPTION) {
                        dispose();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    submitCommentButton.doClick();
                }
            }
        });


        commentPanel.add(commentField, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitCommentButton);
        buttonPanel.add(deleteCommentButton);
        commentPanel.add(buttonPanel, BorderLayout.EAST);
        mainPanel.add(commentPanel);

        setContentPane(mainPanel);
        pack();
        setVisible(true);
    }
    
    private void loadComments(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        List<Comment> comments = commentDAO.viewComment(post.getPostId());

        if (comments.isEmpty()) {
            System.out.println("해당 PID에 댓글이 없음 " + post.getPostId());
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (Comment comment : comments) {
            String userName = commentDAO.getUserNameById(comment.getcUserId());
            if (userName == null) {
                userName = "사용자 이름 불명";
            }
            String writeDate = dateFormat.format(comment.getWriteDate());
            tableModel.addRow(new Object[]{userName, comment.getComment(), writeDate});
        }
    }


}
