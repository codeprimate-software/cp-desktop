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

public class NetscapeStatusBar extends AbstractStatusBar {

  private AbstractStatusBarModel netscapeBar;

  /**
   * Creates an instance of the NetscapeStatusBar UI component class initialized to the specified size.
   * @param barSize a Dimension object specifying the size (width and height) of the status bar.
   */
  public NetscapeStatusBar(Dimension barSize) {
    setBarSize(barSize);
    setBorder(BorderFactory.createLoweredBevelBorder());
  }

  /**
   * Returns the instance of the AbstractStatusBarModel (NetscapeStatusBarModel) object used to model the bar displayed in the UI
   * of the status bar.
   * @return the AbstractStatusBarModel object model of the visual bar in the status bar.
   */
  protected synchronized AbstractStatusBarModel getStatusBarModel() {

    if (netscapeBar == null) {
      netscapeBar = new NetscapeStatusBarModel();
    }
    return netscapeBar;
  }

  /**
   * The NetscapeStatusBarModel class models the status bar in the Netscape Web Browser.
   */
  private class NetscapeStatusBarModel extends AbstractStatusBarModel {

    private boolean forward = true;

    private double getBarExtent() {
      return (getOrigin().x + getSize().getWidth());
    }

    protected boolean isBackward() {
      return !isForward();
    }

    private Color getColor1() {
      return isForward() ? getBackground() : getBarColor();
    }

    private Color getColor2() {
      return isForward() ? getBarColor() : getBackground();
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

    protected void move(Dimension area) {

      if (isForward()) {
        getOrigin().x += getBarSpeed();
        setForward(Math.max(getBarExtent(), area.getWidth()) == area.getWidth());
        if (isBackward()) {
          getOrigin().setLocation(area.getWidth(), 0);
        }
      }
      else {
        getOrigin().x -= getBarSpeed();
        setForward(Math.min(getOrigin().getX(), 0) == getOrigin().getX());
        if (isForward()) {
          getOrigin().setLocation(-getSize().width, 0);
        }
      }

      firePropertyChangeEvent(new PropertyChangeEvent(this, "move", Boolean.FALSE, Boolean.TRUE));
    }

    protected void paint(Graphics2D g2) {

      if (isVisible()) {
        g2.setPaint(new GradientPaint(((float) getOrigin().getX()), 0.0f, getColor1(),
          ((float) getOrigin().getX() + getSize().width) , 0.0f, getColor2()));
        g2.fill(new Rectangle2D.Double(getOrigin().getX(), getOrigin().getY(), getSize().getWidth(), getSize().getHeight()));
      }
    }
  }
}
