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

import javax.swing.text.Document;

public final class BoundedLengthTextFormat implements TextFormat {

  private final int maxLength;

  /**
   * Creates an instance of the BoundedLengthTextFormat class to restrict the length of text entered in the
   * JFormattedField component to the specified maximum length.
   * @param maxLength is a integer value specifying the maximum number of characters (length of text) that can be
   * inserted into the JFormattedField component.
   */
  public BoundedLengthTextFormat(int maxLength) {
    this.maxLength = maxLength;
  }

  /**
   * Returns the maximum number characters allowed by this TextFormat.
   * @return a integer value specifying the maximum number characters allowed by this TextFormat.
   */
  public int getMaxLength() {
    return maxLength;
  }

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

    if ((doc.getLength() + text.length()) > getMaxLength()) {
      throw new InvalidTextFormatException("The length of the text to be inserted plus the current text length exceeds the maximum length of "
        + getMaxLength());
    }

    return text;
  }

  /**
   * Returns a String description of this class.
   * @return a String value describing this class.
   */
  public String toString() {
    return "BoundedLengthTextFormat (" + getMaxLength() + " characters)";
  }
}
