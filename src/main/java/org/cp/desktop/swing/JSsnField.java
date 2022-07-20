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
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.cp.desktop.util.Strings;
import org.cp.elements.lang.StringUtils;

public final class JSsnField extends JTextField {

  private static final int END_POSITION = 11;
  private static final int LEFT_DASH_POSITION = 6;
  private static final int RIGHT_DASH_POSITION = 3;

  private static final Color SELECTION_COLOR = new Color(10, 36, 106);

  private static final Font DEFAULT_FONT = new Font("Monospaced", Font.PLAIN, 12);

  private static final String DEFAULT_SSN = "   -  -    ";

  private static final Toolkit TOOLKIT = Toolkit.getDefaultToolkit();

  private boolean typeOver;

  /**
   * Creates an instance of the JSsnField component class to represent social security numbers in a text field.
   */
  public JSsnField() {

    super(12);

    addKeyListener(new SsnFieldKeyListener());
    setFont(DEFAULT_FONT);
    setSelectedTextColor(Color.white);
    setSelectionColor(SELECTION_COLOR);
    setText(DEFAULT_SSN);
  }

  /**
   * Creates the default implementation of the model to be used at construction if one isn't explicitly given.
   * Returns an instance of SsnDocument.
   * @return a javax.swing.text.Document model implementation for the ssn field, a basic text field component
   * enforcing an ssn format using the SsnDocument model.
   */
  protected Document createDefaultModel() {
    return new SsnDocument();
  }

  /**
   * Determines whether the user pressed the insert key and activated text over write.
   * @return a boolean value indicating if text over write has been activated.
   */
  public boolean isTypeOver() {
    return typeOver;
  }

  /**
   * Sets whether the user has pressed the insert key activating the text over write.
   * @param typeOver a boolean value indicating if the user activated text over write by pressing the insert key.
   */
  public void setTypeOver(final boolean typeOver) {
    this.typeOver = typeOver;
  }

  /**
   * Sets the current caret position for type overs.
   * @param caret is an integer index representing the offset into the document for the ssn field.
   */
  private void setCaret(int caret) {

    if (isTypeOver()) {
      switch (caret) {
        case LEFT_DASH_POSITION:
        case RIGHT_DASH_POSITION:
        case END_POSITION:
          setSelectionStart(caret - 1);
          setSelectionEnd(caret);
          break;
        default:
          setSelectionStart(caret);
          setSelectionEnd(caret + 1);
      }
    }
    else {
      switch (caret) {
        case LEFT_DASH_POSITION:
        case RIGHT_DASH_POSITION:
          caret++;
          break;
      }
      getCaret().setDot(caret);
    }
  }

  /**
   * Sets the caret position for type overs to the left of the current caret position.
   * @param caret is an integer index representing the offset into the document for this ssn field.
   */
  private void setCaretLeft(int caret) {

    if (isTypeOver()) {
      if (caret == 5 || caret == 8) {
        setSelectionStart(caret - 3);
        setSelectionEnd(caret - 2);
      }
      else {
        setSelectionStart(caret - 2);
        setSelectionEnd(caret - 1);
      }
    }
    else {
      caret--;
      switch (caret) {
        case LEFT_DASH_POSITION:
        case RIGHT_DASH_POSITION:
          caret--;
          break;
      }
      getCaret().setDot(caret);
    }
  }

  /**
   * Sets the caret position for type overs to the right of the current caret position.
   * @param caret is an integer index representing the offset into the document for this ssn field.
   */
  private void setCaretRight(int caret) {

    if (isTypeOver()) {
      if ((caret == LEFT_DASH_POSITION) || (caret == RIGHT_DASH_POSITION)) {
        setSelectionStart(caret + 1);
        setSelectionEnd(caret + 2);
      }
      else if (caret == END_POSITION) {
        setSelectionStart(END_POSITION - 1);
        setSelectionEnd(END_POSITION);
      }
      else {
        setSelectionStart(caret);
        setSelectionEnd(caret + 1);
      }
    }
    else {
      caret++;
      switch (caret) {
        case LEFT_DASH_POSITION:
        case RIGHT_DASH_POSITION:
          caret++;
          break;
      }
      getCaret().setDot(caret);
    }
  }

  /**
   * The SsnDocument class is the default document used by the JSsnField component to model social security numbers
   * in a standard JTextField component.
   */
  private final class SsnDocument extends PlainDocument {

    private static final char DASH_CHARACTER = '-';
    private static final int MAX_NUMBER_OF_SSN_DIGITS = 9;

    private boolean replacing = false;

    private String replaceText = null;

    /**
     * The replaceText property keeps track of the text that was removed for replacement on a replace operation.
     * This text will be needed if the inserted text is an invalid ssn format.
     * @return a String value representing the replaced text.
     */
    private String getReplaceText() {
      return replaceText;
    }

    /**
     * Sets the replaced text upon removal for a replace operation.
     * @param replaceText the text that will be removed and replaced during the replace operation.
     */
    private void setReplaceText(final String replaceText) {
      this.replaceText = replaceText;
    }

    /**
     * Determines if a replace operation is occurring.
     * @return a boolean value indicating if the replace operation is occurring.
     */
    private boolean isReplacing() {
      return replacing;
    }

    /**
     * Sets the replacing property during a replace operation.
     * @param replacing a boolean value indicating if a replace operation has occurred.
     */
    private void setReplacing(final boolean replacing) {
      this.replacing = replacing;
    }

    /**
     * Formats the specified String value starting at offset according to the ssn format imposed by this JSsnField
     * component class.
     * @param offset the offset into the document of this JSsnField component class.
     * @param value the value to format.
     * @return the formatted value.
     */
    private String format(final int offset, final String value) {

      String valueDigits = StringUtils.getDigits(value);

      if (StringUtils.hasText(valueDigits)) {

        StringBuffer buffer = new StringBuffer();

        for (int position = offset, index = 0, len = valueDigits.length();  index < len; position++) {
          switch (position) {
            case LEFT_DASH_POSITION:
            case RIGHT_DASH_POSITION:
              buffer.append(DASH_CHARACTER);
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
     * Inserts the value into this document starting at offset formatted according to the AttributeSet.
     * @param offset the offset with the document of this JSsnField component class to insert the specified value.
     * @param value the value being inserted into this document.
     * @param attrSet the AttributeSet used to markup the value.
     * @throws BadLocationException if the insert position for the value within this document is not valid.
     */
    public void insertString(int offset, String value, AttributeSet attrSet) throws BadLocationException {

      try {
        if (!isValidSsn(offset, value)) {
          if (getReplaceText() != null) {
            super.insertString(offset, getReplaceText(), attrSet);
          }
          TOOLKIT.beep();
        }
        else {
          if (!isTypeOver()) {
            value = shiftRight(offset, value);
          }
          super.insertString(offset, value, attrSet);
          setCaret(offset + value.length());
        }
      }
      finally {
        setReplaceText(null);
        setReplacing(false);
      }
    }

    /**
     * Verifies that the user inputed value constitute a valid social security number.
     * @param offset is an integer value indicating the offset into document object for the social security number
     * validating that the content (value) conforms to the rules and format of a valid ssn.
     * @param value the String content containing a full/partial social security number.
     */
    public boolean isValidSsn(int offset, String value) throws BadLocationException {

      // if the value is equal to the default SSN, then the value is valid
      if (DEFAULT_SSN.equals(value)) {
        return true;
      }

      // verify the the number of digits in the current ssn and value do not exceed the maximum number of digits
      // in a valid ssn
      int numSsnDigits = StringUtils.getDigits(this.getText(0, getLength())).length();
      int numValueDigits = StringUtils.getDigits(value).length();

      if (numValueDigits > Strings.countWhitespace(DEFAULT_SSN.substring(offset))
        || ((numSsnDigits + numValueDigits) > MAX_NUMBER_OF_SSN_DIGITS)) {
        return false;
      }

      // if the value contains only digits and does not violate length, then the value is valid and the format
      // method will adjust the value according to the phone number format
      if (StringUtils.isDigits(value)) {
        return true;
      }

      boolean valid = true; // innocent until proven guilty

      for (int index = 0, len = value.length(); index < len && valid; index++) {
        switch (offset + index) {
          case LEFT_DASH_POSITION:
          case RIGHT_DASH_POSITION:
            valid &= (value.charAt(index) == DASH_CHARACTER);
            break;
          default:
            valid &= Character.isDigit(value.charAt(index));
        }
      }

      return valid;
    }

    /**
     * Removes a portion of the content (ssn) from this document.  If a replace operation is not occuring,
     * this will cause the digits after offset to be shifted left, retaining the format of the ssn.
     * @param offset the offset from the begining >= 0
     * @param length the number of characters to remove >= 0
     */
    public void remove(int offset, int length) throws BadLocationException {

      // do not remove the ssn formatting
      int adjustedOffset = offset;

      switch (offset) {
        case LEFT_DASH_POSITION:
        case RIGHT_DASH_POSITION:
          adjustedOffset--;
          break;
      }

      // if a replace operation is occurring, then record the removed text
      if (isReplacing()) {
        setReplaceText(this.getText(offset, length));
      }

      super.remove(adjustedOffset, length);

      // adjust the caret position
      if (!isReplacing()) {
        shiftLeft(adjustedOffset);
        setCaret(adjustedOffset);
      }
    }

    /**
     * Replaces the text between offset and offset + length with the specified text.
     * @param offset an integer value specifying the begin index into this document to replace text.
     * @param length an integer value specifying the amount of text to replace.
     * @param text the text replacement.
     * @param attrs the cosmetic attributes for the text.
     * @throws BadLocationException
     */
    public void replace(int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
      setReplacing(true);
      super.replace(offset, length, text, attrs);
    }

    /**
     * Called by the remove operation to shift all digit characters to the left introducing a space characters
     * at the end of the document as needed according to the ssn format.
     * @param offset is an integer value indicating an offset into the document of this JSsnField component class
     * to left shift digits of the ssn.
     * @throws BadLocationException if the given delete position is not a valid position
     * within the document.
     */
    private void shiftLeft(int offset) throws BadLocationException {

      String currentSsn = this.getText(0, getLength());

      int beginIndex = Math.min(currentSsn.length(), offset);

      String shiftedDigits = StringUtils.getDigits(currentSsn.substring(beginIndex));

      shiftedDigits = format(offset, shiftedDigits);

      StringBuffer buffer = new StringBuffer();

      buffer.append(shiftedDigits);
      buffer.append(DEFAULT_SSN.substring(offset + shiftedDigits.length()));

      super.remove(offset, getLength() - offset);
      super.insertString(offset, buffer.toString(), null);
    }

    /**
     * Shifts all digit characters to the right accounting for the ssn format by formatting the value and the
     * remaining portion of the current ssn as needed.
     * @param offset is an integer value indicating an offset into the document of this JSsnField component to
     * right shift digits of the current ssn.
     * @throws BadLocationException if the given insert position is not a valid position
     * within the document.
     */
    private String shiftRight(int offset, String value) throws BadLocationException {

      value = format(offset, value);

      String currentSsn = this.getText(0, getLength());

      String digitsAfterOffset = StringUtils.getDigits(currentSsn.substring(offset));

      int endIndex = Math.min(digitsAfterOffset.length(), (DEFAULT_SSN.length() - offset - value.length()));

      String shiftedDigits = digitsAfterOffset.substring(0, endIndex);

      shiftedDigits = format(offset + value.length(), shiftedDigits);

      StringBuffer buffer = new StringBuffer();

      buffer.append(shiftedDigits);
      buffer.append(DEFAULT_SSN.substring(offset + value.length() + shiftedDigits.length()));

      super.remove(offset, getLength() - offset);
      super.insertString(offset, buffer.toString(), null);

      return value;
    }
  }

  /**
   * The SsnFieldKeyListener is used by the JSsnField component class to track key events targeted at editing
   * and traversing the social security number.
   */
  public final class SsnFieldKeyListener extends KeyAdapter {

    public void keyPressed(KeyEvent event) {
      int dot = getCaret().getDot();
      switch (event.getKeyCode()) {
        case KeyEvent.VK_INSERT:
          setTypeOver(!isTypeOver());
          if (isTypeOver()) {
            setCaret(dot);
          }
          else {
            setCaret(dot - 1);
          }
          event.consume();
          break;
        case KeyEvent.VK_LEFT:
          setCaretLeft(dot);
          event.consume();
          break;
        case KeyEvent.VK_RIGHT:
          setCaretRight(dot);
          event.consume();
          break;
        default:
      }
    }
  }
}
