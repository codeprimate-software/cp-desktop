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

import java.util.Calendar;
import java.util.Date;

import org.cp.elements.lang.annotation.NotNull;

@SuppressWarnings("unused")
public class DefaultTimeModel extends DefaultDateModel implements TimeModel {

  /**
   * Creates an instance of the DateFieldModel class to model time initialized to the specified Calendar object.
   * @param calendar a Calendar object specifying the time to initialize the model.
   */
  public DefaultTimeModel(@NotNull Calendar calendar) {
    super(calendar);
  }

  /**
   * Creates an instance of the DateFieldModel class to model time initialized to the specified Date object.
   * @param date a Date object specifying the time to initialize the model.
   */
  public DefaultTimeModel(@NotNull Date date) {
    super(date);
  }

  /**
   * Returns the time from the Calendar object represented by this DateFieldModel class.
   * @return a Calendar signifying the time for this time model.
   */
  public Calendar getTime() {
    return getCalendar();
  }

  /**
   * Sets the time property to the specified Calendar object containing time information.
   * @param time the Calendar object used to keep track of the current time.
   */
  public void setTime(Calendar time) {
    setCalendar(time);
  }

  /**
   * Determines whether the specified hour is valid.
   * @param hour an integer value specifying the hour.
   * @return a boolean value indicating if the hour is valid.  Returns true if 0 <= hour < 24,
   * false otherwise.
   */
  public static boolean isValidHour(int hour) {
    return hour >= 0 && hour < 24;
  }

  /**
   * Determines whether the specified minute is valid.
   * @param minute an integer value specifying the minute.
   * @return a boolean value indicating if the minute is valid.  Returns true if 0 <= minute < 60,
   * false otherwise.
   */
  public static boolean isValidMinute(int minute) {
    return minute >= 0 && minute < 60;
  }

  /**
   * Determines whether the specified second is valid.
   * @param second an integer value specifying the second.
   * @return a boolean value indicating if the second is valid.  Returns true if 0 <= second < 60,
   * false otherwise.
   */
  public static boolean isValidSecond(int second) {
    return second >= 0 && second < 60;
  }

  /**
   * Decrements the hour of day by 1 unit.
   * @param calendar the Calendar object in which the hour of day will be adjusted.
   */
  protected int decrementHour(@NotNull Calendar calendar) {

    int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

    if (currentHour > 0) {
      calendar.roll(Calendar.HOUR_OF_DAY, false);
    }
    else {
      decrementDay(calendar);
      calendar.set(Calendar.HOUR_OF_DAY, 23);
    }

    return calendar.get(Calendar.HOUR_OF_DAY);
  }

  /**
   * Decrements the minute in the hour by 1 unit.
   * @param calendar the Calendar object in which the minute of the hour will be adjusted.
   */
  protected int decrementMinute(@NotNull Calendar calendar) {

    int currentMinute = calendar.get(Calendar.MINUTE);

    if (currentMinute > 0) {
      calendar.roll(Calendar.MINUTE, false);
    }
    else {
      decrementHour(calendar);
      calendar.set(Calendar.MINUTE, 59);
    }

    return calendar.get(Calendar.MINUTE);
  }

  /**
   * Decrements the second in a minute by 1 unit.
   * @param calendar the Calendar object in which the second in the minute will be adjusted.
   */
  protected int decrementSecond(@NotNull Calendar calendar) {

    int currentSecond = calendar.get(Calendar.SECOND);

    if (currentSecond > 0) {
      calendar.roll(Calendar.SECOND, false);
    }
    else {
      decrementMinute(calendar);
      calendar.set(Calendar.SECOND, 59);
    }

    return calendar.get(Calendar.SECOND);
  }

  /**
   * Increments the hour in a day by 1 unit.
   * @param calendar the Calendar object in which the hour of day will be adjusted.
   */
  protected int incrementHour(@NotNull Calendar calendar) {

    int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

    if (currentHour < 23) {
      calendar.roll(Calendar.HOUR_OF_DAY, true);
    }
    else {
      incrementDay(calendar);
      calendar.set(Calendar.HOUR_OF_DAY, 0);
    }

    return calendar.get(Calendar.HOUR_OF_DAY);
  }

  /**
   * Increments the minute in an hour by 1 unit.
   * @param calendar the Calendar object in which the minute in an hour will be adjusted.
   */
  protected int incrementMinute(@NotNull Calendar calendar) {

    int currentMinute = calendar.get(Calendar.MINUTE);

    if (currentMinute < 59) {
      calendar.roll(Calendar.MINUTE, true);
    }
    else {
      incrementHour(calendar);
      calendar.set(Calendar.MINUTE, 0);
    }

    return calendar.get(Calendar.MINUTE);
  }

  /**
   * Increments the second in a minute by 1 unit.
   * @param calendar the Calendar object in which the second in a minute will be adjusted.
   */
  protected int incrementSecond(@NotNull Calendar calendar) {

    int currentSecond = calendar.get(Calendar.SECOND);

    if (currentSecond < 59) {
      calendar.roll(Calendar.SECOND, true);
    }
    else {
      incrementMinute(calendar);
      calendar.set(Calendar.SECOND, 0);
    }

    return calendar.get(Calendar.SECOND);
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
      case Calendar.HOUR_OF_DAY:
        rollHour(calendar, up);
        break;
      case Calendar.MINUTE:
        rollMinute(calendar, up);
        break;
      case Calendar.SECOND:
        rollSecond(calendar, up);
        break;
      default:
        super.roll(calendarField, up);
    }

    setCalendar(calendar);
  }

  /**
   * Increments or decrements, according to the up boolean parameter, the hour of day by 1 unit.
   * @param calendar the Calendar object who's hour of day value will be modified.
   * @param up a boolean value determining whether to increment or decrement the hour of day.
   */
  private int rollHour(Calendar calendar, boolean up) {
    return up ? incrementHour(calendar) : decrementHour(calendar);
  }

  /**
   * Increments or decrements, according to the up boolean parameter, the minute in an hour by 1 unit.
   * @param calendar the Calendar object who's minute in an hour value will be modified.
   * @param up a boolean value determining whether to increment or decrement the minute in an hour.
   */
  private int rollMinute(Calendar calendar, boolean up) {
    return up ? incrementMinute(calendar) : decrementMinute(calendar);
  }

  /**
   * Increments or decrements, according to the up boolean parameter, the second in a minute by 1 unit.
   * @param calendar the Calendar object who's second in a minute value will be modified.
   * @param up a boolean value determining whether to increment or decrement the second in a minute.
   */
  private int rollSecond(Calendar calendar, boolean up) {
    return up ? incrementSecond(calendar) : decrementSecond(calendar);
  }

  public void toggleAmPm() {

    Calendar calendar = getCalendar();

    if (calendar.get(Calendar.AM_PM) == Calendar.AM) {
      calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 12);
      calendar.set(Calendar.AM_PM, Calendar.PM);
    }
    else {
      calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 12);
      calendar.set(Calendar.AM_PM, Calendar.AM);
    }

    setCalendar(calendar);
  }

  /**
   * Sets the date field, specified by the Calendar constant, to the corresponding value.
   * @param calendarField a Calendar constant referring to the date field to set with the value.
   * @param value an integer value for the second, minute, hour, day, month or year as determined
   * by the Calendar constant.
   * @throws IllegalArgumentException if the calendarField is not a valid date field,
   * or Calendar constant.
   */
  public void set(int calendarField, int value) {

    Calendar calendar = getCalendar();

    switch (calendarField) {
      case Calendar.HOUR_OF_DAY:
        setHour(calendar, value);
        break;
      case Calendar.MINUTE:
        setMinute(calendar, value);
        break;
      case Calendar.SECOND:
        setSecond(calendar, value);
        break;
      default:
        super.set(calendarField, value);
    }

    setCalendar(calendar);
  }

  /**
   * Sets the hour of day on the specified Calendar object to the corresponding value.
   * @param calendar the Calendar object in which to set the hour of day.
   * @param hour an integer value for the hour of day.
   */
  private void setHour(Calendar calendar, int hour) {

    if (isValidHour(hour)) {

      int currentHourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

      currentHourOfDay = currentHourOfDay * 10 + hour;

      if (isValidHour(currentHourOfDay)) {
        hour = currentHourOfDay;
      }

      calendar.set(Calendar.HOUR_OF_DAY, hour);
    }
    else {
      throw newIllegalArgumentException("Hour [%d] is not valid", hour);
    }
  }

  /**
   * Sets the minute in an hour on the specified Calendar object to the corresponding value.
   * @param calendar the Calendar object in which to set the minute in an hour.
   * @param minute an integer value for the minute in an hour.
   */
  private void setMinute(Calendar calendar, int minute) {

    if (isValidMinute(minute)) {

      int currentMinute = calendar.get(Calendar.MINUTE);

      currentMinute = currentMinute * 10 + minute;

      if (isValidMinute(currentMinute)) {
        minute = currentMinute;
      }

      calendar.set(Calendar.MINUTE, minute);
    }
    else {
      throw newIllegalArgumentException("Minute [%d] is not valid", minute);
    }
  }

  /**
   * Sets the second in a minute on the specified Calendar object to the corresponding value.
   * @param calendar the Calendar object in which to set the second in a minute.
   * @param second an integer value for the second in a minute.
   */
  private void setSecond(Calendar calendar, int second) {

    if (isValidSecond(second)) {

      int currentSecond = calendar.get(Calendar.SECOND);

      currentSecond = currentSecond * 10 + second;

      if (isValidSecond(currentSecond)) {
        second = currentSecond;
      }

      calendar.set(Calendar.SECOND, second);
    }
    else {
      throw newIllegalArgumentException("Second [%d] is not valid", second);
    }
  }
}
