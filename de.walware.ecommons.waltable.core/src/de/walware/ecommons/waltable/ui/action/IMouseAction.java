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
package de.walware.ecommons.waltable.ui.action;

import org.eclipse.swt.events.MouseEvent;

import de.walware.ecommons.waltable.NatTable;


/**
 * Implementing this interface will create an action that translates an SWT
 * MouseEvent into a command that should be executed in the NatTable.
 * <p>
 * This concept allows to catch general MouseEvents on the NatTable control
 * itself, and translate it into commands that correspond to a cell which
 * is determined via x/y coordinates.
 */
public interface IMouseAction {

	/**
	 * Translates the SWT MouseEvent to a NatTable command and executes
	 * that command accordingly.
	 * 
	 * @param natTable The NatTable instance on which the MouseEvent was
	 * 			fired and on which the command should be executed.
	 * @param event The received MouseEvent.
	 */
	void run(NatTable natTable, MouseEvent event);
}
