package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Random;

import server.messages.Message;
import server.messages.initialization.IsWaiting;


public class Player 
{	private Socket socket;
	  private ObjectOutputStream out;
	  private ObjectInputStream in;
	  private int playerNum;
	  private String avatarFileName;

	 
	  
	  public Player(Socket socket, ObjectOutputStream out, ObjectInputStream in, int playerNum) {
		   
		    this.socket = socket;
		    this.out = out;
		    this.in = in;
		    this.playerNum = playerNum;
		    
		    Random r=new Random();
		    String num=Integer.toString(r.nextInt(6)); //generate num between 0 to 5
		    
				avatarFileName = "src/server/avatar"+num+".png";
			
		  }

		  /**
		   * Sends an object that implements Message.
		   * @param message the Message object to be sent
		   */
		  public synchronized void sendMessage(Message message) {
		    try {
		      if(!socket.isClosed()) {
		    	  out.reset();
		        out.writeObject(message);
		        out.flush();
		       
		      }
		    }
		    catch(IOException e) {
		      e.printStackTrace();
		    }
		  }

	  
	  	public String getAvatarFileName() {
		    return avatarFileName;
		  }

		
		  public Socket getSocket() {
		    return socket;
		  }

		  public ObjectOutputStream getOut() {
		    return out;
		  }

		  public ObjectInputStream getIn() {
		    return in;
		  }

		  public int getPlayerNum() {
		    return playerNum;
		  }

		  /**
		   * The method closes the input and output streams and the connection socket.
		   * Used after the game has finished to close open connections.
		   */
		  public void terminate() {
		    try {
		      if(!socket.isClosed()) {
		        socket.close();
		      }
		    }
		    catch(IOException e) {
		      e.printStackTrace();
		    }
		  }
	
}
