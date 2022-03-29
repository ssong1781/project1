package client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import login.util.DBHelper;


public class DAO {
	
	public DAO() {
	}
	
	public ArrayList<KeyWordVO> getKeyWord(){
		String sql = "SELECT * FROM keyword";
		ArrayList<KeyWordVO> list = new ArrayList<KeyWordVO>();
		Connection con = null;
		PreparedStatement pstmt = null;
		
		ResultSet rs = null;
		KeyWordVO KeyWordVO = null;
		
		try {
			con = DBHelper.getConnection();
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				KeyWordVO = new KeyWordVO(rs.getString(1));
				list.add(KeyWordVO);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("sql오류");
		} catch (Exception ee) {
			System.out.println("가져오기 오류");
		} finally {
			DBHelper.close(rs,pstmt);
		}
		
		
		return list;
		
	}
}