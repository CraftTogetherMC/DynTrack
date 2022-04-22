package de.odinoxin.dyntrack.editor;

import de.gcmclan.team.guides.Guide;
import de.gcmclan.team.guides.GuideReceiver;
import de.gcmclan.team.guides.Segment;
import de.odinoxin.dyntrack.DynTrack;
import de.odinoxin.dyntrack.enums.DataActionResult;
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
import org.jdom2.JDOMException;

public class LinkEditor implements GuideReceiver {
  private final CommandSender S;
  private Link link;
  private Linkable toAdd;
  public static final Segment[] SEGMENTS_CONSOLE = new Segment[] {
          new Segment(0, "What would you like to edit?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "You can edit:;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "ID;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Name Visibility;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Name;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Link Synonym;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Hide;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Edging;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Location;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Style;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Layer;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Linked", false, false, -1, -1), new Segment(1, "Enter the new ID.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "ID's are unique.", false, false, 0, -1), new Segment(2, "Should the name of the Link be visible?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 0, -1), new Segment(3, "Enter a new name.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Names are not unique.", false, true, 0, -1), new Segment(4, "Enter a word, that should replace the word;\"Link\" in the infobox.", false, true, 0, -1), new Segment(5, "Should the connections from the Link to each Element be visible?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 0, -1), new Segment(6, "Should an edging around the linked Locations be visible?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 0, -1), new Segment(7, "Enter the location: " + ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + "x" + ChatColor.DARK_GREEN + "," + ChatColor.LIGHT_PURPLE + "y" + ChatColor.DARK_GREEN + "," + ChatColor.LIGHT_PURPLE + "z" + ChatColor.DARK_GREEN + "," + ChatColor.DARK_AQUA + "world" + ChatColor.DARK_GRAY + "]", false, false, 0, -1), new Segment(8, "Enter the ID of a Style.", false, false, 0, -1), new Segment(9, "Enter the ID of a Layer.", false, false, 0, -1), new Segment(10, "Enter " + ChatColor.DARK_GREEN + "finish " + ChatColor.RESET + "to finish the Editor.;" + ChatColor.DARK_GRAY + " [----- " + ChatColor.GRAY + "OR " + ChatColor.DARK_GRAY + "-----];" + "Enter the ID of the " + ChatColor.DARK_GRAY + "<" + ChatColor.RESET + "Path " + ChatColor.DARK_GRAY + "| " + ChatColor.RESET + "Square" + ChatColor.DARK_GRAY + "> " + ChatColor.RESET + "you want;to " + ChatColor.DARK_GRAY + "<" + ChatColor.RESET + "add " + ChatColor.DARK_GRAY + "| " + ChatColor.RESET + "remove" + ChatColor.DARK_GRAY + ">" + ChatColor.RESET + ".", false, false, 0, -1), new Segment(11, "Enter the # of the Trackpoint you want to link to.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "nearest" + ChatColor.RESET + " to link to the nearest Trackpoint.", false, false, 10, -1)
  };

  public static final Segment[] SEGMENTS_PLAYER = new Segment[] {
          new Segment(0, "What would you like to edit?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "You can edit:;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "ID;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Name Visibility;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Name;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Link Synonym;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Hide;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Edging;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Location;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Style;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Layer;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Linked", false, false, -1, -1), new Segment(1, "Enter the new ID.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "ID's are unique.", false, false, 0, -1), new Segment(2, "Should the name of the Link be visible?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 0, -1), new Segment(3, "Enter a new name.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Names are not unique.", false, true, 0, -1), new Segment(4, "Enter a word, that should replace the word;\"Link\" in the infobox.", false, true, 0, -1), new Segment(5, "Should the connections from the Link to each Element be visible?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 0, -1), new Segment(6, "Should an edging around the linked Locations be visible?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 0, -1), new Segment(7, "Enter the location: " + ChatColor.DARK_GRAY + "[" + ChatColor.DARK_GREEN + "Here" + ChatColor.DARK_GRAY + " | " + ChatColor.LIGHT_PURPLE + "x" + ChatColor.DARK_GREEN + "," + ChatColor.LIGHT_PURPLE + "y" + ChatColor.DARK_GREEN + "," + ChatColor.LIGHT_PURPLE + "z" + ChatColor.DARK_GREEN + "," + ChatColor.DARK_AQUA + "world" + ChatColor.DARK_GRAY + "]", false, false, 0, -1), new Segment(8, "Enter the ID of a Style.", false, false, 0, -1), new Segment(9, "Enter the ID of a Layer.", false, false, 0, -1), new Segment(10, "Enter " + ChatColor.DARK_GREEN + "finish " + ChatColor.RESET + "to finish the Editor.;" + ChatColor.DARK_GRAY + " [----- " + ChatColor.GRAY + "OR " + ChatColor.DARK_GRAY + "-----];" + "Enter the ID of the " + ChatColor.DARK_GRAY + "<" + ChatColor.RESET + "Path " + ChatColor.DARK_GRAY + "| " + ChatColor.RESET + "Square" + ChatColor.DARK_GRAY + "> " + ChatColor.RESET + "you want;to " + ChatColor.DARK_GRAY + "<" + ChatColor.RESET + "add " + ChatColor.DARK_GRAY + "| " + ChatColor.RESET + "remove" + ChatColor.DARK_GRAY + ">" + ChatColor.RESET + ".", false, false, 0, -1), new Segment(11, "Enter the # of the Trackpoint you want to link to.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "nearest" + ChatColor.RESET + " to link to the nearest Trackpoint.", false, false, 10, -1)
  };

  public LinkEditor(CommandSender s, Link link) {
    this.S = s;
    this.link = link;
  }
  public int receive(Guide guide, Segment seg, String msg) {
    Link newLink;
    Style style;
    Layer layer;
    TrinaryAnswer answer;
    switch (seg.getID()) {

      case 0:
        if (msg.equalsIgnoreCase("ID")) {
          return 1;
        }
        if (msg.equalsIgnoreCase("Link Visibility")) {
          return 2;
        }
        if (msg.equalsIgnoreCase("Name")) {
          return 3;
        }
        if (msg.equalsIgnoreCase("LinkSynonym")) {
          return 4;
        }
        if (msg.equalsIgnoreCase("hide")) {
          return 5;
        }
        if (msg.equalsIgnoreCase("edging")) {
          return 6;
        }
        if (msg.equalsIgnoreCase("Location")) {
          return 7;
        }
        if (msg.equalsIgnoreCase("Style")) {
          return 8;
        }
        if (msg.equalsIgnoreCase("Layer")) {
          return 9;
        }
        if (msg.equalsIgnoreCase("Linked")) {
          return 10;
        }

        MsgSender.sErr(guide.getGuideSyn(), this.S, ChatColor.RED + msg + ChatColor.RESET + " cannot be edited.");
        return 0;

      case 1:
        if (((DynTrack) guide.getExecutor()).isUsed(msg)) {

          MsgSender.sErr(guide.getGuideSyn(), this.S, "The ID is already used.");
          return 1;
        }
        if (!Pattern.matches("\\w+", msg)) {

          MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.RED + msg + ChatColor.RESET + " contains an invalid character.");
          MsgSender.sWarn(guide.getGuideSyn(), guide.getCmdSender(), "IDs should only contain a-z, A-Z and 0-9.");
          return 1;
        }
        newLink = this.link.clone();
        newLink.setId(msg);

        try {
          newLink.save();
        } catch (JDOMException | java.io.IOException | java.sql.SQLException e) {

          MsgSender.sErr(guide.getGuideSyn(), this.S, "Could not update the Link " + ChatColor.DARK_AQUA + this.link.getId() + ChatColor.RESET + ".");
          MsgSender.sErr(guide.getGuideSyn(), this.S, e.getMessage());
          return -1;
        }
        if (this.link.delete(true) == DataActionResult.DELETED_FAILED) {
          MsgSender.sErr(guide.getGuideSyn(), this.S, "The old version of the Link could not be deleted.");
        }
        this.link = newLink;
        MsgSender.sInfo(guide.getGuideSyn(), this.S, "ID: " + ChatColor.DARK_AQUA + this.link.getId());
        MsgSender.sEmpty(guide.getGuideSyn(), this.S, ChatColor.GREEN);
        MsgSender.sInfo(guide.getGuideSyn(), this.S, "Saved " + ChatColor.DARK_AQUA + this.link.getId() + ChatColor.RESET + ".");
        return -2;
      case 2:
        answer = MethodPool.getAnswer(msg);
        if (answer == TrinaryAnswer.POSITIVE) {

          this.link.setHideName(false);
          MsgSender.sInfo(guide.getGuideSyn(), this.S, "The Name is now visible.");
          finish(guide.getCmdSender());
          return -2;
        }
        if (answer == TrinaryAnswer.NEGATIVE) {

          this.link.setHideName(true);
          MsgSender.sInfo(guide.getGuideSyn(), this.S, "The Name is now hidden.");
          finish(guide.getCmdSender());
          return -2;
        }

        MsgSender.sErr(guide.getGuideSyn(), this.S, "You have to type yes or no.");
        MsgSender.sWarn(guide.getGuideSyn(), this.S, "You typed: " + ChatColor.RED + msg);
        return 2;

      case 3:
        this.link.setName(msg);
        MsgSender.sInfo(guide.getGuideSyn(), this.S, "Name: " + ChatColor.AQUA + this.link.getName());
        finish(guide.getCmdSender());
        return -2;
      case 4:
        this.link.setLinkSyn(msg);
        MsgSender.sInfo(guide.getGuideSyn(), this.S, "\"Link\" replaced by " + ChatColor.AQUA + this.link.getLinkSyn());
        finish(guide.getCmdSender());
        return -2;
      case 5:
        answer = MethodPool.getAnswer(msg);
        if (answer == TrinaryAnswer.POSITIVE) {

          this.link.setHide(false);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "The connections are now visible.");
          finish(guide.getCmdSender());
          return -2;
        }
        if (answer == TrinaryAnswer.NEGATIVE) {

          this.link.setHide(true);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "The connections are now hidden.");
          finish(guide.getCmdSender());
          return -2;
        }

        MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "Your answer should be yes or no!");
        MsgSender.sWarn(guide.getGuideSyn(), guide.getCmdSender(), "You typed: " + ChatColor.RED + msg);
        return 5;

      case 6:
        answer = MethodPool.getAnswer(msg);
        if (answer == TrinaryAnswer.POSITIVE) {

          this.link.setEdging(true);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "The edging is now visible.");
          finish(guide.getCmdSender());
          return -2;
        }
        if (answer == TrinaryAnswer.NEGATIVE) {

          this.link.setEdging(false);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "The edging is now hidden.");
          finish(guide.getCmdSender());
          return -2;
        }

        MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "Your answer should be yes or no!");
        MsgSender.sWarn(guide.getGuideSyn(), guide.getCmdSender(), "You typed: " + ChatColor.RED + msg);
        return 6;

      case 7:
        if (msg.equalsIgnoreCase("here")) {

          if (this.S instanceof Player) {

            Location loc = ((Player) this.S).getLocation().clone();
            loc.setX(loc.getBlockX() + 0.5D);
            loc.setY(loc.getBlockY() + 0.5D);
            loc.setZ(loc.getBlockZ() + 0.5D);
            this.link.setLocation(loc);
            MsgSender.sInfo(guide.getGuideSyn(), this.S, "Location: " + MethodPool.toColorLoc(this.link.getLocation()));
          } else {

            MsgSender.sErr(guide.getGuideSyn(), this.S, "Only online players can use their current location for a Link.");
            return 7;
          }
        } else {
          double x, y, z;

          if (!Pattern.matches("\\d+.*\\d*,\\d+.*\\d*,\\d+.*\\d*,\\w+", msg) &&
                  !Pattern.matches("\\[\\d+.*\\d*,\\d+.*\\d*,\\d+.*\\d*,\\w+]", msg)) {

            MsgSender.sErr(guide.getGuideSyn(), this.S, ChatColor.RED + msg + ChatColor.RESET + " is not compatible.");
            MsgSender.sWarn(guide.getGuideSyn(), this.S, "The format is [x,y,z,world]");
            return 7;
          }
          msg = msg.replace("[", "").replace("]", "").replace(" ", "");
          String[] parts = msg.split(",");

          try {
            x = Double.parseDouble(parts[0]);
            y = Double.parseDouble(parts[1]);
            z = Double.parseDouble(parts[2]);
          } catch (NumberFormatException e) {

            MsgSender.sErr(guide.getGuideSyn(), this.S, ChatColor.RED + msg + ChatColor.RESET + " is not compatible.");
            MsgSender.sWarn(guide.getGuideSyn(), this.S, "The format is [x,y,z,world]");
            return 7;
          }

          World w = guide.getExecutor().getServer().getWorld(parts[3]);
          if (w == null) {

            MsgSender.sErr(guide.getGuideSyn(), this.S, "The world " + ChatColor.RED + msg + ChatColor.RESET + " was not found.");
            if (this.S instanceof Player) {
              MsgSender.sWarn(guide.getGuideSyn(), this.S, "Your current world is " + ChatColor.DARK_AQUA + ((Player) this.S).getWorld().getName());
            }
            return 7;
          }
          this.link.setLocation(new Location(w, (int) x + 0.5D, (int) y + 0.5D, (int) z + 0.5D));
          MsgSender.sInfo(guide.getGuideSyn(), this.S, "Location: " + MethodPool.toColorLoc(this.link.getLocation()));
        }
        finish(guide.getCmdSender());
        return -2;
      case 8:
        style = ((DynTrack) guide.getExecutor()).getStyle(msg);
        if (style != null) {

          this.link.setStyle(style);
          MsgSender.sInfo(guide.getGuideSyn(), this.S, "Style: " + ChatColor.DARK_AQUA + this.link.getStyle().getId());
          finish(guide.getCmdSender());
          return -2;
        }

        MsgSender.sErr(guide.getGuideSyn(), this.S, "Could not find the Style " + ChatColor.RED + msg + ChatColor.RESET + ".");
        return 8;

      case 9:
        layer = ((DynTrack) guide.getExecutor()).getLayer(msg);
        if (layer != null) {

          this.link.setLayer(layer);
          MsgSender.sInfo(guide.getGuideSyn(), this.S, "Layer: " + ChatColor.DARK_AQUA + this.link.getLayer().getId());
          finish(guide.getCmdSender());
          return -2;
        }

        MsgSender.sErr(guide.getGuideSyn(), this.S, "Could not find the Layer " + ChatColor.RED + msg + ChatColor.RESET + ".");
        return 9;

      case 10:
        if (msg.equalsIgnoreCase("finish")) {

          finish(guide.getCmdSender());
          return -2;
        }
        this.toAdd = ((DynTrack) guide.getExecutor()).getPath(msg);
        if (this.toAdd == null) {

          this.toAdd = ((DynTrack) guide.getExecutor()).getSquare(msg);
          if (this.toAdd == null) {

            MsgSender.sErr(guide.getExecutor(), guide.getCmdSender(), ChatColor.RED + msg + ChatColor.RESET + " is neither a Path nor a Square.");
            return 10;
          }
        }
        if (this.link.getTable().containsKey(this.toAdd)) {

          this.link.del(this.toAdd);
          MsgSender.sInfo(guide.getExecutor(), guide.getCmdSender(), "Unlinked " + ChatColor.DARK_AQUA + this.toAdd.getId() + ChatColor.RESET + ".");
          return 10;
        }
        return 11;

      case 11:
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
          MsgSender.sInfo(guide.getExecutor(), guide.getCmdSender(), "Linked " + ChatColor.DARK_AQUA + this.toAdd.getId() + ChatColor.RESET + ".");
          return 10;
        } catch (NumberFormatException e) {

          MsgSender.sErr(guide.getExecutor(), guide.getCmdSender(), ChatColor.RED + msg + ChatColor.RESET + " is not a valid number.");
          return 11;
        }
    }
    return -3;
  }

  public void finish(CommandSender s) {
    try {
      this.link.save();
      MsgSender.sInfo("Editor", this.S, "Saved " + ChatColor.DARK_AQUA + this.link.getId() + ChatColor.RESET + ".");
    } catch (JDOMException | java.io.IOException | java.sql.SQLException e) {

      MsgSender.sErr("Editor", this.S, "Could not save the Changes.");
      MsgSender.sErr("Editor", this.S, e.getMessage());
    }
  }

  public boolean usesId(String arg0) {
    return false;
  }
}