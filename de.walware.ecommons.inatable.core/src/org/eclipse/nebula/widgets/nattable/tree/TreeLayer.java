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
package org.eclipse.nebula.widgets.nattable.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.nebula.widgets.nattable.command.ILayerCommand;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.hideshow.AbstractRowHideShowLayer;
import org.eclipse.nebula.widgets.nattable.hideshow.command.MultiRowHideCommand;
import org.eclipse.nebula.widgets.nattable.hideshow.command.RowHideCommand;
import org.eclipse.nebula.widgets.nattable.hideshow.event.HideRowPositionsEvent;
import org.eclipse.nebula.widgets.nattable.hideshow.event.ShowRowPositionsEvent;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.BackgroundPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.CellPainterDecorator;
import org.eclipse.nebula.widgets.nattable.tree.command.TreeExpandCollapseCommandHandler;
import org.eclipse.nebula.widgets.nattable.tree.config.DefaultTreeLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.tree.painter.IndentedTreeImagePainter;
import org.eclipse.nebula.widgets.nattable.ui.util.CellEdgeEnum;


public class TreeLayer extends AbstractRowHideShowLayer {

	public static final String TREE_COLUMN_CELL = "TREE_COLUMN_CELL"; //$NON-NLS-1$

	public static final long TREE_COLUMN_NUMBER = 0;

	/**
	 * The ITreeRowModelListener that is used to get information about the tree structure.
	 */
	private final ITreeRowModel<?> treeRowModel;

	private final Set<Long> hiddenRowIndexes;

	/**
	 * The IndentedTreeImagePainter that paints indentation to the left of the configured base painter
	 * and icons for expand/collapse if possible, to render tree structure accordingly.
	 */
	private IndentedTreeImagePainter indentedTreeImagePainter;
	
	/**
	 * Creates a TreeLayer instance based on the given information. Will use a default IndentedTreeImagePainter
	 * that uses 10 pixels for indentation and simple + and - icons for expand/collapse icons. It also
	 * uses the DefaultTreeLayerConfiguration.
	 * @param underlyingLayer The underlying layer on whose top this layer will be set.
	 * @param treeRowModel The ITreeRowModelListener that is used to get information about the tree structure.
	 */
	public TreeLayer(IUniqueIndexLayer underlyingLayer, ITreeRowModel<?> treeRowModel) {
		this(underlyingLayer, treeRowModel, new IndentedTreeImagePainter(treeRowModel));
	}
	
	/**
	 * Creates a TreeLayer instance based on the given information. Allows to specify the IndentedTreeImagePainter
	 * while using the DefaultTreeLayerConfiguration.
	 * @param underlyingLayer The underlying layer on whose top this layer will be set.
	 * @param treeRowModel The ITreeRowModelListener that is used to get information about the tree structure.
	 * @param indentedTreeImagePainter The IndentedTreeImagePainter that paints indentation to the left of the 
	 * 			configured base painter	and icons for expand/collapse if possible, to render tree structure accordingly.
	 */
	public TreeLayer(IUniqueIndexLayer underlyingLayer, ITreeRowModel<?> treeRowModel, 
			IndentedTreeImagePainter indentedTreeImagePainter) {
		this(underlyingLayer, treeRowModel, indentedTreeImagePainter, true);
	}
	
	/**
	 * Creates a TreeLayer instance based on the given information. Will use a default IndentedTreeImagePainter
	 * that uses 10 pixels for indentation and simple + and - icons for expand/collapse icons.
	 * @param underlyingLayer The underlying layer on whose top this layer will be set.
	 * @param treeRowModel The ITreeRowModelListener that is used to get information about the tree structure.
	 * @param useDefaultConfiguration <code>true</code> to use the DefaultTreeLayerConfiguration, <code>false</code>
	 * 			if you want to specify your own configuration.
	 */
	public TreeLayer(IUniqueIndexLayer underlyingLayer, ITreeRowModel<?> treeRowModel, boolean useDefaultConfiguration) {
		this(underlyingLayer, treeRowModel, new IndentedTreeImagePainter(treeRowModel), useDefaultConfiguration);
	}
	
	/**
	 * Creates a TreeLayer instance based on the given information.
	 * @param underlyingLayer The underlying layer on whose top this layer will be set.
	 * @param treeRowModel The ITreeRowModelListener that is used to get information about the tree structure.
	 * @param indentedTreeImagePainter The IndentedTreeImagePainter that paints indentation to the left of the 
	 * 			configured base painter	and icons for expand/collapse if possible, to render tree structure accordingly.
	 * @param useDefaultConfiguration <code>true</code> to use the DefaultTreeLayerConfiguration, <code>false</code>
	 * 			if you want to specify your own configuration.
	 */
	public TreeLayer(IUniqueIndexLayer underlyingLayer, ITreeRowModel<?> treeRowModel, 
			IndentedTreeImagePainter indentedTreeImagePainter, boolean useDefaultConfiguration) {
		
		super(underlyingLayer);
		this.treeRowModel = treeRowModel;

		this.hiddenRowIndexes = new TreeSet<Long>();
		
		if (useDefaultConfiguration) {
			addConfiguration(new DefaultTreeLayerConfiguration(this));
		}
		
		setConfigLabelAccumulator(new IConfigLabelAccumulator() {
			@Override
			public void accumulateConfigLabels(LabelStack configLabels, long columnPosition, long rowPosition) {
				if (isTreeColumn(columnPosition)) {
					configLabels.addLabelOnTop(TREE_COLUMN_CELL);
				}
			}
		});

		this.indentedTreeImagePainter = indentedTreeImagePainter;

		registerCommandHandler(new TreeExpandCollapseCommandHandler(this));
	}

	/**
	 * @return The ITreeRowModelListener that is used to get information about the tree structure.
	 */
	public ITreeRowModel<?> getModel() {
		return this.treeRowModel;
	}

	/**
	 * @return The IndentedTreeImagePainter that paints indentation to the left of the configured base painter
	 * 			and icons for expand/collapse if possible, to render tree structure accordingly.
	 */
	public IndentedTreeImagePainter getIndentedTreeImagePainter() {
		return this.indentedTreeImagePainter;
	}

	/**
	 * @return The ICellPainter that is used to paint the images in the tree by the IndentedTreeImagePainter. 
	 * 			Usually it is some type	of TreeImagePainter that paints expand/collapse/leaf icons regarding 
	 * 			the node state.<br/>
	 * 			Can be <code>null</code> if set explicitly to the IndentedTreeImagePainter!
	 */
	public ICellPainter getTreeImagePainter() {
		return this.indentedTreeImagePainter != null ? this.indentedTreeImagePainter.getTreeImagePainter() : null;
	}
	
	/**
	 * @param columnPosition The column position to check.
	 * @return <code>true</code> if the given column position is the tree column, <code>false</code>
	 * 			if not.
	 */
	private boolean isTreeColumn(long columnPosition) {
		return columnPosition == TREE_COLUMN_NUMBER;
	}
	
	@Override
	public ICellPainter getCellPainter(long columnPosition, long rowPosition, ILayerCell cell, IConfigRegistry configRegistry) {
		ICellPainter cellPainter = super.getCellPainter(columnPosition, rowPosition, cell, configRegistry);
		
		if (cell.getConfigLabels().hasLabel(TREE_COLUMN_CELL)) {
			cellPainter = new BackgroundPainter(new CellPainterDecorator(
					cellPainter, CellEdgeEnum.LEFT, this.indentedTreeImagePainter));
		}
		
		return cellPainter;
	}

	@Override
	public boolean isRowIndexHidden(long rowIndex) {
		return this.hiddenRowIndexes.contains(Long.valueOf(rowIndex)) || isHiddenInUnderlyingLayer(rowIndex);
	}

	@Override
	public Collection<Long> getHiddenRowIndexes() {
		return this.hiddenRowIndexes;
	}

	/**
	 * Performs an expand/collapse action dependent on the current state of the tree node
	 * at the given row index.
	 * @param parentIndex The index of the row that shows the tree node for which the
	 * 			expand/collapse action should be performed.
	 */
	public void expandOrCollapseIndex(long parentIndex) {
		if (this.treeRowModel.isCollapsed(parentIndex)) {
			expandTreeRow(parentIndex);
		} else {
			collapseTreeRow(parentIndex);
		}
	}

	/**
	 * Collapses the tree node for the given row index.
	 * @param parentIndex The index of the row that shows the node that should be collapsed
	 */
	public void collapseTreeRow(long parentIndex) {
		List<Long> rowIndexes = this.treeRowModel.collapse(parentIndex);
		List<Long> rowPositions = new ArrayList<Long>();
		for (Long rowIndex : rowIndexes) {
			long rowPos = getRowPositionByIndex(rowIndex);
			//if the rowPos is negative, it is not visible because of hidden state in an underlying layer
			if (rowPos >= 0) {
				rowPositions.add(rowPos);
			}
		}
		this.hiddenRowIndexes.addAll(rowIndexes);
		invalidateCache();
		fireLayerEvent(new HideRowPositionsEvent(this, rowPositions));
	}

	/**
	 * Expands the tree node for the given row index.
	 * @param parentIndex The index of the row that shows the node that should be expanded
	 */
	public void expandTreeRow(long parentIndex) {
		List<Long> rowIndexes = 	this.treeRowModel.expand(parentIndex);
		this.hiddenRowIndexes.removeAll(rowIndexes);
		invalidateCache();
		fireLayerEvent(new ShowRowPositionsEvent(this, rowIndexes));
	}
	
	/**
	 * Checks the underlying layer if the row is hidden by another layer.
	 * @param rowIndex The index of the row whose hidden state should be checked
	 * @return <code>true</code> if the row at the given index is hidden in the underlying layer
	 * 			<code>false</code> if not.
	 */
	private boolean isHiddenInUnderlyingLayer(long rowIndex) {
		IUniqueIndexLayer underlyingLayer = (IUniqueIndexLayer) getUnderlyingLayer();
		return (underlyingLayer.getRowPositionByIndex(rowIndex) == -1);
	}
	
	@Override
	public boolean doCommand(ILayerCommand command) {
		//special command transformations are needed to hide also child nodes
		if (command instanceof RowHideCommand) {
			return handleRowHideCommand((RowHideCommand)command);
		} else if (command instanceof MultiRowHideCommand) {
			return handleMultiRowHideCommand((MultiRowHideCommand)command);
		}
		return super.doCommand(command);
	}
	
	/**
	 * Checks if the given command tries to hide a row that is a node that is not collapsed and has children.
	 * In that case also the child rows need to be hidden. 
	 * @param command The {@link RowHideCommand} to process
	 * @return <code>true</code> if the command has been handled, <code>false</code> otherwise
	 */
	protected boolean handleRowHideCommand(RowHideCommand command) {
		//transform position to index
		if (command.convertToTargetLayer(this)) {
			long rowIndex = getRowIndexByPosition(command.getRowPosition());
			if (this.treeRowModel.hasChildren(rowIndex) && !this.treeRowModel.isCollapsed(rowIndex)) {
				List<Long> childIndexes = this.treeRowModel.getChildIndexes(rowIndex);
				long[] childPositions = new long[childIndexes.size()+1];
				childPositions[0] = command.getRowPosition();
				for (int i = 1; i < childIndexes.size()+1; i++) {
					long childPos = getRowPositionByIndex(childIndexes.get(i-1));
					childPositions[i] = childPos;
				}
				return super.doCommand(new MultiRowHideCommand(this, childPositions));
			}
		}
		return super.doCommand(command);
	}
	
	/**
	 * Checks if the given command tries to hide rows that are nodes that are not collapsed and have children.
	 * In that case also the child rows need to be hidden. 
	 * @param command The {@link MultiRowHideCommand} to process
	 * @return <code>true</code> if the command has been handled, <code>false</code> otherwise
	 */
	protected boolean handleMultiRowHideCommand(MultiRowHideCommand command) {
		//transform position to index
		if (command.convertToTargetLayer(this)) {
			List<Long> rowPositionsToHide = new ArrayList<Long>();
			for (Long rowPos : command.getRowPositions()) {
				rowPositionsToHide.add(rowPos);
				long rowIndex = getRowIndexByPosition(rowPos);
				if (this.treeRowModel.hasChildren(rowIndex) && !this.treeRowModel.isCollapsed(rowIndex)) {
					List<Long> childIndexes = this.treeRowModel.getChildIndexes(rowIndex);
					for (Long childIndex : childIndexes) {
						rowPositionsToHide.add(getRowPositionByIndex(childIndex));
					}
				}
			}
			
			long[] childPositions = new long[rowPositionsToHide.size()];
			for (int i = 0; i < rowPositionsToHide.size(); i++) {
				childPositions[i] = rowPositionsToHide.get(i);
			}
			return super.doCommand(new MultiRowHideCommand(this, childPositions));
		}
		return super.doCommand(command);
	}
}
