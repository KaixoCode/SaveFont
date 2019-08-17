
import processing.core.*;
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 
import processing.awt.*; 

import java.util.ArrayList;

public class PController implements PConstants {

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

  private boolean mainController;
  /* True when this is the main controller.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private PApplet parent;
  /* Stores the main PApplet from the user's sketch.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private PGraphics graphics;
  /* The graphics on which the POBjects will be drawn.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private ArrayList<PObject> content = new ArrayList<PObject>();
  /* All the created content is stored inside the 
   * controller.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private PObject objectUnderMouse;
  /* This will store the PObject which is directly under
   * the mouse, so this depends on when it was drawn
   * since it also takes into account overlapping objects.
   * But only in the same controller
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private PObject objectInFocus;
  /* This will store the PObject which is in focus
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private PObject objectInHover;
  /* This will store the PObject which the mouse is
   * currently hovering over, this will take into account
   * all the sub controllers.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  protected int pressmouseX = 0, pressmouseY = 0; // X and Y from last press
  protected int mouseX = 0, mouseY = 0;
  protected boolean mousePressed = false;
  /* useful mouse information 
   * * * * * * * * * * * * * * * * * * * * * * * * * * */


  protected int biggestX = 0, biggestY = 0;
  /* Stores the biggest coords at which something was drawn 
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private int cursor = ARROW;
  /* Cursor currently being displayed 
   * * * * * * * * * * * * * * * * * * * * * * * * * * */






  /* CONSTRUCTOR :
   * One for the main PApplet of the sketch and one
   * for a PPanel, which functions like a small PApplet
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  // Constructor for main controller, using PApplet.
  public PController(PApplet p) {
    parent = p;
    graphics = p.getGraphics();

    // Only register methods for the main controller
    // so we can limit the activity on subcontrollers
    // using the main controller.
    parent.registerMethod("draw", this);
    parent.registerMethod("mouseEvent", this);
    parent.registerMethod("keyEvent", this);

    // And this is the main controller, so true
    mainController = true;
  }

  // Constructor for sub controller, using PPanel.
  protected PController(PPanel p) {
    parent = p.getPApplet();
    graphics = (PGraphics) p;

    // This is not a main controller since it originated from a PPanel
    mainController = false;
  }






  /* CREATE POBJECTS :
   * Every PObject is created and stored inside the 
   * PController to manage the focus of all the objects, 
   * and to control all the key/mouse events.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  // Create a PPanel
  public PPanel createPanel(int w, int h) {
    PPanel panel = new PPanel(this, w, h);  

    // Store object in the content
    content.add(panel);

    return panel;
  }

  // Create a PButton
  public PButton createButton() {
    PButton button = new PButton(this);  

    // Store object in the content
    content.add(button);

    return button;
  }

  // Create a PScrollbar
  public PScrollbar createScrollbar(int type, int len) {
    PScrollbar slider = new PScrollbar(this, type, len);  

    // Store object in the content
    content.add(slider);

    return slider;
  }

  // Create a PScrollPane
  public PInput createInput(int type, int w, int h) {
    PInput textPane = new PInput(this, type, w, h);  

    // Store object in the content
    content.add(textPane);

    return textPane;
  }






  /* VOID METHODS :
   * Methods that don't return something.
   * * * * * * * * * * * * * * * * * * * * * * * * * */

  // Add content to the controller object
  public void addContent(PObject c) {
    content.add(c);
  }

  // Set cursor used by this controller
  public void setCursor(int c) {
    if (cursor == -1) cursor = c;
  }

  // This updates the object that currently has focus
  public void setObjectInFocus(PObject o) {
    objectInFocus = o;
    for (PObject g : content) g.setFocus(false);
    if (o != null) objectInFocus.setFocus(true);
  }






  /* RETURN METHODS :
   * Methods that return something.
   * * * * * * * * * * * * * * * * * * * * * * * * * */

  // Returns the main PApplet 
  public PApplet getPApplet() {
    return parent;
  }

  // Returns the object currently under the mouse
  public PObject getObjectUnderMouse() {
    return objectUnderMouse;
  }

  // Returns the object that currently has focus
  public PObject getObjectInFocus() {
    return objectInFocus;
  }

  // Returns all the PObjects in this controller
  public ArrayList<PObject> getContent() {
    return content;
  }

  // Returns cursor currently in use by this controller
  public int getCursor() {
    return cursor;
  }

  // Returns biggest x coordinate where something is drawn
  public int getBiggestX() {
    return biggestX;
  }

  // Returns biggest y coordinate where something is drawn
  public int getBiggestY() {
    return biggestY;
  }






  /* UPDATE :
   * 
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  // This method only gets used by the main controller.
  public void draw() {

    // updateHover functions the same as updateFocus
    // only this one updates constantly.
    if (mainController) updateHover(true);

    // Set cursor, if -1 set to arrow
    // if cursor is -1 it allows the user to use 
    // "public void PController.setCursor();"
    // This prevents the user from overriding 
    // a pre-existing cursor.
    // Only on main controller, since draw()
    // only gets registered when made with a PApplet
    parent.cursor(cursor == -1 ? ARROW : cursor);

    // The main update for all the controllers because
    // draw() only happens in the main controller
    update();
  }

  // Main update for all the controllers
  protected void update() {
    updateCursor();

    // Make sure all objects update
    for (PObject o : content) o.update();


    // Set objectUnderMouse to null if mouse is no longer over that object
    if (objectUnderMouse != null && !objectUnderMouse.mouseOver()) 
      objectUnderMouse = null;
  }

  // Cursor updates
  private void updateCursor() {

    // If mousepressed, set cursor to cursor of object in focus
    // else set cursor to object under mouse
    if (objectInFocus != null && mousePressed) cursor = objectInFocus.getCursor();
    else if (objectUnderMouse != null) cursor = objectUnderMouse.getCursor();
    else cursor = -1;
  }

  // This method gets called when a mousepress occurs if it is the main
  // controller, or when the PPanel this controller belongs to
  // receives a call for PPanel.setFocus(boolean f);
  protected void updateFocus(boolean setFocus) {

    // First remove focus from all the content
    // Then, if setFocus is true, set focus to the 
    // object under the mouse to true.
    for (PObject p : content) p.setFocus(false);  
    if (objectUnderMouse != null && setFocus) objectUnderMouse.setFocus(true);

    // Also set object in focus to the objectUnderMouse if setFocus is true.
    objectInFocus = setFocus ? objectUnderMouse : null;
  }

  // This method is like updateFocus() except this one updates constantly
  // so it basically functions like hovering.
  protected void updateHover(boolean setHover) {

    // First remove focus from all the content
    // Then, if setFocus is true, set focus to the 
    // object under the mouse to true.
    for (PObject p : content) p.setHover(false);  
    if (objectUnderMouse != null && setHover) objectUnderMouse.setHover(true);

    // Also set object in focus to the objectUnderMouse if setFocus is true.
    objectInHover = setHover ? objectUnderMouse : null;
  }






  /* VISUALS :
   * 
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  // Displays the PObject on the PGraphics object of
  // this controller.
  protected void set(PObject object, int x, int y) {

    // Keeps track of the biggest coord at which an object is being put.
    if (biggestX < x + object.getWidth()) biggestX = x + object.getWidth();
    if (biggestY < y + object.getHeight()) biggestY = y + object.getHeight();

    // This makes sure that the last object drawn 
    // will be the one in the variable.
    if (object.mouseOver()) objectUnderMouse = object;

    try {

      // Manually apply the translate by getting the current matrix
      // and adding the translate x and y to the position.
      // Because PGraphics.set() does not support translation
      // but is faster when drawing to the screen
      float[] target = graphics.getMatrix().get(new float[6]);
      graphics.set(x + (int)target[2], y + (int)target[5], object.getGraphics());
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }






  /* EVENTS :
   * 
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  // Mouse Events
  public void mouseEvent(MouseEvent e) {

    // First its own mouse event handling
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

    // Then send the mouse event to the content
    for (PObject p : content) p.mouseEvent(e);
  }

  private void mousePressed(MouseEvent e) {

    // Update the press mouse coords 
    pressmouseX = e.getX();
    pressmouseY = e.getY();


    // Only the main controller handles the updateFocus
    // All subcontrollers are handled by their respective
    // PPanel objects in "public void setFocus(boolean f);".
    if (mainController) updateFocus(true);

    // Mousepressed stays true until
    // mousereleased has been called
    mousePressed = true;
  }

  private void mouseMoved(MouseEvent e) {

    //Update the mouse coords
    mouseX = e.getX();
    mouseY = e.getY();
  }

  private void mouseDragged(MouseEvent e) {

    // Update the mouse coords
    mouseX = e.getX();
    mouseY = e.getY();
  }

  private void mouseReleased(MouseEvent e) {

    // Set mousepressed to false again
    mousePressed = false;
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

    // Then send the mouse event to the content
    //for (PObject p : content) p.keyEvent(e);
    if (objectInFocus != null) objectInFocus.keyEvent(e);
  }

  private void keyPressed(KeyEvent e) {
  }

  private void keyTyped(KeyEvent e) {
  }

  private void keyReleased(KeyEvent e) {
  }
}
