/*******************************************************************************
 * Copyright (c) 2013-2016 Stephan Wahlbrink and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 ******************************************************************************/

package de.walware.ecommons.waltable.layer.cell;

import de.walware.ecommons.waltable.coordinate.Orientation;


public interface ILayerCellDim {
	
	
	Orientation getOrientation();
	
	long getId();
	
	long getPosition();
	
	long getOriginPosition();
	long getPositionSpan();
	
}
