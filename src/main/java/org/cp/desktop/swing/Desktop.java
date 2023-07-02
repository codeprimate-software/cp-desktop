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

import javax.swing.Action;

/**
 * Interface defining a Windows {@literal desktop} GUI component containing internal frames (windows).
 *
 * @author John Blum
 */
@SuppressWarnings("unused")
public interface Desktop {

  /**
   * Closes the currently visible, active internal frames (windows) on the desktop having focus.
   *
   * @see Desktop#closeAll
   */
  void close();

  /**
   * Closes all internal frames (windows) on the desktop.
   *
   * @see Desktop#close
   */
  void closeAll();

  /**
   * Restores all iconified internal frames currently open in this desktop to their non-iconified state.
   *
   * @see #iconifyAll()
   */
  void deiconifyAll();

  /**
   * Returns an {@link Action} used to organize all internal frames contained within this desktop
   * in a cascading fashion.
   *
   * @return an {@link Action} used to cascade the internal frames on the desktop.
   * @see javax.swing.Action
   */
  Action getCascadeAction();

  /**
   * Returns an {@link Action} used to organize all internal frames in the desktop by splitting all open,
   * visible internal frames horizontally or vertically.
   *
   * @param orientation {@link Integer} specifying the orientation of the split. Acceptable values for orientation
   * are {@link javax.swing.SwingUtilities#HORIZONTAL} and {@link javax.swing.SwingUtilities#VERTICAL}.
   *
   * @return {@link Action} used to split the internal frames on the desktop.
   * @see javax.swing.Action
   */
  Action getSplitAction(int orientation);

  /**
   * Returns an {@link Action} used to organize the internal frames in this desktop by tiling all open, visible
   * internal frames.
   *
   * @return an {@link Action} used to tile the internal frames in this desktop.
   * @see javax.swing.Action
   */
  Action getTileAction();

  /**
   * Iconifies all internal frames open in this desktop.
   *
   * @see #deiconifyAll()
   */
  void iconifyAll();

  /**
   * Maximizes all internal frames open in this desktop by stretching the internal frame's width and height
   * to the largest possible extent.
   *
   * @see #minimizeAll()
   */
  void maximizeAll();

  /**
   * Minimizes all internal frames open on this desktop by setting their width and height to the original
   * values and returning the internal frames to their original location.
   *
   * @see #maximizeAll()
   */
  void minimizeAll();

  /**
   * Sets the active internal frame to the next open internal frame in this desktop.
   * <p>
   * The internal frame will be made visible and have focus.
   *
   * @see #previous()
   */
  void next();

  /**
   * Adds the given internal frame to the desktop, making it visible and setting focus to the internal frame.
   *
   * @param internalFrame {@link XInternalFrame} representing the internal frame to add to this desktop.
   * @see #open(XInternalFrame, Integer)
   * @see XInternalFrame
   */
  void open(XInternalFrame internalFrame);

  /**
   * Adds the internal frame to the desktop, making it visible and setting focus to the internal frame.
   *
   * @param internalFrame {@link XInternalFrame} representing the internal frame to add to this desktop.
   * @param layer {@link Integer} specifying the layer within the container to add the specified internal frame.
   * @see XInternalFrame
   */
  void open(XInternalFrame internalFrame, Integer layer);

  /**
   * Sets the active internal frame to the previous open internal frame on this desktop.
   * <p>
   * The internal frame will be made visible and have focus.
   *
   * @see #next()
   */
  void previous();

  /**
   * Restores all internal frames in this desktop to their natural state (not maximized or iconified).
   */
  void restoreAll();

}
