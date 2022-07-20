/*
 * WebBrowser.java (c) 17 April 2002
 *
 * Copyright (c) 2001, Code Primate
 * All Rights Reserved
 * @author John J. Blum
 * @version 2004.1.16
 * @deprecated see the javax.swing.event.HyperlinkListener class.
*/
package org.cp.desktop.awt.support;

import java.net.URL;

public interface WebBrowser {

  /**
   * Redirects the system {@link WebBrowser} to the specified {@link URL}.
   *
   * @param url {@link URL}, or {@literal Uniform Resource Locator} of the Web resource
   * to display in the {@link WebBrowser}.
   * @see java.net.URL
   */
  void goTo(URL url);

}
