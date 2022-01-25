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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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
    
    collapsingStrategy.beforeCollapse(mRoot);
    
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

            if (collapsingStrategy.areCollapsible(leafNode, sibling)) {
              MutableTreeNode collapsed = collapsingStrategy.collapseNodes(leafNode, sibling);

              // remove collapsed nodes
              Arrays.asList(leafNode, sibling).forEach(node -> {
                parent.removeChild(node);
              });

              parent.addChild(collapsed);

              if (parent.getChildren().size() == 1) {
                // grand parent
                MutableTreeNode grandParent = parent.getParent();

                if (grandParent != null) {
                  MutableTreeNode collapsedUpChild = collapsingStrategy.collapseUp(parent, collapsed);
                  grandParent.removeChild(parent);
                  grandParent.addChild(collapsedUpChild);

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
      mRoot = mRoot.getChildren().get(0);
    }

    collapsingStrategy.afterCollapse(mRoot);
    
    return mRoot;
  }
}
