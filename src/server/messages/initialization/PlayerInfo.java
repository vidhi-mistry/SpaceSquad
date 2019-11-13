package server.messages.initialization;

import server.Player;
import server.messages.Message;


//Player File contains socket, i/o object stream which are not serializable 
//so we created this class of basic info to pass it over the object stream
public class PlayerInfo implements Message
{
	 private int playerNum;
	  private String avatarFileName;
	  
	  public PlayerInfo(Player p)
	  {
		  playerNum=p.getPlayerNum();
		  avatarFileName=p.getAvatarFileName();
	  }
	  
	  public PlayerInfo(int i)
	  {
		  playerNum=i;
				  avatarFileName="src/server/avatar"+playerNum+".png";
	  }
	  
	  public  int getPlayerNumber()
	  {
		  return playerNum;
	  }
	  
	  public String getAvatarFileName()
	  {
		  return avatarFileName;
	  }
	
}
