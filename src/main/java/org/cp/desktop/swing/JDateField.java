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
import org.cp.desktop.swing.model.DateModel;
import org.cp.desktop.swing.model.DefaultDateModel;
import org.cp.desktop.util.Strings;
import org.cp.elements.lang.Assert;
import org.cp.elements.lang.ObjectUtils;
import org.cp.elements.lang.annotation.NotNull;
import org.cp.elements.lang.annotation.Nullable;
import org.cp.elements.time.DateTimeUtils;

@SuppressWarnings("unused")
public class JDateField extends JTextField implements PropertyChangeListener {

  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

  private static final Font PLAIN_MONOSPACED_12 = new Font("Monospaced", Font.PLAIN, 12);

  private static final String DEFAULT_DATE_VALUE = "  /  /  ";

  private static final Toolkit TOOLKIT = Toolkit.getDefaultToolkit();

  /**
   * Factory method used to construct a new instance of {@link JDateField} initialized with the given, required
   * {@link String} representing a date.
   *
   * @param value {@link String} containing a date; must not be {@literal null}.
   * @return a new instance of {@link JDateField} initialized initialized with the given, required {@link String}
   * representing a date.
   * @throws IllegalArgumentException if the given {@link String date} is not valid.
   * @see java.lang.String
   */
  public static JDateField from(String value) {

    try {
      return new JDateField(value);
    }
    catch (ParseException cause) {
      throw newIllegalArgumentException(cause, "Date [%s] is not valid", value);
    }
  }

  private DateModel model;

  private SelectionModel selectionModel;

  /**
   * Constructs a new instance of the {@link JDateField} GUI component initialized with
   * {@link Calendar#getInstance() today's date}.
   *
   * @see #JDateField(Calendar)
   */
  public JDateField() {
    this(Calendar.getInstance());
  }

  /**
   * Constructs a new instance of {@link JDateField} initialized with the given {@link Calendar}.
   *
   * @param calendar {@link Calendar} specifying the date and time used to initialize this {@link JDateField}.
   * @see java.util.Calendar
   */
  public JDateField(@Nullable Calendar calendar) {

    super(10);

    addKeyListener(new DateFieldKeyListener());
    addMouseInputListener(new DateFieldMouseInputListener());

    setModel(new DefaultDateFieldModel(calendar));
    setFont(PLAIN_MONOSPACED_12);
    setSelectionModel(new DateFieldSelectionModel());

    super.setText(calendar != null ? DATE_FORMAT.format(calendar.getTime()) : DEFAULT_DATE_VALUE);
  }

  /**
   * Constructs a new instance of {@link JDateField} initialized with the given, required {@link Date}.
   *
   * @param date {@link Date} used to initialize this {@link JDateField}; must not be {@literal null}.
   * @see #JDateField(Calendar)
   * @see java.util.Date
   */
  public JDateField(@NotNull Date date) {
    this(DateTimeUtils.create(date.getTime()));
  }

  /**
   * Constructs a new instance of {@link JDateField} initialized with the {@link String} representing a {@literal date}.
   *
   * @param dateValue {@link String} containing a representation of a {@literal date}
   * in the format {@literal MM/dd/yyyy} used to initialize this {@link JDateField}.
   * @see #JDateField(Date)
   */
  public JDateField(@Nullable String dateValue) throws ParseException {
    this(Strings.hasText(dateValue) ? DATE_FORMAT.parse(dateValue) : Calendar.getInstance().getTime());
  }

  /**
   * Adds the specified MouseInputListener object as both the mouse listener and mouse motion listener.
   * @param listener the javax.swing.event.MouseInputListener to be registered with this date field component
   * as the mouse listener and mouse motion listener.
   */
  private void addMouseInputListener(MouseInputListener listener) {

    addMouseListener(listener);
    addMouseMotionListener(listener);
  }

  /**
   * Creates the default implementation of the model to be used at construction if one isn't explicitly given.
   * Returns an instance of DateDocument.
   * @return the default javax.swing.text.Document object used to represent the content for this date field
   * component.
   */
  protected Document createDefaultModel() {
    return new DateDocument();
  }

  /**
   * Receives notification of the property change events from the DateFieldModel when the date value for this
   * component changes.
   * @param event the PropertyChangeEvent tracking the date changes.
   */
  public void propertyChange(@NotNull PropertyChangeEvent event) {
    Calendar date = (Calendar) event.getNewValue();
    super.setText(DATE_FORMAT.format(date.getTime()));
  }

  /**
   * Returns the date value of the date field as a Calendar object.
   * @return a java.util.Calendar object representing the date value in the date field.
   */
  public Calendar getCalendar() {
    return getModel().getCalendar();
  }

  /**
   * Sets the date value of the date field to the specified Calendar value and updates the view.
   * @param calendar the Calendar object specifying the date value to set on the date field.
   */
  public void setCalendar(Calendar calendar) {
    getModel().setCalendar(calendar);
  }

  /**
   * Gets the data model used to encapsulate data, maintain state and manage changes to the properties
   * of this date field component.
   *
   * @param <T> concrete {@link DateModel} {@link Class type} of the date field's model.
   * @return the data model for the date field component.
   * @see DateModel
   */
  @SuppressWarnings("unchecked")
  public @NotNull <T extends DateModel> T getModel() {
    return (T) model;
  }

  /**
   * Sets the data dataModel to the specified DateModel for this date field to contain the data, maintain state and
   * handle changes to the properties of this date field.
   * @param model the DateModel used by this date field to manage state.
   */
  public void setModel(@NotNull DateModel model) {
    this.model = ObjectUtils.requireObject(model, "DateFieldModel is required");
    this.model.register(this);
  }

  /**
   * Returns the date value contained by this date field as a Date object.
   * @return a java.util.Date object representing the date value specified by this date input field component.
   */
  public Date getDate() {
    return getModel().getDate();
  }

  /**
   * Sets the JDateField to the specified date by formatting the Date object and extracting the String representation
   * of the date value and calling setText.
   * @param date is the specified Date value used to set the contents of this date input field component.
   */
  public void setDate(Date date) {
    getModel().setDate(date);
  }

  /**
   * Returns the SelectionModel for this date field compnent, which manages the selection of the individual date fields
   * in a date.
   * @return the SelectionModel for this date field.
   */
  public SelectionModel getSelectionModel() {
    return selectionModel;
  }

  /**
   * Sets the specified SelectionModel for this date field component.
   * @param selectionModel the SelectionModel used to manage date field selections of a date for this date field
   * component.
   */
  public void setSelectionModel(@Nullable SelectionModel selectionModel) {
    this.selectionModel = selectionModel != null ? selectionModel : new DateFieldSelectionModel();
  }

  /**
   * Overridden setText method sets the Calendar to the specified date and then calls super.setText to set the
   * String representation of the date in this date field.  Note, the date should be in the following format
   * MM/dd/yyyy.
   * @param text a String value representing the date to set in this date field component.
   */
  public void setText(String text) {

    try {
      getModel().setDate(text != null ? DATE_FORMAT.parse(text) : null);
    }
    catch (ParseException e) {
      throw newIllegalArgumentException("The text [%s] is not a valid date format;"
        + " please specify the date in the following format [%s]", text, DATE_FORMAT);
    }
  }

  /**
   * The DateDocument class is used by the JDateField component to format the JTextField component as a
   * date input field with forwarded slashes separating the month, day, and year.
   */
  private class DateDocument extends PlainDocument {

    private static final char DATE_SEPARATOR = '/';

    /**
     * Called by the JDateField component to enter the specified date content into the JTextField component of this
     * date field at the specified position formatted with the given AttributeSet object.
     * @param offset is an integer offset into the Document to insert content.
     * @param value String content to insert into the Document of this date field component.
     * @param attributeSet AttributeSet used to sytle the content.
     * @exception BadLocationException  the given insert position is not attrSet valid
     *   position within the document
     */
    public void insertString(int offset, String value, AttributeSet attributeSet) throws BadLocationException {

      if (!isValidDate(offset, value)) {
        TOOLKIT.beep();
      }
      else {
        super.insertString(offset, value, attributeSet);
      }

      // TODO: determine if I need this method call.
      getSelectionModel().selectDateField(getSelectionModel().getSelectedDateField());
    }

    /**
     * Determines whether the given String value at the specified offset is a valid Date or Date field.
     * NOTE: the implementation of this method is tied to the DATE_FORMAT DateFormat constant of the JDateField class.
     * @param value String object representing the date or date field.
     * @return a boolean value of true if the String value contains a valid date field or represents a valid date,
     * false otherwise.
     */
    private boolean isValidDate(int offset, String value) {

      Assert.argument(value, argument -> !Strings.hasText(argument) || argument.length() != 10,
        "Date [%s] is not valid", value);

      if (DEFAULT_DATE_VALUE.equals(value)) {
        return true;
      }

      boolean valid = true; // innocent until proven guilty
      char[] chars = value.toCharArray();

      for (int index = 0; index < chars.length && valid; index++) {
        char c = chars[index];
        switch (offset + index) {
          case 2:
          case 5:
            valid = c == DATE_SEPARATOR;
            break;
          default:
            valid = Character.isDigit(c);
        }
      }

      return valid;
    }
  }

  /**
   * This class represents the various fields of a Date (or Calendar object), such as Month, Day, Year, etc.
   */
  private static class DateField {

    private static final String DATE_FIELD_TO_STRING =
      "{ @type %s, calendarConstant = %s, description = %s, selectionEnd = %s, selectionStart = %s }";

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

    private final int calendarConstant;
    private final int selectionEnd;
    private final int selectionStart;

    private DateField next;
    private DateField previous;

    private final String description;

    private DateField(String description, int calendarConstant, int selectionStart, int selectionEnd) {

      this.description = description;
      this.calendarConstant = calendarConstant;
      this.selectionStart = selectionStart;
      this.selectionEnd = selectionEnd;
    }

    /**
     * Returns the Calendar constant of the date field that this object represents.
     * @return a Calendar constant of the date field that this object represents.
     */
    public int getCalendarConstant() {
      return this.calendarConstant;
    }

    /**
     * Returns a descriptive name for the date field represented by this object.
     * @return a String description of the date field.
     */
    public String getDescription() {
      return this.description;
    }

    /**
     * Returns the next date field in the order of date fields according to the specified date format.
     * @return a DateField object representing the date field after this date field.
     */
    public DateField getNext() {
      return this.next;
    }

    /**
     * Sets the next date field in the order of date fields according to the specified date format.
     * @param next is the next DateField object after this date field.
     */
    public void setNext(DateField next) {
      this.next = next;
    }

    /**
     * Returns the previous date field in the order of date fields according to the specified date format.
     * @return a DateField object representing the date field before this date field.
     */
    public DateField getPrevious() {
      return this.previous;
    }

    /**
     * Sets the previous date field in the order of date fields according to the specified date format.
     * @param previous is the previous DateField object before this date field.
     */
    public void setPrevious(DateField previous) {
      this.previous = previous;
    }

    /**
     * Used by the SelectionModel to keep track of the selected portion of the date value in the input component
     * view.
     * @return the an integer position in the view specifying the end of the selected portion of the date value.
     */
    public int getSelectionEnd() {
      return this.selectionEnd;
    }

    /**
     * Used by the SelectionModel to keep track of the selected portion of the date value in the input component
     * view.
     * @return the an integer position in the view specifying the start of the selected portion of the date value.
     */
    public int getSelectionStart() {
      return this.selectionStart;
    }

    /**
     * Determines whether this DateField object is equal to the specified Object.
     * @param obj the Object used in testing equality with this DateField.
     * @return a boolean value if the specified Object is equal to this DateField, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {

      if (this == obj) {
        return true;
      }

      if (!(obj instanceof DateField)) {
        return false;
      }

      DateField that = (DateField) obj;

      return getCalendarConstant() == that.getCalendarConstant()
        && getDescription().equals(that.getDescription())
        && getSelectionEnd() == that.getSelectionEnd()
        && getSelectionStart() == that.getSelectionStart();
    }

    /**
     * Computes a hash value based on the state of this DateField object.
     * @return an integer hash value for the state of this DateField object.
     */
    @Override
    public int hashCode() {
      return ObjectUtils.hashCodeOf(getCalendarConstant(), getDescription(), getSelectionEnd(), getSelectionStart());
    }

    /**
     * Returns a String representation of this DateField object.
     * @return a String containing state information of this DateField object.
     */
    @Override
    public String toString() {
      return String.format(DATE_FIELD_TO_STRING, getClass().getName(),
        getCalendarConstantDescription(getCalendarConstant()), getDescription(), getSelectionEnd(), getSelectionStart());
    }
  }

  /**
   * The DateFieldKeyListener class is used by the JDateField component to track LEFT, RIGHT, UP, AND DOWN arrow keyboard
   * key events.
   */
  private class DateFieldKeyListener extends KeyAdapter {

    /**
     * Processes the key pressed event.
     * @param event the KeyEvent object representing the key pressed event.
     */
    public void keyPressed(KeyEvent event) {

      switch (event.getKeyCode()) {
        case KeyEvent.VK_BACK_SPACE:
        case KeyEvent.VK_DELETE:
        case KeyEvent.VK_SPACE:
          event.consume(); // cannot remove the contents of the date
          TOOLKIT.beep();
          break;
        case KeyEvent.VK_ALT:
          break;
        case KeyEvent.VK_END:
          event.consume();
          getSelectionModel().selectYear();
          break;
        case KeyEvent.VK_HOME:
          event.consume();
          getSelectionModel().selectMonth();
          break;
        case KeyEvent.VK_LEFT:
          event.consume();
          getSelectionModel().selectPrevious();
          break;
        case KeyEvent.VK_RIGHT:
          event.consume();
          getSelectionModel().selectNext();
          break;
        case KeyEvent.VK_UP:
        case KeyEvent.VK_DOWN:
          getModel().roll(getSelectionModel().getSelectedDateField().getCalendarConstant(),
            (event.getKeyCode() == KeyEvent.VK_UP));
          break;
        default:
          event.consume();
          if (KeyEventUtils.isNumeric(event.getKeyCode())) {
            int calendarField = getSelectionModel().getSelectedDateField().getCalendarConstant();
            int value = Integer.parseInt(String.valueOf(event.getKeyChar()));
            getModel().set(calendarField, value);
          }
      }
    }

    /**
     * Processes the key typed event.  Consumes all key typed events!  This method is needed for property
     * functioning of the JDateField component.
     * @param event the KeyEvent object representing the key typed event.
     */
    public void keyTyped(KeyEvent event) {
      event.consume();
    }
  }

  /**
   * The DateFieldMouseInputListener class is used by the JDateField component to
   * track mouse clicked/pressed events when the user sets focus to this date
   * field component.
   */
  private class DateFieldMouseInputListener extends MouseInputAdapter {

    /**
     * Called to handle the MouseEvents defined by this DateFieldMouseInputListener class.
     * @param event the MouseEvent object capturing information about the mouse event.
     */
    private void handleMouseEvent(MouseEvent event) {
      getSelectionModel().selectMonth();
      event.consume();
    }

    /**
     * Called when a mouse button has been clicked in the date field component.
     * @param event a MouseEvent object capturing the mouse clicked event in the date field component.
     */
    public void mouseClicked(MouseEvent event) {
      handleMouseEvent(event);
    }

    /**
     * Called when the mouse has been dragged in the date field component.
     * @param event a MouseEvent object capturing the mouse drag event in the date field component.
     */
    public void mouseDragged(MouseEvent event) {
      handleMouseEvent(event);
    }

    /**
     * Called when a mouse button has been pressed in the date field component.
     * @param event a MouseEvent object capturing the mouse pressed event in the date field component.
     */
    public void mousePressed(MouseEvent event) {
      handleMouseEvent(event);
    }
  }

  protected static class DefaultDateFieldModel extends DefaultDateModel implements DateModel {

    protected DefaultDateFieldModel(Calendar calendar) {
      super(calendar);
    }

    protected DefaultDateFieldModel(Date date) {
      super(date);
    }
  }

  /**
   * Interface to describe the operations required by the {@link JDateField} component
   * used to select the various date fields in the component view.
   */
  public interface SelectionModel {

    DateField getSelectedDateField();

    void selectDateField(DateField dateField);

    void selectDay();

    void selectMonth();

    void selectNext();

    void selectPrevious();

    void selectYear();

  }

  /**
   * The default SelectionModel for instances of the JDateField component.  This SelectionModel highlights the
   * selected date field.
   */
  private class DateFieldSelectionModel implements PropertyChangeListener, SelectionModel {

    private final DateField DAY = new DateField("day", Calendar.DAY_OF_MONTH, 3, 5);
    private final DateField MONTH = new DateField("month", Calendar.MONTH, 0, 2);
    private final DateField YEAR = new DateField("year", Calendar.YEAR, 6, 10);

    // instance initializer block used to set up date field navigation
    {
      DAY.setNext(YEAR);
      DAY.setPrevious(MONTH);
      MONTH.setNext(DAY);
      MONTH.setPrevious(YEAR);
      YEAR.setNext(MONTH);
      YEAR.setPrevious(DAY);
    }

    private DateField selectedDateField = MONTH;

    /**
     * Creates a default instance of the DateFieldSelectionModel class with selected text color set to white
     * and selection color set to navy blue.
     */
    public DateFieldSelectionModel() {

      addPropertyChangeListener(this);
      setSelectedTextColor(Color.white);
      setSelectionColor(new Color(10, 36, 106));
    }

    /**
     * Returns the selected date field as an instance of the DateField class.
     * @return a DateField object representing the selected date field.
     */
    public DateField getSelectedDateField() {
      return this.selectedDateField;
    }

    /**
     * Receives notification of date value changes in the JDateField component.
     * @param event a PropertyChangeEvent encasulating the date value changes in the JDateField component.
     */
    public void propertyChange(PropertyChangeEvent event) {
      if (event.getPropertyName().equals("text")) {
        selectDateField(getSelectedDateField());
      }
    }

    /**
     * Sets the selected date field of the date value.
     * @param dateField a DateField object representing the date field of the date value to select.
     */
    public void selectDateField(DateField dateField) {

      selectedDateField = dateField;
      setSelectionStart(selectedDateField.getSelectionStart());
      setSelectionEnd(selectedDateField.getSelectionEnd());
    }

    /**
     * Selects the day date field.
     */
    public void selectDay() {
      selectDateField(DAY);
    }

    /**
     * Selects the month date field.
     */
    public void selectMonth() {
      selectDateField(MONTH);
    }

    /**
     * Selects the next date field in succession to the current date field selected.
     */
    public void selectNext() {
      selectDateField(getSelectedDateField().getNext());
    }

    /**
     * Selects the previous date field in precession to the current date field selected.
     */
    public void selectPrevious() {
      selectDateField(getSelectedDateField().getPrevious());
    }

    /**
     * Selects the year date field.
     */
    public void selectYear() {
      selectDateField(YEAR);
    }
  }
}
