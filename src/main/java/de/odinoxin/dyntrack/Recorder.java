package de.odinoxin.dyntrack;

import de.odinoxin.dyntrack.generals.MsgSender;
import de.odinoxin.dyntrack.interfaces.Linkable;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import java.util.Objects;

public class Recorder implements Listener {
    private final Player P;
    private final SPEED speed;
    private final double SECTOR;

    private final Linkable trkele;
    private boolean tracking = false;
    private boolean enableHand = true;
    private int insertAt;
    private long sysTime;

    public enum SPEED {
        BY_HAND,
        HIGHEST,
        HIGHER,
        HIGH,
        NORMAL,
        SLOW,
        SLOWER,
        SLOWEST,
        MINECART
    }

    private Recorder(Player p, Linkable trackingelement, SPEED speed, int insertAt, boolean enableHand) {
        this.P = p;
        this.speed = speed;
        this.trkele = trackingelement;
        switch (speed) {
            case BY_HAND:
                this.SECTOR = -1.0D;
                break;
            case HIGHEST:
                this.SECTOR = 0.0D;
                break;
            case HIGHER:
                this.SECTOR = 100.0D;
                break;
            case HIGH:
                this.SECTOR = 200.0D;
                break;
            case NORMAL:
                this.SECTOR = 1000.0D;
                break;
            case SLOW:
                this.SECTOR = 5000.0D;
                break;
            case SLOWER:
                this.SECTOR = 10000.0D;
                break;
            case SLOWEST:
                this.SECTOR = 60000.0D;
                break;
            case MINECART:
                this.SECTOR = -1.0D;
                break;
            default:
                this.SECTOR = -1.0D;
                break;
        }
        if (insertAt > this.trkele.getLocationList().size()) {
            this.insertAt = this.trkele.getLocationList().size();
        } else if (insertAt < 0) {
            this.insertAt = 0;
        } else {
            this.insertAt = insertAt;
        }
        this.enableHand = enableHand;
    }

    public Recorder(Player p, Linkable trackingelement, SPEED speed) {
        this(p, trackingelement, speed, true);
    }

    public Recorder(Player p, Linkable trackingelement, SPEED speed, boolean enableHand) {
        this(p, trackingelement, speed, 0, enableHand);
    }

    public Recorder(Player p, Linkable trackingelement, int insertAt, SPEED speed) {
        this(p, trackingelement, insertAt, speed, true);
    }

    public Recorder(Player p, Linkable trackingelement, int insertAt, SPEED speed, boolean enableHand) {
        this(p, trackingelement, speed, insertAt, enableHand);
    }

    void stopTracking() {
        this.tracking = false;
        MsgSender.pInfo("DynTrack", this.P, "You finished tracking!");
        try {
            this.trkele.save();
            MsgSender.pInfo("DynTrack", this.P, ChatColor.DARK_AQUA + this.trkele.getId() + ChatColor.RESET + " saved and loaded on dynmap!");
        } catch (Exception e) {
            MsgSender.pErr("DynTrack", this.P, "Could not save " + ChatColor.DARK_AQUA + this.trkele.getId() + ChatColor.RESET + ".");
            MsgSender.pErr("DynTrack", this.P, e.getMessage());
        }
    }

    void startTracking() {
        MsgSender.pEmpty("DynTrack", this.P, ChatColor.GRAY);
        MsgSender.pInfo("DynTrack", this.P, "You can now go tracking!");
        MsgSender.pEmpty("DynTrack", this.P, ChatColor.GREEN);
        MsgSender.pInfo("DynTrack", this.P, ChatColor.GRAY + "Use " + ChatColor.DARK_GREEN + "/dynTrack stop");
        MsgSender.pInfo("DynTrack", this.P, ChatColor.GRAY + "to stop tracking.");
        MsgSender.pEmpty("DynTrack", this.P, ChatColor.GREEN);
        MsgSender.pInfo("DynTrack", this.P, ChatColor.GRAY + "Use " + ChatColor.DARK_GREEN + "/dynTrack back [n]");
        MsgSender.pInfo("DynTrack", this.P, ChatColor.GRAY + "to remove the last [n] Trackpoints.");
        if (this.enableHand)
            MsgSender.pInfo("DynTrack", this.P, ChatColor.GRAY + "You can hit the air, to remove the last Trackpoint.");
        MsgSender.pEmpty("DynTrack", this.P, ChatColor.GREEN);
        MsgSender.pInfo("DynTrack", this.P, ChatColor.GRAY + "Use " + ChatColor.DARK_GREEN + "/dynTrack pause");
        MsgSender.pInfo("DynTrack", this.P, ChatColor.GRAY + "to have a break, while tracking.");
        this.trkele.setWorld(this.P.getWorld());
        if (this.speed != SPEED.BY_HAND && this.speed != SPEED.MINECART)
            addLoc(this.P.getLocation());
        this.sysTime = System.currentTimeMillis();
        this.tracking = true;
    }

    @EventHandler
    void movePlayer(PlayerMoveEvent ev) {
        if (isTracking() && ev
                .getPlayer() == this.P && this.speed != SPEED.BY_HAND && this.speed != SPEED.MINECART)
            if ((System.currentTimeMillis() - this.sysTime) >= this.SECTOR) {
                addLoc(ev.getPlayer().getLocation());
                this.sysTime = System.currentTimeMillis();
            }
    }

    @EventHandler
    void moveCart(VehicleMoveEvent ev) {
        if (isTracking() && ev
                .getVehicle().getPassenger() != null && ev
                .getVehicle().getPassenger().equals(this.P) && this.speed == SPEED.MINECART) {
            Location c = ev.getTo();
            c.setX((int) c.getX() - 0.5D);
            c.setY((int) c.getY() + 0.5D);
            c.setZ((int) c.getZ() + 0.5D);
            if (this.insertAt >= 1) {
                Location b = this.trkele.getLocationList().get(this.insertAt - 1);
                if (c.getBlockX() == b.getBlockX() && c
                        .getBlockY() == b.getBlockY() && c
                        .getBlockZ() == b.getBlockZ())
                    return;
                if (this.insertAt >= 2) {
                    double factor;
                    Location a = this.trkele.getLocationList().get(this.insertAt - 2);
                    int abX = b.getBlockX() - a.getBlockX();
                    int abY = b.getBlockY() - a.getBlockY();
                    int abZ = b.getBlockZ() - a.getBlockZ();
                    if (abX != 0) {
                        factor = (c.getBlockX() - a.getBlockX()) / abX;
                    } else if (abZ != 0) {
                        factor = (c.getBlockZ() - a.getBlockZ()) / abZ;
                    } else if (abY != 0) {
                        factor = (c.getBlockY() - a.getBlockY()) / abY;
                    } else {
                        return;
                    }
                    if (factor > 0.0D && a
                            .getBlockX() + factor * abX == c.getBlockX() && a
                            .getBlockY() + factor * abY == c.getBlockY() && a
                            .getBlockZ() + factor * abZ == c.getBlockZ())
                    delLocation(false);
                }
            }
            addLoc(c);
        }
    }

    @EventHandler
    void click(PlayerInteractEvent ev) {
        if (!isTracking() || ev
                .getPlayer() != this.P || !this.enableHand || this.speed == SPEED.MINECART)
            return;
        if (ev.getAction() == Action.RIGHT_CLICK_BLOCK || ev
                .getAction() == Action.LEFT_CLICK_BLOCK) {
            ev.setCancelled(true);
            Location loc = Objects.requireNonNull(ev.getClickedBlock()).getLocation();
            loc.setX(loc.getX() + 0.5D);
            loc.setY(loc.getY() + 0.5D);
            loc.setZ(loc.getZ() + 0.5D);
            if (!addLoc(loc))
                MsgSender.pWarn("DynTrack", this.P, "This was the already the last location!");
        }
        if (ev.getAction() == Action.LEFT_CLICK_AIR || ev
                .getAction() == Action.RIGHT_CLICK_AIR) {
            ev.setCancelled(true);
            delLocation(true);
        }
    }

    @EventHandler
    void place(BlockPlaceEvent ev) {
        if (isTracking() && ev
                .getPlayer() == this.P && this.enableHand && this.speed != SPEED.MINECART) {
            ev.setCancelled(true);
            Location loc = ev.getBlockPlaced().getLocation();
            loc.setX(loc.getX() + 0.5D);
            loc.setY(loc.getY() + 0.5D);
            loc.setZ(loc.getZ() + 0.5D);
            if (!addLoc(loc))
                MsgSender.pWarn("DynTrack", this.P, "This was the already the last location!");
        }
    }

    @EventHandler
    void breakBlock(BlockBreakEvent ev) {
        if (isTracking() && ev
                .getPlayer() == this.P && this.enableHand && this.speed != SPEED.MINECART) {
            ev.setCancelled(true);
            if (ev.getBlock().getX() == this.trkele.getLocationList().get(this.insertAt - 1).getBlockX() && ev
                    .getBlock().getY() == this.trkele.getLocationList().get(this.insertAt - 1).getBlockY() && ev
                    .getBlock().getZ() == this.trkele.getLocationList().get(this.insertAt - 1).getBlockZ())
                delLocation(true);
        }
    }

    private boolean addLoc(Location loc) {
        if (this.trkele.addLocation(loc, this.insertAt)) {
            int i = this.insertAt;
            this.insertAt++;
            MsgSender.pInfo("DynTrack", this.P, "Added Trackpoint " + ChatColor.GRAY + "#" + ChatColor.LIGHT_PURPLE + this.insertAt + ChatColor.GRAY + "/" + ChatColor.LIGHT_PURPLE + this.trkele

                    .getLocationList().size() + ChatColor.RESET + " @ (" + ChatColor.LIGHT_PURPLE + this.trkele

                    .getLocationList().get(i).getBlockX() + ChatColor.GRAY + "x" + ChatColor.RESET + "|" + ChatColor.LIGHT_PURPLE + this.trkele
                    .getLocationList().get(i).getBlockY() + ChatColor.GRAY + "y" + ChatColor.RESET + "|" + ChatColor.LIGHT_PURPLE + this.trkele
                    .getLocationList().get(i).getBlockZ() + ChatColor.GRAY + "z" + ChatColor.RESET + ")");
            return true;
        }
        return false;
    }

    void delLocation(boolean informPlayer) {
        if (this.insertAt > 0) {
            this.insertAt--;
            this.trkele.delLocation(this.insertAt);
            if (informPlayer) {
                MsgSender.pInfo("DynTrack", this.P, "You removed the last Trackpoint.");
                MsgSender.pInfo("DynTrack", this.P, "Trackpoints left: " + ChatColor.LIGHT_PURPLE + this.trkele.getLocationList().size());
            }
        } else {
            MsgSender.pErr("DynTrack", this.P, "No more Trackpoints to remove.");
        }
    }

    void delLocation(int n) {
        if (this.insertAt >= n) {
            for (int i = 0; i < n; i++) {
                this.insertAt--;
                this.trkele.delLocation(this.insertAt);
            }
            MsgSender.pInfo("DynTrack", this.P, "You removed " + ChatColor.LIGHT_PURPLE + n + ChatColor.RESET + " Trackpoints.");
        } else {
            MsgSender.pErr("DynTrack", this.P, "There are only " + ChatColor.LIGHT_PURPLE + this.insertAt + ChatColor.RESET + " Trackpoints to remove.");
        }
    }

    Player getPlayer() {
        return this.P;
    }

    Linkable getTrackelement() {
        return this.trkele;
    }

    boolean isTracking() {
        return this.tracking;
    }

    void setTracking(boolean tracking) {
        this.tracking = tracking;
        if (this.tracking) {
            MsgSender.pInfo("DynTrack", this.P, "You can now go on tracking.");
        } else {
            MsgSender.pInfo("DynTrack", this.P, "You paused tracking. Continue with " + ChatColor.DARK_GREEN + "/dynTr continue");
        }
    }
}