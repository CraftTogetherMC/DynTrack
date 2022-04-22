package de.odinoxin.dyntrack.square;

import de.odinoxin.dyntrack.DBHandler;
import de.odinoxin.dyntrack.DynTrack;
import de.odinoxin.dyntrack.enums.DataActionResult;
import de.odinoxin.dyntrack.generals.DataEvent;
import de.odinoxin.dyntrack.generals.MethodPool;
import de.odinoxin.dyntrack.generals.MsgSender;
import de.odinoxin.dyntrack.interfaces.Drawable;
import de.odinoxin.dyntrack.interfaces.Linkable;
import de.odinoxin.dyntrack.layer.Layer;
import de.odinoxin.dyntrack.link.Link;
import de.odinoxin.dyntrack.style.Style;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

public class Square implements Cloneable, Linkable, Drawable {
  private String id = "";
  private String name = "";
  private String squareSyn = "Square";

  private boolean hideName = false;

  private Style style;

  private Layer layer;

  private World world;

  private List < Location > locList;

  private final DynTrack DYNTRACK;
  private Link[] links;
  private int[] locs;

  public Square(DynTrack DYNTRACK) {
    this.DYNTRACK = DYNTRACK;
    this.locList = new ArrayList < > ();
  }

  public Square(DynTrack DYNTRACK, String id, String name, World world, Style style, Layer layer) {
    this(DYNTRACK);
    this.id = id;
    this.name = name;
    this.world = world;
    this.style = style;
    this.layer = layer;
  }

  public Square(DynTrack DYNTRACK, String id, String name, World world, Style style, Layer layer, List < Location > locList) {
    this(DYNTRACK, id, name, world, style, layer);
    this.locList = locList;
  }

  public Square(DynTrack DYNTRACK, String id, String name, World world, Style style, Layer layer, List < Location > locList, String squareSyn, boolean hideName) {
    this(DYNTRACK, id, name, world, style, layer, locList);
    this.squareSyn = squareSyn;
    this.hideName = hideName;
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof Square)) {
      return false;
    }
    Square sq = (Square) obj;
    return sq.getId().equals(this.id);
  }

  public Square clone() {
    try {
      return (Square) super.clone();
    } catch (CloneNotSupportedException e) {

      return null;
    }
  }

  public static Square loadSquare(DynTrack DYNTRACK, String id) throws JDOMException, IOException, SQLException {
    if (!DYNTRACK.getDynTrackConfig().useDB()) {

      Square sq = new Square(DYNTRACK);
      File f = new File(DYNTRACK.getDataFolder() + "/Squares/" + id + ".xml");
      Document doc = (new SAXBuilder()).build(f);
      Element root = doc.getRootElement();
      sq.setId(root.getChildTextNormalize("id"));
      Element infobox = root.getChild("infobox");
      sq.setName(infobox.getChildTextNormalize("name"));
      sq.setSquareSyn(infobox.getChildTextNormalize("syn"));
      sq.setHideName(Boolean.parseBoolean(infobox.getChildTextNormalize("hide")));
      sq.setWorld(DYNTRACK.getServer().getWorld(root.getChildTextNormalize("world")));
      if (sq.getWorld() == null) {

        MsgSender.cBug(DYNTRACK, "The world " + root.getChildTextNormalize("world") + " was not found.");
        return null;
      }
      List < Element > locList = root.getChild("loclist").getChildren();
      sq.getLocationList().clear();
      for (int i = 0; i < locList.size(); i++) {
        sq.getLocationList().add(locList.get(i).getAttribute("i").getIntValue(), MethodPool.toLoc(locList.get(i).getTextNormalize(), sq.getWorld()));
      }
      sq.setStyle(DYNTRACK.getStyle(root.getChildTextNormalize("style")));
      if (sq.getStyle() == null) {

        MsgSender.cBug(DYNTRACK, "The Style " + root.getChildTextNormalize("style") + " was not found.");
        return null;
      }
      sq.setLayer(DYNTRACK.getLayer(root.getChildTextNormalize("layer")));
      if (sq.getLayer() == null) {

        MsgSender.cBug(DYNTRACK, "The Layer " + root.getChildTextNormalize("layer") + " was not found.");
        return null;
      }
      return sq;
    }

    DBHandler dbHandler = DYNTRACK.getDBHandler();
    String prefix = DYNTRACK.getDynTrackConfig().getDB_Prefix();
    ResultSet res1 = dbHandler.exeQuery("SELECT * FROM " + prefix + "squares WHERE id LIKE \"" + id + "\";");
    ResultSet res2 = dbHandler.exeQuery("SELECT * FROM " + prefix + "square_points WHERE square LIKE \"" + id + "\";");
    if (res1.first() && res2
            .first()) {

      Square sq = new Square(DYNTRACK);
      sq.setId(id);
      sq.setName(res1.getString("name"));
      sq.setSquareSyn(res1.getString("squaresyn"));
      sq.setHideName(res1.getBoolean("hidename"));
      World w = DYNTRACK.getServer().getWorld(res1.getString("world"));
      Style st = DYNTRACK.getStyle(res1.getString("style"));
      Layer lay = DYNTRACK.getLayer(res1.getString("layer"));
      if (w == null) {

        MsgSender.cBug(DYNTRACK, "The world " + res1.getString("world") + " was not found.");
        res1.close();
        res2.close();
        return null;
      }
      if (st == null) {

        MsgSender.cBug(DYNTRACK, "The Style " + res1.getString("style") + " was not found.");
        res1.close();
        res2.close();
        return null;
      }
      if (lay == null) {

        MsgSender.cBug(DYNTRACK, "The Layer " + res1.getString("layer") + " was not found.");
        res1.close();
        res2.close();
        return null;
      }
      sq.setWorld(w);
      sq.setStyle(st);
      sq.setLayer(lay);
      sq.getLocationList().clear();
      res2.beforeFirst();
      while (res2.next()) {
        sq.getLocationList().add(res2.getInt("i"), new Location(w, res2.getDouble("x"), res2.getDouble("y"), res2.getDouble("z")));
      }
      res1.close();
      res2.close();
      return sq;
    }

    res1.close();
    res2.close();
    throw new SQLException("The data of the Path " + id + " were not found in the database.");
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
    Document doc = (new SAXBuilder()).build(Square.class.getClassLoader().getResource("square.xml").toString());
    Element root = doc.getRootElement();
    root.getChild("id").setText(this.id);
    Element infobox = root.getChild("infobox");
    infobox.getChild("name").setText(this.name);
    infobox.getChild("syn").setText(this.squareSyn);
    infobox.getChild("hide").setText(String.valueOf(this.hideName));
    root.getChild("world").setText(this.world.getName());
    List < Element > locList = root.getChild("loclist").getChildren();
    locList.clear();
    Iterator < Location > it = this.locList.iterator();
    int i = 0;
    while (it.hasNext()) {

      locList.add((new Element("loc")).setAttribute(new Attribute("i", String.valueOf(i))).setText(MethodPool.toText(it.next(), false)));
      i++;
    }
    root.getChild("style").setText(this.style.getId());
    root.getChild("layer").setText(this.layer.getId());
    File f = new File(this.DYNTRACK.getDataFolder() + "/Squares/" + this.id + ".xml");
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
    Bukkit.getScheduler().runTask(this.DYNTRACK, () -> Bukkit.getPluginManager().callEvent(new DataEvent(this, DataActionResult.SAVED_IN_FILE)));
    MsgSender.cInfo(this.DYNTRACK, "Saved Square \"" + this.id + "\" in file.");
  }

  public void saveInDatabase() throws SQLException {
    DBHandler dbHandler = this.DYNTRACK.getDBHandler();
    String prefix = this.DYNTRACK.getDynTrackConfig().getDB_Prefix();
    ResultSet res = dbHandler.exeQuery("SELECT * FROM " + prefix + "squares WHERE id LIKE \"" + this.id + "\"");
    if (res.first()) {

      dbHandler.exe("UPDATE " + prefix + "squares SET " + "name = \"" + this.name + "\"," + "squaresyn = \"" + this.squareSyn + "\"," + "hidename = " + this.hideName + "," + "style = \"" + this.style

              .getId() + "\"," + "layer = \"" + this.layer
              .getId() + "\"," + "world = \"" + this.world
              .getName() + "\" " + "WHERE id LIKE \"" + this.id + "\";");

    } else {

      dbHandler.exe("INSERT INTO " + prefix + "squares(id, name, squaresyn, hidename, style, layer, world)" + "VALUES(" + "\"" + this.id + "\"," + "\"" + this.name + "\"," + "\"" + this.squareSyn + "\"," + this.hideName + "," + "\"" + this.style

              .getId() + "\"," + "\"" + this.layer
              .getId() + "\"," + "\"" + this.world
              .getName() + "\" " + ");");
    }

    res.close();
    dbHandler.exe("DELETE FROM " + prefix + "square_points WHERE square LIKE \"" + this.id + "\";");

    for (int i = 0; i < this.locList.size(); i++) {

      Location current = this.locList.get(i);
      dbHandler.exe("INSERT INTO " + prefix + "square_points(square, i, x, y, z)" + "VALUES(" + "\"" + this.id + "\"," + i + "," + current

              .getX() + "," + current
              .getY() + "," + current
              .getZ() + ");");
    }

    MsgSender.cInfo(this.DYNTRACK, "Saved Square \"" + this.id + "\" in Database.");
    Bukkit.getScheduler().runTask(this.DYNTRACK, () -> Bukkit.getPluginManager().callEvent(new DataEvent(this, DataActionResult.SAVED_IN_DB)));
  }

  public DataActionResult delete(boolean ignorDependencies) {
    Iterator < Map.Entry < String, Link >> it = this.DYNTRACK.getLinks().entrySet().iterator();
    while (it.hasNext()) {

      Link link = (Link)((Map.Entry) it.next()).getValue();
      if (link.getTable().containsKey(this)) {

        link.del(this);

        try {
          link.save();
        } catch (JDOMException | IOException | SQLException e) {

          MsgSender.cBug(this.DYNTRACK, "Could not save the Link " + link.getId() + ".");
          MsgSender.cBug(this.DYNTRACK, e.getMessage());
          MsgSender.cInfo(this.DYNTRACK, "The process will continue anyway.");
        }
      }
    }

    if (!this.DYNTRACK.getDynTrackConfig().useDB()) {

      File f = new File(this.DYNTRACK.getDataFolder() + "/Squares/" + this.id + ".xml");
      if (f.delete()) {

        MsgSender.cInfo(this.DYNTRACK, "Deleted file of the Square " + this.id + ".");
        Bukkit.getScheduler().runTask(this.DYNTRACK, () -> Bukkit.getPluginManager().callEvent(new DataEvent(this, DataActionResult.DELETED_SUCCESSFULLY)));
        return DataActionResult.DELETED_SUCCESSFULLY;
      }
      if (f.exists()) {

        MsgSender.cBug(this.DYNTRACK, "Could not delete the file of the Square " + this.id + ".");
        return DataActionResult.DELETED_FAILED;
      }
    } else {

      try {

        DBHandler dbHandler = this.DYNTRACK.getDBHandler();
        String prefix = this.DYNTRACK.getDynTrackConfig().getDB_Prefix();
        dbHandler.exe("DELETE FROM " + prefix + "square_points WHERE square LIKE \"" + this.id + "\";");
        if (dbHandler.exe("DELETE FROM " + prefix + "squares WHERE id LIKE \"" + this.id + "\";") >= 1) {

          MsgSender.cInfo(this.DYNTRACK, "Deleted the data from the Square " + this.id + " in the database.");
          Bukkit.getScheduler().runTask(this.DYNTRACK, () -> Bukkit.getPluginManager().callEvent(new DataEvent(this, DataActionResult.DELETED_SUCCESSFULLY)));
          return DataActionResult.DELETED_SUCCESSFULLY;
        }
        return DataActionResult.DELETED_FAILED;
      } catch (SQLException e) {

        MsgSender.cBug(this.DYNTRACK, "Could not delete the data from the Square " + this.id + " in the database.");
        MsgSender.cBug(this.DYNTRACK, e.getMessage());
        return DataActionResult.DELETED_FAILED;
      }
    }
    return DataActionResult.DELETED_FAILED;
  }

  public boolean addLocation(Location loc, int insertAt) {
    if (insertAt == 0 || this.locList
            .get(insertAt - 1).getX() != loc.getX() || this.locList
            .get(insertAt - 1).getY() != loc.getY() || this.locList
            .get(insertAt - 1).getZ() != loc.getZ()) {

      this.locList.add(insertAt, loc);
      this.world = loc.getWorld();
      return true;
    }
    return false;
  }

  public void delLocation(int removeFrom) {
    this.locList.remove(removeFrom);
  }

  public void delLocation(int removeFrom, int n) {
    for (int i = 0; i < n; i++) {
      delLocation(removeFrom - i);
    }
  }

  public void setLocationList(List < Location > locList) {
    this.locList = locList;
  }

  public List < Location > getLocationList() {
    return this.locList;
  }

  public double[] getxArray() {
    double[] x = new double[this.locList.size()];
    for (int i = 0; i < this.locList.size(); i++) {
      x[i] = this.locList.get(i).getX();
    }
    return x;
  }

  public double[] getyArray() {
    double[] y = new double[this.locList.size()];
    for (int i = 0; i < this.locList.size(); i++) {
      y[i] = this.locList.get(i).getY();
    }
    return y;
  }

  public double[] getzArray() {
    double[] z = new double[this.locList.size()];
    for (int i = 0; i < this.locList.size(); i++) {
      z[i] = this.locList.get(i).getZ();
    }
    return z;
  }

  public Link[] getLinks() {
    Hashtable < Link, Integer > table = new Hashtable < > ();
    Iterator < Link > it = this.DYNTRACK.getLinks().values().iterator();

    while (it.hasNext()) {

      Link link = it.next();
      if (link.getTable().containsKey(this)) {
        table.put(link, link.getTable().get(this));
      }
    }

    this.links = (Link[]) table.keySet().toArray((Object[]) new Link[table.keySet().size()]);
    this.locs = new int[this.links.length];
    for (int i = 0; i < this.locs.length; i++) {
      this.locs[i] = table.get(this.links[i]).intValue();
    }

    if (this.locs.length > 1) {
      quicksort(0, this.links.length - 1);
    }

    return this.links;
  }

  private void quicksort(int pLow, int pHigh) {
    int hLeft = pLow;
    int hRight = pHigh;

    int pivot = this.locs[(pLow + pHigh) / 2];

    while (hLeft <= hRight) {

      while (this.locs[hLeft] < pivot) {
        hLeft++;
      }
      while (this.locs[hRight] > pivot) {
        hRight--;
      }

      if (hLeft <= hRight) {

        Link tmpLink = this.links[hLeft];
        int tmpInt = this.locs[hLeft];

        this.links[hLeft] = this.links[hRight];
        this.locs[hLeft] = this.locs[hRight];

        this.links[hRight] = tmpLink;
        this.locs[hRight] = tmpInt;
        hLeft++;
        hRight--;
      }
    }

    if (pLow < hRight) {
      quicksort(pLow, hRight);
    }
    if (hLeft < pHigh) {
      quicksort(hLeft, pHigh);
    }
  }

  public String getId() {
    return this.id;
  }

  public String getName() {
    if (this.name.isEmpty()) {
      return this.id;
    }
    return this.name;
  }

  public String getSquareSyn() {
    return this.squareSyn;
  }

  public boolean isNameHidden() {
    return this.hideName;
  }

  public World getWorld() {
    return this.world;
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

  public void setSquareSyn(String squareSyn) {
    this.squareSyn = squareSyn;
  }

  public void setHideName(boolean hideName) {
    this.hideName = hideName;
  }

  public void setWorld(World world) {
    this.world = world;
  }

  public void setStyle(Style style) {
    this.style = style;
  }

  public void setLayer(Layer layer) {
    this.layer = layer;
  }
}