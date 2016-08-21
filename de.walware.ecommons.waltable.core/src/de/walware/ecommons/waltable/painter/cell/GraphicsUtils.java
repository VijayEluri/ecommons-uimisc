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
package de.walware.ecommons.waltable.painter.cell;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

/**
 * This class contains utility methods for drawing graphics
 * 
 * @see <a href="http://java-gui.info/Apress-The.Definitive.Guide.to.SWT.and.JFace/8886final/LiB0095.html">GC snippets</a>
 */
public class GraphicsUtils {
	
	
	public static final int check(final long pixel) {
		if (pixel < Integer.MIN_VALUE || pixel > Integer.MAX_VALUE) {
			throw new IndexOutOfBoundsException();
		}
		return (int) pixel;
	}
	
	public static final int safe(final long pixel) {
		return (pixel <= Integer.MIN_VALUE) ? Integer.MIN_VALUE :
			((pixel >= Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) pixel);
	}
	
	public static final Rectangle safe(final long x, final long y, final long width, final long height) {
		final int sx= safe(x);
		final int sy= safe(y);
		return new Rectangle(sx, sy, safe(x + width) - sx, safe(y + height) - sy);
	}
	
	public static final Rectangle safe(final de.walware.ecommons.waltable.coordinate.LRectangle rect) {
		return safe(rect.x, rect.y, rect.width, rect.height);
	}
	
  /**
   * Draws text vertically (rotates plus or minus 90 degrees). Uses the current
   * font, color, and background.
   * <dl>
   * <dt><b>Styles: </b></dt>
   * <dd>UP, DOWN</dd>
   * </dl>
   *
   * @param string the text to draw
   * @param x the x coordinate of the top left corner of the drawing rectangle
   * @param y the y coordinate of the top left corner of the drawing rectangle
   * @param gc the GC on which to draw the text
   * @param style the style (SWT.UP or SWT.DOWN)
   *           <p>
   *           Note: Only one of the style UP or DOWN may be specified.
   *           </p>
   */
  public static void drawVerticalText(final String string, final int x, final int y, final GC gc, final int style) {
	  drawVerticalText(string, x, y, false, false, true, gc, style);
  }
	  
  /**
   * Draws text vertically (rotates plus or minus 90 degrees). Uses the current
   * font, color, and background.
   * <dl>
   * <dt><b>Styles: </b></dt>
   * <dd>UP, DOWN</dd>
   * </dl>
   *
   * @param string the text to draw
   * @param x the x coordinate of the top left corner of the drawing rectangle
   * @param y the y coordinate of the top left corner of the drawing rectangle
   * @param underline set to <code>true</code> to render the text underlined
   * @param strikethrough set to <code>true</code> to render the text strikethrough
   * @param paintBackground set to <code>false</code> to render the background transparent.
   * 			Needed for example to render the background with an image or gradient with another painter
   * 			so the text drawn here should have no background.
   * @param gc the GC on which to draw the text
   * @param style the style (SWT.UP or SWT.DOWN)
   *           <p>
   *           Note: Only one of the style UP or DOWN may be specified.
   *           </p>
   */
  public static void drawVerticalText(final String string, final int x, final int y, 
		  final boolean underline, final boolean strikethrough, final boolean paintBackground, 
		  final GC gc, final int style) {
    // Get the current display
    final Display display= Display.getCurrent();
    if (display == null) {
		SWT.error(SWT.ERROR_THREAD_INVALID_ACCESS);
	}

    // Determine string's dimensions
//    FontMetrics fm= gc.getFontMetrics();
    final Point pt= gc.textExtent(string.trim());

    // Create an image the same size as the string
    final Image stringImage= new Image(display, pt.x, pt.y);

    // Create a GC so we can draw the image
    final GC stringGc= new GC(stringImage);

    // Set attributes from the original GC to the new GC
    stringGc.setAntialias(gc.getAntialias());
    stringGc.setTextAntialias(gc.getTextAntialias());
    stringGc.setForeground(gc.getForeground());
    stringGc.setBackground(gc.getBackground());
    stringGc.setFont(gc.getFont());

    // Fill the image with the specified background color
    // to avoid white spaces if the text does not fill the 
    // whole image (e.g. on new lines)
    stringGc.fillRectangle(0, 0, pt.x, pt.y);
    
    // Draw the text onto the image
    stringGc.drawText(string, 0, 0);
	
    //draw underline and/or strikethrough
    if (underline || strikethrough) {
		//check and draw underline and strikethrough separately so it is possible to combine both
		if (underline) {
			//y= start y of text + font height 
			// - half of the font descent so the underline is between the baseline and the bottom
			final int underlineY= pt.y - (stringGc.getFontMetrics().getDescent() / 2);
			stringGc.drawLine(
					0, 
					underlineY, 
					pt.x, 
					underlineY);
		}
		
		if (strikethrough) {
			//y= start y of text + half of font height + ascent so lower case characters are
			//also strikethrough
			final int strikeY= (pt.y / 2) + (stringGc.getFontMetrics().getLeading() / 2);
			stringGc.drawLine(
					0, 
					strikeY, 
					pt.x, 
					strikeY);
		}
	}

    // Draw the image vertically onto the original GC
    drawVerticalImage(stringImage, x, y, paintBackground, gc, style);

    // Dispose the new GC
    stringGc.dispose();

    // Dispose the image
    stringImage.dispose();
  }

  /**
   * Draws an image vertically (rotates plus or minus 90 degrees)
   * <dl>
   * <dt><b>Styles: </b></dt>
   * <dd>UP, DOWN</dd>
   * </dl>
   *
   * @param image the image to draw
   * @param x the x coordinate of the top left corner of the drawing rectangle
   * @param y the y coordinate of the top left corner of the drawing rectangle
   * @param gc the GC on which to draw the image
   * @param style the style (SWT.UP or SWT.DOWN)
   *           <p>
   *           Note: Only one of the style UP or DOWN may be specified.
   *           </p>
   */
  public static void drawVerticalImage(final Image image, final int x, final int y, final GC gc, final int style) {
	  drawVerticalImage(image, x, y, true, gc, style);
  }

  /**
   * Draws an image vertically (rotates plus or minus 90 degrees)
   * <dl>
   * <dt><b>Styles: </b></dt>
   * <dd>UP, DOWN</dd>
   * </dl>
   *
   * @param image the image to draw
   * @param x the x coordinate of the top left corner of the drawing rectangle
   * @param y the y coordinate of the top left corner of the drawing rectangle
   * @param paintBackground set to <code>false</code> to render the background transparent.
   * 			Needed for example to render the background with an image or gradient with another painter
   * 			so the text drawn here should have no background.
   * @param gc the GC on which to draw the image
   * @param style the style (SWT.UP or SWT.DOWN)
   *           <p>
   *           Note: Only one of the style UP or DOWN may be specified.
   *           </p>
   */
  public static void drawVerticalImage(final Image image, final int x, final int y, final boolean paintBackground, final GC gc, final int style) {
    // Get the current display
    final Display display= Display.getCurrent();
    if (display == null) {
		SWT.error(SWT.ERROR_THREAD_INVALID_ACCESS);
	}

    // Use the image's data to create a rotated image's data
    final ImageData sd= image.getImageData();
    final ImageData dd= new ImageData(sd.height, sd.width, sd.depth, sd.palette);
    dd.transparentPixel= sd.transparentPixel;
    
    //set the defined backgroundcolor to be transparent
    if (!paintBackground) {
    	dd.transparentPixel= sd.palette.getPixel(gc.getBackground().getRGB());
    }

    // Determine which way to rotate, depending on up or down
    final boolean up= (style & SWT.UP) == SWT.UP;

    // Run through the horizontal pixels
    for (int sx= 0; sx < sd.width; sx++) {
      // Run through the vertical pixels
      for (int sy= 0; sy < sd.height; sy++) {
        // Determine where to move pixel to in destination image data
        final int dx= up ? sy : sd.height - sy - 1;
        final int dy= up ? sd.width - sx - 1 : sx;
        // Swap the x, y source data to y, x in the destination
        dd.setPixel(dx, dy, sd.getPixel(sx, sy));
      }
    }

    // Create the vertical image
    final Image vertical= new Image(display, dd);

    // Draw the vertical image onto the original GC
    gc.drawImage(vertical, x, y);

    // Dispose the vertical image
    vertical.dispose();
  }

  /**
   * Creates an image containing the specified text, rotated either plus or minus
   * 90 degrees.
   * <dl>
   * <dt><b>Styles: </b></dt>
   * <dd>UP, DOWN</dd>
   * </dl>
   *
   * @param text the text to rotate
   * @param font the font to use
   * @param foreground the color for the text
   * @param background the background color
   * @param style direction to rotate (up or down)
   * @return Image
   *          <p>
   *          Note: Only one of the style UP or DOWN may be specified.
   *          </p>
   */
  public static Image createRotatedText(final String text, final Font font, final Color foreground,
      final Color background, final int style) {
    // Get the current display
    final Display display= Display.getCurrent();
    if (display == null) {
		SWT.error(SWT.ERROR_THREAD_INVALID_ACCESS);
	}

    // Create a GC to calculate font's dimensions
    GC gc= new GC(display);
    gc.setFont(font);

    // Determine string's dimensions
//    FontMetrics fm= gc.getFontMetrics();
    final Point pt= gc.textExtent(text);

    // Dispose that gc
    gc.dispose();

    // Create an image the same size as the string
    final Image stringImage= new Image(display, pt.x, pt.y);
    // Create a gc for the image
    gc= new GC(stringImage);
    gc.setFont(font);
    gc.setForeground(foreground);
    gc.setBackground(background);

    // Draw the text onto the image
    gc.drawText(text, 0, 0);

    // Draw the image vertically onto the original GC
    final Image image= createRotatedImage(stringImage, style);

    // Dispose the new GC
    gc.dispose();

    // Dispose the horizontal image
    stringImage.dispose();

    // Return the rotated image
    return image;
  }

  /**
   * Creates a rotated image (plus or minus 90 degrees)
   * <dl>
   * <dt><b>Styles: </b></dt>
   * <dd>UP, DOWN</dd>
   * </dl>
   *
   * @param image the image to rotate
   * @param style direction to rotate (up or down)
   * @return Image
   *          <p>
   *          Note: Only one of the style UP or DOWN may be specified.
   *          </p>
   */
  public static Image createRotatedImage(final Image image, final int style) {
    // Get the current display
    final Display display= Display.getCurrent();
    if (display == null) {
		SWT.error(SWT.ERROR_THREAD_INVALID_ACCESS);
	}

    // Use the image's data to create a rotated image's data
    final ImageData sd= image.getImageData();
    final ImageData dd= new ImageData(sd.height, sd.width, sd.depth, sd.palette);

    // Determine which way to rotate, depending on up or down
    final boolean up= (style & SWT.UP) == SWT.UP;

    // Run through the horizontal pixels
    for (int sx= 0; sx < sd.width; sx++) {
      // Run through the vertical pixels
      for (int sy= 0; sy < sd.height; sy++) {
        // Determine where to move pixel to in destination image data
        final int dx= up ? sy : sd.height - sy - 1;
        final int dy= up ? sd.width - sx - 1 : sx;

        // Swap the x, y source data to y, x in the destination
        dd.setPixel(dx, dy, sd.getPixel(sx, sy));
      }
    }

    // Create the vertical image
    return new Image(display, dd);
  }
  
}
