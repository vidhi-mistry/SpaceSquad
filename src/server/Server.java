package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.List;

import client.ClientThread;
import server.messages.Message;
import server.messages.initialization.AcceptedPlayer;
import server.messages.initialization.GameReady;
import server.messages.initialization.GameStarted;
import server.messages.initialization.IAmReady;
import server.messages.initialization.IsWaiting;
import server.messages.initialization.PlayerInfo;
import server.messages.initialization.SetWaitingRoom;

public class Server implements Runnable
{
  public static final int SERVER_PORT1 = 8888;

  private int port;
  
  
  private ServerSocket serverSocket;
  private List<Player> playerList;
  private List<PlayerInfo> playerInfoList;
  private GameThread game1;
  Thread serverThread;
  IsWaiting  waiting=new IsWaiting();
  int i=1;
  static int start=0;
  boolean flagGameReady=false;

  /**
   * Instantiates Server object, which waits for players to join and initializes the game threads.
   * @param port the port number to start the server on
   */
  public Server(int port) {
    try {
      this.port = port;
      playerList = new ArrayList<>();
      playerInfoList=new ArrayList<>();
    
      serverSocket = new ServerSocket(port);
      serverThread =new Thread(this);
      serverThread.start();
      System.out.printf("Connect clients to: %s\n", InetAddress.getLocalHost().getHostAddress());
      waitForPlayers();
     System.out.println("Servr threaad is alive ="+serverThread.isAlive());
      initializeGame();
    }
    catch(IOException e) {
      e.printStackTrace();
    }
  }

/**
   * Waits for  players to join the game. */
  
  private void waitForPlayers() {
      do {
    	if(i<=6 && !flagGameReady){
    		try {
       
		    	Socket socket = serverSocket.accept();
		    	if(!flagGameReady){
		    	ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
		
		        Player p = new Player(socket, out, in, i); // stores all details abt players
		        p.sendMessage(new AcceptedPlayer());//sends the message to the newly created player
		        PlayerInfo pInfo=new PlayerInfo(p);// store only basic info as socket & others arent serializable
		        out.reset(); 							 //flushes any old values -old copies
		        p.sendMessage(pInfo); 						//so that each client has its own info
		        //out.writeObject(pInfo);
		        playerList.add(p);
		        playerInfoList.add(pInfo);  
		        waiting.setPlayerList(playerInfoList);
		 
		        sendMessageAll(waiting);            //sends the message to all players
		        out.reset();
		        p.sendMessage(new SetWaitingRoom());
		        
			    
			   i++;
      }   
    }
      catch(IOException e) {
        e.printStackTrace();
      }
    }
    }while(serverSocket!=null && i<=7 && !flagGameReady);
     
      System.out.println("out of waiting");
  
      
  }

  /**
   * Initializes the game threads 
   
   */
  
  private void sendMessageAll(Message message)
  {
	  for(Player p:playerList)
	  {  
		 p.sendMessage(message);
	  }
	  System.out.println("sent to All"+message);
  }
  
  
  private void initializeGame() {
	  System.out.println("Game Initialized");
      sendMessageAll(new GameStarted());
	  game1 = new GameThread(playerList);
	  game1.start();
  
	  
  }

 
  public static void main(String[] args) {
    Server s = new Server(SERVER_PORT1);
   
    
  }

@Override
public void run() {
	System.out.println("In Run");
	Object obj =new Object();
int i=0;
	try {
		while (flagGameReady!=true) {
			System.out.print("");
			for( i=0;i<playerList.size();i++)
			{	
				System.out.println("Checking Packet Recieved from Player "+i);
				
				
				obj = playerList.get(i).getIn().readObject();
				
				if(obj instanceof IAmReady)
				{
			    int p=((IAmReady) obj).getPlayer();
				System.out.println("Recieved Ready");
				weAreReady(p);
				}
				
			
		
		}
			}
		System.out.println(i);

	}
	 catch (Exception e) {
		e.printStackTrace();
		System.out.println("\n\nError reading object from client. Exiting now.");
	}
	System.out.println("quitting Run");

}


/* TODO   
 * */

private void weAreReady(int p){
	System.out.println("in WeAreReady");

		start++;
		System.out.println("start value = "+start);


		if(start==playerInfoList.size())
		{
			System.out.println("start value = PlayerListSize");

		flagGameReady=true;
		//Passing Dummy Client to exit out of wait-block of serverSocket.accept() of the WaitForPlayers() method
		try {
			ClientThread client = new ClientThread(null,InetAddress.getLocalHost().getHostAddress() , 8888);
			client.getSocket().close(); // for safety purpose closing the socket
		
			} catch ( IOException e) {
			e.printStackTrace();
		}
	
	
		}
		

}
	
}
