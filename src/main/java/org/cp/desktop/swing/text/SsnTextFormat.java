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

public class SsnTextFormat implements TextFormat {

  private static final char DASH_CHAR = '-';

  private static final int FIRST_DASH_POSITION = 3;
  private static final int SECOND_DASH_POSITION = 6;

  private static final String SSN_FORMAT = "###-##-####";

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
      String currentSsn = doc.getText(0, doc.getLength());

      text = mutate(offset, text);

      String composedSsn = Strings.insert(currentSsn, text, offset);

      if (!isValidSsnFormat(composedSsn)) {
        throw new InvalidTextFormatException("(" + composedSsn + ") is not a valid social security number!");
      }

      return text;
    }
    catch (BadLocationException cause) {
      throw new InvalidTextFormatException("Failed to insert text (" + text + ") at offset (" + offset + ")!", cause);
    }
  }

  /**
   * Determines whether the specified String value is a valid SSN format.
   * @param ssnValue the String value representing the current SSN value.
   * @return a boolean value indicating if the current String ssnValue is a valid SSN format.
   */
  protected boolean isValidSsnFormat(String ssnValue) {

    if (ssnValue.length() > SSN_FORMAT.length()) {
      return false;
    }

    boolean valid = true; // innocent until proven guilty

    for (int index = 0, len = ssnValue.length(); index < len && valid; index++) {
      char c = ssnValue.charAt(index);
      switch (index) {
        case FIRST_DASH_POSITION:
        case SECOND_DASH_POSITION:
          valid &= (c == DASH_CHAR);
          break;
        default:
          valid &= Character.isDigit(c);
      }
    }

    return valid;
  }

  /**
   * Mutates the inserted value to conform to the SSN format as defined by this TextFormat.
   * @param offset the offset into the document at which the value will be inserted, thus dictating
   * the type of format that is imposed on the value.
   * @param value the value being inserted into the document of the text component.
   * @return the modified value with SSN formatting imposed.
   */
  private String mutate(int offset, String value) {

    String valueDigits = StringUtils.getDigits(value);

    if (StringUtils.hasText(valueDigits)) {

      StringBuffer buffer = new StringBuffer();

      for (int position = offset, index = 0, len = valueDigits.length(); index < len; position++) {
        switch (position) {
          case FIRST_DASH_POSITION:
          case SECOND_DASH_POSITION:
            buffer.append(DASH_CHAR);
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
  @Override
  public String toString() {
    return getClass().getName() + "(" + SSN_FORMAT + ")";
  }
}
