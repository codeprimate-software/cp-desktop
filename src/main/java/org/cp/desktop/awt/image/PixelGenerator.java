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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Shape;

@SuppressWarnings("unused")
public abstract class PixelGenerator {

  /**
   * Creates a flattened pixel array (one-dimensional array of pixel data) representing the
   * Shape object in black and the canvas as transparent.
   * @param canvasSize a java.awt.Dimension object representing the area of the canvas
   * on which the geometric shape will be drawn (note that areas of the canvas not
   * contained by the Shape obejct will be transparent).
   * @param geometricShape a java.awt.Shape object for which pixel data will be generated as
   * black pixels.
   * @return a one-dimensional array of integer values representing the value of each
   * pixel in the image.
   * @see PixelGenerator#getPixels(Rectangle, Shape)
   */
  public static int[] getPixels(final Dimension canvasSize, final Shape geometricShape) {

    int[] pixels = new int[canvasSize.width * canvasSize.height];

    for (int y = canvasSize.height; --y >= 0;) {
      for (int x = canvasSize.width; --x >= 0;) {
        pixels[y * canvasSize.width + x] = (geometricShape.contains(x, y) ? 0xff000000 : 0x00000000);
      }
    }

    return pixels;
  }

  /**
   * Creates a flattened pixel array (one-dimensional array of pixel data) representing
   * the Shape object in black and the bounding box as transparent.  This overloaded
   * getPixel method is implemented in terms of the getPixel(:Dimension, :Shape) method.
   * @param boundingBox: a java.awt.Rectangle object representing the bounding box
   * enclosing the Shape object Note that the bounding box will be transparent.
   * @param geometricShape a jav.awt.Shape object for which pixel data will be generated
   * (represented by the black pixels).
   * @return a one-dimensional array of integer values representing the value of each
   * pixel in the image.
   * @see PixelGenerator#getPixels(Dimension, Shape)
   */
  public static int[] getPixels(Rectangle boundingBox, Shape geometricShape) {
    return getPixels(new Dimension((int) boundingBox.getWidth(), (int) boundingBox.getHeight()), geometricShape);
  }
}
