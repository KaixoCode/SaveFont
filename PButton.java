

import processing.core.*;
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 
import processing.awt.*; 

public class PButton implements PObject, PConstants {

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
  /* Static variables, aka constants
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private int cursor = HAND;
  /* Cursor currently being displayed.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private int prevState = PButton.IDLE;
  private int state = PButton.IDLE;
  /* The state of the button, this determines the visuals
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  public int width = 80, height = 40, x, y;
  /* useful window information.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private int fillColorIdle;
  private int fillColorHover;
  private int fillColorPress;
  private int borderColorIdle;
  private int borderColorHover;
  private int borderColorPress;
  private int textColorIdle;
  private int textColorHover;
  private int textColorPress;
  /* Color info for displaying the button.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private int indentAmount = 2;
  /* Text to display on the button
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private int textSize = 16;
  /* Text size of text on the button
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private String buttonText = "Button";
  /* Text to display on the button
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private PFont font;
  /* Font of text on the button
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private boolean focused = false;
  /* Is true when this object has focus. 
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private boolean hovering = false;
  /* True when the mouse is over this object.
   * Takes into account all the subcontrollers.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private int clickCount = 0;
  private boolean clicked = false;
  /* Is true when the button has been pressed. 
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private PGraphics graphics;
  /* The PGraphics on which the button will be drawn 
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
   *
   * Possible information for the constructor:
   * - Name
   * - Location
   * - Size
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  protected PButton(PController p) {
    controller = p;
    resetColors();

    graphics = p.getPApplet().createGraphics(width, height);
    redraw();
  }






  /* VOID METHODS :
   * Methods that don't return something.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  // Set the focus of this object
  public void setFocus(boolean f) {
    focused = f;
    redraw();
  }

  // Sets the hovering of this object
  public void setHover(boolean h) {
    hovering = h;
    redraw();
  }

  // Sets the font of this button
  public void setFont(PFont f) {
    font = f;
    redraw();
  }

  // Sets the text that displays on the button
  public void setText(String t) {
    buttonText = t;
    redraw();
  }

  // Sets the textSize of the text on the button
  public void textSize(int s) {
    textSize = s;
    redraw();
  }

  // Changes the size of the button, but only if the new
  // given size is different from the current size to prevent
  // lag.
  public void setSize(int w, int h) {
    if (w != width || h != height) {
      width = w;
      height = h;
      graphics.setSize(w, h);
      redraw();
    }
  }

  // Set color of button fill, using either RGBA, RGB or Greyscale 
  // and the state for which to set the color. Can also use state -1
  // to change color for all states
  public void setFillColor(int state, int r, int g, int b, int a) {
    if (state == PButton.IDLE) fillColorIdle = getColor(r, g, b, a);
    else if (state == PButton.HOVER) fillColorHover = getColor(r, g, b, a);
    else if (state == PButton.PRESS) fillColorPress = getColor(r, g, b, a);
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
  public void setBorderColor(int state, int r, int g, int b, int a) {
    if (state == PButton.IDLE) borderColorIdle = getColor(r, g, b, a);
    else if (state == PButton.HOVER) borderColorHover = getColor(r, g, b, a);
    else if (state == PButton.PRESS) borderColorPress = getColor(r, g, b, a);
    else if (state == -1) {
      borderColorIdle = getColor(r, g, b, a);
      borderColorHover = getColor(r, g, b, a);
      borderColorPress = getColor(r, g, b, a);
    }
    redraw();
  }
  public void setBorderColor(int state, int r, int g, int b) {
    setBorderColor(state, r, g, b, 255);
  }
  public void setBorderColor(int state, int g, int a) {
    setBorderColor(state, g, g, g, a);
  }
  public void setBorderColor(int state, int g) {
    setBorderColor(state, g, g, g, 255);
  }

  // Set color of button text, using either RGBA, RGB or Greyscale 
  // and the state for which to set the color. Can also use state -1
  // to change color for all states
  public void setTextColor(int state, int r, int g, int b, int a) {
    if (state == PButton.IDLE) textColorIdle = getColor(r, g, b, a);
    else if (state == PButton.HOVER) textColorHover = getColor(r, g, b, a);
    else if (state == PButton.PRESS) textColorPress = getColor(r, g, b, a);
    else if (state == -1) {
      textColorIdle = getColor(r, g, b, a);
      textColorHover = getColor(r, g, b, a);
      textColorPress = getColor(r, g, b, a);
    }
    redraw();
  }
  public void setTextColor(int state, int r, int g, int b) {
    setTextColor(state, r, g, b, 255);
  }
  public void setTextColor(int state, int g, int a) {
    setTextColor(state, g, g, g, a);
  }
  public void setTextColor(int state, int g) {
    setTextColor(state, g, g, g, 255);
  }

  // Copies the style from the given PButton
  public void copyStyle(PButton b) {
    font = b.getFont();
    textSize = b.getTextSize();
    textColorIdle = b.getTextColor(PButton.IDLE);
    textColorHover = b.getTextColor(PButton.HOVER);
    textColorPress = b.getTextColor(PButton.PRESS);
    fillColorIdle = b.getFillColor(PButton.IDLE);
    fillColorHover = b.getFillColor(PButton.HOVER);
    fillColorPress = b.getFillColor(PButton.PRESS);
    borderColorIdle = b.getBorderColor(PButton.IDLE);
    borderColorHover = b.getBorderColor(PButton.HOVER);
    borderColorPress = b.getBorderColor(PButton.PRESS);
    buttonText = b.getText();
    setSize(b.width, b.height);
    redraw();
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







  /* RETURN FUNCTIONS :
   * Methods that return something.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  // Returns the cursor currenly in use
  public int getCursor() {
    return cursor;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  // Returns the amount of times in a row the button has been clicked
  public int getCount() {
    return clickCount;
  }

  // Returns text size
  public int getTextSize() {
    return textSize;
  }

  //Returns text color given the state
  public int getTextColor(int state) {
    if (state == PButton.IDLE) return textColorIdle;
    else if (state == PButton.HOVER) return textColorHover;
    else if (state == PButton.PRESS) return textColorPress;
    else return 0;
  }

  //Returns fill color given the state
  public int getFillColor(int state) {
    if (state == PButton.IDLE) return fillColorIdle;
    else if (state == PButton.HOVER) return fillColorHover;
    else if (state == PButton.PRESS) return fillColorPress;
    else return 0;
  }

  //Returns border color given the state
  public int getBorderColor(int state) {
    if (state == PButton.IDLE) return borderColorIdle;
    else if (state == PButton.HOVER) return borderColorHover;
    else if (state == PButton.PRESS) return borderColorPress;
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

  // Returns true if button has been pressed
  // then resets the button.
  public boolean isClicked() {
    if (clicked) {
      clicked = false;
      return true;
    }
    return false;
  }

  // Returns true when mouse is over object
  public boolean mouseOver() {
    int mouseX = controller.mouseX;
    int mouseY = controller.mouseY;

    return mouseX > x && mouseX < x+width && mouseY > y && mouseY < y+height;
  }

  // Returns the text that is being displayed on the button
  public String getText() {
    return buttonText;
  }

  // Returns font 
  public PFont getFont() {
    return font;
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

    // Only redraw when there is a state change
    if (state != prevState) redraw();
    prevState = state;

    // If focused, and mousePressed, state is PRESS
    if (isFocused() && controller.mousePressed) state = PButton.PRESS;

    // Else, if only hovering, state is HOVER
    else if (isHovering()) state = PButton.HOVER;

    // Else it's in IDLE
    else state = PButton.IDLE;
  }






  /* VISUALS :
   * 
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  public void resetColors() {
    fillColorIdle = getColor(200);
    fillColorHover = getColor(215);
    fillColorPress = getColor(190);
    borderColorIdle = getColor(0);
    borderColorHover = getColor(0);
    borderColorPress = getColor(0);
    textColorIdle = getColor(0);
    textColorHover = getColor(0);
    textColorPress = getColor(0);
  }

  // Redraw the PGraphics.
  public void redraw() {
    graphics.beginDraw();
    graphics.background(255);
    graphics.strokeWeight(isFocused() ? indentAmount-(float) 0.5 : indentAmount);
    graphics.textSize(textSize);
    graphics.textAlign(CENTER, CENTER);
    if (state == PButton.IDLE) {
      graphics.fill(fillColorIdle);
      graphics.stroke(borderColorIdle, isFocused() ? 200 : 255);
      graphics.rect(-indentAmount/2, -indentAmount/2, graphics.width, graphics.height);
      graphics.fill(textColorIdle);
      graphics.text(buttonText, graphics.width/2, graphics.height/2 - textSize/4);
    } else if (state == PButton.HOVER) {
      graphics.fill(fillColorHover);
      graphics.stroke(borderColorHover, isFocused() ? 200 : 255);
      graphics.rect(-indentAmount/2, -indentAmount/2, graphics.width, graphics.height);
      graphics.fill(textColorHover);
      graphics.text(buttonText, graphics.width/2, graphics.height/2 - textSize/4);
    } else if (state == PButton.PRESS) {
      graphics.fill(fillColorPress);
      graphics.stroke(borderColorPress, isFocused() ? 200 : 255);
      graphics.rect(0, 0, graphics.width, graphics.height);
      graphics.fill(textColorPress);
      graphics.text(buttonText, graphics.width/2 + indentAmount/2, graphics.height/2 + indentAmount/2 - textSize/4);
    }
    graphics.endDraw();
  }

  // Display the object on using the controller
  public void display(int x_, int y_) {
    x = x_;
    y = y_;

    controller.set(this, x, y);
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
  }

  private void mouseMoved(MouseEvent e) {
  }

  private void mouseDragged(MouseEvent e) {
  }

  private void mouseReleased(MouseEvent e) {
  }

  private void mouseClicked(MouseEvent e) {

    // If the mouse was clicked and mouse is hovering over
    // the button, set pressed to true.
    if (isHovering()) {
      clickCount = e.getCount();
      clicked = true;
    }
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
