
import processing.core.*;
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 
import processing.awt.*; 

public class PScrollbar implements PObject, PConstants {

  /* CONTENT:
   * > VARIABLES
   * > CONSTRUCTOR
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

  public static int IDLE = 0;
  public static int HOVER = 1;
  public static int PRESS = 2;
  public static int VERTICAL = 10;
  public static int HORIZONTAL = 11;
  /* Static variables, aka constants.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private int scrolled = 0;
  private int windowDimension;
  private int scrollDimension;
  private int prevbarPosition = 0; // Used when scrolling with mouse
  private int barPosition = 0;
  private int barLength = 0;
  /* Scrollbar info.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private int prevState = PScrollbar.IDLE;
  private int state = PScrollbar.IDLE;
  /* The state of the button, this determines the visuals
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private int type;
  /* Either horizontal or vertical.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private int cursor = ARROW;
  /* Cursor currently being displayed.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private int barPadding = 1;
  private int barThickness = 20;
  public int width, height;
  private int x, y;
  /* useful window information.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private int fillColorIdle;
  private int fillColorHover;
  private int fillColorPress;
  private int backgroundColorIdle;
  private int backgroundColorHover;
  private int backgroundColorPress;
  /* Color info for displaying the button.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private boolean scrolling = false;
  /* Is true when the scrollbar is pressed. 
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private boolean focused = false;
  /* Is true when this object has focus. 
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private boolean hovering = false;
  /* True when the mouse is over this object.
   * Takes into account all the subcontrollers.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private PGraphics graphics;
  /* The PGraphics on which the object will be drawn 
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private PController controller;
  /* The controller this object is in.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private PObject tabObject, previousTabObject;
  /* These object get focus when pressing tab or 
   * shift + tab
   * * * * * * * * * * * * * * * * * * * * * * * * * * */






  /* CONSTRUCTOR :
   * Protected since this should only be initialized
   * inside of a PController object
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  protected PScrollbar(PController p, int type_, int len) {
    controller = p;

    // Depending on type, change width and height.
    type = type_;

    resetColors();


    windowDimension = len;
    scrollDimension = 1;
    if (type == PScrollbar.HORIZONTAL) {
      height = barThickness;
      width = len;
    } else if (type == PScrollbar.VERTICAL) {
      height = len;
      width = barThickness;
    } else {

      // When illegal type, throw exception cuz why not lmao
      throw new java.lang.IllegalArgumentException("Invalid Scrollbar type");
    }

    // Create graphics
    graphics = p.getPApplet().createGraphics(width, height);

    // Update and redraw
    update();
    redraw();
  }






  /* VOID METHODS :
   * Methods that don't return something.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  // Set the focus of this object
  public void setFocus(boolean f) {
    focused = f;
  }

  // Sets the hovering of this object
  public void setHover(boolean h) {
    hovering = h;
  }

  // Changes the size of the object, but only if the new
  // given size is different from the current size to prevent
  // lag.
  public void setSize(int w, int h) {
    if (w != width || h != height) {

      width = w;
      height = h;

      if (type == PScrollbar.HORIZONTAL) {
        windowDimension = width;
      } else if (type == PScrollbar.VERTICAL) {
        windowDimension = height;
      }
      graphics.setSize(w, h);
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

  // This sets the scrolled amount, also causes an update and a
  // redraw cuz the scrolled value changed
  public void setValue(int v) {
    scrolled = v;
    update();
    redraw();
  }

  // Set color of button fill, using either RGBA, RGB or Greyscale 
  // and the state for which to set the color. Can also use state -1
  // to change color for all states
  public void setFillColor(int state, int r, int g, int b, int a) {
    if (state == PScrollbar.IDLE) fillColorIdle = getColor(r, g, b, a);
    else if (state == PScrollbar.HOVER) fillColorHover = getColor(r, g, b, a);
    else if (state == PScrollbar.PRESS) fillColorPress = getColor(r, g, b, a);
    else if (state == -1) {
      fillColorIdle = getColor(r, g, b, a);
      fillColorHover = getColor(r, g, b, a);
      fillColorPress = getColor(r, g, b, a);
    }
  }
  public void setFillColor(int state, int r, int g, int b) {
    setFillColor(state, r, g, b, 255);
  }
  public void setFillColor(int state, int g, int a) {
    setFillColor(state, g, g, g, a);
  }
  public void setFillColor(int state, int g) {
    setFillColor(state, g, g, g, 255);
  }

  // Set color of button border, using either RGBA, RGB or Greyscale 
  // and the state for which to set the color. Can also use state -1
  // to change color for all states
  public void setBackgroundColor(int state, int r, int g, int b, int a) {
    if (state == PScrollbar.IDLE) backgroundColorIdle = getColor(r, g, b, a);
    else if (state == PScrollbar.HOVER) backgroundColorHover = getColor(r, g, b, a);
    else if (state == PScrollbar.PRESS) backgroundColorPress = getColor(r, g, b, a);
    else if (state == -1) {
      backgroundColorIdle = getColor(r, g, b, a);
      backgroundColorHover = getColor(r, g, b, a);
      backgroundColorPress = getColor(r, g, b, a);
    }
  }
  public void setBackgroundColor(int state, int r, int g, int b) {
    setBackgroundColor(state, r, g, b, 255);
  }
  public void setBackgroundColor(int state, int g, int a) {
    setBackgroundColor(state, g, g, g, a);
  }
  public void setBackgroundColor(int state, int g) {
    setBackgroundColor(state, g, g, g, 255);
  }

  // Copies the style from the given PButton
  public void copyStyle(PScrollbar b) {
    fillColorIdle = b.getFillColor(PScrollbar.IDLE);
    fillColorHover = b.getFillColor(PScrollbar.HOVER);
    fillColorPress = b.getFillColor(PScrollbar.PRESS);
    backgroundColorIdle = b.getBackgroundColor(PScrollbar.IDLE);
    backgroundColorHover = b.getBackgroundColor(PScrollbar.HOVER);
    backgroundColorPress = b.getBackgroundColor(PScrollbar.PRESS);
    redraw();
  }

  // Scrolls a certain amount, also redraws
  public void scroll(int amt) {
    scrolled = PApplet.constrain(scrolled + amt, 0, scrollDimension-windowDimension);
    redraw();
  }

  // Sets the max scroll and also updates and redraws
  public void setMaximumScroll(int m) {
    if (m != scrollDimension) {
      scrollDimension = m;
      update();
      redraw();
    }
  }

  // Resets to standard color
  public void resetColors() {
    fillColorIdle = getColor(190);
    fillColorHover = getColor(150);
    fillColorPress = getColor(120);
    backgroundColorIdle = getColor(220);
    backgroundColorHover = getColor(220);
    backgroundColorPress = getColor(220);
  }







  /* RETURN FUNCTIONS :
   * Methods that return something.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  // Returns the cursor currenly in use
  public int getCursor() {
    return cursor;
  }

  // Returns width of graphics of scrollbar
  public int getWidth() {
    return width;
  }

  // Returns height of graphics of scrollbar
  public int getHeight() {
    return height;
  }

  // Returns the scrolled amount
  public int getValue() {
    return scrolled;
  }

  public int getMaximumScroll() {
    return scrollDimension;
  }

  //Returns fill color given the state
  public int getFillColor(int state) {
    if (state == PScrollbar.IDLE) return fillColorIdle;
    else if (state == PScrollbar.HOVER) return fillColorHover;
    else if (state == PScrollbar.PRESS) return fillColorPress;
    else return 0;
  }

  //Returns border color given the state
  public int getBackgroundColor(int state) {
    if (state == PScrollbar.IDLE) return backgroundColorIdle;
    else if (state == PScrollbar.HOVER) return backgroundColorHover;
    else if (state == PScrollbar.PRESS) return backgroundColorPress;
    else return 0;
  }

  // Returns true when object is focused
  public boolean isFocused() {
    return focused;
  }

  // Returns true when mouse is hovering over this object
  // Takes into account sub controllers
  public boolean isHovering() {
    return hovering;
  }

  // Returns true when mouse is over object
  public boolean mouseOver() {
    int mouseX = controller.mouseX;
    int mouseY = controller.mouseY;

    return mouseX > x && mouseX < x+width && mouseY > y && mouseY < y+height;
  }

  // Returns true if mouse is over scrollbar
  public boolean mouseOverBar() {
    int mouseX = controller.mouseX;
    int mouseY = controller.mouseY;

    if (type == PScrollbar.HORIZONTAL)
      return mouseX > x+barPosition && mouseX < x+barPosition+barLength && mouseY > y && mouseY < y+height;
    else if (type == PScrollbar.VERTICAL) 
      return mouseX > x && mouseX < x+width && mouseY > y+barPosition && mouseY < y+barPosition+barLength;
    else
      return false;
  }

  public boolean canScroll() {
    return scrollDimension > windowDimension;
  }

  // Returns the PGraphics object
  public PGraphics getGraphics() {
    return graphics;
  }

  // Returns the controller of this object
  public PController getController() {
    return controller;
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

    // Manage visual states
    if (scrolling) state = PScrollbar.PRESS;
    else if (mouseOverBar()) state = PScrollbar.HOVER;
    else state = PScrollbar.IDLE;

    // Redraw when there is a state change
    if (state != prevState) redraw();
    prevState = state;

    // This calculates the amount of pixels that is still offscreen
    int maxScroll = PApplet.max(scrollDimension, windowDimension+1) - windowDimension;

    // Constrain scrolled between 0 and maxScroll
    scrolled = PApplet.constrain(scrolled, 0, maxScroll-1);

    // Calculate the barLength and position
    double scrolledScale = (scrollDimension*1.0)/(windowDimension*1.0);

    // Keep the barlength longer than 2 times the bar width
    barLength = PApplet.max((int)((1.0/scrolledScale)*windowDimension), type == PScrollbar.HORIZONTAL ? 2*height : 2*width);
    barPosition = (int) PApplet.map(scrolled, 0, maxScroll, 0, windowDimension-barLength);
  }






  /* VISUALS :
   * 
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  // Redraw the PGraphics.
  public void redraw() {
    graphics.beginDraw();
    graphics.strokeWeight(barPadding);
    if (state == PScrollbar.IDLE) graphics.background(backgroundColorIdle);
    else if (state == PScrollbar.HOVER) graphics.background(backgroundColorHover);
    else if (state == PScrollbar.PRESS) graphics.background(backgroundColorPress);
    if (state == PScrollbar.IDLE) graphics.stroke(backgroundColorIdle);
    else if (state == PScrollbar.HOVER) graphics.stroke(backgroundColorHover);
    else if (state == PScrollbar.PRESS) graphics.stroke(backgroundColorPress);
    if (state == PScrollbar.IDLE) graphics.fill(fillColorIdle);
    else if (state == PScrollbar.HOVER) graphics.fill(fillColorHover);
    else if (state == PScrollbar.PRESS) graphics.fill(fillColorPress);
    if (type == PScrollbar.VERTICAL) graphics.rect(0, barPosition, width-1, barLength);
    else if (type == PScrollbar.HORIZONTAL) graphics.rect(barPosition, 0, barLength, height-1);
    graphics.endDraw();
  }

  // Display the object on using the controller
  public void display(int x_, int y_) {
    x = x_;
    y = y_;

    // Only display if there is something to scroll
    if (canScroll()) controller.set(this, x, y);
  }






  /* EVENTS :
   * 
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  // Mouse Events
  public void mouseEvent(MouseEvent e) {
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
    }
  }

  private void mousePressed(MouseEvent e) {
    if (isFocused() && mouseOverBar()) {
      prevbarPosition = barPosition;
      scrolling = true;
    }
  }

  private void mouseMoved(MouseEvent e) {
  }

  private void mouseDragged(MouseEvent e) {
    if (isFocused() && scrolling) {

      // this will calculate the new bar position
      float newBarPosition = 0;
      if (type == PScrollbar.VERTICAL) newBarPosition = controller.mouseY - controller.pressmouseY + prevbarPosition;
      else if (type == PScrollbar.HORIZONTAL) newBarPosition = controller.mouseX - controller.pressmouseX + prevbarPosition;

      // this converts the new bar position to the amount of scrolled pixels
      scrolled = (int) PApplet.map(newBarPosition, 0, windowDimension, 0, scrollDimension);
      redraw();
    }
  }

  private void mouseReleased(MouseEvent e) {
    scrolling = false;
  }

  private void mouseClicked(MouseEvent e) {
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
