package database;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class DatabaseDriver {
	

	public static ArrayList<GetRandomControl.Control> getRandomControl(int numOfPlayers) {
		GetRandomControl grc = new GetRandomControl(numOfPlayers);
		
		grc.execute();
		return grc.getList();
	}
}
