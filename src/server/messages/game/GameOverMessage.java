package server.messages.game;



import server.messages.Message;


public class GameOverMessage implements Message
{
	int score;
	
	public GameOverMessage(int s){
		score=s;
	}

	public int getScore()
	{
		return score;
	}
}
