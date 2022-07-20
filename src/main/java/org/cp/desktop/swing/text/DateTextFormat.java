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

public class DateTextFormat implements TextFormat {

  private static final char FORWARD_SLASH_CHAR = '/';

  private static final int FIRST_FORWARD_SLASH_POSITION = 2;
  private static final int SECOND_FORWARD_SLASH_POSITION = 5;

  private static final String DATE_FORMAT = "MM/dd/yyyy";

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
  public final String format(Document doc, int offset, String text) throws InvalidTextFormatException {

    try {
      String currentDate = doc.getText(0, doc.getLength());

      text = mutate(offset, text);

      String composedDate = Strings.insert(currentDate, text, offset);

      if (!isValidDateFormat(composedDate)) {
        throw new InvalidTextFormatException("(" + composedDate + ") is not a valid date!");
      }

      return text;
    }
    catch (BadLocationException cause) {
      throw new InvalidTextFormatException("Failed to insert text (" + text + ") at offset (" + offset + ")!", cause);
    }
  }

  /**
   * Determines whether the specified String value is a valid date format.
   * @param dateValue the String value representing the current date value.
   * @return a boolean value indicating if the current String dateValue is a valid date format.
   */
  protected boolean isValidDateFormat(String dateValue) {

    if (dateValue.length() > DATE_FORMAT.length()) {
      return false;
    }

    boolean valid = true; // innocent until proven guilty

    for (int index = 0, len = dateValue.length(); index < len && valid; index++) {
      char c = dateValue.charAt(index);
      switch (index) {
        case FIRST_FORWARD_SLASH_POSITION:
        case SECOND_FORWARD_SLASH_POSITION:
          valid &= (c == FORWARD_SLASH_CHAR);
          break;
        default:
          valid &= Character.isDigit(c);
      }
    }

    return valid;
  }

  /**
   * Mutates the inserted value to conform to the date format as defined by this TextFormat.
   * @param offset the offset into the document at which the value will be inserted, thus dictating
   * the type of format that is imposed on the value.
   * @param value the value being inserted into the document of the text component.
   * @return the modified value with date formatting imposed.
   */
  private String mutate(int offset, String value) {

    // first, get the digits from the value
    String valueDigits = Strings.getDigits(value);

    if (Strings.hasText(valueDigits)) {

      StringBuffer buffer = new StringBuffer();

      for (int position = offset, index = 0, len = valueDigits.length(); index < len; position++) {
        switch (position) {
          case FIRST_FORWARD_SLASH_POSITION:
          case SECOND_FORWARD_SLASH_POSITION:
            buffer.append(FORWARD_SLASH_CHAR);
            break;
          default:
            buffer.append(valueDigits.charAt(index++));
        }
      }

      return buffer.toString();
    }

    return value;
  }

  /**
   * Returns a String description of this class.
   * @return a String value describing this class.
   */
  public String toString() {
    return getClass().getName() + "(" + DATE_FORMAT + ")";
  }
}
