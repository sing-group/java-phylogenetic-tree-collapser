package org.sing_group.treecollapse.core.newick;

import org.sing_group.treecollapse.core.tree.MutableTreeNode;

public class NewickCladogramTree extends NewickTree {

  public NewickCladogramTree(NewickTree tree) {
    super(tree.getRoot());
  }

  protected String formatNode(MutableTreeNode node) {
    return node.getName();
  }
}
