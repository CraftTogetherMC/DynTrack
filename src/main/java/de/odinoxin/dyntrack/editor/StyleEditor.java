package de.odinoxin.dyntrack.editor;

import de.gcmclan.team.guides.Guide;
import de.gcmclan.team.guides.GuideReceiver;
import de.gcmclan.team.guides.Segment;
import de.odinoxin.dyntrack.DynTrack;
import de.odinoxin.dyntrack.enums.DataActionResult;
import de.odinoxin.dyntrack.generals.MsgSender;
import de.odinoxin.dyntrack.interfaces.Drawable;
import de.odinoxin.dyntrack.style.Style;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jdom2.JDOMException;

public class StyleEditor implements GuideReceiver {
  private final CommandSender S;
  private Style st;
  public static final Segment[] SEGMENTS = new Segment[] {
          new Segment(0, "What would you like to edit?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "You can edit:;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "ID;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Line color;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Line opacity;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Line width;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Fill color;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Fill opacity", false, false, -1, -1), new Segment(1, "Enter the new ID.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "ID's are unique.", false, false, 0, -1), new Segment(2, "Enter the new Hex-color for lines.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Possible: 000000 to FFFFFF", false, false, 0, -1), new Segment(3, "Enter the new opacity for the lines.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Possible: 0.0 to 1.0", false, false, 0, -1), new Segment(4, "Enter the new width for the lines.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Possible: >= 0", false, false, 0, -1), new Segment(5, "Enter the new Hex-color to cover the area with.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Possible: 000000 to FFFFFF", false, false, 0, -1), new Segment(6, "Enter the new opacity to cover the area with.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Possible: 0.0 to 1.0", false, false, 0, -1)
  };

  public StyleEditor(CommandSender s, Style st) {
    this.S = s;
    this.st = st;
  }
  public int receive(Guide guide, Segment seg, String msg) {
    Style newStyle;
    HashSet < Drawable > toUpdate;
    HashSet < Drawable > allDraws;
    Iterator < Drawable > it;
    Drawable[] draws;
    int i;
    switch (seg.getID()) {

      case 0:
        if (msg.equalsIgnoreCase("ID")) {
          return 1;
        }
        if (msg.equalsIgnoreCase("LineColor") || msg
                .equalsIgnoreCase("lcolor")) {
          return 2;
        }
        if (msg.equalsIgnoreCase("LineOpacity") || msg
                .equalsIgnoreCase("lopacity")) {
          return 3;
        }
        if (msg.equalsIgnoreCase("LineWidth") || msg
                .equalsIgnoreCase("lwidth")) {
          return 4;
        }
        if (msg.equalsIgnoreCase("FillColor") || msg
                .equalsIgnoreCase("fcolor")) {
          return 5;
        }
        if (msg.equalsIgnoreCase("FillOpacity") || msg
                .equalsIgnoreCase("fopacity")) {
          return 6;
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
        newStyle = this.st.clone();
        newStyle.setId(msg);

        try {
          newStyle.save();
        } catch (JDOMException | java.io.IOException | java.sql.SQLException e) {

          MsgSender.sErr(guide.getGuideSyn(), this.S, "Could not update the Style " + ChatColor.DARK_AQUA + this.st.getId() + ChatColor.RESET + ".");
          MsgSender.sErr(guide.getGuideSyn(), this.S, e.getMessage());
          return -1;
        }
        toUpdate = new HashSet < > ();
        allDraws = new HashSet < > ();
        allDraws.addAll(((DynTrack) guide.getExecutor()).getPaths().values());
        allDraws.addAll(((DynTrack) guide.getExecutor()).getSquares().values());
        allDraws.addAll(((DynTrack) guide.getExecutor()).getLinks().values());
        it = allDraws.iterator();

        while (it.hasNext()) {

          Drawable current = it.next();
          if (current.getStyle().equals(this.st)) {
            toUpdate.add(current);
          }
        }
        draws = toUpdate.toArray(new Drawable[0]);
        for (i = 0; i < draws.length; i++) {

          draws[i].setStyle(newStyle);

          try {
            draws[i].save();
          } catch (Exception e) {

            MsgSender.sErr(guide.getGuideSyn(), this.S, "Could not save " + ChatColor.DARK_AQUA + draws[i].getId() + ChatColor.RESET + ".");
            MsgSender.sErr(guide.getGuideSyn(), this.S, e.getMessage());
            MsgSender.sWarn(guide.getGuideSyn(), this.S, "The process will continue anyway.");
            MsgSender.sWarn(guide.getGuideSyn(), this.S, "The result could be, that the old version of the Style might be not deleted.");
          }
        }
        if (this.st.delete(true) == DataActionResult.DELETED_FAILED) {
          MsgSender.sErr(guide.getGuideSyn(), this.S, "The old version of the Style could not be deleted.");
        }
        this.st = newStyle;
        MsgSender.sInfo(guide.getGuideSyn(), this.S, "ID: " + ChatColor.DARK_AQUA + this.st.getId());
        MsgSender.sEmpty(guide.getGuideSyn(), this.S, ChatColor.GREEN);
        MsgSender.sInfo(guide.getGuideSyn(), this.S, "Saved " + ChatColor.DARK_AQUA + this.st.getId() + ChatColor.RESET + ".");
        return -2;

      case 2:
        try {
          int color = Integer.parseInt(msg.replace("0x", ""), 16);
          if (color > Integer.parseInt("ffffff", 16)) {
            throw new NumberFormatException(msg + " is not a valid color.");
          }
          this.st.setLineColor(color);
          MsgSender.sInfo(guide.getGuideSyn(), this.S, "Line color: " + ChatColor.LIGHT_PURPLE + Integer.toHexString(this.st.getLineColor()));
          finish(guide.getCmdSender());
          return -2;
        } catch (NumberFormatException e) {

          MsgSender.sErr(guide.getGuideSyn(), this.S, ChatColor.RED + msg + ChatColor.RESET + " is not a valid color.");
          return 2;
        }

      case 3:
        try {
          double d = Double.parseDouble(msg);
          if (d > 1.0D || d < 0.0D) {

            MsgSender.sErr(guide.getGuideSyn(), this.S, "The number you typed in, was too small / large.");
            return 3;
          }
          this.st.setLineOpacity(d);
          MsgSender.sInfo(guide.getGuideSyn(), this.S, "Line opacity: " + ChatColor.LIGHT_PURPLE + this.st.getLineOpacity());
          finish(guide.getCmdSender());
          return -2;
        } catch (NumberFormatException e) {

          MsgSender.sErr(guide.getGuideSyn(), this.S, ChatColor.RED + msg + ChatColor.RESET + " is not a valid number.");
          return 3;
        }

      case 4:
        try {
          i = Integer.parseInt(msg);
          if (i < 0) {

            MsgSender.sErr(guide.getGuideSyn(), this.S, "The number you typed in, was too small.");
            return 4;
          }
          this.st.setLineWidth(i);
          MsgSender.sInfo(guide.getGuideSyn(), this.S, "Line width: " + ChatColor.LIGHT_PURPLE + this.st.getLineWidth());
          finish(guide.getCmdSender());
          return -2;
        } catch (NumberFormatException e) {

          MsgSender.sErr(guide.getGuideSyn(), this.S, ChatColor.RED + msg + ChatColor.RESET + " is not a valid number.");
          return 4;
        }

      case 5:
        try {
          int color = Integer.parseInt(msg.replace("0x", ""), 16);
          if (color > Integer.parseInt("ffffff", 16)) {
            throw new NumberFormatException(msg + " is not a valid color.");
          }
          this.st.setFillColor(color);
          MsgSender.sInfo(guide.getGuideSyn(), this.S, "Fill color: " + ChatColor.LIGHT_PURPLE + Integer.toHexString(this.st.getFillColor()));
          finish(guide.getCmdSender());
          return -2;
        } catch (NumberFormatException e) {

          MsgSender.sErr(guide.getGuideSyn(), this.S, ChatColor.RED + msg + ChatColor.RESET + " is not a valid color.");
          return 5;
        }

      case 6:
        try {
          double d = Double.parseDouble(msg);
          if (d > 1.0D || d < 0.0D) {

            MsgSender.sErr(guide.getGuideSyn(), this.S, "The number you typed in, was too small / large.");
            return 6;
          }
          this.st.setFillOpacity(d);
          MsgSender.sInfo(guide.getGuideSyn(), this.S, "Fill opacity: " + ChatColor.LIGHT_PURPLE + this.st.getFillOpacity());
          finish(guide.getCmdSender());
          return -2;
        } catch (NumberFormatException e) {

          MsgSender.sErr(guide.getGuideSyn(), this.S, ChatColor.RED + msg + ChatColor.RESET + " is not a valid number.");
          return 6;
        }
    }
    return -3;
  }

  public void finish(CommandSender s) {
    try {
      this.st.save();
      MsgSender.sInfo("Editor", this.S, "Saved " + ChatColor.DARK_AQUA + this.st.getId() + ChatColor.RESET + ".");
    } catch (JDOMException | java.io.IOException | java.sql.SQLException e) {

      MsgSender.sErr("Editor", this.S, "Could not save the Changes.");
      MsgSender.sErr("Editor", this.S, e.getMessage());
    }
  }

  public boolean usesId(String arg0) {
    return false;
  }
}