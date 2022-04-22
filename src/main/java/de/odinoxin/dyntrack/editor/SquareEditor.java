package de.odinoxin.dyntrack.editor;

import de.gcmclan.team.guides.Guide;
import de.gcmclan.team.guides.GuideReceiver;
import de.gcmclan.team.guides.Segment;
import de.odinoxin.dyntrack.DynTrack;
import de.odinoxin.dyntrack.Recorder;
import de.odinoxin.dyntrack.enums.DataActionResult;
import de.odinoxin.dyntrack.enums.TrinaryAnswer;
import de.odinoxin.dyntrack.generals.MethodPool;
import de.odinoxin.dyntrack.generals.MsgSender;
import de.odinoxin.dyntrack.layer.Layer;
import de.odinoxin.dyntrack.link.Link;
import de.odinoxin.dyntrack.square.Square;
import de.odinoxin.dyntrack.style.Style;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jdom2.JDOMException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Pattern;

public class SquareEditor implements GuideReceiver {
  private final CommandSender S;
  private Square sq;
  private Recorder.SPEED speed = Recorder.SPEED.BY_HAND;
  private int index = 0;
  public static final Segment[] SEGMENTS_CONSOLE = new Segment[] {
          new Segment(0, "What would you like to edit?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "You can edit:;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "ID;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Name Visibility;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Name;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Square Synonym;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Style;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Layer", false, false, -1, -1), new Segment(1, "Enter the new ID.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "ID's are unique.", false, false, 0, -1), new Segment(2, "Should the name of the square be visible?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 0, -1), new Segment(3, "Enter a new name.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Names are not unique.", false, true, 0, -1), new Segment(4, "Enter a word, that should replace the word;\"Square\" in the infobox.", false, true, 0, -1), new Segment(5, "Enter the ID of a Style.", false, false, 0, -1), new Segment(6, "Enter the ID of a Layer.", false, false, 0, -1)
  };

  public static final Segment[] SEGMENTS_PLAYER = new Segment[] {
          new Segment(0, "What would you like to edit?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "You can edit:;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "ID;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Name Visibility;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Name;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Square Synonym;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Style;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Layer;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Trackpoints", false, false, -1, -1), new Segment(1, "Enter the new ID.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "ID's are unique.", false, false, 0, -1), new Segment(2, "Should the name of the square be visible?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 0, -1), new Segment(3, "Enter a new name.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Names are not unique.", false, true, 0, -1), new Segment(4, "Enter a word, that should replace the word;\"Square\" in the infobox.", false, true, 0, -1), new Segment(5, "Enter the ID of a Style.", false, false, 0, -1), new Segment(6, "Enter the ID of a Layer.", false, false, 0, -1), new Segment(7, "Choose the tracking speeed:;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "-      " + ChatColor.RESET + "  Track by hand.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "Highest" + ChatColor.RESET + " As many Trackpoints as possible.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "Higher " + ChatColor.RESET + " 10 Trackpoints per second.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "High   " + ChatColor.RESET + "  5  Trackpoints per second.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "Normal " + ChatColor.RESET + " 1  Trackpoint per second.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "Slow   " + ChatColor.RESET + "  1  Trackpoint per 5 seconds.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "Slower " + ChatColor.RESET + " 1  Trackpoint per 10 seconds.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "Slowest" + ChatColor.RESET + " 1  Trackpoint per minute.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "Minecart" + ChatColor.RESET + " Track by Minecart", false, false, 0, -1), new Segment(8, "Enter the index of the Trackpoint, where you want;to modify the Square.", false, false, 7, -1), new Segment(9, "Do you want to add new Trackpoint " + ChatColor.DARK_GREEN + "before" + ChatColor.RESET + ";or " + ChatColor.DARK_GREEN + "after" + ChatColor.RESET + " the given index?", false, false, 8, -1), new Segment(10, "Would you also like to be able to track by hand?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 9, -1)
  };

  public SquareEditor(CommandSender s, Square sq) {
    this.S = s;
    this.sq = sq;
  }
  public int receive(Guide guide, Segment seg, String msg) {
    Square newSquare;
    HashSet < Link > toUpdate, allLinks;
    Iterator < Link > it;
    Link[] links;
    int i;
    Style style;
    Layer layer;
    TrinaryAnswer answer;
    switch (seg.getID()) {

      case 0:
        if (msg.equalsIgnoreCase("ID")) {
          return 1;
        }
        if (msg.equalsIgnoreCase("NameVisibility")) {
          return 2;
        }
        if (msg.equalsIgnoreCase("Name")) {
          return 3;
        }
        if (msg.equalsIgnoreCase("NameSynonym")) {
          return 4;
        }
        if (msg.equalsIgnoreCase("Style")) {
          return 5;
        }
        if (msg.equalsIgnoreCase("Layer")) {
          return 6;
        }
        if (msg.equalsIgnoreCase("Trackpoints")) {

          if (this.S instanceof Player) {
            return 7;
          }

          MsgSender.sInfo(guide.getGuideSyn(), this.S, "You have to be an online player, to edit the Trackpoints.");
          return 0;
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
        newSquare = this.sq.clone();
        newSquare.setId(msg);

        try {
          newSquare.save();
        } catch (JDOMException | java.io.IOException | java.sql.SQLException e) {

          MsgSender.sErr(guide.getGuideSyn(), this.S, "Could not update the Square " + ChatColor.DARK_AQUA + this.sq.getId() + ChatColor.RESET + ".");
          return -1;
        }
        toUpdate = new HashSet < > ();
        allLinks = new HashSet < > ();
        allLinks.addAll(((DynTrack) guide.getExecutor()).getLinks().values());
        it = allLinks.iterator();

        while (it.hasNext()) {

          Link current = it.next();
          if (!current.getTable().containsKey(this.sq)) {
            toUpdate.add(current);
          }
        }
        links = toUpdate.toArray(new Link[0]);
        for (i = 0; i < links.length; i++) {

          links[i].add(newSquare, links[i].getTable().get(this.sq).intValue());
          links[i].del(this.sq);

          try {
            links[i].save();
          } catch (Exception e) {

            MsgSender.sErr(guide.getGuideSyn(), this.S, "Could not save " + ChatColor.DARK_AQUA + links[i].getId() + ChatColor.RESET + ".");
            MsgSender.sErr(guide.getGuideSyn(), this.S, e.getMessage());
            MsgSender.sWarn(guide.getGuideSyn(), this.S, "The process will continue anyway.");
            MsgSender.sWarn(guide.getGuideSyn(), this.S, "The result could be, that the old version of the Style might be not deleted.");
          }
        }
        if (this.sq.delete(true) == DataActionResult.DELETED_FAILED) {
          MsgSender.sErr(guide.getGuideSyn(), this.S, "The old version of the Square could not be deleted.");
        }
        this.sq = newSquare;
        MsgSender.sInfo(guide.getGuideSyn(), this.S, "ID: " + ChatColor.DARK_AQUA + this.sq.getId());
        MsgSender.sEmpty(guide.getGuideSyn(), this.S, ChatColor.GREEN);
        MsgSender.sInfo(guide.getGuideSyn(), this.S, "Saved " + ChatColor.DARK_AQUA + this.sq.getId() + ChatColor.RESET + ".");
        return -2;
      case 2:
        answer = MethodPool.getAnswer(msg);
        if (answer == TrinaryAnswer.POSITIVE) {

          this.sq.setHideName(false);
          MsgSender.sInfo(guide.getGuideSyn(), this.S, "The Name is now visible.");
          finish(guide.getCmdSender());
          return -2;
        }
        if (answer == TrinaryAnswer.NEGATIVE) {

          this.sq.setHideName(true);
          MsgSender.sInfo(guide.getGuideSyn(), this.S, "The Name is now hidden.");
          finish(guide.getCmdSender());
          return -2;
        }

        MsgSender.sErr(guide.getGuideSyn(), this.S, "You have to type yes or no.");
        MsgSender.sWarn(guide.getGuideSyn(), this.S, "You typed: " + ChatColor.RED + msg);
        return 2;

      case 3:
        this.sq.setName(msg);
        MsgSender.sInfo(guide.getGuideSyn(), this.S, "Name: " + ChatColor.AQUA + this.sq.getName());
        finish(guide.getCmdSender());
        return -2;
      case 4:
        this.sq.setSquareSyn(msg);
        MsgSender.sInfo(guide.getGuideSyn(), this.S, "\"Square\" replaced by: " + ChatColor.AQUA + this.sq.getSquareSyn());
        finish(guide.getCmdSender());
        return -2;
      case 5:
        style = ((DynTrack) guide.getExecutor()).getStyle(msg);
        if (style != null) {

          this.sq.setStyle(style);
          MsgSender.sInfo(guide.getGuideSyn(), this.S, "Style: " + ChatColor.DARK_AQUA + this.sq.getStyle().getId());
          finish(guide.getCmdSender());
          return -2;
        }

        MsgSender.sErr(guide.getGuideSyn(), this.S, "Could not find the Style " + ChatColor.RED + msg + ChatColor.RESET + ".");
        return 5;

      case 6:
        layer = ((DynTrack) guide.getExecutor()).getLayer(msg);
        if (layer != null) {

          this.sq.setLayer(layer);
          MsgSender.sInfo(guide.getGuideSyn(), this.S, "Layer: " + ChatColor.DARK_AQUA + this.sq.getLayer().getId());
          finish(guide.getCmdSender());
          return -2;
        }

        MsgSender.sErr(guide.getGuideSyn(), this.S, "Could not find the Layer " + ChatColor.RED + msg + ChatColor.RESET + ".");
        return 6;

      case 7:
        switch (msg.toLowerCase()) {
          case "-":
            this.speed = Recorder.SPEED.BY_HAND;

            return 8;
          case "highest":
            this.speed = Recorder.SPEED.HIGHEST;
            return 8;
          case "higher":
            this.speed = Recorder.SPEED.HIGHER;
            return 8;
          case "high":
            this.speed = Recorder.SPEED.HIGH;
            return 8;
          case "normal":
            this.speed = Recorder.SPEED.NORMAL;
            return 8;
          case "slow":
            this.speed = Recorder.SPEED.SLOW;
            return 8;
          case "slower":
            this.speed = Recorder.SPEED.SLOWER;
            return 8;
          case "slowest":
            this.speed = Recorder.SPEED.SLOWEST;
            return 8;
          case "minecart":
            this.speed = Recorder.SPEED.MINECART;
            return 8;
        }
        MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.RED + msg + ChatColor.RESET + " cannot be used.");
        return 7;
      case 8:
        try {
          this.index = Integer.parseInt(msg);
          this.index--;
          if (this.index < 0) {

            this.index = 0;
          } else if (this.index >= this.sq.getLocationList().size()) {

            this.index = this.sq.getLocationList().size() - 1;
          }
          MsgSender.sInfo(guide.getGuideSyn(), this.S, "Index: " + ChatColor.LIGHT_PURPLE + (this.index + 1));
          return 9;
        } catch (NumberFormatException e) {

          MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.RED + msg + ChatColor.RESET + " is not a valid Number.");
          return 8;
        }
      case 9:
        if (msg.equalsIgnoreCase("before")) {

          MsgSender.sInfo(guide.getGuideSyn(), this.S, "Trackpoints will be added before the index.");
        } else if (msg.equalsIgnoreCase("after")) {

          this.index++;
          MsgSender.sInfo(guide.getGuideSyn(), this.S, "Trackpoints will be added after the index.");
        } else {

          MsgSender.sErr(guide.getGuideSyn(), this.S, "You have to type after or before.");
          MsgSender.sWarn(guide.getGuideSyn(), this.S, "You typed: " + ChatColor.RED + msg);
          return 9;
        }
        if (this.speed == Recorder.SPEED.BY_HAND || this.speed == Recorder.SPEED.MINECART) {

          finish(guide.getCmdSender());
          ((DynTrack) guide.getExecutor()).addRec(new Recorder((Player) guide.getCmdSender(), this.sq, this.index, this.speed));
          return -1;
        }
        return 10;
      case 10:
        answer = MethodPool.getAnswer(msg);
        if (answer == TrinaryAnswer.POSITIVE) {

          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "You are able to track by hand now.");
        } else if (answer == TrinaryAnswer.NEGATIVE) {

          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "You are not able to track by hand now.");
        } else {

          MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "Your answer should be yes or no!");
          MsgSender.sWarn(guide.getGuideSyn(), guide.getCmdSender(), "You typed: " + ChatColor.RED + msg);
          return 17;
        }
        finish(guide.getCmdSender());
        ((DynTrack) guide.getExecutor()).addRec(new Recorder((Player) guide.getCmdSender(), this.sq, this.index, this.speed, (answer == TrinaryAnswer.POSITIVE)));
        return -1;
    }
    return -3;
  }

  public void finish(CommandSender s) {
    try {
      this.sq.save();
      MsgSender.sInfo("Editor", this.S, "Saved " + ChatColor.DARK_AQUA + this.sq.getId() + ChatColor.RESET + ".");
    } catch (JDOMException | java.io.IOException | java.sql.SQLException e) {

      MsgSender.sErr("Editor", this.S, "Could not save the Changes.");
      MsgSender.sErr("Editor", this.S, e.getMessage());
    }
  }

  public boolean usesId(String arg0) {
    return false;
  }
}