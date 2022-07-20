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

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;

public class XButton extends JButton {

  /**
   * Default constructor.  Creates a button with no set text or icon.
   */
  public XButton() {
    init();
  }

  /**
   * Creates a button where properties are taken from the Action supplied.
   * @param a is an Action object defining the behavior, or action, of this button when clicked.
   */
  public XButton(Action action) {
    super(action);
    init();
  }

  /**
   * Creates a button with an icon.
   * @param icon is an image to be displayed on the button.
   */
  public XButton(Icon icon) {
    super(icon);
    init();
  }

  /**
   * Creates a button with text.
   * @param text is a String value containing the text to be displayed on the button.
   */
  public XButton(String text) {
    super(text);
    init();
  }

  /**
   * Creates a button with initial text and an icon.
   * @param icon is an image to be displayed on the button.
   * @param text is a String value containing the text to be displayed on the button.
   */
  public XButton(String text, Icon icon) {
    super(text, icon);
    init();
  }

  /**
   * Initializes this button with event handlers and it's initial border.
   */
  private void init() {
    addFocusListener(new FocusHandler());
    addMouseListener(new MouseHandler());
    setBorder(BorderFactory.createRaisedBevelBorder());
    setBorderPainted(false);
  }

  /**
   * This method is called to enable or disable this button.
   * @param enable is a boolean value indicating true if the button should be enabled, false otherwise.
   */
  public void setEnabled(boolean enable) {
    super.setEnabled(enable);
    setBorderPainted(false);
  }

  private class FocusHandler extends FocusAdapter {

    public void focusLost(FocusEvent event) {
      if (isEnabled()) {
        setBorderPainted(false);
      }
    }
  }

  private class MouseHandler extends MouseAdapter {

    public void mouseEntered(MouseEvent event) {
      if (isEnabled()) {
        setBorderPainted(true);
      }
    }

    public void mouseExited(MouseEvent event) {
      if (isEnabled()) {
        setBorderPainted(false);
      }
    }

    public void mousePressed(MouseEvent event) {
      if (isEnabled()) {
        setBorder(BorderFactory.createLoweredBevelBorder());
      }
    }

    public void mouseReleased(MouseEvent event) {
      if (isEnabled()) {
        setBorder(BorderFactory.createRaisedBevelBorder());
      }
    }
  }

}
