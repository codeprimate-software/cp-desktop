/*
 * URLListener.java (c) 17 April 2002
 *
 * Copyright (c) 2001, Code Primate
 * All Rights Reserved
 * @author John J. Blum
 * @version 2004.1.16
 * @deprecated see the javax.swing.event.HyperlinkListener class.
 */
package org.cp.desktop.awt.support;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import org.cp.elements.lang.Assert;

public class URLListener extends MouseAdapter implements KeyListener {

  private static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
  private static final Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);

  private Component component;

  private URL url;

  private WebBrowser webBrowser;

  /**
   * Creates a new instance of the URLListener class to handle click events
   * on hyperlinks embeded in applications/applets.
   *
   * @param component the Component containing the URL on which the mouse icon
   * will change and for which this listener is registered.
   * @param webBrowser the WebBrowser object implementing the WebBrowser interface and
   * is capable of redirecting a Web browser to the new Internet resource
   * specified by the URL.
   * @param url a URL the uniform resource locator referring to the Web
   * resource.
   */
  public URLListener(Component component, WebBrowser webBrowser, URL url) {

    setComponent(component);
    setWebBrowser(webBrowser);
    setURL(url);
  }

  /**
   * Sets the component containing the hyperlink.
   * @param component the Component with the hyperlink.
   */
  private void setComponent(Component component) {

    Assert.notNull(component, "Component is required");

    this.component = component;
    component.addKeyListener(this);
    //component.addMouseListener(this);
  }

  /**
   * Returns the component containing the hyperlink.
   * @return the Component containing the hyperlink.
   */
  private Component getComponent() {
    return this.component;
  }

  /**
   * Set the URL to the Web resource to the specified address.
   * @param address the URL to the Web resource.
   */
  private void setURL(URL address) {
    Assert.notNull(address, "URL is required");
    this.url = address;
  }

  /**
   * Returns the URL to the Web resource.
   * @return a URL object referring to the Web resource that will be retrieved
   * and displayed in the WebBrowser.
   */
  private URL getURL() {
    return this.url;
  }

  /**
   * Sets the WebBrowser object responsible for retrieving the Web resource
   * at URL and displaying the resource in a Web browser.
   * @param webBrowser the WebBrowser object used to fetch the Web content and
   * display it in a Web browser.
   */
  private void setWebBrowser(WebBrowser webBrowser) {
    Assert.notNull(webBrowser, "WebBrowser is required");
    this.webBrowser = webBrowser;
  }

  /**
   * Returns the WebBrowser object responsible for retrieving the Web resource
   * and opening a Web browser to view the resource.
   * @return the WebBrowser object.
   */
  private WebBrowser getWebBrowser() {
    return this.webBrowser;
  }

  /**
   * Captures key pressed events when the user presses a key when the
   * hyperlink is selected.
   * @param event the KeyEvent capturing the key pressed event on a
   * hyperlink.
   */
  public void keyPressed(KeyEvent event) {

    if (getComponent().isEnabled()) {
      if (event.getKeyCode() == KeyEvent.VK_ENTER) {
        getWebBrowser().goTo(getURL());
      }
    }
  }

  public void keyReleased(KeyEvent event) { }

  public void keyTyped(KeyEvent event) { }

  /**
   * Called when the user moves his/her mouse over the hyperlink in the UI.
   * The method then proceeds in setting the mouse cursor to a pointing hand.
   *
   * @param event the MouseEvent object representing the mouse over the
   * hyperlink.
   */
  public void mouseEntered(MouseEvent event) {
    getComponent().setCursor(HAND_CURSOR);
  }

  /**
   * Called when the user moves the mouse off of the hyperlink in the UI.
   * The method then sets the mouse back to the default arrow.
   *
   * @param event the MouseEvent of the user moving the mouse off of the
   * hyperlink.
   */
  public void mouseExited(MouseEvent event) {
    getComponent().setCursor(DEFAULT_CURSOR);
  }

  /**
   * Triggers the hyperlink and causes the WebBrowser object to retrieve the resource
   * (content) at the specified URL.
   *
   * @param event the MouseEvent when the user activates the hyperlink by clicking
   * on it with the mouse or hitting the enter key when the hyperlink is selected.
   */
  public void mousePressed(MouseEvent event) {
    if (getComponent().isEnabled()) {
      getWebBrowser().goTo(getURL());
    }
  }
}
