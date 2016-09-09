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
package de.walware.ecommons.waltable.ui.mode;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;

import de.walware.ecommons.waltable.NatTable;


/**
 * Modal event handler for NatTable. This class acts as a proxy event listener.
 * It manages a set of IModeEventHandler instances which control the actual
 * event handling for a given mode. This allows the event handling behavior for
 * different modes to be grouped together and isolated from each other.
 */
public class ModeSupport implements KeyListener, MouseListener,
		MouseMoveListener, MouseTrackListener, FocusListener {

	private final Map<String, IModeEventHandler> modeEventHandlerMap= new HashMap<>();

	private IModeEventHandler currentModeEventHandler;

	public ModeSupport(final NatTable natTable) {
		natTable.addKeyListener(this);
		natTable.addMouseListener(this);
		natTable.addMouseMoveListener(this);
		natTable.addMouseTrackListener(this);
		natTable.addFocusListener(this);
	}

	/**
	 * Register an event handler to handle events for a given mode.
	 * 
	 * @param mode
	 *            The mode.
	 * @param modeEventHandler
	 *            An IModeEventHandler instance that will handle events in the
	 *            given mode.
	 * 
	 * @see IModeEventHandler
	 */
	public void registerModeEventHandler(final String mode,
			final IModeEventHandler modeEventHandler) {
		this.modeEventHandlerMap.put(mode, modeEventHandler);
	}

	/**
	 * Switch to the given mode.
	 * 
	 * @param mode
	 *            The target mode to switch to.
	 */
	public void switchMode(final String mode) {
		if (this.currentModeEventHandler != null) {
			this.currentModeEventHandler.cleanup();
		}
		this.currentModeEventHandler= this.modeEventHandlerMap.get(mode);
	}
	
	public void switchMode(final IModeEventHandler modeEventHandler) {
		if (this.currentModeEventHandler != null) {
			this.currentModeEventHandler.cleanup();
		}
		this.currentModeEventHandler= modeEventHandler;
	}

	@Override
	public void keyPressed(final KeyEvent event) {
		this.currentModeEventHandler.keyPressed(event);
	}

	@Override
	public void keyReleased(final KeyEvent event) {
		this.currentModeEventHandler.keyReleased(event);
	}

	@Override
	public void mouseDoubleClick(final MouseEvent event) {
		this.currentModeEventHandler.mouseDoubleClick(event);
	}

	@Override
	public void mouseDown(final MouseEvent event) {
		this.currentModeEventHandler.mouseDown(event);
	}

	@Override
	public void mouseUp(final MouseEvent event) {
		this.currentModeEventHandler.mouseUp(event);
	}

	@Override
	public void mouseMove(final MouseEvent event) {
		this.currentModeEventHandler.mouseMove(event);
	}
	
	@Override
	public void mouseEnter(final MouseEvent e) {
		this.currentModeEventHandler.mouseEnter(e);
	}
	
	@Override
	public void mouseExit(final MouseEvent e) {
		this.currentModeEventHandler.mouseExit(e);
	}
	
	@Override
	public void mouseHover(final MouseEvent e) {
		this.currentModeEventHandler.mouseHover(e);
	}

	@Override
	public void focusGained(final FocusEvent event) {
		this.currentModeEventHandler.focusGained(event);
	}

	@Override
	public void focusLost(final FocusEvent event) {
		this.currentModeEventHandler.focusLost(event);
	}

}
