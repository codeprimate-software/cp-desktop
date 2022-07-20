/*
 * AbstractListener.java (c) 26 December 2006
 *
 * Copyright (c) 2003, Code Primate
 * All Rights Reserved
 * @author John J. Blum
 * @version 2008.9.4
 * @see com.cp.common.beans.util.BeanUtil
 * @see com.cp.common.beans.util.NoSuchPropertyException
 * @see java.util.EventListener
 */
package org.cp.desktop.swing.event;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.EventListener;
import java.util.Objects;

import org.cp.elements.beans.PropertyNotFoundException;
import org.cp.elements.lang.Assert;
import org.cp.elements.lang.ObjectUtils;

@SuppressWarnings("unused")
public abstract class AbstractListener implements EventListener {

  private final BeanInfo beanInfo;

  private final Object bean;

  private final String propertyName;

  /**
   * Creates an instance of the AbstractListener class.
   */
  public AbstractListener() {

    this.bean = null;
    this.beanInfo = null;
    this.propertyName = null;
  }

  /**
   * Creates an instance of the AbstractListener class initialized with an object reference to the
   * annotated bean object.  The object reference is used to introspect the bean in order to read annotations
   * used by this listener to handle event processing.
   * @param annotatedBean the annotated bean object.
   */
  public AbstractListener(Object annotatedBean) {
    this(annotatedBean, null);
  }

  /**
   * Creates an instance of the AbstractListener class initialized with the specified property name.
   * This listener will only handle property change events from the registered bean for the specified property.
   * @param propertyName the name of the property that this listener will handle and process property change events for.
   */
  public AbstractListener(String propertyName) {

    Assert.hasText(propertyName, "The name of the property cannot be empty!");

    this.bean = null;
    this.beanInfo = null;
    this.propertyName = propertyName;
  }

  /**
   * Creates an instance of the AbstractListener class initialized with an object reference to the
   * annotated bean object as well as the specifed property name for which property change events will be handled.
   * The object reference is used to introspect the bean in order to read annotations used by this listener to handle
   * event processing.  This listener will only handle property change events from the registered bean for the
   * specified property.
   * @param annotatedBean the annotated bean object.
   * @param propertyName the name of the property that this listener will handle and process property change events for.
   */
  public AbstractListener(Object annotatedBean, String propertyName) {

    Assert.notNull(annotatedBean, "Annotated bean is required");
    Assert.hasText(propertyName, "Property name cannot be null or empty");

    this.bean = annotatedBean;
    this.beanInfo = ObjectUtils.doOperationSafely(args -> Introspector.getBeanInfo(annotatedBean.getClass(), null));
    this.propertyName = propertyName;
  }

  /**
   * Gets the Annotation of the specified type for the property given by name.
   * @param propertyName the String name of the property to get the Annotation for.
   * @param annotationType the type of the Annotation.
   * @return the Annotation object for the specified type on the given property.
   * @throws NoSuchMethodException if the property specified by name does not have a property setter.
   * @see AbstractListener#getWriteMethod(String)
   */
  protected <T extends Annotation> T getAnnotation(String propertyName, Class<T> annotationType) throws NoSuchMethodException {
    return getWriteMethod(propertyName).getAnnotation(annotationType);
  }

  /**
   * Determines whether the specified Annotation of type T is present on the property of the registered bean.
   * @param propertyName the name of the property.
   * @param annotationType the type of the Annotation.
   * @return a boolean value indicating whether the specified Annotation of type T is present on the property
   * of the registered bean.
   * @throws NoSuchMethodException if the property specified by name does not have a write method.
   */
  protected <T extends Annotation> boolean isAnnotationPresent(String propertyName, Class<T> annotationType) throws NoSuchMethodException {
    return getWriteMethod(propertyName).isAnnotationPresent(annotationType);
  }

  /**
   * Returns the registered bean object for which this listener listens and handles property change events for.
   * @return the registered bean object that this listener handles property change events for.
   */
  public Object getBean() {
    return this.bean;
  }

  /**
   * Returns the BeanInfo object used to describe the characteristics of the registered bean object.
   * @return a BeanInfo describing the registered bean object.
   */
  protected BeanInfo getBeanInfo() {
    return this.beanInfo;
  }

  /**
   * Determines whether this listener is interested in property change events of the specified property.
   * @param propertyName the name of the property that generated a change event.
   * @return a boolean value indicating whether this listener is interested in change events of the specified property.
   */
  protected boolean isInterested(String propertyName) {
    return Objects.nonNull(getPropertyName()) || ObjectUtils.equals(getPropertyName(), propertyName);
  }

  /**
   * Returns the PropertyDescriptor for the property with the given name.
   * @param propertyName the name of the property on the registered bean.
   * @return the PropertyDescriptor for the specified property.
   * @throws PropertyNotFoundException if the property specified by name does not exist on the registered bean.
   */
  protected PropertyDescriptor getPropertyDescriptor(String propertyName) throws PropertyNotFoundException {

    for (PropertyDescriptor propertyDescriptor : getBeanInfo().getPropertyDescriptors()) {
      if (ObjectUtils.equals(propertyDescriptor.getName(), propertyName)) {
        return propertyDescriptor;
      }
    }

    // Note, the NoSuchPropertyException should never be thrown since this listener was notified as a result
    // of the specified property changing.
    throw new PropertyNotFoundException("(" + propertyName + ") is not a property of bean ("
      + getBean().getClass().getName() + ")");
  }

  /**
   * Returns the name of the property on the registered bean object for which this listener handles and processes
   * property change events for.
   * @return a String value specifying the name of the property on the registered bean object.
   */
  public String getPropertyName() {
    return this.propertyName;
  }

  /**
   * Returns the setter methods for the property with the given name.
   *
   * @param propertyName the name of the property on the registered bean.
   * @return a Method object representing the setter method for the specified property.
   * @throws NoSuchMethodException if a setter method for the specified property does not exist.  There would be
   * no setter method for a read-only property on a bean.
   * @throws PropertyNotFoundException if the property specified by name does not exist on the registered bean.
   * @see AbstractListener#getPropertyDescriptor(String)
   */
  protected Method getWriteMethod(String propertyName) throws NoSuchMethodException, PropertyNotFoundException {

    PropertyDescriptor propertyDescriptor = getPropertyDescriptor(propertyName);
    Method writeMethod = propertyDescriptor.getWriteMethod();

    if (Objects.isNull(writeMethod)) {
      throw new NoSuchMethodException("No write method exists for property (" + propertyName + ") on bean ("
        + getBean().getClass().getName() + ")!");
    }

    return writeMethod;
  }
}
