
import processing.core.*;
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 
import processing.awt.*; 
import processing.awt.PGraphicsJava2D;

public class PPanel extends PGraphicsJava2D implements PObject, PConstants {

  /* CONTENT:
   * > VARIABLES
   * > CONSTRUCTOR
   * > CREATE POBJECTS
   * > VOID METHODS
   * > RETURN METHODS
   * > UPDATE
   * > VISUALS
   * > EVENTS
   * * * * * * * * * * * * * * * * * * * * * * * * * * */






  /* VARIABLES :
   * All the object's variables are right here in this
   * section.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  protected int borderColor = 0;
  protected int x, y;
  /* Useful info about the panel 
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private boolean focused = false;
  /* True when this object has focus 
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private boolean hovering = false;
  /* True when the mouse is over this object.
   * Takes into account all the subcontrollers.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private PController controller;
  /* The controller that controls this PPanel 
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private PController subcontroller;
  /* The subcontroller controls all the object that are
   * being drawn on this PPanel
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private boolean hasBorder = true;
  /* Allow for the panel to have a border.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private PObject tabObject, previousTabObject;
  /* These object get focus when pressing tab or 
   * shift + tab
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private boolean displayBars = true;
  protected PScrollbar verticalBar;
  protected PScrollbar horizontBar;
  /* The scrollbars.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */






  /* CONSTRUCTOR :
   * Store parent PApplet and initialize the extended
   * PGraphicsJava2D object to be able to draw on here.
   * PPanel also has a PController since it basically
   * functions like a second PApplet.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  protected PPanel(PController c, int w, int h) {
    controller = c;

    // Init PGraphics
    setParent(controller.getPApplet());
    setPrimary(false);
    setPath("");
    super.setSize(w, h);

    // Create scrollbars on the same controller as the
    // Scrollpane to prevent misalignments with the translate
    verticalBar = c.createScrollbar(PScrollbar.VERTICAL, h - 20);
    horizontBar = c.createScrollbar(PScrollbar.HORIZONTAL, w - 20);

    // Create subcontroller for this panel
    subcontroller = new PController(this);
  }

  




  /* CREATE POBJECTS :
   * Since PPanel basically functions as a second sketch,
   * this class also has the ability to create PObjects
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  public PPanel createPanel(int w, int h) {
    return subcontroller.createPanel(w, h);
  }

  public PButton createButton() {
    return subcontroller.createButton();
  }

  public PScrollbar createScrollbar(int type, int len) {
    return subcontroller.createScrollbar(type, len);
  }

  public PInput createInput(int t, int w, int h) {
    return subcontroller.createInput(t, w, h);
  }






  /* VOID METHODS :
   * Methods that don't return something.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  // Sets focus of this object, and objects on its subcontroller.
  public void setFocus(boolean f) {

    // Update focus on subcontroller
    subcontroller.updateFocus(f);

    // If the subcontroller has no object under the mouse
    // and f is true, set focus to true.
    focused = subcontroller.getObjectUnderMouse() == null && f;
  }

  // Same as setFocus, only this one gets called constantly.
  public void setHover(boolean h) {

    // Update hover on subcontroller
    subcontroller.updateHover(h);

    // If the subcontroller has no object under the mouse
    // and f is true, set hover to true.
    hovering = subcontroller.getObjectUnderMouse() == null && h;
  }

  // Changes the size of the button, but only if the new
  // given size is different from the current size to prevent
  // lag.
  public void setSize(int w, int h) {
    if ((w != width || h != height) && w > 0 && h > 0) {
      super.setSize(w, h);
    }
  }

  // This given object will get focus when pressing tab
  public void setTabObject(PObject t) {

    // When setting the tab object, also set the previous
    // tab object of the tabobject to this object.
    tabObject = t;
    t.setPreviousTabObject(this);
  }

  // This given object will get focus when pressing shift + tab
  public void setPreviousTabObject(PObject t) {
    previousTabObject = t;
  }

  // Sets the dimensions of the scroll window
  public void setScrollDimensions(int x, int y) {
    horizontBar.setMaximumScroll(x);
    verticalBar.setMaximumScroll(y);
  }

  // Adjusts the value of the horizontal scrollbar
  public void setScrolledX(int v) {
    horizontBar.setValue(v);
  }

  // Adjusts the value of the vertical scrollbar
  public void setScrolledY(int v) {
    verticalBar.setValue(v);
  }

  // Toggle the scrollbar visibility
  public void displayScrollbars(boolean f) {
    displayBars = f;
  }

  // Set color of border, using either RGBA, RGB or Greyscale 
  // and the state for which to set the color. Can also use state -1
  // to change color for all states
  public void setBorderColor(int r, int g, int b, int a) {
    borderColor = getColor(r, g, b, a);
    redraw();
  }
  public void setBorderColor(int r, int g, int b) {
    setBorderColor(r, g, b, 255);
  }
  public void setBorderColor(int g, int a) {
    setBorderColor(g, g, g, a);
  }
  public void setBorderColor(int g) {
    setBorderColor(g, g, g, 255);
  }






  /* RETURN METHODS :
   * Methods that return something.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  // Returns the cursor currenly in use
  public int getCursor() {
    return subcontroller.getCursor();
  }

  // Window width
  public int getWidth() {
    return width;
  }

  // Window height
  public int getHeight() {
    return height;
  }

  // Returns true when object is in focus
  public boolean isFocused() {
    return focused;
  }

  // Returns true when mouse is hovering over this object
  // Takes into account sub controllers
  public boolean isHovering() {
    return hovering;
  }

  // Returns true when mouse is over this object  
  public boolean mouseOver() {
    int mouseX = controller.mouseX;
    int mouseY = controller.mouseY;

    return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
  }

  // Return the main PApplet
  public PApplet getPApplet() {
    return controller.getPApplet();
  }

  // Returns the controller of this object
  public PController getController() {
    return controller;
  }

  // Returns the subcontroller that controls all the content on this object
  public PController getSubcontroller() {
    return subcontroller;
  }

  // Returns the extended PGraphicsJava2D object
  public PGraphics getGraphics() {
    return (PGraphics) this;
  }

  // Returns the scrollbar object
  public PScrollbar getVerticalScrollbar() {
    return verticalBar;
  }

  // Returns the scrollbar object
  public PScrollbar getHorizontalScrollbar() {
    return horizontBar;
  }

  // Returns scroll value of scrollbar
  public int getScrolledX() {
    return horizontBar.getValue();
  }

  // Returns scroll value of scrollbar
  public int getScrolledY() {
    return verticalBar.getValue();
  }

  // Converts color to int
  public int getColor(int r, int g, int b, int a) {
    return controller.getPApplet().color(r, g, b, a);
  }
  public int getColor(int r, int g, int b) {
    return controller.getPApplet().color(r, g, b);
  }
  public int getColor(int g, int a) {
    return controller.getPApplet().color(g, a);
  }
  public int getColor(int g) {
    return controller.getPApplet().color(g);
  }






  /* UPDATE :
   * 
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  public void update() {
    subcontroller.update();

    //Automatically set the maximum scroll to the biggest coord at which something was drawn
    if (horizontBar.getMaximumScroll() < getSubcontroller().getBiggestX()) horizontBar.setMaximumScroll(getSubcontroller().getBiggestX());
    if (verticalBar.getMaximumScroll() < getSubcontroller().getBiggestY()) verticalBar.setMaximumScroll(getSubcontroller().getBiggestY());
  }






  /* VISUALS :
   * 
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  public void beginDraw() {
    super.beginDraw();
    pushMatrix();
    int x = -horizontBar.getValue();
    int y = -verticalBar.getValue();
    translate(x, y);
  }

  public void endDraw() {
    popMatrix();

    // Border around the panel
    noFill();
    stroke(borderColor, isFocused() ? 180 : 255);
    strokeWeight(isFocused() ? (float) 1.5 : 2);
    if (hasBorder) rect(-1, -1, width, height, 2);

    super.endDraw();
  }

  // Not in use since the ppanel is being drawn on by the user
  public void redraw() {
  }


  // Display the visuals
  public void display(int x_, int y_) {
    x = x_;
    y = y_;

    controller.set(this, x, y);

    // Display scrollbars, adjust size depending on which scrollbar is visible
    int padding = 3;

    // If both are visible adjust the size to leave a square at the bottom
    // right open to prevent overlapping.
    if (verticalBar.canScroll() && horizontBar.canScroll() && displayBars) {
      verticalBar.setSize(verticalBar.width, height - 2*padding - horizontBar.height);
      verticalBar.display(x + width - verticalBar.width - padding, y + padding);
      horizontBar.setSize(width - 2*padding - verticalBar.width, horizontBar.height);
      horizontBar.display(x + padding, height - horizontBar.height + y - padding);
    } 

    // Else is only one of them is visible just make the bars be 
    // the entire width/height
    else if (verticalBar.canScroll() && displayBars) {
      verticalBar.setSize(verticalBar.width, height - 2*padding);
      verticalBar.display(x + width - verticalBar.width - padding, y + padding);
    } else if (horizontBar.canScroll() && displayBars) {
      horizontBar.setSize(width - 2*padding, horizontBar.height);
      horizontBar.display(x + padding, height - horizontBar.height + y - padding);
    }
  }






  /* EVENTS :
   * 
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  // Mouse Events
  public void mouseEvent(MouseEvent e) {

    // First the mouse events to subcontroller
    // Adjust the mouseEvent to have mouse coords relative
    // to the object's panel
    int newMouseX = e.getX() - x + horizontBar.getValue();
    int newMouseY = e.getY() - y + verticalBar.getValue();

    // Create new native mouse event object with adjusted coords
    java.awt.event.MouseEvent oj = (java.awt.event.MouseEvent) e.getNative();
    java.awt.event.MouseEvent nj = new java.awt.event.MouseEvent(
      (java.awt.Component) oj.getSource(), oj.getID(), e.getMillis(), 
      e.getModifiers(), newMouseX, newMouseY, e.getCount(), 
      false, oj.getButton());

    // Use native object to create the new mouse event object
    MouseEvent n = new MouseEvent(
      nj, e.getMillis(), e.getAction(), e.getModifiers(), 
      newMouseX, newMouseY, e.getButton(), e.getCount());

    // Send mouse event object to subcontroller
    getSubcontroller().mouseEvent(n);

    // Then the mouse events for this object
    switch(e.getAction()) {
    case MouseEvent.PRESS : 
      mousePressed(e);
      break;
    case MouseEvent.MOVE : 
      mouseMoved(e);
      break;
    case MouseEvent.DRAG : 
      mouseDragged(e);
      break;
    case MouseEvent.RELEASE : 
      mouseReleased(e);
      break;
    case MouseEvent.CLICK : 
      mouseClicked(e);
      break;
    case MouseEvent.WHEEL : 
      mouseWheel(e);
      break;
    }
  }

  private void mousePressed(MouseEvent e) {
  }

  private void mouseMoved(MouseEvent e) {
  }

  private void mouseDragged(MouseEvent e) {
  }

  private void mouseReleased(MouseEvent e) {
  }

  private void mouseClicked(MouseEvent e) {
  }

  private void mouseWheel(MouseEvent e) {
    if (isHovering()) verticalBar.scroll((int) (e.getAmount()*25.0));
  }

  // Key Events
  public void keyEvent(KeyEvent e) {
    switch(e.getAction()) {
    case KeyEvent.PRESS : 
      keyPressed(e);
      break;
    case KeyEvent.TYPE : 
      keyTyped(e);
      break;
    case KeyEvent.RELEASE : 
      keyReleased(e);
      break;
    }

    // Send mouse event object to subcontroller
    subcontroller.keyEvent(e);
  }

  private void keyPressed(KeyEvent e) {
  }

  private void keyTyped(KeyEvent e) {

    // Tab object functionality, when pressing tab or shift + tab this
    // part will set focus to the given tabobject/previoustabobject
    if (isFocused() && e.getKey() == TAB) {
      if (previousTabObject != null && e.isShiftDown()) {
        setFocus(false);
        previousTabObject.setFocus(true);

        // Also make sure the object in focus changes on the controller
        getController().setObjectInFocus(null);
        previousTabObject.getController().setObjectInFocus(previousTabObject);
      } else if (tabObject != null && !e.isShiftDown()) {
        setFocus(false);
        tabObject.setFocus(true);

        // Also make sure the object in focus changes on the controller
        getController().setObjectInFocus(null);
        tabObject.getController().setObjectInFocus(tabObject);
      }
    }
  }

  private void keyReleased(KeyEvent e) {
  }
}
