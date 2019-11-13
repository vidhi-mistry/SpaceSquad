package server.messages.initialization;

import server.messages.Message;

public class IAmReady implements Message{
private int player;

public IAmReady(int p)
{player=p;}

public int getPlayer()
{
	return player;
	}
}
