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
package org.cp.desktop.swing;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;

import javax.swing.Icon;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import org.cp.desktop.swing.actions.AbstractDesktopAction;
import org.cp.elements.util.ArrayUtils;

@SuppressWarnings("unused")
public final class TileAction extends AbstractDesktopAction {

  /**
   * Creates an instance of the TileAction class initilized with the specified desktop.
   * @param desktop the JDesktopPane object acted upon by this Action class.
   */
  public TileAction(JDesktopPane desktop) {
    super(desktop);
  }

  /**
   * Creates an instance of the TileAction class initilized with the specified desktop and name.
   * @param desktop the JDesktopPane object acted upon by this Action class.
   * @param name a String value naming the Action and used to set the button or menu item label.
   */
  public TileAction(JDesktopPane desktop, String name) {
    super(desktop, name);
  }

  /**
   * Creates an instance of the TileAction class initilized with the specified desktop, name and icon.
   * @param desktop the JDesktopPane object acted upon by this Action class.
   * @param name a String value naming the Action and used to set the button or menu item label.
   * @param icon an Icon object specifying the button or menu item icon representing this Action.
   */
  public TileAction(JDesktopPane desktop, String name, Icon icon) {
    super(desktop, name, icon);
  }

  /**
   * Method called to carry out the action of tiling the internal frames in the desktop.
   * @param event the ActionEvent capturing the event details of the tile operation.
   */
  public void actionPerformed(final ActionEvent event) {

    JInternalFrame[] internalFrames = getDesktop().getAllFrames();

    if (ArrayUtils.isNotEmpty(internalFrames)) {

      int frameCount = internalFrames.length;
      int cols = (int) Math.sqrt(frameCount);
      int rows = (frameCount / cols);

      if ((cols * rows) < frameCount) {
        cols++;
        if ((cols * rows) < frameCount) {
          rows++;
        }
      }

      Dimension desktopSize = getDesktop().getSize();

      int frameWidth = (int) (desktopSize.getWidth() / cols);
      int frameHeight = (int) (desktopSize.getHeight() / rows);
      int x = 0;
      int y = 0;

      for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
        for (int colIndex = 0; colIndex < cols && (rowIndex * cols + colIndex) < internalFrames.length; colIndex++) {

          JInternalFrame frame = internalFrames[rowIndex * cols + colIndex];

          if (!frame.isClosed() && frame.isVisible()) {
            try {
              restoreFrame(frame);
              frame.reshape(x, y, frameWidth, frameHeight);
              //getDesktop().getDesktopManager().resizeFrame(frame, x, y, frameWidth, frameHeight);
              x += frameWidth;
            }
            catch (PropertyVetoException ignore) { }
          }
        }

        y += frameHeight;
        x = 0;
      }
    }
  }
}
