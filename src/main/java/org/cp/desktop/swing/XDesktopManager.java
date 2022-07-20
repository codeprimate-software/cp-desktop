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

import java.beans.PropertyVetoException;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.DefaultDesktopManager;
import javax.swing.JInternalFrame;

import org.cp.elements.lang.Assert;
import org.cp.elements.lang.annotation.NotNull;

public class XDesktopManager extends DefaultDesktopManager {

  private final XDesktopPane desktop;

  /**
   * Creates an instance of the XDesktopManager class to handle custom functionality and behavior of the
   * XDesktopPane class.
   * @param desktop a reference to the XDesktopPane class that this desktop manager class serves.
   */
  public XDesktopManager(@NotNull XDesktopPane desktop) {
    Assert.notNull(desktop, "XDesktopPane is required");
    this.desktop = desktop;
  }

  /**
   * Returns the XDesktopPane class governed by this desktop manager class.
   * @return the XDesktopPane class that is associated with this desktop manager.
   */
  private @NotNull XDesktopPane getDesktop() {
    return this.desktop;
  }

  /**
   * Overridden method for setting the active internal frame as the current frame of the desktop.
   * @param frame the internal frame of the desktop being activated.
   * @see XDesktopPane#setCurrentFrame
   */
  public void activateFrame(JInternalFrame frame) {
    super.activateFrame(frame);
    getDesktop().setCurrentFrame((XInternalFrame) frame);
  }

  /**
   * Restores all iconified internal frames currently open in the desktop to their non-iconified state.
   */
  public void deiconifyAll() {

    for (Iterator it = Arrays.asList(getDesktop().getAllFrames()).iterator(); it.hasNext(); ) {

      JInternalFrame internalFrame = (JInternalFrame) it.next();

      try {
        internalFrame.setIcon(false);
      }
      catch (PropertyVetoException ignore) { }
    }
  }

  /**
   * Iconifies all internal frames open on the desktop govnered by this manager.
   */
  public void iconifyAll() {

    for (Iterator it = Arrays.asList(getDesktop().getAllFrames()).iterator(); it.hasNext(); ) {

      JInternalFrame internalFrame = (JInternalFrame) it.next();

      try {
        internalFrame.setIcon(true);
      }
      catch (PropertyVetoException ignore) { }
    }
  }

  /**
   * Maximizes all internal frames open on the desktop by stretching the internal frame's width and height
   * to the largest possible extent to fill the desktop.
   */
  public void maximizeAll() {

    for (Iterator it = Arrays.asList(getDesktop().getAllFrames()).iterator(); it.hasNext(); ) {

      JInternalFrame internalFrame = (JInternalFrame) it.next();

      try {
        internalFrame.setMaximum(true);
      }
      catch (PropertyVetoException ignore) { }
    }
  }

  /**
   * Minimizes all internal frames open on the desktop by setting their width and height to the original
   * values and returng the internal frames to their original location.
   */
  public void minimizeAll() {

    for (Iterator it = Arrays.asList(getDesktop().getAllFrames()).iterator(); it.hasNext(); ) {

      JInternalFrame internalFrame = (JInternalFrame) it.next();

      try {
        internalFrame.setMaximum(false);
      }
      catch (PropertyVetoException ignore) { }
    }
  }

  /**
   * Restores all internal frames within this desktop to their natural state (not maximized or iconified).
   */
  public void restoreAll() {
    deiconifyAll();
    minimizeAll();
  }
}
