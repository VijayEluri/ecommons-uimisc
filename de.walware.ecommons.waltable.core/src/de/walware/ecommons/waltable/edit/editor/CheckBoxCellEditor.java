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
package de.walware.ecommons.waltable.edit.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.walware.ecommons.waltable.edit.EditMode;
import de.walware.ecommons.waltable.painter.cell.CheckBoxPainter;


/**
 * {@link ICellEditor} implementation for checkbox editors.
 * Compared to a TextCellEditor, this editor will immediately commit
 * and close itself on interaction. This is the same behaviour like
 * a regular for a regular checkbox.
 */
public class CheckBoxCellEditor extends AbstractCellEditor {

	/**
	 * The current state of the checkbox stating the corresponding value.
	 */
	private boolean checked;
	
	/**
	 * The editor control which is a Canvas that paints the corresponding
	 * checkbox images. To adjust the look & feel for checkbox editors you need
	 * to look at {@link CheckBoxPainter}
	 */
	private Canvas canvas;

	/**
	 * As soon as the editor is activated, flip the current data value and commit it.
	 * The repaint will pick up the new value and flip the image.
	 * This is only done if the mouse click is done within the rectangle of the painted 
	 * checkbox image.
	 */
	@Override
	protected Control activateCell(final Composite parent, final Object originalCanonicalValue) {
		//if this editor was activated by clicking a letter or digit key, do nothing
		if (originalCanonicalValue instanceof Character) {
			return null;
		}
		
		setCanonicalValue(originalCanonicalValue);

		this.checked= !this.checked;

		this.canvas= createEditorControl(parent);

		commit(null, false);

		if (this.editMode == EditMode.INLINE) {
			// Close editor so will react to subsequent clicks on the cell
			if (this.canvas != null && !this.canvas.isDisposed()) {
				this.canvas.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						close();
					}
				});
			}
		}
		
		return this.canvas;
	}

	@Override
	public Boolean getEditorValue() {
		return Boolean.valueOf(this.checked);
	}

	/**
	 * Sets the given value to editor control. As this method is
	 * called by {@link AbstractCellEditor#setCanonicalValue(Object)} the 
	 * given value should be already a converted Boolean value. The only
	 * other values accepted in here are <code>null</code> which is interpreted
	 * as <code>false</code> and Strings than can be converted to Boolean
	 * directly. Every other object will result in setting the editor value
	 * to <code>false</code>.
	 * @param value The display value to set to the wrapped editor control.
	 */
	@Override
	public void setEditorValue(final Object value) {
		if (value == null) {
			this.checked= false;
		} 
		else {
			if (value instanceof Boolean) {
				this.checked= ((Boolean)value).booleanValue();
			} else if (value instanceof String) {
				this.checked= Boolean.valueOf((String) value).booleanValue();
			} else {
				this.checked= false;
			}
		}
	}

	@Override
	public Canvas getEditorControl() {
		return this.canvas;
	}

	@Override
	public Canvas createEditorControl(final Composite parent) {
		final Canvas canvas= new Canvas(parent, SWT.NONE);

		canvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(final MouseEvent e) {
				CheckBoxCellEditor.this.checked= !CheckBoxCellEditor.this.checked;
				canvas.redraw();
			}
		});

		return canvas;
	}

	@Override
	public boolean openMultiEditDialog() {
		//as it doesn't make sense to open a subdialog for checkbox multi editing, this is not supported
		return false;
	}
	
	@Override
	public boolean activateAtAnyPosition() {
		//as the checkbox should only change its value if the icon that represents the checkbox is
		//clicked, this method needs to return false so the IMouseEventMatcher can react on that.
		//Note that on return false here creates the need to add a special matcher for this editor
		//to be activated.
		return false;
	}
	
}
