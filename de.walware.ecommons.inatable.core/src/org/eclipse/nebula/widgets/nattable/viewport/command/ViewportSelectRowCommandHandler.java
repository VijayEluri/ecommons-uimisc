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
// ~Selection
package org.eclipse.nebula.widgets.nattable.viewport.command;

import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayer;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectRowsCommand;


public class ViewportSelectRowCommandHandler extends AbstractLayerCommandHandler<ViewportSelectRowCommand> {


	private final AbstractLayer viewportLayer;


	public ViewportSelectRowCommandHandler(AbstractLayer viewportLayer) {
		this.viewportLayer = viewportLayer;
	}


	public Class<ViewportSelectRowCommand> getCommandClass() {
		return ViewportSelectRowCommand.class;
	}


	@Override
	protected boolean doCommand(ViewportSelectRowCommand command) {
		viewportLayer.doCommand(new SelectRowsCommand(viewportLayer,
				0, command.getRowPositions(),
				command.getRowPositionToReveal(), command.getSelectionFlags() ));
		return true;
	}

}
