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
// ~Selection
package org.eclipse.nebula.widgets.nattable.selection.command;

import static org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.NO_SELECTION;

import java.util.Collection;

import org.eclipse.nebula.widgets.nattable.command.AbstractMultiColumnCommand;
import org.eclipse.nebula.widgets.nattable.command.LayerCommandUtil;
import org.eclipse.nebula.widgets.nattable.coordinate.ColumnPositionCoordinate;
import org.eclipse.nebula.widgets.nattable.coordinate.RowPositionCoordinate;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;


public class SelectColumnsCommand extends AbstractMultiColumnCommand {


	private final int selectionFlags;

	private RowPositionCoordinate rowPositionCoordinate;

	private ColumnPositionCoordinate columnPositionToReveal;


	public SelectColumnsCommand(final ILayer layer, final int columnPosition, final int rowPosition,
			final int selectionFlags) {
		super(layer, columnPosition);
		
		this.selectionFlags = selectionFlags;
		init(layer, rowPosition, columnPosition);
	}

	public SelectColumnsCommand(final ILayer layer, final Collection<Integer> columnPositions, final int rowPosition,
			final int selectionFlags) {
		this(layer, columnPositions, rowPosition, selectionFlags, columnPositions.iterator().next());
	}

	public SelectColumnsCommand(final ILayer layer, final Collection<Integer> columnPositions, final int rowPosition,
			final int selectionFlags, int columnPositionToReveal) {
		super(layer, columnPositions);
		
		this.selectionFlags = selectionFlags;
		init(layer, rowPosition, columnPositionToReveal);
	}

	private void init(final ILayer layer, final int rowPosition, final int columnPositionToReveal) {
		this.rowPositionCoordinate = new RowPositionCoordinate(layer, rowPosition);
		if (columnPositionToReveal != NO_SELECTION) {
			this.columnPositionToReveal = new ColumnPositionCoordinate(layer, columnPositionToReveal);
		}
	}

	protected SelectColumnsCommand(SelectColumnsCommand command) {
		super(command);
		
		this.selectionFlags = command.selectionFlags;
		this.rowPositionCoordinate = command.rowPositionCoordinate;
		this.columnPositionToReveal = command.columnPositionToReveal;
	}

	public SelectColumnsCommand cloneCommand() {
		return new SelectColumnsCommand(this);
	}


	@Override
	public boolean convertToTargetLayer(ILayer targetLayer) {
		if (super.convertToTargetLayer(targetLayer)) {
			
			rowPositionCoordinate = LayerCommandUtil.convertRowPositionToTargetContext(
					rowPositionCoordinate, targetLayer );
			columnPositionToReveal = LayerCommandUtil.convertColumnPositionToTargetContext(
					columnPositionToReveal, targetLayer );
			
			return (rowPositionCoordinate != null);
		}
		return false;
	}


	public int getRowPosition() {
		return rowPositionCoordinate.rowPosition;
	}

	public int getSelectionFlags() {
		return selectionFlags;
	}

	public int getColumnPositionToReveal() {
		if (columnPositionToReveal != null) {
			return columnPositionToReveal.columnPosition;
		} else {
			return NO_SELECTION;
		}
	}

}