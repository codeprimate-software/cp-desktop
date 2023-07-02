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
package org.cp.desktop.awt.image;

import java.awt.Shape;
import java.awt.image.RGBImageFilter;

import org.cp.elements.lang.Assert;

public class BoundedAreaGrayFilter extends RGBImageFilter {

  private static final boolean DEFAULT_FILL_COLORED = true;

  private static final double RED_LUMINATION = 0.199;
  private static final double GREEN_LUMINATION = 0.387;
  private static final double BLUE_LUMINATION = 0.014;

  private boolean fillColored = DEFAULT_FILL_COLORED;

  private Shape boundedArea;

  /**
   * Constructs a new {@link BoundedAreaGrayFilter} used to gray out within or outside a polygon shaped area.
   *
   * @param boundedArea {@link Shape} defining the area that will contain color or be grayed out.
   * @see java.awt.Shape
   */
  public BoundedAreaGrayFilter(Shape boundedArea) {
    this(boundedArea, DEFAULT_FILL_COLORED);
  }

  /**
   * Constructs a new {@link BoundedAreaGrayFilter} used to gray out within or outside a polygon shaped area.
   *
   * @param boundedArea {@link Shape} defining the area that will contain color or be grayed out.
   * @param fillColored boolean value indicating whether the area inside the polygon is to be colored;
   * false if the area inside the polygon area is to be grayed out.
   * @see java.awt.Shape
   */
  public BoundedAreaGrayFilter(Shape boundedArea, boolean fillColored) {
    setBoundedArea(boundedArea);
    this.fillColored = fillColored;
  }

  /**
   * Returns the bounded area as a Shape object.
   * @return a Shape object representing the bounded area.
   */
  public Shape getBoundedArea() {
    return boundedArea;
  }

  /**
   * Sets the polygon area that will either be filled with color or grayed out.
   * @param boundedArea the polygon defining the area around which pixels
   * are colored or grayed out.
   */
  private void setBoundedArea(Shape boundedArea) {
    Assert.notNull(boundedArea, "Shape for the bounded area is required");
    this.boundedArea = boundedArea;
  }

  /**
   * Returns a boolean value indicating true if the bounded area is to be filled
   * with color, false if the bounded area will be grayed out.
   * @return a boolean value indicating the fill (color or gray).
   */
  public boolean isFillColored() {
    return this.fillColored;
  }

  /**
   * Package-private method to compute the grayscale value of the specified pixel.
   * @param pixel the current pixel color as a 4 byte integer value.
   * @return an integer 4 byte value for the new color of the pixel at (x, y).
   */
  int filterPixel(int pixel) {

    int red = (pixel & 0x00ff0000) >> 16;
    int green = (pixel & 0x0000ff00) >> 8;
    int blue = pixel & 0x000000ff;
    int lumination = (int) (RED_LUMINATION * red + GREEN_LUMINATION * green + BLUE_LUMINATION * blue);

    // Return the lumination value as the value of each RGB component.
    // The Alpha (transparency) component should always be set (not transparent).
    return (0xff << 24) | (lumination << 16) | (lumination << 8) | lumination;
  }

  /**
   * Converts color pixels to grayscale if and only if the point, defined by x and y coordinates, are not contained
   * within the bounded area. The algorithm matches the NTSC specification.
   * @param x is the x coordinate in the image area.
   * @param y is the y coordinate in the image area.
   * @param pixel is the current pixel color as a 4 byte integer value.
   * @return an integer 4 byte value for the new color of the pixel at (x, y).
   * @see BoundedAreaGrayFilter#filterPixel(int)
   */
  public int filterRGB(int x, int y, int pixel) {
    return getBoundedArea().contains(x, y) && isFillColored() ? pixel
      : filterPixel(pixel); // Get the average RGB intensity.
  }
}
