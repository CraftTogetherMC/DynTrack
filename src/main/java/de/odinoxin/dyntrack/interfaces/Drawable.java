package de.odinoxin.dyntrack.interfaces;

import de.odinoxin.dyntrack.layer.Layer;
import de.odinoxin.dyntrack.style.Style;

public interface Drawable extends Nameable {
  Style getStyle();
  
  Layer getLayer();
  
  void setStyle(Style paramStyle);
  
  void setLayer(Layer paramLayer);
}


