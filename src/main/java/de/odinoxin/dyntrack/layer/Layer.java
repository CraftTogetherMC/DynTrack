package de.odinoxin.dyntrack.layer;

import de.odinoxin.dyntrack.DBHandler;
import de.odinoxin.dyntrack.DynTrack;
import de.odinoxin.dyntrack.enums.DataActionResult;
import de.odinoxin.dyntrack.generals.DataEvent;
import de.odinoxin.dyntrack.generals.MsgSender;
import de.odinoxin.dyntrack.interfaces.Nameable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class Layer implements Cloneable, Nameable {
  private String id = "";
  private String name = "";
  private boolean hideName = false;
  private boolean hide = false;
  private byte minzoom = 0;
  private int priority = 0;

  private final DynTrack DYNTRACK;

  public Layer(DynTrack DYNTRACK) {
    this.DYNTRACK = DYNTRACK;
  }

  public Layer(DynTrack DYNTRACK, String id, String name, boolean hideName, boolean hide, byte minzoom, short priority) {
    this(DYNTRACK);
    this.id = id;
    this.name = name;
    this.hideName = hideName;
    this.hide = hide;
    this.minzoom = minzoom;
    this.priority = priority;
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof Layer)) {
      return false;
    }
    Layer l = (Layer) obj;
    return l.getId().equals(this.id);
  }

  public Layer clone() {
    try {
      return (Layer) super.clone();
    } catch (CloneNotSupportedException e) {

      return null;
    }
  }

  public static final Layer loadLayer(DynTrack DYNTRACK, String id) throws JDOMException, IOException, SQLException {
    if (!DYNTRACK.getDynTrackConfig().useDB()) {

      Layer lay = new Layer(DYNTRACK);
      File f = new File(DYNTRACK.getDataFolder() + "/Layers/" + id + ".xml");
      Document doc = (new SAXBuilder()).build(f);
      Element root = doc.getRootElement();
      lay.setId(root.getChildTextNormalize("id"));
      Element infobox = root.getChild("infobox");
      lay.setName(infobox.getChildTextNormalize("name"));
      lay.setHideName(Boolean.parseBoolean(infobox.getChildTextNormalize("hide")));
      Element dynmap = root.getChild("dynmap");
      lay.setMinzoom(Byte.parseByte(dynmap.getChildTextNormalize("minzoom")));
      lay.setPriority(Integer.parseInt(dynmap.getChildTextNormalize("priority")));
      lay.setHide(Boolean.parseBoolean(dynmap.getChildTextNormalize("hide")));
      return lay;
    }

    ResultSet res = DYNTRACK.getDBHandler().exeQuery("SELECT * FROM " + DYNTRACK.getDynTrackConfig().getDB_Prefix() + "layers WHERE id LIKE \"" + id + "\";");
    if (res.first()) {

      Layer lay = new Layer(DYNTRACK);
      lay.setId(id);
      lay.setName(res.getString("name"));
      lay.setHideName(res.getBoolean("hidename"));
      lay.setHide(res.getBoolean("hide"));
      lay.setMinzoom(res.getByte("minzoom"));
      lay.setPriority(res.getShort("priority"));
      res.close();
      return lay;
    }

    res.close();
    throw new SQLException("The data of the Layer " + id + " were not found in the database.");
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
    Document doc = (new SAXBuilder()).build(Layer.class.getClassLoader().getResource("layer.xml").toString());
    Element root = doc.getRootElement();
    root.getChild("id").setText(this.id);
    Element infobox = root.getChild("infobox");
    infobox.getChild("name").setText(this.name);
    infobox.getChild("hide").setText(String.valueOf(this.hideName));
    Element dynmap = root.getChild("dynmap");
    dynmap.getChild("minzoom").setText(String.valueOf(this.minzoom));
    dynmap.getChild("priority").setText(String.valueOf(this.priority));
    dynmap.getChild("hide").setText(String.valueOf(this.hide));
    File f = new File(this.DYNTRACK.getDataFolder() + "/Layers/" + this.id + ".xml");
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
    MsgSender.cInfo(this.DYNTRACK, "Saved Layer \"" + this.id + "\" in File.");
    Bukkit.getScheduler().runTask(this.DYNTRACK, () -> Bukkit.getPluginManager().callEvent(new DataEvent(this, DataActionResult.SAVED_IN_FILE)));
  }

  public void saveInDatabase() throws SQLException {
    DBHandler dbHandler = this.DYNTRACK.getDBHandler();
    String prefix = this.DYNTRACK.getDynTrackConfig().getDB_Prefix();
    ResultSet res = dbHandler.exeQuery("SELECT * FROM " + prefix + "layers WHERE id LIKE \"" + this.id + "\"");
    if (res.first()) {

      dbHandler.exe("UPDATE " + prefix + "layers SET " + "name = \"" + this.name + "\"," + "hidename = " + this.hideName + "," + "hide = " + this.hide + "," + "minzoom = " + this.minzoom + "," + "priority = " + this.priority + " " + "WHERE id LIKE \"" + this.id + "\";");

    } else {

      dbHandler.exe("INSERT INTO " + prefix + "layers(id, name, hidename, hide, minzoom, priority)" + "VALUES(" + "\"" + this.id + "\"," + "\"" + this.name + "\"," + this.hideName + "," + this.hide + "," + this.minzoom + "," + this.priority + ");");
    }

    res.close();
    MsgSender.cInfo(this.DYNTRACK, "Saved Layer \"" + this.id + "\" in Database.");
    Bukkit.getScheduler().runTask(this.DYNTRACK, () -> Bukkit.getPluginManager().callEvent(new DataEvent(this, DataActionResult.SAVED_IN_DB)));
  }

  public DataActionResult delete(boolean ignorDependencies) {
    if (!ignorDependencies && this.DYNTRACK
            .isUsed(this)) {
      return DataActionResult.STILL_USED;
    }
    if (!this.DYNTRACK.getDynTrackConfig().useDB()) {

      File f = new File(this.DYNTRACK.getDataFolder() + "/Layers/" + this.id + ".xml");
      if (f.delete()) {

        MsgSender.cInfo(this.DYNTRACK, "Deleted file of the Layer " + this.id + ".");
        Bukkit.getScheduler().runTask(this.DYNTRACK, () -> Bukkit.getPluginManager().callEvent(new DataEvent(this, DataActionResult.DELETED_SUCCESSFULLY)));
        return DataActionResult.DELETED_SUCCESSFULLY;
      }
      if (f.exists()) {

        MsgSender.cBug(this.DYNTRACK, "Could not delete the file of the Layer " + this.id + ".");
        return DataActionResult.DELETED_FAILED;
      }
    } else {

      try {

        if (this.DYNTRACK.getDBHandler().exe("DELETE FROM " + this.DYNTRACK.getDynTrackConfig().getDB_Prefix() + "layers WHERE id LIKE \"" + this.id + "\";") >= 1) {

          MsgSender.cInfo(this.DYNTRACK, "Deleted the data from the Layer " + this.id + " in the database.");
          Bukkit.getScheduler().runTask(this.DYNTRACK, () -> Bukkit.getPluginManager().callEvent(new DataEvent(this, DataActionResult.DELETED_SUCCESSFULLY)));
          return DataActionResult.DELETED_SUCCESSFULLY;
        }
        return DataActionResult.DELETED_FAILED;
      } catch (SQLException e) {

        MsgSender.cBug(this.DYNTRACK, "Could not delete the data from the Layer " + this.id + " in the database.");
        MsgSender.cBug(this.DYNTRACK, e.getMessage());
        return DataActionResult.DELETED_FAILED;
      }
    }
    return DataActionResult.DELETED_FAILED;
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

  public boolean isHidden() {
    return this.hide;
  }

  public byte getMinzoom() {
    return this.minzoom;
  }

  public int getPriority() {
    return this.priority;
  }

  public boolean isNameHidden() {
    return this.hideName;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setHide(boolean hide) {
    this.hide = hide;
  }

  public void setMinzoom(byte minzoom) {
    this.minzoom = minzoom;
  }

  public void setPriority(int priority) {
    this.priority = priority;
  }

  public void setHideName(boolean hideName) {
    this.hideName = hideName;
  }
}