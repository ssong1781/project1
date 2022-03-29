package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;


public class ServerTask implements Runnable {

	// 현재 연결된 client의 연결 정보
		Socket client;	
		// 현재 연결된 모든 사용자의 정보
		Hashtable<String,PrintWriter> ht;
		// display 정보가 있는 controller
		ServerController sc;
		
		PrintWriter pw;		// 연결된 client로 출력
		
		BufferedReader br;	// 연결된 client에서 메세지 받기
		
		String nickName;	// 현재 연결된 사용자의 nickName
		
		boolean isRun = true;// receive Thread flag
		
		static ArrayList<String> members = new ArrayList<>();
		
		static String keyword;
		
		static int count;
		
		
		public ServerTask(
				Socket client, 
				Hashtable<String,PrintWriter> ht,
				ServerController sc) {
			this.client = client;
			this.ht = ht;
			this.sc = sc;
			
			try {
				OutputStream os = client.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os);
				BufferedWriter bw = new BufferedWriter(osw);
				pw = new PrintWriter(bw,true);
				
				
				InputStream is = client.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				br = new BufferedReader(isr);
			
				
			} catch (IOException e) {
				sc.displayText("Client 연결 오류 : "+e.getMessage());
			}
			
		}

		// receive - 데이터 전달 받기
		@Override
		public void run() {
			System.out.println("Client의 receive 시작");
			while(isRun) {
				
				try {
					String receiveData = br.readLine();
					if(receiveData == null) {
						isRun = false;
					}
					System.out.println(receiveData);
					
					String[] data = receiveData.split("\\|");
					// 0|nickname     1|message
					//[0,nickname]   [1,message]
					String code = data[0];
					String text = data[1];
					if(data.length == 3) {
						String text2 = data[2];
						String xy = text+"|"+text2;
						if(code.equals("2")) {
							broadCast(2, xy);
						}else if(code.equals("3")) {
							broadCast(3, xy);
						}	
					}
					
					switch(code) {
						case "0" :
							nickName = text;
							members.add(text);
							ht.put(text, pw);
							PrintWriter pw = ht.get(members.get(0));
							String sendData = "";
							for(String s : ht.keySet()) {
								sendData += s+",";
							}
							// 0|닉네임,닉네임,닉네임,...
							broadCast(0,sendData);
							broadCast(1,text+"님이 입장하셨습니다. -방인원은 : "+ht.size());
							if(members.size() >= 2) {
								pw.println("8|"+"btnStart 활성화");
							}
							break;
						case "1" : 
							// 메세지 전달
							//1|닉네임 : 메세지
							// /w 문자열로 시작하면 true
							if(text.startsWith("/w")) {
								// /w 닉네임 메시지
								int begin = text.indexOf(" ")+1;
								// 두번째 매개변수 인덱스 위치 부터
								// 첫번째 매개변수 글자가 어디있는지 인덱스 번호 검색
								int end = text.indexOf(" ", begin);
								
								if(begin != -1 && end != -1) {
									String nick = text.substring(begin,end);
									String message = text.substring(end);
									pw = ht.get(nick);
									if(pw != null) {
										// 사용자 존재
										pw.println("1|"+this.nickName+"님의 귓속말 : "+message);
									}else {
										// 사용자가 없음.
										this.pw.println("1|"+nick+"사용자가 존재하지 않습니다.");
									}
								}
							
							}else if(text.equals(keyword)) {
								broadCast(10, this.nickName);
								this.pw.println("11|출제자");	
								broadCast(13, "stoptimer");
							}else {
								broadCast(1,nickName+" : "+text);
							}
							break;
						case "4" :
							broadCast(4,text);
							break;
						case "5" :
							broadCast(5,text);
							break;
						case "6" :
							broadCast(6,text);
							break;
						case "7" :
							if(count >= 5) {
								broadCast(14, "게임끝");
							}else {
								keyword = text;
								broadCast(9, "disable");
								this.pw.println("7|"+text);	
								broadCast(12, "timer");
							}
							count++;
							break;
						case "8" :
							count = 0;
							break;
						case "9" :
							int random = (int)(Math.random() * members.size());
							pw = ht.get(members.get(random));
							broadCast(13, "stoptimer");
							broadCast(9, "disable");
							pw.println("7|"+text);
							broadCast(12, "timer");
							count++;
							break;
					}
					
				} catch (Exception e) {
					System.out.println("client 연결 오류 "+e.getMessage());
					isRun = false;
				}
			} // end while
			
			if(client != null && !client.isClosed()) {
				try {
					client.close();
				} catch (IOException e) {}
			}
			
			ht.remove(nickName);
			String list ="";
			for(String s : ht.keySet()) {
				list+= s+",";
			}
			broadCast(0,list);		
			broadCast(1,nickName+"님이 나가셨습니다. 방인원 : "+ht.size());
			
		}// end Run
		
		// 연결된 모든 client에 메세지 전달
		public void broadCast(int code, String message) {
			for(PrintWriter p : ht.values()) {
				p.println(code+"|"+message);
			}
		}

}
