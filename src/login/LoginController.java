package login;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import client.ClientController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import login.util.DBHelper;

public class LoginController implements Initializable {
	
	
	
	@FXML private TextField txtId, txtPw;
	@FXML private Button btnLogin, btnSignUp, btnCancel;
	
	Connection conn;
	PreparedStatement pstmt;
	ResultSet rs;
	Stage stage;
	
	
	private static String nickName;
	
	


	public String getNickName() {
		return nickName;
	}




	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		btnLogin.setOnAction(e->loginAction(e));
		btnSignUp.setOnAction(e->signUpAction(e));
		
		//취소 버튼 누르면 창 닫기
		btnCancel.setOnAction(e->{
			Stage stage = (Stage) btnCancel.getScene().getWindow();
		    stage.close();
		});
		
		//비밀번호 textfield에서 enter 누르면 로그인 버튼 클릭
		txtPw.setOnKeyPressed(e->{
			if(e.getCode().equals(KeyCode.ENTER)) {
				btnLogin.fire();
			}
		});

	}

	//로그인 버튼 누르면 실행
	//먼저 빈칸이 있는지 확인 후
	//아이디 비밀번호가 db에 있는 data와 일치하는지 확인하고
	//있으면 로그인 성공
	//없으면 로그인 실패 textfield 다 지우고 포커스
	private void loginAction(ActionEvent event) {
		
		if(txtId.getText().trim().equals("")) {
			Alert alert = new Alert(AlertType.WARNING); 
			alert.setHeaderText("아이디를 작성해주세요.");
			alert.show();
			txtId.requestFocus();
			return;
		}else if(txtPw.getText().trim().equals("")) {
			Alert alert = new Alert(AlertType.WARNING); 
			alert.setHeaderText("비밀번호를 작성해주세요.");
			alert.show();
			txtPw.requestFocus();
			return;
		}
		conn = DBHelper.getConnection();
		String sql = "select name,id,pw from di_tb where id = ? and pw = ?";
		String id = txtId.getText();
		String pw = txtPw.getText();
		 try {
			 pstmt = conn.prepareStatement(sql);
			 pstmt.setString(1, id);
			 pstmt.setString(2, pw);
			 rs = pstmt.executeQuery(); 
			 if(rs.next()){			     
			     if(rs.getString("id").equals(id) && rs.getString("pw").equals(pw)){
			    	System.out.println("로그인 성공");
			    	String nick = rs.getString("name");
			    	nickName = nick;
			    	Alert alert = new Alert(AlertType.INFORMATION); 
					alert.setTitle("로그인 성공");
					alert.setHeaderText(rs.getString("name")+"님이 로그인 하셨습니다.");
					alert.showAndWait();		
					Stage st = (Stage) btnLogin.getScene().getWindow();
					Platform.runLater(() -> {
						st.close();
					});
									
					try {
						stage = new Stage();
						stage.setTitle("DRAW IMAGINE");	
						FXMLLoader loader = new FXMLLoader(getClass().getResource("../client/Client.fxml"));
						Parent root = loader.load();
						ClientController con = loader.getController();
						stage.setScene(new Scene(root));
						stage.setResizable(false);
						stage.setOnCloseRequest(new EventHandler<WindowEvent>() {	
							@Override
							public void handle(WindowEvent event) {
								con.stopClient();	
							}
						});
						stage.show();
					}catch (IOException e1) {}
			     }
			 }else {
				 Alert alert = new Alert(AlertType.WARNING); 
				 alert.setHeaderText("로그인 실패");
				 alert.setContentText("아이디 또는 비밀번호가 일치하지 않습니다.");
				 alert.show();
				 txtId.clear();
				 txtPw.clear();
				 txtId.requestFocus();
			 }
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBHelper.close(rs,pstmt);
		}
		
	}
	

	


	//회원가입 버튼 누르면 회원가입 창 열기
	private void signUpAction(ActionEvent event) {
		stage = new Stage();
		stage.setTitle("회원가입");
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(btnSignUp.getScene().getWindow());
		
		try {
			Parent root = FXMLLoader.load(getClass().getResource("SignUp.fxml"));
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
		}catch (IOException e1) {}
	}
	

}
