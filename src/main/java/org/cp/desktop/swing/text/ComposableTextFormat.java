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

import org.cp.elements.lang.Assert;

public final class ComposableTextFormat implements TextFormat {

  private final TextFormat textFormat0;
  private final TextFormat textFormat1;

  /**
   * Creates an instance of the TextFormatComposition class to chain TextFormat objects together in order to
   * perform independent mutations and validations of text input to JFormattedField component.
   * @param textFormat0 a TextFormat object to format and validate text input.
   * @param textFormat1 a TextFormat object to format and validate text input.
   */
  private ComposableTextFormat(TextFormat textFormat0, TextFormat textFormat1) {

    Assert.notNull(textFormat0, "The first text format argument cannot be null");
    Assert.notNull(textFormat1, "The second text format argument cannot be null");

    this.textFormat0 = textFormat0;
    this.textFormat1 = textFormat1;
  }

  /**
   * Composes two TextFormat objects into one interface.
   * @param textFormat0 a TextFormat object to format and validate text input.
   * @param textFormat1 a TextFormat object to format and validate text input.
   * @return a TextFormat composition of the two TextFormat parameters.
   */
  public static TextFormat compose(TextFormat textFormat0, TextFormat textFormat1) {

    return textFormat0 == null ? textFormat1
      : textFormat1 == null ? textFormat0
      : new ComposableTextFormat(textFormat0, textFormat1);
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
    return this.textFormat1.format(doc, offset, this.textFormat0.format(doc, offset, text));
  }
}
