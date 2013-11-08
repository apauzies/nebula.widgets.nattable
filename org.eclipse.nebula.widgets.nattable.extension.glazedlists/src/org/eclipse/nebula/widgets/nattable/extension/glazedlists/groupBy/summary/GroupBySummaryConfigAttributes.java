package org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.summary;

import java.util.Map;

import org.eclipse.nebula.widgets.nattable.style.ConfigAttribute;

public class GroupBySummaryConfigAttributes {

	/**
	 * The configuration attribute that is used to calculate the summary for a
	 * column.
	 */
	public static final ConfigAttribute<Map<Integer,IGroupBySummaryProvider>> GROUP_BY_SUMMARY_PROVIDER = new ConfigAttribute<Map<Integer,IGroupBySummaryProvider>>();
}