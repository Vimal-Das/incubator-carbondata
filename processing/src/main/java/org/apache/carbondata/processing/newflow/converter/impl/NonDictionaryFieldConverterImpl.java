/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.carbondata.processing.newflow.converter.impl;

import java.nio.charset.Charset;

import org.apache.carbondata.core.constants.CarbonCommonConstants;
import org.apache.carbondata.core.metadata.datatype.DataType;
import org.apache.carbondata.core.metadata.schema.table.column.CarbonColumn;
import org.apache.carbondata.core.util.DataTypeUtil;
import org.apache.carbondata.processing.newflow.DataField;
import org.apache.carbondata.processing.newflow.converter.BadRecordLogHolder;
import org.apache.carbondata.processing.newflow.converter.FieldConverter;
import org.apache.carbondata.processing.newflow.row.CarbonRow;

public class NonDictionaryFieldConverterImpl implements FieldConverter {

  private DataType dataType;

  private int index;

  private String nullformat;

  private CarbonColumn column;

  public NonDictionaryFieldConverterImpl(DataField dataField, String nullformat, int index) {
    this.dataType = dataField.getColumn().getDataType();
    this.column = dataField.getColumn();
    this.index = index;
    this.nullformat = nullformat;
  }

  @Override
  public void convert(CarbonRow row, BadRecordLogHolder logHolder) {
    String dimensionValue = row.getString(index);
    if (dimensionValue == null || dimensionValue.equals(nullformat)) {
      dimensionValue = CarbonCommonConstants.MEMBER_DEFAULT_VAL;
    }
    if (dataType != DataType.STRING) {
      if (null == DataTypeUtil.normalizeIntAndLongValues(dimensionValue, dataType)) {
        logHolder.setReason(
            "The value " + " \"" + dimensionValue + "\"" + " with column name " + column
                .getColName() + " and column data type " + dataType + " is not a valid " + dataType
                + " type.");
      }
    }
    row.update(dimensionValue.getBytes(Charset.forName(CarbonCommonConstants.DEFAULT_CHARSET)),
        index);
  }
}
