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
