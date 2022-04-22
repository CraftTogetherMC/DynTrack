package de.odinoxin.dyntrack.editor;

import de.gcmclan.team.guides.Guide;
import de.gcmclan.team.guides.GuideReceiver;
import de.gcmclan.team.guides.Segment;
import de.odinoxin.dyntrack.DynTrack;
import de.odinoxin.dyntrack.generals.MsgSender;
import de.odinoxin.dyntrack.generals.PermHandler;
import de.odinoxin.dyntrack.layer.Layer;
import de.odinoxin.dyntrack.link.Link;
import de.odinoxin.dyntrack.path.Path;
import de.odinoxin.dyntrack.square.Square;
import de.odinoxin.dyntrack.style.Style;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditorBuilder implements GuideReceiver {
    private final DynTrack DYNTRACK;
    public static final Segment[] SEGMENTS = new Segment[] {
            new Segment(0, "Enter the ID of the Element you want to edit.")
    };

    public EditorBuilder(DynTrack DYNTRACK) {
        this.DYNTRACK = DYNTRACK;
    }

    public int receive(Guide guide, Segment seg, String msg) {
        Style st = this.DYNTRACK.getStyle(msg);
        if (st != null) {

            if (guide.getCmdSender() instanceof Player &&
                    !PermHandler.hasPerms((Player) guide.getCmdSender(), "dyntrack.edit.style")) {

                MsgSender.permMsg(this.DYNTRACK, (Player) guide.getCmdSender(), "dyntrack.edit.style");
                return -1;
            }
            MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.GRAY + "You are now editing the Style " + ChatColor.DARK_AQUA + st.getId() + ChatColor.GRAY + ".");
            guide.exitAndFollow(new Guide(this.DYNTRACK.getGuidesPlugin(), new StyleEditor(guide.getCmdSender(), st), this.DYNTRACK, guide.getGuideSyn(), guide.getCmdSender(), StyleEditor.SEGMENTS));
            return -4;
        }

        Layer lay = this.DYNTRACK.getLayer(msg);
        if (lay != null) {

            if (guide.getCmdSender() instanceof Player &&
                    !PermHandler.hasPerms((Player) guide.getCmdSender(), "dyntrack.edit.layer")) {

                MsgSender.permMsg(this.DYNTRACK, (Player) guide.getCmdSender(), "dyntrack.edit.layer");
                return -1;
            }
            MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.GRAY + "You are now editing the Layer " + ChatColor.DARK_AQUA + lay.getId() + ChatColor.GRAY + ".");
            guide.exitAndFollow(new Guide(this.DYNTRACK.getGuidesPlugin(), new LayerEditor(guide.getCmdSender(), lay), this.DYNTRACK, guide.getGuideSyn(), guide.getCmdSender(), LayerEditor.SEGMENTS));
            return -4;
        }

        Path p = this.DYNTRACK.getPath(msg);
        if (p != null) {

            if (guide.getCmdSender() instanceof Player &&
                    !PermHandler.hasPerms((Player) guide.getCmdSender(), "dyntrack.edit.path")) {

                MsgSender.permMsg(this.DYNTRACK, (Player) guide.getCmdSender(), "dyntrack.edit.path");
                return -1;
            }
            MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.GRAY + "You are now editing the Path " + ChatColor.DARK_AQUA + p.getId() + ChatColor.GRAY + ".");
            if (guide.getCmdSender() instanceof Player) {

                guide.exitAndFollow(new Guide(this.DYNTRACK.getGuidesPlugin(), new PathEditor(guide.getCmdSender(), p), this.DYNTRACK, guide.getGuideSyn(), guide.getCmdSender(), PathEditor.SEGMENTS_PLAYER));
            } else {

                guide.exitAndFollow(new Guide(this.DYNTRACK.getGuidesPlugin(), new PathEditor(guide.getCmdSender(), p), this.DYNTRACK, guide.getGuideSyn(), guide.getCmdSender(), PathEditor.SEGMENTS_CONSOLE));
            }
            return -4;
        }

        Square sq = this.DYNTRACK.getSquare(msg);
        if (sq != null) {

            if (guide.getCmdSender() instanceof Player &&
                    !PermHandler.hasPerms((Player) guide.getCmdSender(), "dyntrack.edit.square")) {

                MsgSender.permMsg(this.DYNTRACK, (Player) guide.getCmdSender(), "dyntrack.edit.square");
                return -1;
            }
            MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.GRAY + "You are now editing the Square " + ChatColor.DARK_AQUA + sq.getId() + ChatColor.GRAY + ".");
            if (guide.getCmdSender() instanceof Player) {

                guide.exitAndFollow(new Guide(this.DYNTRACK.getGuidesPlugin(), new SquareEditor(guide.getCmdSender(), sq), this.DYNTRACK, guide.getGuideSyn(), guide.getCmdSender(), SquareEditor.SEGMENTS_PLAYER));
            } else {

                guide.exitAndFollow(new Guide(this.DYNTRACK.getGuidesPlugin(), new SquareEditor(guide.getCmdSender(), sq), this.DYNTRACK, guide.getGuideSyn(), guide.getCmdSender(), SquareEditor.SEGMENTS_CONSOLE));
            }
            return -4;
        }

        Link link = this.DYNTRACK.getLink(msg);
        if (link != null) {

            if (guide.getCmdSender() instanceof Player &&
                    !PermHandler.hasPerms((Player) guide.getCmdSender(), "dyntrack.edit.link")) {

                MsgSender.permMsg(this.DYNTRACK, (Player) guide.getCmdSender(), "dyntrack.edit.link");
                return -1;
            }
            MsgSender.sInfo(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.GRAY + "You are now editing the Link " + ChatColor.DARK_AQUA + link.getId() + ChatColor.GRAY + ".");
            if (guide.getCmdSender() instanceof Player) {

                guide.exitAndFollow(new Guide(this.DYNTRACK.getGuidesPlugin(), new LinkEditor(guide.getCmdSender(), link), this.DYNTRACK, guide.getGuideSyn(), guide.getCmdSender(), LinkEditor.SEGMENTS_PLAYER));
            } else {

                guide.exitAndFollow(new Guide(this.DYNTRACK.getGuidesPlugin(), new LinkEditor(guide.getCmdSender(), link), this.DYNTRACK, guide.getGuideSyn(), guide.getCmdSender(), LinkEditor.SEGMENTS_CONSOLE));
            }
            return -4;
        }

        MsgSender.sErr(this.DYNTRACK, guide.getCmdSender(), "The ID " + ChatColor.RED + msg + ChatColor.RESET + " is unused.");
        return 0;
    }

    public void finish(CommandSender arg0) {}

    public boolean usesId(String arg0) {
        return false;
    }
}