package de.odinoxin.dyntrack.path;

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

public class Path implements Cloneable, Linkable, Drawable {
    private String id = "";

    private String name = "";
    private String pathSyn = "Path";
    private boolean hideName = false;

    private String fromSyn = "From";
    private String fromVal = "";
    private boolean hideFrom = false;

    private String toSyn = "To";
    private String toVal = "";
    private boolean hideTo = false;

    private String viaSyn = "Via";
    private boolean hideVia = false;
    private boolean connected = false;

    private World world = null;
    private List < Location > locList;
    private Style style = null;
    private Layer layer = null;
    private final DynTrack DYNTRACK;

    private Link[] links;
    private int[] locs;

    public Path(DynTrack DYNTRACK) {
        this.DYNTRACK = DYNTRACK;
        this.locList = new ArrayList < > ();
    }

    public Path(DynTrack DYNTRACK, String id, String name, String pathSyn, boolean hideName, String fromSyn, String fromVal, boolean hideFrom, String toSyn, String toVal, boolean hideTo, String viaSyn, boolean hideVia, boolean connected, World world, List < Location > locList, Style style, Layer layer) {
        this(DYNTRACK);
        this.id = id;

        this.name = name;
        this.pathSyn = pathSyn;
        this.hideName = hideName;

        this.fromSyn = fromSyn;
        this.fromVal = fromVal;
        this.hideFrom = hideFrom;

        this.toSyn = toSyn;
        this.toVal = toVal;
        this.hideTo = hideTo;

        this.viaSyn = viaSyn;
        this.hideVia = hideVia;
        this.connected = connected;

        this.world = world;
        this.locList = locList;
        this.style = style;
        this.layer = layer;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Path))
            return false;
        Path p = (Path) obj;
        return p.getId().equals(this.id);
    }

    public Path clone() {
        try {
            return (Path) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public static Path loadPath(DynTrack DYNTRACK, String id) throws JDOMException, IOException, SQLException {
        if (!DYNTRACK.getDynTrackConfig().useDB()) {
            Path p = new Path(DYNTRACK);
            File f = new File(DYNTRACK.getDataFolder() + "/Paths/" + id + ".xml");
            Document doc = (new SAXBuilder()).build(f);
            Element root = doc.getRootElement();
            p.setId(root.getChildTextNormalize("id"));
            Element infobox = root.getChild("infobox");

            Element name = infobox.getChild("name");
            p.setName(name.getChildTextNormalize("val"));
            p.setPathSyn(name.getChildTextNormalize("syn"));
            p.setHideName(Boolean.parseBoolean(name.getChildTextNormalize("hide")));

            Element from = infobox.getChild("from");
            p.setFromSyn(from.getChildTextNormalize("syn"));
            p.setFromVal(from.getChildTextNormalize("val"));
            p.setHideFrom(Boolean.parseBoolean(from.getChildTextNormalize("hide")));

            Element to = infobox.getChild("to");
            p.setToSyn(to.getChildTextNormalize("syn"));
            p.setToVal(to.getChildTextNormalize("val"));
            p.setHideTo(Boolean.parseBoolean(to.getChildTextNormalize("hide")));

            Element via = infobox.getChild("via");
            p.setViaSyn(via.getChildTextNormalize("syn"));
            p.setHideVia(Boolean.parseBoolean(via.getChildTextNormalize("hide")));
            p.setConnected(Boolean.parseBoolean(root.getChildTextNormalize("connected")));

            p.setWorld(DYNTRACK.getServer().getWorld(root.getChildTextNormalize("world")));

            if (p.getWorld() == null) {
                MsgSender.cBug(DYNTRACK, "The world " + root.getChildTextNormalize("world") + " was not found.");
                return null;
            }

            List < Element > locList = root.getChild("loclist").getChildren();
            p.getLocationList().clear();

            for (int i = 0; i < locList.size(); i++)
                p.getLocationList().add(locList.get(i).getAttribute("i").getIntValue(), MethodPool.toLoc(locList.get(i).getTextNormalize(), p.getWorld()));

            p.setStyle(DYNTRACK.getStyle(root.getChildTextNormalize("style")));
            if (p.getStyle() == null) {
                MsgSender.cBug(DYNTRACK, "The Style " + root.getChildTextNormalize("style") + " was not found.");
                return null;
            }

            p.setLayer(DYNTRACK.getLayer(root.getChildTextNormalize("layer")));
            if (p.getLayer() == null) {
                MsgSender.cBug(DYNTRACK, "The Layer " + root.getChildTextNormalize("layer") + " was not found.");
                return null;
            }

            return p;
        }
        DBHandler dbHandler = DYNTRACK.getDBHandler();
        String prefix = DYNTRACK.getDynTrackConfig().getDB_Prefix();
        ResultSet res1 = dbHandler.exeQuery("SELECT * FROM " + prefix + "paths WHERE id LIKE \"" + id + "\";");
        ResultSet res2 = dbHandler.exeQuery("SELECT * FROM " + prefix + "path_points WHERE path LIKE \"" + id + "\";");
        if (res1.first() && res2.first()) {
            Path p = new Path(DYNTRACK);
            p.setId(id);
            p.setName(res1.getString("name"));
            p.setPathSyn(res1.getString("pathsyn"));
            p.setHideName(res1.getBoolean("hidename"));
            p.setFromVal(res1.getString("fromval"));
            p.setFromSyn(res1.getString("fromsyn"));
            p.setHideFrom(res1.getBoolean("hidefrom"));
            p.setToVal(res1.getString("toval"));
            p.setToSyn(res1.getString("tosyn"));
            p.setHideTo(res1.getBoolean("hideto"));
            p.setViaSyn(res1.getString("viasyn"));
            p.setHideVia(res1.getBoolean("hidevia"));
            p.setConnected(res1.getBoolean("connected"));
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

            p.setWorld(w);
            p.setStyle(st);
            p.setLayer(lay);
            p.getLocationList().clear();
            res2.beforeFirst();

            while (res2.next())
                p.getLocationList().add(res2.getInt("i"), new Location(w, res2.getDouble("x"), res2.getDouble("y"), res2.getDouble("z")));

            res1.close();
            res2.close();
            return p;
        }

        res1.close();
        res2.close();

        throw new SQLException("The data of the Path " + id + " were not found in the database.");
    }

    public void save() throws JDOMException, IOException, SQLException {
        this.DYNTRACK.getServer().getPluginManager().callEvent(new DataEvent(this, DataActionResult.SAVED));

        if (!this.DYNTRACK.getDynTrackConfig().useDB()) {
            saveInFile();
        } else {
            saveInDatabase();
        }
    }

    public void saveInFile() throws JDOMException, IOException {
        Document doc = (new SAXBuilder()).build(Path.class.getClassLoader().getResource("path.xml").toString());
        Element root = doc.getRootElement();
        root.getChild("id").setText(this.id);
        Element infobox = root.getChild("infobox");

        Element name = infobox.getChild("name");
        name.getChild("val").setText(this.name);
        name.getChild("syn").setText(this.pathSyn);
        name.getChild("hide").setText(String.valueOf(this.hideName));

        Element from = infobox.getChild("from");
        from.getChild("val").setText(this.fromVal);
        from.getChild("syn").setText(this.fromSyn);
        from.getChild("hide").setText(String.valueOf(this.hideFrom));

        Element to = infobox.getChild("to");
        to.getChild("val").setText(this.toVal);
        to.getChild("syn").setText(this.toSyn);
        to.getChild("hide").setText(String.valueOf(this.hideTo));

        Element via = infobox.getChild("via");
        via.getChild("syn").setText(this.viaSyn);
        via.getChild("hide").setText(String.valueOf(this.hideVia));

        root.getChild("connected").setText(String.valueOf(this.connected));
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

        File f = new File(this.DYNTRACK.getDataFolder() + "/Paths/" + this.id + ".xml");
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

        this.DYNTRACK.getServer().getPluginManager().callEvent(new DataEvent(this, DataActionResult.SAVED_IN_FILE));
        MsgSender.cInfo(this.DYNTRACK, "Saved Path \"" + this.id + "\" in file.");
    }

    public void saveInDatabase() throws SQLException {
        DBHandler dbHandler = this.DYNTRACK.getDBHandler();
        String prefix = this.DYNTRACK.getDynTrackConfig().getDB_Prefix();
        ResultSet res = dbHandler.exeQuery("SELECT * FROM " + prefix + "paths WHERE id LIKE \"" + this.id + "\"");

        if (res.first()) {
            dbHandler.exe("UPDATE " + prefix + "paths SET " + "name = \"" + this.name + "\"," + "pathsyn = \"" + this.pathSyn + "\"," + "hidename = " + this.hideName + "," + "fromval = \"" + this.fromVal + "\"," + "fromsyn = \"" + this.fromSyn + "\"," + "hidefrom = " + this.hideFrom + "," + "toval = \"" + this.toVal + "\"," + "tosyn = \"" + this.toSyn + "\"," + "hideto = " + this.hideTo + "," + "viasyn = \"" + this.viaSyn + "\"," + "hidevia = " + this.hideVia + "," + "connected = " + this.connected + "," + "world = \"" + this.world

                    .getName() + "\"," + "style = \"" + this.style
                    .getId() + "\"," + "layer = \"" + this.layer
                    .getId() + "\" " + "WHERE id LIKE \"" + this.id + "\";");
        } else {
            dbHandler.exe("INSERT INTO " + prefix + "paths(id, name, pathsyn, hidename, fromval, fromsyn, hidefrom, toval, tosyn, hideto, viasyn, hidevia, connected, world, style, layer)" + "VALUES(" + "\"" + this.id + "\"," + "\"" + this.name + "\"," + "\"" + this.pathSyn + "\"," + this.hideName + "," + "\"" + this.fromVal + "\"," + "\"" + this.fromSyn + "\"," + this.hideFrom + "," + "\"" + this.toVal + "\"," + "\"" + this.toSyn + "\"," + this.hideTo + "," + "\"" + this.viaSyn + "\"," + this.hideVia + "," + this.connected + "," + "\"" + this.world

                    .getName() + "\"," + "\"" + this.style
                    .getId() + "\"," + "\"" + this.layer
                    .getId() + "\" " + ");");
        }
        res.close();

        dbHandler.exe("DELETE FROM " + prefix + "path_points WHERE path LIKE \"" + this.id + "\";");
        for (int i = 0; i < this.locList.size(); i++) {
            Location current = this.locList.get(i);
            dbHandler.exe("INSERT INTO " + prefix + "path_points(path, i, x, y, z)" + "VALUES(" + "\"" + this.id + "\"," + i + "," + current

                    .getX() + "," + current
                    .getY() + "," + current
                    .getZ() + ");");
        }

        MsgSender.cInfo(this.DYNTRACK, "Saved Path \"" + this.id + "\" in Database.");
        this.DYNTRACK.getServer().getPluginManager().callEvent(new DataEvent(this, DataActionResult.SAVED_IN_DB));
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
            File f = new File(this.DYNTRACK.getDataFolder() + "/Paths/" + this.id + ".xml");
            if (f.delete()) {
                MsgSender.cInfo(this.DYNTRACK, "Deleted file of the Path " + this.id + ".");
                this.DYNTRACK.getServer().getPluginManager().callEvent(new DataEvent(this, DataActionResult.DELETED_SUCCESSFULLY));
                return DataActionResult.DELETED_SUCCESSFULLY;
            }

            if (f.exists()) {
                MsgSender.cBug(this.DYNTRACK, "Could not delete the file of the Path " + this.id + ".");
                return DataActionResult.DELETED_FAILED;
            }
        }
        if (this.DYNTRACK.getDynTrackConfig().useDB())
            try {
                DBHandler dbHandler = this.DYNTRACK.getDBHandler();
                String prefix = this.DYNTRACK.getDynTrackConfig().getDB_Prefix();
                dbHandler.exe("DELETE FROM " + prefix + "path_points WHERE path LIKE \"" + this.id + "\";");

                if (dbHandler.exe("DELETE FROM " + prefix + "paths WHERE id LIKE \"" + this.id + "\";") >= 1) {
                    MsgSender.cInfo(this.DYNTRACK, "Deleted the data from the Path " + this.id + " in the database.");
                    this.DYNTRACK.getServer().getPluginManager().callEvent(new DataEvent(this, DataActionResult.DELETED_SUCCESSFULLY));
                    return DataActionResult.DELETED_SUCCESSFULLY;
                }
                return DataActionResult.DELETED_FAILED;
            } catch (SQLException e) {
                MsgSender.cBug(this.DYNTRACK, "Could not delete the data from the Path " + this.id + " in the database.");
                MsgSender.cBug(this.DYNTRACK, e.getMessage());
                return DataActionResult.DELETED_FAILED;
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
        for (int i = 0; i < n; i++)
            delLocation(removeFrom - i);
    }

    public void setLocationList(List < Location > locList) {
        this.locList = locList;
    }

    public List < Location > getLocationList() {
        return this.locList;
    }

    public double[] getxArray() {
        double[] x = new double[this.locList.size()];
        for (int i = 0; i < this.locList.size(); i++)
            x[i] = this.locList.get(i).getX();
        return x;
    }

    public double[] getyArray() {
        double[] y = new double[this.locList.size()];
        for (int i = 0; i < this.locList.size(); i++)
            y[i] = this.locList.get(i).getY();
        return y;
    }

    public double[] getzArray() {
        double[] z = new double[this.locList.size()];
        for (int i = 0; i < this.locList.size(); i++)
            z[i] = this.locList.get(i).getZ();
        return z;
    }

    public Link[] getLinks() {
        Hashtable < Link, Integer > table = new Hashtable < > ();
        Iterator < Link > it = this.DYNTRACK.getLinks().values().iterator();

        while (it.hasNext()) {
            Link link = it.next();
            if (link.getTable().containsKey(this))
                table.put(link, link.getTable().get(this));
        }

        this.links = (Link[]) table.keySet().toArray((Object[]) new Link[table.keySet().size()]);
        this.locs = new int[this.links.length];

        for (int i = 0; i < this.locs.length; i++)
            this.locs[i] = table.get(this.links[i]).intValue();

        if (this.locs.length > 1)
            quicksort(0, this.links.length - 1);

        return this.links;
    }

    private void quicksort(int pLow, int pHigh) {
        int hLeft = pLow;
        int hRight = pHigh;
        int pivot = this.locs[(pLow + pHigh) / 2];

        while (hLeft <= hRight) {
            while (this.locs[hLeft] < pivot)
                hLeft++;
            while (this.locs[hRight] > pivot)
                hRight--;
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

        if (pLow < hRight)
            quicksort(pLow, hRight);

        if (hLeft < pHigh)
            quicksort(hLeft, pHigh);
    }

    public String getId() {
        return this.id;
    }

    public String getPathSyn() {
        return this.pathSyn;
    }

    public String getName() {
        if (this.name.isEmpty())
            return this.id;
        return this.name;
    }

    public boolean isNameHidden() {
        return this.hideName;
    }

    public String getFromSyn() {
        return this.fromSyn;
    }

    public String getFromVal() {
        return this.fromVal;
    }

    public boolean isFromHidden() {
        return this.hideFrom;
    }

    public String getToSyn() {
        return this.toSyn;
    }

    public String getToVal() {
        return this.toVal;
    }

    public boolean isToHidden() {
        return this.hideTo;
    }

    public boolean isConnected() {
        return this.connected;
    }

    public String getViaSyn() {
        return this.viaSyn;
    }

    public boolean isViaHidden() {
        return this.hideVia;
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

    public void setPathSyn(String pathSyn) {
        this.pathSyn = pathSyn;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHideName(boolean hideName) {
        this.hideName = hideName;
    }

    public void setFromSyn(String fromSyn) {
        this.fromSyn = fromSyn;
    }

    public void setFromVal(String from) {
        this.fromVal = from;
    }

    public void setHideFrom(boolean hideFrom) {
        this.hideFrom = hideFrom;
    }

    public void setToSyn(String toSyn) {
        this.toSyn = toSyn;
    }

    public void setToVal(String to) {
        this.toVal = to;
    }

    public void setHideTo(boolean hideTo) {
        this.hideTo = hideTo;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void setViaSyn(String viaSyn) {
        this.viaSyn = viaSyn;
    }

    public void setHideVia(boolean hideVia) {
        this.hideVia = hideVia;
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