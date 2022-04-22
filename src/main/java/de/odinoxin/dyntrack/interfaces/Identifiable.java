package de.odinoxin.dyntrack.interfaces;

import de.odinoxin.dyntrack.enums.DataActionResult;

public interface Identifiable {
  String getId();

  void setId(String paramString);

  void save() throws Exception;

  void saveInFile() throws Exception;

  void saveInDatabase() throws Exception;

  DataActionResult delete(boolean paramBoolean);
}