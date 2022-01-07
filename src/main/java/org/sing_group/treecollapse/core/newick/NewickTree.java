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

import static org.sing_group.treecollapse.core.BranchLengthAveragingTaxonomyCollapsingStrategy.BRANCH_LENGTH;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.stream.Collectors;

import org.sing_group.treecollapse.core.tree.MutableTreeNode;
import org.sing_group.treecollapse.core.tree.TreeNode;

public class NewickTree {

  private static DecimalFormatSymbols SYMBOLS = new DecimalFormatSymbols(Locale.US);
  private static NumberFormat BRANCH_LENGTH_FORMAT = new DecimalFormat("#0.0############", SYMBOLS);
  private MutableTreeNode root;

  public NewickTree(String newick) {
    this(NewickTreeParser.parse(newick));
  }

  public NewickTree(TreeNode root) {
    this(new MutableTreeNode(root));
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

  protected String toString(MutableTreeNode node) {
    if (node.getChildren().size() == 0) {
      return formatNode(node);
    }

    StringBuilder sb = new StringBuilder("(");
    sb.append(
      node.getChildren().stream()
        .map(this::toString)
        .collect(Collectors.joining(","))
    ).append(")");

    sb.append(formatNode(node));

    return sb.toString();
  }

  protected String formatNode(MutableTreeNode node) {
    StringBuilder sb = new StringBuilder();
    if (node.getName() != null && !node.getName().isEmpty()) {
      sb.append(node.getName());
    }

    if (node.getAttribute(BRANCH_LENGTH) != null) {
      sb.append(":").append(BRANCH_LENGTH_FORMAT.format(node.getAttribute(BRANCH_LENGTH)));
    }
    return sb.toString();
  }
}
