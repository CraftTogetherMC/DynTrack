package de.odinoxin.dyntrack.setup;

import de.gcmclan.team.guides.Guide;
import de.gcmclan.team.guides.GuideReceiver;
import de.gcmclan.team.guides.Segment;
import de.odinoxin.dyntrack.DynTrack;
import de.odinoxin.dyntrack.enums.TrinaryAnswer;
import de.odinoxin.dyntrack.generals.MethodPool;
import de.odinoxin.dyntrack.generals.MsgSender;
import de.odinoxin.dyntrack.layer.Layer;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jdom2.JDOMException;

public class LayerSetup implements GuideReceiver {
  private final DynTrack DYNTRACK;
  private final Layer lay;
  public static final Segment[] SEGMENTS = new Segment[] {
          new Segment(0, "Enter the ID.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "ID's are unique.", false, false, -1, -1), new Segment(1, "Enter a name.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Names are not unique.", false, true, 0, 2), new Segment(2, "Should the name used as the headline of the;infobox?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 1, 3), new Segment(3, "Should the Layer be visible by default?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Hidden Layers can be activated in the upper left;" + ChatColor.GRAY + "   of the dynmap.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 2, 4), new Segment(4, "Enter a minzoom.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Minzoom is the zoom, which is at least needed to;" + ChatColor.GRAY + "   display the Layer.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Possible: 0 to 8", false, false, 3, 5), new Segment(5, "Enter a priority.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Possible: " + -2147483648 + " to " + 2147483647 + ";" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "The priority sets the order of the Layers;" + ChatColor.GRAY + "   in the Layer control in the upper left of the map.", true, false, 4, 5)
  };

  public LayerSetup(DynTrack DYNTRACK) {
    this.DYNTRACK = DYNTRACK;
    this.lay = new Layer(this.DYNTRACK);
  }

  public int receive(Guide guide, Segment seg, String msg) {
    TrinaryAnswer answer = TrinaryAnswer.UNKNOWN;
    switch (seg.getID()) {

      case 0:
        if (this.DYNTRACK.isUsed(msg) &&
                !this.lay.getId().equalsIgnoreCase(msg)) {

          MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "The ID is already used.");
          return 0;
        }

        if (Pattern.matches("\\w+", msg)) {

          this.lay.setId(msg);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "ID: " + ChatColor.DARK_AQUA + this.lay.getId());
          return 1;
        }

        MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.RED + msg + ChatColor.RESET + " contains an invalid character.");
        MsgSender.sWarn(guide.getGuideSyn(), guide.getCmdSender(), "ID's should only contain a-z, A-Z and 0-9.");
        return 0;

      case 1:
        if (this.lay.getId().equals(msg)) {

          this.lay.setName("");
        } else {

          this.lay.setName(msg);
        }
        MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "Name: " + ChatColor.AQUA + this.lay.getName());
        return 2;
      case 2:
        answer = MethodPool.getAnswer(msg);
        if (answer == TrinaryAnswer.POSITIVE) {

          this.lay.setHideName(false);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "The Name will be used as the Headline, now.");
        } else if (answer == TrinaryAnswer.NEGATIVE) {

          this.lay.setHideName(true);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "The Name will not be used as the Headline, now.");
        } else {

          MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "Your answer should be yes or no!");
          MsgSender.sWarn(guide.getGuideSyn(), guide.getCmdSender(), "You typed: " + ChatColor.RED + msg);
          return 2;
        }
        return 3;
      case 3:
        answer = MethodPool.getAnswer(msg);
        if (answer == TrinaryAnswer.POSITIVE) {

          this.lay.setHide(false);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "The Layer is visible usually, now.");
        } else if (answer == TrinaryAnswer.NEGATIVE) {

          this.lay.setHide(true);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "The Layer is hidden usually, now.");
        } else {

          MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "Your answer should be yes or no!");
          MsgSender.sWarn(guide.getGuideSyn(), guide.getCmdSender(), "You typed: " + ChatColor.RED + msg);
          return 3;
        }
        return 4;

      case 4:
        try {
          byte minzoom = Byte.parseByte(msg);
          if (minzoom > 8 || minzoom < 0) {

            MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "The number you typed in was too small / large.");
            return 4;
          }
          this.lay.setMinzoom(minzoom);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "Minzoom: " + ChatColor.LIGHT_PURPLE + this.lay.getMinzoom());
          return 5;
        } catch (NumberFormatException e) {

          MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.RED + msg + ChatColor.RESET + " is not a valid Number.");
          return 4;
        }

      case 5:
        try {
          int priority = Integer.parseInt(msg);
          this.lay.setPriority(priority);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "Priority: " + ChatColor.LIGHT_PURPLE + this.lay.getPriority());
          finish(guide.getCmdSender());
          return -2;
        } catch (NumberFormatException e) {

          MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.RED + msg + ChatColor.RESET + " is not a valid number.");
          return 5;
        }
    }
    return -3;
  }

  public void finish(CommandSender s) {
    try {
      this.lay.save();
      MsgSender.sInfo("Setup", s, "Saved " + ChatColor.DARK_AQUA + this.lay.getId() + ChatColor.RESET + ".");
    } catch (JDOMException | java.io.IOException | java.sql.SQLException e) {

      MsgSender.sErr("Setup", s, "Could not save " + ChatColor.DARK_AQUA + this.lay.getId() + ChatColor.RESET + ".");
      MsgSender.sErr("Setup", s, e.getMessage());
    }
  }

  public boolean usesId(String id) {
      return this.lay.getId().equalsIgnoreCase(id);
  }
}