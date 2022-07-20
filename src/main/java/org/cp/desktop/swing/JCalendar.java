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
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import org.cp.desktop.swing.model.CalendarModel;
import org.cp.desktop.swing.model.DefaultCalendarModel;
import org.cp.desktop.swing.plaf.CalendarUI;
import org.cp.desktop.swing.plaf.basic.BasicCalendarUI;
import org.cp.elements.lang.Assert;

public final class JCalendar extends JComponent {

  private static final boolean DEFAULT_DOUBLE_BUFFERED = true;
  private static final boolean DEFAULT_FOCUSABLE = true;
  private static final boolean DEFAULT_OPAQUE = true;

  private static final Color BACKGROUND_COLOR = Color.white;
  private static final Color BORDER_COLOR = new Color(56, 73, 101);

  private static final String uiClassID = "CalendarUI";

  private CalendarModel model;

  private PropertyChangeListener modelPropertyChangeListener;

  /**
   * Creates an instance of the JCalendar UI Swing component class initialized with the current data and time.
   */
  public JCalendar() {
    this(Calendar.getInstance());
  }

  /**
   * Creates an instance of the JCalendar UI Swing component class initialized to the current date and time.
   * @param calendar a Calendar object specifying the date and time used to initialize this calendar widget.
   */
  public JCalendar(final Calendar calendar) {
    setBackground(BACKGROUND_COLOR);
    setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 2));
    setDoubleBuffered(DEFAULT_DOUBLE_BUFFERED);
    setFocusable(DEFAULT_FOCUSABLE);
    setModel(new DefaultCalendarModel(calendar));
    setOpaque(DEFAULT_OPAQUE);
    updateUI();
  }

  /**
   * Returns a Calendar object constituting the value of the calendar property for this calendar UI component.
   * @return a Calendar object signifying the current date and time of the calendar UI component.
   */
  public Calendar getCalendar() {
    return getModel().getCalendar();
  }

  /**
   * Sets the specified date and time as a Calendar object for the calendar UI component.
   * @param calendar the Calendar object used to set the calendar property value of the calendar UI component.
   */
  public void setCalendar(Calendar calendar) {
    getModel().setCalendar(calendar);
  }

  /**
   * Returns an instance of the CalendarModel used to represent the date and time data for this calendar UI
   * component.
   * @return an instance of the CalendarModel constituting the model for this calendar UI component used to
   * represent date and time values.
   */
  public CalendarModel getModel() {
    return model;
  }

  /**
   * Sets the specified model for this calendar UI component.
   * @param model the CalendarModel object used to model the date and time values for this calendar UI component.
   */
  public void setModel(CalendarModel model) {

    Assert.notNull(model, "CalendarModel is required");

    if (Objects.nonNull(this.model)) {
      this.model.unregister(getModelPropertyChangeListener());
    }

    CalendarModel oldModel = this.model;

    this.model = model;
    this.model.register("calendar", getModelPropertyChangeListener());
    firePropertyChange("model", oldModel, this.model);

    if (!this.model.equals(oldModel) && isVisible()) {
      revalidate();
      repaint();
    }
  }

  /**
   * Returns the PropertyChangeListener used to listen for property change events on the calendar UI component's
   * model.
   * @return a PropertyChangeListener for listening to property changes on the calendar's model.
   */
  private PropertyChangeListener getModelPropertyChangeListener() {

    if (modelPropertyChangeListener == null) {
      modelPropertyChangeListener = event -> {
        if ("calendar".equals(event.getPropertyName())) {
          repaint();
          firePropertyChange(event.getPropertyName(), event.getOldValue(), event.getNewValue());
        }
      };
    }

    return modelPropertyChangeListener;
  }

  /**
   * Returns an instance of the CalendarUI class responsible for the calendar's user interface in the
   * specified look and feel.
   * @return a CalendarUI class handling the details of the calendar's user interface.
   */
  public CalendarUI getUI() {
    return (CalendarUI) ui;
  }

  /**
   * Sets the specified UI class responsible for handling the details of the calendar's user interface.
   * @param ui the CalendarUI class responsible for the calendar's user interface.
   */
  public void setUI(final CalendarUI ui) {
    super.setUI(ui);
  }

  /**
   * The UI class ID for the calendar's user interface class.
   * @return a String value indicating the user interface class ID.
   */
  public String getUIClassID() {
    return uiClassID;
  }

  /**
   * Called by the Swing framework for updating the calendar's user interface.
   */
  public void updateUI() {
    setUI(new BasicCalendarUI(this));
  }
}
