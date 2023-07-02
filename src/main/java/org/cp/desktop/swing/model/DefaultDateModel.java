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

import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;

import org.cp.elements.beans.AbstractBean;
import org.cp.elements.lang.annotation.NotNull;
import org.cp.elements.lang.annotation.Nullable;
import org.cp.elements.security.model.User;
import org.cp.elements.time.DateTimeUtils;
import org.cp.elements.time.Month;

@SuppressWarnings("unused")
public class DefaultDateModel extends AbstractBean<Integer, User<Integer>, String> implements DateModel {

  protected static final String DATE_MODEL_TO_STRING = "{ @type = %s, calendar = %s, currentDay = %s }";

  /**
   * Determines whether the given month is {@literal February}.
   *
   * @param month {@link Integer} between {@literal 1} and {@literal 12} for the month.
   * @return a boolean value indicating whether the given month is {@literal February}.
   * @see Month#FEBRUARY
   * @see Month
   */
  protected static boolean isFebruary(int month) {
    return Month.FEBRUARY.equals(Month.valueOfPosition(month));
  }

  /**
   * Determines whether the given year is a {@link Year#isLeap() Leap Year}.
   *
   * @param year {@link Integer} value specifying the calendar year.
   * @return a boolean value indicating whether the given year is a {@link Year#isLeap() Leap Year}.
   * @see java.time.Year#isLeap(long)
   * @see java.time.Year
   */
  protected static boolean isLeapYear(int year) {
    return Year.isLeap(year);
  }

  /**
   * Determines whether the given day is valid for the current month and the current year determined by {@link Calendar}.
   * <p>
   * February, non-leap year: 1 - 28 days
   * February in leap year: 1 - 29 days
   * January, March, May, July, August, October, December: 1 - 31 days.
   * April, June, September, November: 1 - 30 days
   *
   * @param day {@link Integer} between {@literal 1} and the maximum number of days for the current month
   * and the current year as determined by {@link Calendar}.
   * @return a boolean value indicating if the given day is valid for the current month and the current year.
   * @see #isValidDay(int, int, int)
   * @see java.util.Calendar
   */
  public static boolean isValidDay(int day) {
    Calendar calendar = Calendar.getInstance();
    return isValidDay(day, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
  }

  /**
   * Determines whether the given day and month is valid for the current year determined by {@link Calendar}.
   * <p>
   * February, non-leap year: 1 - 28 days
   * February in leap year: 1 - 29 days
   * January, March, May, July, August, October, December: 1 - 31 days.
   * April, June, September, November: 1 - 30 days
   *
   * @param day {@link Integer} between {@literal 1} and the maximum number of days for the given month
   * and current year determined by {@link Calendar}.
   * @param month {@link Integer} between {@literal 1} and {@literal 12} for the month.
   * @return a boolean value indicating if the given day and month are valid for the current year.
   * @see #isValidDay(int, int, int)
   * @see java.util.Calendar
   */
  public static boolean isValidDay(int day, int month) {
    return isValidDay(day, month, Calendar.getInstance().get(Calendar.YEAR));
  }

  /**
   * Determines whether the given day, month and year combination are valid.
   *
   * @param day {@link Integer} between {@literal 1} and the maximum number of days for the given month and given year.
   * @param month {@link Integer} between {@literal 1} and {@literal 12} for the month.
   * @param year {@link Integer} value for the year between {@literal 0} and {@link Integer#MAX_VALUE}.
   * @return a boolean value indicating if the given day, month and year combination are valid.
   */
  public static boolean isValidDay(int day, int month, int year) {

    // Normalize month if month is zero based, converting to 1-12.
    month = month == 0 ? normalizeMonth(month) : month;

    if (!isValidYear(year)) {
      throw newIllegalArgumentException("Year [%d] is not valid", year);
    }

    if (!isValidMonth(month)) {
      throw newIllegalArgumentException("Month [%d] is not valid", month);
    }

    int monthIndex = month - 1;
    int numberOfDaysInMonthAdjustment = isFebruary(month) && isLeapYear(year) ? 1 : 0;
    int numberOfDaysInMonth = DateTimeUtils.NUMBER_OF_DAYS_IN_MONTH[monthIndex] + numberOfDaysInMonthAdjustment;

    return day >= 1 && day <= numberOfDaysInMonth;
  }

  /**
   * Determines whether the given month is between {@literal 1} and {@literal 12}.
   *
   * @param month {@link Integer} between {@literal 1} and {@literal 12} for the month.
   * @return a boolean value indicating if the month is valid.
   */
  public static boolean isValidMonth(int month) {
    return month >= 1 && month <= 12;
  }

  /**
   * Determines whether the given year is valid.
   *
   * @param year {@link Integer} for the year.
   * @return a boolean value indicating if the year is valid;
   * {@literal true} if the year is greater than equal to {@literal 0}.
   */
  public static boolean isValidYear(int year) {
    return year >= 0;
  }

  /**
   * Normalizes the month represented as an {@link Integer}.
   * <p>
   * Shifts the month value by 1 if the month value is zero based. Note that {@link Calendar constants
   * for {@literal January} through {@literal December} are zero-based.
   * <p>
   * @param month {@link Integer} for the month.
   * @return a normalized {@link Integer}} for the month.
   */
  static int normalizeMonth(int month) {
    return month + 1;
  }

  private int currentDay;

  @SuppressWarnings("all")
  private Calendar calendar;

  /**
   * Constructs a new {@link DefaultDateModel} initialized with the given, required {@link Calendar}.
   *
   * @param calendar {@link Calendar} used to initialize this {@link DefaultDateModel}; must not be {@literal null}.
   * @see java.util.Calendar
   */
  public DefaultDateModel(@NotNull Calendar calendar) {
    this.calendar = DateTimeUtils.clone(calendar);
    this.currentDay = this.calendar.get(Calendar.DAY_OF_MONTH);
  }

  /**
   * Constructs a new {@link DefaultDateModel} initialized with the given, required {@link Date}.
   *
   * @param date {@link Date} used to initialize this {@link DefaultDateModel}; must not be {@literal null}.
   * @see java.util.Date
   */
  public DefaultDateModel(@NotNull Date date) {
    this(DateTimeUtils.create(date.getTime()));
  }

  // TODO: Add constructors for the java.time types; deprecate old constructors.

  /**
   * Returns the value of the Calendar property.  Note, this method makes a defensive copy of the Calendar
   * property value before the value is returned.
   * @return a Calendar object specifying the date value of the Calendar property.
   */
  public @Nullable Calendar getCalendar() {
    return DateTimeUtils.clone(this.calendar);
  }

  /**
   * Sets the value of the Calendar property.  Note, this method makes a defensive copy of the Calendar
   * parameter before the property is set.
   * @param calendar the Calendar object used to set the Calendar property.
   * @throws IllegalArgumentException if the value of the Calendar object is not valid.
   */
  public void setCalendar(@Nullable Calendar calendar) {
    processChange("calendar", getCalendar(), calendar);
  }

  /**
   * Returns the last modified current day of the month.
   * @return a integer value specifying the current day of the month.  Possible values are in the range of
   * 1 to 31, depending on month.  For example, if the month is February, then the maximum integer value
   * returned is 28.
   */
  private int getCurrentDay() {
    return this.currentDay;
  }

  /**
   * Sets the last modified current day of the month.
   * @param currentDay an integer value specifying the current day of the month.
   */
  protected final void setCurrentDay(int currentDay) {
    this.currentDay = currentDay;
  }

  /**
   * Returns the value of the Date property.  This method delegates to the getCalendar method.
   * @return the value of the Calendar property as a Date instance.
   * @see DefaultDateModel#getCalendar
   */
  public Date getDate() {
    return getCalendar().getTime();
  }

  /**
   * Sets the value of the Date property.  This method delegates to the setCalendar method.
   * @param date the Date object used to set the Calendar property.
   * @see DefaultDateModel#setCalendar
   */
  public void setDate(@NotNull Date date) {
    setCalendar(DateTimeUtils.create(date.getTime()));
  }

  /**
   * Decrements the current day to the previous day of the month, or to the last day of the previous month
   * if the current day is the 1st of the current month, or the last day of the previous month of the previous
   * year if the current day is the 1st of January.
   * @param calendar the Calendar object in which to set the date field values.
   */
  protected int decrementDay(@NotNull Calendar calendar) {

    int currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

    if (currentDayOfMonth > 1) {
      calendar.roll(Calendar.DAY_OF_MONTH, false);
    }
    else {

      decrementMonth(calendar);

      int currentMonth = calendar.get(Calendar.MONTH);
      int currentYear = calendar.get(Calendar.YEAR);
      int numberOfDaysInMonth = DateTimeUtils.NUMBER_OF_DAYS_IN_MONTH[currentMonth]
        + (isFebruary(currentMonth) && Year.of(currentYear).isLeap() ? 1 : 0);

      calendar.set(Calendar.DAY_OF_MONTH, numberOfDaysInMonth);
    }

    setCurrentDay(calendar.get(Calendar.DAY_OF_MONTH));

    return getCurrentDay();
  }

  /**
   * Decrements the current month to the previous month in the year, or to December of the previous year
   * if the current month is January.  This method will also adjust the current day of the month if the previous month
   * is February and the current day is greater than 28 and the year is not a leap year, or the previous month is
   * February and the day is greater than 29 and the year is a leap year, or the previous month is any month with
   * 30 days and the current day of month is greater than 30; in all situations above, the method will set the
   * current day of the month to the last day in the previous month of the year and keep track of the last modified
   * current day of the month.
   * @param calendar the Calendar object in which to set the date field values.
   */
  protected int decrementMonth(@NotNull Calendar calendar) {

    int currentCalendarMonth = calendar.get(Calendar.MONTH);

    if (currentCalendarMonth > Calendar.JANUARY) {

      calendar.set(Calendar.DAY_OF_MONTH, 1);
      calendar.roll(Calendar.MONTH, false);

      Month month = Month.valueOfCalendarMonth(currentCalendarMonth);

      int numberOfDaysInMonthAdjustment = isFebruary(month.getPreviousMonth().getCalendarMonth())
        && Year.of(calendar.get(Calendar.YEAR)).isLeap() ? 1 : 0;

      int numberOfDaysInMonth = DateTimeUtils.NUMBER_OF_DAYS_IN_MONTH[month.getPreviousMonth().getCalendarMonth()]
        + numberOfDaysInMonthAdjustment;

      calendar.set(Calendar.DAY_OF_MONTH, Math.min(getCurrentDay(), numberOfDaysInMonth));
    }
    else {
      calendar.set(Calendar.MONTH, Calendar.DECEMBER);
      decrementYear(calendar);
    }

    return calendar.get(Calendar.MONTH);
  }

  /**
   * Decrements the current year to the previous year.  This method will also adjust the current day of the month if
   * the current month of the year is February.  It will set the current day of the month in February to the last day
   * in February if the current day of the month is greater than 28 and the year is not a a leap year, or if the
   * current day of the month is greater than 29 and the year is a leap year respectively.
   * @param calendar the Calendar object in which to set the date field values.
   */
  protected int decrementYear(@NotNull Calendar calendar) {

    calendar.set(Calendar.DAY_OF_MONTH, 1);
    calendar.roll(Calendar.YEAR, false);

    if (isFebruary(calendar.get(Calendar.MONTH)) && getCurrentDay() > 28) {
      int numberOfDaysInMonth = 28 + (Year.of(calendar.get(Calendar.YEAR)).isLeap() ? 1 : 0);
      calendar.set(Calendar.DAY_OF_MONTH, Math.min(getCurrentDay(), numberOfDaysInMonth));
    }
    else {
      calendar.set(Calendar.DAY_OF_MONTH, getCurrentDay());
    }

    return calendar.get(Calendar.YEAR);
  }

  /**
   * Determines the correct value for the day of month based on the current month and year as specified by
   * the Calendar object.
   * @param calendar the Calendar object in which to adjust the day of month based the current values for
   * month and year.
   */
  protected void determineDay(@NotNull Calendar calendar) {

    int currentMonth = calendar.get(Calendar.MONTH);
    int currentYear = calendar.get(Calendar.YEAR);
    int numberOfDaysInMonthAdjustment = isFebruary(currentMonth) && Year.of(currentYear).isLeap() ? 1 : 0;
    int numberOfDaysInMonth = DateTimeUtils.NUMBER_OF_DAYS_IN_MONTH[currentMonth] + numberOfDaysInMonthAdjustment;

    calendar.set(Calendar.DAY_OF_MONTH, Math.min(getCurrentDay(), numberOfDaysInMonth));
  }

  /**
   * Increments the current day to the next day in the month, or the first day of the next month if the current
   * day is the last day in the current month, or to the first day of the month in the following year if the
   * current day of the month is December 31st.
   * @param calendar the Calendar object in which to set the date field values.
   */
  protected int incrementDay(@NotNull Calendar calendar) {

    int currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

    // NOTE: There are at least 28 days in every calendar month.
    if (currentDayOfMonth < 28) {
      calendar.roll(Calendar.DAY_OF_MONTH, true);
    }
    else {

      int currentMonth = calendar.get(Calendar.MONTH);
      int currentYear = calendar.get(Calendar.YEAR);
      int numberOfDaysInMonthAdjustment = isFebruary(currentMonth) && Year.of(currentYear).isLeap() ? 1 : 0;
      int numberOfDaysInMonth = DateTimeUtils.NUMBER_OF_DAYS_IN_MONTH[currentMonth] + numberOfDaysInMonthAdjustment;

      if (currentDayOfMonth < numberOfDaysInMonth) {
        calendar.roll(Calendar.DAY_OF_MONTH, true);
      }
      else {
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        setCurrentDay(1);
        incrementMonth(calendar);
      }
    }

    setCurrentDay(calendar.get(Calendar.DAY_OF_MONTH));

    return getCurrentDay();
  }

  /**
   * Increments the current month to the next month in the year, or to January of the following year if the
   * current month is December.  This method will also adjust the current day of the month if the next month
   * is February and the current day is greater than 28 and the year is not a leap year, or the next month is
   * February and the day is greater than 29 and the year is a leap year, or the next month is any month with
   * 30 days and the current day of month is greater than 30; in all situations above, the method will set the
   * current day of the month to the last day in the next month of the year and keep track of the last modified
   * current day of the month.
   * @param calendar the Calendar object in which to set the date field values.
   */
  protected int incrementMonth(@NotNull Calendar calendar) {

    int currentCalendarMonth = calendar.get(Calendar.MONTH);

    if (currentCalendarMonth < Calendar.DECEMBER) {

      calendar.set(Calendar.DAY_OF_MONTH, 1);
      calendar.roll(Calendar.MONTH, true);

      Month month = Month.valueOfCalendarMonth(currentCalendarMonth);

      int numberOfDaysInMonthAdjustment = isFebruary(month.getNextMonth().getCalendarMonth())
        && Year.of(calendar.get(Calendar.YEAR)).isLeap() ? 1 : 0;

      int numberOfDaysInMonth = DateTimeUtils.NUMBER_OF_DAYS_IN_MONTH[month.getNextMonth().getCalendarMonth()]
        + numberOfDaysInMonthAdjustment;

      calendar.set(Calendar.DAY_OF_MONTH, Math.min(getCurrentDay(), numberOfDaysInMonth));
    }
    else {
      calendar.set(Calendar.MONTH, Calendar.JANUARY);
      incrementYear(calendar);
    }

    return calendar.get(Calendar.MONTH);
  }

  /**
   * Increments the current year to the next year.  This method will also adjust the current day of the month if
   * the current month of the year is February.  It will set the current day of the month in February to the last day
   * in February if the current day of the month is greater than 28 and the year is not a a leap year, or if the
   * current day of the month is greater than 29 and the year is a leap year respectively.
   * @param calendar the Calendar object in which to set the date field values.
   */
  protected int incrementYear(@NotNull Calendar calendar) {

    calendar.set(Calendar.DAY_OF_MONTH, 1);
    calendar.roll(Calendar.YEAR, true);

    if (isFebruary(calendar.get(Calendar.MONTH)) && getCurrentDay() > 28) {

      int numberOfDaysInMonthAdjustment = Year.of(calendar.get(Calendar.YEAR)).isLeap() ? 1 : 0;
      int numberOfDaysInMonth = 28 + numberOfDaysInMonthAdjustment;

      calendar.set(Calendar.DAY_OF_MONTH, Math.min(getCurrentDay(), numberOfDaysInMonth));
    }
    else {
      calendar.set(Calendar.DAY_OF_MONTH, getCurrentDay());
    }

    return calendar.get(Calendar.YEAR);
  }

  /**
   * Increments or decrements the respective date field, specified by the Calendar constant, one unit in the units
   * of the specified date field.
   * @param calendarField a Calendar constant specifying the date field to roll.
   * @param up a boolean value determining whether to increment or decrement the specified date field.
   * @throws IllegalArgumentException if the calendarField is not a valid date field,
   * or Calendar constant.
   */
  public void roll(int calendarField, boolean up) {

    Calendar calendar = getCalendar();

    switch (calendarField) {
      case Calendar.DAY_OF_MONTH:
        rollDay(calendar, up);
        break;
      case Calendar.MONTH:
        rollMonth(calendar, up);
        break;
      case Calendar.YEAR:
        rollYear(calendar, up);
        break;
      default:
        throw newIllegalArgumentException("[%d] is not a valid Calendar field", calendarField);
    }

    setCalendar(calendar);
  }

  /**
   * Increments or decrements, according to the up boolean parameter, the day of month by 1 unit.
   * @param calendar the Calendar object who's day of month value will be modified.
   * @param up a boolean value determining whether to increment or decrement the day of month.
   */
  protected int rollDay(Calendar calendar, boolean up) {
    return up ? incrementDay(calendar) : decrementDay(calendar);
  }

  /**
   * Increments or decrements, according to the up boolean parameter, the month of year by 1 unit.
   * @param calendar the Calendar object who's month value will be modified.
   * @param up a boolean value determining whether to increment or decrement the month.
   */
  protected int rollMonth(Calendar calendar, boolean up) {
    return up ? incrementMonth(calendar) :decrementMonth(calendar);
  }

  /**
   * Increments or decrements, according to the up boolean parameter, the year by 1 unit.
   * @param calendar the Calendar object who's day of month value will be modified.
   * @param up a boolean value determining whether to increment or decrement the year.
   */
  protected int rollYear(Calendar calendar, boolean up) {
    return up ? incrementYear(calendar) : decrementYear(calendar);
  }

  /**
   * Sets the date field, specified by the Calendar constant, to the corresponding value.
   * @param calendarField a Calendar constant referring to the date field to set with the value.
   * @param value an integer value for the day, month or year as determined by the Calendar
   * constant.
   * @throws IllegalArgumentException if the calendarField is not a valid date field,
   * or Calendar constant.
   */
  public void set(int calendarField, int value) {

    Calendar calendar = getCalendar();

    switch (calendarField) {
      case Calendar.DAY_OF_MONTH:
        setDay(calendar, value);
        break;
      case Calendar.MONTH:
        setMonth(calendar, value);
        break;
      case Calendar.YEAR:
        setYear(calendar, value);
        break;
      default:
        throw newIllegalArgumentException("[%s] is not a valid Calendar field", calendarField);
    }

    setCalendar(calendar);
  }

  /**
   * Sets the day of month on the specified Calendar object to the corresponding value.
   * Note, this method is allowed to be overridden by subclasses to support calendar functionality.
   * @param calendar the Calendar object in which to set the day of month.
   * @param day an integer value for the day of month.
   */
  protected void setDay(@NotNull Calendar calendar, int day) {

    int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
    int currentMonth = calendar.get(Calendar.MONTH);
    int currentYear = calendar.get(Calendar.YEAR);
    int numberOfDaysInMonthAdjustment = isFebruary(currentMonth) && Year.of(currentYear).isLeap() ? 1 : 0;
    int numberOfDaysInMonth = DateTimeUtils.NUMBER_OF_DAYS_IN_MONTH[currentMonth] + numberOfDaysInMonthAdjustment;
    int newValue = currentDay * 10 + day;

    day = day == 0 ? 1 : day;
    calendar.set(Calendar.DAY_OF_MONTH, (newValue <= numberOfDaysInMonth ? newValue : day));
    setCurrentDay(calendar.get(Calendar.DAY_OF_MONTH));
  }

  /**
   * Sets the month on the specified Calendar object to the corresponding value.
   * @param calendar the Calendar object in which to set the month.
   * @param month an integer value for the month.  Valid values are between 1 and 12.
   */
  protected void setMonth(@NotNull Calendar calendar, int month) {

    int currentMonth = calendar.get(Calendar.MONTH);
    int newValue = normalizeMonth(currentMonth) * 10 + month;

    // Set day of month to the 1st so the Calendar does not roll if the current day of month
    // is more than the number of days in the new value for month.
    month = month == 0 ? 1 : month;
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    calendar.set(Calendar.MONTH, (newValue < 13 ? (newValue - 1) : (month - 1)));
    determineDay(calendar);
  }

  /**
   * Sets the year on the specified Calendar object to the corresponding value.
   * @param calendar the Calendar object in which to set the year.
   * @param year an integer value for the year.
   */
  protected void setYear(@NotNull Calendar calendar, int year) {

    year %= 10; // Ensure that the number key pressed does not exceed 9.
    year = year == 0 ? 1 : year;
    // Set day of month to the 1st so the Calendar does not roll the month if the current day of month
    // is more than the number of days in the current month for the new year.
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    calendar.set(Calendar.YEAR, year * 1000);
    determineDay(calendar);
  }

  @Override
  public void register(PropertyChangeListener listener) {
    super.register(listener);
  }

  @Override
  public void register(String propertyName, PropertyChangeListener listener) {
    super.register(propertyName, listener);
  }

  @Override
  public void register(VetoableChangeListener listener) {
    super.register(listener);
  }

  @Override
  public void register(String propertyName, VetoableChangeListener listener) {
    super.register(propertyName, listener);
  }

  @Override
  public void unregister(PropertyChangeListener listener) {
    super.unregister(listener);
  }

  @Override
  public void unregister(VetoableChangeListener listener) {
    super.unregister(listener);
  }

  /**
   * Returns a {@link String} representation of {@literal this} {@link DefaultDateModel}.
   *
   * @return a {@link String} describing {@literal this} {@link DefaultDateModel}.
   */
  @Override
  public String toString() {
    return String.format(DATE_MODEL_TO_STRING, getClass().getName(), getCalendar(), getCurrentDay());
   }
}
