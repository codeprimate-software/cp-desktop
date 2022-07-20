/*
 * CStatusBar.java (c) 3 May 2001
 *
 * Copyright (c) 2001, Code Primate
 * All Rights Reserved
 * @author John J. Blum
 * @version 2009.5.27
 * @see com.cp.common.swing.JStatusBar
 * @see java.awt.Canvas
 */
package org.cp.desktop.awt;

import static org.cp.elements.lang.LangExtensions.assertThat;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Objects;
import java.util.logging.Logger;

import org.cp.desktop.util.Strings;
import org.cp.elements.lang.ObjectUtils;

@SuppressWarnings("unused")
public class CStatusBar extends Canvas {

  private static final int DEFAULT_BAR_LENGTH = 10;
  private static final int DEFAULT_BAR_SPEED = 90;
  private static final int MAX_BAR_SPEED = 99;
  private static final int MIN_BAR_SPEED = 1;

  private static final Color DEFAULT_BAR_COLOR = new Color(0, 0, 128); // Dark Blue
  private static final Color DEFAULT_TEXT_COLOR = Color.black;
  private static final Color DEFAULT_TEXT_OVERLAY_COLOR = Color.white;

  private static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 12);

  private boolean pause = false;
  private boolean running = false;

  private int barLength = DEFAULT_BAR_LENGTH;
  private int barSpeed = DEFAULT_BAR_SPEED;

  private Color barColor = DEFAULT_BAR_COLOR;
  private Color textColor = DEFAULT_TEXT_COLOR;
  private Color textOverlayColor = DEFAULT_TEXT_OVERLAY_COLOR;

  private Dimension previousStatusBarSize = new Dimension(0, 0);

  private Font textFont = DEFAULT_FONT;

  private Graphics graphicsBuffer;

  private Image imageBuffer;

  private final Logger logger = Logger.getLogger(getClass().getName());

  private final Rectangle bar;

  private String text = "";

  /**
   * Constructs a new instance of the {@link CStatusBar} GUI component with no {@link String message}.
   */
  public CStatusBar() {
    this(null);
  }

  /**
   * Constructs a new instance of the {@link CStatusBar} GUI component initialized with the given, initial
   * {@link String message} along with the default {@link Color} and {@link Font}.
   *
   * @param text {@link String} containing the {@literal message} to display in the status bar.
   */
  public CStatusBar(String text) {
    setText(text);
    bar = new Rectangle(new Dimension(0, 0));
  }

  /**
   * Returns the color of the status bar.
   *
   * @return a Color object specifying the color of the status bar.
   */
  public Color getBarColor() {
    return barColor;
  }

  /**
   * Sets the color of the status bar.  If the barColor parameter is null, then the color of the status bar
   * is set to the DEFAULT_BAR_COLOR color.
   *
   * @param barColor a Color object specifying the color of the status bar.
   */
  public void setBarColor(Color barColor) {
    this.barColor = ObjectUtils.returnFirstNonNullValue(barColor, DEFAULT_BAR_COLOR);
  }

  /**
   * Returns an integer value specifying the pixel length of the status bar.  Note, this is not
   * the same as the size of the CStatusBar component.
   *
   * @return a integer value specifying the length in pixels of the status bar.
   */
  public int getBarLength() {
    return barLength;
  }

  /**
   * Sets the length of the status bar in pixels.  Note, this is not the same as the size of
   * the CStatusBar component.
   *
   * @param barLength an integer value specifying the length of the status bar in pixels.
   */
  public void setBarLength(int barLength) {

    assertThat(barLength)
      .describedAs("The length of the bar cannot be less than 0")
      .isGreaterThanEqualTo(0);

    this.barLength = barLength;
  }

  /**
   * Returns the current speed that the status bar moves measured from 1..99, where 99 is the fastest.
   *
   * @return a integer value indicating the speed of the status bar.
   */
  public int getBarSpeed() {
    return barSpeed;
  }

  /**
   * Sets the speed of the status bar.
   *
   * @param barSpeed is an integer value specifying the speed of the status bar.
   */
  public void setBarSpeed(int barSpeed) {

    assertThat(barSpeed)
      .describedAs("The bar speed [%d] is not valid; please enter a value between [%d] and [%d]",
        barSpeed, MIN_BAR_SPEED, MAX_BAR_SPEED)
      .isGreaterThanEqualToAndLessThanEqualTo(MIN_BAR_SPEED, MAX_BAR_SPEED);

    this.barSpeed = barSpeed;
  }

  /**
   * Returns the Font used for the text of the status bar.
   *
   * @return a Font object specifying the type face of the text in the status bar.
   */
  public Font getFont() {
    return textFont;
  }

  /**
   * Sets the Font used for the text in the status bar.
   *
   * @param textFont is a Font object specifying the type face of the text in the status bar.
   */
  public void setFont(Font textFont) {
    this.textFont = ObjectUtils.returnFirstNonNullValue(textFont, DEFAULT_FONT);
  }

  /**
   * Returns the text displayed in the UI of the status bar component.
   *
   * @return a String value specifying the text message displayed in the status bar of the UI.
   */
  public String getText() {
    return text;
  }

  /**
   * Sets the text to be displayed in the status bar of the UI.
   *
   * @param text a java.lang.String value specifying the text message to be displayed in the
   * status bar UI.
   */
  public void setText(String text) {
    this.text = text;
  }

  /**
   * Returns the color of the text display in the status bar.
   *
   * @return a Color object specifying the color of the text in the status bar.
   */
  public Color getTextColor() {
    return textColor;
  }

  /**
   * Sets the color of the text being displayed in the UI of the status bar.
   *
   * @param textColor a Color object specifying the text color of the message displayed in the
   * status bar.
   */
  public void setTextColor(Color textColor) {
    this.textColor = ObjectUtils.returnFirstNonNullValue(textColor, DEFAULT_TEXT_COLOR);
  }

  /**
   * Returns the color of the text when the bar is directly beneath the text.
   *
   * @return a Color object specifying the color of the text when the bar is behind the text.
   */
  public Color getTextOverlayColor() {
    return textOverlayColor;
  }

  /**
   * Sets the color of the text when the bar is behind it.
   *
   * @param textOverlayColor a Color object specifying the color of the text when the bar is behind
   * the text.
   */
  public void setTextOverlayColor(Color textOverlayColor) {
    this.textOverlayColor = ObjectUtils.returnFirstNonNullValue(textOverlayColor, DEFAULT_TEXT_OVERLAY_COLOR);
  }

  /**
   * Builds text segments for the given text when the status bar is running.
   *
   * @return an TextSegment which is a composition of text partitions representing the entire text.
   */
  private TextSegment getTextSegment() {

    FontMetrics fontMetrics = getFontMetrics(getFont());

    int x = 5;
    int y = ((int) getSize().getHeight() - fontMetrics.getMaxDescent());

    TextSegment previousTextSegment = null;
    StringBuilder buffer = new StringBuilder();

    // initial peak
    boolean contains = bar.contains(new Point(x + fontMetrics.charWidth(getText().charAt(0)), y));

    if (isRunning()) {
      for (CharacterIterator it = new StringCharacterIterator(getText()); it.current() != CharacterIterator.DONE; it.next()) {

        char currentCharacter = it.current();

        boolean flag = bar.contains(new Point(x + fontMetrics.stringWidth(buffer.toString()) + fontMetrics.charWidth(currentCharacter), y));

        if (contains != flag) {
          // build text segment; add to text composition
          TextSegment tempTextSegment =
            new DefaultTextSegment(buffer.toString(), new Point(x, y), (contains ? getTextOverlayColor() : getTextColor()));

          previousTextSegment = TextComposition.compose(previousTextSegment, tempTextSegment);

          // reset state...
          x += fontMetrics.stringWidth(buffer.toString());
          contains = flag;
          buffer.delete(0, buffer.length()); // clear the character buffer
          buffer.append(currentCharacter); // do not forget about the character just read!
        }
        else {
          buffer.append(currentCharacter);
        }
      }

      // append any remaining characters to the end of the text segment
      if (buffer.length() > 0) {
        TextSegment tempTextSegment = new DefaultTextSegment(buffer.toString(), new Point(x, y),
          (contains ? getTextOverlayColor() : getTextColor()));
        previousTextSegment = TextComposition.compose(previousTextSegment, tempTextSegment);
      }
    }
    else {
      previousTextSegment = new DefaultTextSegment(getText(), new Point(x, y), getTextColor());
    }

    return previousTextSegment;
  }

  /**
   * Returns a String containing the current state of the status bar.
   *
   * @return a String containing the state of the status bar.
   */
  public String toString() {

    StringBuilder buffer = new StringBuilder("{barColor = ");

    buffer.append(getBarColor());
    buffer.append(", barLength = ").append(getBarLength());
    buffer.append(", barSpeed = ").append(getBarSpeed());
    buffer.append(", font = ").append(getFont());
    buffer.append(", paused = ").append(isPaused());
    buffer.append(", text = ").append(getText());
    buffer.append(", textColor = ").append(getTextColor());
    buffer.append(", textOverlayColor = ").append(getTextOverlayColor());
    buffer.append(", running = ").append(isRunning());
    buffer.append("}:").append(getClass().getName());

    return buffer.toString();
  }

  /**
   * Determines whether the status bar Thread is paused.
   *
   * @return a boolean value indicating whether the status bar thread is paused or not.
   */
  public boolean isPaused() {
    return pause;
  }

  /**
   * Sets whether the status bar Thread is paused or running.
   *
   * @param pause is a boolean value indicating whether to pause the status bar Thread.
   */
  private void setPaused(boolean pause) {
    this.pause = pause;
  }

  /**
   * Determines whether the status bar thread is running or not.
   *
   * @return a boolean value indicating whether the status bar thread is running or not.
   */
  public boolean isRunning() {
    return running;
  }

  /**
   * Sets the run state of the status bar thread.
   *
   * @param running boolean value indicating the run state of the status bar thread.
   */
  private void setRunning(boolean running) {
    this.running = running;
  }

  /**
   * Pauses the status bar Thread; freezes the view.
   */
  public synchronized void pause() {

    if (isRunning()) {
      setPaused(true);
    }
  }

  /**
   * Start the Thread that updates the status bar UI.
   */
  public synchronized void start() {

    if (!isRunning()) {
      logger.fine("Starting StatusBarRunner Thread...");
      setRunning(true);
      new Thread(new StatusBarRunner()).start();
    }
    else if (isPaused()) {
      setPaused(false);
      notifyAll();
    }

    logger.fine("StatusBarRunner Thread is running");
  }

  /**
   * Stops the Thread that updates the status bar UI.
   */
  public synchronized void stop() {
    logger.fine("Stopping StatusBarRunner Thread");
    setPaused(false);
    setRunning(false);
    notifyAll();
  }

  /**
   * Creates an offscreen buffer implementing double buffering to store graphic primitives.
   *
   * @return a offscreen Graphics context (graphics buffer).
   */
  private Graphics getGraphicsBuffer() {

    Dimension currentStatusBarSize = getSize();

    if (!currentStatusBarSize.equals(previousStatusBarSize)) {
      imageBuffer = createImage(currentStatusBarSize.width, currentStatusBarSize.height);
      graphicsBuffer = imageBuffer.getGraphics();
      previousStatusBarSize = currentStatusBarSize;
    }

    return graphicsBuffer;
  }

  /**
   * Paints the current view of the status bar state.
   *
   * @param graphics java.awt.Graphics object used to paint the status bar view (GUI).
   */
  public void paint(Graphics graphics) {

    Graphics graphicsBuffer = getGraphicsBuffer();

    paintBackground(graphicsBuffer);
    paintBorder(graphicsBuffer);

    graphicsBuffer.setClip(2, 2, previousStatusBarSize.width - 2, previousStatusBarSize.height - 2);

    paintBar(graphicsBuffer);
    paintText(graphicsBuffer);

    graphics.drawImage(imageBuffer, 0, 0, null);
  }

  /**
   * Paints the background of the status bar.
   *
   * @param graphicsBuffer the Graphics object used to perform the paint operations.
   */
  private void paintBackground(Graphics graphicsBuffer) {

    Dimension currentStatusBarSize = getSize();

    graphicsBuffer.setColor(Color.lightGray);
    graphicsBuffer.fillRect(0, 0, currentStatusBarSize.width, currentStatusBarSize.height);
  }

  /**
   * Paints the bar portion of the status bar.
   *
   * @param graphicsBuffer the Graphics object used to perform the paint operations.
   */
  private void paintBar(Graphics graphicsBuffer) {

    if (isRunning()) {
      graphicsBuffer.setColor(getBarColor());
      graphicsBuffer.fillRect(bar.x, bar.y, bar.width, bar.height);
    }
  }

  /**
   * Paints the border around the status bar.
   *
   * @param graphicsBuffer the Graphics object used to perform the paint operations.
   */
  private void paintBorder(Graphics graphicsBuffer) {

    Dimension currentStatusBarSize = getSize();

    graphicsBuffer.setColor(SystemColor.controlLtHighlight);
    graphicsBuffer.drawLine(currentStatusBarSize.width - 1, 1, currentStatusBarSize.width - 1,
      currentStatusBarSize.height - 1); // Right Edge
    graphicsBuffer.drawLine(0, currentStatusBarSize.height - 1, currentStatusBarSize.width,
      currentStatusBarSize.height - 1); // Bottom Edge
    graphicsBuffer.setColor(SystemColor.controlDkShadow);
    graphicsBuffer.drawLine(0, 1, currentStatusBarSize.width, 1); // Top Edge
    graphicsBuffer.drawLine(0, 1, 0, currentStatusBarSize.height - 1); // Left Edge
  }

  /**
   * Paints the text overlay on the status bar.
   *
   * @param graphicsBuffer the Graphics object used to perform the paint operations.
   */
  private void paintText(Graphics graphicsBuffer) {

    if (Strings.hasText(getText())) {
      graphicsBuffer.setFont(getFont());
      getTextSegment().paint(graphicsBuffer);
    }
  }

  /**
   * Overridden Component.update method to implement double buffering in the status bar view.
   *
   * @param graphics java.awt.Graphics object used for paint operations in the status bar.
   */
  public void update(Graphics graphics) {
    paint(graphics);
  }

  /**
   * StatusBarRunner is a Thread that computes the position and movement of the bar within the status bar,
   * affecting the text color variations.
   */
  private class StatusBarRunner implements Runnable {

    /**
     * Initializes the status bar.
     */
    private void initStatusBar() {
      bar.setLocation(new Point(-getBarLength(), 0));
      bar.setSize(new Dimension(getBarLength(), (int) getSize().getHeight()));
    }

    /**
     * Resets the state of the status bar.
     */
    private void resetStatusBar() {
      bar.setLocation(new Point(0, 0));
      bar.setSize(new Dimension(0, 0));
    }

    /**
     * Computes position of the bar in the status bar.
     */
    @SuppressWarnings("all")
    public void run() {

      Graphics graphics = getGraphics();

      update(graphics);
      initStatusBar();

      boolean forward = true;

      while (isRunning()) {
        if (forward) {
          if (bar.x < previousStatusBarSize.width) {
            bar.x++;
          }
          else {
            forward = false;
          }
        }
        else {
          if (bar.x > -getBarLength()) {
            bar.x--;
          }
          else {
            forward = true;
          }
        }

        update(graphics);

        try {
          Thread.sleep((MAX_BAR_SPEED + 1) - getBarSpeed());
          while (isPaused()) {
            synchronized (CStatusBar.this) {
              CStatusBar.this.wait();
            }
          }
        }
        catch (InterruptedException ignore) { }
      }

      resetStatusBar();
      update(graphics);
    }
  }

  /**
   * Interface to define a text segment, or portion of some fixed text.
   */
  private interface TextSegment {
    void paint(Graphics graphics);
  }

  /**
   * TextComposition represents a composition of text segments, which is itself a text segment.
   */
  private static class TextComposition implements TextSegment {

    private final TextSegment textSegment0;
    private final TextSegment textSegment1;

    private TextComposition(TextSegment textSegment0, TextSegment textSegment1) {

      this.textSegment0 = textSegment0;
      this.textSegment1 = textSegment1;
    }

    public static TextSegment compose(TextSegment textSegment0, TextSegment textSegment1) {

      return Objects.nonNull(textSegment0) ? textSegment1
        : Objects.isNull(textSegment1) ? textSegment0
        : new TextComposition(textSegment0, textSegment1);
    }

    public void paint(Graphics graphics) {
      textSegment0.paint(graphics);
      textSegment1.paint(graphics);
    }
  }

  /**
   * DefaultTextSegment is used to represent different portions of the text for this status bar.
   */
  private static class DefaultTextSegment implements TextSegment {

    private final Color textColor;
    private final Point position;
    private final String text;

    private DefaultTextSegment(String text, Point position, Color textColor) {
      this.text = text;
      this.position = position;
      this.textColor = textColor;
    }

    public Point getPosition() {
      return position;
    }

    public String getText() {
      return text;
    }

    public Color getTextColor() {
      return textColor;
    }

    public void paint(Graphics graphics) {
      graphics.setColor(getTextColor());
      graphics.drawString(getText(), (int) getPosition().getX(), (int) getPosition().getY());
    }

    @Override
    public boolean equals(Object obj) {

      if (this == obj) {
        return true;
      }

      if (!(obj instanceof DefaultTextSegment)) {
        return false;
      }

      DefaultTextSegment that = (DefaultTextSegment) obj;

      return ObjectUtils.equals(getPosition(), that.getPosition())
        && ObjectUtils.equals(getText(), that.getText())
        && ObjectUtils.equals(getTextColor(), that.getTextColor());
    }

    @Override
    public int hashCode() {

      int hashValue = 17;

      hashValue = 37 * hashValue + ObjectUtils.hashCode(getPosition());
      hashValue = 37 * hashValue + ObjectUtils.hashCode(getText());
      hashValue = 37 * hashValue + ObjectUtils.hashCode(getTextColor());

      return hashValue;
    }

    @Override
    public String toString() {

      StringBuilder buffer = new StringBuilder("{position = ");

      buffer.append(getPosition());
      buffer.append(", text = ").append(getText());
      buffer.append(", textColor = ").append(getTextColor());
      buffer.append("}:").append(getClass().getName());

      return buffer.toString();
    }
  }
}
