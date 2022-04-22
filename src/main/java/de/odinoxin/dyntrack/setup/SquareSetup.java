package de.odinoxin.dyntrack.setup;

import de.gcmclan.team.guides.Guide;
import de.gcmclan.team.guides.GuideReceiver;
import de.gcmclan.team.guides.Segment;
import de.odinoxin.dyntrack.DynTrack;
import de.odinoxin.dyntrack.Recorder;
import de.odinoxin.dyntrack.enums.TrinaryAnswer;
import de.odinoxin.dyntrack.generals.MethodPool;
import de.odinoxin.dyntrack.generals.MsgSender;
import de.odinoxin.dyntrack.interfaces.Linkable;
import de.odinoxin.dyntrack.layer.Layer;
import de.odinoxin.dyntrack.square.Square;
import de.odinoxin.dyntrack.style.Style;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SquareSetup implements GuideReceiver {
  private final DynTrack DYNTRACK;
  private Square sq;
  private Recorder.SPEED speed;
  public static final Segment[] SEGMENTS = new Segment[] {
          new Segment(0, "Enter the ID.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "ID's are unique.", false, false, -1, -1), new Segment(1, "If you would like to copy the values form another;Square, then enter the ID of the Square to copy.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Otherwise...", false, false, 0, 2), new Segment(2, "Should the name of the Square be visible?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "You will define the name next.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 0, 5), new Segment(3, "Enter a name.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "The name will be displayed.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Names are not unique.", false, true, 2, 4), new Segment(4, "Enter a word, that should replace the word;\"Square\" in the infobox.", false, true, 3, 5), new Segment(5, "Enter the ID of a Style.", false, false, 3, -1), new Segment(6, "Enter the ID of a Layer.", false, false, 5, -1), new Segment(7, "Choose the tracking speeed:;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "-      " + ChatColor.RESET + "  Track by hand.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "Highest" + ChatColor.RESET + " As many Trackpoints as possible.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "Higher " + ChatColor.RESET + " 10 Trackpoints per second.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "High   " + ChatColor.RESET + "  5  Trackpoints per second.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "Normal " + ChatColor.RESET + " 1  Trackpoint per second.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "Slow   " + ChatColor.RESET + "  1  Trackpoint per 5 seconds.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "Slower " + ChatColor.RESET + " 1  Trackpoint per 10 seconds.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "Slowest" + ChatColor.RESET + " 1  Trackpoint per minute.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "Minecart" + ChatColor.RESET + " Track by Minecart.", false, false, 6, -1), new Segment(8, "Would you also like to be able to track by hand?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 7, -1)
  };

  public SquareSetup(DynTrack DYNTRACK) {
    this.DYNTRACK = DYNTRACK;
    this.sq = new Square(this.DYNTRACK);
  }
  public int receive(Guide guide, Segment seg, String msg) {
    Square square;
    Style st;
    Layer lay;
    TrinaryAnswer answer = TrinaryAnswer.UNKNOWN;
    switch (seg.getID()) {

      case 0:
        if (this.DYNTRACK.isUsed(msg) &&
                !this.sq.getId().equalsIgnoreCase(msg)) {

          MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "The ID is already used.");
          return 0;
        }

        if (Pattern.matches("\\w+", msg)) {

          this.sq.setId(msg);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "ID: " + ChatColor.DARK_AQUA + this.sq.getId());
          if (this.DYNTRACK.getSquares().size() <= 0) {
            return 2;
          }
          return 1;
        }

        MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.RED + msg + ChatColor.RESET + " contains an invalid character.");
        MsgSender.sWarn(guide.getGuideSyn(), guide.getCmdSender(), "ID's should only contain a-z, A-Z and 0-9.");
        return 0;

      case 1:
        square = this.DYNTRACK.getSquare(msg);
        if (square != null) {

          String id = this.sq.getId();
          this.sq = square.clone();
          this.sq.setId(id);
          this.sq.setLocationList(new ArrayList());
          return 5;
        }

        MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.RED + msg + ChatColor.RESET + " is not a Square.");
        return 1;

      case 2:
        answer = MethodPool.getAnswer(msg);
        if (answer == TrinaryAnswer.POSITIVE) {

          this.sq.setHideName(false);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "The Name is now visible.");
          return 3;
        }
        if (answer == TrinaryAnswer.NEGATIVE) {

          this.sq.setHideName(true);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "The Name is now hidden.");
          return 4;
        }

        MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "Your answer should be yes or no!");
        MsgSender.sWarn(guide.getGuideSyn(), guide.getCmdSender(), "You typed: " + ChatColor.RED + msg);
        return 2;

      case 3:
        if (this.sq.getId().equals(msg)) {

          this.sq.setName("");
        } else {

          this.sq.setName(msg);
        }
        MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "Name: " + ChatColor.AQUA + this.sq.getName());
        return 4;
      case 4:
        this.sq.setSquareSyn(msg);
        MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "\"Square\" replaced by: " + ChatColor.AQUA + this.sq.getSquareSyn());
        return 5;
      case 5:
        st = this.DYNTRACK.getStyle(msg);
        if (st == null) {

          MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "Could not find the Style " + ChatColor.RED + msg + ChatColor.RESET + ".");
          return 5;
        }
        this.sq.setStyle(st);
        MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "Style: " + ChatColor.DARK_AQUA + this.sq.getStyle().getId());
        return 6;
      case 6:
        lay = this.DYNTRACK.getLayer(msg);
        if (lay == null) {

          MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "Could not find the Layer " + ChatColor.RED + msg + ChatColor.RESET + ".");
          return 6;
        }
        this.sq.setLayer(lay);
        MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "Layer: " + ChatColor.DARK_AQUA + this.sq.getLayer().getId());
        return 7;
      case 7:
        switch (msg.toLowerCase()) {
          case "-":
            this.speed = Recorder.SPEED.BY_HAND;
            break;
          case "highest":
            this.speed = Recorder.SPEED.HIGHEST;
            break;
          case "higher":
            this.speed = Recorder.SPEED.HIGHER;
            break;
          case "high":
            this.speed = Recorder.SPEED.HIGH;
            break;
          case "normal":
            this.speed = Recorder.SPEED.NORMAL;
            break;
          case "slow":
            this.speed = Recorder.SPEED.SLOW;
            break;
          case "slower":
            this.speed = Recorder.SPEED.SLOWER;
            break;
          case "slowest":
            this.speed = Recorder.SPEED.SLOWEST;
            break;
          case "minecart":
            this.speed = Recorder.SPEED.MINECART;
            break;
          default:
            MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.RED + msg + ChatColor.RESET + " cannot be used.");
            return 7;
        }
        if (this.speed == Recorder.SPEED.BY_HAND || this.speed == Recorder.SPEED.MINECART) {

          finish(guide.getCmdSender());
          this.DYNTRACK.addRec(new Recorder((Player) guide.getCmdSender(), this.sq, this.speed));
          return -1;
        }
        return 8;
      case 8:
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
        this.DYNTRACK.addRec(new Recorder((Player) guide.getCmdSender(), this.sq, this.speed, (answer == TrinaryAnswer.POSITIVE)));
        return -1;
    }
    return -3;
  }

  public void finish(CommandSender s) {
    MsgSender.sEmpty("Setup", s, ChatColor.GREEN);
    MsgSender.sInfo("Setup", s, "You finished the Setup!");
  }

  public boolean usesId(String id) {
    return this.sq.getId().equalsIgnoreCase(id);
  }
}