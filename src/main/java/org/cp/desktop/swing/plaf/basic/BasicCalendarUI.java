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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;

import org.cp.desktop.swing.GraphicsUtil;
import org.cp.desktop.swing.JCalendar;
import org.cp.desktop.swing.plaf.CalendarUI;
import org.cp.elements.beans.AbstractBean;
import org.cp.elements.lang.ObjectUtils;
import org.cp.elements.lang.annotation.NotNull;
import org.cp.elements.security.model.User;
import org.cp.elements.time.Month;

public class BasicCalendarUI extends CalendarUI {

  private static final int FOOTER_HEIGHT = 25;
  private static final int HEADER_HEIGHT = 25;
  private static final int HORIZONTAL_GAP = 5;
  private static final int WEEKDAY_HEIGHT = 20;

  private static final Color BLUEFISH_GRAY = new Color(53, 73, 101);
  private static final Color LIGHT_GRAY = new Color(204, 204, 204);
  private static final Color BUTTON_COLOR = Color.DARK_GRAY;
  private static final Color BUTTON_PRESSED_COLOR = Color.RED;
  private static final Color BUTTON_ROLLOVER_COLOR = Color.BLACK;
  private static final Color CALENDAR_COLOR = Color.DARK_GRAY;
  private static final Color CALENDAR_ROLLOVER_COLOR = Color.BLACK;
  private static final Color CALENDAR_SELECTED_COLOR = BLUEFISH_GRAY;
  private static final Color DISABLED_COLOR = LIGHT_GRAY;
  private static final Color FOOTER_BACKGROUND = LIGHT_GRAY;
  private static final Color FOOTER_FOREGROUND = Color.BLACK;
  private static final Color HEADER_FOREGROUND = BLUEFISH_GRAY;
  private static final Color WEEKDAY_BACKGROUND = BLUEFISH_GRAY;
  private static final Color WEEKDAY_FOREGROUND = Color.WHITE;

  private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("MM/dd/yyyy h:mm a");
  private static final DateFormat DAY_MONTH_YEAR_FORMAT = new SimpleDateFormat("dd MMM yyyy");
  private static final DateFormat DAY_OF_WEEK_DATE_FORMAT = new SimpleDateFormat("EEE, MMM dd, yyyy");
  private static final DateFormat DAY_OF_WEEK_DATE_TIME_FORMAT = new SimpleDateFormat("EEE, MM dd, yyyy h:mm a");
  private static final DateFormat MONTH_YEAR = new SimpleDateFormat("MMMMM, yyyy");
  private static final DateFormat YEAR_MONTH_DAY_FORMAT = new SimpleDateFormat("yyyy.MM.dd");

  private static final Dimension BUTTON_SIZE = new Dimension(10, 10);
  private static final Dimension PREFERRED_SIZE = new Dimension(200, 165);

  private static final Font HELVETICA_BOLD_14 = new Font("Helvetica", Font.BOLD, 14);
  private static final Font HELVETICA_BOLD_12 = new Font("Helvetica", Font.BOLD, 12);
  private static final Font HELVETICA_BOLD_11 = new Font("Helvetica", Font.BOLD, 11);
  private static final Font HELVETICA_PLAIN_10 = new Font("Helvetica", Font.PLAIN, 10);
  private static final Font CALENDAR_FONT = HELVETICA_PLAIN_10;
  private static final Font CALENDAR_ROLLOVER_FONT = HELVETICA_BOLD_11;
  private static final Font CALENDAR_SELECTED_FONT = HELVETICA_BOLD_12;
  private static final Font FOOTER_FONT = HELVETICA_BOLD_12;
  private static final Font HEADER_FONT = HELVETICA_BOLD_14;
  private static final Font WEEKDAY_FONT = HELVETICA_BOLD_12;

  private static final String[] WEEK_DAYS = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

  private BasicCalendarListener calendarListener;

  private Button decrementMonth;
  private Button decrementYear;
  private Button incrementMonth;
  private Button incrementYear;

  private Day[][] calendarMatrix;

  /**
   * Creates an instance of the BasicCalendarUI class responsible for painting the user interface of the JCalendar
   * Swing UI component and defining actions for handling user generated events.
   * @param calendar the JCalendar Swing UI component who's user interface components will be defined by this
   * BasicCalendarUI class.
   */
  public BasicCalendarUI(JCalendar calendar) {

    initButtons(calendar);
    initCalendar(calendar);
  }

  /**
   * Creates and initializes buttons used in the UI by the user to decrement and increment the month and year
   * of the calendar.
   * @param calendar the JCalendar Swing UI component who's calendar model is updated by this UI's button actions.
   */
  protected void initButtons(JCalendar calendar) {

    decrementMonth = createButton(calendar, new AbstractAction() {
      public void actionPerformed(ActionEvent event) {
        calendar.getModel().decrementMonth();
      }
    });

    decrementMonth.setIcon(new ImageIcon(GraphicsUtil.fillLeftArrow(BUTTON_SIZE, BUTTON_COLOR), "Filled Dark Gray Left Arrow"));
    decrementMonth.setPressedIcon(new ImageIcon(GraphicsUtil.fillLeftArrow(BUTTON_SIZE, BUTTON_PRESSED_COLOR), "Filled Red Left Arrow"));
    decrementMonth.setRolloverIcon(new ImageIcon(GraphicsUtil.fillLeftArrow(BUTTON_SIZE, BUTTON_ROLLOVER_COLOR), "Filled Black Left Arrow"));
    decrementMonth.setSize(BUTTON_SIZE);

    decrementYear = createButton(calendar, new AbstractAction() {
      public void actionPerformed(ActionEvent event) {
        calendar.getModel().decrementYear();
      }
    });

    decrementYear.setIcon(new ImageIcon(GraphicsUtil.fillDoubleLeftArrow(BUTTON_SIZE, BUTTON_COLOR), "Two Filled Dark Gray Left Arrows"));
    decrementYear.setPressedIcon(new ImageIcon(GraphicsUtil.fillDoubleLeftArrow(BUTTON_SIZE, BUTTON_PRESSED_COLOR), "Two Filled Red Left Arrows"));
    decrementYear.setRolloverIcon(new ImageIcon(GraphicsUtil.fillDoubleLeftArrow(BUTTON_SIZE, BUTTON_ROLLOVER_COLOR), "Two Filled Black Left Arrows"));
    decrementYear.setSize(BUTTON_SIZE);

    incrementMonth = createButton(calendar, new AbstractAction() {
      public void actionPerformed(ActionEvent event) {
        calendar.getModel().incrementMonth();
      }
    });

    incrementMonth.setIcon(new ImageIcon(GraphicsUtil.fillRightArrow(BUTTON_SIZE, BUTTON_COLOR), "Filled Dark Gray Right Arrow"));
    incrementMonth.setPressedIcon(new ImageIcon(GraphicsUtil.fillRightArrow(BUTTON_SIZE, BUTTON_PRESSED_COLOR), "Filled Red Right Arrow"));
    incrementMonth.setRolloverIcon(new ImageIcon(GraphicsUtil.fillRightArrow(BUTTON_SIZE, BUTTON_ROLLOVER_COLOR), "Filled Black Right Arrow"));
    incrementMonth.setSize(BUTTON_SIZE);

    incrementYear = createButton(calendar, new AbstractAction() {
      public void actionPerformed(ActionEvent event) {
        calendar.getModel().incrementYear();
      }
    });

    incrementYear.setIcon(new ImageIcon(GraphicsUtil.fillDoubleRightArrow(BUTTON_SIZE, BUTTON_COLOR), "Two Filled Dark Gray Right Arrows"));
    incrementYear.setPressedIcon(new ImageIcon(GraphicsUtil.fillDoubleRightArrow(BUTTON_SIZE, BUTTON_PRESSED_COLOR), "Two Filled Red Right Arrows"));
    incrementYear.setRolloverIcon(new ImageIcon(GraphicsUtil.fillDoubleRightArrow(BUTTON_SIZE, BUTTON_ROLLOVER_COLOR), "Two Filled Black Right Arrows"));
    incrementYear.setSize(BUTTON_SIZE);
  }

  /**
   * Creates and initializes the calendar matrix containing the days of the current calendar month.
   * @param calendar the JCalendar Swing UI component who's calendar matrix of the current calendar month
   * is determined and setup.
   */
  protected void initCalendar(JCalendar calendar) {

    calendarMatrix = new Day[6][7];

    for (int row = 6; --row >= 0; ) {
      for (int col = 7; --col >= 0; ) {
        calendarMatrix[row][col] = createDay(calendar);
      }
    }

    setupCalendarMonth(calendar);
  }

  /**
   * Creates an instance of the Button calendar component class to invoke actions that modify the calendar model
   * upon user input.
   * @param action the Action class defining the calendar model modification to perform.
   * @return an instance of the Button class initialized with the specified Action.
   */
  protected Button createButton(JCalendar calendar, Action action) {

    AbstractButton button = new BasicButton(action);

    button.register(getCalendarListener(calendar));
    getCalendarListener(calendar).register(button);

    return button;
  }

  /**
   * Creates an instance of the Day class which is an abstraction for representing days in the current calendar
   * month.
   * @param calendar the JCalendar Swing UI component.
   * @return an instance of the Day class initialized with this CalendarUI's default look and feel properties.
   */
  protected Day createDay(JCalendar calendar) {

    AbstractDay day = new BasicDay();

    day.register(getCalendarListener(calendar));

    day.setAction(new AbstractAction() {
      public void actionPerformed(ActionEvent event) {
        calendar.getModel().set(Calendar.DAY_OF_MONTH, day.getDayOfMonth());
      }
    });

    day.setColor(CALENDAR_COLOR);
    day.setFont(CALENDAR_FONT);
    day.setRolloverColor(CALENDAR_ROLLOVER_COLOR);
    day.setRolloverFont(CALENDAR_ROLLOVER_FONT);
    day.setSelected(false);
    day.setSelectedColor(CALENDAR_SELECTED_COLOR);
    day.setSelectedFont(CALENDAR_SELECTED_FONT);
    getCalendarListener(calendar).register(day);

    return day;
  }

  /**
   * Used by the Swing framework for creating and instance of this UI with the specified Swing component.
   * @param component the JComponent, or JCalendar, which UI delegates to this class to determine the look and feel.
   * @return an instance of this ComponentUI class to handle the paint operations of the user interface.
   */
  public static ComponentUI createUI(JComponent component) {
    return new BasicCalendarUI((JCalendar) component);
  }

  /**
   * Returns the Singleton instance of the BasicCalendarListener class which acts as a controller in the
   * UI Delegate reletionship with the UI and is responsible for receiving user input events to the JCalendar
   * Swing UI component and associating actions and behaviors with the events.
   * @param calendar the JCalendar Swing UI component for which the controller is attached.
   * @return an instance of the BasicCalendarListener class acting as the controller for processing user
   * input events to the calenar component.
   */
  protected BasicCalendarListener getCalendarListener(JCalendar calendar) {

    if (calendarListener == null) {
      synchronized (this) {
        if (calendarListener == null) {
          calendarListener = new BasicCalendarListener(calendar);
        }
      }
    }

    return calendarListener;
  }

  /**
   * Computes the label position within the rectangular viewable area given the specified FontMetrics used by the
   * Graphics context to paint the label.
   * @param fontMetrics the FontMetrics of the current Font used by the Graphics context to paint the label.
   * @param label a String value specifying the label painted in the viewable area.
   * @param viewableArea the rectangular area in which the label will be painted.
   * @return a Point2D object specifying the labels offseted position within the viewable area to paint the label.
   */
  protected Point2D getLabelPosition(FontMetrics fontMetrics, String label, RectangularShape viewableArea) {

    int maxAscent = fontMetrics.getMaxAscent();
    int maxDescent = fontMetrics.getMaxDescent();
    int width = fontMetrics.stringWidth(label);

    return new Point2D.Double((viewableArea.getX() + ((viewableArea.getWidth() - width) / 2.0)),
      (viewableArea.getY() + (viewableArea.getHeight() / 2.0) - ((maxDescent - maxAscent) / 2.0)));
  }

  /**
   * Returns the preferred size of the calendar component based on the UI subcomponents in the user interface.
   * @param component the JComponent, JCalendar, who's preferred size is determined.
   * @return a Dimension object specifying the preferred size of the specified JComponent.
   */
  public Dimension getPreferredSize(JComponent component) {
    return PREFERRED_SIZE;
  }

  /**
   * Determines the JCalendar object's viewable area by computing the difference between the calendar's preferred
   * size and the border or insets the calendar.
   * @param calendar the JCalendar object who's viewable area is being determined.
   * @return a Dimension object specifying the size (width and height) of the viewable area of the calendar.
   */
  protected Dimension getViewableArea(JCalendar calendar) {

    Dimension preferredSize = getPreferredSize(calendar);
    Insets borderInsets = calendar.getInsets(new Insets(0, 0, 0, 0));

    int width = (preferredSize.width - (borderInsets.left + borderInsets.right));
    int height = (preferredSize.height - (borderInsets.top + borderInsets.bottom));

    return new Dimension(width, height);
  }

  /**
   * Returns the location, the (x, y) coordinates, of the viewable area with respect to the calendar component's
   * preferred size.
   * @param calendar the JCalendar object who's viewable area location is computed.
   * @return a Point2D object specifying the (x, y) location of the viewable area with respect to the JCalendar's
   * preferred size.
   */
  protected Point2D getViewableAreaLocation(JCalendar calendar) {

    Dimension preferredSize = getPreferredSize(calendar);
    Dimension viewableArea = getViewableArea(calendar);

    double x = ((preferredSize.getWidth() - viewableArea.getWidth()) / 2.0);
    double y = ((preferredSize.getHeight() - viewableArea.getHeight()) / 2.0);

    return new Point2D.Double(x, y);
  }

  /**
   * Returns a String array containing the text descriptions for the days of the week (Monday, Tuesday, etc).
   * @return a String array containing text descriptions for the week days.
   */
  protected String[] getWeekdays() {
    return WEEK_DAYS;
  }

  /**
   * Installs hooks between this ComponentUI class and the associated JComponent, JCalendar.
   * @param component installs this UI on the specified JComponent, or JCalendar.
   */
  public void installUI(JComponent component) {

    JCalendar calendar = (JCalendar) component;

    component.addMouseListener(getCalendarListener(calendar));
    component.addMouseMotionListener(getCalendarListener(calendar));
  }

  /**
   * Paint draws the basic user interface for the JCalendar Swing UI component.
   * @param graphics the Graphics context used to paint the user interface of the calendar.
   * @param component the calendar component who's user interface is being painted.
   */
  public void paint(Graphics graphics, JComponent component) {

    Graphics2D g2 = (Graphics2D) graphics;
    JCalendar calendar = (JCalendar) component;
    Point2D viewableAreaLocation = getViewableAreaLocation(calendar);

    paintBorder(g2, calendar);
    g2.translate(viewableAreaLocation.getX(), viewableAreaLocation.getY());
    paintHeader(g2, calendar);
    paintWeekdays(g2, calendar);
    paintCalendar(g2, calendar);
    paintFooter(g2, calendar);
    g2.translate(-viewableAreaLocation.getX(), -viewableAreaLocation.getY());
  }

  /**
   * Paints the border around the calendar.
   * @param g2 the Graphics context used to paint the border of the calendar.
   * @param calendar the JCalendar Swing UI component for which the border is being painted around.
   */
  protected void paintBorder(Graphics2D g2, JCalendar calendar) {

    Border calendarBorder = calendar.getBorder();

    if (calendarBorder != null) {
      Dimension preferredSize = getPreferredSize(calendar);
      calendarBorder.paintBorder(calendar, g2, 0, 0, preferredSize.width, preferredSize.height);
    }
  }

  /**
   * Paints the current calendar month, or the days in the current calendar month.
   * @param g2 the Graphics context used to paint the current calendar month.
   * @param calendar the JCalendar Swing UI component.
   */
  protected void paintCalendar(Graphics2D g2, JCalendar calendar) {

    Dimension viewableArea = getViewableArea(calendar);
    RectangularShape calendarViewableArea = new Rectangle2D.Double(0, (HEADER_HEIGHT + WEEKDAY_HEIGHT),
      viewableArea.getWidth(), (viewableArea.getHeight() - HEADER_HEIGHT - WEEKDAY_HEIGHT - FOOTER_HEIGHT));

    double cellWidth = (calendarViewableArea.getWidth() / 7.0);
    double cellHeight = (calendarViewableArea.getHeight() / 6.0);

    Dimension cellSize = new Dimension((int) cellWidth, (int) cellHeight);

    int currentDay = calendar.getCalendar().get(Calendar.DAY_OF_MONTH);

    double xOffset = 0.0;
    double yOffset = calendarViewableArea.getY();

    if (calendar.getModel().getMonthYearChanged()) {
      setupCalendarMonth(calendar);
    }

    for (int rows = 0; rows < 6; rows++) {
      for (int cols = 0; cols < 7; cols++) {
        calendarMatrix[rows][cols].setLocation(new Point2D.Double(xOffset, yOffset));

        if (calendarMatrix[rows][cols].isEnabled()) {
          calendarMatrix[rows][cols].setSelected(calendarMatrix[rows][cols].getDayOfMonth() == currentDay);
        }

        calendarMatrix[rows][cols].setSize(cellSize);
        calendarMatrix[rows][cols].paint(g2, calendar);
        xOffset += cellWidth;
      }
      xOffset = 0;
      yOffset += cellHeight;
    }
  }

  /**
   * Paints the footer of the JCalendar Swing UI component.  The footer of the calendar displays the current day
   * and date.
   * @param g2 the Graphics context object used to paint the footer of the calendar.
   * @param calendar the JCalendar Swing UI component who's footer will be painted.
   */
  protected void paintFooter(Graphics2D g2, JCalendar calendar) {

    String currentDate = DAY_OF_WEEK_DATE_FORMAT.format(Calendar.getInstance().getTime());

    Dimension viewableArea = getViewableArea(calendar);

    RectangularShape footerViewableArea = new Rectangle2D.Double(0, viewableArea.getHeight() - FOOTER_HEIGHT,
      viewableArea.getWidth(), FOOTER_HEIGHT);

    Point2D labelPosition = getLabelPosition(g2.getFontMetrics(FOOTER_FONT), currentDate, footerViewableArea);

    g2.setColor(FOOTER_BACKGROUND);
    g2.fill(footerViewableArea);
    g2.setColor(FOOTER_FOREGROUND);
    g2.setFont(FOOTER_FONT);
    g2.drawString(currentDate, (float) labelPosition.getX(), (float) labelPosition.getY());
  }

  /**
   * Paints the header of the of the JCalendar Swing UI component.  The header displays buttons to increment
   * and decrement the month and year as well as the current month and year.
   * @param g2 the Graphics context object used to paint the header of the calendar.
   * @param calendar the JCalendar Swing UI component who's header will be painted.
   */
  protected void paintHeader(Graphics2D g2, JCalendar calendar) {

    String monthYear = MONTH_YEAR.format(calendar.getCalendar().getTime());

    Dimension viewableArea = getViewableArea(calendar);

    RectangularShape headerViewableArea = new Rectangle2D.Double(0.0, 0.0, viewableArea.getWidth(), HEADER_HEIGHT);

    double buttonYOffset = ((headerViewableArea.getHeight() - BUTTON_SIZE.getHeight()) / 2.0);

    Point2D monthYearLabelLocation = getLabelPosition(g2.getFontMetrics(HEADER_FONT), monthYear, headerViewableArea);

    decrementYear.setLocation(new Point2D.Double(HORIZONTAL_GAP, buttonYOffset));
    decrementMonth.setLocation(new Point2D.Double(2 * HORIZONTAL_GAP + BUTTON_SIZE.getWidth(), buttonYOffset));
    incrementMonth.setLocation(new Point2D.Double(headerViewableArea.getWidth() - (2 * HORIZONTAL_GAP) - (2 * BUTTON_SIZE.getWidth()), buttonYOffset));
    incrementYear.setLocation(new Point2D.Double(headerViewableArea.getWidth() - HORIZONTAL_GAP - BUTTON_SIZE.getWidth(), buttonYOffset));

    decrementYear.paint(g2, calendar);
    decrementMonth.paint(g2, calendar);

    g2.setColor(HEADER_FOREGROUND);
    g2.setFont(HEADER_FONT);
    g2.drawString(monthYear, (float) monthYearLabelLocation.getX(), (float) monthYearLabelLocation.getY());

    incrementMonth.paint(g2, calendar);
    incrementYear.paint(g2, calendar);
  }

  /**
   * Paints the days of the week on the calendar face.
   * @param g2 the Graphics context used to paint the days of the week.
   * @param calendar the JCalendar Swing UI component who's week days will be painted.
   */
  protected void paintWeekdays(Graphics2D g2, JCalendar calendar) {

    Dimension viewableArea = getViewableArea(calendar);

    RectangularShape weekdayViewableArea =
      new Rectangle2D.Double(0.0, HEADER_HEIGHT, viewableArea.getWidth(), WEEKDAY_HEIGHT);

    double width = (viewableArea.getWidth() / 7.0);
    double xOffset = 0.0;

    g2.setPaint(WEEKDAY_BACKGROUND);
    g2.fill(weekdayViewableArea);
    g2.setFont(WEEKDAY_FONT);
    g2.setPaint(WEEKDAY_FOREGROUND);

    for (Iterator<?> it = Arrays.asList(getWeekdays()).iterator(); it.hasNext(); xOffset += width) {

      String weekDayLabel = it.next().toString();
      RectangularShape weekdayCell = new Rectangle2D.Double(xOffset, HEADER_HEIGHT, width, WEEKDAY_HEIGHT);
      Point2D location = getLabelPosition(g2.getFontMetrics(WEEKDAY_FONT), weekDayLabel, weekdayCell);

      g2.drawString(weekDayLabel, (float) location.getX(), (float) location.getY());
    }
  }

  /**
   * Sets up the days in the calendar month for the specified JCalendar Swing UI component.
   * @param calendar the JCalendar Swing UI component.
   */
  protected void setupCalendarMonth(JCalendar calendar) {

    Calendar currentCalendar = calendar.getCalendar();

    int currentYear = currentCalendar.get(Calendar.YEAR);
    int currentMonth = currentCalendar.get(Calendar.MONTH);
    int currentDayOfMonth = currentCalendar.get(Calendar.DAY_OF_MONTH);
    int currentWeekday = currentCalendar.get(Calendar.DAY_OF_WEEK);
    int previousMonth = Month.valueOfCalendarMonth(currentMonth).getPreviousMonth().getCalendarMonth();
    int numberOfDaysInCurrentMonth = calendar.getModel().getNumberOfDaysInMonth(currentMonth, currentYear);

    // NOTE: If the current month is January, the number of days in the previous month (December) of last year
    // would be the same as the number of days in December of the current year.  We need to specify currentYear
    // in the call to getNumberOfDaysInMonth if currentMonth is March and the current year is a leap year (since
    // February will have 29 days instead of 28).
    int numberOfDaysInPreviousMonth = calendar.getModel().getNumberOfDaysInMonth(previousMonth, currentYear);
    int firstWeekDayInMonth = calendar.getModel().getFirstDayOfMonth(currentDayOfMonth, currentWeekday);

    boolean enabled = true;

    int currentDay = 1;
    int numberOfDaysInMonth = numberOfDaysInCurrentMonth;

    if (firstWeekDayInMonth != Calendar.SUNDAY) {
      enabled = false;
      currentDay = numberOfDaysInPreviousMonth - (firstWeekDayInMonth - 2);
      numberOfDaysInMonth = numberOfDaysInPreviousMonth;
    }

    for (int rows = 0; rows < 6; rows++) {
      for (int cols = 0; cols < 7; cols++) {

        calendarMatrix[rows][cols].setDayOfMonth(currentDay);
        calendarMatrix[rows][cols].setEnabled(enabled);

        if (currentDay++ >= numberOfDaysInMonth) {
          enabled = !enabled;
          currentDay = 1;
          numberOfDaysInMonth = numberOfDaysInCurrentMonth;
        }
      }
    }
  }

  /**
   * Removes the association, hooks, between this UI and the calendar component.
   * @param component the JComponent, or JCalendar, that had this UI installed.
   */
  public void uninstallUI(JComponent component) {

    JCalendar calendar = (JCalendar) component;

    component.removeMouseListener(getCalendarListener(calendar));
    component.removeMouseMotionListener(getCalendarListener(calendar));
  }

  /**
   * The Actionable interface defines calendar components that are actionable, which perform some function, or action,
   * when the component receives user input, either from the keyboard or mouse.
   */
  protected interface Actionable {

    Action getAction();

    void setAction(Action action);

    boolean isArmed();

    void setArmed(boolean armed);

    boolean isTriggered();

    void setTriggered(boolean triggered);

    void doAction(ActionEvent event);

  }

  /**
   * The CalendarComponent interface defines a specification for various subcomponents of the calendar's user interface.
   */
  protected interface CalendarComponent {

    boolean isEnabled();

    void setEnabled(boolean enabled);

    Point2D getLocation();

    void setLocation(Point2D location);

    Dimension getSize();

    void setSize(Dimension size);

    boolean contains(Point2D location);

    void paint(Graphics2D g2, JCalendar calendar);

  }

  /**
   * The Button interface specifies a calendar UI component abstraction that allows the user to interact with the
   * calendar model by incrementing and decrementing the year and month.
   */
  protected interface Button extends CalendarComponent {

    Icon getIcon();

    void setIcon(Icon icon);

    Icon getPressedIcon();

    void setPressedIcon(Icon pressedIcon);

    Icon getRolloverIcon();

    void setRolloverIcon(Icon rolloverIcon);

  }

  /**
   * The Day interface specifies a calendar UI component displaying a single calendar day.
   */
  protected interface Day extends CalendarComponent {

    Color getColor();

    void setColor(Color color);

    int getDayOfMonth();

    void setDayOfMonth(int dayOfMonth);

    Font getFont();

    void setFont(Font font);

    Color getRolloverColor();

    void setRolloverColor(Color rolloverColor);

    Font getRolloverFont();

    void setRolloverFont(Font rolloverFont);

    boolean isSelected();

    void setSelected(boolean selected);

    Color getSelectedColor();

    void setSelectedColor(Color selectedColor);

    Font getSelectedFont();

    void setSelectedFont(Font selectedFont);

  }

  /**
   * An Abstract base class for defining and implement calendar UI subcomponents that appear in the calendar's
   * user interface.  This abstract class defines properties, behaviors and events common to all calendar UI
   * subcomponents.
   */
  protected abstract class AbstractCalendarComponent extends AbstractBean<Integer, User<Integer>, String>
      implements CalendarComponent {

    private boolean enabled = true;
    private Dimension size;
    private Point2D location;

    public boolean isEnabled() {
      return this.enabled;
    }

    public void setEnabled( boolean enabled) {
      this.enabled = enabled;
    }

    public Point2D getLocation() {
      return this.location;
    }

    public void setLocation(@NotNull Point2D location) {
      this.location = ObjectUtils.requireObject(location, "Location is required");
    }

    public Dimension getSize() {
      return this.size;
    }

    public void setSize(@NotNull Dimension size) {
      this.size = ObjectUtils.requireObject(size, "Size is required");
    }

    public Object getValue() {
      throw new UnsupportedOperationException("Operation Not Supported!");
    }

    public void setValue(Object value) {
      throw new UnsupportedOperationException("Operation Not Supported!");
    }

    public boolean contains(Point2D location) {
      return GraphicsUtil.getRectangle(getLocation(), getSize()).contains(location);
    }

    @Override
    protected void register(PropertyChangeListener listener) {
      super.register(listener);
    }

    @Override
    protected void register(VetoableChangeListener listener) {
      super.register(listener);
    }
  }

  /**
   * An abstract class constituting the basis for actionable calendar UI subcomponents.  This class defined properties,
   * behaviors and events that make a calendar UI subcomponent respond to a user, and perform some action.  This class
   * also forms the basis for receiving and processing user input.
   */
  protected abstract class ActionableCalendarComponent extends AbstractCalendarComponent
      implements Actionable, MouseInputListener {

    private boolean armed;
    private boolean triggered;

    private Action action;

    public Action getAction() {
      return this.action;
    }

    public final void setAction(Action action) {
      this.action = action;
    }

    public boolean isArmed() {
      return this.armed;
    }

    public void setArmed(boolean armed) {
      processChange("armed", isArmed(), armed);
    }

    public boolean isTriggered() {
      return this.triggered;
    }

    public void setTriggered(boolean triggered) {
      processChange("triggered", isTriggered(), triggered);
    }

    public void doAction(ActionEvent event) {
      if (isEnabled() && getAction() != null) {
        getAction().actionPerformed(event);
      }
    }

    protected abstract void handleMousePressed(MouseEvent event);

    protected abstract void handleMouseReleased(MouseEvent event);

    public void mouseClicked(MouseEvent event) { }

    public void mouseDragged(@NotNull MouseEvent event) {

      if (isEnabled()) {
        if (contains(event.getPoint())) {
          setArmed(true);
        }
        else {
          setArmed(false);
          setTriggered(false);
        }
      }
    }

    public void mouseEntered(@NotNull MouseEvent event) {

      if (isEnabled()) {
        setArmed(contains(event.getPoint()));
      }
    }

    public void mouseExited(@NotNull MouseEvent event) {

      if (isEnabled()) {
        setArmed(false);
        setTriggered(false);
      }
    }

    public void mouseMoved(@NotNull MouseEvent event) {

      if (isEnabled()) {
        setArmed(contains(event.getPoint()));
      }
    }

    public void mousePressed(@NotNull MouseEvent event) {

      if (isEnabled()) {
        if (contains(event.getPoint())) {
          setTriggered(true);
          handleMousePressed(event);
        }
      }
    }

    public void mouseReleased(@NotNull MouseEvent event) {

      if (isEnabled()) {
        if (contains(event.getPoint())) {
          handleMouseReleased(event);
        }
        setTriggered(false);
      }
    }
  }

  /**
   * The AbstractButton class defines calendar UI button component for triggering some programmatical action.
   */
  protected abstract class AbstractButton extends ActionableCalendarComponent implements Button {

    private Icon icon;
    private Icon pressedIcon;
    private Icon rolloverIcon;

    public AbstractButton(Action action) {
      setAction(action);
    }

    public Icon getIcon() {
      return icon;
    }

    public final void setIcon(Icon icon) {
      this.icon = icon;
    }

    public Icon getPressedIcon() {
      return pressedIcon;
    }

    public final void setPressedIcon(Icon pressedIcon) {
      this.pressedIcon = pressedIcon;
    }

    public Icon getRolloverIcon() {
      return rolloverIcon;
    }

    public final void setRolloverIcon(Icon rolloverIcon) {
      this.rolloverIcon = rolloverIcon;
    }

    @Override
    public String toString() {

      StringBuilder buffer = new StringBuilder(" {armed = ");

      buffer.append(isArmed());
      buffer.append(", enabled = ").append(isEnabled());
      buffer.append(", icon = ").append(getIcon());
      buffer.append(", location = ").append(getLocation());
      buffer.append(", pressedIcon = ").append(getPressedIcon());
      buffer.append(", rolloverIcon = ").append(getRolloverIcon());
      buffer.append(", size = ").append(getSize());
      buffer.append(", triggered = ").append(isTriggered());
      buffer.append(" }:").append(getClass().getName());

      return buffer.toString();
    }
  }

  /**
   * The AbstractDay class defines a calendar UI subcomponent representation of a calendar day.  Details of appearance
   * are left to subclasses.
   */
  private abstract class AbstractDay extends ActionableCalendarComponent implements Day {

    private boolean selected;

    private int dayOfMonth= 1;

    private Color color;
    private Color rolloverColor;
    private Color selectedColor;

    private Font font;
    private Font rolloverFont;
    private Font selectedFont;

    public Color getColor() {
      return this.color;
    }

    public void setColor(Color color) {
      this.color = color;
    }

    public int getDayOfMonth() {
      return this.dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
      this.dayOfMonth = dayOfMonth;
    }

    public Font getFont() {
      return this.font;
    }

    public void setFont(Font font) {
      this.font = font;
    }

    public Color getRolloverColor() {
      return this.rolloverColor;
    }

    public void setRolloverColor(Color rolloverColor) {
      this.rolloverColor = rolloverColor;
    }

    public Font getRolloverFont() {
      return this.rolloverFont;
    }

    public void setRolloverFont(Font rolloverFont) {
      this.rolloverFont = rolloverFont;
    }

    public boolean isSelected() {
      return this.selected;
    }

    public void setSelected(boolean selected) {
      this.selected = selected;
    }

    public Color getSelectedColor() {
      return this.selectedColor;
    }

    public void setSelectedColor(Color selectedColor) {
      this.selectedColor = selectedColor;
    }

    public Font getSelectedFont() {
      return this.selectedFont;
    }

    public void setSelectedFont(Font selectedFont) {
      this.selectedFont = selectedFont;
    }

    @Override
    public String toString() {

      StringBuilder buffer = new StringBuilder("{armed = ");

      buffer.append(isArmed());
      buffer.append(", color = ").append(getColor());
      buffer.append(", dayOfMonth = ").append(getDayOfMonth());
      buffer.append(", enabled = ").append(isEnabled());
      buffer.append(", font = ").append(getFont());
      buffer.append(", location = ").append(getLocation());
      buffer.append(", rolloverColor = ").append(getRolloverColor());
      buffer.append(", rolloverFont = ").append(getRolloverFont());
      buffer.append(", selected = ").append(isSelected());
      buffer.append(", selectedColor = ").append(getSelectedColor());
      buffer.append(", selectedFont = ").append(getSelectedFont());
      buffer.append(", size = ").append(getSize());
      buffer.append(", triggered = ").append(isTriggered());
      buffer.append("}:").append(getClass().getName());

      return buffer.toString();
    }
  }

  /**
   * The BasicButton class specifies the look and feel of a calendar button for the basic look and feel.
   */
  private class BasicButton extends AbstractButton {

    public BasicButton(Action action) {
      super(action);
    }

    public BasicButton(Action action, Icon icon) {
      super(action);
      setIcon(icon);
    }

    public BasicButton(Action action, Icon icon, Icon pressedIcon, Icon rolloverIcon) {
      super(action);
      setIcon(icon);
      setPressedIcon(pressedIcon);
      setRolloverIcon(rolloverIcon);
    }

    private Color getBorderColor() {
      return isTriggered() || isArmed() ? Color.black : Color.lightGray;
    }

    private Icon getDisplayIcon() {
      return isTriggered() ? getPressedIcon() : (isArmed() ? getRolloverIcon() : getIcon());
    }

    protected void handleMousePressed(MouseEvent event) { }

    protected void handleMouseReleased(MouseEvent event) {
      doAction(new ActionEvent(event.getSource(), ActionEvent.ACTION_PERFORMED, null));
    }

    public void paint(Graphics2D g2, JCalendar calendar) {

      Point2D location = getLocation();

      getDisplayIcon().paintIcon(calendar, g2, (int) location.getX(), (int) location.getY()); // paint icon
      g2.setColor(getBorderColor());
      //g2.draw(GraphicsUtil.getRectangle(getLocation(), getSize())); // paint border
    }
  }

  /**
   * The BasicDay class specifies the look and feel of the calendar day for the basic look and feel.
   */
  private class BasicDay extends AbstractDay {

    private String getLabel() {
      return getDayOfMonth() < 10 ? "0" + getDayOfMonth() : String.valueOf(getDayOfMonth());
    }

    private Color getDisplayColor() {

      return isEnabled()
        ? (isSelected() ? getSelectedColor() : (isArmed() ? getRolloverColor() : getColor()))
        : DISABLED_COLOR;
    }

    private Font getDisplayFont() {
      return isSelected()  ? getSelectedFont()
        : (isArmed() ? getRolloverFont() : getFont());
    }

    protected void handleMousePressed(MouseEvent event) {
      doAction(new ActionEvent(event.getSource(), ActionEvent.ACTION_PERFORMED, null));
    }

    protected void handleMouseReleased(MouseEvent event) { }

    public void paint(Graphics2D g2, JCalendar calendar) {

      String label = getLabel();

      Point2D labelLocation =
        getLabelPosition(g2.getFontMetrics(getDisplayFont()), label, GraphicsUtil.getRectangle(getLocation(), getSize()));

      g2.setColor(getDisplayColor());
      g2.setFont(getDisplayFont());
      g2.drawString(label, (float) labelLocation.getX(), (float) labelLocation.getY());
    }
  }
}
