/*******************************************************************************
 * Copyright (c) 2012-2013 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
// -depend
package org.eclipse.nebula.widgets.nattable.data.convert;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.eclipse.nebula.widgets.nattable.Messages;
import org.eclipse.nebula.widgets.nattable.internal.NatTablePlugin;


/**
 * Converts a java.util.Date object to a given format and vice versa
 */
public class DefaultDateDisplayConverter extends DisplayConverter {

	private SimpleDateFormat dateFormat;

	/**
	 * Convert {@link Date} to {@link String} using the default format from {@link SimpleDateFormat}
	 */
	public DefaultDateDisplayConverter() {
		this(null, null);
	}
	
	public DefaultDateDisplayConverter(TimeZone timeZone) {
		this(null, timeZone);
	}

	/**
	 * @param dateFormat as specified in {@link SimpleDateFormat}
	 */
	public DefaultDateDisplayConverter(String dateFormat) {
		this(dateFormat, null);
	}
	
	public DefaultDateDisplayConverter(String dateFormat, TimeZone timeZone) {
		if (dateFormat != null) {
			this.dateFormat = new SimpleDateFormat(dateFormat);
		} else {
			this.dateFormat = new SimpleDateFormat();
		}
		
		if (timeZone != null) {
			this.dateFormat.setTimeZone(timeZone);
		}
	}

	public Object canonicalToDisplayValue(Object canonicalValue) {
		try {
			if (canonicalValue != null) {
				return dateFormat.format(canonicalValue);
			}
		} catch (IllegalArgumentException e) {
			NatTablePlugin.log(new Status(IStatus.WARNING, NatTablePlugin.PLUGIN_ID,
					"Invalid date value", e ));
		}
		return ""; //$NON-NLS-1$
	}

	public Object displayToCanonicalValue(Object displayValue) {
		try {
			return dateFormat.parse(displayValue.toString());
		} catch (Exception e) {
			throw new ConversionFailedException(
					Messages.getString("DefaultDateDisplayConverter.failure", //$NON-NLS-1$
							new Object[] {displayValue, dateFormat.toPattern()}), e);
		}
	}

}
