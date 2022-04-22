package de.odinoxin.dyntrack;

import de.odinoxin.dyntrack.enums.DataActionResult;
import de.odinoxin.dyntrack.generals.DataEvent;
import de.odinoxin.dyntrack.generals.MsgSender;
import de.odinoxin.dyntrack.interfaces.Linkable;
import de.odinoxin.dyntrack.layer.Layer;
import de.odinoxin.dyntrack.link.Link;
import de.odinoxin.dyntrack.path.Path;
import de.odinoxin.dyntrack.square.Square;
import de.odinoxin.dyntrack.style.Style;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;
import org.dynmap.markers.PolyLineMarker;

class DynmapHandler
        implements Listener {
  private final DynTrack DYNTRACK;
  private Plugin dynmap;
  private DynmapAPI dynmapAPI;
  private MarkerAPI markerAPI;
  private static final String infoWindowPath = "<div class=\"regioninfo\"><span style=\"font-weight:bold;color:green\">[ %layerName% ]</span><br1/><br1/><span style=\"font-weight:bold;\">%pathSyn%: </span>%pathName%<br2/><span style=\"font-weight:bold;\">%fromSyn%: </span>%from%<br3/><span style=\"font-weight:bold;\">%toSyn%: </span>%to%<br4/>%via%</div>";
  private static final String infoWindowSquare = "<div class=\"regioninfo\"><span style=\"font-weight:bold;color:green\">[ %layerName% ]</span><br1/><br1/><span style=\"font-weight:bold;\">%sqSyn%: </span>%sqName%</div>";
  private static final String infoWindowLink = "<div class=\"regioninfo\"><span style=\"font-weight:bold;color:green\">[ %layerName% ]</span><br1/><br1/><span style=\"font-weight:bold;\">%linkSyn%: </span>%linkId%</div>";

  DynmapHandler(DynTrack DYNTRACK) {
    try {
      this.dynmap = DYNTRACK.getServer().getPluginManager().getPlugin("dynmap");
      this.dynmapAPI = (DynmapAPI) this.dynmap;
      this.markerAPI = this.dynmapAPI.getMarkerAPI();
    } catch (NullPointerException e) {

      MsgSender.cBug(DYNTRACK, "Dynmap was not found!");
      DYNTRACK.getServer().getPluginManager().disablePlugin(DYNTRACK);
    } finally {

      this.DYNTRACK = DYNTRACK;
    }
  }

  @EventHandler
  public void updateStyle(DataEvent < Style > ev) {
    if (!ev.isCancelled() && ev
            .getData() != null && ev
            .getAction() == DataActionResult.SAVED) {

      int updatedElements = 0;
      Iterator < Path > itPath = this.DYNTRACK.getPaths().values().iterator();

      if (itPath.hasNext())
        MsgSender.cInfo(this.DYNTRACK, "Dynmap: Updating all Paths, using the Style: " + ev.getData().getId());
      while (itPath.hasNext()) {

        Path p = itPath.next();
        if (p.getStyle().equals(ev.getData())) {

          add(p);
          updatedElements++;
        }
      }
      Iterator < Square > itSquare = this.DYNTRACK.getSquares().values().iterator();

      if (itSquare.hasNext())
        MsgSender.cInfo(this.DYNTRACK, "Dynmap: Updating all Squares, using the Style: " + ev.getData().getId());
      while (itSquare.hasNext()) {

        Square sq = itSquare.next();
        if (sq.getStyle().equals(ev.getData())) {

          add(sq);
          updatedElements++;
        }
      }
      Iterator < Link > itLink = this.DYNTRACK.getLinks().values().iterator();

      if (itLink.hasNext())
        MsgSender.cInfo(this.DYNTRACK, "Dynmap: Updating all Links, using the Style: " + ev.getData().getId());
      while (itLink.hasNext()) {

        Link link = itLink.next();
        if (link.getStyle().equals(ev.getData())) {

          add(link);
          updatedElements++;
        }
      }
      if (updatedElements > 0)
        MsgSender.cInfo(this.DYNTRACK, "Dynmap: Updated " + updatedElements + " elements, using the Style: " + ev.getData().getId());
    }
  }

  @EventHandler
  public void updateLayer(DataEvent < Layer > ev) {
    if (!ev.isCancelled() && ev
            .getData() != null) {
      if (ev.getAction() == DataActionResult.DELETED_SUCCESSFULLY) {

        remove(ev.getData(), true);
      } else if (ev.getAction() == DataActionResult.SAVED) {

        int updatedElements = 0;
        Iterator < Path > itPath = this.DYNTRACK.getPaths().values().iterator();

        if (itPath.hasNext())
          MsgSender.cInfo(this.DYNTRACK, "Dynmap: Updating all Paths, inside the Layer: " + ev.getData().getId());
        while (itPath.hasNext()) {

          Path p = itPath.next();
          if (p.getLayer().equals(ev.getData())) {

            add(p);
            updatedElements++;
          }
        }
        Iterator < Square > itSquare = this.DYNTRACK.getSquares().values().iterator();

        if (itSquare.hasNext())
          MsgSender.cInfo(this.DYNTRACK, "Dynmap: Updating all Squares, inside the Layer: " + ev.getData().getId());
        while (itSquare.hasNext()) {

          Square sq = itSquare.next();
          if (sq.getLayer().equals(ev.getData())) {

            add(sq);
            updatedElements++;
          }
        }
        Iterator < Link > itLink = this.DYNTRACK.getLinks().values().iterator();

        if (itLink.hasNext())
          MsgSender.cInfo(this.DYNTRACK, "Dynmap: Updating all Links, inside the Layer: " + ev.getData().getId());
        while (itLink.hasNext()) {

          Link link = itLink.next();
          if (link.getLayer().equals(ev.getData())) {

            add(link);
            updatedElements++;
          }
        }
        if (updatedElements > 0)
          MsgSender.cInfo(this.DYNTRACK, "Dynmap: Updated " + updatedElements + " elements, inside the Layer: " + ev.getData().getId());
      }
    }
  }

  @EventHandler
  public void updatePath(DataEvent < Path > ev) {
    if (!ev.isCancelled() && ev
            .getData() != null) {
      if (ev.getAction() == DataActionResult.DELETED_SUCCESSFULLY) {

        remove(ev.getData(), true);
      } else if (ev.getAction() == DataActionResult.SAVED) {

        int updatedElements = 0;
        if ((ev.getData().getLinks()).length > 0)
          MsgSender.cInfo(this.DYNTRACK, "Dynmap: Updating all Links, connected to: " + ev.getData().getId());
        for (int i = 0; i < (ev.getData().getLinks()).length; i++) {

          add(ev.getData().getLinks()[i]);
          updatedElements++;
        }
        add(ev.getData());
        if (updatedElements > 0)
          MsgSender.cInfo(this.DYNTRACK, "Dynmap: " + updatedElements + " Links updated, connected to the Path: " + ev.getData().getId());
      }
    }
  }

  @EventHandler
  public void updateSquare(DataEvent < Square > ev) {
    if (!ev.isCancelled() && ev
            .getData() != null) {
      if (ev.getAction() == DataActionResult.DELETED_SUCCESSFULLY) {

        remove(ev.getData(), true);
      } else if (ev.getAction() == DataActionResult.SAVED) {

        int updatedElements = 0;
        if ((ev.getData().getLinks()).length > 0)
          MsgSender.cInfo(this.DYNTRACK, "Dynmap: Updating all Links, that are connected to: " + ev.getData().getId());
        for (int i = 0; i < (ev.getData().getLinks()).length; i++) {

          add(ev.getData().getLinks()[i]);
          updatedElements++;
        }
        add(ev.getData());
        if (updatedElements > 0)
          MsgSender.cInfo(this.DYNTRACK, "Dynmap: " + updatedElements + " Links updated, connected to the Square: " + ev.getData().getId());
      }
    }
  }

  @EventHandler
  public void updateLink(DataEvent < Link > ev) {
    if (!ev.isCancelled() && ev
            .getData() != null) {
      if (ev.getAction() == DataActionResult.DELETED_SUCCESSFULLY) {

        remove(ev.getData(), true);
      } else if (ev.getAction() == DataActionResult.SAVED) {

        add(ev.getData());
      }
    }
  }

  private MarkerSet getMarkerSet(Layer l) {
    MarkerSet ms = this.markerAPI.getMarkerSet(l.getId() + ".markerset");
    if (ms == null) {
      ms = this.markerAPI.createMarkerSet(l.getId() + ".markerset", l.getName(), null, true);
    }
    ms.setHideByDefault(l.isHidden());
    ms.setMinZoom(l.getMinzoom());
    ms.setLayerPriority(l.getPriority());
    return ms;
  }

  void add(Path p) {
    if (p == null || p
            .getWorld() == null) {
      return;
    }

    remove(p, false);
    MsgSender.cInfo(this.DYNTRACK, "Dynmap: Now drawing Path: " + p.getId());
    String desc = "<div class=\"regioninfo\"><span style=\"font-weight:bold;color:green\">[ %layerName% ]</span><br1/><br1/><span style=\"font-weight:bold;\">%pathSyn%: </span>%pathName%<br2/><span style=\"font-weight:bold;\">%fromSyn%: </span>%from%<br3/><span style=\"font-weight:bold;\">%toSyn%: </span>%to%<br4/>%via%</div>";
    if (p.getLayer().isNameHidden()) {

      desc = desc.replace("<span style=\"font-weight:bold;color:green\">[ %layerName% ]</span>", "");
    } else {

      desc = desc.replace("%layerName%", p.getLayer().getName());
    }
    if (p.getLayer().isNameHidden() || (p
            .isNameHidden() && p
            .isFromHidden() && p
            .isToHidden() && p
            .isViaHidden())) {

      desc = desc.replace("<br1/>", "");
    } else {

      desc = desc.replace("<br1/>", "<br />");
    }
    if (p.isNameHidden()) {

      desc = desc.replace("<span style=\"font-weight:bold;\">%pathSyn%: </span>%pathName%", "");
    } else {

      desc = desc.replace("%pathSyn%", p.getPathSyn());
      desc = desc.replace("%pathName%", p.getName());
    }
    if (p.isNameHidden() || (p
            .isFromHidden() && p
            .isToHidden() && p
            .isViaHidden())) {

      desc = desc.replace("<br2/>", "");
    } else {

      desc = desc.replace("<br2/>", "<br />");
    }
    if (p.isFromHidden()) {

      desc = desc.replace("<span style=\"font-weight:bold;\">%fromSyn%: </span>%from%", "");
    } else {

      desc = desc.replace("%fromSyn%", p.getFromSyn());
      desc = desc.replace("%from%", p.getFromVal());
    }
    if (p.isFromHidden() || (p
            .isToHidden() && p
            .isViaHidden())) {

      desc = desc.replace("<br3/>", "");
    } else {

      desc = desc.replace("<br3/>", "<br />");
    }
    if (p.isToHidden()) {

      desc = desc.replace("<span style=\"font-weight:bold;\">%toSyn%: </span>%to%", "");
    } else {

      desc = desc.replace("%toSyn%", p.getToSyn());
      desc = desc.replace("%to%", p.getToVal());
    }
    if (p.isToHidden() || p
            .isViaHidden()) {

      desc = desc.replace("<br4/>", "");
    } else {

      desc = desc.replace("<br4/>", "<br />");
    }
    if (!p.isViaHidden()) {

      Link[] links = p.getLinks();
      desc = desc.replace("%via%", "<span style=\"font-weight:bold;\">" + p.getViaSyn() + ":</span>%via%");
      for (Link link : links) {

        if (link.getTable().size() <= 1) {

          desc = desc.replace("%via%", "<br /> - " + link.getName() + "%via%");
        } else {

          boolean wasFirst = false;
          Linkable[] linked = link.getLinkables();
          for (Linkable linkable : linked) {

            if (!linkable.equals(p)) {

              if (!wasFirst) {

                desc = desc.replace("%via%", "<br /> - " + linkable.getName() + "%via%");
                wasFirst = true;
              } else {

                desc = desc.replace("%via%", ", " + linkable.getName() + "%via%");
              }
            }
          }
        }
      }
    }
    desc = desc.replace("%via%", "");

    PolyLineMarker plm = getMarkerSet(p.getLayer()).findPolyLineMarkerByLabel(p.getId());
    if (plm != null) {
      plm.deleteMarker();
    }
    plm = getMarkerSet(p.getLayer()).createPolyLineMarker(p.getId(), p
            .getId(), true, p

            .getWorld().getName(), p
            .getxArray(), p
            .getyArray(), p
            .getzArray(), true);

    if (p.isConnected() && p
            .getLocationList().size() > 0) {
      plm.setCornerLocation(p.getLocationList().size(), p
              .getLocationList().get(0).getX(), p
              .getLocationList().get(0).getY(), p
              .getLocationList().get(0).getZ());
    }
    plm.setLineStyle(p.getStyle().getLineWidth(), p.getStyle().getLineOpacity(), p.getStyle().getLineColor());
    plm.setLabel(p.getId());
    plm.setDescription(desc);
    MsgSender.cInfo(this.DYNTRACK, "Dynmap: Path drawn.");
  }

  void add(Square sq) {
    if (sq == null) {
      return;
    }

    remove(sq, false);
    MsgSender.cInfo(this.DYNTRACK, "Dynmap: Now drawing Square: " + sq.getId());
    String desc = "<div class=\"regioninfo\"><span style=\"font-weight:bold;color:green\">[ %layerName% ]</span><br1/><br1/><span style=\"font-weight:bold;\">%sqSyn%: </span>%sqName%</div>";
    if (sq.getLayer().isNameHidden()) {

      desc = desc.replace("<span style=\"font-weight:bold;color:green\">[ %layerName% ]</span>", "");
    } else {

      desc = desc.replace("%layerName%", sq.getLayer().getName());
    }
    if (sq.getLayer().isNameHidden() || sq
            .isNameHidden()) {

      desc = desc.replace("<br1/>", "");
    } else {

      desc = desc.replace("<br1/>", "<br />");
    }
    if (sq.isNameHidden()) {

      desc = desc.replace("<span style=\"font-weight:bold;\">%sqSyn%: </span>%sqName%", "");
    } else {

      desc = desc.replace("%sqSyn%", sq.getSquareSyn());
      desc = desc.replace("%sqName%", sq.getName());
    }
    double[] y = sq.getyArray();
    double average = 0.0D;
    for (double v : y) {
      average += v;
    }
    average /= y.length;

    AreaMarker arm = getMarkerSet(sq.getLayer()).findAreaMarkerByLabel(sq.getId());
    if (arm != null) {
      arm.deleteMarker();
    }
    arm = getMarkerSet(sq.getLayer()).createAreaMarker(sq.getId(), sq
            .getId(), true, sq

            .getWorld().getName(), sq
            .getxArray(), sq
            .getzArray(), true);

    arm.setLineStyle(sq.getStyle().getLineWidth(), sq.getStyle().getLineOpacity(), sq.getStyle().getLineColor());
    arm.setFillStyle(sq.getStyle().getFillOpacity(), sq.getStyle().getFillColor());
    arm.setLabel(sq.getId());
    arm.setDescription(desc);
    arm.setRangeY(average, average);
    MsgSender.cInfo(this.DYNTRACK, "Dynmap: Square drawn.");
  }

  void add(Link link) {
    if (link == null) {
      return;
    }

    remove(link, false);
    MsgSender.cInfo(this.DYNTRACK, "Dynmap: Now drawing Link: " + link.getId());
    String desc = "<div class=\"regioninfo\"><span style=\"font-weight:bold;color:green\">[ %layerName% ]</span><br1/><br1/><span style=\"font-weight:bold;\">%linkSyn%: </span>%linkId%</div>";
    if (link.getLayer().isNameHidden()) {

      desc = desc.replace("<span style=\"font-weight:bold;color:green\">[ %layerName% ]</span>", "");
    } else {

      desc = desc.replace("%layerName%", link.getLayer().getName());
    }
    if (link.getLayer().isNameHidden() || link
            .isNameHidden()) {

      desc = desc.replace("<br1/>", "");
    } else {

      desc = desc.replace("<br1/>", "<br />");
    }
    if (link.isNameHidden()) {

      desc = desc.replace("<span style=\"font-weight:bold;\">%linkSyn%: </span>%linkId%", "");
    } else {

      desc = desc.replace("%linkSyn%", link.getLinkSyn());
      desc = desc.replace("%linkId%", link.getName());
    }
    if (link.hasEdging()) {

      Location[] locList = new Location[link.getTable().size() + 1];
      Linkable[] linked = link.getLinkables();
      for (int i = 0; i < linked.length; i++) {
        locList[i] = linked[i].getLocationList().get(link.getTable().get(linked[i]));
      }
      locList[locList.length - 1] = link.getLocation();
      locList = getOutline(locList);
      double[] xOutline = new double[locList.length];
      double[] zOutline = new double[locList.length];
      for (int j = 0; j < locList.length; j++) {

        xOutline[j] = locList[j].getX();
        zOutline[j] = locList[j].getZ();
      }

      AreaMarker arm = getMarkerSet(link.getLayer()).findAreaMarkerByLabel(link.getId());
      if (arm != null) {
        arm.deleteMarker();
      }
      arm = getMarkerSet(link.getLayer()).createAreaMarker(link.getId(), link
              .getId(), true, link

              .getLocation().getWorld().getName(), xOutline, zOutline, true);

      arm.setLineStyle(link.getStyle().getLineWidth(), link.getStyle().getLineOpacity(), link.getStyle().getLineColor());
      arm.setFillStyle(link.getStyle().getFillOpacity(), link.getStyle().getFillColor());
      arm.setLabel(link.getId());
      arm.setDescription(desc);
    }
    if (!link.isHidden()) {

      Linkable[] linked = link.getTable().keySet().toArray(new Linkable[0]);
      for (Linkable linkable : linked) {

        double[] x = new double[2];
        double[] y = new double[2];
        double[] z = new double[2];
        x[0] = link.getLocation().getX();
        y[0] = link.getLocation().getY();
        z[0] = link.getLocation().getZ();
        x[1] = linkable.getLocationList().get(link.getTable().get(linkable)).getX();
        y[1] = linkable.getLocationList().get(link.getTable().get(linkable)).getY();
        z[1] = linkable.getLocationList().get(link.getTable().get(linkable)).getZ();
        PolyLineMarker plm = getMarkerSet(link.getLayer()).createPolyLineMarker(link.getId() + "-" + linkable.getId(), link
                .getId() + "-" + linkable.getId(), true, link

                .getLocation().getWorld().getName(), x, y, z, true);

        plm.setLineStyle(link.getStyle().getLineWidth(), link.getStyle().getLineOpacity(), link.getStyle().getLineColor());
        plm.setLabel(link.getId() + "-" + linkable.getId());
        plm.setDescription(desc);
      }
    }
    MsgSender.cInfo(this.DYNTRACK, "Dynmap: Link drawn.");
  }

  void remove(Layer lay, boolean print) {
    getMarkerSet(lay).deleteMarkerSet();
    if (print) {
      MsgSender.cInfo(this.DYNTRACK, "Dynmap: Removed Layer  \"" + lay.getId() + "\".");
    }
  }

  void remove(Path p, boolean print) {
    PolyLineMarker plm = getMarkerSet(p.getLayer()).findPolyLineMarkerByLabel(p.getId());
    if (plm != null) {
      plm.deleteMarker();
    }
    if (print) {
      MsgSender.cInfo(this.DYNTRACK, "Dynmap: Removed Path  \"" + p.getId() + "\".");
    }
  }

  void remove(Square sq, boolean print) {
    AreaMarker arm = getMarkerSet(sq.getLayer()).findAreaMarkerByLabel(sq.getId());
    if (arm != null) {
      arm.deleteMarker();
    }
    if (print) {
      MsgSender.cInfo(this.DYNTRACK, "Dynmap: Removed Square \"" + sq.getId() + "\".");
    }
  }

  void remove(Link link, boolean print) {
    AreaMarker arm = getMarkerSet(link.getLayer()).findAreaMarkerByLabel(link.getId());
    if (arm != null) {
      arm.deleteMarker();
    }
    Linkable[] linked = link.getTable().keySet().toArray(new Linkable[0]);
    for (int i = 0; i < link.getTable().size(); i++) {

      PolyLineMarker plm = getMarkerSet(link.getLayer()).findPolyLineMarkerByLabel(link.getId() + "-" + linked[i].getId());
      if (plm != null) {
        plm.deleteMarker();
      }
    }
    if (print) {
      MsgSender.cInfo(this.DYNTRACK, "Dynmap: Removed Link \"" + link.getId() + "\".");
    }
  }

  private Location[] getOutline(Location[] locList) {
    if (locList.length == 0) {
      return locList;
    }
    List < Location > retur = new ArrayList < > ();

    Location up = locList[0];
    Location left = locList[0];
    Location down = locList[0];
    Location right = locList[0];

    for (Location location : locList) {

      if (location.getZ() < up.getZ() || (location
              .getZ() == up.getZ() && location
              .getX() < up.getX())) {
        up = location;
      }
      if (location.getX() < left.getX() || (location
              .getX() == left.getX() && location
              .getZ() > left.getX())) {
        left = location;
      }
      if (location.getZ() > down.getZ() || (location
              .getZ() == down.getZ() && location
              .getX() > down.getX())) {
        down = location;
      }
      if (location.getX() > right.getX() || (location
              .getX() == right.getX() && location
              .getZ() < right.getZ())) {
        right = location;
      }
    }
    retur.add(up);

    Location current = getNextFromUpLeft(up, locList, left);
    while (!current.equals(left)) {

      retur.add(current);
      current = getNextFromUpLeft(current, locList, left);
    }
    if (!left.equals(up)) {
      retur.add(left);
    }

    current = getNextFromDownLeft(current, locList, down);
    while (!current.equals(down)) {

      retur.add(current);
      current = getNextFromDownLeft(current, locList, down);
    }
    if (!down.equals(left)) {
      retur.add(down);
    }

    current = getNextFromDownRight(current, locList, right);
    while (!current.equals(right)) {

      retur.add(current);
      current = getNextFromDownRight(current, locList, right);
    }
    if (!right.equals(down)) {
      retur.add(right);
    }

    current = getNextFromUpRight(current, locList, up);
    while (!current.equals(up)) {

      retur.add(current);
      current = getNextFromUpRight(current, locList, up);
    }
    if (!up.equals(right)) {
      retur.add(up);
    }

    return retur.toArray(new Location[0]);
  }

  private Location getNextFromUpLeft(Location from, Location[] loc, Location left) {
    if (from == null || loc.length == 0) {

      return null;
    }
    double angle = -90.0D;
    Location retur = left;
    for (Location location : loc) {

      if (!location.equals(from) && location
              .getX() <= from.getX() && location
              .getZ() > from.getZ()) {

        if (location.getX() == from.getX()) {

          if (angle == -90.0D) {
            retur = location;

          }

        } else if (Math.tanh((location.getZ() - from.getZ()) / (location.getX() - from.getX())) > angle) {

          retur = location;
          angle = Math.tanh((location.getZ() - from.getZ()) / (location.getX() - from.getX()));
        }
      }
    }
    return retur;
  }

  private Location getNextFromDownLeft(Location from, Location[] loc, Location down) {
    if (from == null || loc.length == 0) {

      return null;
    }
    double angle = 0.0D;
    Location retur = down;
    for (Location location : loc) {

      if (!location.equals(from) && location
              .getX() > from.getX() && location
              .getZ() >= from.getZ()) {

        if (Math.tanh((location.getZ() - from.getZ()) / (location.getX() - from.getX())) > angle) {

          retur = location;
          angle = Math.tanh((location.getZ() - from.getZ()) / (location.getX() - from.getX()));
        }
      }
    }
    return retur;
  }

  private Location getNextFromDownRight(Location from, Location[] loc, Location right) {
    if (from == null || loc.length == 0) {

      return null;
    }
    double angle = -90.0D;
    Location retur = right;
    for (Location location : loc) {

      if (!location.equals(from) && location
              .getX() >= from.getX() && location
              .getZ() < from.getZ()) {

        if (location.getX() == from.getX()) {

          if (angle == -90.0D) {
            retur = location;

          }

        } else if (Math.tanh((location.getZ() - from.getZ()) / (location.getX() - from.getX())) > angle) {

          retur = location;
          angle = Math.tanh((location.getZ() - from.getZ()) / (location.getX() - from.getX()));
        }
      }
    }
    return retur;
  }

  private Location getNextFromUpRight(Location from, Location[] loc, Location up) {
    if (from == null || loc.length == 0) {

      return null;
    }
    double angle = 0.0D;
    Location retur = up;
    for (Location location : loc) {

      if (!location.equals(from) && location
              .getX() < from.getX() && location
              .getZ() <= from.getZ()) {

        if (location.getZ() == from.getZ()) {

          if (angle == 0.0D) {
            retur = location;

          }

        } else if (Math.tanh((location.getZ() - from.getZ()) / (location.getX() - from.getX())) > angle) {

          retur = location;
          angle = Math.tanh((location.getZ() - from.getZ()) / (location.getX() - from.getX()));
        }
      }
    }
    return retur;
  }
}