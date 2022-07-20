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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public class Matte extends JComponent {

  private static final Color TILE_COLOR = new Color(204, 204, 204);
  private static final Dimension TILE_SIZE = new Dimension(5, 5);

  // offscreen image buffer used for double buffering
  private BufferedImage imageBuffer;

  private Dimension previousMatteSize;

  /**
   * Creates an instance of the Matte class which represents a matte, or canvas, on which to paint
   * or draw images or other graphics primitives.
   */
  public Matte() {
    previousMatteSize = new Dimension(0, 0);
  }

  /**
   * Paints the UI of the matte, or canvas.
   * @param g is a Graphics object used to paint the UI of the matte.
   */
  public void paintComponent(Graphics g) {

    super.paintComponent(g);

    Dimension currentMatteSize = getSize();

    if (!currentMatteSize.equals(previousMatteSize)) {

      previousMatteSize = currentMatteSize;
      imageBuffer = new BufferedImage(currentMatteSize.width, currentMatteSize.height, BufferedImage.TYPE_INT_ARGB);

      Graphics2D g2 = imageBuffer.createGraphics();

      for (int y = 0; y < currentMatteSize.getHeight(); y += TILE_SIZE.height) {
        for (int x = 0; x < currentMatteSize.getWidth(); x += TILE_SIZE.width) {
          g2.setPaint((x % 2) == (y % 2) ? Color.white : TILE_COLOR);
          g2.fill(new Rectangle2D.Double(x, y, TILE_SIZE.getWidth(), TILE_SIZE.getHeight()));
        }
      }
    }

    g.drawImage(imageBuffer, 0, 0, this);
  }
}
