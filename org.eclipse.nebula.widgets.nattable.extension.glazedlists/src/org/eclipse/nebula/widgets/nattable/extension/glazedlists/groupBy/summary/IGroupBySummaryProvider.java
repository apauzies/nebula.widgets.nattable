package org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.summary;

import java.util.List;

public interface IGroupBySummaryProvider<T> {

	public static final Object DEFAULT_SUMMARY_VALUE = "..."; //$NON-NLS-1$

	/**
	 * Register this instance to indicate that a summary is not required. Doing
	 * so avoids calls to the {@link IGroupBySummaryProvider} and is a performance
	 * tweak.
	 */
	public static final IGroupBySummaryProvider NONE = new IGroupBySummaryProvider() {
		@Override
		public Object summarize(int columnIndex, List children) {
			return null;
		}
	};

	/**
	 * @param columnIndex
	 *            The column index of the column for which the summary should be
	 *            calculated.
	 * @return The calculated summary value for the column.
	 */
	public Object summarize(int columnIndex, List<T> children);
}