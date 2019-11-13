package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import server.messages.initialization.IAmReady;
import server.messages.initialization.PlayerInfo;

public class WaitingRoom extends JPanel implements Runnable{

	JLabel waitRoomPic;
	Thread th;
	JButton ready;
	static List<PlayerInfo> players=new ArrayList<>();
	int whichPlayerAmI;
	SpaceSquad parent;
	
	public WaitingRoom(SpaceSquad p)
	{
		parent=p;
		th=new Thread(this);
		setLayout(null);
		
	
		waitRoomPic = new JLabel(new ImageIcon("src/gui/waitingRoom.gif"));
		waitRoomPic.setBounds(0, 0, 800, 620);
		JButton ready=new JButton("I AM READY");
		
		ready.setBackground(Color.red);
		ready.setBounds(100,480 , 600, 40);
		ready.setFont(new Font("Arial",Font.BOLD,20));
		ready.setForeground(Color.WHITE);
		ready.setVisible(true);
		waitRoomPic.add(ready);
		
		
		add(waitRoomPic);
		//th.start();
		
		ready.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				System.out.println("Button Pressed");
				parent.client.sendMessage(new IAmReady(whichPlayerAmI));
				
				
			}
			
		});
		
	
		
		
	}
	
	
	public void setPlayerList(List<PlayerInfo> p)
	{
	
		players=p;
		 
		
	}
	
	public void setPlayer(int p)
	{
		
		whichPlayerAmI=p;
		System.out.println(whichPlayerAmI);
		
	}
	
	@Override
	public void run() {
		int i=0;
		
		while(true)
		{ 
			System.out.print("");

			while(i<=players.size())
			{
		
			 repaint();
			 i++;
			}
		 
		}
	}
	
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		int i=1;
			for(PlayerInfo p:players)
			{
			
				JLabel avatar=new JLabel(new ImageIcon(p.getAvatarFileName()));
			    avatar.setBounds(i*100,285,70,100);
			 
			    waitRoomPic.add(avatar);	
			    i++;						
			}
			
		
	}
	
	

	
}
