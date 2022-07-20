/*
 * FontStyle.java (c) 7 August 2009
 *
 * Copyright (c) 2003, Code Primate
 * All Rights Reserved
 * @author jblum
 * @version 2009.8.7
 * @see java.awt.Font
 */
package org.cp.desktop.awt;

import java.awt.Font;

import org.cp.elements.lang.Assert;

@SuppressWarnings("unused")
public enum FontStyle {

  BOLD(Font.BOLD, "bold"),
  ITALIC(Font.ITALIC, "italic"),
  PLAIN(Font.PLAIN, "plain");

  private final int style;
  private final String description;

  /**
   * Constructs a new FontStyle instance initialized with a corresponding Font constant indicating the style of font
   * as well as a String describing the font style.
   * @param style an integer value constant defined in the AWT Font class corresponding to the font style.
   * @param description a String value describing the style of font.
   */
  FontStyle(int style, String description) {

    Assert.hasText(description, "The description for this font style cannot be null or empty");

    this.style = style;
    this.description = description;
  }

  /**
   * Gets a FontStyle enumerated type value based on a String value describing the style of font.
   * @param description a String value describing the style of font.
   * @return a FontStyle object with the specified description for the font style.
   */
  public static FontStyle getByDescription(String description) {

    for (FontStyle fontStyle : values()) {
      if (fontStyle.getDescription().equalsIgnoreCase(description)) {
        return fontStyle;
      }
    }

    return null;
  }

  /**
   * Gets a FontStyle enumerated type value based on the corresponding AWT Font class constant for the font style.
   * @param style an integer value constant defined in the AWT Font class corresponding to the font style.
   * @return a FontStyle object with the corresponding AWT Font class constant for the font style.
   */
  public static FontStyle getByStyle(int style) {

    for (FontStyle fontStyle : values()) {
      if (fontStyle.getStyle() == style) {
        return fontStyle;
      }
    }

    return null;
  }

  /**
   * Gets a String value describing the style of font.
   * @return a String value description of the AWT Font style.
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * Gets the AWT Font class constant for the style of font.
   * @return an integer value specifying the font style.
   */
  public int getStyle() {
    return this.style;
  }

  /**
   * Gets a String value describing the font style.
   * @return a String value describing the style of font.
   */
  @Override
  public String toString() {
    return getDescription();
  }
}
