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
