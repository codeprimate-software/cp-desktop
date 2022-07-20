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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.cp.elements.lang.Assert;

public class SortedListModel implements ListModel {

  private Comparator listModelComparator;

  private List<Integer> indexList;

  private final ListModel listModel;

  public SortedListModel(ListModel listModel, Comparator orderBy) {

    Assert.notNull(listModel, "ListModel is required");
    Assert.notNull(orderBy, "Comparator used to order the elements in the List is required");

    this.listModel = listModel;
    this.listModel.addListDataListener(new ListModelDataListener());
    listModelComparator = orderBy;
    indexList = new ArrayList<Integer>(listModel.getSize());

    for (int index = 0, length = listModel.getSize(); index < length; index++) {
      indexList.add(index);
    }

    Collections.sort(indexList, new IndexListComparator());
  }

  public void addListDataListener(ListDataListener l) {
    getListModel().addListDataListener(l);
  }

  public Object getElementAt(int index) {
    return getListModel().getElementAt(getIndexList().get(index));
  }

  public int getSize() {
    return getListModel().getSize();
  }

  public void removeListDataListener(ListDataListener listener) {
    getListModel().removeListDataListener(listener);
  }

  protected List<Integer> getIndexList() {
    return indexList;
  }

  protected final ListModel getListModel() {
    return listModel;
  }

  public Comparator getOrderBy() {
    return listModelComparator;
  }

  public void setOrderBy(Comparator orderBy) {

    Assert.notNull(orderBy, "The Comparator used to order the elments in the List is required");

    if (!this.listModelComparator.equals(orderBy)) {
      this.listModelComparator = orderBy;
      handleModelChange();
    }
  }

  private void handleModelChange() {

    if (getIndexList().size() != getSize()) {
      initIndexList();
    }

    Collections.sort(getIndexList(), new IndexListComparator());
  }

  protected void initIndexList() {

    indexList = new ArrayList<>(getSize());

    for (int index = 0, size = getSize(); index < size; index++) {
      indexList.add(index);
    }
  }

  private class IndexListComparator implements Comparator<Integer> {

    public int compare(Integer index0, Integer index1) {

      Object element0 = getListModel().getElementAt(index0);
      Object element1 = getListModel().getElementAt(index1);

      return getOrderBy().compare(element0, element1);
    }
  }

  private class ListModelDataListener implements ListDataListener {

    public void contentsChanged(ListDataEvent event) {
      handleModelChange();
    }

    public void intervalAdded(ListDataEvent e) {
      handleModelChange();
    }

    public void intervalRemoved(ListDataEvent e) {
      handleModelChange();
    }
  }

}
