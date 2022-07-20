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
package org.cp.desktop.swing.table;

import javax.swing.table.AbstractTableModel;

import org.cp.elements.data.struct.tabular.Table;
import org.cp.elements.lang.ObjectUtils;
import org.cp.elements.util.stream.StreamUtils;

public class DefaultTableModel extends AbstractTableModel {

  private final Table table;

  /**
   * Creates an instance of the DefaultTableModel class initialized to the specified RecordTable object.
   * The instance of the this TableModel will be used to view the contents of the RecordTable object in
   * an JTable Swing GUI component.
   */
  public DefaultTableModel(Table table) {
    this.table = ObjectUtils.requireObject(table, "Table is required");
  }

  /**
   * Returns the object type of the column indexed by the integer value parameter.
   * @param columnIndex an integer value index of the column within the table.
   * @return a Class object specifying the type of data contained in the column indexed by columnIndex.
   */
  public Class<?> getColumnClass(int columnIndex) {
    return getTable().getColumn(columnIndex).getType();
  }

  /**
   * Returns the number of columns in the table.
   * @return an integer value specifying the number of columns in the table.
   */
  public int getColumnCount() {
    return Long.valueOf(StreamUtils.stream(getTable().columns()).count()).intValue();
  }

  /**
   * Returns the name of the column at columnIndex within this table.
   * @param columnIndex is an integer value specifying the index of the column in this table.
   * @return a String value specifying the name of the column at the given columnIndex within this table.
   */
  public String getColumnName(int columnIndex) {
    return getTable().getColumn(columnIndex).getName();
  }

  /**
   * Returns the number of rows in the table.
   * @return an integer value indicating the number of rows in this table.
   */
  public int getRowCount() {
    return getTable().size();
  }

  /**
   * Accessor method for subclasses to obtain a reference to the underlying RecordTable object.
   * @return the instance of the underlying RecordTable object.
   */
  protected final Table getTable() {
    return this.table;
  }

  /**
   * Returns whether the information in the specified cell (rowIndex, column) of this table can be modified.
   * @param rowIndex the rowIndex index in this table.
   * @param columnIndex the column index in this table.
   * @return a boolean indicating whether the user can modify the value at the specified cell (rowIndex, column)
   * in the table.
   */
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return true;
  }

  /**
   * Returns the Object value at the specified cell (rowIndex, column) in this table.
   * @param rowIndex the rowIndex index in this table.
   * @param columnIndex the column index in this table.
   * @return the Object value at the specified cell (rowIndex, column) in this table.
   */
  public Object getValueAt(int rowIndex, final int columnIndex) {
    return getTable().getValue(rowIndex, columnIndex);
  }

  /**
   * Sets the corresponding cell (rowIndex, columnIndex) to the specified Object value.
   * @param value the Object value to set the corresponding cell to.
   * @param rowIndex the row index in this table.
   * @param columnIndex the column index in this table.
   */
  public void setValueAt(Object value, int rowIndex, int columnIndex) {
    getTable().setValue(rowIndex, columnIndex, value);
  }
}
