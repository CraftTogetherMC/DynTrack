package de.odinoxin.dyntrack.setup;

import de.gcmclan.team.guides.Guide;
import de.gcmclan.team.guides.GuideManager;
import de.gcmclan.team.guides.GuideReceiver;
import de.gcmclan.team.guides.Segment;
import de.odinoxin.dyntrack.DynTrack;
import de.odinoxin.dyntrack.generals.MsgSender;
import de.odinoxin.dyntrack.generals.PermHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SetupBuilder implements GuideReceiver {
  private final DynTrack DYNTRACK;
  public static final Segment[] SEGMENTS_CONSOLE = new Segment[] {
          new Segment(0, "What would you like to create?;You can create:; - Style; - Layer; - Link")
  };

  public static final Segment[] SEGMENTS_PLAYER = new Segment[] {
          new Segment(0, "What would you like to create?;You can create:; - Style; - Layer; - Path; - Square; - Link")
  };

  public SetupBuilder(DynTrack DYNTRACK) {
    this.DYNTRACK = DYNTRACK;
  }

  public int receive(Guide guide, Segment seg, String msg) {
    switch (msg.toLowerCase()) {

      case "st":
      case "style":
        if (guide.getCmdSender() instanceof Player &&
                !PermHandler.hasPerms((Player) guide.getCmdSender(), "dyntrack.create.style")) {

          MsgSender.permMsg(this.DYNTRACK, (Player) guide.getCmdSender(), "dyntrack.create.style");
          return -1;
        }
        MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.GRAY + "Chosen: " + ChatColor.AQUA + "Style");
        guide.exitAndFollow(new Guide(this.DYNTRACK.getGuidesPlugin(), new StyleSetup(this.DYNTRACK), this.DYNTRACK, guide.getGuideSyn(), guide.getCmdSender(), StyleSetup.SEGMENTS));
        return -4;
      case "lay":
      case "layer":
        if (guide.getCmdSender() instanceof Player &&
                !PermHandler.hasPerms((Player) guide.getCmdSender(), "dyntrack.create.layer")) {

          MsgSender.permMsg(this.DYNTRACK, (Player) guide.getCmdSender(), "dyntrack.create.layer");
          return -1;
        }
        MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.GRAY + "Chosen: " + ChatColor.AQUA + "Layer");
        guide.exitAndFollow(new Guide(this.DYNTRACK.getGuidesPlugin(), new LayerSetup(this.DYNTRACK), this.DYNTRACK, guide.getGuideSyn(), guide.getCmdSender(), LayerSetup.SEGMENTS));
        return -4;
      case "p":
      case "path":
        if (!(guide.getCmdSender() instanceof Player)) {

          MsgSender.sInfo("DynTrack", guide.getCmdSender(), "You have to be an online player, to create a Path.");
          return -1;
        }
        if (!PermHandler.hasPerms((Player) guide.getCmdSender(), "dyntrack.create.path")) {

          MsgSender.permMsg(this.DYNTRACK, (Player) guide.getCmdSender(), "dyntrack.create.path");
          return -1;
        }
        if (guide.getCmdSender() instanceof Player) {

          if (this.DYNTRACK.getStyles().size() <= 0 &&
                  !PermHandler.hasPerms((Player) guide.getCmdSender(), "dyntrack.create.style")) {

            MsgSender.sErr(this.DYNTRACK, guide.getCmdSender(), "You cannot create a Path, because there are currently no Styles available and you cannot create a new one, because...");
            MsgSender.permMsg(this.DYNTRACK, (Player) guide.getCmdSender(), "dyntrack.create.style");
            return -1;
          }
          if (this.DYNTRACK.getLayers().size() <= 0 &&
                  !PermHandler.hasPerms((Player) guide.getCmdSender(), "dyntrack.create.layer")) {

            MsgSender.sErr(this.DYNTRACK, guide.getCmdSender(), "You cannot create a Path, because there are currently no Layers available and you cannot create a new one, because...");
            MsgSender.permMsg(this.DYNTRACK, (Player) guide.getCmdSender(), "dyntrack.create.layer");
            return -1;
          }
        }
        MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.GRAY + "Chosen: " + ChatColor.AQUA + "Path");
        guide.exitAndFollow(new Guide(this.DYNTRACK.getGuidesPlugin(), new PathSetup(this.DYNTRACK), this.DYNTRACK, guide.getGuideSyn(), guide.getCmdSender(), PathSetup.SEGMENTS));
        return -4;
      case "sq":
      case "square":
        if (!(guide.getCmdSender() instanceof Player)) {

          MsgSender.sInfo("DynTrack", guide.getCmdSender(), "You have to be an online player, to create a Square.");
          return -1;
        }
        if (!PermHandler.hasPerms((Player) guide.getCmdSender(), "dyntrack.create.square")) {

          MsgSender.permMsg(this.DYNTRACK, (Player) guide.getCmdSender(), "dyntrack.create.square");
          return -1;
        }
        if (guide.getCmdSender() instanceof Player) {

          if (this.DYNTRACK.getStyles().size() <= 0 &&
                  !PermHandler.hasPerms((Player) guide.getCmdSender(), "dyntrack.create.style")) {

            MsgSender.sErr(this.DYNTRACK, guide.getCmdSender(), "You cannot create a Square, because there are currently no Styles available and you cannot create a new one, because...");
            MsgSender.permMsg(this.DYNTRACK, (Player) guide.getCmdSender(), "dyntrack.create.style");
            return -1;
          }
          if (this.DYNTRACK.getLayers().size() <= 0 &&
                  !PermHandler.hasPerms((Player) guide.getCmdSender(), "dyntrack.create.layer")) {

            MsgSender.sErr(this.DYNTRACK, guide.getCmdSender(), "You cannot create a Square, because there are currently no Layers available and you cannot create a new one, because...");
            MsgSender.permMsg(this.DYNTRACK, (Player) guide.getCmdSender(), "dyntrack.create.layer");
            return -1;
          }
        }
        MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.GRAY + "Chosen: " + ChatColor.AQUA + "Square");
        guide.exitAndFollow(new Guide(this.DYNTRACK.getGuidesPlugin(), new SquareSetup(this.DYNTRACK), this.DYNTRACK, guide.getGuideSyn(), guide.getCmdSender(), SquareSetup.SEGMENTS));
        return -4;
      case "link":
        if (guide.getCmdSender() instanceof Player) {

          if (!PermHandler.hasPerms((Player) guide.getCmdSender(), "dyntrack.create.link")) {

            MsgSender.permMsg(this.DYNTRACK, (Player) guide.getCmdSender(), "dyntrack.create.link");
            return -1;
          }
          if (this.DYNTRACK.getStyles().size() <= 0 &&
                  !PermHandler.hasPerms((Player) guide.getCmdSender(), "dyntrack.create.style")) {

            MsgSender.sErr(this.DYNTRACK, guide.getCmdSender(), "You cannot create a Link, because there are currently no Styles available and you cannot create a new one, because...");
            MsgSender.permMsg(this.DYNTRACK, (Player) guide.getCmdSender(), "dyntrack.create.style");
            return -1;
          }
          if (this.DYNTRACK.getLayers().size() <= 0 &&
                  !PermHandler.hasPerms((Player) guide.getCmdSender(), "dyntrack.create.layer")) {

            MsgSender.sErr(this.DYNTRACK, guide.getCmdSender(), "You cannot create a Link, because there are currently no Layers available and you cannot create a new one, because...");
            MsgSender.permMsg(this.DYNTRACK, (Player) guide.getCmdSender(), "dyntrack.create.layer");
            return -1;
          }
          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.GRAY + "Chosen: " + ChatColor.AQUA + "Link");
          guide.exitAndFollow(new Guide(this.DYNTRACK.getGuidesPlugin(), new LinkSetup(this.DYNTRACK), this.DYNTRACK, guide.getGuideSyn(), guide.getCmdSender(), LinkSetup.SEGMENT_PLAYER));
        } else {

          MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.GRAY + "Chosen: " + ChatColor.AQUA + "Link");
          guide.exitAndFollow(new Guide(this.DYNTRACK.getGuidesPlugin(), new LinkSetup(this.DYNTRACK), this.DYNTRACK, guide.getGuideSyn(), guide.getCmdSender(), LinkSetup.SEGMENT_CONSOLE));
        }
        return -4;
    }
    MsgSender.sErr(this.DYNTRACK, guide.getCmdSender(), ChatColor.RED + msg + ChatColor.RESET + " cannot be created.");
    return 0;
  }

  public void finish(CommandSender s) {}

  public boolean usesId(String id) {
    return false;
  }
}