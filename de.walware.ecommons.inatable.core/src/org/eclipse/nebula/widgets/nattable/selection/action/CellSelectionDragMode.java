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
package org.eclipse.nebula.widgets.nattable.selection.action;

import static org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.RANGE_SELECTION;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectCellCommand;
import org.eclipse.nebula.widgets.nattable.ui.action.IDragMode;


/**
 * Fires commands to select a range of cells when the mouse is dragged in the viewport.
 */
public class CellSelectionDragMode implements IDragMode {


	private Point lastDragInCellPosition = null;


	public CellSelectionDragMode() {
	}


	public void mouseDown(NatTable natTable, MouseEvent event) {
		natTable.forceFocus();

		fireSelectionCommand(natTable,
				natTable.getColumnPositionByX(event.x),
				natTable.getRowPositionByY(event.y),
				event.stateMask & (SWT.SHIFT | SWT.CTRL) );
	}

	public void mouseMove(NatTable natTable, MouseEvent event) {
		if (event.x > natTable.getWidth()) {
			return;
		}
		int selectedColumnPosition = natTable.getColumnPositionByX(event.x);
		int selectedRowPosition = natTable.getRowPositionByY(event.y);

		if (selectedColumnPosition > -1 && selectedRowPosition > -1) {
			Point dragInCellPosition = new Point(selectedColumnPosition, selectedRowPosition);
			if(lastDragInCellPosition == null || !dragInCellPosition.equals(lastDragInCellPosition)){
				lastDragInCellPosition = dragInCellPosition;

				fireSelectionCommand(natTable, selectedColumnPosition, selectedRowPosition, RANGE_SELECTION);
			}
		}
	}

	protected void fireSelectionCommand(NatTable natTable, int columnPosition,	int rowPosition, int selectionFlags) {
		natTable.doCommand(new SelectCellCommand(natTable, columnPosition, rowPosition, selectionFlags ));
	}

	public void mouseUp(NatTable natTable, MouseEvent event) {
		endDrag();
	}

	private void endDrag(){
		lastDragInCellPosition = null;
	}

}
