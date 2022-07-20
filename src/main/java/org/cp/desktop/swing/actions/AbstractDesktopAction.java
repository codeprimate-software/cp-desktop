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

import org.cp.elements.lang.ObjectUtils;
import org.cp.elements.lang.annotation.NotNull;
import org.cp.elements.lang.annotation.Nullable;

/**
 * Abstract base class for all {@link JDesktopPane} {@link AbstractAction Actions}.
 *
 * @author John Blum
 * @see javax.swing.AbstractAction
 * @see javax.swing.JDesktopPane
 * @see javax.swing.JInternalFrame
 */
public abstract class AbstractDesktopAction extends AbstractAction {

  private JDesktopPane desktop;

  /**
   * Constructs a new instance of {@link AbstractDesktopAction} initialized with the given,
   * required {@link JDesktopPane}.
   *
   * @param desktop {@link JDesktopPane} acted upon on by this {@link AbstractAction}; must not be {@literal null}.
   * @throws IllegalArgumentException if {@link JDesktopPane} is {@literal null}.
   * @see javax.swing.JDesktopPane
   */
  public AbstractDesktopAction(@NotNull JDesktopPane desktop) {
    setDesktop(desktop);
  }

  /**
   * Constructs a new instance of {@link AbstractDesktopAction} initialized with the given,
   * required {@link JDesktopPane}.
   *
   * @param desktop {@link JDesktopPane} acted upon on by this {@link AbstractAction}; must not be {@literal null}.
   * @param name {@link String} containing the {@literal name} given to this {@link AbstractAction}.
   * @throws IllegalArgumentException if {@link JDesktopPane} is {@literal null}.
   * @see javax.swing.JDesktopPane
   */
  public AbstractDesktopAction(@NotNull JDesktopPane desktop, @Nullable String name) {
    super(name);
    setDesktop(desktop);
  }

  /**
   * Constructs a new instance of {@link AbstractDesktopAction} initialized with the given,
   * required {@link JDesktopPane}.
   *
   * @param desktop {@link JDesktopPane} acted upon on by this {@link AbstractAction}; must not be {@literal null}.
   * @param name {@link String} containing the {@literal name} given to this {@link AbstractAction}.
   * @param icon {@link Icon} representing this {@link AbstractAction} in the GUI, such as a menu item in a menu
   * or image on a button.
   * @throws IllegalArgumentException if {@link JDesktopPane} is {@literal null}.
   * @see javax.swing.JDesktopPane
   */
  public AbstractDesktopAction(@NotNull JDesktopPane desktop, @Nullable String name, @Nullable Icon icon) {
    super(name, icon);
    setDesktop(desktop);
  }

  /**
   * Return a reference to the {@link JDesktopPane} associated with this {@link AbstractAction}.
   *
   * @return a reference to the {@link JDesktopPane} associated with this {@link AbstractAction}.
   * @see javax.swing.JDesktopPane
   */
  public @NotNull JDesktopPane getDesktop() {
    return this.desktop;
  }

  /**
   * Return a reference to the {@link JDesktopPane} associated with this {@link AbstractAction}.
   *
   * @param desktop reference to the {@link JDesktopPane} associated with this {@link AbstractAction};
   * must not be {@literal null}.
   * @throws IllegalArgumentException if {@link JDesktopPane} is {@literal null}.
   * @see javax.swing.JDesktopPane
   */
  protected void setDesktop(@NotNull JDesktopPane desktop) {
    this.desktop = ObjectUtils.requireObject(desktop, "Desktop is required");
  }

  /**
   * Restores the {@link JInternalFrame} from an iconfied or maximized state.
   *
   * @param frame {@link JInternalFrame} to restore.
   * @throws PropertyVetoException if the act of de-iconifying or minimizing the {@link JInternalFrame} is vetoed.
   */
  protected void restoreFrame(@NotNull JInternalFrame frame) throws PropertyVetoException {

    if (frame.isIcon()) {
      frame.setIcon(false);
    }

    if (frame.isMaximum()) {
      frame.setMaximum(false);
    }
  }
}
