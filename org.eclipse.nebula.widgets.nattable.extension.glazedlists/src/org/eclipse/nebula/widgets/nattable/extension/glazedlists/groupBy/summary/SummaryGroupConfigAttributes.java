package org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.summary;

import org.eclipse.nebula.widgets.nattable.style.ConfigAttribute;

public class SummaryGroupConfigAttributes {

	/**
	 * The configuration attribute that is used to calculate the summary for a
	 * column.
	 */
	public static final ConfigAttribute<ISummaryGroupProvider<?>> SUMMARY_GROUP_PROVIDER = new ConfigAttribute<ISummaryGroupProvider<?>>();
}