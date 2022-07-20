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

import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.cp.desktop.util.Strings;

public class JPercentField extends JTextField {

  private static final String PERCENT_SIGN = "%";
  private static final String DEFAULT_PERCENT_VALUE = "0.0" + PERCENT_SIGN;

  private static final Toolkit TOOLKIT = Toolkit.getDefaultToolkit();

  /**
   * Creates an instance of the JPercentField class to represent percent values in a text field component.
   */
  public JPercentField() {
    this(DEFAULT_PERCENT_VALUE);
  }

  /**
   * Creates an instance of the JPercentField class to represent percent values in a text field component
   * initialized to the specified percent value.
   * @param percentage a String representation of the percent value used to initialize this percent field
   * component.
   */
  public JPercentField(String percentage) {

    super(8);

    addKeyListener(new PercentFieldKeyListener());
    setText(percentage);
  }

  /**
   * Creates the default implementation of the model to be used at construction if one isn't explicitly given.
   * Returns an instance of PercentDocument.
   * @return the default javax.swing.text.Document object used to model percentages input this input field
   * component.
   */
  protected Document createDefaultModel() {
    return new PercentDocument();
  }

  /**
   * Returns a BigDecimal object containing a decimal value representing the percentage contained by this
   * percent field.
   * @return a java.math.BigDecimal value representing the percentage value of the percent field component
   * as a fractional decimal value.
   */
  public BigDecimal getPercentage() {

    String percentValue = getText();

    // remove the percent sign
    BigDecimal decimalValue = new BigDecimal(percentValue.substring(0, percentValue.length() - 1));

    // calculate the decimal value by dividing by 100%
    return decimalValue.divide(new BigDecimal(100.0), BigDecimal.ROUND_HALF_EVEN);
  }

  /**
   * The overridden setText method sets the content of this percent field component to the specified percentage value.
   * @param text a String specifying the percentage value to populate this text field component.
   */
  public void setText(String text) {
    if (Strings.isEmpty(text)) {
      text = DEFAULT_PERCENT_VALUE;
    }
    if (!text.trim().endsWith(PERCENT_SIGN)) {
      text = text.trim() + PERCENT_SIGN;
    }
    super.setText(text);
  }

  /**
   * The PercentDocument class is used by the JPercentField component to format the JTextField component
   * as a percentage input field.
   */
  private class PercentDocument extends PlainDocument {

    private static final char DECIMAL_POINT_CHARACTER = '.';
    private static final char NEGATIVE_SIGN_CHARACTER = '-';
    private static final char PERCENT_SIGN_CHARACTER = '%';

    private static final String DECIMAL_POINT = ".";
    private static final String NEGATIVE_SIGN = "-";

    public void insertString(int offset, String value, AttributeSet attrSet) throws BadLocationException {

      if (!isValidPercent(offset, value)) {
        TOOLKIT.beep();
      }
      else {
        super.insertString(offset, value, attrSet);
      }
    }

    /**
     * Determines whether the String object being inserted into this percent field with the current percent value
     * will maintain a valid percentage.
     * @param offset is an integer offset into the document.
     * @param value a String value to insert into the document providing validation criteria are met.
     * @throws BadLocationException if the insert position is not a valid position within the document.
     */
    private boolean isValidPercent(int offset, String value) throws BadLocationException {

      String currentPercentValue = this.getText(0, getLength());

      if (value.split("\\.").length > 2) {
        return false;
      }

      if (Strings.contains(value, DECIMAL_POINT) && Strings.contains(currentPercentValue, DECIMAL_POINT)) {
        return false;
      }

      if (Strings.contains(value, PERCENT_SIGN) && Strings.contains(currentPercentValue, PERCENT_SIGN)) {
        return false;
      }

      boolean valid = true; // innocent until proven guilty

      for (int index = 0, len = value.length(); index < len && valid; index++) {
        char c = value.charAt(index);
        switch (offset + index) {
          case 0:
            valid &= !Strings.contains(this.getText(0, 1), NEGATIVE_SIGN) &&
              ((c == NEGATIVE_SIGN_CHARACTER) || (c == DECIMAL_POINT_CHARACTER) || Character.isDigit(c));
            break;
          default:
            valid &= (((c == PERCENT_SIGN_CHARACTER) && (index == (len - 1)))
              || (c == DECIMAL_POINT_CHARACTER) || Character.isDigit(c));
        }
      }
      return valid;
    }

    public void remove(int offset, int length) throws BadLocationException {
      if (Strings.contains(this.getText(offset, length), PERCENT_SIGN)) {
        return;
      }
      super.remove(offset, length);
    }
  }

  /**
   * The PercentFieldKeyListener is used by the JPercentField component to track key events targeted at
   * editing and traversing the percentage field component.
   */
  public class PercentFieldKeyListener extends KeyAdapter {

    /**
     * Invoked when a key has been pressed.  This method validates navigation for the JPercentField.
     */
    public void keyPressed(KeyEvent event) {

      switch (event.getKeyCode()) {
        case KeyEvent.VK_END:
          getCaret().setDot(getDocument().getLength() - 1);
          event.consume();
          break;
        case KeyEvent.VK_RIGHT:
          if (getCaret().getDot() == (getDocument().getLength() - 1)) {
            event.consume();
          }
          break;
      }
    }
  }
}
