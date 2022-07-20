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
package org.cp.desktop.swing.text;

import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.Segment;

import org.cp.desktop.util.Strings;
import org.cp.elements.lang.Assert;

public class MockDocument implements Document {

  private String text;

  private TextFormat textFormat;

  public MockDocument(String text) {
    this(text, DefaultTextFormat.INSTANCE);
  }

  public MockDocument(String text, TextFormat textFormat) {

    Assert.hasText(text, "Text is required");

    this.text = text;
    this.textFormat = textFormat != null ? textFormat : DefaultTextFormat.INSTANCE;
  }

  public void addDocumentListener(DocumentListener listener) {
    throw new UnsupportedOperationException("Not Implemented!");
  }

  public void addUndoableEditListener(UndoableEditListener listener) {
    throw new UnsupportedOperationException("Not Implemented!");
  }

  public Position createPosition(int offset) {
    throw new UnsupportedOperationException("Not Implemented!");
  }

  public Element getDefaultRootElement() {
    throw new UnsupportedOperationException("Not Implemented!");
  }

  public int getLength() {
    return text.length();
  }

  public Object getProperty(Object key) {
    throw new UnsupportedOperationException("Not Implemented!");
  }

  public void putProperty(Object key, Object value) {
    throw new UnsupportedOperationException("Not Implemented!");
  }

  public Position getEndPosition() {
    return () -> text.length() - 1;
  }

  public Element[] getRootElements() {
    return new Element[0];
  }

  public Position getStartPosition() {
    return () -> 0;
  }

  public String getText(int offset, int length) throws BadLocationException {

    try {
      return text.substring(offset, length);
    }
    catch (IndexOutOfBoundsException e) {
      throw new BadLocationException("Failed to get " + length + " characters of text starting at "
        + offset + "!", offset);
    }
  }

  public void getText(int offset, int length, Segment txt) throws BadLocationException {
    throw new UnsupportedOperationException("Not Implemented!");
  }

  public void insertString(int offset, String value, AttributeSet attrSet) throws BadLocationException {

    try {
      text = Strings.insert(text, textFormat.format(this, offset, value), offset);
    }
    catch (Exception cause) {
      throw new BadLocationException("Failed to insert text (" + value + ") at offset (" + offset + ")!", offset);
    }
  }

  public void remove(int offset, int length) throws BadLocationException {

    try {
      text = Strings.remove(text, offset, length);
    }
    catch (Exception cause) {
      throw new BadLocationException("Failed to remove text between " + offset + " and " + length + "!", offset);
    }
  }

  public void replace(int offset, int length, String value, AttributeSet attrSet)
      throws BadLocationException {

    String replacedText = null;

    try {

      String temp = text.substring(offset, offset + length);

      remove(offset, length);
      replacedText = temp;
      insertString(offset, value, attrSet);
    }
    catch (BadLocationException cause) {

      if (Strings.hasText(replacedText)) {
        text = Strings.insert(text, replacedText, offset);
      }

      throw cause;
    }
  }

  public void removeDocumentListener(DocumentListener listener) {
    throw new UnsupportedOperationException("Not Implemented!");
  }

  public void removeUndoableEditListener(UndoableEditListener listener) {
    throw new UnsupportedOperationException("Not Implemented!");
  }

  public void render(Runnable runnable) {
    throw new UnsupportedOperationException("Not Implemented!");
  }

  @Override
  public String toString() {
    return this.text;
  }

  private static class DefaultTextFormat implements TextFormat {

    public static final DefaultTextFormat INSTANCE = new DefaultTextFormat();

    public String format(Document doc, int offset, String text) throws InvalidTextFormatException {
      return text;
    }
  }
}
