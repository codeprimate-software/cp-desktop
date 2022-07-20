/*
 * NumberOutOfBoundsVetoableChangeListener.java (c) 30 January 2005
 *
 * Copyright (c) 2003, Code Primate
 * All Rights Reserved
 * @author John J. Blum
 * @version 2008.9.14
 * @see com.cp.common.beans.event.AbstractVetoableChangeListener
 * @see com.cp.common.beans.event.BoundedNumberVetoableChangeListener
 * @see com.cp.common.beans.event.IllegalUseOfListenerException
 * @see com.cp.common.util.Filter
 */
package org.cp.desktop.swing.event;

import static org.cp.elements.lang.LangExtensions.assertThat;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.util.Objects;

import org.cp.elements.lang.Assert;
import org.cp.elements.lang.Filter;
import org.cp.elements.lang.IllegalTypeException;

@SuppressWarnings("unused")
public class NumberOutOfBoundsVetoableChangeListener<N extends Number> extends AbstractVetoableChangeListener implements Filter<N> {

  private final N maxValue;
  private final N minValue;

  /**
   * Creates an instance of the NumberOutOfBoundsVetoableChangeListener class to constrain numerical property
   * values between the specified min and max range.
   * @param minValue a Number object specifying the minimum numerical value.
   * @param maxValue a Number object specifying the maximum numerical value.
   */
  public NumberOutOfBoundsVetoableChangeListener(N minValue, N maxValue) {
    this(null, minValue, maxValue);
  }

  /**
   * Creates an instance of the NumberOutOfBoundsVetoableChangeListener class to constrain the specified numerical
   * property value between the specified min and max range.
   * @param propertyName a String value specifying the name of the property to constrain.
   * @param minValue a Number object specifying the minimum numerical value.
   * @param maxValue a Number object specifying the maximum numerical value.
   */
  public NumberOutOfBoundsVetoableChangeListener(String propertyName, N minValue, N maxValue) {

    super(propertyName);

    Assert.notNull(minValue, "The minimum value cannot be null!");
    Assert.notNull(maxValue, "The maximum value cannot be null!");

    assertThat(maxValue)
      .describedAs("The minimum number value [%d] cannot be greater than the maximum number value [%d]",
        minValue, maxValue)
      .isGreaterThanEqualTo(minValue);

    this.minValue = minValue;
    this.maxValue = maxValue;
  }

  /**
   * Returns the maximum numerical value (upper bound).
   * @return a Number object specifying the maximum numeric value.
   */
  public N getMaxValue() {
    return maxValue;
  }

  /**
   * Returns the minimum numerical value (lower bound).
   * @return a Number object specifying the minimum numeric value.
   */
  public N getMinValue() {
    return minValue;
  }

  /**
   * Determines whether the specified numeric value satisfies the constraints of this listener.
   * @param value the numeric value in question.
   * @return a boolean value if the specified numeric value satisfies the constraints of this listener.
   */
  public boolean accept(N value) {
    return Objects.isNull(value) || (value.doubleValue() >= getMinValue().doubleValue()
      && value.doubleValue() <= getMaxValue().doubleValue());
  }

  /**
   * Event handler method triggered by the event source for changes in numerical property values.
   * @param event the event object containing information pertaining to the property change.
   * @throws PropertyVetoException if the numerical value of the property violates the contraints
   * imposed by the numerical range (min and max numeric values) of this listener.
   */
  @SuppressWarnings("unchecked")
  protected void handle(PropertyChangeEvent event) throws PropertyVetoException {

    try {
      N value = (N) event.getNewValue();

      if (!accept(value)) {
        throw new PropertyVetoException("The new numeric value (" + value + ") must be between the minimum ("
          + getMinValue() + ") and maximum (" + getMaxValue() +") numerical values inclusive!", event);
      }
    }
    catch (ClassCastException cause) {
      throw new IllegalTypeException("Property (" + event.getPropertyName() + ") of bean ("
        + event.getSource().getClass().getName() + ") is not of type Number!", cause);
    }
  }
}
