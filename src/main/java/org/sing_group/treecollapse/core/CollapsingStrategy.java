package org.sing_group.treecollapse.core;

import org.sing_group.treecollapse.core.tree.MutableTreeNode;

public interface CollapsingStrategy {

  public MutableTreeNode mergeNodes(MutableTreeNode node1, MutableTreeNode node2);

  public MutableTreeNode collapse(MutableTreeNode parent, MutableTreeNode child);

  public boolean areMergeable(MutableTreeNode node1, MutableTreeNode node2);

}