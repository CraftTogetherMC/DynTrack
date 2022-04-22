package de.odinoxin.dyntrack;

import de.odinoxin.dyntrack.generals.MsgSender;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBHandler {
  private final String PREFIX;
  private Connection con;
  private Statement sm;

  public DBHandler(String URL, String user, String password, String prefix) throws SQLException {
    MsgSender.cInfo("DynTrack", "Connecting to the database...");

    try {
      this.con = DriverManager.getConnection(URL, user, password);
      this.sm = this.con.createStatement();
      MsgSender.cInfo("DynTrack", "Connected to the database.");
    } catch (Exception ex) {

      MsgSender.cBug("DynTrack", "Cannot connect to the Database!");
    }
    this.PREFIX = prefix;
    MsgSender.cInfo("DynTrack", "Preparing the database...");

    try {
      createStyleTable();
      createLayerTable();
      createPathTable();
      createPathPointsTable();
      createSquareTable();
      createSquarePointsTable();
      createLinkTable();
      createLinkedPathsTable();
      createLinkedSquaresTable();
      MsgSender.cInfo("DynTrack", "Database prepared.");
    } catch (SQLException ex) {

      MsgSender.cBug("DynTrack", "Preparing the database failed!");
      throw ex;
    }
  }

  private void createStyleTable() throws SQLException {
    this.sm.executeUpdate("CREATE TABLE IF NOT EXISTS " + this.PREFIX + "styles(" + "id VARCHAR(50) COLLATE latin1_general_ci NOT NULL," + "linecolor INTEGER NOT NULL DEFAULT 0," + "lineopacity DOUBLE NOT NULL DEFAULT 1," + "linewidth INTEGER NOT NULL DEFAULT 1," + "fillcolor INTEGER NOT NULL DEFAULT 0," + "fillopacity DOUBLE NOT NULL DEFAULT 1," + "PRIMARY KEY(id)" + ") ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;");
  }

  private void createLayerTable() throws SQLException {
    this.sm.executeUpdate("CREATE TABLE IF NOT EXISTS " + this.PREFIX + "layers(" + "id VARCHAR(50) COLLATE latin1_general_ci NOT NULL," + "name VARCHAR(50) COLLATE latin1_general_ci," + "minzoom INTEGER UNSIGNED NOT NULL DEFAULT 0," + "hide TINYINT(1) UNSIGNED NOT NULL DEFAULT FALSE," + "priority INTEGER UNSIGNED NOT NULL DEFAULT 0," + "hidename TINYINT(1) UNSIGNED NOT NULL DEFAULT FALSE," + "PRIMARY KEY(id)" + ") ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;");
  }

  private void createPathTable() throws SQLException {
    this.sm.executeUpdate("CREATE TABLE IF NOT EXISTS " + this.PREFIX + "paths(" + "id VARCHAR(50) COLLATE latin1_general_ci NOT NULL," + "name VARCHAR(50) COLLATE latin1_general_ci," + "pathsyn VARCHAR(50) COLLATE latin1_general_ci DEFAULT \"Path\"," + "hidename TINYINT(1) UNSIGNED NOT NULL DEFAULT FALSE," + "fromsyn VARCHAR(50) COLLATE latin1_general_ci NOT NULL DEFAULT \"From\"," + "fromval VARCHAR(50) COLLATE latin1_general_ci," + "hidefrom TINYINT(1) UNSIGNED NOT NULL DEFAULT FALSE," + "tosyn VARCHAR(50) COLLATE latin1_general_ci NOT NULL DEFAULT \"To\"," + "toval VARCHAR(50) COLLATE latin1_general_ci," + "hideto TINYINT(1) UNSIGNED NOT NULL DEFAULT FALSE," + "viasyn VARCHAR(50) COLLATE latin1_general_ci," + "hidevia TINYINT(1) UNSIGNED NOT NULL DEFAULT FALSE," + "connected TINYINT(1) UNSIGNED NOT NULL DEFAULT FALSE," + "world VARCHAR(50) COLLATE latin1_general_ci NOT NULL," + "style VARCHAR(50) COLLATE latin1_general_ci NOT NULL," + "layer VARCHAR(50) COLLATE latin1_general_ci NOT NULL," + "PRIMARY KEY(id)," + "FOREIGN KEY(style) REFERENCES " + this.PREFIX + "styles(id) ON DELETE NO ACTION ON UPDATE CASCADE," + "FOREIGN KEY(layer) REFERENCES " + this.PREFIX + "layers(id) ON DELETE NO ACTION ON UPDATE CASCADE" + ") ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;");
  }

  private void createPathPointsTable() throws SQLException {
    this.sm.executeUpdate("CREATE TABLE IF NOT EXISTS " + this.PREFIX + "path_points(" + "path VARCHAR(50) COLLATE latin1_general_ci NOT NULL," + "i INTEGER UNSIGNED NOT NULL," + "x DOUBLE NOT NULL," + "y DOUBLE NOT NULL," + "z DOUBLE NOT NULL," + "PRIMARY KEY(path, i)," + "FOREIGN KEY(path) REFERENCES " + this.PREFIX + "paths(id) ON DELETE CASCADE ON UPDATE CASCADE" + ") ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;");
  }

  private void createSquareTable() throws SQLException {
    this.sm.executeUpdate("CREATE TABLE IF NOT EXISTS " + this.PREFIX + "squares(" + "id VARCHAR(50) COLLATE latin1_general_ci NOT NULL," + "name VARCHAR(50) COLLATE latin1_general_ci," + "squaresyn VARCHAR(50) COLLATE latin1_general_ci DEFAULT \"Square\"," + "hidename TINYINT(1) UNSIGNED NOT NULL DEFAULT FALSE," + "style VARCHAR(50) COLLATE latin1_general_ci NOT NULL," + "layer VARCHAR(50) COLLATE latin1_general_ci NOT NULL," + "world VARCHAR(50) COLLATE latin1_general_ci NOT NULL," + "PRIMARY KEY(id)," + "FOREIGN KEY(style) REFERENCES " + this.PREFIX + "styles(id) ON DELETE NO ACTION ON UPDATE CASCADE," + "FOREIGN KEY(layer) REFERENCES " + this.PREFIX + "layers(id) ON DELETE NO ACTION ON UPDATE CASCADE" + ") ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;");
  }

  private void createSquarePointsTable() throws SQLException {
    this.sm.executeUpdate("CREATE TABLE IF NOT EXISTS " + this.PREFIX + "square_points(" + "square VARCHAR(50) COLLATE latin1_general_ci NOT NULL," + "i INTEGER UNSIGNED NOT NULL," + "x DOUBLE NOT NULL," + "y DOUBLE NOT NULL," + "z DOUBLE NOT NULL," + "PRIMARY KEY(square, i)," + "FOREIGN KEY(square) REFERENCES " + this.PREFIX + "squares(id) ON DELETE CASCADE ON UPDATE CASCADE" + ") ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;");
  }

  private void createLinkTable() throws SQLException {
    this.sm.executeUpdate("CREATE TABLE IF NOT EXISTS " + this.PREFIX + "links(" + "id VARCHAR(50) COLLATE latin1_general_ci NOT NULL," + "name VARCHAR(50) COLLATE latin1_general_ci," + "linksyn VARCHAR(50) COLLATE latin1_general_ci NOT NULL DEFAULT \"Link\"," + "hidename TINYINT(1) UNSIGNED NOT NULL DEFAULT FALSE," + "hide TINYINT(1) UNSIGNED NOT NULL DEFAULT FALSE," + "edging TINYINT(1) UNSIGNED NOT NULL DEFAULT FALSE," + "x DOUBLE NOT NULL," + "y DOUBLE NOT NULL," + "z DOUBLE NOT NULL," + "world VARCHAR(50) COLLATE latin1_general_ci NOT NULL," + "style VARCHAR(50) COLLATE latin1_general_ci NOT NULL," + "layer VARCHAR(50) COLLATE latin1_general_ci NOT NULL," + "PRIMARY KEY(id)," + "FOREIGN KEY(style) REFERENCES " + this.PREFIX + "styles(id) ON DELETE NO ACTION ON UPDATE CASCADE," + "FOREIGN KEY(layer) REFERENCES " + this.PREFIX + "layers(id) ON DELETE NO ACTION ON UPDATE CASCADE" + ") ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;");
  }

  private void createLinkedPathsTable() throws SQLException {
    this.sm.executeUpdate("CREATE TABLE IF NOT EXISTS " + this.PREFIX + "linked_paths(" + "link VARCHAR(50) COLLATE latin1_general_ci NOT NULL," + "linked VARCHAR(50) COLLATE latin1_general_ci NOT NULL," + "i INTEGER UNSIGNED NOT NULL," + "PRIMARY KEY(link, linked)," + "FOREIGN KEY(link) REFERENCES " + this.PREFIX + "links(id) ON DELETE NO ACTION ON UPDATE CASCADE," + "FOREIGN KEY(linked) REFERENCES " + this.PREFIX + "paths(id) ON DELETE NO ACTION ON UPDATE CASCADE" + ") ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;");
  }

  private void createLinkedSquaresTable() throws SQLException {
    this.sm.executeUpdate("CREATE TABLE IF NOT EXISTS " + this.PREFIX + "linked_squares(" + "link VARCHAR(50) COLLATE latin1_general_ci NOT NULL," + "linked VARCHAR(50) COLLATE latin1_general_ci NOT NULL," + "i INTEGER UNSIGNED NOT NULL," + "PRIMARY KEY(link, linked)," + "FOREIGN KEY(link)REFERENCES " + this.PREFIX + "links(id) ON DELETE NO ACTION ON UPDATE CASCADE," + "FOREIGN KEY(linked)REFERENCES " + this.PREFIX + "squares(id) ON DELETE NO ACTION ON UPDATE CASCADE" + ") ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;");
  }

  public void finalize() throws Throwable {
    this.con.close();
    MsgSender.cInfo("DynTrack", "Disconnected from the Database!");
    super.finalize();
  }

  public ResultSet exeQuery(String sql) throws SQLException {
    return this.con.createStatement().executeQuery(sql);
  }

  public int exe(String sql) throws SQLException {
    return this.con.createStatement().executeUpdate(sql);
  }

  public Connection getConnection() {
    return con;
  }

  public String getPrefix() {
    return this.PREFIX;
  }
}