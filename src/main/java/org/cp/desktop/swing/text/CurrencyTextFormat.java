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
package org.cp.desktop.swing.text;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.cp.desktop.util.Strings;

public class CurrencyTextFormat implements TextFormat {

  private static final char DECIMAL_POINT_CHAR = '.';
  private static final char DOLLAR_SIGN_CHAR = '$';
  private static final char MINUS_SIGN_CHAR = '-';

  private static final int DOLLAR_SIGN_POSITION = 0;
  private static final int MINUS_SIGN_DECIMAL_POSITION = 1;

  private static final String CURRENCY_FORMAT = "$0.00";

  /**
   * Verifies that the text that will be inserted into the JTextField component has a valid format and performs
   * any other mutations on the text as determined by the validation and formatting rules specified by instances
   * of this class.
   * @param doc the Document object representing the model used by the JTextField component to format and verify
   * input.
   * @param offset is an integer value specifying the offset into the Document.
   * @param text a String object containing the text to format and validate before inserting into the
   * JTextField component.
   * @throws InvalidTextFormatException if the text format is not valid input to the
   * text field component.
   */
  public String format(Document doc, int offset, String text) throws InvalidTextFormatException {

    try {
      String currentCurrency = doc.getText(0, doc.getLength());

      text = mutate(offset, text);

      String composedCurrency = Strings.insert(currentCurrency, text, offset);

      if (!isValidCurrency(composedCurrency)) {
        throw new InvalidTextFormatException("(" + composedCurrency + ") is not a valid currency!");
      }

      return text;
    }
    catch (BadLocationException cause) {
      throw new InvalidTextFormatException("Failed to insert text (" + text + ") at offset (" + offset + ")!", cause);
    }
  }

  /**
   * Determines whether the specified String value is a valid currency format.
   * @param currency the String value representing the current currency value.
   * @return a boolean value indicating if the current String currency is a valid currency format.
   */
  private boolean isValidCurrency(String currency) {

    if (Strings.count(currency, DECIMAL_POINT_CHAR) > 1) {
      return false;
    }

    int decimalPointIndex = currency.indexOf(DECIMAL_POINT_CHAR);

    if ((decimalPointIndex > -1) && currency.substring(decimalPointIndex + 1).length() > 2) {
      return false;
    }

    boolean valid = true; // innocent until proven guilty

    for (int index = 0, len = currency.length(); index < len && valid; index++) {
      char c = currency.charAt(index);
      switch (index) {
        case DOLLAR_SIGN_POSITION:
          valid &= (c == DOLLAR_SIGN_CHAR);
          break;
        case MINUS_SIGN_DECIMAL_POSITION:
          valid &= (c == MINUS_SIGN_CHAR) || (c == DECIMAL_POINT_CHAR) || Character.isDigit(c);
          break;
        default:
          valid &= (c == DECIMAL_POINT_CHAR) || Character.isDigit(c);
      }
    }
    return valid;
  }

  /**
   * Mutates the inserted value to conform to the currency format as defined by this TextFormat.
   * @param offset the offset into the document at which the value will be inserted, thus dictating
   * the type of format that is imposed on the value.
   * @param value the value being inserted into the document of the text component.
   * @return the modified value with currency formatting imposed.
   */
  private String mutate(int offset, String value) {

    if (Strings.hasText(value)) {
      if ((offset == DOLLAR_SIGN_POSITION) && !value.startsWith(String.valueOf(DOLLAR_SIGN_CHAR))) {
        value = DOLLAR_SIGN_CHAR + value;
      }
    }

    return value;
  }

  /**
   * Returns a String description of this class.
   * @return a String value describing this class.
   */
  public String toString() {
    return getClass().getName() + "(" + CURRENCY_FORMAT + ")";
  }
}
