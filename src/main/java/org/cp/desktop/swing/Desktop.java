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

@SuppressWarnings("unused")
public interface Desktop {

  /**
   * Closes the currently visible, active internal frame on the desktop that has focus.
   * @see Desktop#closeAll
   */
  void close();

  /**
   * Closes all internal frames on the desktop.
   * @see Desktop#close
   */
  void closeAll();

  /**
   * Restores all iconified internal frames currently open in this desktop to their non-iconified state.
   */
  void deiconifyAll();

  /**
   * Returns an Action to organize all internal frames contained within this desktop in a cascading fashion.
   * @return an Action object to cascade the internal frames on the desktop.
   */
  Action getCascadeAction();

  /**
   * Returns an Action to organize all internal frames in the desktop by splitting all open, visible internal frames
   * horizontally or veritcally.
   * @param orientation is a integer specifying the orientation of the split.  Acceptable values for orientation are
   * SwingUtilities.HORIZONTAL and SwingUtilities.VERTICAL.
   * @return an Action object to split the internal frames on the desktop.
   */
  Action getSplitAction(int orientation);

  /**
   * Returns an Action to organize the internal frames within this desktop by tiling all open, visible internal frames.
   * @return an Action object to tile the internal frames on the desktop.
   */
  Action getTileAction();

  /**
   * Iconifies all internal frames open on this desktop.
   */
  void iconifyAll();

  /**
   * Maximizes all internal frames open on this desktop by stretching the internal frame's width and height
   * to the largest possible extent.
   */
  void maximizeAll();

  /**
   * Minimizes all internal frames open on this desktop by setting their width and height to the original
   * values and returng the internal frames to their original location.
   */
  void minimizeAll();

  /**
   * Sets the active internal frame to the next open internal frame on this desktop.  The internal frame will be
   * made visible and have focus.
   */
  void next();

  /**
   * Adds the internal frame to the desktop, making it visible and setting focus to the internal frame.
   * @param internalFrame the XInternalFrame object representing the internal frame to add to this desktop.
   */
  void open(XInternalFrame internalFrame);

  /**
   * Adds the internal frame to the desktop, making it visible and setting focus to the internal frame.
   * @param internalFrame the XInternalFrame object representing the internal frame to add to this desktop.
   * @param layer an Integer value specifying the layer within the container to add the specified internal frame.
   */
  void open(XInternalFrame internalFrame, Integer layer);

  /**
   * Sets the active internal frame to the previous open internal frame on this desktop.  The internal frame will be
   * made visible and have focus.
   */
  void previous();

  /**
   * Restores all internal frames within this desktop to their natural state (not maximized or iconified).
   */
  void restoreAll();

}
