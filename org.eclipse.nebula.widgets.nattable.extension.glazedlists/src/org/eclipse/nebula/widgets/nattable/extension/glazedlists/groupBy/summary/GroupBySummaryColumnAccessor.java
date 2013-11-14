package org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.summary;

import java.util.List;
import java.util.Map;

import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByColumnAccessor;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByDataLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByObject;

public class GroupBySummaryColumnAccessor<T> extends GroupByColumnAccessor<Object> {

	private final Map<Integer, IGroupBySummaryProvider<T>> summaryProviderByColumn;
	private final GroupByDataLayer<T> groupByDataLayer;

	public GroupBySummaryColumnAccessor(IColumnAccessor<Object> columnAccessor,
			Map<Integer, IGroupBySummaryProvider<T>> summaryProviderByColumn, GroupByDataLayer<T> groupByDataLayer) {
		super(columnAccessor);
		this.groupByDataLayer = groupByDataLayer;
		this.summaryProviderByColumn = summaryProviderByColumn;
	}
	
	public Object getDataValue(Object rowObject, int columnIndex) {
		if (rowObject instanceof GroupByObject) {
			IGroupBySummaryProvider<T> summaryProvider = summaryProviderByColumn.get(columnIndex);
			GroupByObject groupByObject = (GroupByObject) rowObject;
			if (summaryProvider == null) {
				if (columnIndex == 0) {
					// Print the name of the group
					return groupByObject.getValue();
				}
				return ""; //$NON-NLS-1$ // No aggregation, print nothing
			}
			List<T> children = groupByDataLayer.getElementsInGroup(groupByObject);
			return summaryProvider.summarize(columnIndex, children);
		} else {
			return columnAccessor.getDataValue(rowObject, columnIndex);
		}
	}
}
