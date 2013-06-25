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
// -depend
package org.eclipse.nebula.widgets.nattable.data.validate;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;


public abstract class ContextualDataValidator implements IDataValidator {

	public boolean validate(int columnIndex, int rowIndex, Object newValue) {
		throw new UnsupportedOperationException(this.getClass().getName() 
				+ " is a ContextualDataValidator and has therefore to be called with context informations."); //$NON-NLS-1$
	}

	public abstract boolean validate(ILayerCell cell, IConfigRegistry configRegistry, Object newValue);

}
