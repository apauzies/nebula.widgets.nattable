/*******************************************************************************
 * Copyright (c) 2012 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public abstract class AbstractTreeRowModel<T> implements ITreeRowModel<T>{

	private final Collection<ITreeRowModelListener> listeners = new HashSet<ITreeRowModelListener>();

	private final ITreeData<T> treeData;

	public AbstractTreeRowModel(ITreeData<T> treeData) {
		this.treeData = treeData;
	}

	public void registerRowGroupModelListener(ITreeRowModelListener listener) {
		this.listeners.add(listener);
	}

	public void notifyListeners() {
		for (ITreeRowModelListener listener : this.listeners) {
			listener.treeRowModelChanged();
		}
	}

	@Override
	public int depth(int index) {
		return this.treeData.getDepthOfData(this.treeData.getDataAtIndex(index));
	}

	@Override
	public boolean isLeaf(int index) {
		return !hasChildren(index);
	}

	@Override
	public String getObjectAtIndexAndDepth(int index, int depth) {
		return this.treeData.formatDataForDepth(depth, index);
	}

	@Override
	public boolean hasChildren(int index) {
		return this.treeData.hasChildren(index);
	}

	/**
	 * @return TRUE if the row group this index is collapseable
	 */
	@Override
	public boolean isCollapseable(int index) {
		return hasChildren(index);
	}

	@Override
	public List<Integer> getChildIndexes(int parentIndex) {
		List<Integer> result = new ArrayList<Integer>();
		List<T> children = getDirectChildren(parentIndex);
		for (T child : children) {
			int index = this.treeData.indexOf(child);
			//if the index is -1 the element is not found
			//this means it is not visible and therefore can not be handled
			if (index >= 0) {
				result.add(index);
				result.addAll(getChildIndexes(index));
			}
		}
		return result;
	}
	
	@Override
	public List<Integer> getDirectChildIndexes(int parentIndex) {
		List<Integer> result = new ArrayList<Integer>();
		List<T> children = getDirectChildren(parentIndex);
		for (T child : children) {
			int index = this.treeData.indexOf(child);
			//if the index is -1 the element is not found
			//this means it is not visible and therefore can not be handled
			if (index >= 0) {
				result.add(index);
			}
		}
		return result;
	}

	@Override
	public List<Integer> getRootIndexes() {
		List<Integer> result = new ArrayList<Integer>();
		List<T> roots = this.treeData.getRoots();
		for (T root : roots) {
			int index = this.treeData.indexOf(root);
			result.add(index);
		}
		return result;
	}

	@Override
	public List<T> getChildren(int parentIndex) {
		return getChildren(this.treeData.getDataAtIndex(parentIndex));
	}

	protected List<T> getChildren(T parent) {
		List<T> result = new ArrayList<T>();
		List<T> children = getDirectChildren(parent);
		for (T child : children) {
			result.add(child);
			result.addAll(getChildren(child));
		}
		return result;
	}

	@Override
	public List<T> getDirectChildren(int parentIndex) {
		return this.treeData.getChildren(parentIndex);
	}

	protected List<T> getDirectChildren(T parent) {
		return this.treeData.getChildren(parent);
	}
	
	protected ITreeData<T> getTreeData() {
		return this.treeData;
	}
}
