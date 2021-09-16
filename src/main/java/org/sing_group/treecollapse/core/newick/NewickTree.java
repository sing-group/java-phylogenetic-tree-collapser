package org.sing_group.treecollapse.core.newick;

import java.util.stream.Collectors;

import org.sing_group.treecollapse.core.tree.MutableTreeNode;

public class NewickTree {

  public static final String BRANCH_LENGTH = "branch.length";
  private MutableTreeNode root;

  public NewickTree(String newick) {
    this(NewickTreeParser.parse(newick));
  }

  public NewickTree(MutableTreeNode root) {
    this.root = root;
  }

  public MutableTreeNode getRoot() {
    return root;
  }

  @Override
  public String toString() {
    return toString(root) + ";";
  }

  private static String toString(MutableTreeNode node) {
    if (node.getChildren().size() == 0) {
      String length = "";
      if (node.getAttribute(BRANCH_LENGTH) != null) {
        length = ":" + node.getAttribute(BRANCH_LENGTH);
      }
      return node.getName() + length;
    }

    StringBuilder sb = new StringBuilder("(");
    sb.append(
      node.getChildren().stream()
        .map(NewickTree::toString)
        .collect(Collectors.joining(","))
    ).append(")");

    if (node.getName() != null && !node.getName().isEmpty()) {
      sb.append(node.getName());
    }
    if (node.getAttribute(BRANCH_LENGTH) != null) {
      sb.append(":").append(node.getAttribute(BRANCH_LENGTH).toString());
    }

    return sb.toString();
  }
}
