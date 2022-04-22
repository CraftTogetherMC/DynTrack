package de.odinoxin.dyntrack;

import de.odinoxin.dyntrack.generals.MethodPool;
import de.odinoxin.dyntrack.generals.MsgSender;
import de.odinoxin.dyntrack.generals.PermHandler;
import de.odinoxin.dyntrack.interfaces.Drawable;
import de.odinoxin.dyntrack.interfaces.Linkable;
import de.odinoxin.dyntrack.layer.Layer;
import de.odinoxin.dyntrack.link.Link;
import de.odinoxin.dyntrack.path.Path;
import de.odinoxin.dyntrack.square.Square;
import de.odinoxin.dyntrack.style.Style;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class InfoHelper {
  static void info(CommandSender s, int styles, int layers, int paths, int squares, int links) {
    MsgSender.sInfo("DynTrack", s, ChatColor.DARK_GRAY + "-- " + ChatColor.RESET + "Information about DynTrack " + ChatColor.DARK_GRAY + "--");
    MsgSender.sEmpty("DynTrack", s, ChatColor.GREEN);
    MsgSender.sInfo("DynTrack", s, ChatColor.GRAY + "DynTrack by OdinOxin.");
    MsgSender.sInfo("DynTrack", s, "DynTrack is runninig in version: " + ChatColor.LIGHT_PURPLE + DynTrack.getVersion());
    MsgSender.sInfo("DynTrack", s, "Styles:   " + ChatColor.LIGHT_PURPLE + styles);
    MsgSender.sInfo("DynTrack", s, "Layers:   " + ChatColor.LIGHT_PURPLE + layers);
    MsgSender.sInfo("DynTrack", s, "Paths:    " + ChatColor.LIGHT_PURPLE + paths);
    MsgSender.sInfo("DynTrack", s, "Squares:  " + ChatColor.LIGHT_PURPLE + squares);
    MsgSender.sInfo("DynTrack", s, "Links:    " + ChatColor.LIGHT_PURPLE + links);
    MsgSender.sEmpty("DynTrack", s, ChatColor.GREEN);
    MsgSender.sInfo("DynTrack", s, ChatColor.DARK_GREEN + "/DynTrack help " + ChatColor.RESET + "- Shows the help for DynTrack.");
    MsgSender.sEmpty("DynTrack", s, ChatColor.GREEN);
    MsgSender.sInfo("DynTrack", s, ChatColor.GRAY + "Please report all bugs and issues to OdinOxin.");
    MsgSender.sInfo("DynTrack", s, ChatColor.BLACK + "Made " + ChatColor.RED + "in " + ChatColor.GOLD + "Germany");
    MsgSender.sEmpty("DynTrack", s, ChatColor.GREEN);
    MsgSender.sInfo("DynTrack", s, ChatColor.DARK_GRAY + "-- " + ChatColor.RESET + "This is the end of the Info-page " + ChatColor.DARK_GRAY + "--");
  }

  static void infoStyle(CommandSender s, Style st) {
    MsgSender.sEmpty("DynTrack", s, ChatColor.GRAY);
    MsgSender.sInfo("DynTrack", s, ChatColor.DARK_GRAY + "-- " + ChatColor.RESET + "Information about the Style " + ChatColor.DARK_GRAY + "--");
    MsgSender.sEmpty("DynTrack", s, ChatColor.GREEN);
    MsgSender.sInfo("DynTrack", s, "ID: " + ChatColor.DARK_AQUA + st.getId());
    MsgSender.sInfo("DynTrack", s, "Line color: " + ChatColor.LIGHT_PURPLE + Integer.toHexString(st.getLineColor()));
    MsgSender.sInfo("DynTrack", s, "Line opacity: " + ChatColor.LIGHT_PURPLE + st.getLineOpacity());
    MsgSender.sInfo("DynTrack", s, "Line width: " + ChatColor.LIGHT_PURPLE + st.getLineWidth());
    MsgSender.sInfo("DynTrack", s, "Fill color: " + ChatColor.LIGHT_PURPLE + Integer.toHexString(st.getFillColor()));
    MsgSender.sInfo("DynTrack", s, "Fill opacity: " + ChatColor.LIGHT_PURPLE + st.getFillOpacity());
    MsgSender.sEmpty("DynTrack", s, ChatColor.GREEN);
    MsgSender.sInfo("DynTrack", s, ChatColor.DARK_GREEN + "/dynTrack info " + st.getId() + " # " + ChatColor.RESET + "Lists Paths, Squares");
    MsgSender.sInfo("DynTrack", s, "and Links using " + ChatColor.DARK_AQUA + st.getId() + ChatColor.RESET + ".");
    MsgSender.sEmpty("DynTrack", s, ChatColor.GREEN);
    MsgSender.sInfo("DynTrack", s, ChatColor.DARK_GRAY + "-- " + ChatColor.RESET + "This is the end of the Info-Page " + ChatColor.DARK_GRAY + "--");
  }

  static void infoStyleImpl(DynTrack dynTrack, CommandSender s, Style st, int page) {
    MsgSender.sEmpty("DynTrack", s, ChatColor.GRAY);
    HashSet < Drawable > set = new HashSet < > ();
    set.addAll(dynTrack.getPaths().values());
    set.addAll(dynTrack.getSquares().values());
    set.addAll(dynTrack.getLinks().values());
    Iterator < Drawable > it = set.iterator();

    while (it.hasNext()) {

      Drawable current = it.next();
      if (!current.getStyle().equals(st)) {
        set.remove(current);
      }
    }
    MsgSender.sInfo("DynTrack", s, "Total Elements using " + ChatColor.DARK_AQUA + st.getId() + ChatColor.RESET + ": " + ChatColor.LIGHT_PURPLE + set.size());
    if (set.size() <= 0) {
      return;
    }

    Drawable[] draws = (Drawable[]) set.toArray((Object[]) new Drawable[0]);
    page--;
    if (page < 0) {

      page = 0;
    } else if (page >= draws.length) {

      page = draws.length - 1;
    }
    int to = page + 15;
    if (to > draws.length) {
      to = draws.length;
    }
    MsgSender.sInfo("DynTrack", s, ChatColor.DARK_AQUA + st.getId() + ChatColor.RESET + "-Users " + ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + (page + 1) + ChatColor.GRAY + "-" + ChatColor.LIGHT_PURPLE + to + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + ":");

    for (int i = page; i < to; i++) {
      MsgSender.sInfo("DynTrack", s, ChatColor.LIGHT_PURPLE + "#" + (i + 1) + " " + ChatColor.RESET + draws[i].getId());
    }
  }

  static void infoLayer(CommandSender s, Layer lay) {
    MsgSender.sEmpty("DynTrack", s, ChatColor.GRAY);
    MsgSender.sInfo("DynTrack", s, ChatColor.DARK_GRAY + "-- " + ChatColor.RESET + "Information about the Layer " + ChatColor.DARK_GRAY + "--");
    MsgSender.sEmpty("DynTrack", s, ChatColor.GREEN);
    MsgSender.sInfo("DynTrack", s, "ID: " + ChatColor.DARK_AQUA + lay.getId());
    MsgSender.sInfo("DynTrack", s, "Name: " + ChatColor.AQUA + lay.getName());
    MsgSender.sInfo("DynTrack", s, ("Name as Headline: " + lay.isNameHidden()).replace("true", ChatColor.RED + "False").replace("false", ChatColor.GREEN + "True"));
    MsgSender.sInfo("DynTrack", s, ("Visible by default: " + lay.isHidden()).replace("true", ChatColor.RED + "False").replace("false", ChatColor.GREEN + "True"));
    MsgSender.sInfo("DynTrack", s, "Minzoom: " + ChatColor.LIGHT_PURPLE + lay.getMinzoom());
    MsgSender.sInfo("DynTrack", s, "Priority: " + ChatColor.LIGHT_PURPLE + lay.getPriority());
    MsgSender.sEmpty("DynTrack", s, ChatColor.GREEN);
    MsgSender.sInfo("DynTrack", s, ChatColor.DARK_GREEN + "/dynTrack info " + lay.getId() + " # " + ChatColor.RESET + "Lists Paths, Squares");
    MsgSender.sInfo("DynTrack", s, "and Links using " + ChatColor.DARK_AQUA + lay.getId() + ChatColor.RESET + ".");
    MsgSender.sEmpty("DynTrack", s, ChatColor.GREEN);
    MsgSender.sInfo("DynTrack", s, ChatColor.DARK_GRAY + "-- " + ChatColor.RESET + "This is the end of the Info-Page " + ChatColor.DARK_GRAY + "--");
  }

  static void infoLayerImpl(DynTrack dynTrack, CommandSender s, Layer lay, int page) {
    MsgSender.sEmpty("DynTrack", s, ChatColor.GRAY);
    HashSet < Drawable > set = new HashSet < > ();
    set.addAll(dynTrack.getPaths().values());
    set.addAll(dynTrack.getSquares().values());
    set.addAll(dynTrack.getLinks().values());
    Iterator < Drawable > it = set.iterator();

    while (it.hasNext()) {

      Drawable current = it.next();
      if (!current.getLayer().equals(lay)) {
        set.remove(current);
      }
    }
    MsgSender.sInfo("DynTrack", s, "Total Elements in " + ChatColor.DARK_AQUA + lay.getId() + ChatColor.RESET + ": " + ChatColor.LIGHT_PURPLE + set.size());
    if (set.size() <= 0) {
      return;
    }

    Drawable[] draws = (Drawable[]) set.toArray((Object[]) new Drawable[0]);
    page--;
    if (page < 0) {

      page = 0;
    } else if (page >= draws.length) {

      page = draws.length - 1;
    }
    int to = page + 15;
    if (to > draws.length) {
      to = draws.length;
    }
    MsgSender.sInfo("DynTrack", s, ChatColor.DARK_AQUA + lay.getId() + ChatColor.RESET + "-Users " + ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + (page + 1) + ChatColor.GRAY + "-" + ChatColor.LIGHT_PURPLE + to + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + ":");

    for (int i = page; i < to; i++) {
      MsgSender.sInfo("DynTrack", s, ChatColor.LIGHT_PURPLE + "#" + (i + 1) + " " + ChatColor.RESET + draws[i].getId());
    }
  }

  static void infoPath(CommandSender s, Path p) {
    MsgSender.sEmpty("DynTrack", s, ChatColor.GRAY);
    MsgSender.sInfo("DynTrack", s, ChatColor.DARK_GRAY + "-- " + ChatColor.RESET + "Information about the Path " + ChatColor.DARK_GRAY + "--");
    MsgSender.sEmpty("DynTrack", s, ChatColor.GREEN);
    MsgSender.sInfo("DynTrack", s, "ID: " + ChatColor.DARK_AQUA + p.getId());
    MsgSender.sInfo("DynTrack", s, "\"Path\" replaced by: " + ChatColor.AQUA + p.getPathSyn());
    MsgSender.sInfo("DynTrack", s, "Name: " + ChatColor.AQUA + p.getName());
    MsgSender.sInfo("DynTrack", s, ("Name visible: " + p.isNameHidden()).replace("true", ChatColor.RED + "False").replace("false", ChatColor.GREEN + "True"));
    MsgSender.sInfo("DynTrack", s, "\"From\" replaced by: " + ChatColor.AQUA + p.getFromSyn());
    MsgSender.sInfo("DynTrack", s, "Comes from: " + ChatColor.AQUA + p.getFromVal());
    MsgSender.sInfo("DynTrack", s, ("\"From\" visible: " + p.isFromHidden()).replace("true", ChatColor.RED + "False").replace("false", ChatColor.GREEN + "True"));
    MsgSender.sInfo("DynTrack", s, "\"To\" replaced by: " + ChatColor.AQUA + p.getToSyn());
    MsgSender.sInfo("DynTrack", s, "Goes to: " + ChatColor.AQUA + p.getToVal());
    MsgSender.sInfo("DynTrack", s, ("\"To\" visible: " + p.isToHidden()).replace("true", ChatColor.RED + "False").replace("false", ChatColor.GREEN + "True"));
    MsgSender.sInfo("DynTrack", s, ("Via-List visible: " + p.isViaHidden()).replace("true", ChatColor.RED + "False").replace("false", ChatColor.GREEN + "True"));
    MsgSender.sInfo("DynTrack", s, "\"Via\" replaced by: " + ChatColor.AQUA + p.getViaSyn());
    MsgSender.sInfo("DynTrack", s, ("Endpoints connected: " + p.isConnected()).replace("true", ChatColor.GREEN + "True").replace("false", ChatColor.RED + "False"));
    MsgSender.sInfo("DynTrack", s, "Style: " + ChatColor.DARK_AQUA + p.getStyle().getId());
    MsgSender.sInfo("DynTrack", s, "Layer: " + ChatColor.DARK_AQUA + p.getLayer().getId() + " " + ChatColor.RESET + "- " + ChatColor.AQUA + p.getLayer().getName());
    MsgSender.sInfo("DynTrack", s, "In World: " + ChatColor.DARK_AQUA + p.getWorld().getName());
    MsgSender.sInfo("DynTrack", s, "Trackpoints: " + ChatColor.LIGHT_PURPLE + p.getLocationList().size());
    MsgSender.sInfo("DynTrack", s, "Concatenations: " + ChatColor.LIGHT_PURPLE + (p.getLinks()).length);
    MsgSender.sEmpty("DynTrack", s, ChatColor.GREEN);
    MsgSender.sInfo("DynTrack", s, ChatColor.DARK_GREEN + "/dynTrack info " + p.getId() + " # " + ChatColor.RESET + "Lists the Trackpoints.");
    MsgSender.sInfo("DynTrack", s, ChatColor.DARK_GREEN + "/dynTrack info " + p.getId() + " Via # " + ChatColor.RESET + "Lists the Links.");
    MsgSender.sEmpty("DynTrack", s, ChatColor.GREEN);
    MsgSender.sInfo("DynTrack", s, ChatColor.DARK_GRAY + "-- " + ChatColor.RESET + "This is the end of the Info-Page " + ChatColor.DARK_GRAY + "--");
  }

  static void infoPathLocations(CommandSender s, Path p, int page) {
    MsgSender.sEmpty("DynTrack", s, ChatColor.GRAY);
    if (p.getLocationList().isEmpty()) {

      MsgSender.sInfo("DynTrack", s, "The Path " + ChatColor.DARK_AQUA + p.getId() + ChatColor.RESET + " has not Trackpoints.");
      return;
    }
    page--;
    if (page < 0) {

      page = 0;
    } else if (page >= p.getLocationList().size()) {

      page = p.getLocationList().size() - 1;
    }
    int to = page + 15;
    if (to > p.getLocationList().size()) {
      to = p.getLocationList().size();
    }
    MsgSender.sInfo("DynTrack", s, ChatColor.DARK_AQUA + p.getId() + ChatColor.RESET + "-Locations " + ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + (page + 1) + ChatColor.GRAY + "-" + ChatColor.LIGHT_PURPLE + to + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + ":");

    for (int i = page; i < to; i++) {
      MsgSender.sInfo("DynTrack", s, ChatColor.LIGHT_PURPLE + "#" + (i + 1) + " " + ChatColor.RESET + MethodPool.toColorLoc(p.getLocationList().get(i)));
    }
  }

  static void infoPathLinks(CommandSender s, Path p, int page) {
    MsgSender.sEmpty("DynTrack", s, ChatColor.GRAY);
    Link[] links = p.getLinks();
    if (links.length == 0) {

      MsgSender.sInfo("DynTrack", s, "The Path " + ChatColor.DARK_AQUA + p.getId() + ChatColor.RESET + " is not linked to any Link.");
      return;
    }
    page--;
    if (page < 0) {

      page = 0;
    } else if (page >= links.length) {

      page = links.length - 1;
    }
    int to = page + 15;
    if (to > links.length) {
      to = links.length;
    }
    MsgSender.sInfo("DynTrack", s, ChatColor.DARK_AQUA + p.getId() + ChatColor.RESET + "-Links " + ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + (page + 1) + ChatColor.GRAY + "-" + ChatColor.LIGHT_PURPLE + to + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + ":");

    for (int i = page; i < to; i++) {
      MsgSender.sInfo("DynTrack", s, ChatColor.LIGHT_PURPLE + "#" + (i + 1) + " " + ChatColor.DARK_AQUA + links[i].getId());
    }
  }

  static void infoSquare(CommandSender s, Square sq) {
    MsgSender.sEmpty("DynTrack", s, ChatColor.GRAY);
    MsgSender.sInfo("DynTrack", s, ChatColor.DARK_GRAY + "-- " + ChatColor.RESET + "Information about the Square " + ChatColor.DARK_GRAY + "--");
    MsgSender.sEmpty("DynTrack", s, ChatColor.GREEN);
    MsgSender.sInfo("DynTrack", s, "ID: " + ChatColor.DARK_AQUA + sq.getId());
    MsgSender.sInfo("DynTrack", s, "Name: " + ChatColor.AQUA + sq.getName());
    MsgSender.sInfo("DynTrack", s, "\"Square\" replaced by: " + ChatColor.AQUA + sq.getSquareSyn());
    MsgSender.sInfo("DynTrack", s, ("Name visible: " + sq.isNameHidden()).replace("true", ChatColor.RED + "False").replace("false", ChatColor.GREEN + "True"));
    MsgSender.sInfo("DynTrack", s, "Style: " + ChatColor.DARK_AQUA + sq.getStyle().getId());
    MsgSender.sInfo("DynTrack", s, "Layer: " + ChatColor.DARK_AQUA + sq.getLayer().getId());
    MsgSender.sInfo("DynTrack", s, "In World: " + ChatColor.DARK_AQUA + sq.getWorld().getName());
    MsgSender.sInfo("DynTrack", s, "Trackpoints: " + ChatColor.LIGHT_PURPLE + sq.getLocationList().size());
    MsgSender.sInfo("DynTrack", s, "Concatenations: " + ChatColor.LIGHT_PURPLE + (sq.getLinks()).length);
    MsgSender.sEmpty("DynTrack", s, ChatColor.GREEN);
    MsgSender.sInfo("DynTrack", s, ChatColor.DARK_GREEN + "/dynTrack info " + sq.getId() + " # " + ChatColor.RESET + "Lists the Trackpoints.");
    MsgSender.sInfo("DynTrack", s, ChatColor.DARK_GREEN + "/dynTrack info " + sq.getId() + " Via # " + ChatColor.RESET + "Lists the Links.");
    MsgSender.sEmpty("DynTrack", s, ChatColor.GREEN);
    MsgSender.sInfo("DynTrack", s, ChatColor.DARK_GRAY + "-- " + ChatColor.RESET + "This is the end of the Info-Page " + ChatColor.DARK_GRAY + "--");
  }

  static void infoSquareLocations(CommandSender s, Square sq, int page) {
    MsgSender.sEmpty("DynTrack", s, ChatColor.GRAY);
    if (sq.getLocationList().isEmpty()) {

      MsgSender.sInfo("DynTrack", s, "The Square " + ChatColor.DARK_AQUA + sq.getId() + ChatColor.RESET + " has not Trackpoints.");
      return;
    }
    page--;
    if (page < 0) {

      page = 0;
    } else if (page >= sq.getLocationList().size()) {

      page = sq.getLocationList().size() - 1;
    }
    int to = page + 15;
    if (to > sq.getLocationList().size()) {
      to = sq.getLocationList().size();
    }
    MsgSender.sInfo("DynTrack", s, ChatColor.DARK_AQUA + sq.getId() + ChatColor.RESET + "-Locations " + ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + (page + 1) + ChatColor.GRAY + "-" + ChatColor.LIGHT_PURPLE + to + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + ":");

    for (int i = page; i < to; i++) {
      MsgSender.sInfo("DynTrack", s, ChatColor.LIGHT_PURPLE + "#" + (i + 1) + " " + ChatColor.RESET + MethodPool.toColorLoc(sq.getLocationList().get(i)));
    }
  }

  static void infoSquareLinks(CommandSender s, Square sq, int page) {
    MsgSender.sEmpty("DynTrack", s, ChatColor.GRAY);
    Link[] links = sq.getLinks();
    if (links.length == 0) {

      MsgSender.sInfo("DynTrack", s, "The Square " + ChatColor.DARK_AQUA + sq.getId() + ChatColor.RESET + " is not linked to any Link.");
      return;
    }
    page--;
    if (page < 0) {

      page = 0;
    } else if (page >= links.length) {

      page = links.length - 1;
    }
    int to = page + 15;
    if (to > links.length) {
      to = links.length;
    }
    MsgSender.sInfo("DynTrack", s, ChatColor.DARK_AQUA + sq.getId() + ChatColor.RESET + "-Links " + ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + (page + 1) + ChatColor.GRAY + "-" + ChatColor.LIGHT_PURPLE + to + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + ":");

    for (int i = page; i < to; i++) {
      MsgSender.sInfo("DynTrack", s, ChatColor.LIGHT_PURPLE + "#" + (i + 1) + " " + ChatColor.DARK_AQUA + links[i].getId());
    }
  }

  static void infoLink(CommandSender s, Link link) {
    MsgSender.sEmpty("DynTrack", s, ChatColor.GRAY);
    MsgSender.sInfo("DynTrack", s, ChatColor.DARK_GRAY + "-- " + ChatColor.RESET + "Information about the Link " + ChatColor.DARK_GRAY + "--");
    MsgSender.sEmpty("DynTrack", s, ChatColor.GREEN);
    MsgSender.sInfo("DynTrack", s, "ID: " + ChatColor.DARK_AQUA + link.getId());
    MsgSender.sInfo("DynTrack", s, "Name: " + ChatColor.AQUA + link.getName());
    MsgSender.sInfo("DynTrack", s, ("Name visible: " + link.isNameHidden()).replace("true", ChatColor.RED + "False").replace("false", ChatColor.GREEN + "True"));
    MsgSender.sInfo("DynTrack", s, "\"Link\" replaced by: " + ChatColor.AQUA + link.getLinkSyn());
    MsgSender.sInfo("DynTrack", s, "Location: " + MethodPool.toColorLoc(link.getLocation()));
    MsgSender.sInfo("DynTrack", s, "Style: " + ChatColor.DARK_AQUA + link.getStyle().getId());
    MsgSender.sInfo("DynTrack", s, "Layer: " + ChatColor.DARK_AQUA + link.getLayer().getId() + " " + ChatColor.RESET + "- " + ChatColor.AQUA + link.getLayer().getName());
    MsgSender.sInfo("DynTrack", s, "Concatenations: " + ChatColor.DARK_AQUA + link.getTable().size());
    MsgSender.sEmpty("DynTrack", s, ChatColor.GREEN);
    MsgSender.sInfo("DynTrack", s, ChatColor.DARK_GREEN + "/dynTrack info " + link.getId() + " # " + ChatColor.RESET + "Lists the Concatenations.");
    MsgSender.sEmpty("DynTrack", s, ChatColor.GREEN);
    MsgSender.sInfo("DynTrack", s, ChatColor.DARK_GRAY + "-- " + ChatColor.RESET + "This is the end of the Info-Page " + ChatColor.DARK_GRAY + "--");
  }

  static void infoLinkLinked(CommandSender s, Link link, int page) {
    MsgSender.sEmpty("DynTrack", s, ChatColor.GRAY);
    Linkable[] linked = (Linkable[]) link.getTable().keySet().toArray((Object[]) new Linkable[link.getTable().keySet().size()]);
    if (linked.length == 0) {

      MsgSender.sInfo("DynTrack", s, "The Link " + ChatColor.DARK_AQUA + link.getId() + ChatColor.RESET + " is not connected with any Element.");
      return;
    }
    page--;
    if (page < 0) {

      page = 0;
    } else if (page >= linked.length) {

      page = linked.length - 1;
    }
    int to = page + 15;
    if (to > linked.length) {
      to = linked.length;
    }
    MsgSender.sInfo("DynTrack", s, ChatColor.DARK_AQUA + link.getId() + ChatColor.RESET + "-Concatenations " + ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + (page + 1) + ChatColor.GRAY + "-" + ChatColor.LIGHT_PURPLE + to + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + ":");

    for (int i = page; i < to; i++) {
      MsgSender.sInfo("DynTrack", s, ChatColor.LIGHT_PURPLE + "#" + (i + 1) + " " + ChatColor.DARK_AQUA + linked[i].getId());
    }
  }

  static void helpPlayer(Player p) {
    MsgSender.pEmpty("DynTrack", p, ChatColor.GRAY);
    MsgSender.pInfo("DynTrack", p, ChatColor.DARK_GRAY + "-- " + ChatColor.RESET + "Help-Page for DynTrack " + ChatColor.DARK_GRAY + "--");
    MsgSender.pInfo("DynTrack", p, ChatColor.GRAY + "Every Command starts with " + ChatColor.DARK_GREEN + "/dynTrack " + ChatColor.GRAY + "or " + ChatColor.DARK_GREEN + "/dynTr");
    MsgSender.pInfo("DynTrack", p, ChatColor.GRAY + "You can use " + ChatColor.DARK_GRAY + "<" + ChatColor.DARK_GREEN + "st " + ChatColor.GRAY + "= " + ChatColor.DARK_GREEN + "style " + ChatColor.DARK_GRAY + "| " + ChatColor.DARK_GREEN + "lay " + ChatColor.GRAY + "= " + ChatColor.DARK_GREEN + "layer " + ChatColor.DARK_GRAY + "| " + ChatColor.DARK_GREEN + "p " + ChatColor.GRAY + "= " + ChatColor.DARK_GREEN + "path " + ChatColor.DARK_GRAY + "|");

    MsgSender.pInfo("DynTrack", p, "                  " + ChatColor.DARK_GREEN + "sq " + ChatColor.GRAY + "= " + ChatColor.DARK_GREEN + "square " + ChatColor.DARK_GRAY + "| " + ChatColor.DARK_GREEN + "link " + ChatColor.GRAY + "= " + ChatColor.DARK_GREEN + "link" + ChatColor.DARK_GRAY + "> " + ChatColor.GRAY + "for short.");

    MsgSender.pEmpty("DynTrack", p, ChatColor.GREEN);
    MsgSender.pInfo("DynTrack", p, ChatColor.DARK_GREEN + "? " + ChatColor.DARK_GRAY + "| " + ChatColor.DARK_GREEN + "h " + ChatColor.DARK_GRAY + "| " + ChatColor.DARK_GREEN + "help");

    MsgSender.pInfo("DynTrack", p, " " + ChatColor.YELLOW + "-> " + ChatColor.RESET + "Shows this help.");
    if (PermHandler.hasPerms(p, "dyntrack.info.intro")) {

      MsgSender.pEmpty("DynTrack", p, ChatColor.GREEN);
      MsgSender.pInfo("DynTrack", p, ChatColor.DARK_GREEN + "intro " + ChatColor.DARK_GRAY + "[" + ChatColor.DARK_GREEN + "#" + ChatColor.DARK_GRAY + "]");
      MsgSender.pInfo("DynTrack", p, " " + ChatColor.YELLOW + "-> " + ChatColor.RESET + "Runs " + ChatColor.DARK_GRAY + "[" + ChatColor.RESET + "the part of" + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + " the intro.");
    }
    if (PermHandler.hasPerms(p, "dyntrack.info.plugin")) {

      MsgSender.pEmpty("DynTrack", p, ChatColor.GREEN);
      MsgSender.pInfo("DynTrack", p, ChatColor.DARK_GREEN + "info");
      MsgSender.pInfo("DynTrack", p, " " + ChatColor.YELLOW + "-> " + ChatColor.RESET + "Show basic information about DynTrack.");
    }
    if (PermHandler.hasPerms(p, "dyntrack.reload")) {

      MsgSender.pEmpty("DynTrack", p, ChatColor.GREEN);
      MsgSender.pInfo("DynTrack", p, ChatColor.DARK_GREEN + "reload");
      MsgSender.pInfo("DynTrack", p, " " + ChatColor.YELLOW + "-> " + ChatColor.RESET + "Reloads DynTrack.");
    }
    if (PermHandler.hasPerms(p, "dyntrack.info.list")) {

      MsgSender.pEmpty("DynTrack", p, ChatColor.GREEN);
      MsgSender.pInfo("DynTrack", p, ChatColor.DARK_GREEN + "list " + ChatColor.DARK_GRAY + "<" + ChatColor.DARK_GREEN + "all " + ChatColor.DARK_GRAY + "| " + ChatColor.DARK_GREEN + "styles " + ChatColor.DARK_GRAY + "| " + ChatColor.DARK_GREEN + "layers " + ChatColor.DARK_GRAY + "| " + ChatColor.DARK_GREEN + "paths " + ChatColor.DARK_GRAY + "| " + ChatColor.DARK_GREEN + "squares " + ChatColor.DARK_GRAY + "| " + ChatColor.DARK_GREEN + "links" + ChatColor.DARK_GRAY + ">");
      MsgSender.pInfo("DynTrack", p, " " + ChatColor.YELLOW + "-> " + ChatColor.RESET + "Lists " + ChatColor.DARK_GRAY + "<" + ChatColor.RESET + "all " + ChatColor.DARK_GRAY + "| " + ChatColor.RESET + "Styles " + ChatColor.DARK_GRAY + "| " + ChatColor.RESET + "Layers " + ChatColor.DARK_GRAY + "| " + ChatColor.RESET + "Paths " + ChatColor.DARK_GRAY + "| " + ChatColor.RESET + "Squares " + ChatColor.DARK_GRAY + "|");
      MsgSender.pInfo("DynTrack", p, "             Links" + ChatColor.DARK_GRAY + ">" + ChatColor.RESET + ".");
    }
    if (PermHandler.hasPerms(p, "dyntrack.info.element")) {

      MsgSender.pEmpty("DynTrack", p, ChatColor.GREEN);
      MsgSender.pInfo("DynTrack", p, ChatColor.DARK_GREEN + "info ID");
      MsgSender.pInfo("DynTrack", p, " " + ChatColor.YELLOW + "-> " + ChatColor.RESET + "Gives information about the " + ChatColor.DARK_GRAY + "<" + ChatColor.RESET + "Style " + ChatColor.DARK_GRAY + "| " + ChatColor.RESET + "Layer " + ChatColor.DARK_GRAY + "|");
      MsgSender.pInfo("DynTrack", p, "     Path " + ChatColor.DARK_GRAY + "| " + ChatColor.RESET + "Square " + ChatColor.DARK_GRAY + "| " + ChatColor.RESET + "Link" + ChatColor.DARK_GRAY + ">" + ChatColor.RESET + ".");
    }
    if (PermHandler.hasPerms(p, "dyntrack.create.*") ||
            PermHandler.hasPerms(p, "dyntrack.create.style") ||
            PermHandler.hasPerms(p, "dyntrack.create.layer") ||
            PermHandler.hasPerms(p, "dyntrack.create.path") ||
            PermHandler.hasPerms(p, "dyntrack.create.square") ||
            PermHandler.hasPerms(p, "dyntrack.create.link")) {

      MsgSender.pEmpty("DynTrack", p, ChatColor.GREEN);
      MsgSender.pInfo("DynTrack", p, ChatColor.DARK_GREEN + "create");
      MsgSender.pInfo("DynTrack", p, " " + ChatColor.YELLOW + "-> " + ChatColor.RESET + "Starts the Setup.");
    }
    if (PermHandler.hasPerms(p, "dyntrack.edit.*") ||
            PermHandler.hasPerms(p, "dyntrack.edit.style") ||
            PermHandler.hasPerms(p, "dyntrack.edit.layer") ||
            PermHandler.hasPerms(p, "dyntrack.edit.path") ||
            PermHandler.hasPerms(p, "dyntrack.edit.square") ||
            PermHandler.hasPerms(p, "dyntrack.edit.link")) {

      MsgSender.pEmpty("DynTrack", p, ChatColor.GREEN);
      MsgSender.pInfo("DynTrack", p, ChatColor.DARK_GREEN + "edit");
      MsgSender.pInfo("DynTrack", p, " " + ChatColor.YELLOW + "-> " + ChatColor.RESET + "Starts the Editor.");
    }
    if (PermHandler.hasPerms(p, "dyntrack.del.style") ||
            PermHandler.hasPerms(p, "dyntrack.del.layer") ||
            PermHandler.hasPerms(p, "dyntrack.del.path") ||
            PermHandler.hasPerms(p, "dyntrack.del.square") ||
            PermHandler.hasPerms(p, "dyntrack.del.link")) {

      MsgSender.pEmpty("DynTrack", p, ChatColor.GREEN);
      MsgSender.pInfo("DynTrack", p, ChatColor.DARK_GREEN + "delete ID");
      MsgSender.pInfo("DynTrack", p, " " + ChatColor.YELLOW + "-> " + ChatColor.RESET + "Deletes the " + ChatColor.DARK_GRAY + "<" + ChatColor.RESET + "Style " + ChatColor.DARK_GRAY + "| " + ChatColor.RESET + "Layer " + ChatColor.DARK_GRAY + "| " + ChatColor.RESET + "Path " + ChatColor.DARK_GRAY + "| " + ChatColor.RESET + "Square " + ChatColor.DARK_GRAY + "|");
      MsgSender.pInfo("DynTrack", p, "                     Link" + ChatColor.DARK_GRAY + ">" + ChatColor.RESET + ".");
    }
    MsgSender.pEmpty("DynTrack", p, ChatColor.GREEN);
    MsgSender.pInfo("DynTrack", p, ChatColor.DARK_GRAY + "-- " + ChatColor.RESET + "This is the end of the Help-Page " + ChatColor.DARK_GRAY + "--");
  }

  static void helpConsole() {
    MsgSender.cEmpty("DynTrack");
    MsgSender.cInfo("DynTrack", "-- Help-page for DynTrack --");
    MsgSender.cInfo("DynTrack", "Every Command starts with dynTrack btw. dynTr");
    MsgSender.cInfo("DynTrack", "You can use <st = style | lay = layer | p = path | sq = square | link = link> for short.");
    MsgSender.cEmpty("DynTrack");
    MsgSender.cInfo("DynTrack", "<? | h | help>");
    MsgSender.cInfo("DynTrack", " -> Shows this help.");
    MsgSender.cEmpty("DynTrack");
    MsgSender.cInfo("DynTrack", "intro [#]");
    MsgSender.cInfo("DynTrack", " -> Runs [the part of] the intro.");
    MsgSender.cEmpty("DynTrack");
    MsgSender.cInfo("DynTrack", "reload");
    MsgSender.cInfo("DynTrack", " -> Reloads DynTrack.");
    MsgSender.cEmpty("DynTrack");
    MsgSender.cInfo("DynTrack", "list <all | styles | layers | paths | squares | links>");
    MsgSender.cInfo("DynTrack", " -> Lists <all | Styles | Layers | Paths | Squares | Links>.");
    MsgSender.cEmpty("DynTrack");
    MsgSender.cInfo("DynTrack", "info ID");
    MsgSender.cInfo("DynTrack", " -> Gives information about the <Style | Layer | Path | Square | Link>.");
    MsgSender.cEmpty("DynTrack");
    MsgSender.cInfo("DynTrack", "create");
    MsgSender.cInfo("DynTrack", " -> Runs the Setup.");
    MsgSender.cEmpty("DynTrack");
    MsgSender.cInfo("DynTrack", "edit");
    MsgSender.cInfo("DynTrack", " -> Runs the Editor.");
    MsgSender.cEmpty("DynTrack");
    MsgSender.cInfo("DynTrack", "delete ID");
    MsgSender.cInfo("DynTrack", " -> Deletes the <Style | Layer | Path | Square | Link>.");
    MsgSender.cEmpty("DynTrack");
    MsgSender.cInfo("DynTrack", "import");
    MsgSender.cInfo("DynTrack", " -> Imports Styles, Layers, Paths, Squares and Links from files into the Database.");
    MsgSender.cEmpty("DynTrack");
    MsgSender.cInfo("DynTrack", "export");
    MsgSender.cInfo("DynTrack", " -> Exports Styles, Layers, Paths, Squares and Links from the Database into files.");
    MsgSender.cEmpty("DynTrack");
    MsgSender.cInfo("DynTrack", "-- This is the end of the help-page --");
  }
}