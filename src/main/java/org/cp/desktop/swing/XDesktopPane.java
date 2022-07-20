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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DesktopManager;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.KeyStroke;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import org.cp.desktop.swing.actions.CascadeAction;

// TODO: Finish coding the accelerator to navigate next and previous internal frames on the desktop!
public class XDesktopPane extends JDesktopPane implements Desktop {

  private static final int FRAME_LOCATION_OFFSET = 25;
  private static final int OPENED_FRAME_CASCADE_LIMIT = 9;

  private static final Color BACKGROUND_COLOR = new Color(66, 66, 66);

  private static final String NEXT_FRAME_ACTION_KEY = "NEXT_INTERNAL_FRAME_ACTION_KEY";
  private static final String PREVIOUS_FRAME_ACTION_KEY = "PREVIOUS_INTERNAL_FRAME_ACTION_KEY";

  private int openedFrameCount = 0;

  private Action cascadeAction;
  private Action splitAction;
  private Action tileAction;

  private InternalFrameListener desktopInternalFrameListener;

  private XDesktopManager desktopManager;

  private XInternalFrame currentFrame;

  /**
   * Creates an instance of the XDesktopPane component class implementing a MDI (multiple document interface) container,
   * or virtual desktop supporting internal frames.
   */
  public XDesktopPane() {

    //installKeyboardActions();
    //putClientProperty("JDesktopPane.dragMode", "outline");
    setBackground(BACKGROUND_COLOR);
    //setDragMode(OUTLINE_DRAG_MODE);
  }

  // @deprecated
  private void installKeyboardActions() {
    final ActionMap actionMap = getActionMap();

    actionMap.put(NEXT_FRAME_ACTION_KEY, new AbstractAction() {
      public void actionPerformed(ActionEvent event) {
        System.out.println("NEXT");
        next();
      }
    });

    actionMap.put(PREVIOUS_FRAME_ACTION_KEY, new AbstractAction() {
      public void actionPerformed(ActionEvent event) {
        System.out.println("PREVIOUS");
        previous();
      }
    });

    InputMap keyMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

    keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.VK_ALT, false), NEXT_FRAME_ACTION_KEY);
    keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.VK_ALT, false), PREVIOUS_FRAME_ACTION_KEY);
  }

  /**
   * Returns an Action to organize all internal frames contained within this desktop in a cascading fashion.
   * @return an Action object to cascade the internal frames on the desktop.
   */
  public synchronized Action getCascadeAction() {

    if (cascadeAction == null) {
      cascadeAction = new CascadeAction(this);
    }

    return cascadeAction;
  }

  /**
   * Returns the current internal frame in the desktop which is the last frame to be active, have focus or most
   * recent frame to have been selected by the user.
   * @return the internal frame that is currently active in the desktop, has focus, or most recently selected by
   * the user.
   */
  public XInternalFrame getCurrentFrame() {
    return currentFrame;
  }

  /**
   * Sets the current internal frame in the desktop which is called when the frame becomes active, receives focus
   * or is selected by the user.  Newly opened frames are current.
   * @param currentFrame the internal desktop frame to be active, attain focus or be selected by the user.
   * @see XDesktopPane#open
   */
  protected void setCurrentFrame(XInternalFrame currentFrame) {
    this.currentFrame = currentFrame;
  }

  /**
   * Method to lazy initialize the DesktopInternalFrameListener class to handle events from internal frames on the
   * desktop.
   * @return an ActionListener to handle internal frame events and notify this desktop component.
   */
  protected synchronized InternalFrameListener getDesktopInternalFrameListener() {

    if (desktopInternalFrameListener == null) {
      desktopInternalFrameListener = new DesktopInternalFrameListener();
    }

    return desktopInternalFrameListener;
  }

  /**
   * Returns a custom DesktopManager implementation for the XDesktopPane component class to handle operations such
   * as minimizing, iconifying and handling the currently active internal frame.
   * @return a custom DesktopManager implementation to support the functionality and behavior of the XDesktopPane
   * class.
   */
  public synchronized DesktopManager getDesktopManager() {

    if (desktopManager == null) {
      desktopManager = new XDesktopManager(this);
    }

    return desktopManager;
  }

  /**
   * Returns a "recommended" location to place newly opened internal frames on the desktop.
   * @return a Point object specifying the new desktop location to place the next internal frame.
   */
  protected Point getRecommendedFrameLocation() {

    int multiplier = (openedFrameCount++ % OPENED_FRAME_CASCADE_LIMIT);
    int offset = (FRAME_LOCATION_OFFSET * multiplier);

    return new Point(offset, offset);
  }

  /**
   * Returns the "recommended" size of internal frames in proportion to their desktop container component.
   * @return a Dimension object specifying a recommended size for internal frames on the desktop.
   */
  public Dimension getRecommendedFrameSize() {

    Dimension desktopSize = getSize();

    int internalFrameHeight = (int) (desktopSize.getHeight() * 0.60);
    int internalFrameWidth = (int) (desktopSize.getWidth() * 0.60);

    return new Dimension(internalFrameWidth, internalFrameHeight);
  }

  /**
   * Returns an Action to organize all internal frames in the desktop by splitting all open, visible internal frames
   * horizontally or veritcally.
   * @param orientation is a integer specifying the orientation of the split.  Acceptable values for orientation are
   * SwingUtilities.HORIZONTAL and SwingUtilities.VERTICAL.
   * @return an Action object to split the internal frames on the desktop.
   */
  public synchronized Action getSplitAction(int orientation) {

    if (splitAction == null) {
      splitAction = new SplitAction(this, orientation);
    }

    return splitAction;
  }

  /**
   * Returns an Action to organize the internal frames within this desktop by tiling all open, visible internal frames.
   * @return an Action object to tile the internal frames on the desktop.
   */
  public synchronized Action getTileAction() {

    if (tileAction == null) {
      tileAction = new TileAction(this);
    }

    return tileAction;
  }

  /**
   * Closes the currently visible, selected, active internal frame on the desktop with focus.
   * @see Desktop#closeAll
   */
  public void close() {

    XInternalFrame selectedFrame = (XInternalFrame) getSelectedFrame();

    if (selectedFrame != null) {
      try {
        selectedFrame.setClosed(true);
      }
      catch (PropertyVetoException ignore) { }
    }
  }

  /**
   * Internal method for closing internal frames on the desktop by removing them from being part of the internal
   * frame chain.
   * @param internalFrame the internal frame currently being closed on the desktop.
   */
  private void close(XInternalFrame internalFrame) {

    if (getAllFrames().length > 1) {

      XInternalFrame nextFrame = internalFrame.getNext();
      XInternalFrame previousFrame = internalFrame.getPrevious();

      previousFrame.setNext(nextFrame);
      nextFrame.setPrevious(previousFrame);
      nextFrame.show();
      setCurrentFrame(nextFrame);
    }
    else {
      setCurrentFrame(null);
    }

    internalFrame.removeInternalFrameListener(getDesktopInternalFrameListener());
    internalFrame.setVisible(false);
  }

  /**
   * Closes all internal frames on the desktop.
   * @see Desktop#close
   */
  public void closeAll() {

    for (Iterator it = Arrays.asList(getAllFrames()).iterator(); it.hasNext();) {
      final XInternalFrame internalFrame = (XInternalFrame) it.next();
      try {
        internalFrame.setClosed(true);
      }
      catch (PropertyVetoException ignore) { }
    }
  }

  /**
   * Restores all iconified internal frames currently open in this desktop to their non-iconified state.
   * @see XDesktopManager#deiconifyAll
   */
  public void deiconifyAll() {
    ((XDesktopManager) getDesktopManager()).deiconifyAll();
  }

  /**
   * Iconifies all internal frames open on this desktop.
   * @see XDesktopManager#iconifyAll
   */
  public void iconifyAll() {
    ((XDesktopManager) getDesktopManager()).iconifyAll();
  }

  /**
   * Maximizes all internal frames open on this desktop by stretching the internal frame's width and height
   * to the largest possible extent to fill the desktop.
   * @see XDesktopManager#maximizeAll
   */
  public void maximizeAll() {
    ((XDesktopManager) getDesktopManager()).maximizeAll();
  }

  /**
   * Minimizes all internal frames open on this desktop by setting their width and height to the original
   * values and returng the internal frames to their original location.
   * @see XDesktopManager#minimizeAll
   */
  public void minimizeAll() {
    ((XDesktopManager) getDesktopManager()).minimizeAll();
  }

  /**
   * Sets the active internal frame to the next open internal frame on this desktop.  The internal frame will be
   * made visible and have focus.
   */
  public void next() {

    XInternalFrame selectedFrame = (XInternalFrame) getSelectedFrame();

    if (selectedFrame != null) {
      try {
        selectedFrame.getNext().setSelected(true);
      }
      catch (PropertyVetoException ignore) { }
    }
  }

  /**
   * Adds the internal frame to the desktop, making it the currently active, visible internal frame with focus.
   * @param internalFrame the XInternalFrame object representing the internal frame to add to this desktop.
   */
  public void open(XInternalFrame internalFrame) {
    open(internalFrame, DEFAULT_LAYER);
  }

  /**
   * Adds the internal frame to the desktop, making it the currently active, visible internal frame with focus.
   * @param internalFrame the XInternalFrame object representing the internal frame to add to this desktop.
   * @param layer an Integer value specifying the layer within the container to add the specified internal frame.
   */
  public void open(XInternalFrame internalFrame, Integer layer) {

    if (getAllFrames().length > 0) {

      XInternalFrame selectedFrame = (XInternalFrame) getSelectedFrame();
      selectedFrame = selectedFrame != null ? selectedFrame : (XInternalFrame) getAllFrames()[0];
      final XInternalFrame nextFrame = selectedFrame.getNext();
      selectedFrame.setNext(internalFrame);
      nextFrame.setPrevious(internalFrame);
      internalFrame.setNextPrevious(nextFrame, selectedFrame);
    }
    else {
      internalFrame.setNextPrevious(internalFrame, internalFrame);
    }

    Point frameLocation = getRecommendedFrameLocation();

    Dimension frameSize = getRecommendedFrameSize();

    internalFrame.setBounds(frameLocation.x, frameLocation.y, frameSize.width, frameSize.height);
    internalFrame.addInternalFrameListener(getDesktopInternalFrameListener());
    add(internalFrame, layer);
    internalFrame.show();
  }

  /**
   * Sets the active internal frame to the previous open internal frame on this desktop.  The internal frame will be
   * made visible and have focus.
   */
  public void previous() {

    XInternalFrame selectedFrame = (XInternalFrame) getSelectedFrame();

    if (selectedFrame != null) {
      try {
        selectedFrame.getPrevious().setSelected(true);
      }
      catch (PropertyVetoException ignore) { }
    }
  }

  /**
   * Restores all internal frames within this desktop to their natural state (not maximized or iconified).
   */
  public void restoreAll() {
    ((XDesktopManager) getDesktopManager()).restoreAll();
  }

  /**
   * The DesktopInternalFrameListener class handles internal frame events and notifies this desktop component.
   */
  private class DesktopInternalFrameListener extends InternalFrameAdapter {

    public void internalFrameClosing(InternalFrameEvent event) {
      close((XInternalFrame) event.getInternalFrame());
    }
  }
}
