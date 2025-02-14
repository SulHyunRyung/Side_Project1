package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserExists {

    // USER_ID가 중복 여부 체크
    public boolean isUserIdExists(String userId) {
        String sql = "SELECT COUNT(*) FROM MEMBER_LIST WHERE USER_ID = ?";
        try (Connection conn = DBconn.connectDB(); // DBconn을 통해 DB 연결
        	     PreparedStatement pstmt = conn.prepareStatement(sql)) {

        	    pstmt.setString(1, userId);
        	    
        	    try (ResultSet rs = pstmt.executeQuery()) {
        	        return rs.next() && rs.getInt(1) > 0; 
        	    }
        	} catch (SQLException e) {
        	    e.printStackTrace();
        	}
        return false;
    }

    // USER_EMAIL 중복 여부 체크
    public boolean isUserEmailExists(String userEmail) {
        String sql = "SELECT COUNT(*) FROM MEMBER_LIST WHERE USER_EMAIL = ?";
        try (Connection conn = DBconn.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, userEmail);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
