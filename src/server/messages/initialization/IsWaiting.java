package server.messages.initialization;

import java.util.List;

import server.messages.Message;

public class IsWaiting implements Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4047345037937961648L;
	public List<PlayerInfo> playerList;
	
	
	public void setPlayerList(List<PlayerInfo> p)
	{
		playerList=p;
		
	}
	
	public List<PlayerInfo> getPlayerList()
	{
		 		return playerList;
	
	}

	
}
