/*******************************************************************************
 * Copyright (c) 2013 Stephan Wahlbrink and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.nattable.layer;

import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.HORIZONTAL;
import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.VERTICAL;


public class AbstractTransformIndexLayer extends AbstractTransformLayer
		implements IUniqueIndexLayer {
	
	
	public AbstractTransformIndexLayer(final IUniqueIndexLayer underlyingLayer) {
		super(underlyingLayer);
	}
	
	protected AbstractTransformIndexLayer() {
		super();
	}
	
	
	@Override
	protected void setUnderlyingLayer(final ILayer underlyingLayer) {
		if (!(underlyingLayer instanceof IUniqueIndexLayer)) {
			throw new IllegalArgumentException("underlyingLayer"); //$NON-NLS-1$
		}
		super.setUnderlyingLayer(underlyingLayer);
	}
	
	
	@Override
	public int getColumnPositionByIndex(final int columnIndex) {
		final IUniqueIndexLayer underlyingLayer = (IUniqueIndexLayer) getUnderlyingLayer();
		final int underlyingPosition = underlyingLayer.getColumnPositionByIndex(columnIndex);
		if (underlyingPosition == Integer.MIN_VALUE) {
			return Integer.MIN_VALUE;
		}
		return getDim(HORIZONTAL).underlyingToLocalPosition(underlyingLayer,
				underlyingPosition );
	}
	
	@Override
	public int getRowPositionByIndex(final int rowIndex) {
		final IUniqueIndexLayer underlyingLayer = (IUniqueIndexLayer) getUnderlyingLayer();
		final int underlyingPosition = underlyingLayer.getRowPositionByIndex(rowIndex);
		if (underlyingPosition == Integer.MIN_VALUE) {
			return Integer.MIN_VALUE;
		}
		return getDim(VERTICAL).underlyingToLocalPosition(underlyingLayer,
				underlyingPosition );
	}
	
}
