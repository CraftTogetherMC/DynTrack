package de.odinoxin.dyntrack;

import de.odinoxin.dyntrack.generals.MsgSender;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Intro
        extends BukkitRunnable
        implements Listener {
  private final Plugin DYNTRACK;
  private final CommandSender S;
  private byte part = 0;
  private byte index = 0;
  private static final String[] welcome = new String[] {
          ChatColor.DARK_GRAY + "=== " + ChatColor.GOLD + "Intro" + ChatColor.RESET + ": Welcome " + ChatColor.DARK_GRAY + "===", "Hi,", "this is OdinOxin" + ChatColor.GRAY + ", the Author of DynTrack.", "I want to introduce you in DynTrack,", "so you know how to use it!", "You can run the Intro with " + ChatColor.DARK_GREEN + "/dynTrack intro 1" + ChatColor.RESET + ".", ChatColor.DARK_GRAY + "=== " + ChatColor.GOLD + "Intro" + ChatColor.RESET + "-End " + ChatColor.DARK_GRAY + "==="
  };

  private static final short[] welcomeWait = new short[] {
          10,
          10,
          60,
          50,
          60,
          40,
          0
  };

  private static final String[] overview = new String[] {
          ChatColor.DARK_GRAY + "=== " + ChatColor.GOLD + "Intro" + ChatColor.RESET + ": Overview " + ChatColor.DARK_GRAY + "===", "This Intro is seperated into several parts.", "You can start each part with " + ChatColor.DARK_GREEN + "/dynTrack intro #", ChatColor.DARK_GREEN + " 0" + ChatColor.RESET + ": Welcome", ChatColor.DARK_GREEN + " 1" + ChatColor.RESET + ": Overview " + ChatColor.DARK_GRAY + "(" + ChatColor.GRAY + "This" + ChatColor.DARK_GRAY + ")", ChatColor.DARK_GREEN + " 2" + ChatColor.RESET + ": Shortcuts", ChatColor.DARK_GREEN + " 3" + ChatColor.RESET + ": Setups", ChatColor.DARK_GREEN + " 4" + ChatColor.RESET + ": Editors", ChatColor.DARK_GREEN + " 5" + ChatColor.RESET + ": Styles", ChatColor.DARK_GREEN + " 6" + ChatColor.RESET + ": Layers", ChatColor.DARK_GREEN + " 7" + ChatColor.RESET + ": Paths", ChatColor.DARK_GREEN + " 8" + ChatColor.RESET + ": Squares", ChatColor.DARK_GREEN + " 9" + ChatColor.RESET + ": Links", ChatColor.DARK_GREEN + "10" + ChatColor.RESET + ": Let's Try! - Style", ChatColor.DARK_GREEN + "11" + ChatColor.RESET + ": Let's Try! - Layer", ChatColor.DARK_GREEN + "12" + ChatColor.RESET + ": Let's Try! - Path", ChatColor.DARK_GREEN + "13" + ChatColor.RESET + ": More help", ChatColor.DARK_GREEN + "14" + ChatColor.RESET + ": Thank you!", "Just take a moment, to inform yourself.", ChatColor.DARK_GRAY + "=== " + ChatColor.GOLD + "Intro" + ChatColor.RESET + "-End " + ChatColor.DARK_GRAY + "==="
  };

  private static final short[] overviewWait = new short[] {
          10,
          60,
          60,
          20,
          20,
          20,
          20,
          20,
          20,
          20,
          20,
          20,
          20,
          20,
          20,
          20,
          20,
          20,
          60,
          0
  };

  private static final String[] shortcuts = new String[] {
          ChatColor.DARK_GRAY + "=== " + ChatColor.GOLD + "Intro" + ChatColor.RESET + ": Sortcuts " + ChatColor.DARK_GRAY + "===", "Annoyed by typing words like \"Square\"?", "You " + ChatColor.UNDERLINE + "can" + ChatColor.RESET + " easily use shortcuts anywhere:", "DynTrack " + ChatColor.YELLOW + "->" + ChatColor.DARK_GREEN + " dyntr", "Style " + ChatColor.YELLOW + "->" + ChatColor.DARK_GREEN + " st", "Layer " + ChatColor.YELLOW + "->" + ChatColor.DARK_GREEN + " lay", "Path " + ChatColor.YELLOW + "->" + ChatColor.DARK_GREEN + " p", "Square " + ChatColor.YELLOW + "->" + ChatColor.DARK_GREEN + " sq", "Link " + ChatColor.YELLOW + "->" + ChatColor.DARK_GREEN + " link", ChatColor.DARK_GRAY + "=== " + ChatColor.GOLD + "Intro" + ChatColor.RESET + "-End " + ChatColor.DARK_GRAY + "==="
  };

  private static final short[] shortcutsWait = new short[] {
          10,
          60,
          60,
          20,
          20,
          20,
          20,
          20,
          20,
          0
  };

  private static final String[] setups = new String[] {
          ChatColor.DARK_GRAY + "=== " + ChatColor.GOLD + "Intro" + ChatColor.RESET + ": Setups " + ChatColor.DARK_GRAY + "===", "When you want to create someting (e.g. a Path),", "a Setup will guide you,", "so you cannot miss any option.", "The are several keywords,", "which you should keep in mind, while using a Setup:", ChatColor.DARK_GREEN + "exit" + ChatColor.DARK_GRAY + " | " + ChatColor.DARK_GREEN + "stop" + ChatColor.YELLOW + " -> " + ChatColor.RESET + "Leaves the Setup.", ChatColor.DARK_GREEN + "back" + ChatColor.YELLOW + " -> " + ChatColor.RESET + "Switches back, to the last option.", ChatColor.DARK_GREEN + "next" + ChatColor.DARK_GRAY + " | " + ChatColor.DARK_GREEN + "skip" + ChatColor.YELLOW + " -> " + ChatColor.RESET + "Skippes the option.", "While using a Setup, you do not need any commands:", "You just type in the words.", "Also, your are not able to chat.", "The Setup can be run with " + ChatColor.DARK_GREEN + "/dynTrack create" + ChatColor.RESET + ".", ChatColor.DARK_GRAY + "=== " + ChatColor.GOLD + "Intro" + ChatColor.RESET + "-End " + ChatColor.DARK_GRAY + "==="
  };

  private static final short[] setupsWait = new short[] {
          10,
          60,
          50,
          60,
          50,
          60,
          60,
          60,
          60,
          50,
          60,
          50,
          60,
          0
  };

  private static final String[] editors = new String[] {
          ChatColor.DARK_GRAY + "=== " + ChatColor.GOLD + "Intro" + ChatColor.RESET + ": Editors " + ChatColor.DARK_GRAY + "===", "With the Editor you can edit already existing", "Styles, Layers, Paths, Squares and Links.", "Editors working in the same way, as Setups do.", "The Editor can be run with " + ChatColor.DARK_GREEN + "/dynTrack edit" + ChatColor.RESET + ".", ChatColor.DARK_GRAY + "=== " + ChatColor.GOLD + "Intro" + ChatColor.RESET + "-End " + ChatColor.DARK_GRAY + "==="
  };

  private static final short[] editorsWait = new short[] {
          10,
          60,
          60,
          60,
          60,
          0
  };

  private static final String[] styles = new String[] {
          ChatColor.DARK_GRAY + "=== " + ChatColor.GOLD + "Intro" + ChatColor.RESET + ": Styles " + ChatColor.DARK_GRAY + "===", "Before you track something (e.g. a Path),", "you should think about, how it will represented.", ChatColor.GRAY + "Examples:", ChatColor.GRAY + "Paths are " + ChatColor.UNDERLINE + "colored" + ChatColor.GRAY + " lines.", ChatColor.GRAY + "Squares are " + ChatColor.UNDERLINE + "colored" + ChatColor.GRAY + " areas.", "To color an Element, you need a Style,", "which saves the color, the opacity and so on.", "Then you tell your Element to use this Style.", "How to tell an Element that?", "Easily in the corresponding Setup or in the Editor.", "Styles can be created with " + ChatColor.DARK_GREEN + "/dynTrack create" + ChatColor.RESET + ".", ChatColor.DARK_GRAY + "=== " + ChatColor.GOLD + "Intro" + ChatColor.RESET + "-End " + ChatColor.DARK_GRAY + "==="
  };

  private static final short[] stylesWait = new short[] {
          10,
          50,
          60,
          30,
          60,
          60,
          50,
          60,
          60,
          50,
          60,
          60,
          0
  };

  private static final String[] layers = new String[] {
          ChatColor.DARK_GRAY + "=== " + ChatColor.GOLD + "Intro" + ChatColor.RESET + ": Layers " + ChatColor.DARK_GRAY + "===", "The dynmap provides Layers.", "These Layers can be enabled or disabled", "independently from each other", "in the upper left of the map.", "", "The Layers can include Elements", "(Paths, Squares and Links).", "Each Element has to know,", "in which Layer it should be drawn.", "This can be set in the corresponding Setup.", "Layers can be created with " + ChatColor.DARK_GREEN + "/dynTrack create" + ChatColor.RESET + ".", ChatColor.DARK_GRAY + "=== " + ChatColor.GOLD + "Intro" + ChatColor.RESET + "-End " + ChatColor.DARK_GRAY + "==="
  };

  private static final short[] layersWait = new short[] {
          10,
          50,
          60,
          50,
          50,
          10,
          50,
          60,
          50,
          60,
          60,
          60,
          0
  };

  private static final String[] paths = new String[] {
          ChatColor.DARK_GRAY + "=== " + ChatColor.GOLD + "Intro" + ChatColor.RESET + ": Paths " + ChatColor.DARK_GRAY + "===", "Paths are the colored lines on the map.", "There are several infos about a Path,", "which are displayed in the infobox.", "The infobox will be displayed,", "by clicking the Path on the map.", "Paths can be created with " + ChatColor.DARK_GREEN + "/dynTrack create" + ChatColor.RESET + ".", ChatColor.DARK_GRAY + "=== " + ChatColor.GOLD + "Intro" + ChatColor.RESET + "-End " + ChatColor.DARK_GRAY + "==="
  };

  private static final short[] pathsWait = new short[] {
          10,
          60,
          50,
          60,
          50,
          60,
          60,
          0
  };

  private static final String[] squares = new String[] {
          ChatColor.DARK_GRAY + "=== " + ChatColor.GOLD + "Intro" + ChatColor.RESET + ": Squares " + ChatColor.DARK_GRAY + "===", "Squares working in the same way, as Paths do.", "The different is,", "that the area inside the Track is colored, too.", "Squares can be created with " + ChatColor.DARK_GREEN + "/dynTrack create" + ChatColor.RESET + ".", ChatColor.DARK_GRAY + "=== " + ChatColor.GOLD + "Intro" + ChatColor.RESET + "-End " + ChatColor.DARK_GRAY + "==="
  };

  private static final short[] squaresWait = new short[] {
          10,
          60,
          40,
          60,
          60,
          0
  };

  private static final String[] links = new String[] {
          ChatColor.DARK_GRAY + "=== " + ChatColor.GOLD + "Intro" + ChatColor.RESET + ": Links " + ChatColor.DARK_GRAY + "===", "A Link has a location.", "You can connect a Trackpoint of", "a Path or a Square with the Link.", "So you can easily depict,", "where Paths and Squares are connected", "and where a Path just routes above a tunnel.", "Links can be created with " + ChatColor.DARK_GREEN + "/dynTrack create" + ChatColor.RESET + ".", ChatColor.DARK_GRAY + "=== " + ChatColor.GOLD + "Intro" + ChatColor.RESET + "-End " + ChatColor.DARK_GRAY + "==="
  };

  private static final short[] linksWait = new short[] {
          10,
          60,
          50,
          60,
          50,
          60,
          60,
          60,
          0
  };

  private static final String[] letsTry_Style = new String[] {
          ChatColor.DARK_GRAY + "=== " + ChatColor.GOLD + "Intro" + ChatColor.RESET + ": Let's try! - Style" + ChatColor.DARK_GRAY + "===", "Now, once you know the basics,", "We can try to create your first Style.", "Start the Setup with " + ChatColor.DARK_GREEN + "/dynTrack create" + ChatColor.RESET + ",", "and select " + ChatColor.DARK_GREEN + "Style" + ChatColor.RESET + ".", "Then just follow the introductions of the Setup.", "When you finished the Setup,", "run the next part of the Intro with", ChatColor.DARK_GREEN + "/dynTrack intro 11" + ChatColor.RESET + ".", ChatColor.DARK_GRAY + "=== " + ChatColor.GOLD + "Intro" + ChatColor.RESET + "-End " + ChatColor.DARK_GRAY + "==="
  };

  private static final short[] letsTry_StyleWait = new short[] {
          10,
          50,
          60,
          50,
          60,
          60,
          50,
          50,
          60,
          0
  };

  private static final String[] letsTry_Layer = new String[] {
          ChatColor.DARK_GRAY + "=== " + ChatColor.GOLD + "Intro" + ChatColor.RESET + ": Let's try! - Layer" + ChatColor.DARK_GRAY + "===", "Now we need to create a Layer.", "Start the Setup with " + ChatColor.DARK_GREEN + "/dynTrack create" + ChatColor.RESET + ",", "and select " + ChatColor.DARK_GREEN + "Layer" + ChatColor.RESET + ".", "Then just follow the introductions of the Setup.", "When you finished the Setup,", "run the next part of the Intro with", ChatColor.DARK_GREEN + "/dynTrack intro 12" + ChatColor.RESET + ".", ChatColor.DARK_GRAY + "=== " + ChatColor.GOLD + "Intro" + ChatColor.RESET + "-End " + ChatColor.DARK_GRAY + "==="
  };

  private static final short[] letsTry_LayerWait = new short[] {
          10,
          60,
          50,
          60,
          60,
          50,
          50,
          60,
          0
  };

  private static final String[] letsTry_Path = new String[] {
          ChatColor.DARK_GRAY + "=== " + ChatColor.GOLD + "Intro" + ChatColor.RESET + ": Let's try! - Path" + ChatColor.DARK_GRAY + "===", "Now you got an image of how the Setups work.", "I am sure, you are able", "to create a Path by your own now.", "If not, type " + ChatColor.DARK_GREEN + "/dynTrack intro 13" + ChatColor.RESET + ".", ChatColor.DARK_GRAY + "=== " + ChatColor.GOLD + "Intro" + ChatColor.RESET + "-End " + ChatColor.DARK_GRAY + "==="
  };

  private static final short[] letsTry_PathWait = new short[] {
          10,
          60,
          50,
          60,
          60,
          0
  };

  private static final String[] moreHelp = new String[] {
          ChatColor.DARK_GRAY + "=== " + ChatColor.GOLD + "Intro" + ChatColor.RESET + ": More help " + ChatColor.DARK_GRAY + "===", "If this Intro does not help you,", "then do not despair!", "Feel free to contact me (OdinOxin).", "Via BukkitDev or E-Mail:", "OdinOxin@googlemail.com", "I will be glad to help you!", ChatColor.DARK_GRAY + "=== " + ChatColor.GOLD + "Intro" + ChatColor.RESET + "-End " + ChatColor.DARK_GRAY + "==="
  };

  private static final short[] moreHelpWait = new short[] {
          10,
          50,
          50,
          60,
          50,
          60,
          60,
          0
  };

  private static final String[] thanks = new String[] {
          ChatColor.DARK_GRAY + "=== " + ChatColor.GOLD + "Intro" + ChatColor.RESET + ": Thank you! " + ChatColor.DARK_GRAY + "===", "Please visit:", ChatColor.BLUE + "http://dev.bukkit.org/bukkit-plugins/dyntrack/", "and join the list of servers.", ChatColor.GOLD + "Thank you for using DynTrack!", ChatColor.DARK_GRAY + "=== " + ChatColor.GOLD + "Intro" + ChatColor.RESET + "-End " + ChatColor.DARK_GRAY + "==="
  };

  private static final short[] thanksWait = new short[] {
          10,
          30,
          60,
          50,
          60,
          0
  };

  private static final String[][] all = new String[][] {
          welcome,
          overview,
          shortcuts,
          setups,
          editors,
          styles,
          layers,
          paths,
          squares,
          links,
          letsTry_Style,
          letsTry_Layer,
          letsTry_Path,
          moreHelp,
          thanks
  };
  private static final short[][] allWait = new short[][] {
          welcomeWait,
          overviewWait,
          shortcutsWait,
          setupsWait,
          editorsWait,
          stylesWait,
          layersWait,
          pathsWait,
          squaresWait,
          linksWait,
          letsTry_StyleWait,
          letsTry_LayerWait,
          letsTry_PathWait,
          moreHelpWait,
          thanksWait
  };

  public Intro(Plugin DYNTRACK, CommandSender S, byte part) {
    this.DYNTRACK = DYNTRACK;
    this.S = S;
    if (part < 0 || part >= all.length) {

      part = 0;
    }
    this.part = part;
  }

  private Intro(Intro g) {
    this.DYNTRACK = g.DYNTRACK;
    this.S = g.S;
    this.part = g.part;
    this.index = g.index;
    this.index = (byte)(this.index + 1);
  }

  public void run() {
    if (this.index >= (all[this.part]).length || this.S == null) {

      cancel();
      return;
    }
    MsgSender.sInfo("DynTrack", this.S, all[this.part][this.index]);
    (new Intro(this)).runTaskLaterAsynchronously(this.DYNTRACK, allWait[this.part][this.index]);
  }
}