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
package org.eclipse.nebula.widgets.nattable.painter.cell.decorator;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.CellPainterWrapper;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.CellStyleUtil;


public class PaddingDecorator extends CellPainterWrapper {
	
	
	private final int topPadding;
	private final int rightPadding;
	private final int bottomPadding;
	private final int leftPadding;
	
	
	public PaddingDecorator(ICellPainter interiorPainter) {
		this(interiorPainter, 2);
	}
	
	public PaddingDecorator(ICellPainter interiorPainter, int padding) {
		this(interiorPainter, padding, padding, padding, padding);
	}
	
	public PaddingDecorator(ICellPainter interiorPainter, int topPadding, int rightPadding, int bottomPadding, int leftPadding) {
		super(interiorPainter);
		this.topPadding = topPadding;
		this.rightPadding = rightPadding;
		this.bottomPadding = bottomPadding;
		this.leftPadding = leftPadding;
	}
	
	
	public int getPreferredWidth(ILayerCell cell, GC gc, IConfigRegistry configRegistry) {
		return leftPadding + super.getPreferredWidth(cell, gc, configRegistry) + rightPadding;
	}
	
	public int getPreferredHeight(ILayerCell cell, GC gc, IConfigRegistry configRegistry) {
		return topPadding + super.getPreferredHeight(cell, gc, configRegistry) + bottomPadding;
	}
	
	public void paintCell(ILayerCell cell, GC gc, Rectangle adjustedCellBounds, IConfigRegistry configRegistry) {
		Color originalBg = gc.getBackground();
		Color cellStyleBackground = getBackgroundColor(cell, configRegistry);
		if (cellStyleBackground != null) {
			gc.setBackground(cellStyleBackground);
			gc.fillRectangle(adjustedCellBounds);
			gc.setBackground(originalBg);
		}
		else {
			gc.fillRectangle(adjustedCellBounds);
		}
		
		Rectangle interiorBounds = getInteriorBounds(adjustedCellBounds);
		if (interiorBounds.width > 0 && interiorBounds.height > 0) {
			super.paintCell(cell, gc, interiorBounds, configRegistry);
		}
	}
	
	protected Rectangle getInteriorBounds(Rectangle adjustedCellBounds) {
		return new Rectangle(
				adjustedCellBounds.x + leftPadding,
				adjustedCellBounds.y + topPadding,
				adjustedCellBounds.width - leftPadding - rightPadding,
				adjustedCellBounds.height - topPadding - bottomPadding
		);
	}
	
	protected Color getBackgroundColor(ILayerCell cell, IConfigRegistry configRegistry) {
		return CellStyleUtil.getCellStyle(cell, configRegistry).getAttributeValue(CellStyleAttributes.BACKGROUND_COLOR);
	}
	
}
