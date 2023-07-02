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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import javax.swing.text.Position;

import org.cp.desktop.swing.event.RequiredFieldVetoableChangeListener;
import org.cp.elements.beans.IllegalPropertyValueException;
import org.cp.elements.lang.ObjectUtils;
import org.cp.elements.lang.annotation.NotNull;
import org.cp.elements.lang.annotation.Nullable;

@SuppressWarnings("unused")
public abstract class AbstractTextStatusBar extends AbstractStatusBar {

  private static final Color DEFAULT_TEXT_COLOR = Color.black;
  private static final Color DEFAULT_TEXT_OVERLAY_COLOR = Color.white;

  private static final Font DEFAULT_TEXT_FONT = new Font("Arial", Font.PLAIN, 12);

  private static final Position DEFAULT_TEXT_OFFSET = newPosition(5);

  private Color textColor = DEFAULT_TEXT_COLOR;
  private Color textOverlayColor = DEFAULT_TEXT_OVERLAY_COLOR;

  private Font textFont = DEFAULT_TEXT_FONT;

  private TextSegment textSegment;

  private Position textOffset = DEFAULT_TEXT_OFFSET;

  private String text;

  /**
   * Constructs a new {@link AbstractTextStatusBar} user interface (UI) component.
   * <p>
   * This constructor also registers the {@link TextPropertyChangeListener} as an event handler used to listen for
   * text change events on this text component in order to update the view with the new text.
   */
  public AbstractTextStatusBar() {

    TextPropertyChangeListener textChangeHandler = new TextPropertyChangeListener();

    addPropertyChangeListener("text", textChangeHandler);
    addPropertyChangeListener("textColor", textChangeHandler);
    addPropertyChangeListener("textFont", textChangeHandler);
    addPropertyChangeListener("textOffset", textChangeHandler);
    addVetoableChangeListener(new RequiredFieldVetoableChangeListener("text"));
  }

  /**
   * Factory method used to construct a new {@link Position} initialized with the given offset.
   *
   * @param offset {@link Integer} value specifying the offset from some origin.
   * @return a new {@link Position} with the given offset.
   */
  protected static Position newPosition(int offset) {
    return () -> offset;
  }

  /**
   * Factory method used to construct a new {@link TextSegment {, representing the specified portion of text.
   * <p>
   * @param text the String value denoting the portion of text the segment will represent.
   * @param origin a Point value specifying the location of the text in the given context.
   * @param textColor the color of the specified text.
   * @return a TextSegment object representing the text at the specified location (origin),
   * having the specified color.
   */
  protected static TextSegment newTextSegment(String text, Point origin, Color textColor) {
    return newTextSegment(text, origin, textColor, DEFAULT_TEXT_FONT);
  }

  /**
   * Factory method used to construct a new {@link TextSegment}, representing the specified portion of text.
   *
   * @param text the String value denoting the portion of text the segment will represent.
   * @param origin a Point value specifying the location of the text in the given context.
   * @param textColor the color of the specified text.
   * @param textFont the font of the specified text.
   * @return a TextSegment object representing the text at the specified location (origin),
   * having the specified color.
   */
  protected static TextSegment newTextSegment(String text, Point origin, Color textColor, Font textFont) {
    return new DefaultTextSegment(text, origin, textColor, textFont);
  }

  /**
   * Returns the text to display in the status bar.
   * @return a String value denoting the text displayed in the status bar.
   */
  public String getText() {
    return this.text;
  }

  /**
   * Sets the specified text to be displayed in this status bar.
   * @param text a String value denoting the text to be displayed in this status bar.
   */
  public void setText(String text) {

    try {
      String previousText = getText();
      fireVetoableChange("text", previousText, text);
      this.text = text;
      firePropertyChange("text", previousText, text);
    }
    catch (PropertyVetoException cause) {
      throw new IllegalPropertyValueException(String.format("Text [%s] is not valid", text), cause);
    }
  }

  /**
   * Returns the color of the text when not overlayed on the bar.
   * @return a Color object specifying the color of the text when not overlayed on the bar.
   */
  public Color getTextColor() {
    return this.textColor;
  }

  /**
   * Sets the color of the text when the text is NOT displayed over the bar.
   * @param textColor a Color object specifying the color of the text when the text is NOT displayed over the bar.
   */
  public void setTextColor(Color textColor) {
    Color previousTextColor = getTextColor();
    this.textColor = ObjectUtils.returnFirstNonNullValue(textColor, DEFAULT_TEXT_COLOR);
    firePropertyChange("textColor", previousTextColor, this.textColor);
  }

  /**
   * Returns the font used as the face for the text.
   * @return a Font object specifying the face of the font.
   */
  public Font getTextFont() {
    return this.textFont;
  }

  /**
   * Sets the font used as the face for the text.
   * @param textFont a Font object specifying the face of the font.
   */
  public void setTextFont(Font textFont) {
    Font previousTextFont = getTextFont();
    this.textFont = ObjectUtils.returnFirstNonNullValue(textFont, DEFAULT_TEXT_FONT);
    firePropertyChange("textFont", previousTextFont, this.textFont);
  }

  /**
   * Returns the offset in the specified context at which to begin displaying the text.
   * @return a Position object denoting the location (offset) at which to begin displaying the text.
   */
  public Position getTextOffset() {
    return this.textOffset;
  }

  /**
   * Sets the offset for the specified context at which to begin displaying the text.
   * @param textOffset a Position object denoting the location (offset) at which to begin displaying the text.
   */
  public void setTextOffset(Position textOffset) {
    Position previousTextOffset = getTextOffset();
    this.textOffset = ObjectUtils.returnFirstNonNullValue(textOffset, DEFAULT_TEXT_OFFSET);
    firePropertyChange("textOffset", previousTextOffset, this.textOffset);
  }

  /**
   * Returns the color of the text when the text is displayed over the bar.
   * @return a Color object specifying the color of the text when displayed over the bar.
   */
  public Color getTextOverlayColor() {
    return this.textOverlayColor;
  }

  /**
   * Sets the color of the text when the text is displayed over the bar.
   * @param textOverlayColor a Color object specifying the color of the text when displayed over the bar.
   */
  public void setTextOverlayColor(Color textOverlayColor) {
    Color previousTextOverlayColor = getTextOverlayColor();
    this.textOverlayColor = ObjectUtils.returnFirstNonNullValue(textOverlayColor, DEFAULT_TEXT_OVERLAY_COLOR);
    firePropertyChange("textOverlayColor", previousTextOverlayColor, this.textOverlayColor);
  }

  /**
   * Returns the composed text segment for the specified text used by the status bar.
   * @return a TextSegment object composed of various ITextSegments representing portions of the text
   * displayed by this status bar.
   */
  public final TextSegment getTextSegment() {

    if (this.textSegment == null) {
      this.textSegment = newTextSegment(getText(), new Point(getTextOffset().getOffset(),
        (getSize().height - getFontMetrics(getTextFont()).getMaxDescent())), DEFAULT_TEXT_COLOR);
    }

    return this.textSegment;
  }

  /**
   * Paints the status bar with the configured {@link String text} displayed over the bar.
   *
   * @param g2 {@link Graphics2D} object used to paint graphic primitives.
   * @see java.awt.Graphics2D
   */
  protected void paintStatusBar(Graphics2D g2) {

    super.paintStatusBar(g2);
    getTextSegment().paint(g2);
  }

  /**
   * Receives {@link PropertyChangeEvent events} from the {@link AbstractStatusBarModel} to refresh
   * the user interface (UI).
   *
   * @param event {@link PropertyChangeEvent} object indicating the model state change.
   * @see java.beans.PropertyChangeEvent
   * @see #recreateTextSegment()
   */
  public void propertyChange(PropertyChangeEvent event) {

    recreateTextSegment();
    super.propertyChange(event);
  }

  /**
   * Recomposes the text segment representing the various portions of the text.
   */
  private void recreateTextSegment() {

    FontMetrics fontMetrics = getFontMetrics(getTextFont());

    int x = getTextOffset().getOffset();
    int y = (int) getSize().getHeight() - fontMetrics.getMaxDescent();

    // initial peak
    boolean contains = ((AbstractTextStatusBarModel) getStatusBarModel())
      .contains(new Point(x + fontMetrics.charWidth(getText().charAt(0)), y));

    TextSegment previousTextSegment = null;
    StringBuilder buffer = new StringBuilder();

    if (isRunning()) {
      for (CharacterIterator it = new StringCharacterIterator(getText()); it.current() != CharacterIterator.DONE; it.next()) {

        char currentCharacter = it.current();

        // offset + current buffer width (characters contained in current iteration) + next character
        boolean flag = ((AbstractTextStatusBarModel) getStatusBarModel())
          .contains(new Point(x + fontMetrics.stringWidth(buffer.toString()) + fontMetrics.charWidth(currentCharacter), y));

        if (flag != contains) {
          // build text segment
          TextSegment tempTextSegment = newTextSegment(buffer.toString(), new Point(x, y),
            contains ? getTextOverlayColor() : getTextColor());

          // compose the text segments (composition)
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
        TextSegment tempTextSegment = newTextSegment(buffer.toString(), new Point(x, y), (contains ? getTextOverlayColor() : getTextColor()));
        previousTextSegment = TextComposition.compose(previousTextSegment, tempTextSegment);
      }
    }
    else {
      previousTextSegment = newTextSegment(getText(), new Point(x, y), getTextColor());
    }

    this.textSegment = previousTextSegment;
  }

  /**
   * TextComposition represents a composition of text segments, which is itself a text segment.
   */
  protected static class TextComposition implements TextSegment {

    private final TextSegment textSegment0;
    private final TextSegment textSegment1;

    private TextComposition(@NotNull TextSegment textSegment0, @NotNull TextSegment textSegment1) {
      this.textSegment0 = textSegment0;
      this.textSegment1 = textSegment1;
    }

    public static @Nullable TextSegment compose(@Nullable TextSegment textSegment0, @Nullable TextSegment textSegment1) {

      return textSegment0 == null ? textSegment1
        : textSegment1 == null ? textSegment0
        : new TextComposition(textSegment0, textSegment1);
    }

    public String getText() {
      return this.textSegment0.getText() + this.textSegment1.getText();
    }

    public void paint(Graphics2D g2) {
      this.textSegment0.paint(g2);
      this.textSegment1.paint(g2);
    }
  }

  /**
   * {@link PropertyChangeListener} implementation used to listen for text property changes
   * in the {@link AbstractTextStatusBar} UI component.
   */
  protected class TextPropertyChangeListener implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent event) {
      if (!isRunning() && AbstractTextStatusBar.this.isVisible()) {
        repaint();
      }
    }
  }

  /**
   * {@link Class Interface} to define a segment of {@link String text}, or some portion of fixed {@link String text}.
   */
  protected interface TextSegment {
    String getText();
    void paint(Graphics2D g2);
  }

  /**
   * {@link TextSegment} implementation used to represent different portions of the {@link String text} shown
   * in this status bar.
   */
  protected static class DefaultTextSegment implements TextSegment {

    protected static final String TEXT_SEGMENT_TO_STRING =
      "{ @type = %s, origin = %s, text = %s, textColor = %s, textFond = %s }";

    private final Color textColor;
    private final Font textFont;
    private final Point origin;
    private final String text;

    public DefaultTextSegment(String text, Point origin, Color textColor, Font textFont) {
      this.text = text;
      this.origin = origin;
      this.textColor = textColor;
      this.textFont = textFont;
    }

    public Point getOrigin() {
      return this.origin;
    }

    public String getText() {
      return this.text;
    }

    public Color getTextColor() {
      return this.textColor;
    }

    public Font getTextFont() {
      return this.textFont;
    }

    public void paint(Graphics2D g2) {

      g2.setColor(getTextColor());
      g2.setFont(getTextFont());
      g2.drawString(getText(), (int) getOrigin().getX(), (int) getOrigin().getY());
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

      return ObjectUtils.equals(getOrigin(), that.getOrigin())
        && ObjectUtils.equals(getText(), that.getText())
        && ObjectUtils.equals(getTextColor(), that.getTextColor())
        && ObjectUtils.equals(getTextFont(), that.getTextFont());
    }

    @Override
    public int hashCode() {
      return ObjectUtils.hashCodeOf(getOrigin(), getText(), getTextColor(), getTextFont());
    }

    @Override
    public String toString() {
      return String.format(TEXT_SEGMENT_TO_STRING, getClass().getName(),
        getOrigin(), getText(), getTextColor(), getTextFont());
    }
  }

  /**
   * {@link AbstractStatusBarModel} extensions adding functionality to determine overlap between the text
   * and the moving bar.
   *
   * @see org.cp.desktop.swing.AbstractStatusBar.AbstractStatusBarModel
   */
  protected static abstract class AbstractTextStatusBarModel extends AbstractStatusBarModel {
    public abstract boolean contains(Point position);
  }
}
