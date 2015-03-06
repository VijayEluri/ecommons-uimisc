/*******************************************************************************
 * Copyright (c) 2012-2015 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.freeze.event;

import java.util.Collection;

import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.freeze.FreezeLayer;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEventHandler;
import org.eclipse.nebula.widgets.nattable.layer.event.IStructuralChangeEvent;
import org.eclipse.nebula.widgets.nattable.layer.event.StructuralDiff;


public class FreezeEventHandler implements ILayerEventHandler<IStructuralChangeEvent> {

	private final FreezeLayer freezeLayer;
	
	public FreezeEventHandler(FreezeLayer freezeLayer) {
		this.freezeLayer = freezeLayer;
	}

	public Class<IStructuralChangeEvent> getLayerEventClass() {
		return IStructuralChangeEvent.class;
	}

	public void handleLayerEvent(IStructuralChangeEvent event) {
		PositionCoordinate topLeftPosition = freezeLayer.getTopLeftPosition();
		PositionCoordinate bottomRightPosition = freezeLayer.getBottomRightPosition();
		
		// The handling of diffs have to be in sync with ViewportDim#handleStructuralChanges
		Collection<StructuralDiff> columnDiffs = event.getColumnDiffs();
		if (columnDiffs != null) {
			int leftOffset = 0;
			int rightOffset = 0;
			int freezeMove = 0; // 0 = unset, 1 == true, -1 == false
			
			for (StructuralDiff diff : columnDiffs) {
				final long start = diff.getBeforePositionRange().start;
				switch (diff.getDiffType()) {
				case ADD:
					if (start < topLeftPosition.columnPosition) {
						leftOffset += diff.getAfterPositionRange().size();
					}
					if (start <= bottomRightPosition.columnPosition
							|| (freezeMove == 1 && start == bottomRightPosition.columnPosition + 1) ) {
						rightOffset += diff.getAfterPositionRange().size();
					}
					continue;
				case DELETE:
					if (start < topLeftPosition.columnPosition) {
						leftOffset -= Math.min(diff.getBeforePositionRange().end, topLeftPosition.columnPosition + 1) - start;
					}
					if (start <= bottomRightPosition.columnPosition) {
						rightOffset -= Math.min(diff.getBeforePositionRange().end, bottomRightPosition.columnPosition + 1) - start;
						if (freezeMove == 0) {
							freezeMove = 1;
						}
					}
					else {
						freezeMove = -1;
					}
					continue;
				default:
					continue;
				}
			}
			
			topLeftPosition.columnPosition += leftOffset;
			bottomRightPosition.columnPosition += rightOffset;
		}
		
		Collection<StructuralDiff> rowDiffs = event.getRowDiffs();
		if (rowDiffs != null) {
			int leftOffset = 0;
			int rightOffset = 0;
			int freezeMove = 0; // 0 = unset, 1 == true, -1 == false
			
			for (StructuralDiff diff : rowDiffs) {
				final long start = diff.getBeforePositionRange().start;
				switch (diff.getDiffType()) {
				case ADD:
					if (start < topLeftPosition.rowPosition) {
						leftOffset += diff.getAfterPositionRange().size();
					}
					if (start <= bottomRightPosition.rowPosition
							|| (freezeMove == 1 && start == bottomRightPosition.rowPosition + 1) ) {
						rightOffset += diff.getAfterPositionRange().size();
					}
					continue;
				case DELETE:
					if (start < topLeftPosition.rowPosition) {
						leftOffset -= Math.min(diff.getBeforePositionRange().end, topLeftPosition.rowPosition + 1) - start;
					}
					if (start <= bottomRightPosition.rowPosition) {
						rightOffset -= Math.min(diff.getBeforePositionRange().end, bottomRightPosition.rowPosition + 1) - start;
						if (freezeMove == 0) {
							freezeMove = 1;
						}
					}
					else {
						freezeMove = -1;
					}
					continue;
				default:
					continue;
				}
			}
			
			topLeftPosition.rowPosition += leftOffset;
			bottomRightPosition.rowPosition += rightOffset;
		}
	}
	
}
