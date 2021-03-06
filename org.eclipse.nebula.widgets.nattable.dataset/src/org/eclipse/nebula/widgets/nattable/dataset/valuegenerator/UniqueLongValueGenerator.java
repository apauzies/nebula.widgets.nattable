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
package org.eclipse.nebula.widgets.nattable.dataset.valuegenerator;

import java.util.Random;

import org.eclipse.nebula.widgets.nattable.dataset.generator.IValueGenerator;


public class UniqueLongValueGenerator implements IValueGenerator {

	private static long value = 0;
	
	public Object newValue(Random random) {
		return Long.valueOf(value++);
	}

}
