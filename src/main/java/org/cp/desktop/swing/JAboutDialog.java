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

import java.awt.BorderLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.cp.desktop.awt.model.AboutInfo;
import org.cp.desktop.awt.support.WebBrowser;

public class JAboutDialog extends JDialog {

  private static final boolean DEFAULT_MODAL = true;
  private static final boolean DEFAULT_RESIZABLE = false;

  /**
   * Constructs an instance of the JAboutDialog Swing component class.
   * @param owner the JFrame container owning this dialog.
   * @param aboutInfo the AboutInfo instance containing the content information in the dialog.
   * @param browser the Browser object used to invoke a URL from the dialog.
   */
  public JAboutDialog(JFrame owner, AboutInfo aboutInfo, WebBrowser browser) {

    super(owner, "About " + aboutInfo.getProductName(), DEFAULT_MODAL);

    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setResizable(DEFAULT_RESIZABLE);
    getContentPane().add(getImageComponent(aboutInfo.getCompanyLogo()), BorderLayout.CENTER);
    pack();
    setLocationRelativeTo(owner);
  }

  private JComponent getImageComponent(Image image) {
    return new JLabel(new ImageIcon(image));
  }
}
