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
package org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.aggregator;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.aggregator.Aggregator.IAggregator;

/**
 * Simple aggregators (sum) for Numbers<br/>
 * FIXME: Would rather use https://code.google.com/p/generic-java-math/ if it
 * was in Orbit
 * 
 * @author Alexandre Pauzies
 * 
 */
public class BasicAggregators {

	private static class SumInt<T> implements IAggregator<T, Integer> {

		@Override
		public Integer aggregate(List<T> input, IColumnAccessor<T> columnAccessor, int columnIndex) {
			Integer result = 0;
			for (T i : input) {
				result += (Integer) columnAccessor.getDataValue(i, columnIndex);
			}
			return result;
		}

	}

	private static class SumDouble<T> implements IAggregator<T, Double> {

		@Override
		public Double aggregate(List<T> input, IColumnAccessor<T> columnAccessor, int columnIndex) {
			Double result = 0.0;
			for (T i : input) {
				result += (Double) columnAccessor.getDataValue(i, columnIndex);
			}
			return result;
		}

	}

	private static class SumLong<T> implements IAggregator<T, Long> {

		@Override
		public Long aggregate(List<T> input, IColumnAccessor<T> columnAccessor, int columnIndex) {
			Long result = 0L;
			for (T i : input) {
				result += (Long) columnAccessor.getDataValue(i, columnIndex);
			}
			return result;
		}

	}

	private static class Sum<T> implements IAggregator<T, Number> {

		@Override
		public Number aggregate(List<T> input, IColumnAccessor<T> columnAccessor, int columnIndex) {
			if (input.size() == 0) {
				return 0;
			}
			Object obj = columnAccessor.getDataValue(input.get(0), columnIndex);
			if (obj instanceof Integer) {
				return new SumInt<T>().aggregate(input, columnAccessor, columnIndex);
			} else if (obj instanceof Double) {
				return new SumDouble<T>().aggregate(input, columnAccessor, columnIndex);
			} else if (obj instanceof Float) {
				return new SumFloat<T>().aggregate(input, columnAccessor, columnIndex);
			} else if (obj instanceof Long) {
				return new SumLong<T>().aggregate(input, columnAccessor, columnIndex);
			}
			throw new IllegalStateException();
		}

	}

	private static class SumFloat<T> implements IAggregator<T, Float> {

		@Override
		public Float aggregate(List<T> input, IColumnAccessor<T> columnAccessor, int columnIndex) {
			Float result = 0F;
			for (T i : input) {
				result += (Float) columnAccessor.getDataValue(i, columnIndex);
			}
			return result;
		}

	}

	public static <T> IAggregator<T, Number> sum() {
		return new Sum<T>();
	}

	public static <T> IAggregator<T, Integer> sumInt() {
		return new SumInt<T>();
	}

	public static <T> IAggregator<T, Double> sumDouble() {
		return new SumDouble<T>();
	}

	public static <T> IAggregator<T, Long> sumLong() {
		return new SumLong<T>();
	}

	public static <T> IAggregator<T, Float> sumFloat() {
		return new SumFloat<T>();
	}

}
