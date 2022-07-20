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
import java.awt.event.MouseEvent;

import javax.swing.JTextField;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.cp.desktop.util.Strings;
import org.cp.elements.lang.StringUtils;

public final class JPhoneNumberField extends JTextField {

  private static final int DASH_POSITION = 8;
  private static final int END_POSITION = 13;
  private static final int LEFT_PAREN_POSITION = 0;
  private static final int RIGHT_PAREN_POSITION = 4;

  private static final Color SELECTION_COLOR = new Color(10, 36, 106);

  private static final Font DEFAULT_FONT = new Font("Monospaced", Font.PLAIN, 12);

  private static final String DEFAULT_PHONE_NUMBER = "(   )   -    ";

  private static final Toolkit TOOLKIT = Toolkit.getDefaultToolkit();

  private boolean typeOver = false;

  /**
   * Creates an instance of the JPhoneNumberField component class to represent phone numbers in a text field.
   */
  public JPhoneNumberField() {

    super(14);

    addKeyListener(new PhoneNumberFieldKeyListener());
    addMouseInputListener(new PhoneNumberFieldMouseListener());
    setFont(DEFAULT_FONT);
    setSelectedTextColor(Color.white);
    setSelectionColor(SELECTION_COLOR);
    setText(DEFAULT_PHONE_NUMBER);
    getCaret().setDot(1);
  }

  /**
   * Adds a MouseInputListener to the JPhoneNumberField component class.
   * @param listener the MouseInputListener to listen for mouse input and mouse motion events on this
   * JPhoneNumberField component class.
   */
  private void addMouseInputListener(MouseInputListener listener) {
    addMouseListener(listener);
    addMouseMotionListener(listener);
  }

  /**
   * Creates the default implementation of the model to be used at construction if one isn't explicitly given.
   * Returns an instance of PhoneNumberDocument.
   * @return a javax.swing.text.Document model implementation for this phone number field, a basic text field component,
   * using the PhoneNumberDocument model.
   */
  protected Document createDefaultModel() {
    return new PhoneNumberDocument();
  }

  /**
   * Sets the current caret position for type overs.
   * @param caret is an integer index representing the offset into the document for the phone number field.
   */
  private void setCaret(int caret) {

    if (isTypeOver()) {
      switch (caret) {
        case RIGHT_PAREN_POSITION:
        case DASH_POSITION:
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
        case LEFT_PAREN_POSITION:
        case RIGHT_PAREN_POSITION:
        case DASH_POSITION:
          caret++;
          break;
      }
      getCaret().setDot(caret);
    }
  }

  /**
   * Sets the caret position for type overs to the left of the current caret position.
   * @param caret is an integer index representing the offset into the document for this phone number field.
   */
  private void setCaretLeft(int caret) {

    if (isTypeOver()) {
      if (caret <= 2) {
        setSelectionStart(1);
        setSelectionEnd(2);
      }
      else if (caret == 6 || caret == 10) {
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
        case RIGHT_PAREN_POSITION:
        case DASH_POSITION:
          caret--;
          break;
        case LEFT_PAREN_POSITION:
          caret = 1;
          break;
      }
      getCaret().setDot(caret);
    }
  }

  /**
   * Sets the caret position for type overs to the right of the current caret position.
   * @param caret is an integer index representing the offset into the document for this phone number field.
   */
  private void setCaretRight(int caret) {

    if (isTypeOver()) {
      if ((caret == RIGHT_PAREN_POSITION) || (caret == DASH_POSITION)) {
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
        case RIGHT_PAREN_POSITION:
        case DASH_POSITION:
          caret++;
          break;
      }
      getCaret().setDot(caret);
    }
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
  public void setTypeOver(boolean typeOver) {
    this.typeOver = typeOver;
  }

  /**
   * The PhoneNumberDocument class is the default document used by the JPhoneNumberField component class to
   * represent phone numbers in a standard JTextField component.
   */
  private final class PhoneNumberDocument extends PlainDocument {

    private static final char DASH_CHAR = '-';
    private static final char LEFT_PAREN_CHAR = '(';
    private static final char RIGHT_PAREN_CHAR = ')';

    private static final int MAX_PHONE_NUMBER_DIGITS = 10;

    private boolean replacing = false;

    private String replaceText = null;

    /**
     * The replaceText property keeps track of the text that was removed for replacement on a replace operation.
     * This text will be needed if the inserted text is an invalid phone number format.
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
     * Formats the specified String value starting at offset according to the phone number format imposed
     * by this JPhoneNumberField component class.
     * @param offset the offset into the document of this JPhoneNumberField component class.
     * @param value the value to format.
     * @return the formatted value.
     */
    private String format(int offset, String value) {

      String valueDigits = StringUtils.getDigits(value);

      if (StringUtils.hasText(valueDigits)) {
        StringBuffer buffer = new StringBuffer();
        for (int position = offset, index = 0, len = valueDigits.length();  index < len; position++) {
          if (position == RIGHT_PAREN_POSITION) {
            buffer.append(RIGHT_PAREN_CHAR);
          }
          else if (position == DASH_POSITION) {
            buffer.append(DASH_CHAR);
          }
          else {
            buffer.append(valueDigits.charAt(index++));
          }
        }
        return buffer.toString();
      }

      return value;
    }

    /**
     * Inserts the value into this document starting at offset formatted according to the AttributeSet.
     * @param offset the offset with the document of this JPhoneNumberField component class to insert the
     * specified value.
     * @param value the value being inserted into this document.
     * @param attrSet the AttributeSet used to markup the value.
     * @throws BadLocationException if the insert position for the value within this document is not valid.
     */
    public void insertString(final int offset, String value, final AttributeSet attrSet)
        throws BadLocationException {
      try {

        // preformatting: remove the left paren if the value contains one
        if (!DEFAULT_PHONE_NUMBER.equals(value) && value.startsWith(String.valueOf(LEFT_PAREN_CHAR))) {
          value = value.substring(1);
        }

        if (!isValidPhoneNumber(offset, value)) {
          // NOTE: we could be checking the value of the replacing property (isReplacing), but I figure it is better
          // that we check that we actually had a value that was replaced
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
     * Verifies the user inputed value constitutes a valid phone number according to the specified format.
     * @param offset is an integer value indicating the offset into document for this phone number field
     * validating that the content (value) conforms to the format and rules enforced by this method for
     * phone numbers.
     * @param value String content containing full/partial phone number information to validate.
     */
    public boolean isValidPhoneNumber(final int offset, final String value) throws BadLocationException {

      // if the value is equal to the default phone number, the value is valid
      if (DEFAULT_PHONE_NUMBER.equals(value)) {
        return true;
      }

      // verify the the number of digits in the current phone number and value do not exceed the maximum
      // number of digits in a valid phone number
      int numPhoneNumberDigits = StringUtils.getDigits(this.getText(0, getLength())).length();
      int numValueDigits = StringUtils.getDigits(value).length();

      if ((numValueDigits > Strings.countWhitespace(DEFAULT_PHONE_NUMBER.substring(offset)))
        || ((numPhoneNumberDigits + numValueDigits) > MAX_PHONE_NUMBER_DIGITS)) {
        return false;
      }

      // if the value contains only digits and does not violate length, then the value is valid and the format
      // method will adjust the value according to the phone number format
      if (StringUtils.isDigits(value)) {
        return true;
      }

      // the value is formatted, ensure the value has the correct phone number format
      boolean valid = true; // innocent until proven guilty

      for (int index = 0, len = value.length(); index < len && valid; index++) {
        char c = value.charAt(index);
        switch (offset + index) {
          case LEFT_PAREN_POSITION:
            valid &= (c == LEFT_PAREN_CHAR);
            break;
          case RIGHT_PAREN_POSITION:
            valid &= (c == RIGHT_PAREN_CHAR) || (c == DASH_CHAR);
            break;
          case DASH_POSITION:
            valid &= (c == DASH_CHAR);
            break;
          default:
            valid &= Character.isDigit(c);
        }
      }

      return valid;
    }

    /**
     * Removes a portion of the content (phone number) from this document.  If a replace operation is not occuring,
     * this will cause the digits after offset to be shifted left, retaining the format of the phone number.
     * @param offset the offset from the begining >= 0
     * @param length the number of characters to remove >= 0
     */
    public void remove(int offset, final int length) throws BadLocationException {

      // do not remove the phone number formatting
      int adjustedOffset = offset;

      switch (offset) {
        case LEFT_PAREN_POSITION:
          return;
        case RIGHT_PAREN_POSITION:
        case DASH_POSITION:
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
    public void replace(final int offset, final int length, final String text, final AttributeSet attrs)
        throws BadLocationException {
      setReplacing(true);
      super.replace(offset, length, text, attrs);
    }

    /**
     * Called by the remove operation to shift all digit characters to the left introducing a space characters
     * at the end of the document as needed according to the phone number format.
     * @param offset is an integer value indicating an offset into the document of this JPhoneNumberField
     * component class to left shift digits of the phone number.
     * @throws BadLocationException if the given delete position is not a valid position
     * within the document.
     */
    private void shiftLeft(final int offset) throws BadLocationException {

      String currentPhoneNumber = this.getText(0, getLength());

      int beginIndex = Math.min(currentPhoneNumber.length(), offset);

      String shiftedDigits = StringUtils.getDigits(currentPhoneNumber.substring(beginIndex));

      shiftedDigits = format(offset, shiftedDigits);

      StringBuffer buffer = new StringBuffer();

      buffer.append(shiftedDigits);
      buffer.append(DEFAULT_PHONE_NUMBER.substring(offset + shiftedDigits.length()));

      super.remove(offset, getLength() - offset);
      super.insertString(offset, buffer.toString(), null);
    }

    /**
     * Shifts all digit characters to the right accounting for the phone number format by formatting the value
     * and the remaining portion of the current phone number as needed.
     * @param offset is an integer value indicating an offset into the document of this JPhoneNumberField component
     * to right shift digits of the current phone number.
     * @throws BadLocationException if the given insert position is not a valid position
     * within the document.
     */
    private String shiftRight(final int offset, String value) throws BadLocationException {

      value = format(offset, value);

      String currentPhoneNumber = this.getText(0, getLength());

      String digitsAfterOffset = StringUtils.getDigits(currentPhoneNumber.substring(offset));

      int endIndex = Math.min(digitsAfterOffset.length(), (DEFAULT_PHONE_NUMBER.length() - offset - value.length()));

      String shiftedDigits = digitsAfterOffset.substring(0, endIndex);

      shiftedDigits = format(offset + value.length(), shiftedDigits);

      StringBuffer buffer = new StringBuffer();

      buffer.append(shiftedDigits);
      buffer.append(DEFAULT_PHONE_NUMBER.substring(offset + value.length() + shiftedDigits.length()));

      super.remove(offset, getLength() - offset);
      super.insertString(offset, buffer.toString(), null);

      return value;
    }
  }

  /**
   * The PhoneNumberFieldKeyListener class is used by the JPhoneNumberField component to track key events
   * targeted at editing and traversing the phone number field.
   */
  private final class PhoneNumberFieldKeyListener extends KeyAdapter {

    public void keyPressed(KeyEvent e) {
      final int dot = getCaret().getDot();
      switch (e.getKeyCode()) {
        case KeyEvent.VK_A:
          if (e.isControlDown()) {
            setSelectionStart(1);
            setSelectionEnd(END_POSITION);
            e.consume();
          }
          break;
        case KeyEvent.VK_END:
          if (!e.isShiftDown()) {
            setCaret(END_POSITION);
            e.consume();
          }
          else {
            if (isTypeOver()) {
              e.consume();
            }
          }
          break;
        case KeyEvent.VK_HOME:
          if (!e.isShiftDown()) {
            setCaret(1);
          }
          else {
            if (!isTypeOver()) {
              setSelectionStart(1);
              setSelectionEnd(dot);
            }
          }
          e.consume();
          break;
        case KeyEvent.VK_INSERT:
          setTypeOver(!isTypeOver());
          if (isTypeOver()) {
            setCaret(dot);
          }
          else {
            setCaret(dot - 1);
          }
          e.consume();
          break;
        case KeyEvent.VK_LEFT:
          setCaretLeft(dot);
          e.consume();
          break;
        case KeyEvent.VK_RIGHT:
          setCaretRight(dot);
          e.consume();
          break;
        default:
      }
    }
  }

  /**
   * The PhoneNumberFieldMouseListener is used to handle mouse events generated by the user working with the mouse
   * in the phone number field.
   */
  private final class PhoneNumberFieldMouseListener extends MouseInputAdapter {

    private void handleEvent(MouseEvent event) {

      if (getSelectionStart() != getSelectionEnd()) {
        if (getSelectionStart() == 0) {
          setSelectionStart(1);
        }
      }
      else {
        setCaret(getCaret().getDot());
      }
    }

    public void mouseClicked(MouseEvent event) {

      if (event.getClickCount() > 1) {
        handleEvent(event);
      }
    }

    public void mouseReleased(MouseEvent event) {
      handleEvent(event);
    }
  }
}
