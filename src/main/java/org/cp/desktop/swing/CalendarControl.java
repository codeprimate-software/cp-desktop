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
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.cp.desktop.awt.image.ImageUtils;
import org.cp.desktop.swing.model.CalendarModel;
import org.cp.desktop.swing.model.DefaultCalendarModel;
import org.cp.elements.lang.annotation.NotNull;

@SuppressWarnings("unused")
public class CalendarControl extends JPanel {

  private static final boolean LIGHTWEIGHT_POPUP_ENABLED = false;

  private static final Dimension BUTTON_SIZE = new Dimension(21, 21);

  private final CalendarModel calendarModel;

  private JButton showCalendar;

  private JCalendar calendar;

  private JDateField dateField;

  /**
   * Constructs a new {@link CalendarControl}.
   */
  public CalendarControl() {
    this.calendarModel = new DefaultCalendarModel();
    buildUI();
  }

  /**
   * The overridden setEnabled method sets the CalendarControl component to enabled with a boolean value of true and
   * disabled with a boolean value of false.
   * @param enabled is a boolean value indicating true to enable this CalendarControl component, false to disable it.
   */
  public void setEnabled(boolean enabled) {

    super.setEnabled(enabled);

    this.calendar.setEnabled(enabled);
    this.dateField.setEnabled(enabled);
    this.showCalendar.setEnabled(enabled);
  }

  /**
   * Returns the {@link CalendarModel} used by this {@link CalendarControl} to represent the date and time.
   *
   * @return the configured {@link CalendarModel} used to store the date and time for the {@link JDateField date field}
   * and {@link JCalendar calendar component}.
   * @see CalendarModel
   */
  public @NotNull CalendarModel getModel() {
    return this.calendarModel;
  }

  /**
   * Creates an AncestorListener responsible for listening to add, remove and move events from the calendar control.
   *
   * @param popupMenu the popup menu that needs to be hidden if the arrangement, presence or absense of the calendar
   * component (popup) changes.
   * @return an AncestorListener for listening to events from the calendar control.
   */
  private @NotNull AncestorListener newAncestorListener(@NotNull JPopupMenu popupMenu) {

    return new AncestorListener() {

      public void ancestorAdded(AncestorEvent event) {
        handleAncestorEvent(event);
      }

      public void ancestorRemoved(AncestorEvent event) {
        handleAncestorEvent(event);
      }

      public void ancestorMoved(AncestorEvent event) {
        handleAncestorEvent(event);
      }

      private void handleAncestorEvent(AncestorEvent event) {
        popupMenu.setVisible(false);
      }
    };
  }

  /**
   * Creates a FocusListener to listen for calendar focus lost events in order to hide the popup menu containing
   * the calendar component.
   *
   * @param popupMenu the popup menu displaying the calendar component.
   * @return a FocusListener controling the display of the popup menu when the calendar loses focus.
   */
  private @NotNull FocusListener newCalendarFocusListener(@NotNull JPopupMenu popupMenu) {

    return new FocusAdapter() {

      public void focusLost(FocusEvent event) {
        popupMenu.setVisible(false);
      }
    };
  }

  /**
   * Returns an ActionListener for the showCalendar button of the calendar control used by the user to receive the
   * event to show the calendar component.
   * @param popupMenu the popup menu that we be made visible displaying the calendar component.
   * @return an ActionListener used to handle the event of showing the calendar component.
   */
  private @NotNull ActionListener newShowCalendarActionListener(@NotNull JPopupMenu popupMenu) {

    return event -> {

      if (!popupMenu.isVisible()) {

        Dimension calendarSize = this.calendar.getPreferredSize();
        Dimension showCalendarButtonSize = this.showCalendar.getSize();

        Point showCalendarButtonScreenLocation = this.showCalendar.getLocation();

        Point popupMenuScreenLocation =
          new Point((showCalendarButtonScreenLocation.x - (calendarSize.width - showCalendarButtonSize.width)),
            (showCalendarButtonScreenLocation.y + showCalendarButtonSize.height));

        popupMenu.show(CalendarControl.this, popupMenuScreenLocation.x, popupMenuScreenLocation.y);
        this.calendar.requestFocus();
      }
      else {
        popupMenu.setVisible(false);
      }
    };
  }

  /**
   * Constructs and flows / lays out the visual UI components constituting the user interface for the calendar control.
   */
  private void buildUI() {

    setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

    this.dateField = (JDateField) add(new JDateField());
    this.dateField.setModel(getModel());

    this.calendar = new JCalendar();
    this.calendar.setModel(getModel());

    JPopupMenu popupMenu = new JPopupMenu();

    popupMenu.add(this.calendar);
    popupMenu.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    popupMenu.setLightWeightPopupEnabled(LIGHTWEIGHT_POPUP_ENABLED);
    popupMenu.pack();
    addAncestorListener(newAncestorListener(popupMenu));

    this.calendar.addFocusListener(newCalendarFocusListener(popupMenu));
    this.showCalendar = (XButton) add(new XButton(new ImageIcon(ImageUtils.getDownArrowImage(new Dimension(10, 10)))));
    this.showCalendar.setPreferredSize(BUTTON_SIZE);
    this.showCalendar.addActionListener(newShowCalendarActionListener(popupMenu));
  }
}
