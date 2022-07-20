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

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JWindow;

import org.cp.desktop.awt.image.ImageUtils;
import org.cp.desktop.awt.support.WindowUtils;
import org.cp.elements.lang.Assert;

public final class JSplashWindow extends JWindow {

  /**
   * Creates an instance of the JSplashWindow UI component class initialized with the specified JFrame owner
   * and splash image.
   * @param owner the application frame owning this Splash Window.
   * @param image the splash image to display in the Splash Window.
   */
  public JSplashWindow(JFrame owner, Image image) {
    this(owner, image, 0);
  }

  /**
   * Creates an instance of the JSplashWindow UI component class by calling the parent constructor to set ownership,
   * register a MouseListener on the Splash Window, set size and location and kick off a Thread to close the Splash
   * Window after a specified duration.
   * @param owner a JFrame component that is the parent of the splash window.
   * @param image Image object that will be displayed in the splash window.
   * @param numberOfSeconds is an integer value specifying the number of seconds to display the splash window.
   */
  public JSplashWindow(JFrame owner, Image image, int numberOfSeconds) {

    super(owner);

    Assert.notNull(image, "The image displayed in the Splash Window cannot be null!");

    addMouseListener(new SplashWindowMouseListener());
    getContentPane().add(createImageComponent(createSplashImage(image)));
    pack();
    setLocationRelativeTo(null);

    if (numberOfSeconds > 0) {
      // Note, paint problem occurred using SwingUtilities.invokeLater(new SplashWindowRunner(numberOfSeconds)); method.
      new Thread(new SplashWindowRunner(numberOfSeconds)).start();
    }
    else {
      owner.addWindowListener(new FrameOwnerWindowListener());
      setVisible(true);
    }
  }

  /**
   * Creates and JComponent object to display the image in the Splash Window.
   * @param image the image to display in the Splash Window.
   * @return a JComponent object capable of displaying the image in the Splash Window.
   */
  private JComponent createImageComponent(Image image) {
    JLabel imageComponent = new JLabel(new ImageIcon(image));
    imageComponent.setOpaque(false);
    return imageComponent;
  }

  /**
   * Composes a new Image to be used as the Splash image from a combination of the screen shot and user's
   * specified image.  The screen capture is used to give the appearance that the Splash Window is transparent.
   * @param image the user image used to overlay the screen capture to form the Splash image.
   * @return an Image object composed of the screen shot and user image constituting the Splash image.
   */
  private Image createSplashImage(Image image) {

    Assert.notNull(image, "The image to be displayed by the Splash Window cannot be null!");

    try {
      Dimension splashWindowSize = ImageUtils.getImageSize(image, this);

      Image splashImage = new Robot()
        .createScreenCapture(new Rectangle(WindowUtils.getDesktopLocation(splashWindowSize), splashWindowSize));

      MediaTracker mediaTracker = new MediaTracker(this);

      mediaTracker.addImage(splashImage, 0);
      mediaTracker.waitForAll();

      Graphics g = splashImage.getGraphics();

      g.drawImage(image, 0, 0, this);

      return splashImage;
    }
    catch (AWTException e) {
      throw new IllegalStateException("Failed to capture screen image!", e);
    }
    catch (InterruptedException ignore) { }

    return null;
  }

  private final class FrameOwnerWindowListener extends WindowAdapter {

    public void windowOpened(WindowEvent event) {
      setVisible(false);
      dispose();
    }
  }

  private final class SplashWindowMouseListener extends MouseAdapter {

    public void mousePressed(MouseEvent event) {
      setVisible(false);
      dispose();
    }
  }

  private final class SplashWindowRunner implements Runnable {

    private final int numberOfSeconds;

    /**
     * Creates an instance of the SplashWindowRunner Runnable class.
     * @param numberOfSeconds the number of seconds to delay the Thread that runs this Runnable.
     */
    private SplashWindowRunner(int numberOfSeconds) {

      Assert.isTrue(numberOfSeconds > 0,
        "The number of seconds [%d] must be greater than zero", numberOfSeconds);

      this.numberOfSeconds = numberOfSeconds;
    }

    /**
     * The run method is called by a Swing Thread and is responsible for displaying the JSplashWindow UI component
     * on the user's desktop for the specified delayed number of seconds.
     */
    public void run() {

      setVisible(true);

      try {
        Thread.sleep(TimeUnit.SECONDS.toMillis(numberOfSeconds));
      }
      catch (InterruptedException ignore) { }

      setVisible(false);
      dispose();
    }
  }
}
