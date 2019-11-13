package controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;


public class PushButtonWidget implements Widget {

	protected transient InteractionListener listener;
	private String name;
	private String verb;
	
	public PushButtonWidget(String name, String verb) {
		this.name = name;
		this.verb = verb;
	}
	
@Override
	public String getName() {
		return name;
	}

	@Override
	public String getVerb() {
		return verb;
	}
	
	
	
	@Override
	public void addInteractionListener(InteractionListener listener) {
		this.listener=listener;
	}
	
	@Override
	public void removeInteractionListener(InteractionListener listener) {
	}
	
	protected void interactionOccurred(int value) {
			listener.interactionOccurred(value);
		}
	
	
	public static PushButtonWidget generateWidget(String name, String verb) {
		
		return new PushButtonWidget(name,verb);

	}

	public JButton getComponent() {
		JButton button = new JButton(getName());

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				interactionOccurred(1);
			}		
		});
		return button;
	}

	@Override
	public int getRandomValue() {
		return 1;
	}
	
}