/*******************************************************************************
 * Copyright (c) 2012, 2013 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
// ~
package org.eclipse.nebula.widgets.nattable.grid.data;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

public class DefaultCornerDataProvider implements IDataProvider {
	
	private final IDataProvider columnHeaderDataProvider;
	private final IDataProvider rowHeaderDataProvider;

	public DefaultCornerDataProvider(IDataProvider columnHeaderDataProvider, IDataProvider rowHeaderDataProvider) {
		this.columnHeaderDataProvider = columnHeaderDataProvider;
		this.rowHeaderDataProvider = rowHeaderDataProvider;
	}
	
	public int getColumnCount() {
		return rowHeaderDataProvider.getColumnCount();
	}

	public int getRowCount() {
		return columnHeaderDataProvider.getRowCount();
	}

	public Object getDataValue(int columnIndex, int rowIndex) {
		return ""; //$NON-NLS-1$
	}
	
	public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
		throw new UnsupportedOperationException();
	}

}
