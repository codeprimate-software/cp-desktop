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
package org.cp.desktop.swing.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.cp.elements.lang.ObjectUtils;
import org.cp.elements.lang.annotation.NotNull;

public class SortedListModel<E> implements ListModel<E> {

  private Comparator<E> listModelComparator;

  private List<Integer> indexList;

  private final ListModel<E> listModel;

  public SortedListModel(@NotNull ListModel<E> listModel, @NotNull Comparator<E> orderBy) {

    this.listModel = ObjectUtils.requireObject(listModel, "ListModel is required");
    this.listModel.addListDataListener(new ListModelDataListener());

    this.listModelComparator =
      ObjectUtils.requireObject(orderBy, "Comparator used to order elements in the List is required");

    this.indexList = new ArrayList<>(listModel.getSize());

    for (int index = 0, length = listModel.getSize(); index < length; index++) {
      this.indexList.add(index);
    }

    this.indexList.sort(new IndexListComparator());
  }

  protected List<Integer> getIndexList() {
    return this.indexList;
  }

  protected @NotNull ListModel<E> getListModel() {
    return this.listModel;
  }

  public @NotNull Comparator<E> getOrderBy() {
    return this.listModelComparator;
  }

  public void setOrderBy(@NotNull Comparator<E> orderBy) {

    if (this.listModelComparator == null || !this.listModelComparator.equals(orderBy)) {
      this.listModelComparator = ObjectUtils.requireObject(orderBy,
        "The Comparator used to order elements in the List is required");
      handleModelChange();
    }
  }

  @Override
  public void addListDataListener(ListDataListener listener) {
    getListModel().addListDataListener(listener);
  }

  public E getElementAt(int index) {
    return getListModel().getElementAt(getIndexList().get(index));
  }

  public int getSize() {
    return getListModel().getSize();
  }

  protected void handleModelChange() {

    if (getIndexList().size() != getSize()) {
      initIndexList();
    }

    getIndexList().sort(new IndexListComparator());
  }

  protected void initIndexList() {

    this.indexList = new ArrayList<>(getSize());

    for (int index = 0, size = getSize(); index < size; index++) {
      this.indexList.add(index);
    }
  }

  @Override
  public void removeListDataListener(ListDataListener listener) {
    getListModel().removeListDataListener(listener);
  }

  protected class IndexListComparator implements Comparator<Integer> {

    public int compare(Integer indexOne, Integer indexTwo) {

      E elementOne = getListModel().getElementAt(indexOne);
      E elementTwo = getListModel().getElementAt(indexTwo);

      return getOrderBy().compare(elementOne, elementTwo);
    }
  }

  protected class ListModelDataListener implements ListDataListener {

    public void contentsChanged(ListDataEvent event) {
      handleModelChange();
    }

    public void intervalAdded(ListDataEvent event) {
      handleModelChange();
    }

    public void intervalRemoved(ListDataEvent event) {
      handleModelChange();
    }
  }
}
