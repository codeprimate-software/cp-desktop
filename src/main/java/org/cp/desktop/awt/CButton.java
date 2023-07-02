/*
 * CButton.java (c) 14 March 2001
 *
 * Copyright (c) 2001, Code Primate
 * All Rights Reserved
 * @author John J. Blum
 * @version 2009.5.27
 * @see com.cp.common.swing.XButton
 * @see java.awt.Button
 */
package org.cp.desktop.awt;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.util.Objects;

@SuppressWarnings("unused")
public class CButton extends Button {

  private static final int PAD_X = 4;
  private static final int PAD_Y = 4;

  private final Dimension previousButtonSize = new Dimension(0, 0);

  private Image icon;

  /**
   * Constructs a new {@link CButton} UI component.
   */
  public CButton() { }

  /**
   * Constructs a new {@link CButton} UI component initialized with the given {@link String label}.
   *
   * @param label {@link String} containing the {@literal text} used to label this button.
   */
  public CButton(String label) {
    super(label);
  }

  /**
   * Constructs a new {@link CButton} UI component initialized with the given {@link Image icon}.
   *
   * @param icon {@link Image} used as the icon to draw on the face of this button.
   * @see java.awt.Image
   */
  public CButton(Image icon) {
    this.icon = icon;
  }

  /**
   * Returns the image painted on the face of this button component.
   *
   * @return an Image object representing the content of the button icon.
   */
  public Image getIcon() {
    return icon;
  }

  /**
   * Sets the image of the icon painted on the button component face.
   *
   * @param icon an Image object representing the content of the icon
   * being displayed on this button component.
   */
  public void setIcon(Image icon) {
    this.icon = icon;
  }

  /**
   * Method called by the rendering context to display the button on screen.
   *
   * @param graphics the Graphics object used to paint the UI of the button component.
   */
  public void paint(Graphics graphics) {
    super.paint(graphics);
    paintIcon(graphics);
  }

  /**
   * Method called to paint the icon on the face of the button component.
   *
   * @param graphics the Graphics object used to paint the icon.
   */
  private void paintIcon(Graphics graphics) {
    if (Objects.nonNull(getIcon()) && !getSize().equals(previousButtonSize)) {
      new IconRenderer(getIcon()).paint(graphics);
    }
  }

  /**
   * The IconRenderer handles the details of rendering, or painting, the icon image on the UI component.
   */
  private class IconRenderer {

    private Dimension iconSize;

    private final Image icon;

    private Point iconLocation;

    public IconRenderer(Image icon) {
      this.icon = icon;
      init();
    }

    private void init() {

      double buttonWidth = (getSize().getWidth() - PAD_X);
      double buttonHeight = (getSize().getHeight() - PAD_Y);
      double iconWidth = getIcon().getWidth(CButton.this);
      double iconHeight = getIcon().getHeight(CButton.this);

      if ((iconWidth / buttonWidth) > (iconHeight / buttonHeight)) {
        iconHeight = Math.min(buttonHeight, (iconHeight * (buttonWidth / iconWidth)));
        iconWidth = buttonWidth;
      }
      else {
        iconWidth = Math.min(buttonWidth, (iconWidth * (buttonHeight / iconHeight)));
        iconHeight = buttonHeight;
      }

      setIconLocation(new Point((int) ((buttonWidth - iconWidth) / 2), (int) ((buttonHeight - iconHeight) / 2)));
      setIconSize(new Dimension((int) iconWidth, (int) iconHeight));
    }

    private Image getIcon() {
      return icon;
    }

    private Point getIconLocation() {
      return iconLocation;
    }

    private void setIconLocation(Point iconLocation) {
      this.iconLocation = iconLocation;
    }

    private Dimension getIconSize() {
      return iconSize;
    }

    private void setIconSize(Dimension iconSize) {
      this.iconSize = iconSize;
    }

    public void paint(Graphics graphics) {
      graphics.drawImage(getIcon(), (int) getIconLocation().getX(), (int) getIconLocation().getY(),
        (int) getIconSize().getWidth(), (int) getIconSize().getHeight(), CButton.this);
    }

    @Override
    public String toString() {

      StringBuilder buffer = new StringBuilder("{iconLocation = ");

      buffer.append(getIconLocation());
      buffer.append(", iconSize = ").append(getIconSize());
      buffer.append("}:").append(getClass().getName());

      return buffer.toString();
    }
  }
}
