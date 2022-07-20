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

import java.beans.PropertyVetoException;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import org.cp.elements.lang.Assert;

public abstract class AbstractDesktopAction extends AbstractAction {

  private JDesktopPane desktop;

  /**
   * Creates an instance of the AbtractDesktopAction class initilized with the specified desktop.
   * @param desktop the JDesktopPane object acted upon on by this Action class.
   */
  public AbstractDesktopAction(JDesktopPane desktop) {
    setDesktop(desktop);
  }

  /**
   * Creates an instance of the AbtractDesktopAction class initilized with the specified desktop and name.
   * @param desktop the JDesktopPane object acted upon by this Action class.
   * @param name a String value naming the Action and used to set the button or menu item label.
   */
  public AbstractDesktopAction(JDesktopPane desktop, String name) {
    super(name);
    setDesktop(desktop);
  }

  /**
   * Creates an instance of the AbtractDesktopAction class initilized with the specified desktop, name and icon.
   * @param desktop the JDesktopPane object acted upon by this Action class.
   * @param name a String value naming the Action and used to set the button or menu item label.
   * @param icon an Icon object specifying the button or menu item icon representing this Action.
   */
  public AbstractDesktopAction(JDesktopPane desktop, String name, Icon icon) {
    super(name, icon);
    setDesktop(desktop);
  }

  /**
   * Returns the desktop associated with this Action.
   * @return an instance of the JDesktopPane component for which this Action object acts upon.
   */
  public JDesktopPane getDesktop() {
    return this.desktop;
  }

  /**
   * Sets the desktop component object associated with this Action.
   * @param desktop the JDesktopPane instance associated with this Action.
   */
  private void setDesktop(JDesktopPane desktop) {
    Assert.notNull(desktop, "Desktop is required");
    this.desktop = desktop;
  }

  /**
   * Restores the internal frame from an iconfied or maximized state.
   * @param frame the internal frame to restore.
   * @throws PropertyVetoException if the act of deiconifying or minimizing the internal frame is vetoed.
   */
  protected void restoreFrame(JInternalFrame frame) throws PropertyVetoException {

    if (frame.isIcon()) {
      frame.setIcon(false);
    }
    if (frame.isMaximum()) {
      frame.setMaximum(false);
    }
  }
}
