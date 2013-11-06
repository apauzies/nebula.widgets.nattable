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
package org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsDataProvider;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.aggregator.Aggregator.AggregatorColumnAccessor;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.tree.GlazedListTreeData;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.tree.GlazedListTreeRowModel;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.event.RowStructuralRefreshEvent;
import org.eclipse.nebula.widgets.nattable.sort.ISortModel;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.TreeList;
import ca.odell.glazedlists.matchers.Matcher;

public class GroupByDataLayer<T> extends DataLayer implements Observer {

	/** Map the group to a dynamic list of group elements */
	private final Map<GroupByObject, FilterList<T>> filtersByGroup = new ConcurrentHashMap<GroupByObject, FilterList<T>>();
	/**
	 * Label that indicates the shown tree item object as GroupByObject
	 */
	public static final String GROUP_BY_OBJECT = "GROUP_BY_OBJECT"; //$NON-NLS-1$
	/**
	 * The underlying base EventList.
	 */
	private final EventList<T> eventList;
	/**
	 * Convenience class to retrieve information and operate on the TreeList.
	 */
	private final GlazedListTreeData<Object> treeData;
	/**
	 * The ITreeRowModel that is responsible to retrieve information and operate on tree items.
	 */
	private final GlazedListTreeRowModel<Object> treeRowModel;
	/**
	 * The TreeList that is created internally by this GroupByDataLayer to enable groupBy.
	 */
	private final TreeList<Object> treeList;
	
	private final GroupByTreeFormat<T> treeFormat;
	
	public GroupByDataLayer(GroupByModel groupByModel, EventList<T> eventList, IColumnAccessor<T> columnAccessor) {
		this(groupByModel, eventList, columnAccessor, null);
	}
	
	private final IColumnAccessor<T> columnAccessor;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public GroupByDataLayer(GroupByModel groupByModel, EventList<T> eventList, IColumnAccessor<T> columnAccessor, AggregatorColumnAccessor aggregatorColumnAccessor) {
		this.eventList = eventList;
		this.columnAccessor = columnAccessor;
		
		groupByModel.addObserver(this);
		
		treeFormat = new GroupByTreeFormat<T>(groupByModel, columnAccessor, aggregatorColumnAccessor);
		this.treeList = new TreeList(eventList, treeFormat, new GroupByExpansionModel());
		
		treeData = new GlazedListTreeData<Object>(getTreeList());
		treeRowModel = new GlazedListTreeRowModel<Object>(treeData);
		
		IColumnAccessor<Object> groupByColumnAccessor = aggregatorColumnAccessor == null ? new GroupByColumnAccessor<T>(columnAccessor) : aggregatorColumnAccessor;		
		setDataProvider(new GlazedListsDataProvider<Object>(getTreeList(), groupByColumnAccessor));
		
//		if (aggregatorColumnAccessor == null) {
//			addConfiguration(new GroupByDataLayerConfiguration());
//		}
	}
	
	public void setSortModel(ISortModel model) {
		treeFormat.setSortModel(model);
	}
	
	/**
	 * Method to update the tree list after filter or TreeList.Format changed.
	 * Need this workaround to update the tree list for presentation because of
	 * http://java.net/jira/browse/GLAZEDLISTS-521
	 * 
	 * @see http://glazedlists.1045722.n5.nabble.com/sorting-a-treelist-td4704550.html
	 */
	protected void updateTree() {
		this.eventList.getReadWriteLock().writeLock().lock();
		try {
			for (int i = 0; i < this.eventList.size(); i++) {
				this.eventList.set(i,
						this.eventList.get(i));
			}
		} finally {
			this.eventList.getReadWriteLock().writeLock().unlock();
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		updateTree();
		fireLayerEvent(new RowStructuralRefreshEvent(this));
	}

	/**
	 * @return The ITreeRowModel that is responsible to retrieve information and operate on tree items.
	 */
	public GlazedListTreeRowModel<Object> getTreeRowModel() {
		return treeRowModel;
	}
	
	/**
	 * @return The TreeList that is created internally by this GroupByDataLayer to enable groupBy.
	 */
	public TreeList<Object> getTreeList() {
		return treeList;
	}
	
	@Override
	public LabelStack getConfigLabelsByPosition(int columnPosition, int rowPosition) {
		LabelStack configLabels = super.getConfigLabelsByPosition(columnPosition, rowPosition);
		if (treeData.getDataAtIndex(getRowIndexByPosition(rowPosition)) instanceof GroupByObject) {
			configLabels.addLabel(GROUP_BY_OBJECT);
		}
		return configLabels;
	}

	/**
	 * Simple {@link ExpansionModel} that shows every node expanded initially
	 * and doesn't react on expand/collapse state changes.
	 * 
	 * It is not strictly necessary for implementors to record the
	 * expand/collapsed state of all nodes, since TreeList caches node state
	 * internally.
	 * 
	 * @see http://publicobject.com/glazedlists/glazedlists-1.8.0/api/ca/odell/
	 *      glazedlists/TreeList.ExpansionModel.html
	 */
	private class GroupByExpansionModel implements TreeList.ExpansionModel<Object> {
		/**
		 * Determine the specified element's initial expand/collapse state.
		 */
		@Override
		public boolean isExpanded(final Object element, final List<Object> path) {
			return true;
		}

		/**
		 * Notifies this handler that the specified element's expand/collapse
		 * state has changed.
		 */
		@Override
		public void setExpanded(final Object element, final List<Object> path, final boolean expanded) {
			//do nothing
		}
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
			elementsInGroup = new FilterList<T>(eventList, new GroupDescriptorMatcher<T>(groupDescriptor, columnAccessor));
			filtersByGroup.put(groupDescriptor, elementsInGroup);
		}
		return elementsInGroup;
	}

}
