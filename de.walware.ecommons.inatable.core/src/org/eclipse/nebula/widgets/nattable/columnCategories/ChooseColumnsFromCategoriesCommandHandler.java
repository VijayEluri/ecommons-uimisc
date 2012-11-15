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
// ~Direction
package org.eclipse.nebula.widgets.nattable.columnCategories;

import static org.eclipse.nebula.widgets.nattable.columnChooser.ColumnChooserUtils.getHiddenColumnEntries;
import static org.eclipse.nebula.widgets.nattable.columnChooser.ColumnChooserUtils.getVisibleColumnsEntries;
import static org.eclipse.nebula.widgets.nattable.util.ObjectUtils.isNotNull;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.columnCategories.gui.ColumnCategoriesDialog;
import org.eclipse.nebula.widgets.nattable.columnChooser.ColumnChooserUtils;
import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.command.ILayerCommand;
import org.eclipse.nebula.widgets.nattable.coordinate.Direction;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionUtil;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.reorder.command.ColumnReorderCommand;
import org.eclipse.nebula.widgets.nattable.reorder.command.MultiColumnReorderCommand;
import org.eclipse.nebula.widgets.nattable.util.ObjectUtils;


public class ChooseColumnsFromCategoriesCommandHandler
	extends AbstractLayerCommandHandler<ChooseColumnsFromCategoriesCommand>
	implements IColumnCategoriesDialogListener {

	private final ColumnHideShowLayer columnHideShowLayer;
	private final ColumnHeaderLayer columnHeaderLayer;
	private final DataLayer columnHeaderDataLayer;
	private final ColumnCategoriesModel model;
	private ColumnCategoriesDialog dialog;

	public ChooseColumnsFromCategoriesCommandHandler(
				ColumnHideShowLayer columnHideShowLayer,
				ColumnHeaderLayer columnHeaderLayer,
				DataLayer columnHeaderDataLayer,
				ColumnCategoriesModel model) {
		super();
		this.columnHideShowLayer = columnHideShowLayer;
		this.columnHeaderLayer = columnHeaderLayer;
		this.columnHeaderDataLayer = columnHeaderDataLayer;
		this.model = model;
	}

	@Override
	protected boolean doCommand(ChooseColumnsFromCategoriesCommand command) {
		dialog = new ColumnCategoriesDialog(
				command.getShell(),
				model,
				getHiddenColumnEntries(columnHideShowLayer, columnHeaderLayer, columnHeaderDataLayer),
				getVisibleColumnsEntries(columnHideShowLayer, columnHeaderLayer, columnHeaderDataLayer));

		dialog.addListener(this);
		dialog.open();
		return true;
	}

	public Class<ChooseColumnsFromCategoriesCommand> getCommandClass() {
		return ChooseColumnsFromCategoriesCommand.class;
	}

	// Listen and respond to the dialog events

	public void itemsRemoved(List<Integer> removedColumnPositions) {
		ColumnChooserUtils.hideColumnPositions(removedColumnPositions, columnHideShowLayer);
		refreshDialog();
	}

	public void itemsSelected(List<Integer> addedColumnIndexes) {
		ColumnChooserUtils.showColumnIndexes(addedColumnIndexes, columnHideShowLayer);
		refreshDialog();
	}

	/**
	 * Moves the columns up or down by firing commands on the dialog.<br/>
	 *
	 * Individual columns are moved using the {@link ColumnReorderCommand}<br/>
	 * Contiguously selected columns are moved using the {@link MultiColumnReorderCommand}<br/>
	 */
	public void itemsMoved(Direction direction, List<Integer> selectedPositions) {
		List<List<Integer>> fromPositions = PositionUtil.getGroupedByContiguous(selectedPositions);
		List<Integer> toPositions = getDestinationPositions(direction, fromPositions);

		for (int i = 0; i < fromPositions.size(); i++) {
			boolean multipleColumnsMoved = fromPositions.get(i).size() > 1;

			ILayerCommand command = null;
			if (!multipleColumnsMoved) {
				int fromPosition = fromPositions.get(i).get(0).intValue();
				int toPosition = toPositions.get(i);
				command = new ColumnReorderCommand(columnHideShowLayer, fromPosition, toPosition);
			} else if(multipleColumnsMoved){
				command = new MultiColumnReorderCommand(columnHideShowLayer, fromPositions.get(i), toPositions.get(i));
			}
			columnHideShowLayer.doCommand(command);
		}

		refreshDialog();
	}

	/**
	 * Calculates the destination positions taking into account the move direction
	 * and single/contiguous selection.
	 *
	 * @param selectedPositions grouped together if they are contiguous.
	 * 	Example: if 2,3,4, 9, 12 are selected, they are grouped as [[2, 3, 4], 9, 12]
	 * 		While moving up the destination position for [2, 3, 4] is 1
	 * 		While moving up the destination position for [2, 3, 4] is 6
	 */
	protected List<Integer> getDestinationPositions(Direction direction, List<List<Integer>> selectedPositions) {
		List<Integer> destinationPositions = new ArrayList<Integer>();
		for (List<Integer> contiguousPositions : selectedPositions) {
			switch (direction) {
			case UP:
				destinationPositions.add(ObjectUtils.getFirstElement(contiguousPositions) - 1);
				break;
			case DOWN:
				destinationPositions.add(ObjectUtils.getLastElement(contiguousPositions) + 2);
				break;
			default:
				break;
			}
		}
		return destinationPositions;
	}

	private void refreshDialog() {
		if (isNotNull(dialog)) {
			dialog.refresh(
					ColumnChooserUtils.getHiddenColumnEntries(columnHideShowLayer, columnHeaderLayer, columnHeaderDataLayer),
					ColumnChooserUtils.getVisibleColumnsEntries(columnHideShowLayer, columnHeaderLayer, columnHeaderDataLayer));
		}
	}

}
