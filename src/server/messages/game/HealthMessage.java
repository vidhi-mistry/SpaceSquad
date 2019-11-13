package server.messages.game;

import server.messages.Message;

public class HealthMessage implements Message
{
  private int currHealth;

  public HealthMessage(int currHealth) {
    this.currHealth = currHealth;
  }

  public int getCurrHealth() {
    return currHealth;
  }
}
