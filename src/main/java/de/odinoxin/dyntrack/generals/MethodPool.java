package de.odinoxin.dyntrack.generals;

import de.odinoxin.dyntrack.enums.TrinaryAnswer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public abstract class MethodPool {
  public static TrinaryAnswer getAnswer(String msg) {
    if (msg.startsWith("y") || msg
            .equalsIgnoreCase("ok") || msg
            .equalsIgnoreCase("okay") || msg
            .equalsIgnoreCase("true") || msg
            .equalsIgnoreCase("right") || msg
            .equalsIgnoreCase("sure") || msg
            .equalsIgnoreCase("positive")) {
      return TrinaryAnswer.POSITIVE;
    }
    if (msg.startsWith("n") || msg
            .equalsIgnoreCase("false") || msg
            .equalsIgnoreCase("wrong")) {
      return TrinaryAnswer.NEGATIVE;
    }
    return TrinaryAnswer.UNKNOWN;
  }

  public static String toColorLoc(Location loc) {
    return ChatColor.DARK_GRAY + "(" + ChatColor.LIGHT_PURPLE + loc
            .getBlockX() + ChatColor.GRAY + "x" + ChatColor.DARK_GRAY + " | " + ChatColor.LIGHT_PURPLE + loc

            .getBlockY() + ChatColor.GRAY + "y" + ChatColor.DARK_GRAY + " | " + ChatColor.LIGHT_PURPLE + loc

            .getBlockZ() + ChatColor.GRAY + "z" + ChatColor.DARK_GRAY + ")" + ChatColor.RESET;
  }

  public static String toText(Location loc, boolean printWorld) {
    if (!printWorld) {
      return "(" + loc
              .getX() + "," + loc
              .getY() + "," + loc
              .getZ() + ")";
    }

    return "(" + loc
            .getX() + "," + loc
            .getY() + "," + loc
            .getZ() + "," + loc
            .getWorld().getName() + ")";
  }

  public static Location toLoc(String txt, World world) {
    txt = txt.replace("(", "").replace(")", "");
    String[] parts = txt.split(",");
    if (parts.length != 3) {
      return null;
    }

    try {
      Location loc = new Location(world, Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
      return loc;
    } catch (NumberFormatException e) {

      return null;
    }
  }

  public static Location toLoc(String txt, Plugin plugin) {
    txt = txt.replace("(", "").replace(")", "");
    String[] parts = txt.split(",");
    if (parts.length != 4) {
      return null;
    }

    try {
      Location loc = new Location(plugin.getServer().getWorld(parts[3]), Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
      return loc;
    } catch (NumberFormatException e) {

      return null;
    }
  }
}

/* Location:              C:\Users\Joscha\Desktop\DynTrack-2.4b3 (1).jar!\de\odinoxin\dyntrack\generals\MethodPool.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */