
import processing.core.*;
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 
import processing.awt.*; 

import java.util.ArrayList;


public class PInput extends PPanel implements PObject, PConstants {

  /* CONTENT:
   * > VARIABLES
   * > CONSTRUCTOR
   * > VOID METHODS
   * > RETURN METHODS
   * > EVENT TO ACTION
   * > ACTIONS
   * > UPDATE
   * > VISUALS
   * > EVENTS
   * * * * * * * * * * * * * * * * * * * * * * * * * * */






  /* VARIABLES :
   * All the object's variables are right here in this
   * section.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private PGraphics graphics;
  /* Store a copy of the graphics object of PPanel in here
   * for easy acces when drawing.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private String[] lines = new String[0];
  private String[][] splitLines = new String[0][0];
  private String text = "";
  private String placeholder = "";
  private String prefix = "";
  /* The text on this textpane, the placeholder for when
   * the pane is empty, and the prefix that will always
   * be displayed at the beginning of the textpane.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private int cursor = TEXT;
  private int typePos = 0;
  private int typeXV = 0; // This one is used for up and down key to determine index position in that line 
  private int typeX = 0;
  private int typeY = 0;
  private int typeLine = 0;
  /* The index where the user types in the text and the
   * location on the panel where that text is located.
   * also the line in the text where typepos is currently
   * located.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private int selectPos = 0; // Index in text 
  private int selectX = 0; // Second Location in PGraphics object
  private int selectY = 0; // To determine the selected text
  /* Used as a sort of second type pos to determine the 
   * beginning and ending of selection.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private int typeTimer = 0; 
  /* Used as timer for the flickering line where you type
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private PFont font; 
  private int textSize = 16;
  private int textLeading = 20;
  private int paddingX = 10; // Padding inside the textarea for text
  private int paddingY = 10;
  /* Some display settings like textsize and line height
   * padding on the sides of the text etc.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private int updateSyntaxOnLine = 0;
  /* The syntax colors get updated passively, this 
   * variable keeps track which line should be updated
   * on this frame.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private PSyntax syntax = null;
  /* The syntax object used by this textpane
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private int textColor;
  private int selectColor;
  private int backgroundColor;
  /* Some colors
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private ArrayList<IntList> syntaxColor = new ArrayList<IntList>();
  /* Stores the syntax colors of the entire text
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private int enabledSyntaxIndex = -1;
  private boolean multiLine = false;
  private String disablingChar = ""; 
  /* Used to keep track of Syntax things that go through
   * multiple parts of text, the enabledSyntaxIndex stores
   * the index of which color should be displayed and 
   * the disablingChar keeps track of what char disables 
   * the Syntax color. Multiline tells if the syntax color  
   * can be on multiple lines.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private boolean editable = true;       // Is textArea editable?
  private boolean submitable = false; // Allows for use as, for example, a search bar, press enter to submit.
  private boolean submitted = false; // true when enter has been pressed and submitable was true
  private int type = PInput.TEXT;
  public static int TEXT = 0;
  public static int LINE = 1;
  public static int PASSWORD = 2;
  public static int HEXCOLOR = 3;
  public static int NUMBER = 4;
  /* Some toggleable settings and type of textpane
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  private int refreshFrame = 0; // This one makes sure the text area still redraws a couple frames after it's unfocused
  private boolean refresh = true;
  private boolean update = true;
  private boolean recalculate = true;
  private boolean scrollToTypePos = true;
  private boolean updateTypeXV = false; // True when x pos for up and down key needs to update with other typeX
  /* Some variables that are true when stuff needs to
   * happen, this makes it more efficient, because if
   * 2 places need the same function to be called, it
   * will only be called once at the end of the cycle.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */






  /* CONSTRUCTOR :
   * Protected since this should only be initialized
   * inside of a PController object
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  protected PInput(PController p, int t, int w, int h) {
    super(p, w, h);
    graphics = getGraphics();

    // Set input type
    setType(t);

    // Set the standard colors
    textColor = getColor(0);
    selectColor = getColor(255, 204, 0);
    backgroundColor = getColor(255, 255, 255);

    // This makes sure there's no nullpointer when
    // first displaying the textpane, because PGraphics...
    beginDraw();
    endDraw();

    // Standard font, textSize and textLeading.
    textFont(parent.createFont("Arial", 24, true));

    // Do the calculations and then redraw for the first time.
    recalculate();
    redraw();
  }






  /* VOID METHODS :
   * Methods that don't return something.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  public void setFocus(boolean f) {
    super.setFocus(f);

    // This makes sure that when the textpane isn't focused, no text
    // can be selected. This also takes into account the scrollbars
    // since it'd be very annoying that it deselected everything when
    // using the scrollbars.
    if (!(isFocused() || horizontBar.isFocused() || verticalBar.isFocused())) selectPos = typePos;

    typeTimer = 0;
  }

  // Set the textSize
  public void textFont(PFont s) {

    // First update the font
    font = s;
    super.textFont(font);

    // Then update the textSize to the one given in the font
    textSize = font.getSize();

    // Also adjust the textLeading to the new font size
    textLeading = textSize;

    // Recalculate the positions because the font changed
    recalculate = true;
    refresh = true;
  }

  // Set the textSize
  public void textSize(int s) {

    // To change the textSize we need to reinitialize the font
    // because otherwise it'll look blurry.
    font = parent.createFont(font.getName(), s, true);
    super.textFont(font);

    // Then also update the textSize
    textSize = s;

    // Recalculate because the textsize changed
    recalculate = true;
    refresh = true;
  }

  // Set the textLeading
  public void textLeading(int s) {

    // Update the textLeading
    textLeading = s;

    // Calculations
    recalculate = true;
    refresh = true;
  }

  // Set the syntax of this textarea using a PSyntax object.
  public void setSyntax(PSyntax s) {
    syntax = s;

    // Refresh
    recalculate = true;
    refresh = true;
  }

  // Set the text of the textpane
  public void setText(String t) {
    text = t;

    // Recalculate
    recalculate = true;
    refresh = true;
  }

  public boolean scrollBottom = false;
  public void addText(String t) {
    text += t;

    scrollBottom = true;


    // Recalculate
    recalculate = true;
    refresh = true;
  }

  public void changeLastLine(String t) {
    text = text.substring(0, text.lastIndexOf("\n")) + t;
scrollBottom = true;
    // Recalculate
    recalculate = true;
    refresh = true;
  }

  // This method scrolls to the typepos
  public void scrollToTypePos() {

    // Recalculate now because this method depends on typeX which gets
    // updated in recalculate().
    recalculate();

    // Scroll stuff
    if (typeX - width +  2*textSize > getScrolledX()) setScrolledX(typeX - width +  2*textSize); 
    if (typeX - paddingX - textSize < getScrolledX()) setScrolledX(typeX - paddingX - textSize); 
    if (typeY - height + 2*textSize > getScrolledY()) setScrolledY(typeY - height + 2*textSize); 
    if (typeY - paddingY - textSize < getScrolledY()) setScrolledY(typeY - paddingY - textSize);
  }

  public void setSize(int w, int h) {
    if ((w != width || h != height) && w > 0 && h > 0) {
      super.setSize(w, h);
      refresh = true;
      recalculate = true;
    }
  }

  // Defines if the pane is editable
  public void setEditable(boolean n) {
    editable = n;
  }

  public void setSubmitable(boolean n) {
    submitable = n;
  }

  // Sets the placeholder text to show when textpane is empty
  public void setPlaceholder(String t) {
    placeholder = t;
  }

  // Sets the type of textpane this is
  public void setType(int t) {
    if (t == PInput.TEXT) {
      type = PInput.TEXT;

      // Display the scrollbars
      displayScrollbars(true);
    } else if (t == PInput.LINE) {
      type = PInput.LINE;

      // Don't display the scrollbars if it is only a single
      // line, because it looks weird.
      displayScrollbars(false);
    } else if (t == PInput.PASSWORD) {
      type = PInput.PASSWORD;

      // Don't display the scrollbars if it is only a single
      // line, because it looks weird.
      displayScrollbars(false);
    } else if (t == PInput.HEXCOLOR) {
      type = PInput.HEXCOLOR;

      // Don't display the scrollbars if it is only a single
      // line, because it looks weird.
      displayScrollbars(false);
    } else if (t == PInput.NUMBER) {
      type = PInput.NUMBER;

      // Don't display the scrollbars if it is only a single
      // line, because it looks weird.
      displayScrollbars(false);
    }
  }

  // Sets the prefix of the textarea.
  public void setPrefix(String p) {
    prefix = p;
    recalculate = true;
    refresh = true;
  }

  // Set color of background, using either RGBA, RGB or Greyscale 
  // and the state for which to set the color. Can also use state -1
  // to change color for all states
  public void setBackgroundColor(int r, int g, int b, int a) {
    backgroundColor = getColor(r, g, b, a);
    recalculate = true;
    refresh = true;
  }
  public void setBackgroundColor(int r, int g, int b) {
    setBackgroundColor(r, g, b, 255);
  }
  public void setBackgroundColor(int g, int a) {
    setBackgroundColor(g, g, g, a);
  }
  public void setBackgroundColor(int g) {
    setBackgroundColor(g, g, g, 255);
  }


  // Set color of text, using either RGBA, RGB or Greyscale 
  // and the state for which to set the color. Can also use state -1
  // to change color for all states
  public void setTextColor(int r, int g, int b, int a) {
    textColor = getColor(r, g, b, a);
    recalculate = true;
    refresh = true;
  }
  public void setTextColor(int r, int g, int b) {
    setTextColor(r, g, b, 255);
  }
  public void setTextColor(int g, int a) {
    setTextColor(g, g, g, a);
  }
  public void setTextColor(int g) {
    setTextColor(g, g, g, 255);
  }

  // Set color of selection, using either RGBA, RGB or Greyscale 
  // and the state for which to set the color. Can also use state -1
  // to change color for all states
  public void setSelectColor(int r, int g, int b, int a) {
    selectColor = getColor(r, g, b, a);
    recalculate = true;
    refresh = true;
  }
  public void setSelectColor(int r, int g, int b) {
    setSelectColor(r, g, b, 255);
  }
  public void setSelectColor(int g, int a) {
    setSelectColor(g, g, g, a);
  }
  public void setSelectColor(int g) {
    setSelectColor(g, g, g, 255);
  }

  public void copyStyle(PInput s) {
    selectColor = s.getSelectColor();
    backgroundColor = s.getBackgroundColor();
    borderColor = s.getBorderColor();
    textColor = s.getTextColor();
    font = s.getFont();
    syntax = s.getSyntax();
  }







  /* RETURN METHODS :
   * Methods that return something.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  public int getCursor() {
    return PGraphics.TEXT;
  }

  public int getSelectColor() {
    return selectColor;
  }

  public int getBackgroundColor() {
    return backgroundColor;
  }

  public int getBorderColor() {
    return borderColor;
  }

  public int getTextColor() {
    return textColor;
  }

  public PFont getFont() {
    return font;
  }

  public PSyntax getSyntax() {
    return syntax;
  }

  public String getContent() {

    // Depending on the type of textpane, return stuff
    if (type == PInput.TEXT) {
      return text;
    } else if (type == PInput.LINE) {
      return text.replaceAll("\n", "");
    } else if (type == PInput.PASSWORD) {
      return text;
    } else if (type == PInput.HEXCOLOR) {
      String s = text.replaceAll("[^0-9a-fA-F]", "");
      return (s.length() == 0 ? "ffffff" : s);
    } else if (type == PInput.NUMBER) {
      String s = text.replaceAll("[^0-9-.]", "");
      return (s.length() == 0 ? "0" : s);
    }

    return "";
  }

  // Returns true if enter was pressed and it is a submitable textpane
  public boolean isSubmitted() {
    if (submitted) {
      submitted = false;
      return true;
    }
    return false;
  }






  /* EVENT TO ACTION :
   * Methods that convert a key event to an action.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  // Key actions when ctrl is down
  private void ctrlActions(char key) {

    // CTRL + Backspace removes entire words
    if ((int) key == 127 && editable) ctrlBackspace();

    // CTRL + a to select everything
    else if ((int) key == 1) ctrlA();

    // CTRL + c to copy
    else if ((int) key == 3) ctrlC();

    // CTRL + x to cut
    else if ((int) key == 24 && editable) ctrlX();

    // CTRL + v to paste
    else if ((int) key == 22 && editable) ctrlV();
  }

  // Normal type actions
  private void typeActions(KeyEvent e) {

    // Store key in var for easier access
    int keyCode = e.getKeyCode();
    char key = e.getKey();

    // This is true when there is text selected
    boolean selection = selectPos != typePos;

    // Always remove the selection
    removeSelection();

    // Don't do anything when there's a selection and backspace
    if (key == BACKSPACE && selection);

    // If it's only backspace, just do backspace();
    else if (key == BACKSPACE) backspace(); 

    // Same for delete, only when there's no selection
    else if (key == DELETE && selection);
    else if (key == DELETE) delete();

    // Normal type stuff, tab separate because it's just 2 spaces
    else if (key == TAB) tab();
    else typeKey(key);

    // Adjust the selectpos to the typepos
    selectPos = typePos;
  }

  // Update the typepos using the arrow keys
  private void updateTypepos(KeyEvent e) {

    // Resets the typebar timer so it's visible when pressing a key
    typeTimer = 0;

    // easy access key and keyCode
    int keyCode = e.getKeyCode();
    char key = e.getKey();

    // When ctrl is down the arrow keys skip words.
    if (e.isControlDown()) {
      if (keyCode == LEFT) typePos = moveLeftCtrlIndex(typePos);
      else if (keyCode == RIGHT) typePos = moveRightCtrlIndex(typePos);
      else if (keyCode == UP) typePos = getIndexFromCoords(typeXV, typeY - (float)0.5*textLeading);
      else if (keyCode == DOWN) typePos = getIndexFromCoords(typeXV, typeY + (float)1.5*textLeading);
    } else {

      // If something is selected, left and right keys teleport to 
      // beginning or ending of selected text, only when not pressing 
      // shift because then it'll just keep increasing the selection.
      if (!e.isShiftDown() && selectPos != typePos) {
        if (keyCode == LEFT) typePos = PApplet.min(selectPos, typePos);
        else if (keyCode == RIGHT) typePos = PApplet.max(selectPos, typePos);
      } 

      // Else, normal increase or decrease in typepos
      else {
        if (keyCode == LEFT) typePos--;
        else if (keyCode == RIGHT) typePos++;
      }

      // Up and down key functionality, use the y coord and 
      // textleading to go to the line above or below
      if (keyCode == UP) typePos = getIndexFromCoords(typeXV, typeY - (float)0.5*textLeading);
      else if (keyCode == DOWN) typePos = getIndexFromCoords(typeXV, typeY + (float)1.5*textLeading);
    }

    // Constrain the typepos to prevent exceptions.
    typePos = PApplet.constrain(typePos, 0, text.length());
    selectPos = PApplet.constrain(selectPos, 0, text.length());

    // Adjust the selectpos to the typepos only when shift isn't pressed
    // Because when shift is pressed you select text.
    if ((keyCode == LEFT || 
      keyCode == RIGHT || 
      keyCode == UP || 
      keyCode == DOWN) && 
      !e.isShiftDown()) selectPos = typePos;
  }






  /* ACTIONS :
   * Methods that do specific actions.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  // Backspace an entire word
  private void ctrlBackspace() {
    selectPos = moveLeftCtrlIndex(typePos);
    text = text.substring(0, PApplet.max(0, selectPos)) + text.substring(typePos, text.length());
    typePos = selectPos;
  }

  // Select everything
  private void ctrlA() {
    typePos = text.length();
    selectPos = 0;
  }

  // Copy selection
  private void ctrlC() {
    try {

      // Store selected text in a String.
      String copy = text.substring(PApplet.min(typePos, selectPos), PApplet.max(typePos, selectPos));

      // Put the selection in the clipboard
      java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new java.awt.datatransfer.StringSelection(copy), null);
    } 
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  // Cut selection
  private void ctrlX() {
    try {

      // Store the selected text in String
      String copy = text.substring(PApplet.min(typePos, selectPos), PApplet.max(typePos, selectPos));

      // Put the selected text into the clipboard
      java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new java.awt.datatransfer.StringSelection(copy), null);

      // Remove the selected text
      removeSelection();
    } 
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  // Paste clipboard
  private void ctrlV() {
    try {

      // Get clipboard into String
      String addThing = ((String) java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().getData(java.awt.datatransfer.DataFlavor.stringFlavor));

      // Remove any selected text
      removeSelection();

      // Insert clipboard into text at typepos
      text = text.substring(0, typePos) + addThing + text.substring(typePos, text.length());

      //  Adjust typepos to be at the end of the pasted text
      typePos+=addThing.length();

      // Adjust the selectpos to the typepos to make sure nothing
      // is selected
      selectPos = typePos;
    } 
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  // Put the typed char into the text
  private void typeKey(char key) {

    // To prevent arrayindexoutofbounds exceptions
    if (text.length() == 0) selectPos = typePos;

    // Put the char at the typepos in the text.
    text = text.substring(0, PApplet.max(typePos, 0)) + key + text.substring(PApplet.max(typePos, 0), text.length());
    typePos++; // Also adjust typepos
  }

  // Remove the entire selection
  private void removeSelection() {
    if (selectPos > typePos) {
      text = text.substring(0, PApplet.max(0, typePos)) + text.substring(PApplet.max(selectPos, 0), text.length());
    } else if (selectPos < typePos) {
      text = text.substring(0, PApplet.max(0, selectPos)) + text.substring(PApplet.max(typePos, 0), text.length());
      typePos = selectPos;
    }
  }

  // Remove a single character from the text before the typepos
  private void backspace() {
    text = text.substring(0, PApplet.max(0, typePos-1)) + text.substring(typePos, text.length());
    typePos--; // Also adjust the typepos
  }

  // Remove a single character from the text after the typepos
  private void delete() {
    text = text.substring(0, PApplet.max(0, typePos)) + text.substring(PApplet.min(typePos+1, text.length()), text.length());
  }

  // Add a double space, since tab doesn't work with processing's text() method
  private void tab() {
    text = text.substring(0, typePos) + "  " + text.substring(typePos, text.length());
    typePos+=2; // Also adjust the typepos
  }






  /* CALCULATION METHODS :
   * Big methods that calculate something.
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  // Get the index from the coords on the textarea
  private int getIndexFromCoords(float x, float y) {

    // Get the line from the y pos using the textLeading
    // also keep the padding into account.
    int lineIndex = PApplet.constrain(Math.round((y - paddingY - textLeading/2)/textLeading), 0, lines.length-1);

    // Add up the amount of characters in all the lines before
    // Add 1 to lines[i].length() because the "\n" was split out.
    int index = 0; 
    for (int i = 0; i < lineIndex; i++) {
      index += lines[i].length() + 1;
    }

    // Now store the line using the found line index as a String.
    String line = lines[lineIndex];

    // Go through all the characters in the line to see which one 
    // is closest to the given x. Also adjust for padding.
    float xoffset = paddingX;
    for (int i = 0; i < line.length(); i++) {

      // Store the textWidth of current char
      float textW = textWidth(line.charAt(i));

      // If the xoffset + half of the letter width has surpassed
      // the given x position, return the found index.
      if (x < xoffset + 0.5*textW) return index;

      // Else add the complete letter width to the xoffset
      // and increment the index
      xoffset += textW;
      index++;
    }

    // If the x and y are so big that it got to here, this will
    // return the last index of text.
    return index;
  }

  // Get the x, y on the pane using the index in the text
  private PVector getCoordsFromIndex(int i) {

    // Get all text before given index. Add a space to make
    // sure when the index is at the start of a line it doesn't 
    // get removed when we split at "\n".
    String s = text.substring(0, PApplet.constrain(i, 0, PApplet.max(text.length(), 0))) + " ";

    // Split into lines so we can use the textleading to calculate 
    // the y position of the text
    String[] l = s.split("\n");
    float y = (l.length-1)*textLeading;

    // The last String in the array is the line in which the index is
    // located, and we cut it at the given index, so the textWidth of
    // that String is the x position.
    float x = textWidth(l[l.length-1]);

    // Set the x to something different when password, because it only
    // consists of "*".
    if (type == PInput.PASSWORD) x = (l[l.length-1].length()-1) * textWidth("*") + textWidth(" ");

    // Adjust for padding and the added " " to make up for the removed enter
    float xP = x + paddingX - textWidth(" ");
    float yP = y + paddingY + (textAscent() + textDescent())/2 - textSize/2;

    // Return the vector
    return new PVector(xP, yP);
  }

  // Calculates the index left from index "cur" when pressing ctrl.
  // This is divided into 2 classes: 1. [a-zA-Z0-9] and 2. [^a-zA-Z0-9], 
  // so if the current class is (1) then the returned index will be the
  // closest letter on the left that is from class (2) and vice versa.
  private int moveLeftCtrlIndex(int cur) {

    // Make sure the index is within the text boundaries.
    cur = PApplet.constrain(cur, 0, text.length());

    // Get all lines from beginning of text up until current index.
    // add a space at the end so when the typepos is at the beginning
    // of a line it doesn't result in an empty String which means it
    // wont be in the final String[].
    String[] l = (text.substring(0, cur) + " ").split("\n");

    // The current line is the last one in the array of lines.
    String current = l[l.length-1];
    int currentIndex = cur;

    // Go to previous line if length is 1, this happens when the typepos
    // is at the beginning of a line, so doing CTRL left wraps to previous line.
    // We check for length 1 and not 0 because we added a space.
    if (current.length() == 1) {
      currentIndex--;
      return currentIndex;
    }

    // Check from which class the current char is.
    if ((current.charAt(current.length()-2)+"").replaceAll("[a-zA-Z0-9]", "").length() == 0) {

      // Go through each character in order and see if it's the other class
      for (int i = current.length()-2; i >= 0; i--) {

        // If it has found a char from the other class, it just breaks out of the 
        // for-loop, leaving the currentIndex variable at the right value.
        if ((current.charAt(i)+"").replaceAll("[^a-zA-Z0-9]", "").length() == 0) break;
        currentIndex--;
      }
    } else {

      // Go through each character in order and see if it's the other class
      for (int i = current.length()-2; i >= 0; i--) {

        // If it has found a char from the other class, it just breaks out of the 
        // for-loop, leaving the currentIndex variable at the right value.
        if ((current.charAt(i)+"").replaceAll("[a-zA-Z0-9]", "").length() == 0) break;
        currentIndex--;
      }
    }

    // Return the found index.
    return currentIndex;
  }

  // Calculates the index right from index "cur" when pressing ctrl.
  // Same as left one: divided into 2 classes: 1. [a-zA-Z0-9] and 2. [^a-zA-Z0-9], 
  // so if the current class is (1) then the returned index will be the
  // closest letter on the right that is from class (2) and vice versa.
  private int moveRightCtrlIndex(int cur) {

    // To prevent some weird exceptions just return the cur
    // if it's the last character in the text.
    if (cur == text.length()) return cur;

    // Make sure the index is within the text boundaries.
    cur = PApplet.constrain(cur, 0, text.length());

    // Get all lines from current up until the end of the text.
    String[] l = text.substring(cur, text.length()).split("\n");

    // If there's at least 1 line, set that as String current, else
    // just place an empty string as current;
    String current = l.length > 0 ? l[0] : "";
    int currentIndex = cur;

    // Go to next line if length is 0, this happens when the typepos
    // is at the end of a line, so doing CTRL right wraps to next line.
    if (current.length() == 0) {
      currentIndex++;
      return currentIndex;
    }

    // Check from which class the current char is.
    if ((current.charAt(0)+"").replaceAll("[a-zA-Z0-9]", "").length() == 0) {

      // Go through each character in order and see if it's the other class
      for (int i = 0; i < current.length(); i++) {

        // If it has found a char from the other class, it just breaks out of the 
        // for-loop, leaving the currentIndex variable at the right value.
        if ((current.charAt(i)+"").replaceAll("[^a-zA-Z0-9\n]", "").length() == 0) break;
        currentIndex++;
      }
    } else if (current.charAt(0) != '\n') {

      // Go through each character in order and see if it's the other class
      for (int i = 0; i < current.length(); i++) {

        // If it has found a char from the other class, it just breaks out of the 
        // for-loop, leaving the currentIndex variable at the right value.
        if ((current.charAt(i)+"").replaceAll("[a-zA-Z0-9]", "").length() == 0) break;
        currentIndex++;
      }
    }

    // Return the found index.
    return currentIndex;
  }

  // This method takes in a part of text and the surrounding 4 parts of text and returns]
  // the right color for that part according to the currently loaded syntax.
  // If there's no syntax it will simply return the textColor.
  private int getSyntaxColor(String prev2, String prev, String s, String following, String following2) {

    // Return textcolor if there is no syntax
    if (syntax == null) return textColor;

    // This part is for syntax rules that span multiple text parts
    // when enabledSyntaxIndex != -1 it will keep the color associated with
    // the SyntaxIndex stored in said value until it finds the disablingChar.
    if (enabledSyntaxIndex != -1) {
      int temp = enabledSyntaxIndex;

      // Keep the color till end of the line if disablingChar is empty. Else
      // try to find the disabling char in the given text, it can be up to 2 chars
      // long because it will check agains the current and the previous part
      // This disabling wont work if the char before it is a "\"
      if (disablingChar.length() != 0 && 
        ((s.equals(disablingChar) && !prev.equals("\\")) || 
        ((prev+s).equals(disablingChar) && !prev2.equals("\\")))) enabledSyntaxIndex = -1;

      // Keep returning the stored SyntaxIndex color
      return syntax.getClassColor(temp);
    }

    // Go through all different classes in the syntax
    for (int j = 0; j < syntax.classAmount(); j++) {

      // Store all the syntax rules for said class
      String[] a = syntax.getClassContent(j);

      // Also store the color of the current class
      int classColor = syntax.getClassColor(j);

      // Go through all the rules and see if the text parts matches
      // any of the rules.
      for (int i = 1; i < a.length; i++) {

        // If it is a specific word that matches.
        if (s.equals(a[i])) return classColor;

        // a general syntax thing, kinda like regex, but custom.
        // Will probably be updated later to support more.
        if (a[i].split("(\\[.*\\])").length == 0) {

          // [.<?] any character before '?'
          if (following2.length() > 0 && following.length() > 0 && 
            ((a[i].split("<")[0].replace("[", "").equals("n") && s.replaceAll("[0-9]", "").length() == 0) ||
            a[i].split("<")[0].replace("[", "").equals("c") && s.replaceAll("[a-zA-Z]", "").length() == 0) &&
            (a[i].split("<")[1].replace("]", "").equals(following)||
            a[i].split("<")[1].replace("]", "").equals(following + following2)))
            return classColor;

          // [?>.] any character after '?'
          else if (prev2.length() > 0 && prev.length() > 0 && 
            ((prev.equals(a[i].split(">")[0].replace("[", ""))||
            (prev2 + prev).equals(a[i].split(">")[0].replace("[", ""))) &&
            ((a[i].split(">")[1].replace("]", "").equals("n") && s.replaceAll("[0-9]", "").length() == 0) ||
            a[i].split(">")[1].replace("]", "").equals("c") && s.replaceAll("[a-zA-Z]", "").length() == 0)))
            return classColor;

          // ["-"] any characters between " and "
          else if (((s).equals(a[i].split("-")[0].replace("[", "")) || 
            (s+following).equals(a[i].split("-")[0].replace("[", ""))) && !prev.equals("\\")) {
            enabledSyntaxIndex = j;
            disablingChar = a[i].split("-")[1].replace("]", "");
            multiLine = false;
            return classColor;
          }

          // ["="] any characters between " and ", multiline
          else if ((s).equals(a[i].split("=")[0].replace("[", "")) || 
            (s+following).equals(a[i].split("=")[0].replace("[", ""))) {
            enabledSyntaxIndex = j;
            disablingChar = a[i].split("=")[1].replace("]", "");
            multiLine = true;
            return classColor;
          }

          // [n] all numbers
          else if (a[i].length() == 3 && a[i].charAt(1) == 'n' && s.replaceAll("[0-9]", "").length() == 0) {
            return classColor;
          }

          // [^] all caps
          else if (a[i].length() == 3 && a[i].charAt(1) == '^' && s.replaceAll("[A-Z]", "").length() == 0) {
            return classColor;
          }

          // [^n] all caps or numbers
          else if (a[i].length() == 4 && a[i].charAt(1) == '^' && a[i].charAt(2) == 'n' && s.replaceAll("[A-Z0-9]", "").length() == 0) {
            return classColor;
          }
        }
      }
    }

    // Return standard color if nothing is found
    return textColor;
  }






  /* UPDATE :
   * 
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  // This method recalculates the syntax color for the lines
  // start till end and then puts it into the syntaxColor array
  private void updateSyntaxColors(int start, int end) {

    // Loop through the lines start till end.
    for (int j = PApplet.constrain(start, 0, splitLines.length); j < PApplet.constrain(end, 0, splitLines.length); j++) {

      // Store the split line in a String
      String[] s = splitLines[j];

      // Go through all the parts in the current line
      // and calculate the color of that part
      for (int i = 0; i < s.length; i++) {

        // Store part in String.
        String w = s[i];

        // Calculate the color using the surrounding 4 parts, 2 on each side. 
        int c = getSyntaxColor((i-2 > 0 ? s[i-2] : ""), 
          (i-1 > 0 ? s[i-1] : ""), w, (i+1 < s.length ? s[i+1] : ""), 
          (i+2 < s.length ? s[i+2] : ""));

        // Insert in array if the array is already long enough
        if (j < syntaxColor.size()) {  
          syntaxColor.get(j).set(i, c);
        } 

        // Else, add more elements until the array is long enough and
        // then add the color in the right position.
        else {
          while (syntaxColor.size() <= j)
            syntaxColor.add(new IntList());
          syntaxColor.get(j).set(i, c);
        }
      }

      // Only reset syntax color thing if it's not a multiline one
      if (!multiLine) enabledSyntaxIndex = -1;
    }

    // Reset syntax color thing to not wrap back up top
    enabledSyntaxIndex = -1;
  }

  // This method recalculates the biggestX and biggestY value
  // and sets the scroll dimensions. It also updates the syntax
  // color for the line where the typepos is located.
  private void recalculate() {

    // Constrain the typepos to prevent exceptions
    typePos = PApplet.constrain(typePos, 0, text.length());
    selectPos = PApplet.constrain(selectPos, 0, text.length());

    // Store the lines for easy access, and to make things
    // faster since they only have to be split from the
    // main text once per recalculation.
    lines = (text + " ").split("\n");

    // Also store the text parts seperately per line.
    // Also for faster access.
    splitLines = new String[lines.length][];

    // And also calculate the biggest coords to get the
    // right scroll dimensions.
    int biggestY = (lines.length)*textLeading;
    int biggestX = 0;

    // Go through all lines
    for (int i = 0; i < lines.length; i++) {

      // Store line in String for easy access.
      String s = lines[i];

      // Check if line is bigger than biggestX.
      if (biggestX < textWidth(s)) biggestX = (int) Math.ceil(textWidth(s + "w"));

      // If type is password, set lines to "*"
      if (type == PInput.PASSWORD) {

        // Add the right amount of "*" 
        String[] pass = new String[]{""};
        for (int j = 0; j < s.length()-1; j++) pass[0] += "*";

        // Set both lines and split lines to be "*"
        lines[i] = pass[0];
        splitLines[i] = pass;

        // Make sure the splitting below doesn't happen
        continue;
      }

      // And store the split text into the array.
      splitLines[i] = (s + " ").split(
        "(?<=[^a-zA-Z0-9])(?=[a-zA-Z0-9])|"+ // Split words and numbers from symbols (including spaces)
        "(?<=[^a-zA-Z0-9 ])(?=[^a-zA-Z0-9 ])|"+ // split symbols from symbols (not spaces)
        "(?<=[^0-9a-zA-Z])(?=[0-9])|"+ // split symbols (including spaces) from numbers
        "(?<=[0-9])(?=[^0-9a-zA-Z])|"+ // split numbers from symbols (including spaces)
        "(?<=[^0-9a-zA-Z ])(?=[ ])|"+ // split symbols (not spaces) from spaces
        "(?<=[ ])(?=[^0-9a-zA-Z ])|"+ // split spaces from symbols (not spaces)
        "(?<=[a-zA-Z0-9])(?=[^a-zA-Z0-9])"); // Split symbols (including spaces) from words and numbers
    }

    // Adjust scroll dimension to text, but only if it is not a single line of text. 
    setScrollDimensions(biggestX + textSize, biggestY+2*paddingY);

    // Get the coords of the typepos
    // But only update the x position when pressing left or right
    PVector p = getCoordsFromIndex(typePos);
    if (updateTypeXV) typeXV = (int) p.x;
    updateTypeXV = true;
    typeX = (int) p.x;
    typeY = (int) p.y;

    // Get the coords of the selectpos
    // But only update the x position when pressing left or right
    PVector s = getCoordsFromIndex(selectPos);
    selectX = (int) s.x;
    selectY = (int) s.y;

    // Store the line in which currently typing.
    typeLine = text.substring(0, typePos).split("\n").length-1;

    // Update the syntax colors in the typing line
    updateSyntaxColors(typeLine, typeLine+1);
  }

  // Updates that only happen when a redraw happens.
  private void redrawUpdates() {

    // Only allow hexcolor
    if (type == PInput.HEXCOLOR) {

      // First replace everything that shouldn't be in a hex color
      text = text.replaceAll("[^0-9a-fA-F#]", "");

      // The make sure it is less than 6 + "#" chars long
      if (text.length() > 7) text = text.substring(0, 7);

      // And set the prefix
      prefix = "#";
    } 

    // Replace all non numbers when it is a number.
    else if (type == PInput.NUMBER) {

      // Replace all characters that are never in a number.
      text = text.replaceAll("[^0-9-.+]", "");

      // Make sure there is only 1 period in the textpane
      while (text.replaceAll("[^.]", "").length() > 1) {
        text = text.replaceFirst("[.]", "");
      }

      // Also make sure the "-" is at the start and there is only 1.
      if (text.contains("-")) text = "-"+text.replaceAll("[-]", "");
      if (text.contains("+")) text = text.replaceAll("[-+]", "");
    }

    // Replace all line breaks with nothing if it is a single line
    else if (type == PInput.LINE) {
      text = text.replaceAll("\n", "");
    }

    // Replace all line breaks when password, because passwords don't
    // have line breaks in them
    else if (type == PInput.PASSWORD) {
      text = text.replaceAll("\n", "");
    }

    // Make sure the prefix is at the start of the text.
    if (typePos < prefix.length()) typePos = prefix.length(); // Keep the typepos after the prefix.
    if (selectPos < prefix.length()) selectPos = prefix.length(); // Also the selectPos.
    text = prefix + text.substring(PApplet.min(prefix.length(), text.length())); // Put the prefix in the text.

    // Do recalculations when it was requested.
    if (recalculate) recalculate();
    recalculate = false;

    // Scroll to typepos if if was requested
    if (scrollToTypePos) scrollToTypePos();
    scrollToTypePos = false;

    // Auto update 1 line per frame that is currently visible on screen
    // Only 1 because more will have a great impact on the framerate.

    // This stores the first and last line that is currently visible on screen.
    int beginLine = (int) (getScrolledY()/textLeading);
    int endLine = (int)((getScrolledY()+height)/textLeading);

    // Make sure the updateSyntaxOnLine is within the endLine and beginLine.
    if (updateSyntaxOnLine > endLine) updateSyntaxOnLine = beginLine;
    if (updateSyntaxOnLine < beginLine) updateSyntaxOnLine = beginLine;

    // Update the color on that line. And also increase the index by 1
    updateSyntaxColors(updateSyntaxOnLine++, updateSyntaxOnLine);
  }







  /* VISUALS :
   * 
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  public void redraw() {


    // First to some redraw updates.
    redrawUpdates();

    // Begin the drawing
    beginDraw();

    // Set some basic setting
    textAlign(LEFT, TOP);
    background(backgroundColor);
    super.textLeading(textLeading);

    // Display a rectangle around the selection if there is a selection
    // meaning, the selectPos and typePos aren't the same.
    if (selectPos != typePos) {

      // Fill the right color and nostroke();
      fill(selectColor);
      noStroke();

      // Set rectmode to CORNERS since it's easier to draw like this
      rectMode(CORNERS);

      // Depending on where and how the two positions relate to eachother
      // draw certain squares.
      float min = textLeading;
      float bigX = width;

      // Same line
      if (selectY == typeY) {
        rect(selectX, typeY, typeX, typeY + min);
      } 

      // Selectpos is after typepos
      else if (selectY > typeY) {  
        rect(selectX, selectY, paddingX, selectY + min);
        rect(paddingX, typeY + min, bigX, selectY);
        rect(typeX, typeY, bigX, typeY + min);
      } 

      // Typepos is after selectpos
      else if (selectY < typeY) {
        rect(selectX, selectY, bigX, selectY + min);
        rect(paddingX, typeY, bigX, selectY + min);
        rect(typeX, typeY, paddingX, typeY + min);
      }

      // Reset the rectmode
      rectMode(CORNER);
    }

    // Display text, with syntax if loaded, or placeholder if no text
    // Set the start position to be the padding.
    float xPos = paddingX; 
    float yPos = paddingY;

    // Only display if there is text, otherwise display the placeholder text
    if (text.length() > prefix.length()) {

      // Display the text with syntax, will just display
      // in textcolor if no syntax is loaded.
      displayTextWithSyntax(xPos, yPos);
    } else {

      // Placeholder text, same color as text, but transparent.
      // Also display the placeholder after the prefix if there
      // is a prefix
      fill(textColor);
      text(prefix, xPos, yPos);
      fill(textColor, 80);
      text(placeholder, xPos + textWidth(prefix), yPos);
    }

    // Timer for the flashing line where you type
    typeTimer = (int)((typeTimer + 1) % parent.frameRate);

    // Flashing typebar thing
    if (

      // Only display if focused
      (isFocused() || horizontBar.isFocused() || verticalBar.isFocused()) && 

      // this makes it flash every second.
      typeTimer % parent.frameRate < parent.frameRate/2) {

      // Display line itself
      strokeWeight(1);
      stroke(textColor);
      line(typeX, typeY, typeX, typeY+textLeading);
    }

    // End draw, because PGraphics... 
    endDraw();

    if (scrollBottom) {
      scrollBottom = false;  
      verticalBar.setValue(verticalBar.getMaximumScroll());
    }
  }

  // This function displays parts of the text separately so it
  // can adjust the color for each part.
  private void displayTextWithSyntax(float xPos, float yPos) {

    // Go through all lines
    for (int j = 0; j < splitLines.length; j++) {

      // only draw line if it is currently on screen, this also 
      // keeps in mind the scrolled amount.
      if (yPos < getScrolledY() + height && 
        yPos > getScrolledY() - textSize) {

        // Store the line in a String for easy access
        String[] s = splitLines[j];

        // Go through all the parts in the current line
        // and display them separately in order to be able
        // to have differently colored words.
        for (int i = 0; i < s.length; i++) {

          // Like usual, store in String for easy access
          String w = s[i];

          // Calculate width of current part
          float yw = textWidth(w);

          // Get color according to syntax, from array because that is
          // faster than recalculating it every frame.
          if (j < syntaxColor.size() && i < syntaxColor.get(j).size()) {

            // Get the color from the array
            int clr = syntaxColor.get(j).get(i);

            // Use processing's red() green() and blue() because somehow it doesn't
            // work without it, since it is an int value created by processing's color().
            fill(red(clr), green(clr), blue(clr));
          }

          // Draw the part of the text at the right position
          text(w, xPos, yPos);

          // Debug show lines inbetween text parts
          //stroke(textColor);
          //strokeWeight((float)0.1);
          //line(xPos, yPos, xPos, yPos + textSize);

          // Add the width of the part to the xpos for the next part.
          xPos += yw;
        }
      }

      // next line, reset the xpos to the padding and add the
      // textleading to the ypos for the next line.
      yPos += textLeading;
      xPos = paddingX;
    }
  }


  public void display(int x, int y) {

    // Only redraw the textarea when refresh is true or when mouse
    // is over the panel. When it is in focus, refreshFrame gets
    // set to 30, and when it loses focus it counts down. Until
    // it reaches 0 it will keep redrawing to make sure everything
    // is well up to date.
    if (isFocused() || horizontBar.isFocused() || verticalBar.isFocused()) refreshFrame = 30;
    else if (refreshFrame > 0) refreshFrame--;
    if (refreshFrame > 1 || refresh || isHovering()) {
      refresh = false;
      redraw();
    }

    // Display the thing
    super.display(x, y);
  }






  /* EVENTS :
   * 
   * * * * * * * * * * * * * * * * * * * * * * * * * * */

  // Mouse Events
  public void mouseEvent(MouseEvent e) {
    super.mouseEvent(e);

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
    }
  }

  private void mousePressed(MouseEvent e) {

    // Only do stuff when the textpane is focused
    if (isFocused()) {

      // set to 0 to make type line thing visible
      typeTimer = 0;

      // This converts the mouse coords to the index in the text
      // and then sets the type pos to that index.
      typePos = getIndexFromCoords(getSubcontroller().mouseX, getSubcontroller().mouseY);

      // Press 3 times to select and entire line.
      if (e.getCount() >= 3) {
        selectPos = getIndexFromCoords(0, getSubcontroller().mouseY);
        typePos = getIndexFromCoords(1000, getSubcontroller().mouseY);
      } else 

      // Press 2 times to select a single word.
      if (e.getCount() == 2) {
        selectPos = moveLeftCtrlIndex(typePos);
        typePos = moveRightCtrlIndex(typePos-1);
      } 

      // Otherwise it will just reset any selection that was made
      // by setting the select pos to the type pos.
      else {
        selectPos = typePos;
      }

      // Also scroll to the typepos after it has been moved.
      // Which also does recalculate, so no need to set that to true 
      scrollToTypePos = true;
    }
  }

  private void mouseMoved(MouseEvent e) {
  }

  private void mouseDragged(MouseEvent e) {

    // If the panel is focused, select a piece of text. This happens
    // because the select pos gets set when you mousepress, but when
    // dragging the typepos keeps being updated to the mouse coords
    // meaning you will select the area of text from where you pressed
    // up to where you drag.
    if (isFocused()) {

      // set to 0 to make type line thing visible
      typeTimer = 0;

      // set typepos using mouseX and mouseY
      typePos = getIndexFromCoords(getSubcontroller().mouseX, getSubcontroller().mouseY);

      // Also scroll to the typepos after it has been moved.
      // Which also does recalculate, so no need to set that to true 
      scrollToTypePos = true;
    }
  }

  private void mouseReleased(MouseEvent e) {
  }

  private void mouseClicked(MouseEvent e) {
  }

  // Key Events
  public void keyEvent(KeyEvent e) {
    super.keyEvent(e);

    // Only allow keyevents if it's focused, or if anoy of the scrollbars are focused
    if (isFocused() || horizontBar.isFocused() || verticalBar.isFocused()) {
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
  }

  private void keyPressed(KeyEvent e) {

    // When a keyPress occurs, the updateSyntaxOnLine
    // moves to the line where the typepos is located
    // Because it looks better.
    updateSyntaxOnLine = typeLine;

    // Store key in var for easier access.
    int keyCode = e.getKeyCode();
    char key = e.getKey();

    if ((key == ENTER || key == RETURN) && submitable) submitted = true; 

    // This handles the arrow keys and uses them
    // to move around the typepos.
    updateTypepos(e);

    // Do not update the typeXV when moving typepos up or down a line,
    // because otherwise with empty lines it'll just be set to 0 and
    // really has no use.
    if (keyCode == UP || keyCode == DOWN) updateTypeXV = false;

    // Also scroll to the typepos after it has been moved.
    // Which also 
    scrollToTypePos = true;
  }

  private void keyTyped(KeyEvent e) {

    // Store key in var for easier access
    int keyCode = e.getKeyCode();
    char key = e.getKey();

    // Constrain the typepos to prevent exceptions
    typePos = PApplet.constrain(typePos, 0, text.length());
    selectPos = PApplet.constrain(selectPos, 0, text.length());

    // Don't do anything if the key is a CODED one
    // Else do the appropriate action.
    if (key == CODED) ; // Don't do stuff when key is coded
    else if (e.isControlDown()) ctrlActions(key);
    else if (editable) typeActions(e);

    // Also scroll to the typepos after it has been moved.
    // Which also does recalculate, so no need to set that to true 
    scrollToTypePos = true;
  }

  private void keyReleased(KeyEvent e) {
  }
}
