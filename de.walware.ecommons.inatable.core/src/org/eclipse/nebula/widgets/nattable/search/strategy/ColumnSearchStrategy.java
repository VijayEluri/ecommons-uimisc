/*******************************************************************************
 * Copyright (c) 2012 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.search.strategy;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.search.ISearchDirection;


public class ColumnSearchStrategy extends AbstractSearchStrategy {

	private long[] columnPositions;
	private long startingRowPosition;
	private final String searchDirection;
	private final IConfigRegistry configRegistry;

	public ColumnSearchStrategy(long[] columnPositions, IConfigRegistry configRegistry) {
		this(columnPositions, 0, configRegistry, ISearchDirection.SEARCH_FORWARD);
	}
	
	public ColumnSearchStrategy(long[] columnPositions, long startingRowPosition, IConfigRegistry configRegistry, String searchDirection) {
		this.columnPositions = columnPositions;
		this.startingRowPosition = startingRowPosition;
		this.configRegistry = configRegistry;
		this.searchDirection = searchDirection;
	}
	
	public PositionCoordinate executeSearch(Object valueToMatch) {
		return CellDisplayValueSearchUtil.findCell(getContextLayer(), configRegistry, getColumnCellsToSearch(getContextLayer()), valueToMatch, getComparator(), isCaseSensitive());
	}
	
	public void setStartingRowPosition(long startingRowPosition) {
		this.startingRowPosition = startingRowPosition;
	}
	
	public void setColumnPositions(long[] columnPositions) {
		this.columnPositions = columnPositions;
	}
	
	protected List<PositionCoordinate> getColumnCellsToSearch(ILayer contextLayer) {
		List<PositionCoordinate> cellsToSearch = new ArrayList<PositionCoordinate>();
		long rowPosition = startingRowPosition;
		// See how many rows we can add, depends on where the search is starting from
		final long rowCount = contextLayer.getRowCount();
		long height = rowCount;
		if (searchDirection.equals(ISearchDirection.SEARCH_FORWARD)) {
			height = height - startingRowPosition;
		} else {
			height = startingRowPosition;
		}
		for (int columnIndex = 0; columnIndex < columnPositions.length; columnIndex++) {
			final long startingColumnPosition = columnPositions[columnIndex];
			if (searchDirection.equals(ISearchDirection.SEARCH_BACKWARDS)) {
				cellsToSearch.addAll(CellDisplayValueSearchUtil.getDescendingCellCoordinates(getContextLayer(), startingColumnPosition, rowPosition, 1, height));
				rowPosition = rowCount - 1;
			} else {
				cellsToSearch.addAll(CellDisplayValueSearchUtil.getCellCoordinates(getContextLayer(), startingColumnPosition, rowPosition, 1, height));
				rowPosition = 0;
			}
			height = rowCount;
			// After first column is set, start the next column from the top
		}
		return cellsToSearch;
	}
}
