package de.odinoxin.dyntrack.generals;

import de.odinoxin.dyntrack.enums.DataActionResult;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DataEvent < D > extends Event implements Cancellable {
  private static final HandlerList HANDLERS = new HandlerList();

  private boolean canceled = false;
  private final D d;
  private final DataActionResult action;

  public DataEvent(D d, DataActionResult action) {
    this.d = d;
    this.action = action;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public boolean isCancelled() {
    return this.canceled;
  }

  public void setCancelled(boolean canceled) {
    this.canceled = canceled;
  }

  public D getData() {
    return this.d;
  }

  public DataActionResult getAction() {
    return this.action;
  }
}