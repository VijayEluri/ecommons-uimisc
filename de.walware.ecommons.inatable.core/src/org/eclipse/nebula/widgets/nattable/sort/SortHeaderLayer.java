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
// ~
package org.eclipse.nebula.widgets.nattable.sort;

import org.eclipse.nebula.widgets.nattable.layer.AbstractIndexLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.persistence.IPersistable;
import org.eclipse.nebula.widgets.nattable.sort.command.ClearSortCommandHandler;
import org.eclipse.nebula.widgets.nattable.sort.command.SortColumnCommandHandler;
import org.eclipse.nebula.widgets.nattable.sort.config.DefaultSortConfiguration;

/**
 * Enables sorting of the data. Uses an {@link ISortModel} to do/track the sorting.
 * @param <T> Type of the Beans in the backing data source.
 *
 * @see DefaultSortConfiguration
 * @see SortStatePersistor
 */
public class SortHeaderLayer<T> extends AbstractIndexLayerTransform implements IPersistable {

	/** Handles the actual sorting of underlying data */
	private final ISortModel sortModel;

	public SortHeaderLayer(IUniqueIndexLayer underlyingLayer, ISortModel sortModel) {
		this(underlyingLayer, sortModel, true);
	}

	public SortHeaderLayer(IUniqueIndexLayer underlyingLayer, ISortModel sortModel, boolean useDefaultConfiguration) {
		super(underlyingLayer);
		this.sortModel = sortModel;
		
		registerPersistable(new SortStatePersistor<T>(sortModel));
		registerCommandHandler(new SortColumnCommandHandler(sortModel, this));
		registerCommandHandler(new ClearSortCommandHandler(sortModel, this));
		
		if (useDefaultConfiguration) {
			addConfiguration(new DefaultSortConfiguration());
		}
	}

	/**
	 * @return adds a special configuration label to the stack taking into account the following:<br/>
	 * 	<ol>
	 * 		<li>Is the column sorted ?</li>
	 * 		<li>What is the sort order of the column</li>
	 * 	</ol>
	 * A special painter is registered against the above labels to render the sort arrows
	 */
	@Override
	public LabelStack getConfigLabelsByPosition(int columnPosition, int rowPosition) {
		LabelStack configLabels = super.getConfigLabelsByPosition(columnPosition, rowPosition);

		if (sortModel != null) {
			int columnIndex = getColumnIndexByPosition(columnPosition);
			if (sortModel.isColumnIndexSorted(columnIndex)) {
				SortDirectionEnum sortDirection = sortModel.getSortDirection(columnIndex);

				switch (sortDirection) {
				case ASC:
					configLabels.addLabel(DefaultSortConfiguration.SORT_UP_CONFIG_TYPE);
					break;
				case DESC:
					configLabels.addLabel(DefaultSortConfiguration.SORT_DOWN_CONFIG_TYPE);
					break;
				}
				String sortConfig = DefaultSortConfiguration.SORT_SEQ_CONFIG_TYPE + sortModel.getSortOrder(columnIndex);
				configLabels.addLabel(sortConfig);
			}
		}
		return configLabels;
	}
	
	protected ISortModel getSortModel() {
		return sortModel;
	}

}
