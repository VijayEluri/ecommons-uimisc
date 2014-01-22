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
package org.eclipse.nebula.widgets.nattable.selection.event;

import static org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.NO_SELECTION;

import java.util.Collection;

import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.coordinate.RangeList;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.event.ColumnVisualChangeEvent;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;


public class ColumnSelectionEvent extends ColumnVisualChangeEvent implements ISelectionEvent {
	
	
	private final SelectionLayer selectionLayer;
	
	private long columnPositionToReveal;
	
	
	public ColumnSelectionEvent(final SelectionLayer selectionLayer,
			final long columnPosition, final boolean revealColumn) {
		this(selectionLayer, new RangeList(columnPosition),
				(revealColumn) ? columnPosition : NO_SELECTION );
	}
	
	public ColumnSelectionEvent(final SelectionLayer selectionLayer,
			final Collection<Range> columnPositions, final long columnPositionToReveal) {
		super(selectionLayer, columnPositions);
		this.selectionLayer = selectionLayer;
		this.columnPositionToReveal = columnPositionToReveal;
	}
	
	protected ColumnSelectionEvent(ColumnSelectionEvent event) {
		super(event);
		this.selectionLayer = event.selectionLayer;
		this.columnPositionToReveal = event.columnPositionToReveal;
	}
	
	@Override
	public ColumnSelectionEvent cloneEvent() {
		return new ColumnSelectionEvent(this);
	}
	
	
	@Override
	public SelectionLayer getSelectionLayer() {
		return selectionLayer;
	}
	
	public long getColumnPositionToReveal() {
		return columnPositionToReveal;
	}
	
	@Override
	public boolean convertToLocal(ILayer localLayer) {
		if (columnPositionToReveal != NO_SELECTION) {
			columnPositionToReveal = localLayer.underlyingToLocalColumnPosition(getLayer(), columnPositionToReveal);
		}
		
		return super.convertToLocal(localLayer);
	}
	
}
