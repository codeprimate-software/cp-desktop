/*
 * Copyright 2011-Present Author or Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cp.desktop.awt.support;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

@SuppressWarnings("unused")
public abstract class WindowUtils {

  public static final Dimension DEFAULT_SCREEN_SIZE = new Dimension(640, 480);

  public static final Point DEFAULT_DESKTOP_LOCATION = new Point(0, 0);

  /**
   * Returns the screen location specified as a java.awt.Point object that positions the frame of the application
   * in the center of the user's desktop in a windows environment.  Note that the positions signifies the top left
   * corner of the application window.
   * @param windowSize is a Dimension object specifying the width and height of the application frame.
   * @return a Point specifying the center location in the desktop environement to position the application frame.
   * @see WindowUtils#getDialogLocation(Container, Dimension)
   * @see WindowUtils#getScreenSize()
   * @see java.awt.Window#setLocationRelativeTo
   */
  public static Point getDesktopLocation(Dimension windowSize) {

    Dimension screenSize = getScreenSize();

    return new Point((int) ((screenSize.getWidth() - windowSize.getWidth()) / 2.0),
        (int) ((screenSize.getHeight() - windowSize.getHeight()) / 2.0));
  }

  /**
   * Returns a screen location specified as a java.awt.Point object that centers a dialog box about
   * the application frame.
   * @param frame is a java.awt.Container object refering to the application frame which spawned the dialog box.
   * @param dialogSize is a Dimension object specifying the width and height of the dialog box.
   * @return a java.awt.Point object specifying the location of the upper left corner of the dialog box which
   * cernters the box about the application frame.
   * @see WindowUtils#getDesktopLocation(Dimension)
   * @see java.awt.Window#setLocationRelativeTo
   */
  public static Point getDialogLocation(Container frame, Dimension dialogSize) {

    Point frameLocation = frame.getLocationOnScreen();
    Dimension frameSize = frame.getSize();

    int xCenter = frameSize.width / 2;
    int yCenter = frameSize.height / 2;
    int xOffset = -(dialogSize.width / 2);
    int yOffset = -(dialogSize.height / 2);

    return new Point(frameLocation.x + (xCenter + xOffset), frameLocation.y + (yCenter + yOffset));
  }

  /**
   * Determines the size or resolution of the screen/desktop of the machine running this Java VM.
   * @return a Dimension object indicating the size or resolution of the user's screen/desktop of the machine
   * running this Java VM.
   */
  private static Dimension getScreenSize() {
    return Boolean.getBoolean("java.awt.headless")
      ? DEFAULT_SCREEN_SIZE
      : Toolkit.getDefaultToolkit().getScreenSize();
  }
}
