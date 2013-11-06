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
package org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.summary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.nebula.widgets.nattable.command.ILayerCommand;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByDataLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByObject;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.layer.event.IVisualChangeEvent;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.util.ArrayUtil;

import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.summary.ISummaryGroupProvider;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.summary.SummaryGroupConfigAttributes;

/**
 * Adds a summary row at the end. Uses {@link ISummaryProvider} to calculate the
 * summaries for all columns.
 * <p>
 * This layer also adds the following labels:
 * <ol>
 * <li>{@link SummaryGroupRowLayer#DEFAULT_SUMMARY_COLUMN_CONFIG_LABEL_PREFIX} +
 * column index</li>
 * <li>{@link SummaryGroupRowLayer#DEFAULT_SUMMARY_ROW_CONFIG_LABEL} to all
 * cells in the row</li>
 * </ol>
 * 
 * Example: column with index 1 will have the
 * DEFAULT_SUMMARY_COLUMN_CONFIG_LABEL_PREFIX + 1 label applied. Styling and
 * {@link ISummaryProvider} can be hooked up to these labels.
 * 
 * @see DefaultSummaryRowConfiguration
 */
public class SummaryGroupRowLayer<T> extends AbstractLayerTransform implements IUniqueIndexLayer {

	/**
	 * Label that indicates the shown tree item object should summarize its
	 * children
	 */
	public static final String SUMMARIZE = "SUMMARIZE"; //$NON-NLS-1$

	/**
	 * Label that gets attached to the LabelStack for every cell in the summary
	 * row.
	 */
	public static final String DEFAULT_SUMMARY_ROW_CONFIG_LABEL = "SummaryRow"; //$NON-NLS-1$
	/**
	 * Prefix of the labels that get attached to cells in the summary row. The
	 * complete label will consist of this prefix and the column index at the
	 * end of the label. This way every cell in the summary row can be accessed
	 * directly via label mechanism.
	 */
	public static final String DEFAULT_SUMMARY_COLUMN_CONFIG_LABEL_PREFIX = "SummaryColumn_"; //$NON-NLS-1$

	private final IConfigRegistry configRegistry;

	/**
	 * Creates a SummaryRowLayer on top of the given underlying layer.
	 * <p>
	 * Note: This constructor will create the SummaryRowLayer by using the
	 * default configuration. The default configuration doesn't fit the needs so
	 * you usually will use your custom summary row configuration.
	 * 
	 * @param underlyingDataLayer
	 *            The underlying layer on which the SummaryRowLayer should be
	 *            build.
	 * @param configRegistry
	 *            The ConfigRegistry for retrieving the ISummaryProvider per
	 *            column.
	 * 
	 * @see DefaultSummaryRowConfiguration
	 */
	public SummaryGroupRowLayer(GroupByDataLayer<T> underlyingDataLayer, IConfigRegistry configRegistry) {
		this(underlyingDataLayer, configRegistry, true);
	}

	/**
	 * Creates a SummaryRowLayer on top of the given underlying layer.
	 * <p>
	 * Note: This constructor will create the SummaryRowLayer by using the
	 * default configuration if the autoConfig parameter is set to
	 * <code>true</code>. The default configuration doesn't fit the needs so you
	 * usually will use your custom summary row configuration. When using a
	 * custom configuration you should use this constructor setting autoConfig
	 * to <code>false</code>. Otherwise you might get strange behaviour as the
	 * default configuration will be set additionally to your configuration.
	 * 
	 * @param underlyingDataLayer
	 *            The underlying layer on which the SummaryRowLayer should be
	 *            build.
	 * @param configRegistry
	 *            The ConfigRegistry for retrieving the ISummaryProvider per
	 *            column.
	 * @param autoConfigure
	 *            <code>true</code> to use the DefaultSummaryRowConfiguration,
	 *            <code>false</code> if a custom configuration will be set after
	 *            the creation.
	 * 
	 * @see DefaultSummaryRowConfiguration
	 */
	public SummaryGroupRowLayer(GroupByDataLayer<T> underlyingDataLayer, IConfigRegistry configRegistry,
			boolean autoConfigure) {
		super(underlyingDataLayer);
		this.configRegistry = configRegistry;
		if (autoConfigure) {
			//addConfiguration(new DefaultSummaryRowConfiguration());
		}
	}

	/**
	 * Calculates the summary for the column using the {@link ISummaryProvider}
	 * from the {@link IConfigRegistry}. In order to prevent the table from
	 * freezing (for large data sets), the summary is calculated in a separate
	 * Thread. While summary is being calculated
	 * {@link ISummaryProvider#DEFAULT_SUMMARY_VALUE} is returned.
	 * <p>
	 * NOTE: Since this is a {@link IUniqueIndexLayer} sitting close to the
	 * {@link DataLayer}, columnPosition == columnIndex
	 */
	@Override
	public Object getDataValueByPosition(final int columnPosition, final int rowPosition) {
		if (isSummaryRowPosition(rowPosition, columnPosition)) {
			ILayerCell cell = underlyingLayer.getCellByPosition(columnPosition, rowPosition);
			GroupByObject group = (GroupByObject) cell.getDataValue();
			return calculateNewSummaryValue(group, rowPosition, columnPosition, false);
		}
		return super.getDataValueByPosition(columnPosition, rowPosition);
	}

	private Object calculateNewSummaryValue(final GroupByObject group, final int rowPosition, final int columnPosition,
			boolean calculateInBackground) {

		// Get the summary provider from the configuration registry
		LabelStack labelStack = getConfigLabelsByPosition(columnPosition, rowPosition);
		String[] configLabels = labelStack.getLabels().toArray(ArrayUtil.STRING_TYPE_ARRAY);

		final ISummaryGroupProvider summaryProvider = configRegistry.getConfigAttribute(
				SummaryGroupConfigAttributes.SUMMARY_GROUP_PROVIDER, DisplayMode.NORMAL, configLabels);

		// If there is no Summary provider - skip processing
		if (summaryProvider == ISummaryGroupProvider.NONE) {
			return ""; //$NON-NLS-1$
		}

		// List<Integer> childRowIndexes = ((TreeLayer)	// underlyingLayer).getModel().getChildIndexes(rowPosition);
		GroupByDataLayer<T> dataLayer = (GroupByDataLayer<T>) underlyingLayer;
		List<T> children = dataLayer.getElementsInGroup(group);

		Object summaryValue = calculateColumnSummary(columnPosition, rowPosition, children, summaryProvider);
		return summaryValue;
	}

	private Object calculateColumnSummary(int columnIndex, int rowIndex, List<T> children,
			ISummaryGroupProvider<T> summaryProvider) {
		Object summaryValue = null;
		if (summaryProvider != null) {
			summaryValue = summaryProvider.summarize(columnIndex, rowIndex, children);
		}
		return summaryValue;
	}

	protected boolean isSummaryRowPosition(int rowPosition, int colPosition) {
		ILayerCell cell = underlyingLayer.getCellByPosition(colPosition, rowPosition);
		if (cell == null) {
			return false;
		}
		return cell.getConfigLabels().hasLabel(GroupByDataLayer.GROUP_BY_OBJECT)
				&& cell.getConfigLabels().hasLabel(SUMMARIZE);
	}

	@Override
	public LabelStack getConfigLabelsByPosition(int columnPosition, int rowPosition) {
		// FIXME: not really needed?
		if (isSummaryRowPosition(rowPosition, columnPosition)) {
			// create a new LabelStack that takes the config labels into account
			LabelStack labelStack = new LabelStack();
			if (getConfigLabelAccumulator() != null) {
				getConfigLabelAccumulator().accumulateConfigLabels(labelStack, columnPosition, rowPosition);
			}
			labelStack.addLabelOnTop(DEFAULT_SUMMARY_ROW_CONFIG_LABEL);
			labelStack.addLabelOnTop(DEFAULT_SUMMARY_COLUMN_CONFIG_LABEL_PREFIX + columnPosition);
			return labelStack;
		}
		return super.getConfigLabelsByPosition(columnPosition, rowPosition);
	}

	@Override
	public int getRowPositionByIndex(int rowIndex) {
		if (rowIndex >= 0 && rowIndex < getRowCount()) {
			return rowIndex;
		} else {
			return -1;
		}
	}

	@Override
	public int getColumnPositionByIndex(int columnIndex) {
		if (columnIndex >= 0 && columnIndex < getColumnCount()) {
			return columnIndex;
		} else {
			return -1;
		}
	}
}
