/*
 * VerticalFlowLayout.java (c) 17 April 2002
 *
 * A layout algorithm for laying out Components in a Container from top to bottom, left to right.
 * Note, the FlowLayout manager has similar behavior when the Locale varies and the ComponentOrientation
 * changes.
 *
 * Copyright (c) 2001, Code Primate
 * All Rights Reserved
 * @author John J. Blum
 * @version 2009.5.29
 * @see java.awt.LayoutManager
 * @see java.io.Serializable
 */
package org.cp.desktop.awt;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public final class VerticalFlowLayout implements LayoutManager, Serializable {

  public static final int BOTTOM = 0;
  public static final int MIDDLE = 1;
  public static final int TOP = 2;

  private static final int DEFAULT_ALIGNMENT = MIDDLE;
  private static final int DEFAULT_HORIZONTAL_GAP = 5;
  private static final int DEFAULT_VERTICAL_GAP = 5;

  private int alignment = DEFAULT_ALIGNMENT;
  private int horizontalGap = DEFAULT_HORIZONTAL_GAP;
  private int verticalGap = DEFAULT_VERTICAL_GAP;

  private final Logger logger = Logger.getLogger(getClass().getName());

  /**
   * Creates an instance of the VerticalFlowLayout, with defaults for alignment, horizontal gap and
   * vertical gap.
   */
  public VerticalFlowLayout() {
    this(DEFAULT_ALIGNMENT, DEFAULT_HORIZONTAL_GAP, DEFAULT_VERTICAL_GAP);
  }

  /**
   * Creates an instance of the VerticalFlowLayout class with the specified alignment, and defaults for the
   * horizontal gap and vertical gap.
   * @param alignment the alignment of the components in the container, TOP, MIDDLE and BOTTOM.
   */
  public VerticalFlowLayout(final int alignment) {
    this(alignment, DEFAULT_HORIZONTAL_GAP, DEFAULT_VERTICAL_GAP);
  }

  /**
   * Creates an instance of the VerticalFlowLayout class with the specified horizontal and vertical gaps,
   * and a default alignment.
   * @param horizontalGap the horizontal space between components layed out with this LayoutManager.
   * @param verticalGap the vertical space between components layed out with this LayoutManager.
   */
  public VerticalFlowLayout(final int horizontalGap, final int verticalGap) {
    this(DEFAULT_ALIGNMENT, horizontalGap, verticalGap);
  }

  /**
   * Creates an instance of the VerticalFlowLayout class with the specified alignment, horizontal gap
   * and vertical gap.
   * @param alignment the alignment of the components in the container, TOP, MIDDLE and BOTTOM.
   * @param horizontalGap the horizontal space between components layed out with this LayoutManager.
   * @param verticalGap the vertical space between components layed out with this LayoutManager.
   */
  public VerticalFlowLayout(final int alignment, final int horizontalGap, final int verticalGap) {

    setAlignment(alignment);
    setHorizontalGap(horizontalGap);
    setVerticalGap(verticalGap);
  }

  /**
   * Returns the alignment of the Components used by this LayoutManager to layout components in the Container.
   * @return an integer value specifying the alignment of Components in the Container.
   */
  public int getAlignment() {
    return alignment;
  }

  /**
   * Sets the specified alignment of the Components in this Container.
   * @param alignment an integer value specifying the alignment of Components in this Container.  Possible values
   * are TOP, MIDDLE and BOTTOM.
   * @throws IllegalArgumentException if the value of the alignment is not one of TOP, MIDDLE and BOTTOM.
   */
  public void setAlignment(int alignment) {

    switch (alignment) {
      case BOTTOM:
      case MIDDLE:
      case TOP:
        this.alignment = alignment;
        break;
      default:
        throw new IllegalArgumentException("The alignment (" + alignment + ") is invalid; Please specify a SwingConstants of TOP, CENTER or BOTTOM");
    }
  }

  /**
   * Returns the horizontal spacing used by this LayoutManager to layout Components in the Container.
   * @return an integer value specifying the horizontal spacing used by this LayoutManager to layout Components
   * in the Container.
   */
  public int getHorizontalGap() {
    return horizontalGap;
  }

  /**
   * Returns the horizontal spacing used by this LayoutManager to layout Components in the Container.
   * @param horizontalGap an integer value specifying the horizontal spacing used by this LayoutManager
   * to layout Components in the Container.
   */
  private void setHorizontalGap(int horizontalGap) {
    this.horizontalGap = horizontalGap;
  }

  /**
   * Returns the vertical spacing used by this LayoutManager to layout Components in the Container.
   * @return an integer value specifying the horizontal spacing used by this LayoutManager to layout Components
   * in the Container.
   */
  public int getVerticalGap() {
    return verticalGap;
  }

  /**
   * Returns the vertical spacing used by this LayoutManager to layout Components in the Container.
   * @param verticalGap an integer value specifying the vertical spacing used by this LayoutManager
   * to layout Components in the Container.
   */
  private void setVerticalGap(int verticalGap) {
    this.verticalGap = verticalGap;
  }

  /**
   * The implementation of this method for the VerticalFlowLayout manager does nothing.
   * @param name the string to be associated with the component.
   * @param component the component to be added.
   */
  public void addLayoutComponent(String name, Component component) {
    logger.warning("addLayoutComponent not implemented");
  }

  /**
   * Lays out the Components in the Container according the VerticalFlowLayout manager's algorithm for laying out
   * Components.
   * @param parent the Container in which the Components will be layed out.
   */
  public void layoutContainer(Container parent) {

    Insets insets = parent.getInsets();

    int maxWidth = (parent.getWidth() - (insets.left + insets.right));
    int maxHeight = (parent.getHeight() - (insets.top + insets.bottom));

    ColumnLayout colLayout = new ColumnLayout(insets.left);

    for (int index = 0, count = parent.getComponentCount(); index < count; index++) {

      Component component = parent.getComponent(index);

      if (component.isVisible()) {
        component.setSize(component.getPreferredSize());
        if (colLayout.projectedHeight(component) < maxHeight) {
          colLayout.addComponent(component);
        }
        else {
          colLayout.layoutColumn(parent);
          colLayout = new ColumnLayout(colLayout.nextColumnPosition());
          colLayout.addComponent(component);
        }
      }
    }

    colLayout.layoutColumn(parent);
  }

  /**
   * Template method for implementing the minimumSize and preferredSize methods of the VerticalFlowLayout manager.
   * This method can be used to compute the minimum and preferred sizes of the Container.
   * @param parent the Container who's size is being determined based on the Components that the parent contains.
   * @param sizeDeterminator the size determination strategy for Components in the Container.
   * @return a Dimension object specifying the ideal size of the parent Container.
   */
  private Dimension layoutSize(Container parent, IComponentSizeDeterminator sizeDeterminator) {

    int width = 0;
    int height = 0;
    int numberOfVisibleComponents = 0;

    for (int index = parent.getComponentCount(); --index >= 0; ) {

      Component component = parent.getComponent(index);

      if (component.isVisible()) {

        Dimension componentSize = sizeDeterminator.getSize(component);

        width = (int) Math.max(width, componentSize.getWidth());
        height += componentSize.getHeight();
        numberOfVisibleComponents++;
      }
    }

    Insets containerInsets = parent.getInsets();

    width += (containerInsets.left + containerInsets.right);
    height += ((containerInsets.top + containerInsets.bottom) + (getVerticalGap() * (numberOfVisibleComponents - 1)));

    return new Dimension(width, height);
  }

  /**
   * Returns the minimum size of the parent Container based on the minimum sizes of the Components contained within
   * the parent.
   * @param parent the Container who's minimum size is being determined based on the Components that the
   * parent contains.
   * @return a Dimension object specifying the minimum size of the parent Container.
   */
  public Dimension minimumLayoutSize(Container parent) {
    return layoutSize(parent, MinimumComponentSizeDeterminator.INSTANCE);
  }

  /**
   * Returns the preferred size of the parent Container based on the minimum sizes of the Components contained within
   * the parent.
   * @param parent the Container who's preferred size is being determined based on the Components that the
   * parent contains.
   * @return a Dimension object specifying the preferred size of the parent Container.
   */
  public Dimension preferredLayoutSize(Container parent) {
    return layoutSize(parent, PreferredComponentSizeDeterminator.INSTANCE);
  }

  /**
   * The implementation of this method for the VerticalFlowLayout manager does nothing.
   * @param comp the component to be added.
   */
  public void removeLayoutComponent(Component comp) {
    logger.warning("removeLayoutComponent not implemented");
  }

  /**
   * Returns a String description of this LayoutManager.
   * @return a String describing the current state of this LayoutManager.
   */
  @Override
  public String toString() {

    StringBuilder buffer = new StringBuilder("{alignment = ");

    buffer.append(getAlignment());
    buffer.append(", horizontalGap = ").append(getHorizontalGap());
    buffer.append(", verticalGap = ").append(getVerticalGap());
    buffer.append("}:").append(getClass().getName());

    return buffer.toString();
  }

  /**
   * A separate class to specify the layout of a column in the vertical layout of the parent Container.
   */
  private final class ColumnLayout {

    private final int columnPosition;
    private int height;
    private int width;

    private final List<Object> componentQueue = new LinkedList<>();

    public ColumnLayout(int columnPosition) {
      this.columnPosition = columnPosition;
    }

    public void addComponent(Component component) {

      width = Math.max(width, component.getWidth());
      height += (componentQueue.isEmpty() ? component.getHeight() : (getVerticalGap() + component.getHeight()));
      componentQueue.add(component);
    }

    private int getColumnPosition() {
      return columnPosition;
    }

    public int getComponentCount() {
      return componentQueue.size();
    }

    public int getHeight() {
      return height;
    }

    private int getWidth() {
      return width;
    }

    private int getYOffset(Container parent) {

      Dimension parentSize = parent.getSize();
      Insets parentInsets = parent.getInsets();

      switch (getAlignment()) {
        case BOTTOM:
          return (parentSize.height - parentInsets.bottom - getHeight());
        case MIDDLE:
          return ((parentSize.height - getHeight()) / 2);
        case TOP:
          return parentInsets.top;
        default:
          return 0;
      }
    }

    public void layoutColumn(Container parent) {

      int yOffset = getYOffset(parent);

      for (Object obj : componentQueue) {
        Component component = (Component) obj;
        component.setLocation(getColumnPosition(), yOffset);
        yOffset += (component.getHeight() + getVerticalGap());
      }
    }

    public int nextColumnPosition() {
      return (getColumnPosition() + getWidth() + getHorizontalGap());
    }

    public int projectedHeight(Component component) {
      return (getHeight() + (componentQueue.isEmpty() ? component.getHeight() : (getVerticalGap() + component.getHeight())));
    }

    @Override
    public String toString() {

      StringBuilder buffer = new StringBuilder("{columnPosition = ");

      buffer.append(getColumnPosition());
      buffer.append(", componentCount = ").append(getComponentCount());
      buffer.append(", height = ").append(getHeight());
      buffer.append(", width = ").append(getWidth());
      buffer.append("}:").append(getClass().getName());

      return buffer.toString();
    }
  }

  /**
   * An interface to describe the size determination strategy for Components contained by their parent.
   */
  private interface IComponentSizeDeterminator {
    Dimension getSize(Component component);
  }

  /**
   * The MinimumComponentSizeDeterminator class determines the minimum acceptable size of a Component in a
   * Container.
   */
  private static final class MinimumComponentSizeDeterminator implements IComponentSizeDeterminator {

    static final MinimumComponentSizeDeterminator INSTANCE = new MinimumComponentSizeDeterminator();

    public Dimension getSize(Component component) {
      return component.getMinimumSize();
    }
  }

  /**
   * The PreferredComponentSizeDeterminator class determines the preferred size of a Component in a
   * Container.
   */
  private static final class PreferredComponentSizeDeterminator implements IComponentSizeDeterminator {

    static final PreferredComponentSizeDeterminator INSTANCE = new PreferredComponentSizeDeterminator();

    public Dimension getSize(Component component) {
      return component.getPreferredSize();
    }
  }
}
