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

import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.cp.desktop.swing.text.InvalidTextFormatException;
import org.cp.desktop.swing.text.TextFormat;

public class JFormattedTextField extends JTextField {

  private static final Font DEFAULT_FONT = new Font("Monospaced", Font.PLAIN, 12);

  private static final Toolkit TOOLKIT = Toolkit.getDefaultToolkit();

  private TextFormat textFormat;

  /**
   * Creates an instance of the JFormattedField component class with a default text format used to format
   * and verify input into this text field component.
   */
  public JFormattedTextField() {
    this(DefaultTextFormat.INSTANCE);
  }

  /**
   * Creates an instance of the JFormattedField component class initialized with the specified TextFormat object
   * used to format and validate text input to this text field component.
   * @param textFormat a TextFormat object specifying the format and validation scheme used when inserting text
   * into this formatted text field.
   */
  public JFormattedTextField(TextFormat textFormat) {
    setTextFormat(textFormat);
    setFont(DEFAULT_FONT);
  }

  /**
   * Creates the default implementation of the model to be used at construction if one isn't explicitly given.
   * Returns an instance of SchemaDocument.
   * @return a default Ljavax.swing.text.Document object used to format and verify the content for this
   * formatted text field component.
   */
  protected Document createDefaultModel() {
    return new SchemaDocument();
  }

  /**
   * Returns the TextFormat class used by this text compenent to format and valid input.
   * @return a TextFormat class representing the format enforced by this text component.
   */
  public TextFormat getTextFormat() {
    return textFormat;
  }

  /**
   * Sets the specified text format enforced by this text component to format and validate input.
   * @param textFormat the TextFormat object used to constrain input.
   */
  public void setTextFormat(TextFormat textFormat) {
    if (textFormat == null) {
      textFormat = DefaultTextFormat.INSTANCE;
    }
    setText(""); // clear the current text
    this.textFormat = textFormat;
  }

  /**
   * The DefaultTextFormat class requires no special format and performs no validation on the text input.  The class
   * is merely an adapter for the TextFormat interface.
   */
  private static class DefaultTextFormat implements TextFormat {

    private static final DefaultTextFormat INSTANCE = new DefaultTextFormat();

    public String format(Document doc, int offset, String text) throws InvalidTextFormatException {
      return text;
    }
  }

  /**
   * The SchemaDocument class is used by the JFormattedTextField component class to model text input via formatting
   * and validation.
   */
  public class SchemaDocument extends PlainDocument {

    private String replacedText = null;

    /**
     * Determines whether a replace operation is occurring.
     * @return a boolean value indicating if a replace operation is occurring.
     */
    private boolean isReplacing() {
      return getReplacedText() != null;
    }

    /**
     * The replaceText property keeps track of the text that was removed for replacement on a replace operation.
     * This text will be needed if the inserted text is an invalid ssn format.
     * @return a String value representing the replaced text.
     */
    private String getReplacedText() {
      return replacedText;
    }

    /**
     * Sets the replaced text upon removal for a replace operation.
     * @param replacedText the text that will be removed and replaced during the replace operation.
     */
    private void setReplacedText(String replacedText) {
      this.replacedText = replacedText;
    }

    public void insertString(int offset, String value, AttributeSet attrSet)
        throws BadLocationException {
      try {
        super.insertString(offset, textFormat.format(this, offset, value), attrSet);
      }
      catch (InvalidTextFormatException itf) {
        if (getReplacedText() == null) {
          super.insertString(offset, getReplacedText(), attrSet);
        }
        TOOLKIT.beep();
      }
    }

    public void replace(int offset, int length, String value, AttributeSet attrs) throws BadLocationException {
      setReplacedText(getText(offset, length));
      super.replace(offset, length, value, attrs);
    }
  }
}
