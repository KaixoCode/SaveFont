
import processing.core.*;
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 
import processing.awt.*; 
import processing.awt.PGraphicsJava2D;

import java.io.File;


public class PSyntax implements PData {
    private static String[] syntax;

    public PSyntax(String[] f) {
      syntax = f;
    }

    public PSyntax(File f) {
      syntax = PApplet.loadStrings(f);
    }

    public static String[] getSyntax() {
      return syntax;
    }

    public static int getClassColor(int c) {
      try {
        String hex = syntax[c].split("; ")[0].replaceAll("[^a-zA-Z0-9]", "");
        return PApplet.unhex(hex);
      } 
      catch( Exception e) {
        e.printStackTrace();
        return 0;
      }
    }

    public static String[] getClassContent(int c) {
      return (syntax[c].split("; "));
    }

    public static int classAmount() {
      return syntax.length;
    }
  }
