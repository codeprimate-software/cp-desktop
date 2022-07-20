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
package org.cp.desktop.awt.support;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import org.cp.desktop.awt.VerticalFlowLayout;

@SuppressWarnings("unused")
public abstract class LayoutManagerUtils {

  /**
   * Returns a GridBagConstraints object populated with the specified values.
   * @param gridx is an integer value indicating the cell at the left of the component's display area,
   * where the leftmost cell has gridx=0.
   * @param gridy is an integer value indicating the cell at the top of the component's display area,
   * where the topmost cell has gridy=0.
   * @param gridWidth is an integer value specifying the number of cells in a row for the component's display area.
   * @param gridHeight is an integer value specifying the number of cells in a column for the component's display area.
   * @param weightx is an integer value specifying how to distribute extra horizontal space.
   * @param weighty is an integer value specifying how to distribute extra vertical space.
   * @param anchor is used to determine which part of the cell the component should be placed.  The field is used
   * when the component is smaller than its display area. Possible values include: CENTER, NORTH, NORTHEAST, EAST,
   * SOUTHEAST, SOUTH, SOUTHWEST, WEST and NORTHWEST.
   * @param fill is an integer field is used when the component's display area is larger than the component's
   * requested size.
   * @param insets is a java.awt.Insets object field specifiing the external padding of the component, the minimum
   * amount of space between the component and the edges of its display area.
   * @param ipadx is an integer value field specifying the internal padding of the component, how much space to add
   * to the minimum width of the component.
   * @param ipady is an integer value This field specifying the internal padding, that is, how much space to add
   * to the minimum.
   * @return an instance of the GridBagConstraints object fully populated.
   * @see LayoutManagerUtils#setConstraints(GridBagConstraints, int, int, int, int, int, int, int, int,
   * Insets, int, int)
   */
  public static GridBagConstraints getConstraints(int gridx,
                                                  int gridy,
                                                  int gridWidth,
                                                  int gridHeight,
                                                  int weightx,
                                                  int weighty,
                                                  int anchor,
                                                  int fill,
                                                  Insets insets,
                                                  int ipadx,
                                                  int ipady) {

    return new GridBagConstraints(gridx, gridy, gridWidth, gridHeight, weightx, weighty,
      anchor, fill, insets, ipadx, ipady);
  }

  /**
   * Sets the properties of the GridBagConstraints object used in determining the layout of components
   * in a container object using the GridBagLayout manager.
   * @param constraints is a java.awt.GridBagConstraints variable containing layout constraints.
   * @param gridx is an integer value indicating the cell at the left of the component's display area,
   * where the leftmost cell has gridx=0.
   * @param gridy is an integer value indicating the cell at the top of the component's display area,
   * where the topmost cell has gridy=0.
   * @param gridWidth is an integer value specifying the number of cells in a row for the component's display area.
   * @param gridHeight is an integer value specifying the number of cells in a column for the component's display area.
   * @param weightx is an integer value specifying how to distribute extra horizontal space.
   * @param weighty is an integer value specifying how to distribute extra vertical space.
   * @param anchor is used to determine which part of the cell the component should be placed.  The field is used
   * when the component is smaller than its display area. Possible values include: CENTER, NORTH, NORTHEAST, EAST,
   * SOUTHEAST, SOUTH, SOUTHWEST, WEST and NORTHWEST.
   * @param fill is an integer field is used when the component's display area is larger than the component's
   * requested size.
   * @param insets is a java.awt.Insets object field specifiing the external padding of the component, the minimum
   * amount of space between the component and the edges of its display area.
   * @param ipadx is an integer value field specifying the internal padding of the component, how much space to add
   * to the minimum width of the component.
   * @param ipady is an integer value This field specifying the internal padding, that is, how much space to add
   * to the minimum.
   * @see LayoutManagerUtils @getConstraints
   */
  public static void setConstraints(GridBagConstraints constraints,
                                    int gridx,
                                    int gridy,
                                    int gridWidth,
                                    int gridHeight,
                                    int weightx,
                                    int weighty,
                                    int anchor,
                                    int fill,
                                    Insets insets,
                                    int ipadx,
                                    int ipady) {

    constraints.gridx = gridx;
    constraints.gridy = gridy;
    constraints.gridwidth = gridWidth;
    constraints.gridheight = gridHeight;
    constraints.weightx = weightx;
    constraints.weighty = weighty;
    constraints.fill = fill;
    constraints.anchor = anchor;
    constraints.insets = insets;
    constraints.ipadx = ipadx;
    constraints.ipady = ipady;
  }

  /**
   * Returns an instance of the VerticalFlowLayout manager with center alignment
   * and 5 pixel vertical and horizontal spacing between components.
   *
   * @return an instance of the VerticalFlowLayout manager with center alignment
   * and 5 pixels of padding between components in the UI.
   */
  public static VerticalFlowLayout getVerticalFlowLayout() {
    return new VerticalFlowLayout();
  }

  /**
   * Returns an instance of the VerticalFlowLayout manager with the specified
   * alignment and and 5 pixel vertical and horizontal spacing between
   * components.
   *
   * @param align is an integer value specifying the alignment of components
   * within their given space (CENTER, LEFT, RIGHT).
   * @return an instance of the VerticalFlowLayout manager with the specified
   * alignment and 5 pixels of padding between components in the UI.
   */
  public static VerticalFlowLayout getVerticalFlowLayout(int align) {
    return new VerticalFlowLayout(align);
  }

  /**
   * Returns an instance of the VerticalFlowLayout manager with the specified
   * alignment, horizontal and vertical spacing betweeen components in the
   * UI.
   *
   * @param align is an integer value specifying the alignment of components
   * within their given space (CENTER, LEFT, RIGHT).
   * @param horizontalGap is an integer value specifying the number of pixels
   * between components horizontally.
   * @param verticalGap is an integer value specifying the number of pixels
   * between components vertically.
   * @return an instance of the VerticalFlowLayout manager with the specified
   * alignment, horizontalGap and verticalGap.
   */
  public static VerticalFlowLayout getVerticalFlowLayout(int align, int horizontalGap, int verticalGap) {
    return new VerticalFlowLayout(align, horizontalGap, verticalGap);
  }
}
