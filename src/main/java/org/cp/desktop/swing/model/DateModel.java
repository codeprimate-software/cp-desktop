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
import java.beans.VetoableChangeListener;
import java.util.Calendar;
import java.util.Date;

import org.cp.desktop.swing.JDateField;
import org.cp.elements.beans.Bean;
import org.cp.elements.security.model.User;

/**
 * Interface used to describe the data and operations on the data required by this {@link JDateField} component.
 */
@SuppressWarnings("unused")
public interface DateModel extends Bean<Integer, User<Integer>, String> {

  Calendar getCalendar();

  void setCalendar(Calendar calendar);

  default Date getDate() {
    return getCalendar().getTime();
  }

  default void setDate(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.clear();
    calendar.setTime(date);
    setCalendar(calendar);
  }

  void register(PropertyChangeListener listener);

  void register(VetoableChangeListener listener);

  void roll(int field, boolean up);

  void set(int field, int value);

  void unregister(PropertyChangeListener listener);

  void unregister(VetoableChangeListener listener);

}
