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
package org.cp.desktop.swing.plaf.basic;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.function.BiConsumer;

import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

import org.cp.desktop.swing.JCalendar;
import org.cp.elements.lang.Assert;
import org.cp.elements.lang.annotation.NotNull;
import org.cp.elements.lang.annotation.NullSafe;

@SuppressWarnings("unused")
public class BasicCalendarListener extends MouseInputAdapter implements PropertyChangeListener {

  private boolean repaint;

  private final EventListenerList mouseListenerList = new EventListenerList();

  private final JCalendar calendar;

  /**
   * Creates an instance of the BasicCalendarListener class initialized to the specified JCalendar Swing UI component.
   * The listener is responsible for receiving user input fields in the form of mouse or even keyboard events
   * transforming the user events into actions associated with the UI of the calendar component.
   * @param calendar the JCalendar Swing UI component generating input events for this listener to process.
   */
  public BasicCalendarListener(@NotNull JCalendar calendar) {
    Assert.notNull(calendar, "The JCalendar observed by the BasicCalendarListener is required");
    this.calendar = calendar;
  }

  /**
   * Returns the JCalendar Swing UI component registered with this listener to receive input events.
   * @return the JCalenar Swing UI component that this listener registered with to receive input events.
   */
  private @NotNull JCalendar getCalendar() {
    return this.calendar;
  }

  /**
   * Returns a boolean value of the repaint property, which indicates whether the calendar UI needs to be refreshed
   * due to a user input event.  Note, the act of reading the repaint property value is a destructive read, resetting
   * it's value to false.
   * @return a boolean value indicating the current state of the repaint property value.
   */
  private boolean isRepaint() {
    boolean oldRepaint = this.repaint;
    setRepaint(false);
    return oldRepaint;
  }

  /**
   * Sets the value fo the repaint property value to indicate whether a UI refresh operation on the calendar component
   * should occur.
   * @param repaint a boolean value indicating whether the calendar component UI should be refreshed.
   */
  private void setRepaint(boolean repaint) {
    this.repaint = repaint;
  }

  /**
   * Handles all mouse input events from the user in a standard way.
   * @param event the mouse event capturing the user's input.
   */
  @SuppressWarnings("unused")
  private void handleMouseEvent(MouseEvent event) {

    if (isRepaint()) {
      getCalendar().repaint();
    }
  }

  /**
   * Indicates that the user clicked the mouse over the calendar UI component.
   * @param event the MouseEvent capturing the user's input.
   */
  public void mouseClicked(MouseEvent event) {
    processMouseEvent(event, MouseInputListener::mouseClicked);
  }

  /**
   * Indicates that the user pressed and held the mouse button while dragging the mouse over the calendar UI component.
   * @param event the MouseEvent capturing the user's input.
   */
  public void mouseDragged(MouseEvent event) {
    processMouseEvent(event, MouseInputListener::mouseDragged);
  }

  /**
   * Indicates that the user moved the mouse from outside the calendar UI component to some position over
   * the calendar UI component.
   * @param event the MouseEvent capturing the user's input.
   */
  public void mouseEntered(MouseEvent event) {
    processMouseEvent(event, MouseInputListener::mouseEntered);
  }

  /**
   * Indicates that the user moved the mouse cursor from over the calendar UI component to outside the UI components
   * bounds.
   * @param event the MouseEvent capturing the user's input.
   */
  public void mouseExited(MouseEvent event) {
    processMouseEvent(event, MouseInputListener::mouseExited);
  }

  /**
   * Indicates that the user moved the mouse cursor over the calendar UI component.
   * @param event the MouseEvent capturing the user's input.
   */
  public void mouseMoved(MouseEvent event) {
    processMouseEvent(event, MouseInputListener::mouseMoved);
  }

  /**
   * Indicates that the user pressed a mouse button while in the calendar UI component's visable area.
   * @param event the MouseEvent capturing the user's input.
   */
  public void mousePressed(MouseEvent event) {
    processMouseEvent(event, MouseListener::mousePressed);
  }

  private void processMouseEvent(MouseEvent event, BiConsumer<MouseInputListener, MouseEvent> mouseListenerConsumer) {

    SwingUtilities.invokeLater(() -> {

      for (MouseInputListener mouseListener : this.mouseListenerList.getListeners(MouseInputListener.class)) {
        mouseListenerConsumer.accept(mouseListener, event);
      }

      handleMouseEvent(event);
    });
  }

  /**
   * Indicates that the user released the mouse button while in the calendar UI component's visible area.
   * @param event the MouseEvent capturing the user's input.
   */
  public void mouseReleased(MouseEvent event) {

    SwingUtilities.invokeLater(() -> {
      for (MouseInputListener mouseListener : this.mouseListenerList.getListeners(MouseInputListener.class)) {
        mouseListener.mouseReleased(event);
      }
      handleMouseEvent(event);
    });
  }

  /**
   * Receives property change events from the UI model components in the calendar's UI class.
   * @param event the PropertyChangeEvent object capturing the details of the property change.
   */
  public void propertyChange(PropertyChangeEvent event) {
    setRepaint(true);
  }

  /**
   * Registers a MouseListener for forwarding mouse input events to from this listener.
   * @param mouseListener the MouseListener object being registered with this listener.
   */
  @NullSafe
  public void register(@NotNull MouseInputListener mouseListener) {

    if (mouseListener != null) {
      this.mouseListenerList.add(MouseInputListener.class, mouseListener);
    }
  }

  /**
   * Removes a register MouseListener for receiving forwarded mouse events from this calendar UI listener.
   * @param mouseListener the MouseListener which was previously registered with this listener to receive
   * forward mouse events from the calendar component.
   */
  @NullSafe
  public void unregister(@NotNull MouseInputListener mouseListener) {

    if (mouseListener != null) {
      this.mouseListenerList.remove(MouseInputListener.class, mouseListener);
    }
  }
}
