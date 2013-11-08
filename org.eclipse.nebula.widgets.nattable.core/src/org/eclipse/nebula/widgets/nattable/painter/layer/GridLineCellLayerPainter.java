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
package org.eclipse.nebula.widgets.nattable.painter.layer;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;


public class GridLineCellLayerPainter extends CellLayerPainter {
	
	private final Color gridColor;
	
	public GridLineCellLayerPainter(final Color gridColor) {
		this.gridColor = gridColor;
	}
	
	public GridLineCellLayerPainter() {
		this.gridColor = GUIHelper.COLOR_GRAY;
	}
	
	public Color getGridColor() {
		return gridColor;
	}
	
	@Override
	public void paintLayer(ILayer natLayer, GC gc, int xOffset, int yOffset, Rectangle rectangle, IConfigRegistry configRegistry) {
		//Draw GridLines
		drawGridLines(natLayer, gc, rectangle);

		super.paintLayer(natLayer, gc, xOffset, yOffset, rectangle, configRegistry);
	}
	
	@Override
	public Rectangle adjustCellBounds(int columnPosition, int rowPosition, Rectangle bounds) {
		return new Rectangle(bounds.x, bounds.y, Math.max(bounds.width - 1, 0), Math.max(bounds.height - 1, 0));
	}
	
	protected void drawGridLines(ILayer natLayer, GC gc, Rectangle rectangle) {
		gc.setForeground(gridColor);
		
		drawHorizontalLines(natLayer, gc, rectangle);
		drawVerticalLines(natLayer, gc, rectangle);
	}

	private void drawHorizontalLines(ILayer natLayer, GC gc, Rectangle rectangle) {
		int endX = rectangle.x + Math.min(natLayer.getWidth() - 1, rectangle.width);
		
		//this can happen on resizing if there is no CompositeLayer involved
		//without this check grid line fragments may be rendered below the last column
		if (endX > natLayer.getWidth()) return;
		
		int rowPositionByY = natLayer.getRowPositionByY(rectangle.y + rectangle.height);
		int maxRowPosition = rowPositionByY > 0 ? Math.min(natLayer.getRowCount(), rowPositionByY) : natLayer.getRowCount();
		for (int rowPosition = natLayer.getRowPositionByY(rectangle.y); rowPosition < maxRowPosition; rowPosition++) {
			final int size = natLayer.getRowHeightByPosition(rowPosition);
			if (size > 0) {
				int y = natLayer.getStartYOfRowPosition(rowPosition) + size - 1;
				gc.drawLine(rectangle.x, y, endX, y);
			}
		}
	}

	private void drawVerticalLines(ILayer natLayer, GC gc, Rectangle rectangle) {
		int endY = rectangle.y + Math.min(natLayer.getHeight() - 1, rectangle.height);
		
		//this can happen on resizing if there is no CompositeLayer involved
		//without this check grid line fragments may be rendered below the last row
		if (endY > natLayer.getHeight()) return;

		int columnPositionByX = natLayer.getColumnPositionByX(rectangle.x + rectangle.width);
		int maxColumnPosition = columnPositionByX > 0 ? Math.min(natLayer.getColumnCount(), columnPositionByX) : natLayer.getColumnCount();
		for (int columnPosition = natLayer.getColumnPositionByX(rectangle.x); columnPosition < maxColumnPosition; columnPosition++) {
			final int size = natLayer.getColumnWidthByPosition(columnPosition);
			if (size > 0) {
				int x = natLayer.getStartXOfColumnPosition(columnPosition) + size - 1;
				gc.drawLine(x, rectangle.y, x, endY);
			}
		}
	}

}
