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
package org.cp.desktop.swing.model;

import static org.cp.elements.lang.RuntimeExceptionsFactory.newIllegalArgumentException;

import java.time.Year;
import java.util.Calendar;
import java.util.Objects;
import java.util.Optional;

import org.cp.elements.lang.NumberUtils;
import org.cp.elements.time.DateTimeUtils;
import org.cp.elements.time.Weekday;

@SuppressWarnings("unused")
public class DefaultCalendarModel extends DefaultDateModel implements CalendarModel {

  private boolean monthYearChanged = false;

  private int theMonth = 0;
  private int theYear = 0;

  /**
   * Creates a default instance of the DefaultCalendarModel class initialized to the current date and time.
   */
  public DefaultCalendarModel() {
    this(Calendar.getInstance());
  }

  /**
   * Creates an instance of the DefaultCalendarModel class initialized to the specified date and time.
   * @param calendar a Calendar object specifying the date and time used to initialize this instance of the DefaultCalendarModel.
   */
  public DefaultCalendarModel(Calendar calendar) {
    super(calendar);
    init();
  }

  /**
   * Stores the current month and year state of the DefaultCalendarModel, called by the constructor to initialize the
   * monthYearChanged model property.
   */
  private void init() {

    Calendar currentCalendar = getCalendar();

    if (Objects.nonNull(currentCalendar)) {
      theMonth = currentCalendar.get(Calendar.MONTH);
      theYear = currentCalendar.get(Calendar.YEAR);
    }
  }

  /**
   * Overridden DefaultDateModel.setCalendar method used to update the monthYearChanged property value of this model upon
   * calendar changes.
   * @param calendar the Calendar object represented by this DefaultCalendarModel, used to set the calendar property.
   */
  public void setCalendar(Calendar calendar) {
    super.setCalendar(calendar);
    updateMonthYearChange();
  }

  /**
   * Overridden DefaultDateModel.setDay method to set the current day of the month on the calendar and implement the desired
   * behavior for the JCalendar Swing UI component.
   * @param calendar the Calendar object in which to set the day of month.
   * @param day an integer value for the day of month.
   */
  protected void setDay(Calendar calendar, int day) {
    calendar.set(Calendar.DAY_OF_MONTH, day);
    setCurrentDay(calendar.get(Calendar.DAY_OF_MONTH));
  }

  /**
   * Returns a week day name for the given day of week specified as a Calendar constant (for example, Calendar.FRIDAY).
   * @param calendarDay an integer value Calendar constant specifying the day of week.
   * @return a String value description for the day of week.
   */
  private String getDayOfWeekDescription(int calendarDay) {

    return Optional.ofNullable(Weekday.valueOfCalendarDay(calendarDay))
      .map(Weekday::getName)
      .orElse("Unknown");
  }

  /**
   * Returns a integer value specifying the index for the day of the week.
   * @param dayOfWeek is a Calendar constant indicating the week day (for example, Calendar.FRIDAY).
   * @return a integer value specifying the index for the week day.
   */
  private int getDayOfWeekIndex(int dayOfWeek) {

    return Optional.ofNullable(Weekday.valueOfCalendarDay(dayOfWeek))
      .map(Weekday::getPosition)
      .map(position -> position - 1)
      .orElseThrow(() -> newIllegalArgumentException("Weekday [%s] is not valid", getDayOfWeekDescription(dayOfWeek)));
  }

  /**
   * Returns an integer value indicating the first day of the month as a week day (for example, as in TGIF,
   * Calendar.FRIDAY), regardless of the month or year.
   * @param dayOfMonth an integer value specifying the day of month.  For example, today's date is April 4th, 2005,
   * therefore the day of month is 4.
   * @param dayOfWeek a Calendar constant indicating the day of week that the day of month falls on.  For example,
   * today is Monday, April 4th, 2005, therefore, the day of week is Calendar.MONDAY.
   * @return an integer value specifying the day of week as a Calendar constant, for example Calendar.THURSDAY,
   * indicating the first day of the month.
   */
  public int getFirstDayOfMonth(int dayOfMonth, int dayOfWeek) {

    // Optimization note...
    // If it is the first of the month, then just return the day of the week.
    // Note, however, that first of the month determination algorithm would still come up with day of the week.
    if (dayOfMonth == 1) {
      return dayOfWeek;
    }

    int difference = (dayOfMonth % 7) - 1;
    int firstDayOfMonthIndex = getDayOfWeekIndex(dayOfWeek) - difference;

    if (NumberUtils.isNegative(firstDayOfMonthIndex)) {
      firstDayOfMonthIndex += 7;
    }
    else if (firstDayOfMonthIndex == 7) {
      firstDayOfMonthIndex = 0;
    }

    int position = firstDayOfMonthIndex + 1;

    return Weekday.valueOfPosition(position).getCalendarDay();
  }

  /**
   * Peforms a destructive read of the monthYearChanged property value.  This property value indicates if either the
   * month or the year has changed since the last calendar property update, the last read operation on this property,
   * which ever happened last.  If just the day of the calendar changes, then the value of the property will be false.
   * @return a boolean value indicating whether the month or year changed since the last read on this property value.
   */
  public boolean getMonthYearChanged() {
    boolean previousMonthYearChanged = isMonthYearChanged();
    setMonthYearChanged(false);
    return previousMonthYearChanged;
  }

  /**
   * Reads the current value of the monthYearChanged property.  This property value indicates if either the
   * month or the year has changed since the last calendar property update.  If just the day of the calendar changes,
   * then the value of the property will be false.
   * @return a boolean value indicating whether the month or year changed since the calendar update.
   */
  public boolean isMonthYearChanged() {
    return monthYearChanged;
  }

  /**
   * Sets the value of the monthYearChanged property upon calendar change events.
   * @param monthYearChanged a boolean value indicating the value of the monthYearChanged property.
   */
  private void setMonthYearChanged(boolean monthYearChanged) {
    this.monthYearChanged = monthYearChanged;
  }

  /**
   * Returns the number of days in the specified month, expressed as a Calendar constant, for the current year.
   * @param month a Calendar constant (for example, Calendar.MAY) specifying the month of the current year.
   * @return an integer value specifying the number of days in the specified month for the current year.
   */
  public int getNumberOfDaysInMonth(int month) {
    return getNumberOfDaysInMonth(month, Calendar.getInstance().get(Calendar.YEAR));
  }

  /**
   * Returns the number of days for the specified month, expressed as a Calendar constant, for the specified year.
   * @param month a Calendar constant (for example, Calendar.MAY) specifying the month of the specified year.
   * @param year an integer value specifying the year used in determining the number of days in the specified month.
   * Note, year only matters for February in leap years.
   * @return an integer value specifying the number of days in the specified month of the specified year.
   */
  public int getNumberOfDaysInMonth(int month, int year) {

    int numberOfDaysInMonth = DateTimeUtils.NUMBER_OF_DAYS_IN_MONTH[month];

    numberOfDaysInMonth = isFebruary(month) && Year.of(year).isLeap() ? numberOfDaysInMonth + 1 : numberOfDaysInMonth;

    return numberOfDaysInMonth;
  }

  /**
   * Decrements the calendar's month.
   */
  public void decrementMonth() {
    roll(Calendar.MONTH, false);
  }

  /**
   * Decrements the calendar's year.
   */
  public void decrementYear() {
    roll(Calendar.YEAR, false);
  }

  /**
   * Increments the calendar's month.
   */
  public void incrementMonth() {
    roll(Calendar.MONTH, true);
  }

  /**
   * Increments the calendar's year.
   */
  public void incrementYear() {
    roll(Calendar.YEAR, true);
  }

  /**
   * Determines the value of the monthYearChanged property value.
   */
  private void updateMonthYearChange() {

    Calendar currentCalendar = getCalendar();

    int currentMonth = 0;
    int currentYear = 0;

    if (Objects.nonNull(currentCalendar)) {
      currentMonth = currentCalendar.get(Calendar.MONTH);
      currentYear = currentCalendar.get(Calendar.YEAR);
    }

    setMonthYearChanged((this.theMonth != currentMonth) || (this.theYear != currentYear));

    this.theMonth = currentMonth;
    this.theYear = currentYear;
  }
}
