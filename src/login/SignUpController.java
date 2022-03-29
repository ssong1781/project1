package login;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import login.util.DBHelper;

public class SignUpController implements Initializable {
	
	@FXML private TextField txtName, txtId;
	@FXML private PasswordField txtPw, txtPw2;
	@FXML private Button btnCheck, btnOk, btnCancel;
	
	Connection conn;
	PreparedStatement pstmt;
	ResultSet rs;
	Stage stage;
	Alert alert;
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		//중복확인 버튼
		btnCheck.setOnAction(e->idCheck(e));
		
		//중복확인 버튼 enter 누르면 클릭
		btnCheck.setOnKeyPressed(e->{
			if(e.getCode().equals(KeyCode.ENTER)) {
				btnCheck.fire();
			}
		});
		
		btnOk.setOnAction(e->signUpCheck(e));
		
		//취소 버튼 누르면 창 닫힘
		btnCancel.setOnAction(e->{
			Stage stage = (Stage) btnCancel.getScene().getWindow();
		    stage.close();
		});
		
		//비밀번호 확인 textfield에서 enter 누르면 회원가입 버튼 클릭
		txtPw2.setOnKeyPressed(e->{
			if(e.getCode().equals(KeyCode.ENTER)) {
				btnOk.fire();
			}
		});
		

	}
	
	

	//중복확인 버튼 누르면 실행
	//한글 특수 문자 공백 사용 x
	//이미 있는 아이디 x
	private void idCheck(ActionEvent e) {
		String id = txtId.getText();
		System.out.println(id);
		
		//정규식으로 한글, 특수문자, 공백있는지 확인
		for(int i =0;i<id.length();i++) {
			if (String.valueOf(id.charAt(i)).matches("[^a-zA-Z0-9]")) { //특수문자 인 경우
				alert = new Alert(AlertType.ERROR); 
				alert.setHeaderText("한글, 특수문자, 공백은 사용하실 수 없습니다.");
				alert.show();
				txtId.clear();
				txtId.requestFocus();  
				return;
			}
		}
		
		
		if(id.equals("")) {
			alert = new Alert(AlertType.WARNING); 
			alert.setHeaderText("아이디를 입력하세요.");
			alert.show();
			txtId.requestFocus();
		}else if(!selectMember(id)) {
			alert = new Alert(AlertType.ERROR); 
			alert.setHeaderText("이미 사용중인 아이디입니다.");
			alert.show();
			txtId.clear();
			txtId.requestFocus();
		}else {
			alert = new Alert(AlertType.CONFIRMATION); 
			alert.setHeaderText("사용가능한 아이디입니다.");
			alert.setContentText(id+"로 사용하시겠습니까?");
			Optional<ButtonType> result = alert.showAndWait();
			//ok버튼 누르면 아이디 입력 textfield랑 중복 확인 버튼 비활성화
			if(result.get() == ButtonType.OK) {
				txtId.setDisable(true);
				btnCheck.setDisable(true);
				txtPw.requestFocus();
			}else if(result.get() == ButtonType.CANCEL) {
				txtId.clear();
				txtId.requestFocus();
			}
		}
	}
	
	
	//회원가입 버튼 눌렀을때 실행
	//빈칸이 있는지 확인 후 있으면 그 textfield로 포커스
	//비밀번호, 비밀번호 확인이 같은지 확인 후 다르면 두개 다 지우고 다시 포커스
	//중복확인 버튼 비활성화를 통해서 중복확인 버튼 눌렀는지 확인
	private void signUpCheck(ActionEvent e) {
		
		if(txtName.getText().trim().equals("")) {
			alert = new Alert(AlertType.WARNING); 
			alert.setHeaderText("이름을 입력하세요.");
			alert.show();
			txtName.requestFocus();
			return;
		}else if(txtId.getText().trim().equals("")) {
			alert = new Alert(AlertType.WARNING); 
			alert.setHeaderText("아이디를 입력하세요.");
			alert.show();
			txtId.requestFocus();
			return;
		}else if(txtPw.getText().trim().equals("")) {
			alert = new Alert(AlertType.WARNING); 
			alert.setHeaderText("비밀번호를 입력하세요.");
			alert.show();
			txtPw.requestFocus();
			return;
		}else if(txtPw2.getText().trim().equals("")) {
			alert = new Alert(AlertType.WARNING); 
			alert.setHeaderText("비밀번호를 입력하세요.");
			alert.show();
			txtPw2.requestFocus();
			return;
		}
		
		if(!(txtPw.getText().equals(txtPw2.getText()))) {
			alert = new Alert(AlertType.WARNING); 
			alert.setHeaderText("비밀번호가 일치하지 않습니다.");
			alert.show();
			txtPw.clear();
			txtPw2.clear();
			txtPw.requestFocus();
			return;
		}
		
		if(!(txtId.isDisabled())) {
			alert = new Alert(AlertType.WARNING); 
			alert.setHeaderText("아이디 중복확인을 해주세요.");
			alert.show();
			return;
		}
		
		//db연결
		conn = DBHelper.getConnection();
		
		String name = txtName.getText().trim();
		String id = txtId.getText();
		String pw = txtPw.getText().trim();
		
	    String sql = "INSERT INTO di_tb(name,id,pw) VALUES(?,?,?)";
	    try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, name);
			pstmt.setString(2, id);
			pstmt.setString(3, pw);
			pstmt.executeUpdate();

		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			DBHelper.close(pstmt);
		}
		
		alert = new Alert(AlertType.INFORMATION);
		alert.setHeaderText("회원가입 성공");
		alert.showAndWait();
		
		//회원가입 완료 후 창 닫기
		Stage stage = (Stage) btnOk.getScene().getWindow();
	    stage.close();
		
	}
	
	

	//아이디 입력 textfield에 있는 아이디가
	//db에 이미 있는지 확인하는 메소드
	public boolean selectMember(String mId) {
		boolean isChecked = true;
		
		conn = DBHelper.getConnection();
		try {
			pstmt = conn.prepareStatement(
					"SELECT * FROM di_tb WHERE Id = ?"
					);
			pstmt.setString(1, mId);
			rs = pstmt.executeQuery();
			if(rs.next()) isChecked = false;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			DBHelper.close(rs,pstmt);
		}
		
		return isChecked;
	}

}
