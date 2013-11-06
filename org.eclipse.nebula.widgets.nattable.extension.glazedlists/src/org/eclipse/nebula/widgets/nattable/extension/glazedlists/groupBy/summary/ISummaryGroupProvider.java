package org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.summary;

import java.util.List;

public interface ISummaryGroupProvider<T> {

	public static final Object DEFAULT_SUMMARY_VALUE = "..."; //$NON-NLS-1$

	/**
	 * Register this instance to indicate that a summary is not required. Doing
	 * so avoids calls to the {@link ISummaryGroupProvider} and is a performance
	 * tweak.
	 */
	public static final ISummaryGroupProvider NONE = new ISummaryGroupProvider() {
		@Override
		public Object summarize(int columnIndex, int rowIndex, List children) {
			return null;
		}
	};

	/**
	 * @param columnIndex
	 *            The column index of the column for which the summary should be
	 *            calculated.
	 * @return The calculated summary value for the column.
	 */
	public Object summarize(int columnIndex, int rowIndex, List<T> children);
}