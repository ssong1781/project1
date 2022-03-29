package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import login.LoginController;

public class ClientController implements Initializable {

	@FXML private TextArea txtDisplay;
	@FXML private TextField txtInput;
	@FXML private ListView<String> listView;
	@FXML private Button btnSend, btnStart, btnClear, btnBlack, btnRed, btnBlue, btnGreen, btnYellow;
	@FXML private Canvas canvas;
	@FXML private Label lblAnswer, lblWord, lblTime;
	@FXML private Slider slider;
	@FXML private ProgressBar progressbar;
	@FXML private ImageView imageMusic;
	
	
	GraphicsContext gc;
	
	// 연결된 서버 소켓 정보
	Socket server;
	// 연결 요청을 보낼 server ip 주소
	InetAddress ip;
	// 사용자 닉네임
	String nickName;
	
	// 서버로 데이터 출력
	PrintWriter pw;

	
	// 서버에서 데이터를 읽음
	BufferedReader br;
	
	LoginController lc;
	
	DAO dao;
	
	Timer timer;
	
	TimerTask task;
	
	int count;
	
	int imageCount = 0;
	
	
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		
		
		startClient();
		
	
		Media media = new Media(getClass().getResource("audio.mp3").toString());
		MediaPlayer mp = new MediaPlayer(media);
		mp.play();	
		
		
				
		gc = canvas.getGraphicsContext2D();
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(5);
		slider.setMin(1);			//슬라이더 최소값 지정
		slider.setMax(100);			//슬라이더 최대값 지정
		slider.setValue(50);
		
		
		canvas.setOnMousePressed(event->{
			double x = event.getX();
			double y = event.getY();
			String pressed = x+"|"+y;
			send(2, pressed);
			
		});
		canvas.setOnMouseDragged(event->{
			double x = event.getX();
			double y = event.getY();
			String dragged = x+"|"+y;
			send(3, dragged);
		});
		
		btnBlack.setOnAction(event->{
			send(4,"0x000000ff");
		});
		btnRed.setOnAction(event->{
			send(4,"0xff0000ff");
		});
		btnBlue.setOnAction(event->{
			send(4,"0x0000ffff");
		});
		btnGreen.setOnAction(event->{
			send(4,"0x00ff00ff");
		});
		btnYellow.setOnAction(event->{
			send(4,"0xfffd00ff");
		});
		
		imageMusic.setOnMouseClicked(event->{
			if(imageCount == 0) {
				String path = getClass().getResource("../img/bgmOFF.png").toString();
				Image image = new Image(path);
				imageMusic.setImage(image);
				mp.pause();
				imageCount = 1;
			}else if(imageCount == 1) {
				String path = getClass().getResource("../img/bgmON.png").toString();
				Image image = new Image(path);
				imageMusic.setImage(image);
				mp.play();
				imageCount = 0;
			}
		});
			
		
		
		
		slider.valueProperty().addListener((ob,oldValue,newValue)->{
			int value = newValue.intValue();
			double result = value/10.0;
			String dr = result+"";
			send(5, dr);
			
		});

		
		
		btnClear.setOnAction(event->{
			send(6,"지우기");
		});
		
		
		
		// send event
		btnSend.setOnAction(event->{
			
			String text = txtInput.getText().trim();
			if(text.equals("")) {
				displayText("메세지를 작성해 주세요");
				return;
			}
			send(1,text);
		}); 
		

		txtInput.setOnKeyPressed(e->{
			if(e.getCode().equals(KeyCode.ENTER)) {
				btnSend.fire();
			}
		});
		
		
		
		listView.setOnMouseClicked(event->{
			if(event.getClickCount() == 2) {
				String nickName 
				= listView.getSelectionModel().getSelectedItem();
				System.out.println(nickName);
				if(nickName == null) {
					displayText("먼저 닉네임을 선택해주세요.");
					return;
				}
				
				if(nickName.equals(this.nickName)) {
					displayText("자신은 선택이 안됩니다.");
					return;
				}
				
				txtInput.clear();
				txtInput.setText("/w "+nickName+" ");
				txtInput.requestFocus();
			}
		});
		

		
		btnStart.setOnAction(event->{
			send(8, "count");
			startGame();		
		});
			
	}
	
	public void startGame() {	
		send(7,startKeyword());		
	}
	
	public void starter() {
		btnSend.setDisable(true);
		txtInput.setDisable(true);
		canvas.setDisable(false);
		btnClear.setDisable(false);
		btnBlack.setDisable(false);
		btnRed.setDisable(false);
		btnBlue.setDisable(false);
		btnGreen.setDisable(false);
		btnYellow.setDisable(false);
		slider.setDisable(false);
	}
	
	
	


	public void disableAll() {
		btnClear.fire();
		btnSend.setDisable(false);
		txtInput.setDisable(false);
		btnStart.setDisable(true);
		canvas.setDisable(true);
		btnClear.setDisable(true);
		btnBlack.setDisable(true);
		btnRed.setDisable(true);
		btnBlue.setDisable(true);
		btnGreen.setDisable(true);
		btnYellow.setDisable(true);
		slider.setDisable(true);
		Platform.runLater(()->{
			lblWord.setText("? ? ?");
		});
	}


	// client 시작
	public void startClient() {
		try {
			server = new Socket("192.168.1.30",5001);
			
			displayText("[연결완료 : "+server.getRemoteSocketAddress()+"]");
			
			pw = new PrintWriter(
					new BufferedWriter(
							new OutputStreamWriter(
									server.getOutputStream()
									)
							),true);
			
			br = new BufferedReader(
					new InputStreamReader(
						server.getInputStream()
					)
				);
	
			send(0,nick());
		} catch (IOException e) {
			displayText("[서버 연결 안됨 IP를 확인해보세요.-"+ip+"]");
			stopClient();
			return;
		}
		receive();
	}
	
	public String nick() {
		lc = new LoginController();
	
		String nick = lc.getNickName();
		nickName = nick;
		System.out.println(nick);
		return nick;
	}
	
	
	public void timerStart() {
		count = 120;
		
		timer=new Timer();
		//setProgress(progress);
		
		task=new TimerTask(){
			@Override
			public void run() {
				
			
				if(count > 0){ //count값이 0보다 작을 때 까지 수행
					Platform.runLater(()->{
						lblTime.setText(count+"초");
					});								
					count--; // 
			
					double now = ((120-count)/120.0);   
					progressbar.setProgress(now);
				}
				else{
					send(9,startKeyword());
				}
			}	
		};
		timer.schedule(task, 1000, 1000); //실행 Task, 1초뒤 실행, 1초마다 반복
	}
	
	public void timerStop() {
		timer.cancel();
	}
	
	
	
	public String startKeyword() {
		dao = new DAO();
		ArrayList<KeyWordVO> list = dao.getKeyWord();
		int random = (int)(Math.random() * list.size());
		String keyword = list.get(random).getKeyWord();
		
		return keyword;
	}
	
	public void gameReset() {
		btnStart.setDisable(false);
		btnSend.setDisable(false);
		txtInput.setDisable(false);
		canvas.setDisable(false);
		btnClear.setDisable(false);
		btnBlack.setDisable(false);
		btnRed.setDisable(false);
		btnBlue.setDisable(false);
		btnGreen.setDisable(false);
		btnYellow.setDisable(false);
		slider.setDisable(false);
		Platform.runLater(()->{
			lblWord.setText("? ? ?");
		});
		slider.setValue(50);
		btnClear.fire();
		timerStop();
		Platform.runLater(()->{
			Alert alert = new Alert(AlertType.INFORMATION); 
			alert.setTitle("종료");
			alert.setHeaderText("종료");
			alert.setContentText("재게임을 원하시면 게임시작 버튼을 눌러주세요");
			alert.show();		
		});
	}
	


	// client 종료
	public void stopClient() {
		try {
			displayText("[Server와 연결 종료]");
			if(server != null && !server.isClosed()) {
				server.close();
			}
		} catch (IOException e) {}
	}
	
	
	// 서버에 정보를 전달
	// num == 0 : 닉네임 전달
	// num == 1 : 메세지 전달
	public void send(int num,String text) {
		// 0|닉네임
		// 1|메세지
		System.out.println("서버로 보내기"+num+"."+text);
		pw.println(num+"|"+text);
		if(num == 1) {
			displayText("[보내기 완료]"+text);
		}
		Platform.runLater(()->{
			txtInput.clear();
			txtInput.requestFocus();
		});	
	}
	
	

	// 서버에서 메세지를 전달 받음
	public void receive() {
		new Thread(()->{
			while(true) {
				
				try {
					String receiveData = br.readLine();
					// 0|사용자목록
					// 1|nickName+message
					String[] data = receiveData.split("\\|");
					// 1|message
					// 0|nickname,nickname,nickname,nickname,
					String code = data[0];
					String text = data[1];
					System.out.println(receiveData);
					if(data.length == 3) {
						String text2 = data[2];
						double x = Double.parseDouble(text);
						double y = Double.parseDouble(text2);
						if(code.equals("2")) {
							gc.beginPath();	//선그리기 시작
							gc.lineTo(x,y);
						}else if(code.equals("3")) {
							gc.lineTo(x, y);
							gc.stroke();
						}
					}
					if(code.equals("0")) {
						// listView 사용자 목록 갱신
						String[] list = text.split("\\,");
						System.out.println(text);
						
						Platform.runLater(()->{
							listView.setItems(
								FXCollections.observableArrayList(
									// 매개변수로 전달 받은 배열을
									// List type으로 변환
									Arrays.asList(list)
								)
							);

						});
						
					}else if(code.equals("1")) {
						// message 출력
						displayText(text);			
					}else if(code.equals("4")) {
						//colorbtn
						Color color = Color.web(text);
						gc.setStroke(color);					
					}else if(code.equals("5")) {
						//선 굵기
						double dText = Double.parseDouble(text);
						gc.setLineWidth(dText);
					}else if(code.equals("6")) {
						//canvas clear
						Platform.runLater(()->{
							gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
						});					
					}else if(code.equals("7")) {	
						//제시어 시작버튼 누른 사람 or 문제 맞춘 사람한테만 날라옴
						Platform.runLater(()->{
							lblWord.setText(text);
						});							
						starter();
					}else if(code.equals("8")) {
						//2명이상일때 첫번째로 온 사람 btnStart 활성화
						btnStart.setDisable(false);
					}else if(code.equals("9")) {
						disableAll();
					}else if(code.equals("10")) {
						Platform.runLater(()->{
							Alert alert = new Alert(AlertType.INFORMATION); 
							alert.setTitle("정답");
							alert.setHeaderText(text+"님 정답!");
							alert.show();		
						});
					}else if(code.equals("11")) {
						startGame();
					}else if(code.equals("12")) {
						timerStart();
					}else if(code.equals("13")) {
						timerStop();
					}else if(code.equals("14")) {
						gameReset();
					}
				} catch (IOException e) {
					stopClient();
					break;
				}
			}
		}).start();
	}
	
	// textArea에 text 추가
	public void displayText(String text) {
		Platform.runLater(()->{
			txtDisplay.appendText(text+"\n");
		});
	}
}