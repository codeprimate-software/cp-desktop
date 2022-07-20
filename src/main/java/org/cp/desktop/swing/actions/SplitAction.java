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
package org.cp.desktop.swing.actions;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;

import javax.swing.Icon;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.SwingConstants;

import org.cp.elements.util.ArrayUtils;

public class SplitAction extends AbstractDesktopAction {

  private int orientation;

  /**
   * Creates an instance of the SplitAction class initilized with the specified desktop.
   * @param desktop the JDesktopPane object acted upon by this Action class.
   * @param orientation the orientation of the split, either horizontal or vertical.
   */
  public SplitAction(JDesktopPane desktop, int orientation) {
    super(desktop);
    setOrientation(orientation);
  }

  /**
   * Creates an instance of the SplitAction class initilized with the specified desktop and name.
   * @param desktop the JDesktopPane object acted upon by this Action class.
   * @param orientation the orientation of the split, either horizontal or vertical.
   * @param name a String value naming the Action and used to set the button or menu item label.
   */
  public SplitAction(JDesktopPane desktop, int orientation, String name) {
    super(desktop, name);
    setOrientation(orientation);
  }

  /**
   * Creates an instance of the SplitAction class initilized with the specified desktop, name and icon.
   * @param desktop the JDesktopPane object acted upon by this Action class.
   * @param orientation the orientation of the split, either horizontal or vertical.
   * @param name a String value naming the Action and used to set the button or menu item label.
   * @param icon an Icon object specifying the button or menu item icon representing this Action.
   */
  public SplitAction(JDesktopPane desktop, int orientation, String name, Icon icon) {
    super(desktop, name, icon);
    setOrientation(orientation);
  }

  /**
   * Method called to carry out the action of splitting the internal frames in the desktop.
   * @param event the ActionEvent capturing the event details of the split operation.
   */
  public void actionPerformed(ActionEvent event) {

    JInternalFrame[] internalFrames = getDesktop().getAllFrames();

    if (ArrayUtils.isNotEmpty(internalFrames)) {

      Dimension frameSize = getFrameSize(getDesktop());

      int x = 0;
      int y = 0;

      for (JInternalFrame frame : internalFrames) {
        if (!frame.isClosed() && frame.isVisible()) {
          try {
            restoreFrame(frame);
            frame.reshape(x, y, frameSize.width, frameSize.height);
            //getDesktop().getDesktopManager().resizeFrame(frame, x, y, frameSize.width, frameSize.height);

            if (getOrientation() == SwingConstants.HORIZONTAL) {
              y += frameSize.height;
            }
            else {
              x += frameSize.width;
            }
          }
          catch (PropertyVetoException ignore) {
          }
        }
      }
    }
  }

  /**
   * Determines size of an internal frame when splitting all internal frames in the desktop.
   * @param desktop the desktop component used in factoring the size of the internal frames when splitting them
   * to fill the desktop.
   * @return a Dimension object specifying the internal frame size when splitting them across the desktop.
   */
  private Dimension getFrameSize(JDesktopPane desktop) {

    int frameCount = desktop.getAllFrames().length;

    Dimension desktopSize = desktop.getSize();

    int width;
    int height;

    if (getOrientation() == SwingConstants.HORIZONTAL) {
      width = desktopSize.width;
      height = (int) (desktopSize.getHeight() / frameCount);
    }
    else {
      height = desktopSize.height;
      width = (int) (desktopSize.getWidth() / frameCount);
    }

    return new Dimension(width, height);
  }

  /**
   * Returns the orientation used to split the internal frames across the desktop.
   * @return a integer value specifying either the HORIZONTAL or VERTICAL split.
   */
  public int getOrientation() {
    return orientation;
  }

  /**
   * Sets the orientation used to split internal frames across the desktop.
   * @param orientation an integer value specifying either a HORIZONTAL or VERTICAL split.
   * @throws IllegalArgumentException if the value of the orientation is invalid.  The value for orientation must
   * be one of SwingConstants.HORIZONTAL or SwingConstants.VERTICAL.
   */
  private void setOrientation(int orientation) {

    switch (orientation) {
      case SwingConstants.HORIZONTAL:
      case SwingConstants.VERTICAL:
        this.orientation = orientation;
        break;
      default:
        throw new IllegalArgumentException("The orientation (" + orientation + ") is invalid!");
    }
  }
}
