package de.odinoxin.dyntrack;

import de.odinoxin.dyntrack.enums.DataActionResult;
import de.odinoxin.dyntrack.generals.MsgSender;
import de.odinoxin.dyntrack.interfaces.Identifiable;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Config implements Identifiable {
  private final File f;
  private YamlConfiguration yml;
  private boolean useDB = false;
  private final String PREFIX;

  public Config(DynTrack DYNTRACK) {
    this.f = new File(DYNTRACK.getDataFolder() + "/config.yml");
    if (!this.f.exists()) {

      MsgSender.cInfo(DYNTRACK, "Creating config...");
      this.yml = new YamlConfiguration();
      this.yml.options().header("This is the configuration file for DynTrack.");

      try {
        save();
        MsgSender.cInfo(DYNTRACK, "Config created.");
      } catch (IOException e) {

        DYNTRACK.getServer().getPluginManager().disablePlugin(DYNTRACK);
        this.PREFIX = "";
        return;
      }
    }
    MsgSender.cInfo(DYNTRACK, "Loading config...");
    this.yml = YamlConfiguration.loadConfiguration(this.f);
    this.useDB = this.yml.getBoolean("db.use");
    this.PREFIX = this.yml.getString("db.prefix");
    if (this.useDB) {

      try {

        DYNTRACK.setDBHandler(new DBHandler(this.yml.getString("db.url"), this.yml.getString("db.user"), this.yml.getString("db.password"), this.PREFIX));
      } catch (SQLException e) {

        this.useDB = false;
        e.printStackTrace();
        DYNTRACK.getServer().getPluginManager().disablePlugin(DYNTRACK);
        return;
      }
    }
    MsgSender.cInfo(DYNTRACK, "Config loaded.");
  }

  public void save() throws IOException {
    this.yml.set("db.use", Boolean.valueOf(this.useDB));
    this.yml.set("db.url", "url");
    this.yml.set("db.user", "user");
    this.yml.set("db.password", "password");
    this.yml.set("db.prefix", "dyntrack_");
    this.yml.save(this.f);
  }

  public boolean useDB() {
    return this.useDB;
  }

  public String getDB_Prefix() {
    return this.PREFIX;
  }

  public String getId() {
    return "config";
  }

  public void setId(String id) {}

  public void saveInFile() throws IOException {
    save();
  }

  public void saveInDatabase() throws IOException {
    throw new IOException("The config can only be saved as file.");
  }

  public DataActionResult delete(boolean ignorDependencies) {
    return DataActionResult.STILL_USED;
  }
}