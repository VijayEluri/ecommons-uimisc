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
// -depend, ~
package org.eclipse.nebula.widgets.nattable.sort.painter;

import org.eclipse.swt.graphics.Image;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.CellPainterWrapper;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ImagePainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.CellPainterDecorator;
import org.eclipse.nebula.widgets.nattable.sort.SortHeaderLayer;
import org.eclipse.nebula.widgets.nattable.sort.config.DefaultSortConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.util.CellEdgeEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;

public class SortableHeaderTextPainter extends CellPainterWrapper {

	/**
	 * Default setup, uses the {@link TextPainter} as its companion painter
	 */
	public SortableHeaderTextPainter() {
		setWrappedPainter(new CellPainterDecorator(new TextPainter(), CellEdgeEnum.RIGHT, new SortIconPainter(true)));
	}

    public SortableHeaderTextPainter(ICellPainter interiorPainter, boolean paintBg, boolean interiorPainterToSpanFullWidth) {
	    ICellPainter sortPainter = new SortIconPainter(paintBg);
	    CellPainterDecorator painter = new CellPainterDecorator(interiorPainter, CellEdgeEnum.RIGHT, 0, sortPainter);
	    painter.setBaseCellPainterSpansWholeCell(interiorPainterToSpanFullWidth);
        setWrappedPainter(painter);
	}

	/**
	 * Paints the triangular sort icon images.
	 */
	protected static class SortIconPainter extends ImagePainter {

		public SortIconPainter(boolean paintBg) {
			super(null, paintBg);
		}

		@Override
		protected Image getImage(ILayerCell cell, IConfigRegistry configRegistry) {
			Image icon = null;

			if (isSortedAscending(cell)) {
				icon = selectDownImage(getSortSequence(cell));
			} else if (isSortedDescending(cell)) {
				icon = selectUpImage(getSortSequence(cell));
			}

			return icon;
		}

		private boolean isSortedAscending(ILayerCell cell) {
			return cell.getConfigLabels().hasLabel(DefaultSortConfiguration.SORT_UP_CONFIG_TYPE);
		}

		private boolean isSortedDescending(ILayerCell cell) {
			return cell.getConfigLabels().hasLabel(DefaultSortConfiguration.SORT_DOWN_CONFIG_TYPE);
		}

		private int getSortSequence(ILayerCell cell) {
			int sortSeq = 0;

			for (String configLabel : cell.getConfigLabels().getLabels()) {
				if (configLabel.startsWith(DefaultSortConfiguration.SORT_SEQ_CONFIG_TYPE)) {
					sortSeq = Integer.parseInt(configLabel.substring(
							DefaultSortConfiguration.SORT_SEQ_CONFIG_TYPE.length() ));
				}
			}
			return sortSeq;
		}

		private Image selectUpImage(int sortSequence) {
			switch (sortSequence) {
			case 0:
				return GUIHelper.getImage("up_0"); //$NON-NLS-1$
			case 1:
				return GUIHelper.getImage("up_1"); //$NON-NLS-1$
			case 2:
				return GUIHelper.getImage("up_2"); //$NON-NLS-1$
			default:
				return GUIHelper.getImage("up_2"); //$NON-NLS-1$
			}
		}

		private Image selectDownImage(int sortSequence) {
			switch (sortSequence) {
			case 0:
				return GUIHelper.getImage("down_0"); //$NON-NLS-1$
			case 1:
				return GUIHelper.getImage("down_1"); //$NON-NLS-1$
			case 2:
				return GUIHelper.getImage("down_2"); //$NON-NLS-1$
			default:
				return GUIHelper.getImage("down_2"); //$NON-NLS-1$
			}
		}

	}

}
