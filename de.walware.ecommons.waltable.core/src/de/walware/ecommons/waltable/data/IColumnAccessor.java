/*******************************************************************************
 * Copyright (c) 2012-2016 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package de.walware.ecommons.waltable.data;

/**
 * Maps the properties from the row object to the corresponding columns. 
 * @param <T> type of the bean used as a row object
 */
public interface IColumnAccessor<T> {

	public Object getDataValue(T rowObject, long columnIndex);
	
	public void setDataValue(T rowObject, long columnIndex, Object newValue);
	
	public long getColumnCount();
	
}
