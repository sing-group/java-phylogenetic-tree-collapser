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
package org.sing_group.treecollapse.core.taxonomy;

import static java.nio.file.Files.readAllLines;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.sing_group.treecollapse.core.tree.MutableTreeNode;
import org.sing_group.treecollapse.core.tree.TreeManager;
import org.sing_group.treecollapse.core.tree.TreeNode;

public class TaxonomyFileReader {
  private static final String TERM_DELIMITER = ";";

  public static MutableTreeNode read(File file) throws IOException {
    MutableTreeNode root = new MutableTreeNode("root");

    for (String line : readAllLines(file.toPath())) {
      processLine(line, root);
    }

    return root;
  }

  private static void processLine(String line, MutableTreeNode root) {
    MutableTreeNode parent = root;
    TreeManager treeManager = new TreeManager(parent);

    String[] lineSplit = line.split(TERM_DELIMITER);
    for (int i = lineSplit.length - 1; i >= 0; i--) {
      String currentTerm = lineSplit[i].trim();
      Optional<TreeNode> currentTermNode = treeManager.getNodeByName(currentTerm);

      if (!currentTermNode.isPresent()) {
        MutableTreeNode newNode = new MutableTreeNode(currentTerm);
        parent.addChild(newNode);
        parent = newNode;
      } else {
        parent = (MutableTreeNode) currentTermNode.get();
      }

      treeManager = new TreeManager(parent);
    }
  }
}
