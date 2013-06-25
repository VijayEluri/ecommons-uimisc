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
package org.eclipse.nebula.widgets.nattable.ui.mode;


import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.edit.command.EditUtils;
import org.eclipse.nebula.widgets.nattable.ui.NatEventData;
import org.eclipse.nebula.widgets.nattable.ui.action.DragModeEventHandler;
import org.eclipse.nebula.widgets.nattable.ui.action.IDragMode;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseAction;
import org.eclipse.nebula.widgets.nattable.ui.util.CancelableRunnable;
import org.eclipse.swt.events.MouseEvent;

public class MouseModeEventHandler extends AbstractModeEventHandler {
	
	private final NatTable natTable;
	
	private MouseEvent initialMouseDownEvent;
	
	private IMouseAction singleClickAction;
	
	private IMouseAction doubleClickAction;
	
	private boolean mouseDown;
	
	private IDragMode dragMode;
	
	private SingleClickRunnable singleClickRunnable;
	
	private boolean doubleClick;
	
	public MouseModeEventHandler(ModeSupport modeSupport, NatTable natTable, MouseEvent initialMouseDownEvent, IMouseAction singleClickAction, IMouseAction doubleClickAction, IDragMode dragMode) {
		super(modeSupport);
		
		this.natTable = natTable;
		
		mouseDown = true;
		
		this.initialMouseDownEvent = initialMouseDownEvent;
		
		this.singleClickAction = singleClickAction;
		this.doubleClickAction = doubleClickAction;
		this.dragMode = dragMode;
	}
	
	@Override
	public void mouseUp(MouseEvent event) {
		mouseDown = false;
		doubleClick = false;
		
		if (singleClickAction != null) {
			//convert/validate/commit/close possible open editor
			//needed in case of conversion/validation errors to cancel any action
			if (EditUtils.commitAndCloseActiveEditor()) {
				if (doubleClickAction != null) {
					// If a doubleClick action is registered, wait to see if this mouseUp is part of a doubleClick or not.
					singleClickRunnable = new SingleClickRunnable(singleClickAction, event);
					event.display.timerExec(event.display.getDoubleClickTime(), singleClickRunnable);
				} else {
					executeSingleClickAction(singleClickAction, event);
				}
			}
		} else {
			// No single or double click action registered when mouseUp detected. Switch back to normal mode.
			switchMode(Mode.NORMAL_MODE);
		}
	}
	
	@Override
	public void mouseDoubleClick(MouseEvent event) {
		//double click event is fired after second mouse up event, so it needs to be set to true here
		//this way the SingleClickRunnable knows that it should not execute as a double click was performed
		doubleClick = true;
		
		//convert/validate/commit/close possible open editor
		//needed in case of conversion/validation errors to cancel any action
		if (EditUtils.commitAndCloseActiveEditor()) {
			if (doubleClickAction != null) {
				event.data = NatEventData.createInstanceFromEvent(event);
				doubleClickAction.run(natTable, event);
				// Double click action complete. Switch back to normal mode.
				switchMode(Mode.NORMAL_MODE);
			}
		}
	}
	
	@Override
	public synchronized void mouseMove(MouseEvent event) {
		if (mouseDown && dragMode != null) {
			if (EditUtils.commitAndCloseActiveEditor()) {
				dragMode.mouseDown(natTable, initialMouseDownEvent);
				switchMode(new DragModeEventHandler(getModeSupport(), natTable, dragMode));
			}
			else {
				switchMode(Mode.NORMAL_MODE);
			}
		} else {
			// No drag mode registered when mouseMove detected. Switch back to normal mode.
			switchMode(Mode.NORMAL_MODE);
		}
	}
	
	private void executeSingleClickAction(IMouseAction action, MouseEvent event) {
		//convert/validate/commit/close possible open editor
		//needed in case of conversion/validation errors to cancel any action
		if (EditUtils.commitAndCloseActiveEditor()) {
			event.data = NatEventData.createInstanceFromEvent(event);
			action.run(natTable, event);
			// Single click action complete. Switch back to normal mode.
			switchMode(Mode.NORMAL_MODE);
		}
	}
	
	class SingleClickRunnable extends CancelableRunnable {

		private IMouseAction action;
		
		private MouseEvent event;
		
		public SingleClickRunnable(IMouseAction action, MouseEvent event) {
			this.action = action;
			this.event = event;
		}
		
		public void run() {
			if (!doubleClick) {
				executeSingleClickAction(action, event);
			}
		}
		
	}
	
}
