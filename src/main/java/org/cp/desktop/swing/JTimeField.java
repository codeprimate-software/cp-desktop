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

import static org.cp.elements.lang.RuntimeExceptionsFactory.newIllegalArgumentException;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JTextField;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.cp.desktop.awt.event.KeyEventUtils;
import org.cp.desktop.swing.model.DefaultTimeModel;
import org.cp.desktop.swing.model.TimeModel;
import org.cp.desktop.util.Strings;
import org.cp.elements.lang.ObjectUtils;
import org.cp.elements.lang.annotation.NotNull;
import org.cp.elements.lang.annotation.Nullable;
import org.cp.elements.time.DateTimeUtils;

@SuppressWarnings("unused")
public class JTimeField extends JTextField implements PropertyChangeListener {

  private static final DateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a");

  private static final Font MONOSPACED = new Font("Monospaced", Font.PLAIN, 12);

  private static final Toolkit TOOLKIT = Toolkit.getDefaultToolkit();

  private TimeModel timeModel;

  private SelectionModel selectionModel;

  /**
   * Creates an instance of the JTimeField component class.
   */
  public JTimeField() {
    this(Calendar.getInstance());
  }

  /**
   * Creates an instance of the JTimeField component class initialized to the specified time represented with a
   * Calendar object.
   */
  public JTimeField(Calendar time) {

    super(8);

    addKeyListener(new TimeFieldKeyListener());
    addMouseInputListener(new TimeFieldMouseInputListener());

    setModel(new DefaultTimeModel(time));
    setFont(MONOSPACED);
    setSelectionModel(new TimeFieldSelectionModel());

    Date date = time != null ? time.getTime() : Calendar.getInstance().getTime();

    String text = TIME_FORMAT.format(date);

    super.setText(text);
  }

  /**
   * Creates an instance of the JTimeField class initialized to the specified String value of time.  The format
   * of the String value is 'hh:mm a'.
   * @param timeValue a String value representing the time to initialize the JTimeField component.
   * @throws ParseException if the format to the String time value is not valid.
   */
  public JTimeField(@Nullable String timeValue) throws ParseException {
    this(Strings.hasText(timeValue) ? DateTimeUtils.create(TIME_FORMAT.parse(timeValue).getTime()) : null);
  }

  /**
   * Factory method to get an instance of the JTimeField component class.  This method specifically handles
   * the ParseException and throws an IllegalArgumentException instead.
   * @param timeValue a String value representing the time to initialize the JTimeField component.
   * @return an instance of the JTimeField component class if property constructed.
   * @throws IllegalArgumentException if the timeValue String is an invalid time.
   */
  public static JTimeField getTimeField(@NotNull String timeValue) {

    try {
      return new JTimeField(timeValue);
    }
    catch (ParseException cause) {
      throw newIllegalArgumentException(cause, "Time [%s] is not valid", timeValue);
    }
  }

  /**
   * Adds the specified MouseInputListener object as both the mouse listener and mouse motion listener.
   * @param listener the javax.swing.event.MouseInputListener to be registered with this time field component
   * as the mouse listener and mouse motion listener.
   */
  private void addMouseInputListener(MouseInputListener listener) {
    addMouseListener(listener);
    addMouseMotionListener(listener);
  }

  /**
   * Creates the default implementation of the model to be used at construction if one isn't explicitly given.
   * Returns an instance of the TimeDocument.
   * @return a javax.swing.text.Document object representing the default model implementation for the time field
   * text component.
   */
  protected Document createDefaultModel() {
    return new TimeDocument();
  }

  /**
   * Receives notification of the property change events from the DateFieldModel when the time value for this
   * component changes.
   * @param event the PropertyChangeEvent tracking the time changes.
   */
  public void propertyChange(@NotNull PropertyChangeEvent event) {
    Calendar time = (Calendar) event.getNewValue();
    super.setText(TIME_FORMAT.format(time.getTime()));
  }

  public Calendar getCalendar() {
    return getModel().getCalendar();
  }

  public void setCalendar(Calendar time) {
    getModel().setCalendar(time);
  }

  public TimeModel getModel() {
    return this.timeModel;
  }

  public void setModel(TimeModel timeModel) {
    this.timeModel = ObjectUtils.requireObject(timeModel, "TimeModel is required");
    this.timeModel.register(this);
  }

  public SelectionModel getSelectionModel() {
    return this.selectionModel;
  }

  public void setSelectionModel(SelectionModel selectionModel) {
    this.selectionModel = selectionModel != null ? selectionModel : new TimeFieldSelectionModel();
  }

  public void setText(String text) {

    try {
      getModel().setTime(Strings.hasText(text) ? DateTimeUtils.create(TIME_FORMAT.parse(text).getTime())
        : Calendar.getInstance());
    }
    catch (ParseException cause) {
      throw newIllegalArgumentException(cause, "The text [%s] is not a valid time format;"
        + " please specify the time using the following format [%s]", text, TIME_FORMAT);
    }
  }

  public interface SelectionModel {

    boolean isAmPmSelected();

    TimeField getSelectedTimeField();

    void setSelectedTimeField(TimeField selectedTimeField);

    void selectAmPm();

    void selectHour();

    void selectMinute();

    void selectNext();

    void selectPrevious();

    void selectSecond();

  }

  /**
   * The TimeDocument class is the default document used by the JTimeField component class  to represent time
   * in a standard JTextField component.
   */
  private final class TimeDocument extends PlainDocument {

    private static final char A_CHAR = 'a';
    private static final char COLAN_CHAR = ':';
    private static final char M_CHAR = 'm';
    private static final char P_CHAR = 'p';
    private static final char SPACE_CHAR = ' ';

    private static final int AM_PM_POSITION = 6;
    private static final int HOUR_MINUTE_COLAN_POSITION = 2;
    private static final int SPACE_POSITION = 5;

    public void insertString(int offset, String value, AttributeSet attributeSet) throws BadLocationException {

      if (!isValidTime(offset, value)) {
        TOOLKIT.beep();
      }
      else {
        super.insertString(offset, value, attributeSet);
      }

      getSelectionModel().setSelectedTimeField(getSelectionModel().getSelectedTimeField());
    }

    /**
     * Determines whether the given String value at the specified offset is a valid time.
     * @param value a String value representing the time.
     * @return a boolean value of true if the String value is a valid time, false otherwise.
     */
    private boolean isValidTime(int offset, String value) {

      boolean valid = true;  // innocent until proven guilty

      for (int index = 0, len = value.length(); index < len && valid; index++) {
        char c = value.charAt(index);
        switch (offset + index) {
          case HOUR_MINUTE_COLAN_POSITION:
            valid = c == COLAN_CHAR;
            break;
          case SPACE_POSITION:
            valid = c == SPACE_CHAR;
            break;
          case AM_PM_POSITION:
            valid = Character.toLowerCase(c) == A_CHAR || Character.toLowerCase(c) == P_CHAR;
            break;
          case 7:
            valid = Character.toLowerCase(c) == M_CHAR;
            break;
          default:
            valid = Character.isDigit(c);
        }
      }

      return valid;
    }
  }

  private static final class TimeField {

    private final int calendarConstant;
    private final int selectionEnd;
    private final int selectionStart;

    private final String description;

    private TimeField next;
    private TimeField previous;

    public TimeField(String description, int calendarConstant, int selectionStart, int selectionEnd) {

      this.description = description;
      this.calendarConstant = calendarConstant;
      this.selectionStart = selectionStart;
      this.selectionEnd = selectionEnd;
    }

    public int getCalendarConstant() {
      return this.calendarConstant;
    }

    /**
     * Returns a descriptive name for the Calendar constant (Calendar.YEAR, Calenar.MONTH, etc).
     * NOTE: this was not added to the DateUtil generic class since this method is specific to debugging information
     * of this class and does not handle internationalization properly.
     * @param calendarConstant a constant representing the (YEAR, MONTH, etc) in the Calendar object.
     * @return a descriptive name for the Calendar constant.
     */
    private static String getCalendarConstantDescription(int calendarConstant) {

      switch (calendarConstant) {
        case Calendar.YEAR:
          return "Year";
        case Calendar.MONTH:
          return "Month";
        case Calendar.DAY_OF_MONTH:
          return "Day of Month";
        case Calendar.DAY_OF_WEEK:
          return "Day of Week";
        case Calendar.DAY_OF_WEEK_IN_MONTH:
          return "Day of Week in Month";
        case Calendar.DAY_OF_YEAR:
          return "Day of Year";
        case Calendar.HOUR:
          return "Hour";
        case Calendar.HOUR_OF_DAY:
          return "Hour of Day";
        case Calendar.MINUTE:
          return "Minute";
        case Calendar.SECOND:
          return "Second";
        case Calendar.MILLISECOND:
          return "Millisecond";
        default:
          return "Unknown";
      }
    }

    public String getDescription() {
      return this.description;
    }

    public TimeField getNext() {
      return this.next;
    }

    public void setNext(TimeField next) {
      this.next = next;
    }

    public TimeField getPrevious() {
      return this.previous;
    }

    public void setPrevious(TimeField previous) {
      this.previous = previous;
    }

    public int getSelectionEnd() {
      return this.selectionEnd;
    }

    public int getSelectionStart() {
      return this.selectionStart;
    }

    @Override
    public boolean equals(Object obj) {

      if (this == obj) {
        return true;
      }

      if (!(obj instanceof TimeField)) {
        return false;
      }

      final TimeField that = (TimeField) obj;

      return getCalendarConstant() == that.getCalendarConstant()
        && ObjectUtils.equals(getDescription(), that.getDescription())
        && getSelectionEnd() == that.getSelectionEnd()
        && getSelectionStart() == that.getSelectionStart();
    }

    @Override
    public int hashCode() {
      return ObjectUtils.hashCodeOf(getCalendarConstant(), getDescription(), getSelectionEnd(), getSelectionStart());
    }

    @Override
    public String toString() {

      StringBuilder buffer = new StringBuilder("{ calendarConstant = ");

      buffer.append(getCalendarConstantDescription(getCalendarConstant()));
      buffer.append(", description = ").append(getDescription());
      buffer.append(", selectionEnd = ").append(getSelectionEnd());
      buffer.append(", selectionStart = ").append(getSelectionStart());
      buffer.append(" }:").append(getClass().getName());

      return buffer.toString();
    }
  }

  /**
   * The TimeFieldKeyListener is used by the JTimeField component class to track key events targeted at
   * editing and traversing the time field text component.
   */
  private final class TimeFieldKeyListener extends KeyAdapter {

    public void keyPressed(KeyEvent event) {

      switch (event.getKeyCode()) {
        case KeyEvent.VK_BACK_SPACE:
        case KeyEvent.VK_DELETE:
        case KeyEvent.VK_SPACE:
          event.consume(); // cannot remove the contents of time
          TOOLKIT.beep();
          break;
        case KeyEvent.VK_HOME:
          event.consume();
          getSelectionModel().selectHour();
          break;
        case KeyEvent.VK_LEFT:
          event.consume();
          getSelectionModel().selectPrevious();
          break;
        case KeyEvent.VK_END:
          event.consume();
          getSelectionModel().selectAmPm();
          break;
        case KeyEvent.VK_RIGHT:
          event.consume();
          getSelectionModel().selectNext();
          break;
        case KeyEvent.VK_UP:
        case KeyEvent.VK_DOWN:
          if (getSelectionModel().isAmPmSelected()) {
            getModel().toggleAmPm();
          }
          else {
            getModel().roll(getSelectionModel().getSelectedTimeField().getCalendarConstant(),
              (event.getKeyCode() == KeyEvent.VK_UP));
          }
          break;
        default:
          if (KeyEventUtils.isNumeric(event.getKeyCode())) {

            int timeField = getSelectionModel().getSelectedTimeField().getCalendarConstant();
            int value = Integer.parseInt(String.valueOf(event.getKeyChar()));

            getModel().set(timeField, value);
          }
      }
    }

    /**
     * Processes the key typed event.  Consumes all key typed events!  This method is needed for proper functioning
     * of the JTimeField component.
     * @param event the KeyEvent object representing the key typed event.
     */
    public void keyTyped(KeyEvent event) {
      event.consume();
    }
  }

  /**
   * The TimeFieldMouseInputListener class is used by the JTimeField component class to track mouse events
   * when the user sets focus to this time field text component.
   */
  private final class TimeFieldMouseInputListener extends MouseInputAdapter {

    /**
     * Called to handle the MouseEvents defined by this TimeFieldMouseListener class.
     * @param event the MouseEvent object capturing information about the mouse event.
     */
    private void handleMouseEvent(MouseEvent event) {
      getSelectionModel().selectHour();
      event.consume();
    }

    /**
     * Called when a mouse button has been clicked in the time field component.
     * @param event a MouseEvent object capturing the mouse clicked event in the date field component.
     */
    public void mouseClicked(MouseEvent event) {
      handleMouseEvent(event);
    }

    /**
     * Called when the mouse has been dragged in the time field component.
     * @param event a MouseEvent object capturing the mouse drag event in the date field component.
     */
    public void mouseDragged(MouseEvent event) {
      handleMouseEvent(event);
    }

    /**
     * Called when a mouse button has been pressed in the time field component.
     * @param event a MouseEvent object capturing the mouse pressed event in the date field component.
     */
    public void mousePressed(MouseEvent event) {
      handleMouseEvent(event);
    }
  }

  private final class TimeFieldSelectionModel implements SelectionModel {

    private final TimeField AMPM = new TimeField("am/pm", Calendar.AM_PM, 6, 8);
    private final TimeField HOUR = new TimeField("hour", Calendar.HOUR_OF_DAY, 0, 2);
    private final TimeField MINUTE = new TimeField("minute", Calendar.MINUTE, 3, 5);
    private final TimeField SECOND = new TimeField("second", Calendar.SECOND, 6, 8);

    {
      AMPM.setNext(HOUR);
      AMPM.setPrevious(MINUTE);
      MINUTE.setNext(AMPM);
      MINUTE.setPrevious(HOUR);
      HOUR.setNext(MINUTE);
      HOUR.setPrevious(AMPM);
    }

    private TimeField selectedTimeField = HOUR;

    public TimeFieldSelectionModel() {
      setSelectedTextColor(Color.white);
      setSelectionColor(new Color(10, 36, 106));
    }

    public boolean isAmPmSelected() {
      return AMPM.equals(getSelectedTimeField());
    }

    public TimeField getSelectedTimeField() {
      return selectedTimeField;
    }

    public void setSelectedTimeField(final TimeField selectedTimeField) {
      this.selectedTimeField = selectedTimeField;
      setSelectionStart(this.selectedTimeField.getSelectionStart());
      setSelectionEnd(this.selectedTimeField.getSelectionEnd());
    }

    public void selectAmPm() {
      setSelectedTimeField(AMPM);
    }

    public void selectHour() {
      setSelectedTimeField(HOUR);
    }

    public void selectMinute() {
      setSelectedTimeField(MINUTE);
    }

    public void selectNext() {
      setSelectedTimeField(getSelectedTimeField().getNext());
    }

    public void selectPrevious() {
      setSelectedTimeField(getSelectedTimeField().getPrevious());
    }

    public void selectSecond() {
      setSelectedTimeField(SECOND);
    }
  }
}
