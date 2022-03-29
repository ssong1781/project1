package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class ServerController implements Initializable {
	@FXML private TextArea displayText;
	@FXML private Button btnStartStop;
	
	// Client Thread를 관리할 스레드 풀
	ExecutorService serverPool;
	// 운영체제에서 포트를 할당 받아 
	// client socket 연결 관리를 할 ServerSocket 
	ServerSocket server;
	
	// Client 사용자 정보를 저장할 map
	// <사용자닉네임 , Client Socket 출력 스트림>
	Hashtable<String,PrintWriter> ht;
	
	// 사용할 포트 번호
	public final int SERVER_PORT = 5001;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		btnStartStop.setOnAction(e->{
			if(btnStartStop.getText().equals("START")) {
				// 서버 시작
				startServer();
			}else {
				// 서버 종료
				stopServer();
			}
		});
		
		
	}
	
	
	
	// 서버 시작
	public void startServer() {
		serverPool = Executors.newFixedThreadPool(50);
		ht = new Hashtable<>();
		
		try {
			server = new ServerSocket(SERVER_PORT);
		} catch (IOException e) {
			displayText("서버 연결 오류 "+e.getMessage());
			stopServer();
			return;
		}
		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Platform.runLater(()->{
					btnStartStop.setText("STOP");
				});
				displayText("[서버 시작]");
				while(true){
					try {
						// Client 연결 대기 
						Socket client = server.accept();
						String address = client.getRemoteSocketAddress().toString();
						String message = "[연결 수락 : "+address+" ]";
						serverPool.submit(new ServerTask(client,ht,ServerController.this));
						displayText(message);
					} catch (IOException e) {
						stopServer();
						break;
					}
				}
			}
		};
		serverPool=Executors.newCachedThreadPool();
		serverPool.submit(runnable);
	}
	// 서버 종료
	public void stopServer() {
		
		try {
			if(ht != null) {
				for(PrintWriter p : ht.values()) {
					if(p != null) {
						p.close();
					}
				}
			}
			
			
			if(server != null && !server.isClosed()) {
				server.close();
			}
			
			if(serverPool != null && !serverPool.isShutdown()) {
				serverPool.shutdownNow();
			}
			displayText("[ 서버 중지 ]");
			btnStartStop.setText("START");
		} catch (IOException e) {}
		
		
	}
	
	public void displayText(String text) {
		Platform.runLater(()->{
			displayText.appendText(text+"\n");
		});
	}

}
