package org.sing_group.treecollapse.core.tree;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class TreeManager {

  private TreeNode tree;

  public TreeManager(TreeNode tree) {
    this.tree = tree;
  }

  public Optional<TreeNode> getNodeByName(String name) {
    return findInTree(this.tree, (node) -> node.getName().contentEquals(name));
  }

  private Optional<TreeNode> findInTree(TreeNode root, Predicate<TreeNode> isElement) {
    if (isElement.test(root)) {
      return Optional.of(root);
    } else {
      for (TreeNode child : root.getChildren()) {
        Optional<TreeNode> node = findInTree(child, isElement);
        if (node.isPresent()) {
          return node;
        }
      }
      return Optional.empty();
    }
  }

  public TreeNode getParent(TreeNode child) {
    return getParent(this.tree, child);

  }

  public TreeNode getCommonAncestor(TreeNode node1, TreeNode node2) {
    List<TreeNode> node1Path = getTreePath(node1);
    List<TreeNode> node2Path = getTreePath(node2);

    int i = 0;

    TreeNode commonAncestor = null;
    while (i < node1Path.size() && i < node2Path.size() && node1Path.get(i).equals(node2Path.get(i))) {
      commonAncestor = node1Path.get(i);
      i++;
    }
    return commonAncestor;
  }

  public List<TreeNode> getTreePath(TreeNode node) {
    List<TreeNode> treePath = new LinkedList<>();
    treePath.add(0, node);

    TreeNode parent = this.getParent(node);
    while (parent != null) {
      treePath.add(0, parent);
      parent = this.getParent(parent);
    }
    return treePath;
  }

  private TreeNode getParent(TreeNode root, TreeNode child) {
    if (root.isLeaf()) {
      return null;
    }

    for (TreeNode rootChild : root.getChildren()) {
      if (rootChild.equals(child)) {
        return root;
      } else {
        TreeNode innerParent = getParent(rootChild, child);
        if (innerParent != null) {
          return innerParent;
        }
      }
    }

    return null;

  }
}
