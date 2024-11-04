package gui;

import javax.swing.*;
import java.awt.*;

public class BoardFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    public BoardFrame(String userName) {
        setTitle("게시판");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 상단 패널
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        // 로고 이미지 설정
        ImageIcon logoIcon = new ImageIcon("../../img/logo.png");
        Image img = logoIcon.getImage();
        Image newImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(newImg));

        // 제목 레이블
        JLabel titleLabel = new JLabel("게시판", SwingConstants.CENTER);
        titleLabel.setFont(new Font("굴림", Font.BOLD, 24));

        // 인사말 레이블
        JLabel greetingLabel = new JLabel(userName + "님", SwingConstants.RIGHT);
        greetingLabel.setFont(new Font("굴림", Font.BOLD, 16));

        // 상단 패널 구성
        topPanel.add(logoLabel, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(greetingLabel, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // 게시글 목록 패널
        JPanel postPanel = new JPanel();
        postPanel.setLayout(new GridLayout(0, 1));
        postPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // 게시글 더미 데이터
        for (int i = 1; i <= 5; i++) {
            postPanel.add(new JLabel("게시글 " + i + " [P_USER_ID] [POST_TITLE] [CREATE_DATE]"));
        }

        JScrollPane scrollPane = new JScrollPane(postPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // 버튼 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton createPostButton = new JButton("게시글 작성");
        JButton updatePostButton = new JButton("게시글 수정");
        JButton deletePostButton = new JButton("게시글 삭제");

        buttonPanel.add(createPostButton);
        buttonPanel.add(updatePostButton);
        buttonPanel.add(deletePostButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new BoardFrame("User Name");
    }
}