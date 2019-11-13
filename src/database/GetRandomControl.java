package database;

import server.GameThread;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class GetRandomControl extends SQLCommand
{
	int numOfPlayers;
	private ArrayList<Control> controlInfoList;
	
	public GetRandomControl(int n)
	{
		numOfPlayers=n;
	}

    public ArrayList<Control> getList() {
    	return controlInfoList;
  }

 
  @Override
  public boolean execute() {
    try {
    Class.forName(super.DRIVER);
      Connection conn = DriverManager.getConnection(super.DB_ADDRESS + super.DB_NAME, super.USER, super.PASSWORD);

      String controlQuery = "SELECT * FROM controls ORDER BY RAND() LIMIT " + numOfPlayers*GameThread.DASH_PIECES_PER_PLAYER;
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(controlQuery);

      controlInfoList = new ArrayList<>();

      while(rs.next()) {
        String control = rs.getString("control");
        String verb = rs.getString("command_verb");
        controlInfoList.add(new Control(control, verb));
      }
    }
    catch( SQLException | ClassNotFoundException  e) {
      e.printStackTrace();
    }
    return false;
  }

  public class Control {
    private String controlName;
    private String commandVerb;

    public Control(String controlName, String commandVerb) {
      this.controlName = controlName;
      this.commandVerb = commandVerb;
    }

    public String getControlName() {
      return controlName;
    }

    public String getCommandVerb() {
      return commandVerb;
    }
  }

}
