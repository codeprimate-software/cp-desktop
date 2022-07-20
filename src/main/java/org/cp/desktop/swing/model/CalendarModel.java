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

import java.beans.PropertyChangeListener;

@SuppressWarnings("unused")
public interface CalendarModel extends DateModel {

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
  int getFirstDayOfMonth(int dayOfMonth, int dayOfWeek);

  /**
   * Peforms a destructive read of the monthYearChanged property value.  This property value indicates if either the
   * month or the year has changed since the last calendar property update, the last read operation on this property,
   * which ever happened last.  If just the day of the calendar changes, then the value of the property will be false.
   * @return a boolean value indicating whether the month or year changed since the last read on this property value.
   */
  boolean getMonthYearChanged();

  /**
   * Reads the current value of the monthYearChanged property.  This property value indicates if either the
   * month or the year has changed since the last calendar property update.  If just the day of the calendar changes,
   * then the value of the property will be false.
   * @return a boolean value indicating whether the month or year changed since the calendar update.
   */
  boolean isMonthYearChanged();

  /**
   * Returns the number of days in the specified month, expressed as a Calendar constant, for the current year.
   * @param month a Calendar constant (for example, Calendar.MAY) specifying the month of the current year.
   * @return an integer value specifying the number of days in the specified month for the current year.
   */
  int getNumberOfDaysInMonth(int month);

  /**
   * Returns the number of days for the specified month, expressed as a Calendar constant, for the specified year.
   * @param month a Calendar constant (for example, Calendar.MAY) specifying the month of the specified year.
   * @param year an integer value specifying the year used in determining the number of days in the specified month.
   * Note, year only matters for February in leap years.
   * @return an integer value specifying the number of days in the specified month of the specified year.
   */
  int getNumberOfDaysInMonth(int month, int year);

  /**
   * Decrements the calendar's month.
   */
  void decrementMonth();

  /**
   * Decrements the calendar's year.
   */
  void decrementYear();

  /**
   * Increments the calendar's month.
   */
  void incrementMonth();

  /**
   * Increments the calendar's year.
   */
  void incrementYear();

  void register(String propertyName, PropertyChangeListener listener);

  /**
   * Sets the date field, specified by the Calendar constant, to the corresponding value.
   * @param calendarField a Calendar constant referring to the date field to set with the value.
   * @param value an integer value for the day, month or year as determined by the Calendar
   * constant.
   * @throws IllegalArgumentException if the calendarField is not a valid date field,
   * or Calendar constant.
   */
  void set(int calendarField, int value);

}
