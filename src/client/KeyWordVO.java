package client;



public class KeyWordVO{

	
	int no;
	String KeyWord;
	
	public KeyWordVO() {
		super();
	}

	public KeyWordVO(String keyWord) {
		this.KeyWord = keyWord;
	}

	public KeyWordVO(int no, String keyWord) {
		super();
		this.no = no;
		this.KeyWord = keyWord;
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public String getKeyWord() {
		return KeyWord;
	}

	public void setKeyWord(String keyWord) {
		this.KeyWord = keyWord;
	}
	
	
	
	
	
}