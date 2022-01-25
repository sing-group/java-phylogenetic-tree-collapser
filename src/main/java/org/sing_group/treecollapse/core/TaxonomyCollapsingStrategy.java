/*-
 * #%L
 * treecollapse
 * %%
 * Copyright (C) 2021 - 2022 Daniel Glez-Peña, Hugo López-Fernández, Cristina Vieira, Jorge Vieira
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.sing_group.treecollapse.core;

import static java.util.Arrays.asList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.sing_group.treecollapse.core.exception.TaxonomyCollapsingException;
import org.sing_group.treecollapse.core.tree.MutableTreeNode;
import org.sing_group.treecollapse.core.tree.TreeManager;
import org.sing_group.treecollapse.core.tree.TreeNode;

public class TaxonomyCollapsingStrategy implements CollapsingStrategy {
  private static final String TO_BE_RENAMED = "_TO_BE_NAMED_AT_END_";
  public static final String IS_COLLAPSED = "IS_COLLAPSED";
  public static final String COLLAPSED_NODES = "COLLAPSED_NODES";

  protected static final String TAXONOMY_TERM = "TAXONOMY";
  protected static final String SPECIES = "SPECIES";

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
      Optional<TreeNode> speciesNode = taxonomyTreeManager.getNodeByName(getSpecies(node));
      if (speciesNode.isPresent()) {
        node.setAttribute(
          TAXONOMY_TERM, taxonomyTreeManager.getParent(speciesNode.get()).getName()
        );
      } else {
        throw new TaxonomyCollapsingException(
          "Species not found for the current node (" + node.getName()
            + "). Make sure this node is listed in the sequence to species mapping."
        );
      }
    }

    return (String) node.getAttributes().get(TAXONOMY_TERM);
  }

  private List<String> getSpeciesList(MutableTreeNode node) {
    List<String> species = new LinkedList<>();
    if (isCollapsed(node)) {

      for (MutableTreeNode collapsedNode : getCollapsedNodes(node)) {
        species.add(getSpecies(collapsedNode));
      }
    } else {
      return Arrays.asList(getSpecies(node));
    }
    return species;
  }

  private String getSpecies(MutableTreeNode node) {
    if (node.getAttributes().get(SPECIES) == null) {
      String species = this.sequenceToSpecieMap.get(node.getName());
      node.setAttribute(SPECIES, species);
      if (species == null) {
        throw new TaxonomyCollapsingException(
          "Species not found for the node \"" + node.getName()
            + "\". Make sure this node is listed in the sequence to species mapping."
        );
      }
    }
    return (String) node.getAttributes().get(SPECIES);
  }

  @Override
  public MutableTreeNode collapseNodes(MutableTreeNode node1, MutableTreeNode node2) {
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
    newNode.setName(TO_BE_RENAMED);
    return newNode;
  }

  private TreeNode getCommonAncestorInTaxonomy(MutableTreeNode node1, MutableTreeNode node2) {
    Optional<TreeNode> node1TaxonomyTreeNode = taxonomyTreeManager.getNodeByName(getTaxonomyTerm(node1));
    if (!node1TaxonomyTreeNode.isPresent()) {
      throw new TaxonomyCollapsingException("Taxonomy term not found for node: " + node1.getName());
    }

    Optional<TreeNode> node2TaxonomyTreeNode = taxonomyTreeManager.getNodeByName(getTaxonomyTerm(node2));
    if (!node2TaxonomyTreeNode.isPresent()) {
      throw new TaxonomyCollapsingException("Taxonomy term not found for node: " + node2.getName());
    }

    return taxonomyTreeManager.getCommonAncestor(node1TaxonomyTreeNode.get(), node2TaxonomyTreeNode.get());
  }

  @Override
  public boolean areCollapsible(MutableTreeNode node1, MutableTreeNode node2) {
    TreeNode commonAncestor = getCommonAncestorInTaxonomy(node1, node2);

    Optional<TreeNode> node1TaxonomyTreeNode = taxonomyTreeManager.getNodeByName(getTaxonomyTerm(node1));
    if (!node1TaxonomyTreeNode.isPresent()) {
      throw new TaxonomyCollapsingException("Taxonomy term not found for node: " + node1.getName());
    }

    Optional<TreeNode> node2TaxonomyTreeNode = taxonomyTreeManager.getNodeByName(getTaxonomyTerm(node2));
    if (!node2TaxonomyTreeNode.isPresent()) {
      throw new TaxonomyCollapsingException("Taxonomy term not found for node: " + node2.getName());
    }

    List<TreeNode> node1TaxonomyTreePath = taxonomyTreeManager.getTreePath(node1TaxonomyTreeNode.get());
    List<TreeNode> node2TaxonomyTreePath = taxonomyTreeManager.getTreePath(node2TaxonomyTreeNode.get());

    // remove from root to commonAncestor (included) in both treePaths
    while (node1TaxonomyTreePath.get(0) != commonAncestor) {
      node1TaxonomyTreePath = node1TaxonomyTreePath.subList(1, node1TaxonomyTreePath.size());
      node2TaxonomyTreePath = node2TaxonomyTreePath.subList(1, node2TaxonomyTreePath.size());
    }

    // also remove the commonAncestor node from both paths
    node1TaxonomyTreePath = node1TaxonomyTreePath.subList(1, node1TaxonomyTreePath.size());
    node2TaxonomyTreePath = node2TaxonomyTreePath.subList(1, node2TaxonomyTreePath.size());

    List<String> node1TaxonomyTreePathNames =
      node1TaxonomyTreePath.stream().map(node -> node.getName()).collect(Collectors.toList());
    List<String> node2TaxonomyTreePathNames =
      node2TaxonomyTreePath.stream().map(node -> node.getName()).collect(Collectors.toList());

    boolean foundTaxonomyStopTermInSubPaths = false;
    for (String taxonomyStopTerm : this.collapsingTaxonomyStopTerms) {
      if (
        node1TaxonomyTreePathNames.contains(taxonomyStopTerm) || node2TaxonomyTreePathNames.contains(taxonomyStopTerm)
      ) {
        foundTaxonomyStopTermInSubPaths = true;
        break;
      }
    }

    if (!foundTaxonomyStopTermInSubPaths) {
      // check if node1 and node2 have species in common
      Set<String> node1species = new HashSet<>(getSpeciesList(node1));
      Set<String> node2species = new HashSet<>(getSpeciesList(node2));
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
  public MutableTreeNode collapseUp(MutableTreeNode parent, MutableTreeNode child) {
    return child;
  }

  @Override
  public void afterCollapse(MutableTreeNode root) {

    if (root.getName().contentEquals(TO_BE_RENAMED)) {
      root.setName(
        root.getAttribute(TAXONOMY_TERM) + "_" + termIdGenerator.getNewId(root.getAttribute(TAXONOMY_TERM)) + "_" + getCollapsedNodes(root).size()
      );
    }
    for (MutableTreeNode child : root.getChildren()) {
      afterCollapse(child);
    }

  }

  private class AutoIncrementTermIdGenerator {
    private Map<String, Integer> counterByTerm = new HashMap<>();

    public int getNewId(String term) {
      Integer current = counterByTerm.get(term);
      if (current == null) {
        current = 0;
      }
      current++;
      counterByTerm.put(term, current);
      return current;
    }
  }

  private AutoIncrementTermIdGenerator termIdGenerator = new AutoIncrementTermIdGenerator();
}
