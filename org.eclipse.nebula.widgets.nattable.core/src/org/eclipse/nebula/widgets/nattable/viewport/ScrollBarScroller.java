/*******************************************************************************
 * Copyright (c) Sep 3, 2013 esp and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    esp - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.nattable.viewport;

import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;

public class ScrollBarScroller implements IScroller<ScrollBar> {

	private ScrollBar scrollBar;

	public ScrollBarScroller(ScrollBar scrollBar) {
		this.scrollBar = scrollBar;
	}
	
	@Override
	public ScrollBar getUnderlying() {
		return scrollBar;
	}
	
	@Override
	public boolean isDisposed() {
		return scrollBar.isDisposed();
	}

	@Override
	public void addListener(int eventType, Listener listener) {
		scrollBar.addListener(eventType, listener);
	}

	@Override
	public void removeListener(int eventType, Listener listener) {
		scrollBar.removeListener(eventType, listener);
	}
	
	@Override
	public int getSelection() {
		return scrollBar.getSelection();
	}

	@Override
	public void setSelection(int value) {
		scrollBar.setSelection(value);
	}

	@Override
	public int getMaximum() {
		return scrollBar.getMaximum();
	}
	
	@Override
	public void setMaximum(int value) {
		scrollBar.setMaximum(value);
	}
	
	@Override
	public int getPageIncrement() {
		return scrollBar.getPageIncrement();
	}

	@Override
	public void setPageIncrement(int value) {
		scrollBar.setPageIncrement(value);
	}

	@Override
	public int getThumb() {
		return scrollBar.getThumb();
	}
	
	@Override
	public void setThumb(int value) {
		scrollBar.setThumb(value);
	}

	@Override
	public int getIncrement() {
		return scrollBar.getIncrement();
	}
	
	@Override
	public void setIncrement(int value) {
		scrollBar.setIncrement(value);
	}

	@Override
	public boolean getEnabled() {
		return scrollBar.getEnabled();
	}
	
	@Override
	public void setEnabled(boolean b) {
		scrollBar.setEnabled(b);
	}

	@Override
	public boolean getVisible() {
		return scrollBar.getVisible();
	}
	
	@Override
	public void setVisible(boolean b) {
		scrollBar.setVisible(b);
	}

}
