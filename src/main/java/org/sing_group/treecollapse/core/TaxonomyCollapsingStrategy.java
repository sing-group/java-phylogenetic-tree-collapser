package org.sing_group.treecollapse.core;

import static java.util.Arrays.asList;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.sing_group.treecollapse.core.tree.MutableTreeNode;
import org.sing_group.treecollapse.core.tree.TreeManager;
import org.sing_group.treecollapse.core.tree.TreeNode;

public class TaxonomyCollapsingStrategy implements CollapsingStrategy {

  protected static final String TAXONOMY_TERM = "TAXONOMY";
  protected static final String SPECIES = "SPECIES";
  protected static final String IS_COLLAPSED = "IS_COLLAPSED";
  protected static final String COLLAPSED_NODES = "COLLAPSED_NODES";
  private Map<String, String> sequenceToSpecieMap;
  private TreeNode speciesTaxonomy;
  private Set<String> collapsingTaxonomyStopTerms;
  
  private TreeManager taxonomyTreeManager;

  public TaxonomyCollapsingStrategy(
    Map<String, String> sequenceToSpecieMap, TreeNode speciesTaxonomy, Set<String> collapsingTaxonomyStopTerms
  ) {
    this.speciesTaxonomy = speciesTaxonomy;
    this.taxonomyTreeManager = new TreeManager(this.speciesTaxonomy);
    this.sequenceToSpecieMap = sequenceToSpecieMap;
    this.collapsingTaxonomyStopTerms = collapsingTaxonomyStopTerms;
  }
  
  protected boolean isCollapsed(MutableTreeNode node) {
    return node.getAttributes().containsKey(IS_COLLAPSED) && (boolean) node.getAttributes().get(IS_COLLAPSED);
  }

  protected List<MutableTreeNode> getCollapsedNodes(MutableTreeNode node) {
    return node.getAttribute(COLLAPSED_NODES);
  }

  private String getTaxonomyTerm(MutableTreeNode node) {
    if (node.getAttributes().get(TAXONOMY_TERM) == null) {
      node.setAttribute(TAXONOMY_TERM, taxonomyTreeManager.getParent(taxonomyTreeManager.getNodeByName(getSpecie(node))).getName());
    }
    return (String) node.getAttributes().get(TAXONOMY_TERM);
  }

  private List<String> getSpecies(MutableTreeNode node) {
    List<String> species = new LinkedList<>();
    if (isCollapsed(node)) {

      for (MutableTreeNode collapsedNode : getCollapsedNodes(node)) {
        species.add(getSpecie(collapsedNode));
      }
    } else {
      return Arrays.asList(getSpecie(node));
    }
    return species;
  }

  private String getSpecie(MutableTreeNode node) {
    if (node.getAttributes().get(SPECIES) == null) {
      node.setAttribute(SPECIES, this.sequenceToSpecieMap.get(node.getName()));
    }
    return (String) node.getAttributes().get(SPECIES);
  }



  @Override
  public MutableTreeNode mergeNodes(MutableTreeNode node1, MutableTreeNode node2) {
    MutableTreeNode newNode = new MutableTreeNode("");
    newNode.setAttribute(IS_COLLAPSED, true);

    TreeNode commonAncestor = getCommonAncestorInTaxonomy(node1, node2);
    
    
    newNode.setAttribute(TAXONOMY_TERM, commonAncestor.getName());

    LinkedList<MutableTreeNode> collapsedNodes = new LinkedList<>();

    asList(node1, node2).forEach(node -> {
      if (!isCollapsed(node)) {
        collapsedNodes.add(node);

      } else {
        collapsedNodes.addAll(getCollapsedNodes(node));
      }
    });

    newNode.setAttribute(COLLAPSED_NODES, collapsedNodes);

    newNode.setName(getTaxonomyTerm(newNode) + "_(" + getCollapsedNodes(newNode).size() + ")");
    return newNode;
  }

  private TreeNode getCommonAncestorInTaxonomy(MutableTreeNode node1, MutableTreeNode node2) {
    TreeNode node1TaxonomyTreeNode = taxonomyTreeManager.getNodeByName(getTaxonomyTerm(node1));
    TreeNode node2TaxonomyTreeNode = taxonomyTreeManager.getNodeByName(getTaxonomyTerm(node2));
    
    TreeNode commonAncestor = taxonomyTreeManager.getCommonAncestor(node1TaxonomyTreeNode, node2TaxonomyTreeNode);
    return commonAncestor;
  }

  @Override
  public boolean areMergeable(MutableTreeNode node1, MutableTreeNode node2) {
    TreeNode commonAncestor = getCommonAncestorInTaxonomy(node1, node2);
    
    TreeNode node1TaxonomyTreeNode = taxonomyTreeManager.getNodeByName(getTaxonomyTerm(node1));
    TreeNode node2TaxonomyTreeNode = taxonomyTreeManager.getNodeByName(getTaxonomyTerm(node2));
    
    List<TreeNode> node1TaxonomyTreePath = taxonomyTreeManager.getTreePath(node1TaxonomyTreeNode);
    List<TreeNode> node2TaxonomyTreePath = taxonomyTreeManager.getTreePath(node2TaxonomyTreeNode);
    
    //remove from root to commonAncestor (included) in both treePaths
    while(node1TaxonomyTreePath.get(0) != commonAncestor) {
      node1TaxonomyTreePath = node1TaxonomyTreePath.subList(1, node1TaxonomyTreePath.size());
      node2TaxonomyTreePath = node2TaxonomyTreePath.subList(1, node2TaxonomyTreePath.size());
    }
    
    //also remove the commonAncestor node from both paths
    node1TaxonomyTreePath = node1TaxonomyTreePath.subList(1, node1TaxonomyTreePath.size());
    node2TaxonomyTreePath = node2TaxonomyTreePath.subList(1, node2TaxonomyTreePath.size());
    
    List<String> node1TaxonomyTreePathNames = node1TaxonomyTreePath.stream().map(node -> node.getName()).collect(Collectors.toList());
    List<String> node2TaxonomyTreePathNames = node2TaxonomyTreePath.stream().map(node -> node.getName()).collect(Collectors.toList());
    
    boolean foundTaxonomyStopTermInSubPaths = false;
    for (String taxonomyStopTerm : this.collapsingTaxonomyStopTerms) {
      if (node1TaxonomyTreePathNames.contains(taxonomyStopTerm) || node2TaxonomyTreePathNames.contains(taxonomyStopTerm)) {
        foundTaxonomyStopTermInSubPaths = true;
        break;
      }
    }
    
    if (!foundTaxonomyStopTermInSubPaths) {

      // check if node1 and node2 have species in common
      Set<String> node1species = new HashSet<>(getSpecies(node1));
      Set<String> node2species = new HashSet<>(getSpecies(node2));
      node1species.retainAll(node2species);
      if (node1species.size() > 0) {
        return false;
      } else {
        return true;
      }

    }
    return false;

  }

  @Override
  public MutableTreeNode collapse(MutableTreeNode parent, MutableTreeNode child) {
    return child;
  }
}
