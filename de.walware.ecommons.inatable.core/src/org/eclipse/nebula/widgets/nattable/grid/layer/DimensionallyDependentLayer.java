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
package org.eclipse.nebula.widgets.nattable.grid.layer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;

import org.eclipse.nebula.widgets.nattable.command.ILayerCommand;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.layer.cell.TranslatedLayerCell;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.layer.event.IStructuralChangeEvent;
import org.eclipse.nebula.widgets.nattable.painter.layer.ILayerPainter;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.util.IClientAreaProvider;


/**
 * <p>
 * A DimensionallyDependentLayer is a layer whose horizontal and vertical dimensions are dependent on the horizontal and
 * vertical dimensions of other layers. A DimensionallyDependentLayer takes three constructor parameters: the horizontal
 * layer that the DimensionallyDependentLayer's horizontal dimension is linked to, the vertical layer that the
 * DimensionallyDependentLayer is linked to, and a base layer to which all non-dimensionally related ILayer method calls
 * will be delegated to (e.g. command, event methods)
 * </p>
 * <p>
 * Prime examples of dimensionally dependent layers are the column header and row header layers. For example, the column
 * header layer's horizontal dimension is linked to the body layer's horizontal dimension. This means that whatever
 * columns are shown in the body area will also be shown in the column header area, and vice versa. Note that the column
 * header layer maintains its own vertical dimension, however, so it's vertical layer dependency would be a separate
 * data layer. The same is true for the row header layer, only with the vertical instead of the horizontal dimension.
 * The constructors for the column header and row header layers would therefore look something like this:
 * </p>
 * <pre>
 * ILayer columnHeaderLayer = new DimensionallyDependentLayer(columnHeaderRowDataLayer, bodyLayer, columnHeaderRowDataLayer);
 * ILayer rowHeaderLayer = new DimensionallyDependentLayer(rowHeaderColumnDataLayer, bodyLayer, rowHeaderColumnDataLayer);
 * </pre>
 */
public class DimensionallyDependentLayer extends AbstractLayer implements IUniqueIndexLayer {

	private final IUniqueIndexLayer baseLayer;
	private IUniqueIndexLayer horizontalLayerDependency;
	private IUniqueIndexLayer verticalLayerDependency;
	private IClientAreaProvider clientAreaProvider;

	public DimensionallyDependentLayer(IUniqueIndexLayer baseLayer, IUniqueIndexLayer horizontalLayerDependency, IUniqueIndexLayer verticalLayerDependency) {
		this(baseLayer);
		
		init(horizontalLayerDependency, verticalLayerDependency);
	}
	
	protected DimensionallyDependentLayer(IUniqueIndexLayer baseLayer) {
		this.baseLayer = baseLayer;
	}
	
	protected void init(IUniqueIndexLayer horizontalLayerDependency, IUniqueIndexLayer verticalLayerDependency) {
		this.horizontalLayerDependency = horizontalLayerDependency;
		this.verticalLayerDependency = verticalLayerDependency;

		baseLayer.addLayerListener(this);
		horizontalLayerDependency.addLayerListener(new ILayerListener() {

			public void handleLayerEvent(ILayerEvent event) {
				if (event instanceof IStructuralChangeEvent) {
					// TODO refresh horizontal structure
				}
			}

		});
		verticalLayerDependency.addLayerListener(new ILayerListener() {

			public void handleLayerEvent(ILayerEvent event) {
				if (event instanceof IStructuralChangeEvent) {
					// TODO refresh vertical structure
				}
			}

		});
	}

	// Persistence

	@Override
	public void saveState(String prefix, Properties properties) {
		super.saveState(prefix, properties);
		baseLayer.saveState(prefix, properties);
	}

	@Override
	public void loadState(String prefix, Properties properties) {
		super.loadState(prefix, properties);
		baseLayer.loadState(prefix, properties);
	}

	// Configuration
	
	@Override
	public void configure(ConfigRegistry configRegistry, UiBindingRegistry uiBindingRegistry) {
		baseLayer.configure(configRegistry, uiBindingRegistry);
		super.configure(configRegistry, uiBindingRegistry);
	}

	// Dependent layer accessors

//	public void setHorizontalLayerDependency(ILayer horizontalLayerDependency) {
//		this.horizontalLayerDependency = horizontalLayerDependency;
//	}
//
//	public void setVerticalLayerDependency(ILayer verticalLayerDependency) {
//		this.verticalLayerDependency = verticalLayerDependency;
//	}

	public ILayer getHorizontalLayerDependency() {
		return horizontalLayerDependency;
	}

	public ILayer getVerticalLayerDependency() {
		return verticalLayerDependency;
	}

	public IUniqueIndexLayer getBaseLayer() {
		return baseLayer;
	}

	// Commands
	
	@Override
	public boolean doCommand(ILayerCommand command) {
		// Invoke command handler(s) on the Dimensionally dependent layer
		ILayerCommand clonedCommand = command.cloneCommand();
		if (super.doCommand(command)) {
			return true;
		}
		
		clonedCommand = command.cloneCommand();
		if (horizontalLayerDependency.doCommand(clonedCommand)) {
			return true;
		}

		clonedCommand = command.cloneCommand();
		if (verticalLayerDependency.doCommand(clonedCommand)) {
			return true;
		}

		return baseLayer.doCommand(command);
	}

	// Events

	@Override
	public ILayerPainter getLayerPainter() {
		return (layerPainter != null) ? layerPainter : baseLayer.getLayerPainter();
	}

	// Horizontal features

	// Columns

	public int getColumnCount() {
		return horizontalLayerDependency.getColumnCount();
	}

	public int getPreferredColumnCount() {
		return horizontalLayerDependency.getPreferredColumnCount();
	}

	public int getColumnIndexByPosition(int columnPosition) {
		return horizontalLayerDependency.getColumnIndexByPosition(columnPosition);
	}

	@Override
	public int getColumnPositionByIndex(int columnIndex) {
		return horizontalLayerDependency.getColumnPositionByIndex(columnIndex);
	}

	public int localToUnderlyingColumnPosition(int localColumnPosition) {
		return LayerUtil.convertColumnPosition(this, localColumnPosition, baseLayer);
	}

	public int underlyingToLocalColumnPosition(ILayer sourceUnderlyingLayer, int underlyingColumnPosition) {
		return LayerUtil.convertColumnPosition(sourceUnderlyingLayer, underlyingColumnPosition, this);
	}
	
	public Collection<Range> underlyingToLocalColumnPositions(ILayer sourceUnderlyingLayer, Collection<Range> underlyingColumnPositionRanges) {
		return horizontalLayerDependency.underlyingToLocalColumnPositions(sourceUnderlyingLayer, underlyingColumnPositionRanges);
	}

	// Width

	public int getWidth() {
		return horizontalLayerDependency.getWidth();
	}

	public int getPreferredWidth() {
		return horizontalLayerDependency.getPreferredWidth();
	}

	public int getColumnWidthByPosition(int columnPosition) {
		return horizontalLayerDependency.getColumnWidthByPosition(columnPosition);
	}

	// Column resize

	public boolean isColumnPositionResizable(int columnPosition) {
		return horizontalLayerDependency.isColumnPositionResizable(columnPosition);
	}

	// X

	public int getColumnPositionByX(int x) {
		return horizontalLayerDependency.getColumnPositionByX(x);
	}

	public int getStartXOfColumnPosition(int columnPosition) {
		return horizontalLayerDependency.getStartXOfColumnPosition(columnPosition);
	}

	// Underlying

	public Collection<ILayer> getUnderlyingLayersByColumnPosition(int columnPosition) {
		Collection<ILayer> underlyingLayers = new HashSet<ILayer>();
		underlyingLayers.add(baseLayer);
		return underlyingLayers;
	}

	// Vertical features

	// Rows

	public int getRowCount() {
		return verticalLayerDependency.getRowCount();
	}

	public int getPreferredRowCount() {
		return verticalLayerDependency.getPreferredRowCount();
	}

	public int getRowIndexByPosition(int rowPosition) {
		return verticalLayerDependency.getRowIndexByPosition(rowPosition);
	}

	@Override
	public int getRowPositionByIndex(int rowIndex) {
		return verticalLayerDependency.getRowPositionByIndex(rowIndex);
	}

	public int localToUnderlyingRowPosition(int localRowPosition) {
		return LayerUtil.convertRowPosition(this, localRowPosition, baseLayer);
	}

	public int underlyingToLocalRowPosition(ILayer sourceUnderlyingLayer, int underlyingRowPosition) {
		return LayerUtil.convertRowPosition(sourceUnderlyingLayer, underlyingRowPosition, this);
	}

	public Collection<Range> underlyingToLocalRowPositions(ILayer sourceUnderlyingLayer, Collection<Range> underlyingRowPositionRanges) {
		return verticalLayerDependency.underlyingToLocalRowPositions(sourceUnderlyingLayer, underlyingRowPositionRanges);
	}

	// Height

	public int getHeight() {
		return verticalLayerDependency.getHeight();
	}

	public int getPreferredHeight() {
		return verticalLayerDependency.getPreferredHeight();
	}

	public int getRowHeightByPosition(int rowPosition) {
		return verticalLayerDependency.getRowHeightByPosition(rowPosition);
	}

	// Row resize

	public boolean isRowPositionResizable(int rowPosition) {
		return verticalLayerDependency.isRowPositionResizable(rowPosition);
	}

	// Y

	public int getRowPositionByY(int y) {
		return verticalLayerDependency.getRowPositionByY(y);
	}

	public int getStartYOfRowPosition(int rowPosition) {
		return verticalLayerDependency.getStartYOfRowPosition(rowPosition);
	}

	// Underlying

	public Collection<ILayer> getUnderlyingLayersByRowPosition(int rowPosition) {
		Collection<ILayer> underlyingLayers = new HashSet<ILayer>();
		underlyingLayers.add(baseLayer);
		return underlyingLayers;
	}

	// Cell features

	@Override
	public ILayerCell getCellByPosition(int columnPosition, int rowPosition) {
		ILayerCell cell = baseLayer.getCellByPosition(
				localToUnderlyingColumnPosition(columnPosition),
				localToUnderlyingRowPosition(rowPosition));
		if (cell == null) {
			return null;
		}
		return new TranslatedLayerCell(cell, this,
				underlyingToLocalColumnPosition(baseLayer, cell.getOriginColumnPosition()),
				underlyingToLocalRowPosition(baseLayer, cell.getOriginRowPosition()),
				underlyingToLocalColumnPosition(baseLayer, cell.getColumnPosition()),
				underlyingToLocalRowPosition(baseLayer, cell.getRowPosition()) );
	}

	@Override
	public String getDisplayModeByPosition(int columnPosition, int rowPosition) {
		int baseColumnPosition = LayerUtil.convertColumnPosition(this, columnPosition, baseLayer);
		int baseRowPosition = LayerUtil.convertRowPosition(this, rowPosition, baseLayer);
		return baseLayer.getDisplayModeByPosition(baseColumnPosition, baseRowPosition);
	}

	@Override
	public LabelStack getConfigLabelsByPosition(int columnPosition, int rowPosition) {
		int baseColumnPosition = LayerUtil.convertColumnPosition(this, columnPosition, baseLayer);
		int baseRowPosition = LayerUtil.convertRowPosition(this, rowPosition, baseLayer);
		return baseLayer.getConfigLabelsByPosition(baseColumnPosition, baseRowPosition);
	}

	public Object getDataValueByPosition(int columnPosition, int rowPosition) {
		int baseColumnPosition = LayerUtil.convertColumnPosition(this, columnPosition, baseLayer);
		int baseRowPosition = LayerUtil.convertRowPosition(this, rowPosition, baseLayer);
		return baseLayer.getDataValueByPosition(baseColumnPosition, baseRowPosition);
	}

	// IRegionResolver

	@Override
	public LabelStack getRegionLabelsByXY(int x, int y) {
		return baseLayer.getRegionLabelsByXY(x, y);
	}

	@Override
	public IClientAreaProvider getClientAreaProvider() {
		return clientAreaProvider;
	}

	@Override
	public void setClientAreaProvider(IClientAreaProvider clientAreaProvider) {
		this.clientAreaProvider = clientAreaProvider;
	}

	public ILayer getUnderlyingLayerByPosition(int columnPosition, int rowPosition) {
		return baseLayer;
	}

}
