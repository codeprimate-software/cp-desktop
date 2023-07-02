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
package org.cp.desktop.awt.image;

import static org.cp.elements.lang.LangExtensions.assertThat;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;

import org.cp.elements.lang.Assert;

@SuppressWarnings("unused")
public abstract class ImageUtils {

  protected static final Toolkit TOOLKIT = Toolkit.getDefaultToolkit();

  /**
   * Default constructor used by subclasses to extend the functionality of the {@link ImageUtils} class.
   * <p>
   * {@link ImageUtils} is a stateless utility class therefore instances need not be created.
   */
  protected ImageUtils() { }

  /**
   * Generates two arrow images of the given size both pointing to the left.
   *
   * @param boundingBox java.awt.Dimension object specifying the bounding box
   * containing the two left arrows.
   * @return a java.awt.Image object of the double left arrows.
   */
  public static Image getDoubleLeftArrowImage(Dimension boundingBox) {

    validateBoundingBox(boundingBox);

    int width = (int) boundingBox.getWidth();
    int height = (int) boundingBox.getHeight();
    int halfWidth = width / 2;
    int halfHeight = height / 2;

    Polygon shape = new Polygon();

    shape.addPoint(0, halfHeight);
    shape.addPoint(halfWidth, 0);
    shape.addPoint(halfWidth, halfHeight);
    shape.addPoint(width, 0);
    shape.addPoint(width, height);
    shape.addPoint(halfWidth, halfHeight);
    shape.addPoint(halfWidth, height);

    int[] pixels = PixelGenerator.getPixels(shape.getBounds(), shape);

    return TOOLKIT.createImage(new MemoryImageSource(width, height, pixels, 0, width));
  }

  /**
   * Generates two arrow images of the given size both pointing to the right.
   *
   * @param boundingBox java.awt.Dimension object specifying the bounding box
   * containing the two right arrows.
   * @return a java.awt.Image object of the double right arrows.
   */
  public static Image getDoubleRightArrowImage(Dimension boundingBox) {

    validateBoundingBox(boundingBox);

    int width = (int) boundingBox.getWidth();
    int height = (int) boundingBox.getHeight();
    int halfWidth = width / 2;
    int halfHeight = height / 2;

    Polygon shape = new Polygon();

    shape.addPoint(0, height);
    shape.addPoint(0, 0);
    shape.addPoint(halfWidth, halfHeight);
    shape.addPoint(halfWidth, 0);
    shape.addPoint(width, halfHeight);
    shape.addPoint(halfWidth, height);
    shape.addPoint(halfWidth, halfHeight);

    int[] pixels = PixelGenerator.getPixels(shape.getBounds(), shape);

    return TOOLKIT.createImage(new MemoryImageSource(width, height, pixels, 0, width));
  }

  /**
   * Generates an arrow image of the given size pointing down.
   *
   * @param boundingBox java.awt.Dimension object specifying the bounding box
   * containing the down arrow.
   * @return a java.awt.Image object of the down arrow.
   */
  public static Image getDownArrowImage(Dimension boundingBox) {

    validateBoundingBox(boundingBox);

    int width = (int) boundingBox.getWidth();
    int height = (int) boundingBox.getHeight();
    int halfWidth = width / 2;

    Polygon shape = new Polygon();

    shape.addPoint(0, 0);
    shape.addPoint(width, 0);
    shape.addPoint(halfWidth, height);

    int[] pixels = PixelGenerator.getPixels(shape.getBounds(), shape);

    return TOOLKIT.createImage(new MemoryImageSource(width, height, pixels, 0, width));
  }

  /**
   * Loads an image from the specified file in the file system.
   * @param imageFile the File object referring to the file containing the image.
   * @param mediaTracker the Component used by the MediaTracker to track the progress
   * of loading the image from the file.
   * @return an Image object containing the content of the image.
   * @throws FileNotFoundException if the image file cannot be found.
   */
  public static Image getImage(File imageFile, Component mediaTracker) throws FileNotFoundException {

    if (imageFile.exists()) {
      try {
        return getImage(imageFile.toURI().toURL(), mediaTracker);
      }
      catch (MalformedURLException e) {
        throw new FileNotFoundException("The file (" + imageFile + ") cannot be found!");
      }
    }
    else {
      throw new FileNotFoundException("The file (" + imageFile + ") cannot be found!");
    }
  }

  /**
   * Loads the image identified by the uniform resource locator.
   * @param url the Uniform Resource Locator specifying the location of the image to load.
   * @param mediaTracker the Component used by the MediaTracker to track the progress
   * of loading the image from the file.
   * @return an Image object containing the content of the image.
   */
  public static Image getImage(URL url, Component mediaTracker) {

    Image image = Toolkit.getDefaultToolkit().getImage(url);
    MediaTracker resolvedMediaTracker = new MediaTracker(mediaTracker);

    resolvedMediaTracker.addImage(image, 0);

    try {
      resolvedMediaTracker.waitForAll();
    }
    catch (InterruptedException ignore) { }

    return image;
  }

  /**
   * Gets the dimensions (width and height) of the specified image.
   * @param image the Image object for which the size is returned.
   * @param imgObserver the observer of the image.
   * @return a Dimension object specifying the size, both width and height, of the specified image.
   */
  public static Dimension getImageSize(Image image, ImageObserver imgObserver) {
    return new Dimension(image.getWidth(imgObserver), image.getHeight(imgObserver));
  }

  /**
   * Generates an arrow image of the given size pointing left.
   *
   * @param boundingBox java.awt.Dimension object specifying the bounding box
   * containing the left arrow.
   * @return a java.awt.Image object of the left arrow.
   */
  public static Image getLeftArrowImage(Dimension boundingBox) {

    validateBoundingBox(boundingBox);

    int width = (int) boundingBox.getWidth();
    int height = (int) boundingBox.getHeight();
    int halfHeight = height / 2;

    Polygon shape = new Polygon();

    shape.addPoint(0, halfHeight);
    shape.addPoint(width, 0);
    shape.addPoint(width, height);

    int[] pixels = PixelGenerator.getPixels(shape.getBounds(), shape);

    return TOOLKIT.createImage(new MemoryImageSource(width, height, pixels, 0, width));
  }

  /**
   * Generates an arrow image of the given size pointing right.
   *
   * @param boundingBox java.awt.Dimension object specifying the bounding box
   * containing the right arrow.
   * @return a java.awt.Image object of the right arrow.
   */
  public static Image getRightArrowImage(Dimension boundingBox) {

    validateBoundingBox(boundingBox);

    int width = (int) boundingBox.getWidth();
    int height = (int) boundingBox.getHeight();
    int halfHeight = height / 2;

    Polygon shape = new Polygon();

    shape.addPoint(width, halfHeight);
    shape.addPoint(0, 0);
    shape.addPoint(0, height);

    int[] pixels = PixelGenerator.getPixels(shape.getBounds(), shape);

    return TOOLKIT.createImage(new MemoryImageSource(width, height, pixels, 0, width));
  }

  /**
   * Generates an arrow image of the given size pointing up.
   *
   * @param boundingBox: java.awt.Dimension object specifying the bounding box
   * containing the up arrow.
   * @return a java.awt.Image object of the up arrow.
   */
  public static Image getUpArrowImage(Dimension boundingBox) {

    validateBoundingBox(boundingBox);

    int width = (int) boundingBox.getWidth();
    int height = (int) boundingBox.getHeight();
    int halfWidth = width / 2;

    Polygon shape = new Polygon();

    shape.addPoint(0, height);
    shape.addPoint(halfWidth, 0);
    shape.addPoint(width, height);

    int[] pixels = PixelGenerator.getPixels(shape.getBounds(), shape);

    return TOOLKIT.createImage(new MemoryImageSource(width, height, pixels, 0, width));
  }

  /**
   * Verifies that the boudning box is not null and the area of the box is
   * greater than zero.
   * @param boundingBox the smallest area containing the images drawn by this
   * utility class.
   */
  static void validateBoundingBox(Dimension boundingBox) {

    Assert.notNull(boundingBox, "The bounded box defining the area containing the imnage cannot be null");

    assertThat(boundingBox.width * boundingBox.height)
      .describedAs("The area of the bounding box cannot be negative or zero")
      .isGreaterThan(0);
  }
}
