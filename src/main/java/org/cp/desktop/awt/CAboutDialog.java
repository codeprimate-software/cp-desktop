/*
 * CAboutDialog.java (c) 20 July 2002
 *
 * This class implements a dialog to display information "about" a particular application.
 *
 * Copyright (c) 2003, Code Primate
 * All Rights Reserved
 * @author John J. Blum
 * @version 2009.5.29
 * @see com.cp.common.swing.JAboutDialog
 * @see java.awt.Dialog
 */
package org.cp.desktop.awt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.cp.desktop.awt.model.AboutInfo;
import org.cp.desktop.awt.support.URLListener;
import org.cp.desktop.awt.support.WebBrowser;

@SuppressWarnings("unused")
public class CAboutDialog extends Dialog {

  private static final boolean MODAL = true;

  private static final Color NAVY_BLUE = new Color(0, 0, 176);

  private static final Dimension DIALOG_SIZE = new Dimension(400, 300);

  private static final Font BOLD_ARIAL_16 = new Font("Arial", Font.BOLD, 16);
  private static final Font BOLD_ARIAL_12 = new Font("Arial", Font.BOLD, 12);

  /**
   * Constructs a new instance of {@link CAboutDialog} initialized with information about the company
   * and product and the frame from which this dialog was derived.  A reference to a browser object
   * responsible for directing the user to the company website, specified by the URL, is also passed
   * to the constructor.
   *
   * @param owner is a reference to the Frame of the application that opened this dialog.
   * @param aboutInfo is an AbstractAboutInfo object containing informatiou about the name of the product,
   * the product logo, the product version, copyright information, and company logo
   * @param browser is an interface reference to a browser object responsible for directing the
   * user to the company URL.
   */
  public CAboutDialog(Frame owner, AboutInfo aboutInfo, WebBrowser browser) {

    super(owner, "About " + aboutInfo.getProductName(), MODAL);

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent event) {
        dispose();
      }
    });

    setResizable(false);
    setSize(DIALOG_SIZE);
    setLocationRelativeTo(owner);
    buildUI(aboutInfo, browser);
  }

  /**
   * Manages the layout & presentation of the user interface for the about dialog.  The buildUI method
   * constructs a border around the content of the about dialog.
   */
  private void buildUI(AboutInfo aboutInfo, WebBrowser browser) {

    setBackground(Color.white);
    setLayout(new GridBagLayout());
    add(buildAboutInfoPanel(aboutInfo, browser), getConstraints());
  }

  /**
   * Handles the about dialog layout.
   */
  private Panel buildAboutInfoPanel(AboutInfo aboutInfo, WebBrowser browser) {

    Panel aboutInfoPanel = new Panel(new BorderLayout(5,5));
    Label imageLabel = new CompanyLogoLabel(aboutInfo.getCompanyLogo());

    imageLabel.setBackground(Color.white);
    aboutInfoPanel.add(imageLabel, BorderLayout.WEST);
    aboutInfoPanel.add(buildInfoPanel(aboutInfo, browser), BorderLayout.CENTER);

    return aboutInfoPanel;
  }

  /**
   * Constructs the interface for the textual information in the about dialog.
   */
  private Panel buildInfoPanel(AboutInfo aboutInfo, WebBrowser browser) {

    Panel infoPanel = new Panel(new GridLayout(7, 1));

    infoPanel.setBackground(Color.lightGray);
    infoPanel.setSize(new Dimension(300, 300));

    Label tempLabel;

    // Product Logo
    tempLabel = (Label) infoPanel.add(new ProductLogoLabel(aboutInfo.getProductLogo()));
    tempLabel.setFont(BOLD_ARIAL_16);

    // Product Name
    tempLabel = (Label) infoPanel.add(new Label(aboutInfo.getProductName(), Label.LEFT));
    tempLabel.setFont(BOLD_ARIAL_16);

    // Product Version Number
    tempLabel = (Label) infoPanel.add(new Label("Version " + aboutInfo.getVersionNumber(), Label.LEFT));
    tempLabel.setFont(BOLD_ARIAL_12);

    // Add spacer...
    infoPanel.add(new Label(" <<<<<<<<<< - >>>>>>>>>> ", Label.CENTER));

    // Copyright
    tempLabel = (Label) infoPanel.add(new Label(aboutInfo.getCopyright(), Label.LEFT));
    tempLabel.setFont(BOLD_ARIAL_12);

    // Company URL
    tempLabel = (Label) infoPanel.add(new Label(aboutInfo.getCompanyURL().toExternalForm(), Label.LEFT));
    tempLabel.setFont(BOLD_ARIAL_12);
    tempLabel.setForeground(NAVY_BLUE);
    tempLabel.addMouseListener(new URLListener(this, browser, aboutInfo.getCompanyURL()));

    // Licensee
    tempLabel = (Label) infoPanel.add(new Label("Licensed to " + aboutInfo.getLicensee(), Label.LEFT));
    tempLabel.setFont(BOLD_ARIAL_12);

    return infoPanel;
  }

  /**
   * Returns constraints for the about dialog used create a border around the dialog container.
   */
  private GridBagConstraints getConstraints() {
    return new GridBagConstraints(0, 0, 1, 1, 100, 100, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
      new Insets(10, 10, 10, 10), 10, 10);
  }

  private static class CompanyLogoLabel extends ImageLabel {

    public CompanyLogoLabel(Image img) {
      super("               ", img);
    }

    public int getImageX() {
      return ((getSize().width - getImageWidth()) / 2);
    }

    public int getImageY() {
      return (getSize().height - getImageHeight());
    }
  }

  private static class ProductLogoLabel extends ImageLabel {

    public ProductLogoLabel(Image img) {
      super("                              ", img);
    }

    public int getImageX() {
      return 0;
    }

    public int getImageY() {
      return ((getSize().height - getImageHeight()) / 2);
    }
  }

  private static abstract class ImageLabel extends Label {

    private int imageHeight;
    private int imageWidth;

    private final Image image;

    public ImageLabel(String text, Image image) {
      super(text);
      this.image = image;
    }

    private void init() {

      double imgWidth = getImage().getWidth(this);
      double imgHeight = getImage().getHeight(this);

      if ((imgWidth / getSize().width) > (imgHeight / getSize().height)) {
        setImageHeight(Math.min(getSize().height, (int) (imgHeight * (getSize().getWidth() / imgWidth))));
        setImageWidth(getSize().width);
      }
      else {
        setImageWidth(Math.min(getSize().width, (int) (imgWidth * (getSize().getHeight() / imgHeight))));
        setImageHeight(getSize().height);
      }
    }

    public Image getImage() {
      return image;
    }

    public int getImageHeight() {
      return imageHeight;
    }

    private void setImageHeight(int imageHeight) {
      this.imageHeight = imageHeight;
    }

    public int getImageWidth() {
      return imageWidth;
    }

    private void setImageWidth(int imageWidth) {
      this.imageWidth = imageWidth;
    }

    public abstract int getImageX();

    public abstract int getImageY();

    public void paint(Graphics graphics) {

      super.paint(graphics);

      // Note, init needs to be called in the paint method since the size property for label is not set until the
      // Label component is rendered to screen.
      init();

      graphics.drawImage(getImage(), getImageX(), getImageY(), getImageWidth(), getImageHeight(), this);
    }
  }
}
