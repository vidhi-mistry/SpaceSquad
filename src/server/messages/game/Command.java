package server.messages.game;

import server.messages.Message;


public class Command implements Message
{
  private int widgetId;
  private int value;

  public Command(int widgetId, int value) {
    this.widgetId = widgetId;
    this.value = value;
  }

  public int getWidgetId() {
    return widgetId;
  }

  public int getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if(this == o) return true;
    if(o == null || getClass() != o.getClass()) return false;

    Command command = (Command) o;

    return value == command.value && widgetId == command.widgetId;
  }
}
