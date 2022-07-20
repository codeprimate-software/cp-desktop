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
package org.cp.desktop.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import org.cp.elements.lang.Assert;
import org.cp.elements.lang.ObjectUtils;

public class GridCanvas extends JComponent {

  // default grid cell size (size assumes that the grid cell is a square area
  // and the value constitutes the lenght of one side)
  private static final int DEFAULT_GRID_CELL_SIZE = 10;
  // default line thickness within the grid
  private static final int DEFAULT_GRID_LINE_WIDTH = 1;

  private int gridCellSize = DEFAULT_GRID_CELL_SIZE;
  private int gridLineWidth = DEFAULT_GRID_LINE_WIDTH;

  private Color gridLineColor = Color.lightGray;

  /**
   * Constructs an instance of the GridCanvas class, which is a UI component representing graph paper to
   * plot coordinate data.  The grid canvas background color is defaulted to white.
   */
  public GridCanvas() {
    setBackground(Color.white);
  }

  /**
   * Returns the value of the grid cell size, which refers to the length of one of the grid cell's sides.
   * Note, the grid cell is assumed to be a square area.
   * @return an integer value representing the length of one side of a cell in the grid.  A grid cell
   * is a defined as a square unit area in the grid.
   */
  public int getGridCellSize() {
    return gridCellSize;
  }

  /**
   * Sets the length of one side of a cell in the grid, which is defined as a square unit area in the
   * grid.
   * @param size an integer value specifying the side length of a cell in the grid.
   */
  public void setGridCellSize(int size) {

    if (size < gridLineWidth) {
      throw new IllegalArgumentException("The grid cell size must be greater than or equal to grid line width: "
        + gridLineWidth);
    }

    gridCellSize = size;
  }

  /**
   * Returns a grid point refering to a grid cell for the given (x, y) pixel coordinates in the grid.
   * @param point is a java.awt.Point object refering to the pixel coordinates in the grid.
   * @return a GridPoint refering to the grid cell in the grid for the given pixel coordinates.
   */
  public GridPoint getGridCoordinates(Point point) {
    return new GridPoint((int) (point.getX() / getGridCellSize()), (int) (point.getY() / getGridCellSize()));
  }

  /**
   * Returns the color used to paint the lines in the grid.
   * @return a Color object refering to the color used to paint the grid lines.
   */
  public Color getGridLineColor() {
    return gridLineColor;
  }

  /**
   * Sets the color used to paint the lines in the grid.
   * @param gridLineColor is a Color object refering to the color used to paint the lines in the grid.
   */
  public void setGridLineColor(Color gridLineColor) {
    Assert.notNull("The color used to paint the grid line is required");
    this.gridLineColor = gridLineColor;
  }

  /**
   * Returns the width of the grid lines in the grid.
   * @return an integer value specifying the width in pixels of the grid lines in the grid.
   */
  public int getGridLineWidth() {
    return gridLineWidth;
  }

  /**
   * Sets the width of the grid lines in pixels.
   * @param lineWidth is an integer value specifying the width of the grid line in pixels.
   */
  public void setGridLineWidth(int lineWidth) {
    if (lineWidth < 1) {
      throw new IllegalArgumentException("The grid line width must be greater than " + DEFAULT_GRID_LINE_WIDTH);
    }
    gridLineWidth = lineWidth;
  }

  /**
   * Paints the UI of the grid canvas.
   * @param g is the Graphics object used to paint the UI of the grid canvas.
   */
  public void paintComponent(Graphics g) {

    super.paintComponent(g);

    Graphics2D g2 = (Graphics2D) g;

    g2.setStroke(new BasicStroke(getGridLineWidth()));
    g2.setColor(getGridLineColor());

    paintBackground(g2);

    int height = getHeight();
    int width = getWidth();

    for (int index = getGridCellSize(), dim = Math.max(width, height); index < dim; index += getGridCellSize()) {
      // increase x, draw verticle line
      g2.draw(new Line2D.Double(index, 0, index, height));
      // increase y, draw horizontal line
      g2.draw(new Line2D.Double(0, index, width, index));
    }
  }

  /**
   * Paints, or fills in, the background of the GridCanvas component.
   * @param g2 the Graphics object used to paint the background of the GridCanvas.
   */
  private void paintBackground(Graphics2D g2) {

    Color theColor = g2.getColor();

    g2.setColor(Color.white);
    g2.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
    g2.setColor(theColor);
  }

  /**
   * The GridPoint class used to represent the cells as (x, y) coordinates in the grid canvas.
   */
  public static class GridPoint {

    private final int x;
    private final int y;

    private GridPoint(int x, int y) {
      this.x = x;
      this.y = y;
    }

    public int getX() {
      return this.x;
    }

    public int getY() {
      return this.y;
    }

    @Override
    public boolean equals(Object obj) {

      if (this == obj) {
        return true;
      }

      if (!(obj instanceof GridPoint)) {
        return false;
      }

      GridPoint gridPoint = (GridPoint) obj;

      return gridPoint.getX() == getX()
        && gridPoint.getY() == getY();
    }

    @Override
    public int hashCode() {
      return ObjectUtils.hashCodeOf(getX(), getY());
    }

    @Override
    public String toString() {
      return String.format("(%d, %d)", getX(), getY());
    }
  }
}
