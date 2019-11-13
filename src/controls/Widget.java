package controls;

import java.io.Serializable;

import javax.swing.JComponent;

public interface Widget extends Serializable, Interactable {


	String getName();
	
	String getVerb();
	
	
	JComponent getComponent();

	int getRandomValue();
}
