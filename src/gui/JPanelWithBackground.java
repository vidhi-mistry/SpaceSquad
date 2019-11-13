package gui;


import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class JPanelWithBackground extends JPanel {

	
	private Image backgroundImage;
	
	public JPanelWithBackground(String fileName)
	{
		try {
			backgroundImage = ImageIO.read(new File(fileName)); //sets Image whichever file's name is passed
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	@Override
	public void paintComponent(Graphics g)
	{
		//super.paintComponent(g); //clears the Panel every time called and then adds the below graphics
									//method of Jcomponent  overriden to add some graphics
		
	    //Draw the background image
		g.drawImage(backgroundImage, 0, 0, this);	
	}
	
	
	
}