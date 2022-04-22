package de.odinoxin.dyntrack.editor;

import de.gcmclan.team.guides.Guide;
import de.gcmclan.team.guides.GuideReceiver;
import de.gcmclan.team.guides.Segment;
import de.odinoxin.dyntrack.DynTrack;
import de.odinoxin.dyntrack.enums.DataActionResult;
import de.odinoxin.dyntrack.enums.TrinaryAnswer;
import de.odinoxin.dyntrack.generals.MethodPool;
import de.odinoxin.dyntrack.generals.MsgSender;
import de.odinoxin.dyntrack.interfaces.Drawable;
import de.odinoxin.dyntrack.layer.Layer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jdom2.JDOMException;

public class LayerEditor implements GuideReceiver {
    private final CommandSender S;
    private Layer lay;
    public static final Segment[] SEGMENTS = new Segment[] {
            new Segment(0, "What would you like to edit?" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "You can edit:;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "ID;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Name;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Headline;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Visibility;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Minzoom;" + ChatColor.DARK_GRAY + "   > " + ChatColor.DARK_GREEN + "Priority", false, false, -1, -1), new Segment(1, "Enter the new ID.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "ID's are unique.", false, false, 0, -1), new Segment(2, "Enter the new name.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Names are not unique.", false, true, 0, -1), new Segment(3, "Should the name used as the headline of the;infobox?;" + ChatColor.DARK_GRAY + " > " + ChatColor.DARK_GREEN + "yes" + ChatColor.DARK_GRAY + " | " + ChatColor.DARK_GREEN + "no", false, false, 0, -1), new Segment(4, "Should the Layer be visible by default?;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Hidden Layers can be activated in the upper left;" + ChatColor.GRAY + "   of the dynmap.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Type " + ChatColor.DARK_GREEN + "yes" + ChatColor.GRAY + " or " + ChatColor.DARK_GREEN + "no" + ChatColor.GRAY + ".", false, false, 0, -1), new Segment(5, "Enter the new minzoom.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Minzoom is the zoom, which is at least needed to;" + ChatColor.GRAY + "   display the Layer.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Possible: 0 to 8", false, false, 0, -1), new Segment(6, "Enter the new priority.;" + ChatColor.DARK_GRAY + " > " + ChatColor.GRAY + "Possible: " + -2147483648 + " to " + 2147483647, false, false, 0, -1)
    };

    public LayerEditor(CommandSender s, Layer lay) {
        this.S = s;
        this.lay = lay;
    }
    public int receive(Guide guide, Segment seg, String msg) {
        Layer newLayer;
        HashSet < Drawable > toUpdate, allDraws;
        Iterator < Drawable > it;
        Drawable[] draws;
        int i;
        TrinaryAnswer answer;
        switch (seg.getID()) {

            case 0:
                if (msg.equalsIgnoreCase("ID")) {
                    return 1;
                }
                if (msg.equalsIgnoreCase("Name")) {
                    return 2;
                }
                if (msg.equalsIgnoreCase("Headline")) {
                    return 3;
                }
                if (msg.equalsIgnoreCase("Visibility")) {
                    return 4;
                }
                if (msg.equalsIgnoreCase("Minzoom")) {
                    return 5;
                }
                if (msg.equalsIgnoreCase("Priority")) {
                    return 6;
                }

                MsgSender.sErr(guide.getGuideSyn(), this.S, ChatColor.RED + msg + ChatColor.RESET + " cannot be edited.");
                return 0;

            case 1:
                if (((DynTrack) guide.getExecutor()).isUsed(msg)) {

                    MsgSender.sErr(guide.getGuideSyn(), this.S, "The ID is already used.");
                    return 1;
                }
                if (!Pattern.matches("\\w+", msg)) {

                    MsgSender.sErr(guide.getGuideSyn(), guide.getCmdSender(), ChatColor.RED + msg + ChatColor.RESET + " contains an invalid character.");
                    MsgSender.sWarn(guide.getGuideSyn(), guide.getCmdSender(), "IDs should only contain a-z, A-Z and 0-9.");
                    return 1;
                }
                newLayer = this.lay.clone();
                newLayer.setId(msg);

                try {
                    newLayer.save();
                } catch (JDOMException | java.io.IOException | java.sql.SQLException e) {

                    MsgSender.sErr(guide.getGuideSyn(), this.S, "Could not update the Layer " + ChatColor.DARK_AQUA + this.lay.getId() + ChatColor.RESET + ".");
                    MsgSender.sErr(guide.getGuideSyn(), this.S, e.getMessage());
                    return -1;
                }
                toUpdate = new HashSet < > ();
                allDraws = new HashSet < > ();
                allDraws.addAll(((DynTrack) guide.getExecutor()).getPaths().values());
                allDraws.addAll(((DynTrack) guide.getExecutor()).getSquares().values());
                allDraws.addAll(((DynTrack) guide.getExecutor()).getLinks().values());
                it = allDraws.iterator();

                while (it.hasNext()) {

                    Drawable current = it.next();
                    if (current.getLayer().equals(this.lay)) {
                        toUpdate.add(current);
                    }
                }
                draws = toUpdate.toArray(new Drawable[0]);
                for (i = 0; i < draws.length; i++) {

                    draws[i].setLayer(newLayer);

                    try {
                        draws[i].save();
                    } catch (Exception e) {

                        MsgSender.sErr(guide.getGuideSyn(), this.S, "Could not save " + ChatColor.DARK_AQUA + draws[i].getId() + ChatColor.RESET + ".");
                        MsgSender.sErr(guide.getGuideSyn(), this.S, e.getMessage());
                        MsgSender.sWarn(guide.getGuideSyn(), this.S, "The process will continue anyway.");
                        MsgSender.sWarn(guide.getGuideSyn(), this.S, "The result could be, that the old version of the Layer might be not deleted.");
                    }
                }
                if (this.lay.delete(true) == DataActionResult.DELETED_FAILED) {
                    MsgSender.sErr(guide.getGuideSyn(), this.S, "The old version of the Layer could not be deleted.");
                }
                this.lay = newLayer;
                MsgSender.sInfo(guide.getGuideSyn(), this.S, "ID: " + ChatColor.DARK_AQUA + this.lay.getId());
                MsgSender.sEmpty(guide.getGuideSyn(), this.S, ChatColor.GREEN);
                MsgSender.sInfo(guide.getGuideSyn(), this.S, "Saved " + ChatColor.DARK_AQUA + this.lay.getId() + ChatColor.RESET + ".");
                return -2;
            case 2:
                if (this.lay.getId().equalsIgnoreCase(msg)) {

                    this.lay.setName("");
                } else {

                    this.lay.setName(msg);
                }
                MsgSender.sInfo(guide.getGuideSyn(), this.S, "Name: " + ChatColor.AQUA + this.lay.getName());
                finish(guide.getCmdSender());
                return -2;
            case 3:
                answer = MethodPool.getAnswer(msg);
                if (answer == TrinaryAnswer.POSITIVE) {

                    this.lay.setHideName(false);
                    MsgSender.sInfo(guide.getGuideSyn(), this.S, "The Name will now be shown as the Headline.");
                    finish(guide.getCmdSender());
                    return -2;
                }
                if (answer == TrinaryAnswer.NEGATIVE) {

                    this.lay.setHideName(true);
                    MsgSender.sInfo(guide.getGuideSyn(), this.S, "The Name will now not be shown as the Headline.");
                    finish(guide.getCmdSender());
                    return -2;
                }

                MsgSender.sErr(guide.getGuideSyn(), this.S, "You have to type yes or no!");
                MsgSender.sWarn(guide.getGuideSyn(), this.S, "You typed: " + ChatColor.RED + msg);
                return 3;

            case 4:
                answer = MethodPool.getAnswer(msg);
                if (answer == TrinaryAnswer.POSITIVE) {

                    this.lay.setHide(false);
                    MsgSender.sInfo(guide.getGuideSyn(), this.S, "The Layer is now visible by default.");
                    finish(guide.getCmdSender());
                    return -2;
                }
                if (answer == TrinaryAnswer.NEGATIVE) {

                    this.lay.setHide(true);
                    MsgSender.sInfo(guide.getGuideSyn(), this.S, "The Layer is now hidden by default.");
                    finish(guide.getCmdSender());
                    return -2;
                }

                MsgSender.sErr(guide.getGuideSyn(), this.S, "You have to type yes or no!");
                MsgSender.sWarn(guide.getGuideSyn(), this.S, "You typed: " + ChatColor.RED + msg);
                return 4;

            case 5:
                try {
                    byte minzoom = Byte.parseByte(msg);
                    if (minzoom > 8 || minzoom < 0) {

                        MsgSender.sErr(guide.getGuideSyn(), this.S, "The number you typed in was too small / large.");
                        return 5;
                    }

                    this.lay.setMinzoom(minzoom);

                } catch (NumberFormatException e) {

                    MsgSender.sErr(guide.getGuideSyn(), this.S, ChatColor.RED + msg + ChatColor.RESET + " is not a valid number.");
                    return 5;
                }
                MsgSender.sInfo(guide.getGuideSyn(), this.S, "Minzoom: " + ChatColor.LIGHT_PURPLE + this.lay.getMinzoom());
                finish(guide.getCmdSender());
                return -2;

            case 6:
                try {
                    int priority = Integer.parseInt(msg);
                    this.lay.setPriority(priority);
                    MsgSender.sInfo(guide.getGuideSyn(), this.S, "Priority: " + ChatColor.LIGHT_PURPLE + this.lay.getPriority());
                    finish(guide.getCmdSender());
                    return -2;
                } catch (NumberFormatException e) {

                    MsgSender.sErr(guide.getGuideSyn(), this.S, ChatColor.RED + msg + ChatColor.RESET + " is not a valid number.");
                    return 6;
                }
        }
        return -3;
    }

    public void finish(CommandSender s) {
        try {
            this.lay.save();
            MsgSender.sInfo("Editor", this.S, "Saved " + ChatColor.DARK_AQUA + this.lay.getId() + ChatColor.RESET + ".");
        } catch (JDOMException | java.io.IOException | java.sql.SQLException e) {

            MsgSender.sErr("Editor", this.S, "Could not save the Changes.");
            MsgSender.sErr("Editor", this.S, e.getMessage());
        }
    }

    public boolean usesId(String arg0) {
        return false;
    }
}