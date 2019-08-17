
import processing.core.*;
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 
import processing.awt.*; 

public interface PObject {

  // Void methods
  public void setFocus(boolean f);
  public void setHover(boolean h);

  // Return methods
  public int getCursor();
  public int getWidth();
  public int getHeight();
  public boolean mouseOver();
  public boolean isFocused();
  public boolean isHovering();
  public PGraphics getGraphics();
  public PController getController();

  public int getColor(int r, int g, int b, int a);
  public int getColor(int r, int g, int b);
  public int getColor(int g, int a);
  public int getColor(int g);

  public void setTabObject(PObject o);
  public void setPreviousTabObject(PObject o);



  // Update
  public void update();

  // Visuals
  public void redraw();
  public void display(int x, int y);

  // Events
  public void mouseEvent(MouseEvent e);

  public void keyEvent(KeyEvent e);
}
