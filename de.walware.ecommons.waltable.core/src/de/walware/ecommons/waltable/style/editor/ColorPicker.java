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
package de.walware.ecommons.waltable.style.editor;


import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.walware.ecommons.waltable.util.GUIHelper;

/**
 * A button that displays a solid block of color and allows the user to pick a color. The user can double click on the
 * button in order to change the selected color which also changes the background color of the button.
 *
 */
public class ColorPicker extends CLabel {

    private Color selectedColor;
	private Image image;

    public ColorPicker(final Composite parent, final Color originalColor) {
        super(parent, SWT.SHADOW_OUT);
        if (originalColor == null)
		 {
			throw new IllegalArgumentException("null"); //$NON-NLS-1$
		}
        this.selectedColor= originalColor;
        setImage(getColorImage(originalColor));
        addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseDown(final MouseEvent e) {
                        final ColorDialog dialog= new ColorDialog(new Shell(Display.getDefault(), SWT.SHELL_TRIM));
                        dialog.setRGB(ColorPicker.this.selectedColor.getRGB());
                        final RGB selected= dialog.open();
                        if (selected != null) {
                            update(selected);
                        }
                    }
                });
    }

    private Image getColorImage(final Color color){
    	final Display display= Display.getCurrent();
		this.image= new Image(display, new Rectangle(10, 10, 70, 20));
        final GC gc= new GC(this.image);
        gc.setBackground(color);
        gc.fillRectangle(this.image.getBounds());
        gc.dispose();
        return this.image;
    }

    private void update(final RGB selected) {
        this.selectedColor= GUIHelper.getColor(selected);
        setImage(getColorImage(this.selectedColor));
    }

    /**
     * @return the Color most recently selected by the user. <em>Note that it is the responsibility of the client to
     *         dispose this resource</em>
     */
    public Color getSelectedColor() {
        return this.selectedColor;
    }

    /**
     * Set the current selected color that will be displayed by the picker. <em>Note that this class is not responsible
     * for destroying the given Color object. It does not take ownership. Instead it will create its own internal
     * copy of the given Color resource.</em>
     *
     * @param backgroundColor
     */
    public void setSelectedColor(final Color backgroundColor) {
        if (backgroundColor == null)
		 {
			throw new IllegalArgumentException("null"); //$NON-NLS-1$
		}
        update(backgroundColor.getRGB());
    }

    @Override
    public void dispose() {
    	super.dispose();
    	this.image.dispose();
    }
}
