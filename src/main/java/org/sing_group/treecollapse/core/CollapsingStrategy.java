package org.sing_group.treecollapse.core;

import org.sing_group.treecollapse.core.tree.MutableTreeNode;

public interface CollapsingStrategy {

  MutableTreeNode collapseNodes(MutableTreeNode node1, MutableTreeNode node2);

  boolean areCollapsible(MutableTreeNode node1, MutableTreeNode node2);

}