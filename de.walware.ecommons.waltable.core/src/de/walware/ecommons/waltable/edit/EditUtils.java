/*******************************************************************************
 * Copyright (c) 2012-2016 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
// ~
package de.walware.ecommons.waltable.edit;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.walware.ecommons.waltable.config.CellConfigAttributes;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.config.IEditableRule;
import de.walware.ecommons.waltable.coordinate.PositionCoordinate;
import de.walware.ecommons.waltable.data.convert.IDisplayConverter;
import de.walware.ecommons.waltable.edit.editor.ICellEditor;
import de.walware.ecommons.waltable.layer.LabelStack;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.selection.SelectionLayer;
import de.walware.ecommons.waltable.style.DisplayMode;


/**
 * Helper class for retrieving information regarding editing of selected cells.
 */
public class EditUtils {

	/**
	 * 
	 * @param selectionLayer The {@link SelectionLayer} to retrieve the current selection from.
	 * @return The last cell of the current selection in the specified {@link SelectionLayer}.
	 * 			Will return <code>null</code> if there is no selection.
	 */
	public static ILayerCell getLastSelectedCell(final SelectionLayer selectionLayer) {
		final PositionCoordinate selectionAnchor= selectionLayer.getSelectionAnchor();
		return selectionLayer.getCellByPosition(selectionAnchor.columnPosition, selectionAnchor.rowPosition);
	}

	/**
	 * 
	 * @param selectionLayer The {@link SelectionLayer} to retrieve the current selection from.
	 * @param configRegistry The {@link IConfigRegistry} needed to access the configured
	 * 			{@link ICellEditor}.
	 * @return The {@link ICellEditor} of the last cell of the current selection in the specified
	 * 			{@link SelectionLayer}. Will return <code>null</code> if there is no selection.
	 */
	public static ICellEditor getLastSelectedCellEditor(final SelectionLayer selectionLayer, final IConfigRegistry configRegistry) {
		final ILayerCell lastSelectedCell= EditUtils.getLastSelectedCell(selectionLayer);
		if (lastSelectedCell != null) {
			final List<String> lastSelectedCellLabelsArray= lastSelectedCell.getConfigLabels().getLabels();
			return configRegistry.getConfigAttribute(
					EditConfigAttributes.CELL_EDITOR, DisplayMode.EDIT, lastSelectedCellLabelsArray);
		}
		return null;
	}

	/**
	 * For every cell that is selected it is checked whether the cell is editable or not.
	 * @param selectionLayer The {@link SelectionLayer} to retrieve the current selection.
	 * @param configRegistry The {@link IConfigRegistry} needed to access the configured
	 * 			{@link IEditableRule}s.
	 * @return <code>true</code> if all selected cells are editable, <code>false</code> if
	 * 			at least one cell is not editable.
	 */
	public static boolean allCellsEditable(final SelectionLayer selectionLayer, final IConfigRegistry configRegistry) {
		final Collection<ILayerCell> selectedCells= selectionLayer.getSelectedCells();
		for (final ILayerCell layerCell : selectedCells) {
			final LabelStack labelStack= layerCell.getConfigLabels();
			final IEditableRule editableRule= configRegistry.getConfigAttribute(
					EditConfigAttributes.CELL_EDITABLE_RULE, 
					DisplayMode.EDIT, labelStack.getLabels());
			
			if (!editableRule.isEditable(layerCell, configRegistry)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if the cell at the specified coordinates is editable or not.
	 * <p>
	 * Note: The coordinates need to be related to the given SelectionLayer, otherwise
	 * 		 the wrong cell will be used for the check.
	 * @param selectionLayer The {@link SelectionLayer} to check the cell coordinates against.
	 * @param configRegistry The {@link IConfigRegistry} needed to access the configured
	 * 			{@link IEditableRule}s.
	 * @param cellCoords The coordinates of the cell to check the editable state, related to 
	 * 			the given {@link SelectionLayer}
	 * @return <code>true</code> if the cell is editable, <code>false</code> if not
	 */
	public static boolean isCellEditable(
			final SelectionLayer selectionLayer, final IConfigRegistry configRegistry, final PositionCoordinate cellCoords){
		final ILayerCell layerCell= selectionLayer.getCellByPosition(cellCoords.columnPosition, cellCoords.rowPosition);
		final LabelStack labelStack= layerCell.getConfigLabels();
		
		final IEditableRule editableRule= configRegistry.getConfigAttribute(
				EditConfigAttributes.CELL_EDITABLE_RULE, 
				DisplayMode.EDIT, labelStack.getLabels());
		if (editableRule == null) {
			return false;
		}
		
		return editableRule.isEditable(layerCell, configRegistry);
	}

	/**
	 * Checks if all selected cells have the same {@link ICellEditor} configured. This is needed for 
	 * the multi edit feature to determine if a multi edit is possible.
	 * @param selectionLayer The {@link SelectionLayer} to retrieve the current selection.
	 * @param configRegistry The {@link IConfigRegistry} needed to access the configured
	 * 			{@link ICellEditor}s.
	 * @return <code>true</code> if all selected cells have the same {@link ICellEditor}
	 * 			configured, <code>false</code> if at least one cell has another {@link ICellEditor}
	 * 			configured.
	 */
	public static boolean isEditorSame(final SelectionLayer selectionLayer, final IConfigRegistry configRegistry) {
		final List<PositionCoordinate> selectedCells= selectionLayer.getSelectedCellPositions();
		ICellEditor lastSelectedCellEditor= null;
		for (final PositionCoordinate selectedCell : selectedCells) {
			final ILayerCell cell= selectionLayer.getCellByPosition(
					selectedCell.columnPosition, selectedCell.rowPosition);
			final ICellEditor cellEditor= configRegistry.getConfigAttribute(
					EditConfigAttributes.CELL_EDITOR,
					DisplayMode.EDIT, cell.getConfigLabels().getLabels() );
			
			//The first time we get here we need to remember the editor so further checks can
			//use it. Getting the editor before by getLastSelectedCellEditor() might cause 
			//issues in case there is no active selection anchor
			if (lastSelectedCellEditor == null) {
				lastSelectedCellEditor= cellEditor;
			}
			if (cellEditor != lastSelectedCellEditor) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if all selected cells have the same {@link IDisplayConverter} configured. This is needed
	 * for the multi edit feature to determine if a multi edit is possible. 
	 * <p>
	 * Let's assume there are two columns, one containing an Integer, the other a Date. 
	 * Both have a TextCellEditor configured, so if only the editor is checked, the multi edit dialog
	 * would open. On committing a changed value an error would occur because of wrong conversion.
	 * @param selectionLayer The {@link SelectionLayer} to retrieve the current selection.
	 * @param configRegistry The {@link IConfigRegistry} needed to access the configured
	 * 			{@link IDisplayConverter}s.
	 * @return <code>true</code> if all selected cells have the same {@link IDisplayConverter}
	 * 			configured, <code>false</code> if at least one cell has another {@link IDisplayConverter}
	 * 			configured.
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isConverterSame(final SelectionLayer selectionLayer, final IConfigRegistry configRegistry){
		final List<PositionCoordinate> selectedCells= selectionLayer.getSelectedCellPositions();
		final Set<Class> converterSet= new HashSet<>();
		
		for (final PositionCoordinate selectedCell : selectedCells) {
			final ILayerCell cell= selectionLayer.getCellByPosition(
					selectedCell.columnPosition, selectedCell.rowPosition);
			final IDisplayConverter dataTypeConverter= configRegistry.getConfigAttribute(
					CellConfigAttributes.DISPLAY_CONVERTER, DisplayMode.EDIT, cell.getConfigLabels().getLabels() );
			if (dataTypeConverter != null) {
				converterSet.add(dataTypeConverter.getClass());
			}
			if (converterSet.size() > 1) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if all selected cells contain the same canonical value. This is needed for multi edit
	 * to know if the editor should be initialised with the value that is shared amongst all cells.
	 * @param selectionLayer The {@link SelectionLayer} to retrieve the current selection.
	 * @return <code>true</code> if all cells contain the same value, <code>false</code> if at least
	 * 			one cell contains another value.
	 */
	public static boolean isValueSame(final SelectionLayer selectionLayer) {
		Object lastSelectedValue= null;
		final Collection<ILayerCell> selectedCells= selectionLayer.getSelectedCells();
		for (final ILayerCell layerCell : selectedCells) {
			final Object cellValue= layerCell.getDataValue(0, null);
			if (lastSelectedValue == null) {
				lastSelectedValue= cellValue;
			}
			if ((cellValue != null && !cellValue.equals(lastSelectedValue))
					|| cellValue == null && lastSelectedValue != null) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if there is an active editor registered. If there is one, it is tried to
	 * commit the value that is currently entered there. 
	 * @return <code>false</code> if there is an open editor that can not be committed
	 * 			because of conversion/validation errors, <code>true</code> if there is
	 * 			no active open editor or it could be closed after committing the value.
	 */
	public static boolean commitAndCloseActiveEditor() {
		final ICellEditor activeCellEditor= ActiveCellEditorRegistry.getActiveCellEditor();
		if (activeCellEditor != null) {
			return activeCellEditor.commit(null, true);
		}
		return true;
	}

}
