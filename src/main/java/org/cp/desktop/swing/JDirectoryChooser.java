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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.cp.desktop.awt.image.ImageUtils;
import org.cp.elements.io.DirectoriesOnlyFilter;
import org.cp.elements.lang.StringUtils;
import org.cp.elements.util.ArrayUtils;

public final class JDirectoryChooser extends JDialog {

  private static final boolean MODAL = true;

  private static final Dimension DIALOG_SIZE = new Dimension(350, 300);

  private static final String DEFAULT_HOST_NAME = "localhost";
  private static final String DEFAULT_TITLE = "Choose Directory";

  private File selectedDirectory;

  private JTree fileSystemTree;

  private final Set expandedDirectories = new HashSet();

  /**
   * Creates an instance of the JDirectoryChooser class displaying the localhost file system allowing the user
   * to select a directory.
   * @param owner a Frame object owning this dialog.
   */
  public JDirectoryChooser(final Frame owner) {
    super(owner, DEFAULT_TITLE, MODAL);
    setSize(DIALOG_SIZE);
    buildUI();
  }

  /**
   * Returns the root of the file system for the localhost.
   * @return a TreeNode object referring to the root of the file system on the localhost.
   */
  private TreeNode getFileSystemRootNode() {
    final MutableTreeNode fileSystemRootNode = new DefaultMutableTreeNode(getHostName());
    final File[] roots = File.listRoots();

    Arrays.sort(roots);

    for (int index = 0, len = roots.length; index < len; index++) {
      fileSystemRootNode.insert(new DefaultMutableTreeNode(roots[index], true), index);
    }

    return fileSystemRootNode;
  }

  /**
   * Gets an instance of a JTree that is a view of the file system on the localhost.
   * @return a JTree instance constructed from the file system view of the localhost.
   */
  private synchronized JTree getFileSystemTree() {

    if (fileSystemTree == null) {

      fileSystemTree = new JTree(new DefaultTreeModel(getFileSystemRootNode()));
      fileSystemTree.setCellRenderer(new FileSystemTreeCellRenderer());
      fileSystemTree.setRootVisible(false);

      fileSystemTree.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent event) {

          DefaultMutableTreeNode selectedTreeNode = (DefaultMutableTreeNode) fileSystemTree.getLastSelectedPathComponent();

          if (selectedTreeNode != null) {
            setSelectedDiretory((File) selectedTreeNode.getUserObject());
            if (event.getClickCount() > 1 && !expandedDirectories.contains(getSelectedDirectory())) {
              expandDirectory(selectedTreeNode);
              fileSystemTree.expandPath(new TreePath(selectedTreeNode.getPath()));
              expandedDirectories.add(getSelectedDirectory());
            }
          }
          else {
            setSelectedDiretory(null);
          }
        }
      });

      fileSystemTree.addKeyListener(new KeyAdapter() {
        public void keyPressed(final KeyEvent event) {
          if (event.getKeyCode() == KeyEvent.VK_F5) {
            final DefaultMutableTreeNode selectedTreeNode = (DefaultMutableTreeNode) fileSystemTree.getLastSelectedPathComponent();

            if (selectedTreeNode != null) {
              refreshDirectory(selectedTreeNode);
            }
          }
        }
      });
    }
    return fileSystemTree;
  }

  /**
   * Returns the network name of the localhost.
   * @return a String specifying the network name of the localhost.
   */
  private String getHostName() {

    try {
      String hostName = InetAddress.getLocalHost().getHostName();

      hostName = StringUtils.hasText(hostName) ? hostName : DEFAULT_HOST_NAME;

      return hostName;
    }
    catch (Exception ignore) {
      return DEFAULT_HOST_NAME;
    }
  }

  /**
   * Returns the directory that the user selected as a File object.
   * @return a File object referring to the selected directory in the file system on the localhost
   * by the user.
   */
  public File getSelectedDirectory() {
    return selectedDirectory;
  }

  /**
   * Sets the directory selected by the user, denoted as a File object.
   * @param directory a File object referring to the selected directory by the user.
   */
  private void setSelectedDiretory(File directory) {

    if (directory != null && !directory.isDirectory()) {
      throw new IllegalArgumentException("(" + directory + ") is not a valid directory!");
    }

    this.selectedDirectory = directory;
  }

  /**
   * Constructs the JDirectoryChooser Dialog's tool bar used to select a file system directory and/or
   * close the dialog.
   * @return a JToolBar containing the control buttons of the JDirectoryChooser Dialog.
   */
  private JToolBar buildToolBar() {

    JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);

    toolbar.setBorder(BorderFactory.createEmptyBorder());
    toolbar.setFloatable(false);
    toolbar.setLayout(new FlowLayout(FlowLayout.CENTER));

    Dimension BUTTON_SIZE = new Dimension(75, 25);

    JButton select = (JButton) toolbar.add(new XButton("Select"));

    select.addActionListener(event -> {
      File selectedDirectory =
        (File) ((DefaultMutableTreeNode) this.fileSystemTree.getLastSelectedPathComponent()).getUserObject();
      setSelectedDiretory(selectedDirectory);
      dispose();
    });

    select.setPreferredSize(BUTTON_SIZE);
    toolbar.addSeparator();

    final JButton cancel = (JButton) toolbar.add(new XButton("Cancel"));

    cancel.addActionListener(event -> {
      setSelectedDiretory(null);
      dispose();
    });

    cancel.setPreferredSize(BUTTON_SIZE);

    return toolbar;
  }

  /**
   * Contructs the user interface to the JDirectoryChooser Dialog.  Instantiates UI components and hanles layout of
   * the dialog.
   */
  private void buildUI() {
    getContentPane().add(new JScrollPane(getFileSystemTree(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
      JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
    getContentPane().add(buildToolBar(), BorderLayout.SOUTH);
  }

  /**
   * Expands the tree node of the fileSystemTree denoting the directory in the file system on the localhost.
   * @param treeNode is the DefaultMutableTreeNode of the fileSystemTree object being expanded to view the
   * contents of the directory in the file system on the localhost that it represents.
   */
  private void expandDirectory(DefaultMutableTreeNode treeNode) {

    File selectedDirectory = (File) treeNode.getUserObject();

    File[] subdirectories =
      ArrayUtils.nullSafeArray(selectedDirectory.listFiles(new DirectoriesOnlyFilter()), File.class);

    if (ArrayUtils.isNotEmpty(subdirectories)) {
      Arrays.sort(subdirectories);
      for (int index = 0; index < subdirectories.length; index++) {
        ((DefaultTreeModel) getFileSystemTree().getModel()).insertNodeInto(
          new DefaultMutableTreeNode(subdirectories[index], true), treeNode, index);
      }
    }
  }

  /**
   * Removes all the parentNode's children and re-adds them to the parentNode to reflect a change to the directory,
   * represented by the node, in file system on the localhost.
   * @param parentNode is the DefaultMutableTreeNode denoting the directory in the file system on the locahost that
   * is being refreshed.
   */
  private void refreshDirectory(DefaultMutableTreeNode parentNode) {

    for (int index = parentNode.getChildCount(); --index >= 0; ) {
      ((MutableTreeNode) parentNode.getChildAt(index)).removeFromParent();
    }

    ((DefaultTreeModel) getFileSystemTree().getModel()).reload(parentNode);
    expandedDirectories.remove(getSelectedDirectory());
  }

  /**
   * Overridden setVisible method to call the showDialog method.
   */
  public void setVisible() {
    showDialog();
  }

  /**
   * Shows the JDirectoryChooser Dialog with a view of the localhost filesystem.  If the user selects a directory
   * and says "OK", then this Dialog will be closed and the selectedDiretory property will be set to the File
   * object referring to the file system path of the user's choice.
   * @return a File object specifying the selected directory by the user.
   */
  public File showDialog() {
    setLocationRelativeTo(getOwner());
    super.setVisible(true);
    return getSelectedDirectory();
  }

  /**
   * The FileSystemTreeCellRenderer class is responsible for representing the
   * nodes of the fileSystemTree, setting appropriate icons for the nodes that
   * are smbolic to the type of drive/folder in the file system of the
   * localhost.
   */
  public final class FileSystemTreeCellRenderer extends DefaultTreeCellRenderer {

    private final Map<Object, Object> resourceMap = new HashMap<>();

    private synchronized Icon getIcon(String path) {

      Icon icon = (Icon) resourceMap.get(path);

      if (icon == null) {
        Image iconImage = ImageUtils.getImage(FileSystemTreeCellRenderer.class.getResource(path), JDirectoryChooser.this);
        icon = new ImageIcon(iconImage);
        resourceMap.put(path, icon);
      }

      return icon;
    }

    public Component getTreeCellRendererComponent(JTree tree,
                                                  Object value,
                                                  boolean sel,
                                                  boolean expanded,
                                                  boolean leaf,
                                                  int row,
                                                  boolean hasFocus) {

      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

      DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;

      if (treeNode.getLevel() == 0) {
        setText(treeNode.getUserObject().toString());
        setToolTipText(getText());
      }
      else {

        File directory = (File) treeNode.getUserObject();

        if (tree.isExpanded(new TreePath(treeNode.getPath()))) {
          setIcon(getIcon("/etc/content/icons/openFolder.gif"));
        }
        else {
          setIcon(getIcon("/etc/content/icons/folder.gif"));
        }

        if (treeNode.getLevel() == 1) {
          setText(directory.getAbsolutePath());
        }
        else {
          setText(directory.getName());
        }

        setToolTipText(directory.getAbsolutePath());
      }

      return this;
    }
  }
}
