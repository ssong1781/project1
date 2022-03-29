package client;

public class MemberScoreVO {

	private String mNick;
	private int score;
	
	public MemberScoreVO(String mNick, int score) {
		this.mNick = mNick;
		this.score = score;
	}

	public MemberScoreVO() {
	}

	

	public String getmNick() {
		return mNick;
	}

	public void setmNick(String mNick) {
		this.mNick = mNick;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	
	@Override
	public String toString() {
		return "MemberScore [nickName=" + mNick + ", score=" + score + "]";
	}
	
	
	
}
