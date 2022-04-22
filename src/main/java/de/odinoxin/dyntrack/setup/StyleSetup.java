package de.odinoxin.dyntrack.setup;

import de.gcmclan.team.guides.Guide;
import de.gcmclan.team.guides.GuideReceiver;
import de.gcmclan.team.guides.Segment;
import de.odinoxin.dyntrack.DynTrack;
import de.odinoxin.dyntrack.generals.MsgSender;
import de.odinoxin.dyntrack.style.Style;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jdom2.JDOMException;

public class StyleSetup implements GuideReceiver {
  private final DynTrack DYNTRACK;
  private final Style st;

  public static final Segment[] SEGMENTS = new Segment[] {
          new Segment(0, "Enter the ID.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "ID's are unique.", false, false, -1, -1), new Segment(1, "Enter a Hex-color for lines.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Possible: 000000 to FFFFFF", false, false, 0, 2), new Segment(2, "Enter an opacity for the lines.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Possible: 0.0 to 1.0", false, false, 1, 3), new Segment(3, "Enter a width for the lines.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Possible: >= 0", false, false, 2, 4), new Segment(4, "Enter a Hex-color to cover the area with.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Possible: 000000 to FFFFFF", false, false, 3, 5), new Segment(5, "Enter an opacity to cover the area with.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Possible: 0.0 to 1.0", true, false, 4, 5)
  };

  public StyleSetup(DynTrack DYNTRACK) {
    this.DYNTRACK = DYNTRACK;
    this.st = new Style(this.DYNTRACK);
  }

  public int receive(Guide guide, Segment seg, String msg) {
    switch (seg.getID()) {

      case 0:
        if (this.DYNTRACK.isUsed(msg) &&
                !this.st.getId().equalsIgnoreCase(msg)) {

          MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "The ID is already used.");
          return 0;
        }

        if (Pattern.matches("\\w+", msg)) {

          this.st.setId(msg);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "ID: " + ChatColor.DARK_AQUA + this.st.getId());
          return 1;
        }

        MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.RED + msg + ChatColor.RESET + " contains an invalid character.");
        MsgSender.sWarn(guide.getGuideSyn(), guide.getCmdSender(), "ID's should only contain a-z, A-Z and 0-9.");
        return 0;

      case 1:
        try {
          int color = Integer.parseInt(msg.replace("0x", ""), 16);
          if (color < 0 || color >
                  Integer.parseInt("ffffff", 16)) {
            throw new NumberFormatException(msg + " is not a valid color.");
          }
          this.st.setLineColor(color);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "LineColor: " + ChatColor.LIGHT_PURPLE + Integer.toHexString(this.st.getLineColor()));
          return 2;
        } catch (NumberFormatException e) {

          MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.RED + msg + ChatColor.RESET + " is not a valid color.");
          return 1;
        }

      case 2:
        try {
          double d = Double.parseDouble(msg);
          if (d > 1.0D || d < 0.0D) {

            MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "The number you typed in was too small / large.");
            return 2;
          }

          this.st.setLineOpacity(d);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "LineOpacity: " + ChatColor.LIGHT_PURPLE + this.st.getLineOpacity());
          return 3;

        } catch (NumberFormatException e) {

          MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.RED + msg + ChatColor.RESET + " is not a valid number.");
          return 2;
        }

      case 3:
        try {
          int i = Integer.parseInt(msg);
          if (i < 0) {

            MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "The number you typed in was too small.");
            return 3;
          }

          this.st.setLineWidth(i);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "LineWidth: " + ChatColor.LIGHT_PURPLE + this.st.getLineWidth());
          return 4;

        } catch (NumberFormatException e) {

          MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.RED + msg + ChatColor.RESET + " is not a valid number.");
          return 3;
        }

      case 4:
        try {
          int color = Integer.parseInt(msg.replace("0x", ""), 16);
          if (color < 0 || color >
                  Integer.parseInt("ffffff", 16)) {
            throw new NumberFormatException(msg + " is not a valid color.");
          }
          this.st.setFillColor(color);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "FillColor: " + ChatColor.LIGHT_PURPLE + Integer.toHexString(this.st.getFillColor()));
          return 5;
        } catch (NumberFormatException e) {

          MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.RED + msg + ChatColor.RESET + " is not a valid color.");
          return 4;
        }

      case 5:
        try {
          double d = Double.parseDouble(msg);
          if (d > 1.0D || d < 0.0D) {

            MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), "The number you typed in was too small / large.");
            return 5;
          }
          this.st.setFillOpacity(d);
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), "FillOpacity: " + ChatColor.LIGHT_PURPLE + this.st.getFillOpacity());
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
      this.st.save();
      MsgSender.sInfo("Setup", s, "Saved " + ChatColor.DARK_AQUA + this.st.getId() + ChatColor.RESET + ".");
    } catch (JDOMException | java.io.IOException | java.sql.SQLException e) {

      MsgSender.sErr("Setup", s, "Could not save " + ChatColor.DARK_AQUA + this.st.getId() + ChatColor.RESET + ".");
      MsgSender.sErr("Setup", s, e.getMessage());
      e.printStackTrace();
    }
  }

  public boolean usesId(String id) {
      return this.st.getId().equalsIgnoreCase(id);
  }
}