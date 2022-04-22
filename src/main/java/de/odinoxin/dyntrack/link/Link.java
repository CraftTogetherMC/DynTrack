package de.odinoxin.dyntrack.link;

import de.odinoxin.dyntrack.DBHandler;
import de.odinoxin.dyntrack.DynTrack;
import de.odinoxin.dyntrack.enums.DataActionResult;
import de.odinoxin.dyntrack.generals.DataEvent;
import de.odinoxin.dyntrack.generals.MethodPool;
import de.odinoxin.dyntrack.generals.MsgSender;
import de.odinoxin.dyntrack.interfaces.Drawable;
import de.odinoxin.dyntrack.interfaces.Linkable;
import de.odinoxin.dyntrack.layer.Layer;
import de.odinoxin.dyntrack.path.Path;
import de.odinoxin.dyntrack.square.Square;
import de.odinoxin.dyntrack.style.Style;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class Link implements Cloneable, Drawable {
  private String id = "";
  private String name = "";
  private String linkSyn = "Link";

  private boolean hideName = false;

  private boolean hide = false;

  private boolean edging = false;
  private Location loc;
  private Style style;
  private Layer layer;
  private Hashtable < Linkable, Integer > linked;
  private final DynTrack DYNTRACK;

  public Link(DynTrack DYNTRACK) {
    this.DYNTRACK = DYNTRACK;
    this.linked = new Hashtable < > ();
  }

  public Link(DynTrack DYNTRACK, String id, String name, String linkSyn, boolean hideName, boolean hide, boolean edging, Location loc, Style style, Layer layer, Hashtable < Linkable, Integer > linked) {
    this(DYNTRACK);
    this.id = id;
    this.name = name;
    this.linkSyn = linkSyn;
    this.hideName = hideName;
    this.hide = hide;
    this.edging = edging;
    this.loc = loc;
    this.style = style;
    this.layer = layer;
    this.linked = linked;
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof Link)) {
      return false;
    }
    Link link = (Link) obj;
    return link.getId().equals(this.id);
  }

  public Link clone() {
    try {
      return (Link) super.clone();
    } catch (CloneNotSupportedException e) {

      return null;
    }
  }

  public static Link loadLink(DynTrack DYNTRACK, String id) throws JDOMException, IOException, SQLException {
    if (!DYNTRACK.getDynTrackConfig().useDB()) {

      Link link = new Link(DYNTRACK);
      File f = new File(DYNTRACK.getDataFolder() + "/Links/" + id + ".xml");
      Document doc = (new SAXBuilder()).build(f);
      Element root = doc.getRootElement();
      link.setId(root.getChildTextNormalize("id"));
      Element infobox = root.getChild("infobox");
      link.setName(infobox.getChildTextNormalize("name"));
      link.setLinkSyn(infobox.getChildTextNormalize("syn"));
      link.setHideName(Boolean.parseBoolean(infobox.getChildTextNormalize("hide")));
      Element dynmap = root.getChild("dynmap");
      link.setHide(Boolean.parseBoolean(dynmap.getChildTextNormalize("hide")));
      link.setEdging(Boolean.parseBoolean(dynmap.getChildTextNormalize("edging")));
      link.setLocation(MethodPool.toLoc(root.getChildTextNormalize("location"), DYNTRACK));
      if (link.getLocation().getWorld() == null) {

        MsgSender.cBug(DYNTRACK, "The location " + root.getChildTextNormalize("location") + " was not found.");
        return null;
      }
      link.setStyle(DYNTRACK.getStyle(root.getChildTextNormalize("style")));
      if (link.getStyle() == null) {

        MsgSender.cBug(DYNTRACK, "The Style " + root.getChildTextNormalize("style") + " was not found.");
        return null;
      }
      link.setLayer(DYNTRACK.getLayer(root.getChildTextNormalize("layer")));
      if (link.getLayer() == null) {

        MsgSender.cBug(DYNTRACK, "The Layer " + root.getChildTextNormalize("layer") + " was not found.");
        return null;
      }
      List < Element > linked = root.getChild("linked").getChildren();
      Linkable linkable = null;
      int index = -1;
      for (int i = 0; i < linked.size(); i++) {

        linkable = DYNTRACK.getLinkable(linked.get(i).getAttribute("id").getValue());
        if (linkable == null) {

          MsgSender.cBug(DYNTRACK, "The Element " + linked.get(i).getAttribute("id").getValue() + ", which the Link " + link.getId() + " was looking for, was not found.");
        } else {

          index = linked.get(i).getAttribute("i").getIntValue();
          if (index < 0 || index >= linkable
                  .getLocationList().size())

          {
            MsgSender.cBug(DYNTRACK, "The index of the Location in the Element " + linked.get(i).getAttribute("id").getValue() + ", which the Link " + link.getId() + " was looking for, was not found.");
          } else

          {
            link.add(linkable, index);
          }
        }
      }
      return link;
    }

    DBHandler dbHandler = DYNTRACK.getDBHandler();
    String prefix = DYNTRACK.getDynTrackConfig().getDB_Prefix();
    ResultSet res1 = dbHandler.exeQuery("SELECT * FROM " + prefix + "links WHERE id LIKE \"" + id + "\";");
    if (res1.first()) {

      Link link = new Link(DYNTRACK);
      link.setId(id);
      link.setName(res1.getString("name"));
      link.setLinkSyn(res1.getString("linksyn"));
      link.setHideName(res1.getBoolean("hidename"));
      link.setHide(res1.getBoolean("hide"));
      link.setEdging(res1.getBoolean("edging"));
      World w = DYNTRACK.getServer().getWorld(res1.getString("world"));
      if (w == null) {

        MsgSender.cBug(DYNTRACK, "The world " + res1.getString("world") + " was not found.");
        res1.close();
        return null;
      }
      link.setLocation(new Location(w, res1.getDouble("x"), res1.getDouble("y"), res1.getDouble("z")));
      Style st = DYNTRACK.getStyle(res1.getString("style"));
      if (st == null) {

        MsgSender.cBug(DYNTRACK, "The Style " + res1.getString("style") + " was not found.");
        res1.close();
        return null;
      }
      link.setStyle(st);
      Layer lay = DYNTRACK.getLayer(res1.getString("layer"));
      if (lay == null) {

        MsgSender.cBug(DYNTRACK, "The Layer " + res1.getString("layer") + " was not found.");
        res1.close();
        return null;
      }
      link.setLayer(lay);
      ResultSet res2 = dbHandler.exeQuery("SELECT * FROM " + prefix + "linked_paths WHERE link LIKE \"" + id + "\";");
      while (res2.next()) {

        Path p = DYNTRACK.getPath(res2.getString("linked"));
        if (p == null) {
          MsgSender.cBug(DYNTRACK, "The linked Path " + res2.getString("linked") + " was not found.");
        }
        link.add(p, res2.getInt("i"));
      }
      ResultSet res3 = dbHandler.exeQuery("SELECT * FROM " + prefix + "linked_squares WHERE link LIKE \"" + id + "\";");
      while (res3.next()) {

        Square sq = DYNTRACK.getSquare(res3.getString("linked"));
        if (sq == null) {
          MsgSender.cBug(DYNTRACK, "The linked Square " + res3.getString("linked") + " was not found.");
        }
        link.add(sq, res3.getInt("i"));
      }
      res1.close();
      res2.close();
      res3.close();
      return link;
    }

    res1.close();
    throw new SQLException("The data of the Link " + id + " were not found in the database.");
  }

  public void save() throws JDOMException, IOException, SQLException {
    Bukkit.getScheduler().runTask(this.DYNTRACK, () -> Bukkit.getPluginManager().callEvent(new DataEvent(this, DataActionResult.SAVED)));
    if (!this.DYNTRACK.getDynTrackConfig().useDB()) {

      saveInFile();
    } else {

      saveInDatabase();
    }
  }

  public void saveInFile() throws JDOMException, IOException {
    Document doc = (new SAXBuilder()).build(Link.class.getClassLoader().getResource("link.xml").toString());
    Element root = doc.getRootElement();
    root.getChild("id").setText(this.id);
    Element infobox = root.getChild("infobox");
    infobox.getChild("name").setText(this.name);
    infobox.getChild("syn").setText(this.linkSyn);
    infobox.getChild("hide").setText(String.valueOf(this.hideName));
    Element dynmap = root.getChild("dynmap");
    dynmap.getChild("hide").setText(String.valueOf(this.hide));
    dynmap.getChild("edging").setText(String.valueOf(this.edging));
    root.getChild("location").setText(MethodPool.toText(this.loc, true));
    root.getChild("style").setText(this.style.getId());
    root.getChild("layer").setText(this.layer.getId());
    List < Element > linkedList = root.getChild("linked").getChildren();
    linkedList.clear();
    Iterator < Linkable > itLinkable = this.linked.keySet().iterator();
    Iterator < Integer > itIndex = this.linked.values().iterator();
    while (itLinkable.hasNext() && itIndex.hasNext()) {
      linkedList.add((new Element("entry")).setAttribute(new Attribute("id", itLinkable.next().getId())).setAttribute(new Attribute("i", String.valueOf(itIndex.next()))));
    }
    File f = new File(this.DYNTRACK.getDataFolder() + "/Links/" + this.id + ".xml");
    FileOutputStream fos = new FileOutputStream(f);
    OutputStreamWriter osr = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
    XMLOutputter output = new XMLOutputter();
    Format format = output.getFormat();
    format.setEncoding("UTF-8");
    format.setIndent("\t");
    output.setFormat(format);
    output.output(doc, osr);
    osr.close();
    fos.close();
    MsgSender.cInfo(this.DYNTRACK, "Saved Link \"" + this.id + "\" in file.");
    Bukkit.getScheduler().runTask(this.DYNTRACK, () -> Bukkit.getPluginManager().callEvent(new DataEvent(this, DataActionResult.SAVED_IN_FILE)));
  }

  public void saveInDatabase() throws SQLException {
    DBHandler dbHandler = this.DYNTRACK.getDBHandler();
    String prefix = this.DYNTRACK.getDynTrackConfig().getDB_Prefix();
    ResultSet res = dbHandler.exeQuery("SELECT * FROM " + prefix + "links WHERE id LIKE \"" + this.id + "\"");
    if (res.first()) {

      dbHandler.exe("UPDATE " + prefix + "links SET " + "name = \"" + this.name + "\"," + "hidename = " + this.hideName + "," + "linksyn = \"" + this.linkSyn + "\"," + "hide = " + this.hide + "," + "edging = " + this.edging + "," + "x = " + this.loc

              .getX() + "," + "y = " + this.loc
              .getY() + "," + "z = " + this.loc
              .getZ() + "," + "world = \"" + this.loc
              .getWorld().getName() + "\"," + "style = \"" + this.style
              .getId() + "\"," + "layer = \"" + this.layer
              .getId() + "\" " + "WHERE id LIKE \"" + this.id + "\";");

    } else {

      dbHandler.exe("INSERT INTO " + prefix + "links(id, name, linksyn, hidename, hide, edging, x, y, z, world, style, layer)" + "VALUES(" + "\"" + this.id + "\"," + "\"" + this.name + "\"," + "\"" + this.linkSyn + "\"," + this.hideName + "," + this.hide + "," + this.edging + "," + this.loc

              .getX() + "," + this.loc
              .getY() + "," + this.loc
              .getZ() + "," + "\"" + this.loc
              .getWorld().getName() + "\"," + "\"" + this.style
              .getId() + "\"," + "\"" + this.layer
              .getId() + "\" " + ");");
    }

    res.close();
    dbHandler.exe("DELETE FROM " + prefix + "linked_paths WHERE link LIKE \"" + this.id + "\";");
    dbHandler.exe("DELETE FROM " + prefix + "linked_squares WHERE link LIKE \"" + this.id + "\";");
    Iterator < Linkable > it = this.linked.keySet().iterator();

    while (it.hasNext()) {

      Linkable current = it.next();
      if (current instanceof Path) {

        dbHandler.exe("INSERT INTO " + prefix + "linked_paths(link, linked, i)" + "VALUES(" + "\"" + this.id + "\"," + "\"" + current

                .getId() + "\"," + this.linked
                .get(current) + ");");

        continue;
      }

      dbHandler.exe("INSERT INTO " + prefix + "linked_squares(link, linked, i)" + "VALUES(" + "\"" + this.id + "\"," + "\"" + current

              .getId() + "\"," + this.linked
              .get(current) + ");");
    }

    MsgSender.cInfo(this.DYNTRACK, "Saved Link \"" + this.id + "\" in Database.");
    Bukkit.getScheduler().runTask(this.DYNTRACK, () -> Bukkit.getPluginManager().callEvent(new DataEvent(this, DataActionResult.SAVED_IN_DB)));
  }

  public DataActionResult delete(boolean ignorDependencies) {
    if (!this.DYNTRACK.getDynTrackConfig().useDB()) {

      File f = new File(this.DYNTRACK.getDataFolder() + "/Links/" + this.id + ".xml");
      if (f.delete()) {

        MsgSender.cInfo(this.DYNTRACK, "Deleted file of the Link " + this.id + ".");
        Bukkit.getScheduler().runTask(this.DYNTRACK, () -> Bukkit.getPluginManager().callEvent(new DataEvent(this, DataActionResult.DELETED_SUCCESSFULLY)));
        return DataActionResult.DELETED_SUCCESSFULLY;
      }
      if (f.exists()) {

        MsgSender.cBug(this.DYNTRACK, "Could not delete the file of the Link " + this.id + ".");
        return DataActionResult.DELETED_FAILED;
      }
    } else {

      try {

        DBHandler dbHandler = this.DYNTRACK.getDBHandler();
        String prefix = this.DYNTRACK.getDynTrackConfig().getDB_Prefix();
        dbHandler.exe("DELETE FROM " + prefix + "linked_paths WHERE link LIKE \"" + this.id + "\";");
        dbHandler.exe("DELETE FROM " + prefix + "linked_squares WHERE link LIKE \"" + this.id + "\";");
        if (dbHandler.exe("DELETE FROM " + prefix + "links WHERE id LIKE \"" + this.id + "\";") >= 1) {

          MsgSender.cInfo(this.DYNTRACK, "Deleted the data from the Link " + this.id + " in the database.");
          Bukkit.getScheduler().runTask(this.DYNTRACK, () -> Bukkit.getPluginManager().callEvent(new DataEvent(this, DataActionResult.DELETED_SUCCESSFULLY)));
          return DataActionResult.DELETED_SUCCESSFULLY;
        }
        return DataActionResult.DELETED_FAILED;
      } catch (SQLException e) {

        MsgSender.cBug(this.DYNTRACK, "Could not delete the data from the Link " + this.id + " in the database.");
        MsgSender.cBug(this.DYNTRACK, e.getMessage());
        return DataActionResult.DELETED_FAILED;
      }
    }
    return DataActionResult.DELETED_FAILED;
  }

  public boolean add(Linkable e, int trackpointNumber) {
    if (!this.linked.containsKey(e)) {

      this.linked.put(e, Integer.valueOf(trackpointNumber));
      return true;
    }
    return false;
  }

  public void del(Linkable e) {
    this.linked.remove(e);
  }

  public Hashtable < Linkable, Integer > getTable() {
    return this.linked;
  }

  public Linkable[] getLinkables() {
    return (Linkable[]) this.linked.keySet().toArray((Object[]) new Linkable[this.linked.keySet().size()]);
  }

  public String getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public boolean isNameHidden() {
    return this.hideName;
  }

  public String getLinkSyn() {
    return this.linkSyn;
  }

  public boolean isHidden() {
    return this.hide;
  }

  public boolean hasEdging() {
    return this.edging;
  }

  public Location getLocation() {
    return this.loc;
  }

  public Style getStyle() {
    return this.style;
  }

  public Layer getLayer() {
    return this.layer;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setHideName(boolean hideName) {
    this.hideName = hideName;
  }

  public void setLinkSyn(String linkSyn) {
    this.linkSyn = linkSyn;
  }

  public void setHide(boolean hide) {
    this.hide = hide;
  }

  public void setEdging(boolean edging) {
    this.edging = edging;
  }

  public void setLocation(Location loc) {
    this.loc = loc;
  }

  public void setStyle(Style style) {
    this.style = style;
  }

  public void setLayer(Layer layer) {
    this.layer = layer;
  }
}