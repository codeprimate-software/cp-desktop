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
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;

import javax.swing.BorderFactory;

public class JStatusBar extends AbstractTextStatusBar {

  private static final String DEFAULT_TEXT = "";

  private AbstractStatusBarModel gradientBar;

  /**
   * Creates an instance of the JStatusBar UI component class initialized to the specified size.
   * @param barSize a Dimension object specifying the size (width and height) of the status bar.
   */
  public JStatusBar(Dimension barSize) {
    this(barSize, DEFAULT_TEXT);
  }

  /**
   * Creates an instance of the JStatusBar UI component class initialized to the specified size.
   * @param barSize a Dimension object specifying the size (width and height) of the status bar.
   * @param text a String value specifying the message that will be displayed in the status bar.
   */
  public JStatusBar(Dimension barSize, String text) {
    setBarSize(barSize);
    setBorder(BorderFactory.createLoweredBevelBorder());
    setText(text);
  }

  /**
   * Returns the instance of the AbstractStatusBarModel (GradientStatusBarModel) object used to model the bar displayed in the UI
   * of the status bar.
   * @return the AbstractStatusBarModel object model of the visual bar in the status bar.
   */
  protected synchronized AbstractStatusBarModel getStatusBarModel() {

    if (gradientBar == null) {
      gradientBar = new GradientStatusBarModel();
    }

    return gradientBar;
  }

  /**
   * The GradientStatusBarModel class models an oscillating bar that fades down the bar opposite the direction in which
   * the bar is moving.
   */
  private class GradientStatusBarModel extends AbstractTextStatusBarModel {

    private boolean forward = true;

    private Color getColor1() {
      return (forward ? getBackground() : getBarColor());
    }

    private Color getColor2() {
      return (forward ? getBarColor() : getBackground());
    }

    protected boolean isForward() {
      return this.forward;
    }

    protected void setForward(boolean forward) {
      this.forward = forward;
    }

    public void setSize(Dimension size) {
      super.setSize(size);
      setOrigin(new Point(-size.width, 0));
    }

    protected void setVisible(boolean visible) {

      if (isVisible() != visible) {
        setOrigin(new Point(-getSize().width, 0));
      }

      super.setVisible(visible);
    }

    public boolean contains(Point position) {

      if (isVisible()) {
        double x = position.getX();
        if (getOrigin().getX() <= x && (getOrigin().getX() + getSize().getWidth()) >= x) {
          double y = position.getY();
          if (getOrigin().getY() <= y && (getOrigin().getY() + getSize().getHeight()) >= y) {
            return true;
          }
        }
        return false;
      }
      else {
        return false;
      }
    }

    public void move(Dimension area) {

      if (isForward()) {
        getOrigin().x += getBarSpeed();
        setForward(Math.min(getOrigin().getX(), area.getWidth()) == getOrigin().getX());
      }
      else {
        getOrigin().x -= getBarSpeed();
        setForward(Math.min(getOrigin().getX(), -getSize().getWidth()) == getOrigin().getX());
      }

      firePropertyChangeEvent(new PropertyChangeEvent(this, "move", Boolean.FALSE, Boolean.TRUE));
    }

    public void paint(Graphics2D g2) {

      if (isVisible()) {
        g2.setPaint(new GradientPaint(((float) getOrigin().getX()), 0.0f, getColor1(),
          ((float) getOrigin().getX() + getSize().width) , 0.0f, getColor2()));
        g2.fill(new Rectangle2D.Double(getOrigin().getX(), getOrigin().getY(), getSize().getWidth(), getSize().getHeight()));
      }
    }
  }
}
