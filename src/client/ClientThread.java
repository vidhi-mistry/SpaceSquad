package client;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import controls.PushButtonWidget;

import controls.Widget;
import gui.SpaceSquad;
import server.GameThread;
import server.messages.Message;
import server.messages.game.Command;
import server.messages.game.GameOverMessage;
import server.messages.game.HealthMessage;

import server.messages.game.LevelStart;
import server.messages.game.PlayerLeft;
import server.messages.initialization.AcceptedPlayer;
import server.messages.initialization.GameReady;
import server.messages.initialization.GameStarted;
import server.messages.initialization.IsWaiting;
import server.messages.initialization.PlayerInfo;
import server.messages.initialization.SetWaitingRoom;




public class ClientThread extends Thread {
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	public PlayerInfo thisPlayerInfo;
	
	private Timer tt;
	private int timeLimit;
	private List<Widget> widgetList;

	private SpaceSquad parent;

	/**
	 * Constructor for ClientThread
	 */
	public ClientThread(SpaceSquad sp, String hostname, int port) {
		parent = sp;
		
		try {
			socket = new Socket(hostname, port);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public int getPlayerNum()
	{
		return thisPlayerInfo.getPlayerNumber();
	}
	
	/*
	  Passes the new widgets to the GUI method
	  Stores the new time limit for a command and the list of widgets.
	 */
	public void createLevel(LevelStart start) {
		timeLimit = start.getSeconds();
		widgetList = start.getWidgetList();
		
	    	
	    	
			parent.createLevel(widgetList.subList((thisPlayerInfo.getPlayerNumber()-1)*6, thisPlayerInfo.getPlayerNumber()*GameThread.DASH_PIECES_PER_PLAYER), true);
			
	
	}
	

	
	/*
	  Calls the GUI method to display the command to the user and 
	  creates a Timer thread.
	 */
	public void setCommand(Command c) {
		if(tt != null) {
			tt.turnOff();	
		}
		String s;
		s = widgetList.get(c.getWidgetId()).getVerb() + " " + widgetList.get(c.getWidgetId()).getName();
		
		parent.displayCommand(s);
		tt = new Timer(this, timeLimit);
		tt.start();
	}
	
	/*
	  Creates a command object and sends the command to the server for evaluation.
	 */
	public void piecePressed(int widgetId, int value) {
		try {
			Command c = new Command(widgetId, value);
			oos.writeObject(c);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	  Calls the GUI method to update the health display to the user.
	 */
	public void updateHealth(HealthMessage health) {
		parent.updateHealth(health.getCurrHealth());
	}
	
	
	public void updateTime(int current, int total) {
		parent.updateTime(current, total);
	}

	/*
	  Calls the GUI method to end the game 
	 */
	public void endGame(GameOverMessage over) {
		parent.endGame(over);
	}
	
	
	/*
	  Tells the GUI that the player has joined successfully.
	 */
	public void acceptedPlayer() {
		parent.acceptedPlayer();
	}
	
		
	/**
	 * Tells the GUI that the game has started.
	 */
	private void gameStarted() {
		parent.gameStarted();
	}
	
	private void gameReady() {
		parent.gameReady();
		
	}
	
	
	
	private void isWaiting(IsWaiting w)
	{
		parent.isWaiting(w);

     
	}
	private void setWaitingRoom()
	{
		parent.setWaitingRoom();

     
	}
	
	public void sendMessage(Message message)
	{
		System.out.println("Sendin Message  of -->"+ message.getClass());

		try {
			if(!socket.isClosed())
			oos.reset();
			oos.writeObject(message);
			oos.flush();
			
			System.out.println("Message Sent");

		} catch (IOException e) {    

			e.printStackTrace();
		}	
	}
	public void run() {
		try {
			while (true) {
				//sendMessage(null);
				Object obj = ois.readObject();
				if(obj instanceof PlayerInfo)
				{
					thisPlayerInfo=(PlayerInfo)obj;
					System.out.println("Recieved playerinfo");

				}
				else if (obj instanceof AcceptedPlayer) 
				{
					acceptedPlayer();
					System.out.println("Recieved AcceptedPlayer");

				} 
				else if (obj instanceof GameStarted) {
					System.out.println("Recieved game Started");

					gameStarted();
					
				} 
				
				else if (obj instanceof GameReady) {
					gameReady();
					System.out.println("Recieved gameready");

					
				} 
				
				else if(obj instanceof IsWaiting)
				{
					IsWaiting w=(IsWaiting)obj;
					isWaiting(w);
					System.out.println("Recieved isWaiting");

				}
				
				else if(obj instanceof SetWaitingRoom)
				{
					setWaitingRoom();
					System.out.println("Recieved setWaitingRoom");

				}
				

				if (obj instanceof Command) {
					System.out.println("Recieved Command");
					setCommand((Command) obj);
				} else if (obj instanceof LevelStart) {
					System.out.println("Recieved LevelStart");
					createLevel((LevelStart) obj);
				} else if (obj instanceof HealthMessage) {
					System.out.println("Recieved HealthMsg");
					updateHealth((HealthMessage) obj);
				} else if (obj instanceof GameOverMessage) {
					System.out.println("Recieved GameOver");
					endGame((GameOverMessage) obj);
				
				} 
				else if (obj instanceof PlayerLeft)
				{
					//playerLeft();
					break;
				}

			}
			
			if (!socket.isClosed()) {
				socket.close();
			}
		} catch (IOException | ClassNotFoundException e) {
			if(e instanceof EOFException)
			{
				System.out.println("End..");
			}
			else
			{
			e.printStackTrace();
			System.out.println("\n\nError reading object from server. Exiting now.");
			}
			}
		
	}
	
	public Socket getSocket()
	{
		return socket;
		}


}

