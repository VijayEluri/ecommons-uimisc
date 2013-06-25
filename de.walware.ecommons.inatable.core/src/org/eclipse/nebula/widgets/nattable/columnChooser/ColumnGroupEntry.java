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
package org.eclipse.nebula.widgets.nattable.columnChooser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.TreeItem;

import org.eclipse.nebula.widgets.nattable.columnChooser.gui.ColumnChooserDialog;


/**
 * Object representation of a ColumnGroup in the SWT tree.
 * NOTE: this is set as the SWT data on the {@link TreeItem}.
 *
 * @see ColumnChooserDialog#populateModel
 */
public class ColumnGroupEntry {
	private final String label;
	private final Integer firstElementPosition;
	private final Integer firstElementIndex;
	private final boolean isCollapsed;

	public ColumnGroupEntry(String label, Integer firstElementPosition, Integer firstElementIndex, boolean isCollapsed) {
		super();
		this.label = label;
		this.firstElementPosition = firstElementPosition;
		this.firstElementIndex = firstElementIndex;
		this.isCollapsed = isCollapsed;
	}

	public String getLabel() {
		return label;
	}

	public Integer getFirstElementPosition() {
		return firstElementPosition;
	}

	public Integer getFirstElementIndex() {
		return firstElementIndex;
	}

	public boolean isCollapsed() {
		return isCollapsed;
	}

	public static List<Integer> getColumnGroupEntryPositions(List<ColumnGroupEntry> columnEntries) {
		List<Integer> columnGroupEntryPositions = new ArrayList<Integer>();
		for (ColumnGroupEntry ColumnGroupEntry : columnEntries) {
			columnGroupEntryPositions.add(ColumnGroupEntry.getFirstElementPosition());
		}
		return columnGroupEntryPositions;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "["+ //$NON-NLS-1$
				 "Label: " + label + //$NON-NLS-1$
				 ", firstElementPosition: " + firstElementPosition + //$NON-NLS-1$
				 ", firstElementIndex: " + firstElementIndex + //$NON-NLS-1$
				 ", collapsed: " + isCollapsed + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

}
