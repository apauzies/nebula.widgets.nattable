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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByColumnAccessor;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByModel;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByObject;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.matchers.Matcher;

/**
 * To enable summary in the group row<br/>
 * Find out which elements are in a group and help with aggregation (sum, avg
 * etc...)
 * 
 * @author Alexandre Pauzies
 * @param <T>
 */
public class Aggregator<T> {

	/** The original event list */
	private final EventList<T> eventList;

	/** Map the group to a dynamic list of group elements */
	private final Map<GroupByObject, FilterList<T>> filtersByGroup = new ConcurrentHashMap<GroupByObject, FilterList<T>>();

	private final IColumnAccessor<T> columnAccessor;

	private final AggregatorColumnAccessor aggregatorColumnAccessor;

	public Aggregator(GroupByModel groupByModel, EventList<T> eventList, IColumnAccessor<T> columnAccessor,
			Map<Integer, IAggregator<T, ?>> aggregatorByColumn) {
		this.eventList = eventList;
		this.columnAccessor = columnAccessor;
		this.aggregatorColumnAccessor = new AggregatorColumnAccessor(columnAccessor, aggregatorByColumn);
		// When grouping change, get rid of all the filter lists
		groupByModel.addObserver(new Observer() {

			@Override
			public void update(Observable o, Object arg) {
				filtersByGroup.clear();
			}

		});
	}

	public class AggregatorColumnAccessor extends GroupByColumnAccessor<T> {

		private final IColumnAccessor<T> columnAccessor;
		private final Map<Integer, IAggregator<T, ?>> aggregatorByColumn;

		public AggregatorColumnAccessor(IColumnAccessor<T> columnAccessor,
				Map<Integer, IAggregator<T, ?>> aggregatorByColumn) {
			super(columnAccessor);
			this.columnAccessor = columnAccessor;
			this.aggregatorByColumn = aggregatorByColumn;
		}

		@SuppressWarnings("unchecked")
		public Object getDataValue(Object rowObject, int columnIndex) {
			if (rowObject instanceof GroupByObject) {
				IAggregator<T, ?> aggregator = aggregatorByColumn.get(columnIndex);
				GroupByObject groupByObject = (GroupByObject) rowObject;
				if (aggregator == null) {
					if (columnIndex == 0) {
						return groupByObject.getValue(); // Print the name of the group
					}
					return ""; //$NON-NLS-1$ // No aggregation, print nothing
				}
				return aggregator.aggregate(getElementsInGroup(groupByObject), columnAccessor, columnIndex);
			} else {
				return columnAccessor.getDataValue((T) rowObject, columnIndex);
			}
		}
	}

	public AggregatorColumnAccessor getColumnAccessor() {
		return aggregatorColumnAccessor;
	}

	/**
	 * To find out if an element is part of a group
	 */
	public static class GroupDescriptorMatcher<T> implements Matcher<T> {

		private final GroupByObject group;
		private final IColumnAccessor<T> columnAccessor;

		public GroupDescriptorMatcher(GroupByObject group, IColumnAccessor<T> columnAccessor) {
			this.group = group;
			this.columnAccessor = columnAccessor;
		}

		@Override
		public boolean matches(T element) {
			for (Entry<Integer, Object> desc : group.getDescriptor()) {
				int columnIndex = desc.getKey();
				Object groupName = desc.getValue();
				if (!groupName.equals(columnAccessor.getDataValue((T) element, columnIndex))) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * Get the list of elements for a group, create it if it doesn't exists.
	 * @param groupDescriptor The description of the group (columnIndexes..)
	 * @return The FilterList of elements
	 */
	public FilterList<T> getElementsInGroup(GroupByObject groupDescriptor) {
		FilterList<T> elementsInGroup = filtersByGroup.get(groupDescriptor);
		if (elementsInGroup == null) {
			elementsInGroup = new FilterList<T>(eventList, new GroupDescriptorMatcher<T>(groupDescriptor,
					columnAccessor));
			filtersByGroup.put(groupDescriptor, elementsInGroup);
		}
		return elementsInGroup;
	}

	public interface IAggregator<T, K> {
		K aggregate(List<T> input, IColumnAccessor<T> columnAccessor, int columnIndex);
	}

	public <K> K aggregate(ILayerCell cell, IAggregator<T, K> aggregator) {
		return aggregator.aggregate(getElementsInGroup((GroupByObject) cell.getDataValue()), columnAccessor,
				cell.getColumnIndex());
	}

}
