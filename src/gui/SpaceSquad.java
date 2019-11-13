package gui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import client.ClientThread;
import controls.InteractionListener;
import controls.Widget;
import server.GameThread;
import server.messages.game.GameOverMessage;
import server.messages.game.PlayerLeft;
import server.messages.initialization.IsWaiting;
import server.messages.initialization.PlayerInfo;

public class SpaceSquad extends JFrame {
	
	
	private static final long serialVersionUID = 1L;
	
	static final String START = "Start Screen";
	static final String GAME_CONT = "Game Container";
	static final String END = "End Screen";
	static final String GAME = "Game";
	static final String WAIT_PLAYERS = "Waiting for Other Players";
	int score=0;
	
	private JLabel  sLabelLeft,sLabelRight;//label
	private Scrollbar  startBar; //Scroll bar
	
	JLabel commandText;
	String hostname;
	ClientThread client;
	JPanel healthPanel, controlPanel, timePanel, commandPanel;
	JPanel cardsCont, gamePane, gameCard, endCard;
	JPanelWithBackground startCard;
	WaitingRoom waitPanel;
	JLabel scoreLabel;
	List<PlayerInfo> players;
	
	
	JButton continueButton;
	JButton readyButton;
	Dimension contDimensions, wpDimensions;
	

	public SpaceSquad() 
	{
		super("SpaceSquad");
		
		setSize(800,620);
		setResizable(false);
		setLocationRelativeTo(null); //places the window at the center of the screen
			
		cardsCont=new JPanel(new CardLayout());
		cardsCont.setBackground(Color.black);
				
		//Set up starting screen panel
				startCard = new JPanelWithBackground("src/gui/start.jpg");
				startCard.setLayout(null);
			
				
			
				//ScrollBAR
				
				startBar=new Scrollbar(0,0,20,0,500);
				startBar.setBounds(200,400,400,30);	
				startBar.setValue(20);
				startBar.setBackground(Color.blue);
				
				sLabelLeft=new JLabel("STOP");
				sLabelLeft.setForeground(Color.WHITE);
				sLabelLeft.setBounds(150, 400, 50, 30);
				
				sLabelRight=new JLabel("PLAY");	
				sLabelRight.setForeground(Color.WHITE);
				sLabelRight.setBounds(610, 400, 50, 30);
						
				
				
				startCard.add(sLabelLeft);
				startCard.add(startBar);
				startCard.add(sLabelRight);		
			
		
		//Set gameplay panel layout for the game
		gameCard = new JPanel(new CardLayout());
		gameCard.setSize(800, 620);
		
		
		waitPanel = new WaitingRoom(this);
	

		
		
	
		gamePane = new JPanel();
		gamePane.setLayout(new BoxLayout(gamePane, BoxLayout.Y_AXIS));
		

		//set up health bar
		healthPanel = new HealthBar();
		healthPanel.setPreferredSize(new Dimension(540, 100));
		
		//set up commands
		commandText = new JLabel("");
		commandPanel = new JPanel();
		commandPanel.setPreferredSize(new Dimension(540, 50));
		commandPanel.setBackground(Color.GRAY);
	//	commandText.setOpaque(true);
		commandText.setPreferredSize(new Dimension(530, 40));
		commandText.setBackground(Color.BLACK);
		commandText.setHorizontalAlignment(SwingConstants.CENTER);
		commandText.setForeground(Color.GREEN);
		commandPanel.add(commandText);
		
		//set up time remaining bar
		timePanel = new TimeBar();
		timePanel.setPreferredSize(new Dimension(540, 50));
		timePanel.setBackground(Color.CYAN);
		
		//set up controls
		controlPanel = new JPanel();
		controlPanel.setPreferredSize(new Dimension(540, 400));
		controlPanel.setLayout(new GridLayout(2,3));
		controlPanel.setBackground(Color.BLACK);
			
		gamePane.add(healthPanel);
		gamePane.add(commandPanel);
		gamePane.add(timePanel);
		gamePane.add(controlPanel);
		
		
		gameCard.add(waitPanel, WAIT_PLAYERS);
		gameCard.add(gamePane, GAME);
		
		
		//Set up end screen panel
		endCard = new JPanel();
	    endCard.setBackground(Color.BLACK);
	    endCard.setLayout(null);
	    JLabel end =new JLabel(" GAME OVER !!! ");
	    scoreLabel=new JLabel();
	    		Font f=new Font("Arial",Font.ITALIC,30);;
	    scoreLabel.setForeground(Color.WHITE);
	    scoreLabel.setFont(f);
	    scoreLabel.setBounds(100,325,600,100);
	    end.setForeground(Color.WHITE);
	    end.setBounds(270,200 ,300, 100);
	    
	    end.setFont(f);
	    endCard.add(end);
	    
	    	
	    
		
		
		cardsCont.add(startCard,START);
		cardsCont.add(gameCard,GAME_CONT);
		cardsCont.add(endCard,END);
		
		
		
		
		CardLayout cl_game = (CardLayout)(gameCard.getLayout());
		cl_game.show(gameCard, WAIT_PLAYERS);
		
		
		
		
		
		
		CardLayout cl_cont = (CardLayout)(cardsCont.getLayout());
		cl_cont.show(cardsCont,START);
		
		
		add(cardsCont);
	
		
		startBar.addAdjustmentListener(new AdjustmentListener(){
		
			@Override
			public void adjustmentValueChanged(AdjustmentEvent event){
		
				if(startBar.getValueIsAdjusting())
					return;
					
				int val;
				val=startBar.getValue();
				if(val==480)
				{
					createClient();	
				}
			}	
		});
		
		
		startCard.getRootPane().setDefaultButton(null);
	

		this.addWindowListener(new WindowAdapter(){
	        public void windowClosing(WindowEvent e){
	            int i=JOptionPane.showConfirmDialog(null, "Do you want to leave this game ?",
	    				"Confirm Exit", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null);
	            if (i == JOptionPane.YES_OPTION)
	            { 
	            	
	            
		            if(client!=null)
		            {
		            	client.sendMessage(new PlayerLeft());
		            }
		            
		            System.exit(0);

	            }
	            else  setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	            
	        }
	    });

	}
	
		

	public static void main(String args[])
	{
	
        SpaceSquad ss=new SpaceSquad();
        
        if (args.length == 1) {
			ss.setHostname(args[0]);
		} else {
			System.out.print("Host Name: ");
			Scanner scan = new Scanner(System.in);
			ss.setHostname(scan.next());
			scan.close();
		}
		ss.setVisible(true);    
	}
	

	public void setHostname(String hostname)
	{
		this.hostname = hostname;
	}
	
	public void createClient() {
	
		client = new ClientThread(this, hostname, 8888);
		client.start();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("client created :"+client.getPlayerNum());

		
	}
	
	
	public void acceptedPlayer() {
		CardLayout cl = (CardLayout)(cardsCont.getLayout());
		cl.show(cardsCont, GAME_CONT);		
	}
	public void gameReady() {
		System.out.println("A Game Is Ready");

	}
	public void setWaitingRoom()
	{
		waitPanel.setPlayer(client.getPlayerNum());
		waitPanel.th.start();
		
	}
	
	
	/*
	 Waiting Room Setup
	 */
	public void isWaiting(IsWaiting w)
	{
	players=w.getPlayerList();
	waitPanel.setPlayerList(players);
	
	
	
	}
	
	/*
	  Begins the game.
	 */
	public void gameStarted() {
	System.out.println("Game Started");
	}
	
	/*
	  Updates the Health bar to the current health.
	 */
	public void updateHealth(int health) {
		((HealthBar) healthPanel).updateHealthBar(health);
	}
	
	/*
	  Update the time to show how much time is left.
	  current - the time remaining
	  total - the total time
	 */
	public void updateTime(int current, int total) {
		((TimeBar)timePanel).currentTimeRemaining(current, total);
	}
	
	/*
	  Changes the screen to say game over and displays high scores.
	 */
	public void endGame(GameOverMessage over) {
		// TODO 
		score=over.getScore();
		System.out.println("Game Over with Score ="+score);
     	CardLayout cl = (CardLayout)(cardsCont.getLayout());
		cl.show(cardsCont, END);
		if(score>50)
		scoreLabel.setText("Well done, You Guys Are "+score+"% Compatible");
		else
			scoreLabel.setText("You Guys Are "+score+"% Compatible");

	    endCard.add(scoreLabel);
	    

	}
	
	
	
	/*
	  Creates the level.

	 */
	public void createLevel(List<Widget> widgetList, boolean first) {
		CardLayout cl = (CardLayout)(gameCard.getLayout());
		cl.show(gameCard, GAME);
		controlPanel.removeAll();
		for (int i = 0; i < widgetList.size(); i++) {
			Widget w = widgetList.get(i);
			final int id;
			
				id = i + GameThread.DASH_PIECES_PER_PLAYER*((client.thisPlayerInfo.getPlayerNumber())-1);
			
			w.addInteractionListener(new InteractionListener() {
				@Override
				public void interactionOccurred(int value) {
					client.piecePressed(id, value);
				}
			});
			controlPanel.add(w.getComponent());
			
		}
	}
	


	/*
	  Changes the current command displayed.
	 */
	public void displayCommand(String s) {
		commandText.setText(s);
	}
	
}
