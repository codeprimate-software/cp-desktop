/*
 * AboutInfo.java (c) 8 August 2004
 *
 * Copyright (c) 2003, Code Primate
 * All Rights Reserved
 * @author John J. Blum
 * @version 2006.10.23
 */
package org.cp.desktop.awt.model;

import java.awt.Image;
import java.io.Serializable;
import java.net.URL;

@SuppressWarnings("unused")
public interface AboutInfo extends Serializable {

  char COPYRIGHT_SYMBOL = '\u00A9';

  String ALL_RIGHTS_RESERVED = "All rights reserved.";

  /**
   * Returns the image for the company logo.
   * @return and Image object of the company logo.
   */
  Image getCompanyLogo();

  /**
   * Returns the name of the company.
   * @return a String value for the name of the company.
   */
  String getCompanyName();

  /**
   * Returns a uniform resource locator referring to the website of the company.
   * @return a URL object referencing the company's website.
   */
  URL getCompanyURL();

  /**
   * Returns legal information pertaining to copyrighted material.  This information is usually
   * derived from the other properties of this class, such as companyName, etc.
   * @return a String value representing the copyright.
   */
  String getCopyright();

  /**
   * Returns infomration about the End User License Agreement.
   * @return a String value containing the end user license agreement.
   */
  String getEULA();

  /**
   * Returns licensee information for the license holder of the product.
   * @return a String value specifying the license holder of the product.
   */
  String getLicensee();

  /**
   * Returns an image of the product, or a brand logo for the product.
   * @return an Image object representing the brand logo of the product.
   */
  Image getProductLogo();

  /**
   * Returns the name of the product.
   * @return a String value specifying the name of the product.
   */
  String getProductName();

  /**
   * Returns the product version number.
   * @return a String value specifying the product version number.
   */
  String getVersionNumber();

}
