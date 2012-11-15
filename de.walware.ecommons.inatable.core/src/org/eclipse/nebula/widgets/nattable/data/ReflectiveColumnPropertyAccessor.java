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
// -depend
package org.eclipse.nebula.widgets.nattable.data;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Convenience class which uses java reflection to get/set property names
 *  from the row bean. It looks for getter methods for reading and setter
 *  methods for writing according to the Java conventions.
 *
 * @param <R> type of the row object/bean
 */
public class ReflectiveColumnPropertyAccessor<R> implements IColumnPropertyAccessor<R> {

	private final List<String> propertyNames;

	private Map<String, PropertyDescriptor> propertyDescriptorMap;

	/**
	 * @param propertyNames of the members of the row bean
	 */
	public ReflectiveColumnPropertyAccessor(String[] propertyNames) {
		this.propertyNames = Arrays.asList(propertyNames);
	}

	public int getColumnCount() {
		return propertyNames.size();
	}

	public Object getDataValue(R rowObj, int columnIndex) {
		try {
			PropertyDescriptor propertyDesc = getPropertyDescriptor(rowObj, columnIndex);
			Method readMethod = propertyDesc.getReadMethod();
			return readMethod.invoke(rowObj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setDataValue(R rowObj, int columnIndex, Object newValue) {
		try {
			PropertyDescriptor propertyDesc = getPropertyDescriptor(rowObj, columnIndex);
			Method writeMethod = propertyDesc.getWriteMethod();
			if (writeMethod == null) {
				throw new RuntimeException("Setter method not found in backing bean for value at column index: " + columnIndex); //$NON-NLS-1$
			}
			writeMethod.invoke(rowObj, newValue);
//		} catch (IllegalArgumentException ex) {
//			System.err.println("Data type being set does not match the data type of the setter method in the backing bean"); //$NON-NLS-1$
		} catch (Exception e) {
			throw new RuntimeException("Error while setting data value", e); //$NON-NLS-1$
		}
	};

	public String getColumnProperty(int columnIndex) {
		return propertyNames.get(columnIndex);
	}

	public int getColumnIndex(String propertyName) {
		return propertyNames.indexOf(propertyName);
	}

	private PropertyDescriptor getPropertyDescriptor(R rowObj, int columnIndex) throws IntrospectionException {
		if (propertyDescriptorMap == null) {
			propertyDescriptorMap = new HashMap<String, PropertyDescriptor>();
			PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(rowObj.getClass()).getPropertyDescriptors();
			for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
				propertyDescriptorMap.put(propertyDescriptor.getName(), propertyDescriptor);
			}
		}

		final String propertyName = propertyNames.get(columnIndex);
		return propertyDescriptorMap.get(propertyName);
	}

}
