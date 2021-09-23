package org.sing_group.treecollapse.core.newick;

import static org.sing_group.treecollapse.core.BranchLengthAveragingTaxonomyCollapsingStrategy.BRANCH_LENGTH;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.stream.Collectors;

import org.sing_group.treecollapse.core.tree.MutableTreeNode;

public class NewickTree {


  private static NumberFormat branchLengthFormat = new DecimalFormat("##.#############");
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
        length = ":" + branchLengthFormat.format(node.getAttribute(BRANCH_LENGTH));
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
      sb.append(":").append(branchLengthFormat.format(node.getAttribute(BRANCH_LENGTH)));
    }

    return sb.toString();
  }
}
