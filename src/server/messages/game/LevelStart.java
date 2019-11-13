package server.messages.game;

import java.io.Serializable;
import java.util.List;

import server.messages.Message;
import controls.Widget;


public class LevelStart implements Message
{
  private List<Widget> widgetList;
  private int secondsPerCommand;
  private int playerNum;

  public <T extends List<Widget> & Serializable> LevelStart(T widgetList, int secondsPerCommand, int playerNum) {
    this.widgetList = widgetList;
    this.secondsPerCommand = secondsPerCommand;
    this.playerNum = playerNum;
  }
  
  public int getSeconds() {
	  return secondsPerCommand;
  }
  
  public List<Widget> getWidgetList() {
	  return widgetList;
  }
 
}
