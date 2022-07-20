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
package org.cp.desktop.awt.event;

import static org.cp.elements.lang.LangExtensions.assertThat;

import java.awt.TextComponent;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

@SuppressWarnings("unused")
public class MaxLengthTextFieldListener extends KeyAdapter {

  private int maxLength;

  /**
   * Creates an instance of the MaxLengthTextFieldListener class to monitor
   * text components for text entered and bound the number of characters
   * entered to maxLength.
   *
   * @param maxLength is an integer value specifying the maximum number
   * characters allowed to be entered in the text component.
   */
  public MaxLengthTextFieldListener(int maxLength) {
    setMaxLength(maxLength);
  }

  /**
   * Determines whether the number of characters in the text component is
   * greater than or equal to the maximum length of input characters allowed
   * to be entered into the text component by this listener.
   * @param length the length being compared to maxLength.
   * @return a boolean value indicating whether length is greater than or
   * equal to maxLength.
   */
  protected boolean isGreaterThanEqualToMaxLength(int length) {
    return (length >= getMaxLength());
  }

  /**
   * Sets the maximum number of characters allowed to be entered in the
   * text component.
   * @param maxLength an integer value specifying the maximum number of
   * characters allowed to be entered into the text component.
   */
  public void setMaxLength(int maxLength) {
    this.maxLength = validateMaxLength(maxLength);
  }

  /**
   * Returns the maximum number of characters allowed to be entered in the
   * text component.
   * @return an integer value specifying the maximum number of characters
   * allowed to be entered in the text component.
   */
  public int getMaxLength() {
    return this.maxLength;
  }

  /**
   * Handles events associated with pressing keys in a TextComponent. Specifically,
   * the keyEvent method handles key pressed events in the text component constrained
   * to the number of characters entered from surpassing the maximum length defined
   * by this listener, which was recorded by this class when it was initialized.
   *
   * @param event is a java.awt.event.KeyEvent encapsulating the keyboard event.
   * TODO: work out the details of the user pressed keys!
   */
  public void keyPressed(KeyEvent event) {

    TextComponent textComponent = (TextComponent) event.getSource();

    if (!KeyEventUtils.isBackspaceOrDeleteKey(event.getKeyCode())
        && isGreaterThanEqualToMaxLength(textComponent.getText().length())) {
      event.consume();
      Toolkit.getDefaultToolkit().beep();
    }
  }

  /**
   * Validates that the maxLenght parameter is a positive value.
   * @param maxLength the maximum number of characters allowed to be entered
   * into the text component monitored by this Listener.
   * @return a the maxLength value if valid otherwise this method throws an
   * IllegalArgumentException
   * @throws IllegalArgumentException if the maxLength value is invalid.
   */
  private int validateMaxLength(int maxLength) {

    assertThat(maxLength)
      .describedAs("[%d] is not a valid maximum length constraint", maxLength)
      .isGreaterThanEqualTo(0);

    return maxLength;
  }
}
