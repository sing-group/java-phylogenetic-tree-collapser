package org.sing_group.treecollapse.core.newick;

import java.util.LinkedList;
import java.util.List;
import static org.sing_group.treecollapse.core.BranchLengthAveragingTaxonomyCollapsingStrategy.BRANCH_LENGTH;

import org.sing_group.treecollapse.core.tree.MutableTreeNode;

public class NewickTreeParser {

  public static MutableTreeNode parse(String newick) {
    newick = newick.replace(";", "");
    newick = newick.replace(" ;", "");

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
