package de.odinoxin.dyntrack.generals;

import de.gcmclan.team.guides.MsgSender;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class PermHandler {
  private final Plugin PLUGIN;
  private static boolean useVault;
  public static Permission permission = null;

  public PermHandler(Plugin PLUGIN) {
    this.PLUGIN = PLUGIN;
    if (this.PLUGIN.getServer().getPluginManager().isPluginEnabled("Vault")) {

      RegisteredServiceProvider < Permission > permissionProvider = this.PLUGIN.getServer().getServicesManager().getRegistration(Permission.class);
      if (permissionProvider != null) {

        permission = permissionProvider.getProvider();
        if (permission != null) {
          useVault = true;
          MsgSender.cInfo(PLUGIN, "Vault is now used as the permission interface.");
        } else {
          useVault = false;
        }

      } else {

        useVault = false;
      }

    } else {

      useVault = false;
    }
    if (!useVault) {
      MsgSender.cWarn(this.PLUGIN, "Vault was not found. Only Operators have access to " + PLUGIN.getName() + ".");
    }
  }

  public static boolean hasPerms(Player p, String perm) {
    if (useVault) {
      return permission.has(p, perm);
    }

    return p.isOp();
  }

  public static boolean usingVault() {
    return useVault;
  }
}