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
import org.cp.elements.lang.StringUtils;

public class PercentTextFormat implements TextFormat {

  private static final char DECIMAL_POINT_CHAR = '.';
  private static final char MINUS_SIGN_CHAR = '-';
  private static final char PERCENT_SIGN_CHAR = '%';

  private static final int MINUS_SIGN_DECIMAL_POSITION = 0;

  private static final String PERCENT_FORMAT = "100.0%";

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
      String currentPercent = doc.getText(0, doc.getLength());

      text = mutate(offset, doc.getLength(), text);

      String composedPercent = Strings.insert(currentPercent, text, offset);

      if (!isValidPercent(composedPercent)) {
        throw new InvalidTextFormatException("(" + composedPercent + ") is not a valid percent!");
      }
      return text;
    }
    catch (BadLocationException cause) {
      throw new InvalidTextFormatException("Failed to insert text (" + text + ") at offset (" + offset + ")!", cause);
    }
  }

  /**
   * Determines whether the specified String value is a valid percent format.
   * @param percent the String value representing the current percent value.
   * @return a boolean value indicating if the current String percent is a valid percent format.
   */
  private boolean isValidPercent(String percent) {

    if (Strings.count(percent, DECIMAL_POINT_CHAR) > 1) {
      return false;
    }

    int END_POSITION = percent.length() - 1;

    boolean valid = true; // innocent until proven guilty

    for (int index = 0, len = percent.length(); index < len && valid; index++) {
      char c = percent.charAt(index);
      switch (index) {
        case MINUS_SIGN_DECIMAL_POSITION:
          valid &= (c == MINUS_SIGN_CHAR) || (c == DECIMAL_POINT_CHAR) || Character.isDigit(c);
          break;
        default:
          if (index == END_POSITION) {
            valid &= (c == PERCENT_SIGN_CHAR);
          }
          else {
            valid &= (c == DECIMAL_POINT_CHAR) || Character.isDigit(c);
          }
      }
    }

    return valid;
  }

  /**
   * Mutates the inserted value to conform to the percent format as defined by this TextFormat.
   * @param offset the offset into the document at which the value will be inserted, thus dictating
   * the type of format that is imposed on the value.
   * @param endPosition an integer value indicating the end of the document.
   * @param value the value being inserted into the document of the text component.
   * @return the modified value with percent formatting imposed.
   */
  private String mutate(int offset, int endPosition, String value) {

    if (StringUtils.hasText(value)) {
      if ((offset == endPosition) && !value.endsWith(String.valueOf(PERCENT_SIGN_CHAR))) {
        value += PERCENT_SIGN_CHAR;
      }
    }

    return value;
  }

  /**
   * Returns a String description of this class.
   * @return a String value describing this class.
   */
  @Override
  public String toString() {
    return getClass().getName() + "(" + PERCENT_FORMAT + ")";
  }
}
