package de.odinoxin.dyntrack;

import de.gcmclan.team.guides.Guide;
import de.gcmclan.team.guides.GuidesPlugin;
import de.odinoxin.dyntrack.enums.DataActionResult;
import de.odinoxin.dyntrack.generals.DataEvent;
import de.odinoxin.dyntrack.generals.MsgSender;
import de.odinoxin.dyntrack.generals.PermHandler;
import de.odinoxin.dyntrack.interfaces.Linkable;
import de.odinoxin.dyntrack.layer.Layer;
import de.odinoxin.dyntrack.link.Link;
import de.odinoxin.dyntrack.path.Path;
import de.odinoxin.dyntrack.square.Square;
import de.odinoxin.dyntrack.style.Style;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jdom2.JDOMException;

public class DynTrack extends JavaPlugin implements Listener {
    private static DynTrack plugin;
    private static final short[] VERSION = new short[] {
            2,
            4,
            3
    };

    private Config config;
    private DynmapHandler dynmapHandler;
    private DBHandler dbHandler;
    private PermHandler permHandler;
    private HashMap < String, Style > styles;
    private HashMap < String, Layer > layers;
    private HashMap < String, Path > paths;
    private HashMap < String, Square > squares;
    private HashMap < String, Link > links;
    private HashMap < Player, Recorder > recMap;
    private HashMap < Player, Intro > introMap;

    public DynTrack() {
        plugin = this;
    }

    public void onDisable() {
        MsgSender.cInfo(this, "Stopping...");
        this.dbHandler = null;

        MsgSender.cInfo(this, "Removing Links from the dynmap...");
        Iterator < Link > itLink = this.links.values().iterator();
        while (itLink.hasNext())
            this.dynmapHandler.remove(itLink.next(), true);

        MsgSender.cInfo(this, "Removing Paths from the dynmap...");
        Iterator < Path > itPath = this.paths.values().iterator();
        while (itPath.hasNext())
            this.dynmapHandler.remove(itPath.next(), true);

        MsgSender.cInfo(this, "Removing Squares from the dynmap...");
        Iterator < Square > itSquare = this.squares.values().iterator();
        while (itSquare.hasNext())
            this.dynmapHandler.remove(itSquare.next(), true);

        MsgSender.cInfo(this, "Removing Layers from the dynmap...");
        Iterator < Layer > itLayer = this.layers.values().iterator();
        while (itLayer.hasNext())
            this.dynmapHandler.remove(itLayer.next(), true);

        MsgSender.cInfo(this, "Stopped.");
    }

    public void onEnable() {
        MsgSender.cInfo(this, "Starting...");

        this.styles = new HashMap < > ();
        this.layers = new HashMap < > ();
        this.paths = new HashMap < > ();
        this.squares = new HashMap < > ();
        this.links = new HashMap < > ();
        this.recMap = new HashMap < > ();
        this.introMap = new HashMap < > ();
        this.config = new Config(this);
        this.permHandler = new PermHandler(this);

        MsgSender.cInfo(this, "Loading commands...");
        CmdHandler cmdHandler = new CmdHandler(this);
        getCommand("dynTrack").setExecutor(cmdHandler);
        getCommand("dynTr").setExecutor(cmdHandler);
        MsgSender.cInfo(this, "Commands loaded.");

        MsgSender.cInfo(this, "Connecting to dynmap...");
        this.dynmapHandler = new DynmapHandler(this);
        MsgSender.cInfo(this, "Connected to dynmap.");

        MsgSender.cInfo(this, "Register event handlers...");
        getServer().getPluginManager().registerEvents(this.dynmapHandler, this);
        getServer().getPluginManager().registerEvents(this, this);
        MsgSender.cInfo(this, "Event handlers registered.");

        loadElements();
        for (Path path: this.paths.values())
            this.dynmapHandler.add(path);

        for (Square square: this.squares.values())
            this.dynmapHandler.add(square);

        for (Link link: this.links.values())
            this.dynmapHandler.add(link);

        getServer().getPluginManager().registerEvents(this, this);
    }

    private void loadElements() {
        loadStyles();
        loadLayers();
        loadPaths();
        loadSquares();
        loadLinks();
    }

    private void loadStyles() {
        MsgSender.cInfo(this, "Loading Styles...");
        if (!this.config.useDB()) {
            File folder = new File(getDataFolder() + "/Styles");
            folder.mkdir();
            for (File f: folder.listFiles()) {
                try {
                    Style st = Style.loadStyle(this, f.getName().replace(".xml", ""));
                    this.styles.put(st.getId().toLowerCase(), st);
                    MsgSender.cInfo(this, "Style loaded from file: " + st.getId());
                } catch (JDOMException | IOException | SQLException e) {
                    MsgSender.cBug(this, "Could not load the Style " + f.getName().replace(".xml", "") + " from file.");
                    MsgSender.cBug(this, e.getMessage());
                }
            }
        } else {
            try {
                ResultSet res = this.dbHandler.exeQuery("SELECT id FROM " + this.dbHandler.getPrefix() + "styles;");
                while (res.next()) {
                    try {
                        Style st = Style.loadStyle(this, res.getString("id"));
                        this.styles.put(st.getId().toLowerCase(), st);
                        MsgSender.cInfo(this, "Style loaded from database: " + st.getId());
                    } catch (SQLException | JDOMException | IOException e) {
                        MsgSender.cBug(this, "Could not load the Style " + res.getString("id") + " from database.");
                        MsgSender.cBug(this, e.getMessage());
                    }
                }
            } catch (SQLException e) {
                MsgSender.cBug(this, "Could not load any Style from database.");
                MsgSender.cBug(this, e.getMessage());
            }
        }
        MsgSender.cInfo(this, "Styles loaded.");
    }

    private void loadLayers() {
        MsgSender.cInfo(this, "Loading Layers...");
        if (!this.config.useDB()) {
            File folder = new File(getDataFolder() + "/Layers");
            folder.mkdir();
            for (File f: folder.listFiles()) {
                try {
                    Layer lay = Layer.loadLayer(this, f.getName().replace(".xml", ""));
                    this.layers.put(lay.getId().toLowerCase(), lay);
                    MsgSender.cInfo(this, "Layer loaded from file: " + lay.getId());
                } catch (JDOMException | IOException | SQLException e) {
                    MsgSender.cBug(this, "Could not load the Layer " + f.getName().replace(".xml", "") + " from file.");
                    MsgSender.cBug(this, e.getMessage());
                }
            }
        } else {
            try {
                ResultSet res = this.dbHandler.exeQuery("SELECT id FROM " + this.dbHandler.getPrefix() + "layers;");
                while (res.next()) {
                    try {
                        Layer lay = Layer.loadLayer(this, res.getString("id"));
                        this.layers.put(lay.getId().toLowerCase(), lay);
                        MsgSender.cInfo(this, "Layer loaded from database: " + lay.getId());
                    } catch (SQLException | JDOMException | IOException e) {
                        MsgSender.cBug(this, "Could not load the Layer " + res.getString("id") + " from database.");
                        MsgSender.cBug(this, e.getMessage());
                    }
                }
            } catch (SQLException e) {
                MsgSender.cBug(this, "Could not load any Layer from database.");
                MsgSender.cBug(this, e.getMessage());
            }
        }
        MsgSender.cInfo(this, "Layers loaded.");
    }

    private void loadPaths() {
        MsgSender.cInfo(this, "Loading Paths...");
        if (!this.config.useDB()) {
            File folder = new File(getDataFolder() + "/Paths");
            folder.mkdir();
            for (File f: folder.listFiles()) {
                try {
                    Path p = Path.loadPath(this, f.getName().replace(".xml", ""));
                    if (p == null) {
                        MsgSender.cBug(this, "Failed to load Path " + f.getName().replace(".xml", "") + " from file.");
                    } else {
                        this.paths.put(p.getId().toLowerCase(), p);
                        MsgSender.cInfo(this, "Path loaded from file: " + p.getId());
                    }
                } catch (JDOMException | IOException | SQLException e) {
                    MsgSender.cBug(this, "Could not load the Path " + f.getName().replace(".xml", "") + " from file.");
                    MsgSender.cBug(this, e.getMessage());
                }
            }
        } else {
            try {
                ResultSet res = this.dbHandler.exeQuery("SELECT id,world FROM " + this.dbHandler.getPrefix() + "paths;");
                while (res.next()) {
                    try {
                        Path p = Path.loadPath(this, res.getString("id"));

                        // Skip if world not exists
                        if (Bukkit.getServer().getWorld(res.getString("world")) != null) {
                            this.paths.put(p.getId().toLowerCase(), p);
                            MsgSender.cInfo(this, "Path loaded from database: " + p.getId());
                        } else
                            MsgSender.cBug(this, "Could not load the Path " + res.getString("id") + " from database. (World not found)");
                    } catch (SQLException | JDOMException | IOException e) {
                        MsgSender.cBug(this, "Could not load the Path " + res.getString("id") + " from database.");
                        MsgSender.cBug(this, e.getMessage());
                    }
                }
            } catch (SQLException e) {
                MsgSender.cBug(this, "Could not load any Path from database.");
                MsgSender.cBug(this, e.getMessage());
            }
        }
        MsgSender.cInfo(this, "Paths loaded.");
    }

    private void loadSquares() {
        MsgSender.cInfo(this, "Loading Squares...");
        if (!this.config.useDB()) {
            File folder = new File(getDataFolder() + "/Squares");
            folder.mkdir();
            for (File f: folder.listFiles()) {
                try {
                    Square sq = Square.loadSquare(this, f.getName().replace(".xml", ""));
                    if (sq == null) {
                        MsgSender.cBug(this, "Failed to load Square " + f.getName().replace(".xml", "") + " from file.");
                    } else {
                        this.squares.put(sq.getId().toLowerCase(), sq);
                        MsgSender.cInfo(this, "Square loaded from file: " + sq.getId());
                    }
                } catch (JDOMException | IOException | SQLException e) {
                    MsgSender.cBug(this, "Could not load the Square " + f.getName().replace(".xml", "") + " from file.");
                    MsgSender.cBug(this, e.getMessage());
                }
            }
        } else {
            try {
                ResultSet res = this.dbHandler.exeQuery("SELECT id FROM " + this.dbHandler.getPrefix() + "squares;");
                while (res.next()) {
                    try {
                        Square sq = Square.loadSquare(this, res.getString("id"));
                        this.squares.put(sq.getId().toLowerCase(), sq);
                        MsgSender.cInfo(this, "Square loaded from database: " + sq.getId());
                    } catch (SQLException | JDOMException | IOException e) {
                        MsgSender.cBug(this, "Could not load the Square " + res.getString("id") + " from database.");
                        MsgSender.cBug(this, e.getMessage());
                    }
                }
            } catch (SQLException e) {
                MsgSender.cBug(this, "Could not load any Square from database.");
                MsgSender.cBug(this, e.getMessage());
            }
        }
        MsgSender.cInfo(this, "Squares loaded.");
    }

    private void loadLinks() {
        MsgSender.cInfo(this, "Loading Links...");
        if (!this.config.useDB()) {
            File folder = new File(getDataFolder() + "/Links");
            folder.mkdir();
            for (File f: folder.listFiles()) {
                try {
                    Link link = Link.loadLink(this, f.getName().replace(".xml", ""));
                    if (link == null) {
                        MsgSender.cBug(this, "Failed to load Link " + f.getName().replace(".xml", "") + " from file.");
                    } else {
                        this.links.put(link.getId().toLowerCase(), link);
                        MsgSender.cInfo(this, "Link loaded from file: " + link.getId());
                    }
                } catch (JDOMException | IOException | SQLException e) {
                    MsgSender.cBug(this, "Could not load the Link " + f.getName().replace(".xml", "") + " from file.");
                    MsgSender.cBug(this, e.getMessage());
                }
            }
        } else {
            try {
                ResultSet res = this.dbHandler.exeQuery("SELECT id FROM " + this.dbHandler.getPrefix() + "links;");
                while (res.next()) {
                    try {
                        Link link = Link.loadLink(this, res.getString("id"));
                        this.links.put(link.getId().toLowerCase(), link);
                        MsgSender.cInfo(this, "Link loaded from database: " + link.getId());
                    } catch (SQLException | JDOMException | IOException e) {
                        MsgSender.cBug(this, "Could not load the Link " + res.getString("id") + " from database.");
                        MsgSender.cBug(this, e.getMessage());
                        e.printStackTrace();
                    }
                }
            } catch (SQLException e) {
                MsgSender.cBug(this, "Could not load any Link from database.");
                MsgSender.cBug(this, e.getMessage());
            }
        }
        MsgSender.cInfo(this, "Links loaded.");
    }

    public Style getStyle(String id) {
        return this.styles.get(id.toLowerCase());
    }

    public HashMap < String, Style > getStyles() {
        return this.styles;
    }

    @EventHandler
    public void styleEvent(DataEvent < Style > ev) {
        if (ev.isCancelled() ||
                !(ev.getData() instanceof Style))
            return;
        if (ev.getAction() == DataActionResult.DELETED_SUCCESSFULLY) {
            this.styles.remove(ev.getData().getId().toLowerCase());
        } else if (ev.getAction() == DataActionResult.SAVED) {
            this.styles.put(ev.getData().getId().toLowerCase(), ev.getData());
        }
    }

    public Layer getLayer(String id) {
        return this.layers.get(id.toLowerCase());
    }

    public HashMap < String, Layer > getLayers() {
        return this.layers;
    }

    @EventHandler
    public void layerEvent(DataEvent < Layer > ev) {
        if (ev.isCancelled() ||
                !(ev.getData() instanceof Layer))
            return;
        if (ev.getAction() == DataActionResult.DELETED_SUCCESSFULLY) {
            this.layers.remove(ev.getData().getId().toLowerCase());
        } else if (ev.getAction() == DataActionResult.SAVED) {
            this.layers.put(ev.getData().getId().toLowerCase(), ev.getData());
        }
    }

    public Path getPath(String id) {
        return this.paths.get(id.toLowerCase());
    }

    public HashMap < String, Path > getPaths() {
        return this.paths;
    }

    @EventHandler
    public void pathEvent(DataEvent < Path > ev) {
        if (ev.isCancelled() ||
                !(ev.getData() instanceof Path))
            return;
        if (ev.getAction() == DataActionResult.DELETED_SUCCESSFULLY) {
            this.paths.remove(ev.getData().getId().toLowerCase());
        } else if (ev.getAction() == DataActionResult.SAVED) {
            this.paths.put(ev.getData().getId().toLowerCase(), ev.getData());
        }
    }

    public Square getSquare(String id) {
        return this.squares.get(id.toLowerCase());
    }

    public HashMap < String, Square > getSquares() {
        return this.squares;
    }

    @EventHandler
    public void squareEvent(DataEvent < Square > ev) {
        if (ev.isCancelled() ||
                !(ev.getData() instanceof Square))
            return;
        if (ev.getAction() == DataActionResult.DELETED_SUCCESSFULLY) {
            this.squares.remove(ev.getData().getId().toLowerCase());
        } else if (ev.getAction() == DataActionResult.SAVED) {
            this.squares.put(ev.getData().getId().toLowerCase(), ev.getData());
        }
    }

    public Link getLink(String id) {
        return this.links.get(id.toLowerCase());
    }

    public HashMap < String, Link > getLinks() {
        return this.links;
    }

    @EventHandler
    public void linkEvent(DataEvent < Link > ev) {
        if (ev.isCancelled() ||
                !(ev.getData() instanceof Link))
            return;
        if (ev.getAction() == DataActionResult.DELETED_SUCCESSFULLY) {
            this.links.remove(ev.getData().getId().toLowerCase());
        } else if (ev.getAction() == DataActionResult.SAVED) {
            this.links.put(ev.getData().getId().toLowerCase(), ev.getData());
        }
    }

    public Linkable getLinkable(String id) {
        Iterator < Path > itPath = this.paths.values().iterator();
        while (itPath.hasNext()) {
            Linkable current = itPath.next();
            if (current.getId().equalsIgnoreCase(id))
                return current;
        }
        Iterator < Square > itSquare = this.squares.values().iterator();
        while (itSquare.hasNext()) {
            Linkable current = itSquare.next();
            if (current.getId().equalsIgnoreCase(id))
                return current;
        }
        return null;
    }

    boolean anyRecs() {
        return (this.recMap.size() > 0);
    }

    Recorder getRec(Player p) {
        return this.recMap.get(p);
    }

    public void addRec(Recorder rec) {
        this.recMap.put(rec.getPlayer(), rec);
        getServer().getPluginManager().registerEvents(rec, this);
        rec.startTracking();
    }

    boolean removeRecs(Player p) {
        Recorder rec = this.recMap.remove(p);
        if (rec != null) {
            rec.stopTracking();
            HandlerList.unregisterAll(rec);
            return true;
        }
        return false;
    }

    boolean setTracking(Player p, boolean tracking) {
        Recorder rec = this.recMap.get(p);
        if (rec != null) {
            rec.setTracking(tracking);
            return true;
        }
        return false;
    }

    @EventHandler
    public void join(PlayerJoinEvent ev) {
        GuidesPlugin pGuides = getGuidesPlugin();
        Iterator < CommandSender > it = pGuides.getGuides().keySet().iterator();
        while (it.hasNext()) {
            CommandSender current = it.next();
            if (current.getName().equals(ev.getPlayer().getName())) {
                pGuides.replaceCmdSenderInGuides(current, ev.getPlayer());
                break;
            }
        }
        if (pGuides.getGuides().containsKey(ev.getPlayer())) {
            pGuides.setGuideActive(ev.getPlayer(), true);
        } else if ((!PermHandler.usingVault() || (
                PermHandler.hasPerms(ev.getPlayer(), "dyntrack.info.intro") &&
                !PermHandler.hasPerms(ev.getPlayer(), "dyntrack.info.intro.noauto"))) &&
                !this.introMap.containsKey(ev.getPlayer()) &&
                getStyles().size() == 0 &&
                getLayers().size() == 0 &&
                getPaths().size() == 0 &&
                getSquares().size() == 0 &&
                getLinks().size() == 0) {
            Intro intro = new Intro(this, ev.getPlayer(), (byte) 0);
            this.introMap.put(ev.getPlayer(), intro);
            intro.runTaskLaterAsynchronously(this, (int)(Math.random() * 6000.0D + 3000.0D));
            MsgSender.cInfo("DynTrack", "Registered an intro to player: " + ev.getPlayer().getName());
        }
    }

    @EventHandler
    public void quit(PlayerQuitEvent ev) {
        getGuidesPlugin().setGuideActive(ev.getPlayer(), false);
        this.introMap.remove(ev.getPlayer());
    }

    public static String getVersion() {
        StringBuilder version = new StringBuilder();
        version.append(VERSION[0]);
        version.append(".");
        version.append(VERSION[1]);
        version.append("b");
        version.append(VERSION[2]);
        return version.toString();
    }

    public DynTrackAPI getAPI() {
        return new DynTrackAPI(this);
    }

    public void setDBHandler(DBHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    public DBHandler getDBHandler() {
        return this.dbHandler;
    }

    public Config getDynTrackConfig() {
        return this.config;
    }

    public GuidesPlugin getGuidesPlugin() {
        return (GuidesPlugin) getServer().getPluginManager().getPlugin("GuidesPlugin");
    }

    public boolean isUsed(String id) {
        id = id.toLowerCase();
        if (id.equalsIgnoreCase("config") || this.styles
                .containsKey(id) || this.layers
                .containsKey(id) || this.paths
                .containsKey(id) || this.squares
                .containsKey(id) || this.links
                .containsKey(id))
            return true;
        for (Stack < Guide > stack: getGuidesPlugin().getGuides().values()) {
            for (Guide guide: stack) {
                if (guide.getReceiver().usesId(id))
                    return true;
            }
        }
        return false;
    }

    public boolean isUsed(Style st) {
        Iterator < Path > itPath = this.paths.values().iterator();
        while (itPath.hasNext()) {
            if (itPath.next().getStyle().equals(st))
                return true;
        }
        Iterator < Square > itSquare = this.squares.values().iterator();
        while (itSquare.hasNext()) {
            if (itSquare.next().getStyle().equals(st))
                return true;
        }
        Iterator < Link > itLink = this.links.values().iterator();
        while (itLink.hasNext()) {
            if (itLink.next().getStyle().equals(st))
                return true;
        }
        return false;
    }

    public boolean isUsed(Layer lay) {
        Iterator < Path > itPath = this.paths.values().iterator();
        while (itPath.hasNext()) {
            if (itPath.next().getLayer().equals(lay))
                return true;
        }
        Iterator < Square > itSquare = this.squares.values().iterator();
        while (itSquare.hasNext()) {
            if (itSquare.next().getLayer().equals(lay))
                return true;
        }
        Iterator < Link > itLink = this.links.values().iterator();
        while (itLink.hasNext()) {
            if (itLink.next().getLayer().equals(lay))
                return true;
        }
        return false;
    }

    public static DynTrack getInstance() {
        return plugin;
    }
}