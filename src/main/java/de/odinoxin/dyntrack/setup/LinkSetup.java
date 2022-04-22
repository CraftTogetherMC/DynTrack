package de.odinoxin.dyntrack.setup;

import de.gcmclan.team.guides.Guide;
import de.gcmclan.team.guides.GuideReceiver;
import de.gcmclan.team.guides.Segment;
import de.odinoxin.dyntrack.DynTrack;
import de.odinoxin.dyntrack.enums.TrinaryAnswer;
import de.odinoxin.dyntrack.generals.MethodPool;
import de.odinoxin.dyntrack.generals.MsgSender;
import de.odinoxin.dyntrack.interfaces.Linkable;
import de.odinoxin.dyntrack.layer.Layer;
import de.odinoxin.dyntrack.link.Link;
import de.odinoxin.dyntrack.style.Style;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jdom2.JDOMException;

public class LinkSetup implements GuideReceiver {
  private final DynTrack DYNTRACK;
  private final Link link;
  private Linkable toAdd;
  public static final Segment[] SEGMENT_PLAYER = new Segment[] {
          new Segment(0, "Enter the ID.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "ID's are unique.", false, false, -1, -1), new Segment(1, "Should the name of the Link be visible?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "You will define the name next.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 0, 4), new Segment(2, "Enter a name.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "The name will be displayed.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Names are not unique.", false, true, 1, 3), new Segment(3, "Enter a word, that should replace the word \"Link\";in the infobox.", false, true, 2, 4), new Segment(4, "Should the connections from the Link to each;Element be visible?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 1, 5), new Segment(5, "Should an edging around the linked Locations;be visible?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 4, 6), new Segment(6, "Enter the location: " + ChatColor.DARK_GRAY + "[" + ChatColor.DARK_GREEN + "Here" + ChatColor.DARK_GRAY + " | " + ChatColor.LIGHT_PURPLE + "x" + ChatColor.DARK_GREEN + "," + ChatColor.LIGHT_PURPLE + "y" + ChatColor.DARK_GREEN + "," + ChatColor.LIGHT_PURPLE + "z" + ChatColor.DARK_GREEN + "," + ChatColor.DARK_AQUA + "world" + ChatColor.DARK_GRAY + "]", false, false, 5, -1), new Segment(7, "Enter the ID of a Style.", false, false, 6, -1), new Segment(8, "Enter the ID of a Layer.", false, false, 7, -1), new Segment(9, "Enter " + ChatColor.DARK_GREEN + "finish " + ChatColor.RESET + "to finish the Setup.;" + ChatColor.DARK_GRAY + " [----- " + ChatColor.GRAY + "OR " + ChatColor.DARK_GRAY + "-----];" + "Enter the ID of the " + ChatColor.DARK_GRAY + "<" + ChatColor.RESET + "Path " + ChatColor.DARK_GRAY + "| " + ChatColor.RESET + "Square" + ChatColor.DARK_GRAY + "> " + ChatColor.RESET + "you want;to " + ChatColor.DARK_GRAY + "<" + ChatColor.RESET + "add " + ChatColor.DARK_GRAY + "| " + ChatColor.RESET + "remove" + ChatColor.DARK_GRAY + ">" + ChatColor.RESET + ".", false, false, 8, -1), new Segment(10, "Enter the # of the Trackpoint you want to link to.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "nearest" + ChatColor.RESET + " to link to the nearest Trackpoint.", false, false, 9, -1)
  };

  public static final Segment[] SEGMENT_CONSOLE = new Segment[] {
          new Segment(0, "Enter the ID.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "ID's are unique.", false, false, -1, -1), new Segment(1, "Should the name of the Link be visible?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "You will define the name next.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 0, 4), new Segment(2, "Enter a name.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "The name will be displayed.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Names are not unique.", false, true, 1, 3), new Segment(3, "Enter a word, that should replace the word \"Link\";in the infobox.", false, true, 2, 4), new Segment(4, "Should the connections from the Link to each;Element be visible?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 1, 5), new Segment(5, "Should an edging around the linked Locations;be visible?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 4, 6), new Segment(6, "Enter the location: " + ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + "x" + ChatColor.DARK_GREEN + "," + ChatColor.LIGHT_PURPLE + "y" + ChatColor.DARK_GREEN + "," + ChatColor.LIGHT_PURPLE + "z" + ChatColor.DARK_GREEN + "," + ChatColor.DARK_AQUA + "world" + ChatColor.DARK_GRAY + "]", false, false, 5, -1), new Segment(7, "Enter the ID of a Style.", false, false, 6, -1), new Segment(8, "Enter the ID of a Layer.", false, false, 7, -1), new Segment(9, "Enter " + ChatColor.DARK_GREEN + "finish " + ChatColor.RESET + "to finish the Setup.;" + ChatColor.DARK_GRAY + " [----- " + ChatColor.GRAY + "OR " + ChatColor.DARK_GRAY + "-----];" + "Enter the ID of the " + ChatColor.DARK_GRAY + "<" + ChatColor.RESET + "Path " + ChatColor.DARK_GRAY + "| " + ChatColor.RESET + "Square" + ChatColor.DARK_GRAY + "> " + ChatColor.RESET + "you want;to " + ChatColor.DARK_GRAY + "<" + ChatColor.RESET + "add " + ChatColor.DARK_GRAY + "| " + ChatColor.RESET + "remove" + ChatColor.DARK_GRAY + ">" + ChatColor.RESET + ".", false, false, 8, -1), new Segment(10, "Enter the # of the Trackpoint you want to link to.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "nearest" + ChatColor.RESET + " to link to the nearest Trackpoint.", false, false, 9, -1)
  };

  public LinkSetup(DynTrack DYNTRACK) {
    this.DYNTRACK = DYNTRACK;
    this.link = new Link(this.DYNTRACK);
  }
  public int receive(Guide guide, Segment seg, String msg) {
    String[] parts;
    Style st;
    double x, y, z;
    World w;
    Layer lay;
    TrinaryAnswer answer = TrinaryAnswer.UNKNOWN;
    switch (seg.getID()) {

      case 0:
        if (this.DYNTRACK.isUsed(msg) &&
                !this.link.getId().equalsIgnoreCase(msg)) {

          MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "The ID is already used.");
          return 0;
        }

        if (Pattern.matches("\\w+", msg)) {

          this.link.setId(msg);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "ID: " + ChatColor.DARK_AQUA + this.link.getId());
          return 1;
        }

        MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.RED + msg + ChatColor.RESET + " contains an invalid character.");
        MsgSender.sWarn(guide.getGuideSyn(), guide.getCmdSender(), "ID's should only contain a-z, A-Z and 0-9.");
        return 0;

      case 1:
        answer = MethodPool.getAnswer(msg);
        if (answer == TrinaryAnswer.POSITIVE) {

          this.link.setHideName(false);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "The Name is now visible.");
          return 2;
        }
        if (answer == TrinaryAnswer.NEGATIVE) {

          this.link.setHideName(true);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "The Name is now hidden.");
          return 4;
        }

        MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "Your answer should be yes or no!");
        MsgSender.sWarn(guide.getGuideSyn(), guide.getCmdSender(), "You typed: " + ChatColor.RED + msg);
        return 1;

      case 2:
        if (this.link.getId().equals(msg)) {

          this.link.setName("");
        } else {

          this.link.setName(msg);
        }
        MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "Name: " + ChatColor.AQUA + this.link.getName());
        return 3;
      case 3:
        this.link.setLinkSyn(msg);
        MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "Synonym for \"Link\": " + ChatColor.AQUA + this.link.getLinkSyn());
        return 4;
      case 4:
        answer = MethodPool.getAnswer(msg);
        if (answer == TrinaryAnswer.POSITIVE) {

          this.link.setHide(false);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "The connections are now visible.");
          return 5;
        }
        if (answer == TrinaryAnswer.NEGATIVE) {

          this.link.setHide(true);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "The connections are now hidden.");
          return 5;
        }

        MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "Your answer should be yes or no!");
        MsgSender.sWarn(guide.getGuideSyn(), guide.getCmdSender(), "You typed: " + ChatColor.RED + msg);
        return 4;

      case 5:
        answer = MethodPool.getAnswer(msg);
        if (answer == TrinaryAnswer.POSITIVE) {

          this.link.setEdging(true);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "The edging is now visible.");
          return 6;
        }
        if (answer == TrinaryAnswer.NEGATIVE) {

          this.link.setEdging(false);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "The edging is now hidden.");
          return 6;
        }

        MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "Your answer should be yes or no!");
        MsgSender.sWarn(guide.getGuideSyn(), guide.getCmdSender(), "You typed: " + ChatColor.RED + msg);
        return 5;

      case 6:
        if (msg.equalsIgnoreCase("here")) {

          if (guide.getCmdSender() instanceof Player) {

            Location loc = ((Player) guide.getCmdSender()).getLocation().clone();
            loc.setX(loc.getBlockX() + 0.5D);
            loc.setY(loc.getBlockY() + 0.5D);
            loc.setZ(loc.getBlockZ() + 0.5D);
            this.link.setLocation(loc);
            MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "Location: " + MethodPool.toColorLoc(this.link.getLocation()));
            return 7;
          }

          MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "Only online players can use their current location for a Link.");
          return 6;
        }

        if (!Pattern.matches("\\d+.*\\d*,\\d+.*\\d*,\\d+.*\\d*,\\w+", msg) &&
                !Pattern.matches("\\[\\d+.*\\d*,\\d+.*\\d*,\\d+.*\\d*,\\w+\\]", msg)) {

          MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.RED + msg + ChatColor.RESET + " is not compatible.");
          MsgSender.sWarn(guide.getGuideSyn(), guide.getCmdSender(), "The format is [x,y,z,world]");
          return 6;
        }
        msg = msg.replace("[", "").replace("]", "").replace(" ", "");
        parts = msg.split(",");

        try {
          x = Double.parseDouble(parts[0]);
          y = Double.parseDouble(parts[1]);
          z = Double.parseDouble(parts[2]);
        } catch (NumberFormatException e) {

          MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.RED + msg + ChatColor.RESET + " is not compatible.");
          MsgSender.sWarn(guide.getGuideSyn(), guide.getCmdSender(), "The format is [x,y,z,world]");
          return 6;
        }

        w = this.DYNTRACK.getServer().getWorld(parts[3]);
        if (w == null) {

          MsgSender.sErr(this.DYNTRACK, guide.getCmdSender(), "The world " + ChatColor.RED + msg + ChatColor.RESET + " was not found.");
          if (guide.getCmdSender() instanceof Player) {
            MsgSender.sWarn(guide.getGuideSyn(), guide.getCmdSender(), "Your current world is " + ChatColor.DARK_AQUA + ((Player) guide.getCmdSender()).getWorld().getName());
          }
          return 6;
        }
        this.link.setLocation(new Location(w, (int) x + 0.5D, (int) y + 0.5D, (int) z + 0.5D));
        MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "Location: " + MethodPool.toColorLoc(this.link.getLocation()));
        return 7;

      case 7:
        st = this.DYNTRACK.getStyle(msg);
        if (st == null) {

          MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "Could not find the Style " + ChatColor.RED + msg + ChatColor.RESET + ".");
          return 7;
        }
        this.link.setStyle(st);
        MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "Style: " + ChatColor.DARK_AQUA + this.link.getStyle().getId());
        return 8;
      case 8:
        lay = this.DYNTRACK.getLayer(msg);
        if (lay == null) {

          MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "Could not find the Layer " + ChatColor.RED + msg + ChatColor.RESET + ".");
          return 8;
        }
        this.link.setLayer(lay);
        MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "Layer: " + ChatColor.DARK_AQUA + this.link.getLayer().getName());
        return 9;
      case 9:
        if (msg.equalsIgnoreCase("finish")) {

          finish(guide.getCmdSender());
          return -2;
        }
        this.toAdd = this.DYNTRACK.getPath(msg);
        if (this.toAdd == null) {

          this.toAdd = this.DYNTRACK.getSquare(msg);
          if (this.toAdd == null) {

            MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.RED + msg + ChatColor.RESET + " is neither a Path nor a Square.");
            return 9;
          }
        }
        if (this.link.getTable().containsKey(this.toAdd)) {

          this.link.del(this.toAdd);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "Unlinked " + ChatColor.DARK_AQUA + this.toAdd.getId() + ChatColor.RESET + ".");
          return 9;
        }
        if (this.toAdd.getLocationList().size() <= 0) {

          MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "The chosen Element has not Trackpoints.");
          return 9;
        }
        return 10;

      case 10:
        try {
          int at = 0;
          if (msg.equalsIgnoreCase("nearest")) {

            Location loc = this.link.getLocation();
            Location nearest = this.toAdd.getLocationList().get(at);

            for (int i = 1; i < this.toAdd.getLocationList().size(); i++) {

              Location current = this.toAdd.getLocationList().get(i);
              if ((current

                      .getX() - loc.getX()) * (current.getX() - loc.getX()) + (current
                      .getY() - loc.getY()) * (current.getY() - loc.getY()) + (current
                      .getZ() - loc.getZ()) * (current.getZ() - loc.getZ()) < (nearest

                      .getX() - loc.getX()) * (nearest.getX() - loc.getX()) + (nearest
                      .getY() - loc.getY()) * (nearest.getY() - loc.getY()) + (nearest
                      .getZ() - loc.getZ()) * (nearest.getZ() - loc.getZ())) {

                at = i;
                nearest = current;
              }

            }
          } else {

            at = Integer.parseInt(msg.replace("#", ""));
            if (at < 1) {
              at = 1;
            }
            if (at > this.toAdd.getLocationList().size()) {
              at = this.toAdd.getLocationList().size();
            }
            at--;
          }
          this.link.add(this.toAdd, at);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "Linked " + ChatColor.DARK_AQUA + this.toAdd.getId() + ChatColor.RESET + " at " + MethodPool.toColorLoc(this.toAdd.getLocationList().get(at)) + ".");
          return 9;
        } catch (NumberFormatException e) {

          MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.RED + msg + ChatColor.RESET + " is not a valid number.");
          return 10;
        }
    }
    return -3;
  }

  public void finish(CommandSender s) {
    try {
      this.link.save();
      MsgSender.sInfo("Setup", s, "Saved " + ChatColor.DARK_AQUA + this.link.getId() + ChatColor.RESET + ".");
    } catch (JDOMException | java.io.IOException | java.sql.SQLException e) {

      MsgSender.sErr("Setup", s, "Could not save " + ChatColor.DARK_AQUA + this.link.getId() + ChatColor.RESET + ".");
    }
  }

  public boolean usesId(String id) {
    return this.link.getId().equalsIgnoreCase(id);
  }
}