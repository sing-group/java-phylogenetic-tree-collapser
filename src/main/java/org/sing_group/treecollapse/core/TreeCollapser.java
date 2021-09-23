package org.sing_group.treecollapse.core;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.sing_group.treecollapse.core.tree.MutableTreeNode;
import org.sing_group.treecollapse.core.tree.TreeNode;

public class TreeCollapser {

  private List<MutableTreeNode> getLeafNodes(MutableTreeNode root) {
    LinkedList<MutableTreeNode> result = new LinkedList<>();
    putLeafNodes(result, root);
    return result;
  }

  private void putLeafNodes(LinkedList<MutableTreeNode> currentList, MutableTreeNode node) {
    if (node.isLeaf()) {
      currentList.add(node);
    } else {
      node.getChildren().forEach(child -> putLeafNodes(currentList, child));
    }
  }

  public MutableTreeNode collapseTree(TreeNode root, CollapsingStrategy collapsingStrategy) {
    MutableTreeNode mRoot = new MutableTreeNode(root);

    boolean changed = false;

    do {
      List<MutableTreeNode> leafNodes = getLeafNodes(mRoot);
      changed = false;

      for (MutableTreeNode leafNode : leafNodes) {
        MutableTreeNode parent = leafNode.getParent();
        if (parent != null) {

          List<MutableTreeNode> siblings =
            parent.getChildren().stream().filter(node -> node.isLeaf() && node != leafNode)
              .collect(Collectors.toList());

          for (MutableTreeNode sibling : siblings) {

            if (collapsingStrategy.areMergeable(leafNode, sibling)) { ////// ARE
                                                                        ////// MERGEABLE?
              // merge
              MutableTreeNode merged = collapsingStrategy.mergeNodes(leafNode, sibling);

              // remove merged nodes
              Arrays.asList(leafNode, sibling).forEach(node -> {
                parent.removeChild(node);
              });

              parent.addChild(merged);

              if (parent.getChildren().size() == 1) {
                // grand parent
                MutableTreeNode grandParent = parent.getParent();

                if (grandParent != null) {
                  MutableTreeNode collapsed = collapsingStrategy.collapse(parent, merged);
                  grandParent.removeChild(parent);
                  grandParent.addChild(collapsed);

                }
              }

              // we have done modifications, so restart
              changed = true;
              break;
            }
          }
        }
      }
    } while (changed == true);

    // check if root contains only one child
    if (mRoot.getChildren().size() == 1) {
      return mRoot.getChildren().get(0);
    }

    return mRoot;
  }
}
