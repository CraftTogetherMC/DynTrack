package de.odinoxin.dyntrack.interfaces;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;

public interface Linkable extends Drawable {
  String getId();

  List < Location > getLocationList();

  boolean addLocation(Location paramLocation, int paramInt);

  void delLocation(int paramInt);

  void setWorld(World paramWorld);

  World getWorld();
}