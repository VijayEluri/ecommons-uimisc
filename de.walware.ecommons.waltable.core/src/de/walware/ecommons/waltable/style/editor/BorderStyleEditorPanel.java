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
package de.walware.ecommons.waltable.style.editor;

import static org.eclipse.swt.SWT.CHECK;
import static org.eclipse.swt.SWT.NONE;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.walware.ecommons.waltable.Messages;
import de.walware.ecommons.waltable.style.BorderStyle;
import de.walware.ecommons.waltable.style.BorderStyle.LineStyle;
import de.walware.ecommons.waltable.util.GUIHelper;

/**
 * EditorPanel for editing a border style.
 */
public class BorderStyleEditorPanel extends AbstractEditorPanel<BorderStyle> {

    private BorderThicknessPicker thicknessPicker;
    private LineStylePicker lineStylePicker;
    private ColorPicker colorPicker;
    private Button noBordersCheckBox;

    @Override
    public String getEditorName() {
        return Messages.getString("BorderStyleEditorPanel.editorName"); //$NON-NLS-1$
    }

    public BorderStyleEditorPanel(final Composite parent, final int style) {
        super(parent, style);
        initComponents();
    }

    public void initComponents() {
        final GridLayout gridLayout= new GridLayout(2, false);
        gridLayout.marginLeft= 10;
		setLayout(gridLayout);

        new Label(this, NONE).setText(Messages.getString("BorderStyleEditorPanel.noBorder")); //$NON-NLS-1$

        this.noBordersCheckBox= new Button(this, CHECK);
        this.noBordersCheckBox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                    final boolean noBorder= BorderStyleEditorPanel.this.noBordersCheckBox.getSelection();
                    BorderStyleEditorPanel.this.colorPicker.setEnabled(!noBorder);
                    BorderStyleEditorPanel.this.thicknessPicker.setEnabled(!noBorder);
                    BorderStyleEditorPanel.this.lineStylePicker.setEnabled(!noBorder);
            }
        });

        new Label(this, NONE).setText(Messages.getString("BorderStyleEditorPanel.color")); //$NON-NLS-1$
        this.colorPicker= new ColorPicker(this, GUIHelper.COLOR_WIDGET_BORDER);

        new Label(this, NONE).setText(Messages.getString("BorderStyleEditorPanel.lineStyle")); //$NON-NLS-1$
        this.lineStylePicker= new LineStylePicker(this);

        new Label(this, NONE).setText(Messages.getString("BorderStyleEditorPanel.thickness")); //$NON-NLS-1$
        this.thicknessPicker= new BorderThicknessPicker(this);

        // By default, no border is selected and all controls are disabled
        this.noBordersCheckBox.setSelection(true);
        this.colorPicker.setEnabled(false);
        this.thicknessPicker.setEnabled(false);
        this.lineStylePicker.setEnabled(false);
    }

    private void disableEditing() {
        this.colorPicker.setEnabled(false);
        this.thicknessPicker.setEnabled(false);
        this.lineStylePicker.setEnabled(false);
    }

    @Override
	public void edit(final BorderStyle bstyle) throws Exception {
        if (bstyle != null) {
            this.noBordersCheckBox.setSelection(false);
            this.colorPicker.setSelectedColor(bstyle.getColor());
            this.lineStylePicker.setSelectedLineStyle(bstyle.getLineStyle());
            this.thicknessPicker.setSelectedThickness(bstyle.getThickness());
        } else {
            this.noBordersCheckBox.setSelection(true);
            disableEditing();
        }
    }

    @Override
	public BorderStyle getNewValue() {
        if (!this.noBordersCheckBox.getSelection()) {
            final Color borderColor= this.colorPicker.getSelectedColor();
            final LineStyle lineStyle= this.lineStylePicker.getSelectedLineStyle();
            final int borderThickness= this.thicknessPicker.getSelectedThickness();
            return new BorderStyle(borderThickness, borderColor, lineStyle);
        }
        return null;
    }
}
