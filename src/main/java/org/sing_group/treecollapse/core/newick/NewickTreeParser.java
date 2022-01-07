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
package org.sing_group.treecollapse.core.newick;

import java.util.LinkedList;
import java.util.List;
import static org.sing_group.treecollapse.core.BranchLengthAveragingTaxonomyCollapsingStrategy.BRANCH_LENGTH;

import org.sing_group.treecollapse.core.tree.MutableTreeNode;

public class NewickTreeParser {

  public static MutableTreeNode parse(String newick) {
    newick = newick.replace(";", "");
    newick = newick.replace(" ", "");
    newick = newick.replace("\n", "");

    if (newick.startsWith("(")) {
      List<MutableTreeNode> children = new LinkedList<>();
      int openParentheses = 0;
      StringBuilder child = new StringBuilder();
      for (int i = 1; i < newick.length(); i++) {
        if ((newick.charAt(i) == ',' || newick.charAt(i) == ')') && openParentheses == 0) {
          children.add(parse(child.toString()));
          child = new StringBuilder();
        } else {
          child.append(newick.charAt(i));
          if (newick.charAt(i) == '(') {
            openParentheses++;
          }
          if (newick.charAt(i) == ')') {
            openParentheses--;
          }
        }
      }

      MutableTreeNode root = parse(newick.substring(newick.lastIndexOf(")") + 1, newick.length()));
      children.forEach(root::addChild);
      return root;
    } else {

      String[] split = splitNewickNodeName(newick);
      MutableTreeNode toret = new MutableTreeNode(split[0]);
      if (!split[1].isEmpty()) {
        toret.setAttribute(BRANCH_LENGTH, new Double(split[1]));
      }
      return toret;
    }
  }

  private static String[] splitNewickNodeName(String node) {
    String[] toret = new String[2];
    if (!node.contains(":")) {
      toret[0] = node;
      toret[1] = "";
    } else {
      toret[0] = node.substring(0, node.indexOf(":"));
      toret[1] = node.substring(node.indexOf(":") + 1);
    }
    return toret;
  }
}
