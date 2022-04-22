package de.odinoxin.dyntrack;

import de.gcmclan.team.guides.Guide;
import de.gcmclan.team.guides.GuideManager;
import de.gcmclan.team.guides.GuideReceiver;
import de.odinoxin.dyntrack.editor.EditorBuilder;

import de.odinoxin.dyntrack.generals.MsgSender;
import de.odinoxin.dyntrack.generals.PermHandler;
import de.odinoxin.dyntrack.layer.Layer;
import de.odinoxin.dyntrack.link.Link;
import de.odinoxin.dyntrack.path.Path;
import de.odinoxin.dyntrack.setup.SetupBuilder;
import de.odinoxin.dyntrack.square.Square;
import de.odinoxin.dyntrack.style.Style;
import java.io.File;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jdom2.JDOMException;

class CmdHandler implements CommandExecutor {
  private final DynTrack DYNTRACK;

  CmdHandler(DynTrack DYNTRACK) {
    this.DYNTRACK = DYNTRACK;
  }

  public boolean onCommand(CommandSender s, Command cmd, String cmdLable, String[] args) {
    boolean success = false;
    if (cmd.getName().equalsIgnoreCase("dynTrack") || cmd
            .getName().equalsIgnoreCase("dynTr")) {

      if (args.length == 0) {

        InfoHelper.info(s, this.DYNTRACK
                .getStyles().size(), this.DYNTRACK
                .getLayers().size(), this.DYNTRACK
                .getPaths().size(), this.DYNTRACK
                .getSquares().size(), this.DYNTRACK
                .getLinks().size());
        success = true;
      } else if (s instanceof Player) {

        if (args.length >= 1) {
          if (args[0].equalsIgnoreCase("help") || args[0]
                  .equalsIgnoreCase("?") || args[0]
                  .equalsIgnoreCase("h")) {

            InfoHelper.helpPlayer((Player) s);
            success = true;
          } else if (args[0].equalsIgnoreCase("intro")) {

            introCmd(s, args);
            success = true;
          } else if (args[0].equalsIgnoreCase("stop") || args[0]
                  .equalsIgnoreCase("pause") || args[0]
                  .equalsIgnoreCase("cont") || args[0]
                  .equalsIgnoreCase("continue") || args[0]
                  .equalsIgnoreCase("back")) {

            trackCmds((Player) s, args);
            success = true;
          } else if (args[0].equalsIgnoreCase("start") || args[0]
                  .equalsIgnoreCase("create") || args[0]
                  .equalsIgnoreCase("define")) {

            createCmds(s, args);
            success = true;
          } else if (args[0].equalsIgnoreCase("edit") || args[0]
                  .equalsIgnoreCase("set") || args[0]
                  .equalsIgnoreCase("update") || args[0]
                  .equalsIgnoreCase("redefine")) {

            editCmds(s, args);
            success = true;
          } else if (args[0].equalsIgnoreCase("reload")) {

            reloadCmd(s);
            success = true;
          }
        }
        if (args.length >= 2) {
          if (args[0].equalsIgnoreCase("info")) {
            infoCmd(s, args);
            success = true;
          } else if (args[0].equalsIgnoreCase("list")) {
            listCmds(s, args);
            success = true;
          } else if (args[0].equalsIgnoreCase("del") || args[0]
                  .equalsIgnoreCase("delete")) {
            delCmds(s, args);
            success = true;
          }

        }
      } else {

        if (args.length >= 1) {
          if (args[0].equalsIgnoreCase("help") || args[0]
                  .equalsIgnoreCase("?") || args[0]
                  .equalsIgnoreCase("h")) {

            InfoHelper.helpConsole();
            success = true;
          } else if (args[0].equalsIgnoreCase("intro")) {

            introCmd(s, args);
            success = true;
          } else if (args[0].equalsIgnoreCase("start") || args[0]
                  .equalsIgnoreCase("create") || args[0]
                  .equalsIgnoreCase("define")) {

            createCmds(s, args);
            success = true;
          } else if (args[0].equalsIgnoreCase("edit") || args[0]
                  .equalsIgnoreCase("set") || args[0]
                  .equalsIgnoreCase("update") || args[0]
                  .equalsIgnoreCase("redefine")) {

            editCmds(s, args);
            success = true;
          } else if (args[0].equalsIgnoreCase("reload")) {

            reloadCmd(s);
            success = true;
          } else if (args[0].equalsIgnoreCase("import")) {

            importCmd();
            success = true;
          } else if (args[0].equalsIgnoreCase("export")) {

            exportCmd();
            success = true;
          }
        }
        if (args.length >= 2) {
          if (args[0].equalsIgnoreCase("info")) {

            infoCmd(s, args);
            success = true;
          } else if (args[0].equalsIgnoreCase("list")) {

            listCmds(s, args);
            success = true;
          } else if (args[0].equalsIgnoreCase("del") || args[0]
                  .equalsIgnoreCase("delete")) {

            delCmds(s, args);
            success = true;
          }
        }
      }
      if (!success) {

        InfoHelper.info(s, this.DYNTRACK
                .getStyles().size(), this.DYNTRACK
                .getLayers().size(), this.DYNTRACK
                .getPaths().size(), this.DYNTRACK
                .getSquares().size(), this.DYNTRACK
                .getLinks().size());
        success = true;
      }
    }
    return success;
  }

  private void trackCmds(Player p, String[] args) {
    if (!PermHandler.hasPerms(p, "dyntrack.create.path") ||
            !PermHandler.hasPerms(p, "dyntrack.create.square")) {

      String[] perms = {
              "dyntrack.create.path",
              "dyntrack.create.square"
      };
      MsgSender.permMsg(this.DYNTRACK, p, perms);
      return;
    }
    if (args[0].equalsIgnoreCase("stop") || args[0]
            .equalsIgnoreCase("exit")) {

      this.DYNTRACK.removeRecs(p);
    } else if (args[0].equalsIgnoreCase("pause")) {

      this.DYNTRACK.setTracking(p, false);
    } else if (args[0].equalsIgnoreCase("cont") || args[0]
            .equalsIgnoreCase("continue")) {

      this.DYNTRACK.setTracking(p, true);
    } else if (args[0].equalsIgnoreCase("back")) {

      Recorder rec = this.DYNTRACK.getRec(p);
      if (rec != null) {

        if (args.length == 1) {

          rec.delLocation(true);
        } else if (args.length > 1) {

          try {
            int j = Integer.parseInt(args[1]);
            rec.delLocation(j);
          } catch (NumberFormatException e) {
            MsgSender.pErr(this.DYNTRACK, p, ChatColor.RED + args[1] + ChatColor.RESET + " is not a valid number.");
          }

        }
      } else {

        MsgSender.pErr(this.DYNTRACK, p, "You are not tracking at the moment!");
      }
    }
  }

  private void infoCmd(CommandSender s, String[] args) {
    if (!this.DYNTRACK.isUsed(args[1]) || args[1]
            .equalsIgnoreCase("config")) {

      MsgSender.sErr(this.DYNTRACK, s, "The ID " + ChatColor.RED + args[1] + ChatColor.RESET + " is unused.");

      return;
    }
    Style st = this.DYNTRACK.getStyle(args[1]);
    if (st != null) {

      if (s instanceof Player &&
              !PermHandler.hasPerms((Player) s, "dyntrack.info.element.style")) {

        MsgSender.permMsg(this.DYNTRACK, (Player) s, "dyntrack.info.element.style");
        return;
      }
      if (args.length == 2) {

        InfoHelper.infoStyle(s, st);
      } else if (args.length > 2) {

        try {
          int page = Integer.parseInt(args[2]);
          InfoHelper.infoStyleImpl(this.DYNTRACK, s, st, page);
        } catch (NumberFormatException e) {

          InfoHelper.infoStyle(s, st);
        }
      }

      return;
    }

    Layer lay = this.DYNTRACK.getLayer(args[1]);
    if (lay != null) {

      if (s instanceof Player &&
              !PermHandler.hasPerms((Player) s, "dyntrack.info.element.layer")) {

        MsgSender.permMsg(this.DYNTRACK, (Player) s, "dyntrack.info.element.layer");
        return;
      }
      if (args.length == 2) {

        InfoHelper.infoLayer(s, lay);
      } else if (args.length > 2) {

        try {
          int page = Integer.parseInt(args[2]);
          InfoHelper.infoLayerImpl(this.DYNTRACK, s, lay, page);
        } catch (NumberFormatException e) {

          InfoHelper.infoLayer(s, lay);
        }
      }

      return;
    }

    Path p = this.DYNTRACK.getPath(args[1]);
    if (p != null) {

      if (s instanceof Player &&
              !PermHandler.hasPerms((Player) s, "dyntrack.info.element.path")) {

        MsgSender.permMsg(this.DYNTRACK, (Player) s, "dyntrack.info.element.path");
        return;
      }
      if (args.length == 2) {

        InfoHelper.infoPath(s, p);
      } else if (args.length > 2) {

        try {
          if (args[2].equalsIgnoreCase("via")) {
            if (args.length < 4) {
              throw new NumberFormatException("Missing page number.");
            }
            int page = Integer.parseInt(args[3]);
            InfoHelper.infoPathLinks(s, p, page);
          } else {
            int page = Integer.parseInt(args[2]);
            InfoHelper.infoPathLocations(s, p, page);
          }

        } catch (NumberFormatException e) {

          InfoHelper.infoPath(s, p);
        }
      }

      return;
    }

    Square sq = this.DYNTRACK.getSquare(args[1]);
    if (sq != null) {

      if (s instanceof Player &&
              !PermHandler.hasPerms((Player) s, "dyntrack.info.element.square")) {

        MsgSender.permMsg(this.DYNTRACK, (Player) s, "dyntrack.info.element.square");
        return;
      }
      if (args.length == 2) {

        InfoHelper.infoSquare(s, sq);
      } else if (args.length > 2) {

        try {
          if (args[2].equalsIgnoreCase("via")) {
            if (args.length < 4) {
              throw new NumberFormatException("Missing page number.");
            }
            int page = Integer.parseInt(args[3]);
            InfoHelper.infoSquareLinks(s, sq, page);
          } else {
            int page = Integer.parseInt(args[2]);
            InfoHelper.infoSquareLocations(s, sq, page);
          }

        } catch (NumberFormatException e) {

          InfoHelper.infoSquare(s, sq);
        }
      }

      return;
    }

    Link link = this.DYNTRACK.getLink(args[1]);
    if (link != null) {

      if (s instanceof Player &&
              !PermHandler.hasPerms((Player) s, "dyntrack.info.element.link")) {

        MsgSender.permMsg(this.DYNTRACK, (Player) s, "dyntrack.info.element.link");
        return;
      }
      if (args.length == 2) {

        InfoHelper.infoLink(s, link);
      } else if (args.length > 2) {

        try {
          int page = Integer.parseInt(args[2]);
          InfoHelper.infoLinkLinked(s, link, page);
        } catch (NumberFormatException e) {

          InfoHelper.infoLink(s, link);
        }
      }
      return;
    }
  }

  private void introCmd(CommandSender s, String[] args) {
    if (s instanceof Player &&
            !PermHandler.hasPerms((Player) s, "dyntrack.info.intro")) {

      MsgSender.permMsg(this.DYNTRACK, (Player) s, "dyntrack.info.intro");
      return;
    }
    if (args.length == 1) {

      (new Intro(this.DYNTRACK, s, (byte) 0)).runTaskLaterAsynchronously(this.DYNTRACK, 0L);
    } else {

      try {

        (new Intro(this.DYNTRACK, s, Byte.parseByte(args[1]))).runTaskLaterAsynchronously(this.DYNTRACK, 0L);
      } catch (NumberFormatException e) {

        MsgSender.sErr(this.DYNTRACK, s, ChatColor.RED + args[1] + ChatColor.RESET + " is not a valid number.");
      }
    }
  }

  private void createCmds(CommandSender s, String[] args) {
    if (s instanceof Player &&
            !PermHandler.hasPerms((Player) s, "dyntrack.create.style") &&
            !PermHandler.hasPerms((Player) s, "dyntrack.create.layer") &&
            !PermHandler.hasPerms((Player) s, "dyntrack.create.path") &&
            !PermHandler.hasPerms((Player) s, "dyntrack.create.square") &&
            !PermHandler.hasPerms((Player) s, "dyntrack.create.link")) {

      String[] perms = {
              "dyntrack.create.style",
              "dyntrack.create.layer",
              "dyntrack.create.path",
              "dyntrack.create.square",
              "dyntrack.create.link"
      };
      MsgSender.permMsg(this.DYNTRACK, (Player) s, perms);
      return;
    }
    if (s instanceof Player) {

      this.DYNTRACK.getGuidesPlugin().addGuide(new Guide(this.DYNTRACK.getGuidesPlugin(), new SetupBuilder(this.DYNTRACK), this.DYNTRACK, "Setup", s, SetupBuilder.SEGMENTS_PLAYER), true);
    } else {

      this.DYNTRACK.getGuidesPlugin().addGuide(new Guide(this.DYNTRACK.getGuidesPlugin(), new SetupBuilder(this.DYNTRACK), this.DYNTRACK, "Setup", s, SetupBuilder.SEGMENTS_CONSOLE), true);
    }
  }

  private void editCmds(CommandSender s, String[] args) {
    if (s instanceof Player &&
            !PermHandler.hasPerms((Player) s, "dyntrack.edit.style") &&
            !PermHandler.hasPerms((Player) s, "dyntrack.edit.layer") &&
            !PermHandler.hasPerms((Player) s, "dyntrack.edit.path") &&
            !PermHandler.hasPerms((Player) s, "dyntrack.edit.square") &&
            !PermHandler.hasPerms((Player) s, "dyntrack.edit.link")) {

      String[] perms = {
              "dyntrack.edit.style",
              "dyntrack.edit.layer",
              "dyntrack.edit.path",
              "dyntrack.edit.square",
              "dyntrack.edit.link"
      };
      MsgSender.permMsg(this.DYNTRACK, (Player) s, perms);
      return;
    }
    if (s instanceof Player && this.DYNTRACK
            .getRec((Player) s) != null) {

      MsgSender.sErr(this.DYNTRACK, s, "You are still tracking.");
      MsgSender.sWarn(this.DYNTRACK, s, "Finish tracking first with " + ChatColor.DARK_GREEN + "/dynTrack stop" + ChatColor.RESET + ".");
      return;
    }
    this.DYNTRACK.getGuidesPlugin().addGuide(new Guide(this.DYNTRACK.getGuidesPlugin(), new EditorBuilder(this.DYNTRACK), this.DYNTRACK, "Editor", s, EditorBuilder.SEGMENTS), true);
  }

  private void listCmds(CommandSender s, String[] args) {
    if (s instanceof Player &&
            !PermHandler.hasPerms((Player) s, "dyntrack.info.list.style") &&
            !PermHandler.hasPerms((Player) s, "dyntrack.info.list.layer") &&
            !PermHandler.hasPerms((Player) s, "dyntrack.info.list.path") &&
            !PermHandler.hasPerms((Player) s, "dyntrack.info.list.square") &&
            !PermHandler.hasPerms((Player) s, "dyntrack.info.list.link")) {

      String[] perms = {
              "dyntrack.info.list.style",
              "dyntrack.info.list.layer",
              "dyntrack.info.list.path",
              "dyntrack.info.list.square",
              "dyntrack.info.list.link"
      };
      MsgSender.permMsg(this.DYNTRACK, (Player) s, perms);
      return;
    }
    if (args[1].equalsIgnoreCase("st") || args[1]
            .equalsIgnoreCase("style") || args[1]
            .equalsIgnoreCase("styles")) {

      if (s instanceof Player &&
              !PermHandler.hasPerms((Player) s, "dyntrack.info.list.style")) {

        MsgSender.permMsg(this.DYNTRACK, (Player) s, "dyntrack.info.list.style");
        return;
      }
      MsgSender.sInfo(this.DYNTRACK, s, "Total Styles: " + ChatColor.LIGHT_PURPLE + this.DYNTRACK.getStyles().size());
      Iterator < Style > it = this.DYNTRACK.getStyles().values().iterator();
      while (it.hasNext()) {
        MsgSender.sInfo(this.DYNTRACK, s, ChatColor.DARK_GRAY + " - " + ChatColor.DARK_AQUA + it
                .next().getId());
      }
    } else if (args[1].equalsIgnoreCase("lay") || args[1]
            .equalsIgnoreCase("layer") || args[1]
            .equalsIgnoreCase("layers")) {

      if (s instanceof Player &&
              !PermHandler.hasPerms((Player) s, "dyntrack.info.list.layer")) {

        MsgSender.permMsg(this.DYNTRACK, (Player) s, "dyntrack.info.list.layer");
        return;
      }
      MsgSender.sInfo(this.DYNTRACK, s, "Total Layers: " + ChatColor.LIGHT_PURPLE + this.DYNTRACK.getLayers().size());
      Iterator < Layer > it = this.DYNTRACK.getLayers().values().iterator();

      while (it.hasNext()) {
        Layer lay = it.next();
        MsgSender.sInfo(this.DYNTRACK, s, ChatColor.DARK_GRAY + " - " + ChatColor.DARK_AQUA + lay
                .getId() + ChatColor.RESET + " : " + ChatColor.AQUA + lay

                .getName());
      }

    } else if (args[1].equalsIgnoreCase("p") || args[1]
            .equalsIgnoreCase("paht") || args[1]
            .equalsIgnoreCase("pahts")) {

      if (s instanceof Player &&
              !PermHandler.hasPerms((Player) s, "dyntrack.info.list.path")) {

        MsgSender.permMsg(this.DYNTRACK, (Player) s, "dyntrack.info.list.path");
        return;
      }
      MsgSender.sInfo(this.DYNTRACK, s, "Total Paths: " + ChatColor.LIGHT_PURPLE + this.DYNTRACK.getPaths().size());
      Iterator < Path > it = this.DYNTRACK.getPaths().values().iterator();

      while (it.hasNext()) {
        Path p = it.next();
        MsgSender.sInfo(this.DYNTRACK, s, ChatColor.DARK_GRAY + " - " + ChatColor.DARK_AQUA + p
                .getId() + ChatColor.RESET + " : " + ChatColor.AQUA + p

                .getName());
      }

    } else if (args[1].equalsIgnoreCase("sq") || args[1]
            .equalsIgnoreCase("square") || args[1]
            .equalsIgnoreCase("squares")) {

      if (s instanceof Player &&
              !PermHandler.hasPerms((Player) s, "dyntrack.info.list.square")) {

        MsgSender.permMsg(this.DYNTRACK, (Player) s, "dyntrack.info.list.square");
        return;
      }
      MsgSender.sInfo(this.DYNTRACK, s, "Total Squares: " + ChatColor.LIGHT_PURPLE + this.DYNTRACK.getSquares().size());
      Iterator < Square > it = this.DYNTRACK.getSquares().values().iterator();

      while (it.hasNext()) {
        Square sq = it.next();
        MsgSender.sInfo(this.DYNTRACK, s, ChatColor.DARK_GRAY + " - " + ChatColor.DARK_AQUA + sq
                .getId() + ChatColor.RESET + " : " + ChatColor.AQUA + sq

                .getName());
      }

    } else if (args[1].equalsIgnoreCase("link") || args[1]
            .equalsIgnoreCase("links")) {

      if (s instanceof Player &&
              !PermHandler.hasPerms((Player) s, "dyntrack.info.list.link")) {

        MsgSender.permMsg(this.DYNTRACK, (Player) s, "dyntrack.info.list.link");
        return;
      }
      MsgSender.sInfo(this.DYNTRACK, s, "Total Links: " + ChatColor.LIGHT_PURPLE + this.DYNTRACK.getLinks().size());
      Iterator < Link > it = this.DYNTRACK.getLinks().values().iterator();
      while (it.hasNext()) {
        MsgSender.sInfo(this.DYNTRACK, s, ChatColor.DARK_GRAY + " - " + ChatColor.DARK_AQUA + it
                .next().getId());
      }
    } else if (args[1].equalsIgnoreCase("all")) {

      MsgSender.sEmpty(this.DYNTRACK, s, ChatColor.GRAY);
      String[] h = {
              "list",
              "st"
      };
      listCmds(s, h);
      h[1] = "lay";
      listCmds(s, h);
      h[1] = "p";
      listCmds(s, h);
      h[1] = "sq";
      listCmds(s, h);
      h[1] = "link";
      listCmds(s, h);
    } else {

      MsgSender.sErr(this.DYNTRACK, s, "You can list " + ChatColor.DARK_GREEN + "Styles" + ChatColor.RESET + ", " + ChatColor.DARK_GREEN + "Layers" + ChatColor.RESET + ", " + ChatColor.DARK_GREEN + "Paths" + ChatColor.RESET + ", " + ChatColor.DARK_GREEN + "Squares" + ChatColor.RESET + " and " + ChatColor.DARK_GREEN + "Links" + ChatColor.RESET + ".");

      MsgSender.sWarn(this.DYNTRACK, s, "You wanted to list " + ChatColor.RED + args[1] + ChatColor.RESET + ".");
    }
  }

  private void delCmds(CommandSender s, String[] args) {
    Style st = this.DYNTRACK.getStyle(args[1]);
    if (st != null) {

      if (s instanceof Player &&
              !PermHandler.hasPerms((Player) s, "dyntrack.del.style")) {

        MsgSender.permMsg(this.DYNTRACK, (Player) s, "dyntrack.del.style");
        return;
      }
      switch (st.delete(false)) {
        case STILL_USED:
          MsgSender.sErr(this.DYNTRACK, s, "Could not delete the Style " + ChatColor.DARK_AQUA + st.getId() + ChatColor.RESET + ",");
          MsgSender.sErr(this.DYNTRACK, s, "because the Style is still needed.");
          return;
        case DELETED_FAILED:
          MsgSender.sErr(this.DYNTRACK, s, "Could not delete the Style " + ChatColor.DARK_AQUA + st.getId() + ChatColor.RESET + ".");
          return;
        case DELETED_SUCCESSFULLY:
          MsgSender.sInfo(this.DYNTRACK, s, "Style " + ChatColor.DARK_AQUA + st.getId() + ChatColor.RESET + " deleted.");
          return;
      }

      return;
    }
    Layer lay = this.DYNTRACK.getLayer(args[1]);
    if (lay != null) {

      if (s instanceof Player &&
              !PermHandler.hasPerms((Player) s, "dyntrack.del.layer")) {

        MsgSender.permMsg(this.DYNTRACK, (Player) s, "dyntrack.del.layer");
        return;
      }
      switch (lay.delete(false)) {
        case STILL_USED:
          MsgSender.sErr(this.DYNTRACK, s, "Could not delete the Layer " + ChatColor.DARK_AQUA + lay.getId() + ChatColor.RESET + ",");
          MsgSender.sErr(this.DYNTRACK, s, "because the Layer is still needed.");
          return;
        case DELETED_FAILED:
          MsgSender.sErr(this.DYNTRACK, s, "Could not delete the Layer " + ChatColor.DARK_AQUA + lay.getId() + ChatColor.RESET + ".");
          return;
        case DELETED_SUCCESSFULLY:
          MsgSender.sInfo(this.DYNTRACK, s, "Layer " + ChatColor.DARK_AQUA + lay.getId() + ChatColor.RESET + " deleted.");
          return;
      }

      return;
    }
    Path p = this.DYNTRACK.getPath(args[1]);
    if (p != null) {

      if (s instanceof Player &&
              !PermHandler.hasPerms((Player) s, "dyntrack.del.path")) {

        MsgSender.permMsg(this.DYNTRACK, (Player) s, "dyntrack.del.path");
        return;
      }
      switch (p.delete(false)) {
        case STILL_USED:
          MsgSender.sErr(this.DYNTRACK, s, "Could not delete the Path " + ChatColor.DARK_AQUA + p.getId() + ChatColor.RESET + ",");
          MsgSender.sErr(this.DYNTRACK, s, "because the Path is still needed.");
          return;
        case DELETED_FAILED:
          MsgSender.sErr(this.DYNTRACK, s, "Could not delete the Path " + ChatColor.DARK_AQUA + p.getId() + ChatColor.RESET + ".");
          return;
        case DELETED_SUCCESSFULLY:
          MsgSender.sInfo(this.DYNTRACK, s, "Path " + ChatColor.DARK_AQUA + p.getId() + ChatColor.RESET + " deleted.");
          return;
      }

      return;
    }
    Square sq = this.DYNTRACK.getSquare(args[1]);
    if (sq != null) {

      if (s instanceof Player &&
              !PermHandler.hasPerms((Player) s, "dyntrack.del.square")) {

        MsgSender.permMsg(this.DYNTRACK, (Player) s, "dyntrack.del.square");
        return;
      }
      switch (sq.delete(false)) {
        case STILL_USED:
          MsgSender.sErr(this.DYNTRACK, s, "Could not delete the Square " + ChatColor.DARK_AQUA + sq.getId() + ChatColor.RESET + ",");
          MsgSender.sErr(this.DYNTRACK, s, "because the Square is still needed.");
          return;
        case DELETED_FAILED:
          MsgSender.sErr(this.DYNTRACK, s, "Could not delete the Square " + ChatColor.DARK_AQUA + sq.getId() + ChatColor.RESET + ".");
          return;
        case DELETED_SUCCESSFULLY:
          MsgSender.sInfo(this.DYNTRACK, s, "Square " + ChatColor.DARK_AQUA + sq.getId() + ChatColor.RESET + " deleted.");
          return;
      }

      return;
    }
    Link link = this.DYNTRACK.getLink(args[1]);
    if (link != null) {

      if (s instanceof Player &&
              !PermHandler.hasPerms((Player) s, "dyntrack.del.link")) {

        MsgSender.permMsg(this.DYNTRACK, (Player) s, "dyntrack.del.link");
        return;
      }
      switch (link.delete(false)) {
        case STILL_USED:
          MsgSender.sErr(this.DYNTRACK, s, "Could not delete the Link " + ChatColor.DARK_AQUA + link.getId() + ChatColor.RESET + ",");
          MsgSender.sErr(this.DYNTRACK, s, "because the Link is still needed.");
          return;
        case DELETED_FAILED:
          MsgSender.sErr(this.DYNTRACK, s, "Could not delete the Link " + ChatColor.DARK_AQUA + link.getId() + ChatColor.RESET + ".");
          return;
        case DELETED_SUCCESSFULLY:
          MsgSender.sInfo(this.DYNTRACK, s, "Link " + ChatColor.DARK_AQUA + link.getId() + ChatColor.RESET + " deleted.");
          return;
      }

      return;
    }
    MsgSender.sErr(this.DYNTRACK, s, "The ID " + ChatColor.RED + args[1] + ChatColor.RESET + " is unused.");
  }

  private void reloadCmd(CommandSender s) {
    if (s instanceof Player &&
            !PermHandler.hasPerms((Player) s, "dyntrack.reload")) {

      MsgSender.permMsg(this.DYNTRACK, (Player) s, "dyntrack.reload");
      return;
    }
    if (this.DYNTRACK.anyRecs()) {

      MsgSender.sErr(this.DYNTRACK, s, "Could not reload DynTrack.");
      MsgSender.sWarn(this.DYNTRACK, s, "Someone needs it right now.");
      return;
    }
    MsgSender.sWarn(this.DYNTRACK, s, "Reloading DynTrack now...");
    this.DYNTRACK.getServer().getPluginManager().disablePlugin(this.DYNTRACK);
    this.DYNTRACK.getServer().getPluginManager().enablePlugin(this.DYNTRACK);
    MsgSender.sInfo(this.DYNTRACK, s, "You reloaded DynTrack!");
  }

  private void importCmd() {
    if (this.DYNTRACK.getDynTrackConfig().useDB()) {

      File folder = new File(this.DYNTRACK.getDataFolder() + "/Styles");
      for (File f: folder.listFiles()) {

        try {
          Style.loadStyle(this.DYNTRACK, f.getName().replace(".xml", "")).saveInDatabase();
        } catch (JDOMException | java.io.IOException | java.sql.SQLException e) {

          MsgSender.cBug(this.DYNTRACK, "Could not load the Style " + f.getName().replace(".xml", "") + ".");
          MsgSender.cBug(this.DYNTRACK, e.getMessage());
        }
      }
      folder = new File(this.DYNTRACK.getDataFolder() + "/Layers");
      for (File f: folder.listFiles()) {

        try {
          Layer.loadLayer(this.DYNTRACK, f.getName().replace(".xml", "")).saveInDatabase();
        } catch (JDOMException | java.io.IOException | java.sql.SQLException e) {

          MsgSender.cBug(this.DYNTRACK, "Could not load the Layer " + f.getName().replace(".xml", "") + ".");
          MsgSender.cBug(this.DYNTRACK, e.getMessage());
        }
      }
      folder = new File(this.DYNTRACK.getDataFolder() + "/Paths");
      for (File f: folder.listFiles()) {

        try {
          Path p = Path.loadPath(this.DYNTRACK, f.getName().replace(".xml", ""));
          if (p == null) {
            MsgSender.cBug(this.DYNTRACK, "Failed to load Path " + f.getName().replace(".xml", "") + " from file.");
          } else {
            p.saveInDatabase();
          }

        } catch (JDOMException | java.io.IOException | java.sql.SQLException e) {

          MsgSender.cBug(this.DYNTRACK, "Could not load the Path " + f.getName().replace(".xml", "") + ".");
          MsgSender.cBug(this.DYNTRACK, e.getMessage());
        }
      }
      folder = new File(this.DYNTRACK.getDataFolder() + "/Squares");
      for (File f: folder.listFiles()) {

        try {
          Square sq = Square.loadSquare(this.DYNTRACK, f.getName().replace(".xml", ""));
          if (sq == null) {
            MsgSender.cBug(this.DYNTRACK, "Failed to load Square " + f.getName().replace(".xml", "") + " from file.");
          } else {
            sq.saveInDatabase();
          }

        } catch (JDOMException | java.io.IOException | java.sql.SQLException e) {

          MsgSender.cBug(this.DYNTRACK, "Could not load the Square " + f.getName().replace(".xml", "") + ".");
          MsgSender.cBug(this.DYNTRACK, e.getMessage());
        }
      }
      folder = new File(this.DYNTRACK.getDataFolder() + "/Links");
      for (File f: folder.listFiles()) {

        try {
          Link link = Link.loadLink(this.DYNTRACK, f.getName().replace(".xml", ""));
          if (link == null) {
            MsgSender.cBug(this.DYNTRACK, "Failed to load Link " + f.getName().replace(".xml", "") + " from file.");
          } else {
            link.saveInDatabase();
          }

        } catch (JDOMException | java.io.IOException | java.sql.SQLException e) {

          MsgSender.cBug(this.DYNTRACK, "Could not load the Link " + f.getName().replace(".xml", "") + ".");
          MsgSender.cBug(this.DYNTRACK, e.getMessage());
        }
      }
      MsgSender.cInfo(this.DYNTRACK, "Done.");
    } else {

      MsgSender.cBug(this.DYNTRACK, "Could not import Elements from Files into the Database, because DynTrack has no Database-Connection.");
      MsgSender.cWarn(this.DYNTRACK, "Define the Database-Connection in the Config.yml of DynTrack to use the Database.");
    }
  }

  private void exportCmd() {
    if (this.DYNTRACK.getDynTrackConfig().useDB()) {

      Iterator < Style > itStyle = this.DYNTRACK.getStyles().values().iterator();

      while (itStyle.hasNext()) {

        Style st = itStyle.next();

        try {
          st.saveInFile();
        } catch (JDOMException | java.io.IOException e) {

          MsgSender.cBug(this.DYNTRACK, "Could not save the Style " + st.getId() + " in file.");
          MsgSender.cBug(this.DYNTRACK, e.getMessage());
        }
      }
      Iterator < Layer > itLayer = this.DYNTRACK.getLayers().values().iterator();

      while (itLayer.hasNext()) {

        Layer lay = itLayer.next();

        try {
          lay.saveInFile();
        } catch (JDOMException | java.io.IOException e) {

          MsgSender.cBug(this.DYNTRACK, "Could not save the Layer " + lay.getId() + " in file.");
          MsgSender.cBug(this.DYNTRACK, e.getMessage());
        }
      }
      Iterator < Path > itPath = this.DYNTRACK.getPaths().values().iterator();

      while (itPath.hasNext()) {

        Path p = itPath.next();

        try {
          p.saveInFile();
        } catch (JDOMException | java.io.IOException e) {

          MsgSender.cBug(this.DYNTRACK, "Could not save the Path " + p.getId() + " in file.");
          MsgSender.cBug(this.DYNTRACK, e.getMessage());
        }
      }
      Iterator < Square > itSquare = this.DYNTRACK.getSquares().values().iterator();

      while (itSquare.hasNext()) {

        Square sq = itSquare.next();

        try {
          sq.saveInFile();
        } catch (JDOMException | java.io.IOException e) {

          MsgSender.cBug(this.DYNTRACK, "Could not save the Square " + sq.getId() + " in file.");
          MsgSender.cBug(this.DYNTRACK, e.getMessage());
        }
      }
      Iterator < Link > itLink = this.DYNTRACK.getLinks().values().iterator();

      while (itLink.hasNext()) {

        Link link = itLink.next();

        try {
          link.saveInFile();
        } catch (JDOMException | java.io.IOException e) {

          MsgSender.cBug(this.DYNTRACK, "Could not save the Link " + link.getId() + " in file.");
          MsgSender.cBug(this.DYNTRACK, e.getMessage());
        }
      }
      MsgSender.cInfo(this.DYNTRACK, "Done.");
    } else {

      MsgSender.cBug(this.DYNTRACK, "Could not export Elements from the Database into Files, because DynTrack has no Database-Connection.");
      MsgSender.cInfo(this.DYNTRACK, "Define the Database-Connection in the config.yml of DynTrack to use the Database.");
    }
  }
}