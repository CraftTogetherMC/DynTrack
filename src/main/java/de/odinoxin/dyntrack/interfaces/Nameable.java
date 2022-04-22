package de.odinoxin.dyntrack.interfaces;

public interface Nameable extends Identifiable {
  String getName();
  
  void setName(String paramString);
  
  boolean isNameHidden();
  
  void setHideName(boolean paramBoolean);
}


