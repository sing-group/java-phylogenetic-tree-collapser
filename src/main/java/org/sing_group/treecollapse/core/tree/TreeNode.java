package org.sing_group.treecollapse.core.tree;

import java.io.PrintStream;
import java.util.List;
import java.util.stream.IntStream;

public interface TreeNode {

  public List<? extends TreeNode> getChildren();

  public String getName();

  default boolean isLeaf() {
    return this.getChildren() == null || this.getChildren().size() == 0;
  }

  default public void dumpTree(PrintStream out) {
    _dumpTree(1, out);
  }

  default void _dumpTree(int level, PrintStream out) {
    IntStream.range(1, level).forEach(i -> out.print("\t"));
    out.print(this.toString());
    if (this.getChildren().size() > 0) {
      out.print(" [");
      for (TreeNode child : this.getChildren()) {
        out.print("\n");
        child._dumpTree(level + 1, out);

      }
      out.print("\n");
      IntStream.range(1, level).forEach(i -> out.print("\t"));
      out.print("]");
    }
  }
}
