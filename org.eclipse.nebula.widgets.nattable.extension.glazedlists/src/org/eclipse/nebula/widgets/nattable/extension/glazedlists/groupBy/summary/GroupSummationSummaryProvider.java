package org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.summary;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;

public class GroupSummationSummaryProvider<T> implements ISummaryGroupProvider<T> {

	private final IColumnPropertyAccessor<T> columnPropertyAccessor;

	public GroupSummationSummaryProvider(IColumnPropertyAccessor<T> columnPropertyAccessor) {
		this.columnPropertyAccessor = columnPropertyAccessor;
	}

	@Override
	public Object summarize(int columnIndex, int rowIndex, List<T> children) {
		float summaryValue = 0;
		for (T child : children) {
			Object dataValue = columnPropertyAccessor.getDataValue(child, columnIndex);
			if (dataValue instanceof Number) {
				summaryValue = summaryValue + Float.parseFloat(dataValue.toString());
			}
		}
		return summaryValue;
	}

}