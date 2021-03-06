/*******************************************************************************
 * Copyright (c) 2012-2016 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 *     Stephan Wahlbrink - dim-based implementation
 ******************************************************************************/

package de.walware.ecommons.waltable.viewport;

import static de.walware.ecommons.waltable.coordinate.Orientation.HORIZONTAL;
import static de.walware.ecommons.waltable.coordinate.Orientation.VERTICAL;

import org.eclipse.swt.widgets.Display;

import de.walware.ecommons.waltable.command.ILayerCommand;
import de.walware.ecommons.waltable.coordinate.LRange;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.coordinate.Orientation;
import de.walware.ecommons.waltable.grid.ClientAreaResizeCommand;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.TransformLayer;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.layer.event.ILayerEvent;
import de.walware.ecommons.waltable.layer.event.IStructuralChangeEvent;
import de.walware.ecommons.waltable.print.PrintEntireGridCommand;
import de.walware.ecommons.waltable.print.TurnViewportOffCommand;
import de.walware.ecommons.waltable.print.TurnViewportOnCommand;
import de.walware.ecommons.waltable.selection.CellSelectionEvent;
import de.walware.ecommons.waltable.selection.ColumnSelectionEvent;
import de.walware.ecommons.waltable.selection.RowSelectionEvent;
import de.walware.ecommons.waltable.selection.SelectionLayer;
import de.walware.ecommons.waltable.swt.SWTUtil;


/**
 * Viewport - the visible area of NatTable
 * 
 * Places a 'viewport' over the table. Introduces scroll bars over the table and
 * keeps them in sync with the data being displayed. This is typically placed over the
 * {@link SelectionLayer}.
 */
public class ViewportLayer extends TransformLayer {
	
	static final int EDGE_HOVER_REGION_SIZE= 16;
	
	static final int PAGE_INTERSECTION_SIZE= EDGE_HOVER_REGION_SIZE;
	
	
	private final ILayer scrollableLayer;
	
	// The viewport current origin
	private boolean viewportOff= false;
	private final long[] savedOriginPixel= new long[2];
	
	// Edge hover scrolling
	private MoveViewportRunnable edgeHoverRunnable;
	
	
	/**
	 * Creates a new viewport layer.
	 * 
	 * @param underlyingLayer the underlying scrollable layer
	 */
	public ViewportLayer(/*@NonNull*/ final ILayer underlyingLayer) {
		super(underlyingLayer);
		this.scrollableLayer= underlyingLayer;
		
		registerCommandHandlers();
		
		initDims();
	}
	
	
	@Override
	public void dispose() {
		super.dispose();
		
		for (final Orientation orientation : Orientation.values()) {
			disposeDim(orientation);
		}
		
		cancelEdgeHoverScroll();
	}
	
	
	public boolean isViewportOff() {
		return this.viewportOff;
	}
	
	
	@Override
	protected void initDims() {
		final ILayer scrollable= getScrollableLayer();
		if (scrollable == null) {
			return;
		}
		for (final Orientation orientation : Orientation.values()) {
			disposeDim(orientation);
			setDim(new ViewportLayerDim(this, scrollable.getDim(orientation)));
		}
	}
	
	protected void disposeDim(final Orientation orientation) {
		final ViewportLayerDim dim= get(orientation);
		if (dim != null) {
			dim.dispose();
		}
	}
	
	@Override
	public IViewportDim getDim(final Orientation orientation) {
		return (IViewportDim) super.getDim(orientation);
	}
	
	final ViewportLayerDim get(final Orientation orientation) {
		return (ViewportLayerDim) super.getDim(orientation);
	}
	
	// Configuration
	
	@Override
	protected void registerCommandHandlers() {
		registerCommandHandler(new RecalculateScrollBarsCommandHandler(this));
		registerCommandHandler(new ShowCellInViewportCommandHandler(this));
		registerCommandHandler(new ShowPositionInViewportCommandHandler());
		registerCommandHandler(new ViewportSelectDimPositionsCommandHandler(this));
		registerCommandHandler(new ViewportDragCommandHandler(this));
		registerCommandHandler(new SelectRelativePageCommandHandler(this));
		registerCommandHandler(new ScrollStepCommandHandler(this));
		registerCommandHandler(new ScrollPageCommandHandler(this));
	}
	
	
	// Cell features
	
	@Override
	public ILayerCell getCellByPosition(final long columnPosition, final long rowPosition) {
		return super.getCellByPosition(columnPosition, rowPosition);
	}
	
	protected void fireScrollEvent() {
		fireLayerEvent(new ScrollEvent(this));
	}
	
	@Override
	public boolean doCommand(final ILayerCommand command) {
		if (command instanceof ClientAreaResizeCommand) {
			final ClientAreaResizeCommand clientAreaResizeCommand= (ClientAreaResizeCommand) command.cloneCommand();
			if (clientAreaResizeCommand.convertToTargetLayer(this)) {
				//remember the difference from client area to body region area
				//needed because the scrollbar will be removed and therefore the client area will become bigger
				final long widthDiff= clientAreaResizeCommand.getScrollable().getClientArea().width - clientAreaResizeCommand.getCalcArea().width;
				final long heightDiff= clientAreaResizeCommand.getScrollable().getClientArea().height - clientAreaResizeCommand.getCalcArea().height;
				
				get(HORIZONTAL).checkScrollBar(clientAreaResizeCommand.getScrollable());
				get(VERTICAL).checkScrollBar(clientAreaResizeCommand.getScrollable());
				
				get(HORIZONTAL).handleResize();
				get(VERTICAL).handleResize();
				
				//after handling the scrollbars recalculate the area to use for percentage calculation
				final LRectangle possibleArea= SWTUtil.toNatTable(clientAreaResizeCommand.getScrollable().getClientArea());
				possibleArea.width-= widthDiff;
				possibleArea.height-= heightDiff;
				clientAreaResizeCommand.setCalcArea(possibleArea);
				//we don't return true here because the ClientAreaResizeCommand needs to be handled
				//by the DataLayer in case percentage sizing is enabled
				//if we would return true, the DataLayer wouldn't be able to calculate the column/row
				//sizes regarding the client area
				return super.doCommand(clientAreaResizeCommand);
			}
		}
		else if (command instanceof TurnViewportOffCommand) {
			if (!isViewportOff()) {
				for (final Orientation orientation : Orientation.values()) {
					this.savedOriginPixel[orientation.ordinal()]= get(orientation).getOriginPixel();
				}
				this.viewportOff= true;
				fireScrollEvent();
			}
			return true;
		}
		else if (command instanceof TurnViewportOnCommand) {
			if (isViewportOff()) {
				this.viewportOff= false;
				for (final Orientation orientation : Orientation.values()) {
					get(orientation).doSetOriginPixel(this.savedOriginPixel[orientation.ordinal()]);
				}
				fireScrollEvent();
			}
			return true;
		}
		else if (command instanceof PrintEntireGridCommand) {
			get(HORIZONTAL).movePositionIntoViewport(0);
			get(VERTICAL).movePositionIntoViewport(0);
		}
		return super.doCommand(command);
	}
	
	/**
	 * Recalculate scrollbar characteristics.
	 */
	public void recalculateScrollBars() {
		get(HORIZONTAL).handleResize();
		get(VERTICAL).handleResize();
	}
	
	// Event handling
	
	@Override
	public void handleLayerEvent(final ILayerEvent event) {
		if (event instanceof IStructuralChangeEvent) {
			final IStructuralChangeEvent structuralChangeEvent= (IStructuralChangeEvent) event;
			if (structuralChangeEvent.isHorizontalStructureChanged()) {
				get(HORIZONTAL).handleStructuralChange(structuralChangeEvent.getColumnDiffs());
			}
			if (structuralChangeEvent.isVerticalStructureChanged()) {
				get(VERTICAL).handleStructuralChange(structuralChangeEvent.getRowDiffs());
			}
		}
		
		if (event instanceof CellSelectionEvent) {
			processSelection((CellSelectionEvent) event);
		} else if (event instanceof ColumnSelectionEvent) {
			processColumnSelection((ColumnSelectionEvent) event);
		} else if (event instanceof RowSelectionEvent) {
			processRowSelection((RowSelectionEvent) event);
		}
		
		super.handleLayerEvent(event);
	}
	
	/**
	 * Handle {@link CellSelectionEvent}
	 * @param selectionEvent
	 */
	private void processSelection(final CellSelectionEvent selectionEvent) {
		if (selectionEvent.getRevealCell()) {
			get(HORIZONTAL).movePositionIntoViewport(selectionEvent.getColumnPosition());
			get(VERTICAL).movePositionIntoViewport(selectionEvent.getRowPosition());
		}
	}
	
	/**
	 * Handle {@link ColumnSelectionEvent}
	 * @param selectionEvent
	 */
	private void processColumnSelection(final ColumnSelectionEvent selectionEvent) {
		final long explicitePosition= selectionEvent.getColumnPositionToReveal();
		if (explicitePosition >= 0) {
			get(HORIZONTAL).movePositionIntoViewport(explicitePosition);
		}
	}
	
	/**
	 * Handle {@link RowSelectionEvent}
	 * @param selectionEvent
	 */
	private void processRowSelection(final RowSelectionEvent selectionEvent) {
		final long explicitePosition= selectionEvent.getRowPositionToReveal();
		if (explicitePosition >= 0) {
			get(VERTICAL).movePositionIntoViewport(explicitePosition);
			return;
		}
	}
	
	// Accessors
	
	/**
	 * @return The scrollable layer underlying the viewport.
	 */
	public ILayer getScrollableLayer() {
		return this.scrollableLayer;
	}
	
	@Override
	public String toString() {
		return "Viewport Layer"; //$NON-NLS-1$
	}
	
	// Edge hover scrolling
	
	/**
	 * Used for edge hover scrolling. Called from the ViewportDragCommandHandler.
	 * @param x
	 * @param y
	 */
	public void drag(final long x, final long y) {
		if (x < 0 && y < 0) {
			cancelEdgeHoverScroll();
			return;
		}
		
		MoveViewportRunnable move= this.edgeHoverRunnable;
		if (move == null) {
			move= new MoveViewportRunnable();
		}
		
		move.fast= true;
		boolean requireSchedule= false;
		
		final LRectangle clientArea= getClientAreaProvider().getClientArea();
		for (final Orientation orientation : Orientation.values()) {
			final LRange lRange= clientArea.getRange(orientation);
			final long pixel= (orientation == HORIZONTAL) ? x : y;
			int change= 0;
			if (pixel >= lRange.start && pixel < lRange.start + EDGE_HOVER_REGION_SIZE) {
				change= -1;
				if (pixel >= lRange.start + EDGE_HOVER_REGION_SIZE/2) {
					move.fast= false;
				}
			} else if (pixel >= lRange.end - EDGE_HOVER_REGION_SIZE && pixel < lRange.end) {
				change= 1;
				if (pixel < lRange.end - EDGE_HOVER_REGION_SIZE/2) {
					move.fast= false;
				}
			}
			move.change[orientation.ordinal()]= change;
			requireSchedule |= (change != 0);
		}
		
		if (requireSchedule) {
			move.schedule();
		} else {
			cancelEdgeHoverScroll();
		}
	}
	
	/**
	 * Cancels an edge hover scroll.
	 */
	private void cancelEdgeHoverScroll() {
		this.edgeHoverRunnable= null;
	}
	
	/**
	 * Runnable that incrementally scrolls the viewport when drag hovering over an edge.
	 */
	class MoveViewportRunnable implements Runnable {
		
		
		private final int[] change= new int[2];
		private boolean fast;
		
		private final Display display= Display.getCurrent();
		
		
		public MoveViewportRunnable() {
		}
		
		
		public void schedule() {
			if (ViewportLayer.this.edgeHoverRunnable != this) {
				ViewportLayer.this.edgeHoverRunnable= this;
				this.display.timerExec(500, this);
			}
		}
		
		@Override
		public void run() {
			if (ViewportLayer.this.edgeHoverRunnable != this) {
				return;
			}
			
			for (final Orientation orientation : Orientation.values()) {
				switch (this.change[orientation.ordinal()]) {
				case -1:
					get(orientation).scrollBackwardByPosition();
					break;
				case 1:
					get(orientation).scrollForwardByPosition();
					break;
				}
			}
			
			this.display.timerExec(this.fast ? 100 : 500, this);
		}
		
	}
	
}
