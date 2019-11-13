package server;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import controls.PushButtonWidget;
import controls.Widget;
import database.DatabaseDriver;
import database.GetRandomControl;
import server.messages.Message;
import server.messages.game.Command;
import server.messages.game.GameOverMessage;
import server.messages.game.HealthMessage;

import server.messages.game.LevelStart;
import server.messages.game.TimeRunOut;


public class GameThread extends Thread
{

  public static final int DASH_PIECES_PER_PLAYER = 6;
  private final int INITIAL_HEALTH = 10;
  private final int INITIAL_COMMANDS =10;

  private final Random RANDOM = new Random();

  private List<Player> playerList;
  private List<PlayerThread> playerThreadList=new ArrayList<>();
 

  private int score = 0;
  private int health;
  private int commandsRemaining;

  private Lock lock = new ReentrantLock();
  private Lock commandsLock = new ReentrantLock();
  private Condition condition = lock.newCondition();
  
  private List<Widget> dashPieces;

  public GameThread(List<Player> p) {
    playerList = p;
  }

 
  
  /**
   * Generates level for the current game.
   */
  
  public void generateLevel() {
    health = INITIAL_HEALTH;
    commandsRemaining = INITIAL_COMMANDS;
    dashPieces = generateDashPieces();
    System.out.println("--Dash Pieces Generated--");
    for( int i=0;i<dashPieces.size();i++)
    {
        System.out.println("Verb="+dashPieces.get(i).getVerb());

    }

    int commandTime = getCommandTime();
    
    
    for(int i=0;i<playerList.size();i++)
    {
    	ArrayList<Widget> playerPieces = new ArrayList<>();
    	playerPieces.addAll(dashPieces);
    	
        System.out.println("--PlayerPieces Generated--");
    	for( int j=0;j<playerPieces.size();j++)
    	    {
    	        System.out.println("PlayerPieces="+playerPieces.get(j).getName()
    	        		);

    	    }
    	
    		playerList.get(i).sendMessage(new LevelStart(playerPieces, commandTime, playerList.get(i).getPlayerNum()));
            System.out.println("Level Start sent for "+i);

 
    	sendAllMessage(new HealthMessage(health));
    	getNewCommand(playerThreadList.get(i));
    }

  }

  /**
   * Generates a random list of Widgets to be used.
   * Gets a list of controls from the database, and creates widgets based off of these.
   * 
   */
  private List<Widget> generateDashPieces() {
    List<Widget> pieces = new ArrayList<>();
    ArrayList<GetRandomControl.Control> controls = DatabaseDriver.getRandomControl(playerList.size());
    for(GetRandomControl.Control control : controls) {
    pieces.add(PushButtonWidget.generateWidget(control.getControlName(),
                                               control.getCommandVerb()));
    	
    	
    	
    }
    return pieces;
  }

  
  
  

  /**
   * Instantiates  player threads and starts them.
   * Runs until the game is completed .
   */
  public void run() {
	  
	  for(int i=0;i<playerList.size();i++)
	  { 
			System.out.println("PLAYER---"+playerList.get(i).getPlayerNum());
			
			PlayerThread  pThread = new PlayerThread(playerList.get(i), null, this);
			playerThreadList.add(pThread);
			
			System.out.println("PLAYER THREAD CREATED FOR ---"+playerThreadList.get(i).getPlayer().getPlayerNum());
	   }
	  	
		for(int i=0;i<playerList.size();i++)
		{
			List<PlayerThread> teammate=new ArrayList<>();
		  	for(int j=0;j<playerList.size();j++)
			{
					if(j!=i)
					{
							teammate.add(playerThreadList.get(j));
					}
				 }
			playerThreadList.get(i).setTeammate(teammate);

			for(int j=0;j<playerThreadList.get(i).getTeammate().size();j++)
			{
				System.out.println("TEAMMATE OF "+(i+1)+" ---"+playerThreadList.get(i).getTeammate().get(j).getPlayer().getPlayerNum());
			}
		  	playerThreadList.get(i).start();
		}
   	  
	  
      generateLevel();
      try {
       lock.lock();
        condition.await();
        lock.unlock();
    	
      }
      catch(Exception e) {
        e.printStackTrace();
      }
  }

  /**
   * Gets the number of seconds that should be allowed for players to execute a command.
   * @return the number of seconds allowed per command
   */
  public int getCommandTime() {
    return 120;
  }
/*
 
  /*
   * Chooses a random widget and value for the widget.
   * Creates a Command object from these and sets the command for the player
   */
  public void getNewCommand(PlayerThread playerThread) {
    int widgetId;
    widgetId=RANDOM.nextInt(dashPieces.size());
   
    Widget widget = dashPieces.get(widgetId);
    int newValue = widget.getRandomValue();
    Command command = new Command(widgetId, newValue);
    playerThread.setCurrCommand(command);
  }

  /**
   * Decrements the number of commands left for the level by one.
   * If the number of commands is 0, then the level is finished.
   * It then notifies the current thread's lock condition to signal that the level has ended.
   */
  public synchronized boolean decrementCommands() {
	try {
		//TODO 
		commandsLock.lock();
		commandsRemaining--;
    if(commandsRemaining == 0) {
      triggerGameOver();
    	
    }
		return commandsRemaining == 0;
	} finally {
		commandsLock.unlock();
	}
  }

  /**
   * Decrements the health allotted for the level by one.
   * If the team's health is 0, it triggers a game ending function.
   * Otherwise, it sends a message to each player with the remaining health for that level.
   */
  public synchronized void decrementHealth() {
    health--;
    System.out.println("HEALTH======"+health);
    if(health == 0) {
      triggerGameOver();
      return;
    }
    sendAllMessage(new HealthMessage(health));
  }

  /**
   * Triggers a game over sequence for the current game thread.
   * Also sends the final score in GameOverMessage to the players.
   * Interrupts both Player threads and terminates both players.
   * Notifies on the wait condition so that there cannot be a deadlock situation.
   */
  public void triggerGameOver() {
 
    sendAllMessage(new GameOverMessage(score));


    for(int i=0;i<playerList.size();i++)
    {
	  System.out.println("Interrupting Threads---"+i+"---"+playerThreadList.get(i).getPlayer().getPlayerNum());
    	playerThreadList.get(i).interrupt();
    }
    for(int i=0;i<playerList.size();i++)
    { 
  	  System.out.println("Terminating List..."+i+"---"+playerThreadList.get(i).getPlayer().getPlayerNum());
    playerList.get(i).terminate();
    }

	  System.out.println("Closing...");
  
   lock.lock();
    condition.signalAll();
    lock.unlock();
	 
	  System.out.println(":):P");

  }

  /**
   * Utility function to send a message to all players
   * @param s the message to send to all players
   */
  public void sendAllMessage(Message s) {
	  for(int i=0;i<playerList.size();i++)
	    {    playerList.get(i).sendMessage(s);
	    
	    }
   
  }

  
  public Lock getLock() {
    return lock;
  }

  public Condition getCondition() {
    return condition;
  }

  public class PlayerThread extends Thread
  {
    private Player player;
    private List<PlayerThread> teammate=new ArrayList<>();
    private GameThread gameThread;
    private Command currCommand;

    public PlayerThread(Player player, List<PlayerThread> teammate, GameThread gameThread) {
      this.player = player;
      this.teammate = teammate;
      this.gameThread = gameThread;
    }

    /**
     * Runs infinitely until the thread is interrupted.
     * Waits for messages from client and executes the message.
     */
    public void run() {
      while(true) {
        try {
          executeMessage(player.getIn().readObject());
        }
        catch(IOException | ClassNotFoundException e) {
//          e.printStackTrace();
          gameThread.triggerGameOver();
          return;
        }
      }
    }

    /**
     * Sets the current command for the player, and notifies the player of this command.
     * @param c the command to be set and sent to the client
     */
    public void setCurrCommand(Command c) {
      this.currCommand = c;
      player.sendMessage(currCommand);
    }

    public void setTeammate(List<PlayerThread> t) {
       teammate=t;
      }
    
    public List<PlayerThread> getTeammate() {
        return teammate;
       }
   

    /**
     * Executes a message recieved from the client.
     * The two possible objects from the client are handled here:
     * <ol>
     * <li>
     * TimeRunOut: Decrement the health and get a new command.
     * </li>
     * <li>
     * Command: Check if the command is equal to the goal command.
     * Get a new command and decrement number of commands remaining if it is.
     * </li>
     * </ol>
     *
     * @param obj the message received from the client.
     */
    private void executeMessage(Object obj) {
      if(obj instanceof TimeRunOut) {
        System.out.println("Decrementing Heaalth");
    	  gameThread.decrementHealth();
        
        gameThread.getNewCommand(this);
      }
      else if(obj instanceof Command) {
        Command command = (Command) obj;
        if(command.equals(currCommand)) {
          score += 10 ;
          
          if(!gameThread.decrementCommands()) {
            gameThread.getNewCommand(this);
          }
        }
        for(int i=0;i<teammate.size();i++)
        {
         if(command.equals(teammate.get(i).getCurrCommand())) {
          score += 10 ;
          if(!gameThread.decrementCommands()) {
            gameThread.getNewCommand(teammate.get(i));
          }
          }
         }
      }
    }

    public Player getPlayer() {
      return player;
    }

    public Command getCurrCommand() {
      return currCommand;
    }
  }
}
