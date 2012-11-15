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
package org.eclipse.nebula.widgets.nattable.command;

import org.eclipse.nebula.widgets.nattable.coordinate.RowPositionCoordinate;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;

public abstract class AbstractRowCommand implements ILayerCommand {

	private RowPositionCoordinate rowPositionCoordinate;
	
	protected AbstractRowCommand(ILayer layer, int rowPosition) {
		rowPositionCoordinate = new RowPositionCoordinate(layer, rowPosition);
	}
	
	protected AbstractRowCommand(AbstractRowCommand command) {
		this.rowPositionCoordinate = command.rowPositionCoordinate;
	}
	
	public boolean convertToTargetLayer(ILayer targetLayer) {
		rowPositionCoordinate = LayerCommandUtil.convertRowPositionToTargetContext(rowPositionCoordinate, targetLayer);
		return rowPositionCoordinate != null;
	}
	
	public int getRowPosition() {
		return rowPositionCoordinate.getRowPosition();
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " rowPosition=" + rowPositionCoordinate.getRowPosition(); //$NON-NLS-1$
	}

}
