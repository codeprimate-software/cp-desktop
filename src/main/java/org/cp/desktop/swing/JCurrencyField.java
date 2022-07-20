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

import org.cp.elements.lang.StringUtils;

@SuppressWarnings("unused")
public final class JCurrencyField extends JTextField {

  private static final char DECIMAL_POINT_CHARACTER = '.';
  private static final char DOLLAR_SIGN_CHARACTER = '$';
  private static final char NEGATIVE_SIGN_CHARACTER = '-';

  private static final String DECIMAL_POINT = ".";
  private static final String DOLLAR_SIGN = "$";
  private static final String NEGATIVE_SIGN = "-";

  private static final String DEFAULT_CURRENCY_VALUE = DOLLAR_SIGN + "0.00";

  private static final Toolkit TOOLKIT = Toolkit.getDefaultToolkit();

  /**
   * Creates a new instance of the JCurrencyField class, which is an input component for currency in US dollars.
   */
  public JCurrencyField() {
    this(DOLLAR_SIGN);
  }

  /**
   * Creates a new instance of the JCurrencyField class input component initialized with the default monetary value
   * in US currency.
   * @param currency a String value specifyig the default monetary value to set the currency field component to.
   */
  public JCurrencyField(final String currency) {
    super(8);
    addKeyListener(new CurrencyFieldKeyListener());
    setText(currency);
    getCaret().setDot(1); // set the caret position past the dollar sign.
  }

  /**
   * Creates the default implementation of the model to be used at construction if one isn't explicitly given.
   * Returns an instance of CurrencyDocument.
   * @return the default javax.swing.text.Document object used to represent the content for this currency field
   * component.
   */
  protected Document createDefaultModel() {
    return new CurrencyDocument();
  }

  /**
   * Returns a BigDecimal representation of the monetary value contained in this currency field component.
   * @return a java.math.BigDecimal object containing the currency value represented by this currency field component.
   */
  public BigDecimal getCurrency() {
    return new BigDecimal(getText().substring(1)); // remember to remove the dollar sign
  }

  /**
   * The overridden setText method sets the content of this text field component to the specified String value
   * as if it were a currency value.
   * @param text a String value specifying the currency to populate this text field component.
   */
  public void setText(String text) {

    if (!StringUtils.hasText(text)) {
      text = DEFAULT_CURRENCY_VALUE;
    }
    if (!text.trim().startsWith(DOLLAR_SIGN)) {
      text = DOLLAR_SIGN + text.trim();
    }

    super.setText(text);
  }

  /**
   * The CurrencyDocument class is used by the JCurrencyField component to format the
   * JTextField component as a currency input field.
   * NOTE: on a text replacement, the AbstractDocument's (extended by PlainDocument) replace method
   * will call remove followed by insertString.
   */
  private static final class CurrencyDocument extends PlainDocument {

    /**
     * Validates the String value being inserted into the CurrencyField and performs the insert.
     */
    public void insertString(int offset, String value, AttributeSet attributeSet) throws BadLocationException {

      if (!isValidCurrency(offset, value))
        TOOLKIT.beep();
      else {
        super.insertString(offset, value, attributeSet);
      }
    }

    /**
     * Determines whether the String value being inserted into this currency field maintains a valid monetary value.
     * @param offset is an integer offset into the document.
     * @param value the String value being inserted into the document providing the currency validation criteria is met.
     * @throws BadLocationException if the offset position is not a valid position within the document
     * to insert the String value.
     */
    private boolean isValidCurrency(int offset, String value) throws BadLocationException {

      boolean containsDecimal = StringUtils.contains(this.getText(0, getLength()), DECIMAL_POINT);
      int decimalIndex = this.getText(0, getLength()).indexOf(DECIMAL_POINT);

      // check the number of digits after the decimal point
      if (containsDecimal && (decimalIndex < offset)) {
        String digitsAfterDecimalPoint = this.getText(decimalIndex + 1, (getLength() - (decimalIndex + 1)));
        if ((digitsAfterDecimalPoint.length() + value.length()) > 2) {
          return false;
        }
      }

      boolean valueContainsDecimal = StringUtils.contains(value, DECIMAL_POINT);

      decimalIndex = value.indexOf(DECIMAL_POINT);

      // validate value decimal point
      if (valueContainsDecimal && containsDecimal) {
        return false;
      }

      if (countOccurences(value, DECIMAL_POINT) > 1) {
        return false;
      }

      if (valueContainsDecimal && value.substring(decimalIndex + 1).length() > 2) {
        return false;
      }

      // validate currency value
      boolean valid = true; // innocent until proven guilty

      for (int indx = 0, len = value.length(); indx < len && valid; indx++) {
        char c = value.charAt(indx);
        switch (offset + indx) {
          case 0:
            boolean haveDollarSign = StringUtils.contains(this.getText(0, getLength()), DOLLAR_SIGN);
            valid &= (c == DOLLAR_SIGN_CHARACTER && !haveDollarSign);
            break;
          case 1:
            String firstChar = this.getText(1, 1);
            valid &= ((c == NEGATIVE_SIGN_CHARACTER && !NEGATIVE_SIGN.equals(firstChar))
              || (c == DECIMAL_POINT_CHARACTER && !(NEGATIVE_SIGN.equals(firstChar) || DECIMAL_POINT.equals(firstChar)))
              || (Character.isDigit(c) && !NEGATIVE_SIGN.equals(firstChar)));
            break;
          default:
            valid &= (Character.isDigit(c) || (c == DECIMAL_POINT_CHARACTER));
        }
      }

      return valid;
    }

    /**
     * Counts the number of occurences of the contained String value in the sourceValue String.
     * @param sourceValue the String value to determine the number of occurences of containedValue.
     * @param containedValue the String value being counted for occurences in the sourceValue String.
     * @return the number of occurences of containedValue in sourceValue.
     */
    private static int countOccurences(String sourceValue, final String containedValue) {

      int count = 0;

      if (StringUtils.contains(sourceValue, containedValue)) {
        int index = -1;
        while ((index = sourceValue.indexOf(containedValue)) != -1) {
          count++;
          sourceValue = sourceValue.substring(index + 1);
        }
      }

      return count;
    }

    /**
     * Handles the deletion of content in the currency field.
     */
    public void remove(int offset, int length) throws BadLocationException {

      if (offset == 0) {
        TOOLKIT.beep();
        return;
      }
      super.remove(offset, length);
    }
  }

  /**
   * The CurrencyFieldKeyListener is used by the JCurrencyField component to track key events that edit and/or
   * traverse the currency field component.
   */
  private final class CurrencyFieldKeyListener extends KeyAdapter {

    public void keyPressed(final KeyEvent e) {
      switch (e.getKeyCode()) {
        case KeyEvent.VK_HOME:
          getCaret().setDot(1); // do not allow the cursor before the dollar sign
          e.consume(); // consumer the key event since the cursor position was set manually
          break;
        case KeyEvent.VK_LEFT:
          if (getCaret().getDot() == 1) {
            e.consume(); // cannot move before the dollar sign
          }
          break;
      }
    }
  }
}
