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

import org.cp.desktop.swing.XDesktopPane;
import org.cp.elements.util.ArrayUtils;

@SuppressWarnings("unused")
public class CascadeAction extends AbstractDesktopAction {

  private static final int DEFAULT_COUNT = 10;

  /**
   * Creates an instance of the CascadeAction class initilized with the specified desktop.
   * @param desktop the JDesktopPane object acted upon by this Action class.
   */
  public CascadeAction(JDesktopPane desktop) {
    super(desktop);
  }

  /**
   * Creates an instance of the CascadeAction class initilized with the specified desktop and name.
   * @param desktop the JDesktopPane object acted upon by this Action class.
   * @param name a String value naming the Action and used to set the button or menu item label.
   */
  public CascadeAction(JDesktopPane desktop, String name) {
    super(desktop, name);
  }

  /**
   * Creates an instance of the CascadeAction class initilized with the specified desktop, name and icon.
   * @param desktop the JDesktopPane object acted upon by this Action class.
   * @param name a String value naming the Action and used to set the button or menu item label.
   * @param icon an Icon object specifying the button or menu item icon representing this Action.
   */
  public CascadeAction(JDesktopPane desktop, String name, Icon icon) {
    super(desktop, name, icon);
  }

  /**
   * Method called to carry out the action of arranging the internal frames in the desktop in a cascading fashion.
   * @param event the ActionEvent capturing the event details of the cascade operation.
   */
  public void actionPerformed(ActionEvent event) {

    JInternalFrame[] internalFrames = getDesktop().getAllFrames();

    if (ArrayUtils.isNotEmpty(internalFrames)) {

      int offset = getOffset(internalFrames[0]);

      Dimension recommendedSize = getRecommendedSize(getDesktop());

      int x = 0;
      int y = 0;

      for (int index = 0, count = 1; index < internalFrames.length; index++, count++) {

        JInternalFrame frame = internalFrames[index];

        if (!frame.isClosed() && frame.isVisible()) {
          try {
            restoreFrame(frame);
            frame.reshape(x, y, recommendedSize.width, recommendedSize.height);
            //getDesktop().getDesktopManager().resizeFrame(frame, x, y, recommendedSize.width, recommendedSize.height);
            frame.moveToFront();
            x += offset;
            y += offset;

            if ((y + recommendedSize.getHeight()) > getDesktop().getHeight() || count > DEFAULT_COUNT) {
              count = 1;
              y = 0;
            }

            if ((x + recommendedSize.getWidth()) > getDesktop().getWidth()) {
              x = 0;
              y = 0;
            }
          }
          catch (PropertyVetoException ignore) { }
        }

        try {
          internalFrames[internalFrames.length - 1].setSelected(true);
        }
        catch (PropertyVetoException ignore) { }
      }
    }
  }

  /**
   * Determines the offset used to position internal frames in a cascading fashion (over right and down).
   * @param frame the internal frame used as a baiss for determining the offset.
   * @return an interger value specifying the number of pixels to offset the location of internal frames
   * during the cascade operation.
   */
  private int getOffset(JInternalFrame frame) {

    Dimension frameSize = frame.getSize();
    Dimension frameContentPaneSize = frame.getContentPane().getSize();

    return (int) (frameSize.getHeight() - frameContentPaneSize.getHeight());
  }

  /**
   * Returns the "recommended" size of an internal frame when arranging all internal frames in the desktop in a
   * cascading fashion.
   * @param desktop the desktop component used in factoring the size of the internal frames when arranging them
   * in the cascading pattern.
   * @return a Dimension object specifying the internal frame size when arranging the frames in a cascading fashion.
   */
  private Dimension getRecommendedSize(JDesktopPane desktop) {

    if (desktop instanceof XDesktopPane) {
      return ((XDesktopPane) desktop).getRecommendedFrameSize();
    }
    else {
      Dimension desktopSize = desktop.getSize();

      int frameHeight = (int) (desktopSize.getHeight() * 0.60);
      int frameWidth = (int) (desktopSize.getWidth() * 0.60);

      return new Dimension(frameWidth, frameHeight);
    }
  }
}
