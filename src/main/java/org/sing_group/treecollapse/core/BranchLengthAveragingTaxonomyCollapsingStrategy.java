package org.sing_group.treecollapse.core;

import java.util.Map;
import java.util.Set;

import org.sing_group.treecollapse.core.tree.MutableTreeNode;
import org.sing_group.treecollapse.core.tree.TreeNode;

public class BranchLengthAveragingTaxonomyCollapsingStrategy extends TaxonomyCollapsingStrategy {

  public static final String BRANCH_LENGTH = "branch.length";

  public BranchLengthAveragingTaxonomyCollapsingStrategy(
    Map<String, String> sequenceToSpecieMap, TreeNode speciesTaxonomy, Set<String> collapsingTaxonomyStopTerms
  ) {
    super(sequenceToSpecieMap, speciesTaxonomy, collapsingTaxonomyStopTerms);
  }

  @Override
  public MutableTreeNode mergeNodes(MutableTreeNode node1, MutableTreeNode node2) {
    MutableTreeNode merged = super.mergeNodes(node1, node2);

    // set the BRANCH_LENGTH as the average
    double node1Length = node1.getAttribute(BRANCH_LENGTH);
    int node1CollapsedNodes = super.isCollapsed(node1) ? super.getCollapsedNodes(node1).size() : 1;

    double node2Length = node2.getAttribute(BRANCH_LENGTH);
    int node2CollapsedNodes = super.isCollapsed(node2) ? super.getCollapsedNodes(node2).size() : 1;

    double node1Weight = (double) node1CollapsedNodes / (double) (node1CollapsedNodes + node2CollapsedNodes);
    double node2Weight = (double) node2CollapsedNodes / (double) (node1CollapsedNodes + node2CollapsedNodes);

    merged.setAttribute(BRANCH_LENGTH, node1Weight * node1Length + node2Weight * node2Length);

    return merged;

  }

  @Override
  public MutableTreeNode collapse(MutableTreeNode parent, MutableTreeNode child) {

    MutableTreeNode collapsed = super.collapse(parent, child);
    collapsed.setAttribute(
      BRANCH_LENGTH, (double) parent.getAttribute(BRANCH_LENGTH) + (double) child.getAttribute(BRANCH_LENGTH)
    );

    return collapsed;
  }

}