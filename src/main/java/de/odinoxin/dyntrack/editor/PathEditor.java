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
import de.odinoxin.dyntrack.interfaces.Linkable;
import de.odinoxin.dyntrack.layer.Layer;
import de.odinoxin.dyntrack.link.Link;
import de.odinoxin.dyntrack.path.Path;
import de.odinoxin.dyntrack.style.Style;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jdom2.JDOMException;

public class PathEditor implements GuideReceiver {
  private final CommandSender S;
  private Path p;
  private Recorder.SPEED speed = Recorder.SPEED.BY_HAND;
  private int index = 0;
  public static final Segment[] SEGMENTS_CONSOLE = new Segment[] {
          new Segment(0, "What would you like to edit?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "You can edit:;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "ID;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Name Visibility;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Name;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Path Synonym;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "From Visibility;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "From;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "From Synonym;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "To Visibility;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "To;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "To Synonym;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Via-List Visibility;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Via Synonym;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Endpoint Connection;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Style;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Layer", false, false, -1, -1), new Segment(1, "Enter the new ID.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "ID's are unique.", false, false, 0, -1), new Segment(2, "Should the name of the path be visible?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 0, -1), new Segment(3, "Enter a new name.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Names are not unique.", false, true, 0, -1), new Segment(4, "Enter a new word, that should replace the word;\"Path\" in the infobox.", false, true, 0, -1), new Segment(5, "Should it be visible, where the path comes from?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 0, -1), new Segment(6, "Wherer does the path comes from?", false, true, 0, -1), new Segment(7, "Enter a new word, that should replace the word;\"From\" in the infobox.", false, true, 0, -1), new Segment(8, "Should it be visible, where the path goes to?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 0, -1), new Segment(9, "Wherer does the path goes to?", false, true, 0, -1), new Segment(10, "Enter a new word, that should replace the word;\"To\" in the infobox.", false, true, 0, -1), new Segment(11, "Should the endpoints be connected?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "The endpoint will be connected with the;" + ChatColor.GRAY + "   startpoint directly.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 0, -1), new Segment(12, "Enter a new word, that should replace the word \"Via\";in the infobox.", false, true, 0, -1), new Segment(13, "Should the Via-List be visible?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "A list in the infobox, which shows in which order;" + ChatColor.GRAY + "   the path is connected with links, paths and;" + ChatColor.GRAY + "   squares.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 0, -1), new Segment(14, "Enter the ID of a Style.", false, false, 0, -1), new Segment(15, "Enter the ID of a Layer.", false, false, 0, -1)
  };

  public static final Segment[] SEGMENTS_PLAYER = new Segment[] {
          new Segment(0, "What would you like to edit?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "You can edit:;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "ID;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Name Visibility;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Name;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Path Synonym;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "From Visibility;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "From;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "From Synonym;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "To Visibility;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "To;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "To Synonym;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Via-List Visibility;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Via Synonym;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Endpoint Connection;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Style;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Layer;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Trackpoints", false, false, -1, -1), new Segment(1, "Enter the new ID.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "ID's are unique.", false, false, 0, -1), new Segment(2, "Should the name of the path be visible?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 0, -1), new Segment(3, "Enter a new name.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Names are not unique.", false, true, 0, -1), new Segment(4, "Enter a new word, that should replace the word;\"Path\" in the infobox.", false, true, 0, -1), new Segment(5, "Should it be visible, where the path comes from?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 0, -1), new Segment(6, "Wherer does the path comes from?", false, true, 0, -1), new Segment(7, "Enter a new word, that should replace the word;\"From\" in the infobox.", false, true, 0, -1), new Segment(8, "Should it be visible, where the path goes to?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 0, -1), new Segment(9, "Wherer does the path goes to?", false, true, 0, -1), new Segment(10, "Enter a new word, that should replace the word;\"To\" in the infobox.", false, true, 0, -1), new Segment(11, "Should the endpoints be connected?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "The endpoint will be connected with the;" + ChatColor.GRAY + "   startpoint directly.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 0, -1), new Segment(12, "Enter a new word, that should replace the word \"Via\";in the infobox.", false, true, 0, -1), new Segment(13, "Should the Via-List be visible?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "A list in the infobox, which shows in which order;" + ChatColor.GRAY + "   the path is connected with links, paths and;" + ChatColor.GRAY + "   squares.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 0, -1), new Segment(14, "Enter the ID of a Style.", false, false, 0, -1), new Segment(15, "Enter the ID of a Layer.", false, false, 0, -1), new Segment(16, "Choose the tracking speeed:;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "-      " + ChatColor.RESET + "  Track by hand.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "Highest" + ChatColor.RESET + " As many Trackpoints as possible.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "Higher " + ChatColor.RESET + " 10 Trackpoints per second.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "High   " + ChatColor.RESET + "  5  Trackpoints per second.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "Normal " + ChatColor.RESET + " 1  Trackpoint per second.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "Slow   " + ChatColor.RESET + "  1  Trackpoint per 5 seconds.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "Slower " + ChatColor.RESET + " 1  Trackpoint per 10 seconds.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "Slowest" + ChatColor.RESET + " 1  Trackpoint per minute.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "Minecart" + ChatColor.RESET + " Track by Minecart.", false, false, 0, -1), new Segment(17, "Enter the index of the Trackpoint, where you want;to modify the Path.", false, false, 16, -1), new Segment(18, "Do you want to add new Trackpoints " + ChatColor.DARK_GREEN + "before" + ChatColor.RESET + ";or " + ChatColor.DARK_GREEN + "after" + ChatColor.RESET + " the given index?", false, false, 17, -1), new Segment(19, "Would you also like to be able to track by hand?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 18, -1)
  };

  public PathEditor(CommandSender s, Path p) {
    this.S = s;
    this.p = p;
  }
  public int receive(Guide guide, Segment seg, String msg) {
    Path newPath;
    HashSet < Link > toUpdate, allLinks;
    Iterator < Link > it;
    Link[] links;
    int i;
    Style style;
    Layer layer;
    TrinaryAnswer answer = TrinaryAnswer.UNKNOWN;
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
        if (msg.equalsIgnoreCase("PathSynonym")) {
          return 4;
        }
        if (msg.equalsIgnoreCase("FromVisibility")) {
          return 5;
        }
        if (msg.equalsIgnoreCase("From")) {
          return 6;
        }
        if (msg.equalsIgnoreCase("FromSynonym")) {
          return 7;
        }
        if (msg.equalsIgnoreCase("ToVisibility")) {
          return 8;
        }
        if (msg.equalsIgnoreCase("To")) {
          return 9;
        }
        if (msg.equalsIgnoreCase("ToSynonym")) {
          return 10;
        }
        if (msg.equalsIgnoreCase("Via-ListVisibility")) {
          return 11;
        }
        if (msg.equalsIgnoreCase("ViaSynonym")) {
          return 12;
        }
        if (msg.equalsIgnoreCase("EndpointConnection")) {
          return 13;
        }
        if (msg.equalsIgnoreCase("Style")) {
          return 14;
        }
        if (msg.equalsIgnoreCase("Layer")) {
          return 15;
        }
        if (msg.equalsIgnoreCase("Trackpoints")) {

          if (this.S instanceof Player) {
            return 16;
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
        newPath = this.p.clone();
        newPath.setId(msg);

        try {
          newPath.save();
        } catch (JDOMException | java.io.IOException | java.sql.SQLException e) {

          MsgSender.sErr(guide.getGuideSyn(), this.S, "Could not update the Path " + ChatColor.DARK_AQUA + this.p.getId() + ChatColor.RESET + ".");
          MsgSender.sErr(guide.getGuideSyn(), this.S, e.getMessage());
          return -1;
        }
        toUpdate = new HashSet < > ();
        allLinks = new HashSet < > ();
        allLinks.addAll(((DynTrack) guide.getExecutor()).getLinks().values());
        it = allLinks.iterator();

        while (it.hasNext()) {

          Link current = it.next();
          if (current.getTable().containsKey(this.p)) {
            toUpdate.add(current);
          }
        }
        links = (Link[]) toUpdate.toArray((Object[]) new Link[0]);
        for (i = 0; i < links.length; i++) {

          links[i].add(newPath, links[i].getTable().get(this.p).intValue());
          links[i].del(this.p);

          try {
            links[i].save();
          } catch (Exception e) {

            MsgSender.sErr(guide.getGuideSyn(), this.S, "Could not save " + ChatColor.DARK_AQUA + links[i].getId() + ChatColor.RESET + ".");
            MsgSender.sErr(guide.getGuideSyn(), this.S, e.getMessage());
            MsgSender.sWarn(guide.getGuideSyn(), this.S, "The process will continue anyway.");
            MsgSender.sWarn(guide.getGuideSyn(), this.S, "The result could be, that the old version of the Style might be not deleted.");
          }
        }
        if (this.p.delete(true) == DataActionResult.DELETED_FAILED) {
          MsgSender.sErr(guide.getGuideSyn(), this.S, "The old version of the Path could not be deleted.");
        }
        this.p = newPath;
        MsgSender.sInfo(guide.getGuideSyn(), this.S, "ID: " + ChatColor.DARK_AQUA + this.p.getId());
        MsgSender.sEmpty(guide.getGuideSyn(), this.S, ChatColor.GREEN);
        MsgSender.sInfo(guide.getGuideSyn(), this.S, "Saved " + ChatColor.DARK_AQUA + this.p.getId() + ChatColor.RESET + ".");
        return -2;
      case 2:
        answer = MethodPool.getAnswer(msg);
        if (answer == TrinaryAnswer.POSITIVE) {

          this.p.setHideName(false);
          MsgSender.sInfo(guide.getGuideSyn(), this.S, "The Name is now visible.");
          finish(guide.getCmdSender());
          return -2;
        }
        if (answer == TrinaryAnswer.NEGATIVE) {

          this.p.setHideName(true);
          MsgSender.sInfo(guide.getGuideSyn(), this.S, "The Name is now hidden.");
          finish(guide.getCmdSender());
          return -2;
        }

        MsgSender.sErr(guide.getGuideSyn(), this.S, "You have to type yes or no.");
        MsgSender.sWarn(guide.getGuideSyn(), this.S, "You typed: " + ChatColor.RED + msg);
        return 2;

      case 3:
        this.p.setName(msg);
        MsgSender.sInfo(guide.getGuideSyn(), this.S, "Name: " + ChatColor.AQUA + this.p.getName());
        finish(guide.getCmdSender());
        return -2;
      case 4:
        this.p.setPathSyn(msg);
        MsgSender.sInfo(guide.getGuideSyn(), this.S, "\"Path\" replaced by: " + ChatColor.AQUA + this.p.getPathSyn());
        finish(guide.getCmdSender());
        return -2;
      case 5:
        answer = MethodPool.getAnswer(msg);
        if (answer == TrinaryAnswer.POSITIVE) {

          this.p.setHideFrom(false);
          MsgSender.sInfo(guide.getGuideSyn(), this.S, "\"From\" is now visible.");
          finish(guide.getCmdSender());
          return -2;
        }
        if (answer == TrinaryAnswer.NEGATIVE) {

          this.p.setHideFrom(true);
          MsgSender.sInfo(guide.getGuideSyn(), this.S, "\"From\" is now hidden.");
          finish(guide.getCmdSender());
          return -2;
        }

        MsgSender.sErr(guide.getGuideSyn(), this.S, "You have to type yes or no.");
        MsgSender.sWarn(guide.getGuideSyn(), this.S, "You typed: " + ChatColor.RED + msg);
        return 5;

      case 6:
        this.p.setFromVal(msg);
        MsgSender.sInfo(guide.getGuideSyn(), this.S, "From: " + ChatColor.AQUA + this.p.getFromVal());
        finish(guide.getCmdSender());
        return -2;
      case 7:
        this.p.setFromSyn(msg);
        MsgSender.sInfo(guide.getGuideSyn(), this.S, "\"From\" replaced by: " + ChatColor.AQUA + this.p.getFromSyn());
        finish(guide.getCmdSender());
        return -2;
      case 8:
        answer = MethodPool.getAnswer(msg);
        if (answer == TrinaryAnswer.POSITIVE) {

          this.p.setHideTo(false);
          MsgSender.sInfo(guide.getGuideSyn(), this.S, "\"To\" is now visible.");
          finish(guide.getCmdSender());
          return -2;
        }
        if (answer == TrinaryAnswer.NEGATIVE) {

          this.p.setHideTo(true);
          MsgSender.sInfo(guide.getGuideSyn(), this.S, "\"To\" is now hidden.");
          finish(guide.getCmdSender());
          return -2;
        }

        MsgSender.sErr(guide.getGuideSyn(), this.S, "You have to type yes or no.");
        MsgSender.sWarn(guide.getGuideSyn(), this.S, "You typed: " + ChatColor.RED + msg);
        return 8;

      case 9:
        this.p.setToVal(msg);
        MsgSender.sInfo(guide.getGuideSyn(), this.S, "To: " + ChatColor.AQUA + this.p.getToVal());
        finish(guide.getCmdSender());
        return -2;
      case 10:
        this.p.setToSyn(msg);
        MsgSender.sInfo(guide.getGuideSyn(), this.S, "\"To\" replaced by: " + ChatColor.AQUA + this.p.getToSyn());
        finish(guide.getCmdSender());
        return -2;
      case 11:
        answer = MethodPool.getAnswer(msg);
        if (answer == TrinaryAnswer.POSITIVE) {

          this.p.setHideVia(true);
          MsgSender.sInfo(guide.getGuideSyn(), this.S, "It is now visible, which Links are connected.");
          finish(guide.getCmdSender());
          return -2;
        }
        if (answer == TrinaryAnswer.NEGATIVE) {

          this.p.setHideVia(false);
          MsgSender.sInfo(guide.getGuideSyn(), this.S, "It is now hidden, which Links are connected.");
          finish(guide.getCmdSender());
          return -2;
        }

        MsgSender.sErr(guide.getGuideSyn(), this.S, "You have to type yes or no.");
        MsgSender.sWarn(guide.getGuideSyn(), this.S, "You typed: " + ChatColor.RED + msg);
        return 11;

      case 12:
        this.p.setViaSyn(msg);
        MsgSender.sInfo(guide.getGuideSyn(), this.S, "\"Via\" replaced by: " + ChatColor.AQUA + this.p.getViaSyn());
        finish(guide.getCmdSender());
        return -2;
      case 13:
        answer = MethodPool.getAnswer(msg);
        if (answer == TrinaryAnswer.POSITIVE) {

          this.p.setConnected(true);
          MsgSender.sInfo(guide.getGuideSyn(), this.S, "Endpoints connected.");
          finish(guide.getCmdSender());
          return -2;
        }
        if (answer == TrinaryAnswer.NEGATIVE) {

          this.p.setConnected(false);
          MsgSender.sInfo(guide.getGuideSyn(), this.S, "Endpoints disconnected.");
          finish(guide.getCmdSender());
          return -2;
        }

        MsgSender.sErr(guide.getGuideSyn(), this.S, "You have to type yes or no.");
        MsgSender.sWarn(guide.getGuideSyn(), this.S, "You typed: " + ChatColor.RED + msg);
        return 13;

      case 14:
        style = ((DynTrack) guide.getExecutor()).getStyle(msg);
        if (style != null) {

          this.p.setStyle(style);
          MsgSender.sInfo(guide.getGuideSyn(), this.S, "Style: " + ChatColor.DARK_AQUA + this.p.getStyle().getId());
          finish(guide.getCmdSender());
          return -2;
        }

        MsgSender.sErr(guide.getGuideSyn(), this.S, "Could not find the Style " + ChatColor.RED + msg + ChatColor.RESET + ".");
        return 14;

      case 15:
        layer = ((DynTrack) guide.getExecutor()).getLayer(msg);
        if (layer != null) {

          this.p.setLayer(layer);
          MsgSender.sInfo(guide.getGuideSyn(), this.S, "Layer: " + ChatColor.DARK_AQUA + this.p.getLayer().getId());
          finish(guide.getCmdSender());
          return -2;
        }

        MsgSender.sErr(guide.getGuideSyn(), this.S, "Could not find the Layer " + ChatColor.RED + msg + ChatColor.RESET + ".");
        return 15;

      case 16:
        switch (msg.toLowerCase()) {
          case "-":
            this.speed = Recorder.SPEED.BY_HAND;

            return 17;
          case "highest":
            this.speed = Recorder.SPEED.HIGHEST;
            return 17;
          case "higher":
            this.speed = Recorder.SPEED.HIGHER;
            return 17;
          case "high":
            this.speed = Recorder.SPEED.HIGH;
            return 17;
          case "normal":
            this.speed = Recorder.SPEED.NORMAL;
            return 17;
          case "slow":
            this.speed = Recorder.SPEED.SLOW;
            return 17;
          case "slower":
            this.speed = Recorder.SPEED.SLOWER;
            return 17;
          case "slowest":
            this.speed = Recorder.SPEED.SLOWEST;
            return 17;
          case "minecart":
            this.speed = Recorder.SPEED.MINECART;
            return 17;
        }
        MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.RED + msg + ChatColor.RESET + " cannot be used.");
        return 16;
      case 17:
        try {
          this.index = Integer.parseInt(msg);
          this.index--;
          if (this.index < 0) {

            this.index = 0;
          } else if (this.index >= this.p.getLocationList().size()) {

            this.index = this.p.getLocationList().size() - 1;
          }
          MsgSender.sInfo(guide.getGuideSyn(), this.S, "Index: " + ChatColor.LIGHT_PURPLE + (this.index + 1));
          return 18;
        } catch (NumberFormatException e) {

          MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.RED + msg + ChatColor.RESET + " is not a valid Number.");
          return 17;
        }
      case 18:
        if (msg.equalsIgnoreCase("before")) {

          MsgSender.sInfo(guide.getGuideSyn(), this.S, "Trackpoints will be added before the index.");
        } else if (msg.equalsIgnoreCase("after")) {

          this.index++;
          MsgSender.sInfo(guide.getGuideSyn(), this.S, "Trackpoints will be added after the index.");
        } else {

          MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "Your answer should be after or before.");
          MsgSender.sWarn(guide.getGuideSyn(), guide.getCmdSender(), "You typed: " + ChatColor.RED + msg);
          return 18;
        }

        if (this.speed == Recorder.SPEED.BY_HAND || this.speed == Recorder.SPEED.MINECART) {

          finish(guide.getCmdSender());
          ((DynTrack) guide.getExecutor()).addRec(new Recorder((Player) guide.getCmdSender(), this.p, this.index, this.speed));
          return -1;
        }
        return 19;
      case 19:
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
        ((DynTrack) guide.getExecutor()).addRec(new Recorder((Player) guide.getCmdSender(), this.p, this.index, this.speed, (answer == TrinaryAnswer.POSITIVE)));
        return -1;
    }
    return -3;
  }

  public void finish(CommandSender s) {
    try {
      this.p.save();
      MsgSender.sInfo("Editor", this.S, "Saved " + ChatColor.DARK_AQUA + this.p.getId() + ChatColor.RESET + ".");
    } catch (JDOMException | java.io.IOException | java.sql.SQLException e) {

      MsgSender.sErr("Editor", this.S, "Could not save the Changes.");
      MsgSender.sErr("Editor", this.S, e.getMessage());
    }
  }

  public boolean usesId(String arg0) {
    return false;
  }
}