package de.odinoxin.dyntrack.style;

import de.odinoxin.dyntrack.DBHandler;
import de.odinoxin.dyntrack.DynTrack;
import de.odinoxin.dyntrack.enums.DataActionResult;
import de.odinoxin.dyntrack.generals.DataEvent;
import de.odinoxin.dyntrack.generals.MsgSender;
import de.odinoxin.dyntrack.interfaces.Identifiable;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Style implements Cloneable, Identifiable {
    private String id = "";
    private int lineColor = 0;
    private double lineOpacity = 1.0D;
    private int lineWidth = 1;
    private int fillColor = 0;
    private double fillOpacity = 1.0D;

    private final DynTrack DYNTRACK;

    public Style(DynTrack DYNTRACK) {
        this.DYNTRACK = DYNTRACK;
    }

    public Style(DynTrack DYNTRACK, String id, int lineColor, double lineOpacity, int lineWidth, int fillColor, double fillOpacity) {
        this(DYNTRACK);
        this.id = id;
        this.lineColor = lineColor;
        this.lineOpacity = lineOpacity;
        this.lineWidth = lineWidth;
        this.fillColor = fillColor;
        this.fillOpacity = fillOpacity;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Style)) {
            return false;
        }
        Style s = (Style) obj;
        return s.getId().equals(this.id);
    }

    public Style clone() {
        try {
            return (Style) super.clone();
        } catch (CloneNotSupportedException e) {

            return null;
        }
    }

    public static Style loadStyle(DynTrack DYNTRACK, String id) throws JDOMException, IOException, SQLException {
        if (!DYNTRACK.getDynTrackConfig().useDB()) {

            Style st = new Style(DYNTRACK);
            File f = new File(DYNTRACK.getDataFolder() + "/Styles/" + id + ".xml");
            Document doc = (new SAXBuilder()).build(f);
            Element root = doc.getRootElement();
            st.setId(root.getChildTextNormalize("id"));
            Element line = root.getChild("line");
            st.setLineColor(Integer.parseInt(line.getChildTextNormalize("color")));
            st.setLineOpacity(Double.parseDouble(line.getChildTextNormalize("opacity")));
            st.setLineWidth(Integer.parseInt(line.getChildTextNormalize("width")));
            Element fill = root.getChild("fill");
            st.setFillColor(Integer.parseInt(fill.getChildTextNormalize("color")));
            st.setFillOpacity(Double.parseDouble(fill.getChildTextNormalize("opacity")));
            return st;
        }

        ResultSet res = DYNTRACK.getDBHandler().exeQuery("SELECT * FROM " + DYNTRACK.getDynTrackConfig().getDB_Prefix() + "styles WHERE id LIKE \"" + id + "\";");
        if (res.first()) {

            Style st = new Style(DYNTRACK);
            st.setId(id);
            st.setLineColor(res.getInt("linecolor"));
            st.setLineOpacity(res.getDouble("lineopacity"));
            st.setLineWidth(res.getInt("linewidth"));
            st.setFillColor(res.getInt("fillcolor"));
            st.setFillOpacity(res.getDouble("fillopacity"));
            res.close();
            return st;
        }

        res.close();
        throw new SQLException("The data of the Style " + id + " were not found in the database.");
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
        Document doc = (new SAXBuilder()).build(Style.class.getClassLoader().getResource("style.xml").toString());
        Element root = doc.getRootElement();
        root.getChild("id").setText(this.id);
        Element line = root.getChild("line");
        line.getChild("color").setText(String.valueOf(this.lineColor));
        line.getChild("opacity").setText(String.valueOf(this.lineOpacity));
        line.getChild("width").setText(String.valueOf(this.lineWidth));
        Element fill = root.getChild("fill");
        fill.getChild("color").setText(String.valueOf(this.fillColor));
        fill.getChild("opacity").setText(String.valueOf(this.fillOpacity));
        File f = new File(this.DYNTRACK.getDataFolder() + "/Styles/" + this.id + ".xml");
        FileOutputStream fos = new FileOutputStream(f);
        OutputStreamWriter osr = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
        XMLOutputter output = new XMLOutputter();
        Format format = output.getFormat();
        format.setEncoding("UTF-8");
        format.setIndent(" ");
        output.setFormat(format);
        output.output(doc, osr);
        osr.close();
        fos.close();
        MsgSender.cInfo(this.DYNTRACK, "Saved Style \"" + this.id + "\" in File.");
        Bukkit.getScheduler().runTask(this.DYNTRACK, () -> Bukkit.getPluginManager().callEvent(new DataEvent(this, DataActionResult.SAVED_IN_FILE)));

    }

    public void saveInDatabase() throws SQLException {
        DBHandler dbHandler = this.DYNTRACK.getDBHandler();
        String prefix = this.DYNTRACK.getDynTrackConfig().getDB_Prefix();
        ResultSet res = dbHandler.exeQuery("SELECT * FROM " + prefix + "styles WHERE id LIKE \"" + this.id + "\"");
        if (res.first()) {

            dbHandler.exe("UPDATE " + prefix + "styles SET " + "linecolor = " + this.lineColor + "," + "lineopacity = " + this.lineOpacity + "," + "linewidth = " + this.lineWidth + "," + "fillcolor = " + this.fillColor + "," + "fillopacity = " + this.fillOpacity + " " + "WHERE id LIKE \"" + this.id + "\";");

        } else {

            dbHandler.exe("INSERT INTO " + prefix + "styles(id, linecolor, lineopacity, linewidth, fillcolor, fillopacity)" + "VALUES(" + "\"" + this.id + "\"," + this.lineColor + "," + this.fillOpacity + "," + this.lineWidth + "," + this.fillColor + "," + this.fillOpacity + ");");
        }

        res.close();
        MsgSender.cInfo(this.DYNTRACK, "Saved Style \"" + this.id + "\" in Database.");
        Bukkit.getScheduler().runTask(this.DYNTRACK, () -> Bukkit.getPluginManager().callEvent(new DataEvent(this, DataActionResult.SAVED_IN_DB)));
    }

    public DataActionResult delete(boolean ignorDependencies) {
        if (!ignorDependencies && this.DYNTRACK
                .isUsed(this)) {
            return DataActionResult.STILL_USED;
        }
        if (!this.DYNTRACK.getDynTrackConfig().useDB()) {

            File f = new File(this.DYNTRACK.getDataFolder() + "/Styles/" + this.id + ".xml");
            if (f.delete()) {

                MsgSender.cInfo(this.DYNTRACK, "Deleted file of the Style " + this.id + ".");
                Bukkit.getScheduler().runTask(this.DYNTRACK, () -> Bukkit.getPluginManager().callEvent(new DataEvent(this, DataActionResult.DELETED_SUCCESSFULLY)));
                return DataActionResult.DELETED_SUCCESSFULLY;
            }
            MsgSender.cBug(this.DYNTRACK, "Could not delete the file of the Style " + this.id + ".");
            return DataActionResult.DELETED_FAILED;
        }

        try {
            if (this.DYNTRACK.getDBHandler().exe("DELETE FROM " + this.DYNTRACK.getDynTrackConfig().getDB_Prefix() + "styles WHERE id LIKE \"" + this.id + "\";") >= 1) {

                MsgSender.cInfo(this.DYNTRACK, "Deleted the data from the Style " + this.id + " in the database.");
                Bukkit.getScheduler().runTask(this.DYNTRACK, () -> Bukkit.getPluginManager().callEvent(new DataEvent(this, DataActionResult.DELETED_SUCCESSFULLY)));
                return DataActionResult.DELETED_SUCCESSFULLY;
            }
            return DataActionResult.DELETED_FAILED;
        } catch (SQLException e) {

            MsgSender.cBug(this.DYNTRACK, "Could not delete the data from the Style " + this.id + " in the database.");
            MsgSender.cBug(this.DYNTRACK, e.getMessage());
            return DataActionResult.DELETED_FAILED;
        }
    }

    public String getId() {
        return this.id;
    }

    public int getLineColor() {
        return this.lineColor;
    }

    public double getLineOpacity() {
        return this.lineOpacity;
    }

    public int getLineWidth() {
        return this.lineWidth;
    }

    public int getFillColor() {
        return this.fillColor;
    }

    public double getFillOpacity() {
        return this.fillOpacity;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public void setLineOpacity(double lineOpacity) {
        this.lineOpacity = lineOpacity;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
    }

    public void setFillOpacity(double fillOpacity) {
        this.fillOpacity = fillOpacity;
    }
}