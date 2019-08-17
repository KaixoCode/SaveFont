import java.nio.file.*;
import java.io.*;
import java.util.zip.*;

// This stores the names of the found font files
String foundFonts = "";

// Controller to manage all PSObject objects
PController controller;
PInput input; // The textarea to display the log
PButton skipZip, stop, saveDirs; // The buttons at the top

// Width of button
int bA = 333;

// Booleans that turn true when pressing a button
boolean skip = false;
boolean stopS = false;

// Previous size of window, used in resizing the textarea
// so it only resizes when the screen changes size to reduce lag
int pw = 0;
int ph = 0;


void setup() {

  // Let the user select a folder to look for zips
  selectFolder("Select a folder with font zips", "zipSelected");

  // Set screen size and make it resizable
  size(1000, 800);
  surface.setResizable(true);

  // Load the saved directories from file
  String[] dirs = loadStrings(dataPath("directories.txt"));
  if (dirs!=null&&dirs.length > 0) zip = new File(dirs[0]);
  if (dirs!=null&&dirs.length > 1) dest = new File(dirs[1]);

  // Load the files that were already checked from the text file
  String[] done = loadStrings(dataPath("doneFiles.txt"));
  for (String d : done) alreadyDoneFiles += d + "\n";

  // Initialize all the PSObject objects
  controller = new PController(this);
  input = controller.createInput(PInput.TEXT, 1000, 760);
  skipZip = controller.createButton();
  stop = controller.createButton();
  saveDirs = controller.createButton();

  // Set all settings for textarea
  input.setEditable(false);
  input.setFocus(true);
  input.textFont(createFont("Consolas", 12));
  input.setBackgroundColor(0);
  input.setTextColor(0, 255, 0);
  input.setSelectColor(0, 50, 0);

  // Set all settings for the scrollbars in the textarea
  PScrollbar ver = input.getVerticalScrollbar();
  PScrollbar hor = input.getHorizontalScrollbar();
  ver.setFillColor(PScrollbar.IDLE, 0, 50, 0);
  ver.setFillColor(PScrollbar.HOVER, 0, 150, 0);
  ver.setFillColor(PScrollbar.PRESS, 0, 250, 0);
  ver.setBackgroundColor(-1, 0, 0, 0);
  hor.copyStyle(ver);

  // Set the style for the buttons
  skipZip.setSize(bA, 40);
  skipZip.setFillColor(PButton.IDLE, 0, 0, 0);
  skipZip.setFillColor(PButton.HOVER, 0, 50, 0);
  skipZip.setFillColor(PButton.PRESS, 0, 255, 0);
  skipZip.setTextColor(PButton.IDLE, 0, 255, 0);
  skipZip.setTextColor(PButton.HOVER, 0, 255, 0);
  skipZip.setTextColor(PButton.PRESS, 0, 0, 0);
  skipZip.setBorderColor(-1, 0, 255, 0);
  skipZip.setText("Try to skip this zip");
  stop.setSize(bA, 40);
  stop.copyStyle(skipZip);
  stop.setText("Stop scan/install found fonts");
  saveDirs.setSize(bA, 40);
  saveDirs.copyStyle(skipZip);
  saveDirs.setText("Set default directories");
}


// Simple exit when code is done and key is pressed
public void keyPressed() {
  if (stopS || done) exit();
}


void draw() {

  // Calculate button width using window width
  bA = (int) (width/3.0);


  // Resize everything if window changed size
  if (pw != width || ph != height) {
    pw = width;
    ph = height;
    input.setSize(width, height-40);
    skipZip.setSize(bA, 40);
    stop.setSize(bA, 40);
    saveDirs.setSize(bA, 40);
  }

  // Display all PSObject objects
  background(0);
  skipZip.display(bA*0, 0);
  stop.display(bA*1, 0);
  saveDirs.display(bA*2, 0);
  input.display(0, 40);
  input.redraw();

  // Checks for button click
  if (skipZip.isClicked()) {
    skip = true;
    
    // Adds to log
    input.addText("\nAttempting to skip zip");
  }

  // Checks for button click
  if (stop.isClicked()) {
    stopS = true;

    // Adds to log
    input.addText("\nAttempting to close zip");
    
    // If it found any fonts, install them
    if (foundFonts.length() > 0) {
      input.addText("\n\n====adding fonts====\n\n\n\n\n");
      launch(dataPath("saveFonts.bat") + " " + dest.getPath());
    } else {
      input.addText("\n\n====No new fonts found====\n\n\n\n\n");
    }
  }

  // Save directories to file
  if (saveDirs.isClicked()) {
    String[] dirs = new String[]{zip.getPath(), dest.getPath()};
    saveStrings(dataPath("directories.txt"), dirs);
    
    // Add to log
    input.addText("\n\n============\nSetting directories as default: \n  Zip path: " + zip.getPath() + "\n"
      + "  Font path: " + dest.getPath() + "\n============\n\n"
      );
  }
}

File zip;
File dest;
void zipSelected(File f) {
  if (f != null) zip = f;
  input.addText("Zip files: " + zip.getPath() + "\n");
  selectFolder("Select a folder to unzip", "fontSelected");
}

void fontSelected(File d) {
  if (d != null) dest = d;
  input.addText("Destination: " + dest.getPath() + "\n");
  thread("unZip");
}

String alreadyDoneFiles = "";
boolean done = false;


// nice and bodged method to extract fonts from zip files.
void unZip() {
  String destinationFolder = dest.getPath();

  String[] destFolderFiles = dest.list();
  String[] files = zip.list();
  int amt = 0;
  for (String filename : files) {
    if (filename.contains(".zip")) amt++;
  }
  input.addText("\nFound: " + files.length + " files in folder");
  input.addText("\nOf which: " + amt + " zip files");

  int index = 0;
  for (String filename : files) {
    if (filename.contains(".zip")) {
      index++;
      String zipFile = zip.getPath() + "/" + filename;

      input.addText("\n  "+index+"/"+amt+" Next file: " + zipFile);

      if (!alreadyDoneFiles.contains(filename)) {



        File directory = new File(destinationFolder);

        if (!directory.exists())
          directory.mkdirs();

        byte[] buffer = new byte[2048];

        try {

          FileInputStream fInput = new FileInputStream(zipFile);
          ZipInputStream zipInput = new ZipInputStream(fInput);

          ZipEntry entry = zipInput.getNextEntry();
          input.addText("\n    File: " + entry.getName());
          input.getVerticalScrollbar().setValue(input.getVerticalScrollbar().getMaximumScroll());

          while (entry != null) {
            if (skip) {
              entry = null;
              break;
            }
            if (stopS) {
              input.addText("\nStopping session");
              input.addText("\nPress any key to close...");
              return;
            }

            // Get filename
            String entryName = entry.getName();
            input.changeLastLine("\n    File: " + entryName);

            if ((entryName.toLowerCase().contains(".otf") ||
              entryName.toLowerCase().contains(".ttf") ||
              entryName.toLowerCase().contains(".fnt"))) {

              input.changeLastLine("\n      Found font file: " + entryName);

              // If file is inside folder, split out the folder
              // because we dont want folders
              String[] fn = entryName.split("/");
              if (fn.length > 1) {
                entryName = fn[fn.length-1];
              }

              // Make sure we don't unzip already unzipped files
              boolean alreadyExported = false;
              for (String s : destFolderFiles) {
                if (entryName.equals(s)) {
                  if (skip) break;

                  alreadyExported = true;
                  break;
                }
              }

              // Only export file if it's a font
              if (!alreadyExported) {

                // Get the destination file
                File file = new File(destinationFolder + File.separator + entryName);

                // Log
                input.addText("\n        Unzipping file " + entryName + " to " + file.getAbsolutePath()+"\n");

                // Output to file if not a directory
                if (!entry.isDirectory()) {

                  // Output stream stuff
                  FileOutputStream fOutput = new FileOutputStream(file);
                  int count = 0;
                  while ((count = zipInput.read(buffer)) > 0) {
                    fOutput.write(buffer, 0, count);
                    if (skip) break;
                  }
                  fOutput.close();
                  if (!foundFonts.contains(entryName))
                    foundFonts += entryName + "\n";
                }
              } else {
                input.addText("\n        File " + entryName + " already exists\n");
              }
            }

            int mi = (millis());
            if (skip) {
              entry = null;
              break;
            }
            zipInput.closeEntry();
            entry = zipInput.getNextEntry();
            if (millis()-mi > 1) println(millis()-mi);
          }

          zipInput.closeEntry();
          zipInput.close();
          fInput.close();
          if (!skip) alreadyDoneFiles+=filename + "\n";
          else {
            alreadyDoneFiles+=filename + "\n";
            input.addText("\nSkipping file");
            skip = false;
          }
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      } else {
        input.addText("\n--Already did this zip in a previous session--");
      }
    }
    saveStrings(dataPath("doneFiles.txt"), alreadyDoneFiles.split("\n"));
    saveStrings(dataPath("dir_file.txt"), foundFonts.split("\n"));
    //input.addText("\nDone.");
  }


  if (foundFonts.length() > 0) {
    input.addText("\n\n====All Done, adding fonts====\n\n\n\n\n");
    launch(dataPath("saveFonts.bat") + " " + destinationFolder);
  } else {
    input.addText("\n\n====No new fonts found====\n\n\n\n\n");
  }
  delay(100);
  done = true;
  input.addText("\nEnding session\nPress any key to close...\n\n\n\n\n");
  delay(100);
  input.getVerticalScrollbar().setValue(input.getVerticalScrollbar().getMaximumScroll());
}
