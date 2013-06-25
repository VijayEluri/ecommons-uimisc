/*******************************************************************************
 * Copyright (c) 2012, 2013 Dirk Fauth and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Dirk Fauth <dirk.fauth@gmail.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.nattable.freeze;

import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.HORIZONTAL;
import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.VERTICAL;

import org.eclipse.nebula.widgets.nattable.coordinate.Orientation;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.freeze.FreezeLayer.Dim;
import org.eclipse.nebula.widgets.nattable.freeze.command.FreezeColumnCommand;
import org.eclipse.nebula.widgets.nattable.freeze.command.FreezePositionCommand;
import org.eclipse.nebula.widgets.nattable.freeze.command.FreezeRowCommand;
import org.eclipse.nebula.widgets.nattable.freeze.command.FreezeSelectionCommand;
import org.eclipse.nebula.widgets.nattable.freeze.command.UnFreezeGridCommand;
import org.eclipse.nebula.widgets.nattable.freeze.event.FreezeEvent;
import org.eclipse.nebula.widgets.nattable.freeze.event.UnfreezeEvent;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;


/**
 * Helper class to deal with freeze and unfreeze of a NatTable.
 * 
 * <p>This class is intended for internal use only. Consider using the
 * appropriate commands instead of using this helper directly.
 * 
 * @see FreezeColumnCommand
 * @see FreezeRowCommand
 * @see FreezePositionCommand
 * @see FreezeSelectionCommand
 * @see UnFreezeGridCommand
 */
public class FreezeHelper {
	
	/**
	 * Freezes the grid at the specified position.
	 * This method is for internal use. Consider using the appropriate commands
	 * on the NatTable instead to freeze the grid programmatically.
	 * 
	 * @param freezeLayer The FreezeLayer of the grid to perform the freeze action.
	 * @param viewportLayer The ViewportLayer of the grid to perform the freeze action.
	 * @param topLeftPosition The top left position of the freeze area
	 * @param bottomRightPosition The bottom right position of the freeze area
	 * 
	 * @see FreezeColumnCommand
	 * @see FreezeRowCommand
	 * @see FreezePositionCommand
	 * @see FreezeSelectionCommand
	 */
	public static void freeze(FreezeLayer freezeLayer, ViewportLayer viewportLayer, 
			PositionCoordinate topLeftPosition, PositionCoordinate bottomRightPosition) {
		
		if (freezeLayer == null || viewportLayer == null) {
			throw new IllegalArgumentException("freezeLayer and viewportLayer can not be null!"); //$NON-NLS-1$
		}
		
		if (topLeftPosition != null && bottomRightPosition != null) {
			freezeLayer.setFreeze(topLeftPosition.columnPosition, topLeftPosition.rowPosition,
					bottomRightPosition.columnPosition, bottomRightPosition.rowPosition );
			
			viewportLayer.getDim(HORIZONTAL).setMinimumOriginPosition(bottomRightPosition.columnPosition + 1);
			viewportLayer.getDim(VERTICAL).setMinimumOriginPosition(bottomRightPosition.rowPosition + 1);
			
			viewportLayer.fireLayerEvent(new FreezeEvent(viewportLayer));
		}
	}
	
	/**
	 * Unfreezes the grid at the specified position.
	 * This method is for internal use. Consider using the appropriate command
	 * on the NatTable instead to unfreeze the grid programmatically.
	 * 
	 * @param freezeLayer The FreezeLayer of the grid to perform the freeze action.
	 * @param viewportLayer The ViewportLayer of the grid to perform the freeze action.
	 * 
	 * @see UnFreezeGridCommand
	 */
	public static void unfreeze(FreezeLayer freezeLayer, ViewportLayer viewportLayer) {
		if (freezeLayer == null || viewportLayer == null) {
			throw new IllegalArgumentException("freezeLayer and viewportLayer can not be null!"); //$NON-NLS-1$
		}
		
		resetViewport(freezeLayer, viewportLayer);
		
		viewportLayer.fireLayerEvent(new UnfreezeEvent(viewportLayer));
	}
	
	/**
	 * Helper method to reset the origin coordinates of the viewport. Is needed to perform an 
	 * unfreeze or to override a current frozen state.
	 * @param freezeLayer The FreezeLayer of the grid to perform the freeze action.
	 * @param viewportLayer The ViewportLayer of the grid to perform the freeze action.
	 */
	public static void resetViewport(final FreezeLayer freezeLayer, final ViewportLayer viewportLayer) {
		for (final Orientation orientation : Orientation.values()) {
			if (freezeLayer.isFrozen()) {
				final Dim freezeDim = freezeLayer.get(orientation);
				
				final long position = freezeDim.getStartPosition();
				freezeDim.setFreeze(0, 0);
				viewportLayer.getDim(orientation).reset(position);
			}
		}
	}
	
}
