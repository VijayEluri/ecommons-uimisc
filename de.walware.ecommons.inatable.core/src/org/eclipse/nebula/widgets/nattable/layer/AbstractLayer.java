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
package org.eclipse.nebula.widgets.nattable.layer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.swt.graphics.Rectangle;

import org.eclipse.nebula.widgets.nattable.command.ILayerCommand;
import org.eclipse.nebula.widgets.nattable.command.ILayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfiguration;
import org.eclipse.nebula.widgets.nattable.internal.LayerListenerList;
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.layer.cell.LayerCell;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEventHandler;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.layer.GridLineCellLayerPainter;
import org.eclipse.nebula.widgets.nattable.painter.layer.ILayerPainter;
import org.eclipse.nebula.widgets.nattable.persistence.IPersistable;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.util.IClientAreaProvider;


/**
 * Base layer implementation with common methods for managing listeners and caching, etc.
 */
public abstract class AbstractLayer implements ILayer {

	private String regionName;
	protected ILayerPainter layerPainter;
	private IClientAreaProvider clientAreaProvider = IClientAreaProvider.DEFAULT;
	private IConfigLabelAccumulator configLabelAccumulator;

	private final Map<Class<? extends ILayerCommand>, ILayerCommandHandler<? extends ILayerCommand>> commandHandlers = new LinkedHashMap<Class<? extends ILayerCommand>, ILayerCommandHandler<? extends ILayerCommand>>();
	private final Map<Class<? extends ILayerEvent>, ILayerEventHandler<? extends ILayerEvent>> eventHandlers = new HashMap<Class<? extends ILayerEvent>, ILayerEventHandler<? extends ILayerEvent>>();
	
	private final List<IPersistable> persistables = new LinkedList<IPersistable>();
	private final LayerListenerList listeners = new LayerListenerList();
	private final Collection<IConfiguration> configurations = new ArrayList<IConfiguration>();

	// Dispose

	public void dispose() {
	}
	
	// Regions
	
	public LabelStack getRegionLabelsByXY(int x, int y) {
		LabelStack regionLabels = new LabelStack();
		if (regionName != null) {
			regionLabels.addLabel(regionName);
		}
		return regionLabels;
	}
	
	public String getRegionName() {
		return regionName;
	}
	
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}
	
	// Config lables
	
	public LabelStack getConfigLabelsByPosition(int columnPosition, int rowPosition) {
		LabelStack configLabels = new LabelStack();
		if (configLabelAccumulator != null) {
			configLabelAccumulator.accumulateConfigLabels(configLabels, columnPosition, rowPosition);
		}
		if (regionName != null) {
			configLabels.addLabel(regionName);
		}
		return configLabels;
	}
	
	public IConfigLabelAccumulator getConfigLabelAccumulator() {
		return configLabelAccumulator;
	}
	
	public void setConfigLabelAccumulator(IConfigLabelAccumulator cellLabelAccumulator) {
		this.configLabelAccumulator = cellLabelAccumulator;
	}
	
	// Persistence
	
	public void saveState(String prefix, Properties properties) {
		for (IPersistable persistable : persistables) {
			persistable.saveState(prefix, properties);
		}
	}
	
	public void loadState(String prefix, Properties properties) {
		for (IPersistable persistable : persistables) {
			persistable.loadState(prefix, properties);
		}
	}
	  
	public void registerPersistable(IPersistable persistable){
		persistables.add(persistable);
	}

	public void unregisterPersistable(IPersistable persistable){
		persistables.remove(persistable);
	}
	
	// Configuration
	
	public void addConfiguration(IConfiguration configuration) {
		configurations.add(configuration);
	}

	public void clearConfiguration() {
		configurations.clear();
	}
	
	public void configure(ConfigRegistry configRegistry, UiBindingRegistry uiBindingRegistry) {
		for (IConfiguration configuration : configurations) {
			configuration.configureLayer(this);
			configuration.configureRegistry(configRegistry);
			configuration.configureUiBindings(uiBindingRegistry);
		}
	}
	
	// Commands
	
	@SuppressWarnings("unchecked")
	public boolean doCommand(ILayerCommand command) {
		for (Class<? extends ILayerCommand> commandClass : commandHandlers.keySet()) {
			if (commandClass.isInstance(command)) {
				ILayerCommandHandler commandHandler = commandHandlers.get(commandClass);
				if (commandHandler.doCommand(this, command)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	// Command handlers
	
	/**
	 * Layers should use this method to register their command handlers
	 * and call it from their constructor. This allows easy overriding if 
	 * required of command handlers 
	 */
	protected void registerCommandHandlers() {
		// No op
	}
	
	public void registerCommandHandler(ILayerCommandHandler<?> commandHandler) {
		commandHandlers.put(commandHandler.getCommandClass(), commandHandler);
	}

	public void unregisterCommandHandler(Class<? extends ILayerCommand> commandClass) {
		commandHandlers.remove(commandClass);
	}
	
	// Events

	public void addLayerListener(ILayerListener listener) {
		listeners.add(listener);
	}
	
	public void removeLayerListener(ILayerListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Handle layer event notification. Convert it to your context
	 * and propagate <i>UP</i>.
	 *  
	 * If you override this method you <strong>MUST NOT FORGET</strong> to raise
	 * the event up the layer stack by calling <code>super.fireLayerEvent(event)</code>
	 * - unless you plan to eat the event yourself.
	 **/
	@SuppressWarnings("unchecked")
	public void handleLayerEvent(ILayerEvent event) {
		for (Class<? extends ILayerEvent> eventClass : eventHandlers.keySet()) {
			if (eventClass.isInstance(event)) {
				ILayerEventHandler eventHandler = eventHandlers.get(eventClass);
				eventHandler.handleLayerEvent(event);
			}
		}
		
		// Pass on the event to our parent
		if (event.convertToLocal(this)) {
			fireLayerEvent(event);
		}
	}
	
	public void registerEventHandler(ILayerEventHandler<?> eventHandler) {
		eventHandlers.put(eventHandler.getLayerEventClass(), eventHandler);
	}
	
	/**
	 * Pass the event to all the {@link ILayerListener} registered on this layer.
	 * A cloned copy is passed to each listener.
	 */
	public void fireLayerEvent(ILayerEvent event) {
		final ILayerListener[] currentListeners = listeners.getListeners();
		final int last = currentListeners.length - 1;
		if (last >= 0) {
			// Fire cloned event to first n-1 listeners; fire original event to last listener
			for (int i = 0; i < last; i++) {
				currentListeners[i].handleLayerEvent(event.cloneEvent());
			}
			currentListeners[last].handleLayerEvent(event);
		}
	}
	
	/**
	 * @return {@link ILayerPainter}. Defaults to {@link GridLineCellLayerPainter}
	 */
	public ILayerPainter getLayerPainter() {
		if (layerPainter == null) {
			layerPainter = new GridLineCellLayerPainter();
		}
		return layerPainter;
	}
	
	protected void setLayerPainter(ILayerPainter layerPainter) {
		this.layerPainter = layerPainter;
	}

	// Client area
	
	public IClientAreaProvider getClientAreaProvider() {
		return clientAreaProvider;
	}
	
	public void setClientAreaProvider(IClientAreaProvider clientAreaProvider) {
		this.clientAreaProvider = clientAreaProvider;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
	
	public ILayerCell getCellByPosition(int columnPosition, int rowPosition) {
		if (columnPosition < 0 || columnPosition >= getColumnCount()
				|| rowPosition < 0 || rowPosition >= getRowCount()) {
			return null;
		}
		
		return new LayerCell(this, columnPosition, rowPosition);
	}
	
	public Rectangle getBoundsByPosition(int columnPosition, int rowPosition) {
		ILayerCell cell = getCellByPosition(columnPosition, rowPosition);
		ILayer cellLayer = cell.getLayer();
		
		int xOffset = -1;
		int yOffset = -1;
		int width = 0;
		int height = 0;
		{	int column = cell.getOriginColumnPosition();
			int end = column + cell.getColumnSpan();
			for (; column < end; column++) {
				int columnOffset = cellLayer.getStartXOfColumnPosition(column);
				if (columnOffset >= 0) {
					xOffset = columnOffset;
					break;
				}
			}
			for (; column < end; column++) {
				width += cellLayer.getColumnWidthByPosition(column);
			}
		}
		{	int row = cell.getOriginRowPosition();
			int end = row + cell.getRowSpan();
			for (; row < end; row++) {
				int rowOffset = cellLayer.getStartYOfRowPosition(row);
				if (rowOffset >= 0) {
					yOffset = rowOffset;
					break;
				}
			}
			for (; row < end; row++) {
				height += cellLayer.getRowHeightByPosition(row);
			}
		}
		
		return (xOffset >= 0 && yOffset >= 0) ? new Rectangle(xOffset, yOffset, width, height) : null;
	}
	
	public String getDisplayModeByPosition(int columnPosition, int rowPosition) {
		return DisplayMode.NORMAL;
	}
	
	public ICellPainter getCellPainter(int columnPosition, int rowPosition, ILayerCell cell, IConfigRegistry configRegistry) {
		return configRegistry.getConfigAttribute(CellConfigAttributes.CELL_PAINTER, cell.getDisplayMode(), cell.getConfigLabels().getLabels());
	}
	
}
