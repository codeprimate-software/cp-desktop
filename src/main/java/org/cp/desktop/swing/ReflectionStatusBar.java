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
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;

import org.cp.elements.lang.annotation.NotNull;

public final class ReflectionStatusBar extends AbstractStatusBar {

  private AbstractStatusBarModel reflectionBar;

  /**
   * Creates an instance of the ReflectionStatusBar UI component class.
   */
  public ReflectionStatusBar(Dimension barSize) {

    setBarSize(barSize);
    setBorder(BorderFactory.createLoweredBevelBorder());
  }

  /**
   * Returns the instance of the AbstractStatusBarModel (ReflectionStatusBarModel) object used to model the bar displayed in the UI
   * of the status bar.
   * @return the AbstractStatusBarModel object model of the visual bar in the status bar.
   */
  protected synchronized AbstractStatusBarModel getStatusBarModel() {

    if (this.reflectionBar == null) {
      reflectionBar = new ReflectionStatusBarModel(new LeftStatusBarModel(), new RightStatusBarModel());
    }

    return reflectionBar;
  }

  /**
   * The ReflectionStatusBarModel is a composite AbstractStatusBarModel object consisting of instances of the LeftStatusBarModel
   * and RightStatusBarModel classes.  The two independent, but complimentarty bars, Left... and Right..., form a
   * symmetry, or a reflection about the center of the status bar UI component.  This class implements the
   * AbstractStatusBarModel abstract class and delegates to the LeftStatusBarModel and RightStatusBarModel classes.
   */
  private static class ReflectionStatusBarModel extends AbstractStatusBarModel {

    private AbstractStatusBarModel leftBar;
    private AbstractStatusBarModel rightBar;

    public ReflectionStatusBarModel(@NotNull AbstractStatusBarModel leftBar, @NotNull AbstractStatusBarModel rightBar) {
      this.leftBar = leftBar;
      this.rightBar = rightBar;
    }

    public synchronized Color getColor() {
      return this.leftBar.getColor();
    }

    public synchronized void setColor(Color color) {

      processChange("color", getColor(), color, newColor -> {
        this.leftBar.setColor(newColor);
        this.rightBar.setColor(newColor);
      });
    }

    protected Point getOrigin() {
      return this.leftBar.getOrigin();
    }

    protected void setOrigin(Point origin) {

      processChange("origin", getOrigin(), origin, newOrigin -> {
        this.leftBar.setOrigin(newOrigin);
        this.rightBar.setOrigin(newOrigin);
      });
    }

    public Dimension getSize() {
      return this.leftBar.getSize();
    }

    public void setSize(Dimension size) {

      processChange("size", getSize(), size, newSize -> {
        this.leftBar.setSize(newSize);
        this.rightBar.setSize(newSize);
      });
    }

    protected boolean isVisible() {
      return leftBar.isVisible();
    }

    protected void setVisible(boolean visible) {

      processChange("visible", isVisible(), visible, newVisible -> {
        this.leftBar.setVisible(newVisible);
        this.rightBar.setVisible(newVisible);
      });
    }

    protected synchronized void move(Dimension area) {

      processChange("move", null, area, newArea -> {
        this.leftBar.move(newArea);
        this.rightBar.move(newArea);
      });
    }

    protected synchronized void paint(Graphics2D g2) {
      this.leftBar.paint(g2);
      this.rightBar.paint(g2);
    }
  }

  /**
   * Abstract AbstractStatusBarModel class serving as the base class for the LeftStatusBarModel and RightStatusBarModel classes, pulling
   * common operations shared by both AbstractStatusBarModel classes into this class.
   */
  private abstract class GradientStatusBarModel extends AbstractStatusBarModel {

    private boolean forward;

    protected GradientStatusBarModel(boolean forward) {
      setForward(forward);
    }

    private Color getColor1() {
      return this.forward ? getBackground() : getBarColor();
    }

    private Color getColor2() {
      return this.forward ? getBarColor() : getBackground();
    }

    public boolean isForward() {
      return forward;
    }

    public void setForward(boolean forward) {
      this.forward = forward;
    }

    protected abstract Shape getClipShape();

    protected void paint(Graphics2D g2) {

      if (isVisible()) {

        Shape originalClip = g2.getClip();

        g2.setClip(getClipShape());
        g2.setPaint(new GradientPaint(((float) getOrigin().getX()), 0.0f, getColor1(),
          ((float) getOrigin().getX() + getSize().width) , 0.0f, getColor2()));
        g2.fill(new Rectangle2D.Double(getOrigin().getX(), getOrigin().getY(), getSize().getWidth(), getSize().getHeight()));
        g2.setClip(originalClip);
      }
    }
  }

  /**
   * LeftStatusBarModel class represents the left bar that moves through the status bar UI component.
   */
  private final class LeftStatusBarModel extends GradientStatusBarModel {

    public LeftStatusBarModel() {
      super(true);
    }

    protected Shape getClipShape() {

      Dimension statusBarSize = ReflectionStatusBar.this.getSize();

      return new Rectangle2D.Double(0, 0, statusBarSize.getWidth() / 2, statusBarSize.getHeight());
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

      int halfWidth = (int) (area.getWidth() / 2);

      if (isForward()) {
        getOrigin().x += getBarSpeed();
        setForward(Math.max(getOrigin().getX(), halfWidth) == halfWidth);
        if (!isForward()) {
          getOrigin().setLocation(halfWidth, 0);
        }
      }
      else {
        getOrigin().x -= getBarSpeed();
        setForward(Math.min(-getSize().getWidth(), getOrigin().getX()) == getOrigin().getX());
        if (isForward()) {
          getOrigin().setLocation(-getSize().getWidth(), 0);
        }
      }
    }
  }

  /**
   * RightStatusBarModel class represents the right bar that moves through the status bar UI component.
   */
  private final class RightStatusBarModel extends GradientStatusBarModel {

    public RightStatusBarModel() {
      super(false);
    }

    protected Shape getClipShape() {

      Dimension statusBarSize = ReflectionStatusBar.this.getSize();

      int halfWidth = (int) (statusBarSize.getWidth() / 2);

      return new Rectangle2D.Double(halfWidth, 0, halfWidth, statusBarSize.getHeight());
    }

    public void setSize(Dimension size) {

      super.setSize(size);

      setOrigin(new Point(ReflectionStatusBar.this.getSize().width, 0));
    }

    protected void setVisible(boolean visible) {

      if (isVisible() != visible) {
        setOrigin(new Point(ReflectionStatusBar.this.getSize().width, 0));
      }

      super.setVisible(visible);
    }

    protected void move(Dimension area) {

      int halfWidth = (int) (area.getWidth() / 2);

      if (isForward()) {
        getOrigin().x += getBarSpeed();
        setForward(Math.max(getOrigin().getX(), area.getWidth()) == area.getWidth());
        if (!isForward()) {
          getOrigin().setLocation(area.width, 0);
        }
      }
      else {
        getOrigin().x -= getBarSpeed();
        setForward(Math.min((halfWidth - getSize().getWidth()), getOrigin().getX()) == getOrigin().getX());
        if (isForward()) {
          getOrigin().setLocation((halfWidth - getSize().getWidth()), 0);
        }
      }
    }
  }
}
