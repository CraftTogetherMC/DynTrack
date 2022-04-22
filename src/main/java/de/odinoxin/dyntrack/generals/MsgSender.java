package de.odinoxin.dyntrack.generals;

import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public abstract class MsgSender {
  private static final Logger log = Logger.getLogger("MsgSender");

  public static String colorMsg(String uncolored) {
    String colored = "";
    boolean wasLast = false;
    for (int i = 0; i < uncolored.length(); i++) {

      if (uncolored.charAt(i) == '&') {

        wasLast = true;
      } else {

        if (wasLast) {

          switch (uncolored.charAt(i)) {
            case '0':
              colored = colored + ChatColor.BLACK;
              break;
            case '1':
              colored = colored + ChatColor.DARK_BLUE;
              break;
            case '2':
              colored = colored + ChatColor.DARK_GREEN;
              break;
            case '3':
              colored = colored + ChatColor.DARK_AQUA;
              break;
            case '4':
              colored = colored + ChatColor.DARK_RED;
              break;
            case '5':
              colored = colored + ChatColor.DARK_PURPLE;
              break;
            case '6':
              colored = colored + ChatColor.GOLD;
              break;
            case '7':
              colored = colored + ChatColor.GRAY;
              break;
            case '8':
              colored = colored + ChatColor.DARK_GRAY;
              break;
            case '9':
              colored = colored + ChatColor.BLUE;
              break;
            case 'a':
              colored = colored + ChatColor.GREEN;
              break;
            case 'b':
              colored = colored + ChatColor.AQUA;
              break;
            case 'c':
              colored = colored + ChatColor.RED;
              break;
            case 'd':
              colored = colored + ChatColor.LIGHT_PURPLE;
              break;
            case 'e':
              colored = colored + ChatColor.YELLOW;
              break;
            case 'f':
              colored = colored + ChatColor.WHITE;
              break;
            case 'r':
              colored = colored + ChatColor.RESET;
              break;
            case 'B':
              colored = colored + ChatColor.BOLD;
              break;
            case 'i':
              colored = colored + ChatColor.ITALIC;
              break;
            case 'm':
              colored = colored + ChatColor.MAGIC;
              break;
            case 's':
              colored = colored + ChatColor.STRIKETHROUGH;
              break;
            case 'u':
              colored = colored + ChatColor.UNDERLINE;
              break;
            default:
              colored = colored + (38 + uncolored.charAt(i));
              break;
          }

        } else {
          colored = colored + uncolored.charAt(i);
        }
        wasLast = false;
      }
    }
    return colored;
  }

  public static void cEmpty(Plugin plugin) {
    log.info("[" + plugin.getName() + "]");
  }

  public static void cEmpty(String plugin) {
    log.info("[" + plugin + "]");
  }

  public static void cInfo(Plugin plugin, String msg) {
    log.info("[" + plugin.getName() + "]" + " " + msg);
  }

  public static void cInfo(String plugin, String msg) {
    log.info("[" + plugin + "]" + " " + msg);
  }

  public static void cWarn(Plugin plugin, String msg) {
    log.warning("[" + plugin.getName() + "]" + " " + msg);
  }

  public static void cWarn(String plugin, String msg) {
    log.warning("[" + plugin + "]" + " " + msg);
  }

  public static void cBug(Plugin plugin, String msg) {
    log.severe("[" + plugin.getName() + "]" + " " + msg);
  }

  public static void cBug(String plugin, String msg) {
    log.severe("[" + plugin + "]" + " " + msg);
  }

  public static void pEmpty(Plugin plugin, Player p, ChatColor color) {
    p.sendMessage(ChatColor.DARK_GRAY + "[" + color + plugin.getName() + ChatColor.DARK_GRAY + "]");
  }

  public static void pEmpty(String plugin, Player p, ChatColor color) {
    p.sendMessage(ChatColor.DARK_GRAY + "[" + color + plugin + ChatColor.DARK_GRAY + "]");
  }

  public static void pInfo(Plugin plugin, Player p, String msg) {
    p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + plugin.getName() + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + " " + msg);
  }

  public static void pInfo(String plugin, Player p, String msg) {
    p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + plugin + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + " " + msg);
  }

  public static void pWarn(Plugin plugin, Player p, String msg) {
    p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + plugin.getName() + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + " " + msg);
  }

  public static void pWarn(String plugin, Player p, String msg) {
    p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + plugin + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + " " + msg);
  }

  public static void pErr(Plugin plugin, Player p, String msg) {
    p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + plugin.getName() + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + " " + msg);
  }

  public static void pErr(String plugin, Player p, String msg) {
    p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + plugin + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + " " + msg);
  }

  public static void sEmpty(Plugin plugin, CommandSender s, ChatColor color) {
    s.sendMessage(ChatColor.DARK_GRAY + "[" + color + plugin.getName() + ChatColor.DARK_GRAY + "]");
  }

  public static void sEmpty(String plugin, CommandSender s, ChatColor color) {
    s.sendMessage(ChatColor.DARK_GRAY + "[" + color + plugin + ChatColor.DARK_GRAY + "]");
  }

  public static void sInfo(Plugin plugin, CommandSender s, String msg) {
    s.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + plugin.getName() + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + " " + msg);
  }

  public static void sInfo(String plugin, CommandSender s, String msg) {
    s.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + plugin + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + " " + msg);
  }

  public static void sWarn(Plugin plugin, CommandSender s, String msg) {
    s.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + plugin.getName() + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + " " + msg);
  }

  public static void sWarn(String plugin, CommandSender s, String msg) {
    s.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + plugin + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + " " + msg);
  }

  public static void sErr(Plugin plugin, CommandSender s, String msg) {
    s.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + plugin.getName() + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + " " + msg);
  }

  public static void sErr(String plugin, CommandSender s, String msg) {
    s.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + plugin + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + " " + msg);
  }

  public static void permMsg(Plugin plugin, Player p, String perm) {
    p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + plugin.getName() + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + " You need a permission.");
    p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + plugin.getName() + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + " -> " + perm);
  }

  public static void permMsg(String plugin, Player p, String perm) {
    p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + plugin + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + " You need a permission.");
    p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + plugin + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + " -> " + perm);
  }

  public static void permMsg(Plugin plugin, Player p, String[] perms) {
    p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + plugin.getName() + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + " You need one of these permissions:");
    for (int i = 0; i < perms.length; i++) {
      p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + plugin.getName() + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + " -> " + perms[i]);
    }
  }

  public static void permMsg(String plugin, Player p, String[] perms) {
    p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + plugin + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + " You need one of these permissions:");
    for (int i = 0; i < perms.length; i++) {
      p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + plugin + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + " -> " + perms[i]);
    }
  }
}