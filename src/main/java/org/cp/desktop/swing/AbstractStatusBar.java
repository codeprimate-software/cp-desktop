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
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

import javax.swing.JComponent;

import org.cp.desktop.swing.event.NumberOutOfBoundsVetoableChangeListener;
import org.cp.desktop.swing.event.RequiredFieldVetoableChangeListener;
import org.cp.elements.beans.AbstractBean;
import org.cp.elements.beans.IllegalPropertyValueException;
import org.cp.elements.lang.Constants;
import org.cp.elements.lang.ObjectUtils;
import org.cp.elements.security.model.User;

@SuppressWarnings("unused")
public abstract class AbstractStatusBar extends JComponent implements PropertyChangeListener {

  private static final int DEFAULT_BAR_SPEED = 7;
  private static final int MAX_BAR_SPEED = 10;
  private static final int MAX_WAIT_TIME = 100;
  private static final int MIN_BAR_SPEED = 1;

  private static final Color DEFAULT_BACKGROUND_COLOR = Color.LIGHT_GRAY;
  private static final Color DEFAULT_BAR_COLOR = new Color(56, 73, 101); // dark gray

  private boolean paused = false;
  private boolean running = false;

  // Determines the velocity of bar oscillating on a scale of 1 to 10.
  private int barSpeed = DEFAULT_BAR_SPEED;

  private BufferedImage imageBuffer;

  private Dimension previousStatusBarSize;

  private Graphics2D graphicsBuffer;

  /**
   * Constructs a new {@link AbstractStatusBar}.
   * <p>
   * Registers a {@link VetoableChangeListener} on the {@link #getBarSpeed() barSpeed} property to constrain the values
   * between {@link #MIN_BAR_SPEED} and {@link #MAX_BAR_SPEED}. Also sets the background {@link Color}
   * to {@link Color#LIGHT_GRAY}.
   */
  public AbstractStatusBar() {

    addVetoableChangeListener(new NumberOutOfBoundsVetoableChangeListener<>("barSpeed", MIN_BAR_SPEED, MAX_BAR_SPEED));
    getStatusBarModel().register("move", this);
    getStatusBarModel().register("visible", this);
    setBackground(DEFAULT_BACKGROUND_COLOR);
  }

  /**
   * Returns the configured instance of the {@link AbstractStatusBarModel} used to model the bar
   * displayed in the user interface of the status bar.
   *
   * @return the configured instance of the {@link AbstractStatusBarModel}.
   * @see AbstractStatusBarModel
   */
  protected abstract AbstractStatusBarModel getStatusBarModel();

  /**
   * Returns the color of the bar that moves (oscilates) within the status bar UI component.
   * @return a Color object specifying the color of bar within the status bar.
   */
  public Color getBarColor() {
    return getStatusBarModel().getColor();
  }

  /**
   * Sets the color of the bar that moves (oscilates) within the status bar UI component.
   * @param color the Color object specifying the color of the bar within the status bar.
   */
  public void setBarColor(Color color) {
    getStatusBarModel().setColor(color);
  }

  /**
   * Returns the dimensions of the bar displayed in this status bar.
   * @return a Dimension object specifying the width and height of the bar within this status bar.
   */
  public Dimension getBarSize() {
    return getStatusBarModel().getSize();
  }

  /**
   * Sets the dimensions of the bar displayed in this status bar.
   * @param barSize a Dimension object specifying the width and height of the bar within this status bar.
   */
  public void setBarSize(Dimension barSize) {
    getStatusBarModel().setSize(barSize);
  }

  /**
   * Returns the speed, or velocity, at which the bar moves (or oscillates) within this status bar UI component.
   * Note, speed is measured on a scale of 1 to 10.  The greater the number, the higher the speed.  The
   * speed value determines the number of milliseconds between movement requests that are sent to the bar.
   * @return an integer value specifying the speed of the bar within this status bar.
   */
  public int getBarSpeed() {
    return this.barSpeed;
  }

  /**
   * Sets the speed, or velocity, at which the bar moves (or oscillates) within the status bar UI component.
   * Note, speed is measured on a scale of 1 to 10.  The greater the number, the higher the speed.  The
   * speed value determines the number of milliseconds between movement requests that are sent to the bar.
   * @param barSpeed an integer value specifying the speed of movement of the bar within this status bar.
   */
  public void setBarSpeed(int barSpeed) {

    try {
      int previousBarSpeed = getBarSpeed();
      fireVetoableChange("barSpeed", previousBarSpeed, barSpeed);
      this.barSpeed = barSpeed;
      firePropertyChange("barSpeed", previousBarSpeed, barSpeed);
    }
    catch (PropertyVetoException cause) {
      throw new IllegalPropertyValueException(String.format("Bar speed [%d] is not valid", barSpeed), cause);
    }
  }

  /**
   * Returns a {@link Graphics2D} object (off-screen memory buffer) in which to draw images
   * and other graphic primitives.
   *
   * @return a {@link Graphics2D} object used for double-buffering and painting the user interface of the status bar.
   * @see java.awt.Graphics2D
   */
  protected final Graphics2D getGraphicsBuffer() {

    Dimension currentStatusBarSize = getSize();

    if (!currentStatusBarSize.equals(this.previousStatusBarSize)) {
      this.imageBuffer = new BufferedImage(currentStatusBarSize.width, currentStatusBarSize.height, BufferedImage.TYPE_INT_RGB);
      this.graphicsBuffer = imageBuffer.createGraphics();
      this.previousStatusBarSize = currentStatusBarSize;
    }

    return this.graphicsBuffer;
  }

  /**
   * Returns the image buffer associated with the graphics context (buffer) used to paint the status bar UI.
   * This image buffer is used to store a snapshot of the status bar in a moment of time.
   * @return an off-screen Image (context) containing the UI of the status bar.
   */
  protected Image getImageBuffer() {
    return this.imageBuffer;
  }

  /**
   * Determines whether the status bar movement has been suspended.
   * @return a boolean value indicating whether the status bar movement has been suspended.
   */
  public boolean isPaused() {
    return this.paused;
  }

  /**
   * Sets a condition specifying whether the status bar movement should be suspended.
   * @param paused a boolean value indicating if status bar movement should be suspended.
   */
  private void setPaused(boolean paused) {
    this.paused = paused;
  }

  /**
   * Determines whether the status bar is running.  A status bar is considered running if the StatusBarRunner Thread
   * is actively updating the state of the bar, thus causing movement to occur.
   * @return a boolean value indicating if the status bar is running.
   */
  public boolean isRunning() {
    return this.running;
  }

  protected boolean isNotRunning() {
    return !isRunning();
  }

  /**
   * Sets the state of the status bar to either a running or non-running state.
   * @param running is a boolean value indicating if the status bar should be made active.
   */
  private void setRunning(boolean running) {
    this.running = running;
  }

  /**
   * Returns the number of milliseconds that the StatusBarRunner Thread will sleep, thereby affecting the
   * velocity of the bar oscillating within the status bar.
   * @return an integer value specifying the number of milliseconds that the StatusBarRunner Thread will sleep.
   */
  private int getWaitTime() {
    return MAX_WAIT_TIME - ((getBarSpeed() - 1) * 10);
  }

  /**
   * Paints the user interface (UI) of the status bar component.
   *
   * @param graphics {@link Graphics} context in which the paint operations are applied.
   * @see java.awt.Graphics
   */
  @Override
  protected final void paintComponent(Graphics graphics) {

    super.paintComponent(graphics);

    Graphics2D g2 = getGraphicsBuffer();

    g2.setColor(getBackground());
    g2.setClip(graphics.getClip());
    g2.fill(new Rectangle2D.Double(0.0, 0.0, getWidth(), getHeight()));
    paintStatusBar(g2); // Paint the user interface (UI) of the status bar.
    graphics.drawImage(getImageBuffer(), 0, 0, null);
  }

  /**
   * Paints the user interface (UI) of the status bar component.
   * <p>
   * This method is meant to be overridden to apply custom paint operations
   * depending upon the status bar implementation.
   *
   * @param g2 {@link Graphics2D} object context in which the paint operations will be applied.
   * @see java.awt.Graphics2D
   */
  protected void paintStatusBar(Graphics2D g2) {
    getStatusBarModel().paint(g2);
  }

  /**
   * Receives {@link PropertyChangeEvent events} from the {@link AbstractStatusBarModel} signifying changes in
   * position of the bar in order to refresh the user interface (UI) of the status bar.
   *
   * @param event {@link PropertyChangeEvent} object encapsulating the model state change of the status bar.
   * @see java.beans.PropertyChangeEvent
   */
  public void propertyChange(PropertyChangeEvent event) {
    repaint();
  }

  /**
   * Suspends the state of the status bar.
   */
  public synchronized void pause() {

    if (isRunning()) {
      setPaused(true);
    }
  }

  /**
   * Invokes the status bar setting it to an active state and starting the StatusBarRunner to generate model
   * updates.
   */
  public synchronized void start() {

    if (isNotRunning()) {
      new Thread(new StatusBarRunner()).start();
      setPaused(false);
      setRunning(true);
    }
    else if (isPaused()) {
      setPaused(false);
      notifyAll();
    }
  }

  /**
   * Sets the status bar to an inactive state by stopping the StatusBarRunner from sending model updates.
   */
  public synchronized void stop() {

    setPaused(false);
    setRunning(false);
    notifyAll();
  }

  /**
   * {@link AbstractBean} implementation modeling the visual bar that is displayed inside a status bar.
   *
   * @see org.cp.elements.beans.AbstractBean
   * @see org.cp.elements.security.model.User
   */
  @SuppressWarnings("all")
  protected static abstract class AbstractStatusBarModel extends AbstractBean<Integer, User<Integer>, String> {

    protected static final String STATUS_BAR_MODEL_TO_STRING =
      "{ @type = %s, color = %s, origin = %s, size = %s, visible = %s }";

    private boolean visible = false;
    private Color color = DEFAULT_BAR_COLOR;
    private Dimension size;
    private Point origin;

    /**
     * Constructs a new {@link AbstractStatusBarModel}.
     *
     * Registers {@literal origin} and {@literal size} as required properties.
     */
    public AbstractStatusBarModel() {

      register("origin", RequiredFieldVetoableChangeListener.INSTANCE);
      register("size", RequiredFieldVetoableChangeListener.INSTANCE);
    }

    /**
     * Returns a Color object specifying the color of the bar.
     * @return a Color object specifying the color of the bar.
     */
    public Color getColor() {
      return this.color;
    }

    /**
     * Sets the color of the specified bar.
     * @param color a Color object used to paint the bar.
     */
    public void setColor(Color color) {
      processChange("color", getColor(), ObjectUtils.returnFirstNonNullValue(color, DEFAULT_BAR_COLOR));
    }

    /**
     * Return a Point object specifying the origin of the bar in the coordinate system.
     * @return a Point object specifying the bar's location.
     */
    protected Point getOrigin() {
      return this.origin;
    }

    /**
     * Set the originating location of the bar in the coordinate system.
     * @param origin a Point specifying the originating location of the bar.
     */
    protected void setOrigin(Point origin) {
      processChange("origin", getOrigin(), origin);
    }

    /**
     * Returns a Dimension object specifying the size of the bar.
     * @return a Dimension object specifying the width and heigth of the bar.
     */
    public Dimension getSize() {
      return this.size;
    }

    /**
     * Returns the size of the bar.
     * @param size a Dimension object specifying the width and height of the bar.
     */
    public void setSize(Dimension size) {
      processChange("size", getSize(), size);
    }

    /**
     * Returns the value represented by this data model.
     * @return an Object containing the value represented by this data model.
     */
    public final Object getValue() {
      throw new UnsupportedOperationException(Constants.NOT_IMPLEMENTED);
    }

    /**
     * Sets the value to be represented by this data model.
     * @param value an Object containing the value to be represented by this data model.
     */
    public final void setValue(Object value) {
      throw new UnsupportedOperationException(Constants.NOT_IMPLEMENTED);
    }

    /**
     * Determines whether the bar is visible.
     * @return a boolean value indicating if the bar is visible from the UI.
     */
    protected boolean isVisible() {
      return this.visible;
    }

    /**
     * Sets whether the bar is visible, thus, whether the bar should be painted.
     * @param visible a boolean value indicating if the bar should be made visible (painted).
     */
    protected void setVisible(boolean visible) {
      processChange("visible", isVisible(), visible);
    }

    /**
     * Moves the visual bar within the status bar one unit.
     *
     * A unit can be defined as a 1 to N pixels in screen coordinates, 1 to N using world units, or whatever
     * the implementing class deems for the value of the unit. Note, move causes model events to be fired to
     * update the view.
     *
     * @param area {@link Dimension} specifying the area in which the status bar can move.
     * @see java.awt.Dimension
     */
    protected abstract void move(Dimension area);

    /**
     * Paints the user interface (UI) of the bar.
     *
     * @param g2 {@link Graphics2D} context used to perform the paint operations and draw the status bar.
     * @see java.awt.Graphics2D
     */
    protected abstract void paint(Graphics2D g2);

    /**
     * Registers a {@link PropertyChangeListener} for the given {@link String named} property.
     *
     * @param propertyName {@link String} containing the {@literal name} of the property on {@literal this} {@link Bean}
     * for which the listener will be notified of {@link PropertyChangeEvent PropertyChangeEvents}.
     * @param listener {@link PropertyChangeListener} listening for changes to the given {@link String named} property
     * of {@literal this} {@link Bean}.
     * @see java.beans.PropertyChangeListener
     */
    @Override
    protected void register(String propertyName, PropertyChangeListener listener) {
      super.register(propertyName, listener);
    }

    /**
     * Returns a {@link String} representing this {@link AbstractStatusBarModel}.
     *
     * @return a {@link String} describing this {@link AbstractStatusBarModel}.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return String.format(STATUS_BAR_MODEL_TO_STRING, getClass().getName(),
        getColor(), getOrigin(), getSize(), isVisible());
    }
  }

  /**
   * {@link Runnable} implementation used to animate the movement of the status bar.
   *
   * @see java.lang.Runnable
   */
  protected class StatusBarRunner implements Runnable {

    @SuppressWarnings("all")
    public void run() {

      getStatusBarModel().setVisible(true);

      while (isRunning()) {
        getStatusBarModel().move(getSize());
        try {
          Thread.sleep(getWaitTime());
          while (isPaused()) {
            synchronized (AbstractStatusBar.this) {
              AbstractStatusBar.this.wait();
            }
          }
        }
        catch (InterruptedException ignore) { }
      }

      getStatusBarModel().setVisible(false);
    }
  }
}
