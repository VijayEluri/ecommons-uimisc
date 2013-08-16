/*******************************************************************************
 * Copyright (c) 2013 Dirk Fauth and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Dirk Fauth <dirk.fauth@gmail.com> - initial API and implementation
 *******************************************************************************/
// ~
package org.eclipse.nebula.widgets.nattable.edit.gui;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import org.eclipse.nebula.widgets.nattable.coordinate.Direction;


/**
 * This implementation is a proof of concept for special cell editors that wrap dialogs.
 * The {@link FileDialog} is wrapped by this implementation. It will open the default
 * file selection dialog on trying to activate the cell editor.
 */
public class FileDialogCellEditor extends AbstractDialogCellEditor {

	/**
	 * The selection result of the {@link FileDialog}. Needed to update the data model
	 * after closing the dialog.
	 */
	private String selectedFile;
	/**
	 * Flag to determine whether the dialog was closed or if it is still open.
	 */
	private boolean closed = false;
	
	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.nattable.edit.editor.AbstractDialogCellEditor#open()
	 */
	@Override
	public int open() {
		selectedFile = getDialogInstance().open();
		if (selectedFile == null) {
			closed = true;
			return Window.CANCEL;
		}
		else {
			commit(null);
			closed = true;
			return Window.OK;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.nattable.edit.editor.AbstractDialogCellEditor#createDialogInstance()
	 */
	@Override
	public FileDialog createDialogInstance() {
		closed = false;
		return new FileDialog(this.parent.getShell(), SWT.OPEN);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.nattable.edit.editor.AbstractDialogCellEditor#getDialogInstance()
	 */
	@Override
	public FileDialog getDialogInstance() {
		return (FileDialog) this.dialog;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.nattable.edit.editor.AbstractDialogCellEditor#getEditorValue()
	 */
	@Override
	public Object getEditorValue() {
		return this.selectedFile;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.nattable.edit.editor.AbstractDialogCellEditor#setEditorValue(java.lang.Object)
	 */
	@Override
	public void setEditorValue(Object value) {
		//do nothing ... usually it should set the selection
	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.nattable.edit.editor.AbstractDialogCellEditor#close()
	 */
	@Override
	public void close() {
		//as the FileDialog does not support a programmatical way of closing, this method is forced to do nothing
	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.nattable.edit.editor.AbstractDialogCellEditor#isClosed()
	 */
	@Override
	public boolean isClosed() {
		return this.closed;
	}

}