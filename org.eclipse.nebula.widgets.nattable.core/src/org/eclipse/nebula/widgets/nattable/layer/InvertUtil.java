package org.eclipse.nebula.widgets.nattable.layer;

import org.eclipse.swt.graphics.Rectangle;

public class InvertUtil {
	
	public static Rectangle invertRectangle(Rectangle rect) {
		if (rect != null)
			return new Rectangle(rect.y, rect.x, rect.height, rect.width);
		else
			return null;
	}

}