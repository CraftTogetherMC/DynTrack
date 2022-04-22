package de.odinoxin.dyntrack.setup;

import de.gcmclan.team.guides.Guide;
import de.gcmclan.team.guides.GuideManager;
import de.gcmclan.team.guides.GuideReceiver;
import de.gcmclan.team.guides.Segment;
import de.odinoxin.dyntrack.DynTrack;
import de.odinoxin.dyntrack.Recorder;
import de.odinoxin.dyntrack.enums.TrinaryAnswer;
import de.odinoxin.dyntrack.generals.MethodPool;
import de.odinoxin.dyntrack.generals.MsgSender;
import de.odinoxin.dyntrack.interfaces.Linkable;
import de.odinoxin.dyntrack.layer.Layer;
import de.odinoxin.dyntrack.path.Path;
import de.odinoxin.dyntrack.style.Style;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PathSetup implements GuideReceiver {
  private final DynTrack DYNTRACK;
  private Path p;
  private Recorder.SPEED speed = Recorder.SPEED.BY_HAND;

  public static final Segment[] SEGMENTS = new Segment[] {
          new Segment(0, "Enter the ID.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "ID's are unique.", false, false, -1, -1), new Segment(1, "If you would like to copy the values form another;Path, then enter the ID of the Path to copy.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Otherwise ...", false, false, 0, 2), new Segment(2, "Should the name of the Path be visible?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "You will define the name next.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 0, 5), new Segment(3, "Enter a name.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Names are not unique.", false, true, 2, 4), new Segment(4, "Enter a word, that should replace the word \"Path\";in the infobox.", false, true, 3, 5), new Segment(5, "Should it be visible, where the Path comes from?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "You will define, where the path comes from next.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 2, 8), new Segment(6, "Wherer does the Path comes from?", false, true, 5, 7), new Segment(7, "Enter a word, that should replace the word \"From\";in the infobox.", false, true, 6, 8), new Segment(8, "Should it be visible, where the Path goes to?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "You will define, where the path goes to next.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 5, 11), new Segment(9, "Wherer does the Path goes to?", false, true, 8, 10), new Segment(10, "Enter a word, that should replace the word \"To\";in the infobox.", false, true, 9, 11), new Segment(11, "Should the Via-List be visible?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "A list in the infobox, which shows in which order;" + ChatColor.GRAY + "   the Path is connected with Links, Paths and;" + ChatColor.GRAY + "   Squares.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 8, 13), new Segment(12, "Enter a word, that should replace the word \"Via\";in the infobox.", false, true, 11, 13), new Segment(13, "Should the endpoints be connected?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "The endpoint will be connected with the;" + ChatColor.GRAY + "   startpoint directly.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 11, 14), new Segment(14, "Enter the ID of a Style.", false, false, 13, -1), new Segment(15, "Enter the ID of a Layer.", false, false, 14, -1), new Segment(16, "Choose the tracking speeed:;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "-      " + ChatColor.RESET + "  Track by hand only.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "Highest" + ChatColor.RESET + " As many Trackpoints as possible.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "Higher " + ChatColor.RESET + " 10 Trackpoints per second.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "High   " + ChatColor.RESET + "  5  Trackpoints per second.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "Normal " + ChatColor.RESET + " 1  Trackpoint per second.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "Slow   " + ChatColor.RESET + "  1  Trackpoint per 5 seconds.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "Slower " + ChatColor.RESET + " 1  Trackpoint per 10 seconds.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "Slowest" + ChatColor.RESET + " 1  Trackpoint per minute.;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "Minecart" + ChatColor.RESET + " Track by Minecart.", false, false, 15, -1), new Segment(17, "Would you also like to be able to track by hand?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 16, -1)
  };

  public PathSetup(DynTrack DYNTRACK) {
    this.DYNTRACK = DYNTRACK;
    this.p = new Path(this.DYNTRACK);
  }
  public int receive(Guide guide, Segment seg, String msg) {
    Path path;
    Style st;
    Layer lay;
    TrinaryAnswer answer = TrinaryAnswer.UNKNOWN;
    switch (seg.getID()) {

      case 0:
        if (this.DYNTRACK.isUsed(msg) &&
                !this.p.getId().equalsIgnoreCase(msg)) {

          MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "The ID is already used.");
          return 0;
        }

        if (Pattern.matches("\\w+", msg)) {

          this.p.setId(msg);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "ID: " + ChatColor.DARK_AQUA + this.p.getId());
          if (this.DYNTRACK.getPaths().size() <= 0) {
            return 2;
          }
          return 1;
        }

        MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.RED + msg + ChatColor.RESET + " contains an invalid character.");
        MsgSender.sWarn(guide.getGuideSyn(), guide.getCmdSender(), "ID's should only contain a-z, A-Z and 0-9.");
        return 0;

      case 1:
        path = this.DYNTRACK.getPath(msg);
        if (path != null) {

          String id = this.p.getId();
          this.p = path.clone();
          this.p.setId(id);
          this.p.setLocationList(new ArrayList());
          return 14;
        }

        MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.RED + msg + ChatColor.RESET + " is not a Path.");
        return 1;

      case 2:
        answer = MethodPool.getAnswer(msg);
        if (answer == TrinaryAnswer.POSITIVE) {

          this.p.setHideName(false);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "The Name is now visible.");
          return 3;
        }
        if (answer == TrinaryAnswer.NEGATIVE) {

          this.p.setHideName(true);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "The Name is now hidden.");
          return 5;
        }

        MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "Your answer should be yes or no!");
        MsgSender.sWarn(guide.getGuideSyn(), guide.getCmdSender(), "You typed: " + ChatColor.RED + msg);
        return 2;

      case 3:
        if (this.p.getId().equals(msg)) {

          this.p.setName("");
        } else {

          this.p.setName(msg);
        }
        MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "Name: " + ChatColor.AQUA + this.p.getName());
        return 4;
      case 4:
        this.p.setPathSyn(msg);
        MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "\"Path\" replaced by: " + ChatColor.AQUA + this.p.getPathSyn());
        return 5;
      case 5:
        answer = MethodPool.getAnswer(msg);
        if (answer == TrinaryAnswer.POSITIVE) {

          this.p.setHideFrom(false);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "It is now visible, where the Path comes from.");
          return 6;
        }
        if (answer == TrinaryAnswer.NEGATIVE) {

          this.p.setHideFrom(true);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "It is now hidden, where the Path comes from.");
          return 8;
        }

        MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "Your answer should be yes or no!");
        MsgSender.sWarn(guide.getGuideSyn(), guide.getCmdSender(), "You typed: " + ChatColor.RED + msg);
        return 5;

      case 6:
        this.p.setFromVal(msg);
        MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "From: " + ChatColor.AQUA + this.p.getFromVal());
        return 7;
      case 7:
        this.p.setFromSyn(msg);
        MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "\"From\" replaced by: " + ChatColor.AQUA + this.p.getFromSyn());
        return 8;
      case 8:
        answer = MethodPool.getAnswer(msg);
        if (answer == TrinaryAnswer.POSITIVE) {

          this.p.setHideTo(false);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "It is now visible, where the Path goes to.");
          return 9;
        }
        if (answer == TrinaryAnswer.NEGATIVE) {

          this.p.setHideTo(true);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "It is now hidden, where the Path goes to.");
          return 11;
        }

        MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "Your answer should be yes or no!");
        MsgSender.sWarn(guide.getGuideSyn(), guide.getCmdSender(), "You typed: " + ChatColor.RED + msg);
        return 8;

      case 9:
        this.p.setToVal(msg);
        MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "To: " + ChatColor.AQUA + this.p.getToVal());
        return 10;
      case 10:
        this.p.setToSyn(msg);
        MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "\"To\" replaced by: " + ChatColor.AQUA + this.p.getToSyn());
        return 11;
      case 11:
        answer = MethodPool.getAnswer(msg);
        if (answer == TrinaryAnswer.POSITIVE) {

          this.p.setHideVia(false);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "It is now visible, which Links are connected.");
          return 12;
        }
        if (answer == TrinaryAnswer.NEGATIVE) {

          this.p.setHideVia(true);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "It is now hidden, which Links are connected.");
          return 13;
        }

        MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "Your answer should be yes or no!");
        MsgSender.sWarn(guide.getGuideSyn(), guide.getCmdSender(), "You typed: " + ChatColor.RED + msg);
        return 11;

      case 12:
        this.p.setViaSyn(msg);
        MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "\"Via\" replaced by: " + ChatColor.AQUA + this.p.getViaSyn());
        return 13;
      case 13:
        answer = MethodPool.getAnswer(msg);
        if (answer == TrinaryAnswer.POSITIVE) {

          this.p.setConnected(true);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "Endpoints connected.");
        } else if (answer == TrinaryAnswer.NEGATIVE) {

          this.p.setConnected(false);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "Endpoints disconnected.");
        } else {

          MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "Your answer should be yes or no!");
          MsgSender.sWarn(guide.getGuideSyn(), guide.getCmdSender(), "You typed: " + ChatColor.RED + msg);
          return 13;
        }
        if (this.DYNTRACK.getStyles().size() <= 0) {

          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.GRAY + "You will now create a Style first.");
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.GRAY + "The settings for the Path will not get lost.");
          this.DYNTRACK.getGuidesPlugin().addGuide(new Guide(this.DYNTRACK.getGuidesPlugin(), new StyleSetup(this.DYNTRACK), this.DYNTRACK, guide.getGuideSyn(), guide.getCmdSender(), StyleSetup.SEGMENTS), true);
        }
        return 14;
      case 14:
        st = this.DYNTRACK.getStyle(msg);
        if (st == null) {

          MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "Could not find the Style " + ChatColor.RED + msg + ChatColor.RESET + ".");
          return 14;
        }
        this.p.setStyle(st);
        MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "Style: " + ChatColor.DARK_AQUA + this.p.getStyle().getId());
        if (this.DYNTRACK.getLayers().size() <= 0) {

          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.GRAY + "You will now create a Layer first.");
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.GRAY + "The settings for the Path will not get lost.");
          this.DYNTRACK.getGuidesPlugin().addGuide(new Guide(this.DYNTRACK.getGuidesPlugin(), new LayerSetup(this.DYNTRACK), this.DYNTRACK, guide.getGuideSyn(), guide.getCmdSender(), LayerSetup.SEGMENTS), true);
        }
        return 15;
      case 15:
        lay = this.DYNTRACK.getLayer(msg);
        if (lay == null) {

          MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "Could not find the Layer " + ChatColor.RED + msg + ChatColor.RESET + ".");
          return 15;
        }
        this.p.setLayer(lay);
        MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "Layer: " + ChatColor.DARK_AQUA + this.p.getLayer().getId());
        return 16;
      case 16:
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
            return 16;
        }

        if (this.speed == Recorder.SPEED.BY_HAND || this.speed == Recorder.SPEED.MINECART) {

          finish(guide.getCmdSender());
          this.DYNTRACK.addRec(new Recorder((Player) guide.getCmdSender(), this.p, this.speed));
          return -1;
        }
        return 17;
      case 17:
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
        this.DYNTRACK.addRec(new Recorder((Player) guide.getCmdSender(), this.p, this.speed, (answer == TrinaryAnswer.POSITIVE)));
        return -1;
    }
    return -3;
  }

  public void finish(CommandSender s) {
    MsgSender.sEmpty("Setup", s, ChatColor.GREEN);
    MsgSender.sInfo("Setup", s, "You finished the Setup!");
  }

  public boolean usesId(String id) {
    return this.p.getId().equalsIgnoreCase(id);
  }
}